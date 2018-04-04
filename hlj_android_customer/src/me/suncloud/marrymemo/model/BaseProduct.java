package me.suncloud.marrymemo.model;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/8/3.
 */
public class BaseProduct implements Identifiable {

    protected long id;
    protected String photo;
    protected boolean like;
    protected int repliesCount;
    protected int likeCount;
    protected String title;
    protected int width;
    protected int height;
    protected double price;
    protected String subjectDesc;

    @Override
    public Long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getPhoto() {
        return JSONUtil.isEmpty(photo) ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getTitle() {
        return title;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getSubjectDesc() {
        return subjectDesc;
    }
}
