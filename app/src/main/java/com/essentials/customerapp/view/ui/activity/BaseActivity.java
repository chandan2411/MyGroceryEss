package com.essentials.customerapp.view.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.utilities.Constant;
import com.google.firebase.database.DatabaseReference;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.UserModel;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity implements AddorRemoveCallbacks {
    public static final String TAG = "BaseActivity===>";
    private MySharedPref sharedPref;
    private List<CartModel> cartModelList1 = new ArrayList<>();
    private double deductionAmount;
    int cartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new MySharedPref(this);
//        observeCartData();
    }

    List<String> productList = new ArrayList<>();
    public void setSearchListData(List<String> productList){
        this.productList = productList;
    }

    public List<String> getSearchListData(){
        return productList;
    }


    private static Dialog dialog;

    public static void showDialog(final Activity activity, String msg) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void observeCartData() {
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance().getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList) {
                    if (cartModelList != null) {
                        cartModelList1 = cartModelList;
                        cartCount = cartModelList.size();
                    }
                }
            });
        }
    }

    public Double getTotalPrice() {
        Double total = 0.0;
        if (cartModelList1.size() > 0) {
            for (int i = 0; i < cartModelList1.size(); i++) {
                total = total + Double.valueOf(cartModelList1.get(i).getSub_Total());
                Log.d(TAG, "Total :" + total + "");
            }
            Log.d(TAG, "Total :" + total + "");
            return total;
        }
        return total;
    }

    public Double getTotalDiscount() {
        Double total = 0.0;
        if (cartModelList1.size() > 0) {
            for (int i = 0; i < cartModelList1.size(); i++) {
                CartModel cartModel = cartModelList1.get(i);
                String offerID = cartModel.getProduct_Offer_Id();
                String[] offerString = offerID.split("_");
                if (offerString[0].equalsIgnoreCase("BUY")) {
                    total = calculateOfferForBuySomeGetSome(cartModel, offerString);
                } else if (offerString[0].equalsIgnoreCase("OFF")) {
                    total = calculateOfferForPercentDiscount(cartModel, offerString);
                }

            }
        }
        return total;
    }

    private Double calculateOfferForPercentDiscount(CartModel cartModel, String[] offerString) {
        deductionAmount = 0.0;
        try {
            Double percencateOFF = Double.parseDouble(offerString[1]);
            Log.d("Offer Type :", "Percentage OFf");
            Double SP = Double.parseDouble(cartModel.getProduct_SP());
            int noOfOrder = cartModel.getProdctQuantityUnit();
            deductionAmount = ((SP * percencateOFF) / 100) * noOfOrder;
            Log.d("Deduction :", "" + deductionAmount);
            cartModel.setDeductedAmount(deductionAmount);
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return deductionAmount;
    }

    private Double calculateOfferForBuySomeGetSome(CartModel cartModel, String[] offerString) {
        deductionAmount = 0.0;
        try {
            Double totalProduct = Double.parseDouble(offerString[1]) + Double.parseDouble(offerString[3]);
            Log.d("Offer Type :", "BUY SOME GET SOME");
            Double SP = Double.parseDouble(cartModel.getProduct_SP());
            int noOfOrder = cartModel.getProdctQuantityUnit();
            Double priceOfOne = SP * Integer.parseInt(offerString[1]) / totalProduct;
            deductionAmount = priceOfOne * noOfOrder;
            Log.d("Deduction :", "" + deductionAmount);
            cartModel.setDeductedAmount(deductionAmount);
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return deductionAmount;
    }

    public int getCartCount() {
        if (cartModelList1 != null && !cartModelList1.isEmpty()) {
            return cartModelList1.size();
        }
        return 0;
    }

    public List<CartModel> getCartList() {
        return this.cartModelList1;
    }

    private void registerUser() {
        String mobileNo = new MySharedPref(this).readString(PrefConstant.USER_PHONE_NO, "");
        if (!mobileNo.isEmpty()) {
            DatabaseReference mDatabaseRef = DatabaseReferences.getUserReference();
            mDatabaseRef.child(mobileNo).setValue(new UserModel(mobileNo, "", "", "", ""));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerUser();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onAddProduct(int productCount) {
        cartCount = productCount;
    }

    @Override
    public void onRemoveProduct(int productCount) {
        cartCount = productCount;
    }

    @Override
    public void updateTotalPrice(List<CartModel> cartModelList) {

    }

    public void setCartList(List<CartModel> cartModelList) {
        this.cartModelList1 = cartModelList;
    }
}
