package me.suncloud.marrymemo.ad;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljhttplibrary.utils.NetUtil;

/**
 * Created by wangtao on 2018/3/26.
 */

public class MiaoZhenUtil {

    private static final String IMP_URL = "http://g.cn.miaozhen.com/x/k=2075581&p=%s";
    private static final String CLICK_URL = "http://e.cn.miaozhen.com/r/k=2075581&p=%s";
    private static String PARAMETERS;

    public class PId {
        public static final String SPLASH_POSTER = "7DqRo";
        public static final String HOME_PAGE_POSTER = "7DqRp";
        public static final String PRODUCT_HOME_POSTER = "7DqRr";
        public static final String SHARE_POSTER = "7DqRt";
    }


    public static String getImpUrl(Context context, String pId) {
        if(TextUtils.isEmpty(pId)){
            return null;
        }
        return String.format(IMP_URL, pId)+getParameters(context);
    }

    public static String getClickUrl(Context context, String pId) {
        if(TextUtils.isEmpty(pId)){
            return null;
        }
        return String.format(CLICK_URL, pId)+getParameters(context);
    }

    private static String getParameters(Context context) {
        if(!TextUtils.isEmpty(PARAMETERS)){
            return PARAMETERS;
        }
        String parameters = "&dx=__IPDX__&rt=2&ns=__IP__&ni=__IESID__&v=__LOC__&xa=__ADPLATFORM__" +
                "" + "" + "&tr" + "=__REQUESTID__&mo=__0__&m0=__OPENUDID__&m0a=__DUID__&m1" +
                "=__ANDROIDID1__&m1a" + "=__ANDROIDID__&m2" +
                "=__IMEI__&m4=__AAID__&m5=__IDFA__&m6=__MAC1__&m6a=__MAC__&o=";
        String androidId = DeviceUuidFactory.getAndroidId(context);
        if (!TextUtils.isEmpty(androidId)) {
            parameters = parameters.replace("ANDROIDID1", androidId);
            String md5 = CommonUtil.getMD5(androidId);
            if (!TextUtils.isEmpty(md5)) {
                parameters = parameters.replace("ANDROIDID", md5);
            }
        }
        String imei = DeviceUuidFactory.getIMEI(context);
        if (!TextUtils.isEmpty(imei)) {
            String md5 = CommonUtil.getMD5(imei);
            if (!TextUtils.isEmpty(md5)) {
                parameters = parameters.replace("IMEI", md5);
            }
        }
        String mac = NetUtil.getMacAddr(context);
        if (!TextUtils.isEmpty(mac)) {
            mac=mac.toUpperCase();
            String md5 = CommonUtil.getMD5(mac);
            if (!TextUtils.isEmpty(md5)) {
                parameters = parameters.replace("MAC1", md5);
            }
            String formatMd5 = CommonUtil.getMD5(mac.replace(":", ""));
            if (!TextUtils.isEmpty(formatMd5)) {
                parameters = parameters.replace("MAC", formatMd5);
            }
        }
        PARAMETERS=parameters;
        return parameters;
    }

}
