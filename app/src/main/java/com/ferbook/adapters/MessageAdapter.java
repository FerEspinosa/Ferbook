package com.ferbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferbook.R;
import com.ferbook.activities.ChatActivity;
import com.ferbook.models.Message;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.MessageProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirestoreRecyclerAdapter <Message, MessageAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    String mUserId="";
    Authprovider mAuthProvider;

    public MessageAdapter(FirestoreRecyclerOptions <Message> options, Context context){
        super(options);
        this.context = context;
        mUsersProvider  = new UsersProvider();
        mAuthProvider   = new Authprovider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {

        // obtener el documento que contiene el mensaje mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String messageId = document.getId();
        holder.tv_mBubble_text.setText(message.getMessage());
        String relativeTime = RelativeTime.getTimeAgo(message.getTimestamp(),context);
        holder.tv_mBubble_date.setText(relativeTime);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView        tv_mBubble_text;
        TextView        tv_mBubble_date;
        ImageView       iv_doblecheck;

        View viewHolder;

        public ViewHolder (View view){
            super(view);
            tv_mBubble_text = view.findViewById(R.id.message_bubble_text);
            tv_mBubble_date = view.findViewById(R.id.message_bubble_date);
            iv_doblecheck   = view.findViewById(R.id.iv_doblecheck);

            viewHolder = view;
        }

    }
}
