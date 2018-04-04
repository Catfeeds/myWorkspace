package me.suncloud.marrymemo.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.suncloud.hljweblibrary.utils.JsUtil;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonElement;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.MaintainEvent;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.OncePrefUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.hljnotelibrary.utils.NotePrefUtil;
import com.hunliji.hljpushlibrary.models.PushData;
import com.hunliji.hljpushlibrary.models.activity.ActivityData;
import com.hunliji.hljpushlibrary.models.notify.NotifyData;
import com.hunliji.hljpushlibrary.models.notify.NotifyTask;
import com.hunliji.hljpushlibrary.utils.PushUtil;
import com.hunliji.hljpushlibrary.websocket.PushSocket;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.igexin.sdk.PushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.api.bindpartner.PartnerApi;
import me.suncloud.marrymemo.fragment.HomePageFragment;
import me.suncloud.marrymemo.fragment.RelativeCityFragment;
import me.suncloud.marrymemo.fragment.SocialHomeFragment;
import me.suncloud.marrymemo.fragment.merchant.FindMerchantHomeFragment;
import me.suncloud.marrymemo.fragment.product.ProductHomeFragment;
import me.suncloud.marrymemo.fragment.user.UserFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.RelativeCity;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.service.GetuiIntentService;
import me.suncloud.marrymemo.service.GetuiPushService;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.UserTask;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.CityUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DataConfigUtil;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NotificationUtil;
import me.suncloud.marrymemo.util.PointUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.acm.CustomerACMUtil;
import me.suncloud.marrymemo.util.user.UserPrefUtil;
import me.suncloud.marrymemo.view.binding_partner.BindingPartnerActivity;
import me.suncloud.marrymemo.view.finder.UserPrepareCategoryListActivity;
import me.suncloud.marrymemo.view.login.LoginActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.BindPartnerHintView;
import me.suncloud.marrymemo.widget.QuestionAnswerHintView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.SubscriptionList;

@Route(path = RouterPath.IntentPath.Customer.MAIN)
@RuntimePermissions
public class MainActivity extends HljBaseNoBarActivity implements TabHost.OnTabChangeListener,
        OnClickListener {
    public static final String ARG_SHOW_PARTNER_INVITATION = "show_partner_invitation";
    private final int NOTICE_LOGIN = 100;
    @BindView(R.id.realtabcontent)
    FrameLayout realtabcontent;
    @BindView(R.id.msg_hint)
    TextView msgHint;
    @BindView(R.id.main_notice_image)
    ImageView mainNoticeImage;
    @BindView(R.id.main_notice_close)
    ImageView mainNoticeClose;
    @BindView(R.id.main_notice_layout)
    RelativeLayout mainNoticeLayout;
    @BindView(R.id.tab1)
    LinearLayout tab1;
    @BindView(R.id.tab2)
    LinearLayout tab2;
    @BindView(R.id.tab3)
    LinearLayout tab3;
    @BindView(R.id.tab4)
    LinearLayout tab4;
    @BindView(R.id.tab5)
    LinearLayout tab5;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(android.R.id.tabs)
    TabWidget tabs;
    @BindView(android.R.id.tabhost)
    TabHost tabHost;
    @BindView(R.id.hint_layout)
    BindPartnerHintView hintLayout;
    @BindView(R.id.question_answer_hint_layout)
    QuestionAnswerHintView questionAnswerHintLayout;
    @BindView(R.id.iv_send_post_hint)
    ImageView ivSendPostHint;

    private View userNewsIcon;
    public View cardNewsIcon;
    public View communityNews;
    public TextView socialNewsCount;
    private int lastPosition;
    private boolean isExit;
    private QueueDialog cityDialog;
    private Poster popupPoster;
    private QueueDialog popUpDialog;
    private Queue<QueueDialog> dialogQueue = new LinkedList<>();
    private QueueDialog currentDialog; // 当前显示的dialog
    private City city;
    private boolean isCloseNotice = false;
    private Subscription rxBusSubscription;
    private Notification partnerInviteNoti; // 弹窗邀请的通知实体
    private QueueDialog partnerInviteDlg;
    private HljHttpSubscriber acceptInviteSub;
    private Handler handler = new Handler();
    private Realm realm;
    private CityUtil cityUtil;
    private QueueDialog praiseDialog;//点赞dialog
    private HljHttpSubscriber praiseDialogSubscriber;
    private QueueDialog notiOpenDlg;
    private Subscription hxLoginSubscription;
    private Subscription serviceErrorSubscription;
    private Subscription tokenErrorSubscription;
    private HljHttpSubscriber checkServiceSubscriber;

    public static final String ARG_MAIN_ACTION = "action";

    public static final String FRAGMENT_TAG_1 = "first_tab_fragment";
    public static final String FRAGMENT_TAG_2 = "second_tab_fragment";
    public static final String FRAGMENT_TAG_3 = "third_tab_fragment";
    public static final String FRAGMENT_TAG_4 = "fourth_tab_fragment";
    public static final String FRAGMENT_TAG_5 = "fifth_tab_fragment";

    public static final String MAIN_ACTION_COMMUNITY = "community";
    public static final String MAIN_ACTION_LIVE = "live";
    public static final String MAIN_ACTION_PRODUCT = "product";
    public static final String MAIN_ACTION_SIGN_IN = "sign_in";
    private int socialTabPage = SocialHomeFragment.TAB_INDEX_SOCIAL_HOT;

    private Subscription notifyTimerSubscription;
    private boolean isShowFinder;

    private SubscriptionList acmSubscriptions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            initValues();
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            initACMConfig();

            initViews();
            PushSocket.INSTANCE.onStart(this);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    PushSocket.INSTANCE.eventInitial();
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            Process.killProcess(Process.myPid());
        }
    }

    private void initValues() {
        setSwipeBackEnable(false);
        realm = Realm.getDefaultInstance();
        // 初始化或重启RxBus注册
        registerRxBusEvent();
        registerServiceErrorBusEvent();
        registerTokenErrorBusEvent();

        cityUtil = new CityUtil(this, new CityUtil.OnGetCityResultListener() {
            @Override
            public void onResult(City result) {
                if (result != null && result.getId() > 0) {
                    City city = Session.getInstance()
                            .getMyCity(MainActivity.this);
                    if (city == null || !city.getId()
                            .equals(result.getId())) {
                        setMyCity(result);
                    }
                }
            }
        });
        city = Session.getInstance()
                .getMyCity(this);
        checkingPermissions();

        lastPosition = -1;
    }

    private void initViews() {
        Point point = JSONUtil.getDeviceSize(this);
        int size = Math.round(point.x * 5 / 36);
        mainNoticeLayout.getLayoutParams().height = size;
        mainNoticeClose.getLayoutParams().width = size;
        mainNoticeImage.setOnClickListener(this);
        mainNoticeClose.setOnClickListener(this);
        HljUpdate.getInstance()
                .updateCheck(this);
        tabsInit();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String msg = intent.getStringExtra("msg");
                String action = intent.getStringExtra("action");
                Poster poster = intent.getParcelableExtra("poster");
                int index = intent.getIntExtra("index", -1);
                Uri uri = intent.getData();
                if (uri != null || !JSONUtil.isEmpty(msg) || index >= 0 || !JSONUtil.isEmpty
                        (action)) {
                    onNewIntent(intent);
                } else if (poster != null) {
                    BannerUtil.bannerAction(MainActivity.this, poster, city, true, null);
                }
            }
        });
        JsUtil.getInstance()
                .loadJsInfo(this);
        DataConfigUtil.getInstance()
                .executeDataConfigTask(this);
        new GetPopupPosterTask().executeOnExecutor(Constants.INFOTHEADPOOL);

        initShowPraiseDialog();
    }

    /**
     * 同步ACM配置
     */
    private void initACMConfig() {
        acmSubscriptions = new SubscriptionList();
        acmSubscriptions.add(CustomerACMUtil.initFinancialSwitch(this));
        acmSubscriptions.add(CustomerACMUtil.synTrackerParameter(this));
        acmSubscriptions.add(CustomerACMUtil.initSharePosterConfig(this));
        acmSubscriptions.add(CustomerACMUtil.synAppConfigParameter(this));
        isShowFinder = SPUtils.getBoolean(this, CustomerACMUtil.IS_SHOW_FINDER_FRAGMENT, false);
    }

    /**
     * 首页点赞dialog
     */
    private void initShowPraiseDialog() {
        SharedPreferences preferences = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        boolean hasShowed = preferences.getBoolean(HljCommon.SharedPreferencesNames
                        .SHOW_PRAISE_DIALOG,
                false);
        int startCount = preferences.getInt(HljCommon.SharedPreferencesNames.APP_START_COUNT, 0);
        String version = preferences.getString(HljCommon.SharedPreferencesNames
                        .SHOW_PRAISE_DIALOG_VERSION,
                null);
        //如果没有显示，判断是不是第11次显示
        if (!hasShowed) {
            if (startCount == 11) {
                showPraiseDialog();
            }
            return;
        }
        //如果已经显示，判断上次显示的和当前显示的版本号是否一致，不一致请求服务器 判断显示与否
        String currentVersion = Constants.APP_VERSION;
        if (currentVersion.equalsIgnoreCase(version)) {
            return;
        }
        praiseDialogSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        if (jsonElement != null) {
                            boolean needcomment = jsonElement.getAsJsonObject()
                                    .get("needcomment")
                                    .getAsBoolean();
                            if (needcomment) {
                                showPraiseDialog();
                            }
                        }
                    }
                })
                .build();
        CustomCommonApi.getCommentApp()
                .subscribe(praiseDialogSubscriber);
    }

    private void showPraiseDialog() {
        savePraiseDialogConfig();
        praiseDialog = com.hunliji.hljcommonlibrary.utils.DialogUtil
                .createDoubleButtonDialogWithImage(
                this,
                getString(R.string.label_msg_praise_error_hint___cm, getString(R.string.app_name)),
                com.hunliji.hljcardlibrary.R.mipmap.icon_new_praise___cm,
                "给好评",
                "吐槽",
                true,
                null,
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        goToPraise();
                    }
                },
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToFeedback();
                    }
                });
        showDialog(praiseDialog);
    }

    private void savePraiseDialogConfig() {
        SharedPreferences sp = getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        sp.edit()
                .putBoolean(HljCommon.SharedPreferencesNames.SHOW_PRAISE_DIALOG, true)
                .putString(HljCommon.SharedPreferencesNames.SHOW_PRAISE_DIALOG_VERSION,
                        Constants.APP_VERSION)
                .apply();
    }

    private void goToPraise() {
        try {
            String marketUri = "market://details?id=" + getPackageName();
            Uri uri = Uri.parse(marketUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.label_msg_praise_error___cm, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void goToFeedback() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (getIntent() != null) {
            super.onRestoreInstanceState(savedInstanceState);
        } else {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    public void tabsInit() {
        View tab1 = getLayoutInflater().inflate(R.layout.tab_view, null);
        ImageView icon = tab1.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.sl_ic_merchant);
        TextView text = tab1.findViewById(R.id.text);
        text.setText(R.string.label_main_menu2);
        View tab2 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = tab2.findViewById(R.id.icon);
        text = tab2.findViewById(R.id.text);
        if (isShowFinder) {
            icon.setImageResource(R.drawable.sl_ic_found);
            text.setText(R.string.label_main_menu7);
        } else {
            icon.setImageResource(R.drawable.sl_ic_find_merchant);
            text.setText(R.string.label_main_menu8);
        }
        View tab3 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = tab3.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.sl_ic_social);
        text = tab3.findViewById(R.id.text);
        text.setText(R.string.label_main_menu6);
        //新娘说红点，每天显示一次
        communityNews = tab3.findViewById(R.id.news);
        socialNewsCount = tab3.findViewById(R.id.news_count);
        setCommunityNews();
        View tab4;
        tab4 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = tab4.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.sl_ic_shop);
        text = tab4.findViewById(R.id.text);
        text.setText(TextUtils.isEmpty(Session.getInstance()
                .getDataConfig(this)
                .getFourthTabTitle()) ? "严选" : Session.getInstance()
                .getDataConfig(this)
                .getFourthTabTitle());
        View tab5 = getLayoutInflater().inflate(R.layout.tab_view, null);
        icon = tab5.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.sl_ic_setting);
        text = tab5.findViewById(R.id.text);
        userNewsIcon = tab5.findViewById(R.id.news);
        text.setText(R.string.label_main_menu5);
        tabHost.setOnTabChangedListener(this);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec(FRAGMENT_TAG_1)
                .setIndicator(tab1)
                .setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec(FRAGMENT_TAG_2)
                .setIndicator(tab2)
                .setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec(FRAGMENT_TAG_3)
                .setIndicator(tab3)
                .setContent(R.id.tab3));
        tabHost.addTab(tabHost.newTabSpec(FRAGMENT_TAG_4)
                .setIndicator(tab4)
                .setContent(R.id.tab4));
        tabHost.addTab(tabHost.newTabSpec(FRAGMENT_TAG_5)
                .setIndicator(tab5)
                .setContent(R.id.tab5));
        tabHost.getTabWidget()
                .getChildAt(1)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tabHost.getCurrentTab() == 1) {
                            fragmentScrollTop(FRAGMENT_TAG_2);
                        } else {
                            setTabSelect(1);
                        }
                    }
                });
    }

    private void fragmentScrollTop(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null && !fragment.isHidden()) {
            if (fragment instanceof HomePageFragment) {
                ((HomePageFragment) fragment).scrollTop();
            } else if (fragment instanceof SocialHomeFragment) {
            } else if (fragment instanceof FindMerchantHomeFragment) {
                ((FindMerchantHomeFragment) fragment).refresh();
            }
        }
    }

    public void selectChange(int position) {
        if (lastPosition == 2 && lastPosition != position) {
            if (ivSendPostHint.getVisibility() == View.VISIBLE)
                ivSendPostHint.setVisibility(View.GONE);
        }
        if (lastPosition != position) {
            lastPosition = position;
        } else {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        HomePageFragment homePageFragment = (HomePageFragment) fm.findFragmentByTag(FRAGMENT_TAG_1);
        FindMerchantHomeFragment finderFragment = (FindMerchantHomeFragment) fm.findFragmentByTag(
                FRAGMENT_TAG_2);
        SocialHomeFragment socialHomeFragment = (SocialHomeFragment) fm.findFragmentByTag(
                FRAGMENT_TAG_3);
        Fragment fourthFragment;
        fourthFragment = fm.findFragmentByTag(FRAGMENT_TAG_4);
        UserFragment userFragment = (UserFragment) fm.findFragmentByTag(FRAGMENT_TAG_5);
        FragmentTransaction ft = fm.beginTransaction();
        if (finderFragment != null && !finderFragment.isHidden())
            ft.hide(finderFragment);
        if (homePageFragment != null && !homePageFragment.isHidden())
            ft.hide(homePageFragment);
        if (fourthFragment != null && !fourthFragment.isHidden())
            ft.hide(fourthFragment);
        if (socialHomeFragment != null && !socialHomeFragment.isHidden())
            ft.hide(socialHomeFragment);
        if (userFragment != null && !userFragment.isHidden())
            ft.hide(userFragment);
        if (Session.getInstance()
                .getCurrentUser(this) != null) {
            long userId = Session.getInstance()
                    .getCurrentUser(this)
                    .getId();
            if (userId != 0) {
                NotificationUtil.getInstance(this)
                        .getNewNotifications(userId);
            }
        }
        switch (position) {
            case 0:
                new HljTracker.Builder(this).eventableType("tab_main")
                        .action("hit")
                        .sid("A1")
                        .pos(position + 1)
                        .desc(getString(R.string.label_main_menu2))
                        .build()
                        .send();
                if (homePageFragment == null) {
                    ft.add(R.id.realtabcontent, new HomePageFragment(), FRAGMENT_TAG_1);
                } else {
                    ft.show(homePageFragment);
                }
                break;
            case 1:
                new HljTracker.Builder(this).eventableType("tab_find")
                        .action("hit")
                        .sid("A1")
                        .pos(position + 1)
                        .desc(getString(R.string.label_main_menu7))
                        .build()
                        .send();
                if (finderFragment == null) {
                    ft.add(R.id.realtabcontent,
                            FindMerchantHomeFragment.newInstance(isShowFinder),
                            FRAGMENT_TAG_2);
                } else {
                    ft.show(finderFragment);
                }
                break;
            case 2:
                new HljTracker.Builder(this).eventableType("tab_xns")
                        .action("hit")
                        .sid("A1")
                        .pos(position + 1)
                        .desc(getString(R.string.label_main_menu6))
                        .build()
                        .send();
                if (socialHomeFragment == null) {
                    ft.add(R.id.realtabcontent,
                            SocialHomeFragment.newInstance(socialTabPage),
                            FRAGMENT_TAG_3);
                } else {
                    socialHomeFragment.setTabPage(socialTabPage);
                    ft.show(socialHomeFragment);
                }
                socialTabPage = -1;
                break;
            case 3:
                if (fourthFragment == null) {
                    fourthFragment = ProductHomeFragment.newInstance(true, false);
                    ft.add(R.id.realtabcontent, fourthFragment, FRAGMENT_TAG_4);
                } else {
                    ft.show(fourthFragment);
                }
                new HljTracker.Builder(this).eventableType("tab_product_buy")
                        .action("hit")
                        .sid("A1")
                        .pos(position + 1)
                        .desc(getString(R.string.label_main_menu32))
                        .build()
                        .send();
                break;
            case 4:
                new HljTracker.Builder(this).eventableType("tab_me")
                        .action("hit")
                        .sid("A1")
                        .pos(position + 1)
                        .desc(getString(R.string.label_main_menu5))
                        .build()
                        .send();
                if (userFragment == null) {
                    ft.add(R.id.realtabcontent, UserFragment.newInstance(), FRAGMENT_TAG_5);
                } else {
                    ft.show(userFragment);
                }
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
        handler.post(runnableUi);
    }


    public static final String TAB_ACTION_FIRST = "weddingExpoFragment";
    public static final String TAB_ACTION_SECOND = "finderFragment";
    public static final String TAB_ACTION_THIRD = "storyListFragment";
    public static final String TAB_ACTION_FOURTH = "cardEditFragment";
    public static final String TAB_ACTION_FIFTH = "userFragment";

    //weddingExpoFragment 等同 homePageFragment
    public void onTabChanged(String tabId, int page) {
        if (tabId.equalsIgnoreCase(TAB_ACTION_FIRST)) {
            setTabSelect(0);
        } else if (tabId.equalsIgnoreCase(TAB_ACTION_SECOND)) {
            setTabSelect(1);
        } else if (tabId.equalsIgnoreCase(TAB_ACTION_THIRD)) {
            setTabSelect(2);
        } else if (tabId.equalsIgnoreCase(TAB_ACTION_FOURTH)) {
            setTabSelect(3);
        } else if (tabId.equalsIgnoreCase(TAB_ACTION_FIFTH)) {
            setTabSelect(4);
        }
    }

    @Override
    public void onBackPressed() {
        if (tabHost.getCurrentTab() == 1) {
            FindMerchantHomeFragment finderFragment = (FindMerchantHomeFragment)
                    getSupportFragmentManager().findFragmentByTag(
                    FRAGMENT_TAG_2);
            if (finderFragment != null && finderFragment.hideMenu()) {
                return;
            }
        }
        if (hintLayout.getVisibility() == View.VISIBLE) {
            hintLayout.setVisibility(View.GONE);
            return;
        }
        if (questionAnswerHintLayout.getVisibility() == View.VISIBLE) {
            questionAnswerHintLayout.setVisibility(View.GONE);
            return;
        }
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastUtil.showToast(this, null, R.string.label_quit);
            tExit = new Timer();
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

    private Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            User user = Session.getInstance()
                    .getCurrentUser(MainActivity.this);
            if (user == null) {
                return;
            }
            int count = NotificationUtil.getChatNewsCount(MainActivity.this);
            boolean isCanShowUserNewsIcon = UserPrefUtil.getInstance(MainActivity.this)
                    .isCanShowUserNewsIcon();
            if (isCanShowUserNewsIcon && (user != null && (user.getPartnerUser() == null || user
                    .getPartnerUser()
                    .getId() == 0))) {
                // 当天第一次进入，如果没有伴侣，「我们」显示红点，进入后隐藏红点
                userNewsIcon.setVisibility(View.VISIBLE);
            }
            if (count > 0) {
                msgHint.setVisibility(View.VISIBLE);
                msgHint.setText(getString(R.string.hint_unread_msg, String.valueOf(count)));
            } else {
                msgHint.setVisibility(View.GONE);
            }
            setCommunityNews();
        }

    };

    @Override
    public void onTabChanged(String tabId) {
        if (!JSONUtil.isEmpty(tabId)) {

            switch (tabId) {
                case FRAGMENT_TAG_1:
                    selectChange(0);
                    break;
                case FRAGMENT_TAG_2:
                    User user = Session.getInstance()
                            .getCurrentUser(this);
                    DataConfig dataConfig = Session.getInstance()
                            .getDataConfig(this);
                    boolean isShowUserPrepareView = user != null && user.getId() > 0 &&
                            dataConfig != null && dataConfig.isShowUserPreparation() &&
                            NotePrefUtil.getInstance(
                            this)
                            .isShowUserPrepareView();
                    if (isShowUserPrepareView) {
                        setTabSelect(lastPosition);
                        NotePrefUtil.getInstance(this)
                                .setUserPrepareTimestamp(HljTimeUtils.getServerCurrentTimeMillis());
                        Intent intent = new Intent(this, UserPrepareCategoryListActivity.class);
                        startActivityForResult(intent,
                                Constants.RequestCode.USER_PREPARE_CATEGORY_LIST);
                        overridePendingTransition(R.anim.slide_in_up_to_top,
                                R.anim.activity_anim_default);
                        break;
                    }
                    selectChange(1);
                    break;
                case FRAGMENT_TAG_3:
                    selectChange(2);
                    break;
                case FRAGMENT_TAG_4:
                    selectChange(3);
                    break;
                case FRAGMENT_TAG_5:
                    if (!Util.loginBindChecked(MainActivity.this, Constants.Login.MAIN_LOGIN)) {
                        setTabSelect(lastPosition);
                        break;
                    }
                    selectChange(4);
                    break;
                default:
                    break;
            }
        }

        // 所有切换tab的时候都进行一次是否弹窗检测
        handler.post(new Runnable() {
            @Override
            public void run() {
                showPartnerInviteDlg();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_notice_image:
                Util.loginBindChecked(this, NOTICE_LOGIN);
                break;
            case R.id.main_notice_close:
                mainNoticeLayout.setVisibility(View.GONE);
                isCloseNotice = true;
                break;
            default:
                break;
        }
    }

    private void onShowHljPush() {
        if (!CommonUtil.isUnsubscribed(notifyTimerSubscription)) {
            return;
        }
        final PushData pushData = PushUtil.INSTANCE.getPushData(this);
        if (pushData == null) {
            return;
        }
        if (pushData.getPushDataType() == PushData.DATA_TYPE_LIVE) {
            PushUtil.showLiveOnView(this, pushData.getLive(), null);
        } else if (pushData.getPushDataType() == PushData.DATA_TYPE_NOTIFY) {
            NotifyData notifyData = pushData.getNotifyData();
            if (notifyData.getUserId() > 0) {
                User user = Session.getInstance()
                        .getCurrentUser(this);
                if (user == null || user.getId() != notifyData.getUserId()) {
                    return;
                }
            }
            switch (notifyData.getTask()
                    .getKind()) {
                case NotifyTask.SHOW_KIND_IMAGE:
                    if (TextUtils.isEmpty(notifyData.getTask()
                            .getImagePath())) {
                        return;
                    }
                    showDialog(PushUtil.createNotifyPosterDlg(this, notifyData, null, null));
                    break;
                case NotifyTask.SHOW_KIND_TEXT:
                    PushUtil.showNotifyView(this, notifyData, null);
                    break;
            }
        } else if (pushData.getPushDataType() == PushData.DATA_TYPE_ACTIVITY) {
            ActivityData activityData = pushData.getActivityData();
            showDialog(PushUtil.createActivityDlg(this, activityData, null, null));
        }

        notifyTimerSubscription = Observable.timer(1, TimeUnit.MINUTES)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        onShowHljPush();
                    }
                });
    }

    @Override
    protected void onResume() {
        // 初始化或重启RxBus注册
        registerRxBusEvent();

        HljUpdate.getInstance()
                .update(this);
        super.onResume();
        if (cityUtil != null) {
            cityUtil.startLocation();
        }
        final User user = Session.getInstance()
                .getCurrentUser(this);
        if (user == null || user.getId() == 0) {
            PushSocket.INSTANCE.onEnd();
            logout();
            CommonUtil.unSubscribeSubs(hxLoginSubscription);
            HljKeFu.logout(this);
        } else {
            onShowHljPush();

            hxLoginSubscription = HljKeFu.loginObb(this)
                    .subscribe(new EmptySubscriber<Boolean>());
            //猜你喜欢
            PointUtil.getInstance()
                    .syncPointRecord(this, user.getId(), null);
            int count = NotificationUtil.getChatNewsCount(MainActivity.this);
            if (count == 0 || tabHost.getCurrentTab() == 2) {
                msgHint.setVisibility(View.GONE);
            } else {
                msgHint.setVisibility(View.VISIBLE);
                msgHint.setText(getString(R.string.hint_unread_msg, String.valueOf(count)));
            }
            setCommunityNews();

            //收到请帖未读信息数
            long cardNewsCount = realm.where(Notification.class)
                    .equalTo("userId", user.getId())
                    .notEqualTo("status", 2)
                    .beginGroup()
                    .equalTo("notifyType", Notification.NotificationType.GIFT)
                    .or()
                    .equalTo("notifyType", Notification.NotificationType.SIGN)
                    .endGroup()
                    .count();
            if (cardNewsIcon != null) {
                cardNewsIcon.setVisibility(cardNewsCount > 0 ? View.VISIBLE : View.GONE);
            }

            WebSocket.getInstance()
                    .socketConnect(this);
            NotificationUtil.getInstance(MainActivity.this)
                    .getNewNotifications(user.getId());
        }
        onUserCityChange(Session.getInstance()
                .getMyCity(this));
        if (!Util.loginChecked(this)) {
            if (!isCloseNotice) {
                mainNoticeLayout.setVisibility(View.VISIBLE);
            }
        } else {
            mainNoticeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化或重启RxBus注册
     */
    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            User user = Session.getInstance()
                                    .getCurrentUser(MainActivity.this);
                            switch (rxEvent.getType()) {
                                case EM_MESSAGE:
                                case WS_MESSAGE:
                                    // 收到新消息，更新视图
                                    if (user != null && user.getId() > 0) {
                                        handler.post(runnableUi);
                                    }
                                    break;
                                case PARTNER_INVITATION:
                                    // 收到结伴邀请通知，处理邀请弹窗
                                    partnerInviteNoti = (Notification) rxEvent.getObject();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showPartnerInviteDlg();
                                        }
                                    });
                                    break;
                                case NEW_CARD_NOTICE:
                                    //收到请帖礼物通知
                                    if (cardNewsIcon != null) {
                                        cardNewsIcon.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case HLJ_NOTIFY_PUSH:
                                case HLJ_ACTIVITY_PUSH:
                                    if (user != null && user.getId() > 0) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onShowHljPush();
                                            }
                                        });
                                    }
                                    break;
                                case HLJ_LIVE_PUSH:
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onShowHljPush();
                                        }
                                    });
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 初始化token异常rxBus注册
     */
    private void registerTokenErrorBusEvent() {
        if (tokenErrorSubscription == null || tokenErrorSubscription.isUnsubscribed()) {
            tokenErrorSubscription = RxBus.getDefault()
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
                                    Intent intent = new Intent(MainActivity.this,
                                            LoginActivity.class);
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

    /**
     * 初始化服务异常rxBus注册
     */
    private void registerServiceErrorBusEvent() {
        if (serviceErrorSubscription == null || serviceErrorSubscription.isUnsubscribed()) {
            serviceErrorSubscription = RxBus.getDefault()
                    .toObservable(MaintainEvent.class)
                    .filter(new Func1<MaintainEvent, Boolean>() {
                        @Override
                        public Boolean call(MaintainEvent rxEvent) {
                            return rxEvent.getType() == MaintainEvent.EventType.SERVICE_ERROR;
                        }
                    })
                    .distinctUntilChanged(new Func2<MaintainEvent, MaintainEvent, Boolean>() {
                        @Override
                        public Boolean call(
                                MaintainEvent maintainEvent, MaintainEvent maintainEvent2) {
                            return maintainEvent2.getMillis() - maintainEvent.getMillis() > 10000;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<MaintainEvent>() {
                        @Override
                        protected void onEvent(MaintainEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case MaintainEvent.EventType.SERVICE_ERROR:
                                    checkServiceIsMaintain();
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 检测系统是否在维护中
     */
    private void checkServiceIsMaintain() {
        if (checkServiceSubscriber == null || checkServiceSubscriber.isUnsubscribed()) {
            checkServiceSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            try {
                                boolean isMaintaining = CommonUtil.getAsInt(jsonElement,
                                        "is_maintaining") == 1;
                                String message = CommonUtil.getAsString(jsonElement, "message");
                                if (isMaintaining) {
                                    Intent intent = new Intent(MainActivity.this,
                                            ServiceMaintainActivity.class);
                                    intent.putExtra(ServiceMaintainActivity.ARG_MESSAGE, message);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    })
                    .build();
            CommonApi.getServiceState()
                    .subscribe(checkServiceSubscriber);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cityUtil != null) {
            cityUtil.stopLocation();
        }
        CommonUtil.unSubscribeSubs(rxBusSubscription, notifyTimerSubscription);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra(SystemNotificationUtil.ASG_NOTIFY_ID)) {
            int notifyId = intent.getIntExtra(SystemNotificationUtil.ASG_NOTIFY_ID, 0);
            SystemNotificationUtil.INSTANCE.readNotification(this, notifyId);
        }

        Uri uri = intent.getData();
        if (uri != null && "hunliji".equals(uri.getScheme()) && "poster_jumper".equals(uri
                .getHost())) {
            //链接唤起应用
            Poster poster = new Poster();
            try {
                poster.setTargetType(Integer.valueOf(uri.getQueryParameter("target_type")));
                try {
                    poster.setTargetId(Long.valueOf(uri.getQueryParameter("target_id")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                poster.setUrl(uri.getQueryParameter("target_url"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BannerUtil.bannerAction(MainActivity.this, poster, city, true, null);
            }
            return;
        }
        String json = intent.getStringExtra(SystemNotificationUtil.ASG_MSG);
        String taskId = intent.getStringExtra(SystemNotificationUtil.ASG_TASK_ID);
        String messageId = intent.getStringExtra(SystemNotificationUtil.ASG_MESSAGE_ID);
        if (!JSONUtil.isEmpty(taskId)) {
            GetuiIntentService.readNotify(taskId);
        }
        if (!JSONUtil.isEmpty(taskId) && !JSONUtil.isEmpty(messageId)) {
            //推送消息回馈
            PushManager.getInstance()
                    .sendFeedbackMessage(this, taskId, messageId, 90002);
        }
        if (!JSONUtil.isEmpty(json)) {
            try {
                JSONObject object = new JSONObject(json);
                int type = object.optInt("type", 0);
                String action = JSONUtil.getString(object, "action");
                int id = object.optInt("id", 0);
                if (type == 1) {
                    onTabChanged(action, id);
                } else if (type == 4) {
                    int property = object.optInt("property", 0);
                    long forwardId = object.optLong("forwardId", 0);
                    String webUrl = JSONUtil.getString(object, "path");
                    City city = Session.getInstance()
                            .getMyCity(this);
                    BannerUtil.bannerAction(this, property, forwardId, webUrl, city, true);
                } else if (type == 3) {
                    String path = JSONUtil.getString(object, "path");
                    City city = Session.getInstance()
                            .getMyCity(this);
                    if (!JSONUtil.isEmpty(path) && city != null && city.getId()
                            .intValue() != 0) {
                        path += (path.contains("?") ? "&" : "?") + "city=" + city.getId();
                    }
                    if (!JSONUtil.isEmpty(path)) {
                        intent = new Intent(this, HljWebViewActivity.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String action = intent.getStringExtra("action");
            if (!JSONUtil.isEmpty(action)) {
                switch (action) {
                    case "exit":
                        finish();
                        return;
                    case "productChannel":
                        setTabSelect(0);
                        return;
                    case "myOrders":
                    case "myPolicies":
                    case MAIN_ACTION_SIGN_IN:
                        setTabSelect(4);
                        return;
                    case "groupInfo":
                        setTabSelect(2);
                        return;
                    case MAIN_ACTION_PRODUCT:
                        setTabSelect(3);
                        break;
                    case MAIN_ACTION_LIVE:
                        if (tabHost.getCurrentTab() != 2) {
                            socialTabPage = SocialHomeFragment.TAB_INDEX_LIVE;
                            setTabSelect(2);
                        } else {
                            FragmentManager fm = getSupportFragmentManager();
                            SocialHomeFragment socialHomeFragment = (SocialHomeFragment) fm
                                    .findFragmentByTag(
                                    FRAGMENT_TAG_3);
                            if (socialHomeFragment != null) {
                                socialHomeFragment.setTabPage(SocialHomeFragment.TAB_INDEX_LIVE);
                            }
                        }
                        return;
                    case MAIN_ACTION_COMMUNITY:
                        if (tabHost.getCurrentTab() != 2) {
                            socialTabPage = SocialHomeFragment.TAB_INDEX_SOCIAL_HOT;
                            setTabSelect(2);
                        } else {
                            FragmentManager fm = getSupportFragmentManager();
                            SocialHomeFragment socialHomeFragment = (SocialHomeFragment) fm
                                    .findFragmentByTag(
                                    FRAGMENT_TAG_3);
                            if (socialHomeFragment != null) {
                                socialHomeFragment.setTabPage(SocialHomeFragment
                                        .TAB_INDEX_SOCIAL_HOT);
                            }
                        }
                        return;
                    default:
                        onTabChanged(action, 0);
                        break;
                }
            }
            int index = intent.getIntExtra(SystemNotificationUtil.ASG_INDEX, 0);
            if (index != 0) {
                User user = Session.getInstance()
                        .getCurrentUser(this);
                if (user == null || user.getId()
                        .intValue() == 0) {
                    return;
                }
                switch (index) {
                    case SystemNotificationUtil.WS_CHAT_INDEX:
                        Intent newIntent = new Intent(this, MessageHomeActivity.class);
                        startActivity(newIntent);
                        break;
                    case 3:
                    case 7:
                    case 6:
                        newIntent = new Intent(this, MessageHomeActivity.class);
                        startActivity(newIntent);
                        break;
                }
            }
        }

        super.onNewIntent(intent);
    }

    /**
     * 新消息提醒视图的点击事件响应方法
     *
     * @param view
     */
    public void onNewMsg(View view) {
        Intent newIntent = new Intent(this, MessageHomeActivity.class);
        newIntent.putExtra("type", 2);
        startActivity(newIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    public void setTabSelect(final int position) {
        if (position == 4) {
            if (Util.loginBindChecked(MainActivity.this, Constants.Login.MAIN_LOGIN)) {
                tabHost.setCurrentTab(position);
            }
            return;
        }
        tabHost.setCurrentTab(position);
    }

    /**
     * 跳转工具页面，可能是fragment切换，也可能是页面跳转
     */
    public void goToToolsPage() {
        Intent newIntent = new Intent(this, ToolsHomeActivity.class);
        startActivity(newIntent);
    }

    public int getCurrentTab() {
        return tabHost.getCurrentTab();
    }

    /**
     * 跳转婚礼购频道页面，可能是fragment切换,也可能是页面跳转
     */
    public void goToProductChannelPage() {
        tabHost.setCurrentTab(3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.MAIN_LOGIN:
                    setTabSelect(4);
                    break;
                case NOTICE_LOGIN:
                    if (mainNoticeLayout != null) {
                        mainNoticeLayout.setVisibility(View.GONE);
                    }
                    break;
                case Constants.RequestCode.USER_PREPARE_CATEGORY_LIST:
                    setTabSelect(1);
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 注销处理
     */
    private void logout() {
        msgHint.setVisibility(View.GONE);
        socialNewsCount.setVisibility(View.GONE);
        userNewsIcon.setVisibility(View.GONE);
        NotificationUtil.getInstance(this)
                .logout();
        partnerInviteNoti = null;
        if (lastPosition == 4) {
            setTabSelect(0);
        }
        //清除购物车数量
        Session.getInstance()
                .clearCartCount(this);
    }

    /**
     * 判断是否需要显示结伴邀请弹窗，需要的话则弹窗
     */
    private void showPartnerInviteDlg() {
        // 只有在"我的"页面并且收到邀请信息才需要显示弹窗
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (tabHost.getCurrentTab() == 4 && partnerInviteNoti != null && user != null && user
                .getPartnerUid() <= 0) {
            final Notification notification = partnerInviteNoti;
            partnerInviteDlg = DialogUtil.createPartnerInvitationDlg(partnerInviteDlg,
                    this,
                    notification,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptPartnerInvitation(notification.getEntityId());
                        }
                    },
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 关闭弹窗，通知我的页面，结伴邀请弹窗已关闭，可以显示hint view了
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType
                                            .PARTNER_INVITATION_DLG_CLOSED,
                                            null));
                        }
                    });
            partnerInviteDlg.addDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // 关闭弹窗，通知我的页面，结伴邀请弹窗已关闭，可以显示hint view了
                    RxBus.getDefault()
                            .post(new RxEvent(RxEvent.RxEventType.PARTNER_INVITATION_DLG_CLOSED,
                                    null));
                }
            });
            partnerInviteDlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    // 显示一次就不在显示
                    partnerInviteNoti = null;
                    // 告知"我的"页面当前正在显示邀请弹窗
                    RxBus.getDefault()
                            .post(new RxEvent(RxEvent.RxEventType.PARTNER_INVITATION_DLG_SHOWED,
                                    null));
                }
            });
            showDialog(partnerInviteDlg);
        }
    }

    /**
     * 提交接受邀请
     */
    private void acceptPartnerInvitation(long partnerId) {
        Observable observable = PartnerApi.getPostAgreeBindPartnerObb(partnerId);
        acceptInviteSub = HljHttpSubscriber.buildSubscriber(this)
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 成功同意了邀请
                        // 更新用户信息
                        new UserTask(MainActivity.this, null).execute();

                        ToastUtil.showToast(MainActivity.this, "成功绑定伴侣", 0);
                        Intent intent = new Intent(MainActivity.this, BindingPartnerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, 0);
                    }
                })
                .build();

        observable.subscribe(acceptInviteSub);
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        PushSocket.INSTANCE.onEnd();
        realm.close();
        CommonUtil.unSubscribeSubs(acceptInviteSub,
                rxBusSubscription,
                praiseDialogSubscriber,
                hxLoginSubscription,
                serviceErrorSubscription,
                tokenErrorSubscription,
                acmSubscriptions);
        if (WebSocket.getInstance() != null) {
            WebSocket.getInstance()
                    .disconnect(this);
        }
    }

    private class GetPopupPosterTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_POPUP_POSTER_URL, city.getId());
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                JSONObject popObject = jsonObject.optJSONObject("popinfo");
                if (popObject != null) {
                    popupPoster = new Poster();
                    popupPoster.setId(popObject.optLong("posterid"));

                    JSONObject posterObj = popObject.optJSONObject("poster");
                    if (posterObj != null) {
                        popupPoster.setPath(JSONUtil.getString(posterObj, "image_path"));
                        popupPoster.setTitle(JSONUtil.getString(posterObj, "title"));
                        popupPoster.setTargetId(posterObj.optLong("target_id", 0));
                        popupPoster.setTargetType(posterObj.optInt("target_type", 0));
                        popupPoster.setUrl(JSONUtil.getString(posterObj, "target_url"));
                    }
                }
            }
            long lastPopupId = getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE).getLong("popup_poster_id", -1);
            if (popupPoster != null && !popupPoster.getId()
                    .equals(lastPopupId)) {
                // 显示运营弹窗
                popUpDialog = new QueueDialog(MainActivity.this, R.style.BubbleDialogTheme);
                View view = getLayoutInflater().inflate(R.layout.dialog_popup_poster, null);
                ImageView imageView = view.findViewById(R.id.img_poster);
                Button closeBtn = view.findViewById(R.id.btn_close);

                Point point = JSONUtil.getDeviceSize(MainActivity.this);
                int windowWidth = Math.round(point.x * 460 / 640);
                int windowHeight = Math.round(windowWidth * 570 / 460);

                String url = JSONUtil.getImagePath(popupPoster.getPath(), windowWidth);
                if (!JSONUtil.isEmpty(url)) {
                    imageView.setTag(url);
                    ImageLoadTask task = new ImageLoadTask(imageView, null, 0);
                    AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                            R.mipmap.icon_empty_image,
                            task);
                    task.loadImage(url, windowWidth, ScaleMode.WIDTH, image);
                } else {
                    imageView.setImageBitmap(null);
                }
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 存储这次显示的这个popup poster
                        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                                Context.MODE_PRIVATE);
                        preferences.edit()
                                .putLong("popup_poster_id", popupPoster.getId())
                                .apply();
                        popUpDialog.cancel();
                        BannerUtil.bannerAction(MainActivity.this,
                                popupPoster.getTargetType(),
                                popupPoster.getTargetId(),
                                popupPoster.getUrl(),
                                city,
                                false);
                    }
                });
                closeBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpDialog.cancel();
                    }
                });

                popUpDialog.setContentView(view);
                Window window = popUpDialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = windowWidth;
                params.height = windowHeight;
                window.setAttributes(params);
                showDialog(popUpDialog);
            }

            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 主页上要显示dialog都调用这个，是用FIFO队列对dialog进行排队，取消前一个后再显示下一个
     *
     * @param dialog
     */
    private void showDialog(QueueDialog dialog) {
        if (dialog != null) {
            dialogQueue.offer(dialog);
        }

        if (currentDialog == null) {
            currentDialog = dialogQueue.poll();
            if (currentDialog != null) {
                currentDialog.show();
                currentDialog.addDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        currentDialog = null;
                        showDialog(null);
                    }
                });
            }
        }
    }

    public void onUserCityChange(City c) {
        if (c == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        onCityChange(c, false);
    }

    public void onCityChange(City c, boolean isRelative) {
        if (c == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        TrackerHelper.changeCity(this);
        city = c;
        if (isRelative) {
            setTabSelect(0);
        } else if (c.getId() > 0) {
            new RelativeCitiesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    Constants.getAbsUrl(String.format(Constants.HttpPath.RELATIVE_CITY_URL,
                            c.getId())));
        }
        FragmentManager fm = getSupportFragmentManager();
        HomePageFragment homePageFragment = (HomePageFragment) fm.findFragmentByTag(FRAGMENT_TAG_1);
        FindMerchantHomeFragment finderFragment = (FindMerchantHomeFragment) fm.findFragmentByTag(
                FRAGMENT_TAG_2);
        SocialHomeFragment socialHomeFragment = (SocialHomeFragment) fm.findFragmentByTag(
                FRAGMENT_TAG_3);
        ProductHomeFragment productHomeFragment = (ProductHomeFragment) fm.findFragmentByTag(
                FRAGMENT_TAG_4);
        if (homePageFragment != null && !homePageFragment.isHidden()) {
            homePageFragment.cityRefresh(c);
        }
        if (finderFragment != null && !finderFragment.isHidden()) {
            finderFragment.cityRefresh(c);
        }
        if (socialHomeFragment != null && !socialHomeFragment.isHidden()) {
            socialHomeFragment.cityRefresh(c);
        }
        if (productHomeFragment != null && !productHomeFragment.isHidden()) {
            productHomeFragment.cityRefresh(c);
        }
    }

    private class RelativeCitiesTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isFinishing()) {
                return;
            }
            if (url.equals(Constants.getAbsUrl(String.format(Constants.HttpPath.RELATIVE_CITY_URL,
                    Session.getInstance()
                            .getMyCity(MainActivity.this)
                            .getId())))) {
                if (jsonObject != null) {
                    boolean hasMerchant = jsonObject.optBoolean("has_merchant");
                    if (!hasMerchant) {
                        JSONArray array = jsonObject.optJSONArray("relative_cities");
                        if (array != null && array.length() > 0) {
                            ArrayList<RelativeCity> cities = new ArrayList<>();
                            int size = array.length();
                            for (int i = 0; i < size; i++) {
                                RelativeCity city = new RelativeCity(array.optJSONObject(i));
                                if (!city.getWorks()
                                        .isEmpty()) {
                                    cities.add(city);
                                }
                            }
                            if (!cities.isEmpty()) {
                                DialogFragment dialogFragment = new RelativeCityFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("cities", cities);
                                dialogFragment.setArguments(bundle);
                                FragmentTransaction ft = getSupportFragmentManager()
                                        .beginTransaction();
                                ft.add(dialogFragment, "relativeCityFragment");
                                ft.commitAllowingStateLoss();
                            }
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void checkingPermissions() {
        notiOpenDlg = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                Constants.PREF_NOTIFICATION_OPEN_DLG,
                "开启消息通知",
                "优惠活动，商家消息\n请帖宾客回复，一个都不错过",
                0);
        if (notiOpenDlg != null) {
            showDialog(notiOpenDlg);
        }
        MainActivityPermissionsDispatcher.requestPermissionWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest
            .permission.ACCESS_FINE_LOCATION})
    void requestPermission() {
        CustomCommonApi.sendAppAnalytics(this)
                .subscribe(new EmptySubscriber<JsonElement>());
        PushManager.getInstance()
                .initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance()
                .registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
        if (cityUtil != null) {
            cityUtil.location();
        }
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission
            .WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest
            .permission.ACCESS_FINE_LOCATION})
    void onDeniedForPermission() {
        CustomCommonApi.sendAppAnalytics(this)
                .subscribe(new EmptySubscriber<JsonElement>());
        PushManager.getInstance()
                .initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance()
                .registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);
        if (cityUtil != null) {
            cityUtil.location();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
    }

    private void setMyCity(final City result) {
        if (isFinishing() || (cityDialog != null && cityDialog.isShowing())) {
            return;
        }
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("firstCityChange", true);
        if (!isFirst) {
            cityDialog = DialogUtil.createLocationDialog(MainActivity
                            .this,
                    getString(R.string.hint_change_location, result.getName()),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Session.getInstance()
                                        .setMyCity(MainActivity
                                                .this, result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            onCityChange(result, false);
                        }
                    });
            showDialog(cityDialog);
        } else {
            getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit()
                    .putBoolean("firstCityChange", false)
                    .apply();
            try {
                Session.getInstance()
                        .setMyCity(MainActivity
                                .this, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onCityChange(result, false);
        }
    }

    public void hideUserNewsIcon() {
        userNewsIcon.setVisibility(View.GONE);
        UserPrefUtil.getInstance(this)
                .setUserNewsIconTimestamp(HljTimeUtils.getServerCurrentTimeMillis());
    }

    public void setCommunityNews() {
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_YEAR);
        int historyDay = SPUtils.getInt(this, Constants.PREF_COMMUNITY_DAY, 0);
        if (currentDay != historyDay) {
            communityNews.setVisibility(View.VISIBLE);
        } else {
            if (!OncePrefUtil.hasDoneThis(this,
                    HljCommon.SharedPreferencesNames.COMMUNITY_CHANNEL_NEW)) {
                communityNews.setVisibility(View.VISIBLE);
            } else {
                communityNews.setVisibility(View.GONE);
            }
        }
        User user = Session.getInstance()
                .getCurrentUser();
        //社区新通知数
        long msgCount = realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .notEqualTo("status", 2)
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .beginGroup()
                .equalTo("action", "post_reply")
                .or()
                .equalTo("action", "thread_reply")
                .or()
                .equalTo("action", "qa_answer_comment")
                .or()
                .equalTo("action", "qa_answer_comment_reply")
                .or()
                .equalTo("action", "qa_answer")
                .or()
                .equalTo("action", "post_praise")
                .or()
                .equalTo("action", "qa_answer_praise")
                .or()
                .equalTo("action", "plus1")
                .or()
                .equalTo("action", "qa_praise")
                .endGroup()
                .count();
        if (msgCount == 0 || tabHost.getCurrentTab() == 1) {
            socialNewsCount.setVisibility(View.GONE);
        } else {
            socialNewsCount.setVisibility(View.VISIBLE);
            communityNews.setVisibility(View.GONE);
            socialNewsCount.setText(msgCount > 99 ? "99+" : String.valueOf(msgCount));
        }
    }

}
