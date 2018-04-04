package com.hunliji.hljsharelibrary.utils;

import android.os.Handler;
import android.os.Message;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;


/**
 * 腾讯相关分享回调
 * Created by chen_bin on 2017/12/21 0021.
 */
public class TencentShareListener implements IUiListener {
    private int requestCode;
    private String path;
    private Handler handler;

    public TencentShareListener(int requestCode, Handler handler) {
        this.requestCode = requestCode;
        this.handler = handler;
    }

    public TencentShareListener(int requestCode, String path, Handler handler) {
        this.requestCode = requestCode;
        this.path = path;
        this.handler = handler;
    }

    @Override
    public void onComplete(Object o) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = requestCode;
            handler.sendMessage(msg);
        }
        FileUtil.deleteFile(path);
    }

    @Override
    public void onError(UiError uiError) {
        FileUtil.deleteFile(path);
    }

    @Override
    public void onCancel() {
        FileUtil.deleteFile(path);
    }
}
