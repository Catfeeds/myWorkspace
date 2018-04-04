package com.hunliji.marrybiz.view.weddingcar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.weddingcar.WeddingCarOrderDetailAdapter;
import com.hunliji.marrybiz.api.weddingcar.WeddingCarApi;
import com.hunliji.marrybiz.fragment.weddingcar.WeddingCarTakeOrderDialogFragment;
import com.hunliji.marrybiz.model.weddingcar.WeddingCarOrderDetail;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.widget.PriceKeyboardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 婚车订单详情
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarOrderDetailActivity extends HljBaseNoBarActivity implements
        WeddingCarOrderDetailAdapter.onWeddingCarOrderDetailAdapterListener {

    public static final String ORDER_ID = "order_id";
    private static final String ORDER_FRAGMENT_TAG = "take_order_tag";

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.tv_modify_price)
    TextView tvModifyPrice;
    @BindView(R.id.tv_refuse)
    TextView tvRefuse;
    @BindView(R.id.tv_take_order)
    TextView tvTakeOrder;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.layout_fragment)
    FrameLayout layoutFragment;

    private WeddingCarOrderDetailAdapter orderDetailAdapter;
    private HljHttpSubscriber loadSub;
    private HljHttpSubscriber changePriceSub;
    private HljHttpSubscriber refuseSub;
    private long orderId;
    private WeddingCarOrderDetail orderDetail;
    private Dialog priceDialog;
    private Dialog refuseDialog;
    private WeddingCarTakeOrderDialogFragment takeOrderDialogFragment;
    private PriceKeyboardView keyboardView;
    private double latestMoney;//改价dialog 记录上一次输入的价格

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_order_detail);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            orderId = getIntent().getLongExtra(ORDER_ID, 0L);
        }
        orderDetailAdapter = new WeddingCarOrderDetailAdapter(this);
        orderDetailAdapter.setOnWeddingCarOrderDetailAdapterListener(this);
    }

    private void initWidget() {
        setDefaultStatusBarPadding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(orderDetailAdapter);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    private void initLoad() {
        refresh();
    }

    public void refresh() {
        CommonUtil.unSubscribeSubs(loadSub);
        loadSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<WeddingCarOrderDetail>() {
                    @Override
                    public void onNext(WeddingCarOrderDetail weddingCarOrderDetail) {
                        setOrderDetail(weddingCarOrderDetail);
                    }
                })
                .build();
        WeddingCarApi.getWeddingCarOrderDetail(orderId)
                .subscribe(loadSub);
    }

    private void setOrderDetail(WeddingCarOrderDetail orderDetail) {
        this.orderDetail = orderDetail;
        setWidget();
        orderDetailAdapter.setOrderDetail(orderDetail);
        orderDetailAdapter.calculateItemCount();
        orderDetailAdapter.notifyDataSetChanged();
    }

    private void setWidget() {
        if (orderDetail == null) {
            return;
        }
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(orderDetail.getStatusName());
        layoutBottom.setVisibility(orderDetail.getStauts() == WeddingCarOrderDetail
                .WAIT_TAKE_ORDER ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.tv_modify_price)
    void onChangePrice() {
        if (orderDetail == null) {
            return;
        }
        showChangePrice();
    }

    private void showChangePrice() {
        if (priceDialog != null && priceDialog.isShowing()) {
            return;
        }
        if (priceDialog == null) {
            priceDialog = new Dialog(this, R.style.BubbleDialogTheme);
            priceDialog.setContentView(R.layout.dialog_change_price_new);
            priceDialog.findViewById(R.id.btn_key_hide)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            priceDialog.dismiss();
                        }
                    });
            Window win = priceDialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);

            keyboardView = (PriceKeyboardView) priceDialog.findViewById(R.id.keyboard);
            keyboardView.setConfirmOnClickListener(new PriceKeyboardView.ConfirmOnClickListener() {
                @Override
                public void priceConfirm(double newTotalPrice, double newDepositPrice) {
                    if (newTotalPrice == orderDetail.getActualMoney()) {
                        Toast.makeText(WeddingCarOrderDetailActivity.this,
                                getString(R.string.msg_need_new_price),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        if (newTotalPrice > 0 && newTotalPrice != orderDetail.getActualMoney()) {
                            latestMoney = newTotalPrice;
                            changePrice(newTotalPrice);
                        }
                        priceDialog.dismiss();
                    }
                }
            });
        }
        if (keyboardView == null) {
            keyboardView = (PriceKeyboardView) priceDialog.findViewById(R.id.keyboard);
        }
        if (keyboardView != null) {
            keyboardView.setPriceModifyMode(PriceKeyboardView.MODE_TOTAL);
            keyboardView.setTotalPrices(latestMoney, orderDetail.getActualMoney());
        }
        priceDialog.show();
    }

    private void changePrice(final double price) {
        CommonUtil.unSubscribeSubs(changePriceSub);
        changePriceSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        setChangePriceResult(result, price);
                    }
                })
                .build();
        WeddingCarApi.weddingCarChangePrice(orderId, price)
                .subscribe(changePriceSub);
    }

    private void setChangePriceResult(HljHttpResult result, double price) {
        if (result != null) {
            if (result.getStatus() != null) {
                if (result.getStatus()
                        .getRetCode() == 0) {
                    refresh();
                    Toast.makeText(this, "改价成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this,
                            result.getStatus()
                                    .getMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @OnClick(R.id.tv_refuse)
    void onRefuse() {
        if (orderDetail == null) {
            return;
        }
        showRefuseDialog();
    }

    private void showRefuseDialog() {
        if (refuseDialog != null && refuseDialog.isShowing()) {
            return;
        }
        if (refuseDialog == null) {
            refuseDialog = DialogUtil.createDoubleButtonDialog(this,
                    "确认拒绝接单？",
                    "确定",
                    "取消",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refuseDialog.dismiss();
                            refuseOrder();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refuseDialog.dismiss();
                        }
                    });
        }
        refuseDialog.show();
    }

    private void refuseOrder() {
        CommonUtil.unSubscribeSubs(refuseSub);
        refuseSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        setRefuseResult(result);
                    }
                })
                .build();
        WeddingCarApi.refuseOrder(orderId)
                .subscribe(refuseSub);
    }

    private void setRefuseResult(HljHttpResult result) {
        if (result != null) {
            if (result.getStatus() != null) {
                if (result.getStatus()
                        .getRetCode() == 0) {
                    Toast.makeText(this, "成功拒绝接单", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this,
                            result.getStatus()
                                    .getMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(this, "拒绝接单失败", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this, "拒绝接单失败", Toast.LENGTH_SHORT)
                    .show();
        }
        refresh();
    }

    @OnClick(R.id.tv_take_order)
    void onTakeOrder() {
        if (refuseDialog != null && refuseDialog.isShowing()) {
            return;
        }
        showTakeOrderDialog();
    }

    private void showTakeOrderDialog() {
        if (takeOrderDialogFragment == null) {
            takeOrderDialogFragment = WeddingCarTakeOrderDialogFragment.newInstance(orderId,
                    getStatusBarHeight(this));
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment, takeOrderDialogFragment, ORDER_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(ORDER_FRAGMENT_TAG) != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(takeOrderDialogFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSub, changePriceSub, refuseSub);
    }

    @Override
    public void onCall(WeddingCarOrderDetail orderDetail) {
        if (orderDetail == null || TextUtils.isEmpty(orderDetail.getBuyerPhone())) {
            return;
        }
        callUp(Uri.parse("tel:" + orderDetail.getBuyerPhone()));
    }

    @Override
    public void onSms(WeddingCarOrderDetail orderDetail) {
        if (orderDetail == null || orderDetail.getUserId() <= 0) {
            return;
        }
        Intent intent = new Intent(this, WSMerchantChatActivity.class);
        CustomerUser user = new CustomerUser();
        user.setAvatar(orderDetail.getUser() == null ? null : orderDetail.getUser()
                .getAvatar());
        user.setId(orderDetail.getUserId());
        user.setNick(orderDetail.getUser() == null ? null : orderDetail.getUser()
                .getNick());
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
