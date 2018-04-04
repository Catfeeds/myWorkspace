package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;

/**
 * 商家评价
 * Created by chen_bin on 2017/9/28 0028.
 */
public class HljHttpHotelHallData implements HljRZData {
    @SerializedName(value = "merchant")
    private Merchant merchant;
    @SerializedName(value = "hall")
    private HotelHall hotelHall;

    public Merchant getMerchant() {
        return merchant;
    }

    public HotelHall getHotelHall() {
        return hotelHall;
    }

    @Override
    public boolean isEmpty() {
        return merchant == null || hotelHall == null;
    }
}
