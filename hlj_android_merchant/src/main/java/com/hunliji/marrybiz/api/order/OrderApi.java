package com.hunliji.marrybiz.api.order;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.marrybiz.model.orders.ChangeOrderPriceBody;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.model.orders.MerchantOrderFilter;
import com.hunliji.marrybiz.model.orders.OrderProtocolPhoto;
import com.hunliji.marrybiz.model.orders.OrderProtocolPhotosPostBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by luohanlin on 28/02/2017.
 */

public class OrderApi {

    /**
     * 确认已收到尾款
     *
     * @param orderId
     * @return
     */
    public static Observable<Object> confirmRestMoney(long orderId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", orderId);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .confirmRestMoney(jsonObject)
                .map(new HljHttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传订单合同图片
     *
     * @param orderId
     * @param photos
     * @return
     */
    public static Observable postProtocolPhotos(long orderId, ArrayList<Photo> photos) {
        ArrayList<OrderProtocolPhoto> protocolPhotos = new ArrayList<>();
        for (Photo p : photos) {
            OrderProtocolPhoto protocolPhoto = new OrderProtocolPhoto(p.getImagePath(),
                    p.getWidth(),
                    p.getHeight());
            protocolPhotos.add(protocolPhoto);
        }
        OrderProtocolPhotosPostBody body = new OrderProtocolPhotosPostBody(orderId, protocolPhotos);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .postProtocolPhotos(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改价格
     *
     * @param orderSubId
     * @param actualPrice
     * @param earnestMoney
     * @return
     */
    public static Observable<JsonElement> postChangeOrderPrice(
            long orderSubId, Double actualPrice, Double earnestMoney) {
        ChangeOrderPriceBody body = new ChangeOrderPriceBody(orderSubId, actualPrice, earnestMoney);
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .postChangePrice(body)
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家订单列表（BD）
     *
     * @return
     */
    public static Observable<HljHttpData<List<MerchantOrder>>> getMerchantOrderListObb
    (String url) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getMerchantOrderList(url)
                .map(new HljHttpResultFunc<HljHttpData<List<MerchantOrder>>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家订单详情
     *
     * @param id
     * @return
     */
    public static Observable<MerchantOrder> getMerchantOrderDetailObb(long id) {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getMerchantOrderDetail(id)
                .map(new HljHttpResultFunc<MerchantOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家筛选列表
     *
     * @return
     */
    public static Observable<List<MerchantOrderFilter>> getMerchantOrderFilterListObb() {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getMerchantOrderFilterList()
                .map(new HljHttpResultFunc<List<MerchantOrderFilter>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取商家订单未支付数量
     *
     * @return
     */
    public static Observable<JsonElement> getWaitPayCountObb() {
        return HljHttp.getRetrofit()
                .create(OrderService.class)
                .getWaitPayCount()
                .map(new HljHttpResultFunc<JsonElement>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
