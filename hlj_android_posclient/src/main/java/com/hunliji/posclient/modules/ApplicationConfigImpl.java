package com.hunliji.posclient.modules;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.ApplicationConfigService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by wangtao on 2018/3/29.
 */
@Route(path = RouterPath.ServicePath.APPLICATION_CONFIG)
public class ApplicationConfigImpl implements ApplicationConfigService{

    @Override
    public int getAppType() {
        return CommonUtil.PacketType.POS_CLIENT;
    }

    @Override
    public String getAppName() {
        return "posClient";
    }

    @Override
    public void init(Context context) {

    }
}
