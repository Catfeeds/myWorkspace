package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Suncloud on 2016/8/10.
 */
public class CheckableLinearButton extends CheckableLinearLayout{

    public CheckableLinearButton(Context context) {
        super(context);
    }

    public CheckableLinearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
    }

    @Override
    public void toggle() {
        if(!isChecked()){
            super.toggle();
        }
    }
}
