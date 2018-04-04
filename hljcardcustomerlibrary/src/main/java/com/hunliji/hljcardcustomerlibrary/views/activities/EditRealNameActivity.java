package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2018/1/22.修改真实姓名
 */

public class EditRealNameActivity extends HljBaseActivity {

    @BindView(R2.id.et_nick)
    EditText etNick;
    @BindView(R2.id.tv_text_counter)
    TextView tvCounter;
    private User user;
    private InputMethodManager imm;
    private static final int MAX = 15;//15个字数限制
    private HljHttpSubscriber submitSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_real_name___card);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setOkText(R.string.label_save___cm);
        user = UserSession.getInstance()
                .getUser(this);
        etNick.setText(user.getRealName());
        if (!CommonUtil.isEmpty(user.getRealName())) {
            etNick.setSelection(user.getRealName()
                    .length());
        }
        tvCounter.setText(String.valueOf(MAX - CommonUtil.getTextLength(user.getRealName())));
        etNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = CommonUtil.getTextLength(etNick.getText());
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

        if ((MAX - CommonUtil.getTextLength(etNick.getText())) < 0) {
            Toast.makeText(this, getString(R.string.msg_name_overlong, MAX), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        CommonUtil.unSubscribeSubs(submitSubscriber);
        submitSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CustomerUser>() {
                    @Override
                    public void onNext(CustomerUser user) {
                        try {
                            UserSession.getInstance()
                                    .setUser(EditRealNameActivity.this, user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        Map<String, Object> map = new HashMap<>();
        map.put("name",
                etNick.getText()
                        .toString());
        CustomerCardApi.editMyBaseInfoObb(map)
                .subscribe(submitSubscriber);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSubscriber);
    }
}
