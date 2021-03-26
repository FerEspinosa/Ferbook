package com.ferbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ferbook.R;
import com.ferbook.activities.ChatActivity;
import com.ferbook.models.Chat;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.MessageProvider;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter <Chat, ChatAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    Authprovider authProvider;
    MessageProvider messageProvider;
    ListenerRegistration listener ;
    ListenerRegistration listenerLastMessage ;

    public ChatAdapter(FirestoreRecyclerOptions <Chat> options, Context context){
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        authProvider = new Authprovider();
        messageProvider = new MessageProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        // obtener el documento que contiene el Post mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId = document.getId();

        //si el usuario 1 del chat es el usuario logueado
        if (authProvider.getUid().equals(chat.getIdUser1())){
            //buscar la info del usuario 2
            getUserInfo(chat.getIdUser2(),holder);

        } else {
            //buscar la info del usuario 1
            getUserInfo(chat.getIdUser1(),holder);
        }

        getUserInfo(chatId, holder);

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });

        getLastMesasge(chatId, holder.tv_LastMessage);

        String senderId = "";
        if (authProvider.getUid().equals(chat.getIdUser1())){
            senderId = chat.getIdUser2();
        } else {
            senderId = chat.getIdUser1();
        }
        getUnreadMessages(chatId, senderId, holder.tv_unreadMessages, holder.fl_unreadMessages);

    }

    private void getUnreadMessages(String chatId, String senderId, TextView tv_unreadMessages, FrameLayout fl_unreadMessages) {

        listener = messageProvider.getMessagesByChatAndSender(chatId, senderId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){
                    int size = value.size();
                    if (size>0){
                        fl_unreadMessages.setVisibility(View.VISIBLE);
                        tv_unreadMessages.setText(String.valueOf(size));
                    } else {
                        fl_unreadMessages.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    //este método envía el listener del método de arriba (getUnreadMessages),
    // para poder terminarlo al abandonar la activity
    public ListenerRegistration getListener() {
        return listener;
    }


    private void getLastMesasge(String chatId, TextView tv_lastMessage) {
        listenerLastMessage = messageProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if (value!=null){
                   int size = value.size();
                   if (size>0){
                       String lastMessage = value.getDocuments().get(0).getString("message");
                       tv_lastMessage.setText(lastMessage);
                   }
               }
            }
        });
    }

    //este método envía el listener del método de "getLastMessage"(arriba),
    // para poder terminarlo al abandonar la activity
    public ListenerRegistration getListenerLastMessage() {
        return listenerLastMessage;
    }

    private void goToChatActivity(String chatId, String userId1, String userId2) {
        Intent intent = new Intent (context, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("userId1", userId1);
        intent.putExtra("userId2", userId2);
        context.startActivity(intent);
    }

    private void getUserInfo (String userId, ViewHolder holder) {

        usersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String name = documentSnapshot.getString("nombre");
                        holder.tv_username.setText(name);
                    }

                    if (documentSnapshot.contains("profile_image")){
                        String profileImage = documentSnapshot.getString("profile_image");

                        if (profileImage != null){
                            if (!profileImage.isEmpty()){
                                Picasso.with(context).load(profileImage).into(holder.civ_chat);
                            }
                        }
                    }
                }

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView        tv_username;
        TextView        tv_LastMessage;
        TextView        tv_unreadMessages;
        CircleImageView civ_chat;
        FrameLayout     fl_unreadMessages;

        // agregado en leccion del video n°45
        View viewHolder;

        public ViewHolder (View view){
            super(view);
            tv_username         = view.findViewById(R.id.tv_chatUsername);
            tv_LastMessage      = view.findViewById(R.id.tv_lastMessageChat);
            tv_unreadMessages   = view.findViewById(R.id.tv_unreadMessages);
            civ_chat            = view.findViewById(R.id.civ_chat);
            fl_unreadMessages   = view.findViewById(R.id.fl_unreadMessages);

            viewHolder          = view;
        }
    }

}
