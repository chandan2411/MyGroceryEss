package com.essentials.customerapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.OrderRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.OrderModelNew;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class NewCheckoutViewModel extends ViewModel {

    private String addressString;
    private List<CartModel> cartModelList;
    private MutableLiveData<String> totalAmount = new MutableLiveData<>();
    private MutableLiveData<String> totalDiscountText = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    public MutableLiveData<ResponseModel> isOrderPlaced = new MutableLiveData<>();
    private double totalEssentialsDis = 0.0;
    private double toMrp = 0.0;
    private String timeSlot = "";
    private double totalProductDis = 0.0;
    private double totalDeliveryCharges = 0.0;
    private double subTotal = 0.0;
    public String orderId;
    private String deliveryDate;


    public void placeOrder(List<CartModel> cartModelList, String orderId, String paymentMode) {
        this.cartModelList = cartModelList;
        placeOrderWS(orderId, paymentMode);
    }

    public void placeOrderWS(String orderId, String paymentMode) {

        this.orderId = orderId;
        String orderDate = AppUtils.dateAndTime();
        String orderDateFormatted = AppUtils.dateAndTimeFormatted1();
        OrderModelNew orderModel = new OrderModelNew();

        orderModel.setOrderId(orderId);
        orderModel.setPaymentMode(paymentMode);
        orderModel.setOrderDate(orderDate);
        orderModel.setOrderFormattedDate(orderDateFormatted);
        orderModel.setTotalMrp(toMrp);
        orderModel.setNormalDiscount(totalProductDis);
        orderModel.setEssentialsDiscount(totalEssentialsDis);
        orderModel.setSubtotal(subTotal);
        orderModel.setDeliveryAmount(totalDeliveryCharges);
        orderModel.setOrderTimeSlot(timeSlot);
        orderModel.setDeliveryCancel(false);


        orderModel.setOrderTimeSlot(timeSlot);
        orderModel.setDeliveryDate(deliveryDate);
        orderModel.setAddressModel(addressString);

        DatabaseReferences.getOrderHistoryRef().child(Constant.USER_ID).child(orderId).setValue(orderModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateOrderStatus(orderId, "Order Placed");
                        placeItemInOrderHistory();
                    }
                });
    }

    public void updateOrderStatus(String orderId, String orderStatus) {
        OrderRepository.getInstance().updateOrderStatus(orderStatus, orderId);
    }

    private void placeItemInOrderHistory() {
        double amount = Constant.USER_WALLET_AMOUNT - totalEssentialsDis;
        DatabaseReferences.getItemOrderedRef().child(Constant.USER_ID).child(orderId).setValue(cartModelList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateWalletTotalBalance(Constant.USER_ID, amount);
                    }
                });
    }

    public MutableLiveData<ResponseModel> updateWalletTotalBalance(String userId, double amount) {
        isOrderPlaced.setValue(null);
        DatabaseReferences.getWalletReference().child(Constant.USER_ID).child(Constant.USER_WALLET_ID).child("walletTotalBalance").setValue(amount)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isOrderPlaced.setValue(new ResponseModel(true, ""));
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isOrderPlaced.setValue(new ResponseModel(false, ""));
                    }
                });
        return isOrderPlaced;
    }


    public MutableLiveData<String> getTotalAmount() {
        return totalAmount;
    }

    public MutableLiveData<String> getTotalDiscountText() {
        return totalDiscountText;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }


    public void initData(String addressModel, double totalMrp, double totalProductDis,
                         double totalEssentialsDis, double totalDeliveryCharges, double subTotal,
                         String timeSlot, String deliveryDate) {
        this.timeSlot = timeSlot;
        this.addressString = addressModel;
        this.toMrp = totalMrp;
        this.totalProductDis = totalProductDis;
        this.totalEssentialsDis = totalEssentialsDis;
        this.totalDeliveryCharges = totalDeliveryCharges;
        this.deliveryDate = deliveryDate;
        this.subTotal = subTotal;
        double totalSaving = totalEssentialsDis + totalProductDis;
        String savings = String.format(Locale.ENGLISH, "%.2f", totalSaving);

        String saveText = "You are saving ₹" + savings + " in this order";
        totalDiscountText.setValue(saveText);

        totalAmount.setValue(String.format(Locale.ENGLISH, "%.2f",subTotal));
    }
}
