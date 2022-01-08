package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.view.ui.activity.AddressListingActivity;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.AddressRepository;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.AddAddressActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressLayout> {
    Context mContext;
    private List<AddressModel> addressModels = new ArrayList<>();

    public AddressAdapter(AddressListingActivity addressListingActivity) {
        mContext = addressListingActivity;
    }

    @NonNull
    @Override
    public AddressLayout onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_layout, parent, false);
        return new AddressLayout(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressLayout holder, int position) {
        AddressModel addressModel = addressModels.get(position);
        String name = addressModel.getName();
        String mobile = addressModel.getMobileNumber();
        String pincode = addressModel.getPincode();
        String houseNo = addressModel.getHouseNo();
        String streetName = addressModel.getSocietyName();
        String landmark = addressModel.getLandmark();
        String addressNickName = addressModel.getAddressNickName();
        String addressId = addressModel.getAddressId();


        if (name != null && !name.isEmpty()) {
            holder.tvName.setText(name);
        }
        if (mobile != null && !mobile.isEmpty()) {
            holder.tvMobile.setText(mobile);
        }
        if (pincode != null && !pincode.isEmpty()) {
            holder.tvPincode.setText(pincode);
        }
        if (houseNo != null && !houseNo.isEmpty()) {
            holder.tvHouseOrOfficeNo.setText(houseNo);
        }
        if (streetName != null && !streetName.isEmpty()) {
            holder.tvStreetName.setText(streetName);
        }

        if (landmark != null && !landmark.isEmpty()) {
            holder.llLandmark.setVisibility(View.VISIBLE);
            holder.tvLandmark.setText(landmark);
        } else {
            holder.llLandmark.setVisibility(View.GONE);
        }

        if (addressNickName != null && !addressNickName.isEmpty()) {
            holder.tvAddressType.setText(addressNickName);
            if (addressNickName.equalsIgnoreCase("home")) {
                holder.ivImageType.setImageResource(R.drawable.home);
            } else if (addressNickName.equalsIgnoreCase("office")) {
                holder.ivImageType.setImageResource(R.drawable.office);
            } else {
                holder.ivImageType.setImageResource(R.drawable.office);
            }
        }

        /*if (addressModel.getAddressId().equalsIgnoreCase(new MySharedPref(mContext).readString(PrefConstant.SELECTED_ADD_ID, ""))) {
            holder.rbSelectedAdd.setChecked(true);
        } else {
            holder.rbSelectedAdd.setChecked(false);
        }*/

        holder.llAddressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MySharedPref(mContext).writeString(PrefConstant.SELECTED_ADD_ID, addressModel.getAddressId());
                ((AddressListingActivity) mContext).moveToNextActivity();
            }
        });


        holder.cvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateAddress(addressModel, 0);
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("addressid", addressModel.getAddressId());
                ((AppCompatActivity) mContext).startActivityForResult(intent, Constant.ADD_ADDRESS_INTENT);
                ((AddressListingActivity) mContext).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        holder.rbSelectedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MySharedPref(mContext).writeString(PrefConstant.SELECTED_ADD_ID, addressModel.getAddressId());
                ((AddressListingActivity) mContext).moveToNextActivity();
            }
        });

        holder.cvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressModels.remove(addressModel);
                AddressRepository.getInstance().removeAddress(addressModel.getAddressId());
                notifyItemRemoved(position);
                if (addressModels.isEmpty()) {
                    ((AddressListingActivity) mContext).fetchAddressData();
                }
            }
        });
    }

    private void updateAddress(AddressModel addressModel, int type) {
        AddressRepository.getInstance().addAddress(addressModel).observe((AddressListingActivity) mContext, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel != null) {
                    if (type == 1) {
                        ((AddressListingActivity) mContext).finish();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModels != null && !addressModels.isEmpty() ? addressModels.size() : 0;
    }

    public void setDataList(List<AddressModel> addressModels) {
        Collections.reverse(addressModels);
        this.addressModels = addressModels;
        /*  this.addressModels.addAll(addressModels);*/
        notifyDataSetChanged();
    }

    public class AddressLayout extends RecyclerView.ViewHolder {
        LinearLayout llAddressView, llLandmark;
        TextView tvAddressType, tvName, tvMobile, tvHouseOrOfficeNo, tvStreetName, tvPincode, tvLandmark;
        RadioButton rbSelectedAdd;
        ImageView ivImageType;
        CardView cvRemove, cvEdit;

        public AddressLayout(@NonNull View itemView) {
            super(itemView);
            llAddressView = itemView.findViewById(R.id.llAddressView);
            tvAddressType = itemView.findViewById(R.id.tvAddressType);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvHouseOrOfficeNo = itemView.findViewById(R.id.tvHouseOrOfficeNo);
            tvStreetName = itemView.findViewById(R.id.tvStreetName);
            rbSelectedAdd = itemView.findViewById(R.id.rbSelectedAdd);
            tvPincode = itemView.findViewById(R.id.tvPincode);
            llLandmark = itemView.findViewById(R.id.llLandmark);
            tvLandmark = itemView.findViewById(R.id.tvLandmark);
            cvEdit = itemView.findViewById(R.id.cvEdit);
            cvRemove = itemView.findViewById(R.id.cvRemove);
            ivImageType = itemView.findViewById(R.id.ivImageType);
        }
    }
}
