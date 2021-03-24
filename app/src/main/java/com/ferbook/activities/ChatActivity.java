package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.adapters.MessageAdapter;
import com.ferbook.models.Chat;
import com.ferbook.models.FCMBody;
import com.ferbook.models.FCMResponse;
import com.ferbook.models.Message;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ChatProvider;
import com.ferbook.providers.MessageProvider;
import com.ferbook.providers.NotificationProvider;
import com.ferbook.providers.TokenProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.RelativeTime;
import com.ferbook.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraUserId1;
    String mExtraUserId2;
    String mExtraChatId;
    long mNotificationChatId;

    ChatProvider    mChatProvider;
    MessageProvider mMessageProvider;
    Authprovider    mAuthProvider;
    UsersProvider   mUsersProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    View            mActionBarView;
    EditText        mEt_message;
    CircleImageView mCiv_send;

    CircleImageView mCiv_profile;
    TextView        mTvRelativeTime;
    TextView        mTvUsername;
    ImageView       mIv_back;

    RecyclerView    mRecViewMessage;
    MessageAdapter  mMessageAdapter;

    LinearLayoutManager mLinearLayoutManager;

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatProvider       = new ChatProvider();
        mMessageProvider    = new MessageProvider();
        mAuthProvider       = new Authprovider();
        mUsersProvider      = new UsersProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider      = new TokenProvider();

        mRecViewMessage     = findViewById(R.id.recyclerViewMessage);
        mEt_message         = findViewById(R.id.et_chatMessage);
        mCiv_send           = findViewById(R.id.civ_send);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true); //esto indica que se muestren los últimos mensajes del chat en lugar de mostrar los primeros
        mRecViewMessage.setLayoutManager(mLinearLayoutManager);

        mExtraUserId1   = getIntent().getStringExtra("userId1");
        mExtraUserId2   = getIntent().getStringExtra("userId2");
        mExtraChatId    = getIntent().getStringExtra("chatId");

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mCiv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExists();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mMessageAdapter!=null){
            mMessageAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener!=null){
            mListener.remove();
        }
    }

    private void getChatMessage () {
        Query query = mMessageProvider.getMessagesByChat(mExtraChatId);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mMessageAdapter = new MessageAdapter(options, ChatActivity.this);
        mRecViewMessage.setAdapter(mMessageAdapter);
        mMessageAdapter.startListening();

        //el siguiente método detecta cambios en el adapter
        mMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int messageNumber = mMessageAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastMessagePosition == -1 || (positionStart >= (messageNumber-1)&& lastMessagePosition == (positionStart-1))){
                    mRecViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessage()  {
        String textMessage = mEt_message.getText().toString();
        if (!textMessage.isEmpty()){
            Message message = new Message();
            message.setChatId(mExtraChatId);
            if (mAuthProvider.getUid().equals(mExtraUserId1)){
                message.setSenderId(mExtraUserId1);
                message.setReceiverId(mExtraUserId2);
            } else {
                message.setSenderId(mExtraUserId2);
                message.setReceiverId(mExtraUserId1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setChatId(mExtraChatId);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mEt_message.setText("");
                        mMessageAdapter.notifyDataSetChanged();
                        sendNotification(message.getMessage());
                    } else {
                        Toast.makeText(ChatActivity.this, "No se pudo crear el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        mCiv_profile = mActionBarView.findViewById(R.id.circleImage_Profile_chatToolbar);
        mTvUsername = mActionBarView.findViewById(R.id.tv_usernameChatToolbar);
        mTvRelativeTime = mActionBarView.findViewById(R.id.tv_relativeTime_ChatToolbar);
        mIv_back = mActionBarView.findViewById(R.id.iv_back_chatToolbar);

        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        String userIdInfo = "";
        if (mAuthProvider.getUid().equals(mExtraUserId1)){
            userIdInfo = mExtraUserId2;
        } else {
            userIdInfo = mExtraUserId1;
        }

        mListener = mUsersProvider.getUserRealTime(userIdInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String username = documentSnapshot.getString("nombre");
                        mTvUsername.setText(username);
                    }
                    if (documentSnapshot.contains("online")){
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online){
                            mTvRelativeTime.setText("En línea");

                        } else if (documentSnapshot.contains("lastConnect")){

                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTvRelativeTime.setText(relativeTime);
                        }
                    }
                    if (documentSnapshot.contains("profile_image")){
                        String profileImage = documentSnapshot.getString("profile_image");
                        if (profileImage!=null){
                            if (!profileImage.equals("")){
                                Picasso.with(ChatActivity.this).load(profileImage).into(mCiv_profile);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExists () {
        mChatProvider.checkChatExists(mExtraUserId1,mExtraUserId2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size==0){
                    createChat();
                } else {
                    mExtraChatId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mNotificationChatId = queryDocumentSnapshots.getDocuments().get(0).getLong("notificationId");
                    getChatMessage();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String senderId = "";
        if (mAuthProvider.getUid().equals(mExtraUserId1)){
            senderId = mExtraUserId2;
        } else {
            senderId = mExtraUserId1;
        }
        mMessageProvider.getMessagesByChatAndSender(mExtraChatId, senderId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                    mMessageProvider.updateViewed(document.getId(), true);
                }
            }
        });
    }

    private void createChat () {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraUserId1);
        chat.setIdUser2(mExtraUserId2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        //los siguientes chat fueron agregados en el video 70. que hace modificaciones a las estructura de datos de los chats
        chat.setId(mExtraUserId1+mExtraUserId2);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraUserId1);
        ids.add(mExtraUserId2);
        chat.setIds(ids);
        // hasta acá las modificaciones del video 70

        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setNotificationId(n);
        mNotificationChatId = n;


        mChatProvider.create(chat);
        mExtraChatId = chat.getId();
        getChatMessage();
    }

    private void sendNotification (String message){

        String userId = "";
        if (mAuthProvider.getUid().equals(mExtraUserId1)){
            userId = mExtraUserId2;
        } else {
            userId = mExtraUserId1;
        }

        mTokenProvider.getToken(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "Nuevo mensaje");
                        data.put("body", message);

                        data.put("notificationId", String.valueOf(mNotificationChatId));
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body()!=null){
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(ChatActivity.this, "La notificación se envió correctamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatActivity.this, "No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ChatActivity.this, "No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                            }
                        });
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}