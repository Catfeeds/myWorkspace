package com.hunliji.hljhttplibrary.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.PosterSite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 2016/9/13.
 */
public class PosterUtil {


    public static ArrayList<Poster> getPosterList(
            JsonObject jsonObject, String site, boolean posterNullable) {
        boolean isClosed=FinancialSwitch.INSTANCE.isClosed(null);
        ArrayList<Poster> posters = new ArrayList<>();
        if (jsonObject == null || TextUtils.isEmpty(site)) {
            return posters;
        }
        JsonObject siteObject = jsonObject.getAsJsonObject(site);
        if (siteObject != null) {
            JsonArray array = siteObject.getAsJsonArray("holes");
            Gson gson = GsonUtil.getGsonInstance();
            for (JsonElement jsonElement : array) {
                try {
                    Poster poster = gson.fromJson(jsonElement.getAsJsonObject()
                            .get("posters"), Poster.class);
                    if (poster == null) {
                        continue;
                    }
                    if (posterNullable || poster.getId() > 0) {
                        if (isClosed && poster.getTargetType() == 48) {
                            continue;
                        }
                        posters.add(poster);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return posters;
    }

    public static <T> T getAnnotationPoster(
            JsonObject jsonObject, T t) {
        Field[] fields = t.getClass()
                .getDeclaredFields();
        for (Field field : fields) {
            PosterSite posterSite = field.getAnnotation(PosterSite.class);
            if (posterSite != null) {
                field.setAccessible(true);
                List<Poster> posters = PosterUtil.getPosterList(jsonObject,
                        posterSite.name(),
                        !posterSite.emptyVerify());
                if (posterSite.index() >= 0) {
                    if (!CommonUtil.isCollectionEmpty(posters)) {
                        int index = Math.max(posterSite.index(), posters.size() - 1);
                        try {
                            field.set(t, posters.get(index));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        field.set(t, posters);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return t;
    }
}
