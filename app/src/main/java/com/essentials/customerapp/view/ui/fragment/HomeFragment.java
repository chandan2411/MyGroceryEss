package com.essentials.customerapp.view.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.data.repository.ProductsRepository;
import com.essentials.customerapp.models.DeliverableLocationModel;
import com.essentials.customerapp.models.Offer2;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.view.ui.activity.OfflineOrderActivity;
import com.essentials.customerapp.view.ui.adapter.ProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.CategoryModel;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.OffersModel;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.adapter.CategoryAdapter;
import com.essentials.customerapp.view.ui.adapter.HomeSliderAdapter;
import com.essentials.customerapp.view.ui.adapter.PopularProductAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private ViewPager viewPager, viewPager3,viewPager4,viewPager5;
    private LinearLayout sliderDotspanel, sliderDotspanel3, sliderDotspanel4, sliderDotspanel5 ;
    private Timer timer;
    private int page_position = 0;
    private int dotscount,dotscount3,dotscount4,dotscount5 ;
    private static final String TAG = "HomeFragment";
    private ImageView[] dots,dots3,dots4,dots5;
    private ImageView disImageView, offerImage;
    private RecyclerView category_bottom_rv, category_top_rv, popular_product_rv, rec_product_rv, daily_ess_product_rv;
    private CategoryAdapter categoryAdapterBottom, categoryAdapterTop;
    private PopularProductAdapter dealsOfTheDayAdapter, recommendedAdapter, dailyEssentialsAdapter;
    private Integer[] images = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4, R.drawable.slider5};
    private RelativeLayout rlParent;
    private List<CategoryModel> categoryModelList, categoryModelList1;
    private LinearLayoutManager layoutManager, layoutManager1;
    private LinearLayout scrollView;
    private List<CartModel> cartModelList = new ArrayList<>();
    private MySharedPref sharedPref;
    private String userMobileNo;
    CartDataListener cartDataRecieveListener;
    private RelativeLayout rlBanner,rlBanner3,rlBanner4,rlBanner5;
    private ProductsRepository productsRepository;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;
    private FloatingActionButton fabOfflineOrder;
    private RelativeLayout rlPinCode;
    private TextView tvPincodeText;
    private String deliPi = "";
    String offerImageURL="";
    DatabaseReference mFirebaseDatabaseReference;


    public HomeFragment() {
        // Required empty public constructor
    }

    private void inititeProgessHud() {
        progressHUD = KProgressHUD.create(activityContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void showProgressHud() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressHud() {
        rlProgress.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        fabOfflineOrder.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPref = new MySharedPref(activityContext);
        assignRef(view);
        getDeliverableCities();
        inititeProgessHud();
        setUpGridRecyclerView();
        scheduleSlider();
        scheduleSlider3();
        scheduleSlider4();
        scheduleSlider5();
        getOffersData();
        getOffers2Data();
        getOffers3Data();
        getOffers4Data();
        getOffers5Data();
        return view;
    }

    private void assignRef(View view) {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        rlPinCode = view.findViewById(R.id.rlPinCode);
        scrollView = view.findViewById(R.id.scrollView);
        rlBanner = view.findViewById(R.id.rlBanner);
        rlBanner3 = view.findViewById(R.id.rlBanner3);
        rlBanner4 = view.findViewById(R.id.rlBanner4);
        rlBanner5 = view.findViewById(R.id.rlBanner5);
        rec_product_rv = view.findViewById(R.id.rec_product_rv);
        daily_ess_product_rv = view.findViewById(R.id.daily_ess_product_rv);
        category_bottom_rv = view.findViewById(R.id.category_bottom_rv);
        category_top_rv = view.findViewById(R.id.category_top_rv);
        popular_product_rv = view.findViewById(R.id.popular_product_rv);
        rlParent = view.findViewById(R.id.rlParent);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager3 = view.findViewById(R.id.viewPager3);
        viewPager4 = view.findViewById(R.id.viewPager4);
        viewPager5 = view.findViewById(R.id.viewPager5);
        sliderDotspanel = view.findViewById(R.id.SliderDots);
        sliderDotspanel3 = view.findViewById(R.id.SliderDots3);
        sliderDotspanel4 = view.findViewById(R.id.SliderDots4);
        sliderDotspanel5 = view.findViewById(R.id.SliderDots5);
        rlProgress = view.findViewById(R.id.rlProgress);
        disImageView = view.findViewById(R.id.cDiscountImage);
        offerImage = view.findViewById(R.id.cDiscountImage);

        tvPincodeText = view.findViewById(R.id.tvPincodeText);
        String savedPincode = sharedPref.readString(PrefConstant.PINCODE, "");
        SpannableString content;
        if (savedPincode.isEmpty()) {
            content = new SpannableString("834001");
        } else {
            content = new SpannableString(savedPincode);
        }
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvPincodeText.setText(content);

        rlPinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForPinValidation();

            }
        });

        fabOfflineOrder = view.findViewById(R.id.fabOfflineOrder);
        fabOfflineOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activityContext, OfflineOrderActivity.class));
                activityContext.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        dealsOfTheDayAdapter = new PopularProductAdapter(activityContext, "Home");
        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(activityContext, LinearLayoutManager.HORIZONTAL, false);
        popular_product_rv.setLayoutManager(pLayoutManager);
        popular_product_rv.setNestedScrollingEnabled(true);
        popular_product_rv.setItemAnimator(new DefaultItemAnimator());
        popular_product_rv.setAdapter(dealsOfTheDayAdapter);

        recommendedAdapter = new PopularProductAdapter(activityContext, "Home");
        RecyclerView.LayoutManager pLayoutManager1 = new LinearLayoutManager(activityContext, LinearLayoutManager.HORIZONTAL, false);
        rec_product_rv.setLayoutManager(pLayoutManager1);
        rec_product_rv.setNestedScrollingEnabled(true);
        rec_product_rv.setItemAnimator(new DefaultItemAnimator());
        rec_product_rv.setAdapter(recommendedAdapter);

        dailyEssentialsAdapter = new PopularProductAdapter(activityContext, "Home");
        RecyclerView.LayoutManager pLayoutManager3 = new LinearLayoutManager(activityContext, LinearLayoutManager.HORIZONTAL, false);
        daily_ess_product_rv.setLayoutManager(pLayoutManager3);
        daily_ess_product_rv.setNestedScrollingEnabled(true);
        daily_ess_product_rv.setItemAnimator(new DefaultItemAnimator());
        daily_ess_product_rv.setAdapter(dailyEssentialsAdapter);


        timer = new Timer();
        viewPager.setPadding(10, 10, 40, 10);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(10);
        viewPager3.setPadding(10, 10, 10, 10);
        viewPager3.setClipToPadding(false);
        viewPager3.setPageMargin(10);
        viewPager4.setPadding(10, 10, 10, 10);
        viewPager4.setClipToPadding(false);
        viewPager4.setPageMargin(10);
        viewPager5.setPadding(10, 10, 10, 10);
        viewPager5.setClipToPadding(false);
        viewPager5.setPageMargin(10);


    }

    private void showDialogForPinValidation() {
        final Dialog dialog = new Dialog(activityContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);

        TextView tvApply = dialog.findViewById(R.id.tvApply);
        TextView tvError = dialog.findViewById(R.id.tvError);
        EditText edtApply = dialog.findViewById(R.id.edtApply);
        ImageView ivClose = dialog.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                edtApply.setText("");
            }
        });

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pincode = edtApply.getText().toString();
                if (pincode.length() == 6) {
                    for (DeliverableLocationModel model : locationModel) {
                        if (model.getPincode().equalsIgnoreCase(pincode)) {
                            Toast.makeText(activityContext, "We deliver at this location", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            edtApply.setText("");
                            SpannableString content = new SpannableString(pincode);
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            tvPincodeText.setText(content);
                            sharedPref.writeString(PrefConstant.PINCODE, pincode);
                        } else {
                            tvError.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(activityContext, "Please enter valid pincode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }

    /*Fetch the cities or area where fresh choice is delivering*/
    final List<DeliverableLocationModel> locationModel = new ArrayList<>();

    private void getDeliverableCities() {
        Query locationRef = DatabaseReferences.getDeliverableLocations().orderByChild("Location_Id")/*.orderByChild("Location_Id")*/;
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationModel.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DeliverableLocationModel model = dataSnapshot1.getValue(DeliverableLocationModel.class);
                    locationModel.add(model);
                }
                rlPinCode.setClickable(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                rlPinCode.setClickable(true);
            }
        });
    }


    private void getOffersData() {
        Query productRef = DatabaseReferences.getOfferRef().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OffersModel> offersModels = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OffersModel ringtoneModel = postSnapshot.getValue(OffersModel.class);
                    offersModels.add(ringtoneModel);
                }
                if (offersModels.isEmpty()) {
                    rlBanner.setVisibility(View.GONE);
                } else {
                    addSlider(offersModels);
                    rlBanner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                rlBanner.setVisibility(View.GONE);
            }
        });
    }
    private void getOffers3Data() {
        Query productRef = DatabaseReferences.getOffer3Ref().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OffersModel> offersModels = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OffersModel ringtoneModel = postSnapshot.getValue(OffersModel.class);
                    offersModels.add(ringtoneModel);
                }
                if (offersModels.isEmpty()) {
                    rlBanner3.setVisibility(View.GONE);
                } else {
                    addSlider3(offersModels);
                    rlBanner3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                rlBanner3.setVisibility(View.GONE);
            }
        });
    }
    private void getOffers4Data() {
        Query productRef = DatabaseReferences.getOffer4Ref().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OffersModel> offersModels = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OffersModel ringtoneModel = postSnapshot.getValue(OffersModel.class);
                    offersModels.add(ringtoneModel);
                }
                if (offersModels.isEmpty()) {
                    rlBanner4.setVisibility(View.GONE);
                } else {
                    addSlider4(offersModels);
                    rlBanner4.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                rlBanner4.setVisibility(View.GONE);
            }
        });
    }
    private void getOffers5Data() {
        Query productRef = DatabaseReferences.getOffer5Ref().orderByChild("Product_Modified_Date");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OffersModel> offersModels = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OffersModel ringtoneModel = postSnapshot.getValue(OffersModel.class);
                    offersModels.add(ringtoneModel);
                }
                if (offersModels.isEmpty()) {
                    rlBanner5.setVisibility(View.GONE);
                } else {
                    addSlider5(offersModels);
                    rlBanner5.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                rlBanner5.setVisibility(View.GONE);
            }
        });
    }

    private void getOffers2Data() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Offers2/" + "-MFxyFIvLTr9IxOFdFqB");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                offerImageURL = String.valueOf(dataSnapshot.child("Offers_Image_Url").getValue());
                if(!offerImageURL.equals("")){
                    offerImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(offerImageURL).into(offerImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void addSlider(List<OffersModel> offersModels) {
        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(activityContext);
        dotscount = offersModels.size();
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.setDataList(offersModels);
        dots = new ImageView[dotscount];
        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(activityContext);
            dots[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void addSlider3(List<OffersModel> offersModels) {
        HomeSliderAdapter viewPagerAdapter2 = new HomeSliderAdapter(activityContext);
        dotscount3 = offersModels.size();
        viewPager3.setAdapter(viewPagerAdapter2);
        viewPagerAdapter2.setDataList(offersModels);
        dots3 = new ImageView[dotscount3];
        for (int i = 0; i < dotscount3; i++) {
            dots3[i] = new ImageView(activityContext);
            dots3[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel3.addView(dots3[i], params);

        }
        dots3[0].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));
        viewPager3.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount3; i++) {
                    dots3[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
                }

                dots3[position].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    private void addSlider4(List<OffersModel> offersModels) {
        HomeSliderAdapter viewPagerAdapter2 = new HomeSliderAdapter(activityContext);
        dotscount4 = offersModels.size();
        viewPager4.setAdapter(viewPagerAdapter2);
        viewPagerAdapter2.setDataList(offersModels);
        dots4 = new ImageView[dotscount4];
        for (int i = 0; i < dotscount4; i++) {
            dots4[i] = new ImageView(activityContext);
            dots4[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel4.addView(dots4[i], params);

        }
        dots4[0].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));
        viewPager4.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount4; i++) {
                    dots4[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
                }

                dots4[position].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void addSlider5(List<OffersModel> offersModels) {
        HomeSliderAdapter viewPagerAdapter2 = new HomeSliderAdapter(activityContext);
        dotscount5 = offersModels.size();
        viewPager5.setAdapter(viewPagerAdapter2);
        viewPagerAdapter2.setDataList(offersModels);
        dots5 = new ImageView[dotscount5];
        for (int i = 0; i < dotscount5; i++) {
            dots5[i] = new ImageView(activityContext);
            dots5[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel5.addView(dots5[i], params);

        }
        dots5[0].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));
        viewPager5.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount5; i++) {
                    dots5[i].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.non_active_dot));
                }

                dots5[position].setImageDrawable(ContextCompat.getDrawable(activityContext, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



    private void fetchDealsOfTheDay() {
        Query productRef = null;
       // productRef = DatabaseReferences.getProductRef().orderByChild("Product_Modified_Date").limitToLast(20);
        productRef = mFirebaseDatabaseReference.child("Product").orderByChild("DealsOftheday").equalTo("true");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                List<ProductModel> productModelList1 = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
                Collections.reverse(productModelList);
                dealsOfTheDayAdapter.setDataList(productModelList);
                /*newProductAdapter.setDataList(productModelList);*/
                fetchRecommendedProduct();
//                fetchCartData(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                fetchCartData(false);
                fetchRecommendedProduct();
            }
        });
    }

   private void fetchRecommendedProduct() {
        Query productRef = null;
//        productRef = DatabaseReferences.getProductRef().orderByChild("Product_MRP").limitToLast(20);
     //   productRef = DatabaseReferences.getProductRef().orderByPriority().limitToLast(20);
       productRef = mFirebaseDatabaseReference.child("Product").orderByChild("RecommendedEssentials").equalTo("true");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                List<ProductModel> productModelList1 = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
                Collections.reverse(productModelList);
                recommendedAdapter.setDataList(productModelList);
                /*newProductAdapter.setDataList(productModelList);*/
                fetchDailyEssentials();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fetchDailyEssentials();
            }
        });

    }



    private void fetchDailyEssentials() {
        Query productRef = null;
//        productRef = DatabaseReferences.getProductRef().orderByChild("Product_MRP").limitToLast(20);
       // productRef = DatabaseReferences.getProductRef().orderByChild("Product_Name").limitToLast(20);
        productRef = mFirebaseDatabaseReference.child("Product").orderByChild("DailyEssentials").equalTo("true");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProductModel> productModelList = new ArrayList<>();
                List<ProductModel> productModelList1 = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ProductModel ringtoneModel = postSnapshot.getValue(ProductModel.class);
                    productModelList.add(ringtoneModel);
                }
                Collections.reverse(productModelList);
                dailyEssentialsAdapter.setDataList(productModelList);
                /*newProductAdapter.setDataList(productModelList);*/
                fetchCartData(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fetchCartData(false);
            }
        });
    }

    public interface CartDataListener {
        public void onCartDetaRecieve(List<CartModel> cartModelList);
    }


    AppCompatActivity activityContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            cartDataRecieveListener = (CartDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }


        if (context instanceof Activity) {
            activityContext = (AppCompatActivity) context;
        }

    }


    public void fetchCartData(final boolean isCalledFromResume) {
        cartModelList.clear();
        if (Constant.IS_USER_LOGIN) {
            CartRepository.getInstance()
                    .getUserCartData()
                    .observe(activityContext, new Observer<List<CartModel>>() {
                        @Override
                        public void onChanged(List<CartModel> cartModelList) {
                            if (cartModelList != null) {
                                if (!cartModelList.isEmpty()) {
                                    recommendedAdapter.setCartModelList(cartModelList, isCalledFromResume);
                                    dealsOfTheDayAdapter.setCartModelList(cartModelList, isCalledFromResume);
                                    cartDataRecieveListener.onCartDetaRecieve(cartModelList);
                                } else {
                                    cartDataRecieveListener.onCartDetaRecieve(null);
                                    recommendedAdapter.setCartModelList(new ArrayList<>(), isCalledFromResume);
                                    dealsOfTheDayAdapter.setCartModelList(new ArrayList<>(), isCalledFromResume);
                                }
                                if (!isCalledFromResume)
                                    hideProgressHud();
                            }
                        }
                    });

        } else {
            Gson gson = new Gson();
            String cartItem = sharedPref.readString(PrefConstant.CART_ITEM, "");
            if (!cartItem.isEmpty()) {
                Log.d("CART : ", cartItem);
                Type type = new TypeToken<List<CartModel>>() {
                }.getType();
                cartModelList = gson.fromJson(cartItem, type);
                if (cartModelList != null && !cartModelList.isEmpty()) {
                    dealsOfTheDayAdapter.setCartModelList(cartModelList, isCalledFromResume);
                    cartDataRecieveListener.onCartDetaRecieve(cartModelList);
                } else {
                    cartDataRecieveListener.onCartDetaRecieve(null);
                    dealsOfTheDayAdapter.setCartModelList(new ArrayList<>(), isCalledFromResume);
                }
                if (!isCalledFromResume)
                    hideProgressHud();
            } else {
                cartDataRecieveListener.onCartDetaRecieve(null);
                dealsOfTheDayAdapter.setCartModelList(new ArrayList<>(), true);
                if (!isCalledFromResume)
                    hideProgressHud();
            }
        }


    }

    private void setUpGridRecyclerView() {
        categoryAdapterBottom = new CategoryAdapter(activityContext, "Bottom");
        categoryAdapterTop = new CategoryAdapter(activityContext, "Top");

        layoutManager = new GridLayoutManager(activityContext, 2);
        layoutManager1 = new GridLayoutManager(activityContext, 2);

        category_bottom_rv.setLayoutManager(layoutManager);
        category_bottom_rv.setNestedScrollingEnabled(true);
        category_bottom_rv.setItemAnimator(new DefaultItemAnimator());
        category_bottom_rv.setAdapter(categoryAdapterBottom);

        category_top_rv.setLayoutManager(layoutManager1);
        category_top_rv.setNestedScrollingEnabled(true);
        category_top_rv.setItemAnimator(new DefaultItemAnimator());
        category_top_rv.setAdapter(categoryAdapterTop);

        fetchCategoryData();
    }


    public void hideShimmer() {
        CustomToast.dismissDialog();
//        scrollView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchCartData(true);
    }

    private void fetchCategoryData() {
        showProgressHud();
        DatabaseReferences.getCategoryRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryModelList = new ArrayList<>();
                categoryModelList1 = new ArrayList<>();
                int count = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryModel categoryModel = postSnapshot.getValue(CategoryModel.class);
                    if (count < 2) {
                        categoryModelList1.add(categoryModel);
                    } else {
                        categoryModelList.add(categoryModel);
                    }
                    count++;
                }
                categoryAdapterBottom.setCatDataList(categoryModelList);
                categoryAdapterTop.setCatDataList(categoryModelList1);
                fetchDealsOfTheDay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fetchDealsOfTheDay();
            }
        });

        /*get product data*/
//        productsRepository = ProductsRepository.getInstance();
//        productsRepository.getProductCategory().observe(activityContext, categoryModels -> {
//            for (int i = 0; i < categoryModels.size(); i++) {
//                if (i < 3) {
//                    categoryModelList1.add(categoryModels.get(i));
//                } else {
//                    categoryModelList1.add(categoryModels.get(i));
//                }
//                categoryAdapter.setCatDataList(categoryModelList);
//                categoryAdapter1.setCatDataList(categoryModelList1);
//            }
//        });
    }


    public void scheduleSlider() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);

            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }
    public void scheduleSlider3() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount3) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }

                viewPager3.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    public void scheduleSlider4() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount4) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }

                viewPager4.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }

    public void scheduleSlider5() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount5) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }

                viewPager5.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 4000);
    }


    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        activityContext.setTitle("Home");
    }


}
