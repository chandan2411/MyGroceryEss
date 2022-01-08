package com.essentials.customerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.models.ReferralCodeModel;
import com.essentials.customerapp.models.WalletTransaction;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.WalletModel;

import java.util.ArrayList;
import java.util.List;

public class WalletRepository {
    private static WalletRepository walletRepository;
    private MutableLiveData<ResponseModel> isWalletExist = new MutableLiveData<>();
    private MutableLiveData<ResponseModel> isTransactionDone = new MutableLiveData<>();
    private MutableLiveData<WalletModel> walletModel = new MutableLiveData<>();

    public static WalletRepository getInstance() {
        if (walletRepository == null) {
            walletRepository = new WalletRepository();
        }
        return walletRepository;
    }

    public MutableLiveData<List<WalletModel>> getWalletData(String userId) {
        final MutableLiveData<List<WalletModel>> walletData = new MutableLiveData<>();
        final List<WalletModel> walletModelList = new ArrayList<>();
        DatabaseReferences.getWalletReference().child(Constant.USER_ID).orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletModel model;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    model = dataSnapshot1.getValue(WalletModel.class);
                    walletModelList.add(model);
                }
                if (!walletModelList.isEmpty())
                    walletData.setValue(walletModelList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                walletData.setValue(null);
            }
        });
        return walletData;
    }

    public MutableLiveData<List<WalletTransaction>> geTransactionData(String userId) {

        final MutableLiveData<List<WalletTransaction>> walletData = new MutableLiveData<>();
        final List<WalletTransaction> walletModelList = new ArrayList<>();
        DatabaseReferences.getWalletTransReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletTransaction model;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    model = dataSnapshot1.getValue(WalletTransaction.class);
                    walletModelList.add(model);
                }
                if (!walletModelList.isEmpty())
                    walletData.setValue(walletModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                walletData.setValue(null);
            }
        });

        return walletData;
    }

    public MutableLiveData<ResponseModel> checkForWalletExist(String userId, String referralCode, boolean newUser) {
        isWalletExist.setValue(null);
        Query walletReference;
        if (newUser) {
            DatabaseReferences.getWalletReference().child(userId)
                    .orderByChild("userId")
                    .equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                WalletModel model = null;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    model = snapshot.getValue(WalletModel.class);
                                }
                                if (model != null)
                                    isWalletExist.setValue(new ResponseModel(true,
                                            "Wallet Exist", model.getUserId(), model.getUserName(), model.getWalletTotalBalance(), newUser));

                            } else {
                                isWalletExist.setValue(new ResponseModel(false, "Wallet doesn't Exist", newUser));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            isWalletExist.setValue(new ResponseModel(false, "" + databaseError.getMessage(), newUser));
                        }
                    });
        } else {
            DatabaseReferences.getReferralCodeReference().child(referralCode)
                    .orderByChild("referralCode")
                    .equalTo(referralCode)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists()) {
                                ReferralCodeModel model = null;
                                for (DataSnapshot snapshot : snapshot1.getChildren()) {
                                    model = snapshot.getValue(ReferralCodeModel.class);
                                }
                                if (model != null)
                                    isWalletExist.setValue(new ResponseModel(true,
                                            "Wallet Exist", model.getUserId(), model.getUserName(), 0, newUser));

                            } else {
                                isWalletExist.setValue(new ResponseModel(false, "Wallet doesn't Exist", newUser));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            isWalletExist.setValue(new ResponseModel(false, "" + error.getMessage()));
                        }
                    });
        }

        return isWalletExist;
    }

    public MutableLiveData<WalletModel> createWalletForUser(String userId, String userName) {
        walletModel.setValue(null);
        String walletId = DatabaseReferences.getWalletReference().child(userId).push().getKey();

        WalletModel model = new WalletModel();
        model.setUserId(userId);
        model.setUserName(userName);
        model.setWalletTotalBalance(200);
        model.setWalletId(walletId);
        model.setAmountExpireDate(AppUtils.getMonthAfterCurrentDate(2));
        model.setWalletCreateDate(AppUtils.dateAndTime());
        String referralCode = AppUtils.getReferralCode(6);
        model.setWalletReferCode(referralCode);

        DatabaseReferences.getWalletReference().child(userId).child(walletId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                walletModel.setValue(model);
                createReferralCodeTable(userName, userId, walletId, referralCode);
            }
        });
        return walletModel;
    }

    private void createReferralCodeTable(String userName, String userId, String walletId, String referralCode) {
        ReferralCodeModel model = new ReferralCodeModel();
        String key = DatabaseReferences.getReferralCodeReference().child(referralCode).push().getKey();
        model.setUserId(userId);
        model.setUserName(userName);
        model.setCreatedOn(AppUtils.dateAndTime());
        model.setWalletId(walletId);
        model.setReferralCode(referralCode);
        model.setKey(key);

        DatabaseReferences.getReferralCodeReference().child(referralCode).child(key).setValue(model);

    }

    public MutableLiveData<ResponseModel> addWalletTransaction(String userId, double amount,
                                                               boolean amountHasToCredit, String transDetails,
                                                               boolean newUser) {
        isTransactionDone.setValue(null);
        WalletTransaction transaction = new WalletTransaction();
        String transactionId = DatabaseReferences.getWalletReference().child(userId).push().getKey();
        transaction.setTransactionID(transactionId);
        transaction.setAmountAdded(amountHasToCredit);
        transaction.setAmountTransaction(amount);
        transaction.setUserId(userId);
        transaction.setTransactionReason(transDetails);
        transaction.setTransactionDate(AppUtils.dateAndTime());

        /*check for the referral code */
        DatabaseReferences.getWalletTransReference().child(userId)
                .child(transactionId)
                .setValue(transaction)
                .addOnSuccessListener(aVoid -> {
                    if (newUser)
                        isTransactionDone.setValue(new ResponseModel(true, "Transaction done"));
                    else {
                        isTransactionDone = (updateWalletTotalBalanceWithNewAmount(userId, amount, amountHasToCredit));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isTransactionDone.setValue(new ResponseModel(false, "Something went wrong.. Please try after some time"));
                    }
                });
        return isTransactionDone;
    }

    public MutableLiveData<ResponseModel> updateWalletTotalBalanceWithNewAmount(String userId, double amount, boolean amountHasToCredit) {
        isTransactionDone.setValue(null);
        /*Get Wallet Balance*/
        DatabaseReferences.getWalletReference().child(userId).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        WalletModel model = null;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            model = dataSnapshot1.getValue(WalletModel.class);
                        }
                        if (model != null) {
                            double totalAmount;
                            if (amountHasToCredit)
                                totalAmount = model.getWalletTotalBalance() + amount;
                            else
                                totalAmount = model.getWalletTotalBalance() - amount;
                            model.setWalletTotalBalance(totalAmount);
                            /*Update wallet balance*/
                            DatabaseReferences.getWalletReference().child(userId)
                                    .child(model.getWalletId())
                                    .setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            isTransactionDone.setValue(new ResponseModel(true, ""));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            isTransactionDone.setValue(new ResponseModel(false, "Something went wrong.. Please try after some time"));
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        isTransactionDone.setValue(new ResponseModel(false, ""));

                    }
                });
        return isTransactionDone;
    }

    public MutableLiveData<ResponseModel> updateWalletTotalBalance(String userId, double amount) {
        isTransactionDone.setValue(null);
        DatabaseReferences.getWalletReference().child(userId).child("walletTotalBalance").setValue(amount)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isTransactionDone.setValue(new ResponseModel(true, ""));
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isTransactionDone.setValue(new ResponseModel(false, ""));
                    }
                });
        return isTransactionDone;
    }

}
