package com.essentials.customerapp.view.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.OrderRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.OrderModelNew;
import com.essentials.customerapp.models.OrderModelNewWithStatus;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.adapter.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rcvOrderHistory;
    private OrderAdapter adapter;
    private RelativeLayout rlItemOrder;
    private TextView tvStartShopping;
    private boolean comeFromNotification;
    private KProgressHUD progressHUD;
    private RelativeLayout rlProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        Toolbar toolbar = findViewById(R.id.white_toolbar);
        rcvOrderHistory = findViewById(R.id.rcvOrderHistory);
        rlProgress = findViewById(R.id.rlProgress);
        tvStartShopping = findViewById(R.id.tvStartShopping);
        rlItemOrder = findViewById(R.id.rlItemOrder);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        comeFromNotification = getIntent().getBooleanExtra("ComeFromNotification", false);

        tvStartShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rcvOrderHistory.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rcvOrderHistory.setLayoutManager(manager);
        adapter = new OrderAdapter(OrderHistoryActivity.this);
        rcvOrderHistory.setAdapter(adapter);
        inititeProgessHud();
        fetchOrderHistoryData();
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

    private void showEmptyView(int i) {
        if (i == 0) {
            rlItemOrder.setVisibility(View.VISIBLE);
            rcvOrderHistory.setVisibility(View.INVISIBLE);
        } else {
            rlItemOrder.setVisibility(View.INVISIBLE);
            rcvOrderHistory.setVisibility(View.VISIBLE);
        }
    }

    List<OrderModelNew> orderModelNewList;

    private void fetchOrderHistoryData() {
        orderModelNewList = new ArrayList<>();
        showProgressHud();
        String userId = new MySharedPref(this).readString(PrefConstant.USER_ID, "");
        DatabaseReferences.getOrderHistoryRef().child(userId).orderByChild("orderDate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            OrderModelNew orderModel = data.getValue(OrderModelNew.class);
                            orderModelNewList.add(orderModel);
                        }
                        if (!orderModelNewList.isEmpty()) {
                            showEmptyView(1);
                            adapter.setDataList(orderModelNewList);
                        } else {
                            showEmptyView(0);
                        }
                        hideProgressHud();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        hideProgressHud();
                    }
                });
    }

    /*private void fetchOrderStatus(OrderModelNew orderModel) {
        OrderRepository.getInstance().getOrderStatus(orderModel.getOrderId())
                .observe(OrderHistoryActivity.this, new Observer<List<OrderStatusModel>>() {
                    @Override
                    public void onChanged(List<OrderStatusModel> orderStatusModels) {
                        if (orderStatusModels != null) {
                            adapter.setDataList(new OrderModelNewWithStatus(orderModel, orderStatusModels));
                        }
                    }
                });
    }*/

    @Override
    public void onBackPressed() {
        if (comeFromNotification) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            OrderHistoryActivity.this.finish();
        }
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
