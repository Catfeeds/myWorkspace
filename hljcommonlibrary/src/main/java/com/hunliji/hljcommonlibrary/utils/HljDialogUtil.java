package com.hunliji.hljcommonlibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.R;

/**
 * Created by mo_yu on 2016/12/26.首次弹窗提示
 */

public class HljDialogUtil {

    public final static int QUESTION_ANSWER = 1;
    public final static int NOTE_COLLECT = 2;
    private static Dialog newFirstDialog;

    /**
     * 新的点赞dialog 修改
     *
     * @param context
     * @param type
     * @return
     */
    public static void showFirstCollectNoticeDialog(Context context, int type) {
        if (isNewFirstCollect(context, type)) {
            newFirstDialog = new Dialog(context, R.style.BubbleDialogTheme);
            View view = View.inflate(context, R.layout.dialog_praise_msg_notice___cm, null);
            TextView btnConfirm = (TextView) view.findViewById(R.id.btn_notice_confirm);
            btnConfirm.setText(R.string.label_confirm_praise___cm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newFirstDialog.dismiss();
                }
            });
            newFirstDialog.setContentView(view);
            Window window = newFirstDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = CommonUtil.getDeviceSize(context).x * 27 / 32;
            window.setAttributes(params);
            TextView tvMsg = (TextView) newFirstDialog.findViewById(R.id.tv_notice_content);
            switch (type) {
                case QUESTION_ANSWER:
                    tvMsg.setText(R.string.hint_praise_first___cm);
                    break;
                default:
                    break;
            }
            try {
                newFirstDialog.show();
                SharedPreferences preferences = context.getSharedPreferences(HljCommon.FileNames
                                .PREF_FILE,
                        Context.MODE_PRIVATE);
                preferences.edit()
                        .putBoolean("is_first_praise" + type, false)
                        .apply();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新的点赞dialog判断  问答
     *
     * @param mContext
     * @param type     1 问答
     * @return
     */
    public static boolean isNewFirstCollect(Context mContext, int type) {
        SharedPreferences preferences = mContext.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first_praise" + type, true);
    }

    /**
     * 新的收藏dialog判断  灵感
     *
     * @param mContext
     * @param type  1 note
     * @return
     */
    public static boolean isNewFirstNoteCollect(Context mContext, int type) {
        SharedPreferences preferences = mContext.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first_collect" + type, true);
    }

    /**
     * 收藏灵感
     *
     * @param context
     * @param type
     * @return
     */
    public static void showFirstCollectNoteNoticeDialog(Context context, int type, final View.OnClickListener onConfirmListener,int resId) {
        if (isNewFirstNoteCollect(context, type)) {
            int width = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 60);
            int imgHeight = width * 9 / 16;
            final Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
            View view = View.inflate(context, R.layout.dialog_note_collect___cm, null);
            ((ImageView)view.findViewById(R.id.img_cover)).setImageResource(resId);
            TextView btnConfirm = (TextView) view.findViewById(R.id.tv_go_collect);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onConfirmListener!=null){
                        onConfirmListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.img_cover)
                    .getLayoutParams().height = imgHeight;
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            assert window != null;
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            window.setAttributes(params);
            try {
                dialog.show();
                SharedPreferences preferences = context.getSharedPreferences(HljCommon.FileNames
                                .PREF_FILE,
                        Context.MODE_PRIVATE);
                preferences.edit()
                        .putBoolean("is_first_collect" + type, false)
                        .apply();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }
}
