package com.ferbook.retrofit;

import com.ferbook.models.FCMBody;
import com.ferbook.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAAP9kceeM:APA91bGr5njrsxnPA3OQqSArtIqgDyK73ul6FSBUN27_kkM_P8xN35kSPlm_P6Fp9PC6Br59GxDRNixmkO_ijCEuTw2NgXJ8NYdOyMzggn8RMTyTeExGCK3DRH0yNR2tf6MNZGah6ZQL"
    })
    @POST("fcm/send")
    Call<FCMResponse> send (@Body FCMBody body);
}
