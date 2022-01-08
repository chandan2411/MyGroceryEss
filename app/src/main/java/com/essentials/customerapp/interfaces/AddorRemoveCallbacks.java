package com.essentials.customerapp.interfaces;


import com.essentials.customerapp.models.CartModel;

import java.util.List;

public interface AddorRemoveCallbacks {

    void onAddProduct(int productCount);

    void onRemoveProduct(int productCount);

    void updateTotalPrice(List<CartModel> cartModelList);
}