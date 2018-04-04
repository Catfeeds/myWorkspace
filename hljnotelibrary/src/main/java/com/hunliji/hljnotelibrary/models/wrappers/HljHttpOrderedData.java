package com.hunliji.hljnotelibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by chen_bin on 2017/6/28 0028.
 */
public class HljHttpOrderedData implements HljRZData {
    @SerializedName(value = "set_meal_orders")
    private List<Merchant> merchants;
    @SerializedName(value = "shop_product_orders")
    private List<ShopProduct> products;

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public List<ShopProduct> getProducts() {
        return products;
    }


    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(merchants) && CommonUtil.isCollectionEmpty(products);
    }
}
