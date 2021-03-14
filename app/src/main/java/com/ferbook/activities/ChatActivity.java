package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ferbook.R;
import com.ferbook.models.Chat;
import com.ferbook.providers.ChatProvider;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String mExtraUserId1;
    String mExtraUserId2;

    ChatProvider mChatProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraUserId1 = getIntent().getStringExtra("userId1");
        mExtraUserId2 = getIntent().getStringExtra("userId2");

        mChatProvider = new ChatProvider();
        createChat();
    }

    private void createChat () {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraUserId1);
        chat.setIdUser2(mExtraUserId2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        mChatProvider.create(chat);
    }
}