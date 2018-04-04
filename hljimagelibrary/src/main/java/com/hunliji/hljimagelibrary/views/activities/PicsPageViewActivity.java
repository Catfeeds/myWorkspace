package com.hunliji.hljimagelibrary.views.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
 * Created by werther on 16/8/3.
 */
public class PicsPageViewActivity extends HljBaseNoBarActivity {
    private ArrayList<String> images;
    private int width;
    private int height;
    private HackyViewPager mViewPager;
    private TextView tvCount;

    public final static String ARG_PHOTOS = "photos";
    public final static String ARG_IMAGES = "images";
    public final static String ARG_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_page_view___img);

        setSwipeBackEnable(false);
        View actionLayout = findViewById(R.id.action_layout);
        setActionBarPadding(actionLayout);

        Point point = CommonUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        FrameLayout bottomLayout = (FrameLayout) findViewById(R.id.bottom_layout);
        mViewPager = (HackyViewPager) findViewById(R.id.pager);
        tvCount = (TextView) findViewById(R.id.tv_count);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        ArrayList<Photo> photos = intent.getParcelableArrayListExtra(ARG_PHOTOS);
        images = intent.getStringArrayListExtra(ARG_IMAGES);
        if (photos != null && !photos.isEmpty()) {
            images = new ArrayList<>();
            for (Photo photo : photos) {
                images.add(photo.getImagePath());
            }
        }
        int position = intent.getIntExtra(ARG_POSITION, 0);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(position);
        tvCount.setText(position + 1 + "/" + images.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvCount.setText(position + 1 + "/" + images.size());
            }
        });

        initBottomView(bottomLayout);
    }

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.pics_page_item_view___img, container, false);
            final PhotoView image = (PhotoView) view.findViewById(R.id.image);
            final View progressBar = view.findViewById(R.id.progress_bar);
            String imgUrl = images.get(position);
            Glide.with(PicsPageViewActivity.this)
                    .load(ImagePath.buildPath(imgUrl)
                            .width(width)
                            .height(height)
                            .path())
                    .apply(new RequestOptions().fitCenter()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .listener(new PageImageRequestListener(image, progressBar))
                    .into(image);
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

    public void initBottomView(FrameLayout bottomLayout) {
    }
}
