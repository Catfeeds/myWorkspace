package com.hunliji.hljhttplibrary.authorization;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by werther on 16/7/23.
 * 用于管理用户登录管理
 * 登入和登出记录,用户信息持久化等
 */
public class UserSession {
    private User user;
    private static UserConverterDelegate userConverterDelegate;

    private static class SingleHolder {
        private static final UserSession INSTANCE = new UserSession();
    }

    public static final UserSession getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 在当前User会话实例中设置当前用户
     * 并将User信息存储在文件中,作为已登录的记录
     *
     * @param context
     * @param user
     * @throws IOException
     */
    public void setUser(Context context, User user) throws IOException {
        this.user = user;
        // 任何重新设置用户信息都会该表网络请求的登陆校验信息,所以需要重新生成网络请求client
        HljHttp.rebuildRetrofit();
        saveUser(context, user);
    }

    private void saveUser(Context context, User user) throws IOException {
        FileOutputStream fileOutputStream = context.openFileOutput(HljCommon.FileNames.USER_FILE,
                Context.MODE_PRIVATE);

        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            Gson gson = GsonUtil.getGsonInstance();
            out.write(gson.toJson(user));
            out.flush();
            out.close();
            fileOutputStream.close();
        }
    }

    /**
     * 获取当前已登录的用户信息,如果该单例实例中并没有值的话,尝试从已存储的文件中读取,如果两者都没有话说明没有登录
     *
     * @param context
     * @return
     * @throws IOException
     */
    public User getUser(@Nullable Context context) {
        if (user == null&&context!=null) {
            if(context.getFileStreamPath(HljCommon.FileNames.USER_FILE) != null && context.getFileStreamPath(HljCommon.FileNames.USER_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(HljCommon.FileNames.USER_FILE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }

                    if (userConverterDelegate != null) {
                        user = userConverterDelegate.fromJson(stream.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return user;
    }

    /**
     * 注册用户信息的转换方法(从文件到model)
     *
     * @param delegate
     * @return
     */
    public UserSession registerConverter(UserConverterDelegate delegate) {
        UserSession.userConverterDelegate = delegate;
        return SingleHolder.INSTANCE;
    }

    /**
     * 注销子模块中的用户信息,退出登录时调用
     *
     * @param context
     */
    public void logout(Context context) {
        context.deleteFile(HljCommon.FileNames.USER_FILE);
        this.user = null;
        //清除缓存
        HljHttp.clearCache(context);
        // 注销用户后各种登陆校验信息应该同时被注销,需要重新生成网络请求client
        HljHttp.rebuildRetrofit();
    }
}
