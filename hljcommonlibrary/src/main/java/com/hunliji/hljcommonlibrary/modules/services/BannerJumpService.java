package com.hunliji.hljcommonlibrary.modules.services;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.hunliji.hljcommonlibrary.models.Poster;

import org.json.JSONObject;

/**
 * Created by luohanlin on 2017/7/3.
 * Banner跳转服务的接口声明
 */

public interface BannerJumpService extends IProvider {

    /**
     * Banner跳转，不同的service实现对应不同的App中banner跳转逻辑
     *
     * @param poster    poster数据
     * @param trackData 跟踪数据
     */
    void bannerJump(Context context,Poster poster, JSONObject trackData);
}
