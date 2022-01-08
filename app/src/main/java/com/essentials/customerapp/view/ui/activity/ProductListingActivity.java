package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.GrandCategoryModel;
import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.helper.Converter;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.adapter.ProductPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductListingActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, AddorRemoveCallbacks {

    private ViewPager viewPager;
    private List<GrandCategoryModel> grandCategoryModels;
    private RelativeLayout rlError;
    private LinearLayout llDataFound;
    private TabLayout tabLayout;
    private ArrayList<TabLayout.Tab> tabList;
    public int cart_count = 0;
    private List<CartModel> cartModelList1 = new ArrayList<>();
    private String id;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);
        cart_count = getCartCount();
        viewPager = findViewById(R.id.viewPager);
        rlProgress = findViewById(R.id.rlProgress);
        llDataFound = findViewById(R.id.llDataFound);
        tabLayout = findViewById(R.id.tabLayout);
        rlError = findViewById(R.id.rlError);
        TextView tvTitle = findViewById(R.id.tvTitle);
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        fetchCartData(1);
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
        llDataFound.setVisibility(View.VISIBLE);
    }

    private void fetchCartData(int callType) {
//        showProgressHud();
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance().getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList) {
                    if (cartModelList != null) {
                        cartModelList1 = cartModelList;
                        ProductListingActivity.super.setCartList(cartModelList);
                        cart_count = cartModelList.size();
                        invalidateOptionsMenu();
                        /*if (callType == 0)
                            hideProgressHud();*/
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
                    ProductListingActivity.super.setCartList(cartModelList1);
                    cart_count = cartModelList1.size();
                    invalidateOptionsMenu();

                }
            }
            /*if (callType == 0)
                hideProgressHud();*/
        }
        getIntentData();
    }

    @Override
    public void onBackPressed() {
        ProductListingActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    protected void onResume() {
        super.onResume();
        cart_count = getCartCount();
        invalidateOptionsMenu();
        fetchCartData(0);
    }

    public void onAddProduct(int productCount) {
        cart_count = productCount;
        super.onAddProduct(productCount);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_action:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra(Constants.COME_FROM, Constants.HOME);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case android.R.id.home:
                ProductListingActivity.this.finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
    public void onRemoveProduct(int productCount) {
        cart_count = productCount;
        super.onRemoveProduct(cart_count);
        invalidateOptionsMenu();
    }

    private void getIntentData() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        if (getIntent() != null) {
            String name = getIntent().getStringExtra(Constants.CAT_NAME);
            id = getIntent().getStringExtra(Constants.CAT_ID);
            tvTitle.setText(name);
            if (id != null && !id.isEmpty())
                fetchGrandCategory(id);
        }

    }

    private void fetchGrandCategory(String catId) {
        ProductsRepository.getInstance().getGrandCategoryList(catId)
                .observe(ProductListingActivity.this, new Observer<List<GrandCategoryModel>>() {
                    @Override
                    public void onChanged(List<GrandCategoryModel> grandCategoryModels) {
                        if (grandCategoryModels != null) {
                            if (grandCategoryModels.isEmpty()) {
                                hideProgressHud();
                                llDataFound.setVisibility(View.GONE);
                                rlError.setVisibility(View.VISIBLE);
                            } else {
                                rlError.setVisibility(View.GONE);
                                setTabLayoutAndViewPager(grandCategoryModels);
                            }
                        }
                    }
                });

    }

    int count = 0;

    private void setTabLayoutAndViewPager(List<GrandCategoryModel> grandCategoryModels) {
        if (count == 1)
            return;
        count = 1;
        List<TabLayout.Tab> tabList = new ArrayList<>();

        for (int i = 0; i < grandCategoryModels.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tabLayout.addTab(tab);
            tabList.add(tab);
        }
        fetchProduct(grandCategoryModels);
    }


    private void fetchProduct(List<GrandCategoryModel> grandCategoryModels) {
        List<List<ProductModel>> productList = new ArrayList<>();
        for (int position = 0; position < grandCategoryModels.size(); position++) {
            int finalPosition = position;
            DatabaseReferences.getProductRef().orderByChild("Product_Grand_Cat_Id")
                    .equalTo(grandCategoryModels.get(position).getGrand_Cat_Id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<ProductModel> productModelList = new ArrayList<>();
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                                productModelList.add(ringtoneModel);
                            }
                            productList.add(productModelList);
                            if (productList.size() == grandCategoryModels.size()) {
                                if (productList.isEmpty())
                                    return;
                                ProductPager adapter = new ProductPager(getSupportFragmentManager(), tabLayout.getTabCount(), productList, grandCategoryModels);
                                viewPager.setAdapter(adapter);
                                tabLayout.setupWithViewPager(viewPager);
                                viewPager.setOffscreenPageLimit(0);
                                tabLayout.addOnTabSelectedListener(ProductListingActivity.this);
                                hideProgressHud();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            hideProgressHud();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(ProductListingActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
