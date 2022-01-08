package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.CategoryModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.view.ui.adapter.CategoryAdapter;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView category_rv;
    CategoryAdapter categoryAdapter;
    private String title;
    private String catID;
    private RelativeLayout rlParent;
    private TextView tvTitle;
    private Toolbar toolbar;
    private KProgressHUD progressHUD;
    private LinearLayout llParent;
    private ArrayList<CategoryModel> categoryModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        category_rv = findViewById(R.id.category_bottom_rv);
        llParent = findViewById(R.id.llParent);
        tvTitle = findViewById(R.id.tvTitle);
        toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        inititeProgessHud();
        getIntentData();
        setUpGridRecyclerView();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            title = getIntent().getStringExtra(Constants.CAT_NAME);
            catID = getIntent().getStringExtra(Constants.CAT_ID);
        }
        tvTitle.setText(title);
    }

    private void setUpGridRecyclerView() {
        categoryAdapter = new CategoryAdapter(CategoryActivity.this, "Category");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        category_rv.setLayoutManager(mLayoutManager);
        category_rv.setItemAnimator(new DefaultItemAnimator());
        category_rv.setAdapter(categoryAdapter);
        getCategoryData();
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
        progressHUD.show();
    }

    public void hideProgressHud() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    private void getCategoryData() {
        showProgressHud();
        DatabaseReferences.getCategoryRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryModel categoryModel = postSnapshot.getValue(CategoryModel.class);
                    categoryModelList.add(categoryModel);
                }
                categoryAdapter.setCatDataList(categoryModelList);
                hideProgressHud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressHud();
            }
        });
    }

}
