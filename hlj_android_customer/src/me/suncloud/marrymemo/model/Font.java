package me.suncloud.marrymemo.model;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by moyu on 2016/4/29.
 */
public class Font implements Identifiable {

    /**
     * id : 1
     * logo : http://7u2nhk.com3.z0.glb.qiniucdn.com/%E5%B0%81%E9%9D%A2.jpg
     * url : http://www.baidu.com
     * size : 1024.00
     * deleted : 0
     * created_at : 2016-04-25 13:42:33
     * updated_at : 2016-04-25 13:42:34
     */

    private long id;
    private String logo;
    private String url;
    private double size;
    private String deleted;
    private Date created_at;
    private Date updated_at;

    //标志位
    private boolean isDownloading;
    private int value;
    private boolean unSaved;
    private boolean isCheck;
    private File filePath;
    private String name;

    public Font(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.logo = JSONUtil.getString(jsonObject, "logo");
            this.url = JSONUtil.getString(jsonObject, "url");
            this.deleted = JSONUtil.getString(jsonObject, "deleted");
            this.name = JSONUtil.getString(jsonObject, "name");
            this.created_at = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            this.updated_at = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            this.id = jsonObject.optLong("id", 0);
            this.size = jsonObject.optDouble("size", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isUnSaved() {
        return unSaved;
    }

    public void setUnSaved(boolean unSaved) {
        this.unSaved = unSaved;
    }

    public void setUnSaved(Context context) {
        if (!JSONUtil.isEmpty(url)) {
            File file = FileUtil.createFontFile(context, getUrl());
            if (file == null || !file.exists()||file.length()==0) {
                this.unSaved = true;
                return;
            }
        }
        this.unSaved = false;
    }

    public boolean isUnSaved(Context context) {
        if (!JSONUtil.isEmpty(url)) {
            File file = FileUtil.createFontFile(context, getUrl());
            if (file == null || !file.exists()||file.length()==0) {
                return true;
            }
        }
        return false;
    }
    public File getFilePath() {
        return filePath;
    }

    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getName() {
        return name;
    }
}
