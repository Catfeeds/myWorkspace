package com.hunliji.hljquestionanswer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.PostReportIdBody;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by mo_yu on 2016/8/17.举报通用类
 * 类型 thread:话题 post:回帖 question:问题 answer:回答 comment 评论
 */
public class ReportActivity extends HljBaseActivity {

    @BindView(R2.id.cb_1)
    CheckableLinearButton cb1;
    @BindView(R2.id.cb_2)
    CheckableLinearButton cb2;
    @BindView(R2.id.cb_3)
    CheckableLinearButton cb3;
    @BindView(R2.id.cb_4)
    CheckableLinearButton cb4;
    @BindView(R2.id.cb_5)
    CheckableLinearButton cb5;
    @BindView(R2.id.cb_6)
    CheckableLinearButton cb6;
    @BindView(R2.id.cb_group)
    CheckableLinearGroup cbGroup;
    @BindView(R2.id.btn_submit)
    Button btnSubmit;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private long id; // 话题id
    private String kind; // 举报类型
    private String message;

    private HljHttpSubscriber reportSubscriber;//举报

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_question___qa);
        ButterKnife.bind(this);

        initValue();
        initView();
    }

    private void initValue() {
        id = getIntent().getLongExtra("id", 0);
        kind = getIntent().getStringExtra("kind");
        if (kind.equals(HljQuestionAnswer.REPORT_QUESTION) || kind.equals(HljQuestionAnswer
                .REPORT_ANSWER) || kind.equals(
                HljQuestionAnswer.REPORT_COMMENT)) {
            cb5.setVisibility(View.GONE);
        }
    }

    private void initView() {
        // 默认选中
        message = "垃圾广告";
        cbGroup.setOnCheckedChangeListener(new CheckableLinearGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                if (checkedId == R.id.cb_1) {
                    message = "垃圾广告";
                } else if (checkedId == R.id.cb_2) {
                    message = "侮辱诋毁";
                } else if (checkedId == R.id.cb_3) {
                    message = "淫秽色情";
                } else if (checkedId == R.id.cb_4) {
                    message = "盗版侵权";
                } else if (checkedId == R.id.cb_5) {
                    message = "不符合频道主题";
                } else if (checkedId == R.id.cb_6) {
                    message = "其他";
                }
            }
        });
    }


    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        if (AuthUtil.loginBindCheck(this)) {
            PostReportIdBody body = new PostReportIdBody();
            body.setId(id);
            body.setKind(kind);
            body.setMessage(message);
            Observable<HljHttpResult<Object>> observable = QuestionAnswerApi.postReportObb(body);
            reportSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<Object>>() {
                        @Override
                        public void onNext(HljHttpResult<Object> o) {
                            if (o.getStatus()
                                    .getRetCode() != 0) {
                                Toast.makeText(ReportActivity.this,
                                        o.getStatus()
                                                .getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(ReportActivity.this,
                                        R.string.msg_success_to_report___cm,
                                        Toast.LENGTH_SHORT)
                                        .show();
                                onBackPressed();
                            }
                        }
                    })
                    .build();
            observable.subscribe(reportSubscriber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (reportSubscriber != null && !reportSubscriber.isUnsubscribed()) {
            reportSubscriber.unsubscribe();
        }
    }
}
