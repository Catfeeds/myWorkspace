package com.hunliji.marrybiz.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Privilege;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.TextCountWatcher;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/1/19.
 */
public class PrivilegeContentEditActivity extends HljBaseActivity {

    private EditText editText;

    private Privilege privilege;
    private int limitCount;
    private RoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        privilege = getIntent().getParcelableExtra("privilege");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privilege_content_edit);
        int exampleId = 0;
        int hintId = 0;
        if (privilege.getId() == 1) {
            limitCount = 15;
            exampleId = R.string.privilege_content_example1;
            hintId = R.string.privilege_content_hint1;
            findViewById(R.id.hint).setVisibility(View.VISIBLE);
        } else if (privilege.getId() == 4) {
            limitCount = 20;
            hintId = R.string.privilege_content_hint2;
            exampleId = R.string.privilege_content_example2;
        }
        setTitle(getString(R.string.title_activity_privilege_edit, privilege.getName()));
        TextView title = (TextView) findViewById(R.id.title);
        TextView limit = (TextView) findViewById(R.id.limit);
        editText = (EditText) findViewById(R.id.edit);
        editText.setHint(hintId);
        if (limitCount > 0) {
            limit.setText(getString(R.string.privilege_content_limit, limitCount));
            editText.addTextChangedListener(new TextCountWatcher(editText, limitCount));
        }
        title.setText(privilege.getName());
        editText.setText(privilege.getContent());
        if (exampleId != 0) {
            findViewById(R.id.example_layout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.example)).setText(exampleId);
        }

    }

    public void onSave(View view) {
        if (privilege == null) {
            return;
        }
        String text = editText.getText()
                .toString();
        if (JSONUtil.isEmpty(text) || JSONUtil.isEmpty(text.trim())) {
            Util.showToast(this, null, R.string.hint_privilege_content_empty);
            return;
        }
        if (Util.containsEmoji(text)) {
            Util.showToast(this, null, R.string.hint_privilege_content_err);
            return;
        }
        try {
            String url;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grade_privilege_id", privilege.getId());
            jsonObject.put("content", text);
            privilege.setContent(text);
            if (privilege.getStatus() > 0) {
                url = Constants.getAbsUrl(Constants.HttpPath.PRIVILEGE_EDIT);
                jsonObject.put("status", 1);
            } else {
                url = Constants.getAbsUrl(Constants.HttpPath.PRIVILEGE_ADD);
            }
            if (progressDialog == null) {
                progressDialog = JSONUtil.getRoundProgress(this);
            } else {
                progressDialog.show();
            }
            progressDialog.setCancelable(false);
            progressDialog.onLoadComplate();
            new NewHttpPostTask(this, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    if (isFinishing()) {
                        return;
                    }
                    JSONObject jsonObject = (JSONObject) obj;
                    Status status = new Status(jsonObject.optJSONObject("status"));
                    if (status.getRetCode() == 0) {
                        privilege.setStatus(3);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.setCancelable(false);
                            progressDialog.onComplate();
                            progressDialog.setOnUpLoadComplate(new RoundProgressDialog
                                    .OnUpLoadComplate() {

                                @Override
                                public void onUpLoadCompleted() {
                                    EventBus.getDefault()
                                            .post(new MessageEvent(6, privilege));
                                    onBackPressed();
                                }
                            });
                        } else {
                            EventBus.getDefault()
                                    .post(new MessageEvent(6, privilege));
                            onBackPressed();
                        }
                        return;
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Util.showToast(PrivilegeContentEditActivity.this,
                                status.getErrorMsg(),
                                R.string.msg_failed_finish_order);
                    }

                }

                @Override
                public void onRequestFailed(Object obj) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Util.showToast(PrivilegeContentEditActivity.this,
                                null,
                                R.string.msg_failed_finish_order);
                    }

                }
            }, progressDialog).executeOnExecutor(Constants.INFOTHEADPOOL,
                    url,
                    jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
