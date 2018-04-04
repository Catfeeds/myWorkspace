package com.example.suncloud.hljweblibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wangtao on 2017/1/19.
 */

public abstract class WebBar extends RelativeLayout {

    public WebBar(Context context) {
        this(context, null);
    }

    public WebBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    public abstract void initLayout();

    public abstract WebViewBarInterface getInterface();

    public abstract void setBarClickListener(
            WebViewBarClickListener clickListener);


    public interface WebViewBarInterface {

        void setTitle(String title);

        void setCloseEnable(boolean enable);

        void setShareEnable(boolean enable);

        void setOkButtonEnable(boolean enable, int textColor, String text, int textSize);

    }

    public interface WebViewBarClickListener {

        void onSharePressed();

        void onBackPressed();

        void onOkButtonPressed();

    }
}
