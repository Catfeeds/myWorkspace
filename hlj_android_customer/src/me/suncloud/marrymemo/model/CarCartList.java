package me.suncloud.marrymemo.model;

import com.hunliji.hljcarlibrary.models.CarShoppingCartItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Suncloud on 2015/10/26.
 */
public class CarCartList implements Serializable {

    private long userId;
    //    private long cityId;
    private ArrayList<CarShoppingCartItem> items;

//    public long getCityId() {
//        return cityId;
//    }

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
//    public void setmCityId(long cityId) {
//        this.cityId = cityId;
//    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


}
