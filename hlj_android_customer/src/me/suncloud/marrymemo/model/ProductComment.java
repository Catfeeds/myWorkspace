package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/8/14.
 */
public class ProductComment implements Identifiable {


    private long id;
    private Date time;
    private float rating;
    private String content;
    private String sku;
    private User user;
    private ArrayList<Photo> photos;
    private ShopProduct shopProduct;

    public ProductComment(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id", 0);
            this.rating = object.optInt("rating");
            this.time = JSONUtil.getDate(object, "created_at");
            this.content = JSONUtil.getString(object, "content");
            this.user = object.isNull("user") ? null : new User(
                    object.optJSONObject("user"));
            if (user != null) {
                user.setId(object.optLong("user_id"));
            }
            if (!object.isNull("photos")) {
                JSONArray array = object.optJSONArray("photos");
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
            JSONObject skuObject = object.optJSONObject("sku");
            if (skuObject != null && !skuObject.isNull("name")) {
                sku = JSONUtil.getString(skuObject, "name");
            }
            if (!object.isNull("shop_product")) {
                shopProduct = new ShopProduct(object.optJSONObject("shop_product"));
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public User getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public float getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getSku() {
        return sku;
    }

    public ShopProduct getShopProduct() {
        return shopProduct;
    }
}
