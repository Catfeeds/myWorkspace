package com.hunliji.hljcardcustomerlibrary.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcommonlibrary.models.userprofile.WXInfo;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import rx.Subscriber;

/**
 * Created by mo_yu on 2017/6/15.微信登陆帮助类
 */
public class WXHelper implements WXCallbackUtil.WXOnCompleteCallbackListener {

    private static volatile WXHelper helper;
    private static Context mContext;
    private OnWXLoginListener onWXLoginListener;

    public static WXHelper getInstance(Context context) {
        mContext = context;
        if (helper == null) {
            synchronized (WXHelper.class) {
                if (helper == null) {
                    helper = new WXHelper();
                }
            }
        }
        return helper;
    }

    /**
     * 微信登陆
     */
    public void weChatLogin() {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, HljShare.WEIXINKEY, true);
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext,
                    mContext.getString(R.string.unfind_weixin___share),
                    Toast.LENGTH_SHORT)
                    .show();
            if (onWXLoginListener != null) {
                onWXLoginListener.onError(mContext.getString(R.string.unfind_weixin___share));
            }
            return;
        }
        WXCallbackUtil.getInstance()
                .registerLoginCallback(this);
        api.registerApp(HljShare.WEIXINKEY);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        api.sendReq(req);
    }

    @Override
    public void OnComplete(String code) {
        if (!TextUtils.isEmpty(code)) {
            CustomerCardApi.getWXTokenObb(code)
                    .subscribe(new Subscriber<WXInfo>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            if (onWXLoginListener != null) {
                                onWXLoginListener.onError(e.toString());
                            }
                        }

                        @Override
                        public void onNext(WXInfo wxInfo) {
                            if (onWXLoginListener != null) {
                                onWXLoginListener.onLogin(wxInfo);
                            }
                        }
                    });
        }
    }

    @Override
    public void OnError() {
        if (onWXLoginListener != null) {
            onWXLoginListener.onError(null);
        }
    }

    @Override
    public void OnCancel() {
        if (onWXLoginListener != null) {
            onWXLoginListener.onCancel();
        }
    }

    public interface OnWXLoginListener {
        void onLogin(WXInfo wxInfo);

        void onError(String error);

        void onCancel();
    }

    public void setOnWXLoginListener(OnWXLoginListener onWXLoginListener) {
        this.onWXLoginListener = onWXLoginListener;
    }
}
