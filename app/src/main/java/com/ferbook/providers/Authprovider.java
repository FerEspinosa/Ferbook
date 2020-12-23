package com.ferbook.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Authprovider {

    private final FirebaseAuth mAuth;

    public Authprovider(){
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login (String email, String contrasena){
        return mAuth.signInWithEmailAndPassword(email,contrasena);
    }

    public Task<AuthResult> googleLogin (GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }

    public String getUid (){
        if (mAuth.getCurrentUser()!=null){
        return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    public FirebaseUser getUserSession (){
        if (mAuth.getCurrentUser()!=null){
        return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    public String getEmail (){
        if (mAuth.getCurrentUser()!=null){
            return mAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    public Task<AuthResult> register(String email, String contrasena) {
        return mAuth.createUserWithEmailAndPassword(email, contrasena);
    }

    public void logout (){
        if (mAuth != null){
            mAuth.signOut();
        }
    }

}
