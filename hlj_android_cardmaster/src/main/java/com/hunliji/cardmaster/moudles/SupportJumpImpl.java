package com.hunliji.cardmaster.moudles;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.utils.CardMasterSupportUtil;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;

/**
 * Created by wangtao on 2017/11/29.
 */


@Route(path = RouterPath.ServicePath.GO_TO_SUPPORT)
public class SupportJumpImpl implements SupportJumpService {

    @Override
    public void gotoSupport(Context context, int kind) {
        CardMasterSupportUtil.goToSupport(context,kind);
    }

    @Override
    public void init(Context context) {

    }
}
