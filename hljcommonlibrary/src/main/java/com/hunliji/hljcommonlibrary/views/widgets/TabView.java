package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;

public class TabView extends RelativeLayout {
    public TextView textView;
    public int mIndex;

    public TabView(Context context) {
        super(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setText(CharSequence text) {
        if (textView == null) {
            textView = (TextView) findViewById(R.id.title);
        }
        if (textView != null) {
            textView.setText(text);
        }
    }
}