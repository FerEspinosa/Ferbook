package com.ferbook.activities;

import androidx.annotation.NonNull;
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
import com.ferbook.models.User;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.ImageProvider;
import com.ferbook.providers.UsersProvider;
import com.ferbook.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView     mImageView_Back_button;
    Button              mButton_update;

    CircleImageView     mImageView_PROFILE;
    ImageView           mImageView_COVER;

    TextInputEditText   mInput_name;
    String              mName = "";

    TextInputEditText   mInput_phone;
    String              mPhone = "";

    String              mProfileImage ="";
    String              mCoverImage   ="";

    AlertDialog         mWaitDialog;

    AlertDialog.Builder mBuilderSelector;
    CharSequence[]      options;

    // PROFILE GALERIA
    private final int   request_code_GALLERY_PROFILE = 1;
    File                mImageFile_GALLERY_PROFILE = null;

    // COVER GALERIA
    private final int   request_code_GALLERY_COVER = 2;
    File                mImageFile_GALLERY_COVER = null;

    // PROFILE CAMARA
    private  final int  request_code_CAMERA_PROFILE = 3;
    File                mImageFile_CAMERA_PROFILE;
    String              mAbsolutePhotoPath_CAMERA_PROFILE;
    String              mPhotoPath_CAMERA_PROFILE;

    // COVER CAMARA
    private  final int  request_code_CAMERA_COVER = 4;
    File                mImageFile_CAMERA_COVER;
    String              mAbsolutePhotoPath_CAMERA_COVER;
    String              mPhotoPath_CAMERA_COVER;

    // PROVIDERS
    ImageProvider       mImageProvider;
    UsersProvider       mUsersProvider;
    Authprovider        mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mInput_name  = findViewById(R.id.input_nombre);
        mInput_phone = findViewById(R.id.input_telefono);

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider  = new Authprovider();

        mWaitDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

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

                clickEditProfile();

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

        getUser();

    }

    private void getUser() {
        mUsersProvider.getUser(mAuthProvider.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("nombre")) {

                        mName   = documentSnapshot.getString("nombre");
                        mInput_name.setText(mName);
                    }

                    if (documentSnapshot.contains("telefono")){

                        mPhone  = documentSnapshot.getString("telefono");
                        mInput_phone.setText(mPhone);
                    }

                    if (documentSnapshot.contains("profile_image")){

                        mProfileImage = documentSnapshot.getString("profile_image");

                        if (mProfileImage != null){
                            if (!mProfileImage.isEmpty()){
                                Picasso.with(EditProfileActivity.this).load(mProfileImage).into(mImageView_PROFILE);
                            }
                        }
                    }

                    if (documentSnapshot.contains("cover_image")){

                        mCoverImage = documentSnapshot.getString("cover_image");

                        if (mCoverImage != null) {
                            if (!mCoverImage.isEmpty()){

                                Picasso.with(EditProfileActivity.this).load(mCoverImage).into(mImageView_COVER);
                            }
                        }

                    }
                }
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

                        takePhoto(request_code_CAMERA_PROFILE);

                        // PORTADA
                    } else if (whichImage.equals("COVER")){

                        takePhoto(request_code_CAMERA_COVER);

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

        if (requestCode == request_code_CAMERA_PROFILE) {

            mPhotoPath_CAMERA_PROFILE = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath_CAMERA_PROFILE =  photoFile.getAbsolutePath();

        } else if (requestCode == request_code_CAMERA_COVER) {
            mPhotoPath_CAMERA_COVER = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath_CAMERA_COVER =  photoFile.getAbsolutePath();
        }

        return photoFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GALLERY PROFILE
        if (requestCode == request_code_GALLERY_PROFILE && resultCode == RESULT_OK) {

            mImageFile_CAMERA_PROFILE = null;

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

            mImageFile_CAMERA_COVER = null;

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
        if (requestCode == request_code_CAMERA_PROFILE && resultCode == RESULT_OK){

            mImageFile_GALLERY_PROFILE = null;

            mImageFile_CAMERA_PROFILE = new File(mAbsolutePhotoPath_CAMERA_PROFILE);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath_CAMERA_PROFILE).into(mImageView_PROFILE);
        }

        // CAMERA COVER
        if (requestCode == request_code_CAMERA_COVER && resultCode == RESULT_OK){

            mImageFile_GALLERY_COVER = null;

            mImageFile_CAMERA_COVER = new File(mAbsolutePhotoPath_CAMERA_COVER);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath_CAMERA_COVER).into(mImageView_COVER);

        }

    }

    private void clickEditProfile() {

        mWaitDialog.show();

        mName = mInput_name.getText().toString();
        mPhone = mInput_phone.getText().toString();

        if (!mName.isEmpty() && !mPhone.isEmpty()){

            if (mImageFile_GALLERY_PROFILE != null && mImageFile_GALLERY_COVER != null){
                // imagen de perfil y portada de la galería
                saveBothImages(mImageFile_GALLERY_PROFILE,mImageFile_GALLERY_COVER);

            } else if (mImageFile_CAMERA_PROFILE != null && mImageFile_CAMERA_COVER != null){
                // imagen de perfil y portada de la camara
                saveBothImages(mImageFile_CAMERA_PROFILE, mImageFile_CAMERA_COVER);

            } else if (mImageFile_GALLERY_PROFILE != null && mImageFile_CAMERA_COVER != null){
                // imagen de perfil de la galería y portada de la camara
                saveBothImages(mImageFile_GALLERY_PROFILE, mImageFile_CAMERA_COVER);

            } else if (mImageFile_CAMERA_PROFILE != null && mImageFile_GALLERY_COVER!= null){
                // imagen de perfil de la camara y portada de la galeria
                saveBothImages(mImageFile_CAMERA_PROFILE,mImageFile_GALLERY_COVER);

            }  else if (mImageFile_CAMERA_PROFILE != null){
                // sólo imagen de perfil de la camara
                saveSingleImage(mImageFile_CAMERA_PROFILE, true);

            }  else if (mImageFile_GALLERY_PROFILE != null){
                // sólo imagen de perfil de la galería
                saveSingleImage(mImageFile_GALLERY_PROFILE, true);

            }   else if (mImageFile_CAMERA_COVER != null){
                // sólo imagen de portada de la camara
                saveSingleImage(mImageFile_CAMERA_COVER, false);
            }
            else if (mImageFile_GALLERY_COVER != null){
                // sólo imagen de portada de la galería
                saveSingleImage(mImageFile_GALLERY_COVER, false);
            } else {
                User user = new User();
                user.setId(mAuthProvider.getUid());
                user.setNombre(mName);
                user.setTelefono(mPhone);
                user.setProfile_image(mProfileImage);
                user.setCover_image(mCoverImage);
                updateUserProfile(user);

                mWaitDialog.dismiss();
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Completa los campos de texto", Toast.LENGTH_SHORT).show();
        }


    }

    private void saveBothImages(File imageFile1, File imageFile2) {


        mImageProvider.save(EditProfileActivity.this, imageFile1)
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

                            final String url_Profile = uri.toString();

                            //Una vez guardada la imagen 1 y obtenida su Url, hacer lo mismo con imagen 2:

                            //guardar imagen 2:
                            mImageProvider.save(EditProfileActivity.this, imageFile2)
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

                                                        String url_Cover = uri2.toString();

                                                        // ya se guardaron ambas imagenes y tengo ambas url's
                                                        // ahora PUBLICAR (GUARDAR los datos de la publicación en la base de datos)

                                                        User user = new User();
                                                        user.setNombre(mName);
                                                        user.setTelefono(mPhone);
                                                        user.setProfile_image(url_Profile);
                                                        user.setCover_image(url_Cover);
                                                        user.setId(mAuthProvider.getUid());

                                                        updateUserProfile(user);
                                                    }
                                                });

                                    } else {
                                        mWaitDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "Error al guardar la imagen de portada", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });

                } else {
                    mWaitDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error al almacenar la imagen de perfil", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void saveSingleImage(File imageFile, boolean isProfileImage){

        mImageProvider.save(EditProfileActivity.this, imageFile)
        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){

                    // La imagen 1 se guardó correctamente en storage

                    // obtener url de imagen
                    mImageProvider.getStorage().getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            final String url = uri.toString();

                            User user = new User();

                            user.setId(mAuthProvider.getUid());
                            user.setNombre(mName);
                            user.setTelefono(mPhone);

                            if (isProfileImage){
                                user.setProfile_image(url);
                                user.setCover_image(mCoverImage);

                            } else {
                                user.setCover_image(url);
                                user.setProfile_image(mProfileImage);
                            }

                            updateUserProfile(user);
                        }
                    });

                } else {
                    mWaitDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error al almacenar la imagen de perfil", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updateUserProfile(User user) {

        if (mWaitDialog.isShowing()){
            mWaitDialog.show();
        }

        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskSave) {

                mWaitDialog.dismiss();

                if (taskSave.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "La información se actualizó correctamente", Toast.LENGTH_LONG).show();

                    //clearForm();

                } else {
                    Toast.makeText(EditProfileActivity.this, "No se pudo actualizar la información", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}