package me.suncloud.marrymemo.view.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 活动举报成功界面
 * Created by chen_bin on 2016/9/18 0018.
 */
public class AfterReportEventActivity extends HljBaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.message_layout)
    LinearLayout messageLayout;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    private EventInfo eventInfo;
    private int[] reasons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_report);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        eventInfo = getIntent().getParcelableExtra("eventInfo");
        reasons = new int[]{R.string.label_other_setting, R.string.hint_report_reason, R.string
                .hint_report_reason2, R.string.hint_report_reason3, R.string.hint_report_reason4};
    }

    private void initViews() {
        if (eventInfo == null || eventInfo.getReportInfo() == null) {
            return;
        }
        tvTitle.setText(eventInfo.getTitle());
        tvReason.setText(reasons[eventInfo.getReportInfo()
                .getType()]);
        if (TextUtils.isEmpty(eventInfo.getReportInfo()
                .getMessage())) {
            messageLayout.setVisibility(View.GONE);
        } else {
            messageLayout.setVisibility(View.VISIBLE);
            tvMessage.setText(eventInfo.getReportInfo()
                    .getMessage());
        }
    }

}
