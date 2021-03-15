package com.ferbook.providers;

import com.ferbook.models.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatProvider {

    CollectionReference mcollection;

    public ChatProvider () {
        mcollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat){
       mcollection.document(chat.getIdUser1()+chat.getIdUser2()).set(chat);
    }

    public Query checkChatExists (String userId1, String userId2) {

        ArrayList<String> ids= new ArrayList<>();
        ids.add(userId1+userId2);
        ids.add(userId2+userId1);
        return mcollection.whereIn("id", ids);
    }

    public Query getAll (String userId) {

        return mcollection.whereArrayContains("ids", userId);
    }

}
