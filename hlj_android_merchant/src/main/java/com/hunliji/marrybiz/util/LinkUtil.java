package com.hunliji.marrybiz.util;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.task.OnHttpRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Suncloud on 2016/1/21.
 */
public class LinkUtil {

    private static LinkUtil INSTANCE;
    private Context context;
    private Map<String, String> links;
    private GetLinksTask getLinksTask;
    private Map<String, OnHttpRequestListener> requestListeners;


    private LinkUtil(Context context) {
        this.context = context;
        getLinksFromFile(context);
    }

    public static LinkUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LinkUtil(context);
        }
        return INSTANCE;
    }

    public void getLink(String key, OnHttpRequestListener requestListener) {
        if (links != null && !links.isEmpty()) {
            if (requestListener == null) {
                return;
            }
            String value = links.get(key);
            if (!JSONUtil.isEmpty(value)) {
                requestListener.onRequestCompleted(value);
                return;
            }
        }
        if (requestListeners == null) {
            requestListeners = new HashMap<>();
        }
        if (requestListener != null) {
            requestListeners.put(key, requestListener);
        }
        if (getLinksTask == null) {
            getLinksTask = new GetLinksTask();
            getLinksTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }

    }


    private void getLinksFromFile(Context mContext) {
        links = new HashMap<>();
        try {
            if (mContext.getFileStreamPath(Constants.LINKS_FILE) != null && mContext
                    .getFileStreamPath(
                    Constants.LINKS_FILE)
                    .exists()) {
                InputStream in = mContext.openFileInput(Constants.LINKS_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                JSONObject jsonObject = new JSONObject(jsonStr);
                if (jsonObject.length() > 0) {
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = JSONUtil.getString(jsonObject, key);
                        if (!JSONUtil.isEmpty(value)) {
                            links.put(key, value);
                        }
                    }
                }
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        if (getLinksTask == null) {
            getLinksTask = new GetLinksTask();
            getLinksTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    private class GetLinksTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String json = JSONUtil.getStringFromUrl(context,
                        Constants.getAbsUrl(Constants.HttpPath.GET_LINKS_URL));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject != null && jsonObject.length() > 0 && context != null) {
                    FileOutputStream fileOutputStream = context.openFileOutput(Constants.LINKS_FILE,
                            Context.MODE_PRIVATE);
                    if (fileOutputStream != null) {
                        OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                        out.write(jsonObject.toString());
                        out.flush();
                        out.close();
                        fileOutputStream.close();
                    }
                }
                return jsonObject;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null && jsonObject.length() > 0) {
                links = new HashMap<>();
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = JSONUtil.getString(jsonObject, key);
                    if (!JSONUtil.isEmpty(value)) {
                        links.put(key, value);
                    }
                }
                if (requestListeners != null && requestListeners.size() > 0) {
                    for (String key : requestListeners.keySet()) {
                        OnHttpRequestListener listener = requestListeners.get(key);
                        String link = links.get(key);
                        if (JSONUtil.isEmpty(link)) {
                            listener.onRequestCompleted(null);
                        } else {
                            listener.onRequestCompleted(link);
                        }
                    }
                }
            } else if (requestListeners != null && requestListeners.size() > 0) {
                for (OnHttpRequestListener requestListener : requestListeners.values()) {
                    requestListener.onRequestFailed(null);
                }
            }
            if (requestListeners != null) {
                requestListeners.clear();
            }
            getLinksTask = null;
            super.onPostExecute(jsonObject);
        }
    }

}
