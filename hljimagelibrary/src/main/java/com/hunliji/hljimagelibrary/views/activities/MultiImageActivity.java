package com.hunliji.hljimagelibrary.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.PageImageRequestListener;
import com.hunliji.hljimagelibrary.views.widgets.HackyViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Suncloud on 2016/11/2.
 */

public class MultiImageActivity extends HljBaseNoBarActivity {
    private ArrayList<String> images;
    private int width;
    private int height;
    private HackyViewPager mViewPager;
    private TextView tvImageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_image___img);

        setSwipeBackEnable(false);
        View actionLayout = findViewById(R.id.action_layout);
        setActionBarPadding(actionLayout);

        Point point = CommonUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        setSwipeBackEnable(false);
        mViewPager = (HackyViewPager) findViewById(R.id.live_view_pager);
        tvImageCount = (TextView) findViewById(R.id.tv_images_count);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvImageCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        ArrayList<Photo> photos = intent.getParcelableArrayListExtra("photos");
        images = intent.getStringArrayListExtra("images");
        if (photos != null && !photos.isEmpty()) {
            images = new ArrayList<>();
            for (Photo photo : photos) {
                images.add(photo.getImagePath());
            }
        }
        int position = intent.getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setCurrentItem(position);
        tvImageCount.setText(position + 1 + "/" + images.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tvImageCount.setText(position + 1 + "/" + images.size());
                super.onPageSelected(position);
            }
        });

    }

    private class SamplePagerAdapter extends PagerAdapter {

        private Context mContext;

        private SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.pics_page_item_view___img, container, false);
            final PhotoView image = (PhotoView) view.findViewById(R.id.image);
            final View progressBar = view.findViewById(R.id.progress_bar);
            String imgUrl = images.get(position);
            if (TextUtils.isEmpty(imgUrl)) {
                Glide.with(MultiImageActivity.this)
                        .clear(image);
                image.setImageBitmap(null);
            } else {
                Glide.with(MultiImageActivity.this)
                        .load(ImagePath.buildPath(imgUrl)
                                .width(width)
                                .height(height)
                                .path())
                        .apply(new RequestOptions().fitCenter()
                                .placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .listener(new PageImageRequestListener(image, progressBar))
                        .into(image);

            }
            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    onBackPressed();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
