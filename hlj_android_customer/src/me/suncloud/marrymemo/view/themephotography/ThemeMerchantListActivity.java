package me.suncloud.marrymemo.view.themephotography;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
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
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.GuideMerchantAdapter;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 全部商家列表
 * Created by jinxin on 2016/9/23.
 */

public class ThemeMerchantListActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, GuideMerchantAdapter.OnItemClickListener {
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.tv_title)
    TextView tvTitle;


    private long id;//单元id
    private String url;
    private View footView;
    private View endView;
    private View loadView;

    private LinearLayoutManager layoutManager;
    private ArrayList<Merchant> merchants;
    private GuideMerchantAdapter adapter;

    private HljHttpSubscriber refreshSubscriber;//初始（刷新）加载
    private HljHttpSubscriber pageSubScriber;//分页
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide_list);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();
        initValue();
        initWidget();
        initLoad();
    }

    private void initValue() {
        id = getIntent().getLongExtra("id", 0);
        url = Constants.HttpPath.GET_ALL_THEME_MERCHANT;
        merchants = new ArrayList<>();
    }


    private void initWidget() {
        tvTitle.setText(R.string.label_big_business);
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        endView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        adapter = new GuideMerchantAdapter(this, merchants);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView.setOnRefreshListener(this);
        adapter.setFootView(footView);
        adapter.setOnItemClickListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setBackgroundResource(R.color.colorWhite);
    }

    private void initLoad() {
        onRefresh(recyclerView);
    }

    private void initPagination(int pageCount) {
        if (pageSubScriber != null && !pageSubScriber.isUnsubscribed()) {
            pageSubScriber.unsubscribe();
        }

        Observable<HljHttpData<List<Merchant>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Merchant>>>() {
                    @Override
                    public Observable<HljHttpData<List<Merchant>>> onNextPage(
                            int page) {
                        return ThemeApi.getMerchantList(url, id, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubScriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {

                    @Override
                    public void onNext(HljHttpData<List<Merchant>> data) {
                        merchants.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubScriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setContentView(refreshView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(refreshView)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {

                        @Override
                        public void onNext(
                                HljHttpData<List<Merchant>> data) {
                            initPagination(data.getPageCount());
                            merchants.clear();
                            merchants.addAll(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            ThemeApi.getMerchantList(url, id, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    @OnClick({R.id.back_btn, R.id.msg_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.msg_layout:
                if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
                    Intent intent = new Intent(this, MessageHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (refreshSubscriber != null && refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (pageSubScriber != null && pageSubScriber.isUnsubscribed()) {
            pageSubScriber.unsubscribe();
        }
    }

    @Override
    public void onItemClickListener(
            View view, Merchant merchant, int position) {
        if (merchant == null || merchant.getId() <= 0) {
            return;
        }
        Intent intent = new Intent(this, MerchantDetailActivity.class);
        intent.putExtra("id", merchant.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
