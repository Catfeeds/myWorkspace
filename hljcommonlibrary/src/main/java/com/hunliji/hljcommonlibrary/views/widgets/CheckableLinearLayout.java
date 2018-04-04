package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.R;

/**
 * Created by Suncloud on 2016/8/10.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean isChecked;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] ENABLED_STATE_SET = {android.R.attr.state_enabled};
    private OnCheckedChangeListener checkedChangeListener;

    public CheckableLinearLayout(Context context) {
        this(context, null);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckableLayout, 0, 0);
        try {
            setChecked(ta.getBoolean(R.styleable.CheckableLayout_checked, false));
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            refreshDrawableState();
            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChange(this, isChecked);
            }
        }

    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        if(isEnabled()){
            setChecked(!isChecked);
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isEnabled()) {
            if (isChecked()) {
                mergeDrawableStates(drawableState, CHECKED_STATE_SET);
            } else {
                mergeDrawableStates(drawableState, ENABLED_STATE_SET);
            }
        }
        return drawableState;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.checkedChangeListener = onCheckedChangeListener;
    }


    public interface OnCheckedChangeListener {
        void onCheckedChange(View view, boolean checked);
    }
}
