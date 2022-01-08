package com.essentials.customerapp.models;

public class WalletTransaction {

    private String transactionID;
    private boolean isAmountAdded;
    private String userId;
    private double amountTransaction;
    private String transactionReason;
    private String transactionDate;

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public boolean isAmountAdded() {
        return isAmountAdded;
    }

    public void setAmountAdded(boolean amountAdded) {
        isAmountAdded = amountAdded;
    }

    public double getAmountTransaction() {
        return amountTransaction;
    }

    public void setAmountTransaction(double amountTransaction) {
        this.amountTransaction = amountTransaction;
    }

    public String getTransactionReason() {
        return transactionReason;
    }

    public void setTransactionReason(String transactionReason) {
        this.transactionReason = transactionReason;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
