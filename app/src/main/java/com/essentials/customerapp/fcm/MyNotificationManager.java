package com.essentials.customerapp.fcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.essentials.customerapp.MyApplication;
import com.essentials.customerapp.R;
import com.essentials.customerapp.view.ui.activity.HomeActivity;
import com.essentials.customerapp.view.ui.activity.OrderHistoryActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Raj Aryan on 6/29/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class MyNotificationManager {
    private Context mCtx;
    private static MyNotificationManager mInstance;
    private Intent resultIntent;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, FCMConstant.CHANNEL_ID)
                        .setSmallIcon(R.drawable.newappicon)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(body);

        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */
        if (title.equalsIgnoreCase("Order placed") || title.equalsIgnoreCase("Order Cancelled")
                || title.equalsIgnoreCase("Order is Out For Delivery") || title.equalsIgnoreCase("Order Delivered")) {
            resultIntent = new Intent(mCtx, OrderHistoryActivity.class);
            resultIntent.putExtra("ComeFromNotification", true);
        } else {
            resultIntent = new Intent(mCtx, HomeActivity.class);
        }

        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All paramters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        /*
         *  Setting the pending intent to notification builder
         * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
         * The first parameter is the notification id
         * better don't give a literal here (right now we are giving a int literal)
         * because using this id we can modify it later
         * */
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }
}
