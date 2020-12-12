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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class registerActivity extends AppCompatActivity {

    CircleImageView     btn_atras;
    Button              btn_registrarse;
    TextInputEditText   tv_nombre, tv_email, tv_contrasena, tv_confirmar_contrasena;
    Authprovider        mAuthProvider;
    UsersProvider       mUsersProvider;
    AlertDialog         mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuthProvider   = new Authprovider();
        mUsersProvider  = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        set_inputs();
        set_btn_atras();
        set_btn_registrarse();

    }

    private void set_inputs() {
        tv_nombre                 = findViewById(R.id.input_nombre);
        tv_email                  = findViewById(R.id.input_email);
        tv_contrasena             = findViewById(R.id.input_contrasena);
        tv_confirmar_contrasena   = findViewById(R.id.input_confirmar_contrasena);
    }

    private void set_btn_atras(){
        btn_atras = findViewById(R.id.btn_atras);
        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void set_btn_registrarse () {
        btn_registrarse = findViewById(R.id.btn_registrar);
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();

                String nombre       = tv_nombre.getText().toString();
                String email        = tv_email.getText().toString();
                String contrasena   = tv_contrasena.getText().toString();
                String confirmar    = tv_confirmar_contrasena.getText().toString();


                // Validaciones de los campos de texto:

                if (!nombre.isEmpty() && !email.isEmpty() && !contrasena.isEmpty() && !confirmar.isEmpty()) {
                    if (isEmailValid(email)){
                        if (contrasena.equals(confirmar)){
                            if (contrasena.length()>=6){

                                //REGISTRAR
                                crear_usuario(email, contrasena, nombre);

                            } else {
                                Toast.makeText(registerActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(registerActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(registerActivity.this, "Ingresa un email válido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(registerActivity.this, "Completá todos los campos", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void crear_usuario(String email, String contrasena, String nombre) {

        mAuthProvider.register(email, contrasena)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();

                    User user = new User();

                    user.setNombre(nombre);
                    user.setEmail(email);
                    user.setId(id);
                    mUsersProvider.create(user);

                    mUsersProvider.create(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(registerActivity.this, "Se registró el usuario y se guardó el nombre en la base de datos", Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                                goHome();
                            } else {
                                Toast.makeText(registerActivity.this, "No se pudo guardar el nombre de usuario", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    mDialog.dismiss();
                    Toast.makeText(registerActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void goHome () {
        Intent intent = new Intent(registerActivity.this, HomeActivity.class);
        startActivity(intent);

    }

}