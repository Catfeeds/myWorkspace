package com.hunliji.hljnotelibrary.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;

/**
 * Created by chen_bin on 2017/7/21 0021.
 */
public class NoteDialogUtil {

    //发布笔记提示框
    public static Dialog showCreateNoteMenuDialog(
            final Context context,
            final View.OnClickListener choosePhotoListener,
            final View.OnClickListener chooseVideoListener) {
        final Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_menu_create_note___note);
        dialog.findViewById(R.id.photo_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (choosePhotoListener != null) {
                            choosePhotoListener.onClick(v);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_video)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (chooseVideoListener != null) {
                            chooseVideoListener.onClick(v);
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

}
