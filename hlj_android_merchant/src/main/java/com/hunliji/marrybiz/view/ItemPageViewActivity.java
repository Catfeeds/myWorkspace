package com.hunliji.marrybiz.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.text.method.ScrollingMovementMethod;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Item;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.widget.HackyViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ItemPageViewActivity extends BaseActivity {
    private static ArrayList<Item> items;
    private Point point;
    private int width;
    private int height;
    private TextView currentView;
    private TextView descriptionView;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.pager);
        currentView = (TextView) findViewById(R.id.count);
        descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setMovementMethod(ScrollingMovementMethod.getInstance());
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        if (height > JSONUtil.getMaximumTextureSize()) {
            height = JSONUtil.getMaximumTextureSize();
        }
        Intent intent = getIntent();
        items = (ArrayList<Item>) intent.getSerializableExtra("items");
        int position = intent.getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setCurrentItem(position);
        menuChange(position);
        mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                menuChange(position);
            }
        });
    }

    public void menuChange(int position) {
        currentView.setText(position + 1 + "/" + items.size());
        Item currentItem = items.get(position);
        if (!JSONUtil.isEmpty(currentItem.getDescription())) {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(currentItem.getDescription());
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }


    public class SamplePagerAdapter extends PagerAdapter {

        PointF pointdown = new PointF();
        PointF pointup = new PointF();
        private Context mContext;

        public SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.story_item_view, null, false);
            final Item item = items.get(position);
            final ViewHolder holder = new ViewHolder();
            holder.image = (PhotoView) view.findViewById(R.id.image);
            holder.play = view.findViewById(R.id.play);
            holder.progressBar = view.findViewById(R.id.progressBar);
            holder.textItemDescription = (TextView) view.findViewById(R.id.text);

            holder.progressBar.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
            ImageLoadTask task = new ImageLoadTask(holder.image, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    holder.progressBar.setVisibility(View.GONE);
                    Bitmap bitmap = (Bitmap) obj;
                    float rate = (float) point.x / bitmap.getWidth();
                    int w = point.x;
                    int h = Math.round(bitmap.getHeight() * rate);
                    if (h > point.y || (h * point.x > w * point.y)) {
                        holder.image.setScaleType(ScaleType.CENTER_CROP);
                    } else {
                        holder.image.setScaleType(ScaleType.FIT_CENTER);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            });
            if (item.getKind() == 2) {
                holder.image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        if (!JSONUtil.isEmpty(item.getMediaPath()) && item.getKind() == 2) {
                            String videoPath;
                            if (item.getMediaPath()
                                    .startsWith("http")) {
                                videoPath = JSONUtil.getVideoPath(item.getPersistent());
                                if (JSONUtil.isEmpty(videoPath)) {
                                    videoPath = item.getMediaPath()
                                            .startsWith("http") ? item.getMediaPath() : Constants
                                            .HOST + item.getMediaPath();
                                }
                            } else {
                                videoPath = item.getMediaPath();
                            }
                            Intent intent = new Intent(mContext, VideoViewActivity.class);
                            intent.putExtra("path", videoPath);
                            startActivity(intent);
                        }

                    }
                });
                holder.play.setVisibility(View.VISIBLE);
                if (item.getMediaPath()
                        .startsWith("http")) {
                    String imgUrl;
                    if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                            .getScreenShot())) {
                        imgUrl = JSONUtil.getImagePath(item.getPersistent()
                                .getScreenShot(), point.x);
                    } else {
                        imgUrl = item.getMediaPath() + String.format(Constants.VIDEO_URL_TEN,
                                point.x)
                                .replace("|", JSONUtil.getURLEncoder());
                    }
                    holder.image.setTag(imgUrl);
                    AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext
                            .getResources(),
                            R.mipmap.icon_empty_image,
                            task);
                    task.loadImage(imgUrl, width, ScaleMode.WIDTH, asyncDrawable);
                } else {
                    holder.play.setVisibility(View.VISIBLE);
                    holder.image.setTag(item.getMediaPath());
                    task.loadThumbforPath(item.getMediaPath());
                }
            } else if (item.getKind() == 1) {
                String imgUrl;
                holder.play.setVisibility(View.GONE);
                imgUrl = item.getMediaPath();
                if (imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
                    imgUrl = imgUrl + String.format(Constants.PHOTO_URL2, width, height);
                }
                holder.image.setTag(imgUrl);
                AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext.getResources(),
                        R.mipmap.icon_empty_image,
                        task);
                task.loadImage(imgUrl, width, ScaleMode.WIDTH, asyncDrawable);
            }
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

        private float spacing(float x1, float y1, float x2, float y2) {
            float x = x1 - x2;
            float y = y1 - y2;
            return (float) Math.sqrt(x * x + y * y);
        }

        private class ViewHolder {
            TextView textItemDescription;
            PhotoView image;
            View progressBar;
            View play;
        }

    }

}
