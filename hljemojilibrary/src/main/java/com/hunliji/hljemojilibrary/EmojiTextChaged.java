package com.hunliji.hljemojilibrary;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.EditText;

/**
 * Created by Suncloud on 2016/10/15.
 */

public class EmojiTextChaged implements TextWatcher {

    private EditText editText;
    private int faceSize;

    public EmojiTextChaged(EditText editText, int faceSize) {
        this.editText = editText;
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
        String ss = s.subSequence(start, start + count)
                .toString();
        if (ss.length() > 0) {
            editText.removeTextChangedListener(this);
            editText.getText()
                    .replace(start,
                            start + count,
                            EmojiUtil.parseEmojiByText(editText.getContext(), ss, faceSize));
            editText.addTextChangedListener(this);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
