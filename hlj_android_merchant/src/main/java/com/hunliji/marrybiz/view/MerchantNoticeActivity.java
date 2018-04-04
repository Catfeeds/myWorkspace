package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.merchant.MerchantNotice;
import com.hunliji.marrybiz.model.merchant.MerchantNoticePostBody;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TextCountWatcher;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * 店铺公告activity
 * Created by jinxin on 2017/2/6 0006.
 */

public class MerchantNoticeActivity extends HljBaseActivity {

    @BindView(R.id.tv_review)
    TextView tvReview;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.li_review)
    LinearLayout liReview;
    @BindView(R.id.edit_content)
    EditText editContent;
    @BindView(R.id.tv_counter)
    TextView tvCounter;
    @BindView(R.id.li_content)
    LinearLayout liContent;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.li_option)
    LinearLayout liOption;
    @BindView(R.id.li_notice)
    LinearLayout liNotice;
    @BindView(R.id.tv_notice_hint)
    TextView tvNoticeHint;
    @BindView(R.id.tv_set)
    TextView tvSet;
    @BindView(R.id.progressBar)
    View progressBar;

    private HljHttpSubscriber getSubscriber;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber checkSubscriber;
    private HljHttpSubscriber editSubscriber;
    private Dialog deleteDialog;
    private TextCountWatcher watcher;
    private MerchantUser merchantUser;
    private int maxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_notice);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        maxHeight = Math.round(dm.density * 150);
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
        watcher = new TextCountWatcher(editContent, tvCounter, 500);
        if (getSubscriber == null || getSubscriber.isUnsubscribed()) {
            getSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<MerchantNotice>() {
                        @Override
                        public void onNext(MerchantNotice merchantNotice) {
                            if (merchantNotice != null) {
                                setTitle("店铺公告");
                                liReview.setVisibility(View.VISIBLE);
                                liOption.setVisibility(View.VISIBLE);
                                editContent.setText(merchantNotice.getContent());
                                editContent.setEnabled(false);
                                tvReview.setText(getStatus(merchantNotice.getStatus()));
                                tvDate.setText(merchantNotice.getUpdatedAt()
                                        .toString("yyyy-MM-dd HH:mm:ss"));
                                tvSet.setVisibility(View.GONE);
                                setEditContentHeight(false);
                            } else {
                                setTitle("设置公告");
                                liReview.setVisibility(View.GONE);
                                liOption.setVisibility(View.GONE);
                                editContent.addTextChangedListener(watcher);
                                tvCounter.setVisibility(View.VISIBLE);
                                editContent.setText(null);
                                editContent.setEnabled(true);
                                tvSet.setVisibility(View.VISIBLE);
                                setEditContentHeight(true);
                            }
                        }
                    })
                    .setDataNullable(true)
                    .build();
        }
        Observable<MerchantNotice> noticeObservable = MerchantApi.getMerchantNotice();
        noticeObservable.subscribe(getSubscriber);
    }

    private void setEditContentHeight(boolean max) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editContent
                .getLayoutParams();
        if (params == null) {
            return;
        }
        if (max) {
            params.height = maxHeight;
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        editContent.requestLayout();
    }

    public String getStatus(int status) {
        switch (status) {
            case 0:
                return getString(R.string.label_reviewing);
            case 1:
                return getString(R.string.label_review_pass);
            case 2:
                return getString(R.string.label_review_failed);
            default:
                return null;
        }
    }

    @OnClick(R.id.tv_edit)
    public void onEdit(View view) {
        liReview.setVisibility(View.GONE);
        liOption.setVisibility(View.GONE);
        tvSet.setVisibility(View.VISIBLE);
        editContent.addTextChangedListener(watcher);
        tvCounter.setVisibility(View.VISIBLE);
        editContent.setEnabled(true);
        setEditContentHeight(true);
    }

    @OnClick(R.id.tv_delete)
    public void onDelete(View view) {
        showDeleteDialog();
    }

    @OnClick(R.id.tv_set)
    public void onSet() {

        final String content = editContent.getText()
                .toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "商家简介不能为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (checkSubscriber == null || checkSubscriber.isUnsubscribed()) {
            checkSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    //可以编辑添加
                                    onSubmit(content);
                                } else {
                                    Toast.makeText(MerchantNoticeActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();
        }
        Observable<HljHttpResult> checkObservable = MerchantApi.checkMerchantNotice();
        checkObservable.subscribe(checkSubscriber);
    }

    private void onSubmit(String content) {
        if (editSubscriber == null || editSubscriber.isUnsubscribed()) {
            editSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    liReview.setVisibility(View.VISIBLE);
                                    tvReview.setText("审核中");
                                    DateTime now = new DateTime(Calendar.getInstance()
                                            .getTime());
                                    tvDate.setText(now.toString("yyyy-MM-dd HH:mm:ss"));
                                    tvSet.setVisibility(View.GONE);
                                    liOption.setVisibility(View.VISIBLE);
                                    editContent.removeTextChangedListener(watcher);
                                    tvCounter.setVisibility(View.GONE);
                                    editContent.setEnabled(false);
                                    setEditContentHeight(false);
                                    Toast.makeText(MerchantNoticeActivity.this,
                                            getString(R.string.label_opu_take_ok),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(MerchantNoticeActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();
        }
        MerchantNoticePostBody body = new MerchantNoticePostBody();
        body.setContent(content);
        Observable<HljHttpResult> editObservable = MerchantApi.editMerchantNotice(body);
        editObservable.subscribe(editSubscriber);
    }

    private void showDeleteDialog() {
        if (deleteDialog == null) {
            deleteDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.hint_are_you_sure_to_delete),
                    getString(R.string.action_ok),
                    getString(R.string.action_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete();
                        }
                    },
                    null);
        }
        if (deleteDialog.isShowing()) {
            return;
        }
        deleteDialog.show();
    }

    private void delete() {
        if (deleteSubscriber == null || deleteSubscriber.isUnsubscribed()) {
            deleteSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result != null) {
                                liReview.setVisibility(View.GONE);
                                liOption.setVisibility(View.GONE);
                                editContent.setText(null);
                                editContent.setEnabled(true);
                                tvSet.setVisibility(View.VISIBLE);
                                editContent.addTextChangedListener(watcher);
                                tvCounter.setVisibility(View.VISIBLE);
                                setEditContentHeight(true);
                            }
                        }
                    })
                    .build();
        }
        Observable<HljHttpResult> observable = MerchantApi.deleteMerchantNotice();
        observable.subscribe(deleteSubscriber);
    }

    @Override
    protected void onFinish() {
        if (getSubscriber != null && !getSubscriber.isUnsubscribed()) {
            getSubscriber.isUnsubscribed();
        }
        if (deleteSubscriber != null && !deleteSubscriber.isUnsubscribed()) {
            deleteSubscriber.isUnsubscribed();
        }

        if (checkSubscriber != null && !checkSubscriber.isUnsubscribed()) {
            checkSubscriber.isUnsubscribed();
        }

        if (editSubscriber != null && !editSubscriber.isUnsubscribed()) {
            editSubscriber.isUnsubscribed();
        }
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        super.onFinish();
    }
}
