package com.hunliji.hljimagelibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.PageImageRequestListener;
import com.hunliji.hljimagelibrary.utils.StaticImageList;
import com.hunliji.hljimagelibrary.views.widgets.CountSelectView;
import com.hunliji.hljimagelibrary.views.widgets.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Suncloud on 2016/3/8.
 */
public class ChoosePhotoPageActivity extends HljBaseNoBarActivity {

    private HackyViewPager mViewPager;
    private CountSelectView selectView;
    protected Button btnChooseOk;
    private List<String> photos;
    private ArrayList<String> selectedPaths;
    private int limit;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        width = Math.round(CommonUtil.getDeviceSize(this).x * 3 / 2);
        height = Math.round(CommonUtil.getDeviceSize(this).y * 5 / 2);
        super.onCreate(savedInstanceState);

        photos = StaticImageList.INSTANCE.getImagePaths();
        selectedPaths = new ArrayList<>(StaticImageList.INSTANCE.getSelectedPaths());

        setContentView(R.layout.activity_choose_photo_page___img);
        setActionBarPadding(findViewById(R.id.action_holder_layout));
        setSwipeBackEnable(false);
        mViewPager = findViewById(R.id.pager);
        selectView = findViewById(R.id.select_view);
        btnChooseOk = findViewById(R.id.btn_choose_ok);
        btnChooseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseOk();
            }
        });

        if(CommonUtil.isCollectionEmpty(photos)){
            finish();
            return;
        }

        limit = getIntent().getIntExtra("limit", 0);
        boolean countEnable = getIntent().getBooleanExtra("count_enable", false);
        int position = getIntent().getIntExtra("position", 0);

        if (selectedPaths == null) {
            selectedPaths = new ArrayList<>();
        }
        setChooseOkBtn(selectedPaths.size(), limit);
        selectView.setCountEnable(countEnable);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectView.setSelected(selectedPaths.indexOf(photos.get(position)));
            }
        });
        if (photos != null && !photos.isEmpty()) {
            selectView.setSelected(selectedPaths.indexOf(photos.get(position)));
        }
        mViewPager.setCurrentItem(position);
        selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photo = photos.get(mViewPager.getCurrentItem());
                if (selectedPaths.contains(photo)) {
                    selectedPaths.remove(photo);
                } else {
                    if (limit == 1 && selectedPaths.size() == 1) {
                        selectedPaths.set(0, photo);
                    } else {
                        if (limit > 0 && selectedPaths.size() >= limit) {
                            ToastUtil.showToast(ChoosePhotoPageActivity.this,
                                    null,
                                    R.string.msg_choose_photo_limit_out___img);
                            return;
                        }
                        selectedPaths.add(photo);
                    }
                }
                selectView.setSelected(selectedPaths.indexOf(photo));
                Intent intent = getIntent();
                intent.putExtra("selectedPaths", selectedPaths);
                setResult(RESULT_OK, intent);
                setChooseOkBtn(selectedPaths.size(), limit);
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

    public void onChooseOk() {
        Intent intent = getIntent();
        intent.putExtra("done", true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    public void setChooseOkBtn(int count, int limit) {
        btnChooseOk.setText(getString(R.string.btn_next___img,
                limit > 0 ? count + "/" + limit : String.valueOf(count)));
        btnChooseOk.setEnabled(count > 0 && (limit <= 0 || count <= limit));
    }


    public class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos == null ? 0 : photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = View.inflate(container.getContext(),
                    R.layout.pics_page_item_view___img,
                    null);
            View progressBar = view.findViewById(R.id.progress_bar);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.image);

            String path = photos.get(position);
            if (CommonUtil.isHttpUrl(path)) {
                path = ImagePath.buildPath(path)
                        .width(width)
                        .height(height)
                        .path();
            }
            Glide.with(ChoosePhotoPageActivity.this)
                    .load(path)
                    .apply(new RequestOptions().fitCenter())
                    .listener(new PageImageRequestListener(photoView, progressBar))
                    .into(photoView);
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

    @Override
    protected void onFinish() {
        StaticImageList.INSTANCE.onDestroy();
        super.onFinish();
    }
}
