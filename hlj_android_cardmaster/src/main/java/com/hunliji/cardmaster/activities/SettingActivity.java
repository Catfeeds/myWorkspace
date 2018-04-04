package com.hunliji.cardmaster.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.cardmaster.HLJCardMasterApplication;
import com.hunliji.cardmaster.R;
import com.hunliji.hljcardcustomerlibrary.views.activities.AccountSecurityActivity;
import com.hunliji.hljcommonlibrary.utils.CacheClearUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.HljHttp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置页面
 * Created by jinxin on 2017/11/24 0024.
 */

public class SettingActivity extends HljBaseActivity {

    @BindView(R.id.layout_information)
    LinearLayout layoutInformation;
    @BindView(R.id.account_layout)
    LinearLayout accountLayout;
    @BindView(R.id.layout_suggest)
    LinearLayout layoutSuggest;
    @BindView(R.id.layout_comment)
    LinearLayout layoutComment;
    @BindView(R.id.new_versions_view)
    View newVersionsView;
    @BindView(R.id.layout_about)
    LinearLayout layoutAbout;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.cache_size)
    TextView tvCacheSize;
    @BindView(R.id.layout_cache_clear)
    LinearLayout layoutCacheClear;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    private Dialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initWidget();
    }

    private void initWidget() {
        try {
            tvCacheSize.setText((float) ((CacheClearUtil.getFolderSize(getCacheDir()) +
                    CacheClearUtil.getFolderSize(
                    getExternalCacheDir())) / 104857) / 10 + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.layout_account_security)
    void onAccountSecuriry() {
        Intent intent = new Intent(this, AccountSecurityActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_information)
    void onInformation() {
        Intent intent = new Intent(this, AccountEditActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.account_layout)
    void onAccount() {
        Intent intent = new Intent(this, ThirdBindAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_suggest)
    void onSuggest() {
        Intent intent = new Intent(this, FeedBackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_comment)
    void omComment() {
        try {
            String marketUri = "market://details?id=" + this.getPackageName();
            Uri uri = Uri.parse(marketUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showToast(this, null, R.string.label_msg_praise_error___cm);
        }
    }

    @OnClick(R.id.layout_about)
    void onAbout() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_cache_clear)
    void onCacheClear() {
        CacheClearUtil.cleanInternalCache(this);
        CacheClearUtil.cleanExternalCache(this);
        CacheClearUtil.clearWebStorage();
        HljHttp.clearCache(this);
        try {
            tvCacheSize.setText((float) ((CacheClearUtil.getFolderSize(getCacheDir()) +
                    CacheClearUtil.getFolderSize(
                    getExternalCacheDir())) / 104857) / 10 + "M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_logout)
    void onLogout() {
        if (logoutDialog != null && logoutDialog.isShowing()) {
            return;
        }
        if (logoutDialog == null) {
            logoutDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View view = getLayoutInflater().inflate(R.layout.dialog_msg_notice, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_notice);
            Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
            Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
            tvCancel.setVisibility(View.VISIBLE);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
            tvMsg.setText(R.string.logout_hint);
            tvConfirm.setText(R.string.action_ok);
            tvCancel.setText(R.string.action_cancel);
            imageView.setImageResource(R.mipmap.icon_notice_bell_primary);

            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logoutDialog.dismiss();
                }
            });
            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    HLJCardMasterApplication.logout(SettingActivity.this);

                    logoutDialog.dismiss();
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    intent.putExtra("logout", true);
                    startActivity(intent);
                    finish();
                }
            });

            logoutDialog.setContentView(view);
            Window window = logoutDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
        }
        logoutDialog.show();
    }
}
