package com.essentials.customerapp.interfaces;

import com.essentials.customerapp.firebase.models.CategoryModel;

import java.util.List;

public interface CategoryDataReceiveListener {
    void onCategoryDataReceive(boolean Status, String message, List<CategoryModel> categoryModelList);
}
