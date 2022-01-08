package com.essentials.customerapp.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.view.ui.adapter.CustomPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.helper.Converter;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProductDetailsActivity extends BaseActivity {
    private static int cart_count = 0;
    int cartId;
    CartModel cartModel;
    private TextView tvTitle;
    private Toolbar toolbar;
    ViewPager viewPager;
    List<String> imageUrlList1 = new ArrayList<>();
    List<String> imageUrlList = new ArrayList<>();
    LinearLayout layout_dot;
    ImageView[] dot;
    private List<CartModel> cartModelList1 = new ArrayList<>();
    private CustomPagerAdapter pagerAdapter;
    private boolean descriptionSelected = false;
    private boolean unitSelected = false;
    private boolean disclaimerSelected = false;
    private boolean moreSelected = false;
    private String productSP, productUnitAdded, productAttribute, amountSubtotal;
    private CartRepository cartRepository;
    private MySharedPref sharedPref;
    private int pQuantity = 0;
    private Timer timer;
    private String productId;
    TextView tvProductName, tvSP, tvMRP, tvDiscount, tvOutOfStock, tvQuantityMinus, tvQuantityAdded,
            tvQuantityPlus, tvProductQuantity, tvDescriptionTitle, tvDescription, tvUnitTitle, tvUnit,
            tvDisclaimer, tvDisclaimerTitle;
    RelativeLayout rlMRP, shopNow, rlDescription, rlUnit, rlDisclaimer;
    LinearLayout llAddMinus;
    ImageView ivDescriptionImage, ivUnitImage, ivDisclaimer;
    private RelativeLayout rlProgress;
    private LinearLayout llDeliveryCharges;
    private TextView tvDeliveryCharges;
    private TextView tvMoreInfo;
    private TextView tvMoreInfoTitle;
    private ImageView ivMoreInfo;
    private RelativeLayout rlMoreInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        timer = new Timer();
        assignRef();
        cartRepository = CartRepository.getInstance();
        sharedPref = new MySharedPref(this);
        cart_count = getCartCount();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        getIntentData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cart_count = getCartCount();
        invalidateOptionsMenu();
        observeCartData();
        fetchProductData(productId);
    }


    private void assignRef() {
        rlProgress = findViewById(R.id.rlProgress);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        llDeliveryCharges = findViewById(R.id.llDeliveryCharges);
        toolbar = findViewById(R.id.white_toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvProductName = findViewById(R.id.tvProductName);
        tvSP = findViewById(R.id.tvSP);
        rlMRP = findViewById(R.id.rlMRP);
        tvMRP = findViewById(R.id.tvMRP);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvOutOfStock = findViewById(R.id.tvOutOfStock);
        llAddMinus = findViewById(R.id.quantity_ll);
        shopNow = findViewById(R.id.rlAddView);
        tvQuantityMinus = findViewById(R.id.tvQuantityMinus);
        tvQuantityAdded = findViewById(R.id.tvQuantityAdded);
        tvQuantityPlus = findViewById(R.id.tvQuantityPlus);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvDescriptionTitle = findViewById(R.id.tvDescriptionTitle);
        tvDisclaimerTitle = findViewById(R.id.tvDisclaimerTitle);
        rlDescription = findViewById(R.id.rlDescription);
        ivDescriptionImage = findViewById(R.id.ivDescriptionImage);
        tvDescription = findViewById(R.id.tvDescription);
        rlUnit = findViewById(R.id.rlUnit);
        rlDisclaimer = findViewById(R.id.rlDisclaimer);
        rlMoreInfo = findViewById(R.id.rlMoreInfo);
        tvUnitTitle = findViewById(R.id.tvUnitTitle);

        tvDisclaimer = findViewById(R.id.tvDisclaimer);
        tvMoreInfo = findViewById(R.id.tvMoreInfo);
        tvMoreInfoTitle = findViewById(R.id.tvMoreInfoTitle);
        ivMoreInfo = findViewById(R.id.ivMoreInfo);
        ivUnitImage = findViewById(R.id.ivUnitImage);
        ivDisclaimer = findViewById(R.id.ivDisclaimer);
        tvUnit = findViewById(R.id.tvUnit);
        viewPager = findViewById(R.id.viewpager);
        layout_dot = findViewById(R.id.llDot);
//        scheduleSlider();
        setSlidingImageView();
    }

    private int page_position = 0;
    private int dotscount;


    public void scheduleSlider() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    private void setSlidingImageView() {
        pagerAdapter = new CustomPagerAdapter(ProductDetailsActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(20);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int ij = 0; ij < dotscount; ij++) {
                    dot[ij].setImageDrawable(ContextCompat.getDrawable(ProductDetailsActivity.this, R.drawable.non_active_dot));
                }

                dot[position].setImageDrawable(ContextCompat.getDrawable(ProductDetailsActivity.this, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void addDot() {
        dot = new ImageView[imageUrlList.size()];
        layout_dot.removeAllViews();
        dotscount = imageUrlList.size();

        for (int i = 0; i < dot.length; i++) {
            dot[i] = new ImageView(this);
            dot[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            layout_dot.addView(dot[i], params);
        }
        //active dot
        if (dot.length > 0)
            dot[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));
    }

    private void getIntentData() {
        Intent intent = getIntent();
        productId = intent.getStringExtra("ProductId");
        fetchProductData(productId);
    }


    private void observeCartData() {
       /* cartModelList1 = getCartList();
        cart_count = cartModelList1.size();
        invalidateOptionsMenu();
        showShopNowView(productId);*/
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance()
                    .getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList) {
                    if (cartModelList != null) {
                        cartModelList1 = cartModelList;
                        ProductDetailsActivity.super.setCartList(cartModelList);
                        cart_count = cartModelList.size();
                        invalidateOptionsMenu();
                        showShopNowView(productId);
                    }
                }
            });
        } else {
            if (!new MySharedPref(this).readString(PrefConstant.CART_ITEM, "").isEmpty()) {
                List<CartModel> list;
                Gson gson = new Gson();
                String cartItem = new MySharedPref(this).readString(PrefConstant.CART_ITEM, "");
                if (!cartItem.isEmpty()) {
                    Type type = new TypeToken<List<CartModel>>() {
                    }.getType();
                    cartModelList1 = gson.fromJson(cartItem, type);
                    cart_count = cartModelList1.size();
                    invalidateOptionsMenu();
                    showShopNowView(productId);
                }

            }
        }
    }


    private void fetchProductData(String productId) {
        ProductsRepository.getInstance().getProductData(productId)
                .observe(this, new Observer<ProductModel>() {
                    @Override
                    public void onChanged(ProductModel productModel) {
                        if (productModel != null) {
                            setValueToView(productModel);
                        }
                    }
                });
    }

    private void setValueToView(ProductModel productModel) {
        imageUrlList.clear();
        if (!productModel.getProduct_Image_Url().isEmpty()) {
            imageUrlList1 = productModel.getProduct_Image_Url();
            for (String imageUrl : imageUrlList1) {
                if (!imageUrl.isEmpty())
                    imageUrlList.add(imageUrl);
            }
            pagerAdapter.setDataList(imageUrlList);
            addDot();
        }
        tvProductName.setText(productModel.getProduct_Name());
        tvTitle.setText(productModel.getProduct_Name());
        tvSP.setText(productModel.getProduct_SP());
        tvMRP.setText(productModel.getProduct_MRP());


        /*If MRP is greater than SP show MRP view*/
        if (Double.parseDouble(productModel.getProduct_MRP()) > Double.parseDouble(productModel.getProduct_SP())) {
            rlMRP.setVisibility(View.VISIBLE);
        } else {
            rlMRP.setVisibility(View.INVISIBLE);
        }

        /*Product discount*/
        if (productModel.getProductDiscount().isEmpty()) {
            int dis = AppUtils.calculateDiscount(productModel.getProduct_MRP(), productModel.getProduct_SP());
            if (dis >= 5) {
                productModel.setProductDiscount(dis + "% OFF");
            } else {
                productModel.setProductDiscount("0");
            }
        }

        if (productModel.getDeliveryCharge()!=null && !productModel.getDeliveryCharge().equalsIgnoreCase("0")){
            llDeliveryCharges.setVisibility(View.VISIBLE);
            tvDeliveryCharges.setText(productModel.getDeliveryCharge());
        }

        /*Show product discount view and mrp view*/
        if (productModel.getProductDiscount().equals("0")) {
            tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            tvDiscount.setText(productModel.getProductDiscount());
            tvMRP.setText(productModel.getProduct_MRP());
            tvDiscount.setVisibility(View.VISIBLE);
        }

        tvUnit.setText(productModel.getProduct_Quantity_Type());
        tvProductQuantity.setText(productModel.getProduct_Quantity_Type());
        tvDescription.setText(productModel.getProduct_Details());

        tvMoreInfo.setText(productModel.getProduct_Details()!=null?productModel.getMoreInfo():"");

        rlProgress.setVisibility(View.GONE);

        rlDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionSelected) {
                    descriptionSelected = false;
                    ivDescriptionImage.setImageResource(R.drawable.ic_keyboard_arrow_down_yello_35dp);
                    tvDescriptionTitle.setTextColor(getResources().getColor(R.color.divider));
                    tvDescription.setMaxLines(1);
                } else {
                    descriptionSelected = true;
                    ivDescriptionImage.setImageResource(R.drawable.ic_keyboard_arrow_up_yello_35dp);
                    tvDescriptionTitle.setTextColor(getResources().getColor(R.color.black));
                    tvDescription.setMaxLines(1000);
                }
            }
        });

        rlDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disclaimerSelected) {
                    disclaimerSelected = false;
                    ivDisclaimer.setImageResource(R.drawable.ic_keyboard_arrow_down_yello_35dp);
                    tvDisclaimerTitle.setTextColor(getResources().getColor(R.color.divider));
                    tvDisclaimer.setMaxLines(1);
                } else {
                    disclaimerSelected = true;
                    ivDisclaimer.setImageResource(R.drawable.ic_keyboard_arrow_up_yello_35dp);
                    tvDisclaimerTitle.setTextColor(getResources().getColor(R.color.black));
                    tvDisclaimer.setMaxLines(10);
                }
            }
        });

        rlMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreSelected) {
                    moreSelected = false;
                    ivMoreInfo.setImageResource(R.drawable.ic_keyboard_arrow_down_yello_35dp);
                    tvMoreInfoTitle.setTextColor(getResources().getColor(R.color.divider));
                    tvMoreInfo.setMaxLines(1);
                } else {
                    moreSelected = true;
                    ivMoreInfo.setImageResource(R.drawable.ic_keyboard_arrow_up_yello_35dp);
                    tvMoreInfoTitle.setTextColor(getResources().getColor(R.color.black));
                    tvMoreInfo.setMaxLines(10);
                }
            }
        });

        rlUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unitSelected) {
                    unitSelected = false;
                    ivUnitImage.setImageResource(R.drawable.ic_keyboard_arrow_down_yello_35dp);
                    tvUnitTitle.setTextColor(getResources().getColor(R.color.divider));
                    tvUnit.setMaxLines(1);
                } else {
                    unitSelected = true;
                    ivUnitImage.setImageResource(R.drawable.ic_keyboard_arrow_up_yello_35dp);
                    tvUnitTitle.setTextColor(getResources().getColor(R.color.black));
                    tvUnit.setMaxLines(10);
                }
            }
        });

        shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopNow.setVisibility(View.GONE);
                llAddMinus.setVisibility(View.VISIBLE);
                productSP = productModel.getProduct_SP();
                productUnitAdded = "1";
                tvQuantityAdded.setText(productUnitAdded);
                productAttribute = productModel.getProduct_Quantity_Type();
                amountSubtotal = String.valueOf(Double.parseDouble(productSP) * Integer.parseInt(productUnitAdded));

                CartModel cartModel = new CartModel(productModel, productUnitAdded, amountSubtotal, AppUtils.dateAndTime());
                cartModelList1.add(cartModel);
                if (Constant.IS_USER_LOGIN) {
                    cartRepository.addProductToCart(cartModel);
                    Toast.makeText(ProductDetailsActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartModelList1);
                    sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                    Toast.makeText(ProductDetailsActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();

                }
                cart_count++;
                invalidateOptionsMenu();
            }
        });

        tvQuantityPlus.setOnClickListener(v -> {
            if (!cartModelList1.isEmpty()) {
                int total_item = Integer.parseInt(tvQuantityAdded.getText().toString());
                total_item++;
                tvQuantityAdded.setText(total_item + "");
                incDecProductCount(productModel, total_item);
            }
        });

        tvQuantityMinus.setOnClickListener(v -> {

            if (!cartModelList1.isEmpty()) {
                pQuantity = Integer.parseInt(tvQuantityAdded.getText().toString());
                if (pQuantity > 1) {
                    int total_item = Integer.parseInt(tvQuantityAdded.getText().toString());
                    total_item--;
                    tvQuantityAdded.setText(total_item + "");
                    incDecProductCount(productModel, total_item);
                } else {
                    deleteProductFromCart(productModel);
                }
            }
        });
    }

    private void deleteProductFromCart(ProductModel product) {
        shopNow.setVisibility(View.VISIBLE);
        llAddMinus.setVisibility(View.GONE);
        if (Constant.IS_USER_LOGIN) {
            cartRepository
                    .deleteProduct(product.getProduct_Id())
                    .observe(ProductDetailsActivity.this, responseModel -> {
                        if (responseModel != null) {
                            if (!cartModelList1.isEmpty()) {
                                removeProduct(product.getProduct_Id());
                                cart_count--;
                                invalidateOptionsMenu();
                            }
                        }
                    });
        } else {

            removeProduct(product.getProduct_Id());
            Gson gson = new Gson();
            String cartData = gson.toJson(cartModelList1);
            sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
            super.setCartList(cartModelList1);
            invalidateOptionsMenu();
        }

    }

    private void removeProduct(String product_id) {
        List<CartModel> cartRemoveLis = new ArrayList<>();

        for (CartModel cartModel : cartModelList1) {
            if (cartModel.getProduct_Id().equalsIgnoreCase(product_id)) {
                cartRemoveLis.add(cartModel);
            }
        }
        cartModelList1.removeAll(cartRemoveLis);
    }

    private void incDecProductCount(ProductModel product, int total_item) {
        for (int i = 0; i < this.cartModelList1.size(); i++) {
            if (this.cartModelList1.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                amountSubtotal = String.valueOf(Double.parseDouble(product.getProduct_SP()) * total_item);
                cartModelList1.get(i).setProdctQuantityUnit(total_item);
                cartModelList1.get(i).setSub_Total(amountSubtotal);
                super.setCartList(cartModelList1);
                /*Update product count in cart firebase db*/
                if (Constant.IS_USER_LOGIN) {
                    cartRepository.addProductToCart(cartModelList1.get(i));
                } else {
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartModelList1);
                    sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                }
            }
        }
    }

    private void showShopNowView(String product_id) {
        /*Show Product count if already added to cart*/
        if (!cartModelList1.isEmpty()) {
            for (int i = 0; i < cartModelList1.size(); i++) {
                if (cartModelList1.get(i).getProduct_Id().equalsIgnoreCase(product_id)) {
                    shopNow.setVisibility(View.GONE);
                    llAddMinus.setVisibility(View.VISIBLE);
                    tvQuantityAdded.setText("" + cartModelList1.get(i).getProdctQuantityUnit());
                    Log.d("Tag : ", cartModelList1.get(i).getProduct_Id() + "-->" + product_id);
                    break;
                } else {
                    shopNow.setVisibility(View.VISIBLE);
                    llAddMinus.setVisibility(View.GONE);
                    Log.d("Tag : ", cartModelList1.get(i).getProduct_Id() + "-->" + product_id);
                }
            }
        } else {
            shopNow.setVisibility(View.VISIBLE);
            llAddMinus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                onBackPressed();
                break;

            case R.id.cart_action:
                Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                intent.putExtra(Constants.COME_FROM, Constants.PRODUCT_VIEW);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;

            case R.id.search_action:
                Intent intent1 = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(ProductDetailsActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
        return true;
    }

}
