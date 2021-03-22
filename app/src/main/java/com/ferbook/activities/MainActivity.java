package com.ferbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ferbook.R;
import com.ferbook.models.User;
import com.ferbook.providers.Authprovider;
import com.ferbook.providers.UsersProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    TextView                    tv_registrate;
    TextInputEditText           input_email, input_contrasena;
    Button                      btn_login;
    Authprovider                mAuthProvider;
    private GoogleSignInClient  mGoogleSignInClient;
    private UsersProvider       mUsersProvider;
    private final int           RC_SIGN_IN = 1;
    SignInButton                btn_Google;
    AlertDialog                 mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthProvider   = new Authprovider();
        mUsersProvider  = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        set_inputs();
        set_btn_registrarse();
        set_btn_login();
        set_btn_google();

    }

    private void set_btn_google() {
        btn_Google = findViewById(R.id.btn_SignInGoogle);
        btn_Google.setOnClickListener(v -> signInGoogle());
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void set_btn_login() {
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> login());
    }

    private void set_btn_registrarse() {
        tv_registrate = findViewById(R.id.tvRegistrateAqui);
        tv_registrate.setOnClickListener(v -> {
            Intent intent = new Intent (MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void set_inputs () {
        input_email         = findViewById(R.id.input_email);
        input_contrasena    = findViewById(R.id.input_contrasena);
    }

    private void login() {

        mDialog.show();
        String email        = input_email.getText().toString();
        String contrasena   = input_contrasena.getText().toString();


        mAuthProvider.login(email, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    goHome();

                } else {
                    Toast.makeText(MainActivity.this, "Los datos no son correctos", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        mDialog.show();

        mAuthProvider.googleLogin(account).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // obtener el id del usuario actual
                            String id = mAuthProvider.getUid();

                            checkUserExist(id);

                        } else {

                            mDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("Error", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "No se pudo iniciar sesion con Google", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    private void checkUserExist(String id) {
        //chequear coleccion 'Users', documento 'id'

        mUsersProvider.getUser(id)
                //el OnSuccessListener verifica si hay algun documento con ese id
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // si el documento existe
                if (documentSnapshot.exists()) {
                    mDialog.dismiss();
                    goHome();
                } else {
                    //  si no existe el documento con el id del usuario de Google que ingresó recién,
                    //  entonces crear un documento con ese id y agregarle los datos (el email nomas)

                    //  obtener el email del usuario registrado
                    String email = mAuthProvider.getEmail();

                    // crear un objeto de tipo User
                    User user = new User();

                    // Guardar el email y el id en ese objeto
                    user.setEmail(email);
                    user.setId(id);

                    // Crear el usuario
                    mUsersProvider.create(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo agregar el mail a la base de datos", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void goHome () {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthProvider.getUserSession() != null){
            goHome();
        }

    }
}