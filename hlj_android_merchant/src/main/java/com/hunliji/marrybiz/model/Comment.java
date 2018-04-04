/**
 *
 */
package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class Comment implements Identifiable {


    private static final long serialVersionUID = -2818839030466981950L;
    private long id;
    private Date time;
    private float rating;
    private float describeRating;
    private float attitudeRating;
    private float qualityRating;
    private String content;
    private Author user;
    private Work work;
    private ArrayList<Photo> photos;
    private String nick;
    private String avatar;
    private int count;
    private CustomSetmeal customSetmeal;
    private int type;
    private int commentType;

    public Comment(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id", 0);
            this.rating = (float) object.optDouble("rating", 3);
            this.describeRating = (float) object.optDouble("describe_rating", 5);
            this.attitudeRating = (float) object.optDouble("attitude_rating", 5);
            this.qualityRating = (float) object.optDouble("quality_rating", 5);
            this.time = JSONUtil.getDate(object, "created_at");
            this.content = JSONUtil.getString(object, "content");
            this.user = object.isNull("author") ? null : new Author(object.optJSONObject("author"));
            if (!object.isNull("work")) {
                this.work = new Work(object.optJSONObject("work"));
            }
            if (!object.isNull("comment_photos")) {
                JSONArray array = object.optJSONArray("comment_photos");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        photos = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getImagePath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            } else if (!object.isNull("imgs")) {
                JSONArray array = object.optJSONArray("imgs");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        photos = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getImagePath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            }
            if (!object.isNull("set_meal")) {
                customSetmeal = new CustomSetmeal(object.optJSONObject("set_meal"));
            }
            this.nick = object.optString("nick");
            this.avatar = object.optString("avatar");
            this.count = object.optInt("count");
            this.type = object.optInt("type");
            this.commentType = object.optInt("comment_type");
        }
    }

    public float getRating() {
        return rating;
    }

    public Author getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Work getWork() {
        return work;
    }

    public ArrayList<Photo> getPhotos() {
        return photos == null ? new ArrayList<Photo>() : photos;
    }

    public float getAttitudeRating() {
        return attitudeRating;
    }

    public float getDescribeRating() {
        return describeRating;
    }

    public float getQualityRating() {
        return qualityRating;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getCount() {
        return count;
    }

    public CustomSetmeal getCustomSetmeal() {
        return customSetmeal;
    }

    public int getType() {
        return type;
    }

    public int getCommentType() {
        return commentType;
    }
}