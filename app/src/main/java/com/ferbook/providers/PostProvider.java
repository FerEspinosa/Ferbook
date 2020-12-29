package com.ferbook.providers;

import com.ferbook.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider () {
        mCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public Task<Void> save(Post post){
        return mCollection.document().set(post);
    }

    public Query getAll(){
        return mCollection.orderBy("titulo", Query.Direction.DESCENDING);
    }


    public Query getPostsByUser(String id) {

        // devuelve todos los documentos donde el campo "id" sea igual al id que se pasa por parámetro
        return mCollection.whereEqualTo("id", id);
    }

}
