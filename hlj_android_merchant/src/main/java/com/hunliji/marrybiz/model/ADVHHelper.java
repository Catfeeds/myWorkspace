package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by Suncloud on 2015/12/21.
 */
public class ADVHHelper implements Identifiable {

    private long id;
    private long userId;
    private String realName;
    private String phone;
    private String nick;
    private String avatar;
    private LinkedHashMap<String, String> contacts;

    public ADVHHelper(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            userId = jsonObject.optLong("userid");
            nick = JSONUtil.getString(jsonObject, "nick");
            avatar = JSONUtil.getString(jsonObject, "avatar");
            realName = JSONUtil.getString(jsonObject, "real_name");
            phone = JSONUtil.getString(jsonObject, "phone");
            JSONObject contactJson = jsonObject.optJSONObject("contact");
            if (contactJson != null && contactJson.length() > 0) {
                contacts = new LinkedHashMap<>();
                Iterator<String> iterator = contactJson.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = JSONUtil.getString(contactJson, key);
                    if (!JSONUtil.isEmpty(value)) {
                        contacts.put(key, value);
                    }
                }
            }
        }

    }

    @Override
    public Long getId() {
        return id;
    }

    public LinkedHashMap<String, String> getContacts() {
        return contacts;
    }

    public long getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getRealName() {
        return realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNick() {
        return nick;
    }
}
