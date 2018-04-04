package com.hunliji.marrybiz.task;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.entity.ProgressListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Suncloud on 2015/12/14.
 */
public class NewHttpPutTask extends AsyncTask<String, Integer, JSONObject> {
    private Context context;
    private OnHttpRequestListener requestListener;
    private RoundProgressDialog progressDialog;
    private boolean b;

    /**
     * @param context
     */
    public NewHttpPutTask(Context context, OnHttpRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    /**
     * @param context
     * @param requestListener
     * @param progressDialog
     */
    public NewHttpPutTask(Context context, OnHttpRequestListener requestListener,
                          RoundProgressDialog progressDialog) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }

    public NewHttpPutTask(Context context, OnHttpRequestListener requestListener,
                          RoundProgressDialog progressDialog, boolean b) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject doInBackground(String... params) {
        String url = (String) params[0];
        String objectString = params[1];
        if (!JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            String jsonStr;
            ProgressListener progressListener = null;
            if (!JSONUtil.isEmpty(objectString)) {
                progressListener = new ProgressListener() {
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
                };
            }
            jsonStr = JSONUtil.putJsonWithAttach(context, url, objectString, progressListener, null);
            if (!JSONUtil.isEmpty(jsonStr)) {
                return new JSONObject(jsonStr);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog != null && !b) {
            progressDialog.setMessage(context.getString(R.string.msg_submitting));
        }
        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (requestListener != null) {
            if (jsonObject != null) {
                requestListener.onRequestCompleted(jsonObject);
            } else {
                requestListener.onRequestFailed(null);
            }
        }
        super.onPostExecute(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }
}