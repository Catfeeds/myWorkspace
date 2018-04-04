package me.suncloud.marrymemo.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.model.V2.ImageInfoV2;
import me.suncloud.marrymemo.model.V2.TemplateV2;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/5/17.
 */
public class DrawPageTask extends AsyncTask<Object, Object, String> {

    private final WeakReference<Context> contextReference;
    private CardPageV2 cardPage;
    private OnHttpRequestListener listener;

    public DrawPageTask(Context context, CardPageV2 cardPage, OnHttpRequestListener listener) {
        this.contextReference = new WeakReference<>(context);
        this.cardPage = cardPage;
        this.listener = listener;
    }

    public void setListener(OnHttpRequestListener listener) {
        this.listener = listener;
    }

    public String getCardPageKey() {
        return cardPage.getCardPageKey();
    }

    @Override
    protected String doInBackground(Object[] params) {
        TemplateV2 template = cardPage.getTemplate();
        if (template.getWidth() == 0 || template.getHeight() == 0) {
            return null;
        }
        Bitmap pageBitmap = Bitmap.createBitmap(template.getWidth(),
                template.getHeight(),
                Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(pageBitmap);
            for (ImageHoleV2 imageHole : cardPage.getImageHoles()) {
                for (ImageInfoV2 imageInfo : cardPage.getImages()) {
                    if (imageHole.getId() != imageInfo.getHoleId()) {
                        continue;
                    }
                    if (!JSONUtil.isEmpty(imageInfo.getH5ImagePath())) {
                        String imagePath = imageInfo.getH5ImagePath();
                        if (contextReference.get() == null || isCancelled()) {
                            return null;
                        }
                        File file = FileUtil.createPageFile(contextReference.get(), imagePath);
                        Bitmap bitmap = JSONUtil.getImageFromUrl(contextReference.get(),
                                imagePath,
                                file,
                                imageHole.getW(),
                                imageHole.getH(),
                                Bitmap.Config.RGB_565);
                        if (bitmap != null) {
                            Bitmap newBitmap = Bitmap.createBitmap(imageHole.getW(),
                                    imageHole.getH(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas imageCanvas = new Canvas(newBitmap);
                            imageCanvas.drawBitmap(bitmap, 0, 0, null);
                            bitmap.recycle();
                            String maskPath = imageHole.getMaskImagePath();
                            if (!JSONUtil.isEmpty(maskPath)) {
                                if (contextReference.get() == null || isCancelled()) {
                                    newBitmap.recycle();
                                    Paint clearPaint = new Paint();
                                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode
                                            .CLEAR));
                                    imageCanvas.drawPaint(clearPaint);
                                    return null;
                                }
                                file = FileUtil.createThemeFile(contextReference.get(), maskPath);
                                Bitmap maskBitmap = JSONUtil.getImageFromUrl(contextReference.get(),
                                        maskPath,
                                        file,
                                        imageHole.getW(),
                                        imageHole.getH(),
                                        Bitmap.Config.ALPHA_8);
                                if (maskBitmap != null) {
                                    Rect rectF = new Rect(0, 0, imageHole.getW(), imageHole.getH());
                                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                    paint.setAntiAlias(true);
                                    paint.setFilterBitmap(true);
                                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode
                                            .DST_IN));
                                    imageCanvas.drawBitmap(maskBitmap, null, rectF, paint);
                                    maskBitmap.recycle();
                                }
                            }
                            canvas.drawBitmap(newBitmap, imageHole.getX(), imageHole.getY(), null);
                            newBitmap.recycle();
                            Paint clearPaint = new Paint();
                            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            imageCanvas.drawPaint(clearPaint);
                        }
                    }
                }
            }
            String backgroundPath = template.getBackground();
            if (JSONUtil.isEmpty(backgroundPath)) {
                return null;
            }
            if (contextReference.get() == null || isCancelled()) {
                return null;
            }
            File file = FileUtil.createThemeFile(contextReference.get(), backgroundPath);
            Bitmap bitmap = JSONUtil.getImageFromUrl(contextReference.get(),
                    backgroundPath,
                    file,
                    template.getWidth(),
                    0);
            if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
                return null;
            }
            float scale = Math.max((float) template.getWidth() / bitmap.getWidth(),
                    (float) template.getHeight() / bitmap.getHeight());
            if (scale != 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                matrix.postTranslate((template.getWidth() - bitmap.getWidth() * scale) / 2,
                        (template.getHeight() - bitmap.getHeight() * scale) / 2);
                canvas.drawBitmap(bitmap, matrix, null);
            } else {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
            bitmap.recycle();
            for (TextHoleV2 textHole : cardPage.getTexts()) {
                if (JSONUtil.isEmpty(textHole.getContent()) || !textHole.isShowText()) {
                    continue;
                }
                Typeface typeface = null;
                if (!JSONUtil.isEmpty(textHole.getFontName())) {
                    Font font = CardResourceUtil.getInstance()
                            .getFont(contextReference.get(), textHole.getFontName());
                    if (font != null && !font.isUnSaved(contextReference.get())) {
                        try {
                            typeface = Typeface.createFromFile(FileUtil.createFontFile
                                    (contextReference.get(),
                                            font.getUrl()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                if (JSONUtil.isEmpty(textHole.getFontName()) || typeface != null) {
                    if (!JSONUtil.isEmpty(textHole.getType())) {
                        switch (textHole.getType()) {
                            case "groom":
                            case "bride":
                            case "time":
                            case "lunar":
                                setDrawText(textHole, canvas, true, true, typeface);
                                break;
                            default:
                                setDrawText(textHole, canvas, false, true, typeface);
                                break;
                        }
                    } else {
                        setDrawText(textHole, canvas, false, false, typeface);
                    }
                } else if (!JSONUtil.isEmpty(textHole.getH5TextPath())) {
                    if (contextReference.get() == null || isCancelled()) {
                        return null;
                    }
                    Bitmap textBitmap = JSONUtil.getImageFromUrl(contextReference.get(),
                            textHole.getH5TextPath(),
                            null,
                            0,
                            0);
                    if (textBitmap != null) {
                        canvas.save();
                        canvas.translate(textHole.getLeft(), textHole.getTop());
                        canvas.rotate(textHole.getAngle(),
                                textHole.getWidth() / 2,
                                textHole.getHeight() / 2);
                        try {
                            Rect rectF = new Rect(0, 0, textHole.getWidth(), textHole.getHeight());
                            canvas.drawBitmap(textBitmap, null, rectF, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        canvas.restore();
                        textBitmap.recycle();
                    }
                }
            }
            if (contextReference.get() == null || isCancelled()) {
                return null;
            }
            File pageFile = FileUtil.createPageFile(contextReference.get(),
                    cardPage.getCardPageKey());
            try {
                OutputStream out = new FileOutputStream(pageFile);
                pageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                CardResourceUtil.getInstance()
                        .addPageMapKey(contextReference.get(),
                                cardPage.getId(),
                                cardPage.getCardPageKey());
                return pageFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            pageBitmap.recycle();
            System.gc();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (!JSONUtil.isEmpty(s)) {
            listener.onRequestCompleted(s);
        } else {
            listener.onRequestFailed(null);
        }
        super.onPostExecute(s);
    }

    private void setDrawText(
            TextHoleV2 textHole,
            Canvas canvas,
            boolean singleLine,
            boolean fix,
            Typeface typeface) {
        TextPaint paintText = new TextPaint();
        if (typeface != null) {
            paintText.setTypeface(typeface);
        }
        paintText.setAntiAlias(true);
        int width = textHole.getW();
        int height = textHole.getH();
        paintText.setColor(textHole.getColor());
        paintText.setTextSize(textHole.getFontSize());
        Layout.Alignment alignment;
        if (textHole.getAlignment() == 2) {
            alignment = Layout.Alignment.ALIGN_OPPOSITE;
        } else if (textHole.getAlignment() == 1) {
            alignment = Layout.Alignment.ALIGN_CENTER;
        } else {
            alignment = Layout.Alignment.ALIGN_NORMAL;
        }
        StringBuffer text = new StringBuffer(textHole.getContent());
        if (singleLine) {
            while (paintText.measureText(text.toString()) > width) {
                text = text.deleteCharAt(text.length() - 1);
            }
        }
        if (!fix) {
            String[] texts = text.toString()
                    .split("\\n");
            if (texts.length > 1) {
                for (String lineText : texts) {
                    width = Math.max((int) paintText.measureText(lineText), width);
                }
            } else {
                width = Math.max((int) paintText.measureText(text.toString()), width);
            }
        }
        StaticLayout layout = new StaticLayout(text,
                paintText,
                width,
                alignment,
                1.0F,
                0.0F,
                false);
        if (!fix) {
            height = Math.max(height, layout.getHeight());
        }
        canvas.save();
        canvas.translate(textHole.getX() - (width - textHole.getW()) / 2,
                textHole.getY() - (height - textHole.getH()) / 2);
        canvas.scale(textHole.getScale(), textHole.getScale(), width / 2, height / 2);
        canvas.rotate(textHole.getAngle(), width / 2, height / 2);
        try {
            layout.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        canvas.restore();
    }
}
