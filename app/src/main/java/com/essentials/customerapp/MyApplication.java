package com.essentials.customerapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.razorpay.Checkout;

/**
 * Created by Raj Aryan on 7/24/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class MyApplication extends Application {

    private static Context mAppContext;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        mInstance = this;
        this.setAppContext(getApplicationContext());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    private Activity mCurrentActivity = null;

    public void setCurrentActivity(Activity mCurrectActivity) {
        this.mCurrentActivity = mCurrectActivity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }
}
