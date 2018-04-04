package me.suncloud.marrymemo.fragment.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.MerchantListAdapter;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.wrappers.MerchantListData;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.CityListActivity;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2018/3/9
 * 找商家列表
 */

public class MerchantListFragment extends ScrollAbleFragment implements OnItemClickListener {

    public static final String ARG_QUERY = "query";
    public static final String ARG_CITY = "city";
    public static final String ARG_CPM_SOURCE = "cpm_source";
    public static final String ARG_IS_INSTALLMENT = "is_installment";

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView pullToRefreshVerticalRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    private String urlQuery;
    private View footerView;
    private View parentFooterView;
    private FooterViewHolder footerViewHolder;
    private ParentFooterViewHolder parentFooterViewHolder;
    private LinearLayoutManager layoutManager;
    private MerchantListAdapter adapter;
    private ArrayList<Merchant> merchants;
    private ArrayList<Merchant> parentMerchants;
    private ArrayList<Merchant> popularMerchants;
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber parentRefreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private City city;
    private long mParentCid;//上一级城市的id
    private String mParentCity;//上一级城市的名称
    private int pageCount;
    private int currentPage;
    private int parentPageCount;//上一级城市的商家列表页数
    private int parentCurrentPage;//上一级城市的商家列表当前页码
    private boolean isHide;
    private Handler mHandler;
    private RecyclerView recyclerView;
    private String cpmSource;

    private boolean isInstallment; //是否是分期

    public static MerchantListFragment newInstance(String urlQuery, City city) {
        return newInstance(urlQuery, city, null, false);
    }

    public static MerchantListFragment newInstance(
            String urlQuery, City city, String cpmSource, boolean isInstallment) {
        MerchantListFragment fragment = new MerchantListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, urlQuery);
        args.putSerializable(ARG_CITY, city);
        args.putBoolean(ARG_IS_INSTALLMENT, isInstallment);
        args.putString(ARG_CPM_SOURCE, cpmSource);
        fragment.setArguments(args);
        return fragment;
    }

    public static MerchantListFragment newInstance(
            String urlQuery, City city, String cpmSource) {
        return newInstance(urlQuery, city, cpmSource, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
        initFoot();
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new MerchantListAdapter(getContext());
        adapter.setMerchants(merchants);
        adapter.setCpmSource(cpmSource);
        adapter.setParentMerchants(parentMerchants);
        adapter.setPopularMerchants(popularMerchants);
        adapter.setInstallment(isInstallment);
    }

    private void initValue() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            urlQuery = bundle.getString(ARG_QUERY);
            city = (City) bundle.getSerializable(ARG_CITY);
            isInstallment = bundle.getBoolean(ARG_IS_INSTALLMENT, false);
            cpmSource = bundle.getString(ARG_CPM_SOURCE);
        }
        if (city == null) {
            city = Session.getInstance()
                    .getMyCity(getContext());
        }
        mHandler = new Handler();
        merchants = new ArrayList<>();
        parentMerchants = new ArrayList<>();
        popularMerchants = new ArrayList<>();
        currentPage = 1;
        parentCurrentPage = 1;
    }

    private void initFoot() {
        footerView = View.inflate(getContext(), R.layout.merchant_list_address_footer, null);
        footerViewHolder = new FooterViewHolder(footerView);
        footerViewHolder.loadView.setVisibility(View.INVISIBLE);
        parentFooterView = View.inflate(getContext(), R.layout.merchant_list_address_footer, null);
        parentFooterViewHolder = new ParentFooterViewHolder(parentFooterView);
        parentFooterViewHolder.endView.setVisibility(View.GONE);
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
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initError();
        initView();
        initTracker();
        onRefresh();
    }

    private void initView() {
        pullToRefreshVerticalRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView = pullToRefreshVerticalRecyclerView.getRefreshableView();
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setPadding(0,
                0,
                0,
                getContext() instanceof MainActivity ? CommonUtil.dp2px(getContext(), 50) : 0);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(this);
        adapter.setFooterView(footerView);
        adapter.setParentFooterView(parentFooterView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //加载下一页分为本城市商家列表和上一次城市商家列表,当有上一级城市时，本城市加载
                        //到最后一页时，加载上一级城市的数据，两个列表的pageCount和currentPage分开记录
                        int position = layoutManager.findLastVisibleItemPosition();
                        int itemCount = recyclerView.getAdapter()
                                .getItemCount();
                        if ((position >= itemCount - 5) && currentPage < pageCount) {
                            // 发出下一个加载的信号
                            // 显示加载视图
                            footerViewHolder.loadView.setVisibility(View.VISIBLE);
                            footerViewHolder.endView.setVisibility(View.GONE);
                            Log.d("MerchantListFragment", "on " + "next page: " + currentPage + 1);
                            initPagination(currentPage + 1, 0);
                        } else {
                            // 到末尾页面了,显示没有更多
                            footerViewHolder.loadView.setVisibility(View.GONE);
                            //没有上一级城市或者上一级城市的商家列表不为空时，才显示当前城市的底部
                            //视图
                            if (mParentCid == 0 || !CommonUtil.isCollectionEmpty(parentMerchants)) {
                                footerViewHolder.endView.setVisibility(View.VISIBLE);
                            } else {
                                footerViewHolder.endView.setVisibility(View.GONE);
                            }
                            if (mParentCid != 0) {
                                if (pageCount > 1 && parentPageCount == 0) {
                                    //当前城市只有一页时，直接加载上一级城市的数据，大于一页时，上拉加
                                    //载更多时加载上一级城市数据
                                    onParentRefresh();
                                } else if ((position >= itemCount - 5) && parentCurrentPage <
                                        parentPageCount) {
                                    parentFooterViewHolder.loadView.setVisibility(View.VISIBLE);
                                    parentFooterViewHolder.endView.setVisibility(View.GONE);
                                    Log.d("MerchantListFragment",
                                            "on " + "next parentCurrentPage: " +
                                                    parentCurrentPage + 1);
                                    initPagination(parentCurrentPage + 1, mParentCid);
                                } else {
                                    parentFooterViewHolder.loadView.setVisibility(View.GONE);
                                    parentFooterViewHolder.endView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager != null) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if (position < 15) {
                        if (!isHide) {
                            hideFiltrateAnimation();
                        }
                    } else if (isHide) {
                        if (btnScrollTop.getVisibility() == View.GONE) {
                            btnScrollTop.setVisibility(View.VISIBLE);
                        }
                        showFiltrateAnimation();
                    }
                }
            }
        });
    }

    private void initError() {
        emptyView.setHintId(R.string.hint_filtrate_merchant_empty);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh();
            }
        });
    }

    private void showFiltrateAnimation() {
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
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
            btnScrollTop.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
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
            btnScrollTop.startAnimation(animation);
        }
    }

    @OnClick(R.id.btn_scroll_top)
    void scrollTop() {
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }


    void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView, "merchant_list");
    }

    public MerchantListAdapter getAdapter() {
        return adapter;
    }

    //当前城市加载商家列表数据
    public void onRefresh() {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<MerchantListData>() {
                    @Override
                    public void onNext(MerchantListData merchantListData) {
                        popularMerchants.clear();
                        if (merchantListData.getPopularMerchant() != null && merchantListData
                                .getPopularMerchant()
                                .getData() != null) {
                            popularMerchants.addAll(merchantListData.getPopularMerchant()
                                    .getData());
                        }
                        HljHttpData<List<Merchant>> listHljHttpData = merchantListData
                                .getNormalMerchant();
                        recyclerView.scrollToPosition(0);
                        merchants.clear();
                        if (listHljHttpData != null) {
                            if (listHljHttpData.getData() != null) {
                                merchants.addAll(listHljHttpData.getData());
                            }
                            pageCount = listHljHttpData.getPageCount();
                            mParentCid = merchantListData.getParentCid();
                            mParentCity = merchantListData.getParentCity();
                            adapter.setCity(city);
                            adapter.notifyDataSetChanged();
                            parentFooterViewHolder.endView.setVisibility(View.GONE);
                            if (pageCount <= 1) {
                                footerViewHolder.endView.setVisibility(View.VISIBLE);
                                footerViewHolder.tvNoMoreHint.setText(getString(R.string
                                        .label_city_no_more));
                                footerViewHolder.tvNoMoreHint.invalidate();
                                if (mParentCid != 0) {
                                    onParentRefresh();
                                }
                            } else {
                                footerViewHolder.endView.setVisibility(View.GONE);
                            }
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        footerViewHolder.endView.setVisibility(View.VISIBLE);
                        footerViewHolder.tvNoMoreHint.setText(getString(R.string
                                .label_city_no_more));
                        footerViewHolder.tvNoMoreHint.invalidate();
                    }
                })
                .setProgressBar(progressBar)
                .build();
        pageCount = 0;
        currentPage = 1;
        MerchantApi.getMerchantListDataObb(getUrl(1), 1, city.getId())
                .subscribe(refreshSubscriber);
    }

    //上一级城市加载商家列表数据
    public void onParentRefresh() {
        if (parentRefreshSubscriber == null || parentRefreshSubscriber.isUnsubscribed()) {
            parentRefreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<MerchantListData>() {
                        @Override
                        public void onNext(MerchantListData merchantListData) {
                            List<Merchant> popularMerchants = null;
                            if (merchantListData.getPopularMerchant() != null) {
                                popularMerchants = merchantListData.getPopularMerchant()
                                        .getData();
                            }
                            parentMerchants.clear();
                            if (popularMerchants != null) {
                                parentMerchants.addAll(0, popularMerchants);
                            }
                            if (merchantListData.getNormalMerchant() != null) {
                                HljHttpData<List<Merchant>> listHljHttpData = merchantListData
                                        .getNormalMerchant();
                                parentPageCount = merchantListData.getNormalMerchant()
                                        .getPageCount();
                                if (listHljHttpData.getData() != null) {
                                    parentMerchants.addAll(listHljHttpData.getData());
                                }
                            }
                            if (parentMerchants.size() == 0) {
                                footerViewHolder.endView.setVisibility(View.GONE);
                                parentFooterViewHolder.endView.setVisibility(View.VISIBLE);
                            } else {
                                footerViewHolder.endView.setVisibility(View.VISIBLE);
                                footerViewHolder.tvNoMoreHint.setText(getString(R.string
                                                .label_city_other,
                                        mParentCity));
                                parentFooterViewHolder.endView.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .build();
            parentPageCount = 0;
            parentCurrentPage = 1;
            MerchantApi.getMerchantListDataObb(getUrl(1), 1, mParentCid)
                    .subscribe(parentRefreshSubscriber);
        }
    }

    private void initPagination(int page, final long parentCid) {
        //parentCid不为0表示当前加载的是上一级城市的数据
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Merchant>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Merchant>> listHljHttpData) {
                        if (parentCid == 0) {
                            footerViewHolder.loadView.setVisibility(View.GONE);
                            currentPage = currentPage + 1;
                            merchants.addAll(listHljHttpData.getData());
                        } else {
                            parentFooterViewHolder.loadView.setVisibility(View.GONE);
                            parentCurrentPage = parentCurrentPage + 1;
                            parentMerchants.addAll(listHljHttpData.getData());
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (parentCid == 0) {
                            footerViewHolder.loadView.setVisibility(View.GONE);
                        } else {
                            parentFooterViewHolder.loadView.setVisibility(View.GONE);
                        }
                    }
                })
                .build();
        MerchantApi.getMerchantListObb(getUrl(page),
                page,
                parentCid == 0 ? city.getId() : parentCid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);

    }

    public String getUrl(int page) {
        StringBuilder builder = new StringBuilder(String.format(Constants.HttpPath.MERCHANTS_URL,
                page));
        if (!JSONUtil.isEmpty(urlQuery)) {
            builder.append(urlQuery);
        }
        return builder.toString();
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0) {
            City mCity = null;
            if (params.length > 1 && params[1] instanceof City) {
                mCity = (City) params[1];
            }
            if ((params[0] instanceof String && !params[0].equals(urlQuery)) || (mCity != null &&
                    !mCity.getId()
                    .equals(city.getId()))) {
                city = mCity;
                urlQuery = (String) params[0];
            }
            onRefresh();
        }
    }

    @Override
    public void onItemClick(int position, Object object) {
        Merchant merchant = (Merchant) object;
        if (merchant == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
        intent.putExtra("id", merchant.getId());
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    class FooterViewHolder {
        @BindView(R.id.tv_no_more_hint)
        TextView tvNoMoreHint;
        @BindView(R.id.no_more_hint)
        LinearLayout endView;
        @BindView(R.id.loading)
        LinearLayout loadView;

        @OnClick(R.id.tv_change_city)
        void onChangeCityClick() {
            Intent intent = new Intent(getActivity(), CityListActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                        R.anim.activity_anim_default);
            }
        }

        FooterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ParentFooterViewHolder {
        @BindView(R.id.no_more_hint)
        LinearLayout endView;
        @BindView(R.id.loading)
        LinearLayout loadView;

        @OnClick(R.id.tv_change_city)
        void onChangeCityClick() {
            Intent intent = new Intent(getActivity(), CityListActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_in_up_to_top,
                        R.anim.activity_anim_default);
            }
        }

        ParentFooterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, parentRefreshSubscriber);
    }

}
