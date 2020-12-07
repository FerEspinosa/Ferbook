package com.ferbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextView            tv_registrate;
    TextInputEditText   input_email, input_contrasena;
    Button              btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set_inputs();
        set_btn_registrarse();
        set_btn_login();

    }

    private void set_btn_login() {
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void set_btn_registrarse() {
        tv_registrate = findViewById(R.id.tvRegistrateAqui);
        tv_registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void set_inputs () {
        input_email         = findViewById(R.id.input_email);
        input_contrasena    = findViewById(R.id.input_contrasena);
    }

    private void login() {
        String email        = input_email.getText().toString();
        String contrasena   = input_contrasena.getText().toString();
        Toast.makeText(this, "El mail es: "+email+ " y la contraseña es: "+contrasena, Toast.LENGTH_LONG).show();

    }

}