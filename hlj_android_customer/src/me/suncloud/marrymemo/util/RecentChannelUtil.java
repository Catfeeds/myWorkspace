package me.suncloud.marrymemo.util;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;

/**
 * Created by mo_yu on 2018/3/14.最近常逛社区频道
 */

public class RecentChannelUtil {

    public static void setRecentChannelIds(Context context, List<Long> ids) {
        if (!CommonUtil.isCollectionEmpty(ids)) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(Constants
                                .RECENT_CHANNEL_FILE,
                        Context.MODE_PRIVATE));
                out.write(JsonHelper.ToJsonString(ids));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            context.deleteFile(Constants.RECENT_CHANNEL_FILE);
        }
    }

    public static List<Long> getRecentChannelIds(Context context) {
        List<Long> ids = new ArrayList<>();
        if (context.getFileStreamPath(Constants.RECENT_CHANNEL_FILE) != null && context
                .getFileStreamPath(
                Constants.RECENT_CHANNEL_FILE)
                .exists()) {
            try {
                InputStream in = context.openFileInput(Constants.RECENT_CHANNEL_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                if (!JSONUtil.isEmpty(jsonStr)) {
                    ids.addAll(JsonHelper.FromJson(jsonStr,
                            new TypeToken<ArrayList<Long>>() {}.getType(),
                            Long.class));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }
}
