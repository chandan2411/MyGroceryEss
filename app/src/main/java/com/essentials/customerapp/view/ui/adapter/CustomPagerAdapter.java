package com.essentials.customerapp.view.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.essentials.customerapp.view.ui.activity.ImageViewerActivity;
import com.essentials.customerapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {
    Context context;
    List<String> imageUrlList = new ArrayList<>();
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;

    public CustomPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, container, false);
        imageView = view.findViewById(R.id.image);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        ImageView zoomableimage = view.findViewById(R.id.zoomableimage);
        imageView.setVisibility(View.VISIBLE);
        if (context instanceof ImageViewerActivity) {
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mScaleGestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putStringArrayListExtra("ImageUrlList", (ArrayList<String>) imageUrlList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
        setImageToView(imageView, imageUrlList.get(position), zoomableimage);
//        imageView.setBackgroundResource(imageUrlList.get(position));
        container.addView(view);


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public void setDataList(List<String> product_image_url) {
        this.imageUrlList = product_image_url;
        notifyDataSetChanged();
    }

    private void setImageToView(ImageView imageView, String imageUrl, ImageView zoomableimage) {
        if (!imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).error(R.drawable.no_image).into(imageView);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

}
