package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljkefulibrary.moudles.EMTrack;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CarProduct;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.view.kefu.EMChatActivity;

/**
 * Created by Suncloud on 2015/12/2.
 */
public class CustomerSupportUtil {


    /**
     * 轻松聊的婚车客服跳转
     *
     * @param activity
     * @param carProduct 婚车实体
     * @param support    已获取的客服实例
     */
    public static void goToSupport(
            final Activity activity, @Nullable final CarProduct carProduct, Support support) {
        if (support != null) {
            Intent intent = new Intent(activity, EMChatActivity.class);
            EMTrack track = objectToTrack(activity, carProduct);
            if (track != null) {
                intent.putExtra("track", track);
            }
            intent.putExtra("support", support);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    /**
     * Native专用的跳转到客服界面
     *
     * @param kind 客服服务类型
     */
    public static void goToSupport(Context context, int kind) {
        goToSupport(context, kind, null);
    }

    /**
     * 跳转到客服界面
     *
     * @param kind     客服服务类型
     * @param extraObj 传入的数据实体,model
     */
    public static void goToSupport(
            final Context context, final int kind, @Nullable final Object extraObj) {
        final Dialog progressDialog = DialogUtil.createProgressDialog(context);
        progressDialog.show();
        SupportUtil.getInstance(context)
                .getSupport(context, kind, new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportCompleted(Support support) {
                        super.onSupportCompleted(support);
                        progressDialog.cancel();
                        if (support != null) {
                            Intent intent = new Intent(context, EMChatActivity.class);
                            EMTrack track = objectToTrack(context, extraObj);
                            if (track != null) {
                                intent.putExtra("track", track);
                            }
                            intent.putExtra("support", support);
                            context.startActivity(intent);
                            if (context instanceof Activity) {
                                ((Activity) context).overridePendingTransition(R.anim
                                                .slide_in_right,
                                        R.anim.activity_anim_default);
                            }
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        progressDialog.cancel();
                        Util.showToast(context, null, R.string.msg_get_supports_error);
                    }
                });
    }

    private static EMTrack objectToTrack(Context context, Object object) {
        if (object == null) {
            return null;
        }
        EMTrack track = null;
        if (object instanceof CarOrder) {
            CarOrder order = (CarOrder) object;
            CarProduct carProduct = order.getSubOrders()
                    .get(0)
                    .getCarProduct();
            if (carProduct == null) {
                return null;
            }
            track = new EMTrack();
            track.setTitle(context.getString(R.string.label_order_code) + order.getOrderNo());
            track.setDesc(carProduct.getTitle());
            track.setPriceStr(context.getString(R.string.label_price,
                    Util.formatDouble2String(order.getOriginActualMoney())));
            track.setImagePath(carProduct.getCover());
            track.setTrackType(EMTrack.TRACK_TYPE_CAR_ORDER);
            track.setTrackId(order.getId());
            track.setTrackImageWidth(16);
            track.setTrackImageHeight(10);
        } else if (object instanceof ProductOrder) {
            ProductOrder order = (ProductOrder) object;
            ShopProduct product = order.getSubOrders()
                    .get(0)
                    .getProduct();
            if (product == null) {
                return null;
            }
            track = new EMTrack();
            track.setTitle(context.getString(R.string.label_order_code) + order.getOrderNo());
            track.setDesc(product.getTitle());
            track.setPriceStr(context.getString(R.string.label_price,
                    Util.formatDouble2String(order.getActualMoney())));
            track.setImagePath(product.getCoverPath());
            track.setTrackType(EMTrack.TRACK_TYPE_PRODUCT_ORDER);
            track.setTrackId(order.getId());
            track.setTrackImageWidth(product.getCoverImage()
                    .getWidth());
            track.setTrackImageHeight(product.getCoverImage()
                    .getHeight());
        } else if (object instanceof Work) {
            Work work = (Work) object;
            track = new EMTrack();
            track.setTitle(work.getTitle());
            track.setDesc(work.getTitle());
            track.setPriceStr(context.getString(R.string.label_price,
                    Util.formatDouble2String(work.getShowPrice())));
            track.setImagePath(work.getCoverPath());
            track.setLinkUrl(work.getUrl());
            track.setTrackType(EMTrack.TRACK_TYPE_WORK);
            track.setTrackId(work.getId());
            track.setTrackImageWidth(20);
            track.setTrackImageHeight(13);
        } else if (object instanceof Merchant) {
            Merchant merchant = (Merchant) object;
            Hotel hotel = merchant.getHotel();
            if (hotel == null) {
                return null;
            }
            track = new EMTrack();
            track.setTitle(merchant.getName());
            track.setDesc(hotel.getCityName() + merchant.getAddress() + hotel.getKind());
            track.setPriceStr(context.getString(R.string.label_hotel_price, hotel.getPriceStr()));
            track.setImagePath(merchant.getLogoPath());
            track.setLinkUrl(merchant.getUrl());
            track.setTrackType(EMTrack.TRACK_TYPE_HOTEL_V2);
            track.setTrackId(merchant.getId());
            track.setTrackImageWidth(16);
            track.setTrackImageHeight(10);
        }
        return track;
    }

    private static void setCarCity(Context context, CarProduct carProduct) {
        if (carProduct.getCity_code() == 0) {
            City city = Session.getInstance()
                    .getMyCity(context);
            carProduct.setCityName(city.getName());
            return;
        }
        JSONObject jsonObject = getCitiesFile(context);
        if (jsonObject == null) {
            return;
        }
        JSONObject citiesJson = jsonObject.optJSONObject("list");
        if (citiesJson != null) {
            Iterator<String> iterator = citiesJson.keys();
            while (iterator.hasNext()) {
                String letter = iterator.next();
                JSONArray array = citiesJson.optJSONArray(letter);
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        City city = new City(array.optJSONObject(i));
                        if (city.getId() == carProduct.getCity_code() && !JSONUtil.isEmpty(city
                                .getName())) {
                            carProduct.setCityName(city.getName());
                            return;
                        }
                    }
                }
            }
        }
    }


    private static JSONObject getCitiesFile(Context context) {
        if (context.getFileStreamPath(Constants.NEW_CITIES_FILE) != null && context
                .getFileStreamPath(
                Constants.NEW_CITIES_FILE)
                .exists()) {
            try {
                InputStream in = context.openFileInput(Constants.NEW_CITIES_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                return new JSONObject(jsonStr);
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从固定的几个提示语中随机取出一个
     *
     * @return
     */
    public static String getCarMerchantChatLinkMsg() {
        String[] messageArray = {"亲爱哒，对婚车租用有疑问就戳我哦！", "亲爱哒，婚车租用优惠多多！戳我小窗告诉你哦~",
                "小主，担心婚车没有档期？点我！纪小犀来帮你！", "不知道如何挑选婚车？点我！纪小犀来帮你！", "该怎么预订婚车？点我！纪小犀来帮你！",
                "好烦恼，提前多久订婚车最合适呢？戳我小窗告诉你哦~", "怎么办，哪款套餐最实惠？戳我小窗告诉你哦~", "怎么组合车队又大气又实惠？戳我小窗告诉你哦~",
                "哎呀，用车超时怎么办？纪小犀来帮你！", "哎呀，用车超过约定距离怎么办？纪小犀来帮你！", "预订婚车有优惠吗？戳我小窗告诉你哦~",
                "看晕了，如何挑选物美价廉的婚车？戳我小窗告诉你哦~", "婚车的费用如何计算？纪小犀在线为您解答！", "亲，婚车档期火爆抢购中，速速戳我预订哦！",
                "HELLO，我是您的专属婚车顾问，有什么能够帮助您的呢？"};
        Random r = new Random();
        int randomIndex = r.nextInt(messageArray.length);

        return messageArray[randomIndex];
    }
}
