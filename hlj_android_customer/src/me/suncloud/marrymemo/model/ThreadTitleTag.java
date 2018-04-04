package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/9/20.
 * 社区话题标题中设置的标签,带颜色和name,自己生成tag图片,混加到标题头部
 */

public class ThreadTitleTag {
    private long id;
    private String name;
    private String kind;
    private int[] colorRgb;

    public ThreadTitleTag(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            name = JSONUtil.getString(jsonObject, "name");
            kind = JSONUtil.getString(jsonObject, "kind");
            colorRgb = new int[3];
            JSONArray colorArray = jsonObject.optJSONArray("colorRGB");
            if (colorArray != null && colorArray.length() == 3) {
                colorRgb[0] = colorArray.optInt(0);
                colorRgb[1] = colorArray.optInt(1);
                colorRgb[2] = colorArray.optInt(2);
            }
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public int[] getColorRgb() {
        return colorRgb;
    }
}
