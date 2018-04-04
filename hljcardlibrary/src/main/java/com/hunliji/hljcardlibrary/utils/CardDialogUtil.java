package com.hunliji.hljcardlibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by wangtao on 2017/7/19.
 */

public class CardDialogUtil {

    public static Dialog createNameEditDialog(
            Context mContext,
            Card card,
            final View.OnClickListener confirmListener,
            final View.OnClickListener editListener) {
        final Dialog dialog = new Dialog(mContext, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_card_name_edit___card);
        TextView tvBrideName = (TextView) dialog.findViewById(R.id.tv_bride_name);
        TextView tvGroomName = (TextView) dialog.findViewById(R.id.tv_groom_name);
        tvBrideName.setText(card.getBrideName());
        tvGroomName.setText(card.getGroomName());
        dialog.findViewById(R.id.btn_edit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.btn_edit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (editListener != null) {
                            editListener.onClick(v);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (confirmListener != null) {
                            confirmListener.onClick(v);
                        }
                    }
                });
        Window win = dialog.getWindow();
        if (win != null) {
            ViewGroup.LayoutParams params = win.getAttributes();
            Point point = CommonUtil.getDeviceSize(mContext);
            params.width = point.x - CommonUtil.dp2px(mContext, 60);
        }
        return dialog;
    }


    public static Dialog createGiftOpenDlg(
            Context context) {
        final Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_wedding_cash_gift);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = dialog.getWindow()
                    .getAttributes();
            lp.dimAmount = 0.8f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return dialog;
    }


}
