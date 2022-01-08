package com.essentials.customerapp.firebase.interfaces;

import com.essentials.customerapp.firebase.models.ProductModel;

import java.util.List;

/**
 * Created by RAJ ARYAN on 2020-01-22.
 */
public interface ProductRcvListener {

    void onProductReceive(Boolean status, List<ProductModel> productModelList, String message);
}
