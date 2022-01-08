package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.databinding.ActivityNewCheckoutBinding;
import com.essentials.customerapp.fcm.LocalNotificationTrigger;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.viewmodel.NewCheckoutViewModel;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import static com.essentials.customerapp.utilities.Constant.COD_MODE;
import static com.essentials.customerapp.utilities.Constant.DEBIT_OR_CREDIT_MODE;
import static com.essentials.customerapp.utilities.Constant.DELIVERY_DATE;
import static com.essentials.customerapp.utilities.Constant.SUB_TOTAL;
import static com.essentials.customerapp.utilities.Constant.TIME_SLOT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_DELIVERY_CHARGES;
import static com.essentials.customerapp.utilities.Constant.TOTAL_ESSENTIALS_DISCOUNT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_MRP;
import static com.essentials.customerapp.utilities.Constant.TOTAL_PRODUCT_DISCOUNT;

public class NewCheckoutActivity extends AppCompatActivity implements PaymentResultListener {

    private NewCheckoutViewModel viewModel;
    private List<CartModel> cartDataList;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;
    private static final String TAG = "NewCheckoutActivity";
    private double subTotal = 0.0;
    MySharedPref sharedPref;
    private RadioButton rbCodPaymentMode;
    private RadioButton rbCreditCardMode;
    private RelativeLayout rlAddPlaceOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new MySharedPref(this);
        bindViewAndModel();

        /**
         * Preload payment resources
         */
        Checkout.preload(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.white_toolbar);
        rlProgress = findViewById(R.id.rlProgress);
        rbCreditCardMode = findViewById(R.id.rbCreditCardMode);
        rbCodPaymentMode = findViewById(R.id.rbCodPaymentMode);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        getIntentData();
        viewModel.isOrderPlaced.observe(this, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel != null) {
                    /*Send Notification*/
                    new LocalNotificationTrigger(NewCheckoutActivity.this).createNotificationChannel("Order Placed",
                            "Thank you for shopping with Essentials. Your order is successfully placed.  #Safe Shopping..  #Happy Shopping...");
                    CustomToast.dismissDialog();
                    showConfirmedOrderDialog();
                    CartRepository.getInstance().clearCart();
                }
            }
        });
    }


    private void showConfirmedOrderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.order_placed_layout);
        RelativeLayout rlOK = dialog.findViewById(R.id.rlOK);
        rlOK.setOnClickListener(v -> {

            dialog.dismiss();
            moveToHomeActivity();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void moveToHomeActivity() {
        viewModel.updateOrderStatus(viewModel.orderId, "Processing");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    String orderId = "";

    private void bindViewAndModel() {
        ActivityNewCheckoutBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_new_checkout);
        viewModel = ViewModelProviders.of(this).get(NewCheckoutViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        TextView tvPayableAmount = binding.tvPayableAmount;
        TextView tvSavedAmount = binding.tvSavedAmount;
        rlAddPlaceOrder = binding.rlAddPlaceOrder;

        rlAddPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedPaymentMode.isEmpty()) {
                    if (selectedPaymentMode.equalsIgnoreCase(COD_MODE)) {
                        orderId = AppUtils.getReferralCode(8);
                        CustomToast.showDialog(NewCheckoutActivity.this, "Please wait... your order is being placed");
                        viewModel.placeOrder(cartDataList, orderId, COD_MODE);
                    } else {
                        openOnlinePaymentPage();
                    }
                } else {
                    CustomToast.showRectToast(NewCheckoutActivity.this, binding.rlAddPlaceOrder, "Please select Payment Option");
                }
            }
        });
    }

    private void openOnlinePaymentPage() {
/**
 * You need to pass current activity in order to let Razorpay create CheckoutActivity
 */

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Essentials Pvt Ltd");
            options.put("description", "order id " + orderId);
            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            /*Calculate amount in paise*/

            double amountInPaise = subTotal*100;
            String subtotal = String.format(Locale.ENGLISH, "%.0f", amountInPaise);
            double totalPay= Double.parseDouble(subtotal);
           // Toast.makeText(this, "Error in payment: "+" "+ subtotal+" "+ amountInPaise+" "+totalPay, Toast.LENGTH_SHORT).show();

            options.put("amount", totalPay);
             // options.put("amount", 100);

            JSONObject preFill = new JSONObject();
            if (sharedPref.readInt(PrefConstant.LOGIN_MODE, PrefConstant.MOBILE) == PrefConstant.MOBILE) {
                preFill.put("contact", sharedPref.readString(PrefConstant.USER_PHONE_NO, ""));
            } else if (sharedPref.readInt(PrefConstant.LOGIN_MODE, PrefConstant.MOBILE) == PrefConstant.GOOGLE) {
                preFill.put("email", sharedPref.readString(PrefConstant.USER_EMAIL, ""));
            }
            options.put("prefill", preFill);

            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void getIntentData() {

        double totalMrp = getIntent().getDoubleExtra(TOTAL_MRP, 0.0);
        String timeSlot = getIntent().getStringExtra(TIME_SLOT);
        String deliveryDate = getIntent().getStringExtra(DELIVERY_DATE);
        double totalProductDis = getIntent().getDoubleExtra(TOTAL_PRODUCT_DISCOUNT, 0.0);
        double totalEssentialsDis = getIntent().getDoubleExtra(TOTAL_ESSENTIALS_DISCOUNT, 0.0);
        double totalDeliveryCharges = getIntent().getDoubleExtra(TOTAL_DELIVERY_CHARGES, 0.0);
        subTotal = getIntent().getDoubleExtra(SUB_TOTAL, 0.0);
        AddressModel addressModel = getIntent().getParcelableExtra("address");

        /*Convert address model to String*/
        Gson gson = new Gson();
        String addressString = gson.toJson(addressModel);

        inititeProgessHud();

        viewModel.initData(addressString, totalMrp, totalProductDis, totalEssentialsDis,
                totalDeliveryCharges, subTotal, timeSlot, deliveryDate);
        fetchCartData();
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

    private void fetchCartData() {
        showProgressHud();
        CartRepository.getInstance().getUserCartData().observe(this, new Observer<List<CartModel>>() {
            @Override
            public void onChanged(List<CartModel> cartModelList) {
                if (cartModelList != null) {
                    hideProgressHud();
                    cartDataList = cartModelList;
                }
            }
        });
    }


    @Override
    public void onPaymentSuccess(String s) {
        CustomToast.showDialog(NewCheckoutActivity.this, "Please wait... your order is being placed");
        orderId = AppUtils.getReferralCode(8);
        viewModel.placeOrder(cartDataList, orderId, selectedPaymentMode);
    }

    @Override
    public void onPaymentError(int i, String s) {
        showFailureDialog(s);
    }

    private void showFailureDialog(String errorMessage) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.order_error_layout);
            TextView tvRetry = dialog.findViewById(R.id.tvRetry);
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            TextView tvErrorMessage = dialog.findViewById(R.id.tvErrorMessage);
            tvErrorMessage.setText(errorMessage);
            tvCancel.setOnClickListener(v -> {

                dialog.dismiss();
            });

            tvRetry.setOnClickListener(v ->{
                rlAddPlaceOrder.performClick();
                dialog.dismiss();
            });


            dialog.show();
    }

    String selectedPaymentMode = "";
    public void onPayOnlineClicked(View view) {
        rbCodPaymentMode.setChecked(false);
        selectedPaymentMode = DEBIT_OR_CREDIT_MODE;
    }

    public void onCodClicked(View view) {
        rbCreditCardMode.setChecked(false);
        selectedPaymentMode = COD_MODE;
    }
}

