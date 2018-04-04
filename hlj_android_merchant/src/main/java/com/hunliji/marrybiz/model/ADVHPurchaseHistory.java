package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

/**
 * Created by werther on 15/12/23.
 */
public class ADVHPurchaseHistory implements Identifiable {

    private static final long serialVersionUID = 6792209498030329529L;
    private long id;
    private int num;
    private double price;
    private DateTime createdAt;
    private int type;

    public ADVHPurchaseHistory(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("entity_id", 0);
            this.num = jsonObject.optInt("value", 0);
            this.price = jsonObject.optDouble("price", 0);
            this.createdAt = JSONUtil.getDateTime(jsonObject, "created_at");
            this.type = jsonObject.optInt("type", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public double getPrice() {
        return price;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public int getType() {
        return type;
    }
}
