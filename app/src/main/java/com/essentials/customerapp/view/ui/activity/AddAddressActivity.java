package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.models.DeliverableLocationModel;
import com.essentials.customerapp.viewmodel.AddressModel;
import com.essentials.customerapp.R;
import com.essentials.customerapp.data.model.ResponseModel;
import com.essentials.customerapp.data.repository.AddressRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity {


    private EditText etName, etPhone, etHouseNo, etStreet, etPincode, etLandmark;
    RadioGroup rgNickname;
    MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
    private String action;
    private String addressId;
    private AddressModel addressModel;
    private RadioButton rbHome, rbOffice, rbOthers;
    private ArrayList<CartModel> cartdatalists;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;

    /*jhrngfhfgjkhfjgbhjdgfjdgfhjgdsklfjdsf;hkjdfkjhnjdfghhjdgjdjfkdflkjjghl*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_address);
        rbHome = findViewById(R.id.rbHome);
        rbOffice = findViewById(R.id.rbOffice);
        rlProgress = findViewById(R.id.rlProgress);
        rbOthers = findViewById(R.id.rbOthers);
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Add Address");
        setSupportActionBar(toolbar);
        inititeProgessHud();
        hideKeyboard();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        setReferences();
        getIntentData();
        getDeliverPin();
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

    @Override
    public void onBackPressed() {
        AddAddressActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData() {

        action = getIntent().getStringExtra("action");
        addressId = getIntent().getStringExtra("addressid");
        if (action.equalsIgnoreCase("edit")) {
            fetchAddress(addressId);
        }else {
            hideProgressHud();
        }
    }

    private void fetchAddress(String addressId) {
        showProgressHud();
        AddressRepository.getInstance().getUserAddress(addressId).observe(this,
                new Observer<List<AddressModel>>() {
                    @Override
                    public void onChanged(List<AddressModel> addressModels) {
                        if (addressModels != null) {
                            setDataToView(addressModels.get(0));
                        }
                        hideProgressHud();
                    }
                });
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

    private void setDataToView(AddressModel addressModel) {
        this.addressModel = addressModel;
        if (addressModel.getName() != null) {
            etName.setText(addressModel.getName());
        }
        if (addressModel.getMobileNumber() != null) {
            etPhone.setText(addressModel.getMobileNumber());
        }
        if (addressModel.getPincode() != null) {
            etPincode.setText(addressModel.getPincode());
        }
        if (addressModel.getHouseNo() != null) {
            etHouseNo.setText(addressModel.getHouseNo());
        }
        if (addressModel.getSocietyName() != null) {
            etStreet.setText(addressModel.getSocietyName());
        }
        if (addressModel.getLandmark() != null) {
            etLandmark.setText(addressModel.getLandmark());
        }
        String addNickname = addressModel.getAddressNickName();
        if (addNickname.equalsIgnoreCase("home")) {
            rbHome.setChecked(true);
        } else if (addNickname.equalsIgnoreCase("office")) {
            rbOffice.setChecked(true);
        } else {
            rbOthers.setChecked(true);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void setReferences() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etHouseNo = findViewById(R.id.etHouseNo);
        etStreet = findViewById(R.id.etStreet);
        rgNickname = findViewById(R.id.rgNickname);
        etPincode = findViewById(R.id.etPincode);
        etLandmark = findViewById(R.id.etLandmark);
        RelativeLayout rlAddAddress = findViewById(R.id.rlAddAddress);

        rlAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressValidation();
            }
        });

    }
    final List<DeliverableLocationModel> locationModel = new ArrayList<>();
    private void getDeliverPin(){
        Query locationRef = DatabaseReferences.getDeliverableLocations().orderByChild("Location_Id")/*.orderByChild("Location_Id")*/;
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationModel.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeliverableLocationModel model = dataSnapshot1.getValue(DeliverableLocationModel.class);
                    locationModel.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addressValidation() {
        String name = etName.getText().toString().trim();
        String mobile = etPhone.getText().toString().trim();
        String houseNo = etHouseNo.getText().toString().trim();
        String street = etStreet.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String landmark = etLandmark.getText().toString().trim();
        ArrayList<String> pin = new ArrayList<>();
        for (DeliverableLocationModel model : locationModel) {
            pin.add(model.getPincode());
        }


        if (TextUtils.isEmpty(name)) {
            etName.setError(Constants.ENTER_NAME);
            return;
        }
        if (TextUtils.isEmpty(mobile) || mobile.length() < 10) {
            etPhone.setError(Constants.ENTER_MOBILE);
            return;
        }
        if (TextUtils.isEmpty(pincode) || pincode.length() < 6) {
            etPincode.setError(Constants.ENTER_PINCODE);
            return;
        }
        if (!pin.contains(pincode)) {
            etPincode.setError(Constants.NOT_Deliverable_Pin);
            return;
        }
       /* if (!Arrays.asList(deliverablePin).contains(pincode)) {
            etPincode.setError(Constants.NOT_Deliverable_Pin);
            return;
        }*/

        if (TextUtils.isEmpty(houseNo)) {
            etHouseNo.setError(Constants.VALID_HOUSE);
            return;
        }
        if (TextUtils.isEmpty(street)) {
            etStreet.setError(Constants.VALID_OFFICE);
            return;
        }

        String key;
        String modifiedDate = "";
        String createdDate = "";
        if (action.equalsIgnoreCase("add")) {
            modifiedDate = AppUtils.dateAndTime();
            createdDate = AppUtils.dateAndTime();
            key = DatabaseReferences.getUserAddressRef().child(Constant.USER_ID).push().getKey();
        } else {
            key = addressId;
            createdDate = addressModel.getCreatedDate();
            modifiedDate = AppUtils.dateAndTime();
        }

        RadioButton rbNickname = findViewById(rgNickname.getCheckedRadioButtonId());
        String nickname = rbNickname.getText().toString();


        AddressModel model = new AddressModel(createdDate, modifiedDate, name, mobile, houseNo, street,
                nickname, key, pincode, landmark, true);

        AddressRepository.getInstance().addAddress(model).observe(this, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel != null) {
                    Intent intent = new Intent();
                    AddAddressActivity.this.setResult(RESULT_OK, intent);
                    AddAddressActivity.this.finish();
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    new MySharedPref(AddAddressActivity.this).writeString(PrefConstant.SELECTED_ADD_ID, key);
                }
            }
        });
    }
}
