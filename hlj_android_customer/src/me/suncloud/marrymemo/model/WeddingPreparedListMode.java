package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kingsun on 15/12/16.
 */
public class WeddingPreparedListMode {
    private List<WeddingPreparedListModelItem> strategys;
    private List<WeddingPreparedListModelItem> recommends;

    public WeddingPreparedListMode(JSONObject object) {
        if (object != null) {
            JSONArray strategu = object.optJSONArray("strategy");
            if (strategu != null) {
                strategys = new LinkedList<>();
                for (int i = 0; i < strategu.length(); i++) {
                    JSONObject obj = strategu.optJSONObject(i);
                    WeddingPreparedListModelItem str = new WeddingPreparedListModelItem(obj);
                    strategys.add(str);
                }
            }

            JSONArray recommend = object.optJSONArray("recommend");
            if (recommend != null) {
                recommends = new LinkedList<>();
                for (int j = 0; j < recommend.length(); j++) {
                    JSONObject obj = recommend.optJSONObject(j);
                    WeddingPreparedListModelItem item = new WeddingPreparedListModelItem(obj);
                    recommends.add(item);
                }
            }
        }
    }

    public List<WeddingPreparedListModelItem> getStrategys() {
        return strategys;
    }

    public void setStrategys(List<WeddingPreparedListModelItem> strategys) {
        this.strategys = strategys;
    }

    public List<WeddingPreparedListModelItem> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<WeddingPreparedListModelItem> recommends) {
        this.recommends = recommends;
    }
}
