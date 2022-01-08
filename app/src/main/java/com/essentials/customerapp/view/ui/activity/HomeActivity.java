package com.essentials.customerapp.view.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.essentials.customerapp.ProductActivity;
import com.essentials.customerapp.data.repository.WalletRepository;
import com.essentials.customerapp.fcm.FCMConstant;
import com.essentials.customerapp.fcm.LocalNotificationTrigger;
import com.essentials.customerapp.models.FCMTokenModel;
import com.essentials.customerapp.models.Offer2;
import com.essentials.customerapp.models.OffersModel;
import com.essentials.customerapp.utilities.AppUpdateChecker;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.ProminentDisclosure;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.helper.Converter;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.DeliverableLocationModel;
import com.essentials.customerapp.models.WalletModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.utilities.LocationUtils;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PermissionClass;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.fragment.HomeFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends BaseActivity implements /*NavigationView.OnNavigationItemSelectedListener,*/ HomeFragment.CartDataListener, LocationUtils.AddressListener, View.OnClickListener {
    public int cart_count = 0;
    private ImageView ivUserImage;
    MySharedPref sharedPref;
    private DrawerLayout drawer;
    private AutoCompleteTextView etSearch;
    private ArrayAdapter adapter;
    private Toolbar toolbar;
    private String addressOutput="All Location  In Ranchi", currentAddress;
    /*private CardView cardViewCategory;*/
    private TextView tvWalletAmount, tvCurrentAddress, tvAddressToolbar, tvUserName;
    RelativeLayout rlCurrentAddress, rlAddress, rlMYOrder, rlMyCart, rlMyWallet, rlOrderByPhone, rlNeedHelp,
            rlShare, rlAboutUs, rlRateUs, rlLogin, rlLogout;
    private TextView tvDeliverableCities;
    private TextView tvCurrentAddressNotDel;
    private TextView tvChangeLocation;
    private RelativeLayout rlNotDeliverable;
    private FrameLayout content_frame;
    private GoogleSignInClient mGoogleSignInClient;
    private WalletRepository walletRepository;
    private MutableLiveData<List<WalletModel>> walletDataList;
    private RelativeLayout rlParent;
    private HomeFragment fragment;
    String WalletDiscount="", MRPForDiscount="", Offerimageurl="";


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUpdateChecker appUpdateChecker=new AppUpdateChecker(this);  //pass the activity in constructure
        appUpdateChecker.checkForUpdate(false); //mannual check false here
       /* ProminentDisclosure prominentDisclosure = new ProminentDisclosure();
         prominentDisclosure.show(getSupportFragmentManager(),"Location");*/
        sharedPref = new MySharedPref(this);
        setContentView(R.layout.activity_home);
        walletRepository = WalletRepository.getInstance();
        getViewsById();
        initialiseGoogle();
        setSupportActionBar(toolbar);
      //  addressOutput = sharedPref.readString(PrefConstant.LOCATION_SELECTED, "");
        cart_count = getCartCount();
        String userId = sharedPref.readString(PrefConstant.USER_ID, "");
        /*Check for login*/
        if (!userId.isEmpty()) {
            Constant.USER_ID = userId;
            Constant.IS_USER_LOGIN = true;
            getWalletAmount();
        }
        getPopularData();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if (!FCMConstant.FCM_TOKEN.isEmpty())
            sharedPref.writeString(PrefConstant.FCM_TOKEN, FCMConstant.FCM_TOKEN);
//        pushTokenToFbTable();
        subsScribeTopic();

    }



    private void subsScribeTopic() {
        new LocalNotificationTrigger(this).subscribeTopics();
    }

    private void pushTokenToFbTable() {
        String fcmToken = sharedPref.readString(PrefConstant.FCM_TOKEN,"");
        String userId = sharedPref.readString(PrefConstant.USER_ID,"");
        if (!fcmToken.isEmpty() && !userId.isEmpty()) {
            String key = DatabaseReferences.getUserFCMTokenRef().child(Constant.USER_ID).push().getKey();
            FCMTokenModel model = new FCMTokenModel();
            model.setUserId(Constant.USER_ID);
            model.setUserName(sharedPref.readString(PrefConstant.USER_NAME, ""));
            model.setCreateTime(AppUtils.dateAndTime());
            model.setFcmToken(FCMConstant.FCM_TOKEN);
            model.setKey(key);
            DatabaseReferences.getUserFCMTokenRef().child(Constant.USER_ID).setValue(model);
        }
    }


    private void initialiseGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void getWalletAmount() {
        walletDataList = walletRepository.getWalletData(
                sharedPref.readString(PrefConstant.USER_ID, ""));
        walletDataList.observe(this, new Observer<List<WalletModel>>() {
            @Override
            public void onChanged(List<WalletModel> walletModels) {
                if (walletModels != null && !walletModels.isEmpty()) {
                    Constant.USER_WALLET_AMOUNT = walletModels.get(0).getWalletTotalBalance();
                    Constant.USER_WALLET_ID = walletModels.get(0).getWalletId();
                    String roundedWalletAmount = String.format(Locale.ENGLISH, "%.2f", walletModels.get(0).getWalletTotalBalance());
                    tvWalletAmount.setText(roundedWalletAmount);
                }
            }
        });
    }


    /*Fetch the cities or area where fresh choice is delivering*/
    private void getDeliverableCities(final String address) {
        final List<DeliverableLocationModel> locationModel = new ArrayList<>();
        Query locationRef = DatabaseReferences.getDeliverableLocations().orderByChild("Location_Id")/*.orderByChild("Location_Id")*/;
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeliverableLocationModel model = dataSnapshot1.getValue(DeliverableLocationModel.class);
                    locationModel.add(model);
                }
//                showUIForNotDeliver(locationModel, address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                showUIForNotDeliver(null, address);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showUIForNotDeliver(List<DeliverableLocationModel> deliverableLocation, String address) {
        StringBuilder deliverableCitiesName = new StringBuilder();
        List<String> deliverablePin = new ArrayList<>();
        DeliverableLocationModel model;
        for (int i = 0; i < deliverableLocation.size(); i++) {
            model = deliverableLocation.get(i);
            deliverablePin.add(model.getPincode());
            if (i == deliverableLocation.size() - 2) {
                deliverableCitiesName.append(model.getAreaName() + "(" + model.getState() + ") and ");
            } else if (i == deliverableLocation.size() - 1) {
                deliverableCitiesName.append(model.getAreaName() + "(" + model.getState() + ")");
            } else {
                deliverableCitiesName.append(model.getAreaName() + "(" + model.getState() + "), ");
            }
        }
        for (String pincode : deliverablePin) {
            if (!address.contains("" + pincode)) {
                content_frame.setVisibility(View.GONE);
                rlNotDeliverable.setVisibility(View.VISIBLE);
                tvCurrentAddressNotDel.setText("Sorry, we don't deliver in " + address);
                tvDeliverableCities.setText("We only deliver in select location in " + deliverableCitiesName);
            } else {
                drawer.closeDrawer(GravityCompat.START);
                content_frame.setVisibility(View.VISIBLE);
                rlNotDeliverable.setVisibility(View.GONE);
                break;
            }
        }

    }

    private void getViewsById() {
        toolbar = findViewById(R.id.toolbar);
        rlParent = findViewById(R.id.content_main);
//        cardViewCategory = findViewById(R.id.cardViewCategory);
        drawer = findViewById(R.id.drawer_layout);
        etSearch = findViewById(R.id.etSearch);
        tvAddressToolbar = findViewById(R.id.tvAddressToolbar);
        LinearLayout llAddressToolbar = findViewById(R.id.llAddressToolbar);
        rlCurrentAddress = findViewById(R.id.rlCurrentAddress);
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        rlAddress = findViewById(R.id.rlAddress);
        rlMYOrder = findViewById(R.id.rlMYOrder);
        rlMyCart = findViewById(R.id.rlMyCart);
        rlMyWallet = findViewById(R.id.rlMyWallet);
        tvWalletAmount = findViewById(R.id.tvWalletAmount);
        rlOrderByPhone = findViewById(R.id.rlOrderByPhone);
        rlNeedHelp = findViewById(R.id.rlNeedHelp);
        rlShare = findViewById(R.id.rlShare);
        rlAboutUs = findViewById(R.id.rlAboutUs);
        rlRateUs = findViewById(R.id.rlRateUs);
        rlLogin = findViewById(R.id.rlLogin);
        rlLogout = findViewById(R.id.rlLogout);
        ivUserImage = findViewById(R.id.ivUserImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvDeliverableCities = findViewById(R.id.tvDeliverableCities);
        tvCurrentAddressNotDel = findViewById(R.id.tvNoDel);
        tvChangeLocation = findViewById(R.id.tvChangeLocation);
        rlNotDeliverable = findViewById(R.id.rlNotDeliverable);
        content_frame = findViewById(R.id.content_frame);
        setUserName();
        llAddressToolbar.setOnClickListener(this);
        rlCurrentAddress.setOnClickListener(this);
        rlAddress.setOnClickListener(this);
        rlMYOrder.setOnClickListener(this);
        rlMyCart.setOnClickListener(this);
        rlMyWallet.setOnClickListener(this);
        rlOrderByPhone.setOnClickListener(this);
        rlNeedHelp.setOnClickListener(this);
        rlShare.setOnClickListener(this);
        rlAboutUs.setOnClickListener(this);
        rlLogin.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        rlAboutUs.setOnClickListener(this);
        rlRateUs.setOnClickListener(this);
        tvChangeLocation.setOnClickListener(this);
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(this);
        /*cardViewCategory.setOnClickListener(this);*/


        fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }

    private void setProfileImage() {
        String profileImage = sharedPref.readString(PrefConstant.USER_IMAGE_URL, "");
        if (!profileImage.isEmpty()) {
            Picasso.get().load(profileImage).into(ivUserImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    ivUserImage.setImageResource(R.drawable.ic_person_50);
                    Log.d("Error : ", e.getMessage());
                }
            });
        } else {
            ivUserImage.setImageResource(R.drawable.ic_person_50);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /*public void toggleCommunicationGroup(View button) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem group = navigationView.getMenu().findItem(R.id.nav_communication_group);
        boolean isVisible = group.isVisible();
        group.setVisible(!isVisible);
        Button toggleButton = (Button)findViewById(R.id.main_toggle_button);
        if (isVisible) {
            toggleButton.setText("Enable communication group");
        } else {
            toggleButton.setText("Disable communication group");
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        MenuItem menuItem1 = menu.findItem(R.id.search_action);
        menuItem1.setVisible(false);
        menuItem.setIcon(Converter.convertLayoutToImage(HomeActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_action:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra(Constants.COME_FROM, Constants.HOME);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNavItem();
       // addressOutput = sharedPref.readString(PrefConstant.LOCATION_SELECTED, "");
      /*  if (addressOutput.isEmpty() || addressOutput.equalsIgnoreCase(getResources().getString(R.string.service_not_available))) {
            fetchAddressAndPermission();
        }*/
        setProfileImage();
        onAddressReceive(addressOutput);
        getWalletAmount();
        cart_count = getCartCount();
        invalidateOptionsMenu();
    }

    private void fetchAddressAndPermission() {
        if (PermissionClass.checkLocationPermission(this)) {
            new LocationUtils(this);
        } else {
            PermissionClass.requestLocationPermission(HomeActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionClass.MY_PERMISSIONS_REQUEST_LOACATION) {
            if (grantResults.length > 0) {
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!locationAccepted)
                    CustomToast.showRectToast(this, tvAddressToolbar, "Permission Denied, You cannot access location data.");
//                    Toast.makeText(this, "Permission Denied, You cannot access location data.", Toast.LENGTH_SHORT).show();
                else
                    new LocationUtils(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                        showAlertDialogToExplain(getResources().getString(R.string.location_message), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PermissionClass.requestLocationPermission(HomeActivity.this);
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

    private void showAlertDialogToExplain(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onAddressReceive(String address) {
        setCurrentAddress(address);
//        getDeliverableCities(address);
        drawer.closeDrawer(GravityCompat.START);
        content_frame.setVisibility(View.VISIBLE);
        rlNotDeliverable.setVisibility(View.GONE);
    }


    private void setCurrentAddress(String address) {
        if (address.isEmpty()) {
            currentAddress = getResources().getString(R.string.service_not_available);
        } else {
            currentAddress = address;
        }
        tvAddressToolbar.setText(currentAddress);
        tvCurrentAddress.setText(currentAddress);
    }

    @Override
    public void onClick(View view) {
        int Id = view.getId();
        Intent intent;
        switch (Id) {
            case R.id.llAddressToolbar:
            case R.id.tvChangeLocation:
            case R.id.rlCurrentAddress:
                new LocationUtils(this);
                break;
            case R.id.rlAddress:
                intent = new Intent(HomeActivity.this, AddressListingActivity.class);
                intent.putExtra("comefrom", "home");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlMYOrder:
                Intent intent1 = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                intent1.putExtra("ComeFromNotification", false);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.etSearch:
                Intent intent11 = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent11);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlMyCart:
                intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra(Constants.COME_FROM, Constants.HOME);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlMyWallet:
                startActivity(new Intent(getApplicationContext(), MyWalletActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlOrderByPhone:
                startActivity(new Intent(getApplicationContext(), OfflineOrderActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlNeedHelp:
                startActivity(new Intent(getApplicationContext(), CustomerSupportActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlShare:
                appShareDialog();
                break;
            case R.id.rlRateUs:
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.essentials.customerapp")));
                break;
            case R.id.rlAboutUs:
                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.rlLogin:
            case R.id.rlLogout:
                loginLogoutUser();
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void loginLogoutUser() {
        drawer.closeDrawer(GravityCompat.START);
        showDialog(HomeActivity.this, Constant.IS_USER_LOGIN);
    }

    public void showDialog(final Activity activity, final boolean isSignIn) {
        String msg = "";
        String title = "";
        if (isSignIn) {
            msg = Constants.SignOutMessage;
            title = Constants.SignOutTitle;
        } else {
            activity.startActivityForResult(new Intent(activity, LoginActivity.class), 200);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return;
            /*msg = Constants.SignInMessage;
            title = Constants.SignInTitle;*/
        }
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvTitle.setText(title);
        tvDialogMessage.setText(msg);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isSignIn) {
                    showDialog(activity, "Please wait.. Signing Out!!");
                    if (sharedPref.readInt(PrefConstant.LOGIN_MODE, 0) == PrefConstant.FACEBOOK) {
                        disconnectFromFacebook();
                    } else {
                        googleSignOut();
                    }

                } else {
                    activity.startActivityForResult(new Intent(activity, LoginActivity.class), 200);
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });
        dialog.show();
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new MySharedPref(HomeActivity.this).deleteAllData();
                        setProfileImage();
                        Constant.IS_USER_LOGIN = false;
                        Constant.USER_ID = "";
                        cart_count = 0;
                        invalidateOptionsMenu();
                        DatabaseReferences.nullReferences();
                        fragment.fetchCartData(false);
                        getWalletAmount();
                        refreshNavItem();
//                        saveCartData();
                        dismissDialog();
                        CustomToast.showRectToast(HomeActivity.this, rlParent, "Sign out SuccessFully");
                    }
                });
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        new MySharedPref(HomeActivity.this).deleteAllData();
                        Constant.IS_USER_LOGIN = false;
                        setProfileImage();
                        Constant.USER_ID = "";
                        cart_count = 0;
                        DatabaseReferences.nullReferences();
                        invalidateOptionsMenu();
                        fragment.fetchCartData(false);
                        getWalletAmount();
                        refreshNavItem();
//                        saveCartData();
                        dismissDialog();
                        CustomToast.showRectToast(HomeActivity.this, rlParent, "Sign out SuccessFully");

                        /*CustomToast.dismissDialog();
                        LoginManager.getInstance().logOut();
                        new MySharedPref(HomeActivity.this).deleteAllData();
                        refreshNavItem();
//                        saveCartData();
                        Constant.IS_USER_LOGIN = false;
                        Constant.USER_ID = "";*/
                    }
                }).executeAsync();
    }

    private void saveCartData() {
        Gson gson = new Gson();
        String cartData = gson.toJson(getCartList());
        sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
    }

    private void refreshNavItem() {
        setUserName();
        if (Constant.IS_USER_LOGIN) {
            rlLogin.setVisibility(View.GONE);
            rlLogout.setVisibility(View.VISIBLE);
            rlAddress.setVisibility(View.VISIBLE);
            rlMYOrder.setVisibility(View.VISIBLE);
            rlMyWallet.setVisibility(View.VISIBLE);
        } else {
            rlLogin.setVisibility(View.VISIBLE);
            rlLogout.setVisibility(View.GONE);
            tvUserName.setText("");
            rlAddress.setVisibility(View.GONE);
            rlMYOrder.setVisibility(View.GONE);
            rlMyWallet.setVisibility(View.GONE);
        }
    }

    private void setUserName() {
        if (sharedPref.readInt(PrefConstant.LOGIN_MODE, PrefConstant.MOBILE) == PrefConstant.MOBILE) {
            tvUserName.setText(sharedPref.readString(PrefConstant.USER_PHONE_NO, ""));
        } else {
            tvUserName.setText(sharedPref.readString(PrefConstant.USER_NAME, ""));
        }
    }

    private void getPopularData() {
        Query productRef = null;
        productRef = DatabaseReferences.getProductRef().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                List<String> textList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                    try {
                        if (ringtoneModel.getKeywords() != null)
                            textList.add(ringtoneModel.getProduct_Name());
                    } catch (Exception ex) {
                        Log.e(TAG, "onDataChange: " + ex.getMessage(), ex);
                    }
                }
                /*adding all product to */
                HomeActivity.super.setSearchListData(textList);
                Constant.SEARCH_TEXT= textList;

                /*etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        etSearch.getText();
                        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                        intent.putExtra(Constants.COME_FROM_HOME, Constants.SEARCH);
                        intent.putExtra(Constants.SEARCH_TEXT, adapter.getItem(i).toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        etSearch.setText("");
                    }
                });*/

                /*adapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, textList);
                etSearch.setAdapter(adapter);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 2) {
                            if (!etSearch.isPopupShowing()) {
                                CustomToast.showRectToastMiddle(HomeActivity.this, etSearch, "No Product Found");
                                return;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void appShareDialog() {
        drawer.closeDrawer(GravityCompat.START);
        Intent shareintent = new Intent();
        shareintent.setAction(Intent.ACTION_SEND);
        shareintent.putExtra(Intent.EXTRA_TEXT, "Supermarket is now just a click away. " +
                "With Essentials app you can buy all your household needs including - Grocery, " +
                "household items, personal care products, Electronics, Books & Stationaries, Fruits and Vegetables " +
                "etc.\n https://play.google.com/store/apps/details?id=com.essentials.customerapp \n " +
//                "Download app now! and use my referral code " +
//                sharedPref.readString(PrefConstant.WALLED_ID, "") + " to get wallet amount");
                "Download the app & get ₹200 instant essentials cash in your wallet");
        shareintent.setType("text/plain");
        startActivity(Intent.createChooser(shareintent, "fabShare via"));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    /*@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }*/

    @Override

    public void onAddProduct(int productCount) {
        cart_count = productCount;
        super.onAddProduct(productCount);
        invalidateOptionsMenu();
    }

    @Override
    public void onRemoveProduct(int productCount) {
        cart_count = productCount;
        super.onRemoveProduct(cart_count);
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200)
            refreshNavItem();
    }

    @Override
    public void onCartDetaRecieve(List<CartModel> cartModelList) {
        if (cartModelList != null) {
            cart_count = cartModelList.size();
            super.setCartList(cartModelList);
        } else
            cart_count = 0;
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
