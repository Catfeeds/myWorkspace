package com.hunliji.marrybiz.fragment.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljquestionanswer.activities.AnswerCommentListActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.notification.CommunityPraiseNotificationAdapter;
import com.hunliji.marrybiz.adapter.notification.OnNotificationClickListener;
import com.hunliji.marrybiz.model.notification.NotificationGroup;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by wangtao on 2016/11/23.
 */

public class CommunityPraiseFragment extends RefreshFragment implements
        OnNotificationClickListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private CommunityPraiseNotificationAdapter adapter;
    private ArrayList<Notification> notifications;

    private Realm realm;
    private Unbinder unbinder;
    private long userId;
    private Subscription loadSubscription;
    private Subscription notificationSubscription;
    private Subscription readAllSubscription;


    public static final String[] COMMUNITY_PRAISE_ACTIONS = {"qa_answer_praise",
            "merchant_feed_praise", "qa_praise"};

    public static CommunityPraiseFragment newInstance() {
        return new CommunityPraiseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        userId = Session.getInstance()
                .getCurrentUser(getContext())
                .getId();
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        notifications = new ArrayList<>();
        adapter = new CommunityPraiseNotificationAdapter(getContext(), this);
        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setHintId(R.string.hint_no_notification);
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);

        progressBar.setVisibility(View.VISIBLE);
        loadNotifications();
        return rootView;
    }

    private void loadNotifications() {
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            return;
        }
        loadSubscription = realm.where(Notification.class)
                .equalTo("userId", userId)
                .in("notifyType", NotificationGroup.COMMUNITY.getIncludeTypes())
                .isNull("mergeId")
                .in("action", COMMUNITY_PRAISE_ACTIONS)
                .findAllSortedAsync("id", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
                .concatMap(new Func1<List<Notification>, Observable<List<Notification>>>() {
                    @Override
                    public Observable<List<Notification>> call(List<Notification> notifications) {
                        if (!CommonUtil.isCollectionEmpty(notifications)) {
                            Map<String, Long> mergeIds = new HashMap<>();
                            realm.beginTransaction();
                            for (Notification notification : notifications) {
                                String key = notification.getAction() + notification
                                        .getParentEntityId();

                                Long mergeId = mergeIds.get(key);
                                if (mergeId == null) {
                                    mergeId = notification.getId();
                                    mergeIds.put(key, mergeId);
                                }
                                notification.setMergeId(mergeId);
                            }
                            realm.commitTransaction();
                        }
                        return loadMergeNotificationsObb();
                    }
                })
                .subscribe(new Subscriber<List<Notification>>() {

                    @Override
                    public void onStart() {
                        notifications.clear();
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        onLoadDone();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadDone();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Notification> notificationList) {
                        notifications.addAll(notificationList);
                    }
                });
    }

    private void onLoadDone() {
        progressBar.setVisibility(View.GONE);
        recyclerView.onRefreshComplete();
        if (notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.showEmptyView();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.setNotifications(notifications);
        }
    }

    private Observable<List<Notification>> loadMergeNotificationsObb() {
        return realm.where(Notification.class)
                .equalTo("userId", userId)
                .in("notifyType", NotificationGroup.COMMUNITY.getIncludeTypes())
                .isNotNull("mergeId")
                .in("action", COMMUNITY_PRAISE_ACTIONS)
                .distinctAsync("mergeId")
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
                .concatMap(new Func1<RealmResults<Notification>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(RealmResults<Notification> notifications) {
                        return Observable.from(notifications)
                                .map(new Func1<Notification, Long>() {
                                    @Override
                                    public Long call(Notification notification) {
                                        return notification.getMergeId();
                                    }
                                });
                    }
                })
                .concatMap(new Func1<Long, Observable<? extends RealmResults<Notification>>>() {
                    @Override
                    public Observable<? extends RealmResults<Notification>> call(Long mergeId) {
                        return realm.where(Notification.class)
                                .equalTo("userId", userId)
                                .in("notifyType", NotificationGroup.COMMUNITY.getIncludeTypes())
                                .equalTo("mergeId", mergeId)
                                .in("action", COMMUNITY_PRAISE_ACTIONS)
                                .findAllSorted("id", Sort.DESCENDING)
                                .asObservable()
                                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                                    @Override
                                    public Boolean call(
                                            RealmResults<Notification> notifications) {
                                        return notifications.isLoaded();
                                    }
                                })
                                .first();
                    }
                })
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return !CommonUtil.isCollectionEmpty(notifications);
                    }
                })
                .map(new Func1<RealmResults<Notification>, Notification>() {
                    @Override
                    public Notification call(RealmResults<Notification> mergeNotifications) {
                        Notification notification = mergeNotifications.first();
                        notification.setMerge(true);
                        notification.setMergeCount(mergeNotifications.size());
                        notification.setMergeNewsCount((int) mergeNotifications.where()
                                .notEqualTo("status", 2)
                                .count());
                        StringBuilder mergeParticipantName = new StringBuilder();
                        int count = 0;
                        for (Notification notificationItem : mergeNotifications) {
                            if (TextUtils.isEmpty(notificationItem.getParticipantName())) {
                                continue;
                            }
                            count++;
                            if (mergeParticipantName.length() > 0) {
                                mergeParticipantName.append(",");
                            }
                            mergeParticipantName.append(notificationItem.getParticipantName());
                            if (count == 3) {
                                break;
                            }
                        }
                        notification.setMergeParticipantName(mergeParticipantName.toString());
                        return notification;
                    }
                })
                .toSortedList(new Func2<Notification, Notification, Integer>() {
                    @Override
                    public Integer call(
                            Notification notification, Notification notification2) {
                        if (notification.getId() > notification2.getId()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            return;
        }
        NotificationUtil.getInstance(getContext())
                .getNewNotifications(userId);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onItemClick(Notification notification) {
        if (notification == null) {
            return;
        }
        onReadAll(notification.getMergeId());
        NotificationUtil.notificationRoute(getContext(), notification);
    }

    @Override
    public void onPause() {
        if (notificationSubscription != null && !notificationSubscription.isUnsubscribed()) {
            notificationSubscription.unsubscribe();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (notificationSubscription == null || notificationSubscription.isUnsubscribed()) {
            notificationSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case NEW_NOTIFICATION:
                                    if ((notifications.isEmpty() || recyclerView.isRefreshing())) {
                                        loadNotifications();
                                    }
                                    break;
                            }
                        }
                    });
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        CommonUtil.unSubscribeSubs(readAllSubscription, loadSubscription, notificationSubscription);
        unbinder.unbind();
        realm.close();
        super.onDestroyView();
    }

    public void onReadAll(final Long mergeId) {
        if (readAllSubscription != null && !readAllSubscription.isUnsubscribed()) {
            return;
        }
        readAllSubscription = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .in("notifyType", NotificationGroup.COMMUNITY.getIncludeTypes())
                .in("action", CommunityPraiseFragment.COMMUNITY_PRAISE_ACTIONS)
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
                .map(new Func1<RealmResults<Notification>, RealmResults<Notification>>() {
                    @Override
                    public RealmResults<Notification> call(RealmResults<Notification>
                                                                   notifications) {
                        if (mergeId != null) {
                            return notifications.where()
                                    .equalTo("mergeId", mergeId)
                                    .findAll();
                        }
                        return notifications;
                    }
                })
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return !CommonUtil.isCollectionEmpty(notifications);
                    }
                })
                .subscribe(new Subscriber<RealmResults<Notification>>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyNotificationRead(mergeId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RealmResults<Notification> notifications) {
                        realm.beginTransaction();
                        for (Notification notification : notifications) {
                            notification.setStatus(2);
                        }
                        realm.commitTransaction();
                    }
                });
    }
}
