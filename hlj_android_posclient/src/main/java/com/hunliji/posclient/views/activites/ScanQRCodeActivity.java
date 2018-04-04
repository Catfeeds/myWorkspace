package com.hunliji.posclient.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.utils.BeepManager;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.posclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 二维码扫描
 * Created by chen_bin on 2018/1/15 0015.
 */
public class ScanQRCodeActivity extends HljBaseNoBarActivity implements QRCodeView.Delegate {

    @BindView(R.id.qrcode_view)
    ZXingView qrcodeView;
    @BindView(R.id.action_holder_layout)
    RelativeLayout actionHolderLayout;

    private BeepManager beepManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        setActionBarPadding(this, actionHolderLayout);
        init();
    }

    private void init() {
        beepManager = new BeepManager(this);
        qrcodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        qrcodeView.showScanRect();
        qrcodeView.startSpotDelay(150); //开启摄像头预览并开始识别
    }

    @Override
    protected void onStop() {
        super.onStop();
        qrcodeView.stopCamera(); // 关闭摄像头预览，隐藏扫描框并关闭识别
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrcodeView.onDestroy();
        beepManager.close();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (CommonUtil.isEmpty(result)) {
            ToastUtil.showToast(this, "二维码扫描失败，请重试", 0);
            qrcodeView.startSpotDelay(150); //扫描失败重新开启扫描
            return;
        }
        beepManager.playBeepSoundAndVibrate(true, true);
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ARG_POS_CODE, result);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        ToastUtil.showToast(this, "打开相机错误", 0);
    }

}
