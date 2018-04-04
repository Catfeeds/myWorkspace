package com.hunliji.marrybiz.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.MaintainEvent;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.fragment.InteractionFragment;
import com.hunliji.marrybiz.fragment.MarketingFragment;
import com.hunliji.marrybiz.fragment.MessageFragment;
import com.hunliji.marrybiz.fragment.UserInfoFragment;
import com.hunliji.marrybiz.fragment.WorkSpaceFragment;
import com.hunliji.marrybiz.model.Certify;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.model.merchant.MerchantUpgradeInfo;
import com.hunliji.marrybiz.service.GetuiIntentService;
import com.hunliji.marrybiz.service.GetuiPushService;
import com.hunliji.marrybiz.task.HttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.BannerUtil;
import com.hunliji.marrybiz.util.DeviceUuidFactory;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.PropertiesUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.login.CompanyCertificationActivity;
import com.hunliji.marrybiz.widget.CheckableRelativeGroup;
import com.hunliji.marrybiz.widget.CheckableRelativeLayout;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

@Route(path = RouterPath.IntentPath.Merchant.HOME)
@RuntimePermissions
public class HomeActivity extends HljBaseNoBarActivity implements CheckableRelativeGroup
        .OnCheckedChangeListener, OnItemClickListener {

    public static final String WORK_SPACE_FRAGMENT_TAG = "work_space_fragment";
    public static final String MARKETING_FRAGMENT_TAG = "marketing_fragment";
    public static final String INTERACTION_FRAGMENT_TAG = "interaction_fragment";
    public static final String MESSAGE_FRAGMENT_TAG = "message_fragment";
    public static final String USER_INFO_FRAGMENT_TAG = "user_info_fragment";

    @BindView(R.id.real_tab_content)
    FrameLayout realTabContent;
    @BindView(R.id.img_home)
    ImageView imgHome;
    @BindView(R.id.cb_work_space)
    CheckableRelativeLayout cbWorkSpace;
    @BindView(R.id.img_marketing)
    ImageView imgMarketing;
    @BindView(R.id.cb_marketing)
    CheckableRelativeLayout cbMarketing;
    @BindView(R.id.img_interaction)
    ImageView imgInteraction;
    @BindView(R.id.tv_interaction)
    TextView tvInteraction;
    @BindView(R.id.img_interaction_layout)
    LinearLayout imgInteractionLayout;
    @BindView(R.id.view_interaction_tag)
    View viewInteractionTag;
    @BindView(R.id.cb_interaction)
    CheckableRelativeLayout cbInteraction;
    @BindView(R.id.img_msg)
    ImageView imgMsg;
    @BindView(R.id.center_layout)
    LinearLayout centerLayout;
    @BindView(R.id.tv_unread_count)
    TextView tvUnreadCount;
    @BindView(R.id.cb_message)
    CheckableRelativeLayout cbMessage;
    @BindView(R.id.img_mine)
    ImageView imgMine;
    @BindView(R.id.img_mine_layout)
    LinearLayout imgMineLayout;
    @BindView(R.id.view_unread_bind)
    View viewUnreadBind;
    @BindView(R.id.cb_mine)
    CheckableRelativeLayout cbMine;
    @BindView(R.id.check_group_menu)
    CheckableRelativeGroup checkGroupMenu;

    private int lastPosition = -1;
    private int unreadMsgCount;
    private int unreadNoticeCount;
    private boolean isExit;
    private MerchantUser user;
    private Subscription newMsgSubscription;
    private Subscription rxBusSubscription;
    private Subscription errorSubscription;
    private Queue<Dialog> dialogQueue = new LinkedList<>();
    private Dialog currentDialog;
    private Handler handler = new Handler();
    private boolean isShowed;//企业强制认证是否已经认证
    private Handler getuiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HANDLE_LOGIN_DONE:
                    // 新账户登陆成功,发送个推注册信息,初始化个推模块
                    // 初始化个推 SDK
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_FILE,
                            Context.MODE_PRIVATE);
                    String cid = sharedPreferences.getString("clientid", "null");
                    MerchantUser user = Session.getInstance()
                            .getCurrentUser(HomeActivity
                                    .this);
                    if (!JSONUtil.isEmpty(cid) && user.getId()
                            .intValue() != 0) {
                        Tag t = new Tag();
                        t.setName("商家类别" + user.getPropertyId());
                        Tag t2 = new Tag();
                        t2.setName("商家类型" + user.getShopType());
                        Tag[] tags = {t, t2};
                        PushManager.getInstance()
                                .setTag(HomeActivity.this, tags, System.currentTimeMillis() + "");
                        Map<String, Object> data = new HashMap<>();
                        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(HomeActivity
                                .this);
                        data.put("info[cid]", cid);
                        data.put("info[user_id]", String.valueOf(user.getId()));
                        data.put("info[from]", Constants.GE_TUI_FROM);
                        data.put("info[phone_token]",
                                deviceUuidFactory.getDeviceUuid()
                                        .toString());
                        data.put("info[apns_token]", String.valueOf(""));
                        data.put("info[app_version]", Constants.APP_VERSION);
                        data.put("info[phone_type]", String.valueOf(2));
                        data.put("info[device]", Build.MODEL);
                        data.put("info[system]", Build.VERSION.RELEASE);
                        new HttpPostTask(HomeActivity.this, new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                Log.e(HomeActivity.class.getSimpleName(),
                                        "Success with post " + "getui token");
                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                            }
                        }).execute(Constants.getAbsUrl(Constants.HttpPath.GEXIN_TOKEN_URL), data);
                    }
                    break;
            }
            return false;
        }
    });
    private HljHttpSubscriber upgradeCheckSub;
    private Dialog workUpgradeInfoDlg;
    private Subscription hxLoginSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);
            ButterKnife.bind(this);

            initValuesAndSync();
            initMenuTab();
        } catch (Exception e) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            Process.killProcess(Process.myPid());
        }

        setSwipeBackEnable(false);
        initPosterJumpAtStart();
        initFirstInteractionTag();
        initMessageTabClickListener();
        checkWorksUpgradeInfo();
    }

    private void initValuesAndSync() {
        user = Session.getInstance()
                .getCurrentUser(this);
        LinkUtil.getInstance(this);
        // 同步MerchantProperties
        new PropertiesUtil.PropertiesSyncTask(this, null).execute();
        // 初始化 Websocket 连接开始监听消息
        sendToken();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String msg = intent.getStringExtra("msg");
                int index = intent.getIntExtra("index", -1);
                String action = intent.getStringExtra("action");
                if (!TextUtils.isEmpty(msg) || index >= 0 || !TextUtils.isEmpty(action)) {
                    onNewIntent(intent);
                }
            }
        });
        // 配置信息
        new GetDataConfigTask(this).execute();
        // 版本升级检测
        HljUpdate.getInstance()
                .updateCheck(this);

        //申请需要权限
        HomeActivityPermissionsDispatcher.requestPermissionWithCheck(this);
    }

    private void initMenuTab() {
        checkGroupMenu.setOnCheckedChangeListener(this);
        // 如果是婚品商家则只显示三个tab
        if (user.getShopType() == MerchantUser.SHOP_TYPE_PRODUCT) {
            cbMarketing.setVisibility(View.GONE);
            cbInteraction.setVisibility(View.GONE);
            cbWorkSpace.setVisibility(View.VISIBLE);
        } else if (user.getShopType() == MerchantUser.SHOP_TYPE_CAR) {
            // 如果是婚车商家则只显示两个tab（消息和我的）
            cbMarketing.setVisibility(View.GONE);
            cbInteraction.setVisibility(View.GONE);
            cbWorkSpace.setVisibility(View.GONE);
        } else {
            cbMarketing.setVisibility(View.VISIBLE);
            cbInteraction.setVisibility(View.VISIBLE);
            cbWorkSpace.setVisibility(View.VISIBLE);
        }
        // 有新的未读消息或者是婚品商家，默认选中消息tab
        if (NotificationUtil.getChatNewsCount(this) > 0 || user.getShopType() == MerchantUser
                .SHOP_TYPE_PRODUCT || user.getShopType() == MerchantUser.SHOP_TYPE_CAR) {
            cbMessage.setChecked(true);
            selectTab(3);
        } else {
            selectTab(0);
        }
    }

    // 推送的poster进入首页后需要跳转
    private void initPosterJumpAtStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                Poster poster = (Poster) intent.getSerializableExtra("poster");
                if (poster != null) {
                    Util.bannerAction(HomeActivity.this, poster);
                }
            }
        });
    }

    //互动红点,首次显示
    private void initFirstInteractionTag() {
        if (getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getBoolean("Interaction", true)) {
            viewInteractionTag.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置互动红点
     *
     * @param visible
     */
    public void setInteractionTagVisible(boolean visible) {
        viewInteractionTag.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    //消息tab 双击操作
    private void initMessageTabClickListener() {
        cbMessage.setOnClickListener(new View.OnClickListener() {
            long clickTime;

            @Override
            public void onClick(View v) {
                if (checkGroupMenu.getCheckedRadioButtonId() == R.id.message) {
                    if (System.currentTimeMillis() - clickTime < 400) {
                        //双击
                        clickTime = 0;
                        MessageFragment msgFragment = (MessageFragment) getSupportFragmentManager
                                ().findFragmentByTag(
                                MESSAGE_FRAGMENT_TAG);
                        if (msgFragment != null && !msgFragment.isHidden()) {
                            msgFragment.dblclickMessage();
                        }
                    } else {
                        //单击
                        clickTime = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if (HljUpdate.getInstance()
                .getUpdateInfo() == null) {
            HljUpdate.getInstance()
                    .updateCheck(this);
        }
        super.onResume();
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null || user.getId() == 0) {
            CommonUtil.unSubscribeSubs(hxLoginSubscription);
            HljKeFu.logout(this);
        } else {
            hxLoginSubscription = HljKeFu.loginObb(this)
                    .subscribe(new EmptySubscriber<Boolean>());
        }

        registerRxBusEvent();
        registerErrorBusEvent();
        syncMerchantUserProfile();
        updateMessageAndNotifications();
    }

    /**
     * 注册RxBus
     */
    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case WECHAT_BIND_CHANGE:
                                    if (rxEvent.getObject() != null && rxEvent.getObject()
                                            instanceof Boolean) {
                                        viewUnreadBind.setVisibility((Boolean) rxEvent.getObject
                                                () ? View.GONE : View.VISIBLE);
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 检测商家套餐是否需要升级，进行必要提示
     */
    private void checkWorksUpgradeInfo() {
        upgradeCheckSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<MerchantUpgradeInfo>() {
                    @Override
                    public void onNext(MerchantUpgradeInfo merchantUpgradeInfo) {
                        if (merchantUpgradeInfo.isNeedUpgrade()) {
                            showWorksUpgradeInfoDialog(merchantUpgradeInfo.getUrl());
                        }
                    }
                })
                .build();
        MerchantApi.checkWorksNeedUpgrade()
                .subscribe(upgradeCheckSub);
    }

    /**
     * 同步商家资料
     */
    private void syncMerchantUserProfile() {
        // 更新商家资料
        MerchantUserSyncUtil.getInstance()
                .sync(this, new MerchantUserSyncUtil.OnMerchantUserSyncListener() {
                    @Override
                    public void onUserSyncFinish(MerchantUser user) {
                        if (!isShowed) {
                            personalShopCompleteInfo(user);
                        }
                    }
                });
        MerchantUserSyncUtil.getInstance()
                .onBindWechatCheck();
        viewUnreadBind.setVisibility(MerchantUserSyncUtil.getInstance()
                .isBind() ? View.GONE : View.VISIBLE);
    }

    //~对于四大金刚以外，认证类型为个人认证的商家，在商家补齐企业认证信息前，每次登陆都弹出该弹窗：只改服务商家
    private void personalShopCompleteInfo(MerchantUser merchantUser) {
        if (!merchantUser.isIndividualMerchant() && merchantUser.getShopType() == MerchantUser
                .SHOP_TYPE_SERVICE && !merchantUser.isSubAccount()) {
            Certify certify = merchantUser.getCertify();
            //type = 1 (个人认证) 或者 老商家 & type = 0
            if (((certify == null || certify.getType() == 0) && merchantUser.isOpenedTrade()) ||
                    (certify != null && certify.getType() == 1)) {
                String msg = getString(R.string.label_extra_info_msg);
                String confirmStr = getString(R.string.label_extra_info);
                Dialog dialog = showShopCertifyDialog(msg, confirmStr);
                isShowed = true;
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            } else if (certify != null && certify.getType() == 2 && certify.getStatus() == 2) {
                String msg = TextUtils.isEmpty(certify.getFailReason()) ? "未通过审核！" :
                        "您的实名认证审核未通过，原因：" + certify.getFailReason() + " 请重新上传，以免影响店铺正常经营。";
                String confirmStr = "前去上传";
                isShowed = true;
                Dialog dialog = showShopCertifyDialog(msg, confirmStr);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }

    private Dialog showShopCertifyDialog(String msg, String confirmStr) {
        return DialogUtil.createDoubleButtonDialog(this,
                msg,
                confirmStr,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,
                                CompanyCertificationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                },
                null);
    }

    /**
     * 更新未读通知和消息
     */
    private void updateMessageAndNotifications() {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);

        if (user == null || user.getId() == 0) {
            resetNotificationAndWebSocket();

            return;
        }

        NotificationUtil.getInstance(this)
                .getNewNotifications(user.getId());

        // 更新私信
        unreadMsgCount = NotificationUtil.getChatNewsCount(this);
        // 获取通知数
        unreadNoticeCount = NotificationUtil.getInstance(this)
                .getCount(user);

        // 更新未读条数
        setUnreadCount();

        WebSocket.getInstance()
                .socketConnect(this);
        if (newMsgSubscription == null || newMsgSubscription.isUnsubscribed()) {
            newMsgSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            MerchantUser user = Session.getInstance()
                                    .getCurrentUser(HomeActivity.this);
                            if (user == null || user.getId() == 0) {
                                return;
                            }
                            switch (rxEvent.getType()) {
                                case EM_MESSAGE:
                                case WS_MESSAGE:
                                    unreadMsgCount = NotificationUtil.getChatNewsCount
                                            (HomeActivity.this);
                                    setUnreadCount();
                                    break;
                                case NEW_NOTIFICATION:
                                    unreadNoticeCount = NotificationUtil.getInstance(HomeActivity
                                            .this)
                                            .getCount(user);
                                    setUnreadCount();
                                    break;

                            }
                        }
                    });
        }
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void requestPermission() {
        sendGetuiCid();
    }


    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE})
    void onDeniedForPermission() {
        sendGetuiCid();
    }

    private void sendGetuiCid() {
        PushManager.getInstance()
                .initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance()
                .registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
        boolean isLoginDone = getIntent().getBooleanExtra("is_login_done", false);
        if (isLoginDone) {
            Message msg = new Message();
            msg.what = Constants.HANDLE_LOGIN_DONE;
            getuiHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (newMsgSubscription != null && !newMsgSubscription.isUnsubscribed()) {
            newMsgSubscription.unsubscribe();
        }
    }

    public void switchMarkingFragment() {
        onTabChanged(1);
    }

    private void selectTab(int position) {
        if (lastPosition != position) {
            lastPosition = position;
        } else {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        WorkSpaceFragment homeFragment = (WorkSpaceFragment) fm.findFragmentByTag(
                WORK_SPACE_FRAGMENT_TAG);
        MarketingFragment marketingFragment = (MarketingFragment) fm.findFragmentByTag(
                MARKETING_FRAGMENT_TAG);
        InteractionFragment interactionFragment = (InteractionFragment) fm.findFragmentByTag(
                INTERACTION_FRAGMENT_TAG);
        UserInfoFragment userInfoFragment = (UserInfoFragment) fm.findFragmentByTag(
                USER_INFO_FRAGMENT_TAG);
        MessageFragment msgFragment = (MessageFragment) fm.findFragmentByTag(MESSAGE_FRAGMENT_TAG);

        FragmentTransaction ft = fm.beginTransaction();
        if (homeFragment != null && !homeFragment.isHidden()) {
            ft.hide(homeFragment);
        }
        if (marketingFragment != null && !marketingFragment.isHidden()) {
            ft.hide(marketingFragment);
        }
        if (interactionFragment != null && !interactionFragment.isHidden()) {
            ft.hide(interactionFragment);
        }
        if (msgFragment != null && !msgFragment.isHidden()) {
            ft.hide(msgFragment);
        }
        if (userInfoFragment != null && !userInfoFragment.isHidden()) {
            ft.hide(userInfoFragment);
        }

        // 切换Fragment的时候,更新通知
        if (Session.getInstance()
                .getCurrentUser(this) != null) {
            long userId = Session.getInstance()
                    .getCurrentUser(this)
                    .getId();
            NotificationUtil.getInstance(this)
                    .getNewNotifications(userId);
        }

        switch (position) {
            case 0:
                // 工作台
                if (homeFragment == null) {
                    ft.add(R.id.real_tab_content,
                            WorkSpaceFragment.newInstance(),
                            WORK_SPACE_FRAGMENT_TAG);
                } else {
                    ft.show(homeFragment);
                }
                break;
            case 1:
                // 营销
                if (marketingFragment == null) {
                    marketingFragment = MarketingFragment.newInstance();
                    ft.add(R.id.real_tab_content, marketingFragment, MARKETING_FRAGMENT_TAG);
                    marketingFragment.setOnItemClickListener(this);
                } else {
                    ft.show(marketingFragment);
                }
                break;
            case 2:
                // 互动
                if (viewInteractionTag.getVisibility() == View.VISIBLE) {
                    //                    viewInteractionTag.setVisibility(View.GONE);
                    getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                            .putBoolean("Interaction", false)
                            .apply();
                }
                if (interactionFragment == null) {
                    ft.add(R.id.real_tab_content,
                            InteractionFragment.newInstance(),
                            INTERACTION_FRAGMENT_TAG);
                } else {
                    ft.show(interactionFragment);
                }
                break;
            case 3:
                // 消息
                if (msgFragment == null) {
                    ft.add(R.id.real_tab_content,
                            MessageFragment.newInstance(),
                            MESSAGE_FRAGMENT_TAG);
                } else {
                    ft.show(msgFragment);
                }
                break;
            case 4:
                // 我的
                if (userInfoFragment == null) {
                    ft.add(R.id.real_tab_content,
                            UserInfoFragment.newInstance(),
                            USER_INFO_FRAGMENT_TAG);
                } else {
                    userInfoFragment.refresh();
                    ft.show(userInfoFragment);
                }
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * 更新未读条数
     */
    private void setUnreadCount() {
        int count = unreadMsgCount + unreadNoticeCount;
        if (count > 0) {
            tvUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(String.valueOf(count > 99 ? "99+" : count));
        } else {
            tvUnreadCount.setVisibility(View.GONE);
        }
    }

    private void resetNotificationAndWebSocket() {
        WebSocket.getInstance()
                .disconnect(this);

        tvUnreadCount.setVisibility(View.GONE);
        NotificationUtil.getInstance(this)
                .logout();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(upgradeCheckSub,
                hxLoginSubscription,
                errorSubscription,
                rxBusSubscription);
        WebSocket.getInstance()
                .disconnect(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra(SystemNotificationUtil.ASG_NOTIFY_ID)) {
            int notifyId = intent.getIntExtra(SystemNotificationUtil.ASG_NOTIFY_ID, 0);
            SystemNotificationUtil.INSTANCE.readNotification(this, notifyId);
        }

        String json = intent.getStringExtra(SystemNotificationUtil.ASG_MSG);
        //跳到HomeActivity 指定页面的index
        int pageIndex = intent.getIntExtra("page_index", -1);
        final int index = intent.getIntExtra(SystemNotificationUtil.ASG_INDEX, -1);
        String taskId = intent.getStringExtra(SystemNotificationUtil.ASG_TASK_ID);
        String messageId = intent.getStringExtra(SystemNotificationUtil.ASG_MESSAGE_ID);
        String action = intent.getStringExtra("action");

        if (!JSONUtil.isEmpty(taskId) && !JSONUtil.isEmpty(messageId)) {
            PushManager.getInstance()
                    .sendFeedbackMessage(this, taskId, messageId, 90002);
        }
        if (!JSONUtil.isEmpty(json)) {
            try {
                JSONObject object = new JSONObject(json);
                int type = object.optInt("type", 1);
                if (type == 4) {
                    int property = object.optInt("property", 0);
                    long forwardId = object.optLong("forwardId", 0);
                    String webUrl = JSONUtil.getString(object, "path");
                    BannerUtil.bannerAction(this, property, forwardId, webUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (index >= 0) {
            MerchantUser user = Session.getInstance()
                    .getCurrentUser(this);
            if (user == null || user.getId()
                    .intValue() == 0) {
                return;
            }
            if (index > 0) {
                onTabChanged(3);
            } else {
                onTabChanged(0);
            }
        } else if (pageIndex >= 0) {
            //跳到HomeActivity 指定页面
            onTabChanged(Math.min(4, pageIndex));
        } else if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case "interaction":
                    onTabChanged(2);
                    break;

            }
        }
        super.onNewIntent(intent);
    }

    /**
     * 发送客户端类型信息
     */
    private void sendToken() {
        Map<String, Object> data = new HashMap<>();

        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(HomeActivity.this);
        data.put("phone[phone_token]",
                deviceUuidFactory.getDeviceUuid()
                        .toString());
        data.put("phone[apns_token]", String.valueOf(""));
        data.put("phone[app_version]", Constants.APP_VERSION);
        data.put("phone[phone_type]", String.valueOf(2));
        data.put("phone[device]", Build.MODEL);
        data.put("phone[system]", Build.VERSION.RELEASE);
        data.put("phone[city]", String.valueOf(""));
        data.put("phone[province]", String.valueOf(""));
        new HttpPostTask(HomeActivity.this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject object = (JSONObject) obj;
                if (object != null) {
                    long systemTime = object.optLong("current_time");
                    if (systemTime > 0) {
                        TimeUtil.setTimeOffset(systemTime * 1000);
                        HljTimeUtils.setTimeOffset(systemTime * 1000);
                    }
                }
            }

            @Override
            public void onRequestFailed(Object obj) {

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.TOKEN_POST_PATH), data);
    }

    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    /**
     * 二次返回退出应用
     */
    private void exitBy2Click() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, R.string.label_quit, Toast.LENGTH_SHORT)
                    .show();
            Timer tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CheckableRelativeGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cb_work_space:
                selectTab(0);
                break;
            case R.id.cb_marketing:
                selectTab(1);
                break;
            case R.id.cb_interaction:
                selectTab(2);
                break;
            case R.id.cb_message:
                selectTab(3);
                break;
            case R.id.cb_mine:
                selectTab(4);
                break;
        }
    }

    private void showWorksUpgradeInfoDialog(final String url) {
        if (workUpgradeInfoDlg == null) {
            workUpgradeInfoDlg = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.msg_works_upgrade_info),
                    "了解详情",
                    getString(R.string.label_close),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            workUpgradeInfoDlg.cancel();
                            Intent intent = new Intent(HomeActivity.this, HljWebViewActivity.class);
                            intent.putExtra("path", url);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    },
                    null);
        }

        showDialog(workUpgradeInfoDlg);
    }

    /**
     * 主页上要显示dialog都调用这个，是用FIFO队列对dialog进行排队，取消前一个后再显示下一个
     * 只要队列中有升级弹窗,优先显示升级弹窗
     *
     * @param dialog
     */
    private void showDialog(Dialog dialog) {
        if (dialog != null) {
            if (currentDialog != null) {
                // 如果新进入的是升级弹窗且当前显示的弹窗不是是升级弹窗
                // 先隐藏当前弹窗,然后将两个弹窗按顺序放入队列,设计弹窗在前
                dialogQueue.offer(dialog);
                dialogQueue.offer(currentDialog);
                currentDialog.setOnDismissListener(null);
                currentDialog.dismiss();
                currentDialog = null;
            } else {
                dialogQueue.offer(dialog);
            }
        }

        if (currentDialog == null) {
            currentDialog = dialogQueue.poll();
            if (currentDialog != null) {
                currentDialog.show();
                currentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // 显示下一个
                        currentDialog = null;
                        showDialog(null);
                    }
                });
            }
        }
    }

    /**
     * 天眼系统 如果商家是旗舰版或者开通了cpm 跳转倒消息页面
     *
     * @param position
     */
    @Override
    public void onItemClick(int position, Object object) {
        onTabChanged(3);
    }

    private void onTabChanged(int index) {
        try {
            CheckableRelativeLayout childView = (CheckableRelativeLayout) checkGroupMenu.getChildAt(
                    index);
            if (childView.getVisibility() != View.VISIBLE) {
                return;
            }
            childView.setChecked(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetDataConfigTask extends AsyncTask<Object, Object, Object> {

        private Context mContext;

        private GetDataConfigTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(HomeActivity.this,
                        Constants.getAbsUrl(Constants.HttpPath.DATA_CONFIG));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                Session.getInstance()
                        .setDataConfig(mContext, new JSONObject(json).optJSONObject("config"));
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * 初始化服务异常rxBus注册
     */
    private void registerErrorBusEvent() {
        if (errorSubscription == null || errorSubscription.isUnsubscribed()) {
            errorSubscription = RxBus.getDefault()
                    .toObservable(MaintainEvent.class)
                    .filter(new Func1<MaintainEvent, Boolean>() {
                        @Override
                        public Boolean call(MaintainEvent rxEvent) {
                            return rxEvent.getType() == MaintainEvent.EventType.USER_TOKEN_ERROR;
                        }
                    })
                    .first()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<MaintainEvent>() {
                        @Override
                        protected void onEvent(MaintainEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case MaintainEvent.EventType.USER_TOKEN_ERROR:
                                    Intent intent = new Intent(HomeActivity.this,
                                            PreLoginActivity.class);
                                    intent.putExtra("logout", true);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent
                                            .FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(RouterPath.IntentPath.Customer.Login
                                                    .ARG_IS_RESET,
                                            true);
                                    getApplicationContext().startActivity(intent);
                                    break;
                            }
                        }
                    });
        }
    }
}
