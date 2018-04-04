package me.suncloud.marrymemo.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.event.EventApi;
import me.suncloud.marrymemo.view.event.AfterSignUpActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我报名参加的活动
 * Created by chen_bin on 2017/11/6 0006.
 */
public class MyEventListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<ListView>, ObjectBindAdapter.ViewBinder<EventInfo>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View footerView;
    private View endView;
    private View loadView;
    private ObjectBindAdapter<EventInfo> adapter;
    private List<EventInfo> events;
    private int imageWidth;
    private int imageHeight;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static MyEventListFragment newInstance() {
        Bundle args = new Bundle();
        MyEventListFragment fragment = new MyEventListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        imageWidth = CommonUtil.dp2px(getContext(), 116);
        imageHeight = CommonUtil.dp2px(getContext(), 74);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(), events, R.layout.my_event_list_item, this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_list_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(events)) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<EventInfo>> listHljHttpData) {
                            listView.getRefreshableView()
                                    .setSelection(0);
                            events.clear();
                            events.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(listHljHttpData
                                        .getTotalCount());
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setListView(listView.getRefreshableView())
                    .build();
            EventApi.getMyEventListObb(1, HljCommon.PER_PAGE)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<EventInfo>>> observable = PaginationTool.buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public Observable<HljHttpData<List<EventInfo>>> onNextPage(int page) {
                        return EventApi.getMyEventListObb(page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<EventInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<EventInfo>> listHljHttpData) {
                        events.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    class MyEventViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.line_layout)
        View lineLayout;

        MyEventViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void setViewValue(View view, EventInfo eventInfo, int position) {
        MyEventViewHolder holder = (MyEventViewHolder) view.getTag();
        if (holder == null) {
            holder = new MyEventViewHolder(view);
            view.setTag(holder);
        }
        holder.lineLayout.setVisibility(position < events.size() - 1 ? View.VISIBLE : View.GONE);
        Glide.with(this)
                .load(ImagePath.buildPath(eventInfo.getListImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(holder.imgCover);
        if (eventInfo.getWinnerLimit() <= 0) {
            holder.tvStatus.setVisibility(View.GONE);
        } else if (eventInfo.getSignUpInfo()
                .getStatus() >= 2) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.hint_get_winner_done1);
        } else if (eventInfo.isStatus() && eventInfo.getSignUpInfo()
                .getStatus() == 1) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.msg_disable_winner1);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(eventInfo.getTitle());
        holder.tvMerchantName.setText(eventInfo.getMerchant()
                .getId() == 0 ? getString(R.string.app_name) : eventInfo.getMerchant()
                .getName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventInfo eventInfo = (EventInfo) parent.getAdapter()
                .getItem(position);
        if (eventInfo != null && eventInfo.getId() > 0) {
            Intent intent = new Intent(getContext(), AfterSignUpActivity.class);
            intent.putExtra("id", eventInfo.getId());
            startActivity(intent);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listView != null) {
            listView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}
