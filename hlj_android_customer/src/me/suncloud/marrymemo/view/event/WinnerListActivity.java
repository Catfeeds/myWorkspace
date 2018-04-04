package me.suncloud.marrymemo.view.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
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
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.event.EventApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 活动中奖名单
 * Created by chen_bin on 2016/8/10 0010.
 */
public class WinnerListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<ListView>, ObjectBindAdapter.ViewBinder<SignUpInfo> {
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View endView;
    private View loadView;
    private ArrayList<SignUpInfo> winners;
    private ObjectBindAdapter<SignUpInfo> adapter;
    private long id;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_list_view___cm);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id", 0);
        winners = new ArrayList<>();
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        listView.getRefreshableView()
                .addFooterView(footerView);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        listView.setOnRefreshListener(this);
        adapter = new ObjectBindAdapter<>(this, winners, R.layout.winner_list_item, this);
        listView.setAdapter(adapter);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<SignUpInfo>>>() {
                        @Override
                        public void onNext(HljHttpData<List<SignUpInfo>> listHljHttpData) {
                            winners.clear();
                            winners.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .build();
            EventApi.getWinnerListObb(id, 1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<SignUpInfo>>> observable = PaginationTool.buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<SignUpInfo>>>() {
                    @Override
                    public Observable<HljHttpData<List<SignUpInfo>>> onNextPage(int page) {
                        return EventApi.getWinnerListObb(id, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<SignUpInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<SignUpInfo>> listHljHttpData) {
                        winners.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    class ViewHolder {
        @BindView(R.id.tv_real_name)
        TextView realName;
        @BindView(R.id.tv_tel)
        TextView tel;
        @BindView(R.id.tv_valid_code)
        TextView validCode;
        @BindView(R.id.line_layout)
        View lineLayout;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    @Override
    public void setViewValue(View view, SignUpInfo signUpInfo, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.lineLayout.setVisibility(position < winners.size() - 1 ? View.VISIBLE : View.GONE);
        holder.realName.setText(signUpInfo.getRealName());
        holder.tel.setText(signUpInfo.getTel());
        holder.validCode.setText(signUpInfo.getValidCode());
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}