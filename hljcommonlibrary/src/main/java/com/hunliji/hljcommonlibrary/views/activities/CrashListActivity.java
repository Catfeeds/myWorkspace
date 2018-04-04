package com.hunliji.hljcommonlibrary.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.adapters.CrashRecyclerAdapter;
import com.hunliji.hljcommonlibrary.models.realm.CrashInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;


/**
 * Created by wangtao on 2017/1/21.
 */

public class CrashListActivity extends HljBaseActivity {

    View progressBar;
    PullToRefreshVerticalRecyclerView recyclerView;

    private CrashRecyclerAdapter adapter;
    private Realm realm;
    private Subscription loadSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = (PullToRefreshVerticalRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);


        adapter = new CrashRecyclerAdapter();
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);


        realm = Realm.getDefaultInstance();
        loadSubscription = realm.where(CrashInfo.class)
                .findAllSortedAsync("time", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<CrashInfo>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<CrashInfo> crashInfos) {
                        return crashInfos.isLoaded();
                    }
                }).first().subscribe(new Subscriber<RealmResults<CrashInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onBackPressed();
                    }

                    @Override
                    public void onNext(RealmResults<CrashInfo> crashInfos) {
                        progressBar.setVisibility(View.GONE);
                        adapter.setCrashs(crashInfos);
                    }
                });
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscription);
        realm.close();
        super.onFinish();
    }
}
