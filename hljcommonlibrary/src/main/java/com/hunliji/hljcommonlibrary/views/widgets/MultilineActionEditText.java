package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by wangtao on 2017/2/10.
 */

public class MultilineActionEditText extends EditText {

    private boolean isDisable;

    public MultilineActionEditText(Context context) {
        super(context);
    }

    public MultilineActionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultilineActionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if(isDisable){
            return super.onCreateInputConnection(outAttrs);
        }
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

    public void setDisAction(boolean isDisable){
        this.isDisable=isDisable;
    }
}
