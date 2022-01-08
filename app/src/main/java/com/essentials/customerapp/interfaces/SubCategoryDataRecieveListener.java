package com.essentials.customerapp.interfaces;
import com.essentials.customerapp.firebase.models.SubCategoryModel;

import java.util.List;

public interface SubCategoryDataRecieveListener {
    void onSubCategoryDataReceive(boolean Status, String message, List<SubCategoryModel> subCategoryModelList);
}
