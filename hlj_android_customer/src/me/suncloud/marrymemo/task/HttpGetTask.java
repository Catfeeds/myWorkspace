package me.suncloud.marrymemo.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.util.JSONUtil;

public class HttpGetTask extends AsyncTask<Object, Integer, JSONObject> {
    private OnHttpRequestListener requestListener;

    public HttpGetTask(OnHttpRequestListener requestListener) {
        super();
        this.requestListener = requestListener;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        try {
            String jsonStr=null;
            if(requestListener==null){
                JSONUtil.getEmptyFromUrl(url);
            }else {
                jsonStr = JSONUtil.getStringFromUrl(url);
            }
            if (!JSONUtil.isEmpty(jsonStr)) {
                try {
                    return new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if(requestListener!=null) {
            if (obj != null) {
                requestListener.onRequestCompleted(obj);
            } else {
                requestListener.onRequestFailed(null);
            }
        }
        super.onPostExecute(obj);
    }

}
