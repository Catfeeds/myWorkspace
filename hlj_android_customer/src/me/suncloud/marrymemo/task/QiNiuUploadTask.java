package me.suncloud.marrymemo.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.entity.ProgressListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

public class QiNiuUploadTask extends AsyncTask<Object, Long, JSONObject> {
    private Context context;
    private boolean uncompress;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog progressDialog;

    public QiNiuUploadTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog) {
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }


    public QiNiuUploadTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog,boolean uncompress) {
        this.context = context;
        this.uncompress=uncompress;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }


    @Override
    protected JSONObject doInBackground(Object... params) {
        try {
            String url = (String) params[0];
            String tokenStr = JSONUtil.getStringFromUrl(url);
            if (JSONUtil.isEmpty(tokenStr)) {
                return null;
            }
            JSONObject object = new JSONObject(tokenStr);
            String token = JSONUtil.getString(object, "uptoken");
            if (JSONUtil.isEmpty(token)) {
                token = JSONUtil.getString(object, "token");
            }
            if (!JSONUtil.isEmpty(token)) {
                File file = (File) params[1];
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("file", file);
                String jsonStr = JSONUtil.postMultipartWithAttach(Constants.QINIU_UPLOAD_URL, data,
                        new ProgressListener() {

                            @Override
                            public void transferred(long transferedBytes) {
                                publishProgress(transferedBytes);
                            }

                            @Override
                            public void setContentLength(long contentLength) {
                                if (progressDialog != null) {
                                    progressDialog.setMax((int) contentLength);
                                }
                            }
                        }, this.context.getContentResolver(),uncompress);
                return new JSONObject(jsonStr);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        if (obj != null) {
            requestListener.onRequestCompleted(obj);
        } else {
            requestListener.onRequestFailed(obj);
        }
        super.onPostExecute(obj);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}