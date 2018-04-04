package me.suncloud.marrymemo.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.login.LoginResult;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.LoginHelper;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;

/**
 * 账密登录activity
 * Created by jinxin on 2016/8/29.
 * 已废弃
 */
@Deprecated
public class PhoneLoginActivity extends BaseLoginActivity {

    @BindView(R.id.tv_account_login)
    TextView tvAccountLogin;
    @BindView(R.id.title_hint)
    TextView titleHint;
    @BindView(R.id.img_phone)
    ImageView imgPhone;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.btn_sms_certify)
    Button btnCertify;
    @BindView(R.id.img_pwd)
    ImageView imgPwd;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.label)
    LinearLayout label;
    @BindView(R.id.label_layout)
    LinearLayout labelLayout;
    @BindView(R.id.qqLogin)
    LinearLayout qqLogin;
    @BindView(R.id.weiboLogin)
    LinearLayout weiboLogin;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.cancel)
    ImageButton cancel;
    @BindView(R.id.back)
    ImageButton back;

    private HljHttpSubscriber loginPwdSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);
        ButterKnife.bind(this);
        progress = findViewById(R.id.progressBar);
        initWidget();
        EventBus.getDefault()
                .register(this);
    }

    public void onEvent(MessageEvent event) {
        if (event != null && event.getType() == MessageEvent.EventType.LOGINCHECK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onFinish() {
        EventBus.getDefault()
                .unregister(this);
        if (loginPwdSubscriber != null && !loginPwdSubscriber.isUnsubscribed()) {
            loginPwdSubscriber.unsubscribe();
        }
        super.onFinish();
    }

    private void initWidget() {
        back.setVisibility(View.VISIBLE);
        tvAccountLogin.setVisibility(View.GONE);
        editPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPwd.setHint(getString(R.string.label_user_password));
        titleHint.setText(getString(R.string.label_phone_login));
        btnCertify.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        editPhone.addTextChangedListener(textWatcher);
        editPwd.addTextChangedListener(textWatcher);
        tvTerm.setText(Html.fromHtml(getString(R.string.label_hlj_terms)));
    }

    @OnClick(R.id.tv_term)
    public void onTerms() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", Constants.USER_PROTOCOL_URL);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.back)
    public void back() {
        this.finish();
    }

    @OnClick(R.id.btn_login)
    public void onLogin() {
        String phone = editPhone.getText()
                .toString();
        if (!Util.isMobileNO(phone)) {
            Toast.makeText(this, R.string.hint_new_number_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String pwd = editPwd.getText()
                .toString();
        if (JSONUtil.isEmpty(pwd)) {
            Toast.makeText(this, R.string.label_user_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Session.getInstance()
                .clearLogout(this);

        loginPwdSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                        .createProgressDialog(
                        this))
                .setOnNextListener(new SubscriberOnNextListener<LoginResult>() {
                    @Override
                    public void onNext(
                            LoginResult result) {
                        if (result != null && result.getUserId() > 0) {
                            //登录成功
                            try {
                                JSONObject json = new JSONObject();
                                json.put("id", String.valueOf(result.getUserId()));
                                json.put("token", result.getToken());
                                Session.getInstance()
                                        .setCurrentUser(PhoneLoginActivity
                                                .this, json);
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PhoneLoginActivity.this,
                                    getString(R.string.msg_login_error),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .build();
        LoginHelper.getInstance()
                .phoneLoginWithPwd(loginPwdSubscriber, phone, pwd);

    }

    private TextWatcher textWatcher = new TextWatcher() {

        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(
                CharSequence s, int start, int before, int count) {
            boolean accountTyped = editPhone.getText()
                    .length() > 0;
            boolean passwordTyped = editPwd.getText()
                    .length() > 0;
            if (accountTyped && passwordTyped) {
                btnLogin.setEnabled(true);
            } else {
                btnLogin.setEnabled(false);
            }
        }

        public void afterTextChanged(Editable s) {

        }
    };
}
