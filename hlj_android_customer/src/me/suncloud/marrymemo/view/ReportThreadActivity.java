package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;

public class ReportThreadActivity extends HljBaseActivity {

    @BindView(R.id.cb_1)
    CheckableLinearLayoutButton cb1;
    @BindView(R.id.cb_2)
    CheckableLinearLayoutButton cb2;
    @BindView(R.id.cb_3)
    CheckableLinearLayoutButton cb3;
    @BindView(R.id.cb_4)
    CheckableLinearLayoutButton cb4;
    @BindView(R.id.cb_5)
    CheckableLinearLayoutButton cb5;
    @BindView(R.id.cb_6)
    CheckableLinearLayoutButton cb6;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.cb_group)
    CheckableLinearLayoutGroup cbGroup;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private long id; // 话题id
    private String kind; // 举报类型
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_thread);
        ButterKnife.bind(this);

        id = getIntent().getLongExtra("id", 0);
        kind = getIntent().getStringExtra("kind");

        // 默认选中
        message = "垃圾广告";
        cbGroup.setOnCheckedChangeListener(new CheckableLinearLayoutGroup.OnCheckedChangeListener
                () {
            @Override
            public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cb_1:
                        message = "垃圾广告";
                        break;
                    case R.id.cb_2:
                        message = "侮辱诋毁";
                        break;
                    case R.id.cb_3:
                        message = "淫秽色情";
                        break;
                    case R.id.cb_4:
                        message = "盗版侵权";
                        break;
                    case R.id.cb_5:
                        message = "不符合频道主题";
                        break;
                    case R.id.cb_6:
                        message = "其他";
                        break;
                }
            }
        });
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (Util.loginBindChecked(this)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("kind", kind);
                jsonObject.put("message", message);

                progressBar.setVisibility(View.VISIBLE);
                new StatusHttpPostTask(this, new StatusRequestListener() {
                    @Override
                    public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ReportThreadActivity.this,
                                R.string.msg_success_to_report_thread,
                                Toast.LENGTH_SHORT)
                                .show();
                        onBackPressed();
                    }

                    @Override
                    public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                        progressBar.setVisibility(View.GONE);
                        Util.postFailToast(ReportThreadActivity.this,
                                returnStatus,
                                R.string.msg_fail_to_report_thread,
                                network);
                    }
                }).execute(Constants.getAbsUrl(Constants.HttpPath.INFORM_THREAD_URL),
                        jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
