package com.hunliji.hljquestionanswer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.SameQuestionAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.QARxEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangtao on 2016/12/20.
 */

public class CreateQuestionTitleActivity extends HljBaseNoBarActivity {
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.tv_title_limit)
    TextView tvTitleLimit;
    @BindView(R2.id.rc_question_list)
    RecyclerView rcQuestionList;

    private Subscription titleSubscription;
    private Subscription lookForSubscription;
    private Subscription rxSubscription;

    private String content;
    private Question question;
    private long markId;


    private final int TITLE_LIMIT = 50;
    private final int CONTENT_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        question = getIntent().getParcelableExtra("question");
        markId = getIntent().getLongExtra("markId", 0);
        content = question == null ? null : question.getContent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_title___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        setSwipeBackEnable(false);
        initView();
    }


    private void initView() {
        etTitle.setText(question == null ? null : question.getTitle());
        tvTitleLimit.setText(CommonUtil.fromHtml(this,
                getString(R.string.html_fmt_question_title_limit___qa),
                TITLE_LIMIT - etTitle.length()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        rcQuestionList.setLayoutManager(layoutManager);

        //监听标题变化
        titleSubscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tvTitleLimit.setText(CommonUtil.fromHtml(CreateQuestionTitleActivity.this,
                                getString(R.string.html_fmt_question_title_limit___qa,
                                        TITLE_LIMIT - CommonUtil.getTextLength(s))));
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(s.toString());
                        }
                    }
                });
            }
        })
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        lookForQuestion(s);
                    }
                });
    }

    /**
     * 匹配相同问题
     *
     * @param keyword 问题标题
     */
    private void lookForQuestion(String keyword) {
        CommonUtil.unSubscribeSubs(lookForSubscription);

        if (TextUtils.isEmpty(keyword.trim()) && rcQuestionList.getAdapter() != null) {
            ((SameQuestionAdapter) rcQuestionList.getAdapter()).clear();
            return;
        }
        lookForSubscription = QuestionAnswerApi.getSameQuestions(keyword, 10, 1)
                .subscribe(new Subscriber<HljHttpData<List<Question>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (rcQuestionList.getAdapter() != null) {
                            ((SameQuestionAdapter) rcQuestionList.getAdapter()).clear();
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(HljHttpData<List<Question>> listHljHttpData) {
                        rcQuestionList.scrollToPosition(0);
                        SameQuestionAdapter adapter = (SameQuestionAdapter) rcQuestionList
                                .getAdapter();
                        if (adapter == null) {
                            adapter = new SameQuestionAdapter(CreateQuestionTitleActivity
                                    .this);
                            rcQuestionList.setAdapter(adapter);
                        }
                        adapter.setQuestions(listHljHttpData.getData());
                    }
                });
    }

    @Override
    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        if (etTitle.getText()
                .length() > 0 || !TextUtils.isEmpty(content)) {
            DialogUtil.createDoubleButtonDialog(CreateQuestionTitleActivity.this,
                    getString(question == null ? R.string.msg_question_create_back___qa : R
                            .string.msg_edit_back___qa),
                    getString(R.string.label_give_up___cm),
                    getString(R.string.label_continue___cm),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CreateQuestionTitleActivity.super.onBackPressed();
                        }
                    },
                    null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R2.id.btn_next)
    public void onNext() {
        String title = etTitle.getText()
                .toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(title.trim())) {
            ToastUtil.showToast(this, null, R.string.msg_question_title_empty___qa);
            return;
        }
        if (title.length() < 7 || title.length() > 50) {
            ToastUtil.showToast(this, null, R.string.msg_question_title_limit___qa);
            return;
        }
        Intent intent = new Intent(this, CreateQuestionContentActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("question", question);
        intent.putExtra("markId", markId);
        startActivityForResult(intent, CONTENT_EDIT);
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(titleSubscription, lookForSubscription, rxSubscription);
        super.onFinish();
    }

    @Override
    protected void onResume() {
        if (rxSubscription == null || rxSubscription.isUnsubscribed()) {
            rxSubscription = RxBus.getDefault()
                    .toObservable(QARxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<QARxEvent>() {
                        @Override
                        protected void onEvent(QARxEvent qaRxEvent) {
                            switch (qaRxEvent.getType()) {
                                case QUESTION_POST_DONE:
                                    //提问完成，接受事件关闭当前页
                                    setResult(RESULT_OK);
                                    finish();
                                    break;
                            }
                        }
                    });
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTENT_EDIT:
                    if (data != null) {
                        content = data.getStringExtra("content");
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
