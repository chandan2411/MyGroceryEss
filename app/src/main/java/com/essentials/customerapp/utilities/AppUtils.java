package com.essentials.customerapp.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AppUtils {

    private static KProgressHUD hud;
    private static final String TAG = "AppUtils";

    public static void show(Context context, String message) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setLabel(message)
                .setDimAmount(0.5f)
                .show();
    }

    public static void show(Context context) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public static void dismissProgressDialog() {
        if (hud != null && hud.isShowing())
            hud.dismiss();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static int calculateDiscount(String product_mrp, String product_sp) {
        double discount = 0;
        try {
            discount = ((Double.parseDouble(product_mrp) - Double.parseDouble(product_sp)) * 100) / Double.parseDouble(product_mrp);
        } catch (Exception ex) {
            Log.e(TAG, "" + ex.getMessage());
        }
        if (discount > 0) {
            return (int) discount;
        } else {
            return 0;
        }
    }
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";


    public static String getTimeStamp() {
        return new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US).format(new java.util.Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
//        return dateFormat.format(date);
        return dateFormat.format(Calendar.getInstance().getTime());
    }
    @SuppressLint("SimpleDateFormat")
    public static String dateAndTimeAMPM() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
//        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String dateAndTimeFormatted() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
//        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM HH:ss a");
        Date date = new Date();
//        return dateFormat.format(date);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String dateAndTimeFormatted1() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d hh:mm aa");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateAfterCurrentDate(int afterDate, boolean isNormal) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, afterDate);
        Date date = cal.getTime();
        DateFormat formatter;
        if (isNormal)
         formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        else
            formatter = new SimpleDateFormat("EEE, d MMM ");

        return formatter.format(date);
    }

    private static String getDate1(Calendar cal) {
        return "" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.DATE) + 1) ;
    }

    public static String formatDateToFrom(String date, String from, String to) throws ParseException {
        Calendar dob = Calendar.getInstance();
        dob.set(Calendar.HOUR_OF_DAY, 0);
        dob.set(Calendar.MINUTE, 0);
        dob.set(Calendar.SECOND, 0);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        SimpleDateFormat sdfrom = new SimpleDateFormat(from, Locale.US);
        SimpleDateFormat sdto = new SimpleDateFormat(to, Locale.US);

        java.util.Date dateDob = sdfrom.parse(date);

        return sdto.format(dateDob);
    }

    public static String getMonthAfterCurrentDate(int noOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.MONTH, noOfMonth);
        return getDate(cal) + " " + getTime(cal);
    }

    public static String getDate(Calendar cal) {
        return "" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
    }

    public static String getTime(Calendar cal) {
        return "" + cal.get(Calendar.HOUR_OF_DAY) + ":" +
                (cal.get(Calendar.MINUTE)) + ":" + cal.get(Calendar.SECOND);
    }


    public static String getReferralCode(int n) {


        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static void printHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.essentials.customerapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static String getCurrentTime() {
//        return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

    }
}
