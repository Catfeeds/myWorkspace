package com.hunliji.hljlivelibrary.activities;

import android.os.Bundle;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.api.LiveApi;
import com.hunliji.hljlivelibrary.fragments.BaseLiveChannelFragment;
import com.hunliji.hljlivelibrary.fragments.CustomerLiveChannelFragment;
import com.hunliji.hljlivelibrary.fragments.HostLiveChannelFragment;
import com.hunliji.hljlivelibrary.fragments.LiveChatRoomFragment;
import com.hunliji.hljlivelibrary.models.LiveContent;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljlivelibrary.models.wrappers.LiveAuthor;
import com.hunliji.hljlivelibrary.websocket.LiveSocket;
import com.hunliji.hljlivelibrary.widget.LiveStatusViewHolder;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterPath.IntentPath.Live.LIVE_CHANNEL_ACTIVITY)
public class LiveChannelActivity extends HljBaseNoBarActivity {

    @Override
    public String pageTrackTagName() {
        return "直播详情";
    }

    @Override
    public VTMetaData pageTrackData() {
        long channelId = getIntent().getLongExtra(ARG_ID, 0);
        return new VTMetaData(channelId, VTMetaData.DATA_TYPE.DATA_TYPE_LIVE);
    }

    public static final String ARG_ID = "id";

    @BindView(R2.id.img_logo)
    RoundedImageView imgLogo;
    @BindView(R2.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R2.id.tv_follow)
    TextView tvFollow;
    @BindView(R2.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R2.id.avatars_layout)
    LinearLayout avatarsLayout;
    @BindView(R2.id.img_close)
    ImageView imgClose;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.live_status_holder)
    LinearLayout liveStatusHolder;
    @BindView(R2.id.action_layout_holder)
    LinearLayout actionLayoutHolder;
    @BindView(R2.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R2.id.img_empty_hint)
    ImageView imgEmptyHint;
    @BindView(R2.id.img_net_hint)
    ImageView imgNetHint;
    @BindView(R2.id.tv_empty_hint)
    TextView tvEmptyHint;
    @BindView(R2.id.tv_empty_hint2)
    TextView tvEmptyHint2;
    @BindView(R2.id.empty_hint_layout)
    LinearLayout emptyHintLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.chat_content_layout)
    FrameLayout chatContentLayout;
    @BindView(R2.id.chat_room_fragment_layout)
    RelativeLayout chatRoomFragmentLayout;

    private long channelId;
    private BaseLiveChannelFragment liveChannelFragment;
    public static final String FRAGMENT_TAG_LIVE = "live_fragment";
    public static final String FRAGMENT_TAG_CHAT = "chat_fragment";
    private LiveChannel channel;
    private Merchant currentMerchant; // 当前正在介绍的商家（主持人商家）
    private HljHttpSubscriber collectCheckSub;
    private HljHttpSubscriber channelSub;
    private boolean isCollectedMerchant;
    private HljHttpSubscriber followSub;
    private LiveChatRoomFragment liveChatRoomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_channel);
        ButterKnife.bind(this);

        setActionBarPadding(actionLayoutHolder);

        initValues();
        initViews();
        initLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMerchantCollectStatus();
    }

    private void initValues() {
        channelId = getIntent().getLongExtra(ARG_ID, 0);

        new HljTracker.Builder(this).eventableId(channelId)
                .eventableType("Live")
                .action("hit")
                .build()
                .add();
    }

    private void initViews() {
    }

    private void initLoad() {
        CommonUtil.unSubscribeSubs(channelSub);
        progressBar.setVisibility(View.VISIBLE);
        channelSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(contentLayout)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<LiveChannel>() {
                    @Override
                    public void onNext(LiveChannel liveChannel) {
                        channel = liveChannel;
                        currentMerchant = channel.getMerchant();
                        loadMerchantCollectStatus();
                        setChannelInfo(channel);
                        setContentFragment();
                    }
                })
                .build();
        LiveApi.getLiveChannelObb(channelId)
                .subscribe(channelSub);
    }

    public void initSocketAndSendEvents() {
        if (channel == null) {
            return;
        }
        if (channel.getThread() != null && CommonUtil.getAppType() != CommonUtil.PacketType.MERCHANT) {
            contentLayout.post(new Runnable() {
                @Override
                public void run() {
                    getLiveSocket().postLiveEvent(new LiveRxEvent(LiveRxEvent.RxEventType
                            .CHANNEL_THREAD,
                            channelId,
                            channel.getThread()));
                }
            });
        }
        initLiveSocket();
    }

    private void loadMerchantCollectStatus() {
        if (currentMerchant != null && currentMerchant.getId() > 0) {
            CommonUtil.unSubscribeSubs(collectCheckSub);
            collectCheckSub = HljHttpSubscriber.buildSubscriber(this)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<Boolean>() {
                        @Override
                        public void onNext(Boolean isCollected) {
                            if (channel.isHost()) {
                                tvFollow.setVisibility(View.GONE);
                            } else {
                                tvFollow.setVisibility(View.VISIBLE);
                                isCollectedMerchant = isCollected;
                                tvFollow.setText(isCollected ? R.string.label_enter___cm : R
                                        .string.label_follow___cm);
                                if (currentMerchant != null) {
                                    currentMerchant.setCollected(isCollectedMerchant);
                                }
                            }
                        }
                    })
                    .build();
            LiveApi.isCollectMerchant(currentMerchant.getId())
                    .subscribe(collectCheckSub);
        }
    }

    private void setMerchantInfo(Merchant merchant) {
        if (currentMerchant != null && currentMerchant.getId() > 0) {
            merchantLayout.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(merchant.getLogoPath())
                    .into(imgLogo);
            tvMerchantName.setText(currentMerchant.getName());
        } else {
            merchantLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setChannelInfo(LiveChannel liveChannel) {
        setLiveStatusLayout(liveChannel);
    }

    private void setParticipantsAvatars(LiveChannel liveChannel) {
        List<Author> users = liveChannel.getUsers();
        if (CommonUtil.isCollectionEmpty(users)) {
            return;
        }

        Collections.reverse(users);

        int avatarSize = CommonUtil.dp2px(this, 28);
        int size = Math.min(4, users.size());
        int maxWidth = CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 237);
        int allowMaxCount = maxWidth / (avatarSize + CommonUtil.dp2px(this, 6));
        size = Math.min(size, allowMaxCount);

        avatarsLayout.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView avatarView;
            avatarView = (ImageView) View.inflate(this, R.layout.watch_user_view___live, null);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(avatarSize,
                    avatarSize);
            params.rightMargin = CommonUtil.dp2px(this, 3);
            params.leftMargin = CommonUtil.dp2px(this, 3);

            avatarsLayout.addView(avatarView, params);
            Author user = users.get(i);
            String avatarPath = user != null ? ImageUtil.getAvatar(user.getAvatar(),
                    avatarSize) : null;
            Glide.with(this)
                    .load(avatarPath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(avatarView);
        }
    }

    private void setContentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (channel.isHost()) {
            liveChannelFragment = HostLiveChannelFragment.newInstance(channel);
        } else {
            liveChannelFragment = CustomerLiveChannelFragment.newInstance(channel);
            liveChatRoomFragment = LiveChatRoomFragment.newInstance(channel);
            ft.add(R.id.chat_content_layout, liveChatRoomFragment, FRAGMENT_TAG_CHAT);
        }
        ft.add(R.id.content_layout, liveChannelFragment, FRAGMENT_TAG_LIVE);
        ft.commit();
    }

    private void setLiveStatusLayout(LiveChannel channelInfo) {
        if (channelInfo.getId() <= 0) {
            return;
        }
        if (!channelInfo.isHost() && channelInfo.getStatus() == LiveChannel.LIVE_PREPARE_STATE) {
            // 如果是观众，并且处于预热状态中，则将status layout放在标题栏下面
            liveStatusHolder.setVisibility(View.VISIBLE);
            liveStatusHolder.removeAllViews();
            LiveStatusViewHolder statusViewHolder = new LiveStatusViewHolder(this);
            liveStatusHolder.addView(statusViewHolder.getView());
            statusViewHolder.setStatus(channelInfo);
        } else {
            // 否则，在交给fragment中自行显示
            liveStatusHolder.removeAllViews();
            liveStatusHolder.setVisibility(View.GONE);
        }
    }

    public void updateChannelInfo(LiveChannel liveChannel) {
        setParticipantsAvatars(liveChannel);
        setLiveStatusLayout(liveChannel);
    }

    public void updateMerchantInfo(Merchant merchant) {
        this.currentMerchant = merchant;
        setMerchantInfo(currentMerchant);
        loadMerchantCollectStatus();
    }

    @Override
    public void onBackPressed() {
        if (chatRoomFragmentLayout.getVisibility() == View.VISIBLE) {
            setChatRoom(false, false);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R2.id.chat_room_fragment_layout)
    void onChatRoomFragmentClick() {
        setChatRoom(false, false);
    }

    public void setChatRoom(boolean isChatRoomVisible, boolean isMessageListVisible) {
        liveChatRoomFragment.setListVisible(isMessageListVisible);
        TransitionManager.beginDelayedTransition(chatRoomFragmentLayout,
                new Slide(Gravity.BOTTOM).setDuration(300));
        chatRoomFragmentLayout.setVisibility(isChatRoomVisible ? View.VISIBLE : View.INVISIBLE);
        if (isChatRoomVisible) {
            liveChatRoomFragment.setFragmentVisible();
            liveChatRoomFragment.focusAndShowKeyboard();
        }
    }

    public void replyChatRoom(LiveMessage replyMessage) {
        if (chatRoomFragmentLayout.getVisibility() == View.VISIBLE) {
            setChatRoom(true, true);
        } else {
            setChatRoom(true, false);
        }
        liveChatRoomFragment.onReplyMessage(replyMessage);
    }

    public void onToggleDanmaku(boolean visible) {
        if (liveChannelFragment != null) {
            liveChannelFragment.toggleDanmaku(visible);
        }
    }

    public void sendText(LiveMessage replyMessage, String text) {
        LiveContent liveContent = new LiveContent(text);
        sendMessage(initLiveMessage(liveContent, replyMessage));
    }

    /**
     * 初始化消息模板
     *
     * @param content 消息内容
     * @return
     */
    public LiveMessage initLiveMessage(LiveContent content, LiveMessage replyMessage) {
        User user = UserSession.getInstance()
                .getUser(this);
        LiveAuthor author = new LiveAuthor();
        author.setId(user.getId());
        author.setName(user.getNick());
        author.setAvatar(user.getAvatar());
        author.setLiveRole(channel.getLiveRole());
        if (user instanceof MerchantUser) {
            author.setKind(1);
            author.setMerchantId(((MerchantUser) user).getMerchantId());
        } else if (user instanceof CustomerUser) {
            author.setSpecialty(((CustomerUser) user).getSpecialty());

        }
        return new LiveMessage(content,
                author,
                channel.getLiveRole() == HljLive.ROLE.CUSTORMER ? HljLive.ROOM.CHAT : HljLive
                        .ROOM.LIVE,
                replyMessage);
    }

    public void sendMessage(LiveMessage message) {
        if (getLiveSocket() == null) {
            return;
        }

        getLiveSocket().sendMessage(this, message);

        if (liveChannelFragment != null) {
            liveChannelFragment.backRoleRoom();
        }
    }

    public void onShare() {
        if (channel != null && channel.getShare() != null) {
            ShareDialogUtil.onCommonShare(this, channel.getShare());
        }
    }

    public void onShopping() {
        if (liveChannelFragment != null) {
            liveChannelFragment.onShopping();
        }
    }

    private void onCollectMerchant() {
        followSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object object) {
                        isCollectedMerchant = true;
                        tvFollow.setText(R.string.label_enter___cm);
                        Toast.makeText(LiveChannelActivity.this, "关注成功！", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .build();
        CommonApi.postMerchantFollowObb(currentMerchant.getId())
                .subscribe(followSub);
    }

    @OnClick(R2.id.img_close)
    void onClose() {
        onBackPressed();
    }

    @OnClick(R2.id.tv_follow)
    void onTvFollow() {
        if (isCollectedMerchant) {
            goMerchant();
        } else {
            onCollectMerchant();
        }
    }

    @OnClick({R2.id.img_logo, R2.id.tv_merchant_name})
    void goMerchant() {
        if (CommonUtil.getAppType() == CommonUtil.PacketType.MERCHANT) {
            return;
        }
        if (currentMerchant.getShopType() == Merchant.SHOP_TYPE_PRODUCT) {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.PRODUCT_MERCHANT_HOME)
                    .withLong("id", currentMerchant.getId())
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(this);
        } else {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                    .withLong("id", currentMerchant.getId())
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(this);
        }
    }

    public void onAfterAppointment() {
        channel.setAppointment(true);
    }

    public Merchant getCurrentMerchant() {
        return currentMerchant;
    }

    private void initLiveSocket() {
        if (getLiveSocket() != null) {
            getLiveSocket().onResume();
            //前后台切换判断重连
            if (!getLiveSocket().isConnect()) {
                getLiveSocket().connect(this);
            }
        }
    }

    @Override
    protected void onFinish() {
        if (getLiveSocket() != null) {
            getLiveSocket().disconnect();
        }
        CommonUtil.unSubscribeSubs(collectCheckSub, channelSub);
        super.onFinish();
    }

    public LiveSocket getLiveSocket() {
        if (channel == null) {
            return null;
        }
        return LiveSocket.getInstance(channel.getLiveRole(), channel.getId());
    }
}
