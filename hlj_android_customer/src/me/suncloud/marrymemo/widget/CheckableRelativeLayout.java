package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/8/28.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private boolean isChecked;
    private boolean uncheckable;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] ENABLED_STATE_SET = {android.R.attr.state_enabled};
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckableRelativeLayout(Context context) {
        this(context, null);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable
                .CheckableRelativeLayout, 0, 0);
        try {
            setChecked(ta.getBoolean(R.styleable.CheckableRelativeLayout_isChecked2, false));
            uncheckable = ta.getBoolean(R.styleable.CheckableRelativeLayout_uncheckable2, false);
        } finally {
            ta.recycle();
        }

    }


    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            refreshDrawableState();
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChange(this, isChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        if (isEnabled() && !uncheckable) {
            setChecked(!isChecked);
        }
    }

    public void setUncheckable(boolean uncheckable) {
        this.uncheckable = uncheckable;
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
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(View view, boolean checked);
    }
}
