package me.suncloud.marrymemo.model.buyWork;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.suncloud.marrymemo.model.MerchantProperty;


/**
 * 买套餐model
 * Created by jinxin on 2016/12/6 0006.
 */

public class SetMeal {
    @SerializedName(value = "package")
    HljHttpData<List<Work>> works;
    @SerializedName(value = "tags")
    JsonElement tags;

    public HljHttpData<List<Work>> getWorks() {
        return works;
    }

    public void setWorks(HljHttpData<List<Work>> works) {
        this.works = works;
    }

    public List<MerchantProperty> getTags() {
        List<MerchantProperty> properties = new ArrayList<>();
        if (tags == null) {
            return properties;
        }
        JsonArray elements = tags.getAsJsonArray();
        for (int i = 0, size = elements.size(); i < size; i++) {
            try {
                JsonObject obj = elements.get(i)
                        .getAsJsonObject();
                if (obj != null) {
                    MerchantProperty p = new MerchantProperty(new JSONObject(obj.toString()));
                    if (p.getId() > 0) {
                        properties.add(p);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }

    public void setTags(JsonElement tags) {
        this.tags = tags;
    }

    public SetMeal() {}
}
