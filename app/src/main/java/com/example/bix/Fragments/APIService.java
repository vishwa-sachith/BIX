package com.example.bix.Fragments;

import com.example.bix.Notifications.MyResponse;
import com.example.bix.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAhwNxE7A:APA91bEreK42w6qdIKUz7CUG1rcXN4BbmP4PSsdJ5iKZE5E_mkeyg5IkfbW5JqfagCZLHFUFxIP_JPGXsr8mPOHopBZGJRKq67oj-vDLLRR3mISlFmBM4fTgQg4A0TNW__wmiHR2BQSx"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
