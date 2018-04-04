package com.hunliji.marrybiz.task;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.marrybiz.util.JSONUtil;

public class HttpGetTask extends AsyncTask<Object, Integer, JSONObject> {
    private Context context;
	private OnHttpRequestListener requestListener;

	public HttpGetTask(Context context, OnHttpRequestListener requestListener) {
		super();
        this.context = context;
		this.requestListener = requestListener;
	}

	@Override
	protected JSONObject doInBackground(Object... params) {
		String url = (String) params[0];
		try {
			String jsonStr;
			jsonStr = JSONUtil.getStringFromUrl(context, url);
			if (!JSONUtil.isEmpty(jsonStr)) {
				try {
					JSONObject json = new JSONObject(jsonStr);
					return json;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				JSONObject json = new JSONObject();
				return json;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

}
