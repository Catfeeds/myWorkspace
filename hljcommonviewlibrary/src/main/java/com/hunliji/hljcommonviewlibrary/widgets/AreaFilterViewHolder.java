package com.hunliji.hljcommonviewlibrary.widgets;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ChildrenArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/8/2.排序筛选
 */

public class AreaFilterViewHolder implements AdapterView.OnItemClickListener {


    @BindView(R2.id.left_menu_list)
    ListView leftMenuList;
    @BindView(R2.id.right_menu_list)
    ListView rightMenuList;
    @BindView(R2.id.list_menu_layout)
    LinearLayout listMenuLayout;
    @BindView(R2.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R2.id.menu_bg_layout)
    RelativeLayout menuBgLayout;

    private Context mContext;
    private ArrayList<ChildrenArea> areaLabels;
    private ChildrenArea parentLabel; //界面中的一级分类
    private ChildrenArea childLabel; //界面中的二级分类

    private View rootView;
    private FiltrateMenuAdapter leftMenuAdapter;
    private FiltrateMenuAdapter rightMenuAdapter;
    private OnAreaFilterListener onAreaFilterListener;
    private boolean isShow;

    public static AreaFilterViewHolder newInstance(
            Context context, OnAreaFilterListener listener) {
        View view = View.inflate(context, R.layout.service_area_filter_view___cv, null);
        AreaFilterViewHolder holder = new AreaFilterViewHolder(context, view, listener);
        holder.init();
        return holder;
    }

    private AreaFilterViewHolder(
            Context context, View view, OnAreaFilterListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.onAreaFilterListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        areaLabels = new ArrayList<>();
        leftMenuAdapter = new FiltrateMenuAdapter(mContext, R.layout.filtrate_menu_list_item___cm);
        leftMenuList.getLayoutParams().height = CommonUtil.getDeviceSize(mContext).y * 3 / 5;
        leftMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        leftMenuList.setItemsCanFocus(true);
        leftMenuList.setOnItemClickListener(this);
        leftMenuList.setAdapter(leftMenuAdapter);

        rightMenuAdapter = new FiltrateMenuAdapter(mContext,
                R.layout.filtrate_menu_list_item2___cm);
        rightMenuList.getLayoutParams().height = CommonUtil.getDeviceSize(mContext).y * 3 / 5;
        rightMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        rightMenuList.setItemsCanFocus(true);
        rightMenuList.setOnItemClickListener(this);
        rightMenuList.setAdapter(rightMenuAdapter);
    }


    public void refreshArea(List<ChildrenArea> childrenAreas, long parentCid, long childCid) {
        if (childrenAreas != null) {
            areaLabels.clear();
            areaLabels.addAll(childrenAreas);
            for (ChildrenArea parent : areaLabels) {
                ChildrenArea allArea = new ChildrenArea();
                allArea.setName("全部" + parent.getName());
                allArea.setCid(parent.getCid());
                List<ChildrenArea> childrenAreaList = parent.getChildrenAreas();
                if (childrenAreaList == null) {
                    childrenAreaList = new ArrayList<>();
                }
                childrenAreaList.add(0, allArea);
                parent.setChildrenAreas(childrenAreaList);
                if (parent.getCid() == parentCid) {
                    parentLabel = parent;
                    if (childCid != 0) {
                        for (ChildrenArea child : childrenAreaList) {
                            if (child.getCid() == childCid) {
                                childLabel = child;
                            }
                        }
                    } else {
                        childLabel = allArea;
                    }
                }
            }
        }
    }

    public ChildrenArea getParentLabel() {
        return parentLabel;
    }

    public ChildrenArea getChildLabel() {
        return childLabel;
    }

    public boolean isShow() {
        return isShow;
    }

    public View getRootView() {
        return rootView;
    }

    public void showAreaView() {
        if (CommonUtil.isCollectionEmpty(areaLabels)) {
            return;
        }
        leftMenuList.setVisibility(View.VISIBLE);
        leftMenuAdapter.setItems(areaLabels);
        int position = 0;
        if (parentLabel != null) {
            position = areaLabels.indexOf(parentLabel);
        } else {
            parentLabel = areaLabels.get(0);
        }
        leftMenuList.setItemChecked(position, true);
        if (parentLabel.getChildrenAreas() == null || parentLabel.getChildrenAreas()
                .isEmpty()) {
            childLabel = null;
            rightMenuList.setVisibility(View.GONE);
        } else {
            rightMenuList.setVisibility(View.VISIBLE);
            rightMenuList.setDividerHeight(0);
            rightMenuAdapter.setItems(parentLabel.getChildrenAreas());
            position = 0;
            if (childLabel != null) {
                position = parentLabel.getChildrenAreas()
                        .indexOf(childLabel);
            } else {
                childLabel = parentLabel.getChildrenAreas()
                        .get(0);
            }
            rightMenuList.setItemChecked(position, true);
        }
        showMenuAnimation();
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayoutClicked() {
        hideMenuAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChildrenArea item = (ChildrenArea) parent.getAdapter()
                .getItem(position);
        if (parent == leftMenuList) {
            //选城市
            if (item.getChildrenAreas() == null || item.getChildrenAreas()
                    .isEmpty()) {
                parentLabel = item;
                childLabel = null;
                rightMenuList.setVisibility(View.GONE);
            } else {
                rightMenuList.setVisibility(View.VISIBLE);
                rightMenuAdapter.setItems(item.getChildrenAreas());
                position = -1;
                if (childLabel != null) {
                    position = item.getChildrenAreas()
                            .indexOf(childLabel);
                }
                if (position < 0) {
                    rightMenuList.setItemChecked(rightMenuList.getCheckedItemPosition(), false);
                } else {
                    rightMenuList.setItemChecked(position, true);
                }
                return;
            }
        } else if (parent == rightMenuList) {
            parentLabel = areaLabels.get(leftMenuList.getCheckedItemPosition());
            childLabel = item;
        }

        if (onAreaFilterListener != null) {
            hideMenuAnimation();
            onAreaFilterListener.onFilterRefresh(parentLabel, childLabel);
        }
    }

    public interface OnAreaFilterListener {
        void onFilterRefresh(ChildrenArea parent, ChildrenArea child);
    }

    private void showMenuAnimation() {
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
                isShow = false;
                menuBgLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuInfoLayout.startAnimation(animation);
    }

}
