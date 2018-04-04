package me.suncloud.marrymemo.model.car;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.Date;
import java.util.List;

/**
 * 婚车评论model婚车主页
 * Created by wangtao on 2017/4/20.
 */

public class CarComment {

    private long id;
    @SerializedName("created_at")
    private Date time;
    private String content;
    private Author user;
    private List<Photo> photos;

    public Date getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public Author getUser() {
        return user;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
