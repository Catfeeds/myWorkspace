package me.suncloud.marrymemo.view.community;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoItem;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljHorizontalProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.ParallaxScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableHelper;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.WeddingPhotoPreviewAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.realm.RealmJsonPic;
import me.suncloud.marrymemo.model.realm.WeddingPhotoDraft;
import me.suncloud.marrymemo.model.realm.WeddingPhotoItemDraft;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.WeddingPhotoListUploader;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.wedding_dress.WeddingReleaseSuccessActivity;
import rx.Subscription;

public class WeddingPhotoPreviewActivity extends HljBaseNoBarActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.cover_layout)
    RelativeLayout coverLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.avatar_layout)
    RelativeLayout avatarLayout;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_photo_title)
    TextView tvPhotoTitle;
    @BindView(R.id.tv_photo_count)
    TextView tvPhotoCount;
    @BindView(R.id.tv_view_count)
    TextView tvViewCount;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.scrollable_layout)
    ParallaxScrollableLayout scrollableLayout;
    @BindView(R.id.shadow_view)
    ImageView shadowView;
    @BindView(R.id.btn_share)
    ImageView btnShare;
    @BindView(R.id.btn_menu)
    ImageView btnMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_share2)
    ImageButton btnShare2;
    @BindView(R.id.btn_menu2)
    ImageView btnMenu2;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_vip)
    ImageView imgVip;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.img_triangle)
    ImageView imgTriangle;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;

    private WeddingPhotoPreviewAdapter adapter;
    private HljHorizontalProgressDialog progressDialog;
    private MerchantViewHolder merchantViewHolder;

    private ArrayList<Subscription> subscriptions;
    private HljHttpSubscriber createSubscriber;
    private WeddingPhotoListUploader allUpLoad;
    private ArrayList<WeddingPhotoItemDraft> groups;
    private WeddingPhotoDraft draft;
    private Merchant merchant;
    private User user;
    private RealmList<RealmJsonPic> localPhotos;
    private int totalCount;

    private int coverWidth;
    private int coverHeight;
    private int avatarSize;
    private int actionBarHeight;
    private int avatarOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_photo_preview);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2);
        setSwipeBackEnable(false);
        initValues();
        initViews();
        initDate();
        initWeddingPhotos();
    }

    private void initValues() {
        coverWidth = CommonUtil.getDeviceSize(this).x;
        avatarOffset = CommonUtil.dp2px(this, 10);
        coverHeight = coverWidth * 9 / 16 + avatarOffset;
        avatarSize = coverWidth * 5 / 32;

        avatarLayout.getLayoutParams().height = avatarSize;
        imgTriangle.getLayoutParams().height = avatarSize - 2 * avatarOffset;
        imgAvatar.getLayoutParams().width = avatarSize;
        imgAvatar.getLayoutParams().height = avatarSize;
        imgAvatar.setCornerRadius(avatarSize / 2);

        actionBarHeight = CommonUtil.dp2px(this, 45);
        groups = new ArrayList<>();
        localPhotos = new RealmList<>();
        draft = getIntent().getParcelableExtra("draft");
    }

    public void initViews() {
        scrollableLayout.setExtraHeight(actionBarHeight);
        imgAvatar.getLayoutParams().width = avatarSize;
        imgAvatar.getLayoutParams().height = avatarSize;
        avatarLayout.getLayoutParams().height = avatarSize;
        imgAvatar.setCornerRadius(avatarSize / 2);
        drawTriangle();

        coverLayout.getLayoutParams().height = coverHeight;
        scrollableLayout.setParallaxView(coverLayout, coverHeight);
        scrollableLayout.setViewsBounds(2);
        scrollableLayout.addOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                if (currentY > maxY) {
                    actionHolderLayout2.setAlpha(1);
                    shadowView.setAlpha(0);
                } else {
                    float f = (float) currentY / maxY;
                    actionHolderLayout2.setAlpha(f);
                    shadowView.setAlpha(1 - f);
                }
            }
        });
        scrollableLayout.getHelper()
                .setCurrentScrollableContainer(new ScrollableHelper.ScrollableContainer() {
                    @Override
                    public View getScrollableView() {
                        return recyclerView;
                    }

                    @Override
                    public boolean isDisable() {
                        return false;
                    }
                });

        adapter = new WeddingPhotoPreviewAdapter(this, groups);
        View merchantFooterView = getLayoutInflater().inflate(R.layout.thread_detail_merchant_item,
                null);
        merchantFooterView.findViewById(R.id.bottom_divider)
                .setVisibility(View.VISIBLE);
        merchantViewHolder = new MerchantViewHolder(merchantFooterView);
        adapter.setMerchantFooterView(merchantFooterView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void drawTriangle() {
        int width = coverWidth;
        int height = avatarSize - 2 * avatarOffset;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(ContextCompat.getColor(this, R.color.colorWhite));
        Path path = new Path();
        path.moveTo(0, 0);// 此点为多边形的起点
        path.lineTo(0, height);
        path.lineTo(width, height);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
        imgTriangle.setImageBitmap(bitmap);
    }

    private void initDate() {
        //用户信息
        user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null) {
            Glide.with(this)
                    .load(ImagePath.buildPath(user.getAvatar())
                            .width(avatarSize)
                            .cropPath())
                    .into(imgAvatar);
            tvNick.setText(user.getNick());
            DateTime weddingDay = null;
            if (user.getWeddingDay() != null) {
                weddingDay = new DateTime(user.getWeddingDay()
                        .getTime());
            }
            tvDate.setText(HljTimeUtils.getWeddingDate(weddingDay,
                    user.getIsPending(),
                    user.getGender() == 1,
                    "yyyy/MM/dd"));
            if (TextUtils.isEmpty(user.getSpecialty()) || user.getSpecialty()
                    .equals("普通用户")) {
                if (user.getMember() != null) {
                    imgVip.setVisibility(View.VISIBLE);
                    imgVip.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    imgVip.setVisibility(View.GONE);
                }
            } else {
                imgVip.setVisibility(View.VISIBLE);
                imgVip.setImageResource(R.mipmap.icon_vip_yellow_28_28);
            }
        }
        //婚纱照信息
        if (draft != null) {
            if (!TextUtils.isEmpty(draft.getMerchantJson())) {
                merchant = GsonUtil.getGsonInstance()
                        .fromJson(draft.getMerchantJson(), Merchant.class);
            }
            setMerchantFooterView();
            CommunityThread thread = new CommunityThread();
            ArrayList<WeddingPhotoItem> items = new ArrayList<>();
            for (WeddingPhotoItemDraft itemDraft : draft.getWeddingPhotoItems()) {
                WeddingPhotoItem weddingPhotoItem = new WeddingPhotoItem();
                weddingPhotoItem.setPhotos(itemDraft.getPhotos());
                weddingPhotoItem.setDescription(itemDraft.getDescription());
                items.add(weddingPhotoItem);
            }
            thread.setWeddingPhotoItems(items);
            adapter.setThread(thread);
            tvPhotoTitle.setText(draft.getTitle());
            if (!TextUtils.isEmpty(draft.getPreface())) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(draft.getPreface());
            }
            if (draft.getCover() != null) {
                Glide.with(this)
                        .load(ImagePath.buildPath(draft.getCover()
                                .getPath())
                                .width(coverWidth)
                                .height(coverHeight)
                                .path())
                        .into(imgCover);
            }
            groups.clear();
            groups.addAll(draft.getWeddingPhotoItems());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置底部商家信息
     */
    public void setMerchantFooterView() {
        if (merchant != null && merchant.getId() > 0) {
            merchantViewHolder.merchantView.setVisibility(View.VISIBLE);
            merchantViewHolder.merchantInfoLayout.setVisibility(View.VISIBLE);
            merchantViewHolder.merchantInfoLayout2.setVisibility(View.GONE);
            setMerchantView();
            merchantViewHolder.merchantInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeddingPhotoPreviewActivity.this,
                            MerchantDetailActivity.class);
                    intent.putExtra("id", merchant.getId());
                    startActivity(intent);
                }
            });
        } else if (!TextUtils.isEmpty(draft.getUnrecordedMerchantName())) {
            merchantViewHolder.merchantView.setVisibility(View.VISIBLE);
            merchantViewHolder.merchantInfoLayout.setVisibility(View.GONE);
            merchantViewHolder.merchantInfoLayout2.setVisibility(View.VISIBLE);
            merchantViewHolder.tvMerchantName2.setText(draft.getUnrecordedMerchantName());
        } else {
            merchantViewHolder.merchantView.setVisibility(View.GONE);
        }
    }

    private void setMerchantView() {
        if (merchant == null) {
            return;
        }
        merchantViewHolder.ivBond.setVisibility(merchant.getBondSign() != null ? View.VISIBLE :
                View.GONE);
        int res = 0;
        switch (merchant.getGrade()) {
            case 2:
                res = R.mipmap.icon_merchant_level2_32_32;
                break;
            case 3:
                res = R.mipmap.icon_merchant_level3_32_32;
                break;
            case 4:
                res = R.mipmap.icon_merchant_level4_32_32;
                break;
            default:
                break;
        }
        if (res != 0) {
            merchantViewHolder.ivLevel.setVisibility(View.VISIBLE);
            merchantViewHolder.ivLevel.setImageResource(res);
        } else {
            merchantViewHolder.ivLevel.setVisibility(View.GONE);
        }
        merchantViewHolder.tvMerchantName.setText(merchant.getName());
        merchantViewHolder.tvMerchantAddress.setText(merchant.getAddress());
        Glide.with(this)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(CommonUtil.dp2px(this, 60))
                        .height(CommonUtil.dp2px(this, 60))
                        .path())
                .apply(new RequestOptions().dontAnimate())
                .into(merchantViewHolder.ivMerchantAvatar);
        if (merchant.getCommentStatistics() != null) {
            merchantViewHolder.ratingViewMerchant.setRating((float) merchant.getCommentStatistics()
                    .getScore());
        }
        if (merchant.getCommentCount() > 0) {
            merchantViewHolder.tvMerchantCommentCount.setText(getString(R.string
                            .label_comment_count5,
                    merchant.getCommentCount()));
        } else {
            merchantViewHolder.tvMerchantCommentCount.setText(R.string.label_no_comment2);
        }
    }

    private void createWeddingPhotos() {
        //上传时，清楚draft中的无用信息
        draft.setMerchantJson(null);

        createSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<BasePostResult>() {
                    @Override
                    public void onNext(BasePostResult result) {
                        deleteDraft();
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.FINISH_CREATE_PHOTO, null));
                        Intent intent = new Intent(WeddingPhotoPreviewActivity.this,
                                WeddingReleaseSuccessActivity.class);
                        intent.putExtra("title", draft.getTitle());
                        intent.putExtra("id", result.getId());
                        intent.putExtra("gold", result.getGold());
                        intent.putExtra("url",
                                draft.getCover()
                                        .getPath());
                        boolean showMerchant = true;
                        if ((draft.getMerchantId() != null && draft.getMerchantId() != 0) ||
                                !TextUtils.isEmpty(
                                draft.getUnrecordedMerchantName())) {
                            showMerchant = false;
                        }
                        intent.putExtra("showMerchant", showMerchant);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        CommunityApi.createWeddingPhoto(draft)
                .subscribe(createSubscriber);
    }

    /**
     * 初始化未上传的图片
     */
    private void initWeddingPhotos() {
        totalCount = 0;
        localPhotos.clear();
        RealmJsonPic cover = draft.getCover();
        if (cover != null && !TextUtils.isEmpty(cover.getPath())) {
            totalCount++;
            if (!cover.getPath()
                    .startsWith("http://") && !cover.getPath()
                    .startsWith("https://")) {
                RealmJsonPic coverPic = new RealmJsonPic();
                coverPic.setPath(cover.getPath());
                //封面图是新建的对象，需要有个标识，上传完后若第一张图片的id=-1，
                //则表示该图片是封面图，需要重新赋值给draft
                coverPic.setId(-1);
                localPhotos.add(coverPic);
            }
        }
        if (draft.getWeddingPhotoItems() != null) {
            for (WeddingPhotoItemDraft itemDraft : draft.getWeddingPhotoItems()) {
                for (RealmJsonPic pic : itemDraft.getPics()) {
                    totalCount++;
                    if (!TextUtils.isEmpty(pic.getPath()) && !pic.getPath()
                            .startsWith("http://") && !pic.getPath()
                            .startsWith("https://")) {
                        localPhotos.add(pic);
                    }
                }
            }
        }
        tvPhotoCount.setText(getString(R.string.label_wedding_photo_count, totalCount - 1));
    }

    /**
     * 晒婚纱照组图批量上传
     */
    private void onUploadAllPhotos() {
        if (draft != null) {
            if (subscriptions == null) {
                subscriptions = new ArrayList<>();
            }
            if (!CommonUtil.isCollectionEmpty(localPhotos)) {
                if (progressDialog == null) {
                    progressDialog = DialogUtil.createUploadDialog(this);
                    progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            unSubscribeUploads();
                        }
                    });
                    progressDialog.setOnConfirmClickListener(new HljHorizontalProgressDialog
                            .OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick() {
                            if (CommonUtil.isNetworkConnected(WeddingPhotoPreviewActivity
                                    .this)) {
                                onUploadAllPhotos();
                            } else {
                                ToastUtil.showToast(WeddingPhotoPreviewActivity
                                        .this, "网络异常，请检查网络！", 0);
                            }
                        }
                    });
                }
                progressDialog.setTotalCount(totalCount);
                progressDialog.show();
                progressDialog.setUploadPosition(totalCount - localPhotos.size());
                if (CommonUtil.isNetworkConnected(WeddingPhotoPreviewActivity
                        .this)) {
                    progressDialog.showUploadView();
                } else {
                    progressDialog.showNetworkErrorView();
                }
                if (allUpLoad == null) {
                    allUpLoad = new WeddingPhotoListUploader(this, subscriptions);
                    allUpLoad.setProgressBar(progressDialog);
                }
                allUpLoad.setPhotos(localPhotos);
                allUpLoad.startUploadPhoto();
                allUpLoad.setOnFinishedListener(new WeddingPhotoListUploader.OnFinishedListener() {
                    @Override
                    public void onFinish(RealmList<RealmJsonPic> resultPics, int currentIndex) {
                        if (currentIndex >= localPhotos.size() - 1) {
                            if (!CommonUtil.isCollectionEmpty(resultPics)) {
                                if (resultPics.get(0)
                                        .getId() == -1) {
                                    draft.setCover(resultPics.get(0));
                                }
                            }
                            createWeddingPhotos();
                        } else {
                            unSubscribeUploads();
                            progressDialog.showNetworkErrorView();
                        }
                    }
                });
            } else {
                createWeddingPhotos();
            }
        }
    }

    /**
     * 删除草稿
     */
    private void deleteDraft() {
        if (user != null) {
            Realm realm = Realm.getDefaultInstance();
            WeddingPhotoDraft realmResult = realm.where(WeddingPhotoDraft.class)
                    .equalTo("userId", user.getId())
                    .findFirst();
            if (realmResult != null) {
                realm.beginTransaction();
                realmResult.deleteFromRealm();
                realm.commitTransaction();
            }
            realm.close();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(createSubscriber);
        unSubscribeUploads();
    }

    private void unSubscribeUploads() {
        if (!CommonUtil.isCollectionEmpty(subscriptions)) {
            for (Subscription subscription : subscriptions) {
                CommonUtil.unSubscribeSubs(subscription);
            }
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        onUploadAllPhotos();
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (draft != null) {
            Intent intent = getIntent();
            intent.putExtra("draft", draft);
            setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    class MerchantViewHolder {
        @BindView(R.id.iv_merchant_avatar)
        RoundedImageView ivMerchantAvatar;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.iv_bond)
        ImageView ivBond;
        @BindView(R.id.rating_view_merchant)
        RatingBar ratingViewMerchant;
        @BindView(R.id.tv_merchant_comment_count)
        TextView tvMerchantCommentCount;
        @BindView(R.id.tv_merchant_address)
        TextView tvMerchantAddress;
        @BindView(R.id.iv_merchant_arrow)
        ImageView ivMerchantArrow;
        @BindView(R.id.merchant_info_layout)
        RelativeLayout merchantInfoLayout;
        @BindView(R.id.iv_merchant_avatar_2)
        RoundedImageView ivMerchantAvatar2;
        @BindView(R.id.tv_merchant_name_2)
        TextView tvMerchantName2;
        @BindView(R.id.merchant_info_layout_2)
        RelativeLayout merchantInfoLayout2;
        @BindView(R.id.merchant_view)
        View merchantView;

        MerchantViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
