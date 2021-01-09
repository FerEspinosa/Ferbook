package com.ferbook.providers;

import com.ferbook.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikesProviders {

    CollectionReference mCollection;

    public LikesProviders (){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create (Like like) {

        DocumentReference document = mCollection.document();
        String likeId = document.getId();
        like.setLikeId(likeId);

        return document.set(like);
    }

    public Query getLikeByPostAndUser (String postId, String userId) {
        return mCollection.whereEqualTo("postId", postId).whereEqualTo("userId",userId);
    }

    public Task<Void> delete (String likeId) {
        return mCollection.document(likeId).delete();
    }

}
