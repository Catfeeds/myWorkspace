package com.hunliji.marrybiz.view.notification;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.notification.CommonNotificationAdapter;
import com.hunliji.marrybiz.adapter.notification.OnNotificationClickListener;
import com.hunliji.marrybiz.model.notification.NotificationGroup;
import com.hunliji.marrybiz.util.NotificationUtil;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * 商家通用通知
 * Created by wangtao on 2016/11/22.
 */

public class CommonNotificationActivity extends HljBaseActivity implements
        OnNotificationClickListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private View footerView;

    private CommonNotificationAdapter adapter;
    private ArrayList<Notification> notifications;

    private Realm realm;
    private long userId;
    private Subscription loadSubscription;
    private Subscription notificationSubscription;
    private Subscription workSubscription;
    private Subscription readAllSubscription;

    private NotificationGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);

        initView();
        initData();
        loadNotifications();
    }

    private void initView() {
        setOkText(R.string.label_all_read);
        emptyView.setHintId(R.string.hint_no_notification);
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_message);
        footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);

        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        userId = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        realm = Realm.getDefaultInstance();
        group = (NotificationGroup) getIntent().getSerializableExtra("group");
        switch (group) {
            case ORDER:
                setTitle(R.string.label_notification_group_order);
                break;
            case RESERVATION:
                setTitle(R.string.label_notification_group_reservation);
                break;
            case COUPON:
                setTitle(R.string.label_notification_group_coupon);
                break;
            case EVENT:
                setTitle(R.string.label_notification_group_event);
                break;
            case COMMENT:
                setTitle(R.string.label_notification_group_comment);
                break;
            case INCOME:
                setTitle(R.string.label_notification_group_income);
                break;
            default:
                setTitle(R.string.label_notification_group_other);
                break;
        }

        notifications = new ArrayList<>();
        adapter = new CommonNotificationAdapter(this, this, group);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);

    }

    private void loadNotifications() {
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            return;
        }
        RealmQuery<Notification> realmQuery = realm.where(Notification.class)
                .equalTo("userId", userId);
        if (group.isNot()) {
            realmQuery.not();
        }
        loadSubscription = realmQuery.in("notifyType", group.getIncludeTypes())
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


    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (readAllSubscription != null && !readAllSubscription.isUnsubscribed()) {
            return;
        }
        RealmQuery<Notification> realmQuery = realm.where(Notification.class)
                .equalTo("userId", userId)
                .notEqualTo("status", 2);
        if (group.isNot()) {
            realmQuery.not();
        }
        readAllSubscription = realmQuery.in("notifyType", group.getIncludeTypes())
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return !CommonUtil.isCollectionEmpty(notifications);
                    }
                })
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
        NotificationUtil.getInstance(this)
                .getNewNotifications(userId);
    }

    @Override
    @SuppressWarnings("unchecked")
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
        NotificationUtil.notificationRoute(this,notification);
    }

    @Override
    protected void onResume() {
        if (notificationSubscription == null || notificationSubscription.isUnsubscribed()) {
            notificationSubscription = RxBus.getDefault()
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
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (notificationSubscription != null && !notificationSubscription.isUnsubscribed()) {
            notificationSubscription.unsubscribe();
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscription,
                workSubscription,
                notificationSubscription,
                readAllSubscription);
        realm.close();
        super.onFinish();
    }
}
