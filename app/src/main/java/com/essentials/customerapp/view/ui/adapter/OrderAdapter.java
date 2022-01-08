package com.essentials.customerapp.view.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.data.repository.OrderRepository;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.models.OrderModelNew;
import com.essentials.customerapp.models.OrderModelNewWithStatus;
import com.essentials.customerapp.models.OrderStatusModel;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.ItemsOrderedActivity;
import com.essentials.customerapp.view.ui.activity.OrderHistoryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.essentials.customerapp.utilities.Constant.OREDR_ID;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    Context context;
    private List<OrderModelNew> orderModelList = new ArrayList<>();


    public OrderAdapter(Context myOrderActivity) {
        context = myOrderActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        OrderModelNew model = orderModelList.get(position);

        holder.tvOrderPlacedDate.setText(model.getOrderFormattedDate());
        holder.tvOrderScheduleDate.setText(model.getOrderTimeSlot());
        /*Checking order status and showing UI*/
        setStatus(holder, model);
        holder.tvTotalPrice.setText(String.valueOf(model.getTotalMrp()));
        /*Checking delivery charges if more than 0 showing amount else free text green color*/
        Double deliveryCharges = model.getDeliveryAmount();
        if (deliveryCharges > 0) {
            holder.tvDeliveryCharges.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.tvDeliveryCharges.setText("₹ " + deliveryCharges);
        } else {
            holder.tvDeliveryCharges.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvDeliveryCharges.setText("Free");
        }

        holder.tvOrderScheduleDate.setText(model.getOrderTimeSlot());
        holder.tvOrderId.setText(model.getOrderId());

        holder.tvWalletDiscount.setText(String.format(Locale.ENGLISH, "%.2f", model.getEssentialsDiscount()));
        holder.tvFinalAmount.setText(String.format(Locale.ENGLISH, "%.2f",model.getSubtotal()));

        holder.tvViewDetails.setOnClickListener(view -> {
            Intent intent = new Intent(context, ItemsOrderedActivity.class);
            intent.putExtra(OREDR_ID, model.getOrderId());
            context.startActivity(intent);
            ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        });

    }

    private void setStatus(MyViewHolder holder, OrderModelNew model) {
        List<OrderStatusModel> modelList = new ArrayList<>();
        DatabaseReferences.getOrderStatusRef().child(Constant.USER_ID).child(model.getOrderId())
                .orderByChild("dateTime")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            OrderStatusModel model = data.getValue(OrderStatusModel.class);
                            modelList.add(model);
                        }
                        /*Show UI*/
                        String orderStatus = modelList.get(modelList.size() - 1).getOrderStatus();
                        if (orderStatus.equalsIgnoreCase("Order Delivered"))
                            holder.rlDeliveredBy.setVisibility(View.VISIBLE);
                        else
                            holder.rlDeliveredBy.setVisibility(View.GONE);

                        setStatusForOrder(holder, orderStatus);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void setStatusForOrder(MyViewHolder holder, String orderStatus) {
        /*Checking order status and setting the text accordingly*/
        if (orderStatus.equalsIgnoreCase("Order Cancelled")) {
            holder.ivOrderStatus.setImageResource(R.drawable.cancelorder);
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.tvOrderStatus.setText("Order Cancelled");
        } else if (orderStatus.equalsIgnoreCase("Order Delivered")) {
            holder.ivOrderStatus.setImageResource(R.drawable.tick);
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvOrderStatus.setText("Order Delivered");
        } else if (orderStatus.equalsIgnoreCase("Order Placed") || orderStatus.equalsIgnoreCase("Processing")) {
            holder.ivOrderStatus.setImageResource(R.drawable.orderplaced);
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.order_placed_text_color));
            holder.tvOrderStatus.setText("Order Placed");
        } else {
            holder.ivOrderStatus.setImageResource(R.drawable.outfordelivery);
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.theme_yellow));
            holder.tvOrderStatus.setText("Order is Out For Delivery");
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvOrderPlacedDate, tvOrderScheduleDate, tvTotalPrice, tvDeliveryCharges,
                tvOrderId, tvWalletDiscount, tvFinalAmount, tvViewDetails, tvOrderStatus;
        private RelativeLayout rlDeliveredBy;
        private ImageView ivOrderStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderPlacedDate = itemView.findViewById(R.id.tvOrderPlacedDate);
            tvOrderScheduleDate = itemView.findViewById(R.id.tvOrderScheduleDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDeliveryCharges = itemView.findViewById(R.id.tvDeliveryCharges);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvFinalAmount = itemView.findViewById(R.id.tvFinalAmount);
            tvWalletDiscount = itemView.findViewById(R.id.tvWalletDiscount);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
            ivOrderStatus = itemView.findViewById(R.id.ivOrderStatus);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            rlDeliveredBy = itemView.findViewById(R.id.rlDeliveredBy);

        }

    }

    private void shoeCancelDialog(final CartModel cartModel) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel_order_layout);

        final TextView tvCancelReason = dialog.findViewById(R.id.tvCancelReason);
        final EditText edtFeedback = dialog.findViewById(R.id.edtFeedback);
        Button btnCancelOrder = dialog.findViewById(R.id.btnCancelOrder);
        Button btnDontCancel = dialog.findViewById(R.id.btnDontCancel);
        final Spinner spnCancelReason = dialog.findViewById(R.id.spnCancelReason);
        LinearLayout llSpinner = dialog.findViewById(R.id.llSpinner);

        llSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spnCancelReason.performClick();
            }
        });

        spnCancelReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvCancelReason.setText(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tvCancelReason.setText(context.getString(R.string.reason_for_cancel));

            }
        });
        // if button is clicked, close the custom dialog
        btnDontCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!tvCancelReason.getText().toString().isEmpty()
                        || tvCancelReason.getText().toString().equalsIgnoreCase(context.getString(R.string.reason_for_cancel)))
                        && edtFeedback.getText().toString().length() > 10) {
                    cartModel.setIs_Order_Cancelled(true);
                    cancelOrder(cartModel);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Please select reason and write feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void cancelOrder(CartModel cartModel) {

        String mobile = new MySharedPref(context).readString(PrefConstant.USER_PHONE_NO, "");
        DatabaseReference databaseReference = DatabaseReferences.getOrderHistoryRef().child(mobile)
                .child(cartModel.getOrder_Id()).child("products")
                .orderByChild("product_Id").equalTo(cartModel.getProduct_Id()).getRef();
        databaseReference.child("Is_Order_Cancelled").setValue("true");

    }


    @Override
    public int getItemCount() {
        return orderModelList != null && !orderModelList.isEmpty() ? orderModelList.size() : 0;
    }

    public void setDataList(List<OrderModelNew> orderModelList) {
        Collections.reverse(orderModelList);
        this.orderModelList = orderModelList;
        notifyDataSetChanged();
    }

}
