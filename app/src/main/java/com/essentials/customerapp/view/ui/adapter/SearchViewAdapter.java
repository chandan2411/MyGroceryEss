package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.models.ProductModel;
import com.essentials.customerapp.interfaces.SearchItemCLickListener;
import com.essentials.customerapp.models.CartModel;
import com.essentials.customerapp.view.ui.activity.SearchActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raj Aryan on 7/26/2020.
 * Mahiti Infotech
 * raj.aryan@mahiti.org
 */
public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.SearchLayout> {

    List<ProductModel> productModelList = new ArrayList<>();
    SearchItemCLickListener listener;

    public SearchViewAdapter(Context context) {
        listener = (SearchItemCLickListener) context;
    }

    @Override
    public SearchLayout onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);
        return new SearchLayout(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchLayout holder, int position) {
        ProductModel model = productModelList.get(position);
        holder.tvProductName.setText(model.getProduct_Name());
        setImageToView(holder, model);

        holder.llSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSearchItemClick(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productModelList.isEmpty() ? 0 : productModelList.size();
    }

    public void setDataList(List<ProductModel> dataList) {
        this.productModelList = dataList;
        notifyDataSetChanged();
    }

    public List<ProductModel> getDataList() {
        return productModelList;
    }

    public static class SearchLayout extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductCategory;
        LinearLayout llSearchItem;

        public SearchLayout(@NonNull View itemView) {
            super(itemView);
            llSearchItem = itemView.findViewById(R.id.llSearchItem);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
        }
    }

    private void setImageToView(SearchLayout holder, ProductModel model) {
        if (!model.getProduct_Image_Url().get(0).isEmpty()) {
            Picasso.get()
                    .load(model.getProduct_Image_Url().get(0))
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
}
