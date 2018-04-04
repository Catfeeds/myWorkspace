package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Suncloud on 2016/3/8.
 */
public class ChoosePhotoPageActivity extends HljBaseNoBarActivity {

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.selected_view)
    CheckableLinearLayout2 selectedView;
    @BindView(R.id.choose_ok)
    Button chooseOk;
    private ArrayList<String> list;
    private ArrayList<String> selectedPaths;
    private Point point;
    private int width;
    private int height;
    private int limit;
    private Toast toast;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        point = JSONUtil.getDeviceSize(this);
        width = point.x;
        height = point.y;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo_page);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        limit = getIntent().getIntExtra("limit", 0);
        list = getIntent().getStringArrayListExtra("images");
        getIntent().removeExtra("images");
        selectedPaths = getIntent().getStringArrayListExtra("selectedPaths");
        if (selectedPaths == null) {
            selectedPaths = new ArrayList<>();
        }
        if (selectedPaths.size() < 1) {
            chooseOk.setEnabled(false);
            chooseOk.setText(R.string.label_choose_ok);
        } else {
            chooseOk.setEnabled(true);
            chooseOk.setText(getString(R.string.label_choose_ok2, selectedPaths.size()));
        }
        int position = getIntent().getIntExtra("position", 0);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectedView.setChecked(selectedPaths.contains(list.get(position)));
            }
        });
        if (list != null && !list.isEmpty()) {
            selectedView.setChecked(selectedPaths.contains(list.get(0)));
        }
        mViewPager.setCurrentItem(position);
        selectedView.setOnCheckedChangeListener(new CheckableLinearLayout2.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                String imagePath = list.get(mViewPager.getCurrentItem());
                if (!checked) {
                    selectedPaths.remove(imagePath);
                } else if (!selectedPaths.contains(imagePath)) {
                    if (limit > 0 && selectedPaths.size() >= limit) {
                        if (toast == null) {
                            toast = Toast.makeText(ChoosePhotoPageActivity.this,
                                    R.string.hint_choose_photo_limit_out,
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                        }
                        toast.show();
                        selectedView.setChecked(false);
                        return;
                    }
                    selectedPaths.add(imagePath);
                }
                Intent intent = getIntent();
                intent.putExtra("selectedPaths", selectedPaths);
                setResult(RESULT_OK, intent);
                if (selectedPaths.size() < 1) {
                    chooseOk.setEnabled(false);
                    chooseOk.setText(R.string.label_choose_ok);
                } else {
                    chooseOk.setEnabled(true);
                    chooseOk.setText(getString(R.string.label_choose_ok2, selectedPaths.size()));
                }
            }
        });
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_anim_out);
    }

    public void onChooseOk(View view) {
        Intent intent = getIntent();
        intent.putExtra("done", true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }


    public class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = View.inflate(container.getContext(),
                    R.layout.thread_photos_view,
                    null);
            View progressBar = view.findViewById(R.id.progressBar);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.image);
            ImageLoadUtil.loadPageImage(ChoosePhotoPageActivity.this,
                    list.get(position),
                    R.mipmap.icon_empty_image,
                    photoView,
                    new ImageLoadRequest(photoView, progressBar),
                    width,
                    height);
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
