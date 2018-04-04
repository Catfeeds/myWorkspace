package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MerchantProperty;


public class Properties2Util {

    private static OnFinishedListener onFinishedListener;

    public static ArrayList<MerchantProperty> getPropertiesFromFile(Context mContext) {
        ArrayList<MerchantProperty> properties = new ArrayList<>();
        JSONArray array = null;
        try {
            File file = mContext.getFileStreamPath(Constants.PROPERTIES2_FILE);
            if (file != null && file.exists()) {
                InputStream in = mContext.openFileInput(Constants.PROPERTIES2_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            } else {
                array = (new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.properties2))));
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


    public static class PropertiesSyncTask extends AsyncTask<Object, Integer,
            ArrayList<MerchantProperty>> {

        private Context mContext;

        public PropertiesSyncTask(Context mContext, @Nullable OnFinishedListener listener) {
            this.mContext = mContext;
            onFinishedListener = listener;
        }


        @Override
        protected ArrayList<MerchantProperty> doInBackground(Object... params) {
            ArrayList<MerchantProperty> properties = new ArrayList<>();
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .GET_CATEGORY_URL));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                JSONArray array = null;
                if (jsonObject != null) {
                    array = jsonObject.optJSONArray("list");
                }
                if (array != null && array.length() > 0) {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject();
                        JSONObject resObject = array.getJSONObject(i);
                        MerchantProperty property = new MerchantProperty();
                        long id = resObject.optLong("id", 0);
                        String name = JSONUtil.getString(resObject, "name");
                        //组装新的json
                        object.put("id", id);
                        object.put("name", name);
                        jsonArray.put(i, object);
                        //设置list列表
                        property.setId(id);
                        property.setName(name);
                        properties.add(property);
                    }
                    OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput(
                            Constants.PROPERTIES2_FILE,
                            Context.MODE_PRIVATE));
                    out.write(jsonArray.toString());
                    out.flush();
                    out.close();
                    return properties;
                }
                return null;
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MerchantProperty> properties) {
            if (onFinishedListener != null && properties != null && !properties.isEmpty()) {
                onFinishedListener.onFinish(properties);
            }
            super.onPostExecute(properties);
        }
    }

    public interface OnFinishedListener {
        public void onFinish(ArrayList<MerchantProperty> properties);
    }
}
