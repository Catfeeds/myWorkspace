package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/10/19.
 */
public class CarComment implements Identifiable {

    private long id;
    private Date time;
    private String content;
    private ArrayList<CarProduct> carProducts;
    private HashMap<Long,Integer> countMap;
    private User user;
    private ArrayList<Photo> photos;
    private boolean isExpand;

    public CarComment(JSONObject object){
        if (object != null) {
            this.id = object.optLong("id", 0);
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
            carProducts=new ArrayList<>();
            countMap=new HashMap<>();
            try {
                JSONArray array=object.optJSONObject("order").optJSONArray("ordersubs");
                int size=array.length();
                if(size>0) {
                    for (int i = 0; i < size; i++) {
                        CarProduct product=new CarProduct(array.optJSONObject(i).optJSONObject("product"));
                        int quantity=array.optJSONObject(i).optInt("quantity");
                        if(quantity>0&&product.getId()>0){
                            if(countMap.get(product.getId())==null){
                                countMap.put(product.getId(),quantity);
                                carProducts.add(product);
                            }else{
                                countMap.put(product.getId(),countMap.get(product.getId())+quantity);
                            }
                        }
                    }

                    Collections.sort(carProducts, new Comparator<CarProduct>() {
                        @Override
                        public int compare(CarProduct car1, CarProduct car2) {
                            return car1.getType()-car2.getType();
                        }

                    });
                }
            }catch (Exception ignored){

            }

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArrayList<CarProduct> getCarProducts() {
        return carProducts;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public Date getTime() {
        return time;
    }

    public int getCount(long id) {
        return countMap.get(id);
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }
}
