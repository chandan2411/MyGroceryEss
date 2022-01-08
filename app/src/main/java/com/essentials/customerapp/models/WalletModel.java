package com.essentials.customerapp.models;

public class WalletModel {

    private double walletTotalBalance;
    private String userId;
    private String userName;
    private String walletId;
    private String amountExpireDate;
    private String walletCreateDate;
    private String walletReferCode;

    public double getWalletTotalBalance() {
        return walletTotalBalance;
    }

    public void setWalletTotalBalance(double walletTotalBalance) {
        this.walletTotalBalance = walletTotalBalance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getAmountExpireDate() {
        return amountExpireDate;
    }

    public void setAmountExpireDate(String amountExpireDate) {
        this.amountExpireDate = amountExpireDate;
    }

    public String getWalletCreateDate() {
        return walletCreateDate;
    }

    public void setWalletCreateDate(String walletCreateDate) {
        this.walletCreateDate = walletCreateDate;
    }

    public String getWalletReferCode() {
        return walletReferCode;
    }

    public void setWalletReferCode(String walletReferCode) {
        this.walletReferCode = walletReferCode;
    }

    /*public String getWalletTransaction() {
        return walletTransaction;
    }

    public void setWalletTransaction(String walletTransaction) {
        this.walletTransaction = walletTransaction;
    }*/
}
