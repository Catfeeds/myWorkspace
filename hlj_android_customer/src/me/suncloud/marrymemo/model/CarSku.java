package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/9/21.
 */
public class CarSku extends Sku {
    private ArrayList<CarSkuItem> items;
    private String skuNames;

    public CarSku(JSONObject jsonObject) {
        super(jsonObject);
        try {
            JSONArray array = jsonObject.getJSONArray("sku_item");
            this.items = initCarSkuitems(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<CarSkuItem> initCarSkuitems(JSONArray array) {
        ArrayList<CarSkuItem> items = new ArrayList<CarSkuItem>();
        try {
            for (int i = 0; i < array.length(); i++) {
                items.add(initCarSkuItem(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }


    private CarSkuItem initCarSkuItem(JSONObject object) {
        CarSkuItem item = null;
        try {
            int id = object.getInt("id");
            String property = object.getString("property");
            String value = object.getString("value");
            int property_id = object.optInt("property_id", -1);
            int sku_id = object.optInt("sku_id", -1);
            int value_id = object.optInt("value_id",-1);
            item = new CarSkuItem();
            item.setId(id);
            item.setProperty(property);
            item.setValue(value);
            item.setProperty_id(property_id);
            item.setSku_id(sku_id);
            item.setValue_id(value_id);
            return item;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }


    public ArrayList<CarSkuItem> getItems() {
        return items;
    }

    public String getSkuNames() {
        if (!JSONUtil.isEmpty(skuNames)) {
            return skuNames;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (items != null && !items.isEmpty()) {
            int size = items.size();
            for (int i = 0; i < size; i++) {
                CarSkuItem item = items.get(i);
                if (i > 0) {
                    stringBuffer.append(";");
                }
                stringBuffer.append(item.getValue());
            }
        }

        skuNames = stringBuffer.toString();

        return skuNames;
    }
}
