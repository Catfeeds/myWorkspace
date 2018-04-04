package me.suncloud.marrymemo.view.notification;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.models.realm.WSChannel;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.Message;
import com.slider.library.Indicators.CirclePageExIndicator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.message.MessageHomeListAdapter;
import me.suncloud.marrymemo.adpter.message.MsgHeaderFlowAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.EMLastMessage;
import me.suncloud.marrymemo.model.LastMessage;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.WSLastMessage;
import me.suncloud.marrymemo.model.message.NotificationGroup;
import me.suncloud.marrymemo.model.message.NotificationGroupItem;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.NotificationUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.LightUpActivity;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;
import me.suncloud.marrymemo.view.kefu.EMChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.widget.CustomTextView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


@Route(path = RouterPath.IntentPath.Customer.MESSAGE_HOME_ACTIVITY)
public class MessageHomeActivity extends HljBaseActivity implements ChatManager.MessageListener,
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>, MessageHomeListAdapter
                .OnMessageClickListener {


    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    public static final ArrayList<NotificationGroup> GROUP_LIST = new
            ArrayList<NotificationGroup>() {{
        add(NotificationGroup.ORDER);
        add(NotificationGroup.COMMUNITY);
        add(NotificationGroup.GIFT);
        add(NotificationGroup.SIGN);
        add(NotificationGroup.FINANCIAL);
        add(NotificationGroup.EVENT);
        add(NotificationGroup.DEFAULT_SYSTEM_NOTICE);
    }};

    private ArrayList<NotificationGroupItem> groupItems;

    private ArrayList<LastMessage> messages;
    private ArrayList<LastMessage> tempMessages;
    private ArrayList<Support> supports;
    private LongSparseArray<MerchantUser> merchantMap;

    private MessageHomeListAdapter adapter;
    private Realm realm;

    private Subscription msgRxSub;
    private HljHttpSubscriber loadMsgSub;
    private Subscription loadNotiSub;
    private Subscription miniUserSub;
    private Subscription deleteSub;
    private Subscription notificationRxSub;

    private View headerView;
    private HeaderViewHolder headerViewHolder;

    private Handler handler = new Handler();
    private Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    };
    private User user;
    private City city;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_home);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
        initNotifyDialog();
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(this);
        messages = new ArrayList<>();
        tempMessages = new ArrayList<>();
        supports = new ArrayList<>();
        groupItems = new ArrayList<>();
        merchantMap = new LongSparseArray<>();
        adapter = new MessageHomeListAdapter(this, messages);
        adapter.setOnMessageClickListener(this);
        realm = Realm.getDefaultInstance();
        user = Session.getInstance()
                .getCurrentUser(this);

        headerView = LayoutInflater.from(this)
                .inflate(R.layout.message_home_list_header, null, false);
        headerViewHolder = new HeaderViewHolder(headerView);
        adapter.setHeaderView(headerView);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        adapter.setFooterView(footerView);

        SupportUtil.getInstance(this)
                .getSupports(this, new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportsCompleted(List<Support> supports) {
                        super.onSupportsCompleted(supports);
                        MessageHomeActivity.this.supports.addAll(supports);
                    }
                });
        ChatClient.getInstance()
                .chatManager()
                .addMessageListener(this);
        msgRxSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        if (rxEvent == null) {
                            return;
                        }
                        User user = Session.getInstance()
                                .getCurrentUser(MessageHomeActivity.this);
                        if (user == null) {
                            return;
                        }
                        switch (rxEvent.getType()) {
                            case WS_MESSAGE:
                            case SEND_MESSAGE:
                                final WSChat chat = (WSChat) rxEvent.getObject();
                                if (chat == null || chat.getUserId() != user.getId()) {
                                    return;
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        newMessage(chat);
                                    }
                                });
                                break;
                            case CHAT_DRAFT:
                                ChatDraft chatDraft = (ChatDraft) rxEvent.getObject();
                                if (user.getId() == chatDraft.getUserId()) {
                                    newDraft(chatDraft);
                                }
                                break;
                            case SEND_EM_MESSAGE:
                                Message message = (Message) rxEvent.getObject();
                                addNewEMMessage(message);
                                adapter.notifyDataSetChanged();
                                break;
                            case WS_RESET_UNREAD_MESSAGE:
                                if (CommonUtil.isCollectionEmpty(messages)) {
                                    return;
                                }
                                final long chatUserId = (long) rxEvent.getObject();
                                Observable.from(messages)
                                        .filter(new Func1<LastMessage, Boolean>() {
                                            @Override
                                            public Boolean call(LastMessage chat) {
                                                return chat instanceof WSLastMessage && chat
                                                        .getSessionId() == chatUserId;
                                            }
                                        })
                                        .subscribe(new Action1<LastMessage>() {
                                            @Override
                                            public void call(LastMessage chat) {
                                                if (chat.getUnreadSessionCount() > 0) {
                                                    chat.setUnreadSessionCount(0);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                break;
                        }
                    }
                });
        notificationRxSub = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .subscribe(new RxBusSubscriber<RxEvent>() {
                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        switch (rxEvent.getType()) {
                            case NEW_NOTIFICATION:
                                loadNotifications();
                                break;
                        }
                    }
                });
    }

    private void initViews() {
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        requestNotifications();
        loadMessages();
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                "",
                getString(R.string.label_not_open_message_notification),
                getString(R.string.label_soon_open_message_notification),
                0);
        if (dialog != null) {
            dialog.show();
        }
    }

    private void requestNotifications() {
        NotificationUtil notificationUtil = NotificationUtil.getInstance(this);
        notificationUtil.getNewNotifications(user.getId());
    }

    private void loadNotifications() {
        if (loadNotiSub != null && !loadNotiSub.isUnsubscribed()) {
            return;
        }
        loadNotiSub = getNotificationGroupObb().subscribe(new Subscriber<List<NotificationGroupItem>>() {

            @Override
            public void onStart() {
                groupItems.clear();
                super.onStart();
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<NotificationGroupItem> notificationGroupItems) {
                groupItems.addAll(notificationGroupItems);
                headerViewHolder.setViews();
            }
        });
    }

    private void loadMessages() {
        if (loadMsgSub != null && !loadMsgSub.isUnsubscribed()) {
            return;
        }

        loadMsgSub = HljHttpSubscriber.buildSubscriber(this)
                .setPullToRefreshBase(recyclerView)
                .setDataNullable(true)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<List<LastMessage>>() {
                    @Override
                    public void onNext(List<LastMessage> lastMessages) {
                        if (lastMessages.isEmpty()) {
                            adapter.setFooterView(null);
                        }
                        messages.clear();
                        messages.addAll(lastMessages);

                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        Observable.zip(getWSChannelChatsObb(),
                getEMConversationChatsObb(),
                new Func2<List<LastMessage>, List<LastMessage>, List<LastMessage>>() {
                    @Override
                    public List<LastMessage> call(
                            List<LastMessage> chats, List<LastMessage> chats2) {
                        List<LastMessage> zipChats = new ArrayList<>();
                        zipChats.addAll(chats);
                        zipChats.addAll(chats2);
                        return zipChats;
                    }
                })
                .concatMap(new Func1<List<LastMessage>, Observable<LastMessage>>() {
                    @Override
                    public Observable<LastMessage> call(List<LastMessage> lastMessages) {
                        return Observable.concat(Observable.from(tempMessages),
                                Observable.from(lastMessages));
                    }
                })
                .distinct(new Func1<LastMessage, Long>() {
                    @Override
                    public Long call(LastMessage chat) {
                        if (chat instanceof WSLastMessage) {
                            return chat.getSessionId();
                        }
                        return System.currentTimeMillis();
                    }
                })
                .toSortedList(new Func2<LastMessage, LastMessage, Integer>() {
                    @Override
                    public Integer call(LastMessage chat1, LastMessage chat2) {
                        return chat1.compareTo(chat2);
                    }
                })
                .subscribe(loadMsgSub);
    }

    private Observable<List<NotificationGroupItem>> getNotificationGroupObb() {
        return Observable.from(GROUP_LIST)
                .concatMap(new Func1<NotificationGroup, Observable<NotificationGroupItem>>() {
                    @Override
                    public Observable<NotificationGroupItem> call(
                            final NotificationGroup notificationGroup) {
                        RealmQuery<Notification> realmQuery = realm.where(Notification.class)
                                .equalTo("userId", user.getId());
                        if (notificationGroup.isNot()) {
                            realmQuery = realmQuery.not();
                        }
                        return realmQuery.in("notifyType", notificationGroup.getIncludeTypes())
                                .findAllSortedAsync("id", Sort.DESCENDING)
                                .asObservable()
                                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                                    @Override
                                    public Boolean call(RealmResults<Notification> notifications) {
                                        return notifications.isLoaded();
                                    }
                                })
                                .first()
                                .map(new Func1<RealmResults<Notification>, NotificationGroupItem>
                                        () {
                                    @Override
                                    public NotificationGroupItem call(
                                            RealmResults<Notification> notifications) {
                                        NotificationGroupItem item = new NotificationGroupItem(
                                                notificationGroup);
                                        if (!notifications.isEmpty()) {
                                            item.setLastId(notifications.first()
                                                    .getId());
                                            item.setNewCount((int) notifications.where()
                                                    .notEqualTo("status", 2)
                                                    .count());
                                        }
                                        return item;
                                    }
                                });
                    }
                })
                .toSortedList(new Func2<NotificationGroupItem, NotificationGroupItem, Integer>() {
                    @Override
                    public Integer call(
                            NotificationGroupItem item, NotificationGroupItem item2) {
                        if (item.getNewCount() > item2.getNewCount()) {
                            if (item2.getNewCount() > 0) {
                                if (item.getLastId() > item2.getLastId()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else {
                                return -1;
                            }
                        } else {
                            if (GROUP_LIST.indexOf(item.getGroup()) < GROUP_LIST.indexOf
                                    (item2.getGroup())) {
                                return -1;
                            }
                            return 1;
                        }
                    }
                });
    }

    private Observable<List<LastMessage>> getWSChannelChatsObb() {
        final User user = Session.getInstance()
                .getCurrentUser(this);
        return ChatApi.getChannelsObb(user.getId())
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        throwable.printStackTrace();
                        return false;
                    }
                })
                .concatMap(new Func1<Boolean, Observable<List<LastMessage>>>() {
                    @Override
                    public Observable<List<LastMessage>> call(Boolean b) {
                        return realm.where(WSChannel.class)
                                .equalTo("userId", user.getId())
                                .findAllAsync()
                                .asObservable()
                                .filter(new Func1<RealmResults<WSChannel>, Boolean>() {
                                    @Override
                                    public Boolean call(RealmResults<WSChannel> channels) {
                                        return channels.isLoaded();
                                    }
                                })
                                .first()
                                .concatMap(new Func1<RealmResults<WSChannel>, Observable<?
                                        extends List<LastMessage>>>() {
                                    @Override
                                    public Observable<? extends List<LastMessage>> call
                                            (RealmResults<WSChannel> channels) {
                                        return Observable.from(channels)
                                                .map(new Func1<WSChannel, LastMessage>() {
                                                    @Override
                                                    public LastMessage call(WSChannel channel) {
                                                        return new WSLastMessage(channel);
                                                    }
                                                })
                                                .toList();
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Observable<List<LastMessage>> getEMConversationChatsObb() {
        return Observable.create(new Observable.OnSubscribe<Hashtable<String, Conversation>>() {
            @Override
            public void call(Subscriber<? super Hashtable<String, Conversation>> subscriber) {
                if (ChatClient.getInstance()
                        .isLoggedInBefore()) {
                    Hashtable<String, Conversation> conversations = ChatClient.getInstance()
                            .chatManager()
                            .getAllConversations();
                    subscriber.onNext(conversations);
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        })
                .map(new Func1<Hashtable<String, Conversation>, List<LastMessage>>() {
                    @Override
                    public List<LastMessage> call(
                            Hashtable<String, Conversation> conversations) {
                        ArrayList<LastMessage> messages = new ArrayList<>();
                        if (conversations != null && !conversations.isEmpty()) {
                            Enumeration<Conversation> element = conversations.elements();
                            while (element.hasMoreElements()) {
                                Conversation conversation = element.nextElement();
                                String name = conversation.conversationId();
                                Support support = null;
                                if (supports != null && !supports.isEmpty()) {
                                    for (Support s : supports) {
                                        if (s.getHxIm()
                                                .equals(name)) {
                                            support = s;
                                        }
                                    }
                                }
                                if (conversation.getLastMessage() != null) {
                                    EMLastMessage lastMessage = new EMLastMessage(conversation
                                            .getLastMessage(),
                                            support);
                                    lastMessage.setUnreadSessionCount(conversation
                                            .unreadMessagesCount());
                                    messages.add(lastMessage);
                                }
                            }
                        }
                        return messages;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onMessage(List<Message> list) {
        if (CommonUtil.isCollectionEmpty(list)) {
            return;
        }
        for (Message message : list) {
            addNewEMMessage(message);
        }
        handler.post(runnableUi);
    }

    @Override
    public void onCmdMessage(List<Message> list) {

    }

    @Override
    public void onMessageStatusUpdate() {

    }

    @Override
    public void onMessageSent() {

    }

    public void newMessage(WSChat newMsg) {
        WSChannel channel = realm.where(WSChannel.class)
                .equalTo("key",
                        WSChannel.getWSChannelKey(newMsg.getUserId(), newMsg.getSessionId()))
                .findFirst();
        WSLastMessage newChat = new WSLastMessage(realm.copyFromRealm(channel));
        if (loadMsgSub != null && !loadMsgSub.isUnsubscribed()) {
            if (tempMessages.isEmpty()) {
                tempMessages.add(newChat);
            } else {
                LastMessage oldMsg = null;
                for (LastMessage msg : tempMessages) {
                    if (msg.getSessionId() == newChat.getSessionId()) {
                        oldMsg = msg;
                        break;
                    }
                }
                if (oldMsg != null) {
                    tempMessages.remove(oldMsg);
                }
                tempMessages.add(newChat);
            }
        } else {
            if (messages.isEmpty()) {
                messages.add(newChat);
            } else {
                LastMessage oldMsg = null;
                for (LastMessage msg : messages) {
                    if (msg.getSessionId() == newChat.getSessionId()) {
                        oldMsg = msg;
                        break;
                    }
                }
                if (oldMsg != null) {
                    messages.remove(oldMsg);
                }
                messages.add(0, newChat);
            }
        }
        handler.post(runnableUi);
    }

    private void newDraft(ChatDraft draft) {
        for (LastMessage msg : messages) {
            if (msg instanceof WSLastMessage) {
                if (draft.getSessionId() == msg.getSessionId()) {
                    if (!TextUtils.isEmpty(draft.getContent())) {
                        if (!draft.getContent()
                                .equals(msg.getDraftContent())) {
                            msg.setDraftContent(draft.getContent());
                            if (messages.indexOf(msg) > 0) {
                                messages.remove(msg);
                                messages.add(0, msg);
                            }
                            handler.post(runnableUi);
                        }
                    } else {
                        msg.clearDraft();
                        handler.post(runnableUi);
                    }
                    break;
                }
            }
        }
    }

    private void addNewEMMessage(Message message) {
        String username = message.getUserName();
        Support support = null;
        if (supports != null && !supports.isEmpty()) {
            for (Support s : supports) {
                if (username.equals(s.getHxIm())) {
                    support = s;
                }
            }
        }
        EMLastMessage lastMessage;
        if (support == null) {
            lastMessage = new EMLastMessage(message);
        } else {
            lastMessage = new EMLastMessage(message, support);
        }
        lastMessage.setUnreadSessionCount(ChatClient.getInstance()
                .chatManager()
                .getConversation(username)
                .unreadMessagesCount());
        if (messages.isEmpty()) {
            messages.add(lastMessage);
        } else {
            LastMessage oldMsg = null;
            for (LastMessage msg : messages) {
                if (msg instanceof EMLastMessage) {
                    if (((EMLastMessage) msg).getUserName()
                            .equals(username)) {
                        oldMsg = msg;
                        break;
                    }
                }
            }
            if (oldMsg != null) {
                messages.remove(oldMsg);
            }
            messages.add(0, lastMessage);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        requestNotifications();
        initLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        WebSocket.getInstance()
                .socketConnect(this);
        requestNotifications();
    }

    @Override
    protected void onFinish() {
        ChatClient.getInstance()
                .chatManager()
                .removeMessageListener(this);
        if (realm != null) {
            realm.close();
        }
        CommonUtil.unSubscribeSubs(loadMsgSub,
                msgRxSub,
                deleteSub,
                loadNotiSub,
                miniUserSub,
                notificationRxSub);
        super.onFinish();
    }

    @Override
    public void onItemClick(LastMessage lastMessage, int position) {
        if (lastMessage != null) {
            if (lastMessage instanceof WSLastMessage) {
                Intent intent = new Intent(this, WSCustomerChatActivity.class);
                MerchantUser user = new MerchantUser();
                user.setAvatar(lastMessage.getSessionAvatar());
                user.setId(lastMessage.getSessionId());
                user.setNick(lastMessage.getSessionNick());
                try {
                    MerchantUser merchant = merchantMap.get(lastMessage.getSessionId());
                    if (merchant != null) {
                        user.setMerchantId(merchant.getMerchantId());
                        user.setShopType(merchant.getShopType());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra("user", user);
                intent.putExtra("channelId", ((WSLastMessage) lastMessage).getChannel());
                startActivity(intent);
            } else if (lastMessage instanceof EMLastMessage) {
                Intent intent = new Intent(this, EMChatActivity.class);
                if (supports != null && !supports.isEmpty()) {
                    for (Support support : supports) {
                        if (support.getHxIm()
                                .equals(((EMLastMessage) lastMessage).getUserName())) {
                            if (support.getKind() == Support.SUPPORT_KIND_ADVISER) {
                                intent = new Intent(this, AdvHelperActivity.class);
                            }
                            intent.putExtra("support", support);
                            break;
                        }
                    }
                }
                intent.putExtra("name", ((EMLastMessage) lastMessage).getUserName());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemLongClick(LastMessage lastMessage, int position) {
        if (lastMessage != null) {
            showClearDialog(lastMessage);
        }
    }

    private void showClearDialog(final LastMessage lastMessage) {
        if (deleteSub != null && !deleteSub.isUnsubscribed()) {
            return;
        }
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.label_detele_msg),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lastMessage instanceof WSLastMessage) {
                            String channel = ((WSLastMessage) lastMessage).getChannel();
                            if (!TextUtils.isEmpty(channel)) {
                                if (progressDialog == null) {
                                    progressDialog = DialogUtil.createProgressDialog(
                                            MessageHomeActivity.this);
                                }
                                deleteSub = ChatApi.deleteChannel(channel)
                                        .subscribe(HljHttpSubscriber.buildSubscriber(
                                                MessageHomeActivity.this)
                                                .setProgressDialog(progressDialog)
                                                .setOnNextListener(new SubscriberOnNextListener<Boolean>() {

                                                    @Override
                                                    public void onNext(Boolean aBoolean) {
                                                        if (aBoolean) {
                                                            User user = Session.getInstance()
                                                                    .getCurrentUser(
                                                                            MessageHomeActivity
                                                                                    .this);
                                                            WSRealmHelper.clearChatMessage(user
                                                                            .getId(),
                                                                    lastMessage.getSessionId());
                                                            messages.remove(lastMessage);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            ToastUtil.showToast
                                                                    (MessageHomeActivity.this,
                                                                    null,
                                                                    R.string.msg_channel_delete_error);
                                                        }

                                                    }
                                                })
                                                .build());

                            } else {
                                User user = Session.getInstance()
                                        .getCurrentUser(v.getContext());
                                WSRealmHelper.clearChatMessage(user.getId(),
                                        lastMessage.getSessionId());
                                messages.remove(lastMessage);
                                adapter.notifyDataSetChanged();
                            }

                        } else if (lastMessage instanceof EMLastMessage) {
                            ChatClient.getInstance()
                                    .chatManager()
                                    .clearConversation(((EMLastMessage) lastMessage).getUserName());
                            messages.remove(lastMessage);
                            adapter.notifyDataSetChanged();
                        }
                    }
                },
                null)
                .show();
    }

    @Override
    public void onUserClick(final LastMessage lastMessage, int position) {
        MerchantUser merchant = merchantMap.get(lastMessage.getSessionId());
        if (merchant != null) {
            gotoMerchant(merchant);
        } else {
            if (miniUserSub != null && !miniUserSub.isUnsubscribed()) {
                return;
            }
            miniUserSub = ChatApi.getMiniUser(lastMessage.getSessionId())
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .setProgressDialog(DialogUtil.createProgressDialog(this))
                            .setOnNextListener(new SubscriberOnNextListener<com.hunliji
                                    .hljcommonlibrary.models.User>() {
                                @Override
                                public void onNext(
                                        com.hunliji.hljcommonlibrary.models.User merchant) {
                                    if (merchant != null && merchant instanceof MerchantUser) {
                                        merchantMap.put(lastMessage.getSessionId(),
                                                (MerchantUser) merchant);
                                        gotoMerchant((MerchantUser) merchant);
                                    }

                                }
                            })
                            .build());
        }
    }

    private void gotoMerchant(MerchantUser merchant) {
        if (merchant == null) {
            return;
        }
        try {
            Intent intent;
            if (merchant.getShopType() == 1) {
                intent = new Intent(this, ProductMerchantActivity.class);
            } else {
                intent = new Intent(this, MerchantDetailActivity.class);
            }
            intent.putExtra("id", merchant.getMerchantId());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HeaderViewHolder {
        @BindView(R.id.view_pager)
        ViewPager viewPager;
        @BindView(R.id.flow_indicator)
        CirclePageExIndicator flowIndicator;
        @BindView(R.id.flow_layout)
        RelativeLayout flowLayout;
        @BindView(R.id.img_logo)
        ImageView imgLogo;
        @BindView(R.id.tv_unread_count)
        TextView tvUnreadCount;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_draft_label)
        TextView tvDraftLabel;
        @BindView(R.id.tv_msg)
        CustomTextView tvMsg;
        @BindView(R.id.msg_layout)
        LinearLayout msgLayout;

        ArrayList<NotificationGroupItem> data;
        MsgHeaderFlowAdapter flowAdapter;
        private int selectedPage;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            data = new ArrayList<>();
            msgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goAdv();
                }
            });
        }

        private void setViews() {
            data.clear();
            data.addAll(groupItems);
            flowAdapter = new MsgHeaderFlowAdapter(MessageHomeActivity.this, data);
            viewPager.setAdapter(flowAdapter);
            flowIndicator.setViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(
                        int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    selectedPage = position;

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(selectedPage);
                }
            });
        }

        private void goAdv() {
            if (AuthUtil.loginBindCheck(MessageHomeActivity.this)) {
                if (city == null) {
                    city = Session.getInstance()
                            .getMyCity(MessageHomeActivity.this);
                }
                boolean isOpen = false;
                DataConfig dataConfig = Session.getInstance()
                        .getDataConfig(MessageHomeActivity.this);
                if (city != null && city.getId() > 0 && dataConfig != null && dataConfig
                        .getAdvCids() != null && !dataConfig.getAdvCids()
                        .isEmpty()) {
                    isOpen = dataConfig.getAdvCids()
                            .contains(city.getId());
                }
                Intent intent;
                if (isOpen) {
                    intent = new Intent(MessageHomeActivity.this, AdvHelperActivity.class);
                } else {
                    intent = new Intent(MessageHomeActivity.this, LightUpActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, true);
                    intent.putExtra("type", 3);
                }
                startActivity(intent);
            }
        }
    }


}
