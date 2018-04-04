package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

public class Music implements Identifiable {

    private static final long serialVersionUID = 6367804500940626476L;
    private long id;
    private int kind;
    private String audioPath;
    private String title;
    private String coverPath;
    private String persistentId;
    private boolean destroy;
    private boolean selected;
    private String m3u8Path;
    private Date createdAt;
    private boolean isNew;
    private boolean hot;

    public Music(JSONObject json) {
        if (json != null) {
            createdAt=JSONUtil.getDateFromFormatLong(json,"created_at",true);
            audioPath = JSONUtil.getString(json, "audio_path");
            m3u8Path = JSONUtil.getString(json, "m3u8_path");
            if(JSONUtil.isEmpty(audioPath)){
                audioPath = JSONUtil.getString(json, "path");
            }
            coverPath = JSONUtil.getString(json, "cover");
            title = JSONUtil.getString(json, "name");
            id = json.optLong("id", 0);
            kind = json.optInt("kind", 0);
            selected=json.optBoolean("selected", false);
            destroy=json.optBoolean("_destroy",false);
            if(!selected){
                selected=json.optInt("selected")>0;
            }
            persistentId=JSONUtil.getString(json,"persistent_id");
            isNew=createdAt!=null&&System.currentTimeMillis()-createdAt.getTime()<7*24*60*60*1000;
            hot=JSONUtil.getBoolean(json,"hot");
        }
    }

    public String getAudioPath() {
        return JSONUtil.isEmpty(audioPath) ? "" : audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getTitle() {
        return JSONUtil.isEmpty(title) ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getKind() {
        return kind;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public String getM3u8Path() {
        return m3u8Path;
    }

    public void setM3u8Path(String m3u8Path) {
        this.m3u8Path = m3u8Path;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isNew(Date date) {
        return isNew&&createdAt!=null&&createdAt.after(date);
    }

    public boolean isHot() {
        return hot;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
}
