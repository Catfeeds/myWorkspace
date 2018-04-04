package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

public class City implements Identifiable {

    private long cid;
    private String name;
    @SerializedName("pinyin")
    private String pinying;
    @SerializedName("short_py")
    private String short_py;
    private transient String letter;

    public static final transient long ID_QUANGUO = 0; //全国

    public City() {
    }

    public City(long cid, String name) {
        this.cid = cid;
        this.name = name;
    }

    public City(JSONObject json) {
        if(json!=null) {
            this.cid = json.optLong("cid",0);
            this.name = JSONUtil.getString(json, "name");
            this.pinying = JSONUtil.getString(json, "pinyin");
            this.short_py = JSONUtil.getString(json, "short_py");
        }
    }

    public Long getId() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPingying() {
        return pinying;
    }

    public void setPinYing(String pinying) {
        this.pinying = pinying;
    }

    public String getShortPy() {
        return short_py;
    }

    public void setLetter(String letter) {
        if(!JSONUtil.isEmpty(letter)){
            this.letter = letter.toUpperCase();
        }else {
            this.letter = letter;
        }
    }

    public long getLetterId() {
        return JSONUtil.isEmpty(letter)?0l:letter.charAt(0);
    }

    public String getLetter() {
        return letter;
    }
}