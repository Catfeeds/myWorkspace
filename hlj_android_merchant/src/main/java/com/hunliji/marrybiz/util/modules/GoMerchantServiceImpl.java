package com.hunliji.marrybiz.util.modules;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.GoMerchantServiceWebViewService;
import com.hunliji.marrybiz.util.popuptip.PopupRule;

/**
 * Created by luohanlin on 2017/7/3.
 * 跳转店铺服务页面的实现
 */
@Route(path = RouterPath.ServicePath.GO_MERCHANT_SER)
public class GoMerchantServiceImpl implements GoMerchantServiceWebViewService {

    @Override
    public void goMerchantServiceAdsWebActivity(Activity activity) {
        PopupRule.getDefault()
                .goMerchantProAdsWebActivity(activity);
    }

    @Override
    public void init(Context context) {

    }
}
