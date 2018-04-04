package com.hunliji.marrybiz.modulehelper;

import android.content.Context;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSProduct;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.marrybiz.model.CustomSetmeal;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Work;

import java.io.IOException;
import java.io.Serializable;

import rx.Subscription;

/**
 * Created by werther on 16/8/8.
 * 主工程内使用子模块的一些工具方法
 */
public class ModuleUtils {

    private static Subscription migrateSubscription;

    /**
     * 将用户信息传入HljHttp模块中,所有子模块中使用的用户信息都有HljHttp模块中获取
     * 这个方法也是子模块中唯一的用户信息来源,也是主模块传入用户信息的唯一接口
     * 所以每一次用户信息改变,都要进行子模块用户信息设置
     *
     * @param context
     * @param user
     */
    public static void setUserToModules(Context context, MerchantUser user) {
        com.hunliji.hljcommonlibrary.models.merchant.MerchantUser merchantUser = new com.hunliji
                .hljcommonlibrary.models.merchant.MerchantUser();
        merchantUser.setId(user.getUserId());
        merchantUser.setAvatar(user.getAvatar());
        merchantUser.setNick(user.getName());
        merchantUser.setToken(user.getUserToken());
        merchantUser.setPhone(user.getPhone());
        merchantUser.setAccessToken(user.getToken());
        merchantUser.setCityName(user.getCityName());
        merchantUser.setLatitude(user.getLatitude());
        merchantUser.setLongitude(user.getLongitude());
        merchantUser.setMerchantId(user.getId());
        merchantUser.setPro(user.getIsPro());
        merchantUser.setShopType(user.getShopType());
        merchantUser.setExamine(user.getExamine());
        merchantUser.setCertifyStatus(user.getCertifyStatus());

        saveCommonLocation(context, user);
        saveCommonCity(context, user);

        try {
            UserSession.getInstance()
                    .setUser(context, merchantUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销子模块中的用户信息
     *
     * @param context
     */
    public static void logoutModules(Context context) {
        UserSession.getInstance()
                .logout(context);
    }


    private static void saveCommonLocation(Context context, MerchantUser user) {
        Location location = new Location();
        location.setLatitude(user.getLatitude());
        location.setLongitude(user.getLongitude());
        location.setProvince(user.getCityName());
        LocationSession.getInstance()
                .saveLocation(context, location);
    }

    private static void saveCommonCity(Context context, MerchantUser user) {
        City city = new City();
        city.setCid(user.getCity_code());
        city.setName(user.getCityName());
        if (city.getCid() == 0 && user.getCity() != null && user.getCity()
                .getId() > 0) {
            city.setCid(user.getCity()
                    .getId());
            city.setName(user.getCity()
                    .getName());
        }
        LocationSession.getInstance()
                .saveCity(context, city);
    }


    public static WSProduct getWSProduct(Serializable track) {
        if (track == null) {
            return null;
        }
        WSProduct extraObj = new WSProduct();
        if (track instanceof Work) {
            extraObj.setKind(WSChat.WORK_OR_CASE);
            extraObj.setTitle(((Work) track).getTitle());
            extraObj.setCoverPath(((Work) track).getCoverPath());
            extraObj.setId(((Work) track).getId());
            if (((Work) track).getCommodityType() == 0) {
                extraObj.setActualPrice(((Work) track).getShowPrice());
            }
            return extraObj;
        } else if (track instanceof CustomSetmeal) {
            extraObj.setKind(WSChat.CUSTOM_MEAL);
            extraObj.setTitle(((CustomSetmeal) track).getTitle());
            extraObj.setCoverPath(((CustomSetmeal) track).getCoverPath());
            extraObj.setId(((CustomSetmeal) track).getId());
            extraObj.setActualPrice(((CustomSetmeal) track).getActualPrice());
            return extraObj;
        } else if (track instanceof ShopProduct) {
            ShopProduct product = (ShopProduct) track;
            extraObj.setKind(WSChat.PRODUCT);
            extraObj.setTitle(product.getTitle());
            extraObj.setCoverPath(product.getCoverPath());
            extraObj.setId(product.getId());
            extraObj.setActualPrice(product.getActualPrice());
            return extraObj;
        }
        return null;
    }

    public static WSProduct getWSProduct(Parcelable track) {
        if (track == null) {
            return null;
        }
        WSProduct extraObj = new WSProduct();
        if (track instanceof ShopProduct) {
            ShopProduct product = (ShopProduct) track;
            extraObj.setKind(WSChat.PRODUCT);
            extraObj.setTitle(product.getTitle());
            extraObj.setCoverPath(product.getCoverPath());
            extraObj.setId(product.getId());
            extraObj.setActualPrice(product.getActualPrice());
            return extraObj;
        }
        return null;
    }

}
