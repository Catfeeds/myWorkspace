package com.hunliji.marrybiz.view.merchantservice;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchantserver.MerchantServerApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchantservice.BondInfo;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.MerchantServerUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.BondBalanceListActivity;
import com.hunliji.marrybiz.view.BondPayActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2018/1/31.保证金计划
 */

public class BondPlanDetailActivity extends HljBaseActivity {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.img_bond_logo)
    RoundedImageView imgBondLogo;
    @BindView(R.id.tv_bond_title)
    TextView tvBondTitle;
    @BindView(R.id.tv_bond_content)
    TextView tvBondContent;
    @BindView(R.id.img_already_open)
    ImageView imgAlreadyOpen;
    @BindView(R.id.tv_bond_balance)
    TextView tvBondBalance;
    @BindView(R.id.bond_balance_layout)
    LinearLayout bondBalanceLayout;
    @BindView(R.id.already_open_layout)
    RelativeLayout alreadyOpenLayout;
    @BindView(R.id.action_pay_bond)
    TextView actionPayBond;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.img_bond_plan_education)
    ImageView imgBondPlanEducation;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.top_space_view)
    View topSpaceView;
    @BindView(R.id.tv_bond_price_tip)
    TextView tvBondPriceTip;
    @BindView(R.id.tv_bond_price)
    TextView tvBondPrice;
    @BindView(R.id.pay_bond_layout)
    LinearLayout payBondLayout;
    private int width;
    private BondInfo bondInfo;
    private MerchantUser user;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_plan_detail);
        ButterKnife.bind(this);
        setOkText(R.string.label_bond_fee_detail);
        MarketItem marketItem = MerchantServerUtil.getInstance()
                .getMarketItem(this, BdProduct.BAO_ZHENG_JIN);
        if (marketItem != null) {
            width = CommonUtil.getDeviceSize(this).x;
            tvBondTitle.setText(marketItem.getTitle());
            tvBondContent.setText(marketItem.getSubTitle2());
            Glide.with(this)
                    .load(ImagePath.buildPath(marketItem.getImagePath())
                            .width(width)
                            .path())
                    .apply(new RequestOptions().fitCenter())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(
                                @Nullable GlideException e,
                                Object model,
                                Target<Drawable> target,
                                boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(
                                Drawable resource,
                                Object model,
                                Target<Drawable> target,
                                DataSource dataSource,
                                boolean isFirstResource) {
                            imgBondPlanEducation.getLayoutParams().height = Math.round(resource
                                    .getIntrinsicHeight() * width / resource.getIntrinsicWidth());
                            return false;
                        }
                    })
                    .into(imgBondPlanEducation);
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        Intent intent = new Intent(this, BondBalanceListActivity.class);
        startActivity(intent);
    }

    private void initLoad() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<BondInfo>() {
                        @Override
                        public void onNext(BondInfo bondInfo) {
                            setBondInfo(bondInfo);
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            MerchantServerApi.getBondInfoObb()
                    .subscribe(initSubscriber);
        }
    }

    private void setBondInfo(BondInfo bondInfo) {
        this.bondInfo = bondInfo;
        if (bondInfo != null && user != null) {
            double balance = bondInfo.getBondFee();
            String amount = CommonUtil.formatDouble2String(user.getBondFee() - balance);
            tvBondBalance.setText(CommonUtil.formatDouble2String(bondInfo.getBondFee()));
            tvBondPriceTip.setVisibility(View.VISIBLE);
            tvBondPrice.setVisibility(View.VISIBLE);
            if (user.isBondPaid() && user.isBondSign() && bondInfo.isBondEnough()) {
                topSpaceView.setVisibility(View.VISIBLE);
                alertLayout.setVisibility(View.GONE);
                alreadyOpenLayout.setVisibility(View.VISIBLE);
                payBondLayout.setVisibility(View.GONE);
            } else if (user.isBondPaid() && !user.isBondSign()) {
                // 余额不足超过限定期限,保证金权限过期
                topSpaceView.setVisibility(View.GONE);
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(getString(R.string.msg_bond_fee_short_expire,
                        user.getBondMerchantExpireDays()));
                alreadyOpenLayout.setVisibility(View.VISIBLE);
                payBondLayout.setVisibility(View.VISIBLE);
                tvBondPrice.setText(amount);
                actionPayBond.setText("立即补缴");
            } else if (user.isBondPaid() && !bondInfo.isBondEnough()) {
                // 余额不足超过限定期限,保证金权限过期
                topSpaceView.setVisibility(View.GONE);
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText("保证金不足，请尽快补缴");
                alreadyOpenLayout.setVisibility(View.VISIBLE);
                payBondLayout.setVisibility(View.VISIBLE);
                tvBondPrice.setText(amount);
                actionPayBond.setText("立即补缴");
            } else {
                topSpaceView.setVisibility(View.VISIBLE);
                alertLayout.setVisibility(View.GONE);
                payBondLayout.setVisibility(View.VISIBLE);
                tvBondPrice.setText(amount);
                actionPayBond.setText("立即充值");
                alreadyOpenLayout.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.pay_bond_layout)
    public void onPay() {
        if (bondInfo == null || user == null) {
            return;
        }
        if (!user.isPro()) {
            // 非专业版商家提示
            DialogUtil.createDoubleButtonDialog(this,
                    "购买旗舰版后才可加入保证金计划",
                    "商家旗舰版是婚礼纪平台为商家打造的集营销、推广、获客、门店管理于一身的高端运营产品",
                    "了解旗舰版",
                    "购买旗舰版",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(BondPlanDetailActivity.this,
                                    MerchantUltimateDetailActivity.class);
                            startActivity(intent);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(BondPlanDetailActivity.this,
                                    MerchantUltimateDetailActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        } else if (!user.isBondPaid()) {
            // 用户没有加入保证金计划,跳转到消费者保障计划页面
            Intent intent = new Intent(this, HljWebViewActivity.class);
            intent.putExtra("path", Constants.getAbsUrl(Constants.HttpPath.BOND_PAY_WEB));
            intent.putExtra("title", getString(R.string.label_bond_plan));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (user.isBondPaid() && user.isBondSign() && bondInfo.isBondEnough()) {
            // 保证金充足无需缴纳
            Toast.makeText(this, R.string.msg_bond_enough, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, BondPayActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = Session.getInstance()
                .getCurrentUser(this);
        initLoad();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
