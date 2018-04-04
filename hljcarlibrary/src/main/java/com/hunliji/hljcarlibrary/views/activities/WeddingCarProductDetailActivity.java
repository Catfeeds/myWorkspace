package com.hunliji.hljcarlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.WeddingCarDetailAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.util.ChatBubbleTimer;
import com.hunliji.hljcarlibrary.util.WeddingCarSession;
import com.hunliji.hljcarlibrary.util.WeddingCarSupportUtil;
import com.hunliji.hljcarlibrary.views.fragments.WeddingCarSkuFragment;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.WeddingCarRouteService;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.activities.AskQuestionListActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

/**
 * 婚车详情activity
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarProductDetailActivity extends HljBaseNoBarActivity implements
        WeddingCarDetailAdapter.onWeddingCarClickListener {

    @Override
    public String pageTrackTagName() {
        return "婚车详情";
    }

    @Override
    public VTMetaData pageTrackData() {
        long id = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(id, VTMetaData.DATA_TYPE.DATA_TYPE_CAR);
    }

    public static final String ARG_ID = "id";
    private String FRAGMENT_TAG = "tag_sku_fragment";
    public static final int SCROLL_Y_DELTA = 1000; // 页面滑动距离触发轻松聊
    public static final int TIME_STAYED_IN_PAGE = 10 * 1000;
    public static final int TIME_HIDE_AFTER = 20 * 1000;

    @BindView(R2.id.tv_car_count)
    TextView tvCarCount;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R2.id.msg_count)
    TextView msgCount;
    @BindView(R2.id.msg_notice)
    View msgNotice;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.chat_bubble_layout)
    LinearLayout chatBubbleLayout;
    @BindView(R2.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R2.id.tv_bubble_msg)
    TextView tvBubbleMsg;

    @BindView(R2.id.layout_call)
    LinearLayout layoutCall;
    @BindView(R2.id.layout_sms)
    LinearLayout layoutSms;
    @BindView(R2.id.btn_join_team)
    Button btnJoinTeam;
    @BindView(R2.id.btn_find_schedule)
    Button btnFindSchedule;

    private long id;
    private WeddingCarDetailAdapter detailAdapter;
    private WeddingCarProduct carProduct;
    private HljHttpQuestion<List<Question>> carQuestion;
    private List<CommentMark> markList;
    private WeddingCarRouteService weddingCarRouteService;
    private Dialog orderCarDialog;
    private ChatBubbleTimer bubbleTimer;
    private LinearLayoutManager manager;
    private String supportMsg;


    private HljHttpSubscriber carProductSub;
    private HljHttpSubscriber questionAndMarkSub;
    private Subscription rxBusSub;
    private HljHttpSubscriber orderCarSub;
    private HljHttpSubscriber chatTrigSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_detail___car);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
        registerRxBus();

        iniTracker();
    }

    private void initConstant() {
        if (getIntent() != null) {
            id = getIntent().getLongExtra(ARG_ID, 0L);
        }
        weddingCarRouteService = (WeddingCarRouteService) ARouter.getInstance()
                .build(RouterPath.ServicePath.WEDDING_CAR_SERVICE)
                .navigation();
        weddingCarRouteService.onNoticeInit(this, msgCount, msgNotice);
        detailAdapter = new WeddingCarDetailAdapter(this);
        detailAdapter.setOnWeddingCarClickListener(this);
    }

    private void initWidget() {
        setDefaultStatusBarPadding();
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(detailAdapter);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        onRecyclerViewScrolled(recyclerView, dx, dy);
                    }
                });
    }

    private void iniTracker() {
        HljVTTagger.buildTagger(chatBubbleLayout)
                .tagName(HljTaggerName.FREE_CHAT_BUBBLE)
                .dataId(id)
                .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_CAR)
                .tag();
        HljVTTagger.buildTagger(layoutCall)
                .tagName(HljTaggerName.BTN_CALL)
                .hitTag();
        HljVTTagger.buildTagger(layoutSms)
                .tagName(HljTaggerName.BTN_CHAT)
                .hitTag();
        HljVTTagger.buildTagger(btnJoinTeam)
                .tagName(HljTaggerName.BTN_ADD_CART)
                .hitTag();
        HljVTTagger.buildTagger(btnFindSchedule)
                .tagName(HljTaggerName.BTN_SCHEDULE)
                .hitTag();
    }

    private void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        onBubbleScrolled();
    }

    private void onBubbleScrolled() {
        if (bubbleTimer == null) {
            return;
        }
        View c = recyclerView.getChildAt(0);
        if (c == null) {
            return;
        }
        int scrollY = -c.getTop() + manager.findFirstVisibleItemPosition() * c.getHeight();
        if (scrollY > SCROLL_Y_DELTA) {
            bubbleTimer.overScrollDelta();
        }
    }

    private void getQuestionAndMark() {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        long merchantId = carProduct.getMerchantComment()
                .getId();
        CommonUtil.unSubscribeSubs(questionAndMarkSub);
        questionAndMarkSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<WeddingCarProductResultZip>() {
                    @Override
                    public void onNext(WeddingCarProductResultZip weddingCarProductResultZip) {
                        setQuestionAndAnswer(weddingCarProductResultZip.carQuestion);
                        setCommentMarks(weddingCarProductResultZip.markList);
                        startBubbleTime();
                        detailAdapter.calculateItemCount();
                        detailAdapter.notifyDataSetChanged();
                    }
                })
                .build();

        Observable<HljHttpQuestion<List<Question>>> questionObb = QuestionAnswerApi.getQAList(
                merchantId,
                1,
                1);
        Observable<HljHttpData<List<CommentMark>>> markObb = WeddingCarApi.getWeddingCarMarks(
                merchantId);
        Observable.zip(questionObb,
                markObb,
                new Func2<HljHttpQuestion<List<Question>>, HljHttpData<List<CommentMark>>,
                        Object>() {
                    @Override
                    public Object call(
                            HljHttpQuestion<List<Question>> question,
                            HljHttpData<List<CommentMark>> listHljHttpData) {
                        WeddingCarProductResultZip resultZip = new WeddingCarProductResultZip();
                        resultZip.carQuestion = question;
                        resultZip.markList = listHljHttpData == null ? null : listHljHttpData
                                .getData();
                        return resultZip;
                    }
                })
                .subscribe(questionAndMarkSub);
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(carProductSub);
        carProductSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<WeddingCarProduct>() {
                    @Override
                    public void onNext(WeddingCarProduct carProduct) {
                        setWeddingCarProduct(carProduct);
                        getQuestionAndMark();
                    }
                })
                .build();
        WeddingCarApi.getWeddingCarProductDetail(id)
                .subscribe(carProductSub);
    }

    private void registerRxBus() {
        rxBusSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        if (rxEvent == null) {
                            return;
                        }
                        switch (rxEvent.getType()) {
                            case WEDDING_CAR_CART_COUNT:
                                refreshCarCount();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startBubbleTime() {
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

    private void showChatBubble(boolean isShow) {
        if (!isShow) {
            chatBubbleLayout.setVisibility(View.GONE);
            return;
        }

        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }

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

        Glide.with(this)
                .load(ImagePath.buildPath(carProduct.getMerchantComment()
                        .getLogoPath())
                        .width(CommonUtil.dp2px(this, 38))
                        .height(CommonUtil.dp2px(this, 38))
                        .cropPath())
                .into(imgLogo);
        supportMsg = WeddingCarSupportUtil.getCarMerchantChatLinkMsg();
        tvBubbleMsg.setText(supportMsg);
        chatBubbleLayout.setVisibility(View.VISIBLE);
    }

    private void refreshCarCount() {
        if (tvCarCount == null || this.isFinishing()) {
            return;
        }
        int count = WeddingCarSession.getInstance()
                .getCarCartCount(this, 0);
        if (count > 0) {
            tvCarCount.setText(count > 99 ? "99+" : String.valueOf(count));
            tvCarCount.setVisibility(View.VISIBLE);
        } else {
            tvCarCount.setVisibility(View.GONE);
        }
    }

    private void setWeddingCarProduct(WeddingCarProduct carProduct) {
        this.carProduct = carProduct;
        detailAdapter.setWeddingCarProduct(carProduct);
    }

    private void setQuestionAndAnswer(HljHttpQuestion<List<Question>> carQuestion) {
        this.carQuestion = carQuestion;
        detailAdapter.setCarQuestion(this.carQuestion);
    }

    private void setCommentMarks(List<CommentMark> markList) {
        this.markList = markList;
        detailAdapter.setMarkList(this.markList);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (weddingCarRouteService != null) {
            weddingCarRouteService.onNoticeResume();
        }
        refreshCarCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (weddingCarRouteService != null) {
            weddingCarRouteService.onNoticePause();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(carProductSub,
                rxBusSub,
                chatTrigSub,
                questionAndMarkSub,
                orderCarSub);
        if (bubbleTimer != null) {
            bubbleTimer.cancel();
            bubbleTimer = null;
        }
    }

    @OnClick(R2.id.layout_call)
    void onCall() {
        //打电话
        if (carProduct == null || carProduct.getMerchantComment() == null || TextUtils.isEmpty(
                carProduct.getMerchantComment()
                        .getContactPhones())) {
            return;
        }
        callUp(Uri.parse("tel:" + carProduct.getMerchantComment()
                .getContactPhones()));
    }

    @OnClick(R2.id.chat_bubble_layout)
    void onChat() {
        bubbleTimer.cancel();
        chatBubbleLayout.setVisibility(View.GONE);

        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackWeddingCarProduct trackWeddingCarProduct = new TrackWeddingCarProduct(carProduct);
        wsTrack.setAction(WSTrack.WEDDING_CAR);
        wsTrack.setCarProduct(trackWeddingCarProduct);

        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        carProduct.getMerchantComment()
                                .getUserId())
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, wsTrack)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY,
                        carProduct.getCity())
                .navigation(this);

        chatTrigSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                    }
                })
                .build();
        WeddingCarApi.postMerchantChatLinkTrigger(carProduct.getMerchantComment()
                .getId(), supportMsg)
                .subscribe(chatTrigSub);
    }

    @OnClick(R2.id.layout_sms)
    void onSms() {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackWeddingCarProduct trackWeddingCarProduct = new TrackWeddingCarProduct(carProduct);
        wsTrack.setAction(WSTrack.WEDDING_CAR);
        wsTrack.setCarProduct(trackWeddingCarProduct);

        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        carProduct.getMerchantComment()
                                .getUserId())
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, wsTrack)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY,
                        carProduct.getCity())
                .navigation(this);
    }

    @OnClick(R2.id.layout_car_team)
    void onCarTeam() {
        if (carProduct == null) {
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WEDDING_CAR_TEAM_ACTIVITY)
                .withParcelable("city", carProduct.getCity())
                .navigation(this);
    }

    @OnClick(R2.id.btn_join_team)
    void onJoinTeam() {
        showSkuFragment();
    }

    private void showSkuFragment() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (carProduct == null) {
            return;
        }

        if (carProduct.getSkus() == null) {
            Toast.makeText(this, "该婚车没有规格", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        WeddingCarSkuFragment carSkuFragment = (WeddingCarSkuFragment) getSupportFragmentManager
                ().findFragmentById(
                R.id.layout_sku);
        if (carSkuFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up,
                    R.anim.slide_out_bottom2,
                    R.anim.slide_in_up,
                    R.anim.slide_out_bottom2);
            ft.show(carSkuFragment);
            ft.commit();
            return;
        }
        carSkuFragment = new WeddingCarSkuFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WeddingCarSkuFragment.ARG_CAR_DETAIL, carProduct);
        carSkuFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up,
                R.anim.slide_out_bottom2,
                R.anim.slide_in_up,
                R.anim.slide_out_bottom2);
        ft.add(R.id.layout_sku, carSkuFragment, FRAGMENT_TAG);
        ft.commit();
    }

    public void backSkuFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up,
                            R.anim.slide_out_bottom2,
                            R.anim.slide_in_up,
                            R.anim.slide_out_bottom2)
                    .hide(fragment)
                    .commit();
        }
    }

    @OnClick(R2.id.btn_find_schedule)
    void onFindSchedule() {
        Intent intent = new Intent(this, WeddingCarCalendarActivity.class);
        intent.putExtra(WeddingCarCalendarActivity.WEDDING_CAR_PRODUCT, carProduct);
        startActivity(intent);
    }

    @OnClick(R2.id.btn_msg2)
    void onNotice() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.MESSAGE_HOME_ACTIVITY)
                .navigation(this);
    }

    @OnClick(R2.id.btn_share2)
    void onShare() {
        if (carProduct == null || carProduct.getShare() == null) {
            return;
        }
        ShareDialogUtil.onCommonShare(this, carProduct.getShare());
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up,
                            R.anim.slide_out_bottom2,
                            R.anim.slide_in_up,
                            R.anim.slide_out_bottom2)
                    .hide(fragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onOrderCar() {
        if (carProduct == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(orderCarSub);
        orderCarSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        setOrderCarSubResult(result);
                    }
                })
                .build();
        WeddingCarApi.orderCar(carProduct.getMerchantComment()
                .getId(), carProduct.getId())
                .subscribe(orderCarSub);
    }

    @Override
    public void onCommentIdList(WeddingCarProduct carProduct, long commentId) {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        Intent intent = new Intent(this, WeddingCarCommentListActivity.class);
        intent.putExtra(WeddingCarCommentListActivity.ARG_MERCHANT_ID,
                carProduct.getMerchantComment()
                        .getId());
        intent.putExtra(WeddingCarCommentListActivity.ARG_MERCHANT_USER_ID,
                carProduct.getMerchantComment()
                        .getUserId());
        intent.putExtra(WeddingCarCommentListActivity.ARG_CAR_PRODUCT, carProduct);
        startActivity(intent);
    }

    @Override
    public void onCommentMarkIdList(WeddingCarProduct carProduct, long markId) {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        Intent intent = new Intent(this, WeddingCarCommentListActivity.class);
        intent.putExtra(WeddingCarCommentListActivity.ARG_MERCHANT_ID,
                carProduct.getMerchantComment()
                        .getId());
        intent.putExtra(WeddingCarCommentListActivity.ARG_MERCHANT_USER_ID,
                carProduct.getMerchantComment()
                        .getUserId());
        intent.putExtra(WeddingCarCommentListActivity.ARG_CAR_PRODUCT, carProduct);
        intent.putExtra(WeddingCarCommentListActivity.ARG_MARK_ID, markId);
        startActivity(intent);
    }

    @Override
    public void onAskQuestionList(long merchantId, boolean isShowKeyboard) {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (merchantId <= 0) {
            return;
        }
        Intent intent = new Intent(this, AskQuestionListActivity.class);
        intent.putExtra(AskQuestionListActivity.ARG_MERCHANT_ID, merchantId);
        intent.putExtra(AskQuestionListActivity.ARG_SHOW_KEYBOARD, isShowKeyboard);
        startActivity(intent);
    }

    private void setOrderCarSubResult(HljHttpResult result) {
        if (result != null) {
            if (result.getStatus() != null) {
                if (result.getStatus()
                        .getRetCode() == 0) {
                    showOrderCarDialog();
                } else {
                    Toast.makeText(this,
                            result.getStatus()
                                    .getMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(this, "预约失败", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this, "预约失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showOrderCarDialog() {
        if (orderCarDialog != null && orderCarDialog.isShowing()) {
            return;
        }
        if (orderCarDialog == null) {
            orderCarDialog = new Dialog(this, R.style.BubbleDialogTheme);
            orderCarDialog.setContentView(R.layout.dialog_wedding_car_order___car);
            orderCarDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            orderCarDialog.dismiss();
                        }
                    });
            Window window = orderCarDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = CommonUtil.getDeviceSize(this).x * 27 / 32;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(params);
        }
        orderCarDialog.show();
    }

    class WeddingCarProductResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpQuestion<List<Question>> carQuestion;
        @HljRZField
        List<CommentMark> markList;
    }
}
