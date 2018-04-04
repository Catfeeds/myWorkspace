package me.suncloud.marrymemo.fragment.user;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljquestionanswer.widgets.CustScrollView;
import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.fragment.WeddingDateFragment;
import me.suncloud.marrymemo.fragment.tools.ShareWeddingDateFragment;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.user.UserStatistics;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.TextShareUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CollectActivity;
import me.suncloud.marrymemo.view.FeedbackActivity;
import me.suncloud.marrymemo.view.FollowActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.MyOrderListActivity;
import me.suncloud.marrymemo.view.MyWalletActivity;
import me.suncloud.marrymemo.view.SettingActivity;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.WXWallActivity;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetActivity;
import me.suncloud.marrymemo.view.budget.NewWeddingBudgetFigureActivity;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import me.suncloud.marrymemo.view.marry.MarryBookEditActivity;
import me.suncloud.marrymemo.view.marry.MarryTaskActivity;
import me.suncloud.marrymemo.view.marry.MarryTaskEditActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.tools.MyPublishActivity;
import me.suncloud.marrymemo.view.tools.WeddingCalendarActivity;
import me.suncloud.marrymemo.view.tools.WeddingTableListActivity;
import me.suncloud.marrymemo.view.user.UserDynamicListActivity;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;
import me.suncloud.marrymemo.view.wallet.OpenMemberActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 我们
 * Created by chen_bin on 2017/11/7 0007.
 */
public class UserFragment extends RefreshFragment {

    @BindView(R.id.img_header_bg)
    ImageView imgHeaderBg;
    @BindView(R.id.shade_layout)
    View shadeLayout;
    @BindView(R.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.wedding_date_layout)
    LinearLayout weddingDateLayout;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;
    @BindView(R.id.collect_layout)
    LinearLayout collectLayout;
    @BindView(R.id.tv_shopping_cart_count)
    TextView tvShoppingCartCount;
    @BindView(R.id.shopping_cart_layout)
    LinearLayout shoppingCartLayout;
    @BindView(R.id.tv_order_count)
    TextView tvOrderCount;
    @BindView(R.id.order_layout)
    LinearLayout orderLayout;
    @BindView(R.id.tv_publish_count)
    TextView tvPublishCount;
    @BindView(R.id.publish_layout)
    LinearLayout publishLayout;
    @BindView(R.id.wallet_layout)
    LinearLayout walletLayout;
    @BindView(R.id.member_view)
    View memberView;
    @BindView(R.id.user_layout)
    RelativeLayout userLayout;
    @BindView(R.id.tv_wedding_task_count)
    TextView tvWeddingTaskCount;
    @BindView(R.id.btn_record_wedding_task)
    ImageButton btnRecordWeddingTask;
    @BindView(R.id.wedding_task_layout)
    LinearLayout weddingTaskLayout;
    @BindView(R.id.tv_wedding_account)
    TextView tvWeddingAccount;
    @BindView(R.id.btn_record_wedding_account)
    ImageButton btnRecordWeddingAccount;
    @BindView(R.id.wedding_account_layout)
    LinearLayout weddingAccountLayout;
    @BindView(R.id.card_notice_view)
    View cardNoticeView;
    @BindView(R.id.card_layout)
    FrameLayout cardLayout;
    @BindView(R.id.wedding_table_layout)
    LinearLayout weddingTableLayout;
    @BindView(R.id.wedding_calendar_layout)
    FrameLayout weddingCalendarLayout;
    @BindView(R.id.wedding_loan_layout)
    LinearLayout weddingLoanLayout;
    @BindView(R.id.wedding_wall_layout)
    LinearLayout weddingWallLayout;
    @BindView(R.id.wedding_budget_layout)
    LinearLayout weddingBudgetLayout;
    @BindView(R.id.btn_bind_partner)
    ImageButton btnBindPartner;
    @BindView(R.id.tv_unread_count)
    TextView tvUnreadCount;
    @BindView(R.id.img_user_dynamic_tag)
    ImageView imgUserDynamicTag;
    @BindView(R.id.user_dynamic_layout)
    FrameLayout userDynamicLayout;
    @BindView(R.id.float_btn_layout)
    RelativeLayout floatBtnLayout;
    @BindView(R.id.scroll_view)
    CustScrollView scrollView;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.btn_merchant_recruit)
    Button btnMerchantRecruit;
    @BindView(R.id.btn_share)
    ImageButton btnShare;
    @BindView(R.id.share_layout)
    RelativeLayout shareLayout;
    @BindView(R.id.btn_msg)
    ImageButton btnMsg;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.btn_more)
    ImageButton btnMore;
    @BindView(R.id.more_layout)
    RelativeLayout moreLayout;
    @BindView(R.id.divider_view)
    View dividerView;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;
    @BindView(R.id.img_partner_avatar)
    RoundedImageView imgPartnerAvatar;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.avatar_layout)
    RelativeLayout avatarLayout;
    @BindView(R.id.avatar_holder_layout)
    RelativeLayout avatarHolderLayout;

    private NoticeUtil noticeUtil;
    private TextShareUtil shareUtil;
    private Dialog shareDialog;
    private PopupWindow morePopWin;
    private ObjectAnimator transAnim;

    private User user;
    private boolean isShowedFloatBtn;
    private boolean isBindedPartner;
    private long lastUserId;

    private int barColor;
    private int bgImgWidth;
    private int bgImgHeight;
    private int finalSize;
    private int avatarSize;
    private int partnerAvatarSize;
    private int actionLayoutHeight;
    private int avatarLayoutWidth;
    private int endScrollY;
    private int unreadCount;

    private float barAlpha;
    private float avatarScale;
    private float partnerAvatarScale;
    private float avatar1TranslationX;
    private float avatar2TranslationX;
    private float avatar1TranslationY;
    private float avatar2TranslationY;

    private Unbinder unbinder;

    private Subscription rxBusUserSub;
    private Subscription rxBusEventSub;
    private Subscription delaySub;
    private HljHttpSubscriber getUnreadCountSub;
    private HljHttpSubscriber getStatisticsSub;
    private HljHttpSubscriber getBudgetSub;
    private HljHttpSubscriber getShareSub;
    private HljHttpSubscriber afterShareSub;

    public static UserFragment newInstance() {
        Bundle args = new Bundle();
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String fragmentPageTrackTagName() {
        return "我的";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point point = CommonUtil.getDeviceSize(getContext());
        barColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
        bgImgWidth = point.x;
        bgImgHeight = Math.round(bgImgWidth * 296.0f / 750.0f);
        finalSize = CommonUtil.dp2px(getContext(), 32);
        avatarSize = Math.round(point.x * 168.0f / 750.0f);
        partnerAvatarSize = Math.round(point.x * 112.0f / 750.0f);
        avatarLayoutWidth = avatarSize + partnerAvatarSize - CommonUtil.dp2px(getContext(), 14);
        actionLayoutHeight = CommonUtil.dp2px(getContext(), 45);
        endScrollY = bgImgHeight - actionLayoutHeight - HljBaseActivity.getStatusBarHeight(
                getContext());
        avatarScale = finalSize * 1.0f / avatarSize;
        partnerAvatarScale = finalSize * 1.0f / partnerAvatarSize;
        avatar1TranslationX = avatarLayoutWidth / 2 - finalSize + CommonUtil.dp2px(getContext(), 3);
        avatar2TranslationX = partnerAvatarSize - avatarLayoutWidth / 2 - CommonUtil.dp2px(
                getContext(),
                3);
        avatar1TranslationY = CommonUtil.dp2px(getContext(),
                12) - (actionLayoutHeight - finalSize) / 2;
        avatar2TranslationY = avatar1TranslationY + (avatarSize - partnerAvatarSize) / 2;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        HljBaseActivity.setActionBarPadding(getContext(), contentLayout);
        HljBaseActivity.setActionBarPadding(getContext(), actionHolderLayout);
        HljBaseActivity.setActionBarPadding(getContext(), avatarHolderLayout);
        imgHeaderBg.getLayoutParams().width = bgImgWidth;
        imgHeaderBg.getLayoutParams().height = bgImgHeight;
        ((ViewGroup.MarginLayoutParams) shadeLayout.getLayoutParams()).topMargin = bgImgHeight -
                HljBaseActivity.getStatusBarHeight(
                getContext());
        ((ViewGroup.MarginLayoutParams) weddingDateLayout.getLayoutParams()).topMargin =
                avatarSize - CommonUtil.dp2px(
                getContext(),
                32);
        ((ViewGroup.MarginLayoutParams) floatBtnLayout.getLayoutParams()).topMargin = avatarSize
                + CommonUtil.dp2px(
                getContext(),
                54);
        imgAvatar.setCornerRadius(avatarSize / 2);
        imgAvatar.getLayoutParams().width = avatarSize;
        imgAvatar.getLayoutParams().height = avatarSize;
        imgPartnerAvatar.setCornerRadius(partnerAvatarSize / 2);
        imgPartnerAvatar.getLayoutParams().width = partnerAvatarSize;
        imgPartnerAvatar.getLayoutParams().height = partnerAvatarSize;
        ((ViewGroup.MarginLayoutParams) imgPartnerAvatar.getLayoutParams()).leftMargin =
                avatarSize - CommonUtil.dp2px(
                getContext(),
                14);
        scrollView.setOnScrollListener(new CustScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                float ratio = 0;
                if (t > endScrollY) {
                    ratio = 1;
                } else if (t > 0) {
                    ratio = ((float) t) / endScrollY;
                }
                setBarAlpha(ratio);

                imgAvatar.setScaleX(1 - (1 - avatarScale) * ratio);
                imgAvatar.setScaleY(1 - (1 - avatarScale) * ratio);
                imgPartnerAvatar.setScaleX(1 - (1 - partnerAvatarScale) * ratio);
                imgPartnerAvatar.setScaleY(1 - (1 - partnerAvatarScale) * ratio);

                imgAvatar.setTranslationX(avatar1TranslationX * ratio);
                imgAvatar.setTranslationY(-avatar1TranslationY * ratio);
                imgPartnerAvatar.setTranslationX(avatar2TranslationX * ratio);
                imgPartnerAvatar.setTranslationY(-avatar2TranslationY * ratio);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTracker();
        hideUserNewsIcon();
        onAppSubmit();
        new UserTask(getContext(), null).execute();
        registerRxBusEvent();

        noticeUtil = new NoticeUtil(getContext(), tvMsgCount, msgNoticeView);
        noticeUtil.onResume();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(tvWeddingDate)
                .tagName(HljTaggerName.BTN_SHARE_WEDDING_DAY)
                .hitTag();
        HljVTTagger.buildTagger(tvEdit)
                .tagName(HljTaggerName.BTN_CHANGE_WEDDING_DAY)
                .hitTag();
    }

    private void registerRxBusEvent() {
        if (rxBusUserSub == null || rxBusUserSub.isUnsubscribed()) {
            rxBusUserSub = RxBus.getDefault()
                    .toObservable(User.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<User>() {
                        @Override
                        protected void onEvent(User user) {
                            setUserData(user);
                        }
                    });
        }
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NEW_CARD_NOTICE:
                                    cardNoticeView.setVisibility(View.VISIBLE);
                                    break;
                                case TRENDS_COUNT_REFRESH:
                                    if (rxEvent.getObject() != null) {
                                        setUnreadCount((int) rxEvent.getObject());
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 审核特殊处理
     */
    private void onAppSubmit() {
        if (FinancialSwitch.INSTANCE.isClosed(getContext())) {
            ViewGroup parent = (ViewGroup) weddingLoanLayout.getParent();
            ViewGroup parent2 = (ViewGroup) weddingWallLayout.getParent();
            parent.removeView(weddingLoanLayout);
            parent2.removeView(weddingWallLayout);
            parent.addView(weddingWallLayout);
        }
    }

    private void setUserData(User user) {
        this.user = user;
        if (user == null) {
            return;
        }
        Glide.with(getContext())
                .load(ImagePath.buildPath(user.getAvatar())
                        .width(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser == null || partnerUser.getId() == 0) {
            imgPartnerAvatar.setImageResource(R.mipmap.icon_cross_avatar_gray);
        } else {
            Glide.with(getContext())
                    .load(ImagePath.buildPath(partnerUser.getAvatar())
                            .width(partnerAvatarSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgPartnerAvatar);
        }
        userLayout.setBackgroundResource(user.getMember() != null && user.getMember()
                .getId() > 0 ? R.drawable.image_bg_opened_member : R.drawable
                .image_bg_un_opened_member);
        tvNick.setText(TextUtils.isEmpty(user.getNick()) ? getString(R.string.label_no_nick___cm)
                : user.getNick());
        tvShoppingCartCount.setText(String.valueOf(Session.getInstance()
                .getCartCount(getContext())));
        cardNoticeView.setVisibility(getCardNewsCount() > 0 ? View.VISIBLE : View.GONE);
        setWeddingDate();
        setShowFloatBtnView();
        getUnreadCount();
    }

    @OnClick(R.id.btn_merchant_recruit)
    void onMerchantRecruit() {
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(getContext());
        if (dataConfig != null && !TextUtils.isEmpty(dataConfig.getMerchantRecruit())) {
            Intent intent = new Intent(getActivity(), HljWebViewActivity.class);
            intent.putExtra("path", dataConfig.getMerchantRecruit());
            intent.putExtra("title", getString(R.string.title_activity_merchant_about));
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_share)
    void onShare() {
        if (shareUtil != null) {
            showShareDialog();
            return;
        }
        if (getShareSub == null || getShareSub.isUnsubscribed()) {
            getShareSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ShareInfo>() {
                        @Override
                        public void onNext(ShareInfo shareInfo) {
                            String url = null;
                            String title = null;
                            String desc = null;
                            String desc2 = null;
                            if (shareInfo != null) {
                                url = shareInfo.getUrl();
                                title = shareInfo.getTitle();
                                desc = shareInfo.getDesc();
                                desc2 = shareInfo.getDesc2();
                            }
                            if (TextUtils.isEmpty(url)) {
                                url = "http://dwz.cn/IRby7";
                            }
                            if (TextUtils.isEmpty(title)) {
                                title = getString(R.string.tools_share_title);
                            }
                            if (TextUtils.isEmpty(desc)) {
                                desc = getString(R.string.tools_share_msg);
                            }
                            if (TextUtils.isEmpty(desc2)) {
                                desc2 = getString(R.string.tools_share_msg);
                            }
                            shareUtil = new TextShareUtil(getContext(),
                                    url,
                                    title,
                                    desc,
                                    desc2,
                                    null);
                            showShareDialog();
                        }
                    })
                    .setDataNullable(true)
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .build();
            ToolsApi.getAppShareObb()
                    .subscribe(getShareSub);
        }
    }

    private void showShareDialog() {
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareDialog == null) {
            shareDialog = Util.initTextShareDialog(getContext(),
                    getString(R.string.label_dialog_app_share_title),
                    shareUtil,
                    new ShareActionViewHolder.OnShareClickListener() {
                        @Override
                        public void onShare(View v, ShareAction action) {
                            afterShareApp();
                        }
                    });
        }
        shareDialog.show();

    }

    private void afterShareApp() {
        afterShareSub = HljHttpSubscriber.buildSubscriber(getContext())
                .build();
        UserApi.afterShareApp()
                .subscribe(afterShareSub);
    }

    @OnClick(R.id.btn_msg)
    void onMsg() {
        startActivity(new Intent(getContext(), MessageHomeActivity.class));
    }

    @OnClick(R.id.btn_more)
    void onMore() {
        if (morePopWin != null && morePopWin.isShowing()) {
            morePopWin.dismiss();
            return;
        }
        if (morePopWin == null) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_user_more, null);
            view.findViewById(R.id.setting_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            view.findViewById(R.id.follow_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            view.findViewById(R.id.praise_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            view.findViewById(R.id.feedback_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            view.findViewById(R.id.contact_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            view.findViewById(R.id.share_layout)
                    .setOnClickListener(onMorePopWinClickListener);
            morePopWin = new PopupWindow(getContext());
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            morePopWin = new PopupWindow(view,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight(),
                    true);
            morePopWin.setContentView(view);
            morePopWin.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),
                    android.R.color.transparent)));
            morePopWin.setOutsideTouchable(true);
            morePopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                }
            });
        }
        setBackgroundAlpha(0.94f);
        morePopWin.showAsDropDown(btnMore,
                -morePopWin.getContentView()
                        .getMeasuredWidth() + btnMore.getMeasuredWidth() - CommonUtil.dp2px(
                        getContext(),
                        12),
                0);
    }

    @OnClick({R.id.img_partner_avatar, R.id.btn_bind_partner})
    void onBindPartner() {
        if (user == null) {
            return;
        }
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser == null || partnerUser.getId() == 0) {
            startActivity(new Intent(getContext(), BindingPartnerActivity.class));
        } else {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra("id", partnerUser.getId());
            startActivity(intent);
        }
    }

    @OnClick(R.id.user_dynamic_layout)
    void onUserDynamic() {
        imgUserDynamicTag.setVisibility(View.VISIBLE);
        tvUnreadCount.setVisibility(View.GONE);
        startActivity(new Intent(getContext(), UserDynamicListActivity.class));
    }

    @OnClick({R.id.img_avatar, R.id.user_layout})
    void onUserProfile() {
        if (user != null) {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra("id", user.getId());
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_wedding_date)
    void onWeddingDate() {
        if (user.getWeddingDay() == null) {
            onEdit();
        } else {
            ShareWeddingDateFragment fragment = ShareWeddingDateFragment.newInstance();
            fragment.show(getChildFragmentManager(), "ShareWeddingDateFragment");
        }
    }

    @OnClick(R.id.tv_edit)
    void onEdit() {
        DateTime dateTime;
        if (user.getWeddingDay() != null) {
            dateTime = new DateTime(user.getWeddingDay()
                    .getTime());
        } else {
            dateTime = new DateTime(Calendar.getInstance()
                    .getTime());
        }
        WeddingDateFragment fragment = WeddingDateFragment.newInstance(dateTime);
        fragment.show(getChildFragmentManager(), "WeddingDateFragment");
        fragment.setOnDateSelectedListener(new WeddingDateFragment.onDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar) {
                DateTime weddingDate = new DateTime(calendar.getTime());
                user.setWeddingDay(weddingDate.toDate());
                setWeddingDate();
            }
        });
    }

    @OnClick(R.id.member_view)
    void onOpenMember() {
        startActivity(new Intent(getContext(), OpenMemberActivity.class));
    }

    @OnClick(R.id.collect_layout)
    void onMyCollect() {
        startActivity(new Intent(getContext(), CollectActivity.class));
    }

    @OnClick(R.id.shopping_cart_layout)
    void onShoppingCart() {
        startActivity(new Intent(getContext(), ShoppingCartActivity.class));
    }

    @OnClick(R.id.order_layout)
    void onMyOrder() {
        startActivity(new Intent(getContext(), MyOrderListActivity.class));
    }

    @OnClick(R.id.publish_layout)
    void onMyPublish() {
        startActivity(new Intent(getContext(), MyPublishActivity.class));
    }

    @OnClick(R.id.wallet_layout)
    void onMyWallet() {
        startActivity(new Intent(getContext(), MyWalletActivity.class));
    }

    @OnClick(R.id.wedding_task_layout)
    void onWeddingTask() {
        startActivity(new Intent(getContext(), MarryTaskActivity.class));
    }

    @OnClick(R.id.btn_record_wedding_task)
    void onRecordWeddingTask() {
        Intent intent = new Intent(getContext(), MarryTaskEditActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                R.anim.activity_anim_default);
    }

    @OnClick(R.id.wedding_account_layout)
    void onWeddingAccount() {
        startActivity(new Intent(getContext(), MarryBookActivity.class));
    }

    @OnClick(R.id.btn_record_wedding_account)
    void onRecordWeddingAccount() {
        Intent intent = new Intent(getContext(), MarryBookEditActivity.class);
        startActivityForResult(intent, Constants.RequestCode.EDIT_WEDDING_ACCOUNT);
        getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                R.anim.activity_anim_default);
    }

    @OnClick(R.id.wedding_table_layout)
    void onWeddingTable() {
        startActivity(new Intent(getContext(), WeddingTableListActivity.class));
    }

    @OnClick(R.id.card_layout)
    void onCardList() {
        startActivity(new Intent(getContext(), CardListActivity.class));
    }

    @OnClick(R.id.wedding_budget_layout)
    void onWeddingBudget() {
        if (user == null) {
            return;
        }
        boolean aBoolean = getActivity().getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE)
                .getBoolean(HljCommon.SharedPreferencesNames.WEDDING_BUDGET + user.getId(), false);
        if (aBoolean) {
            startActivity(new Intent(getActivity(), NewWeddingBudgetActivity.class));
        } else {
            if (getBudgetSub == null || getBudgetSub.isUnsubscribed()) {
                getBudgetSub = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                            @Override
                            public void onNext(JsonElement jsonElement) {
                                Intent intent;
                                String detail = CommonUtil.getAsString(jsonElement, "detail");
                                if (CommonUtil.isEmpty(detail)) {
                                    intent = new Intent(getContext(),
                                            NewWeddingBudgetFigureActivity.class);
                                    intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                            UserFragment.class.getSimpleName());
                                } else {
                                    JsonArray jsonArray = GsonUtil.getGsonInstance()
                                            .fromJson(detail, JsonArray.class);
                                    if (jsonArray == null || jsonArray.size() <= 0) {
                                        intent = new Intent(getContext(),
                                                NewWeddingBudgetFigureActivity.class);
                                        intent.putExtra(NewWeddingBudgetFigureActivity.ARG_FROM,
                                                UserFragment.class.getSimpleName());
                                    } else {
                                        intent = new Intent(getActivity(),
                                                NewWeddingBudgetActivity.class);
                                        getContext().getSharedPreferences(Constants.PREF_FILE,
                                                Context.MODE_PRIVATE)
                                                .edit()
                                                .putBoolean(HljCommon.SharedPreferencesNames
                                                                .WEDDING_BUDGET + user.getId(),
                                                        true)
                                                .apply();
                                    }
                                }
                                startActivity(intent);
                            }
                        })
                        .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                        .build();
                ToolsApi.getBudgetInfoObb()
                        .subscribe(getBudgetSub);
            }
        }
    }

    @OnClick(R.id.wedding_wall_layout)
    void onWeddingWall() {
        startActivity(new Intent(getContext(), WXWallActivity.class));
    }

    @OnClick(R.id.wedding_loan_layout)
    void onWeddingLoan() {
        startActivity(new Intent(getContext(), FinancialHomeActivity.class));
    }

    @OnClick(R.id.wedding_calendar_layout)
    void onWeddingCalendar() {
        startActivity(new Intent(getContext(), WeddingCalendarActivity.class));
    }

    private View.OnClickListener onMorePopWinClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (morePopWin != null && morePopWin.isShowing()) {
                morePopWin.dismiss();
            }
            switch (v.getId()) {
                case R.id.setting_layout:
                    startActivity(new Intent(getContext(), SettingActivity.class));
                    break;
                case R.id.follow_layout:
                    startActivity(new Intent(getContext(), FollowActivity.class));
                    break;
                case R.id.praise_layout:
                    try {
                        String marketUri = "market://details?id=" + getContext().getPackageName();
                        Uri uri = Uri.parse(marketUri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.showToast(getContext(),
                                null,
                                R.string.label_msg_praise_error___cm);
                    }
                    break;
                case R.id.feedback_layout:
                    startActivity(new Intent(getContext(), FeedbackActivity.class));
                    break;
                case R.id.contact_layout:
                    CustomerSupportUtil.goToSupport(getActivity(),
                            Support.SUPPORT_KIND_DEFAULT_ROBOT);
                    break;
                case R.id.share_layout:
                    onShare();
                    break;
            }
        }
    };

    private void setWeddingDate() {
        if (user == null || user.getWeddingDay() == null) {
            tvWeddingDate.setText(R.string.label_set_wedding_date5);
        } else {
            DateTime currentDate = new DateTime();
            DateTime weddingDate = new DateTime(user.getWeddingDay());
            int days = Days.daysBetween(currentDate.toLocalDate(), weddingDate.toLocalDate())
                    .getDays();
            tvWeddingDate.setText(getString(R.string.label_wedding_date_limit_days,
                    Math.max(0, days)));
            tvWeddingDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void getUserStatistics() {
        CommonUtil.unSubscribeSubs(getStatisticsSub);
        getStatisticsSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<UserStatistics>() {
                    @Override
                    public void onNext(UserStatistics statistics) {
                        tvCollectCount.setText(String.valueOf(statistics.getCollectionCount()));
                        tvPublishCount.setText(String.valueOf(statistics.getCommunityCount()));
                        tvOrderCount.setText(String.valueOf(statistics.getOrderCount()));
                        tvWeddingTaskCount.setText(getString(R.string.label_wedding_task_count,
                                statistics.getFinishCount(),
                                statistics.getTotalCount()));
                        tvWeddingAccount.setText(getString(R.string.label_wedding_account,
                                CommonUtil.formatDouble2StringWithTwoFloat(statistics
                                        .getCashBookMoney())));
                    }
                })
                .build();
        UserApi.getUserStatisticsObb()
                .subscribe(getStatisticsSub);
    }

    //未读的动态数
    private void getUnreadCount() {
        if (user == null || lastUserId == user.getId()) {
            return;
        }
        lastUserId = user.getId();
        CommonUtil.unSubscribeSubs(getUnreadCountSub);
        getUnreadCountSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        setUnreadCount(integer == null ? 0 : integer);
                    }
                })
                .build();
        UserApi.getUserDynamicUnreadCountObb()
                .subscribe(getUnreadCountSub);
    }

    private void setUnreadCount(int count) {
        if (user == null) {
            return;
        }
        CustomerUser partnerUser = user.getPartnerUser();
        if (partnerUser == null || partnerUser.getId() == 0) {
            return;
        }
        unreadCount = Math.min(count, 99);
        if (unreadCount <= 0) {
            imgUserDynamicTag.setVisibility(View.VISIBLE);
            tvUnreadCount.setVisibility(View.GONE);
        } else {
            imgUserDynamicTag.setVisibility(View.GONE);
            tvUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(String.valueOf(unreadCount));
        }
        isShowedFloatBtn = false;
        setShowFloatBtnView();
    }

    private void setShowFloatBtnView() {
        if (user == null) {
            return;
        }
        if (!isResumed() || isHidden()) {
            return;
        }
        CustomerUser partnerUser = user.getPartnerUser();
        boolean b = partnerUser != null && partnerUser.getId() > 0; //已绑定伴侣
        if (isShowedFloatBtn && isBindedPartner == b) {
            return;
        }
        View v = null;
        if (b) {
            if (lastUserId != user.getId()) {
                unreadCount = -1;
            }
            if (unreadCount > -1) {
                v = userDynamicLayout;
            }
        } else {
            v = btnBindPartner;
            if (unreadCount > 0) {
                unreadCount = 0;
                imgUserDynamicTag.setVisibility(View.VISIBLE);
                tvUnreadCount.setVisibility(View.GONE);
            }
        }
        if (v != null) {
            hideFloatBtnView();
            isBindedPartner = b;
            isShowedFloatBtn = true;
            CommonUtil.unSubscribeSubs(delaySub);
            delaySub = Observable.just(v)
                    .delay(300, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<View>() {
                        @Override
                        public void call(View view) {
                            view.setVisibility(View.VISIBLE);
                            transAnim = ObjectAnimator.ofFloat(view,
                                    View.TRANSLATION_X,
                                    -view.getMeasuredWidth(),
                                    0);
                            transAnim.setDuration(400);
                            transAnim.start();
                        }
                    });
        }
    }

    private void hideFloatBtnView() {
        if (transAnim != null && transAnim.isRunning()) {
            transAnim.cancel();
        }
        isShowedFloatBtn = false;
        btnBindPartner.setVisibility(View.INVISIBLE);
        userDynamicLayout.setVisibility(View.INVISIBLE);
    }

    private void setBarAlpha(float alpha) {
        if (barAlpha == alpha) {
            return;
        }
        barAlpha = alpha;
        btnMerchantRecruit.setAlpha(alpha);
        btnShare.setAlpha(alpha);
        btnMsg.setAlpha(alpha);
        btnMore.setAlpha(alpha);
        dividerView.setAlpha(alpha);
        int red = Color.red(barColor);
        int green = Color.green(barColor);
        int blue = Color.blue(barColor);
        int a = (int) (Color.alpha(barColor) * alpha);
        actionHolderLayout.setBackgroundColor(Color.argb(a, red, green, blue));
    }

    private int getCardNewsCount() {
        if (user == null) {
            return 0;
        }
        Realm realm = Realm.getDefaultInstance();
        int cardNewsCount = (int) realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .notEqualTo("status", 2)
                .beginGroup()
                .equalTo("notifyType", Notification.NotificationType.GIFT)
                .or()
                .equalTo("notifyType", Notification.NotificationType.SIGN)
                .endGroup()
                .count();
        realm.close();
        return cardNewsCount;
    }

    private void setBackgroundAlpha(float bgAlpha) {
        Window win = getActivity().getWindow();
        if (win != null) {
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.alpha = bgAlpha;
            win.setAttributes(lp);
        }
    }

    private void hideUserNewsIcon() {
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).hideUserNewsIcon();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.EDIT_WEDDING_ACCOUNT:
                    boolean b = false;
                    if (data != null) {
                        b = data.getBooleanExtra(MarryBookActivity.ARG_IS_SCROLL_TO_GIFT, false);
                    }
                    Intent intent = new Intent(getContext(), MarryBookActivity.class);
                    intent.putExtra(MarryBookActivity.ARG_IS_SCROLL_TO_GIFT, b);
                    startActivity(intent);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (noticeUtil != null) {
                noticeUtil.onPause();
            }
            hideFloatBtnView();
        } else {
            if (noticeUtil != null) {
                noticeUtil.onResume();
            }
            User user = Session.getInstance()
                    .getCurrentUser(getContext());
            if (user == null || user.getId() == 0) {
                return;
            }
            setUserData(user);
            getUserStatistics();
            new UserTask(getContext(), null).execute(); //同步数据
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        if (user != null) {
            setUserData(user);
            getUserStatistics();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        if (morePopWin != null && morePopWin.isShowing()) {
            morePopWin.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (transAnim != null && transAnim.isRunning()) {
            transAnim.cancel();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(rxBusUserSub,
                rxBusEventSub,
                delaySub,
                getUnreadCountSub,
                getStatisticsSub,
                getBudgetSub,
                getShareSub,
                afterShareSub);
    }
}