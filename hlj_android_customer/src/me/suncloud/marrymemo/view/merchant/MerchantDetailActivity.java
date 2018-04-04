package me.suncloud.marrymemo.view.merchant;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljchatlibrary.utils.ChatUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.behavior.AppBarLayoutOverScrollViewBehavior;
import com.hunliji.hljcommonlibrary.interfaces.OnFinishedListener;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantChatData;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantRecommendPosterItem;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.adapters.GroupItemDecoration;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonGroupHeaderViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallEventViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljcommonviewlibrary.models.GroupAdapterHeader;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.hljmaplibrary.views.activities.NavigateMapActivity;
import com.hunliji.hljnotelibrary.api.NoteApi;
import com.hunliji.hljnotelibrary.views.activities.MerchantNoteActivity;
import com.hunliji.hljnotelibrary.views.activities.PostServiceCommentActivity;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.makeramen.rounded.RoundedImageView;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HLJCustomerApplication;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentMarksViewHolder;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentViewHolder;
import me.suncloud.marrymemo.adpter.merchant.MerchantHomeAdapter;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeCommentMarksHeaderViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeInstallmentViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeNoticeViewHolder;
import me.suncloud.marrymemo.api.comment.CommentApi;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.api.work_case.WorkApi;
import me.suncloud.marrymemo.fragment.merchant.ExclusiveOfferFragment;
import me.suncloud.marrymemo.fragment.merchant.MerchantDescriptionDetailFragment;
import me.suncloud.marrymemo.fragment.merchant.SubscribeHotelFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.merchant.wrappers.MerchantChatLinkTriggerPostBody;
import me.suncloud.marrymemo.model.wallet.CouponRecord;
import me.suncloud.marrymemo.model.wrappers.HljHttpCommentsData;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.merchant.AppointmentUtil;
import me.suncloud.marrymemo.util.merchant.ChatBubbleTimer;
import me.suncloud.marrymemo.util.merchant.MerchantTogglesUtil;
import me.suncloud.marrymemo.view.CommentMerchantActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.comment.ServiceCommentDetailActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.orders.HotelPeriodFillOrderActivity;
import me.suncloud.marrymemo.view.work_case.MerchantCaseListActivity;
import me.suncloud.marrymemo.view.work_case.MerchantWorkListActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.merchant.MerchantCouponDialog;
import me.suncloud.marrymemo.widget.merchant.UseCouponDialog;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;

/**
 * 商家主页
 * Created by wangtao on 2017/9/22.
 */

@Route(path = RouterPath.IntentPath.Customer.MERCHANT_HOME)
public class MerchantDetailActivity extends HljBaseNoBarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.shadow_view)
    FrameLayout shadowView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.iv_top_avatar)
    RoundedImageView ivTopAvatar;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.btn_message)
    ImageView btnMessage;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.merchant_info_list)
    RecyclerView rcMerchantInfo;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.chat_bubble_layout)
    LinearLayout chatBubbleLayout;
    @BindView(R.id.chat_click_layout)
    RelativeLayout chatClickLayout;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.layout_call)
    LinearLayout layoutCall;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.avatar_shadow_layout)
    FrameLayout avatarShadowLayout;
    @BindView(R.id.iv_ultimate_tag)
    ImageView ivUltimateTag;
    @BindView(R.id.layout_avatar)
    RelativeLayout layoutAvatar;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.tv_works_count)
    TextView tvWorksCount;
    @BindView(R.id.cb_work)
    CheckableLinearLayoutButton cbWork;
    @BindView(R.id.tv_cases_count)
    TextView tvCasesCount;
    @BindView(R.id.cb_case)
    CheckableLinearLayoutButton cbCase;
    @BindView(R.id.tv_halls_count)
    TextView tvHallsCount;
    @BindView(R.id.cb_hall)
    CheckableLinearLayoutButton cbHall;
    @BindView(R.id.tv_menus_count)
    TextView tvMenusCount;
    @BindView(R.id.cb_menu)
    CheckableLinearLayoutButton cbMenu;
    @BindView(R.id.tv_comments_count)
    TextView tvCommentsCount;
    @BindView(R.id.tab_layout)
    FrameLayout tabLayout;
    @BindView(R.id.cb_info)
    CheckableLinearLayoutButton cbInfo;
    @BindView(R.id.cb_comment)
    CheckableLinearLayoutButton cbComment;
    @BindView(R.id.iv_cover_line)
    View ivCoverLine;

    private MerchantInfoViewHolder merchantInfoViewHolder;

    private Merchant merchant;
    private MerchantHomeAdapter adapter;

    private Unbinder unbinder;
    private LinearLayoutManager layoutManager;

    private long id;
    private int color;
    private float barAlpha;
    private boolean isCommented;
    private boolean showUltimateTag;
    private NoticeUtil noticeUtil;
    private CouponRecord couponRecord;
    private int verticalOffset;


    private int currentPage;
    private int pageCount;
    private View loadView;
    private View endView;
    private View bottomMoreLayout;
    private View pageFooterLayout;

    private String commentContent;// 存储用户输入过的回复内容
    private long lastUserId;// 存储上一次的userId
    private Dialog commentDeleteDialog;
    private ServiceCommentViewHolder commentViewHolder;
    private ServiceComment comment;
    private RepliedComment repliedComment;

    private long markId;

    private Dialog noticeDialog;
    private MerchantCouponDialog couponDialog;

    private Subscription loadSubscription;
    private Subscription followSubscription;
    private Subscription posterSubscription;
    private Subscription noteSubscription;
    private Subscription subInfoSubscription;
    private Subscription commentSubscription;
    private Subscription rxSubscription;
    private Subscription authorizedSubscription;
    private HljHttpSubscriber praiseSub;
    private HljHttpSubscriber deleteSub;

    //轻松聊
    public static final int SCROLL_Y_DELTA = 1300; // 页面滑动距离触发轻松聊
    public static final int TIME_STAYED_IN_PAGE = MerchantChatLinkTriggerPostBody
            .TIME_TILL_SHOWING * 1000;
    public static final int TIME_HIDE_AFTER = MerchantChatLinkTriggerPostBody.TIME_TILL_HIDE * 1000;
    public int scrollStartDelta; // 页面滑动距离触发轻松聊

    private ChatBubbleTimer bubbleTimer;
    private HljHttpSubscriber chatDataSub;
    private HljHttpSubscriber chatTrigSub;
    private Subscription appointmentSub;

    public final static String ARG_ID = "id";

    private class ThemeType {
        static final int Default = 0;
        static final int WhiteGolden = 1;
        static final int DarkGolden = 2;
        static final int Green = 3;
        static final int Blue = 4;
        static final int Pink = 5;
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    TrackerHelper.postShareAction(MerchantDetailActivity.this,
                            merchant.getId(),
                            "merchant");
                    new HljTracker.Builder(MerchantDetailActivity.this).eventableId(merchant
                            .getUserId())
                            .eventableType("Merchant")
                            .screen("merchant_detail")
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
    public String pageTrackTagName() {
        return "服务商家主页";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(id, "Merchant");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);
        unbinder = ButterKnife.bind(this);
        initValues();
        initActionBar();
        initLoad(id);
        registerRxBusEvent();
        registerAuthorizedRxBusEvent();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    setSwipeBackEnable(true);
                } else {
                    setSwipeBackEnable(false);
                }
            }
        });
    }

    private void initValues() {
        id = getIntent().getLongExtra(ARG_ID, 0);
        couponRecord = getIntent().getParcelableExtra("couponRecord");
        color = ThemeUtil.getAttrColor(this, R.attr.hljColorBarBackground, 0);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        setActionBarPadding(actionHolderLayout, shadowView);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void registerAuthorizedRxBusEvent() {
        if (authorizedSubscription == null || authorizedSubscription.isUnsubscribed()) {
            authorizedSubscription = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent payRxEvent) {
                            switch (payRxEvent.getType()) {
                                case HAD_AUTHORIZED:
                                    // 已经获得授信
                                    Intent intent = new Intent(MerchantDetailActivity.this,
                                            HotelPeriodFillOrderActivity.class);
                                    intent.putExtra(HotelPeriodFillOrderActivity.ARG_MERCHANT,
                                            merchant);
                                    startActivity(intent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    private void registerRxBusEvent() {
        if (rxSubscription == null || rxSubscription.isUnsubscribed()) {
            rxSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case MEASURE_KEYBOARD_HEIGHT:
                                    if (commentViewHolder != null) {
                                        int position = commentViewHolder.getAdapterPosition();
                                        int offset = getScrollPositionOffset((int) rxEvent
                                                .getObject());
                                        layoutManager.scrollToPositionWithOffset(position, offset);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    //获取偏移量
    private int getScrollPositionOffset(int keyboardHeight) {
        int offset = rcMerchantInfo.getMeasuredHeight() + bottomLayout.getMeasuredHeight() -
                commentViewHolder.itemView.getMeasuredHeight() - keyboardHeight;
        if (repliedComment != null) {
            offset = offset + commentViewHolder.commentLayout.getMeasuredHeight() + CommonUtil
                    .dp2px(
                    this,
                    8);
            for (int i = 0, size = comment.getRepliedComments()
                    .indexOf(repliedComment); i <= size; i++) {
                offset = offset - commentViewHolder.commentListLayout.getChildAt(i)
                        .getMeasuredHeight();
            }
        }
        return offset;
    }

    private void initView() {
        initActionBar();

        View merchantInfoView = View.inflate(this, R.layout.merchant_info_header, null);
        merchantInfoViewHolder = new MerchantInfoViewHolder(merchantInfoView);

        View footerView = View.inflate(this, R.layout.service_comment_footer_layout, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        bottomMoreLayout = footerView.findViewById(R.id.bottom_more_layout);
        pageFooterLayout = footerView.findViewById(R.id.page_footer_layout);
        loadView.setVisibility(View.INVISIBLE);
        bottomMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.showOtherComments();
                bottomMoreLayout.setVisibility(View.GONE);
                pageFooterLayout.setVisibility(View.VISIBLE);
            }
        });

        final int ivCoverHeight = Math.round(CommonUtil.getDeviceSize(this).x * 7.0f / 16.0f);
        int logoSize = Math.round(CommonUtil.getDeviceSize(this).x / 5.0f);
        int ultimateTagSize = Math.round(logoSize * 294.0f / 200.0f);

        ivCover.getLayoutParams().height = ivCoverHeight;
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appbar == null) {
                    return;
                }
                MerchantDetailActivity.this.verticalOffset = verticalOffset;
                if (appbar.getTotalScrollRange() > Math.abs(verticalOffset)) {
                    cbInfo.setChecked(true);
                    setActionBarAlpha((float) -verticalOffset / appbar.getTotalScrollRange());
                } else {
                    onScrollTabChange(rcMerchantInfo);
                    setActionBarAlpha(1);
                }
            }
        });

        try {
            AppBarLayoutOverScrollViewBehavior overScrollViewBehavior =
                    (AppBarLayoutOverScrollViewBehavior) ((CoordinatorLayout.LayoutParams) appbar
                            .getLayoutParams()).getBehavior();
            if (overScrollViewBehavior != null) {
                overScrollViewBehavior.setMaxOverScroll(ivCoverHeight * 7 / 10);
                overScrollViewBehavior.addOnOverScrollListener(new AppBarLayoutOverScrollViewBehavior.OnOverScrollListener() {
                    @Override
                    public void onOverScrollBy(int height, int overScroll) {
                        layoutHeader.setTop(overScroll);
                        layoutHeader.setBottom(layoutHeader.getHeight() + overScroll);
                        ViewHelper.setPivotX(ivCover, ivCover.getWidth() / 2);
                        ViewHelper.setPivotY(ivCover, ivCover.getHeight());
                        ivCover.setScaleY(1 + ((float) overScroll) / ivCoverHeight);
                        ivCover.setScaleX(1 + ((float) overScroll) / ivCoverHeight);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        ivAvatar.getLayoutParams().width = logoSize;
        ivAvatar.getLayoutParams().height = logoSize;
        ivUltimateTag.getLayoutParams().width = ultimateTagSize;
        ivUltimateTag.getLayoutParams().height = ultimateTagSize;

        layoutManager = new LinearLayoutManager(this);
        rcMerchantInfo.setLayoutManager(layoutManager);
        rcMerchantInfo.setItemAnimator(null);
        adapter = new MerchantHomeAdapter(this);
        rcMerchantInfo.setAdapter(adapter);
        rcMerchantInfo.addItemDecoration(new GroupItemDecoration(CommonUtil.dp2px(this, 10)));


        adapter.setMerchantInfoView(merchantInfoView);
        adapter.setCommentFooterView(footerView);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object object) {
                if (object == null) {
                    return;
                }
                if (object instanceof Work) {
                    Intent intent = new Intent(MerchantDetailActivity.this, WorkActivity.class);
                    intent.putExtra("id", ((Work) object).getId());
                    startActivity(intent);
                } else if (object instanceof Merchant) {
                    onMerchantDescribeDetail();
                } else if (object instanceof Question) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            AskQuestionListActivity.class);
                    intent.putExtra("merchant_id", merchant.getId());
                    startActivity(intent);
                } else if (object instanceof ServiceComment) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            ServiceCommentDetailActivity.class);
                    intent.putExtra("id", ((ServiceComment) object).getId());
                    startActivity(intent);
                } else if (object instanceof HotelHall) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            HotelHallDetailActivity.class);
                    intent.putExtra("id", ((HotelHall) object).getId());
                    startActivityForResult(intent, Constants.RequestCode.HOTEL_COLLECT);
                } else if (object instanceof HotelMenu) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            HotelMenuActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("merchant", merchant);
                    startActivity(intent);
                }
            }
        });

        adapter.setHeaderClickListener(new CommonGroupHeaderViewHolder.GroupHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupAdapterHeader header) {
                switch (header.getGroupType()) {
                    case MerchantHomeAdapter.GroupType.Work:
                        Intent intent = new Intent(MerchantDetailActivity.this,
                                MerchantWorkListActivity.class);
                        intent.putExtra("id", merchant.getId());
                        intent.putExtra("style", SmallWorkViewHolder.STYLE_MERCHANT_HOME_PAGE);
                        startActivity(intent);
                        break;
                    case MerchantHomeAdapter.GroupType.Case:
                        intent = new Intent(MerchantDetailActivity.this,
                                MerchantCaseListActivity.class);
                        intent.putExtra("id", merchant.getId());
                        startActivity(intent);
                        break;
                    case MerchantHomeAdapter.GroupType.Note:
                        intent = new Intent(MerchantDetailActivity.this,
                                MerchantNoteActivity.class);
                        intent.putExtra("merchant", merchant);
                        startActivity(intent);
                        break;
                    case MerchantHomeAdapter.GroupType.Describe:
                        onMerchantDescribeDetail();
                        break;
                    case MerchantHomeAdapter.GroupType.Question:
                        intent = new Intent(MerchantDetailActivity.this,
                                AskQuestionListActivity.class);
                        intent.putExtra("merchant_id", merchant.getId());
                        startActivity(intent);
                        break;
                    case MerchantHomeAdapter.GroupType.Hall:
                        intent = new Intent(MerchantDetailActivity.this, HotelHallActivity.class);
                        intent.putExtra("merchant", merchant);
                        startActivityForResult(intent, Constants.RequestCode.HOTEL_COLLECT);
                        break;
                    case MerchantHomeAdapter.GroupType.Menu:
                        intent = new Intent(MerchantDetailActivity.this, HotelMenuActivity.class);
                        intent.putExtra("merchant", merchant);
                        startActivity(intent);
                        break;
                }

            }
        });

        adapter.setNoticeClickListener(new MerchantHomeNoticeViewHolder.NoticeClickListener() {
            @Override
            public void onNoticeClick() {
                if (noticeDialog != null && noticeDialog.isShowing()) {
                    return;
                }
                if (noticeDialog == null) {
                    noticeDialog = me.suncloud.marrymemo.util.DialogUtil.createNoticeDlg(
                            MerchantDetailActivity.this,
                            merchant);
                }
                noticeDialog.show();
            }

            @Override
            public void onGiftClick() {
                onReservation();
            }

            @Override
            public void onCouponClick() {
                if (!AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                    return;
                }
                if (couponDialog != null && couponDialog.isShowing()) {
                    return;
                }
                if (couponDialog == null) {
                    couponDialog = new MerchantCouponDialog(MerchantDetailActivity.this,
                            merchant.getId(),
                            merchant.getUserId());
                }
                couponDialog.getCoupons();
            }

            @Override
            public void onExclusiveOfferClick() {
                if (merchant == null) {
                    return;
                }
                if (!AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                    return;
                }
                ExclusiveOfferFragment fragment = ExclusiveOfferFragment.newInstance(merchant);
                fragment.show(getSupportFragmentManager(), "ExclusiveOfferFragment");
            }

            @Override
            public void onSubscribeHotelClick() {
                if (merchant == null) {
                    return;
                }
                if (!AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                    return;
                }
                SubscribeHotelFragment fragment = SubscribeHotelFragment.newInstance(merchant);
                fragment.show(getSupportFragmentManager(), "SubscribeHotelFragment");
            }
        });

        adapter.setOnPraiseListener(new ServiceCommentViewHolder.OnPraiseListener() {
            @Override
            public void onPraise(
                    final ServiceCommentViewHolder commentViewHolder,
                    final ServiceComment comment) {
                if (comment != null && comment.getId() > 0) {
                    praiseSub = MerchantTogglesUtil.getInstance()
                            .onServiceOrderCommentPraise(MerchantDetailActivity.this,
                                    comment,
                                    commentViewHolder.checkPraised,
                                    commentViewHolder.imgPraise,
                                    commentViewHolder.tvPraiseCount,
                                    praiseSub,
                                    new OnFinishedListener() {
                                        @Override
                                        public void onFinished(Object... objects) {
                                            User user = Session.getInstance()
                                                    .getCurrentUser(MerchantDetailActivity.this);
                                            final Author author = new Author();
                                            author.setId(user.getId());
                                            author.setName(user.getNick());
                                            author.setAvatar(user.getAvatar());
                                            if (!comment.isLike()) {
                                                commentViewHolder.removePraisedUser(comment,
                                                        author);
                                            } else {
                                                comment.getPraisedUsers()
                                                        .add(author);
                                                commentViewHolder.addPraisedUsers(comment);
                                            }
                                        }
                                    });
                }
            }
        });

        adapter.setOnCommentListener(new ServiceCommentViewHolder.OnCommentListener() {
            @Override
            public void onComment(
                    ServiceCommentViewHolder commentViewHolder,
                    ServiceComment comment,
                    RepliedComment repliedComment) {
                if (!AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                    return;
                }
                if (comment.getRating() <= 2) {
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            ServiceCommentDetailActivity.class);
                    intent.putExtra("id", comment.getId());
                    startActivity(intent);
                    return;
                }
                User user = Session.getInstance()
                        .getCurrentUser(MerchantDetailActivity.this);
                if (repliedComment != null && repliedComment.getId() > 0 && user.getId() ==
                        repliedComment.getUser()
                        .getId()) {
                    showCommentDeleteDialog(commentViewHolder, comment, repliedComment);
                } else {
                    MerchantDetailActivity.this.comment = comment;
                    MerchantDetailActivity.this.commentViewHolder = commentViewHolder;
                    MerchantDetailActivity.this.repliedComment = repliedComment;
                    long userId = repliedComment == null ? user.getId() : repliedComment.getUser()
                            .getId();
                    if (lastUserId != userId) {
                        lastUserId = userId;
                        commentContent = "";
                    }
                    Intent intent = new Intent(MerchantDetailActivity.this,
                            PostServiceCommentActivity.class);
                    intent.putExtra("comment", comment);
                    intent.putExtra("comment_content", commentContent);
                    intent.putExtra("replied_comment", repliedComment);
                    startActivityForResult(intent,
                            Constants.RequestCode.POST_SERVICE_ORDER_COMMENT);
                    overridePendingTransition(0, 0);
                }
            }
        });


        adapter.setOnCommentFilterListener(new ServiceCommentMarksViewHolder
                .OnCommentFilterListener() {
            @Override
            public void onCommentFilter(long markId) {
                if (MerchantDetailActivity.this.markId == markId) {
                    return;
                }
                MerchantDetailActivity.this.markId = markId;
                CommonUtil.unSubscribeSubs(commentSubscription);
                adapter.clearComments();
                commentSubscription = CommentApi.getMerchantCommentsObb(MerchantDetailActivity
                        .this, id, MerchantDetailActivity.this.markId, 1, HljCommon.PER_PAGE)
                        .subscribe(HljHttpSubscriber.buildSubscriber(MerchantDetailActivity.this)
                                .setProgressBar(progressBar)
                                .setOnNextListener(new SubscriberOnNextListener<HljHttpCommentsData>() {
                                    @Override
                                    public void onNext(HljHttpCommentsData commentsData) {
                                        if (commentsData != null && commentsData
                                                .getFirstSixMonthAgoIndex() > 0) {
                                            pageFooterLayout.setVisibility(View.GONE);
                                            bottomMoreLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            pageFooterLayout.setVisibility(View.VISIBLE);
                                            bottomMoreLayout.setVisibility(View.GONE);
                                        }
                                        adapter.setComments(commentsData);
                                        currentPage = 1;
                                        pageCount = commentsData == null ? 0 : commentsData
                                                .getPageCount();
                                    }
                                })
                                .build());
            }
        });

        adapter.setOnCommentEmptyClickListener(new MerchantHomeCommentMarksHeaderViewHolder
                .OnCommentEmptyClickListener() {
            @Override
            public void onComment() {
                MerchantDetailActivity.this.onComment();
            }
        });
        adapter.setOnInstallmentClickListener(new MerchantHomeInstallmentViewHolder
                .OnInstallmentClickListener() {
            @Override
            public void onInstallmentClick() {
                if (AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                    XiaoxiInstallmentAuthorization.getInstance()
                            .onAuthorization(MerchantDetailActivity.this, false);
                }
            }
        });
    }

    private void showCommentDeleteDialog(
            final ServiceCommentViewHolder commentViewHolder,
            final ServiceComment comment,
            final RepliedComment repliedComment) {
        if (commentDeleteDialog != null && commentDeleteDialog.isShowing()) {
            return;
        }
        if (commentDeleteDialog == null) {
            commentDeleteDialog = new Dialog(this, R.style.BubbleDialogTheme);
            commentDeleteDialog.setContentView(R.layout.dialog_bottom_menu___cm);
            Button btnMenu = commentDeleteDialog.findViewById(R.id.btn_menu);
            btnMenu.setText(R.string.label_delete___cm);
            commentDeleteDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commentDeleteDialog.dismiss();
                        }
                    });
            Window win = commentDeleteDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                win.setAttributes(params);
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        commentDeleteDialog.findViewById(R.id.btn_menu)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(deleteSub);
                        if (deleteSub == null || deleteSub.isUnsubscribed()) {
                            deleteSub = HljHttpSubscriber.buildSubscriber(MerchantDetailActivity
                                    .this)
                                    .setOnNextListener(new SubscriberOnNextListener() {
                                        @Override
                                        public void onNext(Object o) {
                                            comment.getRepliedComments()
                                                    .remove(repliedComment);
                                            if (commentViewHolder != null) {
                                                commentViewHolder.addComments
                                                        (MerchantDetailActivity.this,
                                                        comment);
                                            }
                                        }
                                    })
                                    .setDataNullable(true)
                                    .setProgressDialog(DialogUtil.createProgressDialog(
                                            MerchantDetailActivity.this))
                                    .build();
                            CommonApi.deleteFuncObb(repliedComment.getId())
                                    .subscribe(deleteSub);
                        }
                    }
                });
        commentDeleteDialog.show();
    }

    private void setActionBarAlpha(float alpha) {
        if (barAlpha == alpha) {
            return;
        }
        barAlpha = alpha;
        shadowView.setAlpha(1 - alpha);
        btnBack.setAlpha(alpha);
        btnMessage.setAlpha(alpha);
        btnShare.setAlpha(alpha);
        ivTopAvatar.setAlpha(alpha);
        tabLayout.setAlpha(alpha);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int a = (int) (Color.alpha(color) * alpha);
        toolbar.setBackgroundColor(Color.argb(a, red, green, blue));
    }

    @SuppressWarnings("unchecked")
    private void initLoad(final long id) {
        progressBar.setVisibility(View.VISIBLE);
        loadSubscription = Observable.zip(MerchantApi.getMerchantInfoV2(id),
                MerchantApi.getMerchantDecoration(id),
                new Func2<HljHttpResult<Merchant>, Integer, HljHttpResult<Merchant>>() {
                    @Override
                    public HljHttpResult<Merchant> call(
                            HljHttpResult<Merchant> merchantHljHttpResult, Integer theme) {
                        try {
                            if (theme > 0 && merchantHljHttpResult.getData()
                                    .getShopType() != Merchant.SHOP_TYPE_HOTEL) {
                                resetTheme(theme);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return merchantHljHttpResult;
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setDataNullable(true)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<Merchant>>() {
                            @Override
                            public void onNext(HljHttpResult<Merchant> result) {
                                if (result == null) {
                                    return;
                                }
                                Merchant merchant = result.getData();
                                if (merchant != null && merchant.getId() > 0) {
                                    initView();
                                    MerchantDetailActivity.this.merchant = merchant;
                                    initMerchantInfo();
                                    loadSubInfo();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    setActionBarAlpha(1);
                                    emptyView.showEmptyView();

                                    HljHttpStatus status = result.getStatus();
                                    if (status != null && status.getRetCode() == 9) {
                                        emptyView.setHintText(status.getMsg());
                                    }
                                }
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                progressBar.setVisibility(View.GONE);
                                setActionBarAlpha(1);
                                emptyView.showEmptyView();
                            }
                        })
                        .build());
    }


    @SuppressWarnings("unchecked")
    private void loadSubInfo() {
        Observable<CommentZip> commentInitObb = Observable.zip(CommentApi.getServiceCommentMarksObb(

                id),
                CommentApi.getMerchantCommentsObb(this, id, markId, 1, HljCommon.PER_PAGE),
                new Func2<HljHttpData<List<ServiceCommentMark>>, HljHttpCommentsData, CommentZip>
                        () {


                    @Override
                    public CommentZip call(
                            HljHttpData<List<ServiceCommentMark>> listMarkData,
                            HljHttpCommentsData commentsData) {
                        if (listMarkData != null && !CommonUtil.isCollectionEmpty(listMarkData
                                .getData()) && commentsData != null && !CommonUtil
                                .isCollectionEmpty(
                                commentsData.getData())) {
                            return new CommentZip(listMarkData.getData(), commentsData);
                        }
                        return null;
                    }
                })
                .onErrorReturn(new Func1<Throwable, CommentZip>() {
                    @Override
                    public CommentZip call(Throwable throwable) {
                        return null;
                    }
                });

        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            subInfoSubscription = commentInitObb.subscribe(HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<CommentZip>() {
                        @Override
                        public void onNext(CommentZip commentZip) {
                            tabLayout.setVisibility(View.VISIBLE);
                            adapter.setMerchant(merchant);
                            Hotel hotel = merchant.getHotel();
                            if (hotel != null) {
                                if (!CommonUtil.isCollectionEmpty(hotel.getHotelHalls())) {
                                    cbHall.setVisibility(View.VISIBLE);
                                    tvHallsCount.setText(getString(R.string.label_hall) + hotel
                                            .getHotelMenus()
                                            .size());
                                    adapter.setHalls(hotel.getHotelHalls());
                                }
                                if (!CommonUtil.isCollectionEmpty(hotel.getHotelMenus())) {
                                    cbMenu.setVisibility(View.VISIBLE);
                                    tvMenusCount.setText(getString(R.string.label_menu) + hotel
                                            .getHotelMenus()
                                            .size());
                                    adapter.setMenus(hotel.getHotelMenus());
                                }
                                initCommentData(commentZip);
                            }
                        }
                    })
                    .build());
        } else {
            Observable<HljHttpData<List<Work>>> workObb = WorkApi.getMerchantRecommendWorksObb(id,
                    1,
                    3)
                    .onErrorReturn(new Func1<Throwable, HljHttpData<List<Work>>>() {
                        @Override
                        public HljHttpData<List<Work>> call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<HljHttpData<List<Work>>> caseObb = WorkApi.getMerchantWorksAndCasesObb(id,
                    "case",
                    null,
                    1,
                    6)
                    .onErrorReturn(new Func1<Throwable, HljHttpData<List<Work>>>() {
                        @Override
                        public HljHttpData<List<Work>> call(Throwable throwable) {
                            return null;
                        }
                    });
            Observable<HljHttpQuestion<List<Question>>> questionObb = QuestionAnswerApi.getQAList
                    (id,
                    1,
                    1)
                    .onErrorReturn(new Func1<Throwable, HljHttpQuestion<List<Question>>>() {
                        @Override
                        public HljHttpQuestion<List<Question>> call(Throwable throwable) {
                            return null;
                        }
                    });

            subInfoSubscription = Observable.zip(workObb,
                    caseObb,
                    questionObb,
                    commentInitObb,
                    new Func4<HljHttpData<List<Work>>, HljHttpData<List<Work>>,
                            HljHttpQuestion<List<Question>>, CommentZip, SubInfoZip>() {
                        @Override
                        public SubInfoZip call(
                                HljHttpData<List<Work>> workListData,
                                HljHttpData<List<Work>> caseListData,
                                HljHttpQuestion<List<Question>> questionListData,
                                CommentZip commentZip) {
                            return new SubInfoZip(workListData,
                                    caseListData,
                                    questionListData,
                                    commentZip);
                        }
                    })
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .setProgressBar(progressBar)
                            .setDataNullable(true)
                            .setOnNextListener(new SubscriberOnNextListener<SubInfoZip>() {
                                @Override
                                public void onNext(SubInfoZip subInfoZip) {
                                    startChatBubble();
                                    tabLayout.setVisibility(View.VISIBLE);
                                    adapter.setMerchant(merchant);
                                    if (subInfoZip.workListData != null && !CommonUtil
                                            .isCollectionEmpty(
                                            subInfoZip.workListData.getData())) {
                                        cbWork.setVisibility(View.VISIBLE);
                                        tvWorksCount.setText(getString(R.string.label_work) +
                                                subInfoZip.workListData.getTotalCount());
                                        adapter.setWorks(subInfoZip.workListData);
                                    }
                                    if (subInfoZip.caseListData != null && !CommonUtil
                                            .isCollectionEmpty(
                                            subInfoZip.caseListData.getData())) {
                                        cbCase.setVisibility(View.VISIBLE);
                                        tvCasesCount.setText(getString(R.string.label_case) +
                                                subInfoZip.caseListData.getTotalCount());
                                        adapter.setCases(subInfoZip.caseListData);
                                    }
                                    adapter.setQuestion(subInfoZip.questionListData);
                                    initCommentData(subInfoZip.commentZip);
                                    loadOtherInfo();
                                }
                            })
                            .build());
        }
    }

    private void initCommentData(CommentZip commentZip) {
        if (commentZip != null) {
            tvCommentsCount.setText(getString(R.string.label_comment) + commentZip.commentsData
                    .getTotalCount());
            merchantInfoViewHolder.tvHeaderCommentCount.setText(getString(R.string
                            .label_comment_count6,
                    commentZip.commentsData.getTotalCount()));
            adapter.initComments(commentZip.marks, commentZip.commentsData);
        } else {
            adapter.initComments(null, null);
        }

        if (commentZip != null && commentZip.commentsData.getFirstSixMonthAgoIndex() > 0) {
            pageFooterLayout.setVisibility(View.GONE);
            bottomMoreLayout.setVisibility(View.VISIBLE);
        } else {
            pageFooterLayout.setVisibility(View.VISIBLE);
            bottomMoreLayout.setVisibility(View.GONE);
        }
        currentPage = 1;
        pageCount = commentZip == null ? 0 : commentZip.commentsData.getPageCount();
        initPagination();
    }

    private void loadOtherInfo() {
        posterSubscription = MerchantApi.getMerchantRecommendPostersObb(id)
                .map(new Func1<HljHttpData<List<MerchantRecommendPosterItem>>,
                        List<MerchantRecommendPosterItem>>() {
                    @Override
                    public List<MerchantRecommendPosterItem> call
                            (HljHttpData<List<MerchantRecommendPosterItem>> listHljHttpData) {
                        return listHljHttpData != null ? listHljHttpData.getData() : null;
                    }
                })
                .filter(new Func1<List<MerchantRecommendPosterItem>, Boolean>() {
                    @Override
                    public Boolean call(
                            List<MerchantRecommendPosterItem> merchantRecommendPosterItems) {
                        return !CommonUtil.isCollectionEmpty(merchantRecommendPosterItems);
                    }
                })
                .subscribe(new RxBusSubscriber<List<MerchantRecommendPosterItem>>() {
                    @Override
                    protected void onEvent(
                            List<MerchantRecommendPosterItem> merchantRecommendPosterItems) {
                        adapter.setRecommendPosterItems(merchantRecommendPosterItems);
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                });

        noteSubscription = NoteApi.myNoteList(merchant.getUserId(), 1)
                .filter(new Func1<HljHttpData<List<Note>>, Boolean>() {
                    @Override
                    public Boolean call(HljHttpData<List<Note>> listHljHttpData) {
                        return listHljHttpData != null && !CommonUtil.isCollectionEmpty(
                                listHljHttpData.getData());
                    }
                })
                .subscribe(new RxBusSubscriber<HljHttpData<List<Note>>>() {
                    @Override
                    protected void onEvent(HljHttpData<List<Note>> noteListData) {
                        adapter.setNotes(noteListData);
                    }
                });
    }


    @SuppressWarnings("unchecked")
    private void initPagination() {
        rcMerchantInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (CommonUtil.isUnsubscribed(commentSubscription) && bottomMoreLayout
                                .getVisibility() != View.VISIBLE) {
                            int lastPosition = layoutManager.findLastVisibleItemPosition();
                            int itemCount = recyclerView.getAdapter()
                                    .getItemCount();
                            if (lastPosition < itemCount - 5) {
                                return;
                            }
                            loadNextPage(currentPage);

                        }
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (appbar == null) {
                    return;
                }
                if (appbar.getTotalScrollRange() > Math.abs(verticalOffset)) {
                    cbInfo.setChecked(true);
                    return;
                }
                if (scrollStartDelta > 0 && bubbleTimer != null) {
                    scrollStartDelta -= dy;
                    if (scrollStartDelta <= 0) {
                        bubbleTimer.overScrollDelta();
                    }
                }

                onScrollTabChange(recyclerView);
            }
        });
    }

    private void onScrollTabChange(RecyclerView recyclerView) {
        int firstPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
        int groupIndex = adapter.getGroupIndex(firstPosition);
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            if (groupIndex >= MerchantHomeAdapter.GroupIndex.Comment) {
                cbComment.setChecked(true);
            } else if (groupIndex >= MerchantHomeAdapter.GroupIndex.Menu && cbMenu.getVisibility
                    () == View.VISIBLE) {
                cbMenu.setChecked(true);
            } else if (groupIndex >= MerchantHomeAdapter.GroupIndex.Hall && cbHall.getVisibility
                    () == View.VISIBLE) {
                cbHall.setChecked(true);
            } else {
                cbInfo.setChecked(true);
            }
        } else {
            if (groupIndex >= MerchantHomeAdapter.GroupIndex.Question) {
                cbComment.setChecked(true);
            } else if (groupIndex >= MerchantHomeAdapter.GroupIndex.Case && cbCase.getVisibility
                    () == View.VISIBLE) {
                cbCase.setChecked(true);
            } else if (groupIndex >= MerchantHomeAdapter.GroupIndex.Work && cbWork.getVisibility
                    () == View.VISIBLE) {
                cbWork.setChecked(true);
            } else {
                cbInfo.setChecked(true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadNextPage(int currentPage) {
        if (currentPage < pageCount) {
            if (loadView != null) {
                loadView.setVisibility(View.VISIBLE);
            }
            if (endView != null) {
                endView.setVisibility(View.GONE);
            }
            commentSubscription = CommentApi.getMerchantCommentsObb(this,
                    id,
                    markId,
                    currentPage + 1,
                    HljCommon.PER_PAGE)
                    .subscribe(HljHttpSubscriber.buildSubscriber(MerchantDetailActivity.this)
                            .setOnNextListener(new SubscriberOnNextListener<HljHttpCommentsData>() {
                                @Override
                                public void onNext(HljHttpCommentsData commentsData) {
                                    if (commentsData != null && commentsData
                                            .getFirstSixMonthAgoIndex() > 0) {
                                        pageFooterLayout.setVisibility(View.GONE);
                                        bottomMoreLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        pageFooterLayout.setVisibility(View.VISIBLE);
                                        bottomMoreLayout.setVisibility(View.GONE);
                                    }
                                    MerchantDetailActivity.this.currentPage++;
                                    pageCount = commentsData == null ? 0 : commentsData
                                            .getPageCount();
                                    adapter.addComments(commentsData);
                                }
                            })
                            .build());
        } else {
            // 到末尾页面了,显示没有更多
            if (loadView != null) {
                loadView.setVisibility(View.GONE);
            }
            if (endView != null) {
                endView.setVisibility(currentPage >= 1 ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    private void resetTheme(int theme) {
        switch (theme) {
            case ThemeType.WhiteGolden:
                showUltimateTag = true;
                setTheme(R.style.NoTitleBarTheme_WhiteGolden);
                break;
            case ThemeType.DarkGolden:
                showUltimateTag = true;
                setTheme(R.style.NoTitleBarTheme_DarkGolden);
                break;
            case ThemeType.Green:
                setTheme(R.style.NoTitleBarTheme_Green);
                break;
            case ThemeType.Blue:
                setTheme(R.style.NoTitleBarTheme_Blue);
                break;
            case ThemeType.Pink:
                setTheme(R.style.NoTitleBarTheme_Pink);
                break;
            default:
                return;
        }

        color = ThemeUtil.getAttrColor(this, R.attr.hljColorBarBackground, 0);
        if (unbinder != null) {
            unbinder.unbind();
        }
        setContentView(R.layout.activity_merchant_detail);
        unbinder = ButterKnife.bind(this);
        if (noticeUtil != null) {
            noticeUtil.resetViews(msgCount, msgNotice);
        }
    }


    private void initTracker(Merchant merchant) {
        String site = getIntent().getStringExtra("site");
        JSONObject siteJson = null;
        if (!JSONUtil.isEmpty(site)) {
            try {
                siteJson = new JSONObject(site);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (siteJson == null && getApplication() != null && getApplication() instanceof
                HLJCustomerApplication) {
            siteJson = TrackerUtil.getSiteJson(null, 0, TrackerHelper.getActivityHistory(this));
        }
        new HljTracker.Builder(this).eventableId(merchant.getUserId())
                .eventableType("Merchant")
                .screen("merchant_detail")
                .action("hit")
                .site(siteJson)
                .build()
                .send();

        HljVTTagger.buildTagger(chatClickLayout)
                .tagName("free_chat_bubble")
                .dataId(merchant.getId())
                .dataType("Merchant")
                .tag();

    }

    private void initMerchantInfo() {
        initTracker(merchant);
        layoutHeader.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);

        merchantInfoViewHolder.setMerchantInfo(merchant);

        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            btnBuy.setText(R.string.label_reservation_hotel);
        }
        initMerchantCommonInfo();

        if (merchant.getEventInfo() == null) {
            handler.removeCallbacks(eventTimeRunnable);
        }
        if (couponRecord != null && couponRecord.getId() > 0) {
            UseCouponDialog couponDialog = new UseCouponDialog(MerchantDetailActivity.this,
                    couponRecord);
            couponDialog.setOnFinishedListener(new OnFinishedListener() {
                @Override
                public void onFinished(Object... objects) {
                    setResult(RESULT_OK, getIntent());
                }
            });
            couponDialog.show();
        }
    }

    private void initMerchantCommonInfo() {
        Point point = CommonUtil.getDeviceSize(this);
        int topLogoSize = CommonUtil.dp2px(this, 30);
        int logoSize = Math.round(point.x / 5.0f);

        if (!TextUtils.isEmpty(merchant.getCoverPath())) {
            int coverWidth = point.x;
            int coverMaxHeight = coverWidth * 7 / 8;
            Glide.with(this)
                    .load(ImagePath.buildPath(merchant.getCoverPath())
                            .width(coverWidth)
                            .height(coverMaxHeight)
                            .path())
                    .apply(new RequestOptions().fitCenter())
                    .into(ivCover);
        } else {
            switch ((int) (merchant.getId() % 4)) {
                case 0:
                    ivCover.setImageResource(R.drawable.image_merchant_cover1);
                    break;
                case 1:
                    ivCover.setImageResource(R.drawable.image_merchant_cover2);
                    break;
                case 2:
                    ivCover.setImageResource(R.drawable.image_merchant_cover3);
                    break;
                case 3:
                    ivCover.setImageResource(R.drawable.image_merchant_cover4);
                    break;
            }
        }


        Glide.with(this)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(topLogoSize)
                        .cropPath())
                .apply(new RequestOptions().error(R.mipmap.icon_avatar_primary)
                        .dontAnimate())
                .into(ivTopAvatar);

        if (showUltimateTag) {
            ivAvatar.setBorderWidth(0);
            ivUltimateTag.setVisibility(View.VISIBLE);
            merchantInfoViewHolder.merchantInfoHeader.setPadding(merchantInfoViewHolder
                            .merchantInfoHeader.getPaddingLeft(),
                    0,
                    merchantInfoViewHolder.merchantInfoHeader.getPaddingRight(),
                    merchantInfoViewHolder.merchantInfoHeader.getPaddingBottom());
            ivCoverLine.getLayoutParams().height = CommonUtil.dp2px(this, 21 + 11);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutAvatar
                    .getLayoutParams();
            if (params != null) {
                params.bottomMargin = CommonUtil.dp2px(this, -28);
            }
            layoutHeader.postInvalidate();
        } else {
            avatarShadowLayout.setBackgroundResource(R.drawable.icon_merchant_avatar_bg);
        }
        Glide.with(this)
                .load(ImagePath.buildPath(merchant.getLogoPath())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .error(R.mipmap.icon_avatar_primary))
                .into(ivAvatar);


        if (CommonUtil.isCollectionEmpty(merchant.getContactPhone())) {
            layoutCall.setVisibility(View.GONE);
        }

        isCommented = merchant.isUserCommented();
    }

    private void startChatBubble() {
        // 启动轻松聊计时器
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
        scrollStartDelta = SCROLL_Y_DELTA - appbar.getTotalScrollRange();
        bubbleTimer = new ChatBubbleTimer(TIME_STAYED_IN_PAGE,
                false,
                new ChatBubbleTimer.ShowBubbleCallBack() {
                    @Override
                    public void toggleBubble(boolean isShow) {
                        showChatBubble(isShow);
                    }
                });
        bubbleTimer.start();
    }

    private void showChatBubble(boolean isShow) {
        if (!isShow) {
            chatBubbleLayout.setVisibility(View.GONE);
            return;
        }

        // 如果用户在今天之内联系过商家，则不再显示气泡
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && ChatUtil.hasChatWithMerchant(user.getId(), merchant.getUserId())) {
            return;
        }
        // 触发轻松聊事件，触发接口和加载内容
        chatDataSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<MerchantChatData>() {
                    @Override
                    public void onNext(MerchantChatData merchantChatData) {
                        if (!TextUtils.isEmpty(merchantChatData.getHomeSpeech())) {
                            bubbleTimer.cancel();
                            bubbleTimer = new ChatBubbleTimer(TIME_HIDE_AFTER,
                                    true,
                                    new ChatBubbleTimer.ShowBubbleCallBack() {
                                        @Override
                                        public void toggleBubble(boolean isShow) {
                                            showChatBubble(isShow);
                                        }
                                    });
                            bubbleTimer.start();

                            chatBubbleLayout.setVisibility(View.VISIBLE);
                            Glide.with(MerchantDetailActivity.this)
                                    .load(ImagePath.buildPath(merchant.getLogoPath())
                                            .width(CommonUtil.dp2px(MerchantDetailActivity.this,
                                                    38))
                                            .cropPath())
                                    .into(imgLogo);
                            tvMsg.setText(merchantChatData.getHomeSpeech());
                        }
                    }
                })
                .build();
        MerchantApi.getMerchantChatData(merchant.getId())
                .subscribe(chatDataSub);
    }

    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.btn_message)
    public void onMessage() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        Intent intent = new Intent(this, MessageHomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_share)
    public void onShare() {
        if (merchant != null && merchant.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(this, merchant.getShareInfo(), handler);
        }
    }


    @OnClick(R.id.layout_call)
    public void onCall() {
        if (merchant == null) {
            return;
        }
        //酒店获取结婚顾问电话
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
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
                                    if (!JSONUtil.isEmpty(support.getPhone())) {
                                        String phone = support.getPhone();
                                        try {
                                            callUp(Uri.parse("tel:" + phone.trim()));
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }

                                @Override
                                public void onFailed() {
                                    super.onFailed();
                                    if (isFinishing()) {
                                        return;
                                    }
                                    ToastUtil.showToast(MerchantDetailActivity.this,
                                            null,
                                            R.string.msg_get_supports_error);
                                }
                            });
        } else {
            List<String> contactPhones = merchant.getContactPhone();
            if (CommonUtil.isCollectionEmpty(contactPhones)) {
                ToastUtil.showToast(this, null, R.string.msg_no_merchant_number);
                return;
            }
            if (contactPhones.size() == 1) {
                onCall(contactPhones.get(0));
                return;
            }
            me.suncloud.marrymemo.util.DialogUtil.createPhoneListDialog(this,
                    contactPhones,
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(
                                AdapterView<?> parent, View view, int position, long id) {
                            onCall((String) parent.getAdapter()
                                    .getItem(position));
                        }
                    })
                    .show();

        }
    }

    private void onCall(String phone) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(phone.trim())) {
            return;
        }
        new HljTracker.Builder(this).eventableId(merchant.getUserId())
                .eventableType("Merchant")
                .screen("chat")
                .action("call")
                .build()
                .add();
        try {
            callUp(Uri.parse("tel:" + phone.trim()));
            new HljTracker.Builder(this).eventableId(merchant.getUserId())
                    .eventableType("Merchant")
                    .screen("chat")
                    .action("real_call")
                    .build()
                    .add();
        } catch (Exception ignored) {
        }
    }

    @OnClick(R.id.layout_chat)
    public void onChat() {
        if (merchant == null) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_HOTEL, merchant);
        } else {
            Intent intent = new Intent(this, WSCustomerChatActivity.class);
            intent.putExtra("user", merchant.toUser());
            intent.putExtra("ws_track", ModuleUtils.getWSTrack(merchant));
            if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                    .isEmpty()) {
                intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
            }
            startActivity(intent);
        }
    }

    @OnClick(R.id.comment_layout)
    public void onComment() {
        if (merchant == null) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (isCommented) {
            ToastUtil.showToast(this, null, R.string.label_commented);
            return;
        }
        Intent intent = new Intent(this, CommentMerchantActivity.class);
        intent.putExtra("merchant", merchant);
        startActivity(intent);
    }

    @OnClick(R.id.btn_buy)
    public void onReservation() {
        if (merchant == null) {
            return;
        }
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        CommonUtil.unSubscribeSubs(appointmentSub);
        if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            // 酒店商家预约不跳转私信提示
            appointmentSub = AppointmentUtil.makeAppointment(this,
                    merchant.getId(),
                    merchant.getUserId(),
                    AppointmentUtil.MERCHANT,
                    new AppointmentUtil.AppointmentCallback() {
                        @Override
                        public void onCallback() {
                            // 弹窗
                            me.suncloud.marrymemo.util.DialogUtil.createAppointmentDlg(
                                    MerchantDetailActivity.this)
                                    .show();
                        }
                    });
        } else {
            appointmentSub = AppointmentUtil.makeAppointment(this,
                    merchant.getId(),
                    merchant.getUserId(),
                    AppointmentUtil.MERCHANT,
                    null);
        }
    }

    @OnClick(R.id.chat_click_layout)
    void onChatBubbleClick() {
        bubbleTimer.cancel();
        chatBubbleLayout.setVisibility(View.GONE);
        onChat();
        chatTrigSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                    }
                })
                .build();
        MerchantApi.postMerchantChatLinkTrigger(merchant.getId(),
                MerchantChatLinkTriggerPostBody.MerchantChatLinkType.TYPE_MERCHANT_HOME,
                null)
                .subscribe(chatTrigSub);
    }

    @OnClick({R.id.cb_info, R.id.cb_comment, R.id.cb_case, R.id.cb_hall, R.id.cb_menu, R.id
            .cb_work})
    void onTabClick(View view) {
        if (!(view instanceof CheckableLinearLayoutButton) || ((CheckableLinearLayoutButton)
                view).isChecked() || tabLayout.getAlpha() == 0) {
            return;
        }
        int offsetPosition = -1;
        switch (view.getId()) {
            case R.id.cb_info:
                offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Info);
                break;
            case R.id.cb_case:
                offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Case);
                break;
            case R.id.cb_work:
                offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Work);
                break;
            case R.id.cb_hall:
                offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Hall);
                break;
            case R.id.cb_menu:
                offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Menu);
                break;
            case R.id.cb_comment:
                if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
                    offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex.Comment);
                } else {
                    offsetPosition = adapter.getGroupOffset(MerchantHomeAdapter.GroupIndex
                            .Question);
                }
                break;
        }
        if (offsetPosition >= 0) {
            rcMerchantInfo.stopScroll();
            int offset = 0;
            if (view.getId() != R.id.cb_info) {
                offset = -CommonUtil.dp2px(this, 10);
            }
            appbar.setExpanded(view.getId() == R.id.cb_info);
            layoutManager.scrollToPositionWithOffset(offsetPosition, offset);
        }

    }

    void onMerchantDescribeDetail() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        if (fragment != null) {
            return;
        }
        MerchantDescriptionDetailFragment detailFragment = MerchantDescriptionDetailFragment
                .newInstance(
                merchant);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_right);
        ft.add(R.id.fragment_content, detailFragment, "detailFragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    handler.sendEmptyMessage(requestCode);
                    break;
                case Constants.RequestCode.POST_SERVICE_ORDER_COMMENT:
                    if (data == null || comment == null || comment.getId() == 0) {
                        return;
                    }
                    RepliedComment repliedComment = data.getParcelableExtra("comment_response");
                    if (repliedComment == null) {
                        commentContent = data.getStringExtra("comment_content");
                    } else {
                        commentContent = "";
                        User user = Session.getInstance()
                                .getCurrentUser(MerchantDetailActivity.this);
                        Author author = new Author();
                        author.setId(user.getId());
                        author.setName(user.getNick());
                        repliedComment.setUser(author);
                        comment.getRepliedComments()
                                .add(repliedComment);
                        if (commentViewHolder != null && comment.isRepliesExpanded()) {
                            commentViewHolder.addComments(this, comment);
                        }
                    }
                    break;
                case Constants.RequestCode.HOTEL_COLLECT:
                    if (data != null) {
                        merchant.setCollected(data.getBooleanExtra("is_followed",
                                merchant.isCollected()));
                        merchantInfoViewHolder.tvFollow.setText(merchant.isCollected() ? R.string
                                .label_followed : R.string.label_follow2);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        if (merchant == null || merchant.getEventInfo() != null) {
            handler.post(eventTimeRunnable);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        handler.removeCallbacks(eventTimeRunnable);
        super.onPause();
    }

    @Override
    protected void onFinish() {
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
        CommonUtil.unSubscribeSubs(loadSubscription,
                followSubscription,
                chatDataSub,
                chatTrigSub,
                posterSubscription,
                noteSubscription,
                subInfoSubscription,
                commentSubscription,
                rxSubscription,
                praiseSub,
                appointmentSub,
                deleteSub,
                authorizedSubscription);
        super.onFinish();
    }

    private Runnable eventTimeRunnable = new Runnable() {
        public void run() {
            try {
                if (isFinishing()) {
                    return;
                }
                if (rcMerchantInfo.getChildCount() == 0) {
                    handler.postDelayed(eventTimeRunnable, 1000);
                }
                for (int i = 0, size = rcMerchantInfo.getChildCount(); i < size; i++) {
                    View childView = rcMerchantInfo.getChildAt(i);
                    RecyclerView.ViewHolder holder = rcMerchantInfo.getChildViewHolder(childView);
                    if (holder != null && holder instanceof SmallEventViewHolder) {
                        long millis = ((SmallEventViewHolder) holder).showTimeDown(merchant
                                .getEventInfo());
                        if (millis <= 0) {
                            return;
                        }
                    }
                }
                handler.postDelayed(eventTimeRunnable, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private class CommentZip {
        List<ServiceCommentMark> marks;
        HljHttpCommentsData commentsData;

        private CommentZip(
                List<ServiceCommentMark> marks, HljHttpCommentsData commentsData) {
            this.marks = marks;
            this.commentsData = commentsData;
        }
    }

    private class SubInfoZip {
        HljHttpData<List<Work>> workListData;
        HljHttpData<List<Work>> caseListData;
        HljHttpQuestion<List<Question>> questionListData;
        CommentZip commentZip;

        private SubInfoZip(
                HljHttpData<List<Work>> workListData,
                HljHttpData<List<Work>> caseListData,
                HljHttpQuestion<List<Question>> questionListData,
                CommentZip commentZip) {
            this.workListData = workListData;
            this.caseListData = caseListData;
            this.questionListData = questionListData;
            this.commentZip = commentZip;
        }
    }

    class MerchantInfoViewHolder {
        @BindView(R.id.rating)
        RatingBar rating;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_header_comment_count)
        TextView tvHeaderCommentCount;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;
        @BindView(R.id.img_attention)
        ImageView imgAttention;
        @BindView(R.id.tv_follow)
        TextView tvFollow;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.img_call)
        ImageView imgCall;
        @BindView(R.id.tv_hotel_prices)
        TextView tvHotelPrices;
        @BindView(R.id.hotel_price_layout)
        LinearLayout hotelPriceLayout;
        @BindView(R.id.tv_achievement)
        TextView tvAchievement;
        @BindView(R.id.merchant_achievement_layout)
        CardView merchantAchievementLayout;
        @BindView(R.id.merchant_info_header)
        View merchantInfoHeader;

        private Merchant merchant;

        private MerchantInfoViewHolder(View view) {
            ButterKnife.bind(this, view);

            try {
                int progressColor = ThemeUtil.getAttrColor(view.getContext(),
                        R.attr.hljMerchantRatingProgressColor,
                        0);
                int backgroundColor = ThemeUtil.getAttrColor(view.getContext(),
                        R.attr.hljRatingBackgroundColor,
                        0);
                LayerDrawable drawable = (LayerDrawable) rating.getProgressDrawable();
                if (backgroundColor != 0) {
                    drawable.getDrawable(0)
                            .mutate()
                            .setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
                    drawable.getDrawable(1)
                            .mutate()
                            .setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
                }
                if (progressColor != 0) {
                    drawable.getDrawable(2)
                            .mutate()
                            .setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setMerchantInfo(Merchant merchant) {
            this.merchant = merchant;
            if (merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
                tvProperty.setText(R.string.title_activity_hotel_list);
                if (merchant.getHotel() != null) {
                    Hotel hotel = merchant.getHotel();
                    hotelPriceLayout.setVisibility(View.VISIBLE);
                    tvHotelPrices.setText(hotel.getPriceStr());
                }
            } else {
                //服务商家
                hotelPriceLayout.setVisibility(View.GONE);
                tvProperty.setText(merchant.getPropertyName());
            }


            SpannableStringBuilder builder = new SpannableStringBuilder(merchant.getName());
            int res = 0;
            switch (merchant.getGrade()) {
                case 2:
                    res = R.mipmap.icon_merchant_level2_36_36;
                    break;
                case 3:
                    res = R.mipmap.icon_merchant_level3_36_36;
                    break;
                case 4:
                    res = R.mipmap.icon_merchant_level4_36_36;
                    break;
                default:
                    break;
            }
            if (res != 0) {
                Drawable d = ContextCompat.getDrawable(tvName.getContext(), res);
                if (d != null) {
                    d.setBounds(0,
                            0,
                            CommonUtil.dp2px(tvName.getContext(), 16),
                            CommonUtil.dp2px(tvName.getContext(), 16));
                    builder.append("  ");
                    builder.setSpan(new HljImageSpan(d),
                            builder.length() - 1,
                            builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (merchant.getBondSign() != null) {
                Drawable d = ContextCompat.getDrawable(tvName.getContext(),
                        R.mipmap.icon_bond_36_36);
                if (d != null) {
                    d.setBounds(0,
                            0,
                            CommonUtil.dp2px(tvName.getContext(), 16),
                            CommonUtil.dp2px(tvName.getContext(), 16));
                    builder.append("  ");
                    builder.setSpan(new HljImageSpan(d),
                            builder.length() - 1,
                            builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            tvName.setText(builder);
            tvFansCount.setText(getString(R.string.label_collect_count3,
                    String.valueOf(merchant.getFansCount())));
            if (merchant.isCollected()) {
                imgAttention.setImageResource(ThemeUtil.getAttrResourceId(imgAttention.getContext(),
                        R.attr.hljIconMerchantCollected,
                        R.mipmap.icon_has_attention));
            } else {
                imgAttention.setImageResource(ThemeUtil.getAttrResourceId(imgAttention.getContext(),
                        R.attr.hljIconMerchantNotCollected,
                        R.mipmap.icon_add_attention));
            }
            tvFollow.setText(merchant.isCollected() ? R.string.label_followed : R.string
                    .label_follow2);

            //地址
            if (!TextUtils.isEmpty(merchant.getAddress())) {
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(TextUtils.isEmpty(merchant.getAddress()) ? "" : merchant
                        .getAddress());
            }

            if (merchant.getCommentStatistics() != null) {
                rating.setRating((float) merchant.getCommentStatistics()
                        .getScore());
            }
            //评论数
            tvHeaderCommentCount.setText(getString(R.string.label_comment_count6,
                    merchant.getCommentCount()));

            //成就
            if (!CommonUtil.isCollectionEmpty(merchant.getMerchantAchievement())) {
                merchantAchievementLayout.setVisibility(View.VISIBLE);
                tvAchievement.setText(merchant.getMerchantAchievement()
                        .get(0)
                        .getTitle());
            } else {
                merchantAchievementLayout.setVisibility(View.GONE);
            }

            if (CommonUtil.isCollectionEmpty(merchant.getContactPhone())) {
                imgCall.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.layout_follow)
        public void onFollow() {
            if (merchant == null) {
                return;
            }
            if (!AuthUtil.loginBindCheck(MerchantDetailActivity.this)) {
                return;
            }
            if (followSubscription != null && !followSubscription.isUnsubscribed()) {
                return;
            }
            Subscriber followSubscriber = HljHttpSubscriber.buildSubscriber
                    (MerchantDetailActivity.this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            if (merchant.isCollected()) {
                                if (Util.isNewFirstCollect(MerchantDetailActivity.this, 6)) {
                                    Util.showFirstCollectNoticeDialog(MerchantDetailActivity.this,
                                            6);
                                } else {
                                    ToastUtil.showCustomToast(MerchantDetailActivity.this,
                                            R.string.hint_collect_complete2);
                                }
                            } else {
                                ToastUtil.showCustomToast(MerchantDetailActivity.this,
                                        R.string.hint_discollect_complete2);
                            }
                        }
                    })
                    .build();
            if (merchant.isCollected()) {
                tvFollow.setText(R.string.label_follow2);
                imgAttention.setImageResource(ThemeUtil.getAttrResourceId(imgAttention.getContext(),
                        R.attr.hljIconMerchantNotCollected,
                        R.mipmap.icon_add_attention));
                merchant.setCollected(false);
                followSubscription = CommonApi.deleteMerchantFollowObb(merchant.getId())
                        .subscribe(followSubscriber);
            } else {
                tvFollow.setText(R.string.label_followed);
                imgAttention.setImageResource(ThemeUtil.getAttrResourceId(imgAttention.getContext(),
                        R.attr.hljIconMerchantCollected,
                        R.mipmap.icon_has_attention));
                merchant.setCollected(true);
                followSubscription = CommonApi.postMerchantFollowObb(merchant.getId())
                        .subscribe(followSubscriber);

            }
        }


        @OnClick(R.id.img_call)
        public void onCall() {
            MerchantDetailActivity.this.onCall();
        }

        @OnClick(R.id.tv_address)
        void onAddress() {
            if (merchant == null || merchant.getLatitude() == 0 || merchant.getLongitude() == 0) {
                return;
            }
            Intent intent = new Intent(MerchantDetailActivity.this, NavigateMapActivity.class);
            intent.putExtra(NavigateMapActivity.ARG_TITLE, merchant.getName());
            intent.putExtra(NavigateMapActivity.ARG_ADDRESS, merchant.getAddress());
            intent.putExtra(NavigateMapActivity.ARG_LATITUDE, merchant.getLatitude());
            intent.putExtra(NavigateMapActivity.ARG_LONGITUDE, merchant.getLongitude());
            startActivity(intent);
        }

        @OnClick(R.id.merchant_achievement_layout)
        public void onAchievement() {
            if (merchant == null) {
                return;
            }
            BannerUtil.bannerJump(MerchantDetailActivity.this,
                    merchant.getMerchantAchievement()
                            .get(0),
                    null);
        }
    }
}
