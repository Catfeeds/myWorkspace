package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;

import java.util.List;

/**
 * Created by werther on 16/12/7.
 * 搜索数据基本model
 */

public class BaseSearchResult implements HljRZData {
    int type;
    int total;
    @SerializedName("page_count")
    int pageCount;
    @SerializedName("total_around")
    int totalAround;

    public int getType() {
        return type;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalWithAround() {
        return total + totalAround;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getTotalAround() {
        return totalAround;
    }

    @Override
    public boolean isEmpty() {
        return total == 0;
    }
}
