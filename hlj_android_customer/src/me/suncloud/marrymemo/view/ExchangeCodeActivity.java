package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.HttpGetTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * 兑换优惠码activity
 * Created by jinxin on 2016/4/12.
 */
public class ExchangeCodeActivity extends HljBaseActivity {
    private EditText codeEt;
    private View progressBar;
    private int totalCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_code);
        codeEt = (EditText) findViewById(R.id.code);
        progressBar = findViewById(R.id.progressBar);
    }

    public void onExchange(View view) {
        final String codeStr = codeEt.getText()
                .toString();
        if (JSONUtil.isEmpty(codeStr)) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        new HttpGetTask(new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject = (JSONObject) obj;
                ReturnStatus returnStatus = null;
                if (jsonObject != null && !jsonObject.isNull("status")) {
                    returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                }
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    new GetRedPacketCountTask().execute();
                    Util.showToast(R.string.msg_exchange_success, ExchangeCodeActivity.this);
                    codeEt.setText("");
                } else if (returnStatus != null && !JSONUtil.isEmpty(returnStatus.getErrorMsg())) {
                    Toast.makeText(ExchangeCodeActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(ExchangeCodeActivity.this,
                            R.string.msg_exchange_code_error,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExchangeCodeActivity.this,
                        R.string.msg_exchange_code_error,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(String.format(Constants.HttpPath.EXCHANGE_CODES, codeStr)));
    }

    private class GetRedPacketCountTask extends AsyncTask<Object, Object, Integer> {

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .GET_NEW_RED_PACKET_COUNT));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optInt("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (!isFinishing()) {
                progressBar.setVisibility(View.GONE);
                totalCount = integer;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, getIntent().putExtra("totalCount", totalCount));
        super.onBackPressed();
    }

}
