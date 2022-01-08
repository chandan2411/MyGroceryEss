package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.databinding.ActivityMyWalletBinding;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.viewmodel.WalletViewModel;

import java.util.Locale;

public class MyWalletActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityMyWalletBinding walletBinding;
    private WalletViewModel viewModel;
    private MySharedPref sharedPref;
    private  TextView knowMore, knowMoreWalletDetails;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewAndModel();
        knowMore = findViewById(R.id.tvKnowMore);
        knowMoreWalletDetails = findViewById(R.id.knowMoreDetails);
        sharedPref = new MySharedPref(this);
        viewModel.init(sharedPref.readString(PrefConstant.USER_ID, ""), sharedPref.readString(PrefConstant.WALLED_REF_CODE, ""));

        viewModel.getWalletData().observe(this, walletModels -> {
            dismissDialog();
            String roundedWalletAmount = String.format(Locale.ENGLISH,"%.2f",  walletModels.get(0).getWalletTotalBalance());
            walletBinding.tvWalletAmount.setText(roundedWalletAmount);
            walletBinding.tvWalletAmount1.setText(roundedWalletAmount);
        });

        knowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knowMoreWalletDetails.setVisibility(View.VISIBLE);
            }
        });

    }

    private static Dialog dialog;
    public static void showDialog(final Activity activity, String msg){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCancelable(true);
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        dialog.show();
    }

    public static void dismissDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }


    private void bindViewAndModel() {
        walletBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_wallet);
        viewModel = ViewModelProviders.of(this).get(WalletViewModel.class);
        walletBinding.setViewModel(viewModel);
        walletBinding.setLifecycleOwner(this);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        walletBinding.tvTitle.setText(getResources().getString(R.string.my_wallet));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MyWalletActivity.this.finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        return super.onOptionsItemSelected(item);
    }


    public void whatsAppShare(View view) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
     /*   whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. With " +
                "Essentials app you can buy all your household needs including - Grocery, household " +
                "items, personal care products " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
                "Download app now! and use my referral code"
                + sharedPref.readString(PrefConstant.WALLED_ID, "") + "to get wallet amount");*/
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. " +
                "With Essentials app you can buy all your household needs including - Grocery, " +
                "household items, personal care products, Electronics, Books & Stationaries, Fruits and Vegetables " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
//                "Download app now! and use my referral code " +
//                sharedPref.readString(PrefConstant.WALLED_ID, "") + " to get wallet amount");
                "Download the app & get ₹200 instant essentials cash in your wallet");
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            CustomToast.showRectToast(this, view, "Whatsapp have not been installed.");
        }
    }

    public void instaShare(View view) {
        Intent instaIntent = new Intent(Intent.ACTION_SEND);
        instaIntent.setType("text/plain");
        instaIntent.setPackage("com.instagram.android");
      /*  instaIntent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. With " +
                "Essentials app you can buy all your household needs including - Grocery, household " +
                "items, personal care products " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
                "Download app now! and use my referral code"
                + sharedPref.readString(PrefConstant.WALLED_REF_CODE, "") + "to get wallet amount");*/
        instaIntent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. " +
                "With Essentials app you can buy all your household needs including - Grocery, " +
                "household items, personal care products, Electronics, Books & Stationaries, Fruits and Vegetables " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
//                "Download app now! and use my referral code " +
//                sharedPref.readString(PrefConstant.WALLED_ID, "") + " to get wallet amount");
                "Download the app & get ₹200 instant essentials cash in your wallet");
        try {
            startActivity(instaIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            CustomToast.showRectToast(this, view, "Whatsapp have not been installed.");
        }
    }

    public void textShare(View view) {
       /* String smsBody="Supermarket is now just a click away. With " +
                "Essentials app you can buy all your household needs including - Grocery, household " +
                "items, personal care products " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
                "Download app now! and use my referral code";*/
        String smsBody= "Supermarket is now just a click away. " +
                "With Essentials app you can buy all your household needs including - Grocery, " +
                "household items, personal care products, Electronics, Books & Stationaries, Fruits and Vegetables " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
                "Download the app & get ₹200 instant essentials cash in your wallet";
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    public void moreShare(View view) {
            Intent shareintent = new Intent();
            shareintent.setAction(Intent.ACTION_SEND);
           /* shareintent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. " +
                    "With Essentials app you can buy all your household needs including - Grocery, " +
                    "household items, personal care products " +
                    "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
                    "Download app now! and use my referral code" +
                    sharedPref.readString(PrefConstant.WALLED_ID, "") + "to get wallet amount");*/
           shareintent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. " +
                "With Essentials app you can buy all your household needs including - Grocery, " +
                "household items, personal care products, Electronics, Books & Stationaries, Fruits and Vegetables " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
//                "Download app now! and use my referral code " +
//                sharedPref.readString(PrefConstant.WALLED_ID, "") + " to get wallet amount");
                "Download the app & get ₹200 instant essentials cash in your wallet");
            shareintent.setType("text/plain");
            startActivity(Intent.createChooser(shareintent, "fabShare via"));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }
}
