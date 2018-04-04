package com.hunliji.hljcommonlibrary.views.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 通讯录右边的滑动条
 * Created by chen_bin on 12/2/23.
 */
public class SideBar extends View {
    private Context context;
    private List<String> letters;
    private Paint paint;
    private float itemHeight;
    private int choose = -1;
    private OnLetterChangedListener onLetterChangedListener;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paint = new Paint();
        this.context = context;
        this.letters = new ArrayList<>();
        this.itemHeight = CommonUtil.dp2px(context, 300) / 27;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ContextCompat.getColor(context, R.color.colorLink));
        paint.setTextSize(getResources().getDimension(R.dimen.small_text_size));
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0, size = letters.size(); i < size; i++) {
            canvas.drawText(letters.get(i), widthCenter, (i + 1) * itemHeight, paint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnLetterChangedListener listener = onLetterChangedListener;
        final int c = (int) (y / getHeight() * letters.size());// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        switch (action) {
            case MotionEvent.ACTION_UP:
                choose = -1;
                invalidate();
                break;
            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < letters.size()) {
                        if (listener != null) {
                            listener.onLetterChanged(letters.get(c));
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setLetters(List<String> letters) {
        this.letters.clear();
        if (!CommonUtil.isCollectionEmpty(letters)) {
            this.letters.addAll(letters);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            params.height = Math.round(letters.size() * itemHeight);
        }
        requestLayout();
    }

    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface OnLetterChangedListener {
        void onLetterChanged(String str);
    }
}
