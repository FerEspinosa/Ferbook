package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferbook.R;
import com.ferbook.adapters.SliderAdapter;
import com.ferbook.models.SliderItem;
import com.ferbook.models.User;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
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

    CircleImageView     mImageView_Back_button;


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
        mBtn_viewProfile    = findViewById(R.id.button_ver_perfil);
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

        getPost();

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
                        String idUser = documentSnapshot.getString("idUser");
                        getUserInfo(idUser);
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