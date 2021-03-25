package com.ferbook.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ferbook.channel.NotificationHelper;
import com.ferbook.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null){
            if (title.equals("Nuevo mensaje")){

                showNotificationMessages(data);

            } else {
                showNotification(title,body);
            }
        }
    }

    private void showNotification (String title, String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getManager().notify(n, builder.build());
    }

    private void showNotificationMessages (Map<String, String> data){
        String title = data.get("title");
        String body = data.get("body");
        String messagesJson = data.get("messages");
        Gson gson = new Gson();

        //la siguiente linea transforma el String "messagesJson" en un Array de objetos de tipo "Message"
        Message [] messages = gson.fromJson(messagesJson, Message[].class);

        int notificationChatId = Integer.parseInt(data.get("notificationId"));
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationMessage(messages);
        notificationHelper.getManager().notify(notificationChatId, builder.build());
    }
}
