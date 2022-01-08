package com.essentials.customerapp.view.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.view.ui.activity.BaseActivity;
import com.essentials.customerapp.view.ui.activity.ProductDetailsActivity;
import com.google.gson.Gson;
import com.essentials.customerapp.R;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.CartActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartModel> cartModelList;
    private Context context;
    private int pQuantity = 1;
    private String amountSubtotal;
    private MySharedPref sharedPref;
    private CartRepository cartRepository;


    public CartAdapter(Context context) {
        this.context = context;
        sharedPref = new MySharedPref(context);
        cartRepository = CartRepository.getInstance();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final CartModel cartModel = cartModelList.get(position);

        holder.title.setText(cartModel.getProduct_Name());
        holder.tvAttribute.setText(cartModel.getProduct_Quantity_Type());

        String productSP = cartModel.getProduct_SP();
        String productMRP = cartModel.getProduct_MRP();
        int quantityAdded = cartModel.getProdctQuantityUnit();

        holder.llCartItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
//                intent.putExtra("Product", product);
                intent.putExtra("ProductId", cartModel.getProduct_Id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((CartActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });


        /*Product discount*/
        if (cartModel.getProductDiscount().isEmpty()) {
            int dis = AppUtils.calculateDiscount(cartModel.getProduct_MRP(), cartModel.getProduct_SP());
            if (dis >= 5) {
                cartModel.setProductDiscount(dis + "% OFF");
            } else {
                cartModel.setProductDiscount("0");
            }
        }

        /*Show product discount view and mrp view*/
        if (cartModel.getProductDiscount().equals("0")) {
            holder.tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDiscount.setText(cartModel.getProductDiscount());
            holder.tvDiscount.setVisibility(View.VISIBLE);
        }
        if (Double.parseDouble(cartModel.getProduct_MRP()) > Double.parseDouble(cartModel.getProduct_SP())) {
            holder.rlMRP.setVisibility(View.VISIBLE);
            holder.tvMRP.setText(cartModel.getProduct_MRP());
        } else {
            holder.rlMRP.setVisibility(View.INVISIBLE);
        }


        holder.tvQuantityAdded.setText("" + quantityAdded);
        holder.tvSP.setText(productSP);

        amountSubtotal = String.valueOf(Double.parseDouble(productMRP) * quantityAdded);
        setImageToView(holder, cartModel);

        /*add button to increase no of item count*/
        holder.tvQuantityPlus.setOnClickListener(v -> {
            pQuantity = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
            if (pQuantity >= 1) {
                int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                total_item++;
                holder.tvQuantityAdded.setText(total_item + "");
                incDecProductCount(holder, cartModel, total_item);
            }
        });

        holder.tvQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pQuantity = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                if (pQuantity > 1) {
                    int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                    total_item--;
                    holder.tvQuantityAdded.setText(total_item + "");
                    incDecProductCount(holder, cartModel, total_item);
                } else {
                    deleteProductFromCart(cartModel);
                }
            }

        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForDeleteProduct("Delete Item",
                        "Are you sure want to delete this item from the cart ?", cartModel);
            }
        });


    }

    private void showAlertForDeleteProduct(String title, String message, CartModel cartModel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvDialogMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvTitle.setText(title);
        tvDialogMessage.setText(message);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("NO");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setText("YES");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteProductFromCart(cartModel);
            }
        });
        dialog.show();
    }


    private void deleteProductFromCart(CartModel cartModel) {
        if (Constant.IS_USER_LOGIN) {
            cartRepository.deleteProduct(cartModel.getProduct_Id());
            cartModelList.remove(cartModel);
            notifyDataSetChanged();
        } else {
            cartModelList.remove(cartModel);
            Gson gson = new Gson();
            String cartData = gson.toJson(cartModelList);
            sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
        }
        ((BaseActivity) context).setCartList(cartModelList);
        notifyDataSetChanged();
        ((CartActivity) context).updateTotalPrice1(cartModelList);
    }

    private void incDecProductCount(MyViewHolder holder, CartModel cartModel, int total_item) {

        for (int i = 0; i < cartModelList.size(); i++) {
            /*Compare product Id and add Amount*/
            if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(cartModel.getProduct_Id())) {
                amountSubtotal = String.valueOf(Double.parseDouble(cartModel.getProduct_SP()) * total_item);
                cartModelList.get(i).setProdctQuantityUnit(total_item);
                cartModelList.get(i).setSub_Total(amountSubtotal);
                ((BaseActivity) context).setCartList(cartModelList);
//                ((CartActivity) context).showNoOfCount(cartModelList);
                if (Constant.IS_USER_LOGIN) {
                    cartRepository.addProductToCart(cartModel);
                } else {
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartModelList);
                    sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                }
            }
        }
        ((CartActivity) context).updateTotalPrice1(cartModelList);
    }

    private void setImageToView(MyViewHolder holder, CartModel cartModel) {
        if (!cartModel.getProduct_Image_Url().get(0).isEmpty()) {
            Picasso.get()
                    .load(cartModel.getProduct_Image_Url().get(0))
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Error : ", e.getMessage());
                        }
                    });
        } else {
            holder.imageView.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public int getItemCount() {

        return cartModelList != null && !cartModelList.isEmpty() ? cartModelList.size() : 0;
    }


    public void setDataModeList(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        ((CartActivity) context).updateTotalPrice1(cartModelList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSP, tvMRP, tvDiscount, tvAttribute, tvQuantityPlus, tvQuantityMinus,
                tvQuantityAdded;
        LinearLayout llCartItems;
        ImageView imageView, ivDelete;
        TextView title;
        ProgressBar progressBar;
        RelativeLayout rlMRP;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            llCartItems = itemView.findViewById(R.id.llCartItems);
            progressBar = itemView.findViewById(R.id.progressbar);
            tvSP = itemView.findViewById(R.id.tvSP);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            rlMRP = itemView.findViewById(R.id.rlMRP);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            title = itemView.findViewById(R.id.product_title);
            tvAttribute = itemView.findViewById(R.id.tvProductQuantity);
            tvQuantityPlus = itemView.findViewById(R.id.tvQuantityPlus);
            tvQuantityMinus = itemView.findViewById(R.id.tvQuantityMinus);
            tvQuantityAdded = itemView.findViewById(R.id.tvQuantityAdded);
        }
    }
}
