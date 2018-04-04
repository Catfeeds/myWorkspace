package me.suncloud.marrymemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import me.suncloud.marrymemo.R;

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
        if (resp instanceof Resp) {
            Resp resp2 = (Resp) resp;
            if (resp2 != null) {
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        WXCallbackUtil.getInstance().LoginOnComplete(resp2.code);
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        WXCallbackUtil.getInstance().LoginOnCancle();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        WXCallbackUtil.getInstance().LoginOnError();
                        break;
                    default:
                        break;
                }
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