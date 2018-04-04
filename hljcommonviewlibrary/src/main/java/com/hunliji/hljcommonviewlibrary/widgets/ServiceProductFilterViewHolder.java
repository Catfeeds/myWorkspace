package com.hunliji.hljcommonviewlibrary.widgets;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.SortLabel;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/7/28.本地服务二级频道底部筛选视图
 */

public class ServiceProductFilterViewHolder {

    @BindView(R2.id.img_sort)
    ImageView imgSort;
    @BindView(R2.id.tv_sort)
    TextView tvSort;
    @BindView(R2.id.sort_menu)
    LinearLayout sortMenu;
    @BindView(R2.id.img_price)
    ImageView imgPrice;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.price_menu)
    LinearLayout priceMenu;
    @BindView(R2.id.img_filter)
    ImageView imgFilter;
    @BindView(R2.id.tv_filter)
    TextView tvFilter;
    @BindView(R2.id.filtrate_menu)
    LinearLayout filtrateMenu;
    @BindView(R2.id.menu_layout)
    LinearLayout menuLayout;
    @BindView(R2.id.service_filter_view)
    RelativeLayout serviceFilterView;

    private MarkFilterViewHolder markFilterViewHolder;
    private PriceFilterViewHolder priceFilterViewHolder;
    private SortFilterViewHolder sortFilterViewHolder;
    private Context mContext;
    private View rootView;
    private long mEntityId;
    private SortLabel mSortLabel;
    private double maxPrice;
    private double minPrice;
    private SparseArray<List<CategoryMark>> mSelects;
    private OnFilterResultListener onFilterResultListener;
    private HljHttpSubscriber initSubscriber;

    public static ServiceProductFilterViewHolder newInstance(
            Context context, long entityId, OnFilterResultListener listener) {
        View view = View.inflate(context, R.layout.service_product_filter_menu_layout___cv, null);
        ServiceProductFilterViewHolder holder = new ServiceProductFilterViewHolder(context,
                view,
                entityId,
                listener);
        holder.init();
        return holder;
    }

    public ServiceProductFilterViewHolder(
            Context context, View view, long entityId, OnFilterResultListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.mEntityId = entityId;
        this.onFilterResultListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        initFilterView();
        refreshMarksFilter(mEntityId);
        maxPrice = -1;
        minPrice = -1;
        mSortLabel = sortFilterViewHolder.getSortLabel();
    }

    private void initFilterView() {
        sortFilterViewHolder = SortFilterViewHolder.newInstance(mContext,
                SortFilterViewHolder.PRODUCT_SORT,
                new SortFilterViewHolder.OnSortFilterListener() {
                    @Override
                    public void onFilterRefresh(SortLabel sortLabel) {
                        if (mSortLabel != null && mSortLabel.getValue()
                                .equals(sortLabel.getValue())) {
                            return;
                        }
                        mSortLabel = sortLabel;
                        onRefresh();
                    }
                });
        priceFilterViewHolder = PriceFilterViewHolder.newInstance(mContext,
                new PriceFilterViewHolder.OnConfirmListener() {

                    @Override
                    public void onConfirm(double priceMax, double priceMin) {
                        if (maxPrice == priceMax && minPrice == priceMin) {
                            return;
                        }
                        maxPrice = priceMax;
                        minPrice = priceMin;
                        onRefresh();
                    }
                });
        markFilterViewHolder = MarkFilterViewHolder.newInstance(mContext,
                MarkFilterViewHolder.PRODUCT_FILTER,
                new MarkFilterViewHolder.OnConfirmListener() {
                    @Override
                    public void onConfirm(
                            double maxPrice,
                            double minPrice,
                            SparseArray<List<CategoryMark>> tags) {
                    }

                    @Override
                    public void onConfirm(SparseArray<List<CategoryMark>> selects) {
                        if (mSelects != null && mSelects.equals(selects)) {
                            return;
                        }
                        mSelects = selects;
                        onRefresh();
                    }
                });
        serviceFilterView.removeAllViews();
        serviceFilterView.addView(sortFilterViewHolder.getRootView());
        serviceFilterView.addView(priceFilterViewHolder.getRootView());
        serviceFilterView.addView(markFilterViewHolder.getRootView());
    }

    private void onRefresh() {
        if (mSortLabel != null && !mSortLabel.getValue()
                .equals("score")) {
            tvSort.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            imgSort.setImageResource(R.mipmap.icon_sort_primary_50_50);
        } else {
            tvSort.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            imgSort.setImageResource(R.mipmap.icon_sort_gray_50_50);
        }
        if (maxPrice >= 0 && minPrice >= 0) {
            tvPrice.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            imgPrice.setImageResource(R.mipmap.icon_price_primary_50_50);
        } else {
            tvPrice.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            imgPrice.setImageResource(R.mipmap.icon_price_gray_50_50);
        }
        if (mSelects != null && mSelects.size() > 0) {
            tvFilter.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            imgFilter.setImageResource(R.mipmap.icon_filter_primary_50_50);
        } else {
            tvFilter.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            imgFilter.setImageResource(R.mipmap.icon_filter_gray_50_50);
        }
        List<String> tags = new ArrayList<>();
        if (mSelects != null) {
            for (int i = 0; i < mSelects.size(); i++) {
                List<CategoryMark> values = mSelects.valueAt(i);
                StringBuilder builder = new StringBuilder();
                for (CategoryMark v : values) {
                    if (v != null && v.getMark()
                            .getId() > 0) {
                        builder.append(v.getMark()
                                .getId())
                                .append(",");
                    }
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.lastIndexOf(","));
                }
                tags.add(builder.toString());
            }
        }
        if (onFilterResultListener != null) {
            String sort = mSortLabel != null ? mSortLabel.getValue() : "";
            onFilterResultListener.onFilterResult(sort, maxPrice, minPrice, tags);
            if (mEntityId == 1) {
                //全部下筛选，不需要保存状态
                long categoryId = 0;
                if (mSelects != null && mSelects.size() > 0) {
                    CategoryMark categoryMark = mSelects.valueAt(0)
                            .get(0);
                    categoryId = categoryMark.getId();
                }
                mSelects = null;
                tvFilter.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
                imgFilter.setImageResource(R.mipmap.icon_filter_gray_50_50);
                onFilterResultListener.onFilterResult(sort, maxPrice, minPrice, categoryId);
            }
        }
    }

    public void setAllProperties(List<ShopCategory> properties) {
        List<CategoryMark> categoryMarks = new ArrayList<>();
        for (ShopCategory shopCategory : properties) {
            CategoryMark categoryMark = new CategoryMark();
            categoryMark.setId(shopCategory.getId());
            Mark mark = new Mark();
            mark.setId(shopCategory.getId());
            mark.setName(shopCategory.getName());
            categoryMark.setMark(mark);
            categoryMarks.add(categoryMark);
        }
        List<CategoryMark> allCategoryMarks = new ArrayList<>();
        CategoryMark categoryMark = new CategoryMark();
        categoryMark.setId(1);
        Mark mark = new Mark();
        mark.setId(1);
        mark.setName("婚纱礼服");
        categoryMark.setMark(mark);
        categoryMark.setChildren(categoryMarks);
        allCategoryMarks.add(categoryMark);
        if (markFilterViewHolder != null) {
            markFilterViewHolder.setProperties(allCategoryMarks);
        }
    }

    public void refreshMarksFilter(long entityId) {
        mEntityId = entityId;
        initTagsData();
    }

    public View getRootView() {
        return rootView;
    }

    private void initTagsData() {
        if (mEntityId == 1) {
            return;
        }
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CategoryMark>>>() {
                        @Override
                        public void onNext(HljHttpData<List<CategoryMark>> data) {
                            if (mContext == null || ((Activity) mContext).isFinishing()) {
                                return;
                            }
                            if (!CommonUtil.isCollectionEmpty(data.getData())) {
                                markFilterViewHolder.setProperties(data.getData());
                            }
                        }
                    })
                    .build();
            CommonApi.getShopProductTags(mEntityId)
                    .subscribe(initSubscriber);
        }
    }


    @OnClick(R2.id.sort_menu)
    public void onSortMenu() {
        sortFilterViewHolder.showSortView();
    }

    @OnClick(R2.id.filtrate_menu)
    public void onFiltrateMenu() {
        markFilterViewHolder.resetMarks(mSelects);
        markFilterViewHolder.showMarkFilterView();
    }

    @OnClick(R2.id.price_menu)
    public void onPriceMenu() {
        priceFilterViewHolder.resetFilter(minPrice, maxPrice);
        priceFilterViewHolder.showPriceFilterView();
    }

    public boolean isShow() {
        if (sortFilterViewHolder.isShow() || priceFilterViewHolder.isShow() ||
                markFilterViewHolder.isShow()) {
            return true;
        }
        return false;
    }

    public void hideFilterView() {
        if (priceFilterViewHolder.isShow()) {
            priceFilterViewHolder.hideMenuAnimation();
        }
        if (sortFilterViewHolder.isShow()) {
            sortFilterViewHolder.hideMenuAnimation();
        }
        if (markFilterViewHolder.isShow()) {
            markFilterViewHolder.hideMenuAnimation();
        }
    }

    public interface OnFilterResultListener {
        void onFilterResult(
                String sort, double maxPrice, double minPrice, List<String> tags);

        void onFilterResult(String sort, double maxPrice, double minPrice, long categoryId);
    }

    public void setOnFilterResultListener(OnFilterResultListener onFilterResultListener) {
        this.onFilterResultListener = onFilterResultListener;
    }

    public void onDestroy() {
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
