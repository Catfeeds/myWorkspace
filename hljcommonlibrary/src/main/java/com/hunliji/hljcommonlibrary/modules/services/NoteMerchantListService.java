package com.hunliji.hljcommonlibrary.modules.services;

import android.app.Activity;
import android.view.View;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by jinxin on 2017/7/13 0013.
 */

public interface NoteMerchantListService extends IProvider {

    /**
     * 实名认证的提示框
     *
     * @param activity
     * @return
     */
    boolean isShowShopReview(Activity activity);

    /**
     * 跳转笔记web宣传页面
     *
     * @param activity
     */
    void onNoteAdsWebView(Activity activity, View progressBar);

}
