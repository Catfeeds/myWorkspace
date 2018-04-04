package me.suncloud.marrymemo.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.entity.ProgressListener;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by Suncloud on 2016/4/14.
 */
public class StatusHttpPatchTask extends AsyncTask<String, Long, JSONObject> {

    private Context context;
    private StatusRequestListener requestListener;
    private RoundProgressDialog progressDialog;
    private boolean b;

    public StatusHttpPatchTask(Context context, StatusRequestListener requestListener) {
        super();
        this.context = context;
        this.requestListener = requestListener;
    }

    public StatusHttpPatchTask(
            Context context,
            StatusRequestListener requestListener,
            RoundProgressDialog progressDialog) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
    }

    public StatusHttpPatchTask(
            Context context,
            StatusRequestListener requestListener,
            RoundProgressDialog progressDialog,
            boolean b) {
        super();
        this.context = context;
        this.requestListener = requestListener;
        this.progressDialog = progressDialog;
        this.b = b;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        String objectString = params[1];
        if (!JSONUtil.isNetworkConnected(context)) {
            return null;
        }
        try {
            ProgressListener progressListener = null;
            if (!JSONUtil.isEmpty(objectString)) {
                progressListener = new ProgressListener() {
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
                };
            }
            String jsonStr = JSONUtil.patchJsonWithAttach(url, objectString, progressListener);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
            } else {
            }
            return new JSONObject(jsonStr);
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

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (requestListener != null) {
            ReturnStatus returnStatus = null;
            if (jsonObject != null && !jsonObject.isNull("status")) {
                returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
            }
            if (returnStatus != null && returnStatus.getRetCode() == 0) {
                requestListener.onRequestCompleted(jsonObject.opt("data"), returnStatus);
            } else {
                requestListener.onRequestFailed(returnStatus, JSONUtil.isNetworkConnected(context));
            }
        }
        super.onPostExecute(jsonObject);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }
}
