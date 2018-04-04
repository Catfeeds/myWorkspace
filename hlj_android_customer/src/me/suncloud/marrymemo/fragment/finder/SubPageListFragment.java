package me.suncloud.marrymemo.fragment.finder;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.subpage.MarkedKeyword;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.SubPageCategoryMarkRecyclerAdapter;
import me.suncloud.marrymemo.adpter.finder.SubPageRecyclerAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.wrappers.HljHttpSubPageCategoryMarksData;
import me.suncloud.marrymemo.model.wrappers.HljHttpTopicsData;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.finder.SelectedSubPageListActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;

/**
 * 专栏列表
 * Created by chen_bin on 2017/3/6 0006.
 */
public class SubPageListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private View headerView;
    private View footerView;
    private View endView;
    private View loadView;

    private LinearLayoutManager layoutManager;
    private SubPageRecyclerAdapter adapter;

    private City city;
    private boolean isShowTopBtn; //回到顶部的按钮是否显示着

    private Unbinder unbinder;

    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    public static SubPageListFragment newInstance() {
        return new SubPageListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = Session.getInstance()
                .getMyCity(getContext());
        headerView = View.inflate(getContext(), R.layout.selected_sub_page_header_item, null);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SubPageRecyclerAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.setOnRefreshListener(this);
        if (getContext() instanceof SelectedSubPageListActivity) {
            adapter.setShowBeginAt(true);
        } else {
            recyclerView.getRefreshableView()
                    .setPadding(0, 0, 0, CommonUtil.dp2px(getContext(), 50));
        }
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (getContext() instanceof MainActivity) {
                            return;
                        }
                        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < 10) {
                            hideFiltrateAnimation();
                        } else {
                            showFiltrateAnimation();
                        }
                    }
                });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(adapter.getTopics())) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            Observable<HljHttpTopicsData> topicsObb = FinderApi.getSubPageListObb(0, 1, 10);
            Observable<HljHttpSubPageCategoryMarksData> categoryMarksObb = getCategoryMarksObb();
            Observable<List<MarkedKeyword>> keywordsObb = getKeywordsObb();
            Observable<ResultZip> observable = Observable.zip(categoryMarksObb,
                    keywordsObb,
                    topicsObb,
                    new Func3<HljHttpSubPageCategoryMarksData, List<MarkedKeyword>,
                            HljHttpTopicsData, ResultZip>() {

                        @Override
                        public ResultZip call(
                                HljHttpSubPageCategoryMarksData categoryMarksData,
                                List<MarkedKeyword> keywords,
                                HljHttpTopicsData topicsData) {
                            ResultZip resultZip = new ResultZip();
                            resultZip.categoryMarksData = categoryMarksData;
                            resultZip.keywords = keywords;
                            resultZip.topicsData = topicsData;
                            return resultZip;
                        }
                    });
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            setData(resultZip);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                            View.VISIBLE ? null : progressBar)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSub);
        }
    }

    private class ResultZip {
        HljHttpSubPageCategoryMarksData categoryMarksData;
        List<MarkedKeyword> keywords;
        HljHttpTopicsData topicsData;
    }

    private Observable<HljHttpSubPageCategoryMarksData> getCategoryMarksObb() {
        if (getContext() instanceof MainActivity) {
            return Observable.just(null);
        } else {
            return FinderApi.getSubPageCategoryMarksObb();
        }
    }

    private Observable<List<MarkedKeyword>> getKeywordsObb() {
        if (getContext() instanceof MainActivity) {
            return Observable.just(null);
        } else {
            return FinderApi.getMarkedKeywordsObb(3, 3);
        }
    }

    private void setData(ResultZip resultZip) {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        //category mark
        boolean isCategoryMarksEmpty;
        if (resultZip.categoryMarksData == null || resultZip.categoryMarksData.isEmpty()) {
            isCategoryMarksEmpty = true;
            adapter.setHeaderView(null);
        } else {
            isCategoryMarksEmpty = false;
            adapter.setHeaderView(headerView);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) headerView.getTag();
            if (headerViewHolder == null) {
                headerViewHolder = new HeaderViewHolder(headerView);
                headerView.setTag(headerViewHolder);
            }
            headerViewHolder.categoryMarkRecyclerView.scrollToPosition(0);
            headerViewHolder.categoryMarkAdapter.setCategoryMarks(resultZip.categoryMarksData
                    .getData());
        }
        //keywords
        adapter.setKeywords(resultZip.keywords);
        //topic
        int pageCount = 0;
        List<TopicUrl> topics = null;
        if (resultZip.topicsData != null) {
            pageCount = resultZip.topicsData.getPageCount();
            topics = resultZip.topicsData.getData();
        }
        if (isCategoryMarksEmpty && CommonUtil.isCollectionEmpty(topics)) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(GONE);
        }
        adapter.setTopics(topics);
        initPagination(pageCount);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpTopicsData> observable = PaginationTool.buildPagingObservable
                (recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpTopicsData>() {
                    @Override
                    public Observable<HljHttpTopicsData> onNextPage(int page) {
                        return FinderApi.getSubPageListObb(0, page, 10);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpTopicsData>() {
                    @Override
                    public void onNext(HljHttpTopicsData listHljHttpData) {
                        adapter.addTopics(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    public class HeaderViewHolder {
        @BindView(R.id.category_mark_recycler_view)
        RecyclerView categoryMarkRecyclerView;
        private SubPageCategoryMarkRecyclerAdapter categoryMarkAdapter;

        public HeaderViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            categoryMarkRecyclerView.setFocusable(false);
            categoryMarkRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false));
            categoryMarkRecyclerView.addItemDecoration(new HeaderViewHolder.SpacesItemDecoration(
                    itemView.getContext()));
            categoryMarkAdapter = new SubPageCategoryMarkRecyclerAdapter(itemView.getContext());
            categoryMarkRecyclerView.setAdapter(categoryMarkAdapter);
        }

        private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
            private int space;

            SpacesItemDecoration(Context context) {
                this.space = CommonUtil.dp2px(context, 10);
            }

            @Override
            public void getItemOffsets(
                    Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                outRect.set(position > 0 ? space : 0, 0, 0, 0);
            }
        }
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        if (layoutManager.findFirstVisibleItemPosition() < 5) {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        } else {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null) {
                        recyclerView.getRefreshableView()
                                .smoothScrollToPosition(0);
                    }
                }
            });
        }
    }

    private void showFiltrateAnimation() {
        if (isShowTopBtn) {
            return;
        }
        isShowTopBtn = true;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            btnScrollTop.startAnimation(animation);
            btnScrollTop.setVisibility(View.VISIBLE);
        }
    }

    private void hideFiltrateAnimation() {
        if (!isShowTopBtn) {
            return;
        }
        isShowTopBtn = false;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
        }
    }

    /**
     * 切换城市，重刷数据
     *
     * @param c
     */
    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
        onRefresh(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        }
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(0);
            recyclerView.setRefreshing(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}