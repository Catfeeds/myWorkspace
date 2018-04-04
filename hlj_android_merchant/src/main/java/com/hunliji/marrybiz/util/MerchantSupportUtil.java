package com.hunliji.marrybiz.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.view.kefu.EMChatActivity;

/**
 * Created by wangtao on 2017/10/27.
 */

public class MerchantSupportUtil {

    public static void goToSupport(
            final Context context, final int kind) {
        final Dialog progressDialog = com.hunliji.hljcommonlibrary.utils.DialogUtil.createProgressDialog(context);
        progressDialog.show();
        SupportUtil.getInstance(context).getSupport(context, kind, new SupportUtil
                .SimpleSupportCallback(){
            @Override
            public void onSupportCompleted(Support support) {
                super.onSupportCompleted(support);
                progressDialog.cancel();
                if (support != null) {
                    Intent intent = new Intent(context, EMChatActivity.class);
                    intent.putExtra("support", support);
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                progressDialog.cancel();
                Util.showToast(context, null, R.string.msg_get_supports_error);
            }
        });
    }
}
