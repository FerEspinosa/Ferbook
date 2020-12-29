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
import com.ferbook.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostsAdapter extends FirestoreRecyclerAdapter <Post,PostsAdapter.ViewHolder> {

    Context context;

    public PostsAdapter (FirestoreRecyclerOptions <Post> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        // obtener el documento que contiene el Post mostrado en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        String postId = document.getId();

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

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_description;
        ImageView iv_img_post;

        // agregado en leccion del video n°45
        View viewHolder;


        public ViewHolder (View view){
            super(view);
            tv_title = view.findViewById(R.id.tv_Postcard_Title);
            tv_description = view.findViewById(R.id.tv_Postcard_Description);
            iv_img_post = view.findViewById(R.id.iv_postCard);

            viewHolder = view;
        }

    }
}
