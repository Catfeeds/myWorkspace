package com.hunliji.hljcommonviewlibrary.widgets;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.SortLabel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/8/2.排序筛选
 */

public class SortFilterViewHolder implements AdapterView.OnItemClickListener {

    public static final int WORK_SORT = 0;
    public static final int PRODUCT_SORT = 1;

    @BindView(R2.id.sort_menu_list)
    ListView sortMenuList;
    @BindView(R2.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R2.id.menu_bg_layout)
    RelativeLayout menuBgLayout;

    private Context mContext;
    private int sortType;
    private int dividerHeight;
    private ArrayList<SortLabel> sortLabels;
    private SortLabel mSortLabel;
    private boolean isShow;

    private View rootView;
    private FiltrateMenuAdapter sortMenuAdapter;
    private OnSortFilterListener onSortFilterListener;

    public static SortFilterViewHolder newInstance(
            Context context, int sortType, OnSortFilterListener listener) {
        View view = View.inflate(context, R.layout.service_sort_filter_view___cv, null);
        SortFilterViewHolder holder = new SortFilterViewHolder(context, view, sortType, listener);
        holder.init();
        return holder;
    }

    private SortFilterViewHolder(
            Context context, View view, int sortType, OnSortFilterListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.sortType = sortType;
        this.onSortFilterListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        initSortFilterList();
        dividerHeight = Math.max(1, CommonUtil.dp2px(mContext, 1) / 2);
        sortMenuAdapter = new FiltrateMenuAdapter(mContext, R.layout.filtrate_menu_list_item2___cm);
        sortMenuAdapter.setItems(sortLabels);
        sortMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        sortMenuList.setItemsCanFocus(true);
        sortMenuList.setOnItemClickListener(this);
        sortMenuList.setAdapter(sortMenuAdapter);
    }

    public void initSortFilterList() {
        sortLabels = new ArrayList<>();
        if (sortType == WORK_SORT) {
            SortLabel menuItem1 = new SortLabel();
            menuItem1.setName("综合排序");
            menuItem1.setValue("score");
            sortLabels.add(menuItem1);

            SortLabel menuItem2 = new SortLabel();
            menuItem2.setName("最新发布");
            menuItem2.setValue("create_time");
            sortLabels.add(menuItem2);

            SortLabel menuItem3 = new SortLabel();
            menuItem3.setName("价格由高到低");
            menuItem3.setValue("price_down");
            sortLabels.add(menuItem3);

            SortLabel menuItem4 = new SortLabel();
            menuItem4.setName("价格由低到高");
            menuItem4.setValue("price_up");
            sortLabels.add(menuItem4);

            mSortLabel = sortLabels.get(0);
        } else if (sortType == PRODUCT_SORT) {
            SortLabel menuItem1 = new SortLabel();
            menuItem1.setName("综合排序");
            menuItem1.setValue("score");
            sortLabels.add(menuItem1);

            SortLabel menuItem2 = new SortLabel();
            menuItem2.setName("销量由高到低");
            menuItem2.setValue("sold_count");
            sortLabels.add(menuItem2);

            SortLabel menuItem3 = new SortLabel();
            menuItem3.setName("价格由高到低");
            menuItem3.setValue("price_down");
            sortLabels.add(menuItem3);

            SortLabel menuItem4 = new SortLabel();
            menuItem4.setName("价格由低到高");
            menuItem4.setValue("price_up");
            sortLabels.add(menuItem4);

            mSortLabel = sortLabels.get(0);
        }
    }

    public SortLabel getSortLabel() {
        return mSortLabel;
    }

    public boolean isShow() {
        return isShow;
    }

    public View getRootView() {
        return rootView;
    }

    public void showSortView() {
        int position = sortLabels.indexOf(mSortLabel);
        sortMenuList.setVisibility(View.VISIBLE);
        sortMenuList.setDividerHeight(dividerHeight);
        sortMenuList.setItemChecked(position, true);
        showMenuAnimation();
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayoutClicked() {
        hideMenuAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SortLabel sortLabel = (SortLabel) parent.getAdapter()
                .getItem(position);
        mSortLabel = sortLabel;
        if (onSortFilterListener != null) {
            hideMenuAnimation();
            onSortFilterListener.onFilterRefresh(sortLabel);
        }
    }

    public interface OnSortFilterListener {
        void onFilterRefresh(SortLabel sortLabel);
    }

    private void showMenuAnimation() {
        if (isShow) {
            return;
        }
        isShow = true;
        menuBgLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
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

    public void hideMenuAnimation() {
        if (!isShow) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuBgLayout.setVisibility(View.GONE);
                isShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuInfoLayout.startAnimation(animation);
    }

}
