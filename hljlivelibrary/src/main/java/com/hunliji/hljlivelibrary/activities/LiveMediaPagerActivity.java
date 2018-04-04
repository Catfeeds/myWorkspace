package com.hunliji.hljlivelibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteSpot;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljTrackedActivity;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.widgets.HackyViewPager;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveSpotMedia;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by wangtao on 2017/8/10.
 */

public class LiveMediaPagerActivity extends HljTrackedActivity {

    @Override
    public String pageTrackTagName() {
        return "直播图片预览";
    }

    public static final String ARG_MESSAGES = "messages";
    public static final String ARG_IS_REPLY = "is_reply";
    public static final String ARG_CLICK_MESSAGE = "click_message";
    public static final String ARG_SUB_POSITION = "sub_position";
    @BindView(R2.id.live_view_pager)
    HackyViewPager liveViewPager;
    @BindView(R2.id.btn_back)
    ImageView btnBack;
    @BindView(R2.id.tv_images_count)
    TextView tvImagesCount;
    @BindView(R2.id.img_download)
    ImageView imgDownload;
    @BindView(R2.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R2.id.tv_desc)
    TextView tvDesc;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;


    private int maxWidth;
    private int maxHeight;
    private int subPosition;
    private ArrayList<LiveMessage> liveMessages;
    private ArrayList<SpotDescMedia> medias;
    private boolean isReply;
    private LiveMessage clickMessage;
    private int currentPosition;
    private static final int ANIMATION_DURING = 240;

    private Subscription downloadSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_media_pager___live);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        Point point = CommonUtil.getDeviceSize(this);
        maxWidth = Math.round(point.x * 3 / 2);
        maxHeight = Math.round(point.y * 5 / 2);

        Intent intent = getIntent();
        liveMessages = intent.getParcelableArrayListExtra(ARG_MESSAGES);
        isReply = intent.getBooleanExtra(ARG_IS_REPLY, false);
        clickMessage = intent.getParcelableExtra(ARG_CLICK_MESSAGE);
        subPosition = intent.getIntExtra(ARG_SUB_POSITION, 0);

        medias = new ArrayList<>();
        currentPosition = 0;
        if (!CommonUtil.isCollectionEmpty(liveMessages)) {
            for (LiveMessage message : liveMessages) {
                if (message.getId() == clickMessage.getId()) {
                    currentPosition = medias.size() + subPosition;
                }
                if (isReply) {
                    // 回复的消息图片列表
                    LiveMessage reply = message.getReply();
                    if (reply != null && reply.getLiveContent() != null) {
                        medias.addAll(generateMediaList(reply.getLiveContent()
                                        .getAllMedias(),
                                message.getLiveContent()
                                        .getText()));
                    }
                } else {
                    // 非回复的图片列表
                    if (message.getLiveContent() != null) {
                        medias.addAll(generateMediaList(message.getLiveContent()
                                        .getAllMedias(),
                                message.getLiveContent()
                                        .getText()));
                    }
                }
            }
        }
    }

    private void initView() {
        liveViewPager.setAdapter(new MultiMediaPagerAdapter(this));
        setDownloadBtn(currentPosition);
        setBottomDesc(currentPosition);
        liveViewPager.setCurrentItem(currentPosition);
        tvImagesCount.setText(currentPosition + 1 + "/" + medias.size());
        liveViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                setDownloadBtn(position);
                setBottomDesc(position);
                tvImagesCount.setText(position + 1 + "/" + medias.size());
                super.onPageSelected(position);
            }
        });
    }

    private void setDownloadBtn(int position) {
        if (medias.get(position)
                .getMedia()
                .getType() == Media.TYPE_VIDEO) {
            imgDownload.setVisibility(View.GONE);
        } else {
            imgDownload.setVisibility(View.VISIBLE);
        }
    }

    private void setBottomDesc(int position) {
        SpotDescMedia media = medias.get(position);
        tvDesc.setText(media.getDesc());
        tvDesc.setVisibility(TextUtils.isEmpty(media.getDesc()) ? View.GONE : View.VISIBLE);
    }

    @Override
    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R2.id.img_download)
    public void onDownload() {
        final SpotDescMedia media = medias.get(liveViewPager.getCurrentItem());
        if (media == null) {
            return;
        }
        if (media.getMedia()
                .getType() == Media.TYPE_VIDEO) {
            Toast.makeText(this, "视频不能下载", Toast.LENGTH_SHORT)
                    .show();
        }
        File imgFile = FileUtil.createImageFile(media.getMedia()
                .getPhoto()
                .getImagePath());
        downloadSubscription=FileApi.download(media.getMedia()
                .getPhoto()
                .getImagePath(),imgFile.getAbsolutePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);
                        ToastUtil.showToast(LiveMediaPagerActivity.this,
                                "保存成功:" + file.getAbsolutePath(),0);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showToast(LiveMediaPagerActivity.this,"下载失败",0);
                    }
                });
    }

    private class MultiMediaPagerAdapter extends PagerAdapter {

        private Context mContext;

        private MultiMediaPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return medias.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.live_pics_page_item_view___live, container, false);
            final PhotoView image = view.findViewById(R.id.image);
            FrameLayout spotTagHolder = view.findViewById(R.id.spot_view_holder);
            View playView = view.findViewById(R.id.play);
            View progressBar = view.findViewById(R.id.progress_bar);
            final SpotDescMedia media = medias.get(position);
            String imagePath = null;
            if (media.getMedia()
                    .getType() == Media.TYPE_VIDEO) {
                playView.setVisibility(View.VISIBLE);
                imagePath = media.getMedia()
                        .getVideo()
                        .getScreenShot();
            } else {
                if (media.getMedia()
                        .getType() == Media.TYPE_PHOTO) {
                    imagePath = media.getMedia()
                            .getPhoto()
                            .getImagePath();
                }
                playView.setVisibility(View.GONE);
            }
            Glide.with(LiveMediaPagerActivity.this)
                    .load(ImagePath.buildPath(imagePath)
                            .width(maxWidth)
                            .height(maxHeight)
                            .path())
                    .apply(new RequestOptions().fitCenter()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .listener(new SpotPageImageRequestListener(image,
                            progressBar,
                            spotTagHolder,
                            media.getSpots()))
                    .into(image);

            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (media.getMedia()
                            .getType() == Media.TYPE_VIDEO) {
                        Intent intent = new Intent(view.getContext(), VideoPreviewActivity.class);
                        if (!TextUtils.isEmpty((media.getMedia()
                                .getVideo()).getLocalPath())) {
                            intent.putExtra("uri",
                                    Uri.parse((media.getMedia()
                                            .getVideo()).getLocalPath()));
                        } else {
                            intent.putExtra("uri",
                                    Uri.parse((media.getMedia()
                                            .getVideo()).getVideoPath()));
                        }
                        startActivity(intent);
                    } else {
                        startLayoutAnimation(actionLayout, true);
                        startLayoutAnimation(bottomLayout, false);
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

        class SpotPageImageRequestListener implements RequestListener<Drawable> {

            private PhotoView photoView;
            private View progressBar;
            private FrameLayout tagHolder;
            private List<NoteSpot> spots;

            public SpotPageImageRequestListener(
                    PhotoView photoView,
                    View progressBar,
                    FrameLayout tagHolder,
                    List<NoteSpot> spots) {
                this.photoView = photoView;
                this.progressBar = progressBar;
                this.tagHolder = tagHolder;
                this.spots = spots;
            }

            @Override
            public boolean onLoadFailed(
                    @Nullable GlideException e,
                    Object model,
                    Target target,
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
                int x = photoView.getWidth();
                int y = photoView.getHeight();
                int width = resource.getIntrinsicWidth();
                int height = resource.getIntrinsicHeight();

                float rate = (float) x / width;
                int h = Math.round(height * rate);
                if (h > y) {
                    photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    setTagsView(x, y);
                } else {
                    photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    setTagsView(x, h);
                }

                return false;
            }

            private void setTagsView(int width, int height) {
                tagHolder.getLayoutParams().width = width;
                tagHolder.getLayoutParams().height = height;
                tagHolder.removeAllViews();

                int tagWidth = CommonUtil.dp2px(LiveMediaPagerActivity.this, 68);
                int tagHeight = CommonUtil.dp2px(LiveMediaPagerActivity.this, 28);

                if (!CommonUtil.isCollectionEmpty(spots)) {
                    for (final NoteSpot spot : spots) {
                        ImageView imgTag = (ImageView) LayoutInflater.from(LiveMediaPagerActivity
                                .this)
                                .inflate(R.layout.img_tag_layout, null);
                        int topMargin = (int) (height * spot.getNoteSpotLayout()
                                .getY()) - tagHeight / 2;
                        int leftMargin = (int) (width * spot.getNoteSpotLayout()
                                .getX());
                        if (leftMargin > (width - tagWidth)) {
                            imgTag.setImageResource(R.mipmap.icon_img_tag_left___live);
                            leftMargin -= tagWidth;
                        } else {
                            imgTag.setImageResource(R.mipmap.icon_img_tag_right___live);
                        }
                        topMargin = Math.min(height - tagHeight, topMargin);
                        topMargin = Math.max(0, topMargin);
                        leftMargin = Math.min(width - tagWidth, leftMargin);
                        leftMargin = Math.max(0, leftMargin);
                        ViewGroup.MarginLayoutParams layoutParams = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(leftMargin, topMargin, 0, 0);

                        imgTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onTagItemClick(spot);
                            }
                        });
                        tagHolder.addView(imgTag, layoutParams);
                    }
                }
            }
        }
    }

    /**
     * 显示或者隐藏 顶部操作栏 底部文字介绍
     *
     * @param animationView
     * @param isTop
     */
    private void startLayoutAnimation(final View animationView, boolean isTop) {
        if (animationView != null) {
            //isShow 是下一刻 需要的状态 与当前状态相反
            final boolean isShow = animationView.getVisibility() == View.GONE;
            animationView.clearAnimation();
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    isShow ? (isTop ? -1 : 1) : 0,
                    Animation.RELATIVE_TO_SELF,
                    isShow ? 0 : (isTop ? -1 : 1));
            animation.setDuration(ANIMATION_DURING);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    animationView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!isShow) {
                        animationView.setVisibility(View.GONE);
                    }
                }
            });
            animationView.startAnimation(animation);
        }
    }

    public void onTagItemClick(NoteSpot noteSpot) {
        if (CommonUtil.getAppType() == CommonUtil.PacketType.MERCHANT) {
            return;
        }
        NoteMark mark = noteSpot.getNoteMark();
        if (mark.getMarkedType() == Mark.MARK_TYPE_PRODUCT) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                    .withLong("id", mark.getMarkableId())
                    .navigation(this);
        } else if (mark.getMarkedType() == Mark.MARK_TYPE_WORK) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.WORK_ACTIVITY)
                    .withLong("id", mark.getMarkableId())
                    .navigation(this);
        }
    }

    private ArrayList<SpotDescMedia> generateMediaList(
            List<LiveSpotMedia> medias, String desc) {
        ArrayList<SpotDescMedia> list = new ArrayList<>();
        for (LiveSpotMedia media : medias) {
            SpotDescMedia descMedia = new SpotDescMedia(media.getMedia(), media.getSpots(), desc);
            list.add(descMedia);
        }

        return list;
    }

    private class SpotDescMedia extends LiveSpotMedia {
        String desc;

        public SpotDescMedia(
                Media media, List<NoteSpot> spots, String desc) {
            super(media, spots);
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(downloadSubscription);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            CommonUtil.unSubscribeSubs(downloadSubscription);
        }
    }
}
