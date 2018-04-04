package me.suncloud.marrymemo.model;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.Member;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import me.suncloud.marrymemo.model.wrappers.SerializableMember;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2014/11/4.
 */
public class Author implements Serializable {

    private long id;
    private String name;
    private String avatar;
    private String nick;
    private Date weddingDay;
    private int is_pending;
    private String img;
    private String specialty;
    private int gender;
    private SerializableMember member;

    public Author(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            if (!json.isNull("name"))
                name = JSONUtil.getString(json, "name");
            if (!json.isNull("nick"))
                name = JSONUtil.getString(json, "nick");
            avatar = JSONUtil.getString(json, "avatar");
            if (JSONUtil.isEmpty(avatar)) {
                avatar = JSONUtil.getString(json, "img");
            }
            nick = JSONUtil.getString(json, "nick");
            img = JSONUtil.getString(json, "img");
            specialty = JSONUtil.getString(json, "specialty");
            weddingDay = JSONUtil.getDate(json, "weddingday");
            is_pending = json.optInt("is_pending", 0);
            gender = json.optInt("gender", 0);
            if (!json.isNull("member") && !TextUtils.isEmpty(json.optString("member"))) {
                this.member = new SerializableMember(json.optJSONObject("member"));
            }
        }
    }

    public SerializableMember getMember() {
        return member;
    }

    public Author(){
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Date getWeddingDay() {
        return weddingDay;
    }

    public void setWeddingDay(Date weddingDay) {
        this.weddingDay = weddingDay;
    }

    public int getIs_pending() {
        return is_pending;
    }

    public void setIs_pending(int is_pending) {
        this.is_pending = is_pending;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

}
