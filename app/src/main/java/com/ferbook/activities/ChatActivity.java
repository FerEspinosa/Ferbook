package com.ferbook.activities;

import androidx.annotation.NonNull;
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
import com.ferbook.adapters.MyPostsAdapter;
import com.ferbook.models.Chat;
import com.ferbook.models.Message;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ChatProvider;
import com.ferbook.providers.MessageProvider;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraUserId1;
    String mExtraUserId2;
    String mExtraChatId;

    ChatProvider mChatProvider;
    MessageProvider mMessageProvider;
    Authprovider mAuthProvider;
    UsersProvider mUsersProvider;

    View            mActionBarView;
    EditText        mEt_message;
    CircleImageView mCiv_send;

    CircleImageView mCiv_profile;
    TextView        mTvRelativeTime;
    TextView        mTvUsername;
    ImageView       mIv_back;

    RecyclerView    mMessage_RecView;
    MessageAdapter  mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatProvider       = new ChatProvider();
        mMessageProvider    = new MessageProvider();
        mAuthProvider       = new Authprovider();
        mUsersProvider      = new UsersProvider();

        mMessage_RecView = findViewById(R.id.recyclerViewMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mMessage_RecView.setLayoutManager(linearLayoutManager);

        mExtraUserId1   = getIntent().getStringExtra("userId1");
        mExtraUserId2   = getIntent().getStringExtra("userId2");
        mExtraChatId    = getIntent().getStringExtra("chatId");

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mEt_message = findViewById(R.id.et_chatMessage);
        mCiv_send = findViewById(R.id.civ_send);

        mCiv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExists();
    }

    private void sendMessage() {
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
                        Toast.makeText(ChatActivity.this, "Se creó el mensaje", Toast.LENGTH_SHORT).show();
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
        mUsersProvider.getUser(userIdInfo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String username = documentSnapshot.getString("nombre");
                        mTvUsername.setText(username);
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

        mChatProvider.create(chat);
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mMessageProvider.getMessagesByChat(mExtraChatId);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mMessageAdapter = new MessageAdapter(options, ChatActivity.this);

        mMessage_RecView.setAdapter(mMessageAdapter);
        mMessageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}