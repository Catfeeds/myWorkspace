package me.suncloud.marrymemo.view.work_case;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljTrackedActivity;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ShareInfo;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class WorkMediaImageActivity extends HljTrackedActivity implements OverscrollContainer
        .OnLoadListener {

    @BindView(R.id.over_scroll_view_page)
    OverScrollViewPager overScrollViewPager;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.tv_images_count)
    TextView tvImagesCount;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;

    public static final String ARG_IS_LOAD = "is_load";
    public static final String ARG_LOAD_DISABLE = "disable_load";

    private ArrayList<Item> items;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private boolean loadDisable;
    private Work work;
    private int position;
    private int maxWidth;
    private int maxHeight;
    private int screenWidth;
    private int screenHeight;
    private MultiMediaPagerAdapter adapter;
    private Dialog dialog;
    private long shareItemId;
    private ShareUtil shareUtil;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    TrackerHelper.postShareAction(WorkMediaImageActivity.this,
                            work.getId(),
                            "set_meal");
                    new HljTracker.Builder(WorkMediaImageActivity.this).eventableId(work.getId())
                            .eventableType("Package")
                            .screen("package_detail")
                            .action("share")
                            .additional(HljShare.getShareTypeName(msg.what))
                            .build()
                            .add();

                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_media_image);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        Intent intent = getIntent();
        work = (Work) intent.getSerializableExtra("work");
        items = (ArrayList<Item>) intent.getSerializableExtra("items");
        if (CommonUtil.isCollectionEmpty(items) && work != null) {
            convertPhotosToItems();
        }
        position = intent.getIntExtra("position", 0);
        loadDisable = intent.getBooleanExtra(ARG_LOAD_DISABLE, false);

        screenWidth = CommonUtil.getDeviceSize(this).x;
        screenHeight = CommonUtil.getDeviceSize(this).y;
        maxWidth = Math.round(screenWidth * 3 / 2);
        maxHeight = Math.round(screenHeight * 5 / 2);

        adapter = new MultiMediaPagerAdapter(this);
    }

    private void convertPhotosToItems() {
        items = new ArrayList<>();
        ArrayList<Photo> photos = work.getDetailPhotos();
        for (Photo p : photos) {
            Item item = new Item();
            item.setId(p.getId());
            item.setKind(1);
            item.setMediaPath(p.getPath());

            items.add(item);
        }
    }

    @SuppressLint("ResourceType")
    private void initViews() {
        overScrollViewPager.setHintStringStart("滑动查看图文详情");
        overScrollViewPager.setHintStringEnd("释放查看图文详情");
        overScrollViewPager.setArrowImageResId(R.drawable.icon_arrow_left_round_white);
        overScrollViewPager.setHintTextColorResId(R.color.colorWhite);
        overScrollViewPager.setOverable(!loadDisable);
        overScrollViewPager.getOverscrollView()
                .setAdapter(adapter);
        overScrollViewPager.getOverscrollView()
                .setCurrentItem(position);
        tvImagesCount.setText(position + 1 + "/" + items.size());
        overScrollViewPager.setOnLoadListener(this);
        overScrollViewPager.getOverscrollView()
                .addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int p) {
                        position = p;
                        tvImagesCount.setText(position + 1 + "/" + items.size());
                        super.onPageSelected(position);
                    }
                });
    }

    @Override
    public void onLoad() {
        Intent intent = getIntent();
        intent.putExtra(ARG_IS_LOAD, true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_share)
    void onShare() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        Item item = items.get(position);
        shareItemId = item.getId();
        if (item.getShareInfo() != null) {
            shareUtil = new ShareUtil(this, item.getShareInfo(), progressBar, null);
        } else {
            String imgUrl = null;
            if (item.getKind() == 1) {
                imgUrl = item.getMediaPath();
            } else {
                if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                        .getScreenShot())) {
                    imgUrl = item.getPersistent()
                            .getScreenShot();
                }
                if (TextUtils.isEmpty(imgUrl)) {
                    imgUrl = item.getMediaPath() + HljCommon.QINIU.SCREEN_SHOT_URL_3_SECONDS;
                }
            }
            if (TextUtils.isEmpty(imgUrl)) {
                return;
            }
            ShareInfo shareInfo = work.getShareInfo(this);
            shareInfo.setIcon(imgUrl);
            shareUtil = new ShareUtil(this, shareInfo, progressBar, handler);
        }
        if (dialog == null) {
            dialog = Util.initShareDialog(this, shareUtil, null);
        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    handler.sendEmptyMessage(requestCode);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MultiMediaPagerAdapter extends PagerAdapter {

        private Context mContext;

        private MultiMediaPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext)
                    .inflate(com.hunliji.hljlivelibrary.R.layout.pics_page_item_view___img,
                            container,
                            false);
            final PhotoView image = view.findViewById(com.hunliji.hljlivelibrary.R.id.image);
            View playView = view.findViewById(com.hunliji.hljlivelibrary.R.id.play);
            final View progressBar = view.findViewById(com.hunliji.hljlivelibrary.R.id
                    .progress_bar);
            final Item media = items.get(position);

            String imagePath = null;

            if (media.getKind() == 2) {
                playView.setVisibility(View.VISIBLE);
                if (media.getMediaPath()
                        .startsWith("http")) {
                    if (media.getPersistent() != null && !TextUtils.isEmpty(media.getPersistent()
                            .getScreenShot())) {
                        imagePath = ImagePath.buildPath(media.getPersistent()
                                .getScreenShot())
                                .width(screenWidth)
                                .path();
                    } else {
                        imagePath = media.getMediaPath() + String.format(Constants.VIDEO_URL_TEN,
                                screenWidth)
                                .replace("|", JSONUtil.getURLEncoder());
                    }

                }
            } else {
                if (media.getMediaPath()
                        .startsWith("http")) {
                    imagePath = media.getMediaPath() + String.format(Constants.PHOTO_URL2,
                            maxWidth,
                            maxHeight);
                } else {
                    imagePath = media.getMediaPath();
                }
                playView.setVisibility(View.GONE);
            }
            Glide.with(WorkMediaImageActivity.this)
                    .load(ImagePath.buildPath(imagePath)
                            .width(maxWidth)
                            .height(maxHeight)
                            .path())
                    .apply(new RequestOptions().fitCenter()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .listener(new RequestListener<Drawable>() {
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
                            int x = image.getWidth();
                            int y = image.getHeight();
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();

                            float rate = (float) x / width;
                            int h = Math.round(height * rate);
                            if (h > y) {
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }
                            return false;
                        }
                    })
                    .into(image);

            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (media.getKind() == 2) {
                        String videoPath;
                        if (media.getMediaPath()
                                .startsWith("http")) {
                            videoPath = JSONUtil.getVideoPath(media.getPersistent());
                            if (TextUtils.isEmpty(videoPath)) {
                                videoPath = media.getMediaPath()
                                        .startsWith("http") ? media.getMediaPath() : Constants
                                        .HOST + media.getMediaPath();
                            }
                        } else {
                            videoPath = media.getMediaPath();
                        }
                        Intent intent = new Intent(view.getContext(), VideoPreviewActivity.class);
                        intent.putExtra("uri", Uri.parse(videoPath));
                        startActivity(intent);
                    } else {
                        onBackPressed();
                    }
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
