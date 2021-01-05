package com.ferbook.providers;

import com.ferbook.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentProvider {

    CollectionReference mCollection;

    public CommentProvider () {
        mCollection = FirebaseFirestore.getInstance().collection("comments");
    }

    public Task<Void> create (Comment comment) {
        return mCollection.document().set(comment);
    }

}
