package com.hunliji.cardmaster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.CommonApi;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 编辑昵称
 * Created by jinxin on 2017/11/28 0028.
 */

public class EditNickNameActivity extends HljBaseActivity {

    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.tv_text_counter)
    TextView tvTextCounter;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private CustomerUser user;
    private InputMethodManager imm;
    private static final int MAX = 14;
    private HljHttpSubscriber modifySub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick_name);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
    }

    private void initConstant() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        user = (CustomerUser) UserSession.getInstance()
                .getUser(this);
    }

    private void initWidget() {
        setOkText(R.string.label_save);
        etNick.addTextChangedListener(new TextCountWatcher(etNick, tvTextCounter, MAX));
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
        super.onBackPressed();
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(modifySub);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (etNick.length() <= 0) {
            Toast.makeText(this, R.string.msg_empty_nick, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if ((MAX - CommonUtil.getTextLength(etNick.getText())) < 0) {
            Toast.makeText(this,
                    getString(R.string.msg_nick_overlong, String.valueOf(MAX)),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        CommonUtil.unSubscribeSubs(modifySub);
        modifySub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<CustomerUser>>() {
                    @Override
                    public void onNext(HljHttpResult<CustomerUser> hljHttpResult) {
                        setHttpResult(hljHttpResult);
                    }
                })
                .build();
        CommonApi.modifyUserNick(etNick.getText()
                .toString())
                .subscribe(modifySub);
    }

    private void setHttpResult(HljHttpResult<CustomerUser> hljHttpResult) {
        if (hljHttpResult != null && hljHttpResult.getStatus()
                .getRetCode() == 0) {
            try {
                Toast.makeText(EditNickNameActivity.this,
                        getString(R.string.msg_success_to_complete_profile),
                        Toast.LENGTH_SHORT)
                        .show();
                CustomerUser user = hljHttpResult.getData();
                UserSession.getInstance()
                        .setUser(this, user);
                Intent intent = getIntent();
                intent.putExtra("refresh", true);
                setResult(RESULT_OK, intent);
                onBackPressed();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.msg_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
