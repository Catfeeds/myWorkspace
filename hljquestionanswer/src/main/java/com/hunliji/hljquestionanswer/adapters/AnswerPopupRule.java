package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.GoMerchantServiceWebViewService;
import com.hunliji.hljcommonlibrary.modules.services.NoteMerchantListService;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljquestionanswer.R;

/**
 * Created by hua_rong on 2017/6/8.
 * 问题--弹窗
 */

public class AnswerPopupRule {

    private static volatile AnswerPopupRule instance;

    private AnswerPopupRule() {}

    public static AnswerPopupRule getDefault() {
        if (instance == null) {
            synchronized (AnswerPopupRule.class) {
                if (instance == null) {
                    instance = new AnswerPopupRule();
                }
            }
        }
        return instance;
    }

    /**
     * 未开通店铺专业版提示
     */
    public void showProDialog(final Activity activity) {
        DialogUtil.createDoubleButtonDialog(activity,
                activity.getString(R.string.hint_merchant_pro__qa),
                activity.getString(R.string.label_learn_about_detail___qa),
                activity.getString(R.string.label_close),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 使用Name索引寻找ARouter中已注册的对应服务
                        GoMerchantServiceWebViewService goMerchantServiceWebViewService =
                                (GoMerchantServiceWebViewService) ARouter.getInstance()
                                .build(RouterPath.ServicePath.GO_MERCHANT_SER)
                                .navigation();
                        if (goMerchantServiceWebViewService != null) {
                            goMerchantServiceWebViewService.goMerchantServiceAdsWebActivity
                                    (activity);
                        }
                    }
                },
                null)
                .show();
    }

    /**
     * 显示未实名认证或未完成店铺审核提示
     */
    public void showHintDialog(Activity activity) {
        NoteMerchantListService service = (NoteMerchantListService) ARouter.getInstance()
                .build(RouterPath.ServicePath.GO_NOTE_ADS_WEB_VIEW)
                .navigation();
        if (service == null) {
            return;
        }
        service.isShowShopReview(activity);
    }

}
