package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;

/**
 * Created by werther on 16/12/6.
 */

public class ProductSearchResult implements HljRZData {

    @SerializedName("product")
    ProductSearchResultList productList;
    @SerializedName("tool")
    ToolsSearchResult toolsSearchResult;

    public ProductSearchResultList getProductList() {
        return productList;
    }

    public ToolsSearchResult getToolsSearchResult() {
        return toolsSearchResult;
    }

    @Override
    public boolean isEmpty() {
        return productList == null || productList.isEmpty();
    }
}
