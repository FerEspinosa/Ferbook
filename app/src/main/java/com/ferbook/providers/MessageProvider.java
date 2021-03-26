package com.ferbook.providers;

import com.ferbook.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

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

    public Query getMessagesByChat (String chatId){
        return mCollection.whereEqualTo("chatId", chatId).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Query getMessagesByChatAndSender (String chatId, String senderId){
        return mCollection.whereEqualTo("chatId", chatId).whereEqualTo("senderId", senderId).whereEqualTo("viewed",false);
    }

    public Query getLastThreeMessagesByChatAndSender (String chatId, String senderId){
        return mCollection.whereEqualTo("chatId", chatId)
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("viewed",false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);
    }

    public Query getLastMessage (String chatId){
        return mCollection.whereEqualTo("chatId", chatId).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Query getSenderLastMessage (String chatId, String senderId){
        return mCollection.whereEqualTo("chatId", chatId).whereEqualTo("senderId",senderId).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Query getUnreadMessages (String chatId) {
        return mCollection.whereEqualTo("chatId", chatId).whereEqualTo("viewed",false);
    }

    public Task <Void> updateViewed (String documentId, boolean state) {
        Map <String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return mCollection.document(documentId).update(map);
    }
}
