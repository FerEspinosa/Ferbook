package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferbook.R;
import com.ferbook.adapters.MyPostsAdapter;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView        tv_Name;
    TextView        tv_Phone;
    TextView        tv_Email;
    TextView        tv_PostNumber;
    TextView        tv_pub;
    TextView        tv_txt_pub;
    ImageView       iv_Cover;
    CircleImageView civ_Profile;

    UsersProvider   mUsersProvider;
    Authprovider    mAuthProvider;
    PostProvider    mPostProvider;

    String          mExtraIdUser;

    MyPostsAdapter  mPostsAdapter;
    RecyclerView    mRecyclerView_myPosts;

    Toolbar         mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tv_Name         = findViewById(R.id.tv_name);
        tv_Phone        = findViewById(R.id.tv_phone);
        tv_Email        = findViewById(R.id.tv_email);
        tv_PostNumber   = findViewById(R.id.tv_postNumber);
        tv_pub          = findViewById(R.id.tv_publicaciones);
        tv_txt_pub      = findViewById(R.id.tv_txt_publicaciones);
        iv_Cover        = findViewById(R.id.iv_cover_image);
        civ_Profile     = findViewById(R.id.circleImage_Profile);

        mRecyclerView_myPosts = findViewById(R.id.recyclerView_MyPosts);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView_myPosts.setLayoutManager(linearLayoutManager);

        mUsersProvider  = new UsersProvider();
        mAuthProvider   = new Authprovider();
        mPostProvider   = new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        // BOTON ATRAS
        /*
        mImageView_Back_button = findViewById(R.id.btn_atras);
        mImageView_Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        getUser();
        getPostNumber();
    }

    private void getUser () {
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("nombre")){

                        String name = documentSnapshot.getString("nombre");
                        tv_Name.setText(name);
                    }

                    if (documentSnapshot.contains("telefono")){
                        String phone = documentSnapshot.getString("telefono");
                        tv_Phone.setText(phone);
                    }

                    if (documentSnapshot.contains("email")){

                        String email = documentSnapshot.getString("email");
                        tv_Email.setText(email);
                    }
                    if (documentSnapshot.contains("cover_image")){

                        String coverImage = documentSnapshot.getString("cover_image");

                        if (coverImage != null){
                            if(!coverImage.isEmpty()){

                                Picasso.with(UserProfileActivity.this).load(coverImage).into(iv_Cover);
                            }
                        }
                    }
                    if (documentSnapshot.contains("profile_image")){

                        String profileImage = documentSnapshot.getString("profile_image");

                        if (profileImage != null){
                            if(!profileImage.isEmpty()){

                                Picasso.with(UserProfileActivity.this).load(profileImage).into(civ_Profile);
                            }
                        }
                    }

                }
            }
        });
    }

    private void getPostNumber () {
        mPostProvider.getPostsByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //con el método ".size", obtenemos el número de publicaciones que llevan el id del usuario
                int post_number = queryDocumentSnapshots.size();
                tv_PostNumber.setText(String.valueOf(post_number));

                if (post_number==0){
                    tv_pub.setText("No hay publicaciones");

                } else if (post_number==1){
                    tv_pub.setText("Publicaciones:");
                    tv_txt_pub.setText("publicación");
                } else if (post_number>1){
                    tv_pub.setText("Publicaciones:");
                    tv_txt_pub.setText("publicaciónes");
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getPostsByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new MyPostsAdapter(options, UserProfileActivity.this);

        mRecyclerView_myPosts.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}