package com.hunliji.marrybiz.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.chat.FastReplyAdapter;
import com.hunliji.marrybiz.api.chat.ChatApi;
import com.hunliji.marrybiz.model.chat.FastReply;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/11/3.
 */

public class FastReplyActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private FastReplyAdapter adapter;
    private View endView;
    private View loadView;

    private Subscription loadSubscription;
    private Subscription pageSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_reply);
        ButterKnife.bind(this);
        initView();
        onRefresh(null);
    }

    private void initView() {
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);

        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        adapter = new FastReplyAdapter(this);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);

        emptyView.setHintId(R.string.hint_fast_reply_empty);

    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm() {
        FastReply reply=adapter.getSelectedItem();
        if(reply!=null){
            Intent intent=getIntent();
            intent.putExtra("fast_reply",reply);
            setResult(RESULT_OK,intent);
        }
        onBackPressed();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (!CommonUtil.isUnsubscribed(loadSubscription)) {
            return;
        }
        loadSubscription = ChatApi.getFastReplies(1)
                .subscribe(HljHttpSubscriber.buildSubscriber(FastReplyActivity.this)
                        .setContentView(recyclerView)
                        .setEmptyView(emptyView)
                        .setPullToRefreshBase(recyclerView)
                        .setProgressBar(recyclerView != null && recyclerView.isRefreshing() ?
                                null : progressBar)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<FastReply>>>() {

                            @Override
                            public void onNext(HljHttpData<List<FastReply>> fastReplyData) {
                                adapter.setReplies(fastReplyData.getData());
                                initPagination(fastReplyData.getPageCount());
                            }
                        })
                        .build());
    }


    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = PaginationTool.buildPagingObservable(recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<FastReply>>>() {
                    @Override
                    public Observable<HljHttpData<List<FastReply>>> onNextPage(int page) {
                        return ChatApi.getFastReplies(page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<FastReply>>>() {
                            @Override
                            public void onNext(HljHttpData<List<FastReply>> listHljHttpData) {
                                btnConfirm.setVisibility(View.VISIBLE);
                                adapter.addReplies(listHljHttpData.getData());
                            }
                        })
                        .build());
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(loadSubscription, pageSubscription);
        super.onFinish();
    }
}
