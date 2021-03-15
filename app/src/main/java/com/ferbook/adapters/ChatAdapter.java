package com.ferbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferbook.R;
import com.ferbook.models.Chat;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter <Chat, ChatAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    String mUserId="";
    Authprovider mAuthProvider;

    public ChatAdapter(FirestoreRecyclerOptions <Chat> options, Context context){
        super(options);
        this.context = context;
        mUsersProvider  = new UsersProvider();
        mAuthProvider   = new Authprovider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        // obtener el documento que contiene el Post mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId = document.getId();

        //si el usuario 1 del chat es el usuario logueado
        if (mAuthProvider.getUid().equals(chat.getIdUser1())){
            //buscar la info del usuario 2
            getUserInfo(chat.getIdUser2(),holder);

        } else {
            //buscar la info del usuario 1
            getUserInfo(chat.getIdUser1(),holder);
        }

        getUserInfo(chatId, holder);

    }

    private void getUserInfo (String userId, ViewHolder holder) {

        mUsersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        CircleImageView civ_chat;

        // agregado en leccion del video n°45
        View viewHolder;


        public ViewHolder (View view){
            super(view);
            tv_username = view.findViewById(R.id.tv_chatUsername);
            tv_LastMessage  = view.findViewById(R.id.tv_lastMessageChat);
            civ_chat        = view.findViewById(R.id.civ_chat);

            viewHolder = view;
        }

    }
}
