package com.essentials.customerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.view.ui.activity.BaseActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartRepository {
    private static CartRepository cartRepository;
    private MutableLiveData<List<CartModel>> cartModelData = new MutableLiveData<>();
    private List<CartModel> cartModelList = new ArrayList<>();
    private MutableLiveData<ResponseModel> responseModel = new MutableLiveData<>();

    private DatabaseReference cartReferences;


    public static CartRepository getInstance() {
        if (cartRepository == null) {
            cartRepository = new CartRepository();
        }
        return cartRepository;
    }


    public MutableLiveData<List<CartModel>> getUserCartData() {
        cartModelData.setValue(null);
        DatabaseReferences.getUserCartItem().child(Constant.USER_ID).orderByChild("Item_Added_Date")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cartModelList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            CartModel cartModel = postSnapshot.getValue(CartModel.class);
                            cartModelList.add(cartModel);
                            Collections.reverse(cartModelList);
                        }
                        cartModelData.setValue(cartModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        cartModelData.setValue(null);
                    }
                });
        return cartModelData;
    }

    public MutableLiveData<ResponseModel> addProductToCart(CartModel cartModel) {
        responseModel.setValue(null);
        DatabaseReferences.getUserCartItem().child(Constant.USER_ID).child(cartModel.getProduct_Id())
                .setValue(cartModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                responseModel.setValue(new ResponseModel(true, ""));

            }
        });
        return responseModel;
    }


    public MutableLiveData<ResponseModel> deleteProduct(String product_id) {
        responseModel.setValue(null);

        DatabaseReferences.getUserCartItem().child(Constant.USER_ID).child(product_id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                responseModel.setValue(new ResponseModel(true, ""));
            }
        });
        return responseModel;
    }

    public void clearCart() {
        DatabaseReferences.getUserCartItem().child(Constant.USER_ID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            }
        });
    }
}
