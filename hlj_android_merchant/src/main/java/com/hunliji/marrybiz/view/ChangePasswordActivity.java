package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.widget.ClearableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends HljBaseActivity {

    @BindView(R.id.et_old_psw)
    ClearableEditText etOldPsw;
    @BindView(R.id.et_new_psw)
    ClearableEditText etNewPsw;
    @BindView(R.id.et_re_new_psw)
    ClearableEditText etReNewPsw;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btnSubmit.setEnabled(etOldPsw.length() > 0 && etNewPsw.length() > 0 && etReNewPsw
                    .length() > 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        etReNewPsw.addTextChangedListener(textWatcher);
        etOldPsw.addTextChangedListener(textWatcher);
        etNewPsw.addTextChangedListener(textWatcher);
    }

    @OnClick(R.id.btn_submit)
    void onSubmint() {
        if (TextUtils.isEmpty(etOldPsw.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_empty_old_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(etNewPsw.getText()
                .toString()) || TextUtils.isEmpty(etReNewPsw.getText()
                .toString())) {
            Toast.makeText(this, R.string.msg_empty_new_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etNewPsw.length() > Constants.MAX_PSW_LENGTH || etNewPsw.length() < Constants
                .MIN_PSW_LENGTH) {
            Toast.makeText(ChangePasswordActivity.this,
                    R.string.msg_invalid_password,
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!etNewPsw.getText()
                .toString()
                .equals(etReNewPsw.getText()
                        .toString())) {
            Toast.makeText(ChangePasswordActivity.this,
                    R.string.msg_wrong_second_password,
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("original_password",
                    etOldPsw.getText()
                            .toString());
            jsonObject.put("password",
                    etNewPsw.getText()
                            .toString());
            progressBar.setVisibility(View.VISIBLE);
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    final JSONObject resultObject = (JSONObject) obj;
                    if (resultObject != null) {
                        final ReturnStatus returnStatus = new ReturnStatus(resultObject
                                .optJSONObject(
                                "status"));
                        if (returnStatus.getRetCode() == 0) {
                            JSONObject dataObj = resultObject.optJSONObject("data");
                            String newToken = JSONUtil.getString(dataObj, "token");
                            if (dataObj != null && !JSONUtil.isEmpty(newToken)) {
                                Session.getInstance()
                                        .setToken(ChangePasswordActivity.this, newToken);
                                Toast.makeText(ChangePasswordActivity.this,
                                        R.string.msg_succeed_change_password,
                                        Toast.LENGTH_SHORT)
                                        .show();
                                ChangePasswordActivity.super.onBackPressed();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        R.string.msg_fail_to_change_password,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    returnStatus.getErrorMsg(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this,
                                R.string.msg_fail_to_change_password,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePasswordActivity.this,
                            R.string.msg_fail_to_change_password,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.CHANGE_PASSWORD),
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
