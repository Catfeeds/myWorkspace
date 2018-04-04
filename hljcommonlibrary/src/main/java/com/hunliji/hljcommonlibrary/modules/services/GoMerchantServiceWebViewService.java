package com.hunliji.hljcommonlibrary.modules.services;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by luohanlin on 2017/7/3.
 * 跳转商家店铺服务Web宣传页面
 */

public interface GoMerchantServiceWebViewService extends IProvider {

    /**
     * 跳转商家店铺服务Web宣传页面
     */
    void goMerchantServiceAdsWebActivity(Activity activity);
}
