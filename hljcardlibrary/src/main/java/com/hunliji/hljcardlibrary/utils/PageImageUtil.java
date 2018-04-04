package com.hunliji.hljcardlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcardlibrary.models.Font;
import com.hunliji.hljcardlibrary.models.HoleFrame;
import com.hunliji.hljcardlibrary.models.TextHole;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Suncloud on 2016/5/12.
 */
public class PageImageUtil {

    private static LongSparseArray<String> pageThumbList;
    private static LongSparseArray<String> cardThumbList;

    public static File getPageThumbFile(Context context, long id) {
        String filePath = getPageThumbList(context).get(id);
        if (!TextUtils.isEmpty(filePath)) {
            return new File(filePath);
        }
        return null;
    }

    public static File getCardThumbFile(Context context, long id) {
        String filePath = getCardThumbList(context).get(id);
        if (!TextUtils.isEmpty(filePath)) {
            return new File(filePath);
        }
        return null;
    }

    private static LongSparseArray<String> getPageThumbList(Context context) {
        if (pageThumbList != null) {
            return pageThumbList;
        }
        String listStr = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE)
                .getString("page_thumb_list", null);
        pageThumbList = GsonUtil.getGsonInstance()
                .fromJson(listStr, new TypeToken<LongSparseArray<String>>() {}.getType());
        if (pageThumbList == null) {
            pageThumbList = new LongSparseArray<>();
        }
        return pageThumbList;
    }

    private static LongSparseArray<String> getCardThumbList(Context context) {
        if (cardThumbList != null) {
            return cardThumbList;
        }
        String listStr = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE)
                .getString("card_thumb_list", null);
        cardThumbList = GsonUtil.getGsonInstance()
                .fromJson(listStr, new TypeToken<LongSparseArray<String>>() {}.getType());
        if (cardThumbList == null) {
            cardThumbList = new LongSparseArray<>();
        }
        return cardThumbList;
    }


    public static void editPageThumbList(Context context, long id, String path) {
        if (pageThumbList == null) {
            pageThumbList = new LongSparseArray<>();
        }
        pageThumbList.put(id, path);
        context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("page_thumb_list",
                        GsonUtil.getGsonInstance()
                                .toJson(pageThumbList))
                .apply();
    }


    public static void editCardThumbList(Context context, long id, String path) {
        if (cardThumbList == null) {
            cardThumbList = new LongSparseArray<>();
        }
        cardThumbList.put(id, path);
        context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE)
                .edit()
                .putString("card_thumb_list",
                        GsonUtil.getGsonInstance()
                                .toJson(cardThumbList))
                .apply();
    }

    public static final float CREATE_SCALE = 1.44f;

    public static File createTextImage(Context context, TextHole textHole, String content) {
        HoleFrame frame = textHole.getHoleFrame();
        int width = (int) (frame.getWidth() * CREATE_SCALE);
        int height = (int) (frame.getHeight() * CREATE_SCALE);
        int textSize = (int) (textHole.getFontSize() * CREATE_SCALE);
        float lineSpace = textHole.getLineSpace() * CREATE_SCALE;
        if (width == 0 || height == 0 || textSize == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        TextPaint paintText = new TextPaint();
        Typeface typeface = null;
        Font font = FontUtil.getInstance()
                .getFont(context, textHole.getFontName());
        if (font != null && !TextUtils.isEmpty(font.getUrl())) {
            try {
                File fontFile = FileUtil.createFontFile(context, font.getUrl());
                if (fontFile != null && fontFile.exists() && fontFile.length() > 0) {
                    typeface = Typeface.createFromFile(fontFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (typeface == null) {
            typeface = Typeface.DEFAULT;
        }
        try {
            paintText.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paintText.setAntiAlias(true);
        paintText.setColor(textHole.getColor());
        paintText.setTextSize(textSize);
        StaticLayout layout = new StaticLayout(content,
                paintText,
                width,
                textHole.getAlignment(),
                1.0f,
                lineSpace,
                false);
        int translateY = 0;
        if(lineSpace!=0) {
            StaticLayout baseLayout = new StaticLayout(content,
                    paintText,
                    width,
                    textHole.getAlignment(),
                    1.0f,
                    0,
                    false);
            try {
                translateY = baseLayout.getLineBaseline(0) - layout.getLineBaseline(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.translate(0, translateY);
        layout.draw(canvas);
        canvas.restore();
        File file = FileUtil.createPageFile(context, textHole.getId() + content);
        try {
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        }
        return null;
    }
}
