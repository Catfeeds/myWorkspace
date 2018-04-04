package com.hunliji.hljhttplibrary.authorization;

import com.hunliji.hljcommonlibrary.models.User;

/**
 * Created by werther on 16/8/8.
 */
public interface UserConverterDelegate {
    public User fromJson(String jsonString);

    public String toJson(User user);
}
