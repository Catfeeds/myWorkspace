package me.suncloud.marrymemo.util.modules;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;

import me.suncloud.marrymemo.util.CustomerSupportUtil;

/**
 * Created by wangtao on 2017/10/27.
 */

@Route(path = RouterPath.ServicePath.GO_TO_SUPPORT)
public class SupportJumpImpl implements SupportJumpService {

    @Override
    public void gotoSupport(Context context, int kind) {
        CustomerSupportUtil.goToSupport(context,kind);
    }

    @Override
    public void init(Context context) {

    }
}
