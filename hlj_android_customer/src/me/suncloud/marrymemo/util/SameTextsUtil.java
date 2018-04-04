package me.suncloud.marrymemo.util;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by wangtao on 2016/12/5.
 */

public class SameTextsUtil implements TextWatcher {

    private TextView[] textViews;

    public SameTextsUtil(@NonNull TextView... textViews) {
        if (this.textViews != null) {
            for (TextView textView : this.textViews) {
                textView.removeTextChangedListener(this);
            }
        }
        this.textViews = textViews;
        for (TextView textView : textViews) {
            textView.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        for (TextView textView : textViews) {
            if(!textView.getText().toString().equals(editable.toString())) {
                textView.removeTextChangedListener(this);
                textView.setText(editable);
                textView.addTextChangedListener(this);
            }
        }
    }
}
