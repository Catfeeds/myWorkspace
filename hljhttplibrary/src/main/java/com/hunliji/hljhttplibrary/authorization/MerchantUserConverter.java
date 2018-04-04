package com.hunliji.hljhttplibrary.authorization;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;

/**
 * Created by werther on 16/8/9.
 */
public class MerchantUserConverter implements UserConverterDelegate {
    @Override
    public User fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, MerchantUser.class);
    }

    @Override
    public String toJson(User user) {
        return new Gson().toJson(user);
    }
}
