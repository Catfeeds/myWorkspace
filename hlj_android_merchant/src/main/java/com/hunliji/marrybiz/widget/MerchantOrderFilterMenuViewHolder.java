package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.orders.MerchantOrderFilter;

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

public class MerchantOrderFilterMenuViewHolder implements AdapterView.OnItemClickListener {

    @BindView(R.id.single_menu_list)
    ListView singleMenuList;
    @BindView(R.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R.id.menu_bg_layout)
    RelativeLayout menuBgLayout;
    @BindView(R.id.pay_order_filter_view)
    FrameLayout payOrderFilterView;
    @BindView(R.id.filter_menu_view)
    LinearLayout filterMenuView;
    @BindView(R.id.menu_layout)
    RelativeLayout menuLayout;
    private Context mContext;
    private View rootView;
    private int dividerHeight;
    private ArrayList<Label> labels;
    private ArrayList<MerchantOrderFilter> merchantOrderFilters;
    private FiltrateMenuAdapter filterMenuAdapter;
    private OnFilterResultListener onFilterResultListener;
    private MerchantOrderFilter selectFilter;
    private Map<String, Object> filterMap;
    private ArrayList<CheckableLinearLayout> checkViews;
    private HljHttpSubscriber initHttpSubscriber;

    public static MerchantOrderFilterMenuViewHolder newInstance(
            Context context, OnFilterResultListener listener) {
        View view = View.inflate(context, R.layout.merchant_order_filter_menu_layout, null);
        MerchantOrderFilterMenuViewHolder holder = new MerchantOrderFilterMenuViewHolder(context,
                view,
                listener);
        holder.init();
        return holder;
    }

    public MerchantOrderFilterMenuViewHolder(
            Context context, View view, OnFilterResultListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.onFilterResultListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        initFilterView();
    }

    private void initFilterView() {
        labels = new ArrayList<>();
        checkViews = new ArrayList<>();
        merchantOrderFilters = new ArrayList<>();
        filterMap = new HashMap<>();
        initMerchantOrderFilterList();
        dividerHeight = Math.max(1, CommonUtil.dp2px(mContext, 1) / 2);
        filterMenuAdapter = new FiltrateMenuAdapter(mContext,
                R.layout.filtrate_menu_list_item2___cm);
        filterMenuAdapter.setItems(labels);
        singleMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        singleMenuList.setItemsCanFocus(true);
        singleMenuList.setOnItemClickListener(this);
        singleMenuList.setAdapter(filterMenuAdapter);
    }

    public void initMerchantOrderFilterList() {
        if (initHttpSubscriber == null || initHttpSubscriber.isUnsubscribed()) {
            initHttpSubscriber = HljHttpSubscriber.buildSubscriber(mContext)
                    .setOnNextListener(new SubscriberOnNextListener<List<MerchantOrderFilter>>() {

                        @Override
                        public void onNext(List<MerchantOrderFilter> list) {
                            merchantOrderFilters.clear();
                            for (MerchantOrderFilter filter : list) {
                                if (!CommonUtil.isCollectionEmpty(filter.getFilterOptions())) {
                                    merchantOrderFilters.add(filter);
                                }
                            }
                            initFilterMenu();
                        }
                    })
                    .build();
            OrderApi.getMerchantOrderFilterListObb()
                    .subscribe(initHttpSubscriber);
        }
    }

    private void initFilterMenu() {
        filterMenuView.removeAllViews();
        checkViews.clear();
        for (int i = 0; i < merchantOrderFilters.size(); i++) {
            final MerchantOrderFilter merchantOrderFilter = merchantOrderFilters.get(i);
            final CheckableLinearLayout cbFilter = (CheckableLinearLayout) View.inflate(mContext,
                    R.layout.merchant_order_filter_menu_item,
                    null);
            TextView tvFilterName = cbFilter.findViewById(R.id.tv_filter_name);
            tvFilterName.setText(merchantOrderFilter.getFilterTitle());
            checkViews.add(cbFilter);
            final int position = i;
            cbFilter.setOnCheckedChangeListener(new CheckableLinearLayout.OnCheckedChangeListener
                    () {
                @Override
                public void onCheckedChange(View view, boolean checked) {
                    if (checked) {
                        hideMenu(position);
                        selectFilter = merchantOrderFilter;
                        labels.clear();
                        if (!CommonUtil.isCollectionEmpty(merchantOrderFilter.getFilterOptions())) {
                            labels.addAll(merchantOrderFilter.getFilterOptions());
                        }
                        showFilterView();
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

    @OnClick(R.id.menu_bg_layout)
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
            MerchantOrderFilter merchantOrderFilter = merchantOrderFilters.get(i);
            checkView.setChecked(false);
            TextView tvFilterName = checkView.findViewById(R.id.tv_filter_name);
            if (merchantOrderFilter != null) {
                Label label = (Label) filterMap.get(merchantOrderFilter.getFilterKey());
                if (label != null && !TextUtils.isEmpty(label.getValue())) {
                    tvFilterName.setText(label.getName());
                } else {
                    tvFilterName.setText(merchantOrderFilter.getFilterTitle());
                }
            }
        }
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isShow() {
        return false;
    }

    public void showFilterView() {
        Label selectLabel = null;
        if (selectFilter != null) {
            selectLabel = (Label) filterMap.get(selectFilter.getFilterKey());
        }
        int position = labels.indexOf(selectLabel);
        if (position <= 0) {
            position = 0;
        }
        singleMenuList.setVisibility(View.VISIBLE);
        singleMenuList.setDividerHeight(dividerHeight);
        singleMenuList.setItemChecked(position, true);
        filterMenuAdapter.setItems(labels);
        filterMenuAdapter.notifyDataSetChanged();
        showMenuAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Label label = (Label) parent.getAdapter()
                .getItem(position);
        if (onFilterResultListener != null) {
            if (selectFilter == null) {
                return;
            }
            filterMap.put(selectFilter.getFilterKey(), label);
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

    public interface OnFilterResultListener {
        void onFilterResult(Map<String, Object> map);
    }

    public void onDestroy() {
        CommonUtil.unSubscribeSubs(initHttpSubscriber);
    }
}
