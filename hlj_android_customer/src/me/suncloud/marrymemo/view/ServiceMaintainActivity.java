package me.suncloud.marrymemo.view;

import android.os.Bundle;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by mo_yu on 2017/12/22.服务器维护页面
 */

public class ServiceMaintainActivity extends HljBaseNoBarActivity {

    public static final String ARG_MESSAGE = "message";
    @BindView(R.id.tv_maintain_message)
    TextView tvMaintainMessage;
    private boolean isExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_maintain);
        ButterKnife.bind(this);
        String message = getIntent().getStringExtra(ARG_MESSAGE);
        tvMaintainMessage.setText(message);
    }

    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastUtil.showToast(this, null, R.string.label_quit);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finishAffinity();
        }
    }
}
