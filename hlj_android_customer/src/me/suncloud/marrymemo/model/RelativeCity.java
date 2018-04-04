package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/10/30.
 */
public class RelativeCity implements Serializable {

    private City city;
    private ArrayList<Work> works;
    private int count;

    public RelativeCity(JSONObject jsonObject) {
        if (jsonObject != null) {
            city = new City(jsonObject.optJSONObject("city"));
            count = jsonObject.optInt("count_meal");
            JSONArray array = jsonObject.optJSONArray("meal");
            if (array != null && array.length() > 0) {
                works = new ArrayList<>();
                int size = array.length();
                for (int i = 0; i < size; i++) {
                    works.add(new Work(array.optJSONObject(i)));
                }
            }
        }
    }

    public ArrayList<Work> getWorks() {
        if (works == null) {
            works = new ArrayList<>();
        }
        return works;
    }

    public City getCity() {
        return city==null?new City():city;
    }

    public int getCount() {
        return count;
    }
}
