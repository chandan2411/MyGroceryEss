package com.essentials.customerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.helper.Converter;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.BaseActivity;
import com.essentials.customerapp.view.ui.activity.CartActivity;
import com.essentials.customerapp.view.ui.activity.ProductListingActivity;
import com.essentials.customerapp.view.ui.activity.SubCategoryActivity;
import com.essentials.customerapp.view.ui.adapter.ProductAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.essentials.customerapp.utilities.Constants.OFFER_ID;
import static com.essentials.customerapp.utilities.Constants.OFFER_NAME;
import static com.essentials.customerapp.utilities.Constants.SEARCH_TEXT;

public class ProductActivity extends BaseActivity implements AddorRemoveCallbacks {

    private TextView tvTitle;
    private Toolbar toolbar;
    private String searchText="";
    private RecyclerView rvProduct;
    private RelativeLayout rlError;
    private ProductAdapter mAdapter;
    public int cart_count = 0;
    private List<CartModel> cartModelList1 = new ArrayList<>();
    private String offerId;
    private String offerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        tvTitle = findViewById(R.id.tvTitle);
        toolbar = findViewById(R.id.white_toolbar);
        rvProduct = findViewById(R.id.rvProduct);
        rlError = findViewById(R.id.rlError);
        setUpRecyclerView();
        setSupportActionBar(toolbar);
        observeCartData();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        searchText = getIntent().getStringExtra(SEARCH_TEXT);
        offerId = getIntent().getStringExtra(OFFER_ID);
        offerName = getIntent().getStringExtra(OFFER_NAME);
        if (!searchText.isEmpty()){
            tvTitle.setText(searchText);
        }else {
            tvTitle.setText(offerName);
        }
        serachProductData(searchText);
    }

    private void serachProductData(String searchText) {
        ProductsRepository.getInstance().getProductList("Product_Modified_Date", "",
                "", searchText, offerId).observe(this, new Observer<List<ProductModel>>() {
            @Override
            public void onChanged(List<ProductModel> productModels) {
                if (productModels != null) {
                    if (productModels.isEmpty()) {
                        rlError.setVisibility(View.VISIBLE);
                        rvProduct.setVisibility(View.GONE);
                    } else {
                        rlError.setVisibility(View.GONE);
                        rvProduct.setVisibility(View.VISIBLE);
                        mAdapter.setDataList(productModels);
                    }
                }
                dismissDialog();
            }
        });
    }

    private void setUpRecyclerView() {
        mAdapter = new ProductAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvProduct.setLayoutManager(mLayoutManager);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        rvProduct.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        ProductActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_action:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra(Constants.COME_FROM, Constants.HOME);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                return true;
            case android.R.id.home:
                ProductActivity.this.finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cart_count = getCartCount();
        invalidateOptionsMenu();
        observeCartData();
    }

    public void onAddProduct(int productCount) {
        cart_count = productCount;
        super.onAddProduct(cart_count);
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        MenuItem menuItem1 = menu.findItem(R.id.search_action);
        menuItem1.setVisible(false);
        menuItem.setIcon(Converter.convertLayoutToImage(ProductActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
        return true;
    }

    @Override
    public void onRemoveProduct(int productCount) {
        cart_count = productCount;
        super.onRemoveProduct(cart_count);
        invalidateOptionsMenu();
    }


    private void observeCartData() {
        /*cartModelList1 = getCartList();
        cart_count = cartModelList1.size();
        mAdapter.setCartModelList(cartModelList1);
        invalidateOptionsMenu();*/
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance().getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList) {
                    if (cartModelList != null) {
                        cartModelList1 = cartModelList;
                        ProductActivity.super.setCartList(cartModelList);
                        cart_count = cartModelList.size();
                        mAdapter.setCartModelList(cartModelList);
                        invalidateOptionsMenu();
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
                    ProductActivity.super.setCartList(cartModelList1);
                    cart_count = cartModelList1.size();
                    mAdapter.setCartModelList(cartModelList1);
                    invalidateOptionsMenu();
                }

            }
        }
    }
}
