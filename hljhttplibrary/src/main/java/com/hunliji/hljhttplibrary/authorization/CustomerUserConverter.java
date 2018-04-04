package com.hunliji.hljhttplibrary.authorization;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by werther on 16/8/9.
 */
public class CustomerUserConverter implements UserConverterDelegate{
    @Override
    public User fromJson(String jsonString) {
        try {
            return GsonUtil.getGsonInstance().fromJson(jsonString, CustomerUser.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toJson(User user) {
        return new Gson().toJson(user);
    }
}
