package me.suncloud.marrymemo.widget.merchant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.AreaLabel;
import com.hunliji.hljcommonlibrary.models.DeskLabel;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.MerchantProperty;
import com.hunliji.hljcommonlibrary.models.SortLabel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljtrackerlibrary.TrackerHelper;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.filter.NewFiltrateMenuAdapter;
import me.suncloud.marrymemo.adpter.filter.NewLabelFilterAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.view.HotelChannelActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayout2;
import me.suncloud.marrymemo.widget.CheckableRelativeLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by mo_yu on 2016/11/29.筛选
 */

public class FindMerchantMenuFilterView extends FrameLayout implements AdapterView
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
    private ArrayList<AreaLabel> areaItems;
    private ArrayList<SortLabel> sortItems;
    private ArrayList<DeskLabel> desks;
    private ArrayList<SortLabel> hotelSortItems;

    private NewFiltrateMenuAdapter rightMenuAdapter;
    private NewFiltrateMenuAdapter leftMenuAdapter;
    private NewLabelFilterAdapter filterAdapter;
    private NewLabelFilterAdapter hotelChannelFilterAdapter;

    private MerchantProperty childProperty;
    private AreaLabel areaItem;
    private SortLabel sortItem;
    private int startPrice;//最低价
    private int endPrice;//最高价
    private MerchantProperty currentProperty;//酒店二级类型
    private DeskLabel currentDesk;//酒店桌数
    private SortLabel hotelSortItem;//酒店排序
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

    public FindMerchantMenuFilterView(Context context) {
        this(context, null);
    }

    public FindMerchantMenuFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FindMerchantMenuFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        dividerHeight = Math.max(1, CommonUtil.dp2px(context, 1) / 2);
        leftMenuAdapter = new NewFiltrateMenuAdapter(context, NewFiltrateMenuAdapter.LEFT);
        rightMenuAdapter = new NewFiltrateMenuAdapter(context, NewFiltrateMenuAdapter.RIGHT);
    }

    @OnTouch(R.id.menu_bg_layout)
    boolean onMenuBgLayout() {
        hideKeyboard();
        return hideMenu(0);
    }

    @OnTouch(R.id.hotel_filtrate_layout)
    boolean onHotelFiltrateLayout() {
        hideKeyboard();
        return true;
    }

    @OnTouch(R.id.touch_view)
    boolean onTouchView() {
        if (menuInfoLayout.getVisibility() == View.VISIBLE) {
            scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(true);
        } else {
            scrollableLayout.requestScrollableLayoutDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    private void initView() {
        int width = Math.round((CommonUtil.getDeviceSize(context).x) / 4);
        cbProperty.getLayoutParams().width = width;
        cbArea.getLayoutParams().width = width;
        cbSort.getLayoutParams().width = width;
        cbFiltrate.getLayoutParams().width = width;
        cbHotelFiltrate.getLayoutParams().width = width;

        leftMenuList.setItemsCanFocus(true);
        leftMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        leftMenuList.setOnItemClickListener(this);
        leftMenuList.setAdapter(leftMenuAdapter);

        rightMenuList.setItemsCanFocus(true);
        rightMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        rightMenuList.setOnItemClickListener(this);
        rightMenuList.setAdapter(rightMenuAdapter);

        Point point = CommonUtil.getDeviceSize(context);
        int filterWidth = Math.round(point.x / 3 - CommonUtil.dp2px(context, 16));
        filterAdapter = new NewLabelFilterAdapter(desks, context, filterWidth);
        gridView.setAdapter(filterAdapter);

        hotelChannelFilterAdapter = new NewLabelFilterAdapter(hotelChildProperties,
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
     */
    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public boolean hideCityMenu() {
        return hideMenu(R.id.cb_area);
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
                List<MerchantProperty> properties = property.getChildren();
                if (CommonUtil.isCollectionEmpty(properties)) {
                    //无二级菜单直接选中
                    this.propertyId = property.getId();
                    childProperty = null;
                    rightMenuList.setVisibility(View.GONE);
                } else {
                    //有二级菜单，初始化二级菜单
                    rightMenuList.setVisibility(View.VISIBLE);
                    rightMenuList.setDividerHeight(0);
                    setRightHeight(properties.size(), true);
                    rightMenuAdapter.setData(properties);
                    position = -1;
                    if (childProperty != null) {
                        position = properties.indexOf(childProperty);
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
                propertyId = properties.get(leftMenuList.getCheckedItemPosition())
                        .getId();
                childProperty = (MerchantProperty) label;
            }
        } else if (label instanceof AreaLabel) {
            //地区
            areaItem = (AreaLabel) label;
        } else if (label instanceof SortLabel) {
            //排序
            setSortItem((SortLabel) label);
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
                    if (propertyId != Constants.Property.HOTEL) {
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
                    if (propertyId != Constants.Property.HOTEL) {
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
                    LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams)
                            leftMenuList.getLayoutParams();
                    leftParams.height = Math.min(CommonUtil.dp2px(context, properties.size() * 45),
                            CommonUtil.getDeviceSize(context).y * 3 / 5);
                    leftMenuList.setLayoutParams(leftParams);
                    leftMenuAdapter.setData(properties);
                    int position = 0;
                    MerchantProperty property = null;
                    for (MerchantProperty merchantProperty : properties) {
                        if (propertyId == merchantProperty.getId()) {
                            property = merchantProperty;
                            break;
                        }
                    }
                    if (property != null) {
                        position = properties.indexOf(property);
                    } else {
                        property = properties.get(0);
                    }
                    leftMenuList.setItemChecked(position, true);
                    //二级菜单
                    List<MerchantProperty> properties = property.getChildren();
                    if (CommonUtil.isCollectionEmpty(properties)) {
                        childProperty = null;
                        rightMenuList.setVisibility(View.GONE);
                    } else {
                        rightMenuList.setVisibility(View.VISIBLE);
                        rightMenuList.setDividerHeight(0);
                        setRightHeight(properties.size(), true);
                        rightMenuAdapter.setData(properties);
                        position = 0;
                        if (childProperty != null) {
                            for (MerchantProperty merchantProperty : properties) {
                                if (merchantProperty.getId() == childProperty.getId()) {
                                    position = properties.indexOf(merchantProperty);
                                    break;
                                }
                            }
                        } else {
                            childProperty = properties.get(0);
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
                        if (onRefreshCallback != null) {
                            onRefreshCallback.onAllCityClick();
                        }
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
                    setRightHeight(areaItems.size(), false);
                    rightMenuAdapter.setData(areaItems);
                    int position = 0;
                    if (areaItem != null) {
                        for (AreaLabel areaLabel : areaItems) {
                            if (areaLabel.getId() == areaItem.getId()) {
                                position = areaItems.indexOf(areaLabel);
                                break;
                            }
                        }
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
                    setRightHeight(getSortItems().size(), false);
                    rightMenuAdapter.setData(getSortItems());
                    int position = 0;
                    SortLabel sortLabel = getSortItem();
                    List<SortLabel> sortLabels = getSortItems();
                    if (sortLabel != null) {
                        for (SortLabel label : sortLabels) {
                            if (label.getValue()
                                    .equals(sortLabel.getValue())) {
                                position = sortLabels.indexOf(label);
                                break;
                            }
                        }
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

    private void setRightHeight(int size, boolean noDivider) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rightMenuList
                .getLayoutParams();
        params.height = Math.min(CommonUtil.dp2px(context,
                size * 44) + (noDivider ? 0 : (size - 1) * dividerHeight),
                CommonUtil.getDeviceSize(context).y * 3 / 5);
        rightMenuList.setLayoutParams(params);
    }

    /**
     * 排序列表，分一般商家和酒店商家
     *
     * @return 排序列表
     */
    public ArrayList<SortLabel> getSortItems() {
        if (propertyId != Constants.Property.HOTEL) {
            return sortItems;
        } else {
            return hotelSortItems;
        }
    }

    /**
     * 获取当前的筛选条件，分一般商家和酒店商家
     */
    public SortLabel getSortItem() {
        if (propertyId != Constants.Property.HOTEL) {
            return sortItem;
        } else {
            return hotelSortItem;
        }
    }

    /**
     * 设置当前的筛选条件，分一般商家和酒店商家
     */
    public void setSortItem(SortLabel sortItem) {
        if (propertyId != Constants.Property.HOTEL) {
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
     */
    public boolean isFiltrate() {
        if (propertyId != Constants.Property.HOTEL) {
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
        MerchantProperty property = null;
        for (MerchantProperty merchantProperty : properties) {
            if (propertyId == 0) {
                propertyId = merchantProperty.getId();
                property = merchantProperty;
                break;
            }
            if (propertyId == merchantProperty.getId()) {
                property = merchantProperty;
                break;
            }
        }
        if (childProperty != null && childProperty.getId() > 0) {
            tvProperty.setText(childProperty.getName());
        } else {
            if (property != null) {
                tvProperty.setText(property.getName());
            }
        }

        if (mCity == null || mCity.getId() == 0) {
            tvArea.setText(R.string.label_city);
        } else {
            if (areaItem != null) {
                tvArea.setText(areaItem.getName());
            } else {
                tvArea.setText(R.string.all_area);
            }
        }
        if (propertyId == Merchant.PROPERTY_WEEDING_HOTEL) {
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
        if (getSortItem() == null) {
            return null;
        }
        long childPropertyId;
        if (propertyId == Constants.Property.HOTEL) {
            childPropertyId = currentProperty == null ? 0 : currentProperty.getId();
        } else {
            childPropertyId = childProperty == null ? 0 : childProperty.getId();
        }
        if (propertyId == 0 && properties.get(0) != null) {
            propertyId = properties.get(0)
                    .getId();
        }
        StringBuilder url = new StringBuilder(String.format(Constants.MERCHANTS_QUERY,
                propertyId,
                childPropertyId,
                areaItem == null ? 0 : areaItem.getId(),
                getSortItem().getValue()));
        if (propertyId != Constants.Property.HOTEL) {
            url.append("&bond_sign=")
                    .append(isBondFiltrate ? 1 : 0);
            url.append("&is_grade=")
                    .append(isGrade ? 1 : 0);
        } else {
            if (currentDesk != null) {
                if (currentDesk.getDeskStart() > 0) {
                    url.append("&min_table_num=")
                            .append(currentDesk.getDeskStart());
                }
                if (currentDesk.getDeskEnd() > 0) {
                    url.append("&max_table_num=")
                            .append(currentDesk.getDeskEnd());
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
    public void initMerchantMenu(JsonObject jsonObject, boolean isCityRefresh) {
        JsonArray array = jsonObject.getAsJsonArray("properties");
        if (array != null && array.size() > 0) {
            //切换城市时，不刷新区域以外的数据
            if (!isCityRefresh) {
                properties.clear();
                for (int i = 0, size = array.size(); i < size; i++) {
                    try {
                        MerchantProperty merchantProperty = GsonUtil.getGsonInstance()
                                .fromJson(array.get(i), MerchantProperty.class);
                        if (merchantProperty.getId() != Constants.Property.HOTEL && !CommonUtil
                                .isCollectionEmpty(
                                merchantProperty.getChildren())) {
                            MerchantProperty allProperty = new MerchantProperty();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (onPropertyMenuCallback != null) {
                    onPropertyMenuCallback.onPropertyMenu(properties);
                }
                array = jsonObject.getAsJsonArray("sort");
                if (array != null && array.size() > 0) {
                    sortItems.clear();
                    for (int i = 0, size = array.size(); i < size; i++) {
                        try {
                            SortLabel sortLabel = GsonUtil.getGsonInstance()
                                    .fromJson(array.get(i), SortLabel.class);
                            sortItems.add(sortLabel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!sortItems.isEmpty()) {
                    if (sortItem == null) {
                        sortItem = sortItems.get(0);
                    }
                } else if (sortItem == null) {
                    sortItem = new SortLabel();
                    sortItem.setValue("default");
                }
            }
            array = jsonObject.getAsJsonArray("area");
            areaItems.clear();
            AreaLabel allArea = new AreaLabel();
            allArea.setName(context.getResources()
                    .getString(R.string.all_area));
            areaItems.add(allArea);
            if (array != null && array.size() > 0) {
                for (int i = 0, size = array.size(); i < size; i++) {
                    try {
                        AreaLabel area = GsonUtil.getGsonInstance()
                                .fromJson(array.get(i), AreaLabel.class);
                        areaItems.add(area);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 初始化酒店相关数据
     */
    public void initHotelMenu(JsonObject hotelFilterJson) {
        JsonArray array = hotelFilterJson.getAsJsonArray("sort");
        if (array != null && array.size() > 0) {
            hotelSortItems.clear();
            for (int i = 0, size = array.size(); i < size; i++) {
                try {
                    SortLabel sortLabel = GsonUtil.getGsonInstance()
                            .fromJson(array.get(i), SortLabel.class);
                    hotelSortItems.add(sortLabel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        array = hotelFilterJson.getAsJsonArray("table");
        if (array != null && array.size() > 0) {
            desks.clear();
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject deskObject = array.get(i)
                        .getAsJsonObject();
                String name;
                int deskStart = 0;
                int deskEnd = 0;
                JsonElement deskStartJe = deskObject.get("min");
                if (deskStartJe != null) {
                    deskStart = deskStartJe.getAsInt();
                }
                JsonElement deskEndJe = deskObject.get("max");
                if (deskEndJe != null) {
                    deskEnd = deskEndJe.getAsInt();
                }
                if (deskStart > 0) {
                    if (deskEnd > 0) {
                        name = context.getResources()
                                .getString(R.string.label_desk_name, deskStart, deskEnd);
                    } else {
                        name = context.getResources()
                                .getString(R.string.label_desk_name3, deskStart);
                    }
                } else {
                    name = context.getResources()
                            .getString(R.string.label_desk_name2, deskEnd);
                }
                DeskLabel deskLabel = new DeskLabel();
                deskLabel.setName(name);
                deskLabel.setDeskStart(deskStart);
                deskLabel.setDeskEnd(deskEnd);
                deskLabel.setId(i);
                desks.add(deskLabel);
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
            hotelSortItem = new SortLabel();
            hotelSortItem.setValue("default");
        }
    }

    @OnClick(R.id.btn_filter_done)
    public void onFilterDone() {
        currentDesk = (DeskLabel) filterAdapter.getCheckPosition();
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
                    currentDesk == null ? "" : currentDesk.getDeskStart() + "-" + currentDesk
                            .getDeskEnd());
            TrackerHelper.hotelFilter(getContext(), filterObject.toString());
        }
    }

    /**
     * 筛选视图显示
     */
    public void setTabMenuView(int visibility) {
        tabMenuLayout.setVisibility(visibility);
    }

    public void setProperty(MerchantProperty merchantProperty) {
        this.propertyId = merchantProperty.getId();
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

        void onAllCityClick();
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
            InputMethodManager imm = ((InputMethodManager) activity.getSystemService(
                    INPUT_METHOD_SERVICE));
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
