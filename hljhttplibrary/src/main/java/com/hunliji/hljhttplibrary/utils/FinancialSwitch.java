package com.hunliji.hljhttplibrary.utils;

import android.content.Context;

import com.hunliji.hljcommonlibrary.BuildConfig;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by wangtao on 2018/4/2.
 */

public enum FinancialSwitch {

    INSTANCE;

    public static final long TEST_USER_ID = 346815;
    List<String> closedChannels;

    public void setClosedChannels(List<String> closedChannels) {
        this.closedChannels = closedChannels;
    }


    public static boolean isOverTime() {
        try {
            DateTime overTime = new DateTime(BuildConfig.BUILD_TIME_STAMP).plusDays(5);
            return overTime.isBefore(System.currentTimeMillis());
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isClosed(Context context) {
        try {
            CustomerUser user = (CustomerUser) UserSession.getInstance()
                    .getUser(context);
            if (user != null && user.getId() == FinancialSwitch.TEST_USER_ID) {
                //测试用户全部关闭
                return true;
            }
            if (isOverTime()) {
                //超过审核时间全部开启
                return false;
            }
            if (CommonUtil.isCollectionEmpty(closedChannels)) {
                //未获取配置全部开启
                return false;
            }
            String channel = ChannelUtil.getChannelFromApk(context);
            return closedChannels.contains(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

