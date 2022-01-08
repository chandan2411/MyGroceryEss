package com.essentials.customerapp.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.essentials.customerapp.R;
import com.essentials.customerapp.view.ui.activity.HomeActivity;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Raj Aryan on 7/3/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class LocalNotificationTrigger {
    Context mContext;

    public LocalNotificationTrigger(Context mContext) {
        this.mContext = mContext;
    }

    public void createNotificationChannel(String title, String message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) ((Activity) mContext).getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(FCMConstant.CHANNEL_ID, FCMConstant.CHANNEL_NAME, importance);
            mChannel.setDescription(FCMConstant.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        MyNotificationManager.getInstance(mContext).displayNotification(title, message);


    }

    public void subscribeTopics() {
        /*For app notification like order out for delivery and all that*/
        FirebaseMessaging.getInstance().subscribeToTopic("Notification");
        /*For promotion like buy 1 get 1 free and all that*/
        FirebaseMessaging.getInstance().subscribeToTopic("Promotional");
        /*For warnings like we will delayed due to some critical situation and all that */
        FirebaseMessaging.getInstance().subscribeToTopic("Warnings");
    }
}
