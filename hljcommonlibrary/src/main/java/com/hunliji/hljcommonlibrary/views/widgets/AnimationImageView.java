package com.hunliji.hljcommonlibrary.views.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Suncloud on 2016/2/23.
 */
public class AnimationImageView extends ImageView {

    public AnimationImageView(Context context) {
        super(context);
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(getVisibility()==VISIBLE) {
            ((AnimationDrawable) getDrawable()).start();
        }else{
            ((AnimationDrawable) getDrawable()).stop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if(getDrawable()!=null&&getDrawable() instanceof AnimationDrawable){
            if(getVisibility()==VISIBLE) {
                ((AnimationDrawable) getDrawable()).start();
            }else{
                ((AnimationDrawable) getDrawable()).stop();
            }
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(getDrawable()!=null&&getDrawable() instanceof AnimationDrawable){
            ((AnimationDrawable) getDrawable()).stop();
        }
        super.onDetachedFromWindow();
    }
}
