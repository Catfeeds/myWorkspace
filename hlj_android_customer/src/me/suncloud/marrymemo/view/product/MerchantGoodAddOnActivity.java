package me.suncloud.marrymemo.view.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljcommonviewlibrary.widgets.ProductFilterMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.product.ProductListFragment;
import me.suncloud.marrymemo.util.NoticeUtil;

/**
 * 婚品凑单页
 * Created by jinxin on 2017/10/20 0020.
 */

public class MerchantGoodAddOnActivity extends HljBaseNoBarActivity implements ProductFilterMenuView
        .OnFilterResultListener, PullToRefreshBase.OnRefreshListener<ScrollableLayout> {


    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.filter_menu_view)
    ProductFilterMenuView filterMenuView;

    private ProductListFragment productListFragment;
    private NoticeUtil noticeUtil;
    private long merchantId;
    private long expressTemplateId;//运费模板id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_good_add_on);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        initConstant();
        initWidget();
    }

    private void initConstant() {
        if (getIntent() != null) {
            merchantId = getIntent().getLongExtra("merchantId", 0L);
            expressTemplateId = getIntent().getLongExtra("expressTemplateId",0L);
        }
        productListFragment = ProductListFragment.newInstance(getUrl(null,0L));
        productListFragment.setShowProgressBar(true);
    }

    private void initWidget() {
        filterMenuView.setOnFilterResultListener(this);
        filterMenuView.setFilterType(ProductFilterMenuView.FILTER_TYPE_PRODUCT_MERCHANT);
        filterMenuView.initLoad(merchantId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, productListFragment, null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (filterMenuView.isShowMenu()) {
            filterMenuView.onHideMenu();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        super.onBackPressed();
    }

    private String getUrl(String order, long categoryId) {
        StringBuilder url = new StringBuilder(String.format(Constants.HttpPath
                        .GET_SHOPPING_CART_ADD_ON,
                merchantId));
        if(expressTemplateId >0){
            url.append("&express_template_id=")
                    .append(expressTemplateId);
        }
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
    public void onFilterResult(String order, long categoryId) {
        if (productListFragment != null && productListFragment.getScrollableView() != null) {
            productListFragment.refresh(getUrl(order, categoryId));
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollableLayout> refreshView) {
        if (productListFragment != null && productListFragment.getScrollableView() != null) {
            productListFragment.setShowProgressBar(false);
            productListFragment.refresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        }
        noticeUtil.onPause();
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType.REFRESH_SHOPPING_CART, null));
    }
}
