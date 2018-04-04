package com.hunliji.cardmaster.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.activities.CardMasterEMChatActivity;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljkefulibrary.moudles.Support;
import com.hunliji.hljkefulibrary.utils.SupportUtil;

/**
 * Created by wangtao on 2017/11/29.
 */

public class CardMasterSupportUtil {

    public static void goToSupport(
            final Context context, int kind) {
//        if (kind == Support.SUPPORT_KIND_DEFAULT || kind == Support.SUPPORT_KIND_DEFAULT_ROBOT) {
            kind = Support.SUPPORT_KIND_CARD_MASTER;
//        }
        final Dialog progressDialog = DialogUtil.createProgressDialog(context);
        progressDialog.show();
        SupportUtil.getInstance(context)
                .getSupport(context, kind, new SupportUtil.SimpleSupportCallback() {
                    @Override
                    public void onSupportCompleted(Support support) {
                        super.onSupportCompleted(support);
                        progressDialog.cancel();
                        if (support != null) {
                            Intent intent = new Intent(context, CardMasterEMChatActivity.class);
                            intent.putExtra("support", support);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        progressDialog.cancel();
                        ToastUtil.showToast(context, null, R.string.msg_get_supports_error___kefu);
                    }
                });
    }
}
