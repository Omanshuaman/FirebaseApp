package com.example.firebaseapp2.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json", "Authorization:key=AAAA_ESmqNw:APA91bFQI_TCzMhKGq2wuX7UFK-V9rxZh2_Db6O3P87WybjvRIt5olovm-67QpDV64N-eTOrTOTAHx-RixyCqSkW5Hh5ZSecjB_voOrAJqusLfoXhlOc6uyxCCEs82D396XraHWQd7qN"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
