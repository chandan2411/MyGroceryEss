package com.essentials.customerapp.models;

/**
 * Created by Raj Aryan on 7/3/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class FCMTokenModel {

    private String fcmToken;
    private String userId;
    private String userName;
    private String createTime;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
