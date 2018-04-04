package com.hunliji.hljimagelibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljimagelibrary.models.Size;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by werther on 16/7/28.
 */
public class ImageUtil {

    private static int MaximumTextureSize;
    private static String signOr;

    public static String getAvatar(String str) {
        return getAvatar(str, 200);
    }

    public static String getAvatar(String str, int size) {
        return TextUtils.isEmpty(str) ? null : (str.startsWith("http") ? (str.endsWith(".avatar")
                ? str : (str + (str.contains(
                "?") ? String.format(HljCommon.QINIU.PHOTO_URL3, size, size)
                .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL3,
                size,
                size)))) : HljHttp.getHOST() + str);
    }

    public static String getOrSignURLEncoder() {
        if (TextUtils.isEmpty(signOr)) {
            try {
                signOr = URLEncoder.encode("|", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                signOr = "%7C";
                e.printStackTrace();
            }
        }
        return signOr;
    }


    public static Bitmap getThumbImageForPath(
            ContentResolver cr, String path, int size) throws FileNotFoundException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (size > 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int rate = Math.max(opts.outHeight, opts.outWidth) / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        addInBitmapOptions(opts);
        Bitmap image;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        ParcelFileDescriptor pfd = null;
        if (cr != null) {
            pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
        }
        if (pfd != null) {
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            try {
                pfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        boolean isChange = false;
        int options = 95;
        while (baos.toByteArray().length / 1024 > 32) {
            isChange = true;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        if (isChange) {
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            image = BitmapFactory.decodeStream(isBm, null, null);
            try {
                isBm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            matrix = new Matrix();
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                Bitmap bm = Bitmap.createBitmap(image,
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        matrix,
                        false);
                image.recycle();
                image = null;
                return bm;
            } catch (OutOfMemoryError e) {
                return image;
            }
        }
        return image;
    }

    private static void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;
    }

    public static int getOrientation(String path) {
        if (TextUtils.isEmpty(path) || !path.endsWith(".jpg")) {
            return 0;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr,
            String path,
            int width,
            int height,
            Bitmap.Config config) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        getMaximumTextureSize();
        int outRate = 1;
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        while (getMaximumTextureSize() > 0 && (outWidth / outRate > getMaximumTextureSize() ||
                outHeight / outRate > getMaximumTextureSize())) {
            outRate++;
        }
        int rate = 1;
        if (width > 0) {
            rate = outWidth / width;
        }
        if (height > 0) {
            rate = Math.min(rate, outHeight / height);
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = Math.max(rate, outRate);
        addInBitmapOptions(opts);
        Bitmap image = null;
        try {
            ParcelFileDescriptor pfd = null;
            if (cr != null) {
                pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            }
            if (pfd != null) {
                image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
                pfd.close();
            } else {
                image = BitmapFactory.decodeFile(path, opts);
            }
            if (image == null) {
                return null;
            }
            Matrix matrix = null;
            int degree = getOrientation(path);
            if (degree > 0) {
                matrix = new Matrix();
                matrix.postRotate(degree);
            }
            if (matrix != null) {
                try {
                    image = Bitmap.createBitmap(image,
                            0,
                            0,
                            image.getWidth(),
                            image.getHeight(),
                            matrix,
                            false);
                } catch (OutOfMemoryError ignored) {
                }
            }
        } catch (OutOfMemoryError e) {
            if (image != null && !image.isRecycled()) {
                image.recycle();
                image = null;
                System.gc();
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr,
            String path,
            int size,
            Bitmap.Config config,
            boolean isMaxLimit) throws IOException {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        if (size > 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int msize = Math.min(opts.outHeight, opts.outWidth);
            if (isMaxLimit) {
                msize = Math.max(opts.outHeight, opts.outWidth);
            }
            int rate = msize / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        addInBitmapOptions(opts);
        Bitmap image;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        ParcelFileDescriptor pfd = null;
        if (cr != null) {
            pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
        }
        if (pfd != null) {
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            pfd.close();
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        if (getMaximumTextureSize() > 0 && (image.getWidth() > getMaximumTextureSize() || image
                .getHeight() > getMaximumTextureSize())) {
            float scale = Math.min(getMaximumTextureSize() / (float) image.getWidth(),
                    getMaximumTextureSize() / (float) image.getHeight());
            matrix = new Matrix();
            matrix.postScale(scale, scale);
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                image = Bitmap.createBitmap(image,
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        matrix,
                        false);
            } catch (OutOfMemoryError ignored) {
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr, String path, int size) throws IOException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, false);
    }

    public static String getImagePath(String str, int width) {
        if (getMaximumTextureSize() > 0) {
            return TextUtils.isEmpty(str) ? null : (str + (str.contains("?") ? String.format(
                    HljCommon.QINIU.PHOTO_URL2,
                    width,
                    getMaximumTextureSize())
                    .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL2,
                    width,
                    getMaximumTextureSize())));
        }
        return TextUtils.isEmpty(str) ? null : (str + (str.contains("?") ? String.format
                (HljCommon.QINIU.PHOTO_URL,
                width)
                .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL,
                width)));
    }

    public static String getImagePath2(String str, int size) {
        return TextUtils.isEmpty(str) ? null : (str + (str.contains("?") ? String.format
                (HljCommon.QINIU.PHOTO_URL3,
                size,
                size)
                .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL3,
                size,
                size)));

    }

    public static String getImagePathForWxH(String str, int width, int height) {
        return TextUtils.isEmpty(str) ? null : (str + (str.contains("?") ? String.format
                (HljCommon.QINIU.PHOTO_URL2,
                width,
                height)
                .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL2,
                width,
                height)));
    }

    public static String getImagePath2ForWxH(String str, int width, int height) {
        return TextUtils.isEmpty(str) ? null : (str + (str.contains("?") ? String.format
                (HljCommon.QINIU.PHOTO_URL3,
                width,
                height)
                .replace("?", getOrSignURLEncoder()) : String.format(HljCommon.QINIU.PHOTO_URL3,
                width,
                height)));
    }

    public static int getMaximumTextureSize() {
        if (MaximumTextureSize == 0) {
            EGL10 egl = (EGL10) EGLContext.getEGL();

            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            // Initialise
            int[] version = new int[2];
            egl.eglInitialize(display, version);

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            // Query actual list configurations
            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display,
                    configurationsList,
                    totalConfigurations[0],
                    totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            // Iterate through all the configurations to located the maximum
            // texture
            // size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check for width since opengl textures are always
                // squared
                egl.eglGetConfigAttrib(display,
                        configurationsList[i],
                        EGL10.EGL_MAX_PBUFFER_WIDTH,
                        textureSize);

                // Keep track of the maximum texture size
                if (maximumTextureSize > textureSize[0] || maximumTextureSize == 0) {
                    maximumTextureSize = textureSize[0];
                }

                Log.i("GLHelper", Integer.toString(textureSize[0]));
            }

            // Release
            egl.eglTerminate(display);
            Log.i("GLHelper", "Maximum GL texture size: " + Integer.toString(maximumTextureSize));
            MaximumTextureSize = maximumTextureSize;
        }

        return MaximumTextureSize;
    }

    /**
     * bitmap中的透明色用白色替换
     *
     * @param bitmap
     * @return
     */
    public static Bitmap changeColor(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] colorArray = new int[w * h];
        int n = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = getMixtureWhite(bitmap.getPixel(j, i));
                colorArray[n++] = color;
            }
        }
        return Bitmap.createBitmap(colorArray, w, h, Bitmap.Config.RGB_565);
    }

    public static Bitmap getThumbFromBytes(
            byte[] data, int width, int height, Bitmap.Config config) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        if (width > 0 || height > 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            int rate = 0;
            if (width > 0) {
                rate = opts.outWidth / width;
            }
            if (height > 0) {
                rate = rate > 0 ? Math.max(rate, opts.outHeight / height) : opts.outHeight / height;
            }
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        addInBitmapOptions(opts);
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (OutOfMemoryError ignored) {
            System.gc();
        }
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        boolean isChange = false;
        int options = 95;
        while (baos.toByteArray().length / 1024 > 32) {
            isChange = true;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        if (isChange) {
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            image = BitmapFactory.decodeStream(isBm, null, null);
            try {
                isBm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String readStreamToString(InputStream is) {
        return new String(readStreamToByteArray(is));
    }

    public static byte[] readStreamToByteArray(InputStream is) {
        return readStreamToByteArray(is, null);
    }

    public static byte[] readStreamToByteArray(
            InputStream is, String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        byte[] data;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取和白色混合颜色
     *
     * @return
     */
    private static int getMixtureWhite(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(getSingleMixtureWhite(red, alpha), getSingleMixtureWhite

                (green, alpha), getSingleMixtureWhite(blue, alpha));
    }

    /**
     * 获取单色的混合值
     *
     * @param color
     * @param alpha
     * @return
     */
    private static int getSingleMixtureWhite(int color, int alpha) {
        int newColor = color * alpha / 255 + 255 - alpha;
        return newColor > 255 ? 255 : newColor;
    }

    public static InputStream getContentFromUrl(
            String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(new Request.Builder().get()
                .url(url)
                .build());
        Response response = call.execute();
        return response.body()
                .byteStream();
    }


    /**
     * 获取图片宽高
     *
     * @param path 本地图片地址
     * @return 本地宽高旋转图片已矫正
     */
    public static Size getImageSizeFromPath(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int degree = getOrientation(path);
        if (degree % 180 != 0) {
            return new Size(opts.outHeight, opts.outWidth);
        }
        return new Size(opts.outWidth, opts.outHeight);
    }


    /**
     * 获取视频宽高
     *
     * @param path 视频地址
     */
    public static Size getVideoSizeFromPath(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            FileInputStream inputStream = new FileInputStream(path);
            mmr.setDataSource(inputStream.getFD());
            inputStream.close();
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //时长(毫秒)
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ;//高
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                String rotation = mmr.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_VIDEO_ROTATION);//高
                switch (rotation) {
                    case "90":
                    case "270":
                        width = mmr.extractMetadata(MediaMetadataRetriever
                                .METADATA_KEY_VIDEO_HEIGHT);//宽
                        height = mmr.extractMetadata(MediaMetadataRetriever
                                .METADATA_KEY_VIDEO_WIDTH);//高
                        break;
                }
            }
            return new Size(Integer.valueOf(width),
                    Integer.valueOf(height),
                    Long.valueOf(duration));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mmr.release();
        }
        return new Size(0, 0, 0);
    }

    public static String getImagePathForUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        if (uri.toString()
                .startsWith("file")) {
            return uri.getPath();
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media
                    .ORIENTATION};
            Cursor cursor = context.getContentResolver()
                    .query(uri, filePathColumn, null, null, null);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            int orientation = 0;
            if (path.endsWith(".jpg")) {
                orientation = cursor.getInt(cursor.getColumnIndex(filePathColumn[1]));
            }
            cursor.close();
            if (orientation != 0) {
                try {
                    ExifInterface exifInterface = new ExifInterface(path);
                    switch (orientation) {
                        case 90:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                            break;
                        case 180:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_180));
                            break;
                        case 270:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
                            break;
                    }
                    exifInterface.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return path;
        }
    }

    public static Drawable getDrawingCache(Context context, View view) {
        if (view == null) {
            return null;
        }
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }


    /**
     * 指定宽高bitmap
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getViewScreenShoot(View view, int width, int height) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        Bitmap tBitmap = view.getDrawingCache();
        Matrix matrix = new Matrix();
        matrix.postScale(width, width);

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(tBitmap, width, height);
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        view.setDrawingCacheEnabled(false);
        if (!tBitmap.isRecycled()) {
            tBitmap.recycle();
        }
        return bitmap;
    }

}
