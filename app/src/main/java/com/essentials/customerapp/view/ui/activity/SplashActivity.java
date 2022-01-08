package com.essentials.customerapp.view.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.essentials.customerapp.R;
import com.essentials.customerapp.utilities.MySharedPref;
import com.razorpay.Checkout;

public class SplashActivity extends AppCompatActivity {
    MySharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        sharedPref = new MySharedPref(this);
//        AppUtils.printHashKey(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCondition();
            }
        }, 2000);
    }

    private void checkCondition() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        this.finish();
    }
}
