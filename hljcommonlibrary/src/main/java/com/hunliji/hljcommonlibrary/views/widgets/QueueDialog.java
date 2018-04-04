package com.hunliji.hljcommonlibrary.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

/**
 * Created by werther on 16/11/24.
 * 主页窗口队列专用的窗口类
 */

public class QueueDialog extends Dialog {

    private ArrayList<OnDismissListener> onDismissListeners;

    public QueueDialog(Context context) {
        super(context);
    }

    public QueueDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected QueueDialog(
            Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void addDismissListener(final OnDismissListener onDismissListener) {
        if (onDismissListeners == null) {
            onDismissListeners = new ArrayList<>();
        }

        onDismissListeners.add(onDismissListener);
        super.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismissListeners != null && !onDismissListeners.isEmpty()) {
                    for (OnDismissListener listener : onDismissListeners) {
                        listener.onDismiss(dialog);
                    }
                }
            }
        });
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        addDismissListener(onDismissListener);
    }
}
