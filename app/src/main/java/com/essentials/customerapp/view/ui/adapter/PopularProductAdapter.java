package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.data.repository.CartRepository;
import com.essentials.customerapp.utilities.Constant;
import com.essentials.customerapp.utilities.CustomToast;
import com.google.gson.Gson;
import com.essentials.customerapp.R;
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

public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.MyViewHolder> {

    private List<ProductModel> productList;
    private Context context;
    private String Tag;
    private List<CartModel> cartModelList = new ArrayList<>();
    private String productUnitAdded, productSP, amountSubtotal;
    private MySharedPref sharedPref;
    private int itemChangePosition = -1;
    private CartRepository cartRepository;
    private int pQuantity;
    private String productAttribute;


    public PopularProductAdapter(List<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public PopularProductAdapter(Context context, String tag) {
        this.context = context;
        Tag = tag;
        sharedPref = new MySharedPref(context);
        cartRepository = CartRepository.getInstance();
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
                    .inflate(R.layout.row_popuular_products, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ProductModel product = productList.get(position);

        enableDisableView(holder, product);

        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();

        if (position == 0) {
            layoutParams.setMargins(15, 15, 10, 15);

        } else if ((productList.size() - 1) == position) {
            layoutParams.setMargins(10, 15, 15, 15);

        } else {
            layoutParams.setMargins(10, 15, 10, 15);

        }

        if (Double.parseDouble(product.getProduct_MRP()) > Double.parseDouble(product.getProduct_SP())) {
            holder.rlMRP.setVisibility(View.VISIBLE);
            holder.tvMRP.setText(product.getProduct_MRP());
        } else {
            holder.rlMRP.setVisibility(View.INVISIBLE);
        }
        holder.cardView.requestLayout();
        holder.title.setText(product.getProduct_Name());
        holder.tvSPCurrency.setText(product.getCurrency());
        holder.tvSP.setText(product.getProduct_SP());
        holder.attribute.setText(product.getProduct_Quantity_Type());

        holder.shopNow.setVisibility(View.VISIBLE);
        holder.llQuantityAddSub.setVisibility(View.GONE);

        String discount = "";

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

        setImageToView(product, holder);

        /*setting */
        if (cartModelList != null && !cartModelList.isEmpty()) {
            for (int i = 0; i < cartModelList.size(); i++) {
                if (product.getProduct_Id() == null || cartModelList.get(i).getProduct_Id() == null)
                    return;
                if (cartModelList.get(i).getProduct_Id().equalsIgnoreCase(product.getProduct_Id())) {
                    holder.shopNow.setVisibility(View.GONE);
                    holder.llQuantityAddSub.setVisibility(View.VISIBLE);
                    holder.tvQuantityAdded.setText("" + cartModelList.get(i).getProdctQuantityUnit());
                    Log.d("Tag : ", cartModelList.get(i).getProduct_Id() + "-->" + product.getProduct_Id());
                }/*else {
                    holder.shopNow.setVisibility(View.VISIBLE);
                    holder.llQuantityAddSub.setVisibility(View.GONE);
                    Log.d("Tag : ", cartModelList.get(i).getProduct_Id() + "-->" + product.getProduct_Id());

                }*/
            }
        } else {
            holder.shopNow.setVisibility(View.VISIBLE);
            holder.llQuantityAddSub.setVisibility(View.GONE);
        }

        holder.shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shopNow.setVisibility(View.GONE);
                holder.tvQuantityAdded.setText("1");
                holder.llQuantityAddSub.setVisibility(View.VISIBLE);
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

        holder.tvQuantityPlus.setOnClickListener(v -> {
            if (!cartModelList.isEmpty()) {
                int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                total_item++;
                holder.tvQuantityAdded.setText(total_item + "");
                incDecProductCount(product, holder, total_item, position);
            }
        });

        holder.minus.setOnClickListener(v -> {
            if (!cartModelList.isEmpty()) {
                pQuantity = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                if (pQuantity > 1) {
                    int total_item = Integer.parseInt(holder.tvQuantityAdded.getText().toString());
                    total_item--;
                    holder.tvQuantityAdded.setText(total_item + "");
                    incDecProductCount(product, holder, total_item, position);
                } else {
                    deleteProductFromCart(product, holder, position);
                }
            }
        });

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
//            intent.putExtra("Product", product);
            intent.putExtra("ProductId", product.getProduct_Id());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((HomeActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        });


    }

    private void deleteProductFromCart(ProductModel product, MyViewHolder holder, int position) {
        holder.shopNow.setVisibility(View.VISIBLE);
        holder.llQuantityAddSub.setVisibility(View.GONE);
        if (Constant.IS_USER_LOGIN) {
            cartRepository.deleteProduct(product.getProduct_Id());
            removeProduct(product.getProduct_Id());
        } else {
            if (cartModelList != null && !cartModelList.isEmpty())
                removeProduct(product.getProduct_Id());
            Gson gson = new Gson();
            String cartData = gson.toJson(cartModelList);
            sharedPref.writeString(PrefConstant.CART_ITEM, cartData);
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

    private void incDecProductCount(ProductModel product, MyViewHolder holder, int total_item, int position) {
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

    private void setImageToView(ProductModel product, MyViewHolder holder) {
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
    }

    @Override
    public int getItemCount() {

        return productList != null ? productList.size() : 0;

    }

    private void enableDisableView(MyViewHolder holder, ProductModel product) {
        if (product.getProduct_Out_Of_Stock().equalsIgnoreCase("true")) {
            holder.tvQuantityPlus.setEnabled(false);
            holder.minus.setEnabled(false);
            holder.tvOutOfStock.setVisibility(View.VISIBLE);
            holder.rlAddToCart.setVisibility(View.INVISIBLE);
            holder.cardView.setEnabled(false);
        } else {
            holder.tvQuantityPlus.setEnabled(true);
            holder.minus.setEnabled(true);
            holder.tvOutOfStock.setVisibility(View.INVISIBLE);
            holder.rlAddToCart.setVisibility(View.VISIBLE);
            holder.cardView.setEnabled(true);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDataList(List<ProductModel> productModelList) {
        this.productList = productModelList;
        notifyDataSetChanged();
    }

    public void setCartModelList(List<CartModel> cartModelList, boolean isCalledFromResume) {
        this.cartModelList = cartModelList;
        ((AddorRemoveCallbacks) context).onAddProduct(cartModelList.size());
        if (!isCalledFromResume) {
            if (itemChangePosition != -1)
                notifyItemChanged(itemChangePosition);
            else
                notifyDataSetChanged();
        } else
            notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, attribute, tvSP, tvSPCurrency, tvMRPCurrency, tvMRP, tvDiscount, tvQuantityPlus, minus, tvQuantityAdded, tvOutOfStock;
        RelativeLayout shopNow, llQuantityAddSub, rlAddToCart, rlMRP;
        ProgressBar progressBar;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            tvOutOfStock = itemView.findViewById(R.id.tvOutOfStock);
            rlAddToCart = itemView.findViewById(R.id.rlAddToCart);
            title = itemView.findViewById(R.id.product_title);
            attribute = itemView.findViewById(R.id.tvProductQuantity);
            tvSP = itemView.findViewById(R.id.tvSP);
            tvMRPCurrency = itemView.findViewById(R.id.tvMRPCurrency);
            tvSPCurrency = itemView.findViewById(R.id.tvSPCurrency);
            rlMRP = itemView.findViewById(R.id.rlMRP);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            shopNow = itemView.findViewById(R.id.rlAddView);
            progressBar = itemView.findViewById(R.id.progressbar);
            llQuantityAddSub = itemView.findViewById(R.id.quantity_ll);
            tvDiscount = itemView.findViewById(R.id.tvOFF);
            tvQuantityAdded = itemView.findViewById(R.id.quantity);
            tvQuantityPlus = itemView.findViewById(R.id.quantity_plus);
            minus = itemView.findViewById(R.id.quantity_minus);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
