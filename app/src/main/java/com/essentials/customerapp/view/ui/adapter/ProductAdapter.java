package com.essentials.customerapp.view.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.utilities.CustomToast;
import com.essentials.customerapp.view.ui.activity.ProductListingActivity;
import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.utilities.Constant;
import com.google.gson.Gson;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.interfaces.AddorRemoveCallbacks;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.utilities.AppUtils;
import com.essentials.customerapp.utilities.MySharedPref;
import com.essentials.customerapp.utilities.PrefConstant;
import com.essentials.customerapp.view.ui.activity.BaseActivity;
import com.essentials.customerapp.view.ui.activity.ProductDetailsActivity;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    String Tag;
    int pQuantity = 1;
    Gson gson;
    private List<CartModel> cartModelList = new ArrayList<>();
    private List<ProductModel> productModelList = new ArrayList<>();
    private MySharedPref sharedPref;
    private int itemChangePosition;
    private CartRepository cartRepository;

    private String productUnitAdded, productSP, productAttribute, amountSubtotal, productMRP;


    public ProductAdapter(Context context) {
        this.context = context;
        sharedPref = new MySharedPref(context);
        cartRepository = CartRepository.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_products, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProductModel product = productModelList.get(position);

        holder.title.setText(product.getProduct_Name());
        holder.tvAttribute.setText(product.getProduct_Quantity_Type());
        cartModelList = ((BaseActivity) context).getCartList();

        holder.shopNow.setVisibility(View.VISIBLE);
        holder.llAddMinus.setVisibility(View.GONE);


        productSP = product.getProduct_SP();
        productMRP = product.getProduct_MRP();
        try {
            if (Double.parseDouble(productMRP) > Double.parseDouble(productSP)) {
                holder.rlMRP.setVisibility(View.VISIBLE);
                holder.tvMRP.setText(productMRP);
            } else {
                holder.rlMRP.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){

        }
        /*If MRP is greater than SP show MRP view*/


        /*Product discount*/
        if (product.getProductDiscount().isEmpty()) {
            int dis = AppUtils.calculateDiscount(product.getProduct_MRP(), product.getProduct_SP());
            if (dis >= 5) {
                product.setProductDiscount(dis + "% OFF");
            } else {
                product.setProductDiscount("0");
            }
        }

        /*Show product discount view and mrp view*/
        if (product.getProductDiscount().equals("0")) {
            holder.tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDiscount.setText(product.getProductDiscount());
            holder.tvDiscount.setVisibility(View.VISIBLE);
        }

        holder.tvSP.setText(productSP);
        setImageToView(holder, product);
        enableDisableView(holder, product);


        /*Add Product to cart*/
        holder.shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shopNow.setVisibility(View.GONE);
                holder.llAddMinus.setVisibility(View.VISIBLE);
                holder.tvQuantityAdded.setText("1");
                productSP = product.getProduct_SP();
                productUnitAdded = holder.tvQuantityAdded.getText().toString();
                productAttribute = product.getProduct_Quantity_Type();
                amountSubtotal = String.valueOf(Double.parseDouble(productSP) * Integer.parseInt(productUnitAdded));
                CartModel cartModel = new CartModel(product, productUnitAdded, amountSubtotal, AppUtils.dateAndTime());
                cartModelList.add(cartModel);
                if (Constant.IS_USER_LOGIN) {
                    cartRepository.addProductToCart(cartModel);
                    Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartModelList);
                    sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                    Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                }
                itemChangePosition = position;
                ((BaseActivity) context).setCartList(cartModelList);
                ((AddorRemoveCallbacks) context).onAddProduct(cartModelList.size());
            }
        });

        /*add button to increase no of item count*/
        holder.tvQuantityPlus.setOnClickListener(v -> {
            if (!cartModelList.isEmpty()) {
                int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                total_item++;
                holder.tvQuantityAdded.setText(total_item + "");
                incDecProductCount(product, holder, total_item);
            }
        });

        holder.tvQuantityMinus.setOnClickListener(v -> {
            if (!cartModelList.isEmpty()) {
                pQuantity = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                if (pQuantity > 1) {
                    int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                    total_item--;
                    holder.tvQuantityAdded.setText(total_item + "");
                    incDecProductCount(product, holder, total_item);
                } else {
                    deleteProductFromCart(product, holder);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!product.getProduct_Out_Of_Stock().equalsIgnoreCase("true")) {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
//                    intent.putExtra("Product", product);
                    intent.putExtra("ProductId", product.getProduct_Id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });

    }

    private void deleteProductFromCart(ProductModel product, MyViewHolder holder) {
        holder.shopNow.setVisibility(View.VISIBLE);
        holder.llAddMinus.setVisibility(View.GONE);
        if (Constant.IS_USER_LOGIN) {
            cartRepository.deleteProduct(product.getProduct_Id());
            removeProduct(product.getProduct_Id());
                    /*.observe((AppCompatActivity) context, responseModel -> {
                        if (responseModel != null) {
                            if (!cartModelList.isEmpty())
                                removeProduct(product.getProduct_Id());
                            notifyDataSetChanged();
                        }
                    });*/
        } else {
            if (!cartModelList.isEmpty())
                removeProduct(product.getProduct_Id());
            Gson gson = new Gson();
            String cartData = gson.toJson(cartModelList);
            sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
            ((BaseActivity) context).setCartList(cartModelList);
            notifyDataSetChanged();
        }

    }

    private void removeProduct(String product_id) {
        List<CartModel> cartRemoveLis = new ArrayList<>();

        for (CartModel cartModel : cartModelList) {
            if (cartModel.getProduct_Id().equalsIgnoreCase(product_id)) {
                cartRemoveLis.add(cartModel);
            }
        }
        cartModelList.removeAll(cartRemoveLis);
        ((BaseActivity) context).setCartList(cartModelList);
        ((AddorRemoveCallbacks) context).onAddProduct(cartModelList.size());

    }


    private void setImageToView(MyViewHolder holder, ProductModel cartModel) {
        if (!cartModel.getProduct_Image_Url().get(0).isEmpty()) {
            Picasso.get()
                    .load(cartModel.getProduct_Image_Url().get(0))
                    .into(holder.productImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Error : ", e.getMessage());
                        }
                    });
        } else {
            holder.productImage.setImageResource(R.drawable.no_image);
        }
    }


    private void enableDisableView(MyViewHolder holder, ProductModel product) {
        if (product.getProduct_Out_Of_Stock().equalsIgnoreCase("true")) {
            holder.tvOutOfStock.setVisibility(View.VISIBLE);
            holder.shopNow.setVisibility(View.GONE);
            holder.llAddMinus.setVisibility(View.GONE);
            holder.cardView.setEnabled(false);
        } else {
            holder.tvOutOfStock.setVisibility(View.GONE);
            holder.cardView.setEnabled(true);
            showShopNowView(holder, product);

        }
    }

    private void showShopNowView(MyViewHolder holder, ProductModel product) {
        /*Show Product count if already added to cart*/
        if (!cartModelList.isEmpty()) {
            for (int i = 0; i < cartModelList.size(); i++) {
                if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                    holder.shopNow.setVisibility(View.GONE);
                    holder.llAddMinus.setVisibility(View.VISIBLE);
                    holder.tvQuantityAdded.setText("" + cartModelList.get(i).getProdctQuantityUnit());
                    Log.d("Tag : ", cartModelList.get(i).getProduct_Id() + "-->" + product.getProduct_Id());
                }/* else {
                    holder.shopNow.setVisibility(View.VISIBLE);
                    holder.llAddMinus.setVisibility(View.GONE);
                    Log.d("Tag : ", cartModelList.get(i).getProduct_Id() + "-->" + product.getProduct_Id());
                }*/
            }
        } else {
            holder.shopNow.setVisibility(View.VISIBLE);
            holder.llAddMinus.setVisibility(View.GONE);
        }
    }

    private void incDecProductCount(ProductModel product, MyViewHolder holder, int total_item) {
        for (int i = 0; i < this.cartModelList.size(); i++) {
            if (this.cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                amountSubtotal = String.valueOf(Double.parseDouble(product.getProduct_SP()) * total_item);
                cartModelList.get(i).setProdctQuantityUnit(total_item);
                cartModelList.get(i).setSub_Total(amountSubtotal);
                /*Update product count in cart firebase db*/
                ((BaseActivity) context).setCartList(cartModelList);
                if (Constant.IS_USER_LOGIN) {
                    cartRepository.addProductToCart(cartModelList.get(i));
                } else {
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartModelList);
                    sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
                }
            }
        }
    }


    public void setCartModelList(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDataList(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView title;
        TextView tvMRP, tvOutOfStock;
        RelativeLayout rlMRP, shopNow;
        LinearLayout llAddMinus;
        CardView cardView;
        TextView tvDiscount, tvSP, tvQuantityAdded, tvAttribute;
        TextView tvQuantityPlus, tvQuantityMinus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            tvSP = itemView.findViewById(R.id.tvSP);
            rlMRP = itemView.findViewById(R.id.rlMRP);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);

            title = itemView.findViewById(R.id.product_title);
            tvAttribute = itemView.findViewById(R.id.tvProductQuantity);
            llAddMinus = itemView.findViewById(R.id.quantity_ll);
            tvOutOfStock = itemView.findViewById(R.id.tvOutOfStock);
            shopNow = itemView.findViewById(R.id.rlAddView);
            cardView = itemView.findViewById(R.id.card_view);
            tvQuantityAdded = itemView.findViewById(R.id.tvQuantityAdded);

            tvQuantityPlus = itemView.findViewById(R.id.tvQuantityPlus);
            tvQuantityMinus = itemView.findViewById(R.id.tvQuantityMinus);
        }
    }
}
