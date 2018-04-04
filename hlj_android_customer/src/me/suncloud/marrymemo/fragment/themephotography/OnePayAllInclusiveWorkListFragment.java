package me.suncloud.marrymemo.fragment.themephotography;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.TopicUrl;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
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
import me.suncloud.marrymemo.adpter.onepayallinclusive.OnePayWorkListAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.api.themephotography.ThemeApi;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 一价全包套餐列表
 * Created by jinxin on 2018/3/2 0002.
 */

public class OnePayAllInclusiveWorkListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener ,OnItemClickListener<Work> {

    public static final String ARG_TAB = "tab";

    @Override
    public String getFragmentPageTagName() {
        return "一价全包页列表";
    }

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private int tab;
    private View footerView;
    private TextView endView;
    private View loadView;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private OnePayWorkListAdapter onePayWorkListAdapter;
    private boolean isShowTopBtn;
    private int scrollPosition;
    private LinearLayoutManager manager;

    public static OnePayAllInclusiveWorkListFragment newInstance(int tab) {
        OnePayAllInclusiveWorkListFragment fragment = new OnePayAllInclusiveWorkListFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARG_TAB, tab);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tab = getArguments().getInt(ARG_TAB);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        endView.setVisibility(View.VISIBLE);
        onePayWorkListAdapter = new OnePayWorkListAdapter(getContext());
        onePayWorkListAdapter.setFooterView(footerView);
        onePayWorkListAdapter.setOnItemClickListener(this);
        scrollPosition = 3;
        manager = new LinearLayoutManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @OnClick(R.id.btn_scroll_top)
    void onScrollToTop() {
        if (manager.findFirstVisibleItemPosition() > 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }
    }

    private void initWidget() {
        recyclerView.setHeaderColor(ContextCompat.getColor(getContext(),R.color.colorWhite));
        recyclerView.setOnRefreshListener(this);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(onePayWorkListAdapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
                            return;
                        }
                        onRecyclerViewScrolled(recyclerView, dx, dy);
                    }
                });
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
    }

    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < scrollPosition) {
            hideFiltrateAnimation();
        } else {
            showFiltrateAnimation();
        }
    }

    //显示回到顶部的按钮
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

    //隐藏回到顶部的按钮
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(refreshSub,pageSub);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        setWork(listHljHttpData);
                    }
                })
                .build();
        ThemeApi.allInOne(tab,1)
                .subscribe(refreshSub);
    }

    private void setWork(HljHttpData<List<Work>> listHljHttpData) {
        if (listHljHttpData == null || listHljHttpData.getData() == null) {
            return;
        }
        onePayWorkListAdapter.setWorks(listHljHttpData.getData());
        initPagination(listHljHttpData.getPageCount());
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return  ThemeApi.allInOne(tab,page)
                                ;
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        onePayWorkListAdapter.addWorks(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onItemClick(int position, Work work) {
        if (work != null) {
            String link = work.getLink();
            if (JSONUtil.isEmpty(link)) {
                Intent intent = new Intent(getContext(), WorkActivity.class);
                intent.putExtra("id", work.getId());
                startActivity(intent);
            } else {
                HljWeb.startWebView(getContext(), link);
            }
        }
    }
}
