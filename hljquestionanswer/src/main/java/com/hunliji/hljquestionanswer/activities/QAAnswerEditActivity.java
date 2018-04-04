package com.hunliji.hljquestionanswer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QAAnswerEditActivity extends HljBaseNoBarActivity {

    @BindView(R2.id.btn_back)
    Button btnBack;
    @BindView(R2.id.btn_post)
    Button btnPost;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.et_content)
    EditText etContent;

    public static final String ARG_ANSWER = "answer";
    public static final String ARG_QUESTION_ID = "question_id";

    private Answer answer;
    private long questionId;
    private HljHttpSubscriber editSub;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qaanswer_edit);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();

        initValues();
        initViews();
    }

    private void initValues() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        answer = getIntent().getParcelableExtra(ARG_ANSWER);
        questionId = getIntent().getLongExtra(ARG_QUESTION_ID, 0);
    }

    private void initViews() {
        if (answer != null) {
            etContent.setText(answer.getContent());
        }
    }

    @Override
    public void onBackPressed() {
        if (etContent.getText()
                .length() > 0) {
            DialogUtil.createDoubleButtonDialog(QAAnswerEditActivity.this,
                    getString(R.string.msg_edit_back___qa),
                    getString(R.string.label_give_up___cm),
                    getString(R.string.label_continue___cm),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            QAAnswerEditActivity.super.onBackPressed();
                        }
                    },
                    null)
                    .show();
            return;
        }

        onExit();
    }

    private void onExit() {
        super.onBackPressed();
        //隐藏软键盘
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R2.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_post)
    void onPost() {
        if (etContent.length() == 0) {
            ToastUtil.showToast(this, "回答不能为空哦~", 0);
            return;
        }
        String content = etContent.getText()
                .toString()
                .trim();
        if (answer != null) {
            // 编辑答案
            onEditAnswer(content);
        }
    }

    private void onEditAnswer(String content) {
        PostAnswerBody body = new PostAnswerBody();
        if (answer != null) {
            body.setAnswerId(answer.getId());
        }
        body.setQuestionId(questionId);
        body.setContent(content);
        CommonUtil.unSubscribeSubs(editSub);
        editSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<BasePostResult>>() {
                    @Override
                    public void onNext(HljHttpResult<BasePostResult> result) {
                        //发布成功
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        onExit();
                    }
                })
                .build();
        QuestionAnswerApi.postAnswerObb(body, QuestionAnswerApi.TYPE_MERCHANT_QA)
                .subscribe(editSub);
    }

}
