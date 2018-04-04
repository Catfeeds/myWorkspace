package me.suncloud.marrymemo.view.merchant;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.HotelMerchantBriefInfoViewHolder;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.HotelHallImageListAdapter;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.wrappers.HljHttpHotelHallData;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.HotelCalendarActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * 宴会厅详情页
 * Created by chen_bin on 2017/10/18 0018.
 */
public class HotelHallDetailActivity extends HljBaseNoBarActivity {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_table_count)
    TextView tvTableCount;
    @BindView(R.id.tv_best_table_count)
    TextView tvBestTableCount;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_pillar)
    TextView tvPillar;
    @BindView(R.id.tv_shape)
    TextView tvShape;
    @BindView(R.id.merchant_layout)
    RelativeLayout merchantLayout;
    @BindView(R.id.tv_section_header)
    TextView tvSectionHeader;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.shadow_view)
    FrameLayout shadowView;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.btn_message)
    ImageButton btnMessage;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.divider_view)
    View dividerView;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.img_collect)
    ImageView imgCollect;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.collect_layout)
    LinearLayout collectLayout;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private HotelHallImageListAdapter adapter;
    private NoticeUtil noticeUtil;
    private Merchant merchant;
    private HotelHall hotelHall;
    private long id;
    private float barAlpha;
    private int imageWidth;
    private int imageHeight;
    private int alphaHeight;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber collectSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_hall_detail);
        ButterKnife.bind(this);
        initValues();
        initActionBar();
        initViews();
        initLoad();
    }

    private void initValues() {
        id = getIntent().getLongExtra("id", 0);
        imageWidth = CommonUtil.getDeviceSize(this).x;
        imageHeight = Math.round(imageWidth * 10.0f / 16.0f);
        alphaHeight = imageHeight - CommonUtil.dp2px(this, 45) - getStatusBarHeight();
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        setActionBarPadding(actionHolderLayout, shadowView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad();
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotelHallImageListAdapter(this);
        recyclerView.setAdapter(adapter);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (-verticalOffset >= alphaHeight) {
                    setActionBarAlpha(1);
                } else {
                    setActionBarAlpha((float) -verticalOffset / alphaHeight);
                }
            }
        });
        noticeUtil = new NoticeUtil(this, tvMsgCount, msgNoticeView);
        noticeUtil.onResume();
    }

    private void initLoad() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpHotelHallData>() {
                        @Override
                        public void onNext(HljHttpHotelHallData hotelHallData) {
                            headerLayout.setVisibility(View.VISIBLE);
                            bottomLayout.setVisibility(View.VISIBLE);
                            setMerchantData(hotelHallData);
                            setHotelHallData(hotelHallData);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            setActionBarAlpha(1);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .setContentView(recyclerView)
                    .build();
            MerchantApi.getHotelHallDetailObb(id)
                    .subscribe(initSub);
        }
    }

    private void setHotelHallData(HljHttpHotelHallData hotelHallData) {
        hotelHall = hotelHallData.getHotelHall();
        if (hotelHall == null) {
            return;
        }
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        Glide.with(this)
                .load(ImagePath.buildPath(hotelHall.getCoverUrl())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvToolbarTitle.setText(hotelHall.getName());
        tvTitle.setText(hotelHall.getName());
        tvArea.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_area, hotelHall.getArea())));
        tvTableCount.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_table_count, hotelHall.getTableNum())));
        tvBestTableCount.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_best_table_count, hotelHall.getBestTableNum())));
        tvHeight.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_height, hotelHall.getHeight())));
        tvPillar.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_pillar, hotelHall.getPillar())));
        tvShape.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_hotel_hall_shape, hotelHall.getShape())));
        List<Photo> photos = null;
        if (!CommonUtil.isCollectionEmpty(hotelHall.getItems())) {
            photos = new ArrayList<>(hotelHall.getItems()
                    .subList(1,
                            hotelHall.getItems()
                                    .size()));
        }
        tvSectionHeader.setVisibility(CommonUtil.isCollectionEmpty(photos) ? View.GONE : View
                .VISIBLE);
        adapter.setPhotos(photos);
    }

    private void setMerchantData(HljHttpHotelHallData hotelHallData) {
        merchant = hotelHallData.getMerchant();
        if (merchant == null) {
            return;
        }
        merchantLayout.setVisibility(View.VISIBLE);
        if (merchantLayout.getChildCount() == 0) {
            View.inflate(this, R.layout.hotel_merchant_brief_info_list_item___cv, merchantLayout);
        }
        View merchantView = merchantLayout.getChildAt(merchantLayout.getChildCount() - 1);
        HotelMerchantBriefInfoViewHolder holder = (HotelMerchantBriefInfoViewHolder) merchantView
                .getTag();
        if (holder == null) {
            holder = new HotelMerchantBriefInfoViewHolder(merchantView);
            merchantView.setTag(holder);
        }
        holder.setView(this, merchant, 0, 0);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_message)
    public void onMessage() {
        if (AuthUtil.loginBindCheck(this)) {
            startActivity(new Intent(this, MessageHomeActivity.class));
        }
    }

    @OnClick(R.id.btn_share)
    public void onShare() {
        if (merchant != null && merchant.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(this, merchant.getShareInfo());
        }
    }

    @OnClick(R.id.btn_schedule)
    public void onSchedule() {
        if (merchant == null || merchant.getId() == 0 || hotelHall == null || hotelHall.getId()
                == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, HotelCalendarActivity.class);
        intent.putExtra("id", merchant.getId());
        intent.putExtra("hall_id", hotelHall.getId());
        startActivity(intent);
    }

    @OnClick(R.id.call_layout)
    void onCall() {
        if (merchant == null) {
            return;
        }
        SupportUtil.getInstance(this)
                .getSupport(this,
                        Support.SUPPORT_KIND_HOTEL,
                        new SupportUtil.SimpleSupportCallback() {
                            @Override
                            public void onSupportCompleted(Support support) {
                                super.onSupportCompleted(support);
                                if (isFinishing()) {
                                    return;
                                }
                                if (support != null && !TextUtils.isEmpty(support.getPhone())) {
                                    String phone = support.getPhone();
                                    try {
                                        callUp(Uri.parse("tel:" + phone.trim()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailed() {
                                super.onFailed();
                                if (isFinishing()) {
                                    return;
                                }
                                ToastUtil.showToast(HotelHallDetailActivity.this,
                                        null,
                                        R.string.msg_get_supports_error);
                            }
                        });
    }

    @OnClick(R.id.chat_layout)
    void onChat() {
        if (merchant == null || merchant.getId() == 0) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_HOTEL, merchant);
    }

    @OnClick(R.id.collect_layout)
    void onCollect() {
        if (merchant == null) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (collectSub == null || collectSub.isUnsubscribed()) {
            collectSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            if (merchant.isCollected()) {
                                if (Util.isNewFirstCollect(HotelHallDetailActivity.this, 6)) {
                                    Util.showFirstCollectNoticeDialog(HotelHallDetailActivity.this,
                                            6);
                                } else {
                                    ToastUtil.showCustomToast(HotelHallDetailActivity.this,
                                            R.string.msg_success_to_collect___cm);
                                }
                            } else {
                                ToastUtil.showCustomToast(HotelHallDetailActivity.this,
                                        R.string.msg_success_to_un_collect___cm);
                            }
                            Intent intent = getIntent();
                            intent.putExtra("is_followed", merchant.isCollected());
                            setResult(RESULT_OK, intent);
                        }
                    })
                    .build();
            if (merchant.isCollected()) {
                merchant.setCollected(false);
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_normal);
                tvCollect.setText(R.string.label_collect___cm);
                CommonApi.deleteMerchantFollowObb(merchant.getId())
                        .subscribe(collectSub);
            } else {
                merchant.setCollected(true);
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
                tvCollect.setText(R.string.label_collected___cm);
                CommonApi.postMerchantFollowObb(merchant.getId())
                        .subscribe(collectSub);

            }
        }
    }

    @OnClick(R.id.btn_merchant)
    void onMerchant() {
        if (merchant != null && merchant.getId() > 0) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra("id", merchant.getId());
            startActivity(intent);
        }
    }

    private void setActionBarAlpha(float alpha) {
        if (barAlpha == alpha) {
            return;
        }
        barAlpha = alpha;
        shadowView.setAlpha(1 - alpha);
        tvToolbarTitle.setAlpha(alpha);
        btnBack.setAlpha(alpha);
        btnMessage.setAlpha(alpha);
        btnShare.setAlpha(alpha);
        dividerView.setAlpha(alpha);
        int red = Color.red(0xffffffff);
        int green = Color.green(0xffffffff);
        int blue = Color.blue(0xffffffff);
        int a = (int) (Color.alpha(0xffffffff) * alpha);
        toolbar.setBackgroundColor(Color.argb(a, red, green, blue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, collectSub);
    }
}