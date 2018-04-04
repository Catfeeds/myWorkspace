package com.hunliji.marrybiz.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Suncloud on 2015/5/21.
 */
public class TextCountWatcher implements TextWatcher {

    private EditText editText;
    private TextView numText;
    private int limitCount;


    public TextCountWatcher(EditText editText, int limitCount) {
        this.editText = editText;
        this.limitCount = limitCount;
    }

    public TextCountWatcher(EditText editText, TextView numText, int limitCount) {
        this.editText = editText;
        this.numText = numText;
        this.limitCount = limitCount;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int editStart = editText.getSelectionStart();
        int editEnd = editText.getSelectionEnd();
        editText.removeTextChangedListener(this);
        if (editStart == 0) {
            editStart = s.length();
            editEnd = s.length();
            if (editStart > limitCount * 2) {
                editStart = limitCount * 2;
            }
        }
        int outCount = Util.getTextLength(s) - limitCount;
        while (outCount > 0) {
            editStart -= outCount;
            s.delete(editStart, editEnd);
            editEnd = editStart;
            outCount = Util.getTextLength(s) - limitCount;
        }
        if (numText != null) {
            numText.setText(String.valueOf(limitCount - Util.getTextLength(s)));
        }
        editText.setSelection(editStart);
        editText.addTextChangedListener(this);
    }
}
