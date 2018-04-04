package com.hunliji.marrybiz.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljchatlibrary.api.ChatApi;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.ChatDraft;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.models.realm.WSChannel;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.adapter.notification.NotificationGroupAdapter;
import com.hunliji.marrybiz.model.EMLastMessage;
import com.hunliji.marrybiz.model.LastMessage;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.WSLastMessage;
import com.hunliji.marrybiz.model.notification.NotificationGroup;
import com.hunliji.marrybiz.model.notification.NotificationGroupItem;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.view.kefu.EMChatActivity;
import com.hunliji.marrybiz.widget.CustomTextView;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.Message;
import com.makeramen.rounded.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.hunliji.marrybiz.model.notification.NotificationGroup.COMMENT;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.COMMUNITY;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.COUPON;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.EVENT;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.INCOME;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.ORDER;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.OTHER;
import static com.hunliji.marrybiz.model.notification.NotificationGroup.RESERVATION;


/**
 * Created by Suncloud on 2014/10/27.
 */
public class MessageFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<LastMessage>, AdapterView.OnItemLongClickListener, AdapterView
        .OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>, ChatManager
        .MessageListener {

    private ObjectBindAdapter<LastMessage> adapter;
    private ArrayList<LastMessage> messages;
    private ArrayList<LastMessage> tempMessages;
    private View footerView;
    private View progressBar;
    private PullToRefreshListView listView;
    private HljEmptyView emptyView;

    private MerchantUser user;
    private int faceSize;
    private int maxWidth;
    private SimpleDateFormat dateFormat;
    private ArrayList<Support> supports;

    private HeaderViewHolder headerViewHolder;

    private Subscription chatLoadSubscription;
    private Subscription deleteSubscription;
    private Subscription rxSubscription;
    private Subscription stickSubscription;
    private Subscription sortSubscription;
    private Subscription notificationSubscription;
    private Realm realm;

    private NotificationGroup[] notificationGroups = {ORDER, RESERVATION, COUPON, EVENT,
            COMMUNITY, COMMENT, INCOME, OTHER};
    private NotificationGroup[] carNotificationGroups = {ORDER, RESERVATION, COMMENT, OTHER};


    private void loadNotificationGroup() {
        if (notificationSubscription != null && !notificationSubscription.isUnsubscribed()) {
            return;
        }
        notificationSubscription = Observable.from((user != null && user.getShopType() ==
                MerchantUser.SHOP_TYPE_CAR) ? carNotificationGroups : notificationGroups)
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
                                    public Boolean call(
                                            RealmResults<Notification> notifications) {
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
                                        if (notifications.size() > 0) {
                                            item.setLastDate(notifications.first()
                                                    .getCreatedAt());
                                            item.setNewsCount((int) notifications.where()
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
                            NotificationGroupItem item1, NotificationGroupItem item2) {
                        if (item1.getNewsCount() == 0 && item2.getNewsCount() == 0) {
                            return 0;
                        } else if (item1.getNewsCount() > 0 && item2.getNewsCount() == 0) {
                            return -1;
                        } else if (item2.getNewsCount() > 0 && item1.getNewsCount() == 0) {
                            return 1;
                        } else if (item2.getLastDate() == null) {
                            return -1;
                        } else if (item1.getLastDate() == null) {
                            return 1;
                        }
                        return item2.getLastDate()
                                .compareTo(item1.getLastDate());
                    }
                })
                .subscribe(new Subscriber<List<NotificationGroupItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<NotificationGroupItem> notificationGroupItems) {
                        if (headerViewHolder != null) {
                            headerViewHolder.setGroupItems(notificationGroupItems);
                        }
                    }
                });
    }


    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        faceSize = CommonUtil.dp2px(getContext(), 18);
        maxWidth = Math.round(CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px
                (getContext(),
                80));
        supports = new ArrayList<>();
        super.onCreate(savedInstanceState);

        user = Session.getInstance()
                .getCurrentUser(getActivity());

        messages = new ArrayList<>();
        tempMessages = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(),
                messages,
                R.layout.message_list_item,
                this);

        footerView = View.inflate(getContext(), R.layout.list_foot_no_more_2, null);

        SupportUtil.getInstance(getContext())
                .getSupports(getContext(), new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportsCompleted(List<Support> supports) {
                        super.onSupportsCompleted(supports);
                        MessageFragment.this.supports.addAll(supports);
                    }
                });
        ChatClient.getInstance()
                .chatManager()
                .addMessageListener(this);
        if (user.getShopType() == MerchantUser.SHOP_TYPE_SERVICE && user.getIsPro() == Constants
                .MERCHANT_ULTIMATE) {
            if (!SPUtils.getBoolean(getContext(), "potential_update", false)) {
                com.hunliji.marrybiz.util.DialogUtil.createPotentialUpdateDialog(getContext())
                        .show();
                SPUtils.put(getContext(), "potential_update", true);
            }

        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        headerViewHolder = new HeaderViewHolder(rootView);
        HljBaseActivity.setActionBarPadding(getContext(),
                rootView.findViewById(R.id.action_layout_holder));
        progressBar = rootView.findViewById(R.id.progressBar);
        listView = rootView.findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(adapter);
        emptyView = rootView.findViewById(R.id.empty_view);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                progressBar.setVisibility(View.VISIBLE);
                refresh();
            }
        });
        listView.setEmptyView(emptyView);
        emptyView.hideEmptyView();
        if (messages.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            refresh();
        }

        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<RxEvent>() {

                    @Override
                    protected void onEvent(RxEvent rxEvent) {
                        if (rxEvent == null || rxEvent.getType() == null) {
                            return;
                        }
                        switch (rxEvent.getType()) {
                            case WS_MESSAGE:
                            case SEND_MESSAGE:
                                //新消息收到的和在私信界面发送的
                                WSChat chat = (WSChat) rxEvent.getObject();
                                if (chat != null && chat.getUserId() == user.getId()) {
                                    newMessage(chat);
                                }
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
                                notifyDataSortChanged();
                                break;
                            case NEW_NOTIFICATION:
                                loadNotificationGroup();
                                break;
                            case WS_USER_UPDATE:
                                if (CommonUtil.isCollectionEmpty(messages)) {
                                    return;
                                }
                                final WSChatAuthor user = (WSChatAuthor) rxEvent.getObject();
                                Observable.from(messages)
                                        .filter(new Func1<LastMessage, Boolean>() {
                                            @Override
                                            public Boolean call(LastMessage chat) {
                                                return chat instanceof WSLastMessage && chat
                                                        .getSessionId() == user.getId();
                                            }
                                        })
                                        .subscribe(new Action1<LastMessage>() {
                                            @Override
                                            public void call(LastMessage chat) {
                                                ((WSLastMessage) chat).setSession(user);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
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
                                                if (chat.getUnreadMessageCount() > 0 || chat
                                                        .getUnreadTrackCount() > 0) {
                                                    chat.setUnreadMessageCount(0);
                                                    chat.setUnreadTrackCount(0);
                                                    notifyDataSortChanged();
                                                }
                                            }
                                        });
                                break;
                        }
                    }
                });
        return rootView;
    }

    @Override
    public void refresh(Object... params) {
        if (chatLoadSubscription == null || chatLoadSubscription.isUnsubscribed()) {
            WebSocket.getInstance()
                    .socketConnect(getContext());
            chatLoadSubscription = Observable.zip(getWSLastMessagesObb(),
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
                        public Observable<LastMessage> call(List<LastMessage> chats) {
                            return Observable.concat(Observable.from(tempMessages),
                                    Observable.from(chats));
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
                    .subscribe(new Subscriber<List<LastMessage>>() {

                        @Override
                        public void onCompleted() {
                            onLoadDone();
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.WS_MESSAGE, null));
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            onLoadDone();
                        }

                        @Override
                        public void onNext(List<LastMessage> chats) {
                            messages.clear();
                            messages.addAll(chats);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        NotificationUtil.getInstance(getActivity())
                .getNewNotifications(user.getId());
    }

    private void onLoadDone() {
        progressBar.setVisibility(View.GONE);
        listView.onRefreshComplete();
        if (!CommonUtil.isCollectionEmpty(messages) || WebSocket.getInstance()
                .isConnect()) {
            emptyView.hideEmptyView();
        } else {
            emptyView.showNetworkError();
        }
    }

    private Observable<List<LastMessage>> getWSLastMessagesObb() {
        return Observable.concat(Observable.just(WebSocket.getInstance()
                        .isSyncFinish())
                        .filter(new Func1<Boolean, Boolean>() {
                            @Override
                            public Boolean call(Boolean aBoolean) {
                                return aBoolean;
                            }
                        }),
                RxBus.getDefault()
                        .toObservable(RxEvent.class)
                        .filter(new Func1<RxEvent, Boolean>() {
                            @Override
                            public Boolean call(RxEvent rxEvent) {
                                return rxEvent.getType() == RxEvent.RxEventType
                                        .FINISH_SYNC_CHANNELS;
                            }
                        }))
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Func1<Object, Observable<? extends List<LastMessage>>>() {
                    @Override
                    public Observable<? extends List<LastMessage>> call(Object o) {
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
                                    public Observable<? extends List<LastMessage>> call(
                                            final RealmResults<WSChannel> channels) {
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
                });
    }

    private Observable<List<LastMessage>> getEMConversationChatsObb() {
        return Observable.create(new Observable.OnSubscribe<Hashtable<String, Conversation>>() {
            @Override
            public void call(Subscriber<? super Hashtable<String, Conversation>> subscriber) {
                Hashtable<String, Conversation> conversations = ChatClient.getInstance()
                        .chatManager()
                        .getAllConversations();
                subscriber.onNext(conversations);
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
                                    EMLastMessage chat = new EMLastMessage(conversation
                                            .getLastMessage(),
                                            support);
                                    chat.setUnreadMessageCount(conversation.unreadMessagesCount());
                                    messages.add(chat);
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
    public void setViewValue(View view, LastMessage chat, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.lineView = view.findViewById(R.id.divider);
            holder.bottomLineView = view.findViewById(R.id.bottom_divider);
            holder.imageView = view.findViewById(R.id.logo_img);
            holder.nick = view.findViewById(R.id.name);
            holder.content = view.findViewById(R.id.last_msg);
            holder.time = view.findViewById(R.id.time);
            holder.unread = view.findViewById(R.id.unread_count);
            holder.unreadTrack = view.findViewById(R.id.unread_track_view);
            holder.tvDraftLabel = view.findViewById(R.id.tv_draft_label);
            holder.tvCity = view.findViewById(R.id.tv_city);
            holder.vipLogo = view.findViewById(R.id.vip_logo);
            view.setTag(holder);
        }
        holder.vipLogo.setVisibility(chat.getExtend() != null && chat.getExtend()
                .getHljMemberPrivilege() > 0 ? View.VISIBLE : View.GONE);
        holder.lineView.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        holder.bottomLineView.setVisibility(position + 1 < messages.size() ? View.GONE : View
                .VISIBLE);
        if (chat.isStick()) {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(),
                    R.color.colorBackground));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
        }
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(getString(R.string.format_date_type1),
                    Locale.getDefault());
        }
        if (chat.getTime() != null) {
            holder.time.setText(dateFormat.format(chat.getTime()));
        }

        Glide.with(this)
                .load(ImagePath.buildPath(chat.getSessionAvatar())
                        .width(CommonUtil.dp2px(getContext(), 42))
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .error(R.mipmap.icon_avatar_primary)
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(holder.imageView);

        String cityName = null;
        if (user.getShopType() == MerchantUser.SHOP_TYPE_CAR && chat instanceof WSLastMessage) {
            cityName = ((WSLastMessage) chat).getCityName();
        }
        if (TextUtils.isEmpty(cityName)) {
            holder.tvCity.setVisibility(View.GONE);
        } else {
            holder.tvCity.setVisibility(View.VISIBLE);
            holder.tvCity.setText(cityName);
        }
        holder.nick.getLayoutParams().width = 0;
        holder.nick.requestLayout();
        holder.nick.setText(chat.getSessionNick());
        if (!TextUtils.isEmpty(chat.getDraftContent())) {
            holder.tvDraftLabel.setVisibility(View.VISIBLE);
        } else {
            holder.tvDraftLabel.setVisibility(View.GONE);
        }
        holder.content.setImageSpanText(chat.getContent(),
                faceSize,
                ImageSpan.ALIGN_BASELINE,
                maxWidth);
        if (chat.getUnreadMessageCount() > 0) {
            if (chat.getUnreadMessageCount() > 99) {
                holder.unread.setText("99+");
            } else {
                holder.unread.setText(String.valueOf(chat.getUnreadMessageCount()));
            }
            holder.unread.setVisibility(View.VISIBLE);
            holder.unreadTrack.setVisibility(View.GONE);
        } else if (chat.getUnreadTrackCount() > 0) {
            holder.unreadTrack.setVisibility(View.VISIBLE);
            holder.unread.setVisibility(View.GONE);
        } else {
            holder.unreadTrack.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        loadNotificationGroup();
        super.onResume();
        WebSocket.getInstance()
                .socketConnect(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        ChatClient.getInstance()
                .chatManager()
                .removeMessageListener(this);
        CommonUtil.unSubscribeSubs(stickSubscription,
                chatLoadSubscription,
                deleteSubscription,
                rxSubscription,
                notificationSubscription,
                sortSubscription);
        if (realm != null) {
            realm.close();
        }
        super.onDestroyView();
    }

    private void notifyDataSortChanged() {
        CommonUtil.unSubscribeSubs(sortSubscription);
        sortSubscription = Observable.from(messages)
                .toSortedList(new Func2<LastMessage, LastMessage, Integer>() {
                    @Override
                    public Integer call(LastMessage message, LastMessage message2) {
                        return message.compareTo(message2);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .
                        subscribe(new Action1<List<LastMessage>>() {
                            @Override
                            public void call(List<LastMessage> lastMessages) {
                                messages.clear();
                                messages.addAll(lastMessages);
                                adapter.notifyDataSetChanged();
                            }
                        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LastMessage chat = (LastMessage) adapterView.getAdapter()
                .getItem(position);
        if (chat != null) {
            if (chat instanceof WSLastMessage) {
                Intent intent = new Intent(getActivity(), WSMerchantChatActivity.class);
                CustomerUser user = new CustomerUser();
                user.setAvatar(chat.getSessionAvatar());
                user.setId(chat.getSessionId());
                user.setNick(chat.getSessionNick());
                intent.putExtra("user", user);
                intent.putExtra("channelId", ((WSLastMessage) chat).getChannel());
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            } else if (chat instanceof EMLastMessage) {
                Intent intent = new Intent(getActivity(), EMChatActivity.class);
                if (supports != null && !supports.isEmpty()) {
                    for (Support support : supports) {
                        if (support.getHxIm()
                                .equals(((EMLastMessage) chat).getUserName())) {
                            intent.putExtra("support", support);
                            break;
                        }
                    }
                }
                intent.putExtra("name", ((EMLastMessage) chat).getUserName());
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        }
    }

    public void newMessage(WSChat newMsg) {
        WSChannel channel = realm.where(WSChannel.class)
                .equalTo("key",
                        WSChannel.getWSChannelKey(newMsg.getUserId(), newMsg.getSessionId()))
                .findFirst();
        WSLastMessage newChat = new WSLastMessage(realm.copyFromRealm(channel));
        if (chatLoadSubscription != null && !chatLoadSubscription.isUnsubscribed()) {
            if (tempMessages.isEmpty()) {
                tempMessages.add(newChat);
            } else {
                LastMessage oldMsg = null;
                for (LastMessage msg : tempMessages) {
                    if (msg.getSessionId() == newMsg.getSessionId()) {
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
                int index = -1;
                for (LastMessage msg : messages) {
                    if (msg.getSessionId() == newMsg.getSessionId()) {
                        index = messages.indexOf(msg);
                        break;
                    }
                }
                if (index < 0) {
                    messages.add(newChat);
                } else {
                    messages.set(index, newChat);
                }
            }
            notifyDataSortChanged();
        }
    }


    private void newDraft(ChatDraft draft) {
        for (LastMessage msg : messages) {
            if (msg instanceof WSLastMessage) {
                if (draft.getSessionId() == msg.getSessionId()) {
                    if (!TextUtils.isEmpty(draft.getContent())) {
                        if (!draft.getContent()
                                .equals(msg.getDraftContent())) {
                            msg.setDraft(draft);
                            notifyDataSortChanged();
                        }
                    } else {
                        msg.clearDraft();
                        notifyDataSortChanged();
                    }
                    break;
                }
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final LastMessage message = (LastMessage) adapterView.getAdapter()
                .getItem(position);
        if (!(message instanceof WSLastMessage) || TextUtils.isEmpty(((WSLastMessage) message)
                .getChannel())) {
            showClearDialog(message);
            return true;
        }
        List<String> menuItems = new ArrayList<>();
        if (message.isStick()) {
            menuItems.add("取消置顶");
        } else {
            menuItems.add("置顶");
        }
        menuItems.add("删除");
        DialogUtil.createListDialog(getContext(), menuItems, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                final String menuItem = (String) parent.getAdapter()
                        .getItem(position);
                switch (menuItem) {
                    case "取消置顶":
                        message.setStick(false);
                        onMessageStickChange(message);
                        break;
                    case "置顶":
                        message.setStick(true);
                        message.setStickTime(new Date());
                        onMessageStickChange(message);
                        break;
                    case "删除":
                        showClearDialog(message);
                        break;
                }
            }
        })
                .show();
        return true;
    }

    @SuppressWarnings("unchecked")
    private void showClearDialog(final LastMessage chat) {
        if (deleteSubscription != null && !deleteSubscription.isUnsubscribed()) {
            return;
        }
        DialogUtil.createDoubleButtonDialog(getContext(),
                getString(R.string.label_detele_msg),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chat instanceof WSLastMessage) {
                            String channel = ((WSLastMessage) chat).getChannel();
                            if (!TextUtils.isEmpty(channel)) {
                                deleteSubscription = ChatApi.deleteChannel(channel)
                                        .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                                                .setProgressDialog(DialogUtil.createProgressDialog(
                                                        getContext()))
                                                .setOnNextListener(new SubscriberOnNextListener<Boolean>() {

                                                    @Override
                                                    public void onNext(Boolean aBoolean) {
                                                        if (aBoolean) {
                                                            WSRealmHelper.clearChatMessage(user
                                                                            .getUserId(),
                                                                    chat.getSessionId());
                                                            messages.remove(chat);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            ToastUtil.showToast(getContext(),
                                                                    null,
                                                                    R.string.msg_channel_delete_error);
                                                        }

                                                    }
                                                })
                                                .build());

                            } else {
                                WSRealmHelper.clearChatMessage(user.getUserId(),
                                        chat.getSessionId());
                                messages.remove(chat);
                                adapter.notifyDataSetChanged();
                            }

                        } else if (chat instanceof EMLastMessage) {
                            ChatClient.getInstance()
                                    .chatManager()
                                    .clearConversation(((EMLastMessage) chat).getUserName());
                            messages.remove(chat);
                            adapter.notifyDataSetChanged();
                        }
                    }
                },
                null)
                .show();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        refresh();
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
        EMLastMessage lastMessage = new EMLastMessage(message, support);
        lastMessage.setUnreadMessageCount(ChatClient.getInstance()
                .chatManager()
                .getConversation(username)
                .unreadMessagesCount());
        if (messages.isEmpty()) {
            messages.add(lastMessage);
        } else {
            int index = 0;
            for (LastMessage msg : messages) {
                if (msg instanceof EMLastMessage) {
                    if (((EMLastMessage) msg).getUserName()
                            .equals(username)) {
                        index = messages.indexOf(msg);
                        if (msg.isStick()) {
                            lastMessage.setStick(true);
                            lastMessage.setStickTime(msg.getStickTime());
                        }
                        break;
                    }
                }
            }
            messages.add(index, lastMessage);
        }
    }

    @Override
    public void onMessage(List<Message> messages) {
        if (CommonUtil.isCollectionEmpty(messages)) {
            return;
        }
        for (Message message : messages) {
            addNewEMMessage(message);
        }
        notifyDataSortChanged();
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


    private class ViewHolder {
        RoundedImageView imageView;
        TextView nick;
        CustomTextView content;
        TextView time;
        TextView unread;
        View unreadTrack;
        TextView tvDraftLabel;
        View lineView;
        View bottomLineView;
        TextView tvCity;
        ImageView vipLogo;
    }

    public void dblclickMessage() {
        if (listView == null || messages.isEmpty() || listView.getRefreshableView()
                .getAdapter() == null) {
            return;
        }
        ListView refreshableView = listView.getRefreshableView();
        int startIndex = 0;
        if (!isLastItemVisible(refreshableView)) {
            startIndex = Math.max(refreshableView.getFirstVisiblePosition() - refreshableView
                            .getHeaderViewsCount() + 1,
                    startIndex);
        }
        for (int i = 0, size = refreshableView.getChildCount(); i < size; i++) {
            View view = refreshableView.getChildAt(i);
            if (view.getHeight() > 0) {
                if (refreshableView.getChildAt(0)
                        .getBottom() == 0) {
                    startIndex += 1;
                }
                break;
            }
        }
        int scrollPosition = 0;
        for (int i = 0, size = messages.size(); i < size; i++) {
            LastMessage chat;
            if (startIndex + i < size) {
                chat = messages.get(startIndex + i);
            } else {
                chat = messages.get(startIndex + i - size);
            }
            if (chat.getUnreadMessageCount() > 0) {
                scrollPosition = startIndex + i + refreshableView.getHeaderViewsCount();
                break;
            }
        }
        refreshableView.smoothScrollToPositionFromTop(scrollPosition, 0);

    }

    private boolean isLastItemVisible(ListView listView) {
        final int lastItemPosition = listView.getCount() - 1;
        final int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - listView.getFirstVisiblePosition();
            final View lastVisibleChild = listView.getChildAt(childIndex);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= listView.getBottom();
            }
        }
        return false;
    }


    private class HeaderViewHolder {

        RecyclerView rcNotificationGroups;
        NotificationGroupAdapter adapter;

        MerchantUser user;

        private HeaderViewHolder(View rootView) {
            user = Session.getInstance()
                    .getCurrentUser(getContext());

            rcNotificationGroups = rootView.findViewById(R.id.notification_groups_layout);
            rcNotificationGroups.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false));
            rcNotificationGroups.addItemDecoration(new SpacesItemDecoration(getContext()));
            adapter = new NotificationGroupAdapter(getContext());
            rcNotificationGroups.setAdapter(adapter);
        }


        private void setGroupItems(List<NotificationGroupItem> groupItems) {
            adapter.setGroupItems(groupItems);
        }

        private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
            private int space;
            private int space2;

            private SpacesItemDecoration(Context context) {
                this.space = CommonUtil.dp2px(context, 4);
                this.space2 = CommonUtil.dp2px(context, 6);
            }

            @Override
            public void getItemOffsets(
                    Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int left = 0;
                int right = space2;
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    left = space;
                } else if (position == parent.getAdapter()
                        .getItemCount() - 1) {
                    right = space;
                }
                outRect.set(left, 0, right, 0);
            }
        }
    }


    private void onMessageStickChange(LastMessage chat) {
        if (!(chat instanceof WSLastMessage)) {
            return;
        }
        String channel = ((WSLastMessage) chat).getChannel();
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        if (stickSubscription != null && !stickSubscription.isUnsubscribed()) {
            return;
        }
        stickSubscription = ChatApi.stickChannel(channel, user.getUserId(), chat.isStick())
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                        .build());
        if (chat.isStick()) {
            WSRealmHelper.addStick(user.getUserId(), channel, chat.getSessionId());
        } else {
            WSRealmHelper.removeStick(channel);
        }
        notifyDataSortChanged();

    }

}
