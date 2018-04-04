package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/5/5.推荐商家列表实体类
 */
public class MerchantRecommend implements Identifiable{
    /**
     * name : 大胖子摄影
     * logo_path : http://qnpic.hunliji.com/FoOarahfh1Yk0PYt5dBeM_tESpDf
     * active_works_pcount : 13
     * active_cases_pcount : 4
     * active_feed_pcount : 1
     * photo : [{"id":"478","cover_path":"http://qnpic4t1HUPM"},{"id":"477","cover_path":"http://qnpic.h6LMTO_gfMty"},{"id":"469","cover_path":"http://qnpic.hunliji.rb"}]
     */

    private Long id;
    private String name;
    private String logo_path;
    private int active_works_pcount;
    private int active_cases_pcount;
    private int active_feed_pcount;
    private boolean is_followed;
    /**
     * id : 478
     * cover_path : http://qnpic4t1HUPM
     */

    private List<Photo> photos = new ArrayList<>();

    public MerchantRecommend(JSONObject json) {
        if (json != null) {
            id = json.optLong("id",0);
            name = JSONUtil.getString(json,"name");
            logo_path = JSONUtil.getString(json,"logo_path");
            active_works_pcount = json.optInt("active_works_pcount", 0);
            active_cases_pcount = json.optInt("active_cases_pcount", 0);
            active_feed_pcount = json.optInt("active_feed_pcount", 0);
            is_followed = json.optBoolean("is_followed", false);
            if (!is_followed) {
                is_followed = json.optInt("is_followed") > 0;
            }
            if(!json.isNull("photo")){
                JSONArray array = json.optJSONArray("photo");
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        photos.add(new Photo(array.optJSONObject(i)));
                    }
                }
            }
        }
    }


    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo_path() {
        return logo_path;
    }

    public int getActive_works_pcount() {
        return active_works_pcount;
    }

    public int getActive_cases_pcount() {
        return active_cases_pcount;
    }

    public int getActive_feed_pcount() {
        return active_feed_pcount;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public boolean is_followed() {
        return is_followed;
    }

    public void setIs_followed(boolean is_followed) {
        this.is_followed = is_followed;
    }

    public static class Photo {
        private Long id;
        private String cover_path;

        public Photo(JSONObject json){
            if (json != null) {
                id = json.optLong("id",0);
                cover_path = JSONUtil.getString(json,"cover_path");
            }
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCover_path() {
            return cover_path;
        }

        public void setCover_path(String cover_path) {
            this.cover_path = cover_path;
        }
    }



}
