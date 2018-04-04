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

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.models.CategoryMark;
import com.hunliji.hljcommonlibrary.models.SortLabel;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
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

public class ServiceWorkFilterViewHolder {
    @BindView(R2.id.sort_menu)
    LinearLayout sortMenu;
    @BindView(R2.id.area_menu)
    LinearLayout areaMenu;
    @BindView(R2.id.filtrate_menu)
    LinearLayout filtrateMenu;
    @BindView(R2.id.menu_layout)
    LinearLayout menuLayout;
    @BindView(R2.id.service_filter_view)
    RelativeLayout serviceFilterView;
    @BindView(R2.id.img_sort)
    ImageView imgSort;
    @BindView(R2.id.tv_sort)
    TextView tvSort;
    @BindView(R2.id.img_area)
    ImageView imgArea;
    @BindView(R2.id.tv_area)
    TextView tvArea;
    @BindView(R2.id.img_filter)
    ImageView imgFilter;
    @BindView(R2.id.tv_filter)
    TextView tvFilter;

    private MarkFilterViewHolder markFilterViewHolder;
    private SortFilterViewHolder sortFilterViewHolder;
    private AreaFilterViewHolder areaFilterViewHolder;
    private Context mContext;
    private View rootView;
    private long mCid;
    private long parentCid;
    private long childCid;
    private long mEntityId;
    private long secondCategoryId;
    private SortLabel mSortLabel;
    private ChildrenArea parentArea;
    private ChildrenArea childArea;
    private double maxPrice;
    private double minPrice;
    private SparseArray<List<CategoryMark>> mSelects;
    private OnFilterResultListener onFilterResultListener;
    private HljHttpSubscriber initSubscriber;
    private HljHttpSubscriber cidSubscriber;
    private HljHttpSubscriber pidSubscriber;

    public static ServiceWorkFilterViewHolder newInstance(
            Context context,
            long cid,
            long entityId,
            long secondCategoryId,
            OnFilterResultListener listener) {
        View view = View.inflate(context, R.layout.service_work_filter_menu_layout___cv, null);
        ServiceWorkFilterViewHolder holder = new ServiceWorkFilterViewHolder(context,
                view,
                cid,
                entityId,
                secondCategoryId,
                listener);
        holder.init();
        return holder;
    }

    public static ServiceWorkFilterViewHolder newInstance(
            Context context,
            long cid,
            long entityId,
            OnFilterResultListener listener) {
        View view = View.inflate(context, R.layout.service_work_filter_menu_layout___cv, null);
        ServiceWorkFilterViewHolder holder = new ServiceWorkFilterViewHolder(context,
                view,
                cid,
                entityId,
                0L,
                listener);
        holder.init();
        return holder;
    }

    public ServiceWorkFilterViewHolder(
            Context context,
            View view,
            long cid,
            long entityId,
            long secondCategoryId,
            OnFilterResultListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.mCid = cid;
        this.mEntityId = entityId;
        this.secondCategoryId = secondCategoryId;
        this.onFilterResultListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        initFilterView();
        refreshMarksFilter(mEntityId);
        refreshArea(mCid);
        maxPrice = -1;
        minPrice = -1;
        mSortLabel = sortFilterViewHolder.getSortLabel();
    }

    private void initFilterView() {
        sortFilterViewHolder = SortFilterViewHolder.newInstance(mContext,
                SortFilterViewHolder.WORK_SORT,
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
        areaFilterViewHolder = AreaFilterViewHolder.newInstance(mContext,
                new AreaFilterViewHolder.OnAreaFilterListener() {
                    @Override
                    public void onFilterRefresh(ChildrenArea parent, ChildrenArea child) {
                        if (childArea != null && child != null && parentArea != null && parent !=
                                null && parentArea.getCid() == parent.getCid() && childArea.getId
                                () == child.getId()) {
                            return;
                        }
                        parentArea = parent;
                        childArea = child;
                        onRefresh();
                    }
                });
        markFilterViewHolder = MarkFilterViewHolder.newInstance(mContext,
                MarkFilterViewHolder.WORK_FILTER,
                new MarkFilterViewHolder.OnConfirmListener() {
                    @Override
                    public void onConfirm(
                            double min, double max, SparseArray<List<CategoryMark>> selects) {
                        if (minPrice == min && maxPrice == max && (mSelects != null && mSelects
                                .equals(
                                selects))) {
                            return;
                        }
                        minPrice = min;
                        maxPrice = max;
                        mSelects = selects;
                        onRefresh();
                    }

                    @Override
                    public void onConfirm(SparseArray<List<CategoryMark>> tags) {

                    }
                });
        serviceFilterView.removeAllViews();
        serviceFilterView.addView(sortFilterViewHolder.getRootView());
        serviceFilterView.addView(areaFilterViewHolder.getRootView());
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
        if (parentArea != null && (parentArea.getCid() != mCid || (childArea != null && childArea
                .getId() != parentArea.getId()))) {
            tvArea.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            imgArea.setImageResource(R.mipmap.icon_area_primary_50_50);
        } else {
            tvArea.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
            imgArea.setImageResource(R.mipmap.icon_area_gray_50_50);
        }
        if (minPrice >= 0 || maxPrice >= 0 || (mSelects != null && mSelects.size() > 0)) {
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
            long cid = parentArea != null ? parentArea.getCid() : mCid;
            long areaId = childArea != null ? childArea.getId() : 0;
            String sort = mSortLabel != null ? mSortLabel.getValue() : "";
            onFilterResultListener.onFilterResult(sort, cid, areaId, minPrice, maxPrice, tags);
        }
    }

    public void hideFilterView() {
        if (areaFilterViewHolder.isShow()) {
            areaFilterViewHolder.hideMenuAnimation();
        }
        if (sortFilterViewHolder.isShow()) {
            sortFilterViewHolder.hideMenuAnimation();
        }
        if (markFilterViewHolder.isShow()) {
            markFilterViewHolder.hideMenuAnimation();
        }
    }

    public void refreshMarksFilter(long entityId) {
        mEntityId = entityId;
        initTagsData();
    }

    public void refreshArea(long cityId) {
        mCid = cityId;
        getCityDetail();
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isShow() {
        if (sortFilterViewHolder.isShow() || areaFilterViewHolder.isShow() ||
                markFilterViewHolder.isShow()) {
            return true;
        }
        return false;
    }

    private void getCityDetail() {
        if (mCid <= 0) {
            return;
        }
        if (cidSubscriber == null || cidSubscriber.isUnsubscribed()) {
            cidSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<ParentArea>() {
                        @Override
                        public void onNext(ParentArea cityDetailWrapper) {
                            ParentArea parentArea = cityDetailWrapper.getParentArea();
                            if (parentArea == null) {
                                return;
                            }
                            if (parentArea.getLevel() == 1) {
                                //省级
                                parentCid = mCid;
                                getChildrenCities(parentArea.getId());
                            } else if (parentArea.getLevel() == 2 && parentArea.getParentArea()
                                    != null) {
                                childCid = mCid;
                                parentCid = parentArea.getCid();
                                //市级需要再向上取省级
                                getChildrenCities(parentArea.getParentArea()
                                        .getId());
                            }
                        }
                    })
                    .build();
            CommonApi.getCityDetailObb(mCid)
                    .subscribe(cidSubscriber);
        }
    }

    private void getChildrenCities(long pid) {
        if (pidSubscriber == null || pidSubscriber.isUnsubscribed()) {
            pidSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<List<ChildrenArea>>() {
                        @Override
                        public void onNext(List<ChildrenArea> childrenAreas) {
                            if (!CommonUtil.isCollectionEmpty(childrenAreas)) {
                                areaFilterViewHolder.refreshArea(childrenAreas.get(0)
                                        .getChildrenAreas(), parentCid, childCid);
                            }
                        }
                    })
                    .build();
            CommonApi.getChildrenCitiesObb(pid)
                    .subscribe(pidSubscriber);
        }
    }

    private void initTagsData() {
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
            CommonApi.getServiceMarksObb(mEntityId, secondCategoryId)
                    .subscribe(initSubscriber);
        }
    }

    @OnClick(R2.id.sort_menu)
    public void onSortMenu() {
        sortFilterViewHolder.showSortView();
    }

    @OnClick(R2.id.area_menu)
    public void onAreaMenu() {
        if (mCid == 0) {
            //全国跳转到城市选择界面
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.CITY_LIST_ACTIVITY)
                    .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                    .navigation(mContext);
            return;
        }
        areaFilterViewHolder.showAreaView();
    }

    @OnClick(R2.id.filtrate_menu)
    public void onFiltrateMenu() {
        markFilterViewHolder.resetFilter(minPrice, maxPrice, mSelects);
        markFilterViewHolder.showMarkFilterView();
    }

    public interface OnFilterResultListener {
        void onFilterResult(
                String sort,
                long cid,
                long areaId,
                double minPrice,
                double maxPrice,
                List<String> tags);
    }

    public void setOnFilterResultListener(OnFilterResultListener onFilterResultListener) {
        this.onFilterResultListener = onFilterResultListener;
    }

    public void onDestroy() {
        CommonUtil.unSubscribeSubs(initSubscriber, cidSubscriber, pidSubscriber);
    }

}
