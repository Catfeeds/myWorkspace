package com.hunliji.hljsharelibrary.utils;

import android.os.Handler;
import android.os.Message;

import com.hunliji.hljcommonlibrary.utils.FileUtil;

/**
 * Created by wangtao on 2017/8/29.
 */

public class WXShareListener implements WXCallbackUtil.WXOnCompleteCallbackListener {

    private int requestCode;
    private String path;
    private Handler handler;

    public WXShareListener(int requestCode, Handler handler) {
        this(requestCode, null, handler);
    }

    public WXShareListener(int requestCode, String path, Handler handler) {
        this.requestCode = requestCode;
        this.path = path;
        this.handler = handler;
    }

    @Override
    public void OnComplete(String code) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = requestCode;
            handler.sendMessage(msg);
        }
        FileUtil.deleteFile(path);
    }

    @Override
    public void OnError() {
        FileUtil.deleteFile(path);
    }

    @Override
    public void OnCancel() {
        FileUtil.deleteFile(path);
    }
}
