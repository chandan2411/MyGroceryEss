package com.essentials.customerapp.models;

/**
 * Created by Raj Aryan on 7/12/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class OrderStatusModel {
    private String orderStatus;
    private String dateTime;
    private String key;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
