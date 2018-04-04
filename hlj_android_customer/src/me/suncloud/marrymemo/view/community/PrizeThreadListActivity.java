package me.suncloud.marrymemo.view.community;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.RichThreadRecyclerAdapter;
import me.suncloud.marrymemo.api.community.CommunityApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/9/26.有奖话题列表
 */

public class PrizeThreadListActivity extends HljBaseActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View footView;
    private View endView;
    private View loadView;

    private ArrayList<CommunityThread> prizeCommunityThreadList;
    private LinearLayoutManager layoutManager;
    private RichThreadRecyclerAdapter adapter;

    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//初始加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);

        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        prizeCommunityThreadList = new ArrayList<>();
    }

    private void initView() {
        footView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm,
                null);
        endView = footView.findViewById(R.id
                .no_more_hint);
        loadView = footView.findViewById(R.id
                .loading);

        adapter = new RichThreadRecyclerAdapter(this, prizeCommunityThreadList);
        adapter.setFooterView(footView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setPullToRefreshBase(refreshView)
                    .setEmptyView(emptyView)
                    .setContentView(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null :
                            progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<CommunityThread>> data) {
                            initPageLoad(data.getPageCount());
                            prizeCommunityThreadList.clear();
                            prizeCommunityThreadList.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();

            CommunityApi.getPrizeThreadListObb(1,20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);

        }
    }

    private void initPageLoad(int pageCount) {
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        Observable<HljHttpData<List<CommunityThread>>> pageObservable =
                PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityThread>>>
                    onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return CommunityApi.getPrizeThreadListObb(page,20);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CommunityThread>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<CommunityThread>> data) {
                        prizeCommunityThreadList.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
    }
}
