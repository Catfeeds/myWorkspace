package me.suncloud.marrymemo.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.SelectMode;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.PosterUtil;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Properties3Util;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.finder.SubPageDetailActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * 婚品分类页面
 * Created by jinxin on 2017/5/8 0008.
 */

public class ShoppingCategoryActivity extends HljBaseNoBarActivity {

    private final long POPULAR_ID = -10L;
    private final int DATA_POSTER = -11;
    private final int DATA_SHOP_CATEGORY = -12;

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.btn_shopping_cart)
    ImageButton btnShoppingCart;
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.recycler_view_category)
    RecyclerView recyclerViewCategory;
    @BindView(R.id.recycler_view_content)
    RecyclerView recyclerViewContent;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;

    private NoticeUtil noticeUtil;
    private List<SelectMode<ShopCategory>> leftCategories;
    private LeftCategoryAdapter leftCategoryAdapter;
    private RightCategoryAdapter rightCategoryAdapter;
    private MeasureSize measureSize;
    private GridLayoutManager rightManager;
    private City mCity;
    private HljHttpSubscriber initSubscriber;
    private Map<Long, ProductTopic> topicMap;
    private ProductTopic currentTopic;//专题
    private Poster currentPoster;//专题
    private View rightFooterView;//右侧列表footerview 多页用来撑开距离
    private int currentDataMode;
    private List<Poster> singlePosters;
    private List<Poster> childPosters;
    private List<ShopCategory> childCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_category);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initConstants();
        initRecyclerView();
        initLoad();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerViewCategory, "left_level1_list");
        HljVTTagger.tagViewParentName(recyclerViewContent, "right_level2_list");
    }

    private void initConstants() {
        mCity = Session.getInstance()
                .getMyCity(this);
        measureSize = new MeasureSize(this);
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        leftCategories = new ArrayList<>();
    }

    private void initRecyclerView() {
        //leftRecyclerView
        leftCategoryAdapter = new LeftCategoryAdapter();
        LinearLayoutManager leftManager = new LinearLayoutManager(this);
        leftManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCategory.setLayoutManager(leftManager);
        recyclerViewCategory.setAdapter(leftCategoryAdapter);

        //rightRecyclerView
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration();
        rightCategoryAdapter = new RightCategoryAdapter();
        rightManager = new GridLayoutManager(this, 3);
        rightManager.setOrientation(GridLayoutManager.VERTICAL);
        rightManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    if (currentTopic != null || currentPoster != null) {
                        return 3;
                    } else {
                        return 1;
                    }
                } else if (position == rightCategoryAdapter.getItemCount() - 1) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        recyclerViewContent.setLayoutManager(rightManager);
        recyclerViewContent.addItemDecoration(itemDecoration);
        recyclerViewContent.setAdapter(rightCategoryAdapter);
        recyclerViewContent.setPadding(measureSize.recyclePadding,
                recyclerViewContent.getPaddingTop(),
                measureSize.recyclePadding,
                recyclerViewContent.getPaddingBottom());
    }

    private void initLoad() {
        Observable<PosterData> bannerObb = CommonApi.getBanner(this,
                HljCommon.BLOCK_ID.SHOPPING_CATEGORY,
                mCity.getId());
        Observable<List<ShopCategory>> categoryObb = Properties3Util.getPropertiesObb(this, true);
        String categoryUrl = Constants.HttpPath.GET_SHOP_CATEGORY_SUB_PAGE +
                "?category_id=0&level=1";
        Observable<Map<Long, ProductTopic>> topicObb = ProductApi.getProductSubPage(categoryUrl);

        Observable<ResultZip> zipObb = Observable.zip(bannerObb,
                categoryObb,
                topicObb,
                new Func3<PosterData, List<ShopCategory>, Map<Long, ProductTopic>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            PosterData posterData,
                            List<ShopCategory> shopCategories,
                            Map<Long, ProductTopic> longProductTopicMap) {
                        ResultZip zip = new ResultZip();
                        zip.posterData = posterData;
                        zip.shopCategories = shopCategories;
                        zip.subPages = longProductTopicMap;
                        return zip;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        initData(resultZip);
                    }
                })
                .build();
        zipObb.subscribe(initSubscriber);
    }

    private void initData(ResultZip zip) {
        if (zip == null) {
            return;
        }

        List<ShopCategory> shopCategories = zip.shopCategories;
        if (shopCategories != null) {
            for (ShopCategory shopCategory : shopCategories) {
                SelectMode<ShopCategory> sp = new SelectMode<>();
                sp.setSelected(false);
                sp.setMode(shopCategory);
                leftCategories.add(sp);
            }
        }

        PosterData posterData = zip.posterData;
        if (posterData != null) {
            boolean add = false;
            singlePosters = PosterUtil.getPosterList(posterData.getFloors(),
                    Constants.POST_SITES.SITE_POP_RECOMMAND_FLOOR1,
                    false);
            if (!singlePosters.isEmpty()) {
                add = true;
            }
            childPosters = PosterUtil.getPosterList(posterData.getFloors(),
                    Constants.POST_SITES.SITE_POP_RECOMMAND_FLOOR2,
                    false);
            if (!childPosters.isEmpty()) {
                add = true;
            }
            if (add) {
                //添加流行推荐
                ShopCategory popular = new ShopCategory();
                popular.setName(getString(R.string.label_popular_recommend));
                popular.setId(POPULAR_ID);
                SelectMode<ShopCategory> sPopular = new SelectMode<>();
                sPopular.setMode(popular);
                sPopular.setSelected(false);
                leftCategories.add(0, sPopular);
            }
        }
        if (topicMap == null) {
            topicMap = new HashMap<>();
        }
        if (zip.subPages != null) {
            topicMap.putAll(zip.subPages);
        }
        if (!leftCategories.isEmpty()) {
            long parentId = getIntent().getLongExtra("parentId", 0);
            int position = 0;
            for (int i = 0, size = leftCategories.size(); i < size; i++) {
                ShopCategory p = leftCategories.get(i)
                        .getMode();
                if (p != null && p.getId() == parentId) {
                    position = i;
                    break;
                }
            }
            leftCategories.get(position)
                    .setSelected(true);
            initRightListData(position);
        }
        leftCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化right recyclerView的数据
     *
     * @param position
     */
    private void initRightListData(int position) {
        ShopCategory shopCategory = leftCategories.get(position)
                .getMode();
        currentDataMode = shopCategory.getId() == POPULAR_ID ? DATA_POSTER : DATA_SHOP_CATEGORY;
        currentTopic = null;
        currentPoster = null;
        if (currentDataMode == DATA_SHOP_CATEGORY) {
            if (childCategories == null) {
                childCategories = new ArrayList<>();
            }
            childCategories.clear();
            if (shopCategory.getChildren() != null) {
                childCategories.addAll(shopCategory.getChildren());
            }
            currentTopic = topicMap.get(shopCategory.getId());
        } else if (currentDataMode == DATA_POSTER) {
            currentPoster = singlePosters.get(position);
        }
        rightCategoryAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_search)
    void onSearch() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE,
                NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_shopping_cart)
    void onShoppingCard() {
        if (Util.loginBindChecked(this, Constants.Login.SHOP_CART_LOGIN)) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.msg_layout)
    void onMessage() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onResume() {
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(initSubscriber);
        super.onFinish();
    }

    class LeftCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final int ITEM_ITEM = 10;
        final int ITEM_FOOTER = 11;

        private View footerView;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_ITEM:
                    View view = getLayoutInflater().inflate(R.layout.left_shopping_category_item,
                            parent,
                            false);
                    return new CategoryViewHolder(view);
                case ITEM_FOOTER:
                    if (footerView == null) {
                        footerView = new View(parent.getContext());
                    }
                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                            .LayoutParams.MATCH_PARENT,
                            CommonUtil.dp2px(parent.getContext(), 50));
                    footerView.setLayoutParams(params);
                    return new ExtraViewHolder<R>(footerView);
                default:
                    return null;
            }
        }

        private void initTracker(View view, ShopCategory shopCategory, int position) {
            if (shopCategory.getId() == POPULAR_ID) {
                HljVTTagger.buildTagger(view)
                        .tagName("item")
                        .atPosition(position)
                        .hitTag();
            } else {
                HljVTTagger.buildTagger(view)
                        .tagName("product_category_item")
                        .atPosition(position)
                        .dataId(shopCategory.getId())
                        .dataType("ProductCategory")
                        .hitTag();
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewType = getItemViewType(position);
            CategoryViewHolder holder = null;
            if (viewType == ITEM_ITEM) {
                holder = (CategoryViewHolder) viewHolder;
            }
            if (holder == null) {
                return;
            }
            SelectMode<ShopCategory> sp = leftCategories.get(position);

            initTracker(holder.tvCategory, sp.getMode(), position);

            holder.tvCategory.setText(sp.getMode()
                    .getName());
            holder.tvCategory.setTag(position);
            if (sp.isSelected()) {
                holder.viewLine.setVisibility(View.VISIBLE);
                holder.tvCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                holder.viewLine.setVisibility(View.GONE);
                holder.tvCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                holder.tvCategory.setTextColor(getResources().getColor(R.color.colorBlack3));
            }
            holder.tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    for (int i = 0, size = leftCategories.size(); i < size; i++) {
                        SelectMode<ShopCategory> sp = leftCategories.get(i);
                        if (i != position) {
                            sp.setSelected(false);
                        } else {
                            if (!sp.isSelected()) {
                                sp.setSelected(true);
                            }
                        }
                    }
                    leftCategoryAdapter.notifyDataSetChanged();
                    initRightListData(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return leftCategories.size() + (leftCategories.isEmpty() ? 0 : 1);
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position != getItemCount() - 1) {
                type = ITEM_ITEM;
            } else {
                type = ITEM_FOOTER;
            }
            return type;
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_line)
        View viewLine;
        @BindView(R.id.tv_category)
        TextView tvCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class RightCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final int ITEM_HEADER = 11;
        final int ITEM_ITEM = 12;
        final int ITEM_FOOTER = 13;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            switch (viewType) {
                case ITEM_HEADER:
                    itemView = getLayoutInflater().inflate(R.layout.image_item, parent, false);
                    return new RightBannerViewHolder(itemView);
                case ITEM_ITEM:
                    itemView = getLayoutInflater().inflate(R.layout.right_shopping_category_item,
                            parent,
                            false);
                    return new RightContentViewHolder(itemView);
                case ITEM_FOOTER:
                    if (rightFooterView == null) {
                        rightFooterView = new View(parent.getContext());
                    }
                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                            .LayoutParams.MATCH_PARENT,
                            measureSize.imgCategoryWidth);
                    rightFooterView.setLayoutParams(params);
                    return new ExtraViewHolder<R>(rightFooterView);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case ITEM_HEADER:
                    RightBannerViewHolder bannerViewHolder = (RightBannerViewHolder) holder;
                    if (currentDataMode == DATA_POSTER) {
                        bannerViewHolder.setImage(currentPoster);
                    } else if (currentDataMode == DATA_SHOP_CATEGORY) {
                        bannerViewHolder.setImage(currentTopic);
                    }
                    break;
                case ITEM_ITEM:
                    RightContentViewHolder contentViewHolder = (RightContentViewHolder) holder;
                    if (currentPoster != null || currentTopic != null) {
                        position--;
                    }
                    if (currentDataMode == DATA_POSTER) {
                        Poster poster = childPosters.get(position);
                        contentViewHolder.setContent(poster, position);
                    } else if (currentDataMode == DATA_SHOP_CATEGORY) {
                        ShopCategory p = childCategories.get(position);
                        contentViewHolder.setContent(p, position);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (currentDataMode == DATA_POSTER) {
                if (!singlePosters.isEmpty()) {
                    count++;
                }
                count += childPosters.size();
            } else if (currentDataMode == DATA_SHOP_CATEGORY) {
                if (currentTopic != null) {
                    count++;
                }
                count += childCategories.size();
            }
            return count + (count == 0 ? 0 : 1);
        }

        @Override
        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);
            if (currentDataMode == DATA_POSTER) {
                if (position == 0) {
                    if (!singlePosters.isEmpty()) {
                        type = ITEM_HEADER;
                    } else {
                        type = ITEM_ITEM;
                    }
                } else if (position == getItemCount() - 1) {
                    type = ITEM_FOOTER;
                } else {
                    type = ITEM_ITEM;
                }
            } else if (currentDataMode == DATA_SHOP_CATEGORY) {
                if (position == 0) {
                    if (currentTopic != null) {
                        type = ITEM_HEADER;
                    } else {
                        type = ITEM_ITEM;
                    }
                } else if (position == getItemCount() - 1) {
                    type = ITEM_FOOTER;
                } else {
                    type = ITEM_ITEM;
                }
            }
            return type;

        }
    }

    class RightBannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;

        private ProductTopic topic;
        private Poster poster;

        public RightBannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            image.getLayoutParams().width = measureSize.bannerWidth;
            image.getLayoutParams().height = measureSize.bannerHeight;
        }

        @OnClick(R.id.image)
        void onImageClick() {
            if (currentDataMode == DATA_SHOP_CATEGORY) {
                if (topic != null) {
                    if (topic.getType() == 3) {
                        Intent intent = new Intent(ShoppingCategoryActivity.this,
                                SubPageDetailActivity.class);
                        intent.putExtra("id", topic.getEntityId());
                        intent.putExtra("productSubPageId", topic.getId());
                        ShoppingCategoryActivity.this.startActivity(intent);
                        ShoppingCategoryActivity.this.overridePendingTransition(R.anim
                                        .slide_in_right,
                                R.anim.activity_anim_default);
                    } else if (!TextUtils.isEmpty(topic.getGotoUrl())) {
                        HljWeb.startWebView(ShoppingCategoryActivity.this, topic.getGotoUrl());
                    }
                }
            } else if (currentDataMode == DATA_POSTER) {
                if (poster != null) {
                    BannerUtil.bannerAction(ShoppingCategoryActivity.this,
                            poster,
                            mCity,
                            false,
                            null);
                }
            }
        }


        private void initTracker(View view, Object object) {
            if (object instanceof ProductTopic) {
                HljVTTagger.buildTagger(view)
                        .tagName("top_section_item")
                        .dataId(((ProductTopic) object).getId())
                        .dataType("ShopProductSubPage")
                        .hitTag();
            } else {
                HljVTTagger.buildTagger(view)
                        .clear();
            }
        }

        public void setImage(ProductTopic topic) {
            initTracker(image, topic);
            if (topic == null || topic.getId() <= 0) {
                return;
            }
            this.topic = topic;
            setImage(topic.getImgTitle());
        }

        public void setImage(Poster poster) {
            initTracker(image, poster);
            if (poster == null || poster.getId() <= 0) {
                return;
            }
            this.poster = poster;
            setImage(poster.getPath());
        }

        private void setImage(String path) {
            Glide.with(ShoppingCategoryActivity.this)
                    .load(ImagePath.buildPath(path)
                            .width(measureSize.bannerWidth)
                            .height(measureSize.bannerHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(image);
        }
    }

    class RightContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_category)
        ImageView imgCategory;
        @BindView(R.id.tv_category)
        TextView tvCategory;

        private ShopCategory shopCategory;
        private Poster poster;

        public RightContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            imgCategory.getLayoutParams().width = measureSize.imgCategoryWidth;
            imgCategory.getLayoutParams().height = measureSize.imgCategoryWidth;
        }

        private void initTracker(View view, Object object, int position) {
            if (object instanceof ShopCategory) {
                HljVTTagger.buildTagger(view)
                        .tagName("bottom_section_item_")
                        .atPosition(position)
                        .dataId(((ShopCategory) object).getId())
                        .dataType("ProductCategory")
                        .hitTag();
            } else {
                HljVTTagger.buildTagger(view)
                        .clear();
            }
        }

        public void setContent(ShopCategory shopCategory, int position) {
            initTracker(itemView, shopCategory, position);
            if (shopCategory == null || shopCategory.getId() <= 0) {
                return;
            }
            this.shopCategory = shopCategory;
            setViewContent(shopCategory.getName(), shopCategory.getCoverImage());
        }

        public void setContent(Poster poster, int position) {
            initTracker(itemView, poster, position);
            if (poster == null || poster.getId() <= 0) {
                return;
            }
            this.poster = poster;
            setViewContent(poster.getTitle(), poster.getPath());
        }

        private void setViewContent(String title, String path) {
            tvCategory.setText(title);
            Glide.with(ShoppingCategoryActivity.this)
                    .load(ImagePath.buildPath(path)
                            .width(measureSize.imgCategoryWidth)
                            .height(measureSize.imgCategoryWidth)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgCategory);
        }

        @Override
        public void onClick(View v) {
            if (currentDataMode == DATA_SHOP_CATEGORY) {
                if (shopCategory != null) {
                    Intent intent = new Intent(ShoppingCategoryActivity.this,
                            ShoppingCategoryDetailActivity.class);
                    intent.putExtra("parentId", shopCategory.getParentId());
                    intent.putExtra("childId", shopCategory.getId());
                    ShoppingCategoryActivity.this.startActivity(intent);
                    ShoppingCategoryActivity.this.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            } else if (currentDataMode == DATA_POSTER) {
                if (poster != null) {
                    BannerUtil.bannerAction(ShoppingCategoryActivity.this,
                            poster,
                            mCity,
                            false,
                            null);
                }
            }
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = rightManager.getPosition(view);
            if (currentTopic != null || currentPoster != null) {
                if (position == 0) {
                    outRect.left = measureSize.bannerLeft;
                    outRect.right = measureSize.bannerLeft;
                    outRect.top = measureSize.bannerTop;
                } else {
                    position--;
                    switch (position % 3) {
                        case 0:
                            outRect.left = measureSize.middleSpace;
                            outRect.right = measureSize.middleSpace;
                            outRect.top = measureSize.leftSpace;
                            break;
                        case 1:
                            outRect.left = measureSize.middleSpace;
                            outRect.right = measureSize.middleSpace;
                            outRect.top = measureSize.leftSpace;
                            break;
                        case 2:
                            outRect.left = measureSize.middleSpace;
                            outRect.right = measureSize.middleSpace;
                            outRect.top = measureSize.leftSpace;
                            break;
                    }
                }
            } else {
                switch (position % 3) {
                    case 0:
                        outRect.left = measureSize.middleSpace;
                        outRect.right = measureSize.middleSpace;
                        outRect.top = measureSize.leftSpace;
                        break;
                    case 1:
                        outRect.left = measureSize.middleSpace;
                        outRect.right = measureSize.middleSpace;
                        outRect.top = measureSize.leftSpace;
                        break;
                    case 2:
                        outRect.left = measureSize.middleSpace;
                        outRect.right = measureSize.middleSpace;
                        outRect.top = measureSize.leftSpace;
                        break;
                }
            }
        }
    }

    class MeasureSize {
        public int bannerWidth;
        public int bannerHeight;
        public int leftSpace;
        public int middleSpace;
        public int imgCategoryWidth;
        public int bannerLeft;
        public int bannerTop;
        public int recyclePadding;

        public MeasureSize(Context context) {
            int screenWidth = CommonUtil.getDeviceSize(context).x;
            int right = Math.round(screenWidth * 1.0f * 59 / (59 + 21));
            bannerWidth = right - CommonUtil.dp2px(context, 24);
            bannerLeft = Math.round(screenWidth * 1.0f * 1 / 320);
            bannerTop = CommonUtil.dp2px(context, 16);
            bannerHeight = Math.round(bannerWidth * 2 / 5);
            leftSpace = Math.round(screenWidth * 1.0f * 3 / 40);
            middleSpace = Math.round(screenWidth * 1.0f * 7 / 160);
            imgCategoryWidth = Math.round((right - middleSpace * 4 - leftSpace * 2) * 1.0f / 3);
            recyclePadding = Math.round(screenWidth * 1.0f * 1 / 32);
        }
    }

    class ResultZip {
        //流行推荐
        PosterData posterData;
        //婚品分类
        List<ShopCategory> shopCategories;
        //subPage
        Map<Long, ProductTopic> subPages;
    }
}
