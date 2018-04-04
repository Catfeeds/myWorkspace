package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ExpressInfo;
import me.suncloud.marrymemo.model.ExpressMethod;
import me.suncloud.marrymemo.model.Label;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

public class ChangeExpressInfoActivity extends HljBaseActivity {

    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.select_express_layout)
    LinearLayout selectExpressLayout;
    @BindView(R.id.et_express_no)
    EditText etExpressNo;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Label selectedExpress;
    private ArrayList<Label> expresses;
    private Dialog selectExpressDialog;
    private long subOrderId;
    private String expressNo;
    private ExpressInfo expressInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_express_info);
        ButterKnife.bind(this);

        subOrderId = getIntent().getLongExtra("sub_order_id", 0);
        expressInfo = (ExpressInfo) getIntent().getSerializableExtra("express_info");

        expresses = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        new GetExpressTask().executeOnExecutor(Constants.INFOTHEADPOOL);
    }

    @OnClick(R.id.select_express_layout)
    void showSelectExpress() {
        selectExpressDialog = DialogUtil.createSingleWheelPickerDialog(ChangeExpressInfoActivity
                .this, expresses, new DialogUtil.onWheelSelectedListener() {
            @Override
            public void selected(Label label) {
                selectedExpress = label;
                if (tvExpressName != null) {
                    tvExpressName.setText(selectedExpress.getName());
                }
            }
        });
        selectExpressDialog.show();
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (selectedExpress == null) {
            Toast.makeText(this, R.string.msg_empty_express, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etExpressNo.length() <= 0) {
            Toast.makeText(this, R.string.msg_empty_shipping_no, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String trackingNo = etExpressNo.getText()
                .toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_sub_id", subOrderId);
            jsonObject.put("type_code", selectedExpress.getKeyWord());
            jsonObject.put("tracking_no", trackingNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpPostTask(ChangeExpressInfoActivity.this, new StatusRequestListener() {

            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                // 提交物流信息成功,刷新页面
                Toast.makeText(ChangeExpressInfoActivity.this,
                        R.string.msg_success_to_submit_express,
                        Toast.LENGTH_SHORT)
                        .show();
                Intent intent = getIntent();
                intent.putExtra("refresh", true);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(ChangeExpressInfoActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_submit_express,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_EXPRESS_INFO_EDIT),
                jsonObject.toString());
    }

    private class GetExpressTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_EXPRESS_METHODS);
            try {
                String json = JSONUtil.getStringFromUrl(url);

                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                progressBar.setVisibility(View.GONE);
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    // 显示待选数据
                    expresses.clear();
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray != null && jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ExpressMethod expressMethod = new ExpressMethod(jsonArray.optJSONObject(
                                    i));
                            expresses.add(expressMethod);
                            if (expressInfo != null && expressInfo.getTypeName()
                                    .equals(expressMethod.getName())) {
                                selectedExpress = expressMethod;
                                tvExpressName.setText(selectedExpress.getName());
                                etExpressNo.setText(expressInfo.getTrackingNo());
                            }
                        }
                    }
                } else {
                    Toast.makeText(ChangeExpressInfoActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
}
