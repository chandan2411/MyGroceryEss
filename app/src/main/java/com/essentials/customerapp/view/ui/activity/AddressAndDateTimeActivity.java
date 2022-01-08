package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.AddressRepository;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.razorpay.Checkout;

import java.util.List;

import static com.essentials.customerapp.utilities.Constant.DELIVERY_DATE;
import static com.essentials.customerapp.utilities.Constant.SUB_TOTAL;
import static com.essentials.customerapp.utilities.Constant.TIME_SLOT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_DELIVERY_CHARGES;
import static com.essentials.customerapp.utilities.Constant.TOTAL_ESSENTIALS_DISCOUNT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_MRP;
import static com.essentials.customerapp.utilities.Constant.TOTAL_PRODUCT_DISCOUNT;

public class AddressAndDateTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvAddressType;
    private ImageView ivImageType;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvPincode;
    private TextView tvHouseOrOfficeNo;
    private TextView tvStreetName;
    private TextView tvLandmark;
    private LinearLayout llLandmark;
    private RelativeLayout rlProceedPayment;
    private NestedScrollView scrollView;
    RelativeLayout rlSlot1, rlSlot2, rlSlot3;
    RadioButton rbSlot1, rbSlot2, rbSlot3;
    TextView tvSlot1, tvSlot2, tvSlot3;
    private String selectTimeSlot = "";
    private AddressModel selectedAddress;
    private TextView tvChangeAddress;
    private double totalMrp, totalProductDis, totalEssentialsDis, totalDeliveryCharges, subTotal;
    private String deliveryDate;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;
    double deliveryTimeSlot=19;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_and_date_time);
        getOffers2Data();
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        inititeProgessHud();
        findRef();
        getIntentData();
        fetchAddressData();
    }
    private void getOffers2Data() {

        //  Query productRef = DatabaseReferences.getOffer2Ref().orderByChild("Offers2");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Offers2/" + "-MFxyFIvLTr9IxOFdFqB");
        //  Query productRef = DatabaseReferences.getOffer2Ref().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //get id and phone_number

                deliveryTimeSlot = Double.parseDouble(String.valueOf(dataSnapshot.child("DeliveryTimeSlot").getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inititeProgessHud() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void showProgressHud() {
        /*if (progressHUD != null)
            progressHUD.show();*/
        rlProceedPayment.setVisibility(View.VISIBLE);
    }

    public void hideProgressHud() {
        /*if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
            scrollView.setVisibility(View.VISIBLE);
            rlProceedPayment.setVisibility(View.VISIBLE);
        }*/
        rlProgress.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        rlProceedPayment.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private void getIntentData() {
        totalMrp = getIntent().getDoubleExtra(TOTAL_MRP, 0.0);
        totalProductDis = getIntent().getDoubleExtra(TOTAL_PRODUCT_DISCOUNT, 0.0);
        totalEssentialsDis = getIntent().getDoubleExtra(TOTAL_ESSENTIALS_DISCOUNT, 0.0);
        totalDeliveryCharges = getIntent().getDoubleExtra(TOTAL_DELIVERY_CHARGES, 0.0);
        subTotal = getIntent().getDoubleExtra(SUB_TOTAL, 0.0);
    }

    public void fetchAddressData() {

        String addressId = new MySharedPref(AddressAndDateTimeActivity.this).readString(PrefConstant.SELECTED_ADD_ID, "");
        if (addressId.isEmpty())
            return;
        showProgressHud();
        AddressRepository.getInstance().getUserAddress(addressId).observe(this, new Observer<List<AddressModel>>() {
            @Override
            public void onChanged(List<AddressModel> addressModels) {
                if (addressModels != null) {
                    for (AddressModel model : addressModels) {
                        if (model.isSelectedAddress()) {
                            setDataToView(model);
                        }
                    }
                }
            }
        });
    }

    private void setDataToView(AddressModel model) {
        selectedAddress = model;
        String addressNickName = model.getAddressNickName();
        tvAddressType.setAllCaps(true);
        tvAddressType.setText(addressNickName);
        if (addressNickName.equalsIgnoreCase("home")) {
            ivImageType.setImageResource(R.drawable.home);
        } else if (addressNickName.equalsIgnoreCase("office")) {
            ivImageType.setImageResource(R.drawable.office);
        } else {
            ivImageType.setImageResource(R.drawable.office);
        }
        tvName.setText(model.getName());
        tvMobile.setText(model.getMobileNumber());
        tvPincode.setText(model.getPincode());
        tvHouseOrOfficeNo.setText(model.getHouseNo());
        tvStreetName.setText(model.getSocietyName());
        tvStreetName.setText(model.getSocietyName());
        if (model.getLandmark().isEmpty())
            llLandmark.setVisibility(View.GONE);
        tvLandmark.setText(model.getLandmark());

        String currentTimeNormal = AppUtils.getCurrentTime();
        String[] time = currentTimeNormal.split(":");
        int time1 = Integer.valueOf(time[0]);
        int time2 = Integer.valueOf(time[1]);
       // int lateTime1 = (int) 0;
        int lateTime1 = (int) deliveryTimeSlot;
        int lateTime2 = 24;
        boolean isLate = false;
        if (time1 == 24 && time2 > 0) {
            isLate = true;
        } else {
            if (time1 >= lateTime1 && time1 <= lateTime2) {
                isLate = true;
            } else {
                isLate = false;
            }
        }

        String currentTimeSlot = AppUtils.dateAndTimeFormatted() + " (8AM - 9PM)";
        String currentTimeSlot1 = AppUtils.getDateAfterCurrentDate(2, false) + " (8AM - 9PM)";
        String currentTimeSlot2 = AppUtils.getDateAfterCurrentDate(3, false) + " (8AM - 9PM)";
        String currentTimeSlot3 = AppUtils.getDateAfterCurrentDate(4, false) + " (8AM - 9PM)";
        if (!isLate) {
            tvSlot1.setText(currentTimeSlot);
            tvSlot2.setText(currentTimeSlot1);
            tvSlot3.setText(currentTimeSlot2);
        } else {
            tvSlot1.setText(currentTimeSlot1);
            tvSlot2.setText(currentTimeSlot2);
            tvSlot3.setText(currentTimeSlot3);
        }
        hideProgressHud();
    }


    private void findRef() {
        tvAddressType = findViewById(R.id.tvAddressType);
        ivImageType = findViewById(R.id.ivImageType);
        tvName = findViewById(R.id.tvName);
        tvMobile = findViewById(R.id.tvMobile);
        tvPincode = findViewById(R.id.tvPincode);
        tvHouseOrOfficeNo = findViewById(R.id.tvHouseOrOfficeNo);
        tvStreetName = findViewById(R.id.tvStreetName);
        tvLandmark = findViewById(R.id.tvLandmark);
        llLandmark = findViewById(R.id.llLandmark);
        rlProgress = findViewById(R.id.rlProgress);
        tvChangeAddress = findViewById(R.id.tvChangeAddress);
        rbSlot1 = findViewById(R.id.rbSlot1);
        rbSlot2 = findViewById(R.id.rbSlot2);
        rbSlot3 = findViewById(R.id.rbSlot3);
        tvSlot1 = findViewById(R.id.tvSlot1);
        tvSlot2 = findViewById(R.id.tvSlot2);
        tvSlot3 = findViewById(R.id.tvSlot3);
        rlSlot1 = findViewById(R.id.rlSlot1);
        rlSlot2 = findViewById(R.id.rlSlot2);
        rlSlot3 = findViewById(R.id.rlSlot3);
        scrollView = findViewById(R.id.scrollView);
        rlProceedPayment = findViewById(R.id.rlProceedPayment);

        rlSlot1.setOnClickListener(this);
        rlSlot2.setOnClickListener(this);
        rlSlot3.setOnClickListener(this);

        rbSlot1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                buttonClicked(1, b);
            }
        });
        rbSlot2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                buttonClicked(2, b);
            }
        });
        rbSlot3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                buttonClicked(3, b);
            }
        });

        rlProceedPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectTimeSlot.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), NewCheckoutActivity.class);
                    intent.putExtra("address", selectedAddress);
                    intent.putExtra(TOTAL_MRP, totalMrp);
                    intent.putExtra(TIME_SLOT, selectTimeSlot);
                    intent.putExtra(DELIVERY_DATE, deliveryDate);
                    intent.putExtra(TOTAL_PRODUCT_DISCOUNT, totalProductDis);
                    intent.putExtra(TOTAL_ESSENTIALS_DISCOUNT, totalEssentialsDis);
                    intent.putExtra(TOTAL_DELIVERY_CHARGES, totalDeliveryCharges);
                    intent.putExtra(SUB_TOTAL, subTotal);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else {
                    Toast.makeText(AddressAndDateTimeActivity.this, "Please Select Time Slot ", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddressListingActivity.class);
                intent.putExtra("comefrom", "slot");
                startActivityForResult(intent, Constant.ADD_ADDRESS_INTENT);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ADD_ADDRESS_INTENT) {
            fetchAddressData();
        }
    }

    @Override
    public void onClick(View view) {
        int ID = view.getId();
        switch (ID) {
            case R.id.rlSlot1:
                buttonClicked(1, true);
                break;
            case R.id.rlSlot2:
                buttonClicked(2, true);
                break;
            case R.id.rlSlot3:
                buttonClicked(3, true);
                break;
        }
    }

    private void buttonClicked(int i, boolean isChecked) {
        if (!isChecked)
            return;

        if (i == 1) {
            rbSlot1.setChecked(true);
            rbSlot2.setChecked(false);
            rbSlot3.setChecked(false);
            selectTimeSlot = tvSlot1.getText().toString();
            deliveryDate = AppUtils.dateAndTime();
        } else if (i == 2) {
            rbSlot1.setChecked(false);
            rbSlot2.setChecked(true);
            rbSlot3.setChecked(false);
            selectTimeSlot = tvSlot2.getText().toString();
            deliveryDate = AppUtils.getDateAfterCurrentDate(1, true);

        } else {
            rbSlot1.setChecked(false);
            rbSlot2.setChecked(false);
            rbSlot3.setChecked(true);
            selectTimeSlot = tvSlot3.getText().toString();
            deliveryDate = AppUtils.getDateAfterCurrentDate(2, true);
        }
    }


}
