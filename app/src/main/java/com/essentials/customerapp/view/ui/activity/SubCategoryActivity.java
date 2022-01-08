package com.essentials.customerapp.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.helper.Converter;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.SubCategoryModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.view.ui.adapter.SubCategoryAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends BaseActivity {

    RecyclerView sub_category_rv;
    SubCategoryAdapter subCategoryAdapter;
    private String title;
    private String catID;
    private RelativeLayout rlParent;
    private TextView tvTitle;
    private Toolbar toolbar;
    private KProgressHUD progressHUD;
    private LinearLayout llParent;
    private int cart_count = 0;
    private List<CartModel> cartModelList1 = new ArrayList<>();
    private RelativeLayout rlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        sub_category_rv = findViewById(R.id.sub_category_rv);
        llParent = findViewById(R.id.llParent);
        rlProgress = findViewById(R.id.rlProgress);
        tvTitle = findViewById(R.id.tvTitle);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        inititeProgessHud();
        observeCartData();
        getIntentData();
        setUpGridRecyclerView();
    }


    @Override
    public void onBackPressed() {
        SubCategoryActivity.this.finish();
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
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);

    }

    private void observeCartData() {
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance().getUserCartData().observe(this, new Observer<List<CartModel>>() {
                @Override
                public void onChanged(List<CartModel> cartModelList) {
                    if (cartModelList != null) {
                        cartModelList1 = cartModelList;
                        SubCategoryActivity.super.setCartList(cartModelList);
                        cart_count = cartModelList.size();
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
                    SubCategoryActivity.super.setCartList(cartModelList1);
                    cart_count = cartModelList1.size();
                    invalidateOptionsMenu();
                    getIntentData();
                }

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        cart_count = getCartCount();
        invalidateOptionsMenu();
        observeCartData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        MenuItem menuItem1 = menu.findItem(R.id.search_action);
        menuItem1.setVisible(false);
        menuItem.setIcon(Converter.convertLayoutToImage(SubCategoryActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
        return true;
    }

    private void getIntentData() {
        if (getIntent() != null) {
            title = getIntent().getStringExtra(Constants.CAT_NAME);
            catID = getIntent().getStringExtra(Constants.CAT_ID);
        }
        tvTitle.setText(title);
    }

    private void setUpGridRecyclerView() {
        subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        sub_category_rv.setLayoutManager(mLayoutManager);
        sub_category_rv.setItemAnimator(new DefaultItemAnimator());
        sub_category_rv.setAdapter(subCategoryAdapter);

        setAdapter();
    }

    private void inititeProgessHud() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

    }

    public void showProgressHud() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressHud() {
        rlProgress.setVisibility(View.GONE);
    }

    private void setAdapter() {
        showProgressHud();
        DatabaseReferences.getSubCategoryRef().orderByChild("Cat_Id").equalTo(catID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SubCategoryModel subCategoryModel = postSnapshot.getValue(SubCategoryModel.class);
                    subCategoryModelList.add(subCategoryModel);
                }
                subCategoryAdapter.setDataList(subCategoryModelList);
                hideProgressHud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressHud();
            }
        });
    }

}

