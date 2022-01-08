package com.essentials.customerapp.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.essentials.customerapp.ProductActivity;
import com.essentials.customerapp.R;
import com.essentials.customerapp.models.OffersModel;
import com.essentials.customerapp.utilities.Constants;
import com.essentials.customerapp.view.ui.activity.HomeActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeSliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images;
    private List<OffersModel> offersModelList = new ArrayList<>();

    public HomeSliderAdapter(Context context) {
        this.context = context;
    }

    /*public HomeSliderAdapter(Context context, Integer[] images) {
        this.context = context;
        this.images = images;
    }*/


    @Override
    public int getCount() {
        return !offersModelList.isEmpty() ? offersModelList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final OffersModel offersModel = offersModelList.get(position);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_home_slider, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        final ProgressBar progressBar = view.findViewById(R.id.progressbar);


        if (!offersModel.getOffers_Image_Url().isEmpty()) {
            Picasso.get().load(offersModel.getOffers_Image_Url()).error(R.drawable.no_image).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("Error : ", e.getMessage());
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offersModel.getOffer_Code().contains("Buy") || offersModel.getOffer_Code().contains("OFF")) {
                    Intent intent = new Intent(context, ProductActivity.class);
                    intent.putExtra(Constants.COME_FROM_HOME, Constants.OFFERS);
                    intent.putExtra(Constants.SEARCH_TEXT, "");
                    intent.putExtra(Constants.OFFER_ID, offersModel.getOffer_ID());
                    intent.putExtra(Constants.OFFER_NAME, offersModel.getOffer_Name());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    ((HomeActivity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    public void setDataList(List<OffersModel> offersModelList) {
        this.offersModelList = offersModelList;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}