package com.ferbook.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.ferbook.R;
import com.ferbook.channel.NotificationHelper;
import com.ferbook.models.Message;
import com.ferbook.receivers.MessageReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY = "NotifiicationReply";

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
        String senderId = data.get("senderId");
        String receiverId = data.get("receiverId");
        String chatId = data.get("chatId");

        int notificationChatId = Integer.parseInt(data.get("notificationId"));

                                        //-----------------Inicio crear un botón en la notificación
        // (para el PendingIntent se creó un nuevo paquete "receivers"
        // y dentro, una nueva clase "message receivers".
        // También se registró esa nueva clase en el Manifest)
        Intent intent = new Intent(this, MessageReceiver.class);
        intent.putExtra("senderId", senderId);
        intent.putExtra("receiverId", receiverId);
        intent.putExtra("chatId", chatId);
        intent.putExtra("notificationChatId", notificationChatId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("tu mensaje...").build();

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        //el action creado se pasa como parámetro al método "getNotificationMessage"
        // Además se modificó el NotificationHelper

                                            //-----------------Fin crear un botón en la notificación

        Gson gson = new Gson();

        //la siguiente linea transforma el String "messagesJson" en un Array de objetos de tipo "Message"
        Message [] messages = gson.fromJson(messagesJson, Message[].class);

        //el siguiente bloque es para colocar las imagenes del sender y del receiver en la notificacion
        // Acá además se pasa el parámetro para mostrar el botón para responder dentro de la notificación
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
                                                    bitmapReceiver,
                                                    // la siguiente acción es la que permite responder desde la notificación
                                                    action
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
