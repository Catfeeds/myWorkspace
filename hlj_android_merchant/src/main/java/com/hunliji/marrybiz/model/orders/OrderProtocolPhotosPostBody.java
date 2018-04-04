package com.hunliji.marrybiz.model.orders;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/5/31.
 */

public class OrderProtocolPhotosPostBody {
    long id;
    @SerializedName("protocol_images")
    ArrayList<OrderProtocolPhoto> photos;

    public OrderProtocolPhotosPostBody(
            long id, ArrayList<OrderProtocolPhoto> photos) {
        this.id = id;
        this.photos = photos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<OrderProtocolPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<OrderProtocolPhoto> photos) {
        this.photos = photos;
    }
}
