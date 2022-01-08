package com.essentials.customerapp.firebase.interfaces;

import com.essentials.customerapp.firebase.models.CategoryModel;

import java.util.List;

/**
 * Created by RAJ ARYAN on 2020-01-22.
 */
public interface CategoryRcvListener {
    void onCategoryReceive(Boolean status, List<CategoryModel> categoryModelList, String message);
}
