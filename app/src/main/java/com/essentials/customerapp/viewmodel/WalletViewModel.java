package com.essentials.customerapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.models.WalletModel;

import java.util.List;

public class WalletViewModel extends AndroidViewModel {

    private MutableLiveData<List<WalletModel>> mutableLiveData;
    private WalletRepository walletRepository;

    public WalletViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(String userId, String walletId) {
        if (mutableLiveData != null) {
            return;
        }
        walletRepository = WalletRepository.getInstance();
        mutableLiveData = walletRepository.getWalletData(userId);

    }

    public LiveData<List<WalletModel>> getWalletData() {
        return mutableLiveData;
    }
}
