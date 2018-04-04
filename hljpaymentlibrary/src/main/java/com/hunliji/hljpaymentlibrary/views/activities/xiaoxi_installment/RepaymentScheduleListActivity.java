package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.RepaymentScheduleListAdapter;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .RepaymentScheduleHeaderViewHolder;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .RepaymentScheduleViewHolder;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentSchedulesData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 小犀分期-还款计划列表
 * Created by chen_bin on 2017/8/17 0017.
 */
public class RepaymentScheduleListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, RepaymentScheduleViewHolder.OnRepayListener {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private View footerView;
    private View headerView;

    private Dialog progressDialog;
    private RepaymentScheduleListAdapter adapter;

    private XiaoxiInstallmentOrder order;
    private boolean isClear;

    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber settleUpSub;
    private HljHttpSubscriber repaySub;

    public static final String ARG_ORDER = "order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        initValues();
        initViews();
        onRefresh(null);
    }

    private void initValues() {
        order = getIntent().getParcelableExtra(ARG_ORDER);
    }

    private void initViews() {
        setOkTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        headerView = View.inflate(this, R.layout.repayment_schedule_header_item___pay, null);
        footerView = View.inflate(this, R.layout.repayment_schedule_footer_item___pay, null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepaymentScheduleListActivity.this,
                        RepaymentScheduleExtInfoActivity.class);
                intent.putExtra(RepaymentScheduleExtInfoActivity.ARG_ASSET_ORDER_ID,
                        order.getAssetOrderId());
                intent.putExtra(RepaymentScheduleExtInfoActivity.ARG_IS_CLEAR, isClear);
                startActivity(intent);
            }
        });
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(null);
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepaymentScheduleListAdapter(this);
        adapter.setCurrentStage(order.getCurrentStage());
        adapter.setOnRepayListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            boolean isShowProgressDialog = progressDialog != null && progressDialog.isShowing();
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<XiaoxiInstallmentSchedulesData>() {
                        @Override
                        public void onNext(XiaoxiInstallmentSchedulesData schedulesData) {
                            isClear = schedulesData.isClear();
                            if (isClear) {
                                hideOkText();
                            } else {
                                setOkText(R.string.btn_settle_up___pay);
                            }
                            RepaymentScheduleHeaderViewHolder headerViewHolder =
                                    (RepaymentScheduleHeaderViewHolder) headerView.getTag();
                            if (headerViewHolder == null) {
                                adapter.setHeaderView(headerView);
                                headerViewHolder = new RepaymentScheduleHeaderViewHolder
                                        (headerView);
                                headerView.setTag(headerViewHolder);
                            }
                            headerViewHolder.setSchedulesData(schedulesData);
                            headerViewHolder.setView(RepaymentScheduleListActivity.this,
                                    order,
                                    0,
                                    0);
                            adapter.setFooterView(footerView);
                            adapter.setSchedules(schedulesData.getSchedules());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressDialog(isShowProgressDialog ? progressDialog : null)
                    .setProgressBar(isShowProgressDialog || recyclerView.isRefreshing() ? null :
                            progressBar)
                    .build();
            XiaoxiInstallmentApi.getRepaymentSchedulesObb(this, order.getAssetOrderId())
                    .subscribe(refreshSub);
        }
    }

    @Override
    public void onOkButtonClick() {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_confirm_settle_up___pay),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShowProgressDialog()) {
                            return;
                        }
                        CommonUtil.unSubscribeSubs(settleUpSub);
                        settleUpSub = HljHttpSubscriber.buildSubscriber(
                                RepaymentScheduleListActivity.this)
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(RepaymentScheduleListActivity
                                                .this, R.string.msg_settle_up_success___pay);
                                        RxBus.getDefault()
                                                .post(new PayRxEvent(PayRxEvent.RxEventType
                                                        .REPAY_SUCCESS,
                                                        null));
                                        onRefresh(null);
                                    }
                                })
                                .setOnErrorListener(new SubscriberOnErrorListener() {
                                    @Override
                                    public void onError(Object o) {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                })
                                .setDataNullable(true)
                                .build();
                        XiaoxiInstallmentApi.settleUpObb(RepaymentScheduleListActivity.this,
                                order.getAssetOrderId())
                                .subscribe(settleUpSub);
                    }
                },
                null)
                .show();
    }

    @Override
    public void onRepay(int position, final RepaymentSchedule schedule) {
        if (schedule == null) {
            return;
        }
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.msg_confirm_repay___pay),
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isShowProgressDialog()) {
                            return;
                        }
                        CommonUtil.unSubscribeSubs(repaySub);
                        repaySub = HljHttpSubscriber.buildSubscriber
                                (RepaymentScheduleListActivity.this)
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(RepaymentScheduleListActivity
                                                .this, R.string.msg_repay_success___pay);
                                        RxBus.getDefault()
                                                .post(new PayRxEvent(PayRxEvent.RxEventType
                                                        .REPAY_SUCCESS,
                                                        null));
                                        onRefresh(null);
                                    }
                                })
                                .setOnErrorListener(new SubscriberOnErrorListener() {
                                    @Override
                                    public void onError(Object o) {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                })
                                .setDataNullable(true)
                                .build();
                        XiaoxiInstallmentApi.repayObb(RepaymentScheduleListActivity.this,
                                order.getAssetOrderId(),
                                schedule.getStage())
                                .subscribe(repaySub);
                    }
                },
                null)
                .show();
    }

    private boolean isShowProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return true;
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        return false;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, settleUpSub, repaySub);
    }
}