package me.suncloud.marrymemo.view.wedding_dress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.PostPraiseBody;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoContent;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoItem;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.AnimUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.PageImageRequestListener;
import com.hunliji.hljimagelibrary.views.widgets.HackyViewPager;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.communitythreads.CommunityThreadApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.ReportThreadActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Observable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hua_rong on 2017/5/2.
 * 婚纱查看大图
 */

public class ViewLargeImageActivity extends HljBaseNoBarActivity {

    @BindView(R.id.pager)
    HackyViewPager mViewPager;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.ib_share)
    ImageButton ibShare;
    @BindView(R.id.ib_more)
    ImageButton ibMore;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_none_live)
    TextView tvNoneLive;
    @BindView(R.id.rl_no_merchant)
    RelativeLayout rlNoMerchant;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.rl_comment)
    RelativeLayout rlComment;
    @BindView(R.id.iv_praise)
    ImageView ivPraise;
    @BindView(R.id.tv_praise)
    TextView tvPraise;
    @BindView(R.id.ll_praise)
    LinearLayout llPraise;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.ll_collect)
    LinearLayout llCollect;
    @BindView(R.id.iv_merchant)
    ImageView ivMerchant;
    @BindView(R.id.tv_merchant)
    TextView tvMerchant;
    @BindView(R.id.ll_merchant)
    LinearLayout llMerchant;
    @BindView(R.id.button_layout)
    LinearLayout buttonLayout;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_bottom_layout)
    LinearLayout llBottomLayout;
    @BindView(R.id.line_view)
    View lineView;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    private int width;
    private int height;
    private boolean isAnim;

    private Context context;
    private List<Photo> photos;
    private Dialog dialog;
    private int position;
    private HljHttpSubscriber likeSubscriber, collectSubscriber;
    private CommunityThread communityThread;
    private Unbinder unBinder;
    public static final int ANIMATION_DURING = 300;
    private int[] praisePic = {R.mipmap.icon_praise_white_40_40_3, R.mipmap
            .icon_praise_primary_40_40};
    private int[] collectPic = {R.drawable.icon_collect_white_44_44, R.drawable
            .icon_collect_primary_44_44_selected};
    private HashMap<Photo, WeddingPhotoItem> photoItemHashMap;
    private ArrayList<WeddingPhotoItem> weddingPhotoItems;
    private static final int maxLine = 4;//描述文本最多显示行数
    private String preDescribe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_large_image);
        unBinder = ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        communityThread = getIntent().getParcelableExtra("thread");
        int itemPosition = getIntent().getIntExtra("item_position", 0);
        int photoPosition = getIntent().getIntExtra("photo_position", 0);
        if (communityThread == null) {
            return;
        }
        photos = new ArrayList<>();
        photoItemHashMap = new HashMap<>();
        weddingPhotoItems = communityThread.getWeddingPhotoItems();
        if (weddingPhotoItems != null) {
            position = initPhotoData(weddingPhotoItems, itemPosition, photoPosition);
            initView();
            initTabValue(position);
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    initTabValue(position);
                    if (rlNoMerchant != null && (rlNoMerchant.getVisibility() == View.VISIBLE) &&
                            !isAnim) {
                        showMerchant(false);
                    }
                }
            });
        }
    }

    private int initPhotoData(
            ArrayList<WeddingPhotoItem> weddingPhotoItems, int itemPosition, int photoPosition) {
        int position = 0;
        for (WeddingPhotoItem weddingPhotoItem : weddingPhotoItems) {
            ArrayList<Photo> photoList = weddingPhotoItem.getPhotos();
            for (Photo photo : photoList) {
                photoItemHashMap.put(photo, weddingPhotoItem);
            }
            photos.addAll(photoList);
        }
        for (int i = 0; i < itemPosition; i++) {
            position += weddingPhotoItems.get(i)
                    .getPhotos()
                    .size();
        }
        position += photoPosition;
        return position;
    }

    private void initTabValue(int position) {
        tvCount.setText(position + 1 + "/" + photos.size());
        Photo photo = photos.get(position);
        WeddingPhotoItem weddingPhotoItem = photoItemHashMap.get(photo);
        if (weddingPhotoItem != null) {
            String describe = weddingPhotoItem.getDescription();
            if (TextUtils.isEmpty(describe)) {
                tvDescription.setVisibility(View.GONE);
                lineView.setVisibility(View.INVISIBLE);
            } else {
                lineView.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(describe);
                int count = tvDescription.getLineCount();
                if (count > maxLine) {
                    tvDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
                    if (!describe.equals(preDescribe)) {
                        preDescribe = describe;
                        tvDescription.scrollTo(0, 0);
                    }
                } else {
                    tvDescription.setMovementMethod(null);
                }
            }
            praiseState(weddingPhotoItem);
        }
    }

    private void initView() {
        Point point = CommonUtil.getDeviceSize(this);
        context = this;
        width = Math.round(point.x * 3 / 2);
        height = Math.round(point.y * 5 / 2);
        setSwipeBackEnable(false);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(position);
        Paint.FontMetrics fontMetrics = tvDescription.getPaint()
                .getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        int maxHeight = (int) (CommonUtil.dp2px(context,
                32 + 6 * (maxLine - 1)) + fontHeight * maxLine);
        tvDescription.setMaxHeight(maxHeight);
        if (communityThread.getId() == 0) {
            ibMore.setVisibility(View.GONE);
            ibShare.setVisibility(View.GONE);
            rlBottom.setVisibility(View.GONE);
        } else {
            User user = Session.getInstance()
                    .getCurrentUser();
            boolean collect = communityThread.isCollected();
            collectState(collect);
            CommunityAuthor author = communityThread.getAuthor();
            if (user != null && user.getId() == author.getId() && author.getId() != 0) {
                ibMore.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_back)
    public void onBack(View view) {
        onBackPressed();
    }

    @OnClick(R.id.rl_comment)
    public void onComment() {
        if (communityThread != null && !communityThread.isHidden()) {
            CommunityPost post = communityThread.getPost();
            CommunityAuthor author = communityThread.getAuthor();
            post.setAuthor(author);
            if (Util.loginBindChecked(this)) {
                Intent intent = new Intent(this, WeddingPhotoCommentActivity.class);
                intent.putExtra("post", post);
                startActivityForResult(intent, Constants.RequestCode.REPLY_THREAD_POST);
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.REPLY_THREAD_POST:
                    Intent intent = getIntent();
                    intent.putExtra("replied_thread", true);
                    setResult(RESULT_OK, intent);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.ll_praise)
    public void onPraise() {
        if (communityThread != null) {
            if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
                PostPraiseBody praiseBody = new PostPraiseBody();
                praiseBody.setEntityType("CommunityThreadItem");
                int position = mViewPager.getCurrentItem();
                Photo photo = photos.get(position);
                final WeddingPhotoItem weddingPhotoItem = photoItemHashMap.get(photo);
                praiseBody.setId(weddingPhotoItem.getId());
                praiseBody.setType(8);
                Observable observable = CommonApi.postPraiseObb(praiseBody);
                onPraiseLike(true, weddingPhotoItem);//先改变点赞状态
                if (likeSubscriber == null || likeSubscriber.isUnsubscribed()) {
                    likeSubscriber = HljHttpSubscriber.buildSubscriber(this)
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    // 发送消息给详情页面改变那边的状态
                                    // index = 点赞图片所属组图在组图列表中的index
                                    if (weddingPhotoItems != null) {
                                        int index = weddingPhotoItems.indexOf(weddingPhotoItem);
                                        RxBus.getDefault()
                                                .post(new RxEvent(RxEvent.RxEventType
                                                        .THREAD_WEDDING_PHOTO_GROUP_PRAISED,
                                                        index));
                                    }
                                    ToastUtil.showCustomToast(context,
                                            weddingPhotoItem.isPraised() ? R.string
                                                    .hint_praised_complete : R.string
                                                    .hint_dispraised_complete);
                                }
                            })
                            .setOnErrorListener(new SubscriberOnErrorListener() {
                                @Override
                                public void onError(Object o) {
                                    onPraiseLike(false, weddingPhotoItem);//失败后还原信息
                                    ToastUtil.showCustomToast(context,
                                            weddingPhotoItem.isPraised() ? R.string
                                                    .msg_fail_to_cancel_post : R.string
                                                    .msg_fail_to_praise_post);
                                }
                            })
                            .build();
                    observable.subscribe(likeSubscriber);
                }
            }
        }
    }

    private void onPraiseLike(boolean anim, WeddingPhotoItem weddingPhotoItem) {
        if (weddingPhotoItem != null) {
            int praiseSum = weddingPhotoItem.getLikesCount();
            if (!weddingPhotoItem.isPraised()) {
                if (anim) {
                    AnimUtil.pulseAnimate(ivPraise);
                }
                weddingPhotoItem.setPraised(true);
                weddingPhotoItem.setLikesCount(praiseSum + 1);
            } else {
                weddingPhotoItem.setPraised(false);
                weddingPhotoItem.setLikesCount(praiseSum - 1);
            }
            praiseState(weddingPhotoItem);
        }
    }

    private void praiseState(WeddingPhotoItem weddingPhotoItem) {
        if (weddingPhotoItem != null) {
            boolean isPraised = weddingPhotoItem.isPraised();
            int count = weddingPhotoItem.getLikesCount();
            String praiseLabel = count > 0 ? String.valueOf(count) : "点赞";
            ivPraise.setImageResource(isPraised ? praisePic[1] : praisePic[0]);
            tvPraise.setTextColor(ContextCompat.getColor(context,
                    isPraised ? R.color.colorPrimary : R.color.colorWhite));
            tvPraise.setText(praiseLabel);
        }
    }

    @OnClick(R.id.ll_merchant)
    public void onMerchant() {
        if (communityThread != null) {
            WeddingPhotoContent weddingPhotoContent = communityThread.getWeddingPhotoContent();
            if (weddingPhotoContent != null) {
                Merchant merchant = weddingPhotoContent.getMerchant();
                String unrecordedName = weddingPhotoContent.getUnrecordedMerchantName();
                if (merchant != null && merchant.getId() != 0) {
                    Intent intent = new Intent(this, MerchantDetailActivity.class);
                    intent.putExtra("id", merchant.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else if (!TextUtils.isEmpty(unrecordedName)) {
                    tvName.setText(unrecordedName);
                    if (!isAnim) {
                        if (rlNoMerchant.getVisibility() == View.VISIBLE) {
                            llBottom.clearAnimation();
                            showMerchant(false);
                        } else {
                            llBottom.clearAnimation();
                            showMerchant(true);
                        }
                    }
                } else {
                    Toast.makeText(context,
                            R.string.label_un_upload_merchant_info,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    /**
     * @param show true 显示 false hide
     */
    private void showMerchant(final boolean show) {
        int height = CommonUtil.dp2px(this, 50);
        ObjectAnimator animator = ObjectAnimator.ofFloat(llBottom,
                View.TRANSLATION_Y,
                show ? height : 0,
                show ? 0 : height);
        animator.setDuration(ANIMATION_DURING);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    rlNoMerchant.setVisibility(View.VISIBLE);
                }
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if (!show) {
                    rlNoMerchant.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @OnClick(R.id.ll_collect)
    public void onCollect() {
        if (communityThread != null) {
            if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
                Observable observable = CommunityThreadApi.postThreadCollect(communityThread
                        .getId());
                collectState(!communityThread.isCollected());//先改变收藏状态 取反
                if (collectSubscriber == null || collectSubscriber.isUnsubscribed()) {
                    collectSubscriber = HljHttpSubscriber.buildSubscriber(this)
                            .setOnNextListener(new SubscriberOnNextListener() {
                                @Override
                                public void onNext(Object o) {
                                    // 发送消息给详情页面改变那边的状态
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType
                                                    .THREAD_WEDDING_PHOTO_COLLECTED,
                                                    null));
                                    ToastUtil.showCustomToast(context,
                                            communityThread.isCollected() ? R.string
                                                    .hint_collect_complete : R.string
                                                    .hint_discollect_complete);
                                }
                            })
                            .setOnErrorListener(new SubscriberOnErrorListener() {
                                @Override
                                public void onError(Object o) {
                                    collectState(!communityThread.isCollected());//还原状态 取反
                                    ToastUtil.showCustomToast(context,
                                            communityThread.isCollected() ? R.string
                                                    .msg_product_del_collect_fail : R.string
                                                    .msg_product_collect_fail);
                                }
                            })
                            .setProgressBar(progressBar)
                            .build();
                    observable.subscribe(collectSubscriber);
                }
            }
        }

    }

    /**
     * 改变收藏状态
     *
     * @param collect 是否收藏
     */
    private void collectState(boolean collect) {
        communityThread.setCollected(collect);
        ivCollect.setImageResource(collect ? collectPic[1] : collectPic[0]);
        tvCollect.setText(collect ? R.string.label_has_collection : R.string.label_collect);
        tvCollect.setTextColor(ContextCompat.getColor(context,
                collect ? R.color.colorPrimary : R.color.colorWhite));
    }

    @OnClick(R.id.ib_share)
    public void onShare() {
        if (communityThread != null) {
            ShareInfo shareInfo = communityThread.getShareInfo();
            if (shareInfo != null) {
                ShareDialogUtil.onCommonShare(this,
                        new ShareInfo(shareInfo.getTitle(),
                                shareInfo.getDesc(),
                                shareInfo.getDesc2(),
                                shareInfo.getUrl(),
                                shareInfo.getIcon()));
            }
        }
    }

    @OnClick(R.id.ib_more)
    public void onMoreSetting() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.BubbleDialogTheme);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_thread_menu);
                View reportLayout = window.findViewById(R.id.report_layout);
                View cancel = window.findViewById(R.id.btn_cancel);
                reportLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (communityThread == null || communityThread.isHidden()) {
                            return;
                        }
                        dialog.cancel();
                        if (Util.loginBindChecked(ViewLargeImageActivity.this,
                                Constants.Login.INFORM_LOGIN)) {
                            onReport(communityThread.getId(), "thread");
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = CommonUtil.getDeviceSize(context);
                params.width = point.x;
                window.setAttributes(params);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        dialog.show();
    }

    private void onReport(Long id, String kind) {
        Intent intent = new Intent(this, ReportThreadActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("kind", kind);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photos == null ? 0 : photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.pics_page_item_view___img, container, false);
            PhotoView image = (PhotoView) view.findViewById(R.id.image);
            View progressBar = view.findViewById(R.id.progress_bar);
            Photo photo = photos.get(position);
            String imgUrl = photo.getImagePath();
            imgUrl = ImagePath.buildPath(imgUrl)
                    .width(width)
                    .height(height)
                    .cropPath();
            if (TextUtils.isEmpty(imgUrl)) {
                Glide.with(context)
                        .clear(image);
                image.setImageBitmap(null);
            } else {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .listener(new PageImageRequestListener(image, progressBar))
                        .into(image);
            }
            if (communityThread.getId() != 0) {
                image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        showLayout(llBottomLayout, false);
                        showLayout(actionLayout, true);
                    }
                });
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


    /**
     * @param view view
     * @param top  在顶部 为true 否则为false
     */
    private void showLayout(final View view, boolean top) {
        if (view != null) {
            final boolean show = view.getVisibility() == View.GONE;
            view.clearAnimation();
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    0,
                    Animation.RELATIVE_TO_SELF,
                    show ? (top ? -1 : 1) : 0,
                    Animation.RELATIVE_TO_SELF,
                    show ? 0 : (top ? -1 : 1));
            animation.setDuration(ANIMATION_DURING);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!show) {
                        view.setVisibility(View.GONE);
                    }
                }
            });
            view.startAnimation(animation);
        }
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        if (unBinder != null) {
            unBinder.unbind();
        }
        CommonUtil.unSubscribeSubs(likeSubscriber, collectSubscriber);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }
}
