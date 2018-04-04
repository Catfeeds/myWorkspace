package me.suncloud.marrymemo.view.product;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalStaggeredRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductRecyclerAdapter;

import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;

import me.suncloud.marrymemo.view.MainActivity;
import me.suncloud.marrymemo.view.MyOrderListActivity;

/**
 * 婚品订单支付成功页
 * Created by chen_bin on 2017/5/2 0002.
 */
public class AfterPayProductOrderActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalStaggeredRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private HeaderViewHolder headerViewHolder;
    private ProductRecyclerAdapter adapter;
    private String productIds;
    private HljHttpSubscriber refreshSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_ptr_staggered_recycler_view___cm);
        ButterKnife.bind(this);
        productIds = getIntent().getStringExtra("product_ids");
        View headerView = View.inflate(this, R.layout.after_pay_product_order_header_item, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setPadding(0, 0, 0, CommonUtil.dp2px(this, 10));
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setItemPrefetchEnabled(false);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new ProductRecyclerAdapter(this);
        adapter.setHeaderView(headerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        initNotifyDialog();
        onRefresh(null);
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                HljCommon.SharedPreferencesNames.PREF_NOTICE_OPEN_DLG_PAY,
                "付款成功",
                "立即开启消息通知，及时掌握订单状态和物流信息哦~",
                R.drawable.icon_dlg_appointment);
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                            recyclerView.setBackgroundColor(ContextCompat.getColor(
                                    AfterPayProductOrderActivity.this,
                                    R.color.colorWhite));
                            if (headerViewHolder != null) {
                                headerViewHolder.recommendTitleLayout.setVisibility(View.VISIBLE);
                            }
                            adapter.setProducts(listHljHttpData.getData());
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CommonApi.getProductsObb(getUrl(), 1, Constants.PER_PAGE)
                    .subscribe(refreshSub);
        }
    }

    public class HeaderViewHolder {
        @BindView(R.id.btn_review_order)
        Button btnReviewOrder;
        @BindView(R.id.btn_go_product_channel)
        Button btnGoProductChannel;
        @BindView(R.id.recommend_title_layout)
        LinearLayout recommendTitleLayout;

        public HeaderViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            btnReviewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            btnGoProductChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra(MainActivity.ARG_MAIN_ACTION, MainActivity.MAIN_ACTION_PRODUCT);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private StaggeredGridLayoutManager.LayoutParams lp;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(AfterPayProductOrderActivity.this, 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int top = 0;
            int left = 0;
            int right = 0;
            int headerViewCount = adapter.getHeaderViewCount();
            int position = parent.getChildAdapterPosition(view);
            if (headerViewCount == 0 || position > 0) {
                top = headerViewCount == 0 || position > 2 ? space : 0;
                left = lp.getSpanIndex() == 0 ? 0 : space / 2;
                right = lp.getSpanIndex() == 0 ? space / 2 : 0;
            }
            outRect.set(left, top, right, 0);
        }
    }

    private String getUrl() {
        return String.format(Constants.HttpPath.GET_RECOMMEND_PRODUCTS,
                3) + "&product_ids=" + productIds;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyOrderListActivity.class);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        finish();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }
}
