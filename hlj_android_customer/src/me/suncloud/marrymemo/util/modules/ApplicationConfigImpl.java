package me.suncloud.marrymemo.util.modules;

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
        return CommonUtil.PacketType.CUSTOMER;
    }

    @Override
    public String getAppName() {
        return "weddingUser";
    }

    @Override
    public void init(Context context) {

    }
}
