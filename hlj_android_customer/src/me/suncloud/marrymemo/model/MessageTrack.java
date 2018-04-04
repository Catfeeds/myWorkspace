package me.suncloud.marrymemo.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.JsonHelper;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2015/12/7.
 */
public class MessageTrack implements Identifiable {

    private String title;
    private String desc;
    private String price;
    private String img_url;
    private String item_url;
    private String trackType;
    private int trackImageWidth;
    private int trackImageHeight;
    private long trackId;

    public static final String TRACK_TYPE_WORK = "trackTypeWork";
    public static final String TRACK_TYPE_SHOP_PRODUCT = "trackTypeProduct";
    public static final String TRACK_TYPE_CAR_PRODUCT = "trackTypeCar";
    public static final String TRACK_TYPE_HOTEL_V2 = "trackTypeHotelV2";
    public static final String TRACK_TYPE_PRODUCT_ORDER = "trackTypeProductOrder";
    public static final String TRACK_TYPE_CAR_ORDER = "trackTypeCarOrder";
    public static final String TRACK_TYPE_HOTEL = "trackTypeHotel";

    public MessageTrack(JSONObject jsonObject) {
        if (jsonObject != null) {
            title = JSONUtil.getString(jsonObject, "title");
            desc = JSONUtil.getString(jsonObject, "desc");
            price = JSONUtil.getString(jsonObject, "price");
            img_url = JSONUtil.getString(jsonObject, "img_url");
            item_url = JSONUtil.getString(jsonObject, "item_url");
            trackType = JSONUtil.getString(jsonObject, "trackType");
            trackId = jsonObject.optLong("trackId");
            trackImageWidth = jsonObject.optInt("trackImageWidth");
            trackImageHeight = jsonObject.optInt("trackImageHeight");
        }

    }

    public MessageTrack(Work work, Context context) {
        title = work.getTitle();
        desc = work.getTitle();
        price = context.getString(R.string.label_price,
                Util.formatDouble2String(work.getShowPrice()));
        img_url = work.getCoverPath();
        item_url = work.getUrl();
        trackType = TRACK_TYPE_WORK;
        trackId = work.getId();
        trackImageWidth = 20;
        trackImageHeight = 13;
    }


    public MessageTrack(ShopProduct product, Context context) {
        title = product.getTitle();
        desc = product.getTitle();
        price = context.getString(R.string.label_price,
                Util.formatDouble2String(product.getPrice()));
        img_url = product.getPhoto();
        item_url = product.getUrl();
        trackType = TRACK_TYPE_SHOP_PRODUCT;
        trackId = product.getId();
        trackImageWidth = product.getWidth();
        trackImageHeight = product.getHeight();
    }

    public MessageTrack(CarProduct car, Context context) {
        title = car.getTitle();
        desc = JSONUtil.isEmpty(car.getCityName()) ? String.valueOf(car.getCity_code()) : car
                .getCityName();
        price = context.getString(R.string.label_price,
                Util.formatDouble2String(car.getShowPrice()));
        img_url = car.getCover();
        item_url = car.getUrl();
        trackType = TRACK_TYPE_CAR_PRODUCT;
        trackId = car.getId();
        trackImageWidth = 16;
        trackImageHeight = 10;
    }

    public MessageTrack(NewMerchant merchant, Context context) {
        Hotel hotel = merchant.getHotel();
        title = merchant.getName();
        if (hotel != null) {
            desc = hotel.getCityName() + merchant.getAddress() + hotel.getKind();
        } else {
            desc = merchant.getAddress();
        }

        price = context.getString(R.string.label_hotel_price, hotel.getPriceStr());
        img_url = merchant.getLogoPath();
        item_url = merchant.getUrl();
        trackType = TRACK_TYPE_HOTEL_V2;
        trackId = merchant.getId();
        trackImageWidth = 16;
        trackImageHeight = 10;
    }


    public MessageTrack(me.suncloud.marrymemo.model.orders.ProductOrder order, Context context) {
        com.hunliji.hljcommonlibrary.models.product.ShopProduct product = order.getSubOrders()
                .get(0)
                .getProduct();
        title = context.getString(R.string.label_order_code) + order.getOrderNo();
        desc = product.getTitle();
        price = context.getString(R.string.label_price,
                Util.formatDouble2String(order.getActualMoney()));
        img_url = product.getCoverPath();
        item_url = "";
        trackType = TRACK_TYPE_SHOP_PRODUCT;
        trackId = order.getId();
        trackImageWidth = product.getCoverImage()
                .getWidth();
        trackImageHeight = product.getCoverImage()
                .getHeight();
    }


    public MessageTrack(CarOrder order, Context context) {
        CarProduct carProduct = order.getSubOrders()
                .get(0)
                .getCarProduct();
        title = context.getString(R.string.label_order_code) + order.getOrderNo();
        desc = carProduct.getTitle();
        price = context.getString(R.string.label_price,
                Util.formatDouble2String(order.getOriginActualMoney()));
        img_url = carProduct.getCover();
        item_url = "";
        trackType = TRACK_TYPE_CAR_ORDER;
        trackId = order.getId();
        trackImageWidth = 16;
        trackImageHeight = 10;
    }


    @Override
    public Long getId() {
        return trackId;
    }


    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getPrice() {
        return price;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getItem_url() {
        return item_url;
    }

    public String getTrackType() {
        return trackType;
    }

    public int getTrackImageWidth() {
        return trackImageWidth;
    }

    public int getTrackImageHeight() {
        return trackImageHeight;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject(JsonHelper.ToJsonString(this));
    }
}
