package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.models.Post;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ImageProvider;
import com.ferbook.providers.PostProvider;
import com.ferbook.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    TextInputEditText   mTextInputTitle;
    TextInputEditText   mTextInputDescription;
    TextView            tv_category;

    String              mCategory="", mTitle="", mDescription="";

    ImageView           iv_PC;
    ImageView           iv_Nintendo;
    ImageView           iv_PlayStation;
    ImageView           iv_Xbox;
    ImageView           mImgView_Post1;
    ImageView           mImgView_Post2;

    ImageProvider       mImageProvider;
    PostProvider        mPostProvider;
    Authprovider        mAuthProvider;

    Button              mButtonPost;
    CircleImageView     mBackButton;

    AlertDialog         mDialog;

    AlertDialog.Builder mBuilderSelector;
    CharSequence        options[];

    File                mImageFile1;
    File                mImageFile2;

    //galería
    private final int   gallery_request_code_1 = 1;
    private final int   gallery_request_code_2 = 2;
    
    //foto
    private  final int photo_request_code_1 = 3;
    private  final int photo_request_code_2 = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider  = new ImageProvider();
        mPostProvider   = new PostProvider();
        mAuthProvider   = new Authprovider();

        iv_PC                   = findViewById(R.id.iv_pc);
        iv_PlayStation          = findViewById(R.id.iv_playstation);
        iv_Nintendo             = findViewById(R.id.iv_nintendo);
        iv_Xbox                 = findViewById(R.id.iv_xbox);
        mTextInputTitle         = findViewById(R.id.input_juego);
        mTextInputDescription   = findViewById(R.id.input_descripcion);
        tv_category             = findViewById(R.id.tv_categoria);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de la galería","Tomar foto"};


        mBackButton = findViewById(R.id.btn_atras);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImgView_Post1 = findViewById(R.id.iv_post1);
        mImgView_Post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(1);
            }
        });

        mImgView_Post2 = findViewById(R.id.iv_post2);
        mImgView_Post2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
            }
        });

        mButtonPost = findViewById(R.id.btn_publicar);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });

        iv_PC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory   = "PC";
                tv_category.setText(mCategory);
            }
        });

        iv_Nintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "nintendo";
                tv_category.setText(mCategory);
            }
        });

        iv_PlayStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "playstation";
                tv_category.setText(mCategory);
            }
        });

        iv_Xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "xbox";
                tv_category.setText(mCategory);
            }
        });
    }

    private void selectOptionImage(int imageNumber) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    //galería

                    if (imageNumber==1){
                        openGallery(gallery_request_code_1);

                    } else if (imageNumber==2){
                        openGallery(gallery_request_code_2);
                    }

                } else if (which == 1) {

                    //foto
                    if (imageNumber==1){

                        //foto 1
                        takePhoto(photo_request_code_1);

                    } else if (imageNumber==2){

                        // foto 2
                        takePhoto(photo_request_code_2);

                    }

                }
            }
        });

        mBuilderSelector.show();
    }

    private void takePhoto(int photo_request_code_1) {
        Toast.makeText(this, "Se tomará la foto", Toast.LENGTH_LONG).show();
    }

    private void clickPost() {

        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();

        if (!mTitle.isEmpty()&&!mDescription.isEmpty()&&!mCategory.isEmpty()){
            if (mImageFile1 != null){
                saveImage();
            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor completá todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void saveImage() {

        mDialog.show();

        mImageProvider.save(PostActivity.this, mImageFile1)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){

                    // La imagen 1 se guardó correctamente en storage

                    // obtener url de imagen 1
                    mImageProvider.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url1 = uri.toString();

                                    //Una vez guardada la imagen 1 y obtenida su Url, hacer lo mismo con imagen 2:

                                    //guardar imagen 2:
                                    mImageProvider.save(PostActivity.this, mImageFile2)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task2) {
                                            if (task2.isSuccessful()) {

                                                // la imagen 2 se guardó correctamente

                                                //obtener url de imagen 2
                                                mImageProvider.getStorage().getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri2) {

                                                        String url2 = uri2.toString();

                                                        // ya se guardaron ambas imagenes y tengo ambas url's
                                                        // ahora PUBLICAR (GUARDAR los datos de la publicación en la base de datos)
                                                        Post post = new Post();

                                                        post.setImage1(url1);
                                                        post.setImage2(url2);
                                                        post.setTitulo(mTitle);
                                                        post.setDescripcion(mDescription);
                                                        post.setCategory(mCategory);
                                                        post.setId(mAuthProvider.getUid());

                                                        mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> taskSave) {
                                                                mDialog.dismiss();
                                                                if (taskSave.isSuccessful()){
                                                                    Toast.makeText(PostActivity.this, "La información se almacenó correctamente", Toast.LENGTH_LONG).show();

                                                                    clearForm();

                                                                } else {
                                                                    Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                mDialog.dismiss();
                                                Toast.makeText(PostActivity.this, "Error al guardar la imagen 2", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            });

                } else {
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void clearForm() {

        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        tv_category.setText("");
        mImgView_Post1.setImageResource(R.drawable.upload_image);
        mImgView_Post2.setImageResource(R.drawable.upload_image);

        mTitle          = "";
        mDescription    = "";
        mCategory       = "";

        mImageFile1 = null;
        mImageFile2 = null;

    }

    private void openGallery(int imgNumber) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //La siguiente línea configura que el Intent abre la galería del teléfono
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, imgNumber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_request_code_1 && resultCode == RESULT_OK) {
            try {
                //transformar la URI en el archivo mImageFile
                mImageFile1 = FileUtil.from(this, data.getData());

                // mostrar la imagen en el primer cardview (Post1)
                mImgView_Post1.setImageBitmap(BitmapFactory.decodeFile(mImageFile1.getAbsolutePath()));


            } catch (Exception e){
                Log.d ("ERROR", "se produjo un error"+e.getMessage());
                Toast.makeText(this, "se produjo un error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == gallery_request_code_2 && resultCode == RESULT_OK) {
            try {
                //transformar la URI en el archivo mImageFile
                mImageFile2 = FileUtil.from(this, data.getData());

                // mostrar la imagen en el primer cardview (Post1)
                mImgView_Post2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));


            } catch (Exception e){
                Log.d ("ERROR", "se produjo un error"+e.getMessage());
                Toast.makeText(this, "se produjo un error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
}