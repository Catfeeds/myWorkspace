/**
 *
 */
package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;


public class Comment implements Identifiable {

    private static final long serialVersionUID = -2818839030466981950L;
    private long id;
    private Date time;
    private float rating;
    private String content;
    private Author user;
    private Work work;
    private ArrayList<Photo> photos;
    private int type; // 0: 普通套餐,1:定制套餐 3 商家评论
    private int commentType; // 2的时候是,婚品评论
    private ShopProduct shopProduct;
    private String nick;
    private String avatar;
    private int count;
    private CustomSetmeal customSetmeal;
    private int knowType;//1：去过实体店 、2：买过套餐


    private boolean showMerchantTitle; //显示套餐评论分隔 本地属性

    public Comment(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id", 0);
            this.rating = (float) object.optDouble("rating", 3);
            this.time = JSONUtil.getDate(object, "created_at");
            this.content = JSONUtil.getString(object, "content");
            this.user = object.isNull("author") ? null : new Author(object.optJSONObject("author"));
            if (!object.isNull("work")) {
                this.work = new Work(object.optJSONObject("work"));
            }

            if (!object.isNull("shop_product")) {
                shopProduct = new ShopProduct(object.optJSONObject("shop_product"));
            }
            if (user == null && !object.isNull("user")) {
                this.user = new Author(object.optJSONObject("user"));
            }

            this.type = object.optInt("type");
            this.commentType = object.optInt("comment_type");

            if (!object.isNull("comment_photos")) {
                JSONArray array = object.optJSONArray("comment_photos");
                if (array != null) {
                    int size = array.length();
                    if (size > 0) {
                        photos = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Photo photo = new Photo(array.optJSONObject(i));
                            if (!JSONUtil.isEmpty(photo.getPath())) {
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
                            if (!JSONUtil.isEmpty(photo.getPath())) {
                                photos.add(photo);
                            }
                        }
                    }
                }
            }
            this.nick = object.optString("nick");
            this.avatar = object.optString("avatar");
            this.count = object.optInt("count");
            if (!object.isNull("set_meal")) {
                customSetmeal = new CustomSetmeal(object.optJSONObject("set_meal"));
            }
            this.knowType = object.optInt("know_type", 0);
        }
    }

    public float getRating() {
        return rating;
    }

    public Author getUser() {
        if (this.user == null) {
            this.user = new Author();
        }
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

    public int getType() {
        return type;
    }

    public int getCommentType() {
        return commentType;
    }

    public ShopProduct getShopProduct() {
        return shopProduct;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public CustomSetmeal getCustomSetmeal() {
        return customSetmeal;
    }

    public int getKnowType() {
        return knowType;
    }

    public void setShowMerchantTitle(boolean showMerchantTitle) {
        this.showMerchantTitle = showMerchantTitle;
    }

    public boolean isShowMerchantTitle() {
        return showMerchantTitle;
    }
}
