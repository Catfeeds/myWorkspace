package com.hunliji.hljlivelibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.live.LiveChannel;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;

import java.util.List;

/**
 * Created by Suncloud on 2016/10/31.
 */

public class LiveSocketData {
    @SerializedName("channel")
    private LiveChannel channel;
    @SerializedName("message")
    private LiveSocketMessage message;
    @SerializedName("merchant")
    private Merchant introMerchant; // 当前正在介绍的商家
    @SerializedName("set_meal")
    private Work introWork; // 当前正在介绍的套餐
    @SerializedName("shop_product")
    private ShopProduct introProduct; // 当前正在介绍的商品
    @SerializedName("cancel_stick_ids")
    private List<Long> cancelStickIds; // 取消置顶的id

    public Merchant getIntroMerchant() {
        return introMerchant;
    }

    public Work getIntroWork() {
        return introWork;
    }

    public ShopProduct getIntroProduct() {
        return introProduct;
    }

    public LiveChannel getChannel() {
        return channel;
    }

    public LiveSocketMessage getMessage() {
        return message;
    }

    public List<Long> getCancelStickIds() {
        return cancelStickIds;
    }
}
