package com.hunliji.marrybiz.util.modules;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.marrybiz.util.BannerUtil;

import org.json.JSONObject;


/**
 * Created by luohanlin on 2017/7/3.
 * ARouter模块间调用service的实现类
 * 实现了针对商家端的banner跳转
 */
@Route(path = RouterPath.ServicePath.BANNER_JUMP)
public class BannerJumpImpl implements BannerJumpService {

    @Override
    public void bannerJump(
            Context context, Poster poster, JSONObject trackData) {
        BannerUtil.bannerJump(context, poster, trackData);
    }

    @Override
    public void init(Context context) {
    }
}
