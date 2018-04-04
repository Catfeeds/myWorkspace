package com.hunliji.hljcommonlibrary.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;

/**
 * Created by luohanlin on 16/03/2017.
 */

public class HLjProgressDialog extends Dialog {
    private ProgressBar progress;
    private TextView tvMsg;

    private String message;

    public HLjProgressDialog(@NonNull Context context) {
        super(context);
    }

    public HLjProgressDialog(
            @NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public HLjProgressDialog(
            @NonNull Context context,
            boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hlj_dialog_progress___cm);
        progress = (ProgressBar) findViewById(R.id.progress);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText(message);
        super.onCreate(savedInstanceState);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
