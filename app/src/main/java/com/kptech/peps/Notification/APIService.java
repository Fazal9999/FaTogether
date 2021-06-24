package com.kptech.peps.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAAm8psKo:APA91bGgUF4x5SfVWtav1NwcrKoxRcLhL0nS54yN_gwoVJ5htY5YpuvcywcLnO_deN_u6hmoCTDOuZz2ukTq1efjSzXQM7iznxzEx3awG59R3o8oH9_wnHpdJaCr9iSJksBGPuSEh99U"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
