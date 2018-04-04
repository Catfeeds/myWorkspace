package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FiltrateMenuAdapter;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by wangtao on 2016/12/10.
 */

public class PropertySortMenuFilterView extends FrameLayout implements AdapterView
        .OnItemClickListener, CheckableLinearLayout2.OnCheckedChangeListener {


    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.cb_property)
    CheckableLinearLayout2 cbProperty;
    @BindView(R.id.cb_area)
    CheckableRelativeLayout cbArea;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.cb_sort)
    CheckableLinearLayout2 cbSort;
    @BindView(R.id.cb_filtrate)
    CheckBox cbFiltrate;
    @BindView(R.id.cb_hotel_filtrate)
    CheckBox cbHotelFiltrate;
    @BindView(R.id.tab_menu_layout)
    LinearLayout tabMenuLayout;
    @BindView(R.id.left_menu_list)
    ListView leftMenuList;
    @BindView(R.id.right_menu_list)
    ListView rightMenuList;
    @BindView(R.id.list_menu_layout)
    LinearLayout listMenuLayout;
    @BindView(R.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R.id.touch_view)
    View touchView;
    @BindView(R.id.menu_bg_layout)
    FrameLayout menuBgLayout;


    private List<MerchantProperty> properties;
    private List<MenuItem> sortItems;

    private FiltrateMenuAdapter rightMenuAdapter;
    private FiltrateMenuAdapter leftMenuAdapter;

    //当前菜单选中项
    private Map<String, Label> filterMap = new HashMap<>();

    private int dividerHeight;

    //需要设置的参数
    private long propertyId;
    private ScrollableLayout scrollableLayout;

    private FilterCallback filterCallback;

    public static class MenuType {
        public static final String PROPERTY = "property";
        public static final String CHILD_PROPERTY = "childProperty";
        public static final String SORT = "sort";
    }

    public PropertySortMenuFilterView(Context context) {
        this(context, null);
    }

    public PropertySortMenuFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PropertySortMenuFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        View view = inflate(context, R.layout.menu_filter_common_layout, this);
        ButterKnife.bind(this, view);
        initValue();
        initView();
    }

    private void initValue() {
        filterMap = new HashMap<>();
        properties = new ArrayList<>();
        sortItems = new ArrayList<>();
        dividerHeight = Math.max(1, Util.dp2px(getContext(), 1) / 2);
        leftMenuAdapter = new FiltrateMenuAdapter(getContext(), R.layout.filtrate_menu_list_item);
        rightMenuAdapter = new FiltrateMenuAdapter(getContext(), R.layout.menu_list_item);
    }

    private void initView() {
        menuBgLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return hideMenu(0);
            }
        });

        touchView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent
                        (menuInfoLayout.getVisibility() == View.VISIBLE);
                return false;
            }
        });
        leftMenuList.setItemsCanFocus(true);
        leftMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        leftMenuList.setOnItemClickListener(this);
        leftMenuList.setAdapter(leftMenuAdapter);

        rightMenuList.setItemsCanFocus(true);
        rightMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        rightMenuList.setOnItemClickListener(this);
        rightMenuList.setAdapter(rightMenuAdapter);

        cbArea.setVisibility(GONE);
        cbFiltrate.setVisibility(GONE);
        cbHotelFiltrate.setVisibility(GONE);
        cbProperty.setOnCheckedChangeListener(this);
        cbSort.setOnCheckedChangeListener(this);
    }

    public void setProperties(List<MerchantProperty> properties) {
        tabMenuLayout.setVisibility(VISIBLE);
        this.properties = properties;
        if (getProperty() == null) {
            if (properties.isEmpty()) {
                setProperty(new MerchantProperty(null));
            } else {
                setProperty(properties.get(0));
            }
        }
    }

    public void setSortItems(List<MenuItem> sortItems) {
        tabMenuLayout.setVisibility(VISIBLE);
        for(MenuItem sort:sortItems){
            sort.setType(3);
        }
        this.sortItems = sortItems;
        if (getSortItem() == null) {
            if (sortItems.isEmpty()) {
                MenuItem sortItem = new MenuItem(null);
                sortItem.setKeyWord("default");
                setSortItem(sortItem);
            } else {
                setSortItem(sortItems.get(0));
            }
        }
    }

    /**
     * 切换服务分类
     *
     * @param propertyId 一级分类id
     */
    public void setProperty(long propertyId) {
        if (properties.isEmpty()) {
            this.propertyId = propertyId;
            return;
        }
        for (MerchantProperty property : properties) {
            if (property.getId() == propertyId) {
                setProperty(property);
                setChildProperty(null);
                onRefresh();
                break;
            }
        }
    }

    private void setProperty(MerchantProperty property) {
        filterMap.put(MenuType.PROPERTY, property);
    }

    public MerchantProperty getProperty() {
        return (MerchantProperty) filterMap.get(MenuType.PROPERTY);
    }

    public void setChildProperty(MerchantProperty childProperty) {
        filterMap.put(MenuType.CHILD_PROPERTY, childProperty);
    }

    public MerchantProperty getChildProperty() {
        return (MerchantProperty) filterMap.get(MenuType.CHILD_PROPERTY);
    }

    public MenuItem getSortItem() {
        return (MenuItem) filterMap.get(MenuType.SORT);
    }

    public void setSortItem(MenuItem sortItem) {
        filterMap.put(MenuType.SORT, sortItem);
    }

    /**
     * 设置悬停控件的滑动视图
     */
    public void setScrollableLayout(ScrollableLayout scrollableLayout) {
        this.scrollableLayout = scrollableLayout;
    }

    /**
     * 商家筛选单选控制
     *
     * @param id 当前选中的id；关闭id不相同选项
     * @return boolean false 未关闭菜单
     */
    public boolean hideMenu(int id) {
        boolean b = false;
        if (id != R.id.cb_property && cbProperty != null && cbProperty.isChecked()) {
            b = true;
            cbProperty.setChecked(false);
        }
        if (id != R.id.cb_sort && cbSort != null && cbSort.isChecked()) {
            b = true;
            cbSort.setChecked(false);
        }
        return b;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Label label = (Label) parent.getAdapter()
                .getItem(position);
        if (label == null) {
            return;
        }
        //菜单列表项选中
        if (label instanceof MerchantProperty) {
            //商家分类
            if (parent == leftMenuList) {
                //一级菜单
                MerchantProperty property = (MerchantProperty) label;
                if (property.getChildren() == null || property.getChildren()
                        .isEmpty()) {
                    //无二级菜单直接选中
                    setProperty(property);
                    setChildProperty(null);
                    rightMenuList.setVisibility(View.GONE);
                } else {
                    //有二级菜单，初始化二级菜单
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(0);
                    rightMenuAdapter.setData(property.getChildren());
                    position = -1;
                    MerchantProperty childProperty = getChildProperty();
                    if (childProperty != null) {
                        position = property.getChildren()
                                .indexOf(childProperty);
                    }
                    if (position < 0) {
                        rightMenuList.setItemChecked(rightMenuList.getCheckedItemPosition(), false);
                    } else {
                        rightMenuList.setItemChecked(position, true);
                    }
                    return;
                }
            } else if (parent == rightMenuList && getChildProperty() != label) {
                //二级菜单
                setProperty(properties.get(leftMenuList.getCheckedItemPosition()));
                setChildProperty((MerchantProperty) label);
            }
        } else if (label instanceof MenuItem) {
            //排序
            setSortItem((MenuItem) label);
        }
        hideMenu(0);
        onRefresh();
    }

    @Override
    public void onCheckedChange(View view, boolean checked) {
        switch (view.getId()) {
            case R.id.cb_property:
                if (properties.isEmpty()) {
                    return;
                }
                if (checked) {
                    //显示商家分类菜单 两级
                    hideMenu(view.getId());
                    listMenuLayout.setVisibility(View.VISIBLE);
                    //一级菜单
                    leftMenuList.setVisibility(View.VISIBLE);
                    leftMenuAdapter.setData(properties);
                    int position = 0;
                    MerchantProperty property = getProperty();
                    if (property != null) {
                        position = properties.indexOf(property);
                    } else {
                        property = properties.get(0);
                    }
                    leftMenuList.setItemChecked(position, true);
                    //二级菜单
                    if (property.getChildren() == null || property.getChildren()
                            .isEmpty()) {
                        setChildProperty(null);
                        rightMenuList.setVisibility(View.GONE);
                    } else {
                        rightMenuList.setVisibility(View.VISIBLE);
                        rightMenuList.setDividerHeight(0);
                        rightMenuAdapter.setData(property.getChildren());
                        position = 0;
                        MerchantProperty childProperty = getChildProperty();
                        if (childProperty != null) {
                            position = property.getChildren()
                                    .indexOf(childProperty);
                        } else {
                            setChildProperty(property.getChildren()
                                    .get(0));
                        }
                        rightMenuList.setItemChecked(position, true);
                    }

                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                }
                break;
            case R.id.cb_sort:
                if (sortItems.isEmpty()) {
                    return;
                }
                if (checked) {
                    //显示排序菜单 单级
                    hideMenu(view.getId());
                    listMenuLayout.setVisibility(View.VISIBLE);
                    leftMenuList.setVisibility(View.GONE);
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(dividerHeight);
                    rightMenuAdapter.setData(sortItems);
                    int position = 0;
                    MenuItem sortItem = getSortItem();
                    if (sortItem != null) {
                        position = sortItems.indexOf(sortItem);
                    } else {
                        setSortItem(sortItems.get(0));
                    }
                    rightMenuList.setItemChecked(position, true);

                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                }
                break;
        }
    }

    /**
     * 根据当前选中条件刷新界面
     */
    public void onRefresh() {
        //刷新顶部菜单显示
        MerchantProperty childProperty = getChildProperty();
        MerchantProperty property = getProperty();
        MenuItem sortItem = getSortItem();
        if (childProperty != null && childProperty.getId() > 0) {
            tvProperty.setText(childProperty.getName());
        } else {
            tvProperty.setText(property.getName());
        }
        tvSort.setText(sortItem.getName());
        if (filterCallback != null) {
            filterCallback.onFilterChange(filterMap);
        }
    }

    public void setFilterCallback(FilterCallback filterCallback) {
        this.filterCallback = filterCallback;
    }

    public interface FilterCallback {
        void onFilterChange(Map<String, Label> filterMap);
    }

}
