package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/7/11.话题专用的富文本
 */
public class ThreadPages implements Identifiable{

    private Long id;
    private String title;
    private String content;
    private String imgPath;
    private ShareInfo shareInfo;
    private String subTitle;
    private int joinCount;

    public ThreadPages(JSONObject json) {
        if(json!=null) {
            this.id = json.optLong("cid",0);
            this.title = JSONUtil.getString(json, "title");
            this.content = JSONUtil.getString(json, "content");
            this.imgPath = JSONUtil.getString(json, "img_path");
            this.subTitle = JSONUtil.getString(json, "sub_title");
            this.joinCount = json.optInt("join_count",0);
            ShareInfo share = new ShareInfo(json.optJSONObject("share"));
            if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                shareInfo = share;
            }
        }
    }

    @Override
    public Long getId() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }
}
