package com.essentials.customerapp.view.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ItemsOrderAdapter extends RecyclerView.Adapter<ItemsOrderAdapter.MyViewHolder> {

    Context context;
    private List<CartModel> orderModelList;


    public ItemsOrderAdapter(Context myOrderActivity) {
        context = myOrderActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_items_order, parent, false);
        return new MyViewHolder(itemView);
    }

    private static final String TAG = "ItemsOrderAdapter";

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final CartModel cartModel = orderModelList.get(position);

        holder.tvProductTitle.setText(cartModel.getProduct_Name());
        holder.tvProductQuantity.setText(cartModel.getProduct_Quantity_Type());
        holder.tvMRP.setText(cartModel.getProduct_MRP());
        String totalSP = cartModel.getProduct_SP();
        try {
            totalSP = String.format(Locale.ENGLISH, "%.2f",(cartModel.getProdctQuantityUnit() * Double.parseDouble(cartModel.getProduct_SP())));

        } catch (Exception ex) {
            Log.e(TAG, "onBindViewHolder: ", ex);
        }
        holder.tvSP.setText(totalSP);
        holder.tvSP1.setText(cartModel.getProduct_SP());
        holder.tvQuantityAdded.setText(String.valueOf(cartModel.getProdctQuantityUnit()));

        if (Double.parseDouble(cartModel.getProduct_MRP()) > Double.parseDouble(cartModel.getProduct_SP())) {
            holder.rlMRP.setVisibility(View.VISIBLE);
            holder.tvMRP.setText(cartModel.getProduct_MRP());
        } else {
            holder.rlMRP.setVisibility(View.INVISIBLE);
        }
        setImageToView(holder, cartModel);
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

    private void setImageToView(MyViewHolder holder, CartModel cartModel) {
        if (!cartModel.getProduct_Image_Url().get(0).isEmpty()) {
            Picasso.get()
                    .load(cartModel.getProduct_Image_Url().get(0))
                    .into(holder.ivProductImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Error : ", e.getMessage());
                        }
                    });
        } else {
            holder.ivProductImage.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public int getItemCount() {
        return orderModelList != null && !orderModelList.isEmpty() ? orderModelList.size() : 0;
    }

    public void setDataList(List<CartModel> orderModelList) {
        this.orderModelList = orderModelList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductTitle, tvProductQuantity, tvSP, tvMRP, tvSP1, tvQuantityAdded;
        ImageView ivProductImage;
        RelativeLayout rlMRP;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductTitle = itemView.findViewById(R.id.productTitle);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvSP = itemView.findViewById(R.id.tvSP);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            tvSP1 = itemView.findViewById(R.id.tvSP1);
            tvQuantityAdded = itemView.findViewById(R.id.tvQuantityAdded);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            rlMRP = itemView.findViewById(R.id.rlMRP);

        }

    }
}
