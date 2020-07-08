package com.skiplab.theselproject.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA2TNWtwA:APA91bGD9MMC744ZSCEOYktXtzoIoQsZ9LQwlHoRo_IBbUTiSV0wdTZHmXwL-rR07KW798lzw7JC4mymaMMMoJ4cAPykUOTpfmSU6I0MfUWh0NlH2yKtX8gDTdZzbanRXqkm4V6uoeCk"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
