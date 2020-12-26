package com.ferbook.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.utils.FileUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView     mImageView_Back_button;
    Button              mButton_update;

    CircleImageView     mImageView_PROFILE;
    ImageView           mImageView_COVER;
    TextInputEditText   mInput_name;
    TextInputEditText   mInput_phone;


    AlertDialog.Builder mBuilderSelector;
    CharSequence[]      options;

    // PROFILE GALERIA
    private final int   request_code_GALLERY_PROFILE = 1;
    File                mImageFile_GALLERY_PROFILE = null;

    // COVER GALERIA
    private final int   request_code_GALLERY_COVER = 2;
    File                mImageFile_GALLERY_COVER = null;

    // PROFILE CAMARA
    private  final int  request_code_PHOTO_PROFILE = 3;
    File                mPhotoFile_PHOTO_PROFILE;
    String              mAbsolutePhotoPath_PHOTO_PROFILE;
    String              mPhotoPath_PHOTO_PROFILE;

    // COVER CAMARA
    private  final int  request_code_PHOTO_COVER = 4;
    File                mPhotoFile_PHOTO_COVER;
    String              mAbsolutePhotoPath_PHOTO_COVER;
    String              mPhotoPath_PHOTO_COVER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mInput_name  = findViewById(R.id.input_nombre);
        mInput_phone = findViewById(R.id.input_telefono);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de la galería","Tomar foto"};

        // IMAGEN DE PERFIL
        mImageView_PROFILE = findViewById(R.id.circleImage_Profile);
        mImageView_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CAMBIAR FOTO DE PERFIL
                selectOptionImage("PROFILE");
            }
        });

        // IMAGEN DE PORTADA
        mImageView_COVER = findViewById(R.id.imageViewCover);
        mImageView_COVER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CAMBIAR FOTO DE PORTADA
                selectOptionImage("COVER");
            }
        });

        // BOTON ACTUALIZAR
        mButton_update = findViewById(R.id.btn_actualizar);
        mButton_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ACTUALIZAR DATOS
            }
        });

        // BOTON ATRAS
        mImageView_Back_button = findViewById(R.id.btn_atras);
        mImageView_Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void selectOptionImage(final String whichImage) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // GALERIA
                if (which == 0) {

                        // PERFIL:
                    if (whichImage.equals("PROFILE")){
                        openGallery(request_code_GALLERY_PROFILE);

                        // PORTADA:
                    } else if (whichImage.equals("COVER")){
                        openGallery(request_code_GALLERY_COVER);
                    }

                // CAMARA
                } else if (which == 1) {

                        // PERFIL
                    if (whichImage.equals("PROFILE")){

                        takePhoto(request_code_PHOTO_PROFILE);

                        // PORTADA
                    } else if (whichImage.equals("COVER")){

                        takePhoto(request_code_PHOTO_COVER);

                    }

                }
            }
        });

        mBuilderSelector.show();
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //La siguiente línea configura que el Intent abre la galería del teléfono
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    private void takePhoto(int requestCode) {

        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager())!=null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);

            } catch(Exception e) {
                Toast.makeText(this, "Hubo un error en el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.ferbook",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }

        }

    }

    private File createPhotoFile( int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile  = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        if (requestCode == request_code_PHOTO_PROFILE) {

            mPhotoPath_PHOTO_PROFILE = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath_PHOTO_PROFILE  =  photoFile.getAbsolutePath();

        } else if (requestCode == request_code_PHOTO_COVER) {
            mPhotoPath_PHOTO_COVER = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath_PHOTO_COVER  =  photoFile.getAbsolutePath();
        }

        return photoFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GALLERY PROFILE
        if (requestCode == request_code_GALLERY_PROFILE && resultCode == RESULT_OK) {

            try {

                //transformar la URI en el archivo mImageFile
                mImageFile_GALLERY_PROFILE = FileUtil.from(this, data.getData());

                // mostrar la imagen en el primer cardview (Post1)
                mImageView_PROFILE.setImageBitmap(BitmapFactory.decodeFile(mImageFile_GALLERY_PROFILE.getAbsolutePath()));


            } catch (Exception e){
                Log.d ("ERROR", "se produjo un error"+e.getMessage());
                Toast.makeText(this, "se produjo un error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // GALLERY COVER
        if (requestCode == request_code_GALLERY_COVER && resultCode == RESULT_OK) {

            try {

                //transformar la URI en el archivo mImageFile2
                mImageFile_GALLERY_COVER = FileUtil.from(this, data.getData());

                // mostrar la imagen en el primer cardview (Post1)
                mImageView_COVER.setImageBitmap(BitmapFactory.decodeFile(mImageFile_GALLERY_COVER.getAbsolutePath()));


            } catch (Exception e){
                Log.d ("ERROR", "se produjo un error"+e.getMessage());
                Toast.makeText(this, "se produjo un error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // CAMERA PROFILE
        if (requestCode == request_code_PHOTO_PROFILE && resultCode == RESULT_OK){

            mPhotoFile_PHOTO_PROFILE = new File(mAbsolutePhotoPath_PHOTO_PROFILE);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath_PHOTO_PROFILE).into(mImageView_PROFILE);
        }

        // CAMERA COVER
        if (requestCode == request_code_PHOTO_COVER && resultCode == RESULT_OK){

            mPhotoFile_PHOTO_COVER = new File(mAbsolutePhotoPath_PHOTO_COVER);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath_PHOTO_COVER).into(mImageView_COVER);

        }

    }
}