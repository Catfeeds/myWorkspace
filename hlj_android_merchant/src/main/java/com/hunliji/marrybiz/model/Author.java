package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Date;


/**
 * Created by Suncloud on 2014/11/4.
 */
public class Author implements Identifiable {

    private long id;
    private String name;
    private String avatar;
    private boolean isMerchant;

    public Author(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            name = JSONUtil.getString(json, "nick");
            avatar = JSONUtil.getString(json, "avatar");
            isMerchant = json.optBoolean( "is_merchant",false );
            if (!isMerchant){
                isMerchant = json.optInt( "is_merchant",0 )>0;
            }
        }
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getAvatar(int size) {
        return JSONUtil.getAvatar(avatar,size);
    }
    public String getAvatar() {
        return avatar == null ? null : (avatar.startsWith("http") ? (avatar.startsWith( Constants
                .QINIU_HOST) ? avatar.endsWith(".avatar") ? avatar : avatar + ".avatar" : avatar)
                : Constants.HOST + avatar);
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setMerchant(boolean merchant) {
        isMerchant = merchant;
    }
}
