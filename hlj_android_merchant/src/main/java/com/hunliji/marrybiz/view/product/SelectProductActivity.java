package com.hunliji.marrybiz.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonviewlibrary.widgets.ProductFilterMenuView;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.product.SelectProductListFragment;
import com.hunliji.marrybiz.util.Session;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择婚品
 * Created by chen_bin on 2017/3/30 0030.
 */
public class SelectProductActivity extends HljBaseActivity implements ProductFilterMenuView
        .OnFilterResultListener {
    @BindView(R.id.root_item)
    TextView btnOk;
    @BindView(R.id.filter_menu_view)
    ProductFilterMenuView filterMenuView;
    private SelectProductListFragment fragment;
    private long merchantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        ButterKnife.bind(this);
        setOkText(R.string.label_send_shop_product);
        btnOk.setEnabled(false);
        merchantId = Session.getInstance()
                .getCurrentUser(this)
                .getMerchantId();
        filterMenuView.setOnFilterResultListener(this);
        filterMenuView.setFilterType(ProductFilterMenuView.FILTER_TYPE_PRODUCT_MERCHANT);
        filterMenuView.initLoad(merchantId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = SelectProductListFragment.newInstance(getUrl(null, 0));
        ft.add(R.id.fragment_content, fragment, "SelectProductListFragment");
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onOkButtonClick() {
        if (fragment != null && fragment.getSelectedProduct() != null) {
            Intent intent = getIntent();
            intent.putExtra("product", fragment.getSelectedProduct());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onFilterResult(String order, long categoryId) {
        if (fragment != null) {
            fragment.refresh(getUrl(order, categoryId));
        }
    }

    public void onCheckedChange() {
        btnOk.setEnabled(true);
    }

    private String getUrl(String order, long categoryId) {
        StringBuilder url = new StringBuilder(String.format(Constants.HttpPath.GET_MERCHANT_GOODS,
                merchantId));
        if (!TextUtils.isEmpty(order)) {
            url.append("&order=")
                    .append(order);
        }
        if (categoryId > 0) {
            url.append("&category_id=")
                    .append(categoryId);
        }
        return url.toString();
    }

    @Override
    public void onBackPressed() {
        if (filterMenuView.isShowMenu()) {
            filterMenuView.onHideMenu();
            return;
        }
        super.onBackPressed();
    }
}