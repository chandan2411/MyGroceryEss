package com.essentials.customerapp.models;

import com.essentials.customerapp.viewmodel.AddressModel;

import java.util.List;

public class OrderModelNew {

    private String orderId;
    private String orderDate;
    private String orderFormattedDate;
    private double totalMrp;
    private double normalDiscount;
    private double subtotal;
    private double essentialsDiscount;
    private double deliveryAmount;
    private boolean deliveryCancel;
    private String orderTimeSlot;
    private String orderStatus;
    private String deliveryDate;
    private String addressModel;
    private String reasonForCancellation;
    private String paymentMode;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderFormattedDate() {
        return orderFormattedDate;
    }

    public void setOrderFormattedDate(String orderFormattedDate) {
        this.orderFormattedDate = orderFormattedDate;
    }

    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    public void setReasonForCancellation(String reasonForCancellation) {
        this.reasonForCancellation = reasonForCancellation;
    }

    public double getTotalMrp() {
        return totalMrp;
    }

    public void setTotalMrp(double totalMrp) {
        this.totalMrp = totalMrp;
    }

    public double getNormalDiscount() {
        return normalDiscount;
    }

    public void setNormalDiscount(double normalDiscount) {
        this.normalDiscount = normalDiscount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getEssentialsDiscount() {
        return essentialsDiscount;
    }

    public void setEssentialsDiscount(double essentialsDiscount) {
        this.essentialsDiscount = essentialsDiscount;
    }

    public double getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(double deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public boolean isDeliveryCancel() {
        return deliveryCancel;
    }

    public void setDeliveryCancel(boolean deliveryCancel) {
        this.deliveryCancel = deliveryCancel;
    }

    public String getOrderTimeSlot() {
        return orderTimeSlot;
    }

    public void setOrderTimeSlot(String orderTimeSlot) {
        this.orderTimeSlot = orderTimeSlot;
    }

    public String getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(String addressModel) {
        this.addressModel = addressModel;
    }
}
