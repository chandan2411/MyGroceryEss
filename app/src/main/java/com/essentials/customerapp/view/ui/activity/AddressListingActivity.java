package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.AddressRepository;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.view.ui.adapter.AddressAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import static com.essentials.customerapp.utilities.Constant.SUB_TOTAL;
import static com.essentials.customerapp.utilities.Constant.TOTAL_DELIVERY_CHARGES;
import static com.essentials.customerapp.utilities.Constant.TOTAL_ESSENTIALS_DISCOUNT;
import static com.essentials.customerapp.utilities.Constant.TOTAL_MRP;
import static com.essentials.customerapp.utilities.Constant.TOTAL_PRODUCT_DISCOUNT;

public class AddressListingActivity extends AppCompatActivity {

    CardView cvAddNewAddress;
    RecyclerView recyclerAddress;
    private AddressAdapter addressAdapter;
    private TextView tvNoAddress;
    private double totalMrp, totalProductDis, totalEssentialsDis, totalDeliveryCharges, subTotal;
    private String comeFrom;
    private List<AddressModel> addressList = new ArrayList<>();
    private List<AddressModel> addressList1 = new ArrayList<>();
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_listing);
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        inititeProgessHud();
        getIntentData();
        getRef();
        fetchAddressData();

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
        rlProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressHud() {
        /*if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }*/
        rlProgress.setVisibility(View.GONE);
    }

    private static Dialog dialog;

    public static void showDialog(final Activity activity, String msg) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(msg);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void getIntentData() {
        comeFrom = getIntent().getStringExtra("comefrom");
        totalMrp = getIntent().getDoubleExtra(TOTAL_MRP, 0.0);
        totalProductDis = getIntent().getDoubleExtra(TOTAL_PRODUCT_DISCOUNT, 0.0);
        totalEssentialsDis = getIntent().getDoubleExtra(TOTAL_ESSENTIALS_DISCOUNT, 0.0);
        totalDeliveryCharges = getIntent().getDoubleExtra(TOTAL_DELIVERY_CHARGES, 0.0);
        subTotal = getIntent().getDoubleExtra(SUB_TOTAL, 0.0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    public void fetchAddressData() {
        showProgressHud();
        AddressRepository.getInstance().getUserAddress("")
                .observe(this, addressModels -> {
                    if (addressModels != null) {
                        if (addressModels.isEmpty()) {
                            recyclerAddress.setVisibility(View.INVISIBLE);
                            tvNoAddress.setVisibility(View.VISIBLE);
                        } else {
                            /*for (int i = addressModels.size() - 1; i >= 0; i--) {
                                addressList1.add(addressModels.get(i));
                            }*/
                            addressList = addressModels;
                            addressAdapter.setDataList(addressModels);
                            recyclerAddress.setVisibility(View.VISIBLE);
                            tvNoAddress.setVisibility(View.INVISIBLE);
                        }
                    }
                    hideProgressHud();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRef() {
        tvNoAddress = findViewById(R.id.tvNoAddress);
        cvAddNewAddress = findViewById(R.id.cvAddNewAddress);
        recyclerAddress = findViewById(R.id.recyclerAddress);
        rlProgress = findViewById(R.id.rlProgress);
        addressAdapter = new AddressAdapter(AddressListingActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerAddress.setLayoutManager(mLayoutManager);
        recyclerAddress.setItemAnimator(new DefaultItemAnimator());
        recyclerAddress.setAdapter(addressAdapter);

        cvAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressList.size() > 5) {
                    Toast.makeText(AddressListingActivity.this, "You have added enough address", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(AddressListingActivity.this, AddAddressActivity.class);
                intent.putExtra("action", "add");
                startActivityForResult(intent, Constant.ADD_ADDRESS_INTENT);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ADD_ADDRESS_INTENT) {
            fetchAddressData();
        }
    }

    public void moveToNextActivity() {
        if (comeFrom.equalsIgnoreCase("cart")) {
            Intent intent = new Intent(this, AddressAndDateTimeActivity.class);
            intent.putExtra(TOTAL_MRP, totalMrp);
            intent.putExtra(TOTAL_PRODUCT_DISCOUNT, totalProductDis);
            intent.putExtra(TOTAL_ESSENTIALS_DISCOUNT, totalEssentialsDis);
            intent.putExtra(TOTAL_DELIVERY_CHARGES, totalDeliveryCharges);
            intent.putExtra(SUB_TOTAL, subTotal);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        } else if (comeFrom.equalsIgnoreCase("slot")) {
            Intent intent = new Intent();
            AddressListingActivity.this.setResult(Constant.ADD_ADDRESS_INTENT, intent);
            onBackPressed();
        }
    }


}
