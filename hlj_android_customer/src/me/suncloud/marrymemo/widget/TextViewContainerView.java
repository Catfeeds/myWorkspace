package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/5/9.
 */
public class TextViewContainerView extends FrameLayout implements TextWatcher {

    private CheckBox btnHide;
    private View btnDrag;
    private PageTextView textView;
    private TextActionListener actionListener;
    private boolean isChange;


    public TextViewContainerView(Context context) {
        super(context);
    }

    public TextViewContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(CheckBox btnHide, View btnDrag, TextActionListener actionListener) {
        this.btnHide = btnHide;
        this.btnDrag = btnDrag;
        this.btnHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textView.setHide(isChecked);
            }
        });
        this.actionListener = actionListener;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (textView == null) {
                    return false;
                }
                TextViewContainerView.this.actionListener.onTextEditDone(textView);
                return true;
            }
        });
    }

    public PageTextView getTextView() {
        return textView;
    }

    public void onTypefaceChange(Font font) {
        if(textView==null){
            return;
        }
        textView.setFont(font);
        TextHoleV2 textHole = textView.getTextHole();
        if (textHole == null || JSONUtil.isEmpty(textHole.getType())) {
            int width = (int) textView.getPaint().measureText("最小");
            int height = new StaticLayout("最小", textView
                    .getPaint(), width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false)
                    .getHeight();
            textView.setMyMinHeight(height);
            textView.setMyMinWidth(width);
            String content = textView.getText().toString();
            if (!JSONUtil.isEmpty(content)) {
                String[] texts = content.split("\\n");
                if (texts.length > 1) {
                    for (String lineText : texts) {
                        width = Math.max((int) textView.getPaint().measureText(lineText),
                                width);
                    }
                } else {
                    width = Math.max((int) textView.getPaint().measureText(content), width);
                }
                StaticLayout layout = new StaticLayout(content, textView
                        .getPaint(), width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                height = Math.max(height, layout.getHeight());
            }
            resetTextViewLayout(height, width);
        }
    }

    public void onTextChange(String string) {
        if (textView.getText().toString().equals(string)) {
            return;
        }
        TextHoleV2 textHole = textView.getTextHole();
        if (textHole == null || JSONUtil.isEmpty(textHole.getType())) {
            int width = textView.getMyMinWidth();
            int height = textView.getMyMinHeight();
            if (!JSONUtil.isEmpty(string)) {
                String[] texts = string.split("\\n");
                if (texts.length > 1) {
                    for (String lineText : texts) {
                        width = Math.max(Math.round(textView.getPaint().measureText(lineText)),
                                width);
                    }
                } else {
                    width = Math.max(Math.round(textView.getPaint().measureText(string)), width);
                }
                StaticLayout layout = new StaticLayout(string, textView
                        .getPaint(), width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                height = Math.max(height, layout.getHeight());
            }
            resetTextViewLayout(height, width);
        }
        textView.setText(string);
    }

    private void resetTextViewLayout(int height, int width) {
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        float scale = textView.getPageScale();
        int w = textView.getWidth();
        int h = textView.getHeight();
        float x = (textView.getLeft() - w * (scale - 1) / 2) / scale - width / 2 + w / 2;
        float y = (textView.getTop() - h * (scale - 1) / 2) / scale - height / 2 + h / 2;
        int top = Math.round((y * scale + height * (scale - 1) / 2));
        int left = Math.round((x * scale + width * (scale - 1) / 2));
        textView.setPivotX(width / 2);
        textView.setPivotY(height / 2);
        textView.layout(left, top, left + width, top + height);
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        setButtonPosition();
        textView.requestLayout();
    }

    public void setTextView(PageTextView tv) {
        if (textView != null) {
            if (tv != textView) {
                textView.setOnTouchListener(null);
                textView.setBackgroundResource(R.drawable.bg_card_page_v2_text);
                LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.leftMargin = textView.getLeft();
                layoutParams.topMargin = textView.getTop();
                textView = null;
            } else {
                return;
            }
        }
        if(tv==null){
            TextViewContainerView.this.btnHide.setVisibility(GONE);
            TextViewContainerView.this.btnDrag.setVisibility(GONE);
            TextViewContainerView.this.btnDrag.setOnTouchListener(null);
            return;
        }
        this.textView = tv;
        textView.setBackgroundResource(R.drawable.sp_stroke_primary);
        actionListener.onTextEdit(tv);
        TextHoleV2 textHole = tv.getTextHole();
        if (textHole == null || JSONUtil.isEmpty(textHole.getType())) {
            textView.setOnTouchListener(new TextMoveTouch());
            btnHide.setChecked(textView.isHide());
            btnHide.setVisibility(VISIBLE);
            btnDrag.setVisibility(VISIBLE);
            btnDrag.setOnTouchListener(new DragTouchListener());
        } else {
            btnHide.setVisibility(GONE);
            btnDrag.setVisibility(GONE);
            btnDrag.setOnTouchListener(null);
        }
        setButtonPosition();
    }

    private void setButtonPosition() {
        TextHoleV2 textHole = textView.getTextHole();
        if (textHole != null && !JSONUtil.isEmpty(textHole.getType())) {
            return;
        }
        float width = textView.getWidth() * textView.getScaleX();
        float height = textView.getHeight() * textView.getScaleY();
        double angle = textView.getRotation() * Math.PI / 180;
        int left = (int) (textView.getLeft() + textView.getWidth() * (1 - textView.getScaleX()) /
                2);
        int top = (int) (textView.getTop() + textView.getHeight() * (1 - textView.getScaleY()) / 2);
        btnDrag.setRotation(textView.getRotation());
        btnHide.setRotation(textView.getRotation());
        btnHide.setX(left + width / 2 - width * (float) Math.cos(angle) / 2 +
                height * (float) Math.sin(angle) / 2 - btnHide.getWidth() / 2);
        btnHide.setY(top + height / 2 - width * (float) Math.sin(angle) / 2 -
                height * (float) Math.cos(angle) / 2 - btnHide.getHeight() / 2);
        btnDrag.setX(left + width / 2 + width * (float) Math.cos(angle) / 2 -
                height * (float) Math.sin(angle) / 2 - btnDrag.getWidth() / 2);
        btnDrag.setY(top + height / 2 + width * (float) Math.sin(angle) / 2 +
                height * (float) Math.cos(angle) / 2 - btnDrag.getHeight() / 2);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (textView != null) {
            onTextChange(s.toString());
        }
    }

    private class TextMoveTouch implements OnTouchListener {

        private int mPreviousX = 0;
        private int mPreviousY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (textView == null) {
                return false;
            }
            final int currentX = (int) event.getX();
            final int currentY = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousX = currentX;
                    mPreviousY = currentY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltX = currentX - mPreviousX;
                    int deltY = currentY - mPreviousY;
                    double angle = v.getRotation() * Math.PI / 180;
                    int x = (int) (deltX * Math.cos(angle) - deltY * Math.sin(angle));
                    int y = (int) (deltX * Math.sin(angle) + deltY * Math.cos(angle));
                    final int iLeft = v.getLeft();
                    final int iTop = v.getTop();
                    if(x<-iLeft-v.getWidth()/2){
                        x= -iLeft-v.getWidth()/2;
                    }else if(x>getWidth()-iLeft-v.getWidth()/2){
                        x= getWidth()-iLeft-v.getWidth()/2;
                    }
                    if(y<-iTop-v.getHeight()/2){
                        y= -iTop-v.getHeight()/2;
                    }else if(y>getHeight()-iTop-v.getHeight()/2){
                        y= getHeight()-iTop-v.getHeight()/2;
                    }
                    if (x != 0 || y != 0) {
                        v.layout(iLeft + x, iTop + y, iLeft + x + v.getWidth(), iTop +
                                y + v.getHeight());
                        setButtonPosition();
                    }
                    mPreviousX = currentX - deltX;
                    mPreviousY = currentY - deltY;
                    isChange=true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
                    layoutParams.leftMargin = textView.getLeft();
                    layoutParams.topMargin = textView.getTop();
                    break;
            }
            return true;
        }
    }

    private class DragTouchListener implements OnTouchListener {

        private int mPreviousX = 0;
        private int mPreviousY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (textView == null) {
                return false;
            }
            final int currentX = (int) event.getX();
            final int currentY = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousX = currentX;
                    mPreviousY = currentY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dealtX = currentX - mPreviousX;
                    int dealtY = currentY - mPreviousY;
                    double angle = v.getRotation() * Math.PI / 180;
                    int x = (int) (dealtX * Math.cos(angle) - dealtY * Math.sin(angle));
                    int y = (int) (dealtX * Math.sin(angle) + dealtY * Math.cos(angle));
                    if (x != 0 || y != 0) {
                        Point centrePoint = new Point((int) textView.getX() + textView.getWidth()
                                / 2,
                                (int) textView.getY() + textView.getHeight() / 2);
                        Point startPoint = new Point((int) textView.getX() + textView.getWidth(),
                                (int) textView.getY() + textView.getHeight());
                        Point currentPoint = new Point((int) v.getX() + x + v.getWidth() / 2,
                                (int) v.getY() + y + v.getHeight() / 2);
                        textView.setPivotX(textView.getWidth() / 2);
                        textView.setPivotY(textView.getHeight() / 2);
                        float rotation = getRotation(centrePoint, startPoint, currentPoint);
                        textView.setRotation(rotation);
                        float scale = getScale(centrePoint, currentPoint, spacing(centrePoint.x,
                                centrePoint.y, startPoint.x, startPoint.y));
                        textView.setScaleX(scale);
                        textView.setScaleY(scale);
                        setButtonPosition();
                    }
                    mPreviousX = currentX - dealtX;
                    mPreviousY = currentY - dealtY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
    }

    private float getRotation(Point centrePoint, Point startPoint, Point currentPoint) {
        float a = spacing(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
        float b = spacing(centrePoint.x, centrePoint.y, startPoint.x, startPoint.y);
        float c = spacing(centrePoint.x, centrePoint.y, currentPoint.x, currentPoint.y);
        if (c == 0) {
            return 0;
        }
        double cosA = (b * b + c * c - a * a) / (2 * c * b);
        if ((currentPoint.y - centrePoint.y) * (startPoint.x - centrePoint.x) < (currentPoint.x -
                centrePoint.x) * (startPoint.y - centrePoint.y)) {
            return (float) (360 - Math.acos(cosA) * 180 / Math.PI);
        } else {
            return (float) (Math.acos(cosA) * 180 / Math.PI);
        }
    }


    private float getScale(Point centrePoint, Point currentPoint, float edge) {
        float b = spacing(centrePoint.x, centrePoint.y, currentPoint.x, currentPoint.y);
        return b / edge;
    }

    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    public interface TextActionListener {

        void onTextEdit(PageTextView textView);

        boolean onTextEditDone(PageTextView textView);
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }
}
