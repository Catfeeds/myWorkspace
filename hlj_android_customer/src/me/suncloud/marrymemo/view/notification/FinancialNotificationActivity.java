package me.suncloud.marrymemo.view.notification;

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
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.NotificationRecyclerAdapter;
import me.suncloud.marrymemo.util.NotificationUtil;
import me.suncloud.marrymemo.util.Session;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Suncloud on 2016/9/7.
 */
public class FinancialNotificationActivity extends HljBaseActivity implements
        NotificationRecyclerAdapter.OnNotificationClickListener, PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private NotificationRecyclerAdapter adapter;
    private ArrayList<Notification> notifications;

    private Realm realm;
    private long userId;
    private Subscription loadSubscription;
    private Subscription notificationSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userId = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        emptyView.setVisibility(View.GONE);

        realm = Realm.getDefaultInstance();
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        notifications = new ArrayList<>();
        adapter = new NotificationRecyclerAdapter(this, this);
        adapter.setFooterView(footerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);

        progressBar.setVisibility(View.VISIBLE);
        loadNotifications();
    }

    private void loadNotifications() {
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            return;
        }
        loadSubscription = realm.where(Notification.class)
                .equalTo("userId", userId)
                .equalTo("notifyType", Notification.NotificationType.FINANCIAL)
                .findAllSortedAsync("id", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<Notification>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Notification> notifications) {
                        return notifications.isLoaded();
                    }
                })
                .first()
                .map(new Func1<RealmResults<Notification>, List<Notification>>() {
                    @Override
                    public List<Notification> call(RealmResults<Notification> notifications) {
                        realm.beginTransaction();
                        for (Notification notification : notifications) {
                            notification.setStatus(2);
                        }
                        realm.commitTransaction();
                        return notifications;
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
                        progressBar.setVisibility(View.GONE);
                        recyclerView.onRefreshComplete();
                        adapter.setNotifications(notifications);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.onRefreshComplete();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Notification> notificationList) {
                        notifications.addAll(notificationList);
                    }
                });
    }

    @Override
    public void onItemClick(Notification notification) {
        NotificationUtil.notificationRoute(this, notification);
    }

    @Override
    public void onGroupClick(Notification notification) {}

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            return;
        }
        NotificationUtil.getInstance(this)
                .getNewNotifications(userId);
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
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            loadSubscription.unsubscribe();
        }
        realm.close();
        super.onFinish();
    }
}
