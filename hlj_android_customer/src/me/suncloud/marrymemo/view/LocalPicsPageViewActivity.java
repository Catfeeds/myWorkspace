package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.slider.library.Indicators.CirclePageIndicator;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by jinxin on 2016/5/17.
 */
public class LocalPicsPageViewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {


    private static ArrayList<Photo> photos;
    private Point point;
    private int width;
    private int height;
    private View actionLayout;

    private TextView title;
    private ViewPager mViewPager;
    private SamplePagerAdapter adapter;
    private ArrayList<Photo> deleteData;
    private Dialog deleteDialog;
    private View deleteIcon;
    private boolean isDeleted = false;//判断是否删除了照片
    //delete  true 有删除图片的功能
    private boolean delete;
    private boolean showActionLayout;
    private boolean showIndicator;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pics_page);
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x / 2);
        height = Math.round(point.y / 2);
        actionLayout = findViewById(R.id.action_layout);
        title = (TextView) findViewById(R.id.title);
        deleteIcon = findViewById(R.id.delete);
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.flow_indicator);
        Intent intent = getIntent();
        photos = (ArrayList<Photo>) intent.getSerializableExtra("photos");
        delete = getIntent().getBooleanExtra("delete", false);
        showActionLayout = getIntent().getBooleanExtra("showActionLayout", false);
        showIndicator = getIntent().getBooleanExtra("showIndicator", false);
        deleteIcon.setVisibility(delete ? View.VISIBLE : View.GONE);
        actionLayout.setVisibility(showActionLayout ? View.VISIBLE : View.GONE);
        indicator.setVisibility(showIndicator ? View.VISIBLE : View.GONE);
        int position = intent.getIntExtra("position", 0);
        title.setText(getTitle(position));
        adapter = new SamplePagerAdapter(this);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(position);
        if (photos.size() > 1) {
            indicator.setViewPager(mViewPager, position);
        } else {
            indicator.setVisibility(View.GONE);
        }

    }


    public String getTitle(int position) {
        if (photos == null) {
            return null;
        }
        position = photos.size() <= 0 ? position : position + 1;
        return position + "/" + photos.size();
    }


    public void onBackPressed(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (isDeleted) {
            Intent data = getIntent();
            data.putExtra("deleteData", deleteData);
            setResult(RESULT_OK, data);
        }
        super.onBackPressed();
    }

    public void onDelete(View view) {
        deleteDialog = DialogUtil.createDoubleButtonDialog(deleteDialog,
                this,
                getString(R.string.label_delete_photo),
                getString(R.string.action_cancel),
                getString(R.string.action_ok),
                new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                        deletePhoto();
                    }
                });

        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void deletePhoto() {
        int currentPosition = mViewPager.getCurrentItem();
        Photo photo = photos.get(currentPosition);
        if (deleteData == null) {
            deleteData = new ArrayList<>();
        }
        isDeleted = true;
        deleteData.add(photo);
        if (currentPosition < photos.size()) {
            photos.remove(currentPosition);
        }
        if (photos.size() <= 0) {
            onBackPressed(null);
        } else {
            adapter = new SamplePagerAdapter(this);
            mViewPager.setAdapter(null);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(Math.max(currentPosition - 1, 0));
            title.setText(getTitle(mViewPager.getCurrentItem()));
        }
    }

    @Override
    public void onPageScrolled(
            int position, float positionOffset, int positionOffsetPixels) {
        int currentPosition = mViewPager.getCurrentItem();
        title.setText(getTitle(currentPosition));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class SamplePagerAdapter extends PagerAdapter {

        private Context mContext;

        public SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return photos == null ? 0 : photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.thread_photos_view, container, false);
            Photo photo = photos.get(position);
            View progressBar = view.findViewById(R.id.progressBar);
            if (photo != null) {
                PhotoView image = (PhotoView) view.findViewById(R.id.image);
                ImageLoadUtil.loadPageImage(LocalPicsPageViewActivity.this,
                        photo.getPath(),
                        R.mipmap.icon_empty_image,
                        image,
                        new ImageLoadRequest(image, progressBar),
                        width,
                        height);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(
                ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private class ImageLoadRequest implements RequestListener<Drawable> {

        private PhotoView photoView;
        private View progressBar;

        private ImageLoadRequest(PhotoView photoView, View progressBar) {
            this.photoView = photoView;
            this.progressBar = progressBar;
        }

        @Override
        public boolean onLoadFailed(
                @Nullable GlideException e,
                Object model,
                Target<Drawable> target,
                boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(
                Drawable resource,
                Object model,
                Target<Drawable> target,
                DataSource dataSource,
                boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            float rate = (float) point.x / resource.getIntrinsicWidth();
            int w = point.x;
            int h = Math.round(resource.getIntrinsicHeight() * rate);
            if (h > point.y || (h * point.x > w * point.y)) {
                photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            return false;
        }
    }

}
