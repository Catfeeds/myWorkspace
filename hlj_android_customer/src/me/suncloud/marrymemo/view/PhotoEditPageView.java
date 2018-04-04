package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.widget.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoEditPageView extends HljBaseNoBarActivity {

    private static ArrayList<Photo> photos;
    private TextView description;
    private TextView currentView;
    private View descriptionView;
    private View descriptionHint;
    private Photo currentPhoto;
    private boolean isChange;
    private Point point;
    private int width;
    private int height;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        setActionBarPadding(findViewById(R.id.action_holder_layout));
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.pager);
        currentView = (TextView) findViewById(R.id.count);
        descriptionView = findViewById(R.id.description_layout);
        descriptionHint = findViewById(R.id.description_hint);
        description = (TextView) findViewById(R.id.description);
        description.setMovementMethod(ScrollingMovementMethod.getInstance());
        point = JSONUtil.getDeviceSize(this);
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        if (height > JSONUtil.getMaximumTextureSize() && JSONUtil.getMaximumTextureSize() > 0) {
            height = JSONUtil.getMaximumTextureSize();
        }
        Intent intent = getIntent();
        photos = (ArrayList<Photo>) intent.getSerializableExtra("photos");
        int position = intent.getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                menuChange(position);
            }
        });
        menuChange(position);
        mViewPager.setCurrentItem(position);
    }

    public void menuChange(int position) {
        currentView.setText(position + 1 + "/" + photos.size());
        currentPhoto = photos.get(position);
        if (!JSONUtil.isEmpty(currentPhoto.getDescription())) {
            description.setVisibility(View.VISIBLE);
            descriptionHint.setVisibility(View.GONE);
            description.setText(currentPhoto.getDescription());
        } else {
            descriptionHint.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
        }
        descriptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEdit();
            }
        });
        description.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEdit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            isChange = true;
            String des = data.getStringExtra("text");
            if (JSONUtil.isEmpty(des)) {
                description.setVisibility(View.GONE);
                descriptionHint.setVisibility(View.VISIBLE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(des);
                descriptionHint.setVisibility(View.GONE);
            }
            currentPhoto.setDescription(des);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void photoEdit() {
        Intent intent = new Intent(PhotoEditPageView.this, TextEditActivity.class);
        intent.putExtra("text", currentPhoto.getDescription());
        intent.putExtra("title", getString(R.string.title_activity_story_photo));
        intent.putExtra("textcount", 70);
        startActivityForResult(intent, Constants.RequestCode.PHOTO_TEXT_EDIT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        if (isChange) {
            Intent intent = getIntent();
            intent.putExtra("photos", photos);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
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
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.photo_edit_view, null, false);
            final View progressBar = view.findViewById(R.id.progressBar);
            final Photo photo = photos.get(position);
            final PhotoView image = (PhotoView) view.findViewById(R.id.image);
            View play = view.findViewById(R.id.play);
            progressBar.setVisibility(View.VISIBLE);
            ImageLoadTask task = new ImageLoadTask(image, new OnHttpRequestListener() {

                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
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
            if (photo.getType() == 1) {
                image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        if (photo.getType() != 1) {
                            return;
                        }
                        String videoPath = JSONUtil.getVideoPath(photo.getPersistent());
                        if (JSONUtil.isEmpty(videoPath)) {
                            if (!JSONUtil.isEmpty(photo.getVideoPath())) {
                                videoPath = photo.getVideoPath();
                            } else {
                                videoPath = photo.getLocalPath();
                            }
                        }
                        if (!JSONUtil.isEmpty(videoPath)) {
                            Intent intent = new Intent(mContext, VideoViewActivity.class);
                            intent.putExtra("path", videoPath);
                            startActivity(intent);
                        }

                    }
                });
                play.setVisibility(View.VISIBLE);
                if (!JSONUtil.isEmpty(photo.getVideoPath())) {
                    String imgUrl;
                    if (photo.getPersistent() != null && !JSONUtil.isEmpty(photo.getPersistent()
                            .getScreenShot())) {
                        imgUrl = JSONUtil.getImagePath(photo.getPersistent()
                                .getScreenShot(), point.x);
                    } else {
                        imgUrl = photo.getVideoPath() + String.format(Constants.VIDEO_URL, point.x)
                                .replace("|", JSONUtil.getURLEncoder());
                    }
                    image.setTag(imgUrl);
                    AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext
                            .getResources(),
                            R.mipmap.icon_empty_image,
                            task);
                    task.loadImage(imgUrl, width, ScaleMode.WIDTH, asyncDrawable);
                } else {
                    image.setTag(photo.getLocalPath());
                    task.loadThumbforPath(photo.getLocalPath());
                }
            } else {
                play.setVisibility(View.GONE);
                String path = null;
                if (!JSONUtil.isEmpty(photo.getPath())) {
                    path = photo.getPath() + String.format(Constants.PHOTO_URL2, width, height);
                }
                if (!JSONUtil.isEmpty(photo.getLocalPath())) {
                    path = photo.getLocalPath();
                }
                image.setTag(path);
                Bitmap placeholder = BitmapFactory.decodeResource(mContext.getResources(),
                        R.mipmap.icon_empty_image);
                AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext.getResources(),
                        placeholder,
                        task);
                task.loadImage(path, width, ScaleMode.WIDTH, asyncDrawable);
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

    }
}
