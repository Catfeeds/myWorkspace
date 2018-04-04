package me.suncloud.marrymemo.ad;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljhttplibrary.utils.NetUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.suncloud.marrymemo.R;

/**
 * Mobile Premium Buying服务器广告
 * Created by wangtao on 2016/12/1.
 */

public class MadUtil {

    private static String webUserAgent;
    public static final String SPLASH_ID = "E0C5B0B3918B057A";
    public static final String HOME_ID = "85803FD4E5DAF3DD";

    public static String getSplashAdUrl(Context context) {
        return getMadUrl(context, SPLASH_ID);
    }

    public static String getHomeAdUrl(Context context) {
        return getMadUrl(context, HOME_ID);
    }

    private static String getMadUrl(Context context, String adspaceid) {
        StringBuilder stringBuilder = new StringBuilder(String.format(
                "p/wedding/index.php/Home/APIUtils/mad_house?os=0&adspaceid=%s",
                adspaceid));
        stringBuilder.append("&pkgname=")
                .append(context.getPackageName());
        try {
            stringBuilder.append("&appname=")
                    .append(URLEncoder.encode(context.getString(R.string.app_name), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append("&conn=");
        switch (NetUtil.getNetType(context)) {
            case "wifi":
                stringBuilder.append(1);
                break;
            case "cell-3":
                stringBuilder.append(3);
                break;
            case "cell-4":
                stringBuilder.append(4);
                break;
            default:
                stringBuilder.append(2);
                break;
        }
        stringBuilder.append("&carrier=")
                .append(NetUtil.getOperator(context));
        stringBuilder.append("&osv=")
                .append(Build.VERSION.RELEASE);

        stringBuilder.append("&imei=");
        if (!TextUtils.isEmpty(DeviceUuidFactory.getIMEI(context))) {
            stringBuilder.append(DeviceUuidFactory.getIMEI(context));
        }
        stringBuilder.append("&aid=");
        if (!TextUtils.isEmpty(DeviceUuidFactory.getAndroidId(context))) {
            stringBuilder.append(DeviceUuidFactory.getAndroidId(context));
        }
        String ua = getWebUserAgent(context);
        stringBuilder.append("&ua=");
        if (!TextUtils.isEmpty(ua)) {
            try {
                stringBuilder.append(URLEncoder.encode(ua, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private static String getWebUserAgent(Context context) {
        if (TextUtils.isEmpty(webUserAgent)) {
            try {
                webUserAgent = new WebView(context).getSettings()
                        .getUserAgentString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return webUserAgent;
    }
}
