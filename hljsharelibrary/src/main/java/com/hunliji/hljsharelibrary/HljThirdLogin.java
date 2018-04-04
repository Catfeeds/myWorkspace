package com.hunliji.hljsharelibrary;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hunliji.hljsharelibrary.activities.ThirdLoginActivity;

import java.util.List;

/**
 * Created by wangtao on 2017/7/14.
 */

public class HljThirdLogin {

    public static class LoginType {
        public static final String QQ = "qq";
        public static final String WEIBO = "sina";
        public static final String WEIXIN = "weixin";
    }

    public static class InfoType {
        public static final String BIND = "bind";
        public static final String LOGIN = "login";
    }

    private Context context;
    private String loginType;

    public HljThirdLogin(Context context, String loginType) {
        this.context = context;
        this.loginType = loginType;
    }

    public static HljThirdLogin qqLogin(Context context) {
        return new HljThirdLogin(context,LoginType.QQ);
    }

    public static HljThirdLogin weiboLogin(Context context) {
        return new HljThirdLogin(context,LoginType.WEIBO);
    }

    public static HljThirdLogin weixinLogin(Context context) {
        return new HljThirdLogin(context,LoginType.WEIXIN);
    }

    public void login() {
        startLogin(InfoType.LOGIN);
    }

    public void bind() {
        startLogin(InfoType.BIND);
    }

    private void startLogin(String infoType){
        Intent intent = new Intent(context, ThirdLoginActivity.class);
        intent.putExtra(ThirdLoginActivity.KEY_LOGIN_TYPE, loginType);
        intent.putExtra(ThirdLoginActivity.KEY_INFO_TYPE, infoType);
        context.startActivity(intent);
        if(context instanceof Activity){
            ((Activity)context).overridePendingTransition(0,0);
        }
    }



    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
