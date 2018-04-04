package me.suncloud.marrymemo.task;

import android.content.Context;

import org.json.JSONObject;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by werther on 16/2/26.
 * 爱家理财接口专用的post task
 * 由于返回参数格式约定于婚礼纪常用的不同,所以单独使用
 */
public class AijiaStatusHttpPostTask extends StatusHttpPostTask {
    public AijiaStatusHttpPostTask(Context context, StatusRequestListener requestListener) {
        super(context, requestListener);
    }

    public AijiaStatusHttpPostTask(Context context, StatusRequestListener requestListener,
                                   RoundProgressDialog progressDialog) {
        super(context, requestListener, progressDialog);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (requestListener != null) {
            ReturnStatus returnStatus = null;
            if (jsonObject != null) {
                String responseCode = JSONUtil.getString(jsonObject, "responseCode");
                String responseMessage = JSONUtil.getString(jsonObject, "responseMessage");
                returnStatus = new ReturnStatus(Integer.valueOf(responseCode), responseMessage);
            }
            if (returnStatus != null && (returnStatus.getRetCode() == Constants.AijiaResponseCode
                    .SUCCESS || returnStatus.getRetCode() == Constants.AijiaResponseCode
                    .BALANCE_SHORT || returnStatus.getRetCode() == Constants.AijiaResponseCode
                    .PAY_AMOUNT_LOW)) {
                requestListener.onRequestCompleted(jsonObject.optJSONObject("result"),
                        returnStatus);
            } else {
                requestListener.onRequestFailed(returnStatus, JSONUtil.isNetworkConnected(context));
            }
        }
    }
}
