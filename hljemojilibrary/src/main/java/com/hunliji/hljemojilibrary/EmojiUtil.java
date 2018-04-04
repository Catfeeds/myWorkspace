package com.hunliji.hljemojilibrary;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by werther on 16/7/28.
 */
public class EmojiUtil {


    /**
     * 表情图片匹配
     */
    private static Pattern emojiPattern = Pattern.compile("\\[[^\\[\\]]+\\]");
    private static LinkedHashMap<String, Integer> emojiMap;
    private static LinkedHashMap<String, Integer> emEmojiMap;

    public static SpannableStringBuilder parseEmojiByText(
            Context context, String content, int faceSize) {
        return parseEmojiByText(context, content, faceSize, 0, ImageSpan.ALIGN_BOTTOM);
    }


    public static SpannableStringBuilder parseEmojiByText2(
            Context context, String content, int faceSize) {
        return parseEmojiByText(context, content, faceSize, 0, HljImageSpan.ALIGN_CENTER);
    }


    public static SpannableStringBuilder parseEmojiByTextForHeight(
            Context context, String content, int faceSize,int textHeight) {
        return parseEmojiByText(context, content, faceSize, textHeight, ImageSpan.ALIGN_BOTTOM);
    }

    public static SpannableStringBuilder parseEmojiByText(
            Context context, String content, int faceSize, int textHeight, int verticalAlignment) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher = emojiPattern.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(0);
            int resId = getImageResFromTag(tag, context);
            if (resId != 0) {
                try {
                    Drawable d = ContextCompat.getDrawable(context, resId);
                    if(textHeight>0){
                        int top=(textHeight-faceSize)/2;
                        d.setBounds(0, top, faceSize, faceSize+top);
                    }else {
                        d.setBounds(0, 0, faceSize, faceSize);// 设置表情图片的显示大小
                    }
                    HljImageSpan span = new HljImageSpan(d, verticalAlignment);
                    builder.setSpan(span,
                            matcher.start(),
                            matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return builder;
    }

    public static int getImageResFromTag(String tag, Context context) {
        if (!TextUtils.isEmpty(tag)) {
            if (emojiMap == null) {
                emojiMap = getFaceMap(context);
            }
            if (emEmojiMap == null) {
                emEmojiMap = getEMFaceMap(context);
            }
            return emojiMap.get(tag) == null ? (emEmojiMap.get(tag) == null ? 0 : emEmojiMap.get
                    (tag)) : emojiMap.get(
                    tag);
        }
        return 0;
    }

    public static LinkedHashMap<String, Integer> getFaceMap(Context context) {
        if (emojiMap == null) {
            LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
            XmlResourceParser parser = context.getResources()
                    .getXml(R.xml.face_tree);
            try {
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName()
                                .equals("face")) {
                            String mTag = null;
                            int mImageId = 0;
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                int value = parser.getAttributeResourceValue(i, 0);
                                if (parser.getAttributeName(i)
                                        .equals("resource")) {
                                    mImageId = value;
                                } else if (parser.getAttributeName(i)
                                        .equals("title")) {
                                    mTag = context.getString(value);
                                }
                            }
                            if (mImageId != 0 && !TextUtils.isEmpty(mTag)) {
                                map.put(mTag, mImageId);
                            }
                        }
                    }
                    eventType = parser.next();
                }
                if (!map.isEmpty()) {
                    return map;
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                parser.close();
            }
        }
        return emojiMap;

    }

    public static LinkedHashMap<String, Integer> getEMFaceMap(Context context) {
        if (emEmojiMap == null || emEmojiMap.isEmpty()) {
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            XmlResourceParser parser = context.getResources()
                    .getXml(R.xml.em_face_tree);
            try {
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName()
                                .equals("face")) {
                            String mTag = null;
                            int mImageId = 0;
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                int value = parser.getAttributeResourceValue(i, 0);
                                if (parser.getAttributeName(i)
                                        .equals("resource")) {
                                    mImageId = value;
                                } else if (parser.getAttributeName(i)
                                        .equals("title")) {
                                    mTag = context.getString(value);
                                }
                            }
                            if (mImageId != 0 && !TextUtils.isEmpty(mTag)) {
                                map.put(mTag, mImageId);
                            }
                        }
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                parser.close();
            }
            return map;
        }
        return emEmojiMap;
    }

    public static void deleteTextOrImage(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start == end) {
            DynamicDrawableSpan[] imageSpan = editText.getText()
                    .getSpans(0, end, DynamicDrawableSpan.class);
            int size = imageSpan.length;
            boolean delSpan = false;
            if (size > 0) {
                for (DynamicDrawableSpan anImageSpan : imageSpan) {
                    int spanEnd = editText.getText()
                            .getSpanEnd(anImageSpan);
                    if (start == spanEnd) {
                        delSpan = true;
                        int spanStart = editText.getText()
                                .getSpanStart(anImageSpan);
                        editText.getText()
                                .delete(spanStart, spanEnd);
                        break;
                    }
                }
            }
            if (!delSpan && start > 0) {
                editText.getText()
                        .delete(start - 1, start);
            }
        } else {
            editText.getText()
                    .delete(start, end);
        }
    }

}
