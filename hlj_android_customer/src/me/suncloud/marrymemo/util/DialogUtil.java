package me.suncloud.marrymemo.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wheelpickerlibrary.picker.SingleWheelPicker;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ContactsAdapter;
import me.suncloud.marrymemo.model.Label;
import permissions.dispatcher.PermissionRequest;

/**
 * 弹窗util
 * Created by jinxin on 2016/3/25.
 */
public class DialogUtil {
    private static final int DEFAULT_DIALOG_LAYOUT = R.layout.dialog_new_confirm;
    private static final int DEFAULT_DIALOG_STYLE = R.style.BubbleDialogTheme;

    /**
     * @param mContext
     * @param locationText
     * @param confirmClickListener
     */
    public static QueueDialog createLocationDialog(
            @NonNull Context mContext,
            @NonNull String locationText,
            @NonNull final View.OnClickListener confirmClickListener) {
        final QueueDialog dialog = createQueueDialog(mContext, DEFAULT_DIALOG_LAYOUT);
        dialog.findViewById(R.id.content_layout)
                .setVisibility(View.VISIBLE);
        Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        TextView title = (TextView) dialog.findViewById(R.id.tv_title);
        confirm.setText(mContext.getString(R.string.label_switch));
        title.setText(locationText);
        dialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmClickListener.onClick(v);
            }
        });

        return dialog;
    }

    private static Dialog createDialog(Context mContext, int layoutResID) {
        Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(layoutResID);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(mContext);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        return dialog;
    }

    public static QueueDialog createQueueDialog(Context mContext, int layoutResID) {
        QueueDialog dialog = new QueueDialog(mContext, DEFAULT_DIALOG_STYLE);
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
            String msgStr,
            String confirmStr,
            View.OnClickListener onClickListener) {
        return createSingleButtonDialog(dialog, mContext, msgStr, confirmStr, 0, onClickListener);
    }

    public static Dialog createSingleButtonDialog(
            Dialog dialog,
            Context mContext,
            String msgStr,
            String confirmStr,
            int centerImgDrawableResId,
            View.OnClickListener onClickListener) {
        if (dialog == null) {
            dialog = createDialog(mContext, R.layout.dialog_msg_single_button);
            dialog.findViewById(R.id.extend_layout)
                    .setVisibility(View.GONE);
        }
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        ImageView imgCenter = (ImageView) dialog.findViewById(R.id.img_center);
        tvAlertMsg.setText(msgStr);
        if (centerImgDrawableResId > 0) {
            imgCenter.setVisibility(View.VISIBLE);
            imgCenter.setImageResource(centerImgDrawableResId);
        } else {
            imgCenter.setVisibility(View.GONE);
        }
        if (!JSONUtil.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm);
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


    public static Dialog createDoubleButtonDialog(
            Dialog dialog,
            Context mContext,
            String msgStr,
            String confirmStr,
            String cancelStr,
            View.OnClickListener confirmListener) {
        return createDoubleButtonDialog(dialog,
                mContext,
                msgStr,
                confirmStr,
                cancelStr,
                confirmListener,
                null);
    }

    /**
     * 创建一个对话框,有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param dialog          dialog实体
     * @param mContext
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
            String msgStr,
            String confirmStr,
            String cancelStr,
            View.OnClickListener confirmListener,
            @Nullable View.OnClickListener cancelListener) {
        if (dialog == null) {
            dialog = createDialog(mContext, R.layout.dialog_confirm);
        }
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(msgStr);
        if (!JSONUtil.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm);
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
     * 创建一个对话框,有一个标题，有一段提示文字,两个按钮,自定义其按钮文字提示文案以及按钮操作
     *
     * @param dialog          dialog实体
     * @param mContext
     * @param titleStr        标题文案
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
        TextView tvMsgTitle = (TextView) dialog.findViewById(R.id.tv_msg_title);
        if (TextUtils.isEmpty(titleStr)) {
            tvMsgTitle.setVisibility(View.GONE);
        } else {
            tvMsgTitle.setVisibility(View.VISIBLE);
            tvMsgTitle.setText(titleStr);
        }
        TextView tvNoticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        tvNoticeMsg.setText(msgStr);
        if (!JSONUtil.isEmpty(confirmStr)) {
            btnConfirm.setText(confirmStr);
        } else {
            btnConfirm.setText(R.string.label_confirm);
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
     * 创建一个单栏滚筒选择框,传入label列表,返回选中的label
     *
     * @param mContext
     * @param list
     * @param onSelectedListener
     * @return
     */
    public static Dialog createSingleWheelPickerDialog(
            Context mContext,
            final List<Label> list,
            final onWheelSelectedListener onSelectedListener) {
        final Dialog dialog = new Dialog(mContext, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.single_wheel_picker___cm);
        final SingleWheelPicker picker = (SingleWheelPicker) dialog.findViewById(R.id.picker);
        List<String> strings = new ArrayList<>();
        for (Label label : list) {
            strings.add(label.getName());
        }
        picker.setItems(strings);
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
                        if (onSelectedListener != null) {
                            int i = picker.getCurrentItem();
                            Label selectedLabel = list.get(i);
                            onSelectedListener.selected(selectedLabel);
                            dialog.cancel();
                        }
                    }
                });
        Window window = dialog.getWindow();
        ViewGroup.LayoutParams params = window.getAttributes();
        Point point = CommonUtil.getDeviceSize(mContext);
        params.width = point.x;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);
        return dialog;
    }

    public interface onWheelSelectedListener {
        void selected(Label label);
    }

    public static void showRationalePermissionsDialog(
            Context context, final PermissionRequest request, String message) {
        final Dialog dialog = createDialog(context, R.layout.dialog_confirm);
        dialog.setCancelable(false);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tv_alert_msg);
        tvAlertMsg.setText(message);
        btnConfirm.setText("允许");
        btnCancel.setText("拒绝");
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

    public static void showPostSuccessDialog(
            @NonNull Context mContext,
            boolean isFromHomePage,
            boolean isPosted,
            @NonNull final View.OnClickListener confirmClickListener,
            @NonNull final View.OnClickListener cancelClickListener) {
        final Dialog dialog = createDialog(mContext, DEFAULT_DIALOG_LAYOUT);
        dialog.findViewById(R.id.content_layout)
                .setVisibility(View.VISIBLE);
        dialog.setCancelable(false);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);
        View multiBtnLayout = dialog.findViewById(R.id.multi_btn_layout);
        Button singleCancel = (Button) dialog.findViewById(R.id.btn_single_cancel);
        ImageView img = (ImageView) dialog.findViewById(R.id.image);
        if (isFromHomePage && isPosted) {
            //首页发布并且已发过
            tvTitle.setText(R.string.label_post_success);
            cancel.setText(R.string.label_back_to_community);
            confirm.setText(R.string.label_go_see_thread);
            singleCancel.setVisibility(View.GONE);
            multiBtnLayout.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.GONE);
        } else if (isFromHomePage && !isPosted) {
            // 首页首次发布
            tvTitle.setText(R.string.label_post_success2);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(R.string.label_post_success3);
            cancel.setText(R.string.label_back_to_community);
            confirm.setText(R.string.label_go_see_thread);
            singleCancel.setVisibility(View.GONE);
            multiBtnLayout.setVisibility(View.VISIBLE);
        } else {
            // 频道首次发布
            tvTitle.setText(R.string.label_post_success2);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(R.string.label_post_success3);
            singleCancel.setText(R.string.label_ok2);
            confirm.setVisibility(View.GONE);
            singleCancel.setVisibility(View.VISIBLE);
            multiBtnLayout.setVisibility(View.GONE);
        }

        // 记录已发过贴
        mContext.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.PREF_THREAD_POSTED, true)
                .apply();

        img.setImageResource(R.mipmap.icon_new_praise___cm);

        tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
        tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmClickListener.onClick(v);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cancelClickListener.onClick(v);
            }
        });
        singleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cancelClickListener.onClick(v);
            }
        });
        dialog.show();
    }


    public static Dialog createProgressDialog(@NonNull Context mContext) {
        Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.progressbar_layout);
        dialog.findViewById(R.id.progressBar)
                .setVisibility(View.VISIBLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    /**
     * 订单相关的红包列表显示窗口
     *
     * @param dialog
     * @param mContext
     * @param adapter
     * @param listener
     * @param selectPosition
     * @return
     */
    public static Dialog createRedPacketDialog(
            Dialog dialog,
            Context mContext,
            BaseAdapter adapter,
            AdapterView.OnItemClickListener listener,
            final View.OnClickListener onClickListener,
            int selectPosition) {
        if (dialog == null) {
            dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
            dialog.setContentView(R.layout.dialog_red_packets);
            final Dialog finalDialog = dialog;
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(listener);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(mContext);
            params.width = point.x;
            params.height = point.y / 2;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
            dialog.findViewById(R.id.tv_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onClickListener != null) {
                                onClickListener.onClick(v);
                            }
                        }
                    });
        }
        if (selectPosition >= 0) {
            ((ListView) dialog.findViewById(R.id.list)).setItemChecked(selectPosition, true);
        } else {
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            int lastSelected = listView.getCheckedItemPosition();
            if (lastSelected > 0) {
                listView.setItemChecked(lastSelected, false);
            }
        }
        return dialog;
    }

    /**
     * 生成本地服务订单选择优惠券的窗口
     *
     * @param dialog
     * @param context
     * @param adapter
     * @param listener
     * @param selectPosition
     * @return
     */
    public static Dialog createdCouponListDialog(
            Dialog dialog,
            Context context,
            BaseAdapter adapter,
            AdapterView.OnItemClickListener listener,
            final View.OnClickListener onClickListener,
            int selectPosition) {
        if (dialog == null) {
            dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
            dialog.setContentView(R.layout.dialog_red_packets);
            TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
            tvTitle.setText("可用优惠券");

            final Dialog finalDialog = dialog;
            dialog.findViewById(R.id.tv_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onClickListener != null) {
                                onClickListener.onClick(v);
                            }
                        }
                    });
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(listener);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(context);
            params.width = point.x;
            params.height = point.y / 2;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        if (selectPosition >= 0) {
            ((ListView) dialog.findViewById(R.id.list)).setItemChecked(selectPosition, true);
        } else {
            ListView listView = (ListView) dialog.findViewById(R.id.list);
            int lastSelected = listView.getCheckedItemPosition();
            if (lastSelected > 0) {
                listView.setItemChecked(lastSelected, false);
            }
        }

        return dialog;
    }

    /**
     * 创建主页"我的"页面上显示结伴邀请的弹窗
     *
     * @param dialog
     * @param context
     * @param notification
     * @param confirmListener
     * @param cancelListener
     * @return
     */
    public static QueueDialog createPartnerInvitationDlg(
            QueueDialog dialog,
            Context context,
            com.hunliji.hljcommonlibrary.models.realm.Notification notification,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        if (dialog == null) {
            dialog = createQueueDialog(context, R.layout.dialog_partner_invitation);

        }
        final Dialog finalDialog = dialog;
        ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btn_close);
        RoundedImageView imageView = (RoundedImageView) dialog.findViewById(R.id.img_avatar);
        TextView tvNick = (TextView) dialog.findViewById(R.id.tv_nick);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_notice_confirm);
        Glide.with(context)
                .load(ImageUtil.getAvatar(notification.getParticipantAvatar()))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                        .dontAnimate())
                .into(imageView);
        tvNick.setText(notification.getParticipantName());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.cancel();
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.cancel();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });

        return dialog;
    }

    /**
     * 注册后邀请另一半弹窗
     *
     * @return
     */
    public static QueueDialog createRegisterPartnerInvitationDlg(
            Context context,
            final View.OnClickListener confirmListener,
            final View.OnClickListener cancelListener) {
        final QueueDialog dialog = new QueueDialog(context, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_main_popup_partner);
        ImageView ivCover = (ImageView) dialog.findViewById(R.id.iv_cover);
        Glide.with(context)
                .load(R.drawable.image_popup_partner_invitation)
                .apply(new RequestOptions().dontAnimate())
                .into(ivCover);
        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
            }
        });
        dialog.findViewById(R.id.btn_close)
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
            lp.dimAmount = 0.5f;
            dialog.getWindow()
                    .setAttributes(lp);
            dialog.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    /**
     * 预约成功弹窗
     *
     * @param mContext
     * @return
     */
    public static Dialog createAppointmentDlg(
            Context mContext) {
        final Dialog dialog = new Dialog(mContext, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_new_notice);

        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        ImageButton btnClose = dialog.findViewById(R.id.btn_close);
        TextView tvTitle = dialog.findViewById(R.id.tv_notice_title);
        TextView tvMsg = dialog.findViewById(R.id.tv_notice_msg);
        ImageView imgIcon = dialog.findViewById(R.id.img_icon);
        imgIcon.setImageResource(R.drawable.icon_dlg_appointment);

        tvTitle.setText(R.string.msg_appointment_success);
        tvMsg.setText(R.string.msg_appointment_success2___cm);

        btnConfirm.setText(R.string.label_ok3);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return dialog;
    }

    public static Dialog createPhoneListDialog(
            Context context,
            List<String> contactPhones,
            final AdapterView.OnItemClickListener onItemClickListener) {
        final Dialog contactDialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        contactDialog.setContentView(R.layout.dialog_contact_phones);
        Point point = JSONUtil.getDeviceSize(context);
        ListView listView = (ListView) contactDialog.findViewById(R.id.contact_list);
        ContactsAdapter contactsAdapter = new ContactsAdapter(context, contactPhones);
        listView.setAdapter(contactsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contactDialog.dismiss();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(adapterView, view, i, l);
                }
            }
        });
        Window win = contactDialog.getWindow();
        if (win != null) {
            ViewGroup.LayoutParams params = win.getAttributes();
            params.width = Math.round(point.x * 3 / 4);
            win.setGravity(Gravity.CENTER);
        }
        return contactDialog;
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

    public static Dialog createNoticeDlg(
            Context context, Merchant merchant) {
        final Dialog dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_text_info);
        TextView textView = dialog.findViewById(R.id.tv_info);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setText(merchant.getNoticeStr());
        dialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        Window win = dialog.getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = CommonUtil.getDeviceSize(context).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        return dialog;
    }

    public static Dialog createHotelPromiseDlg(
            Context context, Merchant merchant) {
        final Dialog dialog = new Dialog(context, DEFAULT_DIALOG_STYLE);
        dialog.setContentView(R.layout.dialog_promise_info);
        if (!JSONUtil.isEmpty(merchant.getFreeOrder())) {
            dialog.findViewById(R.id.free_layout)
                    .setVisibility(View.VISIBLE);
            TextView tvFree = dialog.findViewById(R.id.tv_free);
            tvFree.setText(merchant.getFreeOrder());
        }
        if (!JSONUtil.isEmpty(merchant.getPlatformGift())) {
            dialog.findViewById(R.id.platform_gift_layout)
                    .setVisibility(View.VISIBLE);
            TextView tvPlatformGift = dialog.findViewById(R.id.tv_platform_gift);
            tvPlatformGift.setText(merchant.getPlatformGift());
        }
        dialog.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        Window win = dialog.getWindow();
        if (win != null) {
            WindowManager.LayoutParams params = win.getAttributes();
            params.width = JSONUtil.getDeviceSize(context).x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        return dialog;
    }
}
