package com.ferbook.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.models.Chat;
import com.ferbook.providers.ChatProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String mExtraUserId1;
    String mExtraUserId2;

    ChatProvider mChatProvider;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        showCustomToolbar(R.layout.custom_chat_toolbar);


        mExtraUserId1 = getIntent().getStringExtra("userId1");
        mExtraUserId2 = getIntent().getStringExtra("userId2");

        mChatProvider = new ChatProvider();
        checkIfChatExists();
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