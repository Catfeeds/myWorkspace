package com.hunliji.hljlivelibrary.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.suncloud.hljweblibrary.client.HljWebClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SetLiveWebViewService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljlivelibrary.HljLive;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.adapters.SocialLiveAdapter;
import com.hunliji.hljlivelibrary.api.LiveApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/10/24.新娘说-直播
 */

public class SocialLiveFragment extends ScrollAbleFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView> {

    @Override
    public String fragmentPageTrackTagName() {
        return "直播列表";
    }

    public static final String ARG_BOTTOM = "bottom";
    public static final String ARG_MERCHANT_ID = "merchant_id";

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.back_top_btn)
    ImageButton backTopView;
    @BindView(R2.id.web_view)
    WebView webView;
    private Unbinder unbinder;

    private LinearLayoutManager layoutManager;
    private SocialLiveAdapter adapter;
    private View endView;
    private View loadView;

    private boolean isHide;
    private Handler mHandler;
    private ArrayList<LiveChannel> liveChannels;
    private long merchantId;
    private int bottom;

    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//刷新


    public SocialLiveFragment() {
    }

    /**
     * @param merchantId 商家id
     * @param bottom 底部额外间距
     * @return
     */
    public static SocialLiveFragment newInstance(long merchantId, int bottom) {
        Bundle args = new Bundle();
        args.putInt(ARG_BOTTOM, bottom);
        args.putLong(ARG_MERCHANT_ID, merchantId);
        SocialLiveFragment fragment = new SocialLiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    private void initValue() {
        mHandler = new Handler();
        liveChannels = new ArrayList<>();
        if (getArguments() != null) {
            merchantId = getArguments().getLong(ARG_MERCHANT_ID);
            bottom = getArguments().getInt(ARG_BOTTOM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_social_live___live, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initView();
        initLoad();
        return rootView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        //网络异常,可点击屏幕重新加载
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                recyclerView.setRefreshing(true);
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                recyclerView.setRefreshing(true);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new SocialLiveAdapter(getActivity(), liveChannels);

        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);

        adapter.setFooterView(footerView);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(
                            RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManager != null && layoutManager.findFirstVisibleItemPosition()
                                < 5) {
                            if (!isHide) {
                                hideFiltrateAnimation();
                            }
                        } else if (isHide) {
                            if (backTopView.getVisibility() == View.GONE) {
                                backTopView.setVisibility(View.VISIBLE);
                            }
                            showFiltrateAnimation();
                        }
                    }
                });
        backTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollTop();
            }
        });
        if (bottom > 0) {
            footerView.setPadding(0, 0, 0, bottom);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    backTopView.getLayoutParams();
            layoutParams.bottomMargin += bottom;
        }

        webView.getSettings()
                .setJavaScriptEnabled(true);
        webView.getSettings()
                .setAllowFileAccess(true);
        //        wenbview缓存
        webView.getSettings()
                .setDomStorageEnabled(true);
        webView.getSettings()
                .setAppCachePath(getContext().getCacheDir()
                        .getAbsolutePath());
        webView.getSettings()
                .setAppCacheEnabled(true);
        if (HljCommon.debug) {
            webView.getSettings()
                    .setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings()
                    .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new HljWebClient(getContext()) {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void initLoad() {
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .setPullToRefreshBase(recyclerView)
                    .setDataNullable(merchantId > 0)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<LiveChannel>>>() {

                        @Override
                        public void onNext(
                                HljHttpCountData<List<LiveChannel>> listHljHttpData) {
                            if (listHljHttpData != null && listHljHttpData.getData()
                                    .size() > 0) {
                                if (merchantId > 0) {
                                    webView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                                initPage(listHljHttpData.getPageCount());
                                liveChannels.clear();
                                liveChannels.addAll(listHljHttpData.getData());
                                adapter.notifyDataSetChanged();
                                if (liveChannels.size() > 0) {
                                    getActivity().getSharedPreferences(HljLive.PREF_FILE,
                                            Context.MODE_PRIVATE)
                                            .edit()
                                            .putLong("last_channel_id",
                                                    listHljHttpData.getMaxChannelId())
                                            .apply();
                                }
                            } else {
                                if (merchantId > 0) {
                                    recyclerView.setVisibility(View.GONE);
                                    webView.setVisibility(View.VISIBLE);
                                    // 使用Name索引寻找ARouter中已注册的对应服务
                                    SetLiveWebViewService setLiveWebViewService =
                                            (SetLiveWebViewService) ARouter.getInstance()
                                            .build(RouterPath.ServicePath.SET_LIVE_WEB_VIEW)
                                            .navigation();
                                    if (setLiveWebViewService != null) {
                                        setLiveWebViewService.setLiveWebView(getActivity(),
                                                webView);
                                    }
                                }
                            }
                        }
                    })
                    .build();
            LiveApi.getLiveChannelListObb(1, merchantId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpCountData<List<LiveChannel>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpCountData<List<LiveChannel>>>() {
                    @Override
                    public Observable<HljHttpCountData<List<LiveChannel>>> onNextPage(
                            int page) {
                        Log.d("pagination tool", "on load: " + page);
                        return LiveApi.getLiveChannelListObb(page, merchantId);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<LiveChannel>>>() {
                    @Override
                    public void onNext(HljHttpData<List<LiveChannel>> data) {
                        liveChannels.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                        //                        adapter.setData(liveChannels);
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }


    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    public void scrollTop() {
        if (layoutManager == null) {
            return;
        }
        if (layoutManager.findFirstVisibleItemPosition() >= 5) {
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

    @Override
    public void onDestroy() {
        webView.destroy();
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
    }

    private void showFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopView != null && (backTopView.getAnimation() == null || backTopView
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }
}
