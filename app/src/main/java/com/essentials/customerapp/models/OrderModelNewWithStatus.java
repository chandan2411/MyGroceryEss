package com.essentials.customerapp.models;

import java.util.List;

public class OrderModelNewWithStatus {
    private OrderModelNew orderModelNew;
    private List<OrderStatusModel> orderStatusModelList;

    public OrderModelNewWithStatus() {
    }

    public OrderModelNewWithStatus(OrderModelNew orderModelNew, List<OrderStatusModel> orderStatusModelList) {
        this.orderModelNew = orderModelNew;
        this.orderStatusModelList = orderStatusModelList;
    }

    public OrderModelNew getOrderModelNew() {
        return orderModelNew;
    }

    public void setOrderModelNew(OrderModelNew orderModelNew) {
        this.orderModelNew = orderModelNew;
    }

    public List<OrderStatusModel> getOrderStatusModelList() {
        return orderStatusModelList;
    }

    public void setOrderStatusModelList(List<OrderStatusModel> orderStatusModelList) {
        this.orderStatusModelList = orderStatusModelList;
    }
}
