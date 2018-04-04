package com.hunliji.hljdebuglibrary.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.realm.HttpLogBlock;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;
import com.hunliji.hljdebuglibrary.adapters.HttpLogsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

@Route(path = RouterPath.IntentPath.Debug.HTTP_LOG_LIST)
public class HttpLogActivity extends HljBaseActivity {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private HttpLogsAdapter adapter;
    private List<HttpLogBlock> blockList;
    private Subscription loadSub;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_log);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoads();
    }

    private void initValues() {
        blockList = new ArrayList<>();
        adapter = new HttpLogsAdapter(this, blockList);
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setOkText("Clear");
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        realm.where(HttpLogBlock.class)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
        initLoads();
    }

    private void initLoads() {
        blockList.clear();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);

        realm = Realm.getDefaultInstance();
        loadSub = realm.where(HttpLogBlock.class)
                .findAllSortedAsync("id", Sort.DESCENDING)
                .asObservable()
                .filter(new Func1<RealmResults<HttpLogBlock>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<HttpLogBlock> httpLogBlocks) {
                        return httpLogBlocks.isLoaded();
                    }
                })
                .first()
                .subscribe(new Subscriber<RealmResults<HttpLogBlock>>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.showEmptyView();
                    }

                    @Override
                    public void onNext(RealmResults<HttpLogBlock> httpLogBlocks) {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.hideEmptyView();
                        blockList.clear();
                        blockList.addAll(httpLogBlocks);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        realm.close();
        CommonUtil.unSubscribeSubs(loadSub);
    }
}
