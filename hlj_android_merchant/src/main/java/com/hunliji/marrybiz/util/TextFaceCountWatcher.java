package com.hunliji.marrybiz.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljemojilibrary.EmojiUtil;

/**
 * Created by Suncloud on 2015/7/13.
 */
public class TextFaceCountWatcher implements TextWatcher {

    private EditText editText;
    private TextView numText;
    private Context context;
    private CharSequence ss;
    private int limitCount;
    private int faceSize;
    private int start;
    private int count;
    private AfterTextChangedListener afterTextChangedListener;


    public TextFaceCountWatcher(
            Context context, EditText editText, TextView numText, int limitCount, int faceSize) {
        this.context = context;
        this.numText = numText;
        this.editText = editText;
        this.limitCount = limitCount;
        this.faceSize = faceSize;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count > 0) {
            DynamicDrawableSpan[] spans = editText.getText()
                    .getSpans(start, start + count, DynamicDrawableSpan.class);
            int size = spans.length;
            if (size > 0) {
                for (DynamicDrawableSpan span : spans) {
                    editText.getText()
                            .removeSpan(span);
                }
            }

        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ss = s.subSequence(start, start + count)
                .toString();
        this.start = start;
        this.count = count;
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
            if (ss.length() > 0) {
                ss = ss.subSequence(0, ss.length() - outCount);
                count -= outCount;
            }
            editEnd = editStart;
            outCount = Util.getTextLength(s) - limitCount;
        }
        if (numText != null) {
            numText.setText(String.valueOf(limitCount - Util.getTextLength(s)));
        }
        if (ss.length() > 0) {
            editText.getText()
                    .replace(start,
                            start + count,
                            EmojiUtil.parseEmojiByText(context, ss.toString(), faceSize));
        }
        editText.setSelection(editStart);
        if (afterTextChangedListener != null) {
            afterTextChangedListener.afterTextChanged(s);
        }
        editText.addTextChangedListener(this);
    }

    public interface AfterTextChangedListener {
        void afterTextChanged(Editable s);
    }

    public void setAfterTextChangedListener(AfterTextChangedListener afterTextChangedListener) {
        this.afterTextChangedListener = afterTextChangedListener;
    }
}
