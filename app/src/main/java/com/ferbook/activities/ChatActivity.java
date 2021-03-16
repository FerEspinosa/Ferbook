package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.models.Chat;
import com.ferbook.models.Message;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ChatProvider;
import com.ferbook.providers.MessageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

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

    View mActionBarView;
    EditText et_message;
    CircleImageView civ_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        showCustomToolbar(R.layout.custom_chat_toolbar);

        mExtraUserId1 = getIntent().getStringExtra("userId1");
        mExtraUserId2 = getIntent().getStringExtra("userId2");
        mExtraChatId = getIntent().getStringExtra("chatId");

        mChatProvider       = new ChatProvider();
        mMessageProvider    = new MessageProvider();
        mAuthProvider       = new Authprovider();

        et_message  = findViewById(R.id.et_chatMessage);
        civ_send    = findViewById(R.id.civ_send);

        civ_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExists();
    }

    private void sendMessage() {
        String textMessage = et_message.getText().toString();
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
                        et_message.setText("");
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
    }

    private void checkIfChatExists () {
        mChatProvider.checkChatExists(mExtraUserId1,mExtraUserId2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size==0){
                    Toast.makeText(ChatActivity.this, "Se creará un nuevo chat", Toast.LENGTH_SHORT).show();
                    createChat();
                } else {
                    Toast.makeText(ChatActivity.this, "El chat ya existe", Toast.LENGTH_SHORT).show();
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
}