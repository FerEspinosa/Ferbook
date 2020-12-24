package com.ferbook.providers;

import com.ferbook.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mcollection;

    public UsersProvider () {
        mcollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser (String id) {
        return mcollection.document(id).get();
    }

    public Task<Void> create (User user) {
        return mcollection.document(user.getId()).set(user);
    }

    public Task<Void> updateNombre (User user) {
        Map <String, Object> map = new HashMap<>();
        map.put("nombre",user.getNombre());
        map.put("telefono",user.getTelefono());
        map.put("timestamp",user.getTimestamp());
        return mcollection.document(user.getId()).update(map);
    }

}
