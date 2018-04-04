package com.hunliji.marrybiz.util.popuptip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.view.login.OpenShopScheduleActivity;
import com.hunliji.marrybiz.view.merchantservice.BondPlanDetailActivity;
import com.hunliji.marrybiz.view.merchantservice.MerchantUltimateDetailActivity;

/**
 * Created by hua_rong on 2017/6/5.
 * 商家弹窗提示
 */

public class PopupRule {

    private static volatile PopupRule instance;

    private PopupRule() {}

    public static PopupRule getDefault() {
        if (instance == null) {
            synchronized (PopupRule.class) {
                if (instance == null) {
                    instance = new PopupRule();
                }
            }
        }
        return instance;
    }

    /**
     * 店铺审核--->实名认证--->商家服务--->等级要求
     */
    public boolean showShopReview(final Activity activity, final MerchantUser user) {
        if (user == null) {
            return true;
        }
        if (user.isOpenedTrade()){
            return false;
        }
        if (user.getKind() == 1){
            return false;
        }
        if (user.getExamine() != 1 || user.getCertifyStatus() != 3) {
            DialogUtil.createDoubleButtonDialog(activity,
                    activity.getString(R.string.hint_not_complete_real_name_certification),
                    "立即完成",
                    "取消",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity,
                                    OpenShopScheduleActivity.class));
                        }
                    },
                    null)
                    .show();
            return true;
        }
        return false;
    }

    /**
     * 跳转到商家店铺服务web宣传页
     */
    public void goMerchantProAdsWebActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MerchantUltimateDetailActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 你无法使用此功能，请前往网页端商家后台查看
     */
    public void showSpecialDisableDlg(Context context) {
        DialogUtil.createSingleButtonDialog(context,
                context.getString(R.string.msg_special_merchant_disable),
                context.getString(R.string.label_confirm_text),
                null)
                .show();
    }

    /**
     * 保证金
     */
    public void onBondPlanLayout(Activity activity, MerchantUser user, ProgressBar progressBar) {
        if (user == null) {
            return;
        }
        if (activity == null) {
            return;
        }
        if (user.isBondSign() || user.isBondPaid()) {
            Intent intent = new Intent(activity, BondPlanDetailActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (showShopReview(activity, user)) {
            // 只有通过上面两种审核,才能加入保证金计划
            if (Constants.DEBUG) {
                Log.d("onBondPlanLayout", "只有通过上面两种审核,才能加入保证金计划");
            }
        } else {
            Intent intent = new Intent(activity, BondPlanDetailActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * 商家专业版提示
     */
    public void showProDialog(final Activity activity, final View progressBar) {
        if (activity == null) {
            return;
        }
        DialogUtil.createDoubleButtonDialog(activity,
                activity.getString(R.string.hint_merchant_pro__qa),
                activity.getString(R.string.label_learn_about_detail___qa),
                activity.getString(R.string.label_close),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goMerchantProAdsWebActivity(activity);
                    }
                },
                null)
                .show();

    }

    /**
     * 显示我知道了单button提示
     */
    private void showHintDialog(Activity activity, String hint) {
        if (activity == null) {
            return;
        }
        DialogUtil.createSingleButtonDialog(activity,
                hint,
                activity.getString(R.string.label_confirm_text),
                null)
                .show();

    }

    public void showLevelDialog(final Activity activity, int level) {
        String levelStr = null;
        switch (level) {
            case 1:
                levelStr = "铜牌";
                break;
            case 2:
                levelStr = "银牌";
                break;
            default:
                break;
        }
        DialogUtil.createDoubleButtonDialog(activity,
                activity.getString(R.string.label_merchant_level_too_low, levelStr),
                "好的",
                activity.getString(R.string.label_close),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                        Intent intent = new Intent(activity,
                        // MyLevelActivity.class);
                        //                        activity.startActivity(intent);
                        //                        activity.overridePendingTransition(R.anim
                        // .slide_in_right,
                        //                                R.anim.activity_anim_default);
                    }
                },
                null)
                .show();
    }

    /**
     * @param hint 弹窗提示内容
     * @param type 弹窗类型
     *             0：非店铺专业版
     *             1：等级不足
     *             2：未开通保证金
     *             3：保证余额不足
     *             2-3合并为 了解保证金 2017-06-06
     */
    public void showBondDialog(final Activity activity, String hint, final int type) {
        String confirmStr = "";
        switch (type) {
            case 0:
                confirmStr = activity.getString(R.string.label_learn_about_detail___qa);
                break;
            case 1:
                confirmStr = "好的";
                break;
            case 2:
            case 3:
                confirmStr = activity.getString(R.string.label_learn_about_bond);
                break;
            default:
                break;
        }
        DialogUtil.createDoubleButtonDialog(activity,
                hint,
                confirmStr,
                activity.getString(R.string.label_close),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        switch (type) {
                            case 0:
                                goMerchantProAdsWebActivity(activity);
                                break;
                            //                            case 1:
                            //                                intent.setClass(activity,
                            // MyLevelActivity.class);
                            //                                activity.startActivity(intent);
                            //                                activity.overridePendingTransition
                            // (R.anim.slide_in_right,
                            //                                        R.anim.activity_anim_default);
                            //                                break;
                            case 2:
                            case 3:
                                intent.setClass(activity, BondPlanDetailActivity.class);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                break;
                        }
                    }
                },
                null)
                .show();
    }


}
