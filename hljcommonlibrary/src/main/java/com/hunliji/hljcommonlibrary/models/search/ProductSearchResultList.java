package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;


/**
 * Created by werther on 16/12/6.
 */

public class ProductSearchResultList extends BaseSearchResult {
    @SerializedName("list")
    List<ShopProduct> products;
    @SerializedName("subject")
    List<ProductTopic> topics;

    public List<ShopProduct> getProducts() {
        return products;
    }

    public List<ProductTopic> getTopics() {
        return topics;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || CommonUtil.isCollectionEmpty(products);
    }
}
