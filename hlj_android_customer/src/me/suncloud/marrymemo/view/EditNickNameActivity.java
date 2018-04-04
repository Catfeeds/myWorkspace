package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class EditNickNameActivity extends HljBaseActivity {

    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.tv_text_counter)
    TextView tvCounter;
    private User user;
    private Dialog progressDialog;
    private InputMethodManager imm;
    private static final int MAX = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick_name);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setOkText(R.string.label_save);
        user = Session.getInstance()
                .getCurrentUser(this);

        user = Session.getInstance()
                .getCurrentUser(this);
        etNick.addTextChangedListener(new TextCountWatcher(etNick, tvCounter, MAX));
        //昵称以手机用户开头，表示为未设置昵称
        if (TextUtils.isEmpty(user.getNick()) || user.getNick()
                .startsWith("手机用户")) {
            etNick.setText("");
        } else {
            etNick.setText(user.getNick());
        }

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
            Toast.makeText(this, R.string.msg_empty_nick, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if ((MAX - Util.getTextLength(etNick.getText())) < 0) {
            Toast.makeText(this, getString(R.string.msg_nick_overlong, MAX), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }

        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("nick",
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
                    Toast.makeText(EditNickNameActivity.this,
                            getString(R.string.msg_success_to_complete_profile),
                            Toast.LENGTH_SHORT)
                            .show();
                    JSONObject dataObj = resultObj.optJSONObject("data");
                    Session.getInstance()
                            .editCurrentUser(EditNickNameActivity.this, dataObj);

                    Intent intent = getIntent();
                    intent.putExtra("refresh", true);
                    setResult(RESULT_OK, intent);
                } else {
                    Toast.makeText(EditNickNameActivity.this,
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
                Toast.makeText(EditNickNameActivity.this, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.COMPLETE_USER_PROFILE), data);

    }
}
