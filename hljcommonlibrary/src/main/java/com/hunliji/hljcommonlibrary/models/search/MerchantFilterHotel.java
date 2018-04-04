package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;

import java.util.List;

/**
 * Created by werther on 16/12/5.
 */

public class MerchantFilterHotel {
    List<String> price;
    @SerializedName("table")
    List<MerchantFilterHotelTableLabel> tableNums;
    @SerializedName("sort")
    List<Label> sorts;

    public List<String> getPrice() {
        return price;
    }

    public List<MerchantFilterHotelTableLabel> getTableNums() {
        return tableNums;
    }

    public List<Label> getSorts() {
        return sorts;
    }

    public void setPrice(List<String> price) {
        this.price = price;
    }

    public void setTableNums(List<MerchantFilterHotelTableLabel> tableNums) {
        this.tableNums = tableNums;
    }

    public void setSorts(List<Label> sorts) {
        this.sorts = sorts;
    }
}
