package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferbook.R;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView        tv_Name;
    TextView        tv_Phone;
    TextView        tv_Email;
    TextView        tv_PostNumber;
    ImageView       iv_Cover;
    CircleImageView civ_Profile;

    UsersProvider   mUsersProvider;
    Authprovider    mAuthProvider;
    PostProvider    mPostProvider;

    String mExtraIdUser;

    CircleImageView     mImageView_Back_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tv_Name         = findViewById(R.id.tv_name);
        tv_Phone        = findViewById(R.id.tv_phone);
        tv_Email        = findViewById(R.id.tv_email);
        tv_PostNumber   = findViewById(R.id.tv_postNumber);
        iv_Cover        = findViewById(R.id.iv_cover_image);
        civ_Profile     = findViewById(R.id.circleImage_Profile);

        mUsersProvider  = new UsersProvider();
        mAuthProvider   = new Authprovider();
        mPostProvider   = new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        // BOTON ATRAS
        mImageView_Back_button = findViewById(R.id.btn_atras);
        mImageView_Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

                    /*
                    if (documentSnapshot.contains("telefono")){
                        String phone = documentSnapshot.getString("telefono");
                        tv_Phone.setText(phone);
                    }*/

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

                // Probar si funciona sin parsear el int a String
                //tv_PostNumber.setText(post_number);
            }
        });
    }
}