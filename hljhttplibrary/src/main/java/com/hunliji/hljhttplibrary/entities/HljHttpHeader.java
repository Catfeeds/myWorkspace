package com.hunliji.hljhttplibrary.entities;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.ApplicationConfigService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by werther on 16/7/23.
 * 婚礼纪客户端使用的授权管理信息model
 */
public class HljHttpHeader extends HljHttpHeaderBase {
    private Context context;
    private String appVersion;
    private int width;
    private int height;

    public HljHttpHeader(Context mContext) {
        this.context = mContext.getApplicationContext();
        this.appVersion = CommonUtil.getAppVersion(mContext);
        this.width=CommonUtil.getDeviceSize(mContext).x;
        this.height=CommonUtil.getDeviceSize(mContext).y;
    }

    private String getAppName() {
        try {
            ApplicationConfigService applicationConfig = (ApplicationConfigService) ARouter.getInstance()
                    .build(RouterPath.ServicePath.APPLICATION_CONFIG)
                    .navigation();
            return applicationConfig.getAppName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取授权信息key value的map数据,用于设置http header的授权信息
     *
     * @return
     * @override
     */
    public Map<String, String> getHeaderMap() {
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        if (!headerMap.containsKey("appver")) {
            headerMap.put("appver", appVersion);
        }
        if (!headerMap.containsKey("devicekind")) {
            headerMap.put("devicekind", "android");
        }
        if (!headerMap.containsKey("appName")) {
            headerMap.put("appName", getAppName());
        }
        if (!headerMap.containsKey("test")) {
            headerMap.put("test", HljCommon.debug ? "1" : "0");
        }
        if (!headerMap.containsKey("phone")) {
            headerMap.put("phone",
                    DeviceUuidFactory.getInstance()
                            .getDeviceUuidString(context));
        }
        User user = UserSession.getInstance()
                .getUser(context);
        if (user != null) {
            if (user.getAccessToken() != null) {
                headerMap.put("Http-Access-Token", user.getAccessToken());
            }
            if (user.getToken() != null) {
                headerMap.put("token", user.getToken());
            }
            if (user.getToken() != null) {
                headerMap.put("secret", CommonUtil.getMD5(user.getToken() + HljCommon.LOGIN_SEED));
            }
        } else {
            headerMap.remove("Http-Access-Token");
            headerMap.remove("token");
            headerMap.remove("secret");
        }

        //        城市会变化实时获取
        String cityStr = LocationSession.getInstance()
                .getLocalString(context);
        headerMap.put("city", cityStr);

        City city = LocationSession.getInstance()
                .getCity(context);
        headerMap.put("cid", String.valueOf(city.getCid()));

        headerMap.put("screen_width", String.valueOf(width));
        headerMap.put("screen_height", String.valueOf(height));
        return headerMap;
    }

}
