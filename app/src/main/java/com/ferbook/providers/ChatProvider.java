package com.ferbook.providers;

import com.ferbook.models.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatProvider {

    CollectionReference mcollection;

    public ChatProvider () {
        mcollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat){
        mcollection.document(chat.getIdUser1()).collection("Users").document(chat.getIdUser2()).set(chat);
        mcollection.document(chat.getIdUser2()).collection("Users").document(chat.getIdUser1()).set(chat);

    }

}
