package com.essentials.customerapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.R;
import com.essentials.customerapp.utilities.CheckNetwork;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.Utils;

public class LoginViewModel extends AndroidViewModel {

    public MutableLiveData<String> mobileNumber = new MutableLiveData<>();
    public MutableLiveData<String> referralCode = new MutableLiveData<>();
    public MutableLiveData<Boolean> isProgressShowing = new MutableLiveData<>();
    public MutableLiveData<String> status = new MutableLiveData<>();

    private Context mContext;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mContext = application;
    }

    public void onNextClick(View view) {
        if (CheckNetwork.checkNet(mContext)) {
            validateAndProceed();
        } else {
            CustomToast.showRectToast(mContext, view, mContext.getString(R.string.check_internet));
        }

    }

    private void validateAndProceed() {
        if (mobileNumber.getValue() != null) {
            String validationStatus = Utils.checkMobileValidation(mContext, mobileNumber.getValue());
            if (validationStatus.equalsIgnoreCase(Constants.STATUS_TRUE)) {
                isProgressShowing.setValue(true);
            } else
                status.setValue(validationStatus);
        } else {
            status.setValue(mContext.getResources().getString(R.string.please_enter_mobile_number));
        }
    }

    public void facebookLoginClicked(View view){

    }

    public void googleLoginClicked(View view){

    }


}