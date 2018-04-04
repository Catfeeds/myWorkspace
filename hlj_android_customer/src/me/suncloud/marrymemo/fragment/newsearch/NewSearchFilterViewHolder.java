package me.suncloud.marrymemo.fragment.newsearch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.search.MerchantFilter;
import com.hunliji.hljcommonlibrary.models.search.MerchantFilterHotelTableLabel;
import com.hunliji.hljcommonlibrary.models.search.SearchFilter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.LabelFilterAdapter;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.MerchantProperty;
import me.suncloud.marrymemo.util.MerchantFiltersUtil;
import me.suncloud.marrymemo.util.Properties3Util;

/**
 * Created by luohanlin on 2017/7/19.
 * 新搜索筛选
 */

public class NewSearchFilterViewHolder implements CheckableLinearGroup.OnCheckedChangeListener {

    @BindView(R.id.cb_default)
    CheckableLinearButton cbDefault;
    @BindView(R.id.cb_newest)
    CheckableLinearButton cbNewest;
    @BindView(R.id.tv_price_popular)
    TextView tvPricePopular;
    @BindView(R.id.img_price_sort)
    ImageView imgPriceSort;
    @BindView(R.id.cb_price_popular)
    CheckableLinearButton cbPricePopular;
    @BindView(R.id.cb_sold_count)
    CheckableLinearButton cbSoldCount;
    @BindView(R.id.filtrate_layout)
    LinearLayout filtrateLayout;
    @BindView(R.id.cg_sort)
    CheckableLinearGroup cgSort;
    @BindView(R.id.sort_layout)
    LinearLayout sortLayout;
    @BindView(R.id.iv_filter)
    ImageView ivFilter;

    private Context mContext;
    private View rootView;
    private NewSearchApi.SearchType searchType;
    private OnSearchFilterListener onSearchListener;
    private String sort;
    private SearchFilter searchFilter;
    private Dialog filterDlg;
    private FilterDlgViewHolder dlgViewHolder;
    private InputMethodManager imm;
    private MerchantFilter merchantFilter;
    private Label filterProperty;
    private MerchantFilterHotelTableLabel filterTableNum;
    private double filterPriceMin;
    private double filterPriceMax;
    private ArrayList<MerchantProperty> productCategories;
    private MerchantProperty filterProductProperty;
    private Label filterDiscountLabel;

    public static NewSearchFilterViewHolder newInstance(
            Context context, NewSearchApi.SearchType subType, OnSearchFilterListener listener) {
        View view = View.inflate(context, R.layout.search_work_case_filter_layout, null);
        NewSearchFilterViewHolder holder = new NewSearchFilterViewHolder(context,
                view,
                subType,
                listener);
        holder.init();

        return holder;
    }

    public View getRootView() {
        return rootView;
    }

    public String getSort() {
        return sort;
    }

    public SearchFilter getSearchFilter() {
        return searchFilter;
    }


    public SearchFilter resetSearchFilter() {
        filterProperty = null;
        filterPriceMin = filterPriceMax = 0;
        filterTableNum = null;
        filterProductProperty = null;
        filterDiscountLabel = null;

        setSearchFilter();
        return searchFilter;
    }

    private NewSearchFilterViewHolder(
            Context context,
            View view,
            NewSearchApi.SearchType searchType,
            OnSearchFilterListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.searchType = searchType;
        this.onSearchListener = listener;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        ButterKnife.bind(this, view);
    }

    private void init() {
        loadFilterData();
        sort = NewSearchApi.SORT_DEFAULT;
        searchFilter = new SearchFilter();
        cbDefault.setChecked(true);
        cgSort.setOnCheckedChangeListener(this);
        if (isPrice()) {
            // 套餐
            tvPricePopular.setText("价格");
            imgPriceSort.setVisibility(View.VISIBLE);
            setPriceSortImg();
            if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT || searchType ==
                    NewSearchApi.SearchType.SEARCH_TYPE_CAR) {
                // 婚品、婚车不显示最新筛选
                cbSoldCount.setVisibility(View.VISIBLE);
                cbNewest.setVisibility(View.GONE);
            }
        } else {
            if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_CASE || searchType ==
                    NewSearchApi.SearchType.SEARCH_TYPE_MERCHANT) {
                // 案例 商家
                tvPricePopular.setText("人气");
                imgPriceSort.setVisibility(View.GONE);
            }
        }
    }

    private void loadFilterData() {
        // 同步婚品分类数据
        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT) {
            new Properties3Util.PropertiesSyncTask(mContext,
                    new Properties3Util.OnFinishedListener() {
                        @Override
                        public void onFinish(ArrayList<MerchantProperty> ps) {
                            productCategories = ps;
                        }
                    }).execute();
        } else {
            MerchantFiltersUtil.SyncMerchantFilterData(mContext,
                    new MerchantFiltersUtil.OnFinishedListener() {
                        @Override
                        public void onFinish(MerchantFilter mf) {
                            merchantFilter = mf;
                        }
                    });
        }
    }

    /**
     * 判断"价格"/"人气"
     *
     * @return boolean
     */
    private boolean isPrice() {
        return searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK || searchType ==
                NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT || searchType == NewSearchApi
                .SearchType.SEARCH_TYPE_HOTEL || searchType == NewSearchApi.SearchType
                .SEARCH_TYPE_CAR;
    }

    private void onSearch() {
        setSearchFilter();
        if (this.onSearchListener != null) {
            this.onSearchListener.onFilterRefresh(sort, searchFilter);
        }
    }

    private void setSearchFilter() {
        searchFilter.setPropertyId(filterProperty == null ? 0 : filterProperty.getId());
        if (filterPriceMax < filterPriceMin && filterPriceMax > 0) {
            double tmp = filterPriceMin;
            filterPriceMin = filterPriceMax;
            filterPriceMax = tmp;
        }
        searchFilter.setPriceMin(filterPriceMin);
        searchFilter.setPriceMax(filterPriceMax);
        if (filterTableNum != null) {
            searchFilter.setTableMin(filterTableNum.getMin());
            searchFilter.setTableMax(filterTableNum.getMax());
        }
        //婚品 分类用的 categoryId 包邮 service 字段
        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT) {
            searchFilter.setCategoryId(filterProductProperty == null ? 0 : filterProductProperty
                    .getId());
            searchFilter.setProductService(filterDiscountLabel == null ? -1 : filterDiscountLabel
                    .getId());
        }
    }

    @OnClick(R.id.cb_price_popular)
    void onPriceClick() {
        if (isPrice()) {
            if (sort.equals(NewSearchApi.SORT_PRICE_ASC)) {
                sort = NewSearchApi.SORT_PRICE_DESC;
            } else {
                sort = NewSearchApi.SORT_PRICE_ASC;
            }
        } else {
            sort = NewSearchApi.SORT_HOT;
        }
        setPriceSortImg();
        onSearch();
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cb_default:
                sort = NewSearchApi.SORT_DEFAULT;
                imgPriceSort.setImageResource(R.mipmap.icon_order_gray_18_24);
                onSearch();
                break;
            case R.id.cb_newest:
                sort = NewSearchApi.SORT_NEWEST;
                imgPriceSort.setImageResource(R.mipmap.icon_order_gray_18_24);
                onSearch();
                break;
            case R.id.cb_sold_count:
                sort = NewSearchApi.SORT_SOLD_DESC;
                imgPriceSort.setImageResource(R.mipmap.icon_order_gray_18_24);
                onSearch();
                break;
            case R.id.cb_price_popular:
                // 由setCbPricePopularClickListener方法去处理
                break;
        }
    }

    private void setPriceSortImg() {
        if (cgSort.getCheckedId() == R.id.cb_price_popular) {
            switch (sort) {
                case NewSearchApi.SORT_PRICE_DESC:
                    imgPriceSort.setImageResource(R.mipmap.icon_order_desc_18_24);
                    break;
                case NewSearchApi.SORT_PRICE_ASC:
                    imgPriceSort.setImageResource(R.mipmap.icon_order_asc_18_24);
                    break;
                default:
                    imgPriceSort.setImageResource(R.mipmap.icon_order_gray_18_24);
                    break;
            }
        } else {
            imgPriceSort.setImageResource(R.mipmap.icon_order_gray_18_24);
        }
    }


    @OnClick(R.id.filtrate_layout)
    void showFilterDlg() {
        int filterDlgWidth = CommonUtil.getDeviceSize(mContext).x / 5 * 4;
        int columnWidth = (filterDlgWidth - CommonUtil.dp2px(mContext, 28 + 16)) / 3;
        if (filterDlg == null) {
            filterDlg = new Dialog(mContext, R.style.BubbleDialogTheme);
            View dlgView = View.inflate(mContext, R.layout.dialog_search_filter, null);
            dlgViewHolder = new FilterDlgViewHolder(dlgView);

            filterDlg.setContentView(dlgView);
            filterDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    View currentFocusView = ((Activity) mContext).getCurrentFocus();
                    if (currentFocusView != null) {
                        imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
                    }
                }
            });

            Window win = filterDlg.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                Point point = CommonUtil.getDeviceSize(mContext);
                params.width = filterDlgWidth;
                params.height = point.y;
                win.setGravity(Gravity.END);
                win.setWindowAnimations(R.style.dialog_anim_right_in_style);
            }
        }


        final LabelFilterAdapter adapter;
        LabelFilterAdapter adapter2 = null;
        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_HOTEL) {
            if (merchantFilter == null) {
                return;
            }
            // 酒店桌数分类数据
            MerchantFilterHotelTableLabel defaultTableNum = new MerchantFilterHotelTableLabel();
            defaultTableNum.setId(-1);
            defaultTableNum.setName("全部");
            ArrayList<MerchantFilterHotelTableLabel> tableNums = new ArrayList<>();
            tableNums.add(defaultTableNum);
            tableNums.addAll(merchantFilter.getHotel()
                    .getTableNums());
            int tableNumId = 1;//用于桌数选中显示 无别的意义
            ArrayList<Label> data = new ArrayList<>();
            for (MerchantFilterHotelTableLabel l : tableNums) {
                Label label = new Label();
                if (l.getId() != -1) {
                    label.setId(tableNumId++);
                } else {
                    label.setId(l.getId());
                }
                label.setDesc(l.getDesc());
                label.setName(l.getName());
                label.setKeyWord(l.getKeyWord());
                label.setOrder(l.getOrder());
                label.setType(l.getType());
                data.add(label);
            }
            adapter = new LabelFilterAdapter(data, mContext, columnWidth);

            adapter.setHasDefault(true);
            if (filterTableNum == null) {
                adapter.setChecked(defaultTableNum);
            } else {
                adapter.setChecked(filterTableNum);
            }
            dlgViewHolder.tableNumLayout.setVisibility(View.VISIBLE);
            dlgViewHolder.tableNumsGrid.setAdapter(adapter);
            int hotelRows = data.size() / 3 + (data.size() % 3 == 0 ? 0 : 1);
            dlgViewHolder.tableNumsGrid.getLayoutParams().height = CommonUtil.dp2px(mContext,
                    hotelRows * 36);
            dlgViewHolder.categoryLayout.setVisibility(View.GONE);
            dlgViewHolder.discountLayout.setVisibility(View.GONE);
        } else if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_CAR) {
            ArrayList<Label> carLabels = new ArrayList<>();
            Label label1 = new Label();
            label1.setId(-1);
            label1.setName("全部");
            carLabels.add(label1);
            Label label2 = new Label();
            label2.setId(1);
            label2.setName("精选套餐");
            carLabels.add(label2);
            Label label3 = new Label();
            label3.setId(2);
            label3.setName("个性自选");
            carLabels.add(label3);
            adapter = new LabelFilterAdapter(carLabels, mContext, columnWidth);

            adapter.setHasDefault(true);
            if (filterProperty == null) {
                adapter.setChecked(label1);
            } else {
                adapter.setChecked(filterProperty);
            }
            dlgViewHolder.categoryLayout.setVisibility(View.VISIBLE);
            dlgViewHolder.discountLayout.setVisibility(View.GONE);
            dlgViewHolder.categoryGrid.setAdapter(adapter);
            dlgViewHolder.categoryGrid.getLayoutParams().height = CommonUtil.dp2px(mContext, 36);
            dlgViewHolder.tableNumLayout.setVisibility(View.GONE);

        } else if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT) {
            // 婚品
            if (productCategories == null) {
                productCategories = Properties3Util.getPropertiesFromFile(mContext);
            }
            // 婚品分类数据
            MerchantProperty defaultProductP = new MerchantProperty();
            defaultProductP.setId(-1);
            defaultProductP.setName("全部");
            ArrayList<MerchantProperty> productPropertiesList = new ArrayList<>();
            productPropertiesList.add(defaultProductP);
            productPropertiesList.addAll(productCategories);
            // 婚品折扣分类
            Label label1 = new Label();
            ArrayList<Label> discountLabels = new ArrayList<>();
            label1.setId(-1);
            label1.setName("全部");
            discountLabels.add(label1);
            Label label2 = new Label();
            label2.setId(1);
            label2.setName("包邮");
            discountLabels.add(label2);
            Label label3 = new Label();
            label3.setId(2);
            label3.setName("消费保障");
            discountLabels.add(label3);

            adapter = new LabelFilterAdapter(productPropertiesList, mContext, columnWidth);
            adapter2 = new LabelFilterAdapter(discountLabels, mContext, columnWidth);
            adapter.setHasDefault(true);
            adapter2.setHasDefault(true);
            adapter.setChecked(filterProductProperty == null ? defaultProductP :
                    filterProductProperty);
            adapter2.setChecked(filterDiscountLabel == null ? label1 : filterDiscountLabel);

            int categoryRows = productPropertiesList.size() / 3 + (productPropertiesList.size() %
                    3 == 0 ? 0 : 1);
            int discountRows = discountLabels.size() / 3 + (discountLabels.size() % 3 == 0 ? 0 : 1);
            dlgViewHolder.categoryLayout.setVisibility(View.VISIBLE);
            dlgViewHolder.discountLayout.setVisibility(View.VISIBLE);
            dlgViewHolder.categoryGrid.setAdapter(adapter);
            dlgViewHolder.categoryGrid.getLayoutParams().height = CommonUtil.dp2px(mContext,
                    categoryRows * 36);
            dlgViewHolder.discountGrid.setAdapter(adapter2);
            dlgViewHolder.discountGrid.getLayoutParams().height = CommonUtil.dp2px(mContext,
                    discountRows * 36);
            dlgViewHolder.tableNumLayout.setVisibility(View.GONE);
        } else {
            // 套餐、案例 商家
            if (merchantFilter == null) {
                return;
            }
            // 服务分类数据
            MerchantFilterHotelTableLabel defaultServiceP = new MerchantFilterHotelTableLabel();
            defaultServiceP.setId(-1);
            defaultServiceP.setName("全部");
            ArrayList<MerchantFilterHotelTableLabel> servicePropertyList = new ArrayList<>();
            servicePropertyList.add(defaultServiceP);
            servicePropertyList.addAll(merchantFilter.getMerchantPropertyLabels());
            ArrayList<Label> data = new ArrayList<>();
            for (MerchantFilterHotelTableLabel l : servicePropertyList) {
                Label label = new Label();
                label.setId(l.getId());
                label.setDesc(l.getDesc());
                label.setName(l.getName());
                label.setKeyWord(l.getKeyWord());
                label.setOrder(l.getOrder());
                label.setType(l.getType());

                data.add(label);
            }
            adapter = new LabelFilterAdapter(data, mContext, columnWidth);
            adapter.setHasDefault(true);
            if (filterProperty == null) {
                adapter.setChecked(defaultServiceP);
            } else {
                adapter.setChecked(filterProperty);
            }

            int categoryRows = servicePropertyList.size() / 3 + (servicePropertyList.size() % 3
                    == 0 ? 0 : 1);
            dlgViewHolder.categoryLayout.setVisibility(View.VISIBLE);
            dlgViewHolder.categoryGrid.setAdapter(adapter);
            dlgViewHolder.categoryGrid.getLayoutParams().height = CommonUtil.dp2px(mContext,
                    categoryRows * 36);
            dlgViewHolder.tableNumLayout.setVisibility(View.GONE);
            dlgViewHolder.discountLayout.setVisibility(View.GONE);
        }

        final LabelFilterAdapter finalAdapter = adapter2;
        dlgViewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_HOTEL) {
                    filterTableNum = adapter.getCheckPositionMerchantLabel();
                    filterTableNum.setMin(filterTableNum.getMinFromName());
                    filterTableNum.setMax(filterTableNum.getMaxFromName());
                } else if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT) {
                    if (finalAdapter != null) {
                        filterDiscountLabel = finalAdapter.getCheckPosition();
                    }
                    filterProductProperty = (MerchantProperty) adapter.getCheckPosition();
                } else {
                    filterProperty = adapter.getCheckPosition();
                }
                if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK || searchType ==
                        NewSearchApi.SearchType.SEARCH_TYPE_HOTEL || searchType == NewSearchApi
                        .SearchType.SEARCH_TYPE_PRODUCT || searchType == NewSearchApi.SearchType
                        .SEARCH_TYPE_CAR) {
                    try {
                        String maxPrice = dlgViewHolder.etPriceMax.getText()
                                .toString()
                                .trim();
                        String minPrice = dlgViewHolder.etPriceMin.getText()
                                .toString()
                                .trim();
                        filterPriceMin = TextUtils.isEmpty(minPrice) ? 0 : Double.valueOf(minPrice);
                        filterPriceMax = TextUtils.isEmpty(maxPrice) ? 0 : Double.valueOf(maxPrice);
                    } catch (Exception e) {
                        e.printStackTrace();
                        filterPriceMin = filterPriceMax = 0;
                    }
                } else {
                    filterPriceMin = filterPriceMax = 0;
                }
                onSearch();
                filterDlg.cancel();
                View currentFocusView = ((Activity) mContext).getCurrentFocus();
                if (currentFocusView != null) {
                    imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        final LabelFilterAdapter finalAdapter1 = adapter2;
        dlgViewHolder.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.resetDefault();
                if (finalAdapter1 != null) {
                    finalAdapter1.resetDefault();
                    finalAdapter1.notifyDataSetChanged();
                }
                dlgViewHolder.etPriceMin.setText("");
                dlgViewHolder.etPriceMax.setText("");
                adapter.notifyDataSetChanged();
            }
        });

        // 套餐，酒店，婚品，婚车，都显示价格筛选控件
        if (searchType == NewSearchApi.SearchType.SEARCH_TYPE_WORK || searchType == NewSearchApi
                .SearchType.SEARCH_TYPE_HOTEL || searchType == NewSearchApi.SearchType
                .SEARCH_TYPE_PRODUCT || searchType == NewSearchApi.SearchType.SEARCH_TYPE_CAR) {
            dlgViewHolder.priceFilterLayout.setVisibility(View.VISIBLE);
            if (filterPriceMin > 0) {
                dlgViewHolder.etPriceMin.setText(CommonUtil.formatDouble2String(filterPriceMin));
            } else {
                dlgViewHolder.etPriceMin.setText("");
            }
            if (filterPriceMax > 0) {
                dlgViewHolder.etPriceMax.setText(CommonUtil.formatDouble2String(filterPriceMax));
            } else {
                dlgViewHolder.etPriceMax.setText("");
            }
        } else {
            dlgViewHolder.priceFilterLayout.setVisibility(View.GONE);
        }
        filterDlg.show();
    }

    class FilterDlgViewHolder {
        @BindView(R.id.btn_reset)
        Button btnReset;
        @BindView(R.id.btn_confirm)
        Button btnConfirm;
        @BindView(R.id.action_layout)
        RelativeLayout actionLayout;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.category_grid)
        GridView categoryGrid;
        @BindView(R.id.category_layout)
        LinearLayout categoryLayout;
        @BindView(R.id.discount_grid)
        GridView discountGrid;
        @BindView(R.id.discount_layout)
        LinearLayout discountLayout;
        @BindView(R.id.et_price_min)
        EditText etPriceMin;
        @BindView(R.id.et_price_max)
        EditText etPriceMax;
        @BindView(R.id.price_filter_layout)
        LinearLayout priceFilterLayout;
        @BindView(R.id.table_nums_grid)
        GridView tableNumsGrid;
        @BindView(R.id.table_num_layout)
        LinearLayout tableNumLayout;

        FilterDlgViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    interface OnSearchFilterListener {
        void onFilterRefresh(String sort, SearchFilter filter);
    }
}
