package com.ferbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferbook.R;
import com.ferbook.activities.PostDetailActivity;
import com.ferbook.models.Comment;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter <Comment, CommentAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    String mUserId="";

    public CommentAdapter(FirestoreRecyclerOptions <Comment> options, Context context){
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        // obtener el documento que contiene el Post mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        String commentId = document.getId();
        String userId = document.getString("userId");

        holder.tv_comment.setText(comment.getComment());
        getUserInfo(userId, holder);

    }

    private void getUserInfo (String userId, ViewHolder holder) {

        mUsersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String name = documentSnapshot.getString("nombre");
                        holder.tv_name.setText(name);
                    }

                    if (documentSnapshot.contains("profile_image")){
                        String profileImage = documentSnapshot.getString("profile_image");

                        if (profileImage != null){
                            if (!profileImage.isEmpty()){
                                Picasso.with(context).load(profileImage).into(holder.iv_comment);
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_comment;
        CircleImageView iv_comment;

        // agregado en leccion del video n°45
        View viewHolder;


        public ViewHolder (View view){
            super(view);
            tv_name     = view.findViewById(R.id.tv_userName);
            tv_comment  = view.findViewById(R.id.tv_comment);
            iv_comment  = view.findViewById(R.id.circleImageComment);

            viewHolder = view;
        }

    }
}
