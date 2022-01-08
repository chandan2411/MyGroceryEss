package com.essentials.customerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.models.OrderModelNew;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.WalletModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static OrderRepository orderRepository;
    private DatabaseReference walletReferenceWithChild;
    private DatabaseReference walletTransactionReference;
    private MutableLiveData<ResponseModel> isOrderPlaced = new MutableLiveData<>();
    private MutableLiveData<ResponseModel> isStatusChanged = new MutableLiveData<>();
    private MutableLiveData<List<OrderStatusModel>> orderStatusList = new MutableLiveData<>();
    private MutableLiveData<ResponseModel> isOrderPlaced1 = new MutableLiveData<>();
    private MutableLiveData<WalletModel> walletModel = new MutableLiveData<>();

    public static OrderRepository getInstance() {
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }

    public void placeOrder(String userId, List<CartModel> cartModelList,
                           MutableLiveData<String> totalAmount,
                           MutableLiveData<String> totalDiscountText,
                           AddressModel addressModel) {
        String orderId = AppUtils.getReferralCode(8);
        String orderDate = AppUtils.dateAndTime();

        /*OrderModelNew model = new OrderModelNew(orderId, orderDate, totalAmount.getValue(),
                0, totalDiscountText.getValue(), "false",
                "Order Placed", "", addressModel);*/

        DatabaseReferences.getOrderHistoryRef().child(userId).child(orderId).setValue("model")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        placeItemInOrderHistory(cartModelList, userId, orderId);
                    }
                });
    }

    private MutableLiveData<ResponseModel> placeItemInOrderHistory(List<CartModel> cartModelList,
                                                                   String userId, String orderId) {
        isOrderPlaced.setValue(null);
        DatabaseReferences.getItemOrderedRef().child(userId).child(orderId).setValue(cartModelList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isOrderPlaced1.setValue(new ResponseModel(true, ""));
                    }
                });
        return isOrderPlaced;
    }

    public MutableLiveData<ResponseModel> updateOrderStatus(String status, String orderId) {

        /*Update status in user-order-history as well*/
        DatabaseReference dbRef = DatabaseReferences.getOrderHistoryRef().child(Constant.USER_ID).child(orderId);
        dbRef.child("orderStatus").setValue(status);

        /*Order Status*/
        isStatusChanged.setValue(null);
        OrderStatusModel statusModel = new OrderStatusModel();
        String key = DatabaseReferences.getOrderStatusRef().child(orderId).push().getKey();
        statusModel.setDateTime(AppUtils.dateAndTimeAMPM());
        statusModel.setOrderStatus(status);
        statusModel.setKey(key);
        DatabaseReferences.getOrderStatusRef().child(Constant.USER_ID).child(orderId).child(key).setValue(statusModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isStatusChanged.setValue(new ResponseModel(true, ""));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isStatusChanged.setValue(new ResponseModel(false, ""));
                    }
                });
        return isStatusChanged;
    }

    public MutableLiveData<List<OrderStatusModel>> getOrderStatus(String orderId) {
        orderStatusList.setValue(null);
        List<OrderStatusModel> modelList = new ArrayList<>();
        DatabaseReferences.getOrderStatusRef().child(Constant.USER_ID).child(orderId)
                .orderByChild("dateTime")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            OrderStatusModel model = data.getValue(OrderStatusModel.class);
                            modelList.add(model);
                        }
                        orderStatusList.setValue(modelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        orderStatusList.setValue(modelList);
                    }
                });
        return orderStatusList;
    }
}
