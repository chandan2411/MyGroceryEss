package com.essentials.customerapp.data.model;

public class ResponseModel {
    private boolean status;
    private String message;
    private String userId;
    private String userName;
    private double totalAmount;
    private boolean newUser;

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public ResponseModel(boolean status, String successResult) {
        this.status = status;
        this.message = message;
    }
    public ResponseModel(boolean status, String successResult, boolean newUser) {
        this.status = status;
        this.message = message;
        this.newUser = newUser;
    }

    public String getUserId() {
        return userId;
    }

    public ResponseModel(boolean status, String message, String userId, String userName, double totalAmount, boolean newUser) {
        this.status = status;
        this.message = message;
        this.userId = userId;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.newUser = newUser;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
