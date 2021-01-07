package com.ferbook.providers;

import com.ferbook.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LikesProviders {

    CollectionReference mCollection;

    public LikesProviders (){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create (Like like) {
        return mCollection.document().set(like);
    }

}
