package com.essentials.customerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddressRepository {

    private static AddressRepository addressRepository;

    private MutableLiveData<List<AddressModel>> addressList = new MutableLiveData<>();
    private MutableLiveData<AddressModel> address = new MutableLiveData<>();
    private MutableLiveData<ResponseModel> responseModel = new MutableLiveData<>();
    private DatabaseReference cartReferences;
    private DatabaseReference userAddressRef;
    private List<AddressModel> addressModelList = new ArrayList<>();


    public AddressRepository() {
        userAddressRef = DatabaseReferences.getUserAddressRef();
    }

    public static AddressRepository getInstance() {
        if (addressRepository == null) {
            addressRepository = new AddressRepository();
        }
        return addressRepository;
    }

    public void removeAddress(String addressId) {
        userAddressRef.child(Constant.USER_ID).child(addressId).removeValue();
    }

    public MutableLiveData<List<AddressModel>> getUserAddress(String addressId) {
        addressModelList.clear();
        addressList.setValue(null);
        Query ref;
        if (addressId.isEmpty()) {
            ref = userAddressRef.child(Constant.USER_ID).orderByChild("modifiedDate");
        } else {
            ref = userAddressRef.child(Constant.USER_ID).orderByChild("addressId").equalTo(addressId);
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    AddressModel model = data.getValue(AddressModel.class);
                    addressModelList.add(model);
                }
                addressList.setValue(addressModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                addressList.setValue(addressModelList);
            }
        });
        return addressList;
    }

    public MutableLiveData<ResponseModel> addAddress(AddressModel addressModel) {
        responseModel.setValue(null);
        userAddressRef.child(Constant.USER_ID).child(addressModel.getAddressId()).setValue(addressModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                responseModel.setValue(new ResponseModel(true, ""));
            }
        });
        return responseModel;
    }

    public MutableLiveData<AddressModel> getSelectedAddress(String addressID) {
        address.setValue(null);
        Query ref = userAddressRef.child(Constant.USER_ID).orderByChild("selectedAddress").equalTo(true);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    AddressModel model = data.getValue(AddressModel.class);
                    address.setValue(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                addressList.setValue(null);
            }
        });
        return address;
    }

}
