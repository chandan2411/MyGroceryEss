package com.essentials.customerapp.fcm;


import android.util.Log;

import androidx.annotation.NonNull;

import com.essentials.customerapp.MyApplication;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

/**
 * Created by Raj Aryan on 6/29/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseInstanceIdSer";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if (remoteMessage.getData().size() > 0) {
            //handle the data message here
        }

        //getting the title and the body
        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();

        MyNotificationManager.getInstance((MyApplication)getApplicationContext()).displayNotification(title, body);
        Log.d("MyNotification", remoteMessage.getNotification().toString());
        Log.d("MyNotification", remoteMessage.getNotification().toString());


        //then here we can use the title and body to build a notification
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //now we will have the token
        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Log.d("MyRefreshedToken", token);
        FCMConstant.FCM_TOKEN = token;
    }
}