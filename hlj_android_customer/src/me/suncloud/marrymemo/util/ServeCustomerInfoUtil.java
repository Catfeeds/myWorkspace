package me.suncloud.marrymemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.util.Date;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;

/**
 * Created by werther on 16/10/10.
 * 本地服务订单服务客户信息的帮助类,存储和读取
 * 客户信息包括联系姓名,电话,时间(婚期)和服务地址(婚车才用到)等
 * 用户输入一次后会存储在系统存储中,下会自动填写,但不存储在服务器中
 */

public class ServeCustomerInfoUtil {

    /**
     * 获取存储中的客户信息,如果没有,取用户信息作为客户信息
     *
     * @param context
     * @return
     */
    public static ServeCustomerInfo readServeCustomerInfo(Context context) {
        ServeCustomerInfo info = new ServeCustomerInfo();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);

        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            return null;
        }
        String userId = String.valueOf(user.getId());
        long timeStamp = 0;
        try {
            timeStamp = sharedPreferences.getLong(Constants.PREF_LAST_SERVE_TIME + userId, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serveAddr = sharedPreferences.getString(Constants.PREF_LAST_SERVE_ADDR + userId, "");
        String customerName = sharedPreferences.getString(Constants.PREF_LAST_SERVE_CUSTORMER +
                userId,
                "");
        String customerPhone = sharedPreferences.getString(Constants.PREF_LAST_SERVE_PHONE + userId,
                "");

        if (JSONUtil.isEmpty(customerPhone)) {
            if (user.getId() != 0) {
                customerPhone = user.getPhone();
            }
        }
        if (JSONUtil.isEmpty(customerName)) {
            if (user.getId() != 0) {
                customerName = user.getRealName();
            }
        }

        info.setCustomerName(customerName);
        info.setCustomerPhone(customerPhone);
        info.setServeAddr(serveAddr);
        if (timeStamp > 0) {
            info.setServeTime(timeStamp);
        }

        return info;
    }

    /**
     * 存储客户信息
     *
     * @param context
     * @param info
     */
    public static void saveServeCustomerInfo(Context context, ServeCustomerInfo info) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        User user = Session.getInstance()
                .getCurrentUser(context);
        if (user == null || user.getId() == 0) {
            return;
        }
        String userId = String.valueOf(user.getId());

        if (info.getServeTime() != null) {
            sharedPreferences.edit()
                    .putLong(Constants.PREF_LAST_SERVE_TIME + userId,
                            info.getServeTime()
                                    .getMillis())
                    .apply();
        }
        sharedPreferences.edit()
                .putString(Constants.PREF_LAST_SERVE_CUSTORMER + userId, info.getCustomerName())
                .apply();
        sharedPreferences.edit()
                .putString(Constants.PREF_LAST_SERVE_PHONE + userId, info.getCustomerPhone())
                .apply();
        sharedPreferences.edit()
                .putString(Constants.PREF_LAST_SERVE_ADDR + userId, info.getServeAddr())
                .apply();
    }
}
