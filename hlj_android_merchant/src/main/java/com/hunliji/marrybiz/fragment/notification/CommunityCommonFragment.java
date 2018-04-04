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
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.hunliji.hljquestionanswer.activities.AnswerCommentListActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.notification.CommunityCommentNotificationAdapter;
import com.hunliji.marrybiz.adapter.notification.OnNotificationClickListener;
import com.hunliji.marrybiz.model.notification.NotificationGroup;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.TextActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * 动态问答非点赞通知
 * Created by wangtao on 2016/11/23.
 */

public class CommunityCommonFragment extends RefreshFragment implements
        OnNotificationClickListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private CommunityCommentNotificationAdapter adapter;
    private ArrayList<Notification> notifications;

    private Realm realm;
    private Unbinder unbinder;
    private long userId;
    private Subscription loadSubscription;
    private Subscription notificationSubscription;
    private Subscription readAllSubscription;


    public static CommunityCommonFragment newInstance() {
        return new CommunityCommonFragment();
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
        adapter = new CommunityCommentNotificationAdapter(getContext(), this);
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
                .not()
                .in("action", CommunityPraiseFragment.COMMUNITY_PRAISE_ACTIONS)
                .findAllSortedAsync("id", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
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
        if (notification.isNew()) {
            realm.beginTransaction();
            notification.setStatus(2);
            realm.commitTransaction();
            adapter.notifyItem(notification);
        }
        NotificationUtil.notificationRoute(getContext(),notification);
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

    public void onReadAll() {
        if (readAllSubscription != null && !readAllSubscription.isUnsubscribed()) {
            return;
        }
        readAllSubscription = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2)
                .in("notifyType", NotificationGroup.COMMUNITY.getIncludeTypes())
                .not()
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
                .subscribe(new Subscriber<RealmResults<Notification>>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyDataSetChanged();
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
