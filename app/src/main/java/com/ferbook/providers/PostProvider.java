package com.ferbook.providers;

import com.ferbook.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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


    // En el siguiente método, el parámetro "id" se refiere al "id" del usuario
    // Y recupera TODOS los postS de ese usuario
    public Query getPostsByUser(String id) {

        // devuelve todos los documentos donde el campo "id" sea igual al id que se pasa por parámetro
        return mCollection.whereEqualTo("id", id);
    }

    // En el siguiente método, el parámetro "id" se refiere al "id" del POST que quiero obtener
    // Y recupera un sólo Post
    public Task <DocumentSnapshot> getPostById(String id) {

        // devuelve el documento con el "id" que se pasa por parámetro
        return mCollection.document(id).get();
    }

}
