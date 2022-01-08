package com.essentials.customerapp.firebase.interfaces;

import com.essentials.customerapp.firebase.models.SubCategoryModel;

import java.util.List;

/**
 * Created by RAJ ARYAN on 2020-01-22.
 */
public interface SubCategoryRcvListener {

    void onSubCategoryReceive(Boolean status, List<SubCategoryModel> subCategoryModelList, String message);
}
