package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/5/12.
 */
public class PageTextView extends TextView {

    private TextHoleV2 textHole;
    private boolean isChange;
    private int minWidth;
    private int minHeight;
    private float scale;
    private boolean isHide;
    private int color;

    public void setPageScale(float scale) {
        this.scale = scale;
    }

    public float getPageScale() {
        return scale;
    }

    public int getMyMinHeight() {
        return minHeight;
    }

    public int getMyMinWidth() {
        return minWidth;
    }

    public void setMyMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public void setMyMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public PageTextView(Context context) {
        super(context);
    }

    public void setTextHole(TextHoleV2 textHole) {
        this.textHole = textHole;
    }

    public void setText(String text) {
        super.setText(text);
        if (textHole != null && !text.equals(textHole.getContent())) {
            isChange = true;
        }
    }

    public void setFont(Font font) {
        if (font == null) {
            return;
        }
        Typeface typeface = null;
        if (!JSONUtil.isEmpty(font.getUrl()) && !font.isUnSaved(getContext())) {
            try {
                typeface = Typeface.createFromFile(FileUtil.createFontFile(getContext(),
                        font.getUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (typeface == null) {
            typeface = Typeface.DEFAULT;
        }
        setTypeface(typeface);
        if (textHole != null) {
            isChange = true;
            textHole.setFontName(font.getName());
        }
    }

    @Override
    public void setTextColor(int color) {
        this.color = color;
        if (isHide) {
            super.setTextColor(color & 0x00ffffff);
        } else {
            if (textHole != null && color != textHole.getColor()) {
                isChange = true;
            }
            super.setTextColor(color);
        }
    }

    public int getTextColor() {
        return color;
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        if (textHole != null && scaleX != textHole.getScale()) {
            isChange = true;
        }
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        if (textHole != null && scaleY != textHole.getScale()) {
            isChange = true;
        }
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        if (textHole != null && hide != textHole.isShowText()) {
            isChange = true;
        }
        isHide = hide;
        if (isHide) {
            super.setTextColor(color & 0x00ffffff);
        } else {
            super.setTextColor(color | 0xff000000);
        }
    }

    public TextHoleV2 getTextHole() {
        return textHole;
    }
}
