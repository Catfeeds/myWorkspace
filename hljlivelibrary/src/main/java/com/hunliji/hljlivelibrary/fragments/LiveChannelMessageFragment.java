package com.hunliji.hljlivelibrary.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.activities.LiveChannelActivity;
import com.hunliji.hljlivelibrary.adapters.LiveMessageAdapter;
import com.hunliji.hljlivelibrary.api.LiveApi;
import com.hunliji.hljlivelibrary.models.LiveContent;
import com.hunliji.hljlivelibrary.models.LiveContentCoupon;
import com.hunliji.hljlivelibrary.models.LiveContentRedpacket;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljlivelibrary.models.wrappers.LiveSocketData;
import com.hunliji.hljlivelibrary.models.wrappers.LiveSocketMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by luohanlin on 2017/11/23.
 */

public class LiveChannelMessageFragment extends ScrollAbleFragment implements LiveMessageAdapter
        .OnMessageClickListener {

    public static final String ARG_ROOM_TYPE = "room_type";
    public static final String ARG_CHANNEL_ID = "channel_id";
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_new_live_msg)
    TextView tvNewLiveMsg;

    Unbinder unbinder;
    private int type;
    private long channelId;
    private boolean isEnd;
    private int visibleStatus; //0 从未显示，1 当前显示，2 隐藏
    private int newsCount; //新消息数
    private LiveMessageAdapter adapter;
    private Dialog menuDialog;
    private View loadView;
    private HljHttpSubscriber redPacketSub;
    private HljHttpSubscriber couponSub;
    private HljHttpSubscriber loadNexPageSub;
    private Subscription rxSubscription;

    public static LiveChannelMessageFragment newInstance(int type, long channelId) {
        Bundle args = new Bundle();
        LiveChannelMessageFragment fragment = new LiveChannelMessageFragment();
        args.putInt(ARG_ROOM_TYPE, type);
        args.putLong(ARG_CHANNEL_ID, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
        initRxLiveEventSub();
    }

    private void initValues() {
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_ROOM_TYPE);
            channelId = getArguments().getLong(ARG_CHANNEL_ID);
        }
        User user = UserSession.getInstance()
                .getUser(getContext());
        adapter = new LiveMessageAdapter(getContext(), user.getId(), channelId, this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_channel_message___live,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews() {
        if (type == HljLive.ROOM.LIVE) {
            emptyView.setHintId(R.string.label_live_message_empty___live);
            emptyView.setHint2Id(R.string.label_live_message_empty2___live);
        } else {
            emptyView.setHintId(R.string.label_chat_message_empty___live);
            emptyView.setHint2Id(R.string.label_chat_message_empty2___live);
        }
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter.setFooterView(footerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setRecyclerViewPadding(0, 0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                triggerCollapsedEvent(velocityY);
                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (recyclerView.getAdapter() == null || recyclerView.getAdapter()
                                .getItemCount() < 2 || isEnd) {
                            return;
                        }
                        if (loadNexPageSub != null && !loadNexPageSub.isUnsubscribed()) {
                            return;
                        }
                        if (((LinearLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition() > recyclerView.getAdapter()
                                .getItemCount() - 10) {
                            loadNextPage(adapter.getLastId());
                        }
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                triggerCollapsedEvent(dy);
                readNewMessage();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void triggerCollapsedEvent(int y) {
        if (Math.abs(y) > 10) {
            RxBus.getDefault()
                    .post(new LiveRxEvent(LiveRxEvent.RxEventType.MESSAGE_LIST_SCROLL_TOP,
                            channelId,
                            null));
        }
    }

    private void loadNextPage(long lastId) {
        CommonUtil.unSubscribeSubs(loadNexPageSub);

        loadNexPageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<List<LiveMessage>>() {
                    @Override
                    public void onNext(List<LiveMessage> liveMessages) {
                        if (!liveMessages.isEmpty()) {
                            adapter.loadMessages(liveMessages);
                        }
                        isEnd = liveMessages.isEmpty();
                        if (loadView != null) {
                            loadView.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (loadView != null) {
                            loadView.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .build();
        LiveApi.getLiveHistoriesObb(channelId, type, lastId, 1, 20)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (recyclerView != null) {
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadView != null) {
                                        loadView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                })
                .subscribe(loadNexPageSub);
    }

    private void initRxLiveEventSub() {
        CommonUtil.unSubscribeSubs(rxSubscription);
        rxSubscription = RxBus.getDefault()
                .toObservable(LiveRxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<LiveRxEvent>() {
                    @Override
                    protected void onEvent(LiveRxEvent liveRxEvent) {
                        onLiveRxEvent(liveRxEvent);
                    }
                });
    }

    private void onLiveRxEvent(LiveRxEvent liveRxEvent) {
        if (liveRxEvent.getChannelId() != channelId) {
            return;
        }
        switch (liveRxEvent.getType()) {
            case CHANNEL_THREAD:
                onChannelThread(liveRxEvent);
                break;
            case NEW_MESSAGE:
                onNewMessages(liveRxEvent);
                break;
            case SEND_MESSAGE:
                onUserSendMessage(liveRxEvent);
                break;
            case LIVE_NEWS:
                setNewLiveMsg((Integer) liveRxEvent.getObject());
                break;
            case CHAT_NEWS:
                setNewChatMsg((Integer) liveRxEvent.getObject());
                break;
        }
    }

    private void onChannelThread(LiveRxEvent liveRxEvent) {
        if (type == HljLive.ROOM.LIVE) {
            CommunityThread thread = (CommunityThread) liveRxEvent.getObject();
            if (adapter != null) {
                adapter.setThread(thread);
            }
        }
    }

    private void onNewMessages(LiveRxEvent liveRxEvent) {
        LiveSocketData data = (LiveSocketData) liveRxEvent.getObject();
        if (data == null || (liveRxEvent.getChannelId() != channelId)) {
            return;
        }
        LiveSocketMessage socketMessage = data.getMessage();
        List<LiveMessage> messages = new ArrayList<>();
        List<LiveMessage> stickMessages = new ArrayList<>();
        List<LiveMessage> allMessages = type == HljLive.ROOM.CHAT ? socketMessage.getChatMessages
                () : socketMessage.getLiveMessages();
        if (allMessages != null && !allMessages.isEmpty()) {
            for (LiveMessage message : allMessages) {
                if (!message.isStick()) {
                    messages.add(message);
                }
            }
        }
        if (socketMessage.isHistory()) {
            //历史消息情况
            isEnd = false;
            if (allMessages == null || allMessages.isEmpty()) {
                if (type != HljLive.ROOM.LIVE || (data.getChannel() != null && data.getChannel()
                        .getStatus() != 3)) {
                    emptyView.showEmptyView();
                } else {
                    emptyView.hideEmptyView();
                }
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.hideEmptyView();
                recyclerView.setVisibility(View.VISIBLE);
            }
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            int offset = position < 0 ? 0 : layoutManager.findViewByPosition(position)
                    .getTop();
            Integer offsetPosition = adapter.initHistoryMessages(messages, stickMessages);
            if (offsetPosition == null) {
                layoutManager.scrollToPosition(0);
            } else if (position >= 0) {
                layoutManager.scrollToPositionWithOffset(position + offsetPosition, offset);
            }
        } else if (allMessages != null && !allMessages.isEmpty()) {
            //包含 删除 置顶 新消息情况
            if (recyclerView.getVisibility() != View.VISIBLE) {
                //如果当前界面空视图，切换为显示状态
                emptyView.hideEmptyView();
                recyclerView.setVisibility(View.VISIBLE);
            }
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            final int position = layoutManager.findFirstVisibleItemPosition();
            int offset = 0;
            if (layoutManager.findViewByPosition(position) != null) {
                offset = layoutManager.findViewByPosition(position)
                        .getTop();
            }
            final int offsetPosition = adapter.updateMessages(messages, stickMessages);

            //普通消息到顶部时直接插入消息，否则修改滑动偏移量
            if (position > adapter.getStickMessagesCount()) {
                final int finalOffset = offset;
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        layoutManager.scrollToPositionWithOffset(position + offsetPosition,
                                finalOffset);
                    }
                });
            }

            if (messages.size() > 0 && !messages.get(0)
                    .isDeleted()) {
                //新信息
                if (visibleStatus == 2 || position > adapter.getStickMessagesCount()) {
                    //不在当前页或未滑倒顶部
                    newsCount += messages.size();
                    RxBus.getDefault()
                            .post(new LiveRxEvent(type == HljLive.ROOM.LIVE ? LiveRxEvent
                                    .RxEventType.LIVE_NEWS : LiveRxEvent.RxEventType.CHAT_NEWS,
                                    channelId,
                                    newsCount));
                }
            }
        }
    }

    private void onUserSendMessage(LiveRxEvent liveRxEvent) {
        //用户发送消息更新或插入
        if (recyclerView.getVisibility() != View.VISIBLE) {
            //如果当前界面空视图，切换为显示状态
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
        LiveMessage message = (LiveMessage) liveRxEvent.getObject();
        if (message.getRoomType() == type) {
            int index = adapter.updateSendMessage(message);
            if (index >= 0) {
                recyclerView.scrollToPosition(index);
            }
        }
    }

    /**
     * 根据判断当前滑动判断新消息是否被读取
     */
    private void readNewMessage() {
        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
            return;
        }
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter()
                .getItemCount() < 2 || !(recyclerView.getAdapter() instanceof LiveMessageAdapter)) {
            return;
        }
        if (visibleStatus == 1 && newsCount > 0 && ((LinearLayoutManager) recyclerView
                .getLayoutManager()).findFirstVisibleItemPosition() < newsCount) {
            // 发过新消息且当前列表在滑倒到新消息
            newsCount = 0;
            RxBus.getDefault()
                    .post(new LiveRxEvent(type == HljLive.ROOM.LIVE ? LiveRxEvent.RxEventType
                            .LIVE_NEWS : LiveRxEvent.RxEventType.CHAT_NEWS,
                            channelId,
                            newsCount));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            visibleStatus = 1;
            readNewMessage();
        } else if (visibleStatus == 0) {
            return;
        } else {
            visibleStatus = 2;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(loadNexPageSub, redPacketSub, couponSub, rxSubscription);
    }

    @Override
    public void showMenu(final LiveMessage item) {
        if (menuDialog == null) {
            menuDialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            menuDialog.setContentView(R.layout.dialog_message_menu___live);
            menuDialog.findViewById(R.id.action_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuDialog.dismiss();
                        }
                    });
            Window win = menuDialog.getWindow();
            ViewGroup.LayoutParams params = win.getAttributes();
            Point point = CommonUtil.getDeviceSize(getContext());
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        if (item.getLiveContent() != null && !TextUtils.isEmpty(item.getLiveContent()
                .getText())) {
            menuDialog.findViewById(R.id.action_copy)
                    .setVisibility(View.VISIBLE);
            menuDialog.findViewById(R.id.action_copy)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuDialog.dismiss();
                            ClipboardManager clipboardManager = (ClipboardManager) getContext()
                                    .getSystemService(
                                    CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R
                                            .string.app_name),
                                    item.getLiveContent()
                                            .getText()));
                            ToastUtil.showToast(getContext(),
                                    null,
                                    R.string.msg_copy_success___chat);
                        }
                    });
        } else {
            menuDialog.findViewById(R.id.action_copy)
                    .setVisibility(View.GONE);
        }
        menuDialog.findViewById(R.id.action_reply)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuDialog.dismiss();
                        RxBus.getDefault()
                                .post(new LiveRxEvent(LiveRxEvent.RxEventType.REPLY_MESSAGE,
                                        channelId,
                                        item));
                    }
                });
        menuDialog.show();
    }

    @Override
    public void onUserClick(Author author) {
        //用户端时用户头像跳转
        if (CommonUtil.getAppType()!= CommonUtil.PacketType.CUSTOMER) {
            return;
        }
        if (author.getKind() == 1 && author.getMerchantId() > 0) {
            //跳转到商家主页
            if (author.getShopType() == Merchant.SHOP_TYPE_PRODUCT) {
                // 跳转婚品商家主页
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.PRODUCT_MERCHANT_HOME)
                        .withLong("id", author.getMerchantId())
                        .navigation(getContext());
            } else {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                        .withLong("id", author.getMerchantId())
                        .navigation(getContext());
            }
        } else if (author.getId() > 0) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                    .withLong("id", author.getId())
                    .navigation(getContext());
        }
    }

    @Override
    public void onRedpacket(final LiveContentRedpacket redpacket) {
        if (redpacket == null || TextUtils.isEmpty(redpacket.getRedemptionCode()) || CommonUtil
                .getAppType() == CommonUtil.PacketType.MERCHANT) {
            return;
        }
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        CommonUtil.unSubscribeSubs(redPacketSub);
        redPacketSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 显示成功弹窗
                        showSuccessDlg(redpacket);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        LiveApi.getLiveRedPacketObb(redpacket.getRedemptionCode())
                .subscribe(redPacketSub);
    }

    @Override
    public void onCoupon(final LiveContentCoupon coupon) {
        if (coupon == null || coupon.getId() == 0 || CommonUtil.getAppType() ==
                CommonUtil.PacketType.MERCHANT) {
            return;
        }
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        CommonUtil.unSubscribeSubs(couponSub);
        couponSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 显示成功弹窗
                        showSuccessDlg(coupon);
                    }
                })
                .setDataNullable(true)
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        LiveApi.getLiveCouponObb(String.valueOf(coupon.getId()))
                .subscribe(couponSub);
    }

    private void showSuccessDlg(LiveContent content) {
        final Dialog dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
        if (content instanceof LiveContentCoupon) {
            LiveContentCoupon coupon = (LiveContentCoupon) content;
            dialog.setContentView(R.layout.dlg_got_coupon___live);
            TextView tvMoney = dialog.findViewById(R.id.tv_money);
            TextView tvMoneySill = dialog.findViewById(R.id.tv_money_sill);
            tvMoney.setText(CommonUtil.formatDouble2String(coupon.getValue()));
            tvMoneySill.setText(getString(R.string.label_money,
                    CommonUtil.formatDouble2String(coupon.getValue())));
            tvMoneySill.setText(coupon.isCashCoupon() ? "现金券" : getString(R.string.label_money_sill,
                    CommonUtil.formatDouble2String(coupon.getMoneySill())));
        } else {
            LiveContentRedpacket redpacket = (LiveContentRedpacket) content;
            dialog.setContentView(R.layout.dlg_got_redpacket___live);
            TextView tvMoney = dialog.findViewById(R.id.tv_money);
            tvMoney.setText(CommonUtil.formatDouble2String(redpacket.getTotalMoney()));
        }

        dialog.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
        dialog.findViewById(R.id.content_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        // 跳转第三个tab
                        getLiveChannelActivity().onShopping();
                    }
                });

        dialog.show();
    }

    @OnClick(R2.id.tv_new_live_msg)
    void onNewLiveMsg() {
        tvNewLiveMsg.setVisibility(View.GONE);
        recyclerView.smoothScrollToPosition(0);
    }

    protected void setNewChatMsg(int count) {
        if (type == HljLive.ROOM.CHAT && count > 0) {
            tvNewLiveMsg.setText("有" + count + "条新消息");
            tvNewLiveMsg.setVisibility(View.VISIBLE);
        } else {
            tvNewLiveMsg.setVisibility(View.GONE);
        }
    }

    protected void setNewLiveMsg(int count) {
        if (type == HljLive.ROOM.LIVE && count > 0) {
            tvNewLiveMsg.setText("有" + count + "条新消息");
            tvNewLiveMsg.setVisibility(View.VISIBLE);
        } else {
            tvNewLiveMsg.setVisibility(View.GONE);
        }
    }

    public void setRecyclerViewPadding(int top, int bottom) {
        if (recyclerView != null) {
            top += CommonUtil.dp2px(getContext(), 10);
            recyclerView.setPadding(0, top, 0, bottom);
        }
    }

    protected LiveChannelActivity getLiveChannelActivity() {
        LiveChannelActivity activity = null;
        if (getActivity() instanceof LiveChannelActivity) {
            activity = (LiveChannelActivity) getActivity();
        }

        return activity;
    }

}
