package com.hunliji.hljcommonlibrary.modules.services;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by luohanlin on 2017/7/3.
 * Banner跳转服务的接口声明
 */

public interface MapLibraryService extends IProvider {

    String getAMapUrl(
            double longitude,
            double latitude,
            int width,
            int height,
            int zoom,
            String markerIconPath);
}
