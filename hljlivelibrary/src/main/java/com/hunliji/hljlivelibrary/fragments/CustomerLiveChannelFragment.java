package com.hunliji.hljlivelibrary.fragments;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.adapters.LiveShopItemAdapter;
import com.hunliji.hljlivelibrary.api.LiveApi;
import com.hunliji.hljlivelibrary.models.LiveIntroItem;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRelevantWrapper;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.makeramen.rounded.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static com.hunliji.hljlivelibrary.models.LiveRelevantWrapper.TYPE_PRODUCT;
import static com.hunliji.hljlivelibrary.models.LiveRelevantWrapper.TYPE_WORK;

/**
 * Created by luohanlin on 2017/11/23.
 */

public class CustomerLiveChannelFragment extends BaseLiveChannelFragment implements
        SlidingUpPanelLayout.PanelSlideListener {

    @BindView(R2.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R2.id.danmaku_layout_holder)
    LinearLayout danmakuLayoutHolder;
    @BindView(R2.id.img_bottom_intro_cover)
    RoundedImageView imgBottomIntroCover;
    @BindView(R2.id.tv_bottom_intro_title)
    TextView tvBottomIntroTitle;
    @BindView(R2.id.bottom_intro_layout)
    RelativeLayout bottomIntroLayout;
    @BindView(R2.id.tv_introducing)
    TextView tvIntroducing;
    @BindView(R2.id.img_top_intro_cover)
    RoundedImageView imgTopIntroCover;
    @BindView(R2.id.tv_top_intro_price)
    TextView tvTopIntroPrice;
    @BindView(R2.id.tv_bottom_intro_price)
    TextView tvBottomIntroPrice;
    @BindView(R2.id.top_intro_layout)
    CardView topIntroLayout;
    @BindView(R2.id.live_status_holder)
    LinearLayout liveStatusHolder;
    @BindView(R2.id.stick_images_recycler)
    RecyclerView stickImagesRecycler;
    @BindView(R2.id.tv_appointment)
    TextView tvAppointment;
    @BindView(R2.id.tv_prepare_title)
    TextView tvPrepareTitle;
    @BindView(R2.id.tv_start_hint)
    TextView tvStartHint;
    @BindView(R2.id.prepare_layout)
    LinearLayout prepareLayout;
    @BindView(R2.id.dragView)
    LinearLayout dragView;
    @BindView(R2.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;
    @BindView(R2.id.btn_envelope)
    ImageButton btnEnvelope;
    @BindView(R2.id.btn_share)
    ImageButton btnShare;
    @BindView(R2.id.tv_editor)
    TextView tvEditor;
    @BindView(R2.id.btn_message)
    ImageButton btnMessage;
    @BindView(R2.id.new_msg_dot)
    View newMsgDow;
    @BindView(R2.id.bottom_layout_customer)
    LinearLayout bottomLayoutCustomer;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.img_gift)
    ImageView imgGift;
    @BindView(R2.id.merchant_chat_layout)
    FrameLayout merchantChatLayout;
    @BindView(R2.id.tv_unread_count)
    TextView tvUnreadCount;
    @BindView(R2.id.top_intro_layout_holder)
    LinearLayout topIntroLayoutHolder;

    private boolean hasPrepareInfo;
    private boolean hasStickInfo;

    public static final int DRAG_VIEW_H = 30;
    public static final int PREPARE_VIEW_H = 66;
    public static final int STATUS_VIEW_H = 20;
    public static final int STICK_IMAGES_VIEW_H = 78;

    private final static String FRAGMENT_TAG_LIVE_MESSAGE = "tag_live_message_fragment";
    private Unbinder unbinder;
    private ShopDlgViewHolder shopDlgViewHolder;

    private HljHttpSubscriber shopListSub;
    private HljHttpSubscriber appointmentSub;
    private Subscription wsRxEventSub;

    private Work currentWork;
    private ShopProduct currentProduct;
    private boolean isDanmakuCheck = true; // 弹幕开关
    private boolean hasShowedBottomIntro;
    private ArrayList<LiveMessage> danmakuMessages;
    private ArrayList<LiveIntroItem> shopList;
    private LiveIntroItem currentBottomIntroItem;
    private boolean hasCollapsed; // 只有第一次滑动才会触发收起事件

    public static CustomerLiveChannelFragment newInstance(LiveChannel channel) {
        Bundle args = new Bundle();
        CustomerLiveChannelFragment fragment = new CustomerLiveChannelFragment();
        args.putParcelable(ARG_CHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        initWsRxEventSub();
        refreshUnreadMsgCount();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_live_channel, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();
        initTracker();
        return view;
    }

    private void initTracker() {
        HljVTTagger.buildTagger(imgGift)
                .tagName(HljTaggerName.CustomerLiveChannelFragment.LIVE_GOODS_BTN)
                .hitTag();
    }

    @Override
    void initValues() {
        super.initValues();
        shopList = new ArrayList<>();
        danmakuMessages = new ArrayList<>();
    }

    @Override
    protected void initViews() {
        setFragment();
        setStatusLayout(channel);

        setTopProductInfo();
        // 初始隐藏弹幕
        danmakuLayoutHolder.setVisibility(View.INVISIBLE);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        slidingLayout.addPanelSlideListener(this);

        danmakuLayoutHolder.setEnabled(false);
        loadShopList();
    }

    private void initWsRxEventSub() {
        CommonUtil.unSubscribeSubs(wsRxEventSub);
        wsRxEventSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .buffer(1, TimeUnit.SECONDS)
                .filter(new Func1<List<RxEvent>, Boolean>() {
                    @Override
                    public Boolean call(List<RxEvent> rxEvents) {
                        for (RxEvent rxEvent : rxEvents) {
                            switch (rxEvent.getType()) {
                                case WS_MESSAGE:
                                    return true;
                            }
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<List<RxEvent>>() {
                    @Override
                    protected void onEvent(List<RxEvent> rxEvents) {
                        refreshUnreadMsgCount();
                    }
                });
    }

    private void refreshUnreadMsgCount() {
        User user = UserSession.getInstance()
                .getUser(getContext());
        if (user == null || user.getId() == 0 || getCurrentMerchant() == null ||
                getCurrentMerchant().getUserId() == 0 || tvUnreadCount == null) {
            return;
        }

        int count = WSRealmHelper.getUnreadMessageCountCount(user.getId(),
                getCurrentMerchant().getUserId());

        if (count > 0) {
            tvUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvUnreadCount.setVisibility(View.GONE);
        }
    }

    @Override
    protected RecyclerView getStickRecyclerView() {
        return stickImagesRecycler;
    }

    @Override
    void updateMerchantInfo(Merchant merchant) {
        super.updateMerchantInfo(merchant);
        if (merchant != null && merchant.getId() > 0) {
            merchantChatLayout.setVisibility(View.VISIBLE);
        } else {
            merchantChatLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void toggleDanmaku(boolean visible) {
        isDanmakuCheck = visible;
        danmakuLayoutHolder.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void backRoleRoom() {

    }

    @Override
    @OnClick(R2.id.img_gift)
    public void onShopping() {
        if (shopDlgViewHolder == null) {
            shopDlgViewHolder = new ShopDlgViewHolder(View.inflate(getContext(),
                    R.layout.dialog_live_shop_list___live,
                    null));
        }
        shopDlgViewHolder.show();
        if (CommonUtil.isCollectionEmpty(shopList)) {
            shopDlgViewHolder.progressBar.setVisibility(View.VISIBLE);
        }
        loadShopList();
    }

    @Override
    void setStatusLayout(LiveChannel liveChannel) {
        super.setStatusLayout(liveChannel);
        if (liveChannel.getStatus() != LiveChannel.LIVE_PREPARE_STATE) {
            hasPrepareInfo = false;
            liveStatusHolder.setVisibility(View.VISIBLE);
            liveStatusHolder.removeAllViews();
            liveStatusHolder.addView(statusViewHolder.getView());
            statusViewHolder.setStatus(liveChannel);
            prepareLayout.setVisibility(View.GONE);
        } else {
            hasPrepareInfo = true;
            liveStatusHolder.setVisibility(View.GONE);
            prepareLayout.setVisibility(View.VISIBLE);
            tvAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAppointment();
                }
            });
            tvAppointment.setText(channel.isAppointment() ? "已预约" : "立即预约");
            if (liveChannel.getStartTime() != null) {
                tvStartHint.setText(liveChannel.getStartTime()
                        .toString("MM/dd HH:mm " + "开播"));
            }
        }
        resetSlidingOffset();
    }

    private void loadShopList() {
        CommonUtil.unSubscribeSubs(shopListSub);
        shopListSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<LiveRelevantWrapper>>>() {
                    @Override
                    public void onNext(HljHttpData<List<LiveRelevantWrapper>> listHljHttpData) {
                        if (listHljHttpData != null && listHljHttpData.getData() != null) {
                            resortShopList(listHljHttpData.getData());

                            imgGift.setVisibility(View.VISIBLE);
                            ((AnimationDrawable) imgGift.getDrawable()).start();
                            if (!hasShowedBottomIntro) {
                                setBottomIntroInfo(shopList.get(0));
                            }
                            if (shopDlgViewHolder != null) {
                                shopDlgViewHolder.progressBar.setVisibility(View.GONE);
                                shopDlgViewHolder.adapter.notifyDataSetChanged();
                            }
                        } else {
                            hasShowedBottomIntro = true;
                            showDanmakuView();
                            imgGift.setVisibility(View.GONE);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        LiveApi.getLiveShopListObb(channel.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shopListSub);
    }

    @Override
    void onLiveRxEvent(LiveRxEvent liveRxEvent) {
        super.onLiveRxEvent(liveRxEvent);
        switch (liveRxEvent.getType()) {
            case REPLY_MESSAGE:
                //回复消息
                LiveMessage replyMessage = (LiveMessage) liveRxEvent.getObject();
                onReplay(replyMessage);
                break;
            case CLEAR_INTRODUCING:
                // 清空正在介绍的商品
                clearTopIntroProductInfo();
                break;
            case WORK_UPDATE:
                // 更新套餐信息
                updateIntroWorkInfo((Work) liveRxEvent.getObject());
                break;
            case PRODUCT_UPDATE:
                // 更新婚品信息
                updateIntroProductInfo((ShopProduct) liveRxEvent.getObject());
                break;
            case SEND_MESSAGE:
                //用户发送消息更新或插入
                LiveMessage message = (LiveMessage) liveRxEvent.getObject();
                if (message.getRoomType() == HljLive.ROOM.CHAT && !message.isError() && !message
                        .isSending() && !message.isDeleted() && message.getId() > 0) {
                    addMessage(message);
                }
                break;
            case MESSAGE_LIST_SCROLL_TOP:
                if (slidingLayout != null) {
                    slidingLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (slidingLayout == null) {
                                return;
                            }
                            if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState
                                    .EXPANDED && !hasCollapsed) {
                                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState
                                        .COLLAPSED);
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void setStickMessageLayout(List<LiveMessage> newStickMessages) {
        super.setStickMessageLayout(newStickMessages);
        if (CommonUtil.isCollectionEmpty(newStickMessages)) {
            hasStickInfo = false;
        } else {
            hasStickInfo = true;
        }
        resetSlidingOffset();
    }

    @Override
    protected void addStickMessage(List<LiveMessage> newStickMessage) {
        super.addStickMessage(newStickMessage);
    }

    private void resetSlidingOffset() {
        int panelH = DRAG_VIEW_H;
        int offset = 0;
        if (hasStickInfo) {
            offset += STICK_IMAGES_VIEW_H;
        }
        if (hasPrepareInfo) {
            panelH += PREPARE_VIEW_H;
        } else {
            offset += STATUS_VIEW_H;
        }

        if (getContext() == null) {
            return;
        }
        int panelHeight = CommonUtil.dp2px(getContext(), panelH);
        int offsetHeight = CommonUtil.dp2px(getContext(), offset);
        slidingLayout.setPanelHeight(panelHeight);
        slidingLayout.setParallaxOffset(offsetHeight);
        if (liveMessageFragment != null) {
            liveMessageFragment.setRecyclerViewPadding(panelHeight, offsetHeight);
        }
        topIntroLayoutHolder.setPadding(0, panelHeight, 0, 0);
    }

    private void setBottomIntroInfo(LiveIntroItem liveIntroItem) {
        this.currentBottomIntroItem = liveIntroItem;
        if (bottomIntroLayout == null) {
            return;
        }
        switch (liveIntroItem.getType()) {
            case TYPE_PRODUCT:
                final ShopProduct product = liveIntroItem.getProduct();
                tvBottomIntroTitle.setText(product.getTitle());
                tvBottomIntroPrice.setText("￥" + CommonUtil.formatDouble2StringWithTwoFloat
                        (product.getShowPrice()));
                Glide.with(getContext())
                        .load(ImagePath.buildPath(product.getCoverPath())
                                .width(CommonUtil.dp2px(getContext(), 60))
                                .height(CommonUtil.dp2px(getContext(), 60))
                                .path())
                        .into(imgBottomIntroCover);
                showBottomIntroView();
                break;
            case TYPE_WORK:
                final Work work = liveIntroItem.getWork();
                tvBottomIntroTitle.setText(work.getTitle());
                tvBottomIntroPrice.setText("￥" + CommonUtil.formatDouble2String(work.getShowPrice
                        ()));
                Glide.with(getContext())
                        .load(ImagePath.buildPath(work.getCoverPath())
                                .width(CommonUtil.dp2px(getContext(), 60))
                                .height(CommonUtil.dp2px(getContext(), 60))
                                .path())
                        .into(imgBottomIntroCover);
                showBottomIntroView();
                break;
        }
    }

    private void updateIntroWorkInfo(Work work) {
        if (work != null) {
            this.currentProduct = null;
            this.currentWork = work;
        }
        setTopProductInfo();
    }

    private void updateIntroProductInfo(ShopProduct product) {
        if (product != null) {
            this.currentProduct = product;
            this.currentWork = null;
        }
        setTopProductInfo();
    }

    private void clearTopIntroProductInfo() {
        currentWork = null;
        currentProduct = null;
        if (topIntroLayout != null) {
            topIntroLayout.setVisibility(View.GONE);
        }
    }

    private void setTopProductInfo() {
        if ((currentProduct != null || currentWork != null) && topIntroLayout != null) {
            if (currentWork != null) {
                initTopProductTracker(topIntroLayout, currentWork);
                tvTopIntroPrice.setText("￥" + CommonUtil.formatDouble2String(currentWork
                        .getShowPrice()));
                Glide.with(getContext())
                        .load(ImagePath.buildPath(currentWork.getCoverPath())
                                .width(CommonUtil.dp2px(getContext(), 60))
                                .height(CommonUtil.dp2px(getContext(), 60))
                                .path())
                        .into(imgTopIntroCover);
            } else {
                initTopProductTracker(topIntroLayout, currentProduct);
                tvTopIntroPrice.setText("￥" + CommonUtil.formatDouble2StringWithTwoFloat(
                        currentProduct.getShowPrice()));
                Glide.with(getContext())
                        .load(ImagePath.buildPath(currentProduct.getCoverPath())
                                .width(CommonUtil.dp2px(getContext(), 60))
                                .height(CommonUtil.dp2px(getContext(), 60))
                                .path())
                        .into(imgTopIntroCover);
            }

            topIntroLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initTopProductTracker(View view, Object object) {
        try {
            if (object instanceof ShopProduct) {
                ShopProduct product = (ShopProduct) object;
                HljVTTagger.buildTagger(view)
                        .tagName(HljTaggerName.PRODUCT)
                        .dataId(product.getId())
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PRODUCT)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND,
                                product.getDtExtend())
                        .tag();
            } else if (object instanceof Work) {
                Work work = (Work) object;
                long propertyId = 0;
                try {
                    propertyId = work.getMerchant()
                            .getProperty()
                            .getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HljVTTagger.buildTagger(view)
                        .tagName(HljTaggerName.WORK)
                        .dataId(work.getId())
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_PACKAGE)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_PROPERTY_ID,
                                propertyId > 0 ? propertyId : null)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_DT_EXTEND, work.getDtExtend())
                        .tag();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomIntroView() {
        bottomIntroLayout.setVisibility(View.VISIBLE);
        hasShowedBottomIntro = true;
        bottomIntroLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 五秒后显示婚品提示，和弹幕
                if (bottomIntroLayout == null) {
                    return;
                }
                bottomIntroLayout.setVisibility(View.GONE);
                showDanmakuView();
            }
        }, 5000);
    }

    private void showDanmakuView() {
        if (danmakuLayoutHolder != null) {
            if (isDanmakuCheck && channel.getStatus() != LiveChannel.LIVE_PREPARE_STATE) {
                danmakuLayoutHolder.setVisibility(View.VISIBLE);
            } else {
                danmakuLayoutHolder.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void onAppointment() {
        // 先显示通知开关dialog
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(getContext(),
                "",
                "开启消息通知",
                "立即开启消息通知，第一时间获取开播提醒～",
                0);

        if (dialog != null) {
            dialog.show();
        } else {
            postAppointment();
        }
    }

    private void postAppointment() {
        if (channel.isAppointment()) {
            return;
        }
        CommonUtil.unSubscribeSubs(appointmentSub);
        appointmentSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        getLiveChannelActivity().onAfterAppointment();
                        channel.setAppointment(true);
                        setStatusLayout(channel);
                        Toast.makeText(getContext(), "预约成功", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build();
        LiveApi.makeAppointment(channel.getId())
                .subscribe(appointmentSub);
    }

    private void setFragment() {
        liveMessageFragment = LiveChannelMessageFragment.newInstance(HljLive.ROOM.LIVE,
                channel.getId());
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        liveMessageFragment.setUserVisibleHint(true);
        ft.add(R.id.content_layout, liveMessageFragment, FRAGMENT_TAG_LIVE_MESSAGE);
        ft.commit();
    }

    @Override
    protected void setDanmakuViews(List<LiveMessage> liveMessages) {
        if (!isDanmakuCheck) {
            newMsgDow.setVisibility(View.VISIBLE);
        }
        if (CommonUtil.isCollectionEmpty(liveMessages)) {
            return;
        }
        final ArrayList<LiveMessage> newList = new ArrayList<>();
        for (int i = 0; i < liveMessages.size(); i++) {
            LiveMessage message = liveMessages.get(i);
            if (!TextUtils.isEmpty(message.getLiveContent()
                    .getText())) {
                newList.add(message);
                if (newList.size() == 3) {
                    addMessages(newList);
                    return;
                }
            }
        }

        addMessages(newList);
    }

    private View getDanmakuView(LiveMessage liveMessage) {
        View danmakuView = LayoutInflater.from(getContext())
                .inflate(R.layout.live_danmaku_layout, null);
        DMViewHolder dmVh = new DMViewHolder(danmakuView);
        dmVh.setView(liveMessage);

        return danmakuView;
    }

    private void addMessage(LiveMessage liveMessage) {
        for (LiveMessage lm : danmakuMessages) {
            if (liveMessage.getId() == lm.getId()) {
                // 去重
                return;
            }
        }
        ArrayList<LiveMessage> messages = new ArrayList<>();
        messages.add(liveMessage);
        addMessages(messages);
    }

    private void addMessages(List<LiveMessage> liveMessages) {
        if (getContext() == null) {
            return;
        }
        if (liveMessages.size() > 1) {
            for (int i = liveMessages.size() - 1; i >= 0; i--) {
                LiveMessage message = liveMessages.get(i);
                View danmakuView = getDanmakuView(message);
                if (danmakuLayoutHolder.getChildCount() >= 3) {
                    danmakuLayoutHolder.removeViewAt(0);
                    danmakuMessages.remove(0);
                }
                danmakuLayoutHolder.addView(danmakuView, danmakuLayoutHolder.getChildCount());
                danmakuMessages.add(message);
            }
        } else {
            View danmakuView = getDanmakuView(liveMessages.get(0));
            if (danmakuLayoutHolder.getChildCount() >= 3) {
                danmakuLayoutHolder.removeViewAt(0);
                danmakuMessages.remove(0);
            }
            TransitionManager.beginDelayedTransition(danmakuLayoutHolder,
                    new Slide(Gravity.BOTTOM).setDuration(300));
            danmakuLayoutHolder.addView(danmakuView, danmakuLayoutHolder.getChildCount());
            danmakuMessages.add(liveMessages.get(0));
        }

        if (bottomIntroLayout != null && bottomIntroLayout.getVisibility() == View.VISIBLE) {
            danmakuLayoutHolder.setVisibility(View.GONE);
        }
    }

    @OnClick(R2.id.btn_message)
    void onChatRoom() {
        getLiveChannelActivity().setChatRoom(true, true);
        newMsgDow.setVisibility(View.GONE);
    }

    @OnClick(R2.id.tv_editor)
    void onEditor() {
        getLiveChannelActivity().setChatRoom(true, false);
    }

    private void onReplay(LiveMessage reply) {
        getLiveChannelActivity().replyChatRoom(reply);
    }

    @OnClick(R2.id.btn_envelope)
    void onMerchantChat() {
        if (getCurrentMerchant() == null) {
            return;
        }
        MerchantUser user = getCurrentMerchant().toUser();
        ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomDialogChatActivityPath
                        .WS_CUSTOMER_CHAT_DIALOG_ACTIVITY)
                .withParcelable(RouterPath.IntentPath.Customer.WsCustomDialogChatActivityPath
                                .ARG_USER,
                        user)
                .withBoolean(RouterPath.IntentPath.Customer.WsCustomDialogChatActivityPath
                                .ARG_IS_COLLECTED,
                        getCurrentMerchant().isCollected())
                .withTransition(R.anim.activity_anim_default, R.anim.activity_anim_default)
                .navigation(getContext());
    }

    @OnClick(R2.id.bottom_intro_layout)
    void onBottomIntroLayout() {
        bottomIntroLayout.setVisibility(View.GONE);
        showDanmakuView();
        if (currentBottomIntroItem == null) {
            return;
        }
        switch (currentBottomIntroItem.getType()) {
            case TYPE_PRODUCT:
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                        .withLong("id",
                                currentBottomIntroItem.getProduct()
                                        .getId())
                        .navigation(getContext());
                break;
            case TYPE_WORK:
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.WORK_ACTIVITY)
                        .withLong("id",
                                currentBottomIntroItem.getWork()
                                        .getId())
                        .navigation(getContext());
                break;
        }
    }

    @OnClick(R2.id.top_intro_layout)
    void onTopIntroLayout() {
        if (currentWork != null) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.WORK_ACTIVITY)
                    .withLong("id", currentWork.getId())
                    .navigation(getContext());
        } else if (currentProduct != null) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.SHOP_PRODUCT)
                    .withLong("id", currentProduct.getId())
                    .navigation(getContext());
        }
    }

    @OnClick(R2.id.btn_share)
    void onShare() {
        getLiveChannelActivity().onShare();
    }


    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(shopListSub, wsRxEventSub, appointmentSub);
    }

    private void resortShopList(List<LiveRelevantWrapper> wrapperList) {
        shopList.clear();
        for (LiveRelevantWrapper relevantWrapper : wrapperList) {
            if (relevantWrapper.getType() == TYPE_PRODUCT && !CommonUtil.isCollectionEmpty(
                    relevantWrapper.getInfoList())) {
                for (ShopProduct product : (ArrayList<ShopProduct>) relevantWrapper.getInfoList()) {
                    LiveIntroItem item = new LiveIntroItem(product.isIntroduced(),
                            product,
                            TYPE_PRODUCT);
                    shopList.add(item);
                }
            }
            if (relevantWrapper.getType() == TYPE_WORK && !CommonUtil.isCollectionEmpty(
                    relevantWrapper.getInfoList())) {
                for (Work work : (ArrayList<Work>) relevantWrapper.getInfoList()) {
                    LiveIntroItem item = new LiveIntroItem(work.isIntroduced(), work, TYPE_WORK);
                    shopList.add(item);
                }
            }
        }
        final int currentType;
        if (getCurrentMerchant() != null && getCurrentMerchant().getShopType() == Merchant
                .SHOP_TYPE_PRODUCT) {
            currentType = LiveRelevantWrapper.TYPE_PRODUCT;
        } else {
            currentType = LiveRelevantWrapper.TYPE_WORK;
        }
        Collections.sort(shopList, new Comparator<LiveIntroItem>() {
            @Override
            public int compare(LiveIntroItem o1, LiveIntroItem o2) {
                if (o1.getType() == currentType && o2.getType() != currentType) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        Collections.sort(shopList, new Comparator<LiveIntroItem>() {
            @Override
            public int compare(LiveIntroItem o1, LiveIntroItem o2) {
                if (o1.isIntroducing()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(
            View panel,
            SlidingUpPanelLayout.PanelState previousState,
            SlidingUpPanelLayout.PanelState newState) {
        if (previousState == SlidingUpPanelLayout.PanelState.DRAGGING && newState ==
                SlidingUpPanelLayout.PanelState.COLLAPSED) {
            hasCollapsed = true;
        }
    }

    class DMViewHolder {
        @BindView(R2.id.tv_content)
        TextView tvContent;
        @BindView(R2.id.img_avatar)
        RoundedImageView imgAvatar;

        private View view;

        DMViewHolder(View v) {
            this.view = v;
            ButterKnife.bind(this, view);
        }

        private void setView(LiveMessage liveMessage) {
            Glide.with(view.getContext())
                    .load(ImageUtil.getAvatar(liveMessage.getUser()
                            .getAvatar()))
                    .into(imgAvatar);
            tvContent.setText(liveMessage.getLiveContent()
                    .getText());
        }
    }

    class ShopDlgViewHolder {
        @BindView(R2.id.recycler_view)
        RecyclerView recyclerView;
        @BindView(R2.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R2.id.bottom_layout_customer)
        LinearLayout bottomLayoutCustomer;
        @BindView(R2.id.bottom_layout)
        LinearLayout bottomLayout;
        @BindView(R2.id.img_gift)
        ImageView imgGift;

        Dialog dialog;
        LiveShopItemAdapter adapter;


        public VTMetaData pageTrackData() {
            return new VTMetaData(channel.getId(), VTMetaData.DATA_TYPE.DATA_TYPE_LIVE);
        }

        ShopDlgViewHolder(View view) {
            ButterKnife.bind(this, view);
            try {
                HljVTTagger.tagViewPage(view, "直播相关商品", pageTrackData());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            dialog.setContentView(view);
            Window win = dialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                params.width = Math.round(CommonUtil.getDeviceSize(getContext()).x);
                params.height = CommonUtil.dp2px(getContext(), 256);
                win.setGravity(Gravity.BOTTOM);
            }
            adapter = new LiveShopItemAdapter(getContext(), shopList);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
            recyclerView.setFocusable(false);

            recyclerView.setAdapter(adapter);
        }

        private void show() {
            ((AnimationDrawable) imgGift.getDrawable()).start();
            dialog.show();
        }

        @OnClick({R2.id.bottom_layout})
        void onHide() {
            dialog.cancel();
        }
    }
}
