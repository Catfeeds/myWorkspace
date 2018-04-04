package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.adapter.FundDetailRecyclerAdapter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.FundDetail;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by mo_yu on 2017/11/24.理财明细
 */
public class FundDetailActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private FooterViewHolder footerViewHolder;
    private ArrayList<FundDetail> fundDetails;
    private FundDetailRecyclerAdapter adapter;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

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
        fundDetails = new ArrayList<>();
    }

    private void initView() {
        setOkButton(R.mipmap.icon_question_primary_44_44);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerViewHolder = new FooterViewHolder(footerView);
        recyclerView.setOnRefreshListener(this);
        adapter = new FundDetailRecyclerAdapter(this);
        adapter.setFooterView(footerView);
        adapter.setDataList(fundDetails);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        HljWeb.startWebView(this, HljCard.fundQaUrl);
    }

    private void initLoad() {
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<FundDetail>>>() {

                        @Override
                        public void onNext(HljHttpData<List<FundDetail>> listHljHttpData) {
                            fundDetails.clear();
                            fundDetails.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPage(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .build();
            CustomerCardApi.getFundDetailsObb(1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<FundDetail>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<FundDetail>>>() {
                    @Override
                    public Observable<HljHttpData<List<FundDetail>>> onNextPage(int page) {
                        return CustomerCardApi.getFundDetailsObb(page);
                    }
                })
                .setLoadView(footerViewHolder.loading)
                .setEndView(footerViewHolder.noMoreHint)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<FundDetail>>>() {
                    @Override
                    public void onNext(HljHttpData<List<FundDetail>> hljHttpData) {
                        fundDetails.addAll(hljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        pageObservable.subscribe(pageSubscriber);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    static class FooterViewHolder {
        @BindView(R2.id.no_more_hint)
        TextView noMoreHint;
        @BindView(R2.id.loading)
        LinearLayout loading;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
