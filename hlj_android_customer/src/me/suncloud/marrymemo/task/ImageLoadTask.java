/**
 *
 */
package me.suncloud.marrymemo.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.makeramen.rounded.RoundedImageView;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.entity.ImageLoadProgressListener;
import me.suncloud.marrymemo.util.CacheUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;

/**
 * @author iDay
 */
public class ImageLoadTask extends AsyncTask<Object, Object, Bitmap> implements
        ImageLoadProgressListener {
    private final WeakReference<ImageView> imageViewReference;
    private String url;
    private OnHttpRequestListener requestListener;
    private Context context;
    private ProgressBar progressBar;
    private int transferred;
    private int durationMillis;
    private boolean isPng;
    private boolean otherCache;

    public ImageLoadTask(ImageView imageView) {
        this(imageView, null, null, 300, false, false);
    }


    public ImageLoadTask(ImageView imageView,
                         OnHttpRequestListener requestListener) {
        this(imageView, requestListener, null, 300, false, false);

    }


    public ImageLoadTask(ImageView imageView,
                         OnHttpRequestListener requestListener, boolean isPng) {
        this(imageView, requestListener, null, 300, isPng, false);

    }

    public ImageLoadTask(ImageView imageView,
                         OnHttpRequestListener requestListener, int durationMillis) {
        this(imageView, requestListener, null, durationMillis, false, false);

    }

    public ImageLoadTask(ImageView imageView, boolean isPng) {
        this(imageView, null, null, 300, isPng, false);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis, boolean isPng) {
        this(imageView, null, null, durationMillis, isPng, false);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis) {
        this(imageView, null, null, durationMillis, false, false);

    }

    /**
     * @param imageView
     */
    public ImageLoadTask(ImageView imageView,
                         OnHttpRequestListener requestListener, ProgressBar progressBar,
                         int durationMillis, boolean isPng, boolean otherCache) {
        super();
        context = imageView.getContext();
        this.otherCache = otherCache;
        this.durationMillis = durationMillis;
        this.isPng = isPng;
        this.progressBar = progressBar;
        imageViewReference = new WeakReference<>(imageView);
        this.requestListener = requestListener;
    }

    private static ImageLoadTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncBitmapDrawable) {
                final AsyncBitmapDrawable asyncDrawable = (AsyncBitmapDrawable) drawable;
                return asyncDrawable.getImageLoadTask();
            }
        }
        return null;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        bmpOriginal.recycle();
        return bmpGrayscale;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        url = (String) params[0];
        int size = 0;
        if (params.length > 1) {
            size = (Integer) params[1];
        }
        ScaleMode mode = ScaleMode.ALL;
        if (params.length > 2) {
            mode = (ScaleMode) params[2];
        }
        Bitmap image = null;
        String key = url + size;
        if (!isCancelled() && (getAttachedImageView() != null)) {
            image = CacheUtil.getInstance(context).getBitmapFromCache(key);
        }
        if (image == null && !isCancelled() && (getAttachedImageView() != null)) {
            try {
                if (!JSONUtil.isEmpty(url)) {
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        InputStream is = CacheUtil.getInstance(context).getInputStreamFromDiskCache(key);
                        byte[] data;
                        if (is == null) {
                            data = JSONUtil.getByteArrayFromUrl(url,this);
                            if (data == null) {
                                return null;
                            }
                            image = JSONUtil
                                    .getImageFromBytes(data, size, mode);
                            if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                                CacheUtil.getInstance(context).addByteToCache(key, data, otherCache);
                            }
                        } else {
                            try {
                                FileDescriptor fd = ((FileInputStream) is).getFD();
                                image = JSONUtil.getImageFromDescriptor(fd, Bitmap.Config.ARGB_8888);
                                is.close();
                            } catch (OutOfMemoryError e) {
                                System.gc();
                                if (image != null && !image.isRecycled()) {
                                    image.recycle();
                                }
                                return null;
                            }
                        }
                    } else {
                        if (isPng) {
                            image = JSONUtil.getImageFromPath(
                                    context.getContentResolver(), url, size,
                                    Bitmap.Config.ARGB_8888, false);
                        } else {
                            image = JSONUtil.getImageFromPath(
                                    context.getContentResolver(), url, size);
                        }
                    }
                    if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                        CacheUtil.getInstance(context).addBitmapToCache(key, image, isPng, otherCache);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    @Override
    protected void onCancelled() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        super.onCancelled();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        ImageView imageView = this.imageViewReference.get();
        if (imageView != null && result != null) {
            if (imageView.getTag() != null) {
                if (imageView.getTag().equals(url)) {
                    imageView.setImageDrawable(getTransitionDrawable(result));
                    if (requestListener != null) {
                        this.requestListener.onRequestCompleted(result);
                    }
                }
            } else {
                imageView.setImageDrawable(getTransitionDrawable(result));
                if (requestListener != null) {
                    this.requestListener.onRequestCompleted(result);
                }
            }
        } else if (imageView != null) {
            if (imageView.getTag() != null) {
                if (imageView.getTag().equals(url) && requestListener != null) {
                    this.requestListener.onRequestFailed(null);
                }
            } else if (requestListener != null) {
                this.requestListener.onRequestFailed(null);
            }
        }

    }

    private Drawable getTransitionDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        if (durationMillis == 0) {
            return drawable;
        }
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(Color.TRANSPARENT), drawable});
        td.startTransition(200);
        return td;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        super.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Object... values) {
        if (progressBar != null) {
            int bytes = (Integer) values[0];
            progressBar.setProgress(bytes);

        }
        super.onProgressUpdate(values);
    }

    public void loadImage(String url, int size, ScaleMode mode,
                          Drawable placeholder) {
        if (url == null) {
            return;
        }
        ImageView imageView = this.imageViewReference.get();
        String key = url + size;
        Bitmap value = CacheUtil.getInstance(context).getBitmapFromMemCache(key);
        if (value != null && value.getWidth() > 1 && value.getHeight() > 1) {
            imageView.setImageBitmap(value);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(value);
            }
        } else if (JSONUtil.cancelPotentialWork(url, imageView)) {
            imageView.setImageDrawable(placeholder);
            executeOnExecutor(Constants.THEADPOOL, url, size, mode);
        }
    }

    public void loadThumbforPath(String url) {
        if (url == null) {
            return;
        }
        ImageView imageView = this.imageViewReference.get();
        Bitmap value = CacheUtil.getInstance(context).getBitmapFromMemCache(url);
        if (value != null) {
            imageView.setImageBitmap(value);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(value);
            }
        } else {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(url,
                    Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bm);
            CacheUtil.getInstance(context).addBitmapToCache(url, bm, isPng, otherCache);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(bm);
            }
        }
    }

    private ImageView getAttachedImageView() {
        final ImageView imageView = imageViewReference.get();
        if (imageView == null || (imageView.getTag() != null && !imageView.getTag().equals(url))) {
            return null;
        }
        if (imageView instanceof RoundedImageView) {
            return imageView;
        }
        final ImageLoadTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (this == bitmapWorkerTask) {
            return imageView;
        }
        return null;
    }

    @Override
    public void transferred(int transferedBytes, String url) {
        transferred += transferedBytes;
        publishProgress(transferred, url);
    }

    @Override
    public void setContentLength(long contentLength, String url) {
        if (progressBar != null) {
            progressBar.setMax((int) contentLength);
        }
    }
}