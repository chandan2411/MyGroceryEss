package com.essentials.customerapp.view.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.data.repository.AddressRepository;
import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.utilities.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.essentials.customerapp.R;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.utilities.ViewDialog;
import com.essentials.customerapp.view.ui.adapter.CartAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.essentials.customerapp.utilities.Constant.SUB_TOTAL;
import static com.essentials.customerapp.utilities.Constant.TOTAL_DELIVERY_CHARGES;
import static com.essentials.customerapp.utilities.Constant.TOTAL_ESSENTIALS_DISCOUNT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_MRP;
import static com.essentials.customerapp.utilities.Constant.TOTAL_PRODUCT_DISCOUNT;

public class CartActivity extends BaseActivity {
    private String ID;
    private String title;
    //    MySharedPref sh;
    List<CartModel> cartModelList = new ArrayList<>();
    Gson gson;
    RecyclerView recyclerView;
    CartAdapter adapter;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RelativeLayout emptyCart;
    LinearLayout checkoutLL;
    private String mState = "SHOW_MENU";
    private TextView tvTitle;
    private Toolbar toolbar;
    private MySharedPref sharedPref;
    private TextView tvLogin;
    private RelativeLayout rlParent;
    private TextView tvDeliveryCharges, tvEssentialsDiscount, tvTotalProductDiscountAmount, tvTotalMRPAmount;
    private TextView tvSubtotal, tvSubtotal1;
    private RelativeLayout rlCartFilled;
    private List<AddressModel> addressModelList = new ArrayList<>();
    private double productMrp = 0.0;
    private double normalDiscount = 0.0;
    private double calculatedEssentialDiscount = 0.0;
    private double shippingCharge = 0.0;
    private double subTotal = 0.0;
    private TextView tvStartShopping;
    private TextView tvSubTitle;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;
    HomeActivity  homeActivity= new HomeActivity();
    double TotalMRPForDiscount, discountPercentage, MaximumwalletAmountForOrder=0.0, DeliveryChargesForOrder=0.0,MinimumAmountForaOrder;
    String offerImageURL = "";
    ImageView offerImage;

    //Cupon Code Section
    TextView cupon_description, tvTotalCupondDiscountAmount,cvRemoveCupon;
    EditText cupon_Code;
    Button applyCode;
    String CouponName, CouponDetails;
    Double CuponDiscountAmount, CuponDiscountPercentage1;
    ProgressDialog progressDialog;
    double productSpforCupon = 0;
    LinearLayout promolayout;
    String promoCode="";
    double promodiscount=0.00;
    boolean promoCodeBool = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getOffers2Data();
        tvTitle = findViewById(R.id.tvTitle);

        tvSubTitle = findViewById(R.id.tvSubTitle);
        progressDialog = new ProgressDialog(CartActivity.this);

        //offerImageURL= homeActivity.Offerimageurl;
        cvRemoveCupon = findViewById(R.id.cvRemoveCupon);
        offerImage = findViewById(R.id.cDiscountImage);
        tvStartShopping = findViewById(R.id.tvStartShopping);
        rlProgress = findViewById(R.id.rlProgress);
        toolbar = findViewById(R.id.white_toolbar);
        rlParent = findViewById(R.id.rlParent);
        tvLogin = findViewById(R.id.tvLogin);
        rlCartFilled = findViewById(R.id.rlCartFilled);
        sharedPref = new MySharedPref(this);
        inititeProgessHud();
        showProgressHud();
        tvStartShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        cvRemoveCupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    promoCodeBool = false;
                    promodiscount=0.00;
                    applyCode.setEnabled(true);
                    promolayout.setVisibility(View.GONE);
                    CuponDiscountAmount=0.00;
                    CuponDiscountPercentage1=0.00;
                    cupon_Code.setText("");
                    cupon_description.setText(R.string.cupon_description);
                    cupon_Code.setEnabled(true);
                    cvRemoveCupon.setVisibility(View.INVISIBLE);
                    setTotalPriceAndDiscount();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*Set Text on Bottom */
        if (Constant.IS_USER_LOGIN) {
            tvLogin.setText(getResources().getString(R.string.checkout));

        } else {
            tvLogin.setText(getResources().getString(R.string.login_to_checkout));
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        tvTitle.setText("Cart Items");
        gson = new Gson();
        emptyCart = findViewById(R.id.empty_cart_img);
        if (Constant.IS_USER_LOGIN)
            fetchUserAddress();
        setUpCartRecyclerview();

        applyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(Constant.IS_USER_LOGIN) {
                promoCode = cupon_Code.getText().toString();
                if (subTotal < MinimumAmountForaOrder) {
                    cupon_description.setText("Minimum amount should be  " + MinimumAmountForaOrder + " to apply cupon");
                    Toast.makeText(CartActivity.this, " ", Toast.LENGTH_SHORT).show();
                } else {
                    promoCodeBool = true;
                    if (promoCode.equals("") || promoCode == "") {
                        Toast.makeText(CartActivity.this, "Enter PromoCode ", Toast.LENGTH_SHORT).show();

                    } else {
                        getCuponDetails(promoCode);
                    }
                }


            }else {
                   Toast.makeText(CartActivity.this, "Please login to use Coupon", Toast.LENGTH_SHORT).show();
               }
        }
        });
    }


    private void getOffers2Data() {

        //  Query productRef = DatabaseReferences.getOffer2Ref().orderByChild("Offers2");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Offers2/" + "-MFxyFIvLTr9IxOFdFqB");
        //  Query productRef = DatabaseReferences.getOffer2Ref().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //get id and phone_number

                discountPercentage = Double.parseDouble(String.valueOf(dataSnapshot.child("WalletPercentageUsed").getValue()));
                TotalMRPForDiscount = Double.parseDouble(String.valueOf(dataSnapshot.child("TotalMRPForUsingwallet").getValue()));
                MaximumwalletAmountForOrder = Double.parseDouble(String.valueOf(dataSnapshot.child("MaximumwalletAmountForOrder").getValue()));
                MinimumAmountForaOrder = Double.parseDouble(String.valueOf(dataSnapshot.child("MinimumAmountForaOrder").getValue()));
                DeliveryChargesForOrder = Double.parseDouble(String.valueOf(dataSnapshot.child("DeliveryChargesForOrder").getValue()));
                offerImageURL = String.valueOf(dataSnapshot.child("Offers_Image_Url").getValue());
              //  Toast.makeText(CartActivity.this, offerImageURL,Toast.LENGTH_SHORT).show();
              //  Toast.makeText(CartActivity.this, offerImageURL,Toast.LENGTH_SHORT).show();
                if(!offerImageURL.equals("")){
                   // offerImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(offerImageURL).into(offerImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        rlProgress.setVisibility(View.GONE);
    }

    private void fetchUserAddress() {
//
        AddressRepository.getInstance().getUserAddress("").observe(this, new Observer<List<AddressModel>>() {
            @Override
            public void onChanged(List<AddressModel> addressModels) {
                if (addressModels != null && !addressModels.isEmpty()) {
//                    dismissDialog();
                    addressModelList = addressModels;
                    if (sharedPref.readString(PrefConstant.SELECTED_ADD_ID, "").isEmpty()) {
                        sharedPref.writeString(PrefConstant.SELECTED_ADD_ID, addressModels.get(0).getAddressId());
                    }
                }
            }
        });
    }

    private void setUpCartRecyclerview() {
       // TotalMRPForDiscount =Double.parseDouble(homeActivity.MRPForDiscount);
      //  discountPercentage= Double.parseDouble(homeActivity.WalletDiscount);
        cartModelList = new ArrayList<>();
        recyclerView = findViewById(R.id.cart_rv);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        tvTotalMRPAmount = findViewById(R.id.tvTotalMRPAmount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvSubtotal1 = findViewById(R.id.tvSubtotal1);
        tvEssentialsDiscount = findViewById(R.id.tvEssentialsDiscount);
        tvTotalProductDiscountAmount = findViewById(R.id.tvTotalProductDiscountAmount);
        //cupon code
        cupon_description = findViewById(R.id.cupon_description);
        cupon_Code  = findViewById(R.id.cupon_Code);
        applyCode  = findViewById(R.id.applyCode);
        tvTotalCupondDiscountAmount= findViewById(R.id.tvTotalCupondDiscountAmount);
        promolayout = findViewById(R.id.promolayout);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        adapter = new CartAdapter(CartActivity.this);
        recyclerView.setAdapter(adapter);


//        observeCartData();
    }

    public void showNoOfCount(List<CartModel> cartModelList) {
        int totalItemCount = 0;
        for (CartModel cartModel : cartModelList) {
            totalItemCount = totalItemCount + cartModel.getProdctQuantityUnit();
        }
        tvSubTitle.setText(totalItemCount + " Items");
    }

    private void fetchCartData() {
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance()
                    .getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList1) {
                    if (cartModelList1 != null) {
                        if (!cartModelList1.isEmpty()) {
                            cartModelList = cartModelList1;
                            adapter.setDataModeList(cartModelList);
                            try {
                                setTotalPriceAndDiscount();
                            } catch (Exception ex) {
                                Log.e(TAG, ex.getMessage());
                                hideProgressHud();
                            }
                            CartActivity.super.setCartList(cartModelList);
                            rlCartFilled.setVisibility(View.VISIBLE);
                            showNoOfCount(cartModelList);
                            emptyCart.setVisibility(View.GONE);
                            tvSubTitle.setVisibility(View.VISIBLE);
                        } else {
                            tvSubTitle.setVisibility(View.GONE);
                            emptyCart.setVisibility(View.VISIBLE);
                            rlCartFilled.setVisibility(View.GONE);
                            hideProgressHud();
                        }
                    }
                }
            });
        } else {
            Gson gson = new Gson();
            String cartItem = sharedPref.readString(PrefConstant.CART_ITEM, "");
            if (!cartItem.isEmpty()) {
                Log.d("CART : ", cartItem);
                Type type = new TypeToken<List<CartModel>>() {
                }.getType();
                cartModelList = gson.fromJson(cartItem, type);

                if (!cartModelList.isEmpty()) {
                    super.setCartList(cartModelList);
                    adapter.setDataModeList(cartModelList);
                    rlCartFilled.setVisibility(View.VISIBLE);
                    emptyCart.setVisibility(View.GONE);
                    tvSubTitle.setVisibility(View.VISIBLE);
                    try {
                        setTotalPriceAndDiscount();
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                        hideProgressHud();
                    }
                } else {
                    rlCartFilled.setVisibility(View.GONE);
                    emptyCart.setVisibility(View.VISIBLE);
                    tvSubTitle.setVisibility(View.GONE);
                    hideProgressHud();
                }
            } else {
                hideProgressHud();
                emptyCart.setVisibility(View.VISIBLE);
                rlCartFilled.setVisibility(View.GONE);
                tvSubTitle.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.IS_USER_LOGIN)
            tvLogin.setText(getResources().getString(R.string.checkout));
        else
            tvLogin.setText(getResources().getString(R.string.login_to_checkout));
        fetchCartData();
    }

    private void setTotalPriceAndDiscount() throws Exception {
        double productSp = 0;
        productMrp = 0.0;
        normalDiscount = 0.0;
        calculatedEssentialDiscount = 0.0;
        subTotal = 0.0;
        for (CartModel cartModel : cartModelList) {
            try {
                if (cartModel.getDeliveryCharge() != null && !cartModel.getDeliveryCharge().equalsIgnoreCase("0")) {
                    shippingCharge = +Double.parseDouble(cartModel.getDeliveryCharge());
                }
            } catch (Exception ex) {
                Log.e(TAG, "setTotalPriceAndDiscount: " + ex.getMessage(), ex);
            }
            productMrp = productMrp + ((Double.parseDouble(cartModel.getProduct_MRP())) * cartModel.getProdctQuantityUnit());
            productSp = productSp + ((Double.parseDouble(cartModel.getProduct_SP())) * cartModel.getProdctQuantityUnit());
        }
        normalDiscount = productMrp - productSp;


        tvTotalMRPAmount.setText(String.valueOf(productMrp));
        tvTotalProductDiscountAmount.setText(String.format(Locale.ENGLISH, "%.2f", normalDiscount));



        if (productSp >= MinimumAmountForaOrder ){
           shippingCharge=0.0;
            tvDeliveryCharges.setText("Free");
        }
        else{
            shippingCharge = DeliveryChargesForOrder;
            tvDeliveryCharges.setText(getResources().getString(R.string.indian_rupee_sign) + " " + shippingCharge);
        }

       /* if (shippingCharge == 0.0){
            tvDeliveryCharges.setText("Free");
        }

        else{
            tvDeliveryCharges.setText(getResources().getString(R.string.indian_rupee_sign) + " " + shippingCharge);
        }*/

               // if total amount is equal or greater than 999 then apply 2% of total amount from essentials wallet
        if (productSp >= TotalMRPForDiscount) {
            calculatedEssentialDiscount = ((productSp / 100) * discountPercentage);   // calculate essentials discount of 2 percent
        } else {
            calculatedEssentialDiscount = 0.0;
        }
        /*If 2 percent of total amount greater than 100 show 100 essentials discount else calculate
        amount and check wallet*/
        if (calculatedEssentialDiscount > MaximumwalletAmountForOrder)
            calculatedEssentialDiscount = MaximumwalletAmountForOrder;

        if (Constant.IS_USER_LOGIN) {

            if (calculatedEssentialDiscount > 0) {

                calculateProductEssentialsDiscount(productSp, shippingCharge);
                if(promoCodeBool){
                    calculateCuponDiscount();
                }
            } else {
                tvEssentialsDiscount.setText("00");

                subTotal = (productSp + shippingCharge) - 0;
                String.format(Locale.ENGLISH, "%.2f", subTotal);

                tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                tvSubtotal1.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                hideProgressHud();
            }
        } else {

            tvEssentialsDiscount.setText("00");
            subTotal = (productSp + shippingCharge) - calculatedEssentialDiscount;
            tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
            tvSubtotal1.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
            hideProgressHud();
        }productSpforCupon = subTotal;

    }

    private void calculateProductEssentialsDiscount(double productSp, double shippingCharge) {
        WalletRepository repository = WalletRepository.getInstance();
        repository.getWalletData(Constant.USER_ID)
                .observe(this, walletModels -> {
                    if (walletModels != null && !walletModels.isEmpty()) {
                        double walletAmount = walletModels.get(0).getWalletTotalBalance();
                        if (walletAmount < calculatedEssentialDiscount)
                            calculatedEssentialDiscount = walletAmount;
                        /*round off essentials amount*/
                        String essentialDiscount = String.format(Locale.ENGLISH, "%.2f", calculatedEssentialDiscount);
                        calculatedEssentialDiscount = Double.parseDouble(essentialDiscount);

                        tvEssentialsDiscount.setText(String.valueOf(calculatedEssentialDiscount));
                        subTotal = (productSp + shippingCharge) - calculatedEssentialDiscount;
                        tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                        tvSubtotal1.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                    } else {
                        tvEssentialsDiscount.setText(String.valueOf(calculatedEssentialDiscount));
                        subTotal = (productSp + shippingCharge) - calculatedEssentialDiscount;
                        tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                        tvSubtotal1.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
                    }
                    productSpforCupon = subTotal;

                    hideProgressHud();
                });
    }

    public void getCuponDetails(String Code1){
        progressDialog.setMessage("Applying Code");
        progressDialog.show();
        Query coderef;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        coderef = ref.child("Coupons").orderByChild("CouponName").equalTo(promoCode);
        coderef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        CouponDetails = ds.child("CouponDetails").getValue().toString();
                        CouponName = ds.child("CouponName").getValue().toString();
                        CuponDiscountPercentage1 = Double.parseDouble(ds.child("DiscountPercentage").getValue().toString());
                        CuponDiscountAmount = Double.parseDouble(ds.child("MaximumDiscountAmount").getValue().toString());
                        cupon_description.setText("Cupon Code Applied Successfully "+CouponDetails);
                        cupon_Code.setEnabled(false);
                        progressDialog.dismiss();
                        calculateCuponDiscount();
                        applyCode.setEnabled(false);
                        cvRemoveCupon.setVisibility(View.VISIBLE);

                    }
                }else{
                    Toast.makeText(CartActivity.this,"invalid code",Toast.LENGTH_SHORT).show();
                    cupon_description.setText("Invalid Code");
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void calculateCuponDiscount(){
        promodiscount=0.00;

        promodiscount = ((productSpforCupon / 100) * CuponDiscountPercentage1);
        if(promodiscount>CuponDiscountAmount){
            promodiscount=CuponDiscountAmount;
            subTotal = productSpforCupon  - promodiscount;

        }else{
            subTotal = productSpforCupon  - promodiscount;
        }

        tvTotalCupondDiscountAmount.setText(String.format(Locale.ENGLISH, "%.2f", promodiscount));
        promolayout.setVisibility(View.VISIBLE);
        tvSubtotal.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
        tvSubtotal1.setText(String.format(Locale.ENGLISH, "%.2f", subTotal));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        CartActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    public void onCheckoutClicked(View view) {
        if (Constant.IS_USER_LOGIN) {
            Intent intent;
            if (addressModelList.size() > 0) {
                intent = new Intent(new Intent(getApplicationContext(), AddressAndDateTimeActivity.class));
            } else {
                intent = new Intent(new Intent(getApplicationContext(), AddressListingActivity.class));
                intent.putExtra("comefrom", "cart");
            }
            intent.putExtra(TOTAL_MRP, productMrp);
            intent.putExtra(TOTAL_PRODUCT_DISCOUNT, normalDiscount);
            intent.putExtra(TOTAL_ESSENTIALS_DISCOUNT, calculatedEssentialDiscount);
            intent.putExtra(TOTAL_DELIVERY_CHARGES, shippingCharge);
            intent.putExtra(SUB_TOTAL, subTotal);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        } else {
            ViewDialog.showDialog(CartActivity.this, Constants.SignInMessage, Constants.SignInTitle, true);
        }

    }

    public void updateTotalPrice1(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        showNoOfCount(cartModelList);
        if (cartModelList.isEmpty()) {
            rlCartFilled.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
            tvSubTitle.setVisibility(View.GONE);
        } else {
            tvSubTitle.setVisibility(View.VISIBLE);
            rlCartFilled.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);
        }
        try {
            setTotalPriceAndDiscount();
            calculateCuponDiscount();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


}
