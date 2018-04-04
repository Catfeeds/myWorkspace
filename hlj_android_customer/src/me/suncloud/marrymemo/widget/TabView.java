package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.suncloud.marrymemo.R;

public class TabView extends RelativeLayout {

    public TextView textView;
    private View line;
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

    public TextView getTextView() {
        return textView;
    }

    public void setText(CharSequence text) {
        if (textView == null) {
            textView = (TextView) findViewById(R.id.title);
        }
        textView.setText(text);
    }

    public View getLine() {
        if (line == null) {
            line = findViewById(R.id.line);
        }
        return line;
    }
}