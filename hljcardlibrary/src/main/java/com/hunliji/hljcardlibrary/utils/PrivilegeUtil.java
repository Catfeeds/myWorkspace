package com.hunliji.hljcardlibrary.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.w3c.dom.Text;

/**
 * Created by luohanlin on 2017/12/18.
 * 检测请帖VIP使用权限的帮助类
 */

public class PrivilegeUtil {
    public static final int VIP_PRIVILEGE_FLAG = 0X01;

    public static void isVipAvailable(
            Context context, HljHttpSubscriber checkSub, final PrivilegeCallback callback) {
        CommonUtil.unSubscribeSubs(checkSub);
        User user = UserSession.getInstance()
                .getUser(context);
        if(user==null){
            callback.checkDone(false);
        }
        checkSub = HljHttpSubscriber.buildSubscriber(context)
                .setOnNextListener(new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String s) {
                        boolean isAvailable = checkPrivilege(s);
                        if (callback != null) {
                            callback.checkDone(isAvailable);
                        }
                    }
                })
                .setDataNullable(true)
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        if (callback != null) {
                            callback.checkDone(false);
                        }
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(context))
                .build();
        CardApi.getPrivilegeFlag()
                .subscribe(checkSub);
    }


    /**
     * 检查是否可以使用VIP模板
     *
     * @param flag 服务器取回的标志位
     * @return
     */
    public static boolean checkPrivilege(String flag) {
        if (TextUtils.isEmpty(flag)) {
            return false;
        }
        int f = Integer.parseInt(flag);
        if ((VIP_PRIVILEGE_FLAG & f) == 0x1)
            return true;
        else
            return false;
    }

    public interface PrivilegeCallback {
        void checkDone(boolean isAvailable);
    }
}
