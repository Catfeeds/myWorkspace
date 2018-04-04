package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 回复的基类
 * Created by mo_yu on 2017/8/21.
 */
public abstract class BaseReplyActivity extends Activity {

    @BindView(R2.id.root_layout)
    LinearLayout rootLayout;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.tv_send)
    TextView tvSend;
    private InputMethodManager imm;
    private int maxLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_card___card);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        maxLength = getIntent().getIntExtra("max_length", 50);
        initView();
    }

    private void initView() {
        setOnTextWatcher();
        etContent.requestFocus();
        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    private void setOnTextWatcher() {
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > maxLength) {
                    ToastUtil.showToast(BaseReplyActivity.this, "", 0);
                }
            }
        });
    }

    @OnClick(R2.id.tv_send)
    void onSend() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String message = etContent.getText()
                .toString();
        if (TextUtils.isEmpty(message)) {
            ToastUtil.showToast(this, "你发送的内容不能为空", 0);
            return;
        }
        onReply(message);
    }

    public abstract void onReply(String message);
}
