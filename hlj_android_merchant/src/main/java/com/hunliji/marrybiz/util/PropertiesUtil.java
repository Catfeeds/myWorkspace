package com.hunliji.marrybiz.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.MerchantProperty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


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
                    MerchantProperty property = new MerchantProperty(array.optJSONObject(i));
                    if (property.getId() > 0) {
                        properties.add(property);
                    }
                }
            }
        }

        return properties;
    }

    public static class PropertiesSyncTask extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;

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

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(mContext, Constants.getAbsUrl(Constants
                        .HttpPath.NEW_PROPERTIES_URL));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONArray array = new JSONObject(json).optJSONArray("data");
                if(array != null){
                    int size = array.length();
                    if (size > 0) {
                        OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput
                                (Constants.PROPERTIES_FILE, Context.MODE_PRIVATE));
                        out.write(array.toString());
                        out.close();
                        return array;
                    }
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
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        MerchantProperty property = new MerchantProperty(jsonArray.optJSONObject(i));
                        if (property.getId() > 0) {
                            properties.add(property);
                        }
                    }
                }
                onFinishedListener.onFinish(properties);
            }
            super.onPostExecute(jsonArray);
        }
    }

    public interface OnFinishedListener {
        void onFinish(ArrayList<MerchantProperty> properties);
    }


    /**
     * 获取完整的MerchantProperty
     *
     * @param inCompleteProperty
     * @return
     */
    public static MerchantProperty getCompletedProperty(Context context, MerchantProperty inCompleteProperty) {
        MerchantProperty completedProperty = null;
        List<MerchantProperty> properties = getPropertiesFromFile(context);
        for (MerchantProperty property : properties) {
            // 一级分类无法再修改,所以可选项只有这个一级分类下的二级分类
            if (inCompleteProperty.getId().equals(property.getId())) {
                completedProperty = property;
            }
        }

        return completedProperty;
    }
}
