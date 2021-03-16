package com.ferbook.providers;

import com.ferbook.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MessageProvider {

    CollectionReference mCollection;

    public MessageProvider () {
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }

    public Task<Void> create (Message message) {
        DocumentReference document = mCollection.document();
        message.setId(document.getId());
        return document.set(message);
    }
}
