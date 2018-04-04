package com.hunliji.hljcommonlibrary.models.search;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;

import java.util.List;

/**
 * Created by werther on 16/12/1.
 */

public class MerchantsSearchResult extends BaseSearchResult {

    @SerializedName("list")
    List<Merchant> merchants;
    @SerializedName("hotel")
    MerchantsSearchResult hotelsSearchResult;

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public MerchantsSearchResult getHotelsSearchResult() {
        return hotelsSearchResult;
    }

    /**
     * 酒店计算在内的总数
     *
     * @return
     */
    public int getTotalWithHotel() {
        int totalWithHotel = total;
        if (hotelsSearchResult != null) {
            totalWithHotel += hotelsSearchResult.getTotal();
        }

        return totalWithHotel;
    }

    public int getTotalWithHotelAndAround() {
        return getTotalWithHotel() + totalAround;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || merchants == null || merchants.isEmpty();
    }
}
