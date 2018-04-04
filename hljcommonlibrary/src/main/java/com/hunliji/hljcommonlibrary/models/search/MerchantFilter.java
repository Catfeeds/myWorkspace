package com.hunliji.hljcommonlibrary.models.search;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by werther on 16/12/5.
 * 商家筛选数据统一，暂时实现已使用的部分，扩展部分需要再补充
 */

public class MerchantFilter {
    List<MerchantFilterProperty> properties;
    MerchantFilterHotel hotel;

    private static final long WEDDING_HOTEL_ID = 13;//婚宴酒店的id

    public MerchantFilterHotel getHotel() {
        return hotel;
    }

    public void setHotel(MerchantFilterHotel hotel) {
        this.hotel = hotel;
    }

    public List<MerchantFilterProperty> getProperties() {
        return properties;
    }

    /**
     * 将MerchantFilterProperty列表转换成Label列表
     * 新版搜索专用
     *
     * @return 搜索，套餐、案例、商家搜索结果页筛选分类去掉“婚宴酒店”
     */
    public ArrayList<MerchantFilterHotelTableLabel> getMerchantPropertyLabels() {
        ArrayList<MerchantFilterHotelTableLabel> labels = new ArrayList<>();
        for (MerchantFilterProperty property : properties) {
            if (property.getId() != WEDDING_HOTEL_ID) {
                MerchantFilterHotelTableLabel label = new MerchantFilterHotelTableLabel();
                label.setId(property.getId());
                label.setName(property.getName());
                labels.add(label);
            }
        }

        return labels;
    }
}
