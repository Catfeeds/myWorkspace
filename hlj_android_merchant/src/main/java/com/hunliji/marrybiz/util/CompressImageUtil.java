package com.hunliji.marrybiz.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 图片压缩的工具类
 * Created by jinxin on 2015/8/27.
 */
public class CompressImageUtil {
    /**
     * 等宽高比例缩放bitmap
     * for example w:h 16:10
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap compressBimap(Bitmap bitmap, int width, int height) {
        int mapWidth = bitmap.getWidth();
        int mapHeight = bitmap.getHeight();
        float sx = width * 1.0f / mapWidth;
        float sy = height * 1.0f / mapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, mapWidth, mapHeight, matrix, true);
    }


    /**
     * 按照大小比例压缩图片
     *
     * @param compressBitmap
     * @param compressedBitmapSize
     * @return
     */
    public static Bitmap compressBitmap(Bitmap compressBitmap, int compressedBitmapSize) {
        int optation = 100;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        compressBitmap.compress(Bitmap.CompressFormat.JPEG, optation, byteout);
        while ((byteout.size() / 1024) > compressedBitmapSize) {
            optation -= 5;
            byteout.reset();
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, optation, byteout);
        }
        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bytein, null, null);
        return bitmap;
    }

    /**
     * 宽高 比例压缩
     *
     * @param compressBitmap
     * @param resolutionWidth
     * @param resolutionHeight
     * @param compressedBitmapSize
     * @return
     */
    public static Bitmap compressBitmap(Bitmap compressBitmap, float resolutionWidth, float resolutionHeight, int compressedBitmapSize) {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        int optionCount = 100;
        compressBitmap.compress(Bitmap.CompressFormat.JPEG, optionCount, byteout);
        while ((byteout.size() / 1024) > 1024) {
            byteout.reset();
            optionCount -= 5;
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, optionCount, byteout);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bytein, null, options);
        options.inJustDecodeBounds = false;

        int width = options.outWidth;
        int height = options.outHeight;
        int count = 1;
        if (width > height && width > resolutionWidth) {
            count = (int) (width / resolutionWidth);
        } else if (width < height && height > resolutionHeight) {
            count = (int) (height / resolutionHeight);
        }
        if (count <= 0) {
            count = 1;
        }
        options.inSampleSize = count;
        bytein = new ByteArrayInputStream(byteout.toByteArray());
        bitmap = BitmapFactory.decodeStream(bytein, null, options);
        return compressBitmap(bitmap, compressedBitmapSize);
    }

}
