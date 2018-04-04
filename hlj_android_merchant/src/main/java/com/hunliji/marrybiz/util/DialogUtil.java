package com.hunliji.marrybiz.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;

import permissions.dispatcher.PermissionRequest;


/**
 * 弹窗util
 * Created by jinxin on 2016/3/25.
 */
public class DialogUtil {
    private static final int DEFAULT_DIALOG_STYLE = R.style.BubbleDialogTheme;


    public static Dialog createDialog(Context mContext, int layoutResID) {
        Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(layoutResID);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(mContext);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        return dialog;
    }

    public static Dialog createSingleButtonDialog(
            Dialog dialog,
            Context mContext,
            String titleStr,
            String msgStr,
            String confirmStr,
            View.OnClickListener onClickListener) {
        if (dialog == null) {
            dialog = createDialog(mContext, R.layout.dialog_msg_single_button);
        }
        TextView titleView = (TextView) dialog.findViewById(R.id.dialog_msg_title);
        Button btnConfirm = (Button) dialog.findViewById(R.id.dialog_msg_confirm);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.dialog_msg_content);
        tvAlertMsg.setText(msgStr);
        if (!JSONUtil.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm);
        }
        if (!JSONUtil.isEmpty(titleStr)) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(titleStr);
        } else {
            titleView.setVisibility(View.GONE);
        }
        if (onClickListener == null) {
            final Dialog finalDialog = dialog;
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalDialog.dismiss();
                }
            };
        }
        btnConfirm.setOnClickListener(onClickListener);
        return dialog;
    }

    /**
     * 创建一个对话框,有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param dialog          dialog实体
     * @param mContext
     * @param titleStr        提示标题
     * @param msgStr          提示文案
     * @param confirmStr      确定按钮文字,默认为"确认", 可为空
     * @param cancelStr       取消按钮文字,默认为"取消", 可为空
     * @param confirmListener 确定按钮点击监听器
     * @param cancelListener  取消按钮监听器,默认为点击取消显示dialog,可为空
     * @return
     */
    public static Dialog createDoubleButtonDialog(
            Dialog dialog,
            Context mContext,
            String titleStr,
            String msgStr,
            String confirmStr,
            String cancelStr,
            View.OnClickListener confirmListener,
            @Nullable View.OnClickListener cancelListener) {
        if (dialog == null) {
            dialog = createDialog(mContext, R.layout.dialog_confirm_notice);
        }
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_notice_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_notice_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_msg_title);
        tvAlertMsg.setText(msgStr);
        if (!JSONUtil.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm);
        }
        if (!JSONUtil.isEmpty(titleStr)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(titleStr);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        if (!JSONUtil.isEmpty(cancelStr)) {
            btnCancel.setText(cancelStr);
        } else {
            btnCancel.setText(R.string.action_cancel);
        }
        btnConfirm.setOnClickListener(confirmListener);
        if (cancelListener == null) {
            final Dialog finalDialog = dialog;
            cancelListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalDialog.dismiss();
                }
            };
        }
        btnCancel.setOnClickListener(cancelListener);
        return dialog;
    }

    /**
     * 权限提示的对话框
     *
     * @param context
     * @param request
     * @param message
     */
    public static void showRationalePermissionsDialog(
            Context context, final PermissionRequest request, String message) {
        final Dialog dialog = createDialog(context, R.layout.dialog_confirm_notice);
        dialog.setCancelable(false);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_notice_cancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_notice_confirm);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        tvAlertMsg.setText(message);
        btnCancel.setText(context.getString(R.string.permission_refuse));
        btnConfirm.setText(context.getString(R.string.permission_allow));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request != null) {
                    request.cancel();
                }
                dialog.cancel();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request != null) {
                    request.proceed();
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }


    /**
     * 微信墙分享弹窗
     *
     * @return
     */
    public static Dialog createWXWallShareDlg(
            Context context,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        final Dialog dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_wx_wall_share);
        ImageButton btnWxShare = dialog.findViewById(R.id.btn_share_wx);
        btnWxShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (cancelListener != null) {
                            cancelListener.onClick(v);
                        }
                    }
                });
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = dialog.getWindow()
                    .getAttributes();
            lp.width = CommonUtil.getDeviceSize(context).x;
            lp.height = CommonUtil.getDeviceSize(context).y;
            lp.dimAmount = 0.8f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }


    public static Dialog createPotentialUpdateDialog(Context mContext) {
        final Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_potential_update);
        dialog.findViewById(R.id.image)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(mContext);
            params.height = params.width = Math.round(point.x * 56 / 72);
            window.setAttributes(params);
        }
        return dialog;
    }

}
