package com.example.suncloud.hljweblibrary.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Suncloud on 2016/9/14.
 */
public class WebUtil {

    public static String addPathQuery(Context context, String path) {
        if (Uri.parse(path)
                .getScheme() == null) {
            path = "http://" + path;
        }
        if (isHljUrl(path)) {
            City city = LocationSession.getInstance()
                    .getCity(context);
            if (Uri.parse(path)
                    .getQueryParameter("city") == null) {
                path += (path.contains("?") ? "&" : "?") + "city=" + city.getCid();

            }
            User user = UserSession.getInstance()
                    .getUser(context);
            if (user != null && user.getId() > 0) {
                path += (path.contains("?") ? "&" : "?") + "user_id=" + user.getId();
            }
            try {
                String appVersion = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                path += (path.contains("?") ? "&" : "?") + "appver=" + appVersion;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static Map<String, String> getWebHeaders(Context context) {
        Map<String, String> header = new HashMap<>();
        for (Map.Entry<String, String> entry : new HljHttpHeader(context).getHeaderMap()
                .entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            header.put(key, value);
        }
        return header;
    }


    public static boolean isHljUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String host = Uri.parse(url)
                .getHost();
        return !TextUtils.isEmpty(host) && (host.contains("hunliji") || HljHttp.getHOST()
                .contains(host));
    }

    public static boolean isMadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String host = Uri.parse(url)
                .getHost();
        return !TextUtils.isEmpty(host) && host.contains("ccbwx.onemad.com");
    }

}
