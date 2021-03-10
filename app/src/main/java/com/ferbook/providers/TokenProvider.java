package com.ferbook.providers;

import com.ferbook.models.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {
    CollectionReference mCollection;

    public TokenProvider () {
        mCollection = FirebaseFirestore.getInstance().collection("Token");
    }

    public void create (String userId) {
        if (userId==null){
            return;
        } else {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Token token = new Token (s);
                    mCollection.document(userId).set(token);
                }
            });
        }
    }

    public Task<DocumentSnapshot> getToken (String userId) {
        return mCollection.document(userId).get();
    }
}
