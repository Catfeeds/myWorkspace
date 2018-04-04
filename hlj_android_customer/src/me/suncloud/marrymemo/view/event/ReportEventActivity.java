package me.suncloud.marrymemo.view.event;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.ReportInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.event.EventApi;
import me.suncloud.marrymemo.util.Util;

/**
 * 活动投诉
 * Created by chen_bin on 2016/9/9 0009.
 */
public class ReportEventActivity extends HljBaseActivity {
    @BindView(R.id.tv_report_reason)
    TextView tvReportReason;
    @BindView(R.id.et_message)
    EditText etMessage;
    private Dialog selectReasonDialog;
    private List<String> reasons;
    private EventInfo eventInfo;
    private int type;
    private HljHttpSubscriber reportSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_event);
        ButterKnife.bind(this);
        initValues();
    }

    private void initValues() {
        eventInfo = getIntent().getParcelableExtra("eventInfo");
        reasons = new ArrayList<>();
        reasons.add(getString(R.string.hint_report_reason));
        reasons.add(getString(R.string.hint_report_reason2));
        reasons.add(getString(R.string.hint_report_reason3));
        reasons.add(getString(R.string.hint_report_reason4));
        reasons.add(getString(R.string.label_other_setting));
    }

    @OnClick(R.id.select_reason_layout)
    public void onSelectReason() {
        if (selectReasonDialog != null && selectReasonDialog.isShowing()) {
            return;
        }
        if (selectReasonDialog == null) {
            selectReasonDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    reasons,
                    0,
                    new DialogUtil.OnWheelSelectedListener() {

                        @Override
                        public void onWheelSelected(int position, String str) {
                            type = position == reasons.size() - 1 ? 0 : position + 1;
                            tvReportReason.setText(str);
                        }
                    });
        }
        selectReasonDialog.show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (!Util.loginBindChecked(this)) {
            return;
        }
        if (tvReportReason.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_select_report_reason);
            return;
        }
        final String message = etMessage.getText()
                .toString();
        CommonUtil.unSubscribeSubs(reportSub);
        reportSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showCustomToast(ReportEventActivity.this,
                                R.string.label_report_success_msg);
                        ReportInfo reportInfo = new ReportInfo();
                        reportInfo.setType(type);
                        reportInfo.setMessage(message);
                        eventInfo.setReportInfo(reportInfo);
                        setResult(RESULT_OK, getIntent().putExtra("eventInfo", eventInfo));
                        Intent intent = new Intent(ReportEventActivity.this,
                                AfterReportEventActivity.class);
                        intent.putExtra("eventInfo", eventInfo);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        finish();
                    }
                })
                .build();
        EventApi.reportObb(eventInfo.getId(), message, type)
                .subscribe(reportSub);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(reportSub);
    }
}