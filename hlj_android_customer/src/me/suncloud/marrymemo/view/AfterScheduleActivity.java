package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.kefu.AdvHelperActivity;

/**
 * Created by mo_yu on 2016/11/28. 档期查询提交成功
 */

public class AfterScheduleActivity extends HljBaseActivity {

    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_submit_schedule);
        ButterKnife.bind(this);
        contentLayout.post(new Runnable() {
            @Override
            public void run() {
                Point point = CommonUtil.getDeviceSize(AfterScheduleActivity.this);
                int height = Math.round((point.y - contentLayout.getMeasuredHeight() - CommonUtil
                        .dp2px(
                        AfterScheduleActivity.this,
                        45)) * 60 / 177);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentLayout
                        .getLayoutParams();
                params.topMargin = height;
            }
        });
    }

    @OnClick({R.id.phone_layout, R.id.msg_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_layout:
                SupportUtil.getInstance(this)
                        .getSupport(this,
                                Support.SUPPORT_KIND_HOTEL,
                                new SupportUtil.SimpleSupportCallback() {
                                    @Override
                                    public void onSupportCompleted(Support support) {
                                        super.onSupportCompleted(support);
                                        if (!JSONUtil.isEmpty(support.getPhone()) && !isFinishing
                                                ()) {
                                            String phone = support.getPhone();
                                            try {
                                                callUp(Uri.parse("tel:" + phone.trim()));
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailed() {
                                        super.onFailed();
                                        Util.showToast(AfterScheduleActivity.this,
                                                null,
                                                R.string.msg_get_supports_error);
                                    }
                                });
                break;
            case R.id.msg_layout:
                if (Util.loginBindChecked(this, Constants.Login.ADV_HELPER_LOGIN)) {
                    if (city == null) {
                        city = Session.getInstance()
                                .getMyCity(this);
                    }
                    boolean b = false;
                    DataConfig dataConfig = Session.getInstance()
                            .getDataConfig(this);
                    if (city != null && city.getId() > 0 && dataConfig != null && dataConfig
                            .getAdvCids() != null && !dataConfig.getAdvCids()
                            .isEmpty()) {
                        b = dataConfig.getAdvCids()
                                .contains(city.getId());
                    }
                    Intent intent;
                    int enterAnim = R.anim.slide_in_right;
                    if (b) {
                        intent = new Intent(this, AdvHelperActivity.class);
                        intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, true);
                    } else {
                        intent = new Intent(this, LightUpActivity.class);
                        intent.putExtra("city", city);
                        intent.putExtra(AdvHelperActivity.ARG_IS_HOTEL, true);
                        intent.putExtra("type", 3);
                        enterAnim = R.anim.fade_in;
                    }
                    this.startActivity(intent);
                    overridePendingTransition(enterAnim, R.anim.activity_anim_default);
                }
                break;
        }
    }
}
