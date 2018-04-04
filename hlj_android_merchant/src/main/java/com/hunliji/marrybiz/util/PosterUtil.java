package com.hunliji.marrybiz.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/13.
 */
public class PosterUtil {

    public static List<Poster> getPosterList(
            JsonObject jsonObject, String site, boolean posterNullable) {
        ArrayList<Poster> posters = new ArrayList<>();
        if (jsonObject == null || TextUtils.isEmpty(site)) {
            return posters;
        }
        JsonObject siteObject = jsonObject.getAsJsonObject(site);
        if (siteObject != null) {
            JsonArray array = siteObject.getAsJsonArray("holes");
            if (array != null && array.size() > 0) {
                for (int i = 0, size = array.size(); i < size; i++) {
                    JsonObject postJson = null;
                    if (array.get(i) != null) {
                        postJson = array.get(i).getAsJsonObject().getAsJsonObject("posters");
                    }
                    if (postJson!=null) {
                        Gson gson = GsonUtil.getGsonInstance();
                        Poster poster = gson.fromJson(postJson.toString(), Poster.class);
                        if (posterNullable || poster.getId() > 0) {
                            posters.add(poster);
                        }
                    }
                }
            }
        }
        return posters;
    }
}
