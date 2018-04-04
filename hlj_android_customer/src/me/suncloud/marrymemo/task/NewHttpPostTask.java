/**
 *
 */
package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.entity.ProgressListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

public class NewHttpPostTask extends AsyncTask<Object, Long, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog roundProgressDialog;
    private boolean setString;

    public NewHttpPostTask(Context context,
                           OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    public NewHttpPostTask(Context context,
                           OnHttpRequestListener requestListener, RoundProgressDialog progressDialog) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.roundProgressDialog = progressDialog;
    }

    public NewHttpPostTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog, boolean setString) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.roundProgressDialog = progressDialog;
        this.setString = setString;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String) params[0];
        Map<String, Object> map = null;
        String objectString = null;
        if (params.length > 1) {
            if(params[1] instanceof Map) {
                map = (Map<String, Object>) params[1];
            }else {
                objectString = (String) params[1];
            }
        }
        if (!JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            String jsonStr;
            if ((map == null || map.isEmpty()) && JSONUtil.isEmpty(objectString)) {
                jsonStr = JSONUtil.post(url);
            } else {
                ProgressListener progressListener=new ProgressListener() {
                    @Override
                    public void transferred(long transferedBytes) {
                        publishProgress(transferedBytes);
                    }

                    @Override
                    public void setContentLength(long contentLength) {
                        if (roundProgressDialog != null) {
                            roundProgressDialog.setMax((int) contentLength);
                        }
                    }
                };

                if(map == null || map.isEmpty()){
                    jsonStr = JSONUtil.postJsonWithAttach(url, objectString,
                            progressListener);
                }else {
                    jsonStr = JSONUtil.postFormWithAttach(url, map,
                            progressListener);
                }
            }
            JSONObject json;
            try {
                json = new JSONObject(jsonStr);
                return json;
            } catch (JSONException e) {
                Log.e("HttpPostTask", jsonStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (roundProgressDialog != null) {
            if (!setString) {
                roundProgressDialog.setMessage(context
                        .getString(R.string.msg_submitting));
            }
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if(requestListener!=null) {
            if (obj != null) {
                requestListener.onRequestCompleted(obj);
            } else {
                requestListener.onRequestFailed(obj);
            }
        }
        super.onPostExecute(obj);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if (roundProgressDialog != null) {
            roundProgressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}
