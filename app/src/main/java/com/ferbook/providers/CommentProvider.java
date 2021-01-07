package com.ferbook.providers;

import com.ferbook.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentProvider {

    CollectionReference mCollection;

    public CommentProvider () {
        mCollection = FirebaseFirestore.getInstance().collection("comments");
    }

    public Task<Void> create (Comment comment) {
        return mCollection.document().set(comment);
    }

    public Query getCommentsByPost(String postId) {

        // devuelve todos los documentos donde el campo "postId" sea igual al postId pasado por parámetro
        return mCollection.whereEqualTo("postId", postId).orderBy("timestamp", Query.Direction.DESCENDING);
    }

}
