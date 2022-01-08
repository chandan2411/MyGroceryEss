package com.essentials.customerapp.view.ui.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.essentials.customerapp.data.model.GrandCategoryModel;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.view.ui.fragment.ProductFragments;

import java.util.List;

public class ProductPager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    private int tabCount;
    private List<GrandCategoryModel> grandCategoryModels;
    private List<List<ProductModel>> productList;
    private static final String TAG = "ProductPager";

    //    public ProductPager(@NonNull FragmentManager fm, int tabCount, List<GrandCategoryModel> grandCategoryModels) {
    public ProductPager(@NonNull FragmentManager fm, int tabCount, List<List<ProductModel>> productList, List<GrandCategoryModel> grandCategoryModels) {
//        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        super(fm);
        this.tabCount = tabCount;
        this.productList = productList;
        this.grandCategoryModels= grandCategoryModels;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e(TAG, "" + position);
//        return ProductFragments.newInstance(grandCategoryModels, position);
        return ProductFragments.newInstance(productList.get(position), position);
    }

    @Override
    public int getCount() {
        return tabCount;
    }

   /* @Override
    public int getItemPosition(Object object) {
        ProductFragments fragment = (ProductFragments) object;
        int position = fragment.getPosition();
        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }*/

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return grandCategoryModels.get(position).getGrand_Cat_Name();
    }
}
