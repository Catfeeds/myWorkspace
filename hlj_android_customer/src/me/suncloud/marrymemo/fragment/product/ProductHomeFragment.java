package me.suncloud.marrymemo.fragment.product;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcardcustomerlibrary.utils.ProductRedPacketDialog;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.NewHotKeyWord;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.ad.MiaoZhenUtil;
import me.suncloud.marrymemo.adpter.FlowAdapter;
import me.suncloud.marrymemo.adpter.product.ProductHomeTopicAdapter;
import me.suncloud.marrymemo.adpter.product.SelectedProductRecyclerAdapter;
import me.suncloud.marrymemo.adpter.product.viewholder.ProductCategoryViewHolder;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.Category;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.view.product.ProductSubPageListActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.view.product.WeeklyProductsActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by luohanlin on 2017/11/7.
 * 婚礼购Fragment
 */

public class ProductHomeFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView> {

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.empty_divider)
    View emptyDivider;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.btn_shopping_cart)
    ImageButton btnShoppingCart;
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;
    private boolean isHomePage; // 是不是在首页上
    private boolean backMain;
    private View footerView;
    private View headerView;
    private View endView;
    private View loadView;
    private SelectedProductRecyclerAdapter adapter;
    private HeaderViewHolder headerViewHolder;
    private boolean isShowTopBtn;
    private HljHttpSubscriber initListSub;
    private HljHttpSubscriber pageSub;
    private NoticeUtil noticeUtil;

    private int screenWidth;
    private int singlePosterWidth;
    private int singlePosterHeight;
    private int threePosterWidth1;
    private int threePosterWidth2;
    private int threePosterHeight1;
    private int threePosterHeight2;
    private int weekPWidth;
    private int weekPHeight;
    private int subPageWidth;
    private int subPageHeight;
    private int badgeSize;
    private HljHttpSubscriber headerSub;
    private FlowAdapter flowAdapter;
    private City city;
    private HljHttpSubscriber redPacketSub;
    private ProductRedPacketDialog redPacketDialog;
    private String headImg;
    private List<RedPacket> redPacketList;
    private HljHttpSubscriber inputSubscriber;
    private NewHotKeyWord newHotKeyWord;


    @Override
    public String fragmentPageTrackTagName() {
        return "婚品频道";
    }

    public static ProductHomeFragment newInstance(boolean isHomePage, boolean backMain) {
        Bundle args = new Bundle();
        ProductHomeFragment fragment = new ProductHomeFragment();
        args.putSerializable("is_home_page", isHomePage);
        args.putSerializable("backMain", backMain);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
    }

    private void initValues() {
        city = Session.getInstance()
                .getMyCity(getActivity());
        initDimens();
        Bundle args = getArguments();
        if (args != null) {
            isHomePage = args.getBoolean("is_home_page", false);
            backMain = args.getBoolean("backMain", false);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_product_no_more_footer___cv, null);
        headerView = View.inflate(getContext(), R.layout.product_channel_header, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SelectedProductRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
        adapter.setHeaderView(headerView);
    }

    private void initDimens() {
        screenWidth = CommonUtil.getDeviceSize(getContext()).x;
        singlePosterWidth = screenWidth;
        singlePosterHeight = Math.round(singlePosterWidth / 4.0f);
        threePosterWidth1 = screenWidth * 200 / (175 + 200);
        threePosterWidth2 = screenWidth - threePosterWidth1;
        threePosterHeight1 = threePosterWidth1 * 5 / 4;
        threePosterHeight2 = threePosterHeight1 / 2;
        weekPWidth = (screenWidth - (CommonUtil.dp2px(getContext(), 44))) / 3;
        weekPHeight = weekPWidth;
        subPageWidth = screenWidth * 288 / 375;
        subPageHeight = subPageWidth / 2;
        badgeSize = CommonUtil.dp2px(getContext(), 40);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_home, container, false);
        HljBaseActivity.setActionBarPadding(getContext(), view);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        if (isHomePage) {
            btnBack.setVisibility(View.GONE);
            emptyDivider.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
            emptyDivider.setVisibility(View.GONE);
        }
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
                getInputBoxHotWord();
            }
        });
        // action bar
        tvSearch.setHint(R.string.hint_search_product);
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        noticeUtil = new NoticeUtil(getContext(), tvMsgCount, msgNoticeView);
        noticeUtil.onResume();

        // 列表
        recyclerView.setHeaderColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setPadding(0,
                        0,
                        0,
                        getContext() instanceof MainActivity ? CommonUtil.dp2px(getContext(),
                                50) : 0);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
        getInputBoxHotWord();
        if (isHomePage) {
            initProductRedPacket();
        }
    }

    /**
     * 获取输入框热词
     * 切换城市也需要刷新
     */
    private void getInputBoxHotWord() {
        CommonUtil.unSubscribeSubs(inputSubscriber);
        inputSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<NewHotKeyWord>() {
                    @Override
                    public void onNext(NewHotKeyWord hotKeyWord) {
                        newHotKeyWord = hotKeyWord;
                        if (hotKeyWord != null && !TextUtils.isEmpty(hotKeyWord.getTitle())) {
                            tvSearch.setText(hotKeyWord.getTitle());
                        } else {
                            tvSearch.setText(null);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        NewSearchApi.getInputWord(NewSearchApi.InputType.TYPE_PRODUCT_HOME)
                .subscribe(inputSubscriber);
    }

    private void initProductRedPacket() {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        redPacketSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        setRedPacketResult(jsonElement);
                    }
                })
                .build();
        CustomerCardApi.getUserRedPacketList()
                .subscribe(redPacketSub);
    }

    private void setRedPacketResult(JsonElement jsonElement) {
        if (jsonElement != null) {
            try {
                headImg = jsonElement.getAsJsonObject()
                        .get("head_img")
                        .getAsString();
                JsonElement list = jsonElement.getAsJsonObject()
                        .get("list")
                        .getAsJsonArray();
                if (list != null) {
                    redPacketList = GsonUtil.getGsonInstance()
                            .fromJson(list, new TypeToken<List<RedPacket>>() {}.getType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isShowRedPacketDialog()) {
                showRedPacketDialog();
            }
        }
    }

    //新人有礼红包
    private void showRedPacketDialog() {
        if (redPacketDialog != null && redPacketDialog.isShowing()) {
            return;
        }
        if (redPacketDialog == null) {
            redPacketDialog = new ProductRedPacketDialog(getContext(), R.style.BubbleDialogTheme);
            redPacketDialog.setContentView(getLayoutInflater().inflate(R.layout
                            .dialog_product_red_packet,
                    null,
                    false));
            redPacketDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ProductHomeFragment.this.redPacketList.clear();
                    ProductHomeFragment.this.redPacketList = null;
                }
            });
        }
        redPacketDialog.setHeadImg(headImg);
        redPacketDialog.setRedPacketList(redPacketList);
        redPacketDialog.show();
    }

    private boolean isShowRedPacketDialog() {
        if (Session.getInstance()
                .getCurrentUser(getContext()) == null) {
            return false;
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return false;
        }

        int currentTab = mainActivity.getCurrentTab();
        if (currentTab != 3) {
            return false;
        }
        if (redPacketList == null || redPacketList.isEmpty()) {
            return false;
        }
        return true;
    }

    private void initLoad() {
        loadList();
        loadHeader();
    }

    private void loadList() {
        if (initListSub == null || initListSub.isUnsubscribed()) {
            initListSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.hideEmptyView();
                            headerViewHolder.popularLayoutHeader.setVisibility(View.VISIBLE);
                            adapter.setProducts(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setPullToRefreshBase(recyclerView)
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object e) {
                            if (e instanceof SocketTimeoutException || e instanceof
                                    ConnectException || e instanceof UnknownHostException) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.showEmptyView();
                            }
                        }
                    })
                    .build();
            ProductApi.getHomeRecommendProduct(1)
                    .subscribe(initListSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<ShopProduct>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public Observable<HljHttpData<List<ShopProduct>>> onNextPage(
                            int page) {
                        return ProductApi.getHomeRecommendProduct(page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                        adapter.addProducts(listHljHttpData.getData());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    private void loadHeader() {
        CommonUtil.unSubscribeSubs(headerSub);

        Observable<PosterData> postersObb = CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.ProductChannelActivity,
                Session.getInstance()
                        .getMyCity(getContext())
                        .getId());
        //婚品分类
        Observable<HljHttpData<List<Category>>> categoriesObb = ProductApi
                .getProductCategoriesObb();
        Observable<HljHttpData<List<ProductTopic>>> topicsObb = ProductApi.getProductSubPagesObb(1,
                10);
        Observable<HljHttpData<List<ShopProduct>>> productsObb = ProductApi.getWeekProducts();
        Observable<ResultZip> observable = Observable.zip(postersObb,
                categoriesObb,
                topicsObb,
                productsObb,
                new Func4<PosterData, HljHttpData<List<Category>>,
                        HljHttpData<List<ProductTopic>>, HljHttpData<List<ShopProduct>>,
                        ResultZip>() {
                    @Override
                    public ResultZip call(
                            PosterData posterData,
                            HljHttpData<List<Category>> categoriesData,
                            HljHttpData<List<ProductTopic>> listHljHttpData,
                            HljHttpData<List<ShopProduct>> listHljHttpData2) {
                        ResultZip zip = new ResultZip();
                        zip.posterData = posterData;
                        zip.categoriesData = categoriesData;
                        zip.topicsData = listHljHttpData;
                        zip.weekProductData = listHljHttpData2;
                        return zip;
                    }
                });
        headerSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        headerViewHolder.setViewData(resultZip);
                    }
                })
                .build();
        observable.subscribe(headerSub);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getActivity()));
            if (isShowRedPacketDialog()) {
                showRedPacketDialog();
            }
        }
    }

    public void cityRefresh(City c) {
        if (!city.getId()
                .equals(c.getId())) {
            city = c;
            onRefresh(null);
            getInputBoxHotWord();
        }
    }

    @Override
    public void refresh(Object... params) {
        initLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(redPacketSub, initListSub, inputSubscriber);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        initLoad();
    }

    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < 3) {
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

    //点击跳转搜索
    @OnClick(R.id.tv_search)
    void onSearch() {
        Intent intent = new Intent(getContext(), NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_INPUT_TYPE,
                NewSearchApi.InputType.TYPE_PRODUCT_HOME);
        intent.putExtra(NewSearchActivity.ARG_HOT_KEY_WORD, newHotKeyWord);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE, NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    //新消息点击
    @OnClick(R.id.msg_layout)
    void onNewMsg() {
        if (Util.loginBindChecked(getContext())) {
            startActivity(new Intent(getContext(), MessageHomeActivity.class));
        }
    }

    //点击购物车跳转
    @OnClick(R.id.btn_shopping_cart)
    void onShoppingCart() {
        if (!Util.loginBindChecked(getContext())) {
            return;
        }
        if (notice != null) {
            notice.setVisibility(View.GONE);
        }
        startActivity(new Intent(getContext(), ShoppingCartActivity.class));
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (!backMain) {
            getActivity().onBackPressed();
        } else {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("action", "productChannel");
            startActivity(intent);
            getActivity().finish();
        }
    }

    @OnClick(R.id.btn_scroll_top)
    void onScrollToTop() {
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

    @Override
    public void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        if (flowAdapter != null && flowAdapter.getCount() > 1) {
            headerViewHolder.topPostersSliderLayout.startAutoCycle();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        headerViewHolder.topPostersSliderLayout.stopAutoCycle();
    }

    private class ResultZip {
        PosterData posterData;
        HljHttpData<List<Category>> categoriesData;
        HljHttpData<List<ProductTopic>> topicsData;
        HljHttpData<List<ShopProduct>> weekProductData;
    }

    class HeaderViewHolder {
        @BindView(R.id.top_posters_slider_layout)
        SliderLayout topPostersSliderLayout;
        @BindView(R.id.top_posters_indicator)
        CirclePageExIndicator topPostersIndicator;
        @BindView(R.id.top_posters_layout)
        RelativeLayout topPostersLayout;
        @BindView(R.id.intro_layout)
        LinearLayout introLayout;
        @BindView(R.id.category_flow_layout)
        FlowLayout categoryFlowLayout;
        @BindView(R.id.img_single_poster)
        ImageView imgSinglePoster;
        @BindView(R.id.single_poster_layout)
        LinearLayout singlePosterLayout;
        @BindView(R.id.img_center_poster_1)
        ImageView imgCenterPoster1;
        @BindView(R.id.img_center_poster_2)
        ImageView imgCenterPoster2;
        @BindView(R.id.img_center_poster_3)
        ImageView imgCenterPoster3;
        @BindView(R.id.three_center_poster_layout)
        LinearLayout threeCenterPosterLayout;
        @BindView(R.id.tv_more_week_updates)
        TextView tvMoreWeekUpdates;
        @BindView(R.id.img_week_p1)
        ImageView imgWeekP1;
        @BindView(R.id.tv_week_p1_title)
        TextView tvWeekP1Title;
        @BindView(R.id.tv_week_p1_price)
        TextView tvWeekP1Price;
        @BindView(R.id.week_p1_layout)
        LinearLayout weekP1Layout;
        @BindView(R.id.img_week_p2)
        ImageView imgWeekP2;
        @BindView(R.id.tv_week_p2_title)
        TextView tvWeekP2Title;
        @BindView(R.id.tv_week_p2_price)
        TextView tvWeekP2Price;
        @BindView(R.id.week_p2_layout)
        LinearLayout weekP2Layout;
        @BindView(R.id.img_week_p3)
        ImageView imgWeekP3;
        @BindView(R.id.tv_week_p3_title)
        TextView tvWeekP3Title;
        @BindView(R.id.tv_week_p3_price)
        TextView tvWeekP3Price;
        @BindView(R.id.week_p3_layout)
        LinearLayout weekP3Layout;
        @BindView(R.id.week_updates_layout)
        LinearLayout weekUpdatesLayout;
        @BindView(R.id.tv_more_subs)
        TextView tvMoreSubs;
        @BindView(R.id.fine_subs_recycler)
        RecyclerView fineSubsRecycler;
        @BindView(R.id.fine_subs_layout)
        LinearLayout fineSubsLayout;
        @BindView(R.id.popular_layout_header)
        LinearLayout popularLayoutHeader;
        @BindView(R.id.img_badge_1)
        ImageView imgBadge1;
        @BindView(R.id.img_badge_2)
        ImageView imgBadge2;
        @BindView(R.id.img_badge_3)
        ImageView imgBadge3;

        ProductHomeTopicAdapter adapter;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
            topPostersLayout.getLayoutParams().width = screenWidth;
            topPostersLayout.getLayoutParams().height = Math.round(screenWidth / 2.0f);
            singlePosterLayout.getLayoutParams().width = singlePosterWidth;
            singlePosterLayout.getLayoutParams().height = singlePosterHeight;
            imgCenterPoster1.getLayoutParams().width = threePosterWidth1;
            imgCenterPoster1.getLayoutParams().height = threePosterHeight1;
            imgCenterPoster2.getLayoutParams().width = threePosterWidth2;
            imgCenterPoster2.getLayoutParams().height = threePosterHeight2;
            imgCenterPoster3.getLayoutParams().width = threePosterWidth2;
            imgCenterPoster3.getLayoutParams().height = threePosterHeight2;
            imgWeekP1.getLayoutParams().width = weekPWidth;
            imgWeekP2.getLayoutParams().width = weekPWidth;
            imgWeekP3.getLayoutParams().width = weekPWidth;
            imgWeekP1.getLayoutParams().height = weekPHeight;
            imgWeekP2.getLayoutParams().height = weekPHeight;
            imgWeekP3.getLayoutParams().height = weekPHeight;
            adapter = new ProductHomeTopicAdapter(getContext());
            adapter.setSize(subPageWidth, subPageHeight);

            fineSubsRecycler.setHasFixedSize(true);
            fineSubsRecycler.setLayoutManager(new LinearLayoutManager(getContext(),
                    HORIZONTAL,
                    false));
            fineSubsRecycler.setFocusable(false);
            fineSubsRecycler.setAdapter(adapter);
            initTracker();
        }


        private void initTracker() {
            HljVTTagger.tagViewParentName(topPostersSliderLayout, "product_channel_banner_list");
        }

        private void setViewData(ResultZip zip) {
            introLayout.setVisibility(View.VISIBLE);
            //poster
            if (zip.posterData == null) {
                topPostersSliderLayout.stopAutoCycle();
                topPostersLayout.setVisibility(View.GONE);
                threeCenterPosterLayout.setVisibility(View.GONE);
                singlePosterLayout.setVisibility(View.GONE);
            } else {
                setPosterData(zip.posterData);
            }
            if (CommonUtil.isCollectionEmpty(zip.weekProductData.getData()) || zip
                    .weekProductData.getData()
                    .size() < 3) {
                weekUpdatesLayout.setVisibility(View.GONE);
            } else {
                weekUpdatesLayout.setVisibility(View.VISIBLE);
                setWeekProducts(zip.weekProductData.getData());
            }
            if (CommonUtil.isCollectionEmpty(zip.topicsData.getData())) {
                fineSubsLayout.setVisibility(View.GONE);
            } else {
                fineSubsLayout.setVisibility(View.VISIBLE);
                adapter.setTopics(zip.topicsData.getData());
                adapter.notifyDataSetChanged();
            }
        }

        private void setPosterData(PosterData posterData) {
            List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                    HljCommon.POST_SITES.SITE_SHOP_CHANNEL_BANNER,
                    false);
            flowAdapter = new FlowAdapter(getContext(),
                    new ArrayList<>(posters),
                    R.layout.flow_item);
            flowAdapter.setMiaoZhenPId(MiaoZhenUtil.PId.PRODUCT_HOME_POSTER);
            flowAdapter.setSliderLayout(topPostersSliderLayout);
            topPostersSliderLayout.setPagerAdapter(flowAdapter);
            topPostersSliderLayout.setCustomIndicator(topPostersIndicator);
            if (flowAdapter.getCount() == 0) {
                topPostersSliderLayout.stopAutoCycle();
                topPostersLayout.setVisibility(View.GONE);
                introLayout.setVisibility(View.GONE);
            } else {
                introLayout.setVisibility(View.VISIBLE);
                topPostersLayout.setVisibility(View.VISIBLE);
                if (flowAdapter.getCount() > 1) {
                    topPostersSliderLayout.startAutoCycle();
                } else {
                    topPostersSliderLayout.stopAutoCycle();
                }
            }
            //product category
            posters = PosterUtil.getPosterList(posterData.getFloors(),
                    HljCommon.POST_SITES.SITE_SHOP_CATEGORY,
                    false);
            if (CommonUtil.isCollectionEmpty(posters)) {
                categoryFlowLayout.setVisibility(View.GONE);
            } else {
                categoryFlowLayout.setVisibility(View.VISIBLE);
                int count = categoryFlowLayout.getChildCount();
                int size = posters.size();
                if (count > size) {
                    categoryFlowLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    View view = null;
                    ProductCategoryViewHolder categoryViewHolder;
                    if (count > i) {
                        view = categoryFlowLayout.getChildAt(i);
                    }
                    if (view == null) {
                        View.inflate(getContext(),
                                R.layout.product_category_flow_item,
                                categoryFlowLayout);
                        view = categoryFlowLayout.getChildAt(categoryFlowLayout.getChildCount() -
                                1);
                    }
                    categoryViewHolder = (ProductCategoryViewHolder) view.getTag();
                    if (categoryViewHolder == null) {
                        categoryViewHolder = new ProductCategoryViewHolder(view);
                        view.setTag(categoryViewHolder);
                    }
                    categoryViewHolder.setView(getContext(), posters.get(i), i, 0);
                }
            }
            //single poster
            posters = PosterUtil.getPosterList(posterData.getFloors(),
                    HljCommon.POST_SITES.SHOP_ACTIVITY_BANNER,
                    false);
            if (CommonUtil.isCollectionEmpty(posters)) {
                singlePosterLayout.setVisibility(View.GONE);
            } else {
                final Poster poster = posters.get(0);
                singlePosterLayout.setVisibility(View.VISIBLE);
                singlePosterLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poster.getId() > 0) {
                            BannerUtil.bannerJump(getContext(), poster, null);
                        }
                    }
                });
                Glide.with(getContext())
                        .load(poster.getPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgSinglePoster);
            }
            // 中间三个Poster
            posters = PosterUtil.getPosterList(posterData.getFloors(),
                    HljCommon.POST_SITES.SITE_SHOP_HLG_CENTRE_SITE,
                    false);
            if (CommonUtil.isCollectionEmpty(posters) || posters.size() < 3) {
                threeCenterPosterLayout.setVisibility(View.GONE);
            } else {
                threeCenterPosterLayout.setVisibility(View.VISIBLE);
                final Poster poster1 = posters.get(0);
                final Poster poster2 = posters.get(1);
                final Poster poster3 = posters.get(2);
                Glide.with(getContext())
                        .load(poster1.getPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgCenterPoster1);
                imgCenterPoster1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poster1.getId() > 0) {
                            BannerUtil.bannerJump(getContext(), poster1, null);
                        }
                    }
                });
                Glide.with(getContext())
                        .load(poster2.getPath())
                        .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                .transform(new MultiTransformation(new CenterCrop()))
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(imgCenterPoster2);
                imgCenterPoster2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poster2.getId() > 0) {
                            BannerUtil.bannerJump(getContext(), poster2, null);
                        }
                    }
                });
                Glide.with(getContext())
                        .load(poster3.getPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgCenterPoster3);
                imgCenterPoster3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poster3.getId() > 0) {
                            BannerUtil.bannerJump(getContext(), poster3, null);
                        }
                    }
                });
            }
        }

        private void setWeekProducts(List<ShopProduct> products) {
            setProductView(products.get(0),
                    weekP1Layout,
                    imgWeekP1,
                    imgBadge1,
                    tvWeekP1Title,
                    tvWeekP1Price);
            setProductView(products.get(1),
                    weekP2Layout,
                    imgWeekP2,
                    imgBadge2,
                    tvWeekP2Title,
                    tvWeekP2Price);
            setProductView(products.get(2),
                    weekP3Layout,
                    imgWeekP3,
                    imgBadge3,
                    tvWeekP3Title,
                    tvWeekP3Price);
        }

        private void setProductView(
                final ShopProduct product,
                View holderLayout,
                final ImageView imgCover,
                ImageView imgBadge,
                TextView tvTitle,
                TextView tvPrice) {
            holderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product != null && product.getId() > 0) {
                        Intent intent = new Intent(getContext(), ShopProductDetailActivity.class);
                        intent.putExtra("id", product.getId());
                        startActivity(intent);
                    }
                }
            });
            if (product.getRule() == null || TextUtils.isEmpty(product.getRule()
                    .getShowImg())) {
                imgBadge.setVisibility(View.GONE);
            } else {
                imgBadge.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(ImageUtil.getImagePath2(product.getRule()
                                .getShowImg(), badgeSize))
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .error(R.mipmap.icon_empty_image))
                        .into(imgBadge);
            }
            Glide.with(getContext())
                    .load(product.getCoverPath())
                    .into(imgCover);
            tvTitle.setText(product.getTitle());
            tvPrice.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat(product.getShowPrice())));
        }

        @OnClick(R.id.tv_more_subs)
        void onMoreTopics() {
            Intent intent = new Intent(getContext(), ProductSubPageListActivity.class);
            intent.putExtra("limit", adapter.getItemCount());
            startActivity(intent);
        }

        @OnClick(R.id.tv_more_week_updates)
        void onMoreWeekUpdates() {
            Intent intent = new Intent(getContext(), WeeklyProductsActivity.class);
            startActivity(intent);
        }
    }
}
