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
import com.ferbook.models.Like;
import com.ferbook.models.Post;
import com.ferbook.providers.LikesProviders;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter <Post,PostsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    LikesProviders mLikesProviders;

    public PostsAdapter (FirestoreRecyclerOptions <Post> options, Context context){
        super(options);
        this.context    = context;
        mUsersProvider  = new UsersProvider();
        mLikesProviders = new LikesProviders();

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        // obtener el documento que contiene el Post mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        String postId = document.getId();

        getUserName(post.getIdUser(), holder);

        holder.tv_title.setText(post.getTitulo());
        holder.tv_description.setText(post.getDescripcion());

        if (post.getImage1()!=null){
            if (!post.getImage1().isEmpty()){

                Picasso.with(context).load(post.getImage1()).into(holder.iv_img_post);
            }
        }

        // al hacer click en un ViewHolder de un Post, nos lleva al Post en detalle:
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Post id:"+postId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent (context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like like = new Like();
                like.setUserId(post.getIdUser());
                like.setPostId(postId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });

    }

    private void like(Like like, ViewHolder holder) {

        mLikesProviders.getLikeByPostAndUser(like.getPostId(), like.getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int likeNumber = queryDocumentSnapshots.size();

                if (likeNumber>0){
                    // Si hay un like, borrarlo:
                    String likeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mLikesProviders.delete(likeId);
                    holder.iv_like.setImageResource(R.drawable.corazon_gris);

                } else {
                    //si no hay ningún like, crearlo:
                    mLikesProviders.create(like);
                    holder.iv_like.setImageResource(R.drawable.corazon_rojo);
                }
            }
        });


    }

    private void getUserName (String userId, ViewHolder holder) {
        mUsersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("nombre")){
                        String userName = documentSnapshot.getString("nombre");
                        holder.tv_userName.setText("By: "+userName);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tv_title;
        TextView    tv_description;
        TextView    tv_userName;
        ImageView   iv_img_post;
        ImageView   iv_like;
        TextView    tv_likeNumber;

        // agregado en leccion del video n°45
        View viewHolder;


        public ViewHolder (View view){
            super(view);
            tv_title        = view.findViewById(R.id.tv_Postcard_Title);
            tv_description  = view.findViewById(R.id.tv_Postcard_Description);
            iv_img_post     = view.findViewById(R.id.iv_postCard);

            iv_like         = view.findViewById(R.id.iv_like);
            tv_likeNumber   = view.findViewById(R.id.tv_likeNumber);
            tv_userName     = view.findViewById(R.id.tv_Postcard_userName);

            viewHolder = view;
        }

    }
}
