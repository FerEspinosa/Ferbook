package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ferbook.adapters.CommentAdapter;
import com.ferbook.adapters.SliderAdapter;
import com.ferbook.models.Comment;
import com.ferbook.models.FCMBody;
import com.ferbook.models.FCMResponse;
import com.ferbook.models.SliderItem;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.CommentProvider;
import com.ferbook.providers.LikesProviders;
import com.ferbook.providers.NotificationProvider;
import com.ferbook.providers.PostProvider;
import com.ferbook.providers.TokenProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.RelativeTime;
import com.ferbook.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    SliderView          mSliderView;
    SliderAdapter       mSliderAdapter;
    List <SliderItem>   mSliderItems = new ArrayList<>();
    String              mExtraPostId;
    PostProvider        mPostProvider;
    UsersProvider       mUsersProvider;
    Authprovider        mAuthProvider;
    LikesProviders      mLikesProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider       mTokenProvider;
    CircleImageView     mIv_profileImage;
    TextView            mTv_name;
    TextView            mTv_phone;
    Button              mBtn_viewProfile;
    TextView            mTv_title;
    ImageView           mIv_consola;
    TextView            mTv_consola;
    TextView            mTv_description;
    FloatingActionButton mBtn_comment;
    Toolbar             mToolbar;

    String              mIdUser = "";

    // SECCIÓN COMENTARIOS
    CommentProvider     mCommentProvider;
    RecyclerView        mRecyclerViewComments;
    CommentAdapter      mCommentAdapter;

    TextView            mTv_relativeTime;
    TextView            mTv_likesNumber;

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView         = findViewById(R.id.imageSlider);
        mPostProvider       = new PostProvider();
        mUsersProvider      = new UsersProvider();
        mCommentProvider    = new CommentProvider();
        mAuthProvider       = new Authprovider();
        mLikesProvider      = new LikesProviders();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider      = new TokenProvider();


        mExtraPostId        = getIntent().getStringExtra("id");

        mIv_profileImage    = findViewById(R.id.circleImageView_ProfileImage);
        mTv_name            = findViewById(R.id.tv_name);
        mTv_phone           = findViewById(R.id.tv_phone);
        mTv_title           = findViewById(R.id.tv_titulo_de_juego);
        mIv_consola         = findViewById(R.id.imageView_consola);
        mTv_consola         = findViewById(R.id.tv_consola);
        mTv_description     = findViewById(R.id.tv_descripcion);
        mTv_relativeTime    = findViewById(R.id.tv_relativeTime);
        mTv_likesNumber     = findViewById(R.id.tv_likesNumber);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerViewComments = findViewById(R.id.recyclerViewComments);
        mRecyclerViewComments.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerViewComments.setLayoutManager(linearLayoutManager);


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

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mCommentProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();
        mCommentAdapter = new CommentAdapter(options, PostDetailActivity.this);

        mRecyclerViewComments.setAdapter(mCommentAdapter);
        mCommentAdapter.startListening();

        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
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
                if (!value.isEmpty()){
                    createComment(value);
                    Toast.makeText(PostDetailActivity.this, value, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Ingresa un comentario", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void createComment(String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setPostId(mExtraPostId);
        comment.setUserId(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());

        mCommentProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "El comentario se creó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification (String comment){
        if (mIdUser==null) {
            return;}
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map <String, String> data = new HashMap<>();
                        data.put("title", "Nuevo comentario");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body()!=null){
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(PostDetailActivity.this, "La notificación se envió correctamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(PostDetailActivity.this, "No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                            }
                        });
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

                    if (documentSnapshot.contains("timestamp")){
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        mTv_relativeTime.setText(relativeTime);
                    }

                    getLikesNumber(mExtraPostId);

                    setupSlider();

                }
            }
        });
    }

    private void getLikesNumber(String mExtraPostId) {
        mListener = mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    int likesNumber = value.size();

                    if (likesNumber == 1){
                        mTv_likesNumber.setText("1 like");
                    } else {
                        mTv_likesNumber.setText(likesNumber+" likes");
                    }
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