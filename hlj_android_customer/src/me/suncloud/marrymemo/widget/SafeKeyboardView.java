package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by werther on 15/12/30.
 */
public class SafeKeyboardView extends FrameLayout implements View.OnTouchListener {

    private List<View> keyViews;
    private OnInputListener onInputListener;

    public SafeKeyboardView(Context context) {
        super(context);
        init(context);
    }

    public SafeKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SafeKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.safe_keyboard_layout___cm, this, true);

        keyViews = new ArrayList<>();
        View view1 = findViewById(R.id.k_1);
        View view2 = findViewById(R.id.k_2);
        View view3 = findViewById(R.id.k_3);
        View view4 = findViewById(R.id.k_4);
        View view5 = findViewById(R.id.k_5);
        View view6 = findViewById(R.id.k_6);
        View view7 = findViewById(R.id.k_7);
        View view8 = findViewById(R.id.k_8);
        View view9 = findViewById(R.id.k_9);
        View view10 = findViewById(R.id.k_10);
        View view11 = findViewById(R.id.k_11);
        View view12 = findViewById(R.id.k_12);
        keyViews.add(view1);
        keyViews.add(view2);
        keyViews.add(view3);
        keyViews.add(view4);
        keyViews.add(view5);
        keyViews.add(view6);
        keyViews.add(view7);
        keyViews.add(view8);
        keyViews.add(view9);
        keyViews.add(view10);
        keyViews.add(view11);
        keyViews.add(view12);
        for (View view : keyViews) {
            view.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String s = null;
        switch (v.getId()) {
            case R.id.k_1:
                s = "1";
                break;
            case R.id.k_2:
                s = "2";
                break;
            case R.id.k_3:
                s = "3";
                break;
            case R.id.k_4:
                s = "4";
                break;
            case R.id.k_5:
                s = "5";
                break;
            case R.id.k_6:
                s = "6";
                break;
            case R.id.k_7:
                s = "7";
                break;
            case R.id.k_8:
                s = "8";
                break;
            case R.id.k_9:
                s = "9";
                break;
            case R.id.k_10:
                break;
            case R.id.k_11:
                s = "0";
                break;
            case R.id.k_12:
                if (onInputListener != null) {
                    onInputListener.onDelete();
                }
                break;
        }
        if (!TextUtils.isEmpty(s) && onInputListener != null) {
           onInputListener.onKeyInput(s);
        }
        return false;
    }

    public void setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
    }

    public interface OnInputListener{
        void onKeyInput(String s);

        void onDelete();
    }
}
