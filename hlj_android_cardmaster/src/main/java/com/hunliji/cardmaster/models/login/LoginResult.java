package com.hunliji.cardmaster.models.login;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;

/**
 * Created by wangtao on 2017/11/24.
 */

public class LoginResult {
    CustomerUser user;

    public CustomerUser getUser() {
        return user;
    }
}
