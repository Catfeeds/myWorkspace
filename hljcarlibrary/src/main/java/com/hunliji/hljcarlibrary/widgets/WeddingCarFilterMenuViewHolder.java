package com.hunliji.hljcarlibrary.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.adapter.CarLabelFilterAdapter;
import com.hunliji.hljcarlibrary.api.WeddingCarApi;
import com.hunliji.hljcarlibrary.models.CarFilter;
import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/12/11.支付订单筛选视图
 */

public class WeddingCarFilterMenuViewHolder implements AdapterView.OnItemClickListener {

    public static String[] mealTabs = {"品牌", "综合排序"};
    public static String[] optionalTabs = {"品牌", "款型", "颜色", "综合排序"};
    public static String[] mealKeys = {"product_brand_id", "sort"};
    public static String[] optionalKeys = {"product_brand_id", "brand_id", "color_title", "sort"};
    @BindView(R2.id.single_menu_list)
    ListView singleMenuList;
    @BindView(R2.id.tv_property)
    TextView tvProperty;
    @BindView(R2.id.grid_view)
    GridView gridView;
    @BindView(R2.id.btn_reset)
    Button btnReset;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.bottom_action_layout)
    LinearLayout bottomActionLayout;
    @BindView(R2.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R2.id.edit_confirm_view)
    RelativeLayout editConfirmView;
    @BindView(R2.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R2.id.grid_layout)
    LinearLayout gridLayout;
    @BindView(R2.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R2.id.menu_bg_layout)
    RelativeLayout menuBgLayout;
    @BindView(R2.id.pay_order_filter_view)
    FrameLayout payOrderFilterView;
    @BindView(R2.id.filter_menu_view)
    LinearLayout filterMenuView;
    @BindView(R2.id.menu_layout)
    RelativeLayout menuLayout;
    @BindView(R2.id.touch_view)
    View touchView;
    private Context mContext;
    private View rootView;
    private int dividerHeight;
    private long cid;
    private String tab;
    private ArrayList<Label> labels;
    private Map<String, Object> filterMap;
    private ArrayList<CheckableLinearLayout> checkViews;
    private String[] keys;
    private String[] tabs;
    private String filterKey;
    private FiltrateMenuAdapter filterMenuAdapter;
    private CarLabelFilterAdapter carLabelFilterAdapter;
    private OnFilterResultListener onFilterResultListener;
    private HljHttpSubscriber initHttpSubscriber;
    private CarFilter carFilter;
    private ScrollableLayout scrollableLayout;

    public static WeddingCarFilterMenuViewHolder newInstance(
            Context context,
            long cid,
            String tab,
            ScrollableLayout scrollableLayout,
            OnFilterResultListener listener) {
        View view = View.inflate(context, R.layout.wedding_car_filter_layout___car, null);
        WeddingCarFilterMenuViewHolder holder = new WeddingCarFilterMenuViewHolder(context,
                view,
                cid,
                tab,
                scrollableLayout,
                listener);
        holder.init();
        return holder;
    }

    public WeddingCarFilterMenuViewHolder(
            Context context,
            View view,
            long cid,
            String tab,
            ScrollableLayout scrollableLayout,
            OnFilterResultListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.onFilterResultListener = listener;
        this.cid = cid;
        this.tab = tab;
        this.scrollableLayout = scrollableLayout;
        ButterKnife.bind(this, view);
    }

    private void init() {
        initFilterView();
        initFilterMenu();
    }

    private void initFilterView() {
        labels = new ArrayList<>();
        checkViews = new ArrayList<>();
        filterMap = new HashMap<>();
        keys = tab.equals(CarFilter.MEAL_TAB) ? mealKeys : optionalKeys;
        tabs = tab.equals(CarFilter.MEAL_TAB) ? mealTabs : optionalTabs;
        initMerchantOrderFilterList();
        dividerHeight = Math.max(1, CommonUtil.dp2px(mContext, 1) / 2);
        filterMenuAdapter = new FiltrateMenuAdapter(mContext,
                R.layout.filtrate_menu_list_item_car___cm);
        filterMenuAdapter.setItems(labels);
        singleMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        singleMenuList.setItemsCanFocus(true);
        singleMenuList.setOnItemClickListener(this);
        singleMenuList.setAdapter(filterMenuAdapter);
        int filterWidth = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                44)) / 4;
        carLabelFilterAdapter = new CarLabelFilterAdapter(labels, mContext, filterWidth);
        carLabelFilterAdapter.setMultiSelected(false);
        gridView.setAdapter(carLabelFilterAdapter);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuBgLayout.getVisibility() == View.VISIBLE) {
                    scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(true);
                } else {
                    scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    public void initMerchantOrderFilterList() {
        if (initHttpSubscriber == null || initHttpSubscriber.isUnsubscribed()) {
            initHttpSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<CarFilter>() {

                        @Override
                        public void onNext(CarFilter data) {
                            carFilter = data;
                        }
                    })
                    .build();
            WeddingCarApi.getCarFiltersObb(cid, tab)
                    .subscribe(initHttpSubscriber);
        }
    }

    private void initFilterMenu() {
        filterMenuView.removeAllViews();
        checkViews.clear();
        for (int i = 0; i < tabs.length; i++) {
            final CheckableLinearLayout cbFilter = (CheckableLinearLayout) View.inflate(mContext,
                    R.layout.wedding_car_filter_menu_item___car,
                    null);
            TextView tvFilterName = cbFilter.findViewById(R.id.tv_filter_name);
            tvFilterName.setText(tabs[i]);
            checkViews.add(cbFilter);
            final int position = i;
            cbFilter.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener
                    () {
                @Override
                public void onCheckedChange(View view, boolean checked) {
                    if (scrollableLayout != null) {
                        scrollableLayout.scrollToBottom();
                    }
                    if (carFilter == null) {
                        return;
                    }
                    if (checked) {
                        hideMenu(position);
                        labels.clear();
                        List<Label> carLabels = getCarLabels(tabs[position]);
                        filterKey = keys[position];
                        showFilterView(carLabels, tabs[position]);
                    } else {
                        hideMenuAnimation();
                    }
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            filterMenuView.addView(cbFilter, params);
        }
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayoutClicked() {
        hideMenu(checkViews.size());
    }

    /**
     * 筛选单选控制
     *
     * @param position 当前选中的view；关闭id不相同选项
     * @return boolean false 未关闭菜单
     */
    public void hideMenu(int position) {
        for (int i = 0; i < checkViews.size(); i++) {
            CheckableLinearLayout checkView = checkViews.get(i);
            if (i != position) {
                checkView.setChecked(false);
            }
        }
    }

    private void onRefresh() {
        for (int i = 0; i < checkViews.size(); i++) {
            CheckableLinearLayout checkView = checkViews.get(i);
            checkView.setChecked(false);
            TextView tvFilterName = checkView.findViewById(R.id.tv_filter_name);
            filterKey = keys[i];
            Label label = (Label) filterMap.get(filterKey);
            if (label != null) {
                tvFilterName.setText(label.getName());
                tvFilterName.setTextColor(ContextCompat.getColor(mContext, R.color.colorCarGold));
            } else {
                tvFilterName.setText(tabs[i]);
                tvFilterName.setTextColor(ContextCompat.getColor(mContext,
                        R.color.black3_car_gold));
            }
        }
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isShow() {
        return false;
    }

    public void showSelectLabel(int position, long id) {
        List<Label> carLabels = getCarLabels(tabs[position]);
        for (Label label : carLabels) {
            if (label.getId() == id) {
                filterMap.put(keys[position], label);
            }
        }
        if (onFilterResultListener != null) {
            onFilterResultListener.onFilterResult(filterMap);
        }
        onRefresh();
    }

    public void showFilterView(List<Label> list, String tabName) {
        if (CommonUtil.isCollectionEmpty(list) || filterKey == null) {
            return;
        }
        labels.addAll(list);
        Label selectLabel = (Label) filterMap.get(filterKey);
        int position = labels.indexOf(selectLabel);
        if (tabName.equals("综合排序")) {
            singleMenuList.setVisibility(View.VISIBLE);
            gridLayout.setVisibility(View.GONE);
            singleMenuList.setDividerHeight(dividerHeight);
            if (position <= 0) {
                position = 0;
            }
            singleMenuList.setItemChecked(position, true);
            filterMenuAdapter.setItems(labels);
            filterMenuAdapter.notifyDataSetChanged();
        } else {
            singleMenuList.setVisibility(View.GONE);
            gridLayout.setVisibility(View.VISIBLE);
            tvProperty.setText(tabName);
            carLabelFilterAdapter.clearHashMap();
            carLabelFilterAdapter.setChecked(position);
            carLabelFilterAdapter.notifyDataSetChanged();
        }
        showMenuAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Label label = (Label) parent.getAdapter()
                .getItem(position);
        if (onFilterResultListener != null) {
            if (TextUtils.isEmpty(filterKey)) {
                return;
            }
            filterMap.put(filterKey, label);
            onFilterResultListener.onFilterResult(filterMap);
        }
        onRefresh();
    }

    public void hideMenuAnimation() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_top);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                menuInfoLayout.setVisibility(View.VISIBLE);
                menuBgLayout.setBackgroundResource(android.R.color.transparent);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuBgLayout.setVisibility(View.GONE);
                menuInfoLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuInfoLayout.startAnimation(animation);
    }

    private void showMenuAnimation() {
        menuBgLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_top);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                menuInfoLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (menuBgLayout != null) {
                    menuBgLayout.setBackgroundResource(R.color.transparent_black);
                }
            }
        });
        menuInfoLayout.startAnimation(animation);
    }

    //"品牌", "款型", "颜色", "综合排序"
    public List<Label> getCarLabels(String tabName) {
        List<Label> carLabels = null;
        switch (tabName) {
            case "品牌":
                carLabels = carFilter.getProductBrandList();
                break;
            case "款型":
                carLabels = carFilter.getBrandList();
                break;
            case "颜色":
                carLabels = carFilter.getColorList();
                break;
            case "综合排序":
                carLabels = carFilter.getSortList();
                break;
        }

        return carLabels;
    }

    @OnClick(R2.id.btn_reset)
    public void onBtnResetClicked() {
        carLabelFilterAdapter.clearHashMap();
        carLabelFilterAdapter.notifyDataSetChanged();
    }

    @OnClick(R2.id.btn_confirm)
    public void onBtnConfirmClicked() {
        Label label = carLabelFilterAdapter.getCheckItem();
        if (onFilterResultListener != null) {
            if (TextUtils.isEmpty(filterKey)) {
                return;
            }
            filterMap.put(filterKey, label);
            onFilterResultListener.onFilterResult(filterMap);
        }
        onRefresh();
    }

    public interface OnFilterResultListener {
        void onFilterResult(Map<String, Object> map);
    }

    public void onDestroy() {
        CommonUtil.unSubscribeSubs(initHttpSubscriber);
    }
}
