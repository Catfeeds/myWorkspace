package me.suncloud.marrymemo.fragment.product;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.search.ProductSearchResultList;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.PullToRefreshScrollableLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CategoryMarkFilterAdapter;
import me.suncloud.marrymemo.adpter.product.ProductRecyclerAdapter;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.view.ShoppingCategoryDetailActivity;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;
import me.suncloud.marrymemo.widget.ShoppingCategoryAllCategoryDialog;
import me.suncloud.marrymemo.widget.ShoppingCategoryDetailDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 婚品分类详情页fragment
 * Created by jinxin on 2017/5/10 0010.
 */

public class ShoppingCategoryDetailFragment extends ScrollAbleFragment implements
        PullToRefreshBase.OnRefreshListener<ScrollableLayout>, ShoppingCategoryDetailDialog
        .OnConfirmListener, CheckableLinearGroup.OnCheckedChangeListener,
        ShoppingCategoryAllCategoryDialog.OnItemClickListener {

    private final static String SORT_SCORE = "score";
    private final static String SORT_PRICE_UP = "price_up";
    private final static String SORT_PRICE_DOWN = "price_down";
    private final static String SORT_SOLD_COUNT = "sold_count";

    @BindView(R.id.img_sub_page)
    ImageView imgSubPage;
    @BindView(R.id.filter_view)
    CheckableLinearGroup filterView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.scrollable_layout)
    PullToRefreshScrollableLayout scrollableLayout;
    @BindView(R.id.layout_img)
    LinearLayout imgLayout;
    @BindView(R.id.cb_default)
    CheckableLinearButton cbDefault;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.img_price)
    ImageView imgPrice;
    @BindView(R.id.cb_price)
    CheckableLinearButton cbPrice;
    @BindView(R.id.cb_sold_count)
    CheckableLinearButton cbSoldCount;
    @BindView(R.id.cb_filtrate)
    LinearLayout cbFiltrate;
    @BindView(R.id.tv_filtrate)
    TextView tvFiltrate;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private View footerView;
    private View endView;
    private View loadView;
    private Unbinder unbinder;
    private long categoryId;
    private long parentId;
    private boolean showFilter;
    private List<CategoryMark> categoryMarks;
    private CategoryMark allCategoryMark;
    private Map<Long, ProductTopic> topicMap;
    private String selectTags;
    private ProductRecyclerAdapter adapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber refreshProductSubscriber;
    private ProductTopic currentTopic;
    private ShoppingCategoryDetailDialog categoryDetailDialog;
    private ShoppingCategoryAllCategoryDialog categoryAllDialog;
    private String order;
    private boolean isShowTopBtn; //回到顶部的按钮是否显示着
    private StaggeredGridLayoutManager layoutManager;
    private ShoppingCategoryDetailActivity parentActivity;

    public static ShoppingCategoryDetailFragment newInstance(
            long childId, long parentId, boolean showFilter) {
        ShoppingCategoryDetailFragment f = new ShoppingCategoryDetailFragment();
        Bundle data = new Bundle();
        data.putLong("categoryId", childId);
        data.putLong("parentId", parentId);
        data.putBoolean("showFilter", showFilter);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", 0);
            parentId = getArguments().getLong("parentId", 0);
            showFilter = getArguments().getBoolean("showFilter", false);
        }
        footerView = View.inflate(getContext(), R.layout.hlj_product_no_more_footer___cv, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new ProductRecyclerAdapter(getContext());
        categoryMarks = new ArrayList<>();
        topicMap = new HashMap<>();
        order = SORT_SCORE;
        allCategoryMark = new CategoryMark();
        allCategoryMark.setId(CategoryMarkFilterAdapter.ALL_CATEGORY_MARK_ID);
        Mark mark = new Mark();
        mark.setName("全部");
        allCategoryMark.setMark(mark);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_category_detail,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        initTracker();
        return rootView;
    }

    private void initTracker() {
        HljVTTagger.buildTagger(recyclerView)
                .tagName("product_list")
                .dataId(categoryId)
                .dataType("ProductCategory")
                .tag();
    }

    private void initWidget() {
        imgSubPage.getLayoutParams().height = Math.round(CommonUtil.getDeviceSize(getContext()).x
                / 2);
        filterView.setOnCheckedChangeListener(this);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        scrollableLayout.getRefreshableView()
                .setExtraHeight(CommonUtil.dp2px(getContext(), 40));
        scrollableLayout.getRefreshableView()
                .getHelper()
                .setCurrentScrollableContainer(this);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        scrollableLayout.setOnRefreshListener(this);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .addOnScrollListener(onScrollListener);
        recyclerView.getRefreshableView()
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration(CommonUtil.dp2px(getContext(), 8)));
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
        getParentActivity();
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        if (scrollableLayout != null) {
            scrollableLayout.getRefreshableView()
                    .scrollToTop();
        }
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        if (parentActivity != null && !parentActivity.isFinishing()) {
            parentActivity.scrollToTop();
        }
    }


    public ShoppingCategoryDetailActivity getParentActivity() {
        if (parentActivity == null) {
            parentActivity = (ShoppingCategoryDetailActivity) getActivity();
        }
        return parentActivity;
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        if (categoryDetailDialog != null && categoryDetailDialog.isShowing()) {
            categoryDetailDialog.dismiss();
        }
        if (categoryAllDialog != null && categoryAllDialog.isShowing()) {
            categoryAllDialog.dismiss();
        }
        super.onDestroy();
    }

    private void hideCategoryDialog() {
        if (categoryDetailDialog != null) {
            categoryDetailDialog.dismiss();
        }
    }

    private void showCategoryDialog() {
        if (categoryDetailDialog != null && categoryDetailDialog.isShowing()) {
            return;
        }
        if (categoryDetailDialog == null) {
            View dialogView = getActivity().getLayoutInflater()
                    .inflate(R.layout.dialog_buy_work_list, null);
            categoryDetailDialog = new ShoppingCategoryDetailDialog(getContext(),
                    R.style.BubbleDialogTheme);
            categoryDetailDialog.setContentView(dialogView);
            categoryDetailDialog.setCategoryMarks(categoryMarks);
            categoryDetailDialog.setOnConfirmListener(this);
            categoryDetailDialog.hidePriceLayout();
            categoryDetailDialog.hideClip();
            categoryDetailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
        }
        categoryDetailDialog.show();
    }

    private void hideCategoryAllDialog() {
        if (categoryAllDialog != null) {
            categoryAllDialog.dismiss();
        }
    }

    private void showCategoryAllDialog() {
        if (categoryAllDialog != null && categoryAllDialog.isShowing()) {
            return;
        }
        if (categoryAllDialog == null) {
            View dialogView = getActivity().getLayoutInflater()
                    .inflate(R.layout.dialog_filter_recycer_view, null);
            categoryAllDialog = new ShoppingCategoryAllCategoryDialog(getContext(),
                    R.style.BubbleDialogTheme);
            categoryAllDialog.setContentView(dialogView);
            categoryAllDialog.setShopCategories(parentActivity.getChildCategories());
            categoryAllDialog.setOnItemClickListener(this);
            categoryAllDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
        }
        categoryAllDialog.show();
    }


    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            return;
        }

        //专题
        String categoryUrl = Constants.HttpPath.GET_SHOP_CATEGORY_SUB_PAGE + "?category_id=" +
                categoryId + "&level=2";
        Observable<Map<Long, ProductTopic>> topicObb = ProductApi.getProductSubPage(categoryUrl);
        //筛选
        Observable<HljHttpData<List<CategoryMark>>> tagObb = ProductApi.getShopProductTags(
                categoryId);
        //婚品列表
        Observable<ProductSearchResultList> productObb = ProductApi.getCategoryDetailProductList(
                categoryId,
                selectTags,
                order,
                1);
        Observable<ResultZip> zipObb = Observable.zip(topicObb,
                tagObb,
                productObb,
                new Func3<Map<Long, ProductTopic>, HljHttpData<List<CategoryMark>>,
                        ProductSearchResultList, ResultZip>() {

                    @Override
                    public ResultZip call(
                            Map<Long, ProductTopic> longProductTopicMap,
                            HljHttpData<List<CategoryMark>> categoryMarkData,
                            ProductSearchResultList productSearchResultList) {
                        ResultZip zip = new ResultZip();
                        if (showFilter) {
                            zip.topicMap = null;
                        } else {
                            zip.topicMap = longProductTopicMap;
                        }
                        if (categoryMarkData != null) {
                            zip.marks = categoryMarkData.getData();
                        }
                        zip.products = productSearchResultList;
                        return zip;
                    }
                });

        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setContent(resultZip);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(scrollableLayout)
                .setProgressBar(refreshView == null ? progressBar : null)
                .build();
        zipObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<ProductSearchResultList> pageObservable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<ProductSearchResultList>() {
                    @Override
                    public Observable<ProductSearchResultList> onNextPage(int page) {
                        return ProductApi.getCategoryDetailProductList(categoryId,
                                selectTags,
                                order,
                                page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResultList>() {
                    @Override
                    public void onNext(ProductSearchResultList productSearchResultList) {
                        if (productSearchResultList != null && productSearchResultList
                                .getProducts() != null) {
                            adapter.addProducts(productSearchResultList.getProducts());
                        }
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void setContent(ResultZip zip) {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        topicMap.clear();
        if (zip.topicMap != null) {
            topicMap.putAll(zip.topicMap);
        }
        currentTopic = topicMap.get(categoryId);
        setImage(currentTopic);
        categoryMarks.clear();
        if (zip.marks != null) {
            categoryMarks.addAll(zip.marks);
            for (CategoryMark categoryMark : categoryMarks) {
                if (categoryMark.getChildren() != null && !categoryMark.getChildren()
                        .isEmpty()) {
                    categoryMark.getChildren()
                            .add(0, allCategoryMark);
                }
            }
        }
        if (!showFilter) {
            if (categoryMarks.isEmpty()) {
                cbFiltrate.setEnabled(false);
                tvFiltrate.setTextColor(getResources().getColor(R.color.colorGray));
            } else {
                cbFiltrate.setEnabled(true);
                tvFiltrate.setTextColor(getResources().getColor(R.color.colorBlack3));
            }
        }
        if (categoryDetailDialog != null) {
            categoryDetailDialog.setCategoryMarks(categoryMarks);
        }
        ProductSearchResultList productSearchResultList = zip.products;
        int pageCount = 0;
        List<ShopProduct> products = null;
        if (productSearchResultList != null) {
            pageCount = productSearchResultList.getPageCount();
            products = productSearchResultList.getProducts();
        }
        adapter.setProducts(products);
        initPagination(pageCount);
        if (CommonUtil.isCollectionEmpty(products)) {
            emptyView.showEmptyView();
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.hideEmptyView();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setImage(ProductTopic topic) {
        if (topic == null || topic.getId() == 0) {
            imgSubPage.setVisibility(View.GONE);
            imgLayout.setVisibility(View.GONE);
            return;
        } else {
            imgSubPage.setVisibility(View.VISIBLE);
            imgLayout.setVisibility(View.VISIBLE);
        }
        String path = topic.getImgTitle();
        Glide.with(this)
                .load(ImagePath.buildPath(path)
                        .width(CommonUtil.getDeviceSize(getContext()).x)
                        .height((int) (CommonUtil.getDeviceSize(getContext()).x * 1.0f / 2))
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgSubPage);
    }

    @OnClick(R.id.img_sub_page)
    void onSunPageImage() {
        if (currentTopic == null || currentTopic.getId() == 0) {
            return;
        }
        if (currentTopic.getType() == 3) {
            Intent intent = new Intent(getContext(), SubPageDetailActivity.class);
            intent.putExtra("id", currentTopic.getEntityId());
            intent.putExtra("productSubPageId", currentTopic.getId());
            getContext().startActivity(intent);
            ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        } else if (!TextUtils.isEmpty(currentTopic.getGotoUrl())) {
            HljWeb.startWebView((Activity) getContext(), currentTopic.getGotoUrl());
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    private void onCheckScrollTop() {
        if (scrollableLayout != null) {
            if (scrollableLayout.getRefreshableView()
                    .getScrollY() == 0) {
                int top = imgSubPage.getHeight();
                top += (top == 0 ? 0 : CommonUtil.dp2px(getContext(), 10));
                ScrollAnimation animation = new ScrollAnimation(0, top);
                animation.setDuration(150);
                scrollableLayout.getRefreshableView()
                        .startAnimation(animation);
            }
        }
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
    }

    private void refreshProduct() {
        if (refreshProductSubscriber != null && !refreshProductSubscriber.isUnsubscribed()) {
            return;
        }
        onCheckScrollTop();
        //婚品列表
        Observable<ProductSearchResultList> productObb = ProductApi.getCategoryDetailProductList(
                categoryId,
                selectTags,
                order,
                1);
        refreshProductSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<ProductSearchResultList>() {
                    @Override
                    public void onNext(ProductSearchResultList productSearchResultList) {
                        adapter.setProducts(productSearchResultList.getProducts());
                        initPagination(productSearchResultList.getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setProgressBar(progressBar)
                .build();
        productObb.subscribe(refreshProductSubscriber);
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
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
                    R.anim.shopping_slide_out_bottom);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
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

    private int getScrollYDistance() {
        int[] positions = new int[2];
        layoutManager.findFirstVisibleItemPositions(positions);
        int position = positions[0];
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int dis = 0;
        if (firstVisibleChildView != null) {
            int itemHeight = firstVisibleChildView.getHeight();
            dis = (position) * itemHeight - firstVisibleChildView.getTop();
        }
        return dis;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView != null && recyclerView.getLayoutManager() != null) {
                boolean show = getScrollYDistance() < CommonUtil.getDeviceSize(getContext()).y;
                if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < 3 && show) {
                    hideFiltrateAnimation();
                } else {
                    showFiltrateAnimation();
                }
            }
        }
    };


    @Override
    public void onConfirm(
            String priceMin, String priceMax, List<CategoryMark> selects) {
        if (selects != null) {
            StringBuilder builder = new StringBuilder();
            for (CategoryMark categoryMark : selects) {
                if (categoryMark.getId() == CategoryMarkFilterAdapter.ALL_CATEGORY_MARK_ID) {
                    continue;
                }
                builder.append(categoryMark.getMark()
                        .getId())
                        .append(",");
            }
            if (builder.lastIndexOf(",") > 0) {
                builder.deleteCharAt(builder.lastIndexOf(","));
            }
            selectTags = builder.toString();
            if (!TextUtils.isEmpty(selectTags)) {
                setTvFiltrateColor(true);
            } else {
                setTvFiltrateColor(false);
            }
            refreshProduct();
        }
    }

    private void setTvFiltrateColor(boolean isFiltrate) {
        if (isFiltrate) {
            tvFiltrate.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvFiltrate.setTextColor(getResources().getColor(R.color.colorBlack3));
        }
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        boolean refresh = false;
        if (checkedId == R.id.cb_default) {
            refresh = true;
            order = SORT_SCORE;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        } else if (checkedId == R.id.cb_price) {
            refresh = false;
        } else if (checkedId == R.id.cb_sold_count) {
            refresh = true;
            order = SORT_SOLD_COUNT;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        } else if (checkedId == R.id.cb_filtrate) {
            refresh = false;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        }

        setTvFiltrateColor(false);
        if (refresh) {
            refreshProduct();
        }
    }

    @OnClick(R.id.cb_price)
    public void onClickedPriceSort() {
        if (TextUtils.equals(order, SORT_PRICE_UP)) {
            order = SORT_PRICE_DOWN;
            imgPrice.setImageResource(R.mipmap.icon_order_desc_18_24);
        } else {
            order = SORT_PRICE_UP;
            imgPrice.setImageResource(R.mipmap.icon_order_asc_18_24);
        }
        setTvFiltrateColor(false);
        refreshProduct();
    }

    @OnClick(R.id.cb_filtrate)
    public void onClickedFiltrate(View view) {
        switch (view.getId()) {
            case R.id.cb_filtrate:
                if (parentActivity == null || parentActivity.isFinishing()) {
                    return;
                }
                if (showFilter) {
                    //当前是全部类别 显示全部下的二级分类
                    if (categoryAllDialog == null || !categoryAllDialog.isShowing()) {
                        showCategoryAllDialog();
                    } else {
                        hideCategoryAllDialog();
                    }
                } else {
                    if (categoryDetailDialog == null || !categoryDetailDialog.isShowing()) {
                        showCategoryDialog();
                    } else {
                        hideCategoryDialog();
                    }
                }
                break;
        }
    }

    @Override
    public void onViewClick(ShopCategory shopCategory) {
        if (parentActivity == null || parentActivity.isFinishing()) {
            return;
        }
        parentActivity.setPage(shopCategory);
        hideCategoryAllDialog();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private StaggeredGridLayoutManager.LayoutParams lp;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int left = lp.getSpanIndex() == 0 ? 0 : space / 2;
            int right = lp.getSpanIndex() == 0 ? space / 2 : 0;
            outRect.set(left, space, right, 0);
        }
    }

    private class ScrollAnimation extends Animation {
        int startY;
        int endY;

        private ScrollAnimation(int startY, int endY) {
            this.startY = startY;
            this.endY = endY;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            scrollableLayout.getRefreshableView()
                    .scrollTo(0, (int) (startY - (startY - endY) * interpolatedTime));
        }
    }


    class ResultZip {
        Map<Long, ProductTopic> topicMap;
        List<CategoryMark> marks;
        ProductSearchResultList products;
    }
}
