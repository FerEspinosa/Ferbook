package com.ferbook.providers;

import com.ferbook.models.FCMBody;
import com.ferbook.models.FCMResponse;
import com.ferbook.retrofit.IFCMApi;
import com.ferbook.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider () { }
    
    public Call<FCMResponse> sendNotification (FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
