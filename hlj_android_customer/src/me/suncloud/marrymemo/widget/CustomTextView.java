package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hunliji.hljemojilibrary.EmojiUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.util.JSONUtil;

public class CustomTextView extends TextView {

    private Paint testPaint;
    private Context context;
    private String textString;
    private int spanWidth;
    private int maxWidth;
    private int verticalAlignment;
    private Pattern facePattern = Pattern.compile("\\[\\w+\\]");


    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialise();
    }

    public CustomTextView(Context context) {
        super(context);
    }

    ;

    private void initialise() {
        testPaint = new Paint();
        testPaint.set(this.getPaint());
    }

    private float getTextViewLength(String text) {
        return testPaint.measureText(text);
    }

    public void setImageSpanText(
            String text, int spanWidth, int verticalAlignment, int maxWidth) {
        if (JSONUtil.isEmpty(text)) {
            setText(text);
            return;
        }
        this.maxWidth = maxWidth;
        this.spanWidth = spanWidth;
        this.verticalAlignment = verticalAlignment;
        this.textString = text;
        setNewText();
    }

    private void setNewText() {
        float width = this.maxWidth - getTextViewLength("…") - getPaddingLeft() - getPaddingRight();
        StringBuilder builder = new StringBuilder();
        Matcher matcher = facePattern.matcher(textString);
        float textWidth = 0;
        int last = 0;
        boolean toLong = false;
        while (matcher.find()) {
            String tag = matcher.group(0);
            textWidth += getTextViewLength(textString.substring(last, matcher.start()));
            builder.append(textString.substring(last, matcher.start()));
            if (textWidth > width) {
                builder.append("…");
                toLong = true;
                break;
            }
            int resId = EmojiUtil.getImageResFromTag(tag, context);
            last = matcher.end();
            if (resId != 0) {
                textWidth += spanWidth;
                if (textWidth > width) {
                    builder.append("…");
                    toLong = true;
                    break;
                } else {
                    builder.append(tag);
                }
            } else {
                textWidth += getTextViewLength(textString.substring(matcher.start(), last));
                builder.append(tag);
            }
        }
        if (builder.length() > 0 && toLong) {
            setText(EmojiUtil.parseEmojiByText2(context,
                    builder.toString(),
                    spanWidth));
        } else {
            setText(EmojiUtil.parseEmojiByText(context,
                    textString,
                    spanWidth));
        }
    }

}
