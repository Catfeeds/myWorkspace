/**
 *
 */
package com.hunliji.marrybiz.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.entity.ImageLoadProgressListener;
import com.hunliji.marrybiz.util.CacheUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.makeramen.rounded.RoundedImageView;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;


/**
 * @author iDay
 */
public class ImageLoadTask extends AsyncTask<Object, Object,
        Bitmap> implements ImageLoadProgressListener {
    private final WeakReference<ImageView> imageViewReference;
    private String url;
    private OnHttpRequestListener requestListener;
    private Context context;
    private ProgressBar progressBar;
    private int transferred;
    private int durationMillis;
    private boolean isPng;

    public ImageLoadTask(ImageView imageView) {
        this(imageView, null, null, 300, false);
    }

    public ImageLoadTask(ImageView imageView, OnHttpRequestListener requestListener) {
        this(imageView, requestListener, null, 300, false);

    }


    public ImageLoadTask(ImageView imageView, OnHttpRequestListener requestListener,
                         boolean isPng) {
        this(imageView, requestListener, null, 300, isPng);

    }

    public ImageLoadTask(ImageView imageView, OnHttpRequestListener requestListener,
                         int durationMillis) {
        this(imageView, requestListener, null, durationMillis, false);

    }

    public ImageLoadTask(ImageView imageView, boolean isPng) {
        this(imageView, null, null, 300, isPng);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis, boolean isPng) {
        this(imageView, null, null, durationMillis, isPng);

    }

    public ImageLoadTask(ImageView imageView, int durationMillis) {
        this(imageView, null, null, durationMillis, false);

    }

    /**
     * @param imageView
     */
    public ImageLoadTask(ImageView imageView, OnHttpRequestListener requestListener,
                         ProgressBar progressBar, int durationMillis, boolean isPng) {
        super();
        context = imageView.getContext();
        this.durationMillis = durationMillis;
        this.isPng = isPng;
        this.progressBar = progressBar;
        imageViewReference = new WeakReference<ImageView>(imageView);
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
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
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
                        HttpEntity entity;
                        byte[] data;
                        if (is == null) {
                            entity = JSONUtil.getEntityFromUrl(url, this);
                            if (entity == null) {
                                return null;
                            }
                            is = entity.getContent();
                            if (is == null) {
                                return null;
                            }
                            data = JSONUtil.readStreamToByteArray(is, this, url,
                                    entity.getContentLength());
                            if (data == null) {
                                return null;
                            }
                            image = JSONUtil.getImageFromBytes(data, size, mode);
                            if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                                CacheUtil.getInstance(context).addByteToCache (key, data,false);
                            }
                        } else {
                            try {
                                BitmapFactory.Options opt = new BitmapFactory.Options();
                                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                opt.inPurgeable = true;
                                opt.inInputShareable = true;
                                image = BitmapFactory.decodeStream(is, null, opt);
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
                            image = JSONUtil.getImageFromPath(context.getContentResolver(), url,
                                    size, Bitmap.Config.ARGB_8888, false);
                        } else {
                            image = JSONUtil.getImageFromPath(context.getContentResolver(), url,
                                    size);
                        }
                    }
                    if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                        CacheUtil.getInstance(context).addBitmapToCache(key, image,isPng,false);
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
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{new ColorDrawable(context.getResources().getColor(android.R.color.transparent)), drawable});
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


    public void loadImage(String url, int size, ScaleMode mode, Drawable placeholder) {
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
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(url, Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bm);
            CacheUtil.getInstance(context).addBitmapToCache(url, bm,isPng,false);
            if (requestListener != null) {
                this.requestListener.onRequestCompleted(bm);
            }
        }
    }

    private ImageView getAttachedImageView() {
        final ImageView imageView = imageViewReference.get();
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
