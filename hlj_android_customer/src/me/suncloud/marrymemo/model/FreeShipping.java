package me.suncloud.marrymemo.model;

import org.json.JSONObject;

/**
 * 运费模板model
 * Created by jinxin on 2017/10/19 0019.
 */

public class FreeShipping implements Identifiable{

    long id;
    double money;
    int num;
    int type;

    public FreeShipping(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("express_template_id",0L);
            this.money = jsonObject.optDouble("money");
            this.num = jsonObject.optInt("num");
            this.type= jsonObject.optInt("type");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public double getMoney() {
        return money;
    }

    public int getNum() {
        return num;
    }

    public int getType() {
        return type;
    }
}
