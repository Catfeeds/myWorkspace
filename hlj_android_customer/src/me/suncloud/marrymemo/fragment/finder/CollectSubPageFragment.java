package me.suncloud.marrymemo.fragment.finder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
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
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollectSubPageFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<TopicUrl>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View footerView;
    private View endView;
    private View loadView;
    private ObjectBindAdapter<TopicUrl> adapter;
    private ArrayList<TopicUrl> topics;
    private int imageWidth;
    private int imageHeight;
    private int casePadding;
    private int totalCount;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static CollectSubPageFragment newInstance() {
        Bundle args = new Bundle();
        CollectSubPageFragment fragment = new CollectSubPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageWidth = CommonUtil.dp2px(getContext(), 116);
        imageHeight = CommonUtil.dp2px(getContext(), 74);
        casePadding = CommonUtil.dp2px(getContext(), 8);
        topics = new ArrayList<>();
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ObjectBindAdapter<>(getContext(), topics, R.layout.collect_item, this);
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
        emptyView.setHintId(R.string.hint_collect_sub_page_empty);
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_common);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(topics)) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<TopicUrl>>>() {
                        @Override
                        public void onNext(
                                HljHttpData<List<TopicUrl>> listHljHttpData) {
                            topics.clear();
                            topics.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                            totalCount = listHljHttpData.getTotalCount();
                            if (onTabTextChangeListener != null) {
                                onTabTextChangeListener.onTabTextChange(totalCount);
                            }
                        }
                    })
                    .setProgressBar(listView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(listView)
                    .setListView(listView.getRefreshableView())
                    .build();
            FinderApi.getSubPageCollectListObb(1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<TopicUrl>>> observable = PaginationTool.buildPagingObservable(
                listView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<TopicUrl>>>() {
                    @Override
                    public Observable<HljHttpData<List<TopicUrl>>> onNextPage(
                            int page) {
                        return FinderApi.getSubPageCollectListObb(page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<TopicUrl>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<TopicUrl>> listHljHttpData) {
                        topics.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        TopicUrl topic = (TopicUrl) parent.getAdapter()
                .getItem(position);
        if (topic != null && topic.getId() > 0) {
            Intent intent = new Intent(getContext(), SubPageDetailActivity.class);
            intent.putExtra("id", topic.getId());
            intent.putExtra("position", topics.indexOf(topic));
            startActivityForResult(intent, Constants.RequestCode.SUB_PAGE_DETAIL);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.SUB_PAGE_DETAIL:
                    int position = data.getIntExtra("position", -1);
                    if (position == -1 || position >= topics.size() || topics.isEmpty()) {
                        return;
                    }
                    if (data.getBooleanExtra("isFollowing", false)) {
                        return;
                    }
                    topics.remove(position);
                    adapter.notifyDataSetChanged();
                    if (topics.isEmpty()) {
                        emptyView.showEmptyView();
                        listView.setEmptyView(emptyView);
                    }
                    totalCount = totalCount - 1;
                    if (onTabTextChangeListener != null) {
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_describe)
        TextView tvDescribe;
        @BindView(R.id.info_layout)
        LinearLayout infoLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            tvProperty.setVisibility(View.GONE);
            infoLayout.setPadding(0, casePadding, 0, casePadding);
        }
    }

    @Override
    public void setViewValue(View view, TopicUrl topic, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvTitle.setText(topic.getGoodTitle());
        if (TextUtils.isEmpty(topic.getCategoryName())) {
            holder.tvDescribe.setVisibility(View.GONE);
        } else {
            holder.tvDescribe.setVisibility(View.VISIBLE);
            holder.tvDescribe.setText(topic.getCategoryName());
        }
        Glide.with(this)
                .load(ImagePath.buildPath(topic.getListImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(holder.imgCover);
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