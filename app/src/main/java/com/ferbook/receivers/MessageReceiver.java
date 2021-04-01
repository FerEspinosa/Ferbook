package com.ferbook.receivers;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ferbook.activities.ChatActivity;
import com.ferbook.models.Message;
import com.ferbook.providers.MessageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

import static com.ferbook.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class MessageReceiver extends BroadcastReceiver {

    String mExtraSenderId;
    String mExtraReceiverId;
    String mExtraChatId;
    int mExtraNotificationId;

    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraSenderId      = intent.getExtras().getString("senderId");
        mExtraReceiverId    = intent.getExtras().getString("receiverId");
        mExtraChatId        = intent.getExtras().getString("chatId");
        mExtraNotificationId        = intent.getExtras().getInt("notificationChatId");

        //--------------para cerrar el input una vez se ha enviado la respuesta:
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mExtraNotificationId);
        //--------------

        String message = getMessageText(intent).toString();
        sendMessage(message);


    }

    private void sendMessage(String messageText) {
        Message message = new Message();
        message.setChatId(mExtraChatId);

        //aqui los parámetros (de senderId y receiverId) se entrecruzan,
        // puesto que quien recibe el mensaje se convertirá en sender, y viceversa
        message.setSenderId(mExtraReceiverId);
        message.setReceiverId(mExtraSenderId);

        message.setTimestamp(new Date().getTime());
        message.setViewed(false);
        message.setChatId(mExtraChatId);
        message.setMessage(messageText);

        MessageProvider messageProvider = new MessageProvider();

        messageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //getToken(message);
                }
            }
        });
    }

    private CharSequence getMessageText (Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput!= null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
