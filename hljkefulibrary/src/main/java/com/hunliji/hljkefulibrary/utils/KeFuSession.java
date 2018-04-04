package com.hunliji.hljkefulibrary.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljkefulibrary.moudles.HxUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by wangtao on 2017/10/20.
 */

public class KeFuSession {
    private HxUser user;

    private static class SingleHolder {
        private static final KeFuSession INSTANCE = new KeFuSession();
    }

    public static final KeFuSession getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void saveUser(Context context,HxUser user, String hxUserJson) throws IOException {
        this.user=user;
        FileOutputStream fileOutputStream = context.openFileOutput(HljCommon.FileNames.HX_USER_FILE,
                Context.MODE_PRIVATE);

        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            out.write(hxUserJson);
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
    public HxUser getUser(@Nullable Context context) {
        if (user == null && context != null) {
            if (context.getFileStreamPath(HljCommon.FileNames.HX_USER_FILE) != null && context
                    .getFileStreamPath(
                    HljCommon.FileNames.HX_USER_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(HljCommon.FileNames.HX_USER_FILE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    user = GsonUtil.getGsonInstance()
                            .fromJson(stream.toString(), HxUser.class);
                    stream.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return user;
    }

    /**
     * 注销子模块中的用户信息,退出登录时调用
     *
     * @param context
     */
    public void logout(Context context) {
        context.deleteFile(HljCommon.FileNames.HX_USER_FILE);
        this.user = null;
    }
}
