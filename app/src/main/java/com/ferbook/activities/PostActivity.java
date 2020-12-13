package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.providers.ImageProvider;
import com.ferbook.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    ImageView           mImgView_Post1;
    private final int   gallery_request_code = 1;
    File                mImageFile;
    Button              mButtonPost;
    ImageProvider       mImageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();

        mImgView_Post1 = findViewById(R.id.iv_post1);
        mImgView_Post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mButtonPost = findViewById(R.id.btn_publicar);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });


    }

    private void saveImage() {
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PostActivity.this, "La imagen se almacenó correctamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //La siguiente línea configura que el Intent abre la galería del teléfono
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallery_request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_request_code && resultCode == RESULT_OK) {
            try {
                //transformar la URI en el archivo mImageFile
                mImageFile = FileUtil.from(this, data.getData());

                // mostrar la imagen en el primer cardview (Post1)
                mImgView_Post1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));


            } catch (Exception e){
                Log.d ("ERROR", "se produjo un error"+e.getMessage());
                Toast.makeText(this, "se produjo un error"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
}