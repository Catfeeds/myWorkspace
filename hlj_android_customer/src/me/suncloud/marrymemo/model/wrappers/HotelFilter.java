package me.suncloud.marrymemo.model.wrappers;

import me.suncloud.marrymemo.model.Desk;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.model.MenuItem;

/**
 * Created by mo_yu on 2017/10/17.酒店筛选model
 */

public class HotelFilter implements Identifiable{

    private long id;
    private MenuItem areaItem;
    private Desk currentDesk;//酒店桌数
    private String minPrice;
    private String maxPrice;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MenuItem getAreaItem() {
        return areaItem;
    }

    public void setAreaItem(MenuItem areaItem) {
        this.areaItem = areaItem;
    }

    public Desk getCurrentDesk() {
        return currentDesk;
    }

    public void setCurrentDesk(Desk currentDesk) {
        this.currentDesk = currentDesk;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
}
