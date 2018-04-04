package com.hunliji.hljtrackerlibrary.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Suncloud on 2015/4/9.
 */
public class TrackerUtil {

    public static JSONObject getSiteJson(String sid, int pos, String desc) {
        if (!TextUtils.isEmpty(sid) || !TextUtils.isEmpty(desc) || pos > 0) {
            try {
                JSONObject site = new JSONObject();
                site.put("sid", sid);
                site.put("pos", pos);
                site.put("desc", desc);
                return site;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
