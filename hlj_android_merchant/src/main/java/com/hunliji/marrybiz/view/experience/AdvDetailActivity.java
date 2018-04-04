package com.hunliji.marrybiz.view.experience;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.experience.AdvDetailAdapter;
import com.hunliji.marrybiz.api.experienceshop.ExperienceShopApi;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.model.experience.ShowHistory;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func2;

/**
 * 推荐单详情
 * Created by jinxin on 2017/12/19 0019.
 */

public class AdvDetailActivity extends HljBaseActivity implements AdvDetailAdapter
        .OnAdvDetailAdapterClickListener {

    public static final String ARG_ID = "id";
    public static final String ARG_ADV_TYPE = "adv_type";
    public static final String ARG_ITEM_POSITION = "position";
    public static final String ARG_ITEM_STATUS = "status";
    public static final String ARG_ITEM_IS_COME = "is_come";
    private final int REMARK_REQUEST_CODE = 101;

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.line_remark)
    View lineRemark;
    @BindView(R.id.line_confirm)
    View lineConfirm;

    private long id;
    private AdvDetailAdapter detailAdapter;
    private HljHttpSubscriber loadSubscriber;
    private HljHttpSubscriber modifySaleSubscriber;
    private HljHttpSubscriber arriveShopSubscriber;
    private Dialog saleDialog;
    private Dialog expiredDialog;
    private Dialog arriveDialog;
    private AdvDetail order;
    private int advType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_recommend_detail);
        ButterKnife.bind(this);

        initValues();
        initWidget();
        initLoad();
    }

    private void initValues() {
        if (getIntent() != null) {
            id = getIntent().getLongExtra(ARG_ID, 0L);
            advType = getIntent().getIntExtra(ARG_ADV_TYPE, 0);
        }
        detailAdapter = new AdvDetailAdapter(this, advType);
        detailAdapter.setOnAdvDetailClickListener(this);
    }

    private void initWidget() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(detailAdapter);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    private void initLoad() {
        refresh();
    }

    private void refresh() {
        CommonUtil.unSubscribeSubs(loadSubscriber);
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setEmptyView(emptyView)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setResultZip(resultZip);
                    }
                })
                .build();

        Observable<AdvDetail> orderObb = ExperienceShopApi.getExperienceOrderDetail(id);
        Observable<List<ShowHistory>> showHistoryObb = ExperienceShopApi.getExperienceShowHistory
                (id);
        Observable<ResultZip> zipObb = Observable.zip(orderObb,
                showHistoryObb,
                new Func2<AdvDetail, List<ShowHistory>, ResultZip>() {
                    @Override
                    public ResultZip call(
                            AdvDetail experienceShopOrder, List<ShowHistory> showHistories) {
                        ResultZip resultZip = new ResultZip();
                        resultZip.order = experienceShopOrder;
                        resultZip.showHistoryList = showHistories;
                        return resultZip;
                    }
                });
        zipObb.subscribe(loadSubscriber);
    }

    private void setResultZip(ResultZip resultZip) {
        this.order = resultZip.order;
        if (order != null && order.getStatus() == AdvDetail.ORDER_HAVE_EXPIRED) {
            //已过期
            showExpiredDialog();
            return;
        }
        this.order = resultZip.order;
        setWidget();
        setExperienceShopOrder(order);
        setShowHistory(resultZip.showHistoryList);
        detailAdapter.notifyDataSetChanged();
    }

    private void showExpiredDialog() {
        if (expiredDialog != null && expiredDialog.isShowing()) {
            return;
        }
        if (expiredDialog == null) {
            expiredDialog = DialogUtil.createSingleButtonDialog(this,
                    getString(R.string.label_experience_shop_detail_expired),
                    "知道了",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            expiredDialog.dismiss();
                            AdvDetailActivity.this.finish();
                        }
                    });
        }
        expiredDialog.show();
    }

    private void setWidget() {
        layoutStatus.setVisibility(View.VISIBLE);
    }

    private void setExperienceShopOrder(AdvDetail order) {
        tvStatus.setText(getString(R.string.label_recommend_detail_status,
                AdvDetail.getStatusStringByType(order.getStatus(), advType)));
        setAction(order.getStatus());
        detailAdapter.setShopOrder(order);
    }

    private void setShowHistory(List<ShowHistory> showHistoryList) {
        detailAdapter.setShowHistory(showHistoryList);
    }

    private void setAction(int status) {
        switch (status) {
            case AdvDetail.ORDER_FOLLOW_UP_FAILED:
            case AdvDetail.ORDER_HAVE_CREATE:
                layoutBottom.setVisibility(View.VISIBLE);
                lineRemark.setVisibility(View.GONE);
                tvConfirm.setVisibility(View.GONE);
                lineConfirm.setVisibility(View.GONE);
                tvFollow.setVisibility(View.GONE);
                break;
            case AdvDetail.ORDER_HAVE_READ:
                layoutBottom.setVisibility(View.VISIBLE);
                tvRemark.setVisibility(View.VISIBLE);
                lineRemark.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.VISIBLE);
                lineConfirm.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
                break;
            default:
                layoutBottom.setVisibility(View.VISIBLE);
                tvRemark.setVisibility(View.VISIBLE);
                lineRemark.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.VISIBLE);
                lineConfirm.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.tv_remark)
    void onRemark() {
        onFillRemark(ExperienceShopRecommendRemarkActivity.FILL_REMARK);
    }

    @OnClick(R.id.tv_confirm)
    void onConfirm() {
        onFillRemark(ExperienceShopRecommendRemarkActivity.CONFIRM_ORDER);
    }

    @OnClick(R.id.tv_follow)
    void onFollowFailed() {
        onFillRemark(ExperienceShopRecommendRemarkActivity.FOLLOW_FAILED);
    }

    private void onFillRemark(int status) {
        Intent intent = new Intent(this, ExperienceShopRecommendRemarkActivity.class);
        intent.putExtra(ExperienceShopRecommendRemarkActivity.ARG_STATUS, status);
        intent.putExtra(ExperienceShopRecommendRemarkActivity.ARG_ID, id);
        startActivityForResult(intent, REMARK_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_anim_default, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REMARK_REQUEST_CODE && resultCode == RESULT_OK) {
            refresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSubscriber, modifySaleSubscriber, arriveShopSubscriber);
    }

    @Override
    public void onCall(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            callUp(Uri.parse("tel:" + phone.trim()));
        }
    }

    @Override
    public void onSms(AdvDetail detail) {
        if (detail == null || detail.getLead() == null || detail.getLead()
                .getUserId() <= 0) {
            return;
        }
        Intent intent = new Intent(this, WSMerchantChatActivity.class);
        CustomerUser user = new CustomerUser();
        user.setAvatar(detail.getLead()
                .getUser() == null ? null : detail.getLead()
                .getUser()
                .getAvatar());
        user.setId(detail.getLead()
                .getUserId());
        user.setNick(detail.getLead()
                .getUser() == null ? null : detail.getLead()
                .getUser()
                .getNick());
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onSale(String name, TextView tvName) {
        showSaleDialog(name);
    }

    private void showSaleDialog(String name) {
        if (saleDialog != null && saleDialog.isShowing()) {
            return;
        }

        if (saleDialog == null) {
            saleDialog = new Dialog(this, R.style.BubbleDialogTheme);
            saleDialog.setContentView(R.layout.dialog_modify_saler);
            final EditText editName = (EditText) saleDialog.findViewById(R.id.edit_name);
            editName.setText(name);
            saleDialog.findViewById(R.id.tv_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saleDialog.dismiss();
                        }
                    });
            saleDialog.findViewById(R.id.tv_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saleDialog.dismiss();
                            modifySaleName(editName.getText()
                                    .toString()
                                    .trim());
                        }
                    });
        }
        saleDialog.show();
    }

    private void modifySaleName(final String name) {
        CommonUtil.unSubscribeSubs(modifySaleSubscriber);
        modifySaleSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult != null && hljHttpResult.getStatus() != null ||
                                hljHttpResult.getStatus()
                                .getRetCode() == 0) {
                            detailAdapter.setSaleName(name);
                            detailAdapter.notifyItemChanged(0);
                        } else {
                            Toast.makeText(AdvDetailActivity.this, "修改失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .build();
        ExperienceShopApi.modifySalerName(id, name)
                .subscribe(modifySaleSubscriber);
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        if (!isChecked) {
            showArriveDialog();
        } else {
            Toast.makeText(this, "客户是已经到店", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showArriveDialog() {
        if (arriveDialog != null && arriveDialog.isShowing()) {
            return;
        }
        if (arriveDialog == null) {
            arriveDialog = DialogUtil.createDoubleButtonDialog(this,
                    "确认客户是否已到店,确认后将无法再次修改",
                    "确认",
                    "取消",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arriveDialog.dismiss();
                            arriveShop();

                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arriveDialog.dismiss();
                        }
                    });
        }
        arriveDialog.show();
    }

    private void arriveShop() {
        CommonUtil.unSubscribeSubs(arriveShopSubscriber);
        String message = "客户已到店";
        arriveShopSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult result) {
                        if (result != null) {
                            if (result.getStatus() != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    refresh();
                                    Toast.makeText(AdvDetailActivity.this,
                                            "操作成功",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(AdvDetailActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                Toast.makeText(AdvDetailActivity.this,
                                        "操作失败",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(AdvDetailActivity.this, "操作失败", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                })
                .build();
        ExperienceShopApi.arriveShop(id, message)
                .subscribe(arriveShopSubscriber);
    }

    class ResultZip extends HljHttpResultZip {
        @HljRZField
        AdvDetail order;
        @HljRZField
        List<ShowHistory> showHistoryList;
    }

    @Override
    public void onBackPressed() {
        if (order != null) {
            Intent intent = getIntent();
            intent.putExtra(ARG_ITEM_STATUS, order.getStatus());
            intent.putExtra(ARG_ITEM_IS_COME, order.isCome());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
