package com.ferbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ferbook.R;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView     mImageView_Back_button;
    Button              mButton_update;

    CircleImageView     mImageView_profile;
    ImageView           mImageView_cover;
    TextInputEditText   mInput_name;
    TextInputEditText   mInput_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mInput_name             = findViewById(R.id.input_nombre);
        mInput_phone            = findViewById(R.id.input_telefono);

        // IMAGEN DE PERFIL
        mImageView_profile      = findViewById(R.id.circleImage_Profile);
        mImageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CAMBIAR FOTO DE PERFIL
            }
        });

        // IMAGEN DE PORTADA
        mImageView_cover        = findViewById(R.id.imageViewCover);

        // BOTON ACTUALIZAR
        mButton_update          = findViewById(R.id.btn_actualizar);
        mButton_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ACTUALIZAR DATOS
            }
        });

        // BOTON ATRAS
        mImageView_Back_button  = findViewById(R.id.btn_atras);
        mImageView_Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}