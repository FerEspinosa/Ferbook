package com.ferbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class registerActivity extends AppCompatActivity {

    CircleImageView btn_atras;
    Button btn_registrarse;
    TextInputEditText tv_nombre, tv_email, tv_contrasena, tv_confirmar_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

                String nombre       = tv_nombre.getText().toString();
                String email        = tv_email.getText().toString();
                String contrasena   = tv_contrasena.getText().toString();
                String confirmar    = tv_confirmar_contrasena.getText().toString();

                if (!nombre.isEmpty() && !email.isEmpty() && !contrasena.isEmpty() && !confirmar.isEmpty()) {

                    Toast.makeText(registerActivity.this, "Todos los campos completados", Toast.LENGTH_SHORT).show();

                    if (isEmailValid(email)){
                        Toast.makeText(registerActivity.this, "El email es válido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(registerActivity.this, "Ingresa un email válido", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(registerActivity.this, "Completá todos los campos", Toast.LENGTH_LONG).show();
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

}