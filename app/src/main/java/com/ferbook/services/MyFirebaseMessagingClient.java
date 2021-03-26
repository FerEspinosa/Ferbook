package com.ferbook.services;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ferbook.channel.NotificationHelper;
import com.ferbook.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        String senderUsername = data.get("senderUsername");
        String receiverUsername = data.get("receiverUsername");
        String messagesJson = data.get("messages");
        String lastMessage = data.get("lastMessage");
        String senderImage = data.get("senderImage");
        String receiverImage = data.get("receiverImage");

        int notificationChatId = Integer.parseInt(data.get("notificationId"));

        Gson gson = new Gson();

        //la siguiente linea transforma el String "messagesJson" en un Array de objetos de tipo "Message"
        Message [] messages = gson.fromJson(messagesJson, Message[].class);

        //el siguiente bloque es para colocar las imagenes del sender y del receiver en la notificacion
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext()).load(senderImage).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                Picasso.with(getApplicationContext()).load(receiverImage).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {

                                        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                                        NotificationCompat.Builder builder =
                                                notificationHelper.getNotificationMessage(
                                                        messages,
                                                        senderUsername,
                                                        receiverUsername,
                                                        lastMessage,
                                                        bitmapSender,
                                                        bitmapReceiver
                                                );
                                        notificationHelper.getManager().notify(notificationChatId, builder.build());
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                });


    }
}
