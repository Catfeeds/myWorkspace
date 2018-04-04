package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ColorMenuItem;
import me.suncloud.marrymemo.model.MenuItem;

/**
 * Created by werther on 15/11/13.
 * 专门用于color labels的同步和读取
 */
public class ColorLabelsUtil {

    private static OnFinishedListener onFinishedListener;

    /**
     * 从本地存储的文件中读取properties列表
     *
     * @param mContext
     * @return
     */
    public static ArrayList<MenuItem> getColorLabelsFromFile(Context mContext) {
        ArrayList<MenuItem> colors = new ArrayList<>();
        JSONArray array = null;
        try {
            if (mContext.getFileStreamPath(Constants.COLOR_LABELS_FILE) != null && mContext
                    .getFileStreamPath(Constants.COLOR_LABELS_FILE).exists()) {
                InputStream in = mContext.openFileInput(Constants.COLOR_LABELS_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            } else {
                array = (new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.color_labels))));
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        if (array != null) {
            int size = array.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    ColorMenuItem label = new ColorMenuItem(array.optJSONObject(i));
                    if (label.getId() > 0) {
                        colors.add(label);
                    }
                }
            }
        }

        return colors;
    }

    public static class ColorLabelsSyncTask extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;

        /**
         * 同步properties
         *
         * @param mContext
         * @param listener 同步完成之后的回调监听
         */
        public ColorLabelsSyncTask(Context mContext, @Nullable OnFinishedListener listener) {
            this.mContext = mContext;
            onFinishedListener = listener;
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath.COLOR_LABELS_SYNC_URL));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONArray array = new JSONObject(json).optJSONObject("data").optJSONArray("list");
                int size = array.length();
                if (size > 0) {
                    OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput
                            (Constants.COLOR_LABELS_FILE, Context.MODE_PRIVATE));
                    out.write(array.toString());
                    out.close();
                    return array;
                }

                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (onFinishedListener != null) {
                ArrayList<MenuItem> colors = new ArrayList<>();
                if (jsonArray != null && jsonArray.length() > 0 && colors.isEmpty()) {
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        ColorMenuItem label = new ColorMenuItem(jsonArray.optJSONObject(i));
                        if (label.getId() > 0) {
                            colors.add(label);
                        }
                    }
                }
                onFinishedListener.onFinish(colors);
            }
            super.onPostExecute(jsonArray);
        }
    }

    public interface OnFinishedListener {
        public void onFinish(ArrayList<MenuItem> menuItems);
    }
}
