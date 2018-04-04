package me.suncloud.marrymemo.view.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljlivelibrary.api.LiveApi;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljvideolibrary.activities.VideoPreviewActivity;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.finder.SameCasesHomeFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.util.CaseTogglesUtil;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by mo_yu on 2017/8/10.发现页案例
 */

public class FinderCaseMediaPagerActivity extends FragmentActivity implements OverscrollContainer
        .OnLoadListener {

    public static final String ARG_CASE = "case";
    public static final String ARG_MEDIA_ID = "media_id";
    public static final String ARG_POSITION = "position";
    public static final String ARG_IS_SHOW_MORE = "is_show_more";
    public static final String ARG_ATTR_TYPE = "attr_type";
    private static final int ANIMATION_DURING = 240;
    @BindView(R.id.view_pager)
    OverScrollViewPager viewPager;
    @BindView(R.id.img_merchant_avatar)
    RoundedImageView imgMerchantAvatar;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_follow_merchant)
    TextView tvFollowMerchant;
    @BindView(R.id.tv_images_count)
    TextView tvImagesCount;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_more_cases)
    TextView tvMoreCases;
    @BindView(R.id.tv_query_date)
    TextView tvChatMerchant;
    @BindView(R.id.img_share_case)
    ImageView imgShareCase;
    @BindView(R.id.img_collect_case)
    ImageView imgCollectCase;
    @BindView(R.id.right_layout)
    LinearLayout rightLayout;
    @BindView(R.id.fragment_content)
    View fragmentContent;

    private int maxWidth;
    private int maxHeight;
    private int logoSize;
    private int currentPosition;//定位当前选中的图片
    private boolean isShowMore;//是否展示更多案例
    private boolean isDragBottomEnd = true;//是否拖拽到底部
    private float y1;
    private String attrType;
    private Work work;
    private Merchant merchant;
    private ArrayList<WorkMediaItem> medias;
    private SameCasesHomeFragment sameCasesListFragment;
    private MultiMediaPagerAdapter adapter;
    private HljHttpSubscriber chatTrigSub;
    private HljHttpSubscriber followSubscriber;
    private HljHttpSubscriber collectCheckSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_media_pager);
        ButterKnife.bind(this);
        initValue();
        initView();
        loadMerchantCollectStatus();
    }

    private void initValue() {
        Point point = CommonUtil.getDeviceSize(this);
        maxWidth = Math.round(point.x);
        maxHeight = Math.round(point.y);
        logoSize = CommonUtil.dp2px(this, 32);
        Intent intent = getIntent();
        work = intent.getParcelableExtra(ARG_CASE);
        attrType = intent.getStringExtra(ARG_ATTR_TYPE);
        isShowMore = intent.getBooleanExtra(ARG_IS_SHOW_MORE, true);
        currentPosition = intent.getIntExtra(ARG_POSITION, 0);
        long mediaId = intent.getLongExtra(ARG_MEDIA_ID, 0);
        medias = new ArrayList<>();
        if (work != null) {
            if (!CommonUtil.isCollectionEmpty(work.getMediaItems())) {
                medias.addAll(work.getMediaItems());
            }
            for (int i = 0; i < medias.size(); i++) {
                WorkMediaItem mediaItem = work.getMediaItems()
                        .get(i);
                if (mediaId > 0 && mediaItem.getId() == mediaId) {
                    currentPosition = i;
                }
            }
        }
    }

    private void initView() {
        if (work == null) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                rightLayout.getLayoutParams();
        marginLayoutParams.topMargin = Math.round(CommonUtil.getDeviceSize(this).y * 0.55f);
        if (work.isCollected()) {
            imgCollectCase.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
        } else {
            imgCollectCase.setImageResource(R.drawable.icon_collect_white_stroke3_44_44);
        }
        merchant = work.getMerchant();
        if (merchant != null) {
            if (merchant.getProperty()
                    .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
                tvChatMerchant.setText("查询拍摄档期");
            } else {
                tvChatMerchant.setText("查询婚礼档期");
            }
            tvMerchantName.setText(merchant.getName());
            Glide.with(this)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(logoSize)
                            .height(logoSize)
                            .cropPath())
                    .into(imgMerchantAvatar);
        }
        tvDesc.setText(work.getTitle());
        tvDesc.setVisibility(TextUtils.isEmpty(work.getTitle()) ? View.GONE : View.VISIBLE);
        adapter = new MultiMediaPagerAdapter(this);

        tvImagesCount.setText(currentPosition + 1 + "/" + medias.size());
        viewPager.setOverable(isShowMore);
        viewPager.hideHintView();
        viewPager.hideArrowView();
        viewPager.setOnLoadListener(this);
        viewPager.getOverscrollView()
                .setAdapter(adapter);
        viewPager.getOverscrollView()
                .setCurrentItem(currentPosition);
        viewPager.getOverscrollView()
                .addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        actionLayout.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);
                        rightLayout.setVisibility(View.VISIBLE);
                        tvImagesCount.setText(position + 1 + "/" + medias.size());
                    }
                });
        if (isShowMore) {
            tvMoreCases.setVisibility(View.VISIBLE);
        } else {
            tvMoreCases.setVisibility(View.GONE);
        }
    }

    /**
     * 单独获取商家关注状态
     */
    private void loadMerchantCollectStatus() {
        if (merchant != null && merchant.getId() > 0) {
            CommonUtil.unSubscribeSubs(collectCheckSub);
            collectCheckSub = HljHttpSubscriber.buildSubscriber(this)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                        @Override
                        public void onNext(Boolean isCollected) {
                            merchant.setCollected(isCollected);
                            if (merchant.isCollected()) {
                                tvFollowMerchant.setText(R.string.label_enter___cm);
                            } else {
                                tvFollowMerchant.setText(R.string.label_follow___cm);
                            }
                        }
                    })
                    .build();
            LiveApi.isCollectMerchant(merchant.getId())
                    .subscribe(collectCheckSub);
        }
    }

    @Override
    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (sameCasesListFragment != null && !sameCasesListFragment.isHidden()) {
            hideSameFragment();
        } else {
            Intent intent = getIntent();
            intent.putExtra(ARG_CASE, work);
            setResult(Activity.RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
        }
    }

    @OnClick({R.id.img_merchant_avatar, R.id.tv_merchant_name})
    public void onMerchantAvatar() {
        if (merchant == null) {
            return;
        }
        Intent intent = new Intent(this, MerchantDetailActivity.class);
        intent.putExtra(MerchantDetailActivity.ARG_ID, merchant.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @OnClick(R.id.tv_follow_merchant)
    public void onFollowMerchant() {
        if (!Util.loginBindChecked(this) || merchant == null) {
            return;
        }
        if (merchant.isCollected()) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra(MerchantDetailActivity.ARG_ID, merchant.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
        } else if (followSubscriber == null || followSubscriber.isUnsubscribed()) {
            followSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            merchant.setCollected(true);
                            tvFollowMerchant.setText(R.string.label_enter___cm);
                            Toast.makeText(FinderCaseMediaPagerActivity.this,
                                    "关注成功！",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .build();
            CommonApi.postMerchantFollowObb(merchant.getId())
                    .subscribe(followSubscriber);
        }
    }

    @OnClick(R.id.collect_case_layout)
    public void onCollectCase() {
        if (!Util.loginBindChecked(this) || work == null) {
            return;
        }
        if (work.isCollected()) {
            imgCollectCase.setImageResource(R.drawable.icon_collect_white_stroke3_44_44);
        } else {
            imgCollectCase.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
        }
        CaseTogglesUtil.onCollectCase(this, work, new CaseTogglesUtil.onCollectCompleteListener() {
            @Override
            public void onCollectComplete(boolean isSuccess, String msg) {
                if (!isSuccess) {
                    if (CommonUtil.isEmpty(msg)) {
                        ToastUtil.showToast(FinderCaseMediaPagerActivity.this, "请稍后再试", 0);
                    } else {
                        ToastUtil.showToast(FinderCaseMediaPagerActivity.this, msg, 0);
                    }
                    if (work.isCollected()) {
                        imgCollectCase.setImageResource(R.drawable
                                .icon_collect_primary_44_44_selected);
                    } else {
                        imgCollectCase.setImageResource(R.drawable
                                .icon_collect_white_stroke3_44_44);
                    }
                }
            }
        });
    }

    @OnClick(R.id.share_case_layout)
    public void onShareCase() {
        if (work == null || work.getShareInfo() == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this, work.getShareInfo());
    }

    @OnClick(R.id.tv_query_date)
    public void onQueryDate() {
        if (!AuthUtil.loginBindCheck(this) || merchant == null) {
            return;
        }
        User user = Session.getInstance()
                .getCurrentUser();
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_HOTEL, merchant);
        } else if (user != null) {
            chatTrigSub = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                        }
                    })
                    .build();
            ChatApi.postMsgProxy(getSupportMsg(work),
                    WSChat.TEXT,
                    work.getMerchant()
                            .getUserId(),
                    user.getId(),
                    WSChat.Source.CHAT_SOURCE_CASE_PREVIEW)
                    .subscribe(chatTrigSub);
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant.toUser());
            intent.putExtra("ws_track", ModuleUtils.getWSTrack(work));
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_more_cases)
    public void onMoreCase() {
        if (work == null) {
            return;
        }
        showSameFragment();
    }

    public void showSameFragment() {
        if (!isShowMore) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up_to_top, R.anim.slide_out_down_to_bottom);
        if (sameCasesListFragment != null) {
            ft.show(sameCasesListFragment);
        } else {
            sameCasesListFragment = SameCasesHomeFragment.newInstance(work, attrType);
            ft.add(R.id.fragment_content, sameCasesListFragment);
        }
        ft.commitAllowingStateLoss();
    }

    public void hideSameFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up_to_top, R.anim.slide_out_down_to_bottom);
        if (sameCasesListFragment != null) {
            ft.hide(sameCasesListFragment);
        }
        ft.commitAllowingStateLoss();
    }

    private String getSupportMsg(Work work) {
        String supportMsg;
        if (merchant.getProperty()
                .getId() == Merchant.PROPERTY_WEDDING_DRESS_PHOTO) {
            supportMsg = getString(R.string.msg_finder_case_dress_chat, work.getTitle());
        } else {
            supportMsg = getString(R.string.msg_finder_case_chat, work.getTitle());
        }
        return supportMsg;
    }

    @Override
    public void onLoad() {
        showSameFragment();
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
                    .inflate(R.layout.pics_page_item_view___cv, container, false);
            final PhotoView image = view.findViewById(R.id.image);
            View playView = view.findViewById(com.hunliji.hljlivelibrary.R.id.play);
            final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            final WorkMediaItem media = medias.get(position);
            if (media.getKind() == WorkMediaItem.MEDIA_KIND_VIDEO) {
                playView.setVisibility(View.VISIBLE);
            } else {
                playView.setVisibility(View.GONE);
            }
            String imagePath = media.getItemCover();
            if (!CommonUtil.isEmpty(media.getLocalPath())) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
            Glide.with(FinderCaseMediaPagerActivity.this)
                    .load(ImagePath.buildPath(imagePath)
                            .width(maxWidth)
                            .height(maxHeight)
                            .path())
                    .thumbnail(Glide.with(FinderCaseMediaPagerActivity.this)
                            .load(media.getLocalPath()))
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
                            progressBar.setVisibility(View.GONE);
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
            image.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    startLayoutAnimation(actionLayout, true);
                    startLayoutAnimation(bottomLayout, false);
                    startRightLayoutAnimation(rightLayout);
                }
            });
            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (media.getKind() == WorkMediaItem.MEDIA_KIND_VIDEO) {
                        Intent intent = new Intent(view.getContext(), VideoPreviewActivity.class);
                        if (!TextUtils.isEmpty(media.getVideoPath())) {
                            intent.putExtra("uri", Uri.parse(media.getVideoPath()));
                        }
                        startActivity(intent);
                    } else {
                        startLayoutAnimation(actionLayout, true);
                        startLayoutAnimation(bottomLayout, false);
                        startRightLayoutAnimation(rightLayout);
                    }
                }
            });
            image.setOnDragChangeListener(new PhotoViewAttacher.OnDragChangeListener() {
                @Override
                public void onDragChange(boolean isDragEnd, float dx, float dy) {
                    isDragBottomEnd = isDragEnd && dy < -1f;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当手指按下的时候
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float y2 = event.getY();
                if (y1 - y2 > 200 && isDragBottomEnd) {
                    if ((sameCasesListFragment != null && !sameCasesListFragment.isHidden())) {
                        break;
                    }
                    //向上滑动超过一定距离才触发显示更多操作
                    showSameFragment();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 显示或者隐藏 右侧操作栏
     *
     * @param animationView
     */
    private void startRightLayoutAnimation(final View animationView) {
        if (animationView != null) {
            //isShow 是下一刻 需要的状态 与当前状态相反
            final boolean isShow = animationView.getVisibility() == View.GONE;
            animationView.clearAnimation();
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    isShow ? 1 : 0,
                    Animation.RELATIVE_TO_SELF,
                    isShow ? 0 : 1,
                    Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    0);
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

    @Override
    protected void onDestroy() {
        CommonUtil.unSubscribeSubs(followSubscriber, collectCheckSub);
        super.onDestroy();
    }
}
