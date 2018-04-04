package com.hunliji.hljcommonlibrary.modules.services;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.hunliji.hljcommonlibrary.models.City;

/**
 * 婚车路由service
 * Created by jinxin on 2017/12/29 0029.
 */

public interface WeddingCarRouteService extends IProvider {

    void onNoticeInit(Context mContext, TextView tvCount, View msgView);

    void onNoticeResume();

    void onNoticePause();

    String getMemberRemind(Context mContext);

    void gotoWeddingCarActivity(Context context, String cityName,long cityCOde);
}
