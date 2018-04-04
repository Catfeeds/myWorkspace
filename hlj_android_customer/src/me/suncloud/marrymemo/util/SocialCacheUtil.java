/**
 *
 */
package me.suncloud.marrymemo.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import me.suncloud.marrymemo.Constants;

/**
 * Created by mo_yu on 2016/6/15.缓存社区发现和关注数据
 */
public class SocialCacheUtil {

    private SocialCacheUtil() {
    }

    public static SocialCacheUtil getInstance() {
        return SocialHolder.INSTANCE;
    }

    //获取社区发现数据
    public String getSocialHot(Context context) {
        try {
            InputStream in = context.openFileInput(Constants.SOCIAL_HOT);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            return stream.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    //缓存社区发现数据
    public void setSocialHot(Context context, String jsonObject) {
        if (context == null) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(Constants.SOCIAL_HOT,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(jsonObject);
                out.flush();
                out.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取社区关注feed流第一页数据
    public String getSocialFollow(Context context) {
        try {
            InputStream in = context.openFileInput(Constants.SOCIAL_FOLLOW);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            return stream.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    //缓存社区关注feed流第一页数据
    public void setSocialFollow(Context context, String jsonObject) {
        if (context == null) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(Constants.SOCIAL_FOLLOW,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(jsonObject);
                out.flush();
                out.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class SocialHolder {
        private static final SocialCacheUtil INSTANCE = new SocialCacheUtil();
    }

}
