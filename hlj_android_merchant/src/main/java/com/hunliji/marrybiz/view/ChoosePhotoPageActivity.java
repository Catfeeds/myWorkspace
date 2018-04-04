package com.hunliji.marrybiz.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.widget.CheckableLinearLayout2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Suncloud on 2016/3/8.
 */
public class ChoosePhotoPageActivity extends Activity {

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.selected_view)
    CheckableLinearLayout2 selectedView;
    @BindView(R.id.choose_ok)
    Button chooseOk;
    private ArrayList<Photo> list;
    private ArrayList<String> selectedPaths;
    private Point point;
    private int width;
    private int limit;
    private Toast toast;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        point = JSONUtil.getDeviceSize(this);
        width = point.x;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo_page);
        ButterKnife.bind(this);
        limit = getIntent().getIntExtra("limit", 0);
        list = (ArrayList<Photo>) getIntent().getSerializableExtra("images");
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
        if (list != null && !list.isEmpty() && JSONUtil.isEmpty(list.get(0)
                .getImagePath())) {
            list.remove(0);
            position--;
        }
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Photo image = list.get(position);
                selectedView.setChecked(selectedPaths.contains(image.getImagePath()));
            }
        });
        if (list != null && !list.isEmpty()) {
            selectedView.setChecked(selectedPaths.contains(list.get(0)
                    .getImagePath()));
        }
        mViewPager.setCurrentItem(position);
        selectedView.setOnCheckedChangeListener(new CheckableLinearLayout2.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                Photo image = list.get(mViewPager.getCurrentItem());
                if (!checked) {
                    selectedPaths.remove(image.getImagePath());
                } else if (!selectedPaths.contains(image.getImagePath())) {
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
                    selectedPaths.add(image.getImagePath());
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

        private Context mContext;

        public SamplePagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.thread_photos_view, null, false);
            view.findViewById(R.id.progressBar)
                    .setVisibility(View.VISIBLE);
            Photo image = list.get(position);
            final PhotoView photoView = (PhotoView) view.findViewById(R.id.image);
            ImageLoadTask task = new ImageLoadTask(photoView, new OnHttpRequestListener() {

                @Override
                public void onRequestCompleted(Object obj) {
                    view.findViewById(R.id.progressBar)
                            .setVisibility(View.GONE);
                    Bitmap bitmap = (Bitmap) obj;
                    float rate = (float) point.x / bitmap.getWidth();
                    int w = point.x;
                    int h = Math.round(bitmap.getHeight() * rate);
                    if (h > point.y || (h * point.x > w * point.y)) {
                        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            });
            photoView.setTag(image.getImagePath());
            AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(mContext.getResources(),
                    R.mipmap.icon_empty_image,
                    task);
            task.loadImage(image.getImagePath(), width, ScaleMode.WIDTH, asyncDrawable);
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
