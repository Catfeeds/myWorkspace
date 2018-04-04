package me.suncloud.marrymemo.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductImageAdapter;
import me.suncloud.marrymemo.fragment.ProductSkuFragment;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by wangtao on 2016/11/16.
 */

public class ProductDetailImageActivity extends HljBaseNoBarActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R.id.icon_msg_notice)
    View iconMsgNotice;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.icon_cart_notice)
    View iconCartNotice;
    @BindView(R.id.buy_layout)
    LinearLayout buyLayout;
    @BindView(R.id.info_content)
    FrameLayout infoContent;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.action_holder_layout2)
    LinearLayout actionHolderLayout2;
    @BindView(R.id.action_holder_layout3)
    RelativeLayout actionHolderLayout3;

    private ShopProduct product;

    private int disHeight; //actionBar 渐变范围
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disHeight = Math.round(CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 45));
        setContentView(R.layout.actyivity_product_image_detail);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout, actionHolderLayout2, actionHolderLayout3);
        product = getIntent().getParcelableExtra("product");
        buyLayout.setVisibility(product.getProductCount() == 0 || !product.isPublished() ? View
                .GONE : View.VISIBLE);
        ProductImageAdapter adapter = new ProductImageAdapter(this);
        adapter.setPhotos(product.getDetailPhotos());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (product == null) {
                    return;
                }
                float alpha;
                if (product.getHeaderPhotos() != null && !product.getHeaderPhotos()
                        .isEmpty()) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                            .getLayoutManager();
                    if (layoutManager.findFirstVisibleItemPosition() > 0) {
                        alpha = 1;
                    } else {
                        alpha = (float) -layoutManager.getChildAt(0)
                                .getTop() / disHeight;
                    }
                } else {
                    alpha = 1;
                }
                actionHolderLayout2.setAlpha(alpha);
            }
        });
    }


    @OnClick(R.id.btn_back)
    public void onBack() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @OnTouch(R.id.info_content)
    boolean popBackStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    /**
     * 跳转通知私信
     */
    @OnClick(R.id.btn_msg)
    public void onMessage() {
        if (AuthUtil.loginBindCheck(this)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_share)
    public void onShare() {
        if (product != null && product.getShare() != null) {
            ShareDialogUtil.onCommonShare(this, product.getShare());
        }
    }

    /**
     * 商品购物车
     */
    @OnClick(R.id.btn_shopping_cart)
    public void onToCart() {
        if (AuthUtil.loginBindCheck(this)) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            if (iconCartNotice != null) {
                iconCartNotice.setVisibility(View.GONE);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * 加入购物车
     */
    @OnClick(R.id.btn_cart)
    public void onAddCart() {
        showProductSku(true);
    }

    /**
     * 立即购买
     */
    @OnClick(R.id.btn_buy)
    public void onBuy() {
        showProductSku(false);
    }


    private void showProductSku(boolean addCart) {
        if (product == null) {
            return;
        }
        ProductSkuFragment productSkuFragment = (ProductSkuFragment) getSupportFragmentManager()
                .findFragmentByTag(
                "productSkuFragment");
        if (productSkuFragment != null) {
            return;
        }
        infoContent.setAlpha(1);
        productSkuFragment = (ProductSkuFragment) Fragment.instantiate(this,
                ProductSkuFragment.class.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable("product", product);
        bundle.putBoolean("addCart", addCart);
        productSkuFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up_to_top,
                R.anim.slide_out_down,
                R.anim.slide_in_up_to_top,
                R.anim.slide_out_down);
        ft.add(R.id.info_content, productSkuFragment, "productSkuFragment");
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, tvMsgCount, iconMsgNotice);
        }
        noticeUtil.onResume();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (noticeUtil == null) {
            noticeUtil = new NoticeUtil(this, tvMsgCount, iconMsgNotice);
        }
        noticeUtil.onResume();
        if (iconCartNotice != null) {
            iconCartNotice.setVisibility(Session.getInstance()
                    .isNewCart() ? View.VISIBLE : View.GONE);
        }
        super.onResume();
    }
}
