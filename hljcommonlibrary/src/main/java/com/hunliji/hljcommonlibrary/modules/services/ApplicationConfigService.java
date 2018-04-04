package com.hunliji.hljcommonlibrary.modules.services;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by wangtao on 2018/3/29.
 */

public interface ApplicationConfigService extends IProvider {

    int getAppType();

    String getAppName();
}
