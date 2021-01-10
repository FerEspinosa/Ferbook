package com.ferbook.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ferbook.R;
import com.ferbook.activities.PostDetailActivity;
import com.ferbook.models.Like;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.LikesProviders;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter <Post, MyPostsAdapter.ViewHolder> {

    Context         context;
    UsersProvider   mUsersProvider;
    LikesProviders  mLikesProviders;
    Authprovider    mAutheProvider;
    PostProvider    mPostProvider;

    public MyPostsAdapter(FirestoreRecyclerOptions <Post> options, Context context){
        super(options);
        this.context    = context;
        mUsersProvider  = new UsersProvider();
        mLikesProviders = new LikesProviders();
        mAutheProvider  = new Authprovider();
        mPostProvider   = new PostProvider();

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        // obtener el documento que contiene el Post para mostrar en el cardView
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        String postId = document.getId();

        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);

        holder.mTv_PostRelativeTime.setText(relativeTime);

        holder.mTv_PostTitle.setText(post.getTitulo());

        if (post.getImage1()!=null){
            if (!post.getImage1().isEmpty()){

                Picasso.with(context).load(post.getImage1()).into(holder.mCiv_PostImage);
            }
        }

        // al hacer click en un ViewHolder de un Post, nos lleva al Post en detalle:
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.mIv_deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showDeleteConfirmation(postId);
            }
        });

    }

    private void showDeleteConfirmation(String postId) {

        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estas seguro que querés eliminar esta publicación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePost(String postId) {

        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "La publicación se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No se pudo borra la publicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView        mTv_PostTitle;
        TextView        mTv_PostRelativeTime;
        CircleImageView mCiv_PostImage;
        ImageView       mIv_deletePost;

        View viewHolder;


        public ViewHolder (View view){
            super(view);

            mTv_PostTitle           = view.findViewById(R.id.tv_myPosts_title);
            mTv_PostRelativeTime    = view.findViewById(R.id.tv_myPosts_relativeTime);
            mCiv_PostImage          = view.findViewById(R.id.civ_mypost);
            mIv_deletePost          = view.findViewById(R.id.iv_myPosts_delete);

            viewHolder = view;
        }

    }
}
