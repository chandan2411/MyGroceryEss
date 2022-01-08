package com.essentials.customerapp.firebase;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;

public class UploadDataToFirebaseDbClass {

    DatabaseReference productRef;
    DatabaseReference categoryRef;
    DatabaseReference subCategoryRef;

    public UploadDataToFirebaseDbClass(Context context) {
        productRef = DatabaseReferences.getProductRef();
        categoryRef = DatabaseReferences.getCategoryRef();
        subCategoryRef = DatabaseReferences.getSubCategoryRef();
    }

    public void addProduct(String categoryID, String subCategoryID) {
    }

    public void addCategory() {
    }

    public void addSubCategory() {
    }

}
