package com.hunliji.hljcommonlibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wheelpickerlibrary.picker.SingleWheelPicker;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.views.widgets.HLjProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.HljHorizontalProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.HljRoundProgressDialog;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.PermissionRequest;

/**
 * Created by werther on 16/8/5.
 */
public class DialogUtil {

    private static final int DEFAULT_DIALOG_STYLE = R.style.BubbleDialogTheme;

    /**
     * 创建一个用于提交数据是显示progress bar的dialog
     *
     * @param mContext
     * @return
     */
    public static Dialog createProgressDialog(@NonNull Context mContext) {
        Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.hlj_progressbar___cm);
        dialog.findViewById(R.id.progress_bar)
                .setVisibility(View.VISIBLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 创建一个用于提交数据是显示progress bar的dialog
     *
     * @param mContext
     * @param style
     * @return
     */
    public static Dialog createProgressDialog(@NonNull Context mContext, int style) {
        Dialog dialog = new Dialog(mContext, style);
        dialog.setContentView(R.layout.hlj_progressbar___cm);
        dialog.findViewById(R.id.progress_bar)
                .setVisibility(View.VISIBLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    /**
     * 创建一个对话框,有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param mContext        上下文
     * @param msgStr          提示文案
     * @param confirmStr      确定按钮文字,默认为"确认", 可为空
     * @param cancelStr       取消按钮文字,默认为"取消", 可为空
     * @param confirmListener 确定按钮点击监听器
     * @param cancelListener  取消按钮监听器,默认为点击取消显示dialog,可为空
     * @return
     */
    public static Dialog createDoubleButtonDialog(
            Context mContext,
            String msgStr,
            String confirmStr,
            String cancelStr,
            final View.OnClickListener confirmListener,
            @Nullable final View.OnClickListener cancelListener) {
        final Dialog dialog = createDialog(mContext, R.layout.hlj_dialog_confirm___cm);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(msgStr);
        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }
        if (!TextUtils.isEmpty(cancelStr)) {
            btnCancel.setText(cancelStr);
        } else {
            btnCancel.setText(R.string.label_cancel___cm);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        return dialog;
    }

    /**
     * 创建一个对话框,有一个标题，有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param mContext        上下文
     * @param titleStr        标题
     * @param msgStr          提示文案
     * @param confirmStr      确定按钮文字,默认为"确认", 可为空
     * @param cancelStr       取消按钮文字,默认为"取消", 可为空
     * @param confirmListener 确定按钮点击监听器
     * @param cancelListener  取消按钮监听器,默认为点击取消显示dialog,可为空
     * @return
     */
    public static Dialog createDoubleButtonDialog(
            Context mContext,
            String titleStr,
            String msgStr,
            String confirmStr,
            String cancelStr,
            final View.OnClickListener confirmListener,
            @Nullable final View.OnClickListener cancelListener) {
        final Dialog dialog = createDoubleButtonDialog(mContext,
                msgStr,
                confirmStr,
                cancelStr,
                confirmListener,
                cancelListener);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tv_alert_title);
        if (TextUtils.isEmpty(titleStr)) {
            tvAlertTitle.setVisibility(View.GONE);
        } else {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(titleStr);
        }
        return dialog;
    }

    public static Dialog createDialog(Context mContext, int layoutResID) {
        Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(layoutResID);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(mContext);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        return dialog;
    }

    public static void showRationalePermissionsDialog(
            Context context, final PermissionRequest request, String message) {
        final Dialog dialog = createDialog(context, R.layout.hlj_dialog_confirm___cm);
        dialog.setCancelable(false);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(message);
        btnConfirm.setText(context.getString(R.string.label_allow___cm));
        btnCancel.setText(context.getString(R.string.label_refuse___cm));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.proceed();
                dialog.cancel();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.cancel();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public static Dialog createSingleButtonDialog(
            Context mContext,
            String msgStr,
            String confirmStr,
            final View.OnClickListener onClickListener) {
        final Dialog dialog = createDialog(mContext, R.layout.hlj_dialog_single_button___cm);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(msgStr);
        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    //带图片 而且带标题的single dialog
    public static Dialog createSingleButtonWithImageDialog(
            Context mContext,
            @DrawableRes int resId,
            String titleStr,
            String msgStr,
            String confirmStr,
            final View.OnClickListener onClickListener) {
        final Dialog dialog = createDialog(mContext,
                R.layout.hlj_dialog_single_button_with_image___cm);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(msgStr);
        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tv_alert_title);
        if (TextUtils.isEmpty(titleStr)) {
            tvAlertTitle.setVisibility(View.GONE);
        } else {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(titleStr);
        }
        ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_alert_pic);
        if (resId == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(resId);
        }
        return dialog;
    }


    //带标题的single
    public static Dialog createSingleButtonDialog(
            Context mContext,
            String titleStr,
            String msgStr,
            String confirmStr,
            final View.OnClickListener onClickListener) {
        Dialog dialog = createSingleButtonDialog(mContext, msgStr, confirmStr, onClickListener);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tv_alert_title);
        if (TextUtils.isEmpty(titleStr)) {
            tvAlertTitle.setVisibility(View.GONE);
        } else {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(titleStr);
        }
        return dialog;
    }

    public static Dialog createAddPhotoDialog(
            Context mContext,
            final View.OnClickListener onGalleryClickListener,
            final View.OnClickListener onCameraClickListener) {
        final Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.hlj_dialog_add_menu___cm);
        dialog.findViewById(R.id.action_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.action_gallery)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onGalleryClickListener != null) {
                            onGalleryClickListener.onClick(v);
                        }
                    }
                });
        dialog.findViewById(R.id.action_camera_photo)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (onCameraClickListener != null) {
                            onCameraClickListener.onClick(v);
                        }
                    }
                });
        Window win = dialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = CommonUtil.getDeviceSize(mContext);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        return dialog;
    }

    public static Dialog createListDialog(
            Context mContext,
            List<String> strings,
            final AdapterView.OnItemClickListener onItemClickListener) {
        final Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        Point point = CommonUtil.getDeviceSize(mContext);
        dialog.setContentView(R.layout.hlj_dialog_list___cm);
        ListView actionList = (ListView) dialog.findViewById(R.id.list);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(mContext,
                R.layout.dialog_list_item___cm,
                R.id.contact_phone,
                strings);
        actionList.setAdapter(arrayAdapter);
        actionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });
        Window window = dialog.getWindow();
        assert window != null;
        window.getAttributes().width = Math.round(3 * point.x / 4);
        window.setGravity(Gravity.CENTER);
        return dialog;
    }

    public static HljRoundProgressDialog getRoundProgress(Context context) {
        HljRoundProgressDialog progressDialog = new HljRoundProgressDialog(context,
                DEFAULT_DIALOG_STYLE);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        return progressDialog;
    }

    public static HLjProgressDialog getProgressDialog(Context context) {
        HLjProgressDialog progressDialog = new HLjProgressDialog(context, DEFAULT_DIALOG_STYLE);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    /**
     * 创建一个底部弹出的菜单列表dialog，最底下为取消按钮，其他剩下的菜单列表数据由上而下排列，需要设置菜单名称数组和菜单监听操作数据
     *
     * @param context
     * @param menuListenerMap
     * @param cancelListener
     * @return
     */
    public static Dialog createBottomMenuDialog(
            Context context,
            LinkedHashMap<String, View.OnClickListener> menuListenerMap,
            @Nullable final View.OnClickListener cancelListener) {
        final Dialog dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_bottom_menu___cm, null);
        LinearLayout menuLayout = view.findViewById(R.id.menu_layout);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        menuLayout.removeAllViews();
        for (Map.Entry<String, View.OnClickListener> entry : menuListenerMap.entrySet()) {
            String menuStr = entry.getKey();
            final View.OnClickListener menuListener = entry.getValue();
            View menuView = LayoutInflater.from(context)
                    .inflate(R.layout.bottom_menu_item___cm, null);
            Button btnMenu = menuView.findViewById(R.id.btn_menu);
            btnMenu.setText(menuStr);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuListener.onClick(v);
                    dialog.cancel();
                }
            });
            menuLayout.addView(menuView);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        assert window != null;
        window.getAttributes().width = CommonUtil.getDeviceSize(context).x;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);
        return dialog;
    }

    /**
     * 创建一个水平进度条的dialog
     *
     * @param context
     * @return
     */
    public static HljHorizontalProgressDialog createUploadDialog(Context context) {
        HljHorizontalProgressDialog dialog = new HljHorizontalProgressDialog(context,
                DEFAULT_DIALOG_STYLE);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 创建一个对话框,有一个图标（默认为删除），有一段提示文字,两个按钮（竖向排列）,
     * 自定义其按钮文字提示文案以及按钮操作
     *
     * @param mContext        上下文
     * @param drawable        图标
     * @param msgStr          提示文案
     * @param confirmStr      确定按钮文字,默认为"确认", 可为空
     * @param cancelStr       取消按钮文字,默认为"取消", 可为空
     * @param confirmListener 确定按钮点击监听器
     * @param cancelListener  取消按钮监听器,默认为点击取消显示dialog,可为空
     * @return
     */
    public static Dialog createDialogWithIcon(
            Context mContext,
            Drawable drawable,
            String msgStr,
            String confirmStr,
            String cancelStr,
            final View.OnClickListener confirmListener,
            @Nullable final View.OnClickListener cancelListener) {
        final Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.hlj_dialog_with_icon___cm);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(mContext);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
        }
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvNoticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        ImageView imgNotice = (ImageView) dialog.findViewById(R.id.img_notice);
        tvNoticeMsg.setText(msgStr);
        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }
        if (!TextUtils.isEmpty(cancelStr)) {
            btnCancel.setText(cancelStr);
        } else {
            btnCancel.setText(R.string.label_cancel___cm);
        }
        if (drawable != null) {
            imgNotice.setImageDrawable(drawable);
        } else {
            imgNotice.setImageResource(R.mipmap.icon_delete_primary_158_154___cm);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        return dialog;
    }

    /**
     * 创建一个单栏滚筒选择框,传入label列表,返回选中的label
     *
     * @param context
     * @param list
     * @param currentItem
     * @param onWheelSelectedListener
     * @return
     */
    public static Dialog createSingleWheelPickerDialog(
            Context context,
            final List<String> list,
            final int currentItem,
            final OnWheelSelectedListener onWheelSelectedListener) {
        final Dialog dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.single_wheel_picker___cm);
        final SingleWheelPicker picker = (SingleWheelPicker) dialog.findViewById(R.id.picker);
        picker.setItems(list);
        picker.setCurrentItems(currentItem);
        dialog.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
        dialog.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onWheelSelectedListener != null) {
                            dialog.cancel();
                            int position = picker.getCurrentItem();
                            String str = list.get(position);
                            onWheelSelectedListener.onWheelSelected(position, str);
                        }
                    }
                });
        Window window = dialog.getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(context);
            params.width = point.x;
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        return dialog;
    }

    //申诉提示框
    public static Dialog showBottomDialog(
            final Context context, String msg, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_complain_layout);
        Button button = dialog.findViewById(R.id.btn_menu);
        button.setText(msg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        Window win = dialog.getWindow();
        if (win != null) {
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(context).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        return dialog;
    }

    /**
     * 创建一个对话框,有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param mContext        上下文
     * @param msgStr          提示文案
     * @param imgRes          图片imgRes,没有的传<=0
     * @param confirmStr      确定按钮文字,默认为"确认", 可为空
     * @param cancelStr       取消按钮文字,默认为"取消", 可为空
     * @param confirmListener 确定按钮点击监听器
     * @param cancelListener  取消按钮监听器,默认为点击取消显示dialog,可为空
     * @return
     */
    public static QueueDialog createDoubleButtonDialogWithImage(
            Context mContext,
            String msgStr,
            int imgRes,
            String confirmStr,
            String cancelStr,
            boolean closeAble,
            final View.OnClickListener onCloseListener,
            final View.OnClickListener confirmListener,
            @Nullable final View.OnClickListener cancelListener) {
        final QueueDialog dialog = new QueueDialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.hlj_dialog_confirm_image___cm);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(mContext);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        ImageView imgMsg = (ImageView) dialog.findViewById(R.id.img_msg);
        ImageView imgClose = (ImageView) dialog.findViewById(R.id.img_close);
        imgClose.setVisibility(closeAble ? View.VISIBLE : View.GONE);
        imgMsg.setVisibility(View.GONE);
        tvAlertMsg.setText(msgStr);
        if (imgRes > 0) {
            imgMsg.setImageResource(imgRes);
            imgMsg.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }
        if (!TextUtils.isEmpty(cancelStr)) {
            btnCancel.setText(cancelStr);
        } else {
            btnCancel.setText(R.string.label_cancel___cm);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (onCloseListener != null) {
                    onCloseListener.onClick(view);
                }
            }
        });
        return dialog;
    }

    public interface OnWheelSelectedListener {
        void onWheelSelected(int position, String str);
    }

    public static QueueDialog createNoticeOpenDlg(
            Context mContext,
            String msgTitle,
            String msgStr,
            String confirmStr,
            int iconResId,
            final View.OnClickListener confirmListener,
            @Nullable View.OnClickListener cancelListener) {
        final QueueDialog dialog = new QueueDialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_new_notice);

        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        ImageButton btnClose = dialog.findViewById(R.id.btn_close);
        TextView tvTitle = dialog.findViewById(R.id.tv_notice_title);
        TextView tvMsg = dialog.findViewById(R.id.tv_notice_msg);
        ImageView imgIcon = dialog.findViewById(R.id.img_icon);

        if (iconResId > 0) {
            imgIcon.setImageResource(iconResId);
        }
        tvTitle.setText(msgTitle);
        tvMsg.setText(msgStr);

        if (!TextUtils.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm___cm);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
                dialog.cancel();
            }
        });
        final View.OnClickListener finalCancelListener = cancelListener;
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalCancelListener != null) {
                    finalCancelListener.onClick(view);
                }
                dialog.cancel();
            }
        });

        return dialog;
    }

}
