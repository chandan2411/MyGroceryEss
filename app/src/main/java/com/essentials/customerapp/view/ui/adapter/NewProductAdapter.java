package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.CustomToast;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.DatabaseReferences;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.BaseActivity;
import com.essentials.customerapp.view.ui.activity.HomeActivity;
import com.essentials.customerapp.view.ui.activity.ProductDetailsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.MyViewHolder> {

    List<ProductModel> productList;
    Context context;
    String Tag;

    MySharedPref sharedPref;
    Gson gson;
    List<CartModel> cartModelList = new ArrayList<>();
    String quantitySelected, _price, _attribute, totalProductPrice;

    public NewProductAdapter(List<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public NewProductAdapter(Context context, String tag) {
        this.productList = productList;
        this.context = context;
        Tag = tag;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        if (Tag.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_new_home_products, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_new_products, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProductModel product = productList.get(position);
        product.setPOSITION(position);
        sharedPref = new MySharedPref(context);
        gson = new Gson();
//        cartModelList = ((BaseActivity) context).fetchCartItemWS();
        cartModelList = ((BaseActivity) context).getCartList();
        holder.quantity.setText("1");

        holder.title.setText(product.getProduct_Name());
        holder.price.setText(product.getProduct_SP());
        holder.currency.setText(product.getCurrency());
        holder.attribute.setText(product.getProduct_Quantity_Type());

        if (!product.getProduct_Image_Url().get(0).isEmpty()) {
            Picasso.get().load(product.getProduct_Image_Url().get(0)).error(R.drawable.no_image).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("Error : ", e.getMessage());
                }
            });
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }

        if (!cartModelList.isEmpty()) {
            for (int i = 0; i < cartModelList.size(); i++) {
                if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                    holder.shopNow.setVisibility(View.GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    holder.quantity.setText("" + cartModelList.get(i).getProdctQuantityUnit());

                } else {
                    holder.shopNow.setVisibility(View.VISIBLE);
                    holder.quantity_ll.setVisibility(View.GONE);
                    holder.quantity.setText("0");
                }
            }
        }


        holder.shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shopNow.setVisibility(View.GONE);
                holder.quantity_ll.setVisibility(View.VISIBLE);
                _price = product.getProduct_SP();
                quantitySelected = holder.quantity.getText().toString();
                _attribute = product.getProduct_Quantity_Type();
                totalProductPrice = String.valueOf(Double.parseDouble(_price) * Integer.parseInt(quantitySelected));

                if (context instanceof HomeActivity) {
                    CartModel cartModel = new CartModel(product, quantitySelected, totalProductPrice, AppUtils.dateAndTime());
                    if (!sharedPref.readString(PrefConstant.USER_PHONE_NO, "").isEmpty()) {
                        DatabaseReferences.getUserCartItem().child(Constant.USER_ID)
                                .child(product.getProduct_Id()).setValue(cartModel);
                        Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Gson gson = new Gson();
                        cartModelList.add(cartModel);
                        String cartData = gson.toJson(cartModelList);
                        sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                        Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT).show();

                    }
//                    cartModelList = ((BaseActivity) context).getCartList();
                    ((AddorRemoveCallbacks) context).onAddProduct(cartModelList.size());
                    notifyItemChanged(position);
                }
            }
        });


        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < cartModelList.size(); i++) {
                    if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                        int total_item = cartModelList.get(i).getProdctQuantityUnit();
                        total_item++;
                        Log.d("totalItem", total_item + "");
                        holder.quantity.setText(total_item + "");
                        totalProductPrice = String.valueOf(Double.parseDouble(holder.price.getText().toString()) * total_item);
                        cartModelList.get(i).setProdctQuantityUnit(Integer.parseInt(holder.quantity.getText().toString()));
                        cartModelList.get(i).setSub_Total(totalProductPrice);
                        /*Update product count in cart firebase db*/
                        CartModel cartModel = new CartModel(product, "" + total_item, totalProductPrice, AppUtils.dateAndTime());
                        if (!sharedPref.readString(PrefConstant.USER_PHONE_NO, "").isEmpty()) {
                            DatabaseReference databaseReference = DatabaseReferences.getUserCartItem().child(Constant.USER_ID)
                                    .child(product.getProduct_Id());
                            databaseReference.child("subTotal").setValue(totalProductPrice);
                            databaseReference.child("prodctQuantityUnit").setValue(total_item);
                        } else {
                            Gson gson = new Gson();
                            cartModelList.add(cartModel);
                            String cartData = gson.toJson(cartModelList);
                            sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                        }
                    }
                }


            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(holder.quantity.getText().toString()) != 1) {
                    for (int i = 0; i < cartModelList.size(); i++) {
                        if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                            int total_item = Integer.parseInt(holder.quantity.getText().toString());
                            total_item--;
                            holder.quantity.setText(total_item + "");
                            Log.d("totalItem", total_item + "");

                            totalProductPrice = String.valueOf(Double.parseDouble(holder.price.getText().toString()) * total_item);

                            cartModelList.get(i).setProduct_Quantity_Type(holder.quantity.getText().toString());
                            cartModelList.get(i).setSub_Total(totalProductPrice);

                            if (total_item > 0) {
                                DatabaseReference databaseReference = DatabaseReferences.getUserCartItem().child(Constant.USER_ID)
                                        .child(product.getProduct_Id());
                                databaseReference.child("SubTotal").setValue(totalProductPrice);
                                databaseReference.child("prodctQuantityUnit").setValue(total_item);
                            } else {
                                DatabaseReference databaseReference = DatabaseReferences.getUserCartItem().child(Constant.USER_ID)
                                        .child(product.getProduct_Id());
                                databaseReference.removeValue();
                            }


                        }
                    }

                }


            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
//                intent.putExtra("Product", product);
                intent.putExtra("ProductId", product.getProduct_Id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        return productList != null ? productList.size() : 0;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDataList(List<ProductModel> productModelList) {
        this.productList = productModelList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, attribute, currency, price, shopNow;
        ProgressBar progressBar;
        LinearLayout quantity_ll;
        TextView plus, minus, quantity;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.product_title);
            attribute = itemView.findViewById(R.id.tvProductQuantity);
            price = itemView.findViewById(R.id.product_price);
            currency = itemView.findViewById(R.id.product_currency);
            shopNow = itemView.findViewById(R.id.shop_now);
            progressBar = itemView.findViewById(R.id.progressbar);
            quantity_ll = itemView.findViewById(R.id.quantity_ll);
            quantity = itemView.findViewById(R.id.quantity);
            plus = itemView.findViewById(R.id.quantity_plus);
            minus = itemView.findViewById(R.id.quantity_minus);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
