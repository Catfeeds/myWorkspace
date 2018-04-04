package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/3/27.
 */
public class SpecialTopic implements Identifiable  {
    private long id;
    private long entryCategoryId;
    private String cover;
    private String title;
    private String dateStr;
    private String categoryName;
    private String shareTitle;
    private String shareDesc;
    private String shareDesc2;
    private String shareUrl;
    private String shareIcon;

    public SpecialTopic(JSONObject jsonObject){
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            entryCategoryId = jsonObject.optLong("entry_category_id", 0);
            cover= JSONUtil.getString(jsonObject, "cover");
            categoryName= JSONUtil.getString(jsonObject,"category_name");
            title= JSONUtil.getString(jsonObject,"title");
            dateStr= JSONUtil.getString(jsonObject,"date_desc");
            shareTitle= JSONUtil.getString(jsonObject,"share_title");
            shareDesc= JSONUtil.getString(jsonObject,"share_desc");
            shareDesc2= JSONUtil.getString(jsonObject,"share_desc2");
            shareUrl= JSONUtil.getString(jsonObject,"share_url");
            shareIcon= JSONUtil.getString(jsonObject,"share_icon");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getEntryCategoryId() {
        return entryCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCover() {
        return cover;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getShareDesc() {
        return shareDesc;
    }

    public String getShareDesc2() {
        return shareDesc2;
    }

    public String getShareIcon() {
        return shareIcon;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public String getShareUrl() {
        return shareUrl;
    }
}
