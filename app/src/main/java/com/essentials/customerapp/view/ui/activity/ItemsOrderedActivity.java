package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.OrderRepository;
import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.fcm.LocalNotificationTrigger;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.OrderModelNew;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.adapter.ItemsOrderAdapter;
import com.essentials.customerapp.view.ui.adapter.OrderStatusAdapter;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.essentials.customerapp.utilities.Constant.COD_MODE;
import static com.essentials.customerapp.utilities.Constant.DEBIT_OR_CREDIT_MODE;

public class ItemsOrderedActivity extends AppCompatActivity {

    private TextView tvTitle;
    private Toolbar toolbar;
    private RelativeLayout rlItemOrder;
    private RecyclerView rcvProductList;
    private ItemsOrderAdapter itemsOrderAdapter;
    private MySharedPref sharedPref;
    private List<OrderModelNew> orderModelList = new ArrayList<>();


    private TextView tvPlacedDateTime, tvDeliveredDateTime, tvItemCount, tvAmountCount, tvTotalSaving,
            tvTotalDiscount, tvTotalMRPAmount, tvTotalProductDiscountAmount, tvEssentialsDiscount,
            tvDeliveryCharges, tvSubtotal, tvAddressType, tvName, tvHouseOrOfficeNo, tvStreetName, tvLandmark,
            tvPincode, tvMobile;
    private CardView cvDeliveryDetails;
    private CardView cvCustomerSupport, cvCancelOrder;
    private NestedScrollView scrollView;
    private TextView tvOrderStatusText;
    private TextView tvDelRupee;
    private TextView tvDelPlus, tvTrackOrder;
    private TextView tvOrderId;
    private TextView tvCancelOrder;
    private String selectedText = "";
    private String orderId;
    private LinearLayout llLandmark;
    private double essentialsDiscount = 0.0;
    private String userId;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;
    private ImageView ivPaymentMode;
    private TextView tvPaymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_ordered);
        sharedPref = new MySharedPref(this);
        orderId = getIntent().getStringExtra(Constant.OREDR_ID);
        assignRef();
        setRecyclerView();
        userId = new MySharedPref(this).readString(PrefConstant.USER_ID, "");
        inititeProgessHud();
        observeOrderDetails(orderId);
    }

    private void inititeProgessHud() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void showProgressHud() {
        /*if (progressHUD != null)
            progressHUD.show();*/
        rlProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressHud() {
        /*if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }*/
        rlProgress.setVisibility(View.GONE);
    }

    private void observeOrderDetails(String orderId) {
        showProgressHud();
        List<CartModel> itemsList = new ArrayList<>();
        DatabaseReferences.getItemOrderedRef().child(userId).child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemsList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            CartModel cartModel = data.getValue(CartModel.class);
                            itemsList.add(cartModel);
                        }
                        if (itemsList.size() > 0) {
                            showEmptyView(1);
                            tvItemCount.setText(itemsList.size() + " ITEM");
                            itemsOrderAdapter.setDataList(itemsList);
                            observeHistoryData(orderId);
                        } else {
                            showEmptyView(0);
                            hideProgressHud();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showEmptyView(0);
                        hideProgressHud();
                    }
                });
    }

    private void observeHistoryData(String orderId) {
        List<OrderModelNew> orderModelList = new ArrayList<>();
        DatabaseReferences.getOrderHistoryRef().child(userId).orderByChild("orderId").equalTo(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderModelList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            OrderModelNew orderModel = data.getValue(OrderModelNew.class);
                            orderModelList.add(orderModel);
                        }
                        if (orderModelList.size() > 0) {
                            setValueToView(orderModelList.get(0));
                        } else {
                            hideProgressHud();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideProgressHud();
                    }
                });
    }

    private void setValueToView(OrderModelNew orderModelNew) {
        String paymentmode = orderModelNew.getPaymentMode() != null ? orderModelNew.getPaymentMode() : "";
        if (paymentmode.equalsIgnoreCase(COD_MODE)) {
            ivPaymentMode.setImageResource(R.drawable.pay_met);
            tvPaymentMode.setText("Cash On Delivery");
        } else if (paymentmode.equalsIgnoreCase(DEBIT_OR_CREDIT_MODE)){
            ivPaymentMode.setImageResource(R.drawable.credit_card);
            tvPaymentMode.setText("Online Paid");
        }else {

        }
        tvPlacedDateTime.setText(orderModelNew.getOrderFormattedDate());

        tvAmountCount.setText("AMOUNT: ₹ " + String.format(Locale.ENGLISH, "%.2f", orderModelNew.getSubtotal()));

        List<OrderStatusModel> modelList = new ArrayList<>();
        DatabaseReferences.getOrderStatusRef().child(Constant.USER_ID).child(orderModelNew.getOrderId())
                .orderByChild("dateTime")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            OrderStatusModel model = data.getValue(OrderStatusModel.class);
                            modelList.add(model);
                        }
                        /*Show UI*/
                        String orderStatus = modelList.get(modelList.size() - 1).getOrderStatus();
                        setEnableCancelButton(orderStatus, orderModelNew.getOrderTimeSlot());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        /*Checking order status and setting the text accordingly*/
//        String orderStatus = orderModelNew.getOrderStatus();
//        if (orderStatus.equalsIgnoreCase("Cancelled")) {
//            cvCancelOrder.setCardBackgroundColor(getResources().getColor(R.color.white));
//            tvCancelOrder.setTextColor(getResources().getColor(R.color.divider));
//            tvCancelOrder.setEnabled(false);
//
//            cvDeliveryDetails.setVisibility(View.GONE);
//            tvOrderStatusText.setBackgroundColor(getResources().getColor(R.color.red));
//            tvOrderStatusText.setText("Oops!! You have cancelled order");
//        } else if (orderStatus.equalsIgnoreCase("Delivered")) {
//            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.white));
//            tvCancelOrder.setTextColor(getResources().getColor(R.color.divider));
//            tvCancelOrder.setEnabled(false);
//
//            tvDeliveredDateTime.setText(orderModelNew.getOrderTimeSlot());
//            cvDeliveryDetails.setVisibility(View.VISIBLE);
//            tvOrderStatusText.setBackgroundColor(getResources().getColor(R.color.green));
//            tvOrderStatusText.setText("Yay! Order successfully delivered");
//        } else if (orderStatus.equalsIgnoreCase("Order Placed")) {
//            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.red));
//            tvCancelOrder.setTextColor(getResources().getColor(R.color.white));
//            tvCancelOrder.setEnabled(true);
//
//            cvDeliveryDetails.setVisibility(View.GONE);
//            tvOrderStatusText.setBackgroundColor(getResources().getColor(R.color.order_placed_text_color));
//            tvOrderStatusText.setText("Keep patient.. Your order has placed");
//        } else {
//            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.red));
//            tvCancelOrder.setTextColor(getResources().getColor(R.color.white));
//            tvCancelOrder.setEnabled(true);
//
//            cvDeliveryDetails.setVisibility(View.GONE);
//            tvOrderStatusText.setBackgroundColor(getResources().getColor(R.color.theme_yellow));
//            tvOrderStatusText.setText("Our delivery agent out for delivery");
//        }

        tvTotalMRPAmount.setText(String.valueOf(orderModelNew.getTotalMrp()));
        double normalDiscount = orderModelNew.getNormalDiscount();
        essentialsDiscount = orderModelNew.getEssentialsDiscount();
        tvTotalProductDiscountAmount.setText(String.format(Locale.ENGLISH, "%.2f", normalDiscount));
        tvEssentialsDiscount.setText(String.format(Locale.ENGLISH, "%.2f", essentialsDiscount));
        double shippingCharge = orderModelNew.getDeliveryAmount();
        /*Check delivery charges if zero show free text*/
        if (shippingCharge > 0) {
            tvDelPlus.setVisibility(View.VISIBLE);
            tvDelRupee.setVisibility(View.VISIBLE);
            tvDeliveryCharges.setText(String.valueOf(shippingCharge));
            tvDeliveryCharges.setTextColor(getResources().getColor(R.color.red));
        } else {
            tvDelPlus.setVisibility(View.GONE);
            tvDelRupee.setVisibility(View.GONE);
            tvDeliveryCharges.setText("Free");
            tvDeliveryCharges.setTextColor(getResources().getColor(R.color.green));
        }

        tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", orderModelNew.getSubtotal()));
        double totalSaving = essentialsDiscount + normalDiscount;
        tvTotalSaving.setText(String.format(Locale.ENGLISH, "%.2f", totalSaving));
        double savedPercentage = calculatePercentage(totalSaving, orderModelNew.getTotalMrp());


        tvTotalDiscount.setText("(" + String.format(Locale.ENGLISH, "%.2f", savedPercentage) + "%)");

        Gson gson = new Gson();
        AddressModel model = gson.fromJson(orderModelNew.getAddressModel(), AddressModel.class);

        tvAddressType.setText(model.getAddressNickName());
        tvName.setText(model.getName());
        tvHouseOrOfficeNo.setText(model.getHouseNo());
        tvStreetName.setText(model.getSocietyName());
        String landmark = model.getLandmark();
        if (TextUtils.isEmpty(landmark)) {
            llLandmark.setVisibility(View.GONE);
        } else {
            llLandmark.setVisibility(View.VISIBLE);
            tvLandmark.setText(model.getAddressNickName());
        }

        tvPincode.setText(model.getPincode());
        tvMobile.setText(model.getMobileNumber());

        tvOrderId.setText(orderModelNew.getOrderId());

        hideProgressHud();


    }

    private void setEnableCancelButton(String orderStatus, String orderTimeSlot) {
        if (orderStatus.equalsIgnoreCase("Order Cancelled")) {
            cvCancelOrder.setCardBackgroundColor(getResources().getColor(R.color.white));
            tvCancelOrder.setTextColor(getResources().getColor(R.color.divider));
            tvCancelOrder.setEnabled(false);

            cvDeliveryDetails.setVisibility(View.GONE);
        } else if (orderStatus.equalsIgnoreCase("Processing")) {
            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.red));
            tvCancelOrder.setTextColor(getResources().getColor(R.color.white));
            tvCancelOrder.setEnabled(true);

            cvDeliveryDetails.setVisibility(View.GONE);
        } else if (orderStatus.equalsIgnoreCase("Order Delivered")) {
            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.white));
            tvCancelOrder.setTextColor(getResources().getColor(R.color.divider));
            tvCancelOrder.setEnabled(false);

            tvDeliveredDateTime.setText(orderTimeSlot);
            cvDeliveryDetails.setVisibility(View.VISIBLE);
        } else if (orderStatus.equalsIgnoreCase("Order Placed")) {
            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.red));
            tvCancelOrder.setTextColor(getResources().getColor(R.color.white));
            tvCancelOrder.setEnabled(true);

            cvDeliveryDetails.setVisibility(View.GONE);
        } else {
            cvCancelOrder.setBackgroundColor(getResources().getColor(R.color.red));
            tvCancelOrder.setTextColor(getResources().getColor(R.color.white));
            tvCancelOrder.setEnabled(true);

            cvDeliveryDetails.setVisibility(View.GONE);
        }
    }

    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }


    private void showEmptyView(int i) {
        if (i == 0) {
            rlItemOrder.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
            cvCustomerSupport.setVisibility(View.INVISIBLE);
        } else {
            rlItemOrder.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            cvCustomerSupport.setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        itemsOrderAdapter = new ItemsOrderAdapter(ItemsOrderedActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvProductList.setLayoutManager(mLayoutManager);
        rcvProductList.setItemAnimator(new DefaultItemAnimator());
        rcvProductList.setAdapter(itemsOrderAdapter);
    }


    private void setAdapter(List<OrderModelNew> orderModelList) {
        /*List<CartModel> cartModelList = new ArrayList<>();
        for (OrderModelNew model : orderModelList) {
            for (CartModel model1 : model.getProducts()) {
                model1.setDeliver(model.getIS_ORDER_DELIVER());
                model1.setOrder_Id(model.getOrderId());
                model1.setOrder_Added_Date_And_Time(model.getOrderedDateAndTime());
                cartModelList.add(model1);
            }
        }
        itemsOrderAdapter.setDataList(cartModelList);
        AppUtils.dismissProgressDialog();
*/
    }

    private void assignRef() {
        ivPaymentMode = findViewById(R.id.ivPaymentMode);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        rlItemOrder = findViewById(R.id.rlItemOrder);
        tvTotalMRPAmount = findViewById(R.id.tvTotalMRPAmount);
        tvCancelOrder = findViewById(R.id.tvCancelOrder);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvDelRupee = findViewById(R.id.tvDelRupee);
        tvDelPlus = findViewById(R.id.tvDelPlus);
        rlProgress = findViewById(R.id.rlProgress);
//        tvOrderStatusText = findViewById(R.id.tvOrderStatusText);
        tvTrackOrder = findViewById(R.id.tvTrackOrder);
        scrollView = findViewById(R.id.scrollView);
        tvTitle = findViewById(R.id.tvTitle);
        toolbar = findViewById(R.id.white_toolbar);
        rlItemOrder = findViewById(R.id.rlItemOrder);
        toolbar = findViewById(R.id.white_toolbar);
        tvPlacedDateTime = findViewById(R.id.tvPlacedDateTime);
        tvDeliveredDateTime = findViewById(R.id.tvDeliveredDateTime);
        cvDeliveryDetails = findViewById(R.id.cvDeliveryDetails);
        tvItemCount = findViewById(R.id.tvItemCount);
        tvAmountCount = findViewById(R.id.tvAmountCount);
        tvTotalProductDiscountAmount = findViewById(R.id.tvTotalProductDiscountAmount);
        tvEssentialsDiscount = findViewById(R.id.tvEssentialsDiscount);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotalSaving = findViewById(R.id.tvTotalSaving);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);
        cvCustomerSupport = findViewById(R.id.cvCustomerSupport);
        cvCancelOrder = findViewById(R.id.cvCancelOrder);
        tvAddressType = findViewById(R.id.tvAddressType);
        tvName = findViewById(R.id.tvName);
        tvHouseOrOfficeNo = findViewById(R.id.tvHouseOrOfficeNo);
        tvStreetName = findViewById(R.id.tvStreetName);
        tvStreetName = findViewById(R.id.tvStreetName);
        tvLandmark = findViewById(R.id.tvLandmark);
        llLandmark = findViewById(R.id.llLandmark);
        tvPincode = findViewById(R.id.tvPincode);
        tvMobile = findViewById(R.id.tvMobile);
        rcvProductList = findViewById(R.id.rcvProductList);
        tvTitle.setText("Order Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        tvTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchOrderStatus();
            }
        });

        tvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelOrderReasonDialog();
            }
        });

        cvCustomerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsappContact(getString(R.string.customer_support_no));
            }
        });

    }

    private void fetchOrderStatus() {
        DatabaseReferences.getOrderStatusRef().child(Constant.USER_ID).child(orderId)
                .orderByChild("dateTime")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<OrderStatusModel> statusModelList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            OrderStatusModel statusModel = dataSnapshot1.getValue(OrderStatusModel.class);
                            statusModelList.add(statusModel);
                        }
                        if (!statusModelList.isEmpty()) {
                            showOrderStatusDialog(statusModelList);
                        } else {
                            CustomToast.showRectToast(ItemsOrderedActivity.this, tvTrackOrder, "Something went wrong.. Please try after some time");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CustomToast.showRectToast(ItemsOrderedActivity.this, tvTrackOrder, "Something went wrong.. Please try after some time");
                    }
                });
    }

    private void showOrderStatusDialog(List<OrderStatusModel> statusModelList) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.track_order_dialog);

        RecyclerView rcvTrackOrder = dialog.findViewById(R.id.rcvTrackOrder);
        ImageView ivClose = dialog.findViewById(R.id.ivClose);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcvTrackOrder.setLayoutManager(manager);
        OrderStatusAdapter adapter = new OrderStatusAdapter(ItemsOrderedActivity.this, statusModelList);
        rcvTrackOrder.setAdapter(adapter);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    private Dialog dialog;

    private void showCancelOrderReasonDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cancel_dialog);

        RadioGroup rg = dialog.findViewById(R.id.rg);
        RadioButton rb1 = dialog.findViewById(R.id.rb1);
        RadioButton rb2 = dialog.findViewById(R.id.rb2);
        RadioButton rb3 = dialog.findViewById(R.id.rb3);
        MultiAutoCompleteTextView edtReason = dialog.findViewById(R.id.edtReason);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checedId) {
                if (checedId == R.id.rb1) {
                    selectedText = rb1.getText().toString();
                } else if (checedId == R.id.rb2) {
                    selectedText = rb2.getText().toString();
                } else if (checedId == R.id.rb3) {
                    selectedText = rb3.getText().toString();
                } else {
                    selectedText = rb3.getText().toString();
                }
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedText.isEmpty()) {
                    if (!edtReason.getText().toString().isEmpty() && edtReason.getText().toString().length() >= 10) {
                        cancelOrder(edtReason.getText().toString());
                    } else {
                        CustomToast.showRectToast(ItemsOrderedActivity.this, tvCancel, "Please add proper reason for cancellation");
                    }
                } else {
                    cancelOrder(selectedText);
                }
                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @Override
    public void onBackPressed() {
        ItemsOrderedActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void cancelOrder(String selectedText) {
        DatabaseReference dbRef = DatabaseReferences.getOrderHistoryRef().child(userId).child(orderId);
        /*Order Status*/
        OrderRepository.getInstance().updateOrderStatus("Order Cancelled", orderId);

        dbRef.child("deliveryCancel").setValue(true);
        dbRef.child("orderStatus").setValue("Order Cancelled");
        dbRef.child("reasonForCancellation").setValue(selectedText).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refundWalletAmount();
                showCancelOrderDialog();
                new LocalNotificationTrigger(ItemsOrderedActivity.this).createNotificationChannel("Order Cancelled",
                        "Your Order is cancelled as per your request.");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissDialog();
                        CustomToast.showRectToast(ItemsOrderedActivity.this, tvAddressType,
                                "Error with cancel order..Please try after sometime");
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    private static Dialog dialog1;

    public static void showDialog(final Activity activity, String msg) {
        dialog1 = new Dialog(activity);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.message_dialog);
        dialog1.setCancelable(true);
        TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        dialog1.show();
    }

    public static void dismissDialog() {
        if (dialog1 != null) {
            dialog1.dismiss();
        }
    }


    private void showCancelOrderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.order_cancel_layout);
        RelativeLayout rlOK = dialog.findViewById(R.id.rlOK);
        rlOK.setOnClickListener(v -> {
            dialog.dismiss();
            moveToHomeActivity();
        });
        dialog.show();
    }

    private void refundWalletAmount() {
        if (essentialsDiscount > 0) {
            WalletRepository
                    .getInstance()
                    .updateWalletTotalBalanceWithNewAmount(userId, essentialsDiscount, true)
                    .observe(this, new Observer<ResponseModel>() {
                        @Override
                        public void onChanged(ResponseModel responseModel) {
                            if (responseModel != null) {
                                if (responseModel.isStatus())
                                    CustomToast.showRectToast(ItemsOrderedActivity.this, cvCancelOrder, "Essentials amount ₹" + essentialsDiscount + " has been refunded to your wallet");
                                else {
                                    CustomToast.showRectToast(ItemsOrderedActivity.this, cvCancelOrder, "We encounter some error with your Essentials wallet");
                                }
                            }
                        }
                    });
        } else {
            moveToHomeActivity();
        }
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}
