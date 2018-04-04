package com.hunliji.marrybiz.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.entity.ProgressListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QiNiuUploadTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog progressDialog;

    public QiNiuUploadTask(Context context, OnHttpRequestListener requestListener,
                           RoundProgressDialog progressDialog) {
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        try {
            String url = (String) params[0];
            String tokenStr = JSONUtil.getStringFromUrl(context, url);
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
                String jsonStr = JSONUtil.postMultipartWithAttach(context, Constants
                        .QINIU_UPLOAD_URL, data, new ProgressListener() {

                            @Override
                            public void transferred(int transferedBytes) {
                                publishProgress(transferedBytes);
                            }

                            @Override
                            public void setContentLength(long contentLength) {
                                if (progressDialog != null) {
                                    progressDialog.setMax((int) contentLength);
                                }
                            }
                        }, this.context.getContentResolver(),false, null);
                return new JSONObject(jsonStr);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        super.onPostExecute(obj);

        if (obj != null) {
            requestListener.onRequestCompleted(obj);
        } else {
            requestListener.onRequestFailed(obj);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

}