package com.hunliji.marrybiz.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.WXCallbackUtil;
import com.hunliji.marrybiz.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Suncloud on 2016/9/13.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

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
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WXCallbackUtil.getInstance().PayOnComplete(resp.errCode);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {
        finish();
    }
}
