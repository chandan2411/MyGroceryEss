package com.essentials.customerapp.view.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.essentials.customerapp.R;
import com.essentials.customerapp.view.ui.adapter.CustomPagerAdapterWithZoom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout llImageViewParent;
    ImageView ivClose;
    List<String> imageUrlList = new ArrayList<>();
    private CustomPagerAdapterWithZoom pagerAdapter;
    private ImageView[] imaegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        viewPager = findViewById(R.id.viewpager);
        llImageViewParent = findViewById(R.id.llDot);
        ivClose = findViewById(R.id.ivClose);
        imageUrlList = getIntent().getStringArrayListExtra("ImageUrlList");
        setSlidingImageView();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setSlidingImageView() {
        pagerAdapter = new CustomPagerAdapterWithZoom(ImageViewerActivity.this);
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.setDataList(imageUrlList);
        addBorderToImageView(0);
//        viewPager.setPageMargin(20);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addBorderToImageView(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void addBorderToImageView(int page_position) {
        imaegView = new ImageView[imageUrlList.size()];
        llImageViewParent.removeAllViews();

        for (int i = 0; i < imaegView.length; i++) {
            imaegView[i] = new ImageView(this);
            setImage(imaegView, i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, 120);
            lp.setMargins(10, 10, 10, 10);
            imaegView[i].setPadding(10,10,10,10);
            imaegView[i].setLayoutParams(lp);
            llImageViewParent.addView(imaegView[i]);
        }
        //active dot
        imaegView[page_position].setBackground(getResources().getDrawable(R.drawable.shop_button_border));
    }

    private void setImage(ImageView[] imaegView, int i) {
        Picasso.get()
                .load(imageUrlList.get(i)).placeholder(R.drawable.no_image)
                .into(imaegView[i]);
    }


}
