package com.hunliji.hljtrackerlibrary;

import android.content.Context;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.HljApplicationInterface;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljtrackerlibrary.api.TrackerApi;

import org.json.JSONObject;

import rx.Subscriber;

/**
 * Created by wangtao on 2016/12/14.
 */

public class TrackerHelper {

    /**
     * 酒店频道埋点，用户点击筛选
     *
     * @param context
     * @param filterString 筛选信息  {"type":"1","price":"2000-3000","num":"10-20"}
     */
    public static void hotelFilter(Context context, String filterString) {
        new HljTracker.Builder(context).eventableType("HotelFilter")
                .action("hit")
                .additional(filterString)
                .build()
                .add();
    }

    /**
     * 活动展示埋点
     *
     * @param context
     * @param id       活动id
     * @param siteJson 额外site信息
     */
    public static void activityInfo(Context context, long id, JSONObject siteJson) {
        new HljTracker.Builder(context).eventableType("Activity")
                .eventableId(id)
                .action("hit")
                .site(siteJson)
                .build()
                .add();

    }

    /**
     * 找商家上方10个类别按钮的点击
     *
     * @param context
     * @param id      分类id
     */
    public static void findMerchantProperty(Context context, long id) {
        new HljTracker.Builder(context).eventableType("FindMerchantProperty")
                .eventableId(id)
                .action("hit")
                .build()
                .add();
    }


    /**
     * 用户切换城市埋点
     *
     * @param context
     */
    public static void changeCity(Context context) {
        User user = UserSession.getInstance()
                .getUser(context);
        City city = LocationSession.getInstance()
                .getCity(context);
        new HljTracker.Builder(context).eventableType("User")
                .action("switch_city")
                .eventableId(user != null ? user.getId() : 0)
                .additional(String.valueOf(city != null ? city.getCid() : 0))
                .build()
                .add();
    }


    /**
     * 聊天点击
     *
     * @param merchantUserId 商家的userId
     */
    public static void chatToMerchant(Context context, long merchantUserId) {
        new HljTracker.Builder(context).eventableType("Merchant")
                .action("chat")
                .eventableId(merchantUserId)
                .desc(getActivityHistory(context))
                .build()
                .send();
    }

    public static String getActivityHistory(Context context) {
        if (context.getApplicationContext() == null || !(context.getApplicationContext()
                instanceof HljApplicationInterface)) {
            return "";
        }
        return ((HljApplicationInterface) context.getApplicationContext()).getActivityHistory();
    }


    /**
     * @param id   分享实体id
     * @param type 分享类型 merchant/set_meal/answer/note(动态)
     */
    public static void postShareAction(Context context,long id, String type) {
        if(CommonUtil.getAppType()!= CommonUtil.PacketType.CUSTOMER){
            return;
        }
        TrackerApi.postShareAction(id, type)
                .subscribe(new Subscriber<JsonElement>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonElement jsonElement) {

                    }
                });
    }

    /**
     * 聊天点击
     *
     * @param id 金融产品id
     * @param type 类型
     * @param url 跳转地址
     * @param pos 位置
     */
    public static void hitFinancialProduct(Context context, long id,String type,String url,int pos) {
        new HljTracker.Builder(context).eventableType("financial_supermarket")
                .action("hit")
                .eventableId(id)
                .additional(type)
                .sid(url)
                .pos(pos)
                .build()
                .send();
    }


}
