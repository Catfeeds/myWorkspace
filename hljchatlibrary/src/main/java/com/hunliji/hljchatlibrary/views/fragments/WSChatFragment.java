package com.hunliji.hljchatlibrary.views.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hunliji.hljchatlibrary.R;
import com.hunliji.hljchatlibrary.R2;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljchatlibrary.adapters.ChatAdapter;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljchatlibrary.models.ChatRxEvent;
import com.hunliji.hljchatlibrary.views.widgets.ChatRecordView;
import com.hunliji.hljchatlibrary.views.widgets.SpeakView;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSLocation;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.models.realm.WSMedia;
import com.hunliji.hljcommonlibrary.models.realm.WSProduct;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;
import com.hunliji.hljhttplibrary.utils.HljFileUploadBuilder;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.ImageChooserActivity;
import com.slider.library.Indicators.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by luohanlin on 2017/11/28.
 */

@RuntimePermissions
public abstract class WSChatFragment extends Fragment implements ChatAdapter.OnChatClickListener,
        EmojiPagerAdapter.OnFaceItemClickListener {

    @BindView(R2.id.chat_list)
    public RecyclerView chatList;
    @BindView(R2.id.shadow_view)
    public View shadowView;
    @BindView(R2.id.menu_layout)
    public FrameLayout menuLayout;
    @BindView(R2.id.top_layout)
    FrameLayout topLayout;
    @BindView(R2.id.edit_layout)
    FrameLayout editLayout;
    @BindView(R2.id.face_pager)
    ViewPager facePager;
    @BindView(R2.id.flow_indicator)
    CirclePageIndicator flowIndicator;

    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.record_view)
    ChatRecordView recordView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.layout)
    RelativeLayout layout;
    Unbinder unbinder;

    public static final String ARG_CHANNEL_ID = "channelId";
    public static final String ARG_SOURCE = "source";
    private static final int CHOOSE_PHOTO_RESULT = 1;

    public User selfUser;
    public User sessionUser;
    public String channelId;
    public int source; //消息渠道 1普通2轻松聊3反向私信4自动回复5聚客宝
    public WSTrack wsTrack; // 聊天发送对象
    public long sessionUserId;

    private WSChatAuthor wsChatAuthor;


    public boolean immIsShow;
    public boolean showMenu;
    private long lastMessageTime;
    private final int WRITING_TIME = 15000; //正在输入的发送和显示时间间隔 15秒
    private long sendWritingTime;

    public InputMethodManager imm;
    private View emptyView;
    private EditText etContent;

    private Realm realm;
    private ChatAdapter adapter;

    public OnChatFragmentStateListener chatFragmentStateListener;

    private HljHttpSubscriber miniUserSub;
    private Subscription messageRxSub;
    private Subscription stateRxSub;
    private HljHttpSubscriber loadMessageSub;
    private SubscriptionList uploadSubscriptions;
    private boolean hasUnreadMessage;

    private Handler titleHandler = new Handler();
    private Runnable writingDoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (chatFragmentStateListener != null) {
                chatFragmentStateListener.onStateChange(getUserName());
            }
        }
    };


    protected abstract void onSpeckModeChange();

    protected abstract void showWork(long id);

    protected abstract void showCase(long id);

    protected abstract void showCustomMeal(long id);

    protected abstract void showProduct(long id);

    protected abstract void showUser(long id);

    protected abstract void gotoMerchant(long merchantId, int shopType);

    protected abstract void makeAppointment(long id);

    protected abstract void showWeddingCar(long id);

    protected abstract void autoSendSmartReplay(WSChat chat, String text);

    protected abstract void onFilterMessage(WSChat chat);

    protected WSChatAuthor getChatAuthor() {
        if (wsChatAuthor != null) {
            return wsChatAuthor;
        }
        if (realm != null && !realm.isClosed()) {
            wsChatAuthor = realm.where(WSChatAuthor.class)
                    .equalTo("id", sessionUser.getId())
                    .findFirst();
            if (wsChatAuthor != null) {
                wsChatAuthor = realm.copyFromRealm(wsChatAuthor);
                wsChatAuthor.setNick(sessionUser.getNick());
                wsChatAuthor.setAvatar(sessionUser.getAvatar());
            }
        }
        if (wsChatAuthor == null) {
            wsChatAuthor = new WSChatAuthor(sessionUser);
        }
        return wsChatAuthor;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat___chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    protected void initValues() {
        getSessionUserInfo(sessionUserId);
    }

    private void getSessionUserInfo(long userId) {
        CommonUtil.unSubscribeSubs(miniUserSub);
        miniUserSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<User>() {
                    @Override
                    public void onNext(User user) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (user == null) {
                            getActivity().finish();
                        } else {
                            init(user);
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().finish();
                    }
                })
                .build();
        ChatApi.getMiniUser(userId)
                .onErrorReturn(new Func1<Throwable, User>() {
                    @Override
                    public User call(Throwable throwable) {
                        throwable.printStackTrace();
                        return sessionUser;
                    }
                })
                .subscribe(miniUserSub);
    }

    protected void init(User user) {
        if (getActivity() == null) {
            return;
        }
        if (selfUser == null || user == null) {
            getActivity().finish();
            return;
        }
        this.sessionUser = user;
        if (chatFragmentStateListener != null) {
            chatFragmentStateListener.onSessionUserInfo(this.sessionUser);
        }

        // 初始化realm，socket，视图等
        if (isResumed()) {
            WSRealmHelper.entryChat(getContext(), selfUser.getId(), sessionUser.getId());
        }
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }

        initViews();
        updateSessionUserInLocal();
    }

    private void updateSessionUserInLocal() {
        // 更新Tips类型提示消息的发送者头像和昵称
        // 只在用户端中会出现，实际上只有预约和领券两种子类型消息需要更新
        if (realm != null && !realm.isClosed()) {
            realm.beginTransaction();
            WSChatAuthor author = realm.where(WSChatAuthor.class)
                    .equalTo("id", sessionUser.getId())
                    .findFirst();
            if (author != null) {
                author.setNick(sessionUser.getNick());
                author.setAvatar(sessionUser.getAvatar());
            }
            realm.commitTransaction();
        }
    }

    private void initViews() {
        emptyView = LayoutInflater.from(getContext())
                .inflate(R.layout.empty_layout___chat, null);
        adapter = new ChatAdapter(sessionUser, selfUser, this);
        adapter.setHeaderView(emptyView);

        if (chatFragmentStateListener != null) {
            chatFragmentStateListener.onStateChange(getUserName());
        }

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Point point = CommonUtil.getDeviceSize(getContext());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int faceImageSize = Math.round((point.x - 20 * dm.density) / 7);
        facePager.getLayoutParams().height = Math.round(faceImageSize * 3 + 20 * dm.density);
        EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(getContext(),
                faceImageSize,
                this);
        facePager.setAdapter(emojiPagerAdapter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(EmojiUtil.getFaceMap(getContext())
                .keySet());
        emojiPagerAdapter.setTags(arrayList);
        flowIndicator.setViewPager(facePager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        try {
            ((SimpleItemAnimator) chatList.getItemAnimator()).setSupportsChangeAnimations(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);
        chatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (chatList.getChildCount() > 0) {
                    if (oldBottom > bottom || !isBottom()) {
                        chatList.scrollBy(0, oldBottom - bottom);
                    }
                }
            }
        });
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getActivity().getWindow()
                        .getDecorView()
                        .getHeight();
                immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    onImmShow();
                } else {
                    onImmHide();
                }

            }
        });
    }

    public void addEditLayout(View view) {
        editLayout.removeAllViews();
        if (view != null) {
            editLayout.addView(view);
        }
    }

    public void addTopLayout(View view) {
        topLayout.removeAllViews();
        if (view != null) {
            topLayout.addView(view);
        }
        topLayout.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        topLayout.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        emptyView.setPadding(0, topLayout.getHeight(), 0, 0);
                        return false;
                    }
                });
    }

    public void refreshTopLayoutHeight() {
        topLayout.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        topLayout.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        emptyView.setPadding(0, topLayout.getHeight(), 0, 0);
                        return false;
                    }
                });
    }

    protected void initSpeakView(SpeakView btnSpeak) {
        btnSpeak.setChatRecordView(recordView);
        btnSpeak.setUserName(sessionUser.getNick());
        btnSpeak.setOnSpeakStatusListener(new SpeakView.OnSpeakStatusListener() {
            @Override
            public void recorderDone(String filePath, double time) {
                sendVoice(filePath, time);
            }
        });
    }

    private WSChat initChat(String kind) {
        WSChat chat = new WSChat();
        if (!TextUtils.isEmpty(channelId)) {
            chat.setChannel(channelId);
        }
        chat.setFromId(selfUser.getId());
        chat.setToId(sessionUser.getId());
        chat.setSessionId(sessionUser.getId());
        chat.setSpeaker(new WSChatAuthor(selfUser));
        chat.setSpeakerTo(getChatAuthor());
        chat.setIdStr(selfUser.getId() + "" + System.currentTimeMillis());
        chat.setKind(kind);
        chat.setCreatedAt(new Date());
        chat.setUserId(selfUser.getId());
        chat.setSending(true);
        chat.setUnRead(true);
        chat.setSource(source);
        return chat;
    }

    public void sendText(String content) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            ToastUtil.showToast(getContext(), "不能发送空白消息", 0);
            return;
        }
        sendWSTrack();
        WSChat chat = initChat(WSChat.TEXT);
        chat.setContent(content);
        WebSocket.getInstance()
                .sendMessage(chat, new SendMsgListener(chat));
    }

    public void sendTextAndRead(String content) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            ToastUtil.showToast(getContext(), "不能发送空白消息", 0);
            return;
        }
        sendWSTrack();
        WSChat chat = initChat(WSChat.TEXT);
        chat.setContent(content);
        chat.setUnRead(false);
        WebSocket.getInstance()
                .sendMessage(chat, new SendMsgListener(chat));
    }

    private void sendVoice(String path, double length) {
        sendWSTrack();
        final WSChat chat = initChat(WSChat.VOICE);
        WSMedia media = new WSMedia();
        media.setPath(path);
        media.setVoiceDuration(length);
        chat.setMedia(media);
        chatList.scrollToPosition(adapter.addChat(chat));
        Subscription uploadSubscription = new HljFileUploadBuilder(new File(path)).tokenPath(
                HljFileUploadBuilder.QINIU_VOICE_URL,
                HljFileUploadBuilder.UploadFrom.MESSAGE_VOICE)
                .build()
                .subscribe(new Subscriber<HljUploadResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HljUploadResult hljUploadResult) {
                        WSMedia media = chat.getMedia();
                        media.setPath(hljUploadResult.getUrl());
                        chat.setMedia(media);
                        WebSocket.getInstance()
                                .sendMessage(chat, new SendMsgListener(chat));

                    }
                });
        if (uploadSubscriptions == null) {
            uploadSubscriptions = new SubscriptionList();
        }
        uploadSubscriptions.add(uploadSubscription);
    }

    private void sendImage(String path) {
        sendWSTrack();
        Size size = ImageUtil.getImageSizeFromPath(path);
        final WSChat chat = initChat(WSChat.IMAGE);
        WSMedia media = new WSMedia();
        media.setPath(path);
        media.setHeight(size.getHeight());
        media.setWidth(size.getWidth());
        chat.setMedia(media);
        chatList.scrollToPosition(adapter.addChat(chat));
        Subscription uploadSubscription = new HljFileUploadBuilder(new File(path)).compress(
                getContext())
                .tokenPath(HljFileUploadBuilder.QINIU_IMAGE_TOKEN)
                .build()
                .subscribe(new Subscriber<HljUploadResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HljUploadResult hljUploadResult) {
                        WSMedia media = chat.getMedia();
                        media.setPath(hljUploadResult.getUrl());
                        media.setWidth(hljUploadResult.getWidth());
                        media.setHeight(hljUploadResult.getHeight());
                        chat.setMedia(media);
                        WebSocket.getInstance()
                                .sendMessage(chat, new SendMsgListener(chat));

                    }
                });
        if (uploadSubscriptions == null) {
            uploadSubscriptions = new SubscriptionList();
        }
        uploadSubscriptions.add(uploadSubscription);
    }

    private void sendWSTrack() {
        if (wsTrack == null) {
            return;
        }
        WSChat chat = initChat(WSChat.TRACK);
        chat.setExtContent(new WSExtObject(wsTrack));
        wsTrack = null;
        WebSocket.getInstance()
                .sendMessage(chat, new SendMsgListener(chat));
    }

    protected void sendExtraObj(WSProduct extraObj) {
        if (extraObj != null) {
            if (TextUtils.isEmpty(extraObj.getKind())) {
                return;
            }
            WSChat chat = initChat(extraObj.getKind());
            chat.setProduct(extraObj);
            WebSocket.getInstance()
                    .sendMessage(chat, new SendMsgListener(chat));
        }
    }


    public void sendLocation(String title, String address, double latitude, double longitude) {
        WSLocation location = new WSLocation(title, address, latitude, longitude);
        WSChat chat = initChat(WSChat.LOCATION);
        chat.setExtContent(new WSExtObject(location));
        WebSocket.getInstance()
                .sendMessage(chat, new SendMsgListener(chat));
    }

    protected void initContentEditText(EditText editText) {
        this.etContent = editText;
        String draftContent = WSRealmHelper.readChatDraft(selfUser.getId(), sessionUser.getId());
        if (!TextUtils.isEmpty(draftContent)) {
            editText.setText(draftContent);
        }
        //输入状态变化监听
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onUserWriting();
            }
        });
    }

    protected void initLoad() {
        initRxEventBus();
        initLoadMessages();
    }

    private void initRxEventBus() {
        if (messageRxSub == null || messageRxSub.isUnsubscribed()) {
            messageRxSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            if (loadMessageSub != null && !loadMessageSub.isUnsubscribed()) {
                                return;
                            }
                            if (rxEvent == null) {
                                return;
                            }
                            switch (rxEvent.getType()) {
                                case WS_MESSAGE:
                                case SEND_MESSAGE:
                                    final WSChat chat = (WSChat) rxEvent.getObject();
                                    if (chat == null || chat.getUserId() != selfUser.getId() ||
                                            (chat.getFromId() != sessionUser.getId() && chat
                                                    .getToId() != sessionUser.getId())) {

                                        return;
                                    }
                                    if (!chat.isCurrentUser()) {
                                        //更新对方信息
                                        wsChatAuthor = chat.getSpeaker();
                                        //收到的消息更新最后时间
                                        lastMessageTime = chat.getCreatedAt()
                                                .getTime();
                                        //收到对方消息代表对方输入结束
                                        onSessionWritingDone();
                                        //标志消息为已读
                                        if (isResumed()) {
                                            WebSocket.getInstance()
                                                    .sendReadMessage(chat.getChannel());
                                        } else {
                                            hasUnreadMessage = true;
                                        }
                                    }
                                    addMessage(chat);
                                    break;
                                case WS_USER_UPDATE:
                                    wsChatAuthor = (WSChatAuthor) rxEvent.getObject();
                                    break;
                            }
                        }
                    });
        }
        if (stateRxSub == null || stateRxSub.isUnsubscribed()) {
            stateRxSub = RxBus.getDefault()
                    .toObservable(ChatRxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<ChatRxEvent>() {
                        @Override
                        protected void onEvent(ChatRxEvent chatRxEvent) {
                            switch (chatRxEvent.getType()) {
                                case WRITING_MESSAGE:
                                    if (chatRxEvent.getObject() != null && chatRxEvent.getObject
                                            () instanceof Long && (Long) chatRxEvent.getObject()
                                            == sessionUser.getId()) {
                                        onSessionWriting();
                                    }
                                    break;
                                case READ_MESSAGE:
                                    if (chatRxEvent.getObject() != null && chatRxEvent.getObject()
                                            .equals(channelId)) {
                                        adapter.onMessageReadChanged();
                                    }
                                    break;
                            }
                        }
                    });
        }
    }

    private void initLoadMessages() {
        if (loadMessageSub == null || loadMessageSub.isUnsubscribed()) {
            loadMessageSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setDataNullable(true)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<List<WSChat>>() {
                        @Override
                        public void onNext(List<WSChat> wsChats) {
                            adapter.setChats(wsChats);
                            chatList.scrollToPosition(adapter.getItemCount() - 1);
                            onInitLoadDone(wsChats);
                        }
                    })
                    .build();

            realm.where(WSChat.class)
                    .equalTo("userId", selfUser.getId())
                    .beginGroup()
                    .equalTo("fromId", sessionUser.getId())
                    .or()
                    .equalTo("toId", sessionUser.getId())
                    .endGroup()
                    .findAllSortedAsync("createdAt", Sort.DESCENDING, "idStr", Sort.DESCENDING)
                    .asObservable()
                    .filter(new Func1<RealmResults<WSChat>, Boolean>() {
                        @Override
                        public Boolean call(RealmResults<WSChat> wsChats) {
                            return wsChats.isLoaded();
                        }
                    })
                    .first()
                    .map(new Func1<RealmResults<WSChat>, List<WSChat>>() {
                        @Override
                        public List<WSChat> call(RealmResults<WSChat> wsChats) {
                            //wsChats 按时间倒序拍列
                            lastMessageTime = 0;
                            List<WSChat> chats = new ArrayList<>();
                            boolean isUnRead = true; //标记未读状态
                            realm.beginTransaction();
                            for (WSChat wsChat : wsChats) {
                                if (!wsChat.isCurrentUser() && lastMessageTime == 0) {
                                    //取第一条收到的消息记录时间
                                    lastMessageTime = wsChat.getCreatedAt()
                                            .getTime();
                                }
                                //插入数组顶部调整成正序
                                chats.add(0, realm.copyFromRealm(wsChat));
                                if (TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(wsChat
                                        .getChannel())) {
                                    channelId = wsChat.getChannel();
                                }
                                if (isUnRead) {
                                    //接收消息或已被标为已读的消息以前的消息都可以标为已读
                                    isUnRead = wsChat.isCurrentUser() && wsChat.isUnRead();
                                } else if (wsChat.isCurrentUser() && wsChat.isUnRead()) {
                                    //在标志变成已读时将以后的未读消息设置成已读
                                    wsChat.setUnRead(false);
                                }
                            }
                            realm.commitTransaction();
                            WebSocket.getInstance()
                                    .sendReadMessage(channelId);
                            return chats;
                        }
                    })
                    .subscribe(loadMessageSub);
        }
    }

    /**
     * 首次加载完成
     */
    protected void onInitLoadDone(List<WSChat> wsChats) {

    }

    public void addMessage(WSChat chat) {
        int index = adapter.addChat(chat);
        if (index > 0 && index == adapter.getItemCount() - 1) {
            chatList.scrollToPosition(index);
        }
        if (!chat.getKind()
                .equals(WSChat.TIPS)) {
            onFilterMessage(chat);
        }
    }

    /**
     * 用户正在输入，判断是否需要发送正在输入消息
     */
    private void onUserWriting() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime > WRITING_TIME) {
            //对方上一条消息已经超过时间间隔判断为不需要通知对方正在输入
            return;
        }
        if (currentTime - sendWritingTime < WRITING_TIME) {
            //距离上次发送时间间隔过短不需要重复发送
            return;
        }
        sendWritingTime = currentTime;
        WebSocket.getInstance()
                .sendWriting(selfUser.getId(), sessionUser.getId());
    }

    /**
     * 对方正在输入
     */
    private void onSessionWriting() {
        titleHandler.removeCallbacks(writingDoneRunnable);
        if (chatFragmentStateListener != null) {
            chatFragmentStateListener.onStateChange(getString(R.string.label_writting___chat));
        }
        titleHandler.postDelayed(writingDoneRunnable, WRITING_TIME);
    }

    /**
     * 对方输入结束
     */
    private void onSessionWritingDone() {
        titleHandler.removeCallbacks(writingDoneRunnable);
        if (chatFragmentStateListener != null) {
            chatFragmentStateListener.onStateChange(getUserName());
        }
    }

    protected String getUserName() {
        return getChatAuthor().getNick();
    }

    private boolean isBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) chatList.getLayoutManager();
        int lastItemPosition = layoutManager.getItemCount() - 1;
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            View lastVisibleChild = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= chatList.getBottom();
            }
        }
        return false;
    }

    protected void onImmShow() {
        showMenu = false;
        menuLayout.setVisibility(View.GONE);
        bottomLayout.requestLayout();
    }

    private void onImmHide() {
        if (showMenu) {
            onMenuShow();
        }
    }

    protected void onMenuShow() {
        menuLayout.setVisibility(View.VISIBLE);
    }

    public void onShowMenu(String tag) {
        boolean isCurrentMenuShow = false;
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            View view = menuLayout.getChildAt(i);
            if (!TextUtils.isEmpty(tag) && tag.equals(view.getTag())) {
                if (view.getVisibility() == View.VISIBLE) {
                    isCurrentMenuShow = true;
                } else
                    view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (menuLayout.getVisibility() == View.GONE && !immIsShow) {
            menuLayout.setVisibility(View.VISIBLE);
        } else if (menuLayout.getVisibility() != View.VISIBLE || isCurrentMenuShow) {
            showMenu = true;
            if (getActivity() != null && getActivity().getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void onVoiceMode() {
        WSChatFragmentPermissionsDispatcher.onRecordAudioWithCheck(this);
    }

    public void onAddImage() {
        Intent intent = new Intent(getContext(), ImageChooserActivity.class);
        intent.putExtra("limit", 1);
        startActivityForResult(intent, CHOOSE_PHOTO_RESULT);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordAudio() {
        onSpeckModeChange();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onRecordPermissionDenied() {
        ToastUtil.showToast(getContext(), null, R.string.msg_recording_permission___cm);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        WSChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFaceItemClickListener(
            AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) parent.getAdapter()
                .getItem(position);
        if (!TextUtils.isEmpty(tag)) {
            if (tag.equals("delete")) {
                EmojiUtil.deleteTextOrImage(etContent);
            } else {
                StringBuilder ss = new StringBuilder(tag);
                int start = etContent.getSelectionStart();
                int end = etContent.getSelectionEnd();
                if (start == end) {
                    etContent.getText()
                            .insert(start, ss);
                } else {
                    etContent.getText()
                            .replace(start, end, ss);
                }
            }
        }
    }

    @Override
    public void resendMessage(
            WSChat item, int type, int position) {
        adapter.removeChat(item);
        item.setSending(true);
        item.setUnRead(true);
        item.setCreatedAt(new Date());
        if (TextUtils.isEmpty(item.getChannel()) && !TextUtils.isEmpty(channelId)) {
            item.setChannel(channelId);
        }
        WebSocket.getInstance()
                .sendMessage(item, new SendMsgListener(item));
    }

    @Override
    public void onTextCopyClick(final String text) {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.label_copy___chat));
        DialogUtil.createListDialog(getContext(), list, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                        .getSystemService(
                        CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name),
                        text));
                ToastUtil.showToast(getContext(), null, R.string.msg_copy_success___chat);
            }
        })
                .show();
    }

    @Override
    public void onProductClick(WSProduct product, int type) {
        switch (type) {
            case ChatAdapter.CHAT_TYPE.WORK:
                if (product.getActualPrice() > 0) {
                    showWork(product.getId());
                } else {
                    showCase(product.getId());
                }
                break;
            case ChatAdapter.CHAT_TYPE.CUSTOM_MEAL:
                showCustomMeal(product.getId());
                break;
            case ChatAdapter.CHAT_TYPE.PRODUCT:
                showProduct(product.getId());
                break;
        }
    }

    @Override
    public void onUserClick(long userId) {
        showUser(userId);
    }

    @Override
    public void onTrackClick(WSTrack wsTrack) {
        try {
            switch (wsTrack.getAction()) {
                case WSTrack.WORK:
                    showWork(wsTrack.getWork()
                            .getId());
                    break;
                case WSTrack.CASE:
                    showCase(wsTrack.getCase()
                            .getId());
                    break;
                case WSTrack.PRODUCT:
                    showProduct(wsTrack.getProduct()
                            .getId());
                    break;
                case WSTrack.MERCHANT:
                case WSTrack.SHOW_WINDOW:
                    gotoMerchant(wsTrack.getMerchant()
                                    .getId(),
                            wsTrack.getMerchant()
                                    .getShopType());
                    break;
                case WSTrack.WEDDING_CAR:
                    showWeddingCar(wsTrack.getCarProduct()
                            .getId());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAppointmentClick(long userId) {
        makeAppointment(userId);
    }

    @Override
    public void onMerchantClick(long merchantId) {
        gotoMerchant(merchantId, 0);
    }

    @Override
    public void onSmartReplayClick(WSChat chat, String text) {
        autoSendSmartReplay(chat, text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(miniUserSub,
                messageRxSub,
                loadMessageSub,
                stateRxSub,
                uploadSubscriptions);
        titleHandler.removeCallbacks(writingDoneRunnable);
        if (etContent != null && sessionUser != null) {
            ChatDraft chatDraft = new ChatDraft(selfUser.getId(),
                    sessionUser.getId(),
                    etContent.getText()
                            .toString());
            if (!TextUtils.isEmpty(etContent.getText())) {
                WSRealmHelper.saveChatDraft(chatDraft);
            }
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.CHAT_DRAFT, chatDraft));
        }

        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CHOOSE_PHOTO_RESULT) {
                ArrayList<Photo> selectedPhotos = data.getParcelableArrayListExtra
                        ("selectedPhotos");
                if (CommonUtil.isCollectionEmpty(selectedPhotos)) {
                    return;
                }
                String path = selectedPhotos.get(0)
                        .getImagePath();
                if (!TextUtils.isEmpty(path)) {
                    sendImage(path);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setChatFragmentStateListener(
            OnChatFragmentStateListener chatFragmentStateListener) {
        this.chatFragmentStateListener = chatFragmentStateListener;
    }

    private class SendMsgListener implements WebSocket.SendMsgCallbackListener {
        private WSChat chat;

        SendMsgListener(WSChat chat) {
            this.chat = chat;
        }

        public void onSendSuccess() {
            if (chatList == null) {
                return;
            }
            //消息发送成功重置正在输入发送时间
            sendWritingTime = 0;
            if (TextUtils.isEmpty(channelId)) {
                channelId = chat.getChannel();
            }
            chatList.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyChatChange(chat);
                }
            });

        }

        public void onSendFailure() {
            if (chatList == null) {
                return;
            }
            chatList.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyChatChange(chat);
                }
            });
        }
    }

    public interface OnChatFragmentStateListener {
        void onSessionUserInfo(User sessionUser);

        void onStateChange(String state);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (miniUserSub != null && miniUserSub.isUnsubscribed() && sessionUser != null) {
            WSRealmHelper.entryChat(getContext(), selfUser.getId(), sessionUser.getId());
        }
        if (hasUnreadMessage) {
            hasUnreadMessage = false;
            if (!TextUtils.isEmpty(channelId)) {
                WebSocket.getInstance()
                        .sendReadMessage(channelId);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (miniUserSub != null && miniUserSub.isUnsubscribed() && sessionUser != null) {
            WSRealmHelper.exitChat(getContext(), selfUser.getId(), sessionUser.getId());
        }
    }
}
