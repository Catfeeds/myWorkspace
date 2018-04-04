package com.hunliji.hljcommonlibrary.models.realm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.User;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Suncloud on 2016/10/11.
 */

public class WSChatAuthor extends RealmObject {

    @PrimaryKey
    @SerializedName(value = "id", alternate = "user_id")
    private long id;
    private String nick;
    private String avatar;
    @SerializedName("remark_name")
    private String remarkName;
    private WSCity city;
    private ExtendMember extend;

    public WSChatAuthor() {
    }

    public WSChatAuthor(User user) {
        id = user.getId();
        nick = user.getNick();
        avatar = user.getAvatar();
        extend = user.getExtend();
    }

    public ExtendMember getExtend() {
        return extend;
    }

    public WSChatAuthor(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public WSCity getCity() {
        return city;
    }

    public void setCity(WSCity city) {
        this.city = city;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (TextUtils.isEmpty(remarkName)) {
            return nick;
        }
        return remarkName;
    }

    public String getNick() {
        return nick;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getCityName() {
        if (city == null) {
            return null;
        }
        return city.getName();
    }

    public boolean realmEquals(WSChatAuthor author) {
        try {
            if (!getRemarkName().equals(author.getRemarkName())) {
                return false;
            }
            if (getCity().getId() != author.getCity()
                    .getId()) {
                return false;
            }
            if (!getCity().getName()
                    .equals(author.getCity()
                            .getName())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
