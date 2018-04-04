package com.hunliji.hljupdatelibrary.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.ChannelUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.hunliji.hljupdatelibrary.R;
import com.hunliji.hljupdatelibrary.R2;
import com.hunliji.hljupdatelibrary.models.UpdateInfo;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Suncloud on 2016/9/21.
 */

public class UpdateDialogActivity extends Activity {

    @BindView(R2.id.tv_is_download)
    TextView tvIsDownload;
    @BindView(R2.id.tv_info)
    TextView tvInfo;
    @BindView(R2.id.cb_ignore)
    CheckableLinearLayout cbIgnore;
    @BindView(R2.id.btn_update)
    Button btnUpdate;
    @BindView(R2.id.btn_close)
    ImageButton btnClose;

    private UpdateInfo updateInfo;
    private int currentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog___up);
        ButterKnife.bind(this);
        currentCode = ChannelUtil.getVersionCode(this);
        updateInfo = getIntent().getParcelableExtra("updateInfo");
        tvInfo.setText(getString(R.string.fmt_new_version_info___up, updateInfo.getInfo()));
        tvInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        File apkFile = HljUpdate.downloadedFile(this, updateInfo.getMd5());
        tvIsDownload.setVisibility(apkFile != null ? View.VISIBLE : View.GONE);
        btnUpdate.setText(apkFile != null ? R.string.btn_install___up : R.string.btn_update___up);
        if (updateInfo.getSupportCode() > currentCode) {
            //强制升级
            btnClose.setVisibility(View.GONE);
            cbIgnore.setVisibility(View.GONE);
        } else if (updateInfo.getSuggestCode() > currentCode) {
            //推荐升级
            btnClose.setVisibility(View.VISIBLE);
            cbIgnore.setVisibility(View.GONE);
        } else {
            btnClose.setVisibility(View.VISIBLE);
            cbIgnore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (updateInfo.getSupportCode() > currentCode && !HljCommon.debug) {
            return;
        }
        if (cbIgnore.getVisibility() == View.VISIBLE && cbIgnore.isChecked()) {
            HljUpdate.ignoreUpdate(this, updateInfo.getMd5());
        }
        finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    @OnClick(R2.id.btn_close)
    public void onClose() {
        onBackPressed();
    }

    @OnClick(R2.id.btn_update)
    public void onUpdate() {
        HljUpdate.onUpdate(this, updateInfo);
        if (updateInfo.getSupportCode() > currentCode) {
            finish();
            overridePendingTransition(0, R.anim.fade_out);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                String packageName = getPackageName();
                try {
                    if (packageName.equals("com.hunliji.marrybiz")) {
                        // 商家端
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Merchant.HOME)
                                .withString("action", "exit")
                                .navigation();
                    } else {
                        // 用户端,请帖大师
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.MAIN)
                                .withString("action", "exit")
                                .navigation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            onBackPressed();
        }
    }
}
