package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.slider.library.Indicators.CirclePageIndicator;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ThreadPicsPageViewActivity extends FragmentActivity {

    private static ArrayList<Photo> photos;
    private Point point;
    private int width;
    private int height;
    private boolean isLocal;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pics_page);
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        getWindowManager().getDefaultDisplay()
                .getSize(point);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.flow_indicator);
        Intent intent = getIntent();
        photos = (ArrayList<Photo>) intent.getSerializableExtra("photos");
        int position = intent.getIntExtra("position", 0);
        isLocal = intent.getBooleanExtra("is_local", false);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setCurrentItem(position);
        if (photos.size() > 1) {
            indicator.setViewPager(mViewPager, position);
        } else {
            indicator.setVisibility(View.GONE);
        }

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
                    .inflate(R.layout.thread_photos_view, null, false);
            view.findViewById(R.id.progressBar)
                    .setVisibility(View.VISIBLE);
            final Photo photo = photos.get(position);
            final View play = view.findViewById(R.id.play);
            final PhotoView image = (PhotoView) view.findViewById(R.id.image);
            ImageLoadTask task = new ImageLoadTask(image, new OnHttpRequestListener() {

                @Override
                public void onRequestCompleted(Object obj) {
                    view.findViewById(R.id.progressBar)
                            .setVisibility(View.GONE);
                    Bitmap bitmap = (Bitmap) obj;
                    float rate = (float) point.x / bitmap.getWidth();
                    int w = point.x;
                    int h = Math.round(bitmap.getHeight() * rate);
                    if (h > point.y || (h * point.x > w * point.y)) {
                        image.setScaleType(ScaleType.CENTER_CROP);
                    } else {
                        image.setScaleType(ScaleType.FIT_CENTER);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            });
            String imgUrl;
            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (!JSONUtil.isEmpty(photo.getPath()) && photo.getKind() == 3) {
                        String videoPath = JSONUtil.getVideoPath(photo.getPersistent());
                        if (JSONUtil.isEmpty(videoPath)) {
                            videoPath = photo.getPath()
                                    .startsWith("http") ? photo.getPath() : Constants.HOST +
                                    photo.getPath();
                        }
                        Intent intent = new Intent(mContext, VideoViewActivity.class);
                        intent.putExtra("path", videoPath);
                        startActivity(intent);
                    } else if (photo.getKind() == 2) {
                        onBackPressed();
                    }

                }
            });
            if (photo.getKind() == 3) {
                play.setVisibility(View.VISIBLE);
                if (photo.getPersistent() != null && !JSONUtil.isEmpty(photo.getPersistent()
                        .getScreenShot())) {
                    imgUrl = JSONUtil.getImagePath(photo.getPersistent()
                            .getScreenShot(), point.x);
                } else {
                    imgUrl = photo.getPath() + String.format(Constants.VIDEO_URL, point.x)
                            .replace("|", JSONUtil.getURLEncoder());
                }
            } else {
                play.setVisibility(View.GONE);
                if (isLocal) {
                    imgUrl = photo.getPath();
                } else {
                    imgUrl = JSONUtil.getImagePathForWxH(photo.getPath(), width, height);
                }
            }
            if (isLocal) {
                ImageLoadUtil.loadImageView(mContext, imgUrl, image);
                view.findViewById(R.id.progressBar)
                        .setVisibility(View.GONE);
            } else {
                image.setTag(imgUrl);
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

    }
}
