package com.hunliji.hljcommonviewlibrary.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.AnimUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
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
import butterknife.OnTouch;

/**
 * 婚品通用筛选
 * Created by chen_bin on 2017/5/3 0003.
 */
public class ProductFilterMenuView extends FrameLayout implements AdapterView
        .OnItemClickListener, CheckableLinearGroup.OnCheckedChangeListener, CheckableLinearLayout
        .OnCheckedChangeListener {
    @BindView(R2.id.menu_bg_layout)
    FrameLayout menuBgLayout;
    @BindView(R2.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R2.id.left_menu_list)
    ListView leftMenuList;
    @BindView(R2.id.right_menu_list)
    ListView rightMenuList;
    @BindView(R2.id.menu_layout)
    RelativeLayout menuLayout;
    @BindView(R2.id.cg_sort)
    CheckableLinearGroup cgSort;
    @BindView(R2.id.cb_default)
    CheckableLinearButton cbDefault;
    @BindView(R2.id.cb_price)
    CheckableLinearButton cbPrice;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.img_price)
    ImageView imgPrice;
    @BindView(R2.id.cb_collect)
    CheckableLinearButton cbCollect;
    @BindView(R2.id.cb_sold_count)
    CheckableLinearButton cbSoldCount;
    @BindView(R2.id.cb_category)
    CheckableLinearLayout cbCategory;
    @BindView(R2.id.tv_category)
    TextView tvCategory;
    @BindView(R2.id.img_category)
    ImageView imgCategory;
    @BindView(R2.id.cb_filtrate)
    CheckableLinearButton cbFiltrate;
    private ScrollableLayout scrollableLayout;
    private FiltrateMenuAdapter leftMenuAdapter;
    private FiltrateMenuAdapter rightMenuAdapter;
    private ArrayList<ShopCategory> shopCategories;
    private ShopCategory parentShopCategory; //界面中的一级分类
    private ShopCategory childShopCategory; //界面中的二级分类
    private String order;
    private long categoryId;
    public int type;
    private boolean isShow;
    private final static String SORT_PRICE_UP = "price_up";
    private final static String SORT_PRICE_DOWN = "price_down";
    private final static String SORT_COLLECT = "collect";
    private final static String SORT_SOLD_COUNT = "sold_count";
    public final static int FILTER_TYPE_PRODUCT_MERCHANT = 0; //婚品商家
    public final static int FILTER_TYPE_SELECT_PRODUCT = 1; //商家端选择婚品
    private OnFilterResultListener onFilterResultListener;
    private HljHttpSubscriber initSub;

    public ProductFilterMenuView(@NonNull Context context) {
        this(context, null);
    }

    public ProductFilterMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductFilterMenuView(
            @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.common_filtrate_menu_layout___cm, this);
        ButterKnife.bind(this, view);
        shopCategories = new ArrayList<>();
        cgSort.setOnCheckedChangeListener(this);
        cbCategory.setOnCheckedChangeListener(this);
        leftMenuAdapter = new FiltrateMenuAdapter(getContext(),
                R.layout.filtrate_menu_list_item___cm);
        leftMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        leftMenuList.setItemsCanFocus(true);
        leftMenuList.setOnItemClickListener(this);
        leftMenuList.setAdapter(leftMenuAdapter);
        rightMenuAdapter = new FiltrateMenuAdapter(getContext(),
                R.layout.filtrate_menu_list_item2___cm);
        rightMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        rightMenuList.setItemsCanFocus(true);
        rightMenuList.setOnItemClickListener(this);
        rightMenuList.setAdapter(rightMenuAdapter);
    }

    public void setFilterType(int type) {
        this.type = type;
        switch (type) {
            case FILTER_TYPE_PRODUCT_MERCHANT:
                cbDefault.setVisibility(VISIBLE);
                cbPrice.setVisibility(VISIBLE);
                cbSoldCount.setVisibility(VISIBLE);
                cbCategory.setVisibility(VISIBLE);
                break;
            case FILTER_TYPE_SELECT_PRODUCT:
                cbDefault.setVisibility(VISIBLE);
                cbPrice.setVisibility(VISIBLE);
                cbCollect.setVisibility(VISIBLE);
                cbCategory.setVisibility(VISIBLE);
                break;
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void initLoad(long merchantId) {
        if (merchantId == 0) {
            return;
        }
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopCategory>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ShopCategory>> listHljHttpData) {
                            try {
                                shopCategories.clear();
                                menuLayout.setVisibility(VISIBLE);
                                if (!CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
                                    for (ShopCategory shopCategory : listHljHttpData.getData()) {
                                        if (!CommonUtil.isCollectionEmpty(shopCategory
                                                .getChildren())) {
                                            ShopCategory childShopCategory = new ShopCategory();
                                            childShopCategory.setId(shopCategory.getId());
                                            childShopCategory.setName(getContext().getString(R
                                                    .string.label_all___cm));
                                            shopCategory.getChildren()
                                                    .add(0, childShopCategory);
                                        }
                                        shopCategories.add(shopCategory);
                                    }
                                    if (!CommonUtil.isCollectionEmpty(shopCategories)) {
                                        ShopCategory shopCategory = new ShopCategory();
                                        shopCategory.setName(getContext().getString(R.string
                                                .label_all___cm));
                                        shopCategories.add(0, shopCategory);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .build();
            CommonApi.getMerchantProductCategoriesObb(merchantId)
                    .subscribe(initSub);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShopCategory shopCategory = (ShopCategory) parent.getAdapter()
                .getItem(position);
        if (shopCategory == null) {
            return;
        }
        if (parent == rightMenuList) {
            childShopCategory = shopCategory;
            parentShopCategory = shopCategories.get(leftMenuList.getCheckedItemPosition());
        } else {
            if (CommonUtil.isCollectionEmpty(shopCategory.getChildren())) {
                childShopCategory = null;
                parentShopCategory = shopCategory;
                rightMenuList.setVisibility(View.GONE);
            } else {
                position = -1;
                parentShopCategory = shopCategory;
                rightMenuList.setVisibility(View.VISIBLE);
                rightMenuAdapter.setItems(shopCategory.getChildren());
                if (childShopCategory != null && childShopCategory.getId() > 0) {
                    position = shopCategory.getChildren()
                            .indexOf(childShopCategory);
                }
                if (position < 0) {
                    childShopCategory = parentShopCategory.getChildren()
                            .get(0);
                    rightMenuList.setItemChecked(0, true);
                } else {
                    rightMenuList.setItemChecked(position, true);
                }
                return;
            }
        }
        cbCategory.setChecked(false);
        if (parentShopCategory.getId() == 0) {
            imgCategory.setImageResource(R.drawable.sl_ic_arrow_gray_down_2_red_up);
            tvCategory.setTextColor(ContextCompat.getColorStateList(getContext(),
                    R.color.black3_primary));
        } else {
            imgCategory.setImageResource(R.drawable.sl_ic_arrow_red_down_2_red_up);
            tvCategory.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        if (childShopCategory != null) {
            categoryId = childShopCategory.getId();
            tvCategory.setText(TextUtils.equals(childShopCategory.getName(),
                    getContext().getString(R.string.label_all___cm)) ? parentShopCategory.getName
                    () : childShopCategory.getName());
        } else if (parentShopCategory != null) {
            categoryId = parentShopCategory.getId();
            tvCategory.setText(TextUtils.equals(parentShopCategory.getName(),
                    getContext().getString(R.string.label_all___cm)) ? getContext().getString(R
                    .string.label_category___cm) : parentShopCategory.getName());
        }
        if (onFilterResultListener != null) {
            onFilterResultListener.onFilterResult(order, categoryId);
        }
    }

    @Override
    public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
        if (scrollableLayout != null) {
            scrollableLayout.scrollToBottom();
        }
        if (checkedId == R.id.cb_default) {
            order = null;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        } else if (checkedId == R.id.cb_price) {
            //cbPrice放在onClickedPriceSort()中执行
        } else if (checkedId == R.id.cb_collect) {
            order = SORT_COLLECT;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        } else if (checkedId == R.id.cb_sold_count) {
            order = SORT_SOLD_COUNT;
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        } else if (checkedId == R.id.cb_filtrate) {
            //cbFiltrate放在onClickedFiltrate()中执行
            imgPrice.setImageResource(R.mipmap.icon_order_gray_18_24);
        }
        if (onFilterResultListener != null && checkedId != R.id.cb_price && checkedId != R.id
                .cb_filtrate) {
            onFilterResultListener.onFilterResult(order, categoryId);
        }
    }

    @Override
    public void onCheckedChange(View view, boolean checked) {
        if (scrollableLayout != null) {
            scrollableLayout.scrollToBottom();
        }
        if (CommonUtil.isCollectionEmpty(shopCategories)) {
            ToastUtil.showToast(getContext(), null, R.string.label_no_category___cm);
            return;
        }
        if (!checked) {
            AnimUtil.hideMenuAnimations(menuInfoLayout, menuBgLayout);
        } else {
            rightMenuList.setVisibility(View.GONE);
            leftMenuList.setVisibility(View.VISIBLE);
            leftMenuAdapter.setItems(shopCategories);
            int position = 0;
            if (parentShopCategory != null) {
                position = shopCategories.indexOf(parentShopCategory);
            } else {
                parentShopCategory = shopCategories.get(0);
            }
            leftMenuList.setItemChecked(position, true);
            if (CommonUtil.isCollectionEmpty(parentShopCategory.getChildren())) {
                childShopCategory = null;
                rightMenuList.setVisibility(View.GONE);
            } else {
                rightMenuList.setVisibility(View.VISIBLE);
                rightMenuAdapter.setItems(parentShopCategory.getChildren());
                position = 0;
                if (childShopCategory != null) {
                    position = parentShopCategory.getChildren()
                            .indexOf(childShopCategory);
                } else {
                    childShopCategory = parentShopCategory.getChildren()
                            .get(0);
                }
                rightMenuList.setItemChecked(position, true);
            }
            AnimUtil.showMenuAnimations(menuInfoLayout, menuBgLayout);
        }
    }

    @OnClick(R2.id.cb_price)
    public void onClickedPriceSort() {
        if (scrollableLayout != null) {
            scrollableLayout.scrollToBottom();
        }
        if (TextUtils.equals(order, SORT_PRICE_UP)) {
            order = SORT_PRICE_DOWN;
            imgPrice.setImageResource(R.mipmap.icon_order_desc_18_24);
        } else {
            order = SORT_PRICE_UP;
            imgPrice.setImageResource(R.mipmap.icon_order_asc_18_24);
        }
        if (onFilterResultListener != null) {
            onFilterResultListener.onFilterResult(order, categoryId);
        }
    }

    @OnTouch(R2.id.menu_bg_layout)
    public boolean onHideMenu() {
        cbCategory.setChecked(false);
        return false;
    }

    public boolean isShowMenu() {
        return cbCategory.isChecked();
    }

    public void setScrollableLayout(ScrollableLayout scrollableLayout) {
        this.scrollableLayout = scrollableLayout;
    }

    public interface OnFilterResultListener {
        void onFilterResult(String order, long categoryId);
    }

    public void setOnFilterResultListener(OnFilterResultListener onFilterResultListener) {
        this.onFilterResultListener = onFilterResultListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CommonUtil.unSubscribeSubs(initSub);
    }
}