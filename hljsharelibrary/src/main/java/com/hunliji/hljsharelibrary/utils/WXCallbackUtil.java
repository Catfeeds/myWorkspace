package com.hunliji.hljsharelibrary.utils;

import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * Created by Suncloud on 2015/1/23.
 */
public class WXCallbackUtil {


    private static WXCallbackUtil INSTANCE;
    private static WXOnCompleteCallbackListener loginCompleteListener;
    private static WXOnCompleteCallbackListener shareCompleteListener;
    private static WXOnCompleteCallbackListener payCompleteListener;

    private WXCallbackUtil() {
    }

    public static WXCallbackUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WXCallbackUtil();
        }
        return INSTANCE;
    }

    public void registerLoginCallback(WXOnCompleteCallbackListener callbackListener) {
        if (callbackListener != null) {
            loginCompleteListener = callbackListener;
        }
    }


    public void registerShareCallback(WXOnCompleteCallbackListener callbackListener) {
        if (callbackListener != null) {
            shareCompleteListener = callbackListener;
        }
    }

    public void registerPayCallback(WXOnCompleteCallbackListener callbackListener) {
        if (callbackListener != null) {
            payCompleteListener = callbackListener;
        }
    }

    public void LoginOnComplete(String code) {
        if (loginCompleteListener != null) {
            loginCompleteListener.OnComplete(code);
            loginCompleteListener = null;
        }
    }

    public void LoginOnCancle() {
        if (loginCompleteListener != null) {
            loginCompleteListener.OnCancel();
            loginCompleteListener = null;
        }
    }

    public void LoginOnError() {
        if (loginCompleteListener != null) {
            loginCompleteListener.OnError();
            loginCompleteListener = null;
        }
    }

    public void ShareOnComplete(int errCode) {
        if (shareCompleteListener != null) {
            switch (errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    shareCompleteListener.OnComplete(null);
                    break;
                default:
                    break;
            }
            shareCompleteListener = null;
        }
    }

    public void PayOnComplete(int errCode) {
        if (payCompleteListener != null) {
            payCompleteListener.OnComplete(String.valueOf(errCode));
            payCompleteListener = null;
        }
    }

    public interface WXOnCompleteCallbackListener {
        void OnComplete(String code);

        void OnError();

        void OnCancel();
    }
}
