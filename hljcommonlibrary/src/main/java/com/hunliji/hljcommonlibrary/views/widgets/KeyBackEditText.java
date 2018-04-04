package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by jinxin on 2017/7/18 0018.
 */

public class KeyBackEditText extends EditText {

    private OnKeyPreImeListener onKeyPreImeListener;

    public KeyBackEditText(Context context) {
        super(context);
    }

    public KeyBackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onKeyPreImeListener != null) {
            onKeyPreImeListener.onKeyPreIme(getId(), keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        this.onKeyPreImeListener = onKeyPreImeListener;
    }

    public interface OnKeyPreImeListener {
        void onKeyPreIme(int viewId, int keyCode, KeyEvent event);
    }
}
