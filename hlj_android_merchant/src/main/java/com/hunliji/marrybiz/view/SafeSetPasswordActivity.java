package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.widget.ClearableEditText;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class SafeSetPasswordActivity extends HljBaseActivity {

    private ClearableEditText passwordView;
    private Button okBtn;
    private RoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        passwordView = (ClearableEditText) findViewById(R.id.password);
        passwordView.addTextChangedListener(textWatcher);
        okBtn = (Button) findViewById(R.id.ok_btn);
        okBtn.setEnabled(false);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean passwordTyped = passwordView.getText()
                    .length() > 0;
            if (passwordTyped) {
                okBtn.setEnabled(true);
            } else {
                okBtn.setEnabled(false);
            }
        }

        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void onOk(View view) {
        if (passwordView.getText()
                .length() < 6 || passwordView.getText()
                .length() > 16) {
            Toast.makeText(this, R.string.hint_user_password, Toast.LENGTH_SHORT)
                    .show();
        } else {
            try {
                JSONObject map = new JSONObject();
                map.put("password",
                        passwordView.getText()
                                .toString());
                if (progressDialog == null) {
                    progressDialog = JSONUtil.getRoundProgress(SafeSetPasswordActivity.this);
                }
                progressDialog.show();
                new NewHttpPostTask(SafeSetPasswordActivity.this,
                        new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                final JSONObject jsonObject = (JSONObject) obj;
                                if (jsonObject != null && jsonObject.optJSONObject("status") !=
                                        null) {
                                    Status status = new Status(jsonObject.optJSONObject("status"));
                                    if (status.getRetCode() == 0 && jsonObject.optJSONObject
                                            ("data") != null) {
                                        final String token = JSONUtil.getString(jsonObject
                                                .optJSONObject(
                                                "data"), "token");
                                        if (JSONUtil.isEmpty(token)) {
                                            Toast.makeText(SafeSetPasswordActivity.this,
                                                    R.string.msg_error,
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            progressDialog.dismiss();
                                        }
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.onLoadComplate();
                                            progressDialog.setCancelable(false);
                                            progressDialog.onComplate();
                                            progressDialog.setOnUpLoadComplate(new RoundProgressDialog.OnUpLoadComplate() {

                                                @Override
                                                public void onUpLoadCompleted() {
                                                    Session.getInstance()
                                                            .setToken(SafeSetPasswordActivity.this,
                                                                    token);
                                                    onBackPressed();
                                                }
                                            });
                                        } else {
                                            Session.getInstance()
                                                    .setToken(SafeSetPasswordActivity.this, token);
                                            onBackPressed();
                                        }
                                    } else {
                                        Toast.makeText(SafeSetPasswordActivity.this,
                                                JSONUtil.isEmpty(status.getErrorMsg()) ?
                                                        getString(R.string.msg_error) : status
                                                        .getErrorMsg(),
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        progressDialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(SafeSetPasswordActivity.this,
                                            R.string.msg_error,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onRequestFailed(Object obj) {
                                Toast.makeText(SafeSetPasswordActivity.this,
                                        R.string.msg_error,
                                        Toast.LENGTH_SHORT)
                                        .show();
                                progressDialog.dismiss();
                            }
                        },
                        progressDialog,
                        true).execute(Constants.getAbsUrl(Constants.HttpPath.EDIT_PASSWORD_URL),
                        map.toString());
            } catch (JSONException e) {

            }

        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

}
