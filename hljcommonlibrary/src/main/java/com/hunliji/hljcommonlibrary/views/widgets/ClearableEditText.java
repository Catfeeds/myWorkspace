package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.R;

public class ClearableEditText extends android.support.v7.widget.AppCompatEditText {

    private Drawable imgX;

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText);
            try {
                imgX = ta.getDrawable(R.styleable.ClearableEditText_deleteIcon);
            } finally {
                ta.recycle();
            }
        }
        if (imgX == null) {
            imgX = getResources().getDrawable(android.R.drawable.presence_offline);
        }

        // Set bounds of our X button
        imgX.setBounds(0, 0, imgX.getIntrinsicWidth(), imgX.getIntrinsicHeight());

        // There may be initial text in the field, so we may need to display the
        // button
        manageClearButton();

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ClearableEditText et = ClearableEditText.this;

                // Is there an X showing?
                if (et.getCompoundDrawables()[2] == null)
                    return false;
                // Only do this for up touches
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                // Is touch on our clear button?
                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgX.getIntrinsicWidth
                        ()) {
                    et.setText("");
                    ClearableEditText.this.removeClearButton();
                }
                return false;
            }

        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                ClearableEditText.this.manageClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }
        });
    }

    void manageClearButton() {
        if (this.getText()
                .toString()
                .equals(""))
            removeClearButton();
        else
            addClearButton();
    }

    void addClearButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1],
                imgX,
                this.getCompoundDrawables()[3]);
    }

    void removeClearButton() {
        this.setCompoundDrawables(this.getCompoundDrawables()[0],
                this.getCompoundDrawables()[1],
                null,
                this.getCompoundDrawables()[3]);
    }

}

