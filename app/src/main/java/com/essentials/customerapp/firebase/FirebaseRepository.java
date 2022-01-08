package com.essentials.customerapp.firebase;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.essentials.customerapp.firebase.interfaces.CategoryRcvListener;
import com.essentials.customerapp.firebase.interfaces.ProductRcvListener;
import com.essentials.customerapp.firebase.interfaces.SubCategoryRcvListener;
import com.essentials.customerapp.firebase.models.CategoryModel;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.firebase.models.SubCategoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAJ ARYAN on 2020-01-22.
 */
public class FirebaseRepository {

    private Context context;
    private KProgressHUD progressHUD;
    private ProductRcvListener productRcvListener;
    private SubCategoryRcvListener subCategoryRcvListener;
    private CategoryRcvListener categoryRcvListener;

    public FirebaseRepository(Context context, Fragment fragment) {

        this.context = context;
        if (context==null){
            productRcvListener = (ProductRcvListener) fragment;
            subCategoryRcvListener = (SubCategoryRcvListener)fragment;
            categoryRcvListener = (CategoryRcvListener)fragment;
    }else {
            productRcvListener = (ProductRcvListener) context;
            subCategoryRcvListener = (SubCategoryRcvListener)context;
            categoryRcvListener = (CategoryRcvListener)context;
        }
        inititeProgessHud();
    }

    public FirebaseRepository(Activity activity) {
        subCategoryRcvListener = (SubCategoryRcvListener) activity;
        inititeProgessHud();
    }

    public void getProductList(String sortParams, String categoryID, String searchText){
        showProgressHud();
        Query productRef=null;
        if (!searchText.isEmpty()){
            productRef = DatabaseReferences.getProductRef().startAt("#"+searchText);
        }else {
            productRef = DatabaseReferences.getProductRef()/*.orderByChild(sortParams)*/.orderByChild("productSubCategoryID")
                    .equalTo(categoryID);
        }
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
                productRcvListener.onProductReceive(true, productModelList, "");
                hideProgressHud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                productRcvListener.onProductReceive(true, null, databaseError.getMessage());
                hideProgressHud();
            }
        });
    }

    public void getCategoryList(){
        showProgressHud();
        DatabaseReferences.getCategoryRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CategoryModel> categoryModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryModel categoryModel = postSnapshot.getValue(CategoryModel.class);
                    categoryModelList.add(categoryModel);
                }
                categoryRcvListener.onCategoryReceive(true, categoryModelList, "");
                hideProgressHud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                categoryRcvListener.onCategoryReceive(true, null, databaseError.getMessage());
                hideProgressHud();
            }
        });
    }

    public void getSubCategoryList( String categoryID){
        showProgressHud();
        DatabaseReferences.getSubCategoryRef().equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SubCategoryModel> subCategoryModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SubCategoryModel subCategoryModel = postSnapshot.getValue(SubCategoryModel.class);
                    subCategoryModelList.add(subCategoryModel);
                }
                subCategoryRcvListener.onSubCategoryReceive(true, subCategoryModelList, "");
                hideProgressHud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subCategoryRcvListener.onSubCategoryReceive(true, null, databaseError.getMessage());
                hideProgressHud();
            }
        });
    }

    private void inititeProgessHud() {
        progressHUD = KProgressHUD.create(context)
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
}
