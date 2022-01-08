package com.essentials.customerapp.interfaces;

import com.essentials.customerapp.firebase.models.ProductModel;

import java.util.List;

public interface ProductDataRecieveListener {
    void onProductDataRecieve(Boolean Status, String message, List<ProductModel> productModelList);
}
