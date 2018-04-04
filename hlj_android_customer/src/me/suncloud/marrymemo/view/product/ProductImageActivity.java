package me.suncloud.marrymemo.view.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljsharelibrary.utils.ShareUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ProductImageActivity extends Activity implements OverscrollContainer.OnLoadListener {

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
    public static final String ARG_PRODUCT = "product";
    public static final String ARG_PHOTOS = "photos";
    public static final String ARG_POSITION = "position";

    private ArrayList<Photo> items;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private boolean loadDisable;
    private ShopProduct product;
    private int position;
    private int maxWidth;
    private int maxHeight;
    private int screenWidth;
    private int screenHeight;
    private MultiMediaPagerAdapter adapter;
    private ShareUtil shareUtil;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    trackerShare("QQZone");
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    trackerShare("QQ");
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    trackerShare("Timeline");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare("Session");
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
        product = intent.getParcelableExtra(ARG_PRODUCT);
        items = intent.getParcelableArrayListExtra(ARG_PHOTOS);
        if (CommonUtil.isCollectionEmpty(items) && product != null) {
            items = product.getHeaderPhotos();
        }
        position = intent.getIntExtra(ARG_POSITION, 0);
        loadDisable = intent.getBooleanExtra(ARG_LOAD_DISABLE, false);

        screenWidth = CommonUtil.getDeviceSize(this).x;
        screenHeight = CommonUtil.getDeviceSize(this).y;
        maxWidth = Math.round(screenWidth * 3 / 2);
        maxHeight = Math.round(screenHeight * 5 / 2);

        adapter = new MultiMediaPagerAdapter(this);
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
        Photo item = items.get(position);

        ShareInfo shareInfo = product.getShare();
        shareInfo.setIcon(item.getImagePath());
        ShareDialogUtil.onCommonShare(this, shareInfo, handler);

        shareUtil = new ShareUtil(this, shareInfo, handler);
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
            final View progressBar = view.findViewById(com.hunliji.hljlivelibrary.R.id
                    .progress_bar);
            final Photo media = items.get(position);

            String imagePath = media.getImagePath();

            Glide.with(ProductImageActivity.this)
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

    private void trackerShare(String shareInfo) {
        new HljTracker.Builder(this).eventableId(product.getId())
                .eventableType("Article")
                .action("share")
                .additional(shareInfo)
                .sid("AA1/A1")
                .pos(3)
                .desc("分享")
                .build()
                .send();
    }
}
