package me.suncloud.marrymemo.view.notification;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.NotificationRecyclerAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.message.NotificationGroup;
import me.suncloud.marrymemo.util.NotificationUtil;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.functions.Func1;

public class SystemNotificationActivity extends HljBaseActivity implements
        NotificationRecyclerAdapter.OnNotificationClickListener,
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private ArrayList<Notification> notifications;
    private HljHttpSubscriber loadNotiSub;
    private City city;
    private NotificationRecyclerAdapter adapter;
    private Realm realm;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notification);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(this);
        notifications = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        user = Session.getInstance()
                .getCurrentUser(this);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        adapter = new NotificationRecyclerAdapter(this, this);
        adapter.setFooterView(footerView);
    }

    private void initViews() {
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);
    }

    private void initLoad() {
        loadNotifications();
    }

    private void loadNotifications() {
        CommonUtil.unSubscribeSubs(loadNotiSub);
        loadNotiSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<List<Notification>>() {
                    @Override
                    public void onNext(List<Notification> ns) {
                        notifications.addAll(ns);
                        adapter.setNotifications(notifications);
                    }
                })
                .build();

        getItemNotificationsObb().subscribe(loadNotiSub);
    }

    private Observable<List<Notification>> getItemNotificationsObb() {
        return realm.where(Notification.class)
                .equalTo("userId", user.getId())
                .not()
                .in("notifyType", NotificationGroup.DEFAULT_SYSTEM_NOTICE.getIncludeTypes())
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
                        return notifications;
                    }
                });
    }

    @Override
    public void onItemClick(Notification notification) {
        if (notification.getStatus() != 2) {
            realm.beginTransaction();
            notification.setStatus(2);
            realm.commitTransaction();
            adapter.notifyDataSetChanged();
        }
        NotificationUtil.notificationRoute(this, notification);
    }

    @Override
    public void onGroupClick(Notification notification) {
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        initLoad();
    }
}
