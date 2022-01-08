package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.view.ui.activity.ProductListingActivity;
import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.models.SubCategoryModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.view.ui.activity.SubCategoryActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private List<SubCategoryModel> subCategoryModelList1;
    Context context;

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_sub_cat, parent, false);
        return new SubCategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryAdapter.MyViewHolder holder, int position) {

        final SubCategoryModel categoryModel = subCategoryModelList1.get(position);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cvCategory.getLayoutParams();
        if (position == subCategoryModelList1.size() - 1) {
            holder.cvFolks.setVisibility(View.VISIBLE);
        } else {
            holder.cvFolks.setVisibility(View.GONE);
        }
        holder.tvCategoryName.setText(categoryModel.getSub_Cat_Name());
        holder.tvCategoryDescription.setText(categoryModel.getSub_Cat_Details());
        holder.tvDiscount.setText(categoryModel.getOffer());
        if (!categoryModel.getSub_Cat_Image_Url().isEmpty()) {
            Picasso.get()
                    .load(categoryModel.getSub_Cat_Image_Url())
                    .into(holder.ivCatImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.ivCatImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Error : ", e.getMessage());
                            holder.progressBar.setVisibility(View.GONE);
                            holder.ivCatImage.setVisibility(View.VISIBLE);
                            holder.ivCatImage.setImageResource(R.drawable.no_image);
                        }
                    });
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.ivCatImage.setVisibility(View.VISIBLE);
            holder.ivCatImage.setImageResource(R.drawable.no_image);
        }


        holder.cvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListingActivity.class);
                intent.putExtra(Constants.COME_FROM_HOME, Constants.SUBCAT);
                intent.putExtra(Constants.CAT_ID, categoryModel.getSub_Cat_Id());
                intent.putExtra(Constants.CAT_NAME, categoryModel.getSub_Cat_Name());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((SubCategoryActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

    }


    @Override
    public int getItemCount() {
        if (subCategoryModelList1 != null) {
            return subCategoryModelList1.size();
        } else
            return 0;

    }

    public void setDataList(List<SubCategoryModel> categoryModelList) {
        this.subCategoryModelList1 = categoryModelList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvFolks;
        ImageView ivCatImage;
        ImageView ivClickArrow;
        TextView tvCategoryName, tvDiscount;
        TextView tvCategoryDescription;
        ProgressBar progressBar;
        CardView cvCategory;
        RecyclerView rvSubCat;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cvFolks = itemView.findViewById(R.id.cvFolks);
            ivCatImage = itemView.findViewById(R.id.ivCatImage);
            ivClickArrow = itemView.findViewById(R.id.ivClickArrow);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            progressBar = itemView.findViewById(R.id.progressbar);
            cvCategory = itemView.findViewById(R.id.cvCategory);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
//            rvSubCat = itemView.findViewById(R.id.rvSubCat);
        }
    }
}
