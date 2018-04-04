package com.hunliji.hljhttplibrary.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljhttplibrary.R;
import com.hunliji.hljhttplibrary.authorization.UserSession;

/**
 * Created by werther on 16/8/9.
 * 授权校验帮助类
 */
public class AuthUtil {

    /**
     * 检测用户是否登录并且绑定手机号码,如果没有登录则跳转登录页面,如果没有绑定手机则跳转绑定页面
     *
     * @param context
     * @return true:登录并且已绑定 false: 没有登录或者没有绑定
     */
    public static boolean loginBindCheck(Context context) {
        User user = UserSession.getInstance()
                .getUser(context);
        if (user != null && user.getId() > 0) {
            return true;
        } else {
            // 跳转登录页
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.LoginActivityPath.LOGIN)
                    .withInt("type", HljCommon.Login.LOGINCHECK)
                    .withTransition(R.anim.slide_in_up, R.anim.activity_anim_default)
                    .navigation(context);
            return false;
        }
    }


}
