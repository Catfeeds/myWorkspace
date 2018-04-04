package com.hunliji.hljcommonlibrary.view_tracker;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerPage;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtao on 2018/2/11.
 */

public enum HljTrackerParameter {

    INSTANCE;

    private Map<String, TrackerPage> pageMap;
    private Map<String, List<TrackerView>> viewMap;


    public void setTrackerConfig(JsonObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        Gson gson = new Gson();
        try {
            if (jsonObject.get("pages") != null) {
                List<TrackerPage> pages = gson.fromJson(jsonObject.getAsJsonArray("pages"),
                        new TypeToken<List<TrackerPage>>() {}.getType());
                pageMap = new HashMap<>();
                for (TrackerPage page : pages) {
                    if (!TextUtils.isEmpty(page.getClassName())) {
                        pageMap.put(page.getClassName(), page);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (jsonObject.get("views") != null) {
                viewMap = gson.fromJson(jsonObject.getAsJsonObject("views"),
                        new TypeToken<Map<String, List<TrackerView>>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TrackerPage getPage(String className) {
        if (pageMap == null) {
            return null;
        }
        return pageMap.get(className);
    }


    public List<TrackerView> getViews(String className) {
        if (viewMap == null) {
            return null;
        }
        return viewMap.get(className);
    }
}
