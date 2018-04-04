package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.CommunityChoiceListRecyclerAdapter;
import me.suncloud.marrymemo.adpter.community.viewholder.HotCommunityChannelLayoutViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.community.HljHttpCommunityFeedData;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.MainActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2018/3/9.新娘说3.0精选列表
 */
public class CommunityChoiceFeedListFragment extends ScrollAbleFragment {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_back_top)
    ImageButton btnBackTop;
    @BindView(R.id.msg_refresh_social)
    TextView msgRefreshSocial;
    Unbinder unbinder;

    private boolean isHide;
    private boolean isShowNews;
    private int currentDay;
    private City city;
    private View loadView;
    private View endView;
    private Handler mHandler;
    private LinearLayoutManager layoutManager;
    private CommunityChoiceListRecyclerAdapter adapter;

    private HljHttpSubscriber refreshSubscriber;//热门推荐
    private HljHttpSubscriber pageSubscriber;//feed流分页

    public static CommunityChoiceFeedListFragment newInstance() {
        Bundle args = new Bundle();
        CommunityChoiceFeedListFragment fragment = new CommunityChoiceFeedListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        city = Session.getInstance()
                .getMyCity(getContext());
        Calendar c = Calendar.getInstance();
        currentDay = c.get(Calendar.DAY_OF_YEAR);
        int historyDay = SPUtils.getInt(getContext(), Constants.PREF_COMMUNITY_DAY, 0);
        isShowNews = (currentDay != historyDay);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_choice_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initValue();
        initView();
        initTracker();
        refresh();
    }

    private void initValue() {
        mHandler = new Handler();
    }

    private void initView() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                refresh();
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                refresh();
            }
        });
        View footView = View.inflate(getContext(), R.layout.hlj_foot_no_more___qa, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //热门推荐列表
        adapter = new CommunityChoiceListRecyclerAdapter(getContext());
        adapter.setFooterView(footView);
        adapter.setOnChannelClickListener(new HotCommunityChannelLayoutViewHolder
                .OnChannelClickListener() {
            @Override
            public void onChannelClick() {
                if (getParentFragment() instanceof CommunityChoiceHomeFragment) {
                    CommunityChoiceHomeFragment communityChoiceHomeFragment =
                            (CommunityChoiceHomeFragment) getParentFragment();
                    communityChoiceHomeFragment.onChangeTabToChannel();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(
                    RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager != null && layoutManager.findFirstVisibleItemPosition() < 10) {
                    if (!isHide) {
                        if (getParentFragment() instanceof CommunityChoiceHomeFragment) {
                            CommunityChoiceHomeFragment communityChoiceHomeFragment =
                                    (CommunityChoiceHomeFragment) getParentFragment();
                            communityChoiceHomeFragment.showToolbar();
                        }
                        hideFiltrateAnimation();
                    }
                } else if (isHide) {
                    if (btnBackTop.getVisibility() == View.GONE) {
                        btnBackTop.setVisibility(View.VISIBLE);
                    }
                    if (getParentFragment() instanceof CommunityChoiceHomeFragment) {
                        CommunityChoiceHomeFragment communityChoiceHomeFragment =
                                (CommunityChoiceHomeFragment) getParentFragment();
                        communityChoiceHomeFragment.hideToolbar();
                    }
                    showFiltrateAnimation();
                }
            }
        });
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "community_feed_list?tab=choice");
    }

    //刷新签到按钮状态
    public void refreshPointView() {
        if (getParentFragment() instanceof CommunityChoiceHomeFragment) {
            CommunityChoiceHomeFragment communityChoiceHomeFragment =
                    (CommunityChoiceHomeFragment) getParentFragment();
            if (!isHide) {
                communityChoiceHomeFragment.hideToolbar();
            } else {
                communityChoiceHomeFragment.showToolbar();
            }
        }
    }

    @Override
    public void refresh(Object... params) {
        scrollTop();
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpCommunityFeedData<List<CommunityFeed>>> observable = CommunityApi
                    .getCommunityChoiceListObb(
                    1,
                    HljCommon.PER_PAGE);

            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpCommunityFeedData<List<CommunityFeed>>>() {
                        @Override
                        public void onNext(
                                HljHttpCommunityFeedData<List<CommunityFeed>> listHljHttpData) {
                            if (isShowNews) {
                                msgRefreshSocial.setText(getString(R.string
                                                .msg_refresh_recommend_tip,

                                        99));
                                SPUtils.put(getContext(), Constants.PREF_COMMUNITY_DAY, currentDay);
                                if (getActivity() instanceof MainActivity) {
                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.setCommunityNews();
                                }
                                msgRefreshSocial.setVisibility(View.VISIBLE);
                                mHandler.removeCallbacks(runnable);
                                mHandler.postDelayed(runnable, 3000);
                                isShowNews = false;
                            }
                            adapter.setCommunityFeedsWithFixList(listHljHttpData.getData(),
                                    listHljHttpData.getFixList());
                            initPage(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .build();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(refreshSubscriber);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && !getActivity().isFinishing())
                msgRefreshSocial.setVisibility(View.GONE);
        }
    };

    private void initPage(int pageCount) {
        Observable<HljHttpCommunityFeedData<List<CommunityFeed>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView,
                pageCount,
                new PagingListener<HljHttpCommunityFeedData<List<CommunityFeed>>>() {
                    @Override
                    public Observable<HljHttpCommunityFeedData<List<CommunityFeed>>> onNextPage
                            (int page) {
                        return CommunityApi.getCommunityChoiceListObb(page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCommunityFeedData<List<CommunityFeed>>>() {
                    @Override
                    public void onNext(HljHttpCommunityFeedData<List<CommunityFeed>> hljHttpData) {
                        adapter.addCommunityFeeds(hljHttpData.getData());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    //滑动到顶部
    public void scrollTop() {
        if (layoutManager == null || recyclerView == null) {
            return;
        }
        if (layoutManager.findFirstVisibleItemPosition() >= 5) {
            recyclerView.scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    private void showFiltrateAnimation() {
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
            btnBackTop.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return (btnBackTop.getAnimation() == null || btnBackTop.getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
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
            btnBackTop.startAnimation(animation);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    @OnClick(R.id.btn_back_top)
    public void onBtnBackTopClicked() {
        scrollTop();
    }

    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        refresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        }
    }
}
