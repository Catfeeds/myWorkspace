package com.hunliji.hljcarlibrary.models;

import java.util.ArrayList;

/**
 * Created by jinxin on 2018/1/12 0012.
 */

public class WeddingCarCartList {

    private long userId;
    private ArrayList<CarShoppingCartItem> items;

    public boolean cityChecked(long cityId) {
        for (CarShoppingCartItem item : getItems()) {
            if (item.getCarProduct().getCityCode() != cityId) {
                return false;
            }
        }
        return true;
    }

    public long getUserId() {
        return userId;
    }

    public ArrayList<CarShoppingCartItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
