package me.suncloud.marrymemo.view.binding_partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;

public class AfterBindPartnerActivity extends HljBaseActivity {

    @BindView(R.id.success_layout)
    LinearLayout successLayout;
    @BindView(R.id.wechat_layout)
    LinearLayout wechatLayout;
    @BindView(R.id.sms_layout)
    LinearLayout smsLayout;
    @BindView(R.id.qq_layout)
    LinearLayout qqLayout;
    @BindView(R.id.share_layout)
    LinearLayout shareLayout;

    private ShareInfo shareInfo;
    private ShareUtil shareUtil;
    private String phoneNumber;
    private int type; // 0一般情况；Constants.Login.REGISTER 注册，Constants.Login.LOGINCHECK 登陆检测

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_bind_partner);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", type);
        shareInfo = getIntent().getParcelableExtra("share_info");
        if (shareInfo == null) {
            // 邀请成功
            successLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.GONE);
        } else {
            // 邀请没有成功，需要分享
            phoneNumber = getIntent().getStringExtra("phone_number");
            successLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.VISIBLE);
            shareUtil = new ShareUtil(this, shareInfo, null);
        }
    }

    @OnClick(R.id.wechat_layout)
    void onWechatShare() {
        shareUtil.shareToWeiXin();
    }

    @OnClick(R.id.sms_layout)
    void onSmsShare() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", shareInfo.getDesc());
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.unfind_msg, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @OnClick(R.id.qq_layout)
    void onQQShare() {
        shareUtil.shareToQQ();
    }

}
