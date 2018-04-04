package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.marrybiz.R;

public class TabView extends RelativeLayout {

    public TabView(Context context) {
        super(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView textView;

    public int mIndex;

    public int getIndex() {
        return mIndex;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setText(CharSequence text) {
        if (textView == null) {
            textView = (TextView) findViewById(R.id.title);
        }
        textView.setText(text);
    }
}