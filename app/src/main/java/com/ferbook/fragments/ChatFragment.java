package com.ferbook.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferbook.R;
import com.ferbook.adapters.ChatAdapter;
import com.ferbook.adapters.PostsAdapter;
import com.ferbook.models.Chat;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ChatProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    ChatAdapter     mChatAdapter;
    RecyclerView    mRecyclerView;
    View            mView;
    ChatProvider    mChatProvider;
    Authprovider    mAuthProvider;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAuthProvider = new Authprovider();

        mChatProvider = new ChatProvider();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();
        mChatAdapter = new ChatAdapter(options, getContext());
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatAdapter.getListener() != null){
            mChatAdapter.getListener().remove();
        }
        if (mChatAdapter.getListenerLastMessage() != null){
            mChatAdapter.getListenerLastMessage().remove();
        }
    }
}