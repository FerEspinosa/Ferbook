package com.ferbook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ferbook.R;
import com.ferbook.activities.EditProfileActivity;
import com.ferbook.adapters.MyPostsAdapter;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    LinearLayout    mLinearLayoutEditProfile;
    View            mView;

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

    RecyclerView    mRecyclerView_myPosts;
    MyPostsAdapter  mPostsAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayout_EditProfile);
        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        tv_Name         = mView.findViewById(R.id.tv_name);
        tv_Phone        = mView.findViewById(R.id.tv_phone);
        tv_Email        = mView.findViewById(R.id.tv_email);
        tv_PostNumber   = mView.findViewById(R.id.tv_postNumber);
        iv_Cover        = mView.findViewById(R.id.iv_cover_image);
        civ_Profile     = mView.findViewById(R.id.circleImage_Profile);
        tv_pub          = mView.findViewById(R.id.tv_no_hay_publicaciones);
        tv_txt_pub      = mView.findViewById(R.id.tv_txt_publicaciones);


        mRecyclerView_myPosts = mView.findViewById(R.id.recyclerView_MyPosts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView_myPosts.setLayoutManager(linearLayoutManager);

        mUsersProvider  = new UsersProvider();
        mAuthProvider   = new Authprovider();
        mPostProvider   = new PostProvider();

        getUser();
        getPostNumber();

        return mView;
    }

    private void getPostNumber() {
        mPostProvider.getPostsByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int post_number = value.size();
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

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getUser () {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                                Picasso.with(getContext()).load(coverImage).into(iv_Cover);
                            }
                        }
                    }
                    if (documentSnapshot.contains("profile_image")){

                        String profileImage = documentSnapshot.getString("profile_image");

                        if (profileImage != null){
                            if(!profileImage.isEmpty()){

                                Picasso.with(getContext()).load(profileImage).into(civ_Profile);
                            }
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getPostsByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new MyPostsAdapter(options, getContext());

        mRecyclerView_myPosts.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

}