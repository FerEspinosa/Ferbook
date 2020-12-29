package com.ferbook.providers;

import com.ferbook.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
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

    public Task<Void> update(User user) {
        Map <String, Object> map = new HashMap<>();
        map.put("nombre",user.getNombre());
        map.put("telefono",user.getTelefono());
        map.put("timestamp",new Date().getTime());
        map.put("profile_image",user.getProfile_image());
        map.put("cover_image",user.getCover_image());
        return mcollection.document(user.getId()).update(map);
    }

}
