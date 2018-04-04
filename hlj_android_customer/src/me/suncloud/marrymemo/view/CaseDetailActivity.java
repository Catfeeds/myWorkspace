package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.hunliji.hljchatlibrary.utils.ChatUtil;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantChatData;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.HLJCustomerApplication;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.api.caseapi.CaseApi;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.Article;
import me.suncloud.marrymemo.model.Item;
import me.suncloud.marrymemo.model.NewMerchant;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.ShareInfo;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.merchant.wrappers.MerchantChatLinkTriggerPostBody;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.task.HttpDeleteTask;
import me.suncloud.marrymemo.task.HttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.ShareUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.merchant.AppointmentUtil;
import me.suncloud.marrymemo.util.merchant.ChatBubbleTimer;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.ParallaxScrollListView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

public class CaseDetailActivity extends HljBaseNoBarActivity implements ObjectBindAdapter
        .ViewBinder<Item>, View.OnClickListener, AdapterView.OnItemClickListener, AbsListView
        .OnScrollListener {
    public final static String ARG_ID = "id";
    @BindView(R.id.list)
    ParallaxScrollListView listView;
    @BindView(R.id.iv_collect)
    ImageView imgCollect;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.shadow_view)
    ImageView shadowView;
    @BindView(R.id.action_layout)
    View actionLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_solo_out)
    TextView tvSoldOut;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R.id.tv_bubble_msg)
    TextView tvBubbleMsg;
    @BindView(R.id.chat_bubble_layout)
    LinearLayout chatBubbleLayout;
    @BindView(R.id.action_holder_layout)
    View actionHolderLayout;
    @BindView(R.id.notice_layout)
    View noticeLayout;
    @BindView(R.id.call_layout)
    LinearLayout callLayout;
    private ArrayList<Item> items;
    private View headerView;
    private View footerView;
    private ObjectBindAdapter<Item> adapter;
    private Work work;
    private int width;
    private int imageWidth;
    private Dialog contactDialog;
    private Dialog shareDialog;
    private ShareUtil shareUtil;
    private int showHeight;

    private NoticeUtil noticeUtil;

    public static final int SCROLL_Y_DELTA = 1300; // 页面滑动距离触发轻松聊
    public static final int TIME_STAYED_IN_PAGE = MerchantChatLinkTriggerPostBody
            .TIME_TILL_SHOWING * 1000;
    public static final int TIME_HIDE_AFTER = MerchantChatLinkTriggerPostBody.TIME_TILL_HIDE * 1000;
    // 页面停留时间触发轻松聊
    private ChatBubbleTimer bubbleTimer;
    private HljHttpSubscriber chatDataSub;
    private HljHttpSubscriber chatTrigSub;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    TrackerHelper.postShareAction(CaseDetailActivity.this,
                            work.getId(),
                            "set_meal");
                    new HljTracker.Builder(CaseDetailActivity.this).eventableId(work.getId())
                            .eventableType("Example")
                            .action("example_detail")
                            .action("share")
                            .additional(HljShare.getShareTypeName(msg.what))
                            .build()
                            .add();
                    break;
            }
            return false;
        }
    });
    private Point point;
    private DisplayMetrics dm;
    private int coverHeight;
    private Subscription appointmentSub;
    private HljHttpSubscriber loadSub;
    private HljHttpSubscriber recommendCaseSub;
    private long id;
    private RecommendWorkAdapter recommendWorkAdapter;

    @Override
    public String pageTrackTagName() {
        return "案例详情页";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(id, "Example");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
        setActionBarPadding(actionLayout, actionHolderLayout, noticeLayout);
    }

    private void initValues() {
        point = JSONUtil.getDeviceSize(this);
        dm = getResources().getDisplayMetrics();
        imageWidth = width = point.x;
        if (width > 805) {
            imageWidth = Math.round(width * 3 / 4);
        }
        coverHeight = Math.round(point.x * 5 / 8);
        showHeight = Math.round(coverHeight - 45 * dm.density - getStatusBarHeight());

        items = new ArrayList<>();

        // 启动轻松聊计时器
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
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

    private void initViews() {
        headerView = View.inflate(CaseDetailActivity.this, R.layout.case_detail_header, null);
        footerView = View.inflate(CaseDetailActivity.this, R.layout.case_detail_footer, null);

        View view = headerView.findViewById(R.id.cover_layout);
        view.getLayoutParams().height = coverHeight;

        adapter = new ObjectBindAdapter<>(this, items, R.layout.work_image_text_item, this);
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);
        listView.setParallaxImageView(view, coverHeight);
        listView.setOnScrollListener(this);
        listView.setViewsBounds(2);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        String ads = getIntent().getStringExtra("ads");
        JSONObject siteJson = null;
        String site = getIntent().getStringExtra("site");
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

        progressBar.setVisibility(View.VISIBLE);
        id = getIntent().getLongExtra(ARG_ID, 0);
        new HljTracker.Builder(this).eventableId(id)
                .eventableType("Example")
                .screen("package_detail")
                .action("hit")
                .additional(ads)
                .site(siteJson)
                .build()
                .send();
        initTracker(id);
    }


    private void initLoad() {
        CommonUtil.unSubscribeSubs(loadSub);
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setContentView(listView)
                .setOnNextListener(new SubscriberOnNextListener<CaseDetailZip>() {
                    @Override
                    public void onNext(CaseDetailZip caseDetailZip) {
                        setCaseDetailZip(caseDetailZip);
                    }
                })
                .build();

        Observable<JsonElement> caseDetailObb = CaseApi.getCaseDetail(id);
        Observable<HljHttpData<List<com.hunliji.hljcommonlibrary.models.Work>>> recommendWorkObb
                = CaseApi.getRecommendWork(
                id);
        Observable.zip(caseDetailObb,
                recommendWorkObb,
                new Func2<JsonElement, HljHttpData<List<com.hunliji.hljcommonlibrary.models
                        .Work>>, Object>() {
                    @Override
                    public Object call(
                            JsonElement jsonElement,
                            HljHttpData<List<com.hunliji.hljcommonlibrary.models.Work>>
                                    recommendWork) {
                        CaseDetailZip caseDetailZip = new CaseDetailZip();
                        caseDetailZip.caseDetail = jsonElement;
                        caseDetailZip.recommendWork = recommendWork;
                        return caseDetailZip;
                    }
                })
                .subscribe(loadSub);
    }


    private void setCaseDetailZip(CaseDetailZip caseDetailZip) {
        setCaseDetail(caseDetailZip.caseDetail);
        setRecommendWork(caseDetailZip.recommendWork == null ? null : caseDetailZip.recommendWork
                .getData());
    }

    private void setCaseDetail(JsonElement jsonElement) {
        if (jsonElement == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonElement.toString());
            String emptyStr = null;
            if (jsonObject != null) {
                ReturnStatus status = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (status.getRetCode() == 404 && !JSONUtil.isEmpty(status.getErrorMsg())) {
                    emptyStr = status.getErrorMsg();
                }

                if (jsonObject.optJSONObject("data") != null) {
                    initWorkJson(jsonObject.optJSONObject("data")
                            .optJSONObject("work"));
                }
            }
            if (work == null || work.getId() == 0) {
                View empty = listView.getEmptyView();
                if (empty == null) {
                    empty = findViewById(R.id.empty_hint_layout);
                    listView.setEmptyView(empty);
                }
                Util.setEmptyView(CaseDetailActivity.this,
                        empty,
                        R.string.no_item,
                        R.drawable.icon_common_empty,
                        0,
                        0,
                        emptyStr);
            } else {
                shadowView.setVisibility(View.GONE);
                findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecommendWork(List<com.hunliji.hljcommonlibrary.models.Work> recommendWork) {
        if (footerView == null) {
            return;
        }

        footerView.findViewById(R.id.layout_related_work)
                .setVisibility(CommonUtil.getCollectionSize(recommendWork) <= 0 ? View.GONE :
                        View.VISIBLE);
        ViewPager viewPager = footerView.findViewById(R.id.view_pager);
        CirclePageExIndicator indicator = footerView.findViewById(R.id.flow_indicator);
        if (recommendWorkAdapter == null) {
            recommendWorkAdapter = new RecommendWorkAdapter(this, recommendWork);
            viewPager.setAdapter(recommendWorkAdapter);
            indicator.setViewPager(viewPager);
        } else {
            recommendWorkAdapter.notifyDataSetChanged();
        }
        indicator.setVisibility(CommonUtil.getCollectionSize(recommendWork) >= 2 ? View.VISIBLE :
                View.GONE);
    }

    private void loadRecommendCase() {
        if (work == null || work.getId() <= 0 || work.getMerchantId() <= 0) {
            return;
        }
        CommonUtil.unSubscribeSubs(recommendCaseSub);
        recommendCaseSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setRecommendCase(jsonElement);
                    }
                })
                .build();
        CaseApi.getRecommendCase(work.getMerchantId(), "case", "like", 1, 5)
                .subscribe(recommendCaseSub);
    }


    private void setRecommendCase(JsonElement jsonElement) {
        if (jsonElement == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonElement.toString()).optJSONObject("data");
            ArrayList<Work> cases = new ArrayList<>();
            if (jsonObject != null) {
                JSONArray array = jsonObject.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        Work c = new Work(array.optJSONObject(i));
                        if (!c.getId()
                                .equals(work.getId())) {
                            cases.add(c);
                        }
                    }
                }
            }
            initOtherCases(cases);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initTracker(long id) {
        HljVTTagger.buildTagger(chatBubbleLayout)
                .tagName("free_chat_bubble")
                .dataId(id)
                .dataType("Example")
                .tag();
    }

    private void showChatBubble(boolean isShow) {
        if (!isShow) {
            chatBubbleLayout.setVisibility(View.GONE);
            return;
        }
        // 如果用户在今天之内联系过商家，则不再显示气泡
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && ChatUtil.hasChatWithMerchant(user.getId(),
                work.getMerchant()
                        .getUserId())) {
            return;
        }
        // 触发轻松聊事件，触发接口和加载内容
        chatDataSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<MerchantChatData>() {
                    @Override
                    public void onNext(MerchantChatData merchantChatData) {
                        if (!TextUtils.isEmpty(merchantChatData.getExampleSpeech())) {
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
                            Glide.with(CaseDetailActivity.this)
                                    .load(ImagePath.buildPath(work.getMerchant()
                                            .getLogoPath())
                                            .width(CommonUtil.dp2px(CaseDetailActivity.this, 38))
                                            .height(CommonUtil.dp2px(CaseDetailActivity.this, 38))
                                            .cropPath())
                                    .into(imgLogo);
                            tvBubbleMsg.setText(merchantChatData.getExampleSpeech());
                        }
                    }
                })
                .build();
        MerchantApi.getMerchantChatData(work.getMerchant()
                .getId())
                .subscribe(chatDataSub);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        Item item = (Item) parent.getAdapter()
                .getItem(position);
        if (item != null) {
            Intent intent = new Intent(this, ItemPageViewActivity.class);
            intent.putExtra("items", items);
            intent.putExtra("position", items.indexOf(item));
            ShareInfo shareInfo = work.getShareInfo(this);
            intent.putExtra("title", shareInfo.getTitle());
            intent.putExtra("description", shareInfo.getDesc());
            intent.putExtra("weiboDescription", shareInfo.getDesc2());
            intent.putExtra("url", shareInfo.getUrl());
            startActivity(intent);
        }
    }

    public void onShare(View view) {
        if (work == null) {
            return;
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            shareUtil = new ShareUtil(this, work.getShareInfo(this), progressBar, handler);
        }
        if (shareDialog == null) {
            shareDialog = Util.initShareDialog(this, shareUtil, null);
        }
        shareDialog.show();
    }

    @Override
    public void setViewValue(View view, final Item item, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (!JSONUtil.isEmpty(item.getDescription())) {
            holder.describe.setVisibility(View.VISIBLE);
            holder.describe.setText(item.getDescription());
        } else {
            holder.describe.setVisibility(View.GONE);
        }

        String url;
        if (item.getKind() == 2) {
            if (item.getPersistent() != null && !JSONUtil.isEmpty(item.getPersistent()
                    .getScreenShot())) {
                url = JSONUtil.getImagePath(item.getPersistent()
                        .getScreenShot(), imageWidth);
            } else {
                url = item.getMediaPath() + String.format(Constants.VIDEO_URL_TEN, imageWidth)
                        .replace("|", JSONUtil.getURLEncoder());
            }
            holder.play.setVisibility(View.VISIBLE);
        } else {
            holder.play.setVisibility(View.GONE);
            url = JSONUtil.getImagePath(item.getMediaPath(), imageWidth);
        }

        if (!JSONUtil.isEmpty(url)) {
            int h = 0;
            if (item.getHight() != 0 && item.getWidth() != 0) {
                h = Math.round(width * item.getHight() / item.getWidth());
                holder.imageView.getLayoutParams().height = h;
            }
            ImageLoadUtil.loadImageResetH(this, url, holder.imageView, width, h);
        } else {
            holder.itemsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_layout:
                onMerchant(null);
                break;
        }
    }

    public void onContact(View view) {
        if (work == null || work.getMerchant() == null) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.CONTACT_LOGIN)) {
            NewMerchant merchant = work.getMerchant();
            if (merchant != null && merchant.getUserId() != 0) {
                Intent intent = new Intent(this, WSCustomerChatActivity.class);
                MerchantUser user = new MerchantUser();
                user.setNick(merchant.getName());
                user.setId(merchant.getUserId());
                user.setAvatar(merchant.getLogoPath());
                user.setMerchantId(merchant.getId());
                intent.putExtra("user", user);
                intent.putExtra("ws_track", ModuleUtils.getWSTrack(work));
                if (merchant.getContactPhone() != null && !merchant.getContactPhone()
                        .isEmpty()) {
                    intent.putStringArrayListExtra("contact_phones", merchant.getContactPhone());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    @OnClick(R.id.chat_click_layout)
    void onChatBubbleClick() {
        chatBubbleLayout.setVisibility(View.GONE);
        bubbleTimer.cancel();
        onContact(null);
        chatTrigSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                    }
                })
                .build();
        MerchantApi.postMerchantChatLinkTrigger(work.getMerchant()
                .getId(), MerchantChatLinkTriggerPostBody.MerchantChatLinkType.TYPE_EXAMPLE, null)
                .subscribe(chatTrigSub);
    }

    public void onMessage(View view) {
        if (Util.loginBindChecked(this, Constants.Login.MSG_LOGIN)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public void onMerchant(View view) {
        if (work != null && work.getMerchantId() > 0) {
            Intent intent = new Intent(this, MerchantDetailActivity.class);
            intent.putExtra("id", work.getMerchantId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    public void onReservation(View view) {
        if (work == null || work.getMerchant() == null) {
            return;
        }
        if (Util.loginBindChecked(this, Constants.Login.SUBMIT_LOGIN)) {
            CommonUtil.unSubscribeSubs(appointmentSub);
            appointmentSub = AppointmentUtil.makeAppointment(this,
                    work.getMerchant()
                            .getId(),
                    work.getMerchant()
                            .getUserId(),
                    AppointmentUtil.WORKCASE,
                    null);
        }
    }

    public void onPhoneContact(View v) {
        if (work == null || work.getMerchant() == null) {
            return;
        }
        final NewMerchant merchant = work.getMerchant();
        ArrayList<String> contactPhones = merchant.getContactPhone();
        if (contactPhones == null || contactPhones.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_merchant_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (contactPhones.size() == 1) {
            String phone = contactPhones.get(0);
            if (!JSONUtil.isEmpty(phone) && phone.trim()
                    .length() != 0) {
                new HljTracker.Builder(this).eventableId(merchant.getUserId())
                        .eventableType("Merchant")
                        .action("example_detail")
                        .action("call")
                        .additional(String.valueOf(work.getId()))
                        .build()
                        .add();
                try {
                    callUp(Uri.parse("tel:" + phone.trim()));
                    new HljTracker.Builder(this).eventableId(merchant.getUserId())
                            .eventableType("Merchant")
                            .action("example_detail")
                            .action("real_call")
                            .additional(String.valueOf(work.getId()))
                            .build()
                            .add();
                } catch (Exception ignored) {
                }
            }
            return;
        }

        if (contactDialog != null && contactDialog.isShowing()) {
            return;
        }
        if (contactDialog == null) {
            contactDialog = new Dialog(this, R.style.BubbleDialogTheme);
            Point point = JSONUtil.getDeviceSize(this);
            View view = View.inflate(this, R.layout.dialog_contact_phones, null);
            ListView listView = (ListView) view.findViewById(R.id.contact_list);
            ContactsAdapter contactsAdapter = new ContactsAdapter(this, contactPhones);
            listView.setAdapter(contactsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String phone = (String) adapterView.getAdapter()
                            .getItem(i);
                    if (!JSONUtil.isEmpty(phone) && phone.trim()
                            .length() != 0) {
                        new HljTracker.Builder(CaseDetailActivity.this).eventableId(merchant
                                .getUserId())
                                .eventableType("Merchant")
                                .action("example_detail")
                                .action("call")
                                .additional(String.valueOf(work.getId()))
                                .build()
                                .add();
                        try {
                            callUp(Uri.parse("tel:" + phone.trim()));
                            new HljTracker.Builder(CaseDetailActivity.this).eventableId(merchant
                                    .getUserId())
                                    .eventableType("Merchant")
                                    .action("example_detail")
                                    .action("real_call")
                                    .additional(String.valueOf(work.getId()))
                                    .build()
                                    .add();
                        } catch (Exception ignored) {

                        }
                    }
                }
            });
            contactDialog.setContentView(view);
            Window win = contactDialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                params.width = Math.round(point.x * 3 / 4);
                win.setGravity(Gravity.CENTER);
            }
        }

        contactDialog.show();
    }

    public void onCollect(View view) {
        if (work == null) {
            return;
        }
        if (!Util.loginBindChecked(CaseDetailActivity.this, Constants.Login.LIKE_LOGIN)) {
            return;
        }
        if (work.isCollected()) {
            imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_normal);
            tvCollect.setText(getString(R.string.label_collect));
            new HljTracker.Builder(this).eventableId(work.getId())
                    .eventableType("Example")
                    .action("example_detail")
                    .action("del_collect")
                    .build()
                    .add();
            new HttpDeleteTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject object = (JSONObject) obj;
                    if (object != null && object.optBoolean("result", false)) {
                        work.setCollected(false);
                        work.setCollectorCount(work.getCollectorCount() - 1);
                        Util.showToast(R.string.hint_discollect_complete, CaseDetailActivity.this);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.WORK_COLLECT,
                    work.getId())));
        } else {
            imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
            tvCollect.setText(getString(R.string.label_has_collection));
            new HljTracker.Builder(this).eventableId(work.getId())
                    .eventableType("Example")
                    .action("example_detail")
                    .action("collect")
                    .build()
                    .add();
            new HttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    JSONObject object = (JSONObject) obj;
                    if (object != null && object.optBoolean("result", false)) {
                        work.setCollected(true);
                        work.setCollectorCount(work.getCollectorCount() + 1);
                        if (Util.isNewFirstCollect(CaseDetailActivity.this, 4)) {
                            Util.showFirstCollectNoticeDialog(CaseDetailActivity.this, 4);
                        } else {
                            Util.showToast(R.string.hint_collect_complete, CaseDetailActivity.this);
                        }
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.WORK_COLLECT,
                    work.getId())));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (headerView != null && work != null) {
            float alpha;
            if (-headerView.getTop() > showHeight) {
                alpha = 1;
            } else if (headerView.getTop() >= 0) {
                alpha = 0;
            } else {
                alpha = (float) -headerView.getTop() / showHeight;
            }
            actionLayout.setAlpha(alpha);
            //            shadowView.setAlpha(1 - alpha);
        }

        if (bubbleTimer == null) {
            return;
        }
        View c = listView.getChildAt(0);
        if (c == null) {
            return;
        }
        int scrollY = -c.getTop() + listView.getFirstVisiblePosition() * c.getHeight();
        Log.d(CaseDetailActivity.class.getSimpleName(), "Scroll Y = " + scrollY);
        if (scrollY > SCROLL_Y_DELTA) {
            bubbleTimer.overScrollDelta();
        }
    }

    private void initWorkJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            work = new Work(jsonObject);
            if (work.getId() == 0) {
                return;
            }
            loadRecommendCase();
            if (work.isSoldOut()) {
                tvSoldOut.setVisibility(View.VISIBLE);
                tvSoldOut.setText(getString(R.string.label_sold_out,
                        getString(R.string.label_case)));
            } else {
                tvSoldOut.setVisibility(View.GONE);
            }

            if (jsonObject.optJSONArray("media_items") != null && jsonObject.optJSONArray(
                    "media_items")
                    .length() > 0) {
                JSONArray array = jsonObject.optJSONArray("media_items");
                for (int i = 0, size = array.length(); i < size; i++) {
                    Item item = new Item(array.optJSONObject(i));
                    if (!JSONUtil.isEmpty(item.getMediaPath())) {
                        item.setType(Constants.ItemType.OpuItem);
                        items.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            ImageView ivCover = (ImageView) headerView.findViewById(R.id.iv_cover);
            String url = JSONUtil.getImagePath(work.getCaseHeadImg(), imageWidth);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadUtil.loadImageView(this, url, ivCover, imageWidth, imageWidth * 3 / 2);
            }

            LinearLayout itemsLayout = (LinearLayout) headerView.findViewById(R.id.items_layout);
            ArrayList<Article> articles = new ArrayList<>();
            JSONArray array = jsonObject.optJSONArray("work_items");
            if (array != null && array.length() > 0) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    Article article = new Article(array.optJSONObject(i));
                    if (!JSONUtil.isEmpty(article.getName()) && !JSONUtil.isEmpty(article
                            .getDescribe())) {
                        articles.add(article);
                    }

                }
            }
            if (!articles.isEmpty()) {
                itemsLayout.removeAllViews();
                itemsLayout.setVisibility(View.VISIBLE);
                for (Article article : articles) {
                    View itemView = View.inflate(this, R.layout.description_item, null);
                    TextView key = (TextView) itemView.findViewById(R.id.key);
                    TextView value = (TextView) itemView.findViewById(R.id.value);
                    key.setText(article.getName() + "：");
                    value.setText(article.getDescribe());
                    itemsLayout.addView(itemView);
                }
            } else {
                itemsLayout.setVisibility(View.GONE);
            }
            TextView title = (TextView) headerView.findViewById(R.id.tv_title);
            title.setText(work.getTitle());

            if (work.isCollected()) {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_selected);
                tvCollect.setText(getString(R.string.label_has_collection));
            } else {
                imgCollect.setImageResource(R.drawable.icon_collect_primary_44_44_normal);
                tvCollect.setText(getString(R.string.label_collect));
            }

            TextView tvDescribe = (TextView) headerView.findViewById(R.id.tv_describe);
            if (!JSONUtil.isEmpty(work.getDescribe())) {
                tvDescribe.setText(work.getDescribe());
                tvDescribe.setVisibility(View.VISIBLE);
            } else {
                tvDescribe.setVisibility(View.GONE);
            }

            if (work.getMerchant() != null) {
                NewMerchant merchant = work.getMerchant();
                headerView.findViewById(R.id.merchant_layout)
                        .setOnClickListener(this);
                ImageView ivMerchantAvatar = (ImageView) headerView.findViewById(R.id
                        .iv_merchant_avatar);
                TextView tvMerchantName = (TextView) headerView.findViewById(R.id.tv_merchant_name);
                View icBond = headerView.findViewById(R.id.ic_bond);
                ImageView icLevel = (ImageView) headerView.findViewById(R.id.ic_level);
                TextView tvWorksCount = (TextView) headerView.findViewById(R.id.tv_works_count);
                TextView tvCasesCount = (TextView) headerView.findViewById(R.id.tv_cases_count);
                TextView tvTwitterCount = (TextView) headerView.findViewById(R.id
                        .tv_twitters_count);
                if (merchant.getBondSign() != null) {
                    icBond.setVisibility(View.VISIBLE);
                }
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
                    icLevel.setVisibility(View.VISIBLE);
                    icLevel.setImageResource(res);
                }

                String logoPath = JSONUtil.getImagePath2(merchant.getLogoPath(),
                        Util.dp2px(this, 36));
                if (!JSONUtil.isEmpty(logoPath)) {
                    ImageLoadUtil.loadImageView(this,
                            logoPath,
                            R.mipmap.icon_avatar_primary,
                            ivMerchantAvatar,
                            true);
                } else {
                    ivMerchantAvatar.setImageResource(R.mipmap.icon_avatar_primary);
                }

                tvMerchantName.setText(merchant.getName());
                if (work.getMerchant() == null || CommonUtil.isCollectionEmpty(work.getMerchant()
                        .getContactPhone())) {
                    callLayout.setVisibility(View.GONE);
                }

                tvWorksCount.setText(getString(R.string.label_work_count,
                        String.valueOf(merchant.getActiveWorkCount())));
                tvCasesCount.setText(getString(R.string.label_case_count,
                        String.valueOf(merchant.getActiveCaseCount())));
                tvTwitterCount.setText(getString(R.string.label_twitter_count,
                        String.valueOf(merchant.getFeedCount())));
            }
        }
    }

    private void initOtherCases(ArrayList<Work> works) {
        if (works.size() > 0) {
            footerView.findViewById(R.id.recommend_cases_layout)
                    .setVisibility(View.VISIBLE);
            LinearLayout worksLayout = (LinearLayout) footerView.findViewById(R.id
                    .recommend_case_list);
            int childCount = worksLayout.getChildCount();
            int size = Math.min(4, works.size());
            if (childCount > size) {
                worksLayout.removeViews(size, childCount - size);
            }
            for (int i = 0; i < size; i++) {
                Work work = works.get(i);
                View childView = worksLayout.getChildAt(i);
                if (childView == null) {
                    childView = View.inflate(this, R.layout.other_case_item_view2, null);
                    childView.setVisibility(View.VISIBLE);
                    childView.findViewById(R.id.line_layout)
                            .setVisibility(i == size - 1 ? View.GONE : View.VISIBLE);
                    worksLayout.addView(childView);
                }
                setWorkViewValue(childView, work);
            }
        } else {
            footerView.findViewById(R.id.recommend_cases_layout)
                    .setVisibility(View.GONE);
        }
    }

    private void setWorkViewValue(View view, Work work) {
        view.setTag(work);
        ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvDescribe = (TextView) view.findViewById(R.id.tv_describe);
        TextView tvCollectCount = (TextView) view.findViewById(R.id.tv_collect_count);
        String url = JSONUtil.getImagePath(work.getCoverPath(), Util.dp2px(this, 116));
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadUtil.loadImageView(this, url, ivCover);
        }
        tvTitle.setText(work.getTitle());
        tvDescribe.setText(work.getDescribe());
        tvCollectCount.setText(getString(R.string.label_collect_count,
                String.valueOf(work.getCollectorCount())));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = (Work) v.getTag();
                if (work != null) {
                    Intent intent = new Intent(CaseDetailActivity.this, CaseDetailActivity.class);
                    intent.putExtra("id", work.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        super.onResume();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(chatDataSub,
                chatTrigSub,
                appointmentSub,
                loadSub,
                recommendCaseSub);
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    handler.sendEmptyMessage(requestCode);
                    break;
                case Constants.Login.LIKE_LOGIN:
                    onCollect(null);
                    break;
                case Constants.Login.CONTACT_LOGIN:
                    onContact(null);
                    break;
                case Constants.Login.SUBMIT_LOGIN:
                    onReservation(null);
                    break;
                case Constants.Login.MSG_LOGIN:
                    onMessage(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class CaseDetailZip extends HljHttpResultZip {
        @HljRZField
        JsonElement caseDetail;
        @HljRZField
        HljHttpData<List<com.hunliji.hljcommonlibrary.models.Work>> recommendWork;
    }

    class ViewHolder {
        @BindView(R.id.item_image)
        ImageView imageView;
        @BindView(R.id.items_layout)
        RelativeLayout itemsLayout;
        @BindView(R.id.item_describe)
        TextView describe;
        @BindView(R.id.play)
        ImageView play;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class RecommendWorkAdapter extends PagerAdapter {

        private List<com.hunliji.hljcommonlibrary.models.Work> workList;
        private LayoutInflater inflater;
        private Context mContext;

        public RecommendWorkAdapter(
                Context mContext, List<com.hunliji.hljcommonlibrary.models.Work> workList) {
            this.mContext = mContext;
            this.inflater = LayoutInflater.from(mContext);
            this.workList = workList;
        }

        @Override
        public int getCount() {
            return CommonUtil.getCollectionSize(workList);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = inflater.inflate(R.layout.small_common_work_item___cv, null, false);
            SmallWorkViewHolder workViewHolder = new SmallWorkViewHolder(itemView);
            workViewHolder.setStyle(SmallWorkViewHolder.STYLE_CASE_DETAIL_RELATIVE);
            workViewHolder.setView(mContext, workList.get(position), position, position);
            workViewHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, Object object) {
                    if (object == null) {
                        return;
                    }
                    if (object instanceof com.hunliji.hljcommonlibrary.models.Work) {
                        Intent intent = new Intent(CaseDetailActivity.this, WorkActivity.class);
                        intent.putExtra("id",
                                ((com.hunliji.hljcommonlibrary.models.Work) object).getId());
                        startActivity(intent);
                    }
                }
            });
            container.addView(itemView);
            return itemView;
        }

        @Override
        public boolean isViewFromObject(
                @NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(
                @NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }
}
