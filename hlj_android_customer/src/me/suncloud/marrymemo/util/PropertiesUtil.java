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
import me.suncloud.marrymemo.model.MerchantProperty;

/**
 * Created by werther on 15/11/13.
 * 专门用于properties的同步和读取
 */
public class PropertiesUtil {

    private static OnFinishedListener onFinishedListener;

    /**
     * 从本地存储的文件中读取properties列表
     *
     * @param mContext
     * @return
     */
    public static ArrayList<MerchantProperty> getPropertiesFromFile(Context mContext) {
        ArrayList<MerchantProperty> properties = new ArrayList<>();
        JSONArray array = null;
        try {
            if (mContext.getFileStreamPath(Constants.PROPERTIES_FILE) != null && mContext
                    .getFileStreamPath(Constants.PROPERTIES_FILE).exists()) {
                InputStream in = mContext.openFileInput(Constants.PROPERTIES_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            } else {
                array = (new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.properties))));
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        if (array != null) {
            int size = array.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    MerchantProperty menuItem = new MerchantProperty(array.optJSONObject(i));
                    if (menuItem.getId() > 0) {
                        properties.add(menuItem);
                    }
                }
            }
        }

        return properties;
    }


    public static ArrayList<MerchantProperty> getServerPropertiesFromFile(Context mContext) {
        ArrayList<MerchantProperty> properties = getPropertiesFromFile(mContext);
        for (MerchantProperty property : properties) {
            if (property.getId() == 13) {
                properties.remove(property);
                break;
            }
        }
        return properties;
    }

    public static class PropertiesSyncTask extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;
        private int type;

        /**
         * 同步properties
         *
         * @param mContext
         * @param listener 同步完成之后的回调监听
         */
        public PropertiesSyncTask(Context mContext, @Nullable OnFinishedListener listener) {
            this.mContext = mContext;
            onFinishedListener = listener;
        }

        /**
         * 同步properties
         *
         * @param type 0 全部； 1移除酒店
         */
        public PropertiesSyncTask(int type, Context mContext, @Nullable OnFinishedListener
                listener) {
            this(mContext, listener);
            this.type = type;
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .GET_MERCHANT_FILTER, 0));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                JSONArray array = null;
                if (jsonObject != null) {
                    array = jsonObject.optJSONArray("properties");
                }
                if (array != null && array.length() > 0) {
                    OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput
                            (Constants.PROPERTIES_FILE, Context.MODE_PRIVATE));
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
                ArrayList<MerchantProperty> properties = new ArrayList<>();
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                        MerchantProperty menuItem = new MerchantProperty(jsonArray.optJSONObject
                                (i));
                        if ((menuItem.getId() > 0 && type == 0) || (menuItem.getId() != 13 &&
                                type == 1)) {
                            properties.add(menuItem);
                        }
                    }
                }
                onFinishedListener.onFinish(properties);
            }
            super.onPostExecute(jsonArray);
        }
    }

    public interface OnFinishedListener {
        public void onFinish(ArrayList<MerchantProperty> properties);
    }
}
