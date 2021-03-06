package me.suncloud.marrymemo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.FiltrateMenuAdapter;
import me.suncloud.marrymemo.adpter.LabelFilterAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Desk;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MenuItem;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.HotelChannelActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by mo_yu on 2016/11/29.筛选
 */

public class MerchantMenuFilterView extends FrameLayout implements AdapterView
        .OnItemClickListener, CheckableLinearLayout2.OnCheckedChangeListener,
        CheckableRelativeLayout.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.cb_property)
    CheckableLinearLayout2 cbProperty;
    @BindView(R.id.tv_area)
    TextView tvArea;
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
    @BindView(R.id.hotel_grid_view)
    GridView hotelGridView;
    @BindView(R.id.label_price)
    TextView labelPrice;
    @BindView(R.id.et_min_price)
    EditText etMinPrice;
    @BindView(R.id.et_max_price)
    EditText etMaxPrice;
    @BindView(R.id.grid)
    GridView gridView;
    @BindView(R.id.btn_filter_done)
    Button btnFilterDone;
    @BindView(R.id.hotel_filtrate_layout)
    LinearLayout hotelFiltrateLayout;
    @BindView(R.id.cb_bond)
    CheckableRelativeLayout cbBond;
    @BindView(R.id.server_filtrate_layout)
    LinearLayout serverFiltrateLayout;
    @BindView(R.id.filtrate_menu_layout)
    ScrollView filtrateMenuLayout;
    @BindView(R.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R.id.touch_view)
    View touchView;
    @BindView(R.id.menu_bg_layout)
    FrameLayout menuBgLayout;
    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.cb_level)
    CheckableRelativeLayout cbLevel;
    @BindView(R.id.iv_bond_cancel)
    RoundedImageView ivBondCancel;
    @BindView(R.id.iv_level_cancel)
    RoundedImageView ivLevelCancel;

    private ArrayList<MerchantProperty> properties;
    private ArrayList<MerchantProperty> hotelChildProperties;
    private ArrayList<MenuItem> areaItems;
    private ArrayList<MenuItem> sortItems;
    private ArrayList<Desk> desks;
    private ArrayList<MenuItem> hotelSortItems;

    private FiltrateMenuAdapter rightMenuAdapter;
    private FiltrateMenuAdapter leftMenuAdapter;
    private LabelFilterAdapter filterAdapter;
    private LabelFilterAdapter hotelChannelFilterAdapter;

    //当前菜单选中项
    private MerchantProperty property;
    private MerchantProperty childProperty;
    private MenuItem areaItem;
    private MenuItem sortItem;
    private int startPrice;//最低价
    private int endPrice;//最高价
    private MerchantProperty currentProperty;//酒店二级类型
    private Desk currentDesk;//酒店桌数
    private MenuItem hotelSortItem;//酒店排序
    private boolean isBondFiltrate;//是否保证金
    private boolean isGrade;//是否高等级商家
    private City mCity;

    private int dividerHeight;
    private Context context;

    //需要设置的参数
    private long propertyId;
    private ScrollableLayout scrollableLayout;
    private onRefreshCallback onRefreshCallback;
    private onPropertyMenuCallback onPropertyMenuCallback;

    public MerchantMenuFilterView(Context context) {
        this(context, null);
    }

    public MerchantMenuFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MerchantMenuFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.menu_filter_common_layout, this);
        ButterKnife.bind(this, view);
        this.context = context;
        initValue();
        initView();
    }

    private void initValue() {
        properties = new ArrayList<>();
        hotelChildProperties = new ArrayList<>();
        areaItems = new ArrayList<>();
        sortItems = new ArrayList<>();
        hotelSortItems = new ArrayList<>();
        desks = new ArrayList<>();
        dividerHeight = Math.max(1, Util.dp2px(context, 1) / 2);
        leftMenuAdapter = new FiltrateMenuAdapter(context, R.layout.filtrate_menu_list_item);
        rightMenuAdapter = new FiltrateMenuAdapter(context, R.layout.menu_list_item);
    }

    private void initView() {
        menuBgLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return hideMenu(0);
            }
        });
        hotelFiltrateLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return true;
            }
        });

        touchView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (menuInfoLayout.getVisibility() == View.VISIBLE) {
                    scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(true);
                } else {
                    scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(false);
                }
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

        Point point = JSONUtil.getDeviceSize(context);
        int filterWidth = Math.round(point.x / 3 - Util.dp2px(context, 16));
        filterAdapter = new LabelFilterAdapter(desks, context, filterWidth);
        gridView.setAdapter(filterAdapter);

        hotelChannelFilterAdapter = new LabelFilterAdapter(hotelChildProperties,
                context,
                filterWidth);
        hotelGridView.setAdapter(hotelChannelFilterAdapter);

        cbBond.setOnCheckedChangeListener(this);
        cbLevel.setOnCheckedChangeListener(this);
        cbProperty.setOnCheckedChangeListener(this);
        cbFiltrate.setOnCheckedChangeListener(this);
        cbHotelFiltrate.setOnCheckedChangeListener(this);
        cbArea.setOnCheckedChangeListener(this);
        cbSort.setOnCheckedChangeListener(this);
        cbFiltrate.setVisibility(View.VISIBLE);
    }


    /**
     * 设置悬停控件的滑动视图
     */
    public void setScrollableLayout(ScrollableLayout scrollableLayout) {
        this.scrollableLayout = scrollableLayout;
    }

    /**
     * 设置一级类目id
     *
     * @param propertyId
     */
    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
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
        if (id != R.id.cb_area && cbArea != null && cbArea.isChecked()) {
            b = true;
            cbArea.setChecked(false);
        }
        if (id != R.id.cb_filtrate && cbFiltrate != null && cbFiltrate.isChecked()) {
            b = true;
            cbFiltrate.setChecked(false);
        }
        if (id != R.id.cb_hotel_filtrate && cbHotelFiltrate != null && cbHotelFiltrate.isChecked
                ()) {
            b = true;
            cbHotelFiltrate.setChecked(false);
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
                    this.property = property;
                    childProperty = null;
                    rightMenuList.setVisibility(View.GONE);
                } else {
                    //有二级菜单，初始化二级菜单
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(0);
                    rightMenuAdapter.setData(property.getChildren());
                    position = -1;
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
            } else if (parent == rightMenuList && childProperty != label) {
                //二级菜单
                property = properties.get(leftMenuList.getCheckedItemPosition());
                childProperty = (MerchantProperty) label;
            }
        } else if (label instanceof MenuItem) {
            if (label.getType() == 2) {
                //地区
                areaItem = (MenuItem) label;
            } else if (label.getType() == 3) {
                //排序
                setSortItem((MenuItem) label);
            }
        }
        hideMenu(0);
        onRefresh();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_filtrate:
                if (isChecked) {
                    hideMenu(buttonView.getId());
                    listMenuLayout.setVisibility(View.GONE);
                    filtrateMenuLayout.setVisibility(View.VISIBLE);
                    if (property.getId() != Constants.Property.HOTEL) {
                        //通用商家筛选菜单
                        serverFiltrateLayout.setVisibility(View.VISIBLE);
                        hotelFiltrateLayout.setVisibility(View.GONE);
                    } else {
                        //酒店商家筛选菜单
                        serverFiltrateLayout.setVisibility(View.GONE);
                        hotelFiltrateLayout.setVisibility(View.VISIBLE);
                        refreshHotelFilterMenu();
                    }
                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                    hideKeyboard();
                }
                break;
            case R.id.cb_hotel_filtrate:
                if (isChecked) {
                    hideMenu(buttonView.getId());
                    listMenuLayout.setVisibility(View.GONE);
                    filtrateMenuLayout.setVisibility(View.VISIBLE);
                    if (property.getId() != Constants.Property.HOTEL) {
                        //通用商家筛选菜单
                        serverFiltrateLayout.setVisibility(View.VISIBLE);
                        hotelFiltrateLayout.setVisibility(View.GONE);
                    } else {
                        //酒店商家筛选菜单
                        serverFiltrateLayout.setVisibility(View.GONE);
                        hotelFiltrateLayout.setVisibility(View.VISIBLE);
                        refreshHotelFilterMenu();
                    }
                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                    hideKeyboard();
                }
                break;
        }
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
                    filtrateMenuLayout.setVisibility(View.GONE);
                    //一级菜单
                    leftMenuList.setVisibility(View.VISIBLE);
                    leftMenuAdapter.setData(properties);
                    int position = 0;
                    if (property != null) {
                        position = properties.indexOf(property);
                    } else {
                        property = properties.get(0);
                    }
                    leftMenuList.setItemChecked(position, true);
                    //二级菜单
                    if (property.getChildren() == null || property.getChildren()
                            .isEmpty()) {
                        childProperty = null;
                        rightMenuList.setVisibility(View.GONE);
                    } else {
                        rightMenuList.setVisibility(View.VISIBLE);
                        rightMenuList.setDividerHeight(0);
                        rightMenuAdapter.setData(property.getChildren());
                        position = 0;
                        if (childProperty != null) {
                            position = property.getChildren()
                                    .indexOf(childProperty);
                        } else {
                            childProperty = property.getChildren()
                                    .get(0);
                        }
                        rightMenuList.setItemChecked(position, true);
                    }

                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                }
                break;
            case R.id.cb_area:
                if (mCity != null && mCity.getId() == 0) {
                    if (checked) {
                        DialogUtil.createSingleButtonDialog(context, "您还没有选择城市，请先返回首页选择具体城市", "我知道了",
                                null)
                                .show();
                        cbArea.setChecked(false);
                    }
                    return;
                }
                if (areaItems.isEmpty()) {
                    return;
                }
                if (checked) {
                    //显示区地址菜单 单级
                    hideMenu(view.getId());
                    listMenuLayout.setVisibility(View.VISIBLE);
                    filtrateMenuLayout.setVisibility(View.GONE);
                    leftMenuList.setVisibility(View.GONE);
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(dividerHeight);
                    rightMenuAdapter.setData(areaItems);
                    int position = 0;
                    if (areaItem != null) {
                        position = areaItems.indexOf(areaItem);
                    } else {
                        areaItem = areaItems.get(0);
                    }
                    rightMenuList.setItemChecked(position, true);

                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                }
                break;
            case R.id.cb_sort:
                if (getSortItems().isEmpty()) {
                    return;
                }
                if (checked) {
                    //显示排序菜单 单级
                    hideMenu(view.getId());
                    listMenuLayout.setVisibility(View.VISIBLE);
                    filtrateMenuLayout.setVisibility(View.GONE);
                    leftMenuList.setVisibility(View.GONE);
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(dividerHeight);
                    rightMenuAdapter.setData(getSortItems());
                    int position = 0;
                    if (getSortItem() != null) {
                        position = getSortItems().indexOf(getSortItem());
                    } else {
                        setSortItem(getSortItems().get(0));
                    }
                    rightMenuList.setItemChecked(position, true);

                    scrollableLayout.scrollToBottom();
                    AnimUtil.showMenuAnimation(menuBgLayout, menuInfoLayout);
                } else {
                    AnimUtil.hideMenuAnimation(menuBgLayout, menuInfoLayout);
                }
                break;
            case R.id.cb_bond:
                isBondFiltrate = checked;
                ivBondCancel.setVisibility(checked ? View.VISIBLE : View.GONE);
                break;
            case R.id.cb_level:
                isGrade = checked;
                ivLevelCancel.setVisibility(checked ? View.VISIBLE : View.GONE);
                break;
        }
    }

    /**
     * 排序列表，分一般商家和酒店商家
     *
     * @return 排序列表
     */
    public ArrayList<MenuItem> getSortItems() {
        if (property.getId() != Constants.Property.HOTEL) {
            return sortItems;
        } else {
            return hotelSortItems;
        }
    }

    /**
     * 获取当前的筛选条件，分一般商家和酒店商家
     *
     * @return
     */
    public MenuItem getSortItem() {
        if (property.getId() != Constants.Property.HOTEL) {
            return sortItem;
        } else {
            return hotelSortItem;
        }
    }

    /**
     * 设置当前的筛选条件，分一般商家和酒店商家
     *
     * @param sortItem
     */
    public void setSortItem(MenuItem sortItem) {
        if (property.getId() != Constants.Property.HOTEL) {
            this.sortItem = sortItem;
        } else {
            this.hotelSortItem = sortItem;
        }
    }

    private void refreshHotelFilterMenu() {
        if (currentProperty != null) {
            hotelChannelFilterAdapter.setChecked(currentProperty);
        }
        if (currentDesk != null) {
            filterAdapter.setChecked(currentDesk);
        }
    }


    /**
     * 是否筛选状态
     *
     * @return
     */
    public boolean isFiltrate() {
        if (property == null) {
            return false;
        }
        if (property.getId() != Constants.Property.HOTEL) {
            return isBondFiltrate || isGrade;
        } else {
            return (startPrice < endPrice && startPrice >= 0) || currentDesk != null ||
                    currentProperty != null;
        }
    }

    /**
     * 根据当前选中条件刷新界面
     */
    public void onRefresh() {
        //刷新顶部菜单显示
        if (childProperty != null && childProperty.getId() > 0) {
            tvProperty.setText(childProperty.getName());
        } else {
            tvProperty.setText(property.getName());
        }
        if (areaItem != null) {
            tvArea.setText(areaItem.getName());
        } else {
            tvArea.setText(R.string.all_area);
        }
        if (property != null && property.getId() == Merchant.PROPERTY_WEEDING_HOTEL) {
            cbFiltrate.setText("更多");
        } else {
            cbFiltrate.setText("筛选");
        }
        if (isFiltrate()) {
            cbFiltrate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            cbFiltrate.setTextColor(ContextCompat.getColorStateList(context,
                    R.color.black2_primary));
        }
        if (isFiltrate()) {
            cbHotelFiltrate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            cbHotelFiltrate.setTextColor(ContextCompat.getColorStateList(context,
                    R.color.black2_primary));
        }
        tvSort.setText(getSortItem().getName());
        if (onRefreshCallback != null) {
            onRefreshCallback.onRefresh(propertyId);
        }
    }


    /**
     * 组合请求参数
     */
    public String getUrlQuery() {
        if (property == null || getSortItem() == null) {
            return null;
        }
        long childPropertyId;
        if (property.getId() == Constants.Property.HOTEL) {
            childPropertyId = currentProperty == null ? 0 : currentProperty.getId();
        } else {
            childPropertyId = childProperty == null ? 0 : childProperty.getId();
        }
        StringBuilder url = new StringBuilder(String.format(Constants.MERCHANTS_QUERY,
                property.getId(),
                childPropertyId,
                areaItem == null ? 0 : areaItem.getId(),
                getSortItem().getKeyWord()));
        if (property.getId() != Constants.Property.HOTEL) {
            url.append("&bond_sign=")
                    .append(isBondFiltrate ? 1 : 0);
            url.append("&is_grade=")
                    .append(isGrade ? 1 : 0);
        } else {
            if (currentDesk != null) {
                if (currentDesk.getDesk_start() > 0) {
                    url.append("&min_table_num=")
                            .append(currentDesk.getDesk_start());
                }
                if (currentDesk.getDesk_end() > 0) {
                    url.append("&max_table_num=")
                            .append(currentDesk.getDesk_end());
                }
            }
            if (startPrice > 0) {
                url.append("&min_price=")
                        .append(startPrice);
            }
            if (endPrice > 0) {
                url.append("&max_price=")
                        .append(endPrice);
            }
        }
        return url.toString();
    }

    /**
     * 初始化通用商家
     */
    public void initMerchantMenu(JSONObject jsonObject, boolean isCityRefresh) {
        JSONArray array = jsonObject.optJSONArray("properties");
        if (array != null && array.length() > 0) {
            //切换城市时，不刷新区域以外的数据
            if (!isCityRefresh) {
                properties.clear();
                for (int i = 0, size = array.length(); i < size; i++) {
                    MerchantProperty merchantProperty = new MerchantProperty(array.optJSONObject
                            (i));

                    if (propertyId > 0 && propertyId == merchantProperty.getId()) {
                        property = merchantProperty;
                    }
                    if (merchantProperty.getId() != Constants.Property.HOTEL && merchantProperty
                            .getChildren() != null && !merchantProperty.getChildren()
                            .isEmpty()) {
                        MerchantProperty allProperty = new MerchantProperty(null);
                        allProperty.setName(context.getResources()
                                .getString(R.string.label_all));
                        merchantProperty.getChildren()
                                .add(0, allProperty);
                    }
                    if (merchantProperty.getId() == Constants.Property.HOTEL) {
                        hotelChildProperties.clear();
                        hotelChildProperties.addAll(merchantProperty.getChildren());
                        merchantProperty.setChildren(null);
                    }
                    properties.add(merchantProperty);
                }
                if (onPropertyMenuCallback != null) {
                    onPropertyMenuCallback.onPropertyMenu(properties);
                }
                array = jsonObject.optJSONArray("sort");
                if (array != null && array.length() > 0) {
                    sortItems.clear();
                    for (int i = 0, size = array.length(); i < size; i++) {
                        MenuItem sort = new MenuItem(array.optJSONObject(i));
                        sort.setType(3);
                        sortItems.add(sort);
                    }
                }
                if (!properties.isEmpty()) {
                    if (property == null) {
                        property = properties.get(0);
                    }
                } else if (property == null) {
                    property = new MerchantProperty(null);
                }
                if (!sortItems.isEmpty()) {
                    if (sortItem == null) {
                        sortItem = sortItems.get(0);
                    }
                } else if (sortItem == null) {
                    sortItem = new MenuItem(null);
                    sortItem.setKeyWord("default");
                }
            }
            array = jsonObject.optJSONArray("area");
            areaItems.clear();
            MenuItem allArea = new MenuItem(null);
            allArea.setType(2);
            allArea.setName(context.getResources()
                    .getString(R.string.all_area));
            areaItems.add(allArea);
            if (array != null && array.length() > 0) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    MenuItem area = new MenuItem(array.optJSONObject(i));
                    area.setType(2);
                    areaItems.add(area);
                }
            }
            if (!areaItems.isEmpty()) {
                cbArea.setUncheckable(false);
                areaItem = areaItems.get(0);
            }
        }
    }

    /**
     * 初始化酒店相关数据
     */
    public void initHotelMenu(JSONObject hotelFilterJson) {
        JSONArray array = hotelFilterJson.optJSONArray("sort");
        if (array != null && array.length() > 0) {
            hotelSortItems.clear();
            for (int i = 0, size = array.length(); i < size; i++) {
                MenuItem sort = new MenuItem(array.optJSONObject(i));
                sort.setType(3);
                hotelSortItems.add(sort);
            }
        }
        array = hotelFilterJson.optJSONArray("table");
        if (array != null && array.length() > 0) {
            desks.clear();
            for (int i = 0, size = array.length(); i < size; i++) {
                JSONObject deskObject = array.optJSONObject(i);
                String name;
                int desk_start = deskObject.optInt("min");
                int desk_end = deskObject.optInt("max");
                if (desk_start > 0) {
                    if (desk_end > 0) {
                        name = context.getResources()
                                .getString(R.string.label_desk_name, desk_start, desk_end);
                    } else {
                        name = context.getResources()
                                .getString(R.string.label_desk_name3, desk_start);
                    }
                } else {
                    name = context.getResources()
                            .getString(R.string.label_desk_name2, desk_end);
                }
                desks.add(new Desk(name, desk_start, desk_end, i));
            }
            filterAdapter.clearHashMap();
            filterAdapter.notifyDataSetChanged();
        }

        if (hotelChildProperties != null && hotelChildProperties.size() > 0) {
            hotelChannelFilterAdapter.clearHashMap();
            hotelChannelFilterAdapter.notifyDataSetChanged();
        }

        if (!hotelSortItems.isEmpty()) {
            if (hotelSortItem == null) {
                hotelSortItem = hotelSortItems.get(0);
            }
        } else if (hotelSortItem == null) {
            hotelSortItem = new MenuItem(null);
            hotelSortItem.setKeyWord("default");
        }
    }

    @OnClick(R.id.btn_filter_done)
    public void onFilterDone() {
        currentDesk = (Desk) filterAdapter.getCheckPosition();
        currentProperty = (MerchantProperty) hotelChannelFilterAdapter.getCheckPosition();
        String minPrice = etMinPrice.getText()
                .toString();
        String maxPrice = etMaxPrice.getText()
                .toString();
        if (!TextUtils.isEmpty(minPrice)) {
            startPrice = Integer.valueOf(minPrice);
        } else {
            startPrice = 0;
        }
        if (!TextUtils.isEmpty(maxPrice)) {
            endPrice = Integer.valueOf(maxPrice);
        } else {
            endPrice = Integer.MAX_VALUE;
        }
        //若用户填写的最低价高于最高价，则点击确定按钮后，筛选弹窗收起，并自动将最低价和最高价的数字对换
        int price;
        if (startPrice > endPrice) {
            etMaxPrice.setText(minPrice);
            etMinPrice.setText(maxPrice);
            price = endPrice;
            endPrice = startPrice;
            startPrice = price;
        }
        hideMenu(0);
        onRefresh();
        if (getContext() instanceof HotelChannelActivity) {
            JsonObject filterObject = new JsonObject();
            if (currentProperty != null) {
                filterObject.addProperty("type", currentProperty.getId());
            } else {
                filterObject.addProperty("type", "");
            }
            minPrice = startPrice > 0 ? etMinPrice.getText()
                    .toString() : "";
            maxPrice = endPrice > 0 && endPrice < Integer.MAX_VALUE ? etMaxPrice.getText()
                    .toString() : "";
            if (!TextUtils.isEmpty(minPrice) || !TextUtils.isEmpty(maxPrice)) {
                filterObject.addProperty("price", minPrice + "-" + maxPrice);
            } else {
                filterObject.addProperty("price", "");
            }
            filterObject.addProperty("num",
                    currentDesk == null ? "" : currentDesk.getDesk_start() + "-" + currentDesk
                            .getDesk_end());
            TrackerHelper.hotelFilter(getContext(), filterObject.toString());
        }
    }

    /**
     * 筛选视图显示
     */
    public void setTabMenuView(int visibility) {
        tabMenuLayout.setVisibility(visibility);
    }

    /**
     * 一级分类视图显示
     */
    public void setCbPropertyView(int visibility) {
        cbProperty.setVisibility(visibility);
    }

    /**
     * 筛选视图区分
     */
    public void setCbFiltrateView(boolean isHotel) {
        if (isHotel) {
            cbFiltrate.setVisibility(GONE);
            cbHotelFiltrate.setVisibility(VISIBLE);
        } else {
            cbFiltrate.setVisibility(VISIBLE);
            cbHotelFiltrate.setVisibility(GONE);
        }
    }

    public void setProperty(MerchantProperty merchantProperty) {
        property = merchantProperty;
        childProperty = null;
    }

    public void setCity(City city) {
        this.mCity = city;
    }

    /**
     * 刷新回调
     */
    public void setOnRefreshCallback(onRefreshCallback onRefreshCallback) {
        this.onRefreshCallback = onRefreshCallback;
    }

    @OnClick({R.id.action_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_confirm:
                hideMenu(0);
                onRefresh();
                break;
        }
    }

    public interface onRefreshCallback {
        void onRefresh(long propertyId);
    }

    /**
     * 类目选择点击回调
     */
    public void setOnPropertyMenuCallback(onPropertyMenuCallback onPropertyMenuCallback) {
        this.onPropertyMenuCallback = onPropertyMenuCallback;
    }

    public interface onPropertyMenuCallback {
        void onPropertyMenu(ArrayList<MerchantProperty> properties);
    }

    public void hideKeyboard() {
        Activity activity = (Activity) context;
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                    activity.getCurrentFocus()
                            .getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
