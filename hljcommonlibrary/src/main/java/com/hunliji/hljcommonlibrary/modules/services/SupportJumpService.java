package com.hunliji.hljcommonlibrary.modules.services;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.hunliji.hljcommonlibrary.models.Poster;

import org.json.JSONObject;

/**
 * Created by luohanlin on 2017/7/3.
 * Banner跳转服务的接口声明
 */

public interface SupportJumpService extends IProvider {

    void gotoSupport(Context context, int kind);
}
