package com.hunliji.marrybiz.view.shop;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.MerchantNoticeActivity;
import com.hunliji.marrybiz.view.MyLevelActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by hua_rong on 2017/5/19.
 * 店铺管理
 */

public class ShopManagerActivity extends HljBaseActivity {

    @BindView(R.id.ll_shop_info)
    LinearLayout llShopInfo;
    @BindView(R.id.ll_shop_theme)
    LinearLayout llShopTheme;
    @BindView(R.id.tv_shop_service)
    TextView tvShopService;
    @BindView(R.id.rl_shop_service)
    RelativeLayout rlShopService;
    @BindView(R.id.tv_money_plan)
    TextView tvMoneyPlan;
    @BindView(R.id.rl_money_plan)
    RelativeLayout rlMoneyPlan;
    @BindView(R.id.tv_merchant_level)
    TextView tvMerchantLevel;
    @BindView(R.id.rl_merchant_level)
    RelativeLayout rlMerchantLevel;
    @BindView(R.id.rl_promote_shop)
    RelativeLayout rlPromoteShop;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_service_state)
    TextView tvServiceState;
    private MerchantUser user;
    private Context context;
    private Subscription rxBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_manager);
        ButterKnife.bind(this);
        context = this;
        setOkText(R.string.label_preview_merchant);
        //        setOkTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        initValue();
        registerRxBus();
    }

    private void initValue() {
        user = Session.getInstance()
                .getCurrentUser(this);
        //0.普通 1.铜牌 2.银牌 3.金牌 4.钻石
        Resources resources = this.getResources();
        if (resources != null) {
            String[] levels = resources.getStringArray(R.array.merchant_level);
            tvMerchantLevel.setText(levels[user.getGradeLevel()]);
            if (user.isBondPaid()) {
                tvMoneyPlan.setText(R.string.label_bond_signed);
            } else {
                tvMoneyPlan.setText(R.string.label_no_bond_signed2);
            }
            setShopService();
        }
    }

    private void registerRxBus() {
        if (rxBus == null || rxBus.isUnsubscribed()) {
            rxBus = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case PAY_SUCCESS:
                                    syncUserInfo();
                                    break;
                            }
                        }
                    });
        }
    }

    private void syncUserInfo() {
        MerchantUserSyncUtil.getInstance()
                .sync(this, new MerchantUserSyncUtil.OnMerchantUserSyncListener() {
                    @Override
                    public void onUserSyncFinish(MerchantUser user) {
                        ShopManagerActivity.this.user = user;
                        setShopService();
                    }
                });
    }

    private void setShopService() {
        //已开通店铺xx版
        if (user.isPro() && user.getProDate() != null) {
            int day = HljTimeUtils.getSurplusDay(user.getProDate());
            tvShopService.setText(String.format("已开通店铺%s版", user.getIsPro() == 1 ? "专业" : "旗舰"));
            tvServiceState.setText(getString(R.string.label_pro_expire, day));
        } else {
            tvServiceState.setText(R.string.label_is_not_pro);
            tvShopService.setText(R.string.label_shop_service);
        }
    }

    @Override
    public void onOkButtonClick() {
        //0 服务商家，1 婚品商家 2.婚车
        if (user.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
            Intent intent = new Intent(context, ShopWebViewActivity.class);
            intent.putExtra("title", getString(R.string.label_preview_merchant));
            intent.putExtra("type", 3);
            intent.putExtra("path",
                    String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                            user.getMerchantId(),
                            1));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick({R.id.ll_shop_info, R.id.ll_shop_theme, R.id.ll_shop_notice, R.id.rl_shop_service, R
            .id.rl_money_plan, R.id.rl_merchant_level, R.id.rl_promote_shop})
    public void onClick(View view) {
        Intent intent;
        PopupRule popupRule = PopupRule.getDefault();
        switch (view.getId()) {
            case R.id.ll_shop_info:
                intent = new Intent(this, EditShopActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_shop_theme:
                intent = new Intent(this, ShopThemeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_shop_notice:
                if (popupRule.showShopReview(ShopManagerActivity.this, user)) {
                    return;
                }
                //店铺公告
                if (user.isSpecialChildMerchant()) {
                    popupRule.showSpecialDisableDlg(ShopManagerActivity.this);
                } else {
                    if (user.getGradeLevel() < 1) {
                        //铜牌商家
                        popupRule.showLevelDialog(ShopManagerActivity.this, 1);
                        return;
                    }
                    intent = new Intent(this, MerchantNoticeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rl_shop_service:
                if (popupRule.showShopReview(ShopManagerActivity.this, user)) {
                    return;
                }
                popupRule.goMerchantProAdsWebActivity(ShopManagerActivity.this);
                break;
            case R.id.rl_money_plan:
                popupRule.onBondPlanLayout(ShopManagerActivity.this, user, progressBar);
                break;
            case R.id.rl_merchant_level:
                intent = new Intent(this, MyLevelActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_promote_shop:
                onShare();
                break;
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    private void onShare() {
        if (PopupRule.getDefault()
                .showShopReview(ShopManagerActivity.this, user)) {
            return;
        }
        if (user.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(context,
                    new ShareInfo(user.getShareInfo()
                            .getTitle(),
                            user.getShareInfo()
                                    .getDesc(),
                            user.getShareInfo()
                                    .getDesc2(),
                            user.getShareInfo()
                                    .getUrl(),
                            user.getShareInfo()
                                    .getIcon()));
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBus);
    }
}
