package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.models.User;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    Button              btn_actualizar;
    TextInputEditText   tv_nombre, tv_telefono;

    Authprovider        mAuthProvider;
    UsersProvider       mUsersProvider;

    AlertDialog         mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mAuthProvider   = new Authprovider();
        mUsersProvider  = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        set_inputs();
        set_btn_actualizar();

    }

    private void set_inputs() {

        tv_nombre = findViewById(R.id.input_nombre);
        tv_telefono = findViewById(R.id.input_telefono);
    }

    private void set_btn_actualizar () {
        btn_actualizar = findViewById(R.id.btn_actualizar);
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                String nombre   = tv_nombre.getText().toString();
                String telefono = tv_telefono.getText().toString();

                // Validaciones de los campos de texto:
                if (!nombre.isEmpty()) {

                    actualizar_usuario(nombre, telefono);

                } else {
                    Toast.makeText(CompleteProfileActivity.this, "Completá todos los campos", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void actualizar_usuario(String nombre, String telefono) {

        String id = mAuthProvider.getUid();

        User user = new User();
        user.setId(id);
        user.setNombre(nombre);
        user.setTelefono(telefono);
        user.setTimestamp(new Date().getTime());

        mUsersProvider.update(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CompleteProfileActivity.this, "Se agregó el nombre de usuario correctamente", Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                    goHome();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(CompleteProfileActivity.this, "No se pudo guardar el nombre de usuario", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void goHome () {
        Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }

}