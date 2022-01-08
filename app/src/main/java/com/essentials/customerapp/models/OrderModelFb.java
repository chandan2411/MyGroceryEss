package com.essentials.customerapp.models;

public class OrderModelFb {

    private UserDetails userDetails;
    private String orderedDateAndTime;
    private Boolean IS_ORDER_DELIVER;

    public OrderModelFb() {
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getOrderedDateAndTime() {
        return orderedDateAndTime;
    }

    public void setOrderedDateAndTime(String orderedDateAndTime) {
        this.orderedDateAndTime = orderedDateAndTime;
    }

    public Boolean getIS_ORDER_DELIVER() {
        return IS_ORDER_DELIVER;
    }

    public void setIS_ORDER_DELIVER(Boolean IS_ORDER_DELIVER) {
        this.IS_ORDER_DELIVER = IS_ORDER_DELIVER;
    }
}
