package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.adapters.SliderAdapter;
import com.ferbook.models.SliderItem;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    SliderView          mSliderView;
    SliderAdapter       mSliderAdapter;
    List <SliderItem>   mSliderItems = new ArrayList<>();
    String              mExtraPostId;
    PostProvider        mPostProvider;
    UsersProvider       mUsersProvider;

    CircleImageView     mIv_profileImage;
    TextView            mTv_name;
    TextView            mTv_phone;
    Button              mBtn_viewProfile;
    TextView            mTv_title;
    ImageView           mIv_consola;
    TextView            mTv_consola;
    TextView            mTv_description;
    FloatingActionButton mBtn_comment;

    CircleImageView     mImageView_Back_button;

    String              mIdUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView     = findViewById(R.id.imageSlider);
        mPostProvider   = new PostProvider();
        mUsersProvider  = new UsersProvider();
        mExtraPostId    = getIntent().getStringExtra("id");

        mIv_profileImage    = findViewById(R.id.circleImageView_ProfileImage);
        mTv_name            = findViewById(R.id.tv_name);
        mTv_phone           = findViewById(R.id.tv_phone);
        mTv_title           = findViewById(R.id.tv_titulo_de_juego);
        mIv_consola         = findViewById(R.id.imageView_consola);
        mTv_consola         = findViewById(R.id.tv_consola);
        mTv_description     = findViewById(R.id.tv_descripcion);


        // BOTON ATRAS
        mImageView_Back_button = findViewById(R.id.btn_atras);
        mImageView_Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // BOTON VER PERFIL
        mBtn_viewProfile = findViewById(R.id.button_ver_perfil);
        mBtn_viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

        // BOTON COMENTAR
        mBtn_comment = findViewById(R.id.fab_comment);
        mBtn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogComment();
            }
        });


        getPost();

    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("Dejá un comentario");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Comentario");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36, 0, 36, 36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);


        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void goToShowProfile() {

        if (!mIdUser.equals("")) {

            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "El id del usuario no se ha cargado aún, prueba nuevamente en un momento", Toast.LENGTH_LONG).show();
        }
    }

    private void getPost () {

        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("image1")){

                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }

                    if (documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }

                    if (documentSnapshot.contains("titulo")){
                        mTv_title.setText(documentSnapshot.getString("titulo"));
                    }

                    if (documentSnapshot.contains("category")){
                        String category = documentSnapshot.getString("category");
                        mTv_consola.setText(category);

                        if (category.equals("PC")){
                            mIv_consola.setImageResource(R.drawable.icon_pc);
                        } else if (category.equals("PS4")){
                            mIv_consola.setImageResource(R.drawable.icon_ps4);
                        } else if (category.equals("NINTENDO")){
                            mIv_consola.setImageResource(R.drawable.icon_nintendo);
                        } else if (category.equals("XBOX")){
                            mIv_consola.setImageResource(R.drawable.icon_xbox);
                        }


                    }

                    if (documentSnapshot.contains("descripcion")){
                        mTv_description.setText(documentSnapshot.getString("descripcion"));
                    }

                    if (documentSnapshot.contains("idUser")){
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }

                    setupSlider();

                }
            }
        });
    }

    private void getUserInfo(String idUser) {

        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("nombre")){
                        String userName = documentSnapshot.getString("nombre");
                        mTv_name.setText(userName);
                    }
                    if (documentSnapshot.contains("telefono")){
                        String userPhone = documentSnapshot.getString("telefono");
                        mTv_phone.setText(userPhone);
                    }
                    if (documentSnapshot.contains("profile_image")){
                        String profile_image = documentSnapshot.getString("profile_image");
                        Picasso.with(PostDetailActivity.this).load(profile_image).into(mIv_profileImage);
                    }


                }
            }
        });
    }

    private void setupSlider () {

        //configurar animacion

        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }
}