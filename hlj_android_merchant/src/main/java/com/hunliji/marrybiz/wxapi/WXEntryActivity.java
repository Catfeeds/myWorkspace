package com.hunliji.marrybiz.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.hunliji.marrybiz.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wx_callback);
        api = WXAPIFactory.createWXAPI(this, HljShare.WEIXINKEY, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp resp2 = (SendAuth.Resp) resp;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    WXCallbackUtil.getInstance().LoginOnComplete(resp2.code);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    break;
                default:
                    break;
            }
        } else if (resp instanceof SendMessageToWX.Resp) {
            WXCallbackUtil.getInstance().ShareOnComplete(resp.errCode);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {
        finish();
    }
}