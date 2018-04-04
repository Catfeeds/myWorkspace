package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import me.suncloud.marrymemo.Constants;

/**
 * Created by Suncloud on 2016/2/19.
 */
public class DataConfigUtil {

    private static DataConfigUtil INSTANCE;
    private GetDataConfigTask dataConfigTask;

    public static DataConfigUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataConfigUtil();
        }
        return INSTANCE;
    }

    public void executeDataConfigTask(Context context) {
        if (dataConfigTask == null) {
            dataConfigTask = new GetDataConfigTask(context);
            dataConfigTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    private class GetDataConfigTask extends AsyncTask<Object, Object, JSONObject> {

        private Context mContext;

        private GetDataConfigTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath.DATA_CONFIG));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("config");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            dataConfigTask = null;
            if (jsonObject != null) {
                try {
                    Session.getInstance().setDataConfig(mContext, jsonObject);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

}
