package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.PointRecord;
import me.suncloud.marrymemo.task.OnHttpRequestListener;

/**
 * Created by Suncloud on 2016/3/1.
 */
public class PointUtil {

    private static PointUtil INSTANCE;
    private PointRecord pointRecord;
    private OnHttpRequestListener onHttpRequestListener;
    private PointRecordSyncTask pointRecordSyncTask;

    public static PointUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PointUtil();
        }
        return INSTANCE;
    }

    public PointRecord getPointRecord(Context mContext, long userId) {
        if (pointRecord == null || pointRecord.getUserId() != userId) {
            pointRecord=null;
            try {
                String fileName = String.format(Constants.POINT_RECORD_FILE, userId);
                if (mContext.getFileStreamPath(fileName) != null && mContext
                        .getFileStreamPath(fileName).exists()) {
                    InputStream in = mContext.openFileInput(fileName);
                    String jsonStr = JSONUtil.readStreamToString(in);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    pointRecord=new PointRecord(jsonObject);
                    pointRecord.setUserId(userId);
                }
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }
        return pointRecord;
    }


    public void syncPointRecord(Context mContext, long userId, OnHttpRequestListener onHttpRequestListener) {
        this.onHttpRequestListener=onHttpRequestListener;
        if(pointRecordSyncTask==null){
            pointRecordSyncTask=new PointRecordSyncTask(mContext,userId);
            pointRecordSyncTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    public class PointRecordSyncTask extends AsyncTask<Object, Integer, JSONObject> {

        private Context mContext;
        private long userId;

        public PointRecordSyncTask(Context mContext,long userId) {
            this.userId=userId;
            this.mContext = mContext;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath.GET_POINT_RECORD));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject!=null) {
                    OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput
                            (String.format(Constants.POINT_RECORD_FILE, userId), Context.MODE_PRIVATE));
                    out.write(jsonObject.toString());
                    out.close();

                    pointRecord = new PointRecord(jsonObject);
                    RxBus.getDefault().post(new RxEvent(RxEvent.RxEventType.NEW_POINT_RECORD, pointRecord));
                    return jsonObject;
                }
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject!=null){
                PointRecord pointRecord=new PointRecord(jsonObject);
                pointRecord.setUserId(userId);
                if(onHttpRequestListener!=null){
                    onHttpRequestListener.onRequestCompleted(pointRecord);
                }
            }
            pointRecordSyncTask=null;
            super.onPostExecute(jsonObject);
        }
    }


}
