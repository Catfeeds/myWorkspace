package com.hunliji.hljcommonlibrary.views.widgets.tint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.hunliji.hljcommonlibrary.R;

/**
 * Created by wangtao on 2017/6/8.
 */

public class TintColorListImageView extends AppCompatImageView{

    private ColorStateList tintColorList;

    public TintColorListImageView(Context context) {
        this(context,null);
    }

    public TintColorListImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TintColorListImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintColorListImageView, defStyleAttr, 0);
            tintColorList = a.getColorStateList(R.styleable.TintColorListImageView_tintColorList);
            a.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tintColorList != null && tintColorList.isStateful())
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tintColorList = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        int color = tintColorList.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }
}
