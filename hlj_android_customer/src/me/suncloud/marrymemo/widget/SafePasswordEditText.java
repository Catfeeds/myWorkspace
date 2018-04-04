package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by werther on 15/12/30.
 */
public class SafePasswordEditText extends FrameLayout implements SafeKeyboardView.OnInputListener {
    private List<ImageView> imgs;
    private int checkedCount;
    private static int length = 6;
    private StringBuilder stringBuilder;
    private boolean complete = false;
    private boolean empty = true;
    private onSafeEditTextListener onSafeEditTextListener;

    public SafePasswordEditText(Context context) {
        super(context);
        init(context);
    }

    public SafePasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SafePasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.safe_password_edit_text_view, this, true);

        ImageView img1 = (ImageView) findViewById(R.id.img_1);
        ImageView img2 = (ImageView) findViewById(R.id.img_2);
        ImageView img3 = (ImageView) findViewById(R.id.img_3);
        ImageView img4 = (ImageView) findViewById(R.id.img_4);
        ImageView img5 = (ImageView) findViewById(R.id.img_5);
        ImageView img6 = (ImageView) findViewById(R.id.img_6);
        imgs = new ArrayList<>();
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);

        checkedCount = 0;

        refreshCheckCount();
        stringBuilder = new StringBuilder();
    }

    public boolean addNew() {
        checkedCount++;
        if (checkedCount > length) {
            checkedCount = length;
        }

        refreshCheckCount();

        return checkedCount == length;
    }

    public boolean removeOne() {
        checkedCount--;
        if (checkedCount < 0) {
            checkedCount = 0;
        }

        refreshCheckCount();

        return checkedCount == 0;
    }

    public int getTextCount() {
        return checkedCount;
    }

    public boolean isComplete() {
        return checkedCount == length;
    }

    private void refreshCheckCount() {
        for (int i = 0; i < length; i++) {
            if (i < checkedCount) {
                imgs.get(i).setImageResource(R.drawable.sp_oval_line);
            } else {
                imgs.get(i).setImageDrawable(null);
            }
        }
        invalidate();
    }

    @Override
    public void onKeyInput(String s) {
        empty = false;
        if (!complete) {
            stringBuilder.append(s);
        }
        complete = this.addNew();

        if (onSafeEditTextListener != null) {
            if (complete) {
                onSafeEditTextListener.onInputComplete(stringBuilder.toString());
            }
            if (empty) {
                onSafeEditTextListener.onEditTextEmpty();
            }
            onSafeEditTextListener.onTextChange(stringBuilder.toString(), complete);
        }
    }

    @Override
    public void onDelete() {
        complete = false;
        if (!empty) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        empty = this.removeOne();

        if (onSafeEditTextListener != null) {
            if (complete) {
                onSafeEditTextListener.onInputComplete(stringBuilder.toString());
            }
            if (empty) {
                onSafeEditTextListener.onEditTextEmpty();
            }
            onSafeEditTextListener.onTextChange(stringBuilder.toString(), complete);
        }
    }

    public void clearEditText() {
        checkedCount = 0;

        refreshCheckCount();
        stringBuilder = new StringBuilder();
        complete = false;
        empty = true;

        if (onSafeEditTextListener != null) {
            if (complete) {
                onSafeEditTextListener.onInputComplete(stringBuilder.toString());
            }
            if (empty) {
                onSafeEditTextListener.onEditTextEmpty();
            }
            onSafeEditTextListener.onTextChange(stringBuilder.toString(), complete);
        }
    }

    public void setOnSafeEditTextListener(SafePasswordEditText.onSafeEditTextListener
                                                  onSafeEditTextListener) {
        this.onSafeEditTextListener = onSafeEditTextListener;
    }

    public interface onSafeEditTextListener {
        void onInputComplete(String string);

        void onEditTextEmpty();

        void onTextChange(String string, boolean complete);
    }
}
