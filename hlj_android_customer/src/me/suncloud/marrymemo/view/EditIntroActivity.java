package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class EditIntroActivity extends HljBaseActivity {
    @BindView(R.id.et_intro)
    EditText etIntro;
    @BindView(R.id.tv_text_counter)
    TextView tvCounter;
    private User user;
    private Dialog progressDialog;
    private InputMethodManager imm;
    private static final int MAX = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_intro);
        ButterKnife.bind(this);

        setOkText(R.string.label_save);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        user = Session.getInstance()
                .getCurrentUser(this);
        etIntro.addTextChangedListener(new TextCountWatcher(etIntro, tvCounter, MAX));
        etIntro.setText(user.getDescription());
    }

    @Override
    public void onBackPressed() {
        imm.hideSoftInputFromWindow(etIntro.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        super.onBackPressed();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (etIntro.length() <= 0) {
            Toast.makeText(this, R.string.msg_empty_intro, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if ((MAX - Util.getTextLength(etIntro.getText())) < 0) {
            Toast.makeText(this, getString(R.string.msg_intro_overlong, MAX), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }

        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("description",
                etIntro.getText()
                        .toString());
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject resultObj = (JSONObject) obj;
                if (resultObj != null && resultObj.optJSONObject("status") != null && resultObj
                        .optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    Toast.makeText(EditIntroActivity.this,
                            getString(R.string.msg_success_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .editCurrentUser(EditIntroActivity.this, dataObj);

                    Intent intent = getIntent();
                    intent.putExtra("refresh", true);
                    setResult(RESULT_OK, intent);
                } else {
                    Toast.makeText(EditIntroActivity.this,
                            getString(R.string.msg_fail_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                }

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                onBackPressed();
            }

            @Override
            public void onRequestFailed(Object obj) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditIntroActivity.this, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE), data);
    }
}
