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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.essentials.customerapp.R;
import com.essentials.customerapp.firebase.models.CategoryModel;
import com.essentials.customerapp.firebase.models.SubCategoryModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.view.ui.activity.HomeActivity;
import com.essentials.customerapp.view.ui.activity.SubCategoryActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryModel> categoryList;
    Context context;
    private String Tag;
    private List<SubCategoryModel> subCategoryModelList;
    private SubCategoryAdapter subCategoryAdapter;
    private GridLayoutManager gridLayoutManager;


    public CategoryAdapter(Context context, String tag) {
        this.context = context;
        Tag = tag;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        if (Tag.equalsIgnoreCase("Bottom")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_home_category, parent, false);
        } else if (Tag.equalsIgnoreCase("Top")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_home_category1, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (Tag.equalsIgnoreCase("Bottom")) {
            ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) holder.cvCategory.getLayoutParams();
            if (position % 2 == 0)
                cardViewMarginParams.setMargins(12, 12, 12, 0);
            else
                cardViewMarginParams.setMargins(0, 12, 8, 0);
            holder.cvCategory.requestLayout();
        }

        final CategoryModel categoryModel = categoryList.get(position);
        holder.tvCategoryName.setText(categoryModel.getCat_Name());
        holder.tvDiscount.setText(categoryModel.getOffer());
        if (!categoryModel.getCat_Image_Url().isEmpty()) {
            Picasso.get()
                    .load(categoryModel.getCat_Image_Url())
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
                Intent intent = new Intent(context, SubCategoryActivity.class);
                intent.putExtra(Constants.CAT_ID, categoryModel.getCat_Id());
                intent.putExtra(Constants.CAT_NAME, categoryModel.getCat_Name());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((HomeActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubCategoryActivity.class);
                intent.putExtra(Constants.CAT_NAME, categoryModel.getCat_Id());
                intent.putExtra(Constants.CAT_ID, categoryModel.getCat_Name());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((HomeActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

    }

    private void subCatMakeVisible(RecyclerView rvSubCat, boolean b, String cat_id) {
        subCategoryAdapter = new SubCategoryAdapter(context);
        gridLayoutManager = new GridLayoutManager(context, 3);
        rvSubCat.setLayoutManager(gridLayoutManager);
        if (b) {
            rvSubCat.setVisibility(View.GONE);
        } else {
            rvSubCat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList != null && !categoryList.isEmpty() ? categoryList.size() : 0;
    }

    public void setCatDataList(List<CategoryModel> categoryModelList) {
        this.categoryList = categoryModelList;
        notifyDataSetChanged();
    }

    public void setSubCatDataList(List<SubCategoryModel> subCategoryModelList) {
        this.subCategoryModelList = subCategoryModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCatImage;
        //        ImageView ivClickArrow;
        TextView tvCategoryName, tvDiscount;
        //        TextView tvCategoryDescription;
        ProgressBar progressBar;
        CardView cvCategory;
        RecyclerView rvSubCat;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCatImage = itemView.findViewById(R.id.ivCatImage);
//            ivClickArrow = itemView.findViewById(R.id.ivClickArrow);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
//            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            progressBar = itemView.findViewById(R.id.progressbar);
            cvCategory = itemView.findViewById(R.id.cvCategory);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
//            rvSubCat = itemView.findViewById(R.id.rvSubCat);
        }
    }
}
