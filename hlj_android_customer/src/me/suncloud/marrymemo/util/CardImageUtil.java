package me.suncloud.marrymemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.model.V2.TextHoleV2;
import me.suncloud.marrymemo.widget.DraggableImageView;

/**
 * Created by Suncloud on 2016/5/12.
 */
public class CardImageUtil {

    public static File createTextImage(Context context, TextHoleV2 textHole) {
        Bitmap bitmap = Bitmap.createBitmap(textHole.getWidth(),
                textHole.getHeight(),
                Bitmap.Config.ARGB_8888);
        TextPaint paintText = new TextPaint();
        if (!JSONUtil.isEmpty(textHole.getFontName())) {
            for (Font font : CardResourceUtil.getInstance()
                    .getFonts(context)) {
                if (textHole.getFontName()
                        .equals(font.getName())) {
                    if (!font.isUnSaved(context)) {
                        try {
                            paintText.setTypeface(Typeface.createFromFile(FileUtil.createFontFile(
                                    context,
                                    font.getUrl())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
        paintText.setAntiAlias(true);
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
        if (textHole.isSingleLine()) {
            while (paintText.measureText(text.toString()) > textHole.getW()) {
                text = text.deleteCharAt(text.length() - 1);
            }
        }
        StaticLayout layout = new StaticLayout(text,
                paintText,
                textHole.getW(),
                alignment,
                1.0F,
                0.0F,
                false);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.scale(textHole.getScale(), textHole.getScale());
        layout.draw(canvas);
        canvas.restore();
        File file = FileUtil.createPageFile(context, textHole.getId() + text.toString());
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

    public static File createHoleImage(
            Context context, ImageHoleV2 imageHole, DraggableImageView imageView, float scale) {
        Bitmap bitmap = imageView.getBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(imageHole.getW(),
                imageHole.getH(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        Matrix matrix = new Matrix();
        matrix.set(imageView.getImageMatrix());
        matrix.postScale(1 / scale, 1 / scale);
        canvas.drawBitmap(bitmap, matrix, paint);
        File file = FileUtil.createPageFile(context, "h5" + imageHole.getPath());
        try {
            OutputStream out = new FileOutputStream(file);
            newBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!newBitmap.isRecycled()) {
                newBitmap.recycle();
                System.gc();
            }
        }

        return null;
    }
}
