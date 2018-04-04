package com.hunliji.hljquestionanswer.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hunliji.hljquestionanswer.R;

/**
 * Created by wangtao on 2016/12/26.
 */

public class UploadingTextView extends TextView {

    public UploadingTextView(Context context) {
        super(context);
    }

    public UploadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        if(visibility==VISIBLE){
            post(runnable);
        }else{
            removeCallbacks(runnable);
        }
        super.setVisibility(visibility);
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            int index= (int) ((System.currentTimeMillis()/500)%3);
            String loading="   ";
            switch (index){
                case 2:
                    loading="...";
                    break;
                case 1:
                    loading=".. ";
                    break;
                case 0:
                    loading=".  ";
                    break;
            }
            setText(getResources().getString(R.string.label_image_uploading___qa,loading));
            postDelayed(this,500);
        }
    };
}
