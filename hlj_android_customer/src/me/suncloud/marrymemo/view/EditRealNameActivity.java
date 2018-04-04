package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

public class EditRealNameActivity extends HljBaseActivity {

    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.tv_text_counter)
    TextView tvCounter;
    private User user;
    private Dialog progressDialog;
    private InputMethodManager imm;
    private static final int MAX = 15;//15个字数限制

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick_name);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setOkText(R.string.label_save);
        user = Session.getInstance()
                .getCurrentUser(this);
        etNick.setText(user.getRealName());
        if (!JSONUtil.isEmpty(user.getRealName())) {
            etNick.setSelection(user.getRealName()
                    .length());
        }
        tvCounter.setText(String.valueOf(MAX - Util.getTextLength(user.getRealName())));
        etNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = Util.getTextLength(etNick.getText());
                tvCounter.setText(String.valueOf(MAX - length));
                if ((MAX - length) > 0) {
                    tvCounter.setTextColor(getResources().getColor(R.color.colorBlack3));
                } else {
                    tvCounter.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        super.onBackPressed();
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (etNick.length() <= 0) {
            Toast.makeText(this, R.string.hint_real_name, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if ((MAX - Util.getTextLength(etNick.getText())) < 0) {
            Toast.makeText(this, getString(R.string.msg_name_overlong, MAX), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }

        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("name",
                etNick.getText()
                        .toString());
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject resultObj = (JSONObject) obj;
                if (resultObj != null && resultObj.optJSONObject("status") != null && resultObj
                        .optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .editCurrentUser(EditRealNameActivity.this, dataObj);
                    Intent intent = getIntent();
                    intent.putExtra("refresh", true);
                    setResult(RESULT_OK, intent);
                } else {
                    Toast.makeText(EditRealNameActivity.this,
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
                progressDialog.dismiss();
                Toast.makeText(EditRealNameActivity.this, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE), data);

    }
}
