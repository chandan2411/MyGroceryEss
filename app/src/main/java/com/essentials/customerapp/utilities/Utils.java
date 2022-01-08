package com.essentials.customerapp.utilities;

import android.content.Context;
import android.text.TextUtils;

import com.essentials.customerapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";

    public static String checkMobileValidation(Context context, String mobilenumber) {
        String mobileRegex = "^[6-9]\\d{9}$";
        String message = "";
        if (mobilenumber == null)
            return context.getString(R.string.please_enter_mobile_number);

        if (TextUtils.isEmpty(mobilenumber) && mobilenumber.equalsIgnoreCase("")) {
            message = context.getString(R.string.please_enter_mobile_number);
        } else {
            Matcher matcherObj = Pattern.compile(mobileRegex).matcher(mobilenumber);
            if (mobilenumber.length() < 10) {
                message = context.getString(R.string.please_enter_valid_mobile_number);
            } else if (!matcherObj.matches()) {
                message = context.getString(R.string.please_enter_valid_mobile_number);
            } else {
                message = Constants.STATUS_TRUE;
            }
        }
        return message;
    }


}
