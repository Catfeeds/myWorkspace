/**
 *
 */
package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Date;


/**
 * @author iDay
 */
public class User implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = 4723889163553502490L;

    private long id;
    private String nick;
    private String avatar;

    public User(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id");
            this.nick = JSONUtil.getString(jsonObject, "nick");
            this.avatar = JSONUtil.getString(jsonObject, "avatar");
            if (!jsonObject.isNull("img") && JSONUtil.isEmpty(avatar)) {
                this.avatar = JSONUtil.getString(jsonObject, "img");
            }
        }
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * @param nick the nick to set
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return avatar == null ? null : (avatar.startsWith("http") ? (avatar.startsWith(Constants
                .QINIU_HOST) ? avatar.endsWith(".avatar") ? avatar : avatar + ".avatar" : avatar)
                : Constants.HOST + avatar);
    }

    public String getAvatar(int size) {
        return JSONUtil.getAvatar(avatar,size);
    }

    public String getAvatar2() {
        return JSONUtil.isEmpty(avatar) ? null : avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
