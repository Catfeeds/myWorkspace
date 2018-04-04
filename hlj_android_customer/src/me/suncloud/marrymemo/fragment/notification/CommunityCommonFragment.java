package me.suncloud.marrymemo.fragment.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
 * Created by Suncloud on 2016/9/8.
 */
public class CommunityCommonFragment extends RefreshFragment implements
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
    private Unbinder unbinder;
    private long userId;
    private Subscription loadSubscription;
    private Subscription notificationSubscription;

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
        adapter = new NotificationRecyclerAdapter(getContext(), this);
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
                .beginGroup()
                .equalTo("notifyType", Notification.NotificationType.COMMUNITY)
                .or()
                .equalTo("notifyType", Notification.NotificationType.SUB_PAGE)
                .or()
                .equalTo("notifyType", Notification.NotificationType.NOTE_TYPE)
                .endGroup()
                .notEqualTo("action", "post_praise")
                .notEqualTo("action", "plus1")
                .notEqualTo("action", "qa_answer_praise")
                .notEqualTo("action", "qa_praise")
                .notEqualTo("action", Notification.NotificationAction.SUBPAGE_COMMENT_PRAISE)
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
            adapter.setNotifications(notifications);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(Notification notification) {
        NotificationUtil.notificationRoute(getContext(), notification);
    }

    @Override
    public void onGroupClick(Notification notification) {}


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
        if (loadSubscription != null && !loadSubscription.isUnsubscribed()) {
            loadSubscription.unsubscribe();
        }
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
