package com.hunliji.marrybiz.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.model.Poster;
import com.hunliji.marrybiz.model.Splash;
import com.hunliji.marrybiz.task.OnHttpRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Suncloud on 2015/11/26.
 */
public class SplashUtil {

    private WeakReference<Context> contextWeakReference;
    private ArrayList<String> otherImages;
    private OnHttpRequestListener requestListener;
    private GetSplashTask splashTask;
    private LastSplashListTask lastSplashListTask;
    private Splash splash;
    private Poster poster;
    private int width;

    private static SplashUtil INSTANCE;

    private SplashUtil() {

    }

    public static SplashUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SplashUtil();
        }
        return INSTANCE;
    }

    public void loadSplash(Context context, OnHttpRequestListener listener) {
        contextWeakReference = new WeakReference<>(context);
        this.width = JSONUtil.getDeviceSize(context).x;
        this.requestListener = listener;
        this.poster = null;
        if (lastSplashListTask != null) {
            lastSplashListTask.cancel(true);
            lastSplashListTask = null;
        }
        if (splashTask == null) {
            splashTask = new GetSplashTask();
            splashTask.executeOnExecutor(Constants.INFOTHEADPOOL);
        }
    }

    public Poster getPoster() {
        return poster;
    }

    private class GetSplashTask extends AsyncTask<Object, Object, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... params) {
            try {
                Context context = contextWeakReference != null ? contextWeakReference.get() : null;
                int width = CommonUtil.getDeviceSize(context).x;
                int height = CommonUtil.getDeviceSize(context).y;
                String json = JSONUtil.getStringFromUrl(context,
                        String.format(Constants.getAbsUrl(Constants.HttpPath.SPLASH_LIST_URL),
                                width,
                                height));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject != null) {
                    JSONArray array = jsonObject.optJSONArray("current_splashe");
                    splash = new Splash(new JSONObject());
                    if (array != null && array.length() > 0) {
                        splash = new Splash(array.optJSONObject(0));
                    }
                    array = jsonObject.optJSONArray("other_splashes");
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        otherImages = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            JSONObject object = array.optJSONObject(i);
                            String path = object == null ? null : object.optString("cover_image");
                            if (!JSONUtil.isEmpty(path) && !path.equals(splash.getCoverImage())
                                    && !otherImages.contains(
                                    path)) {
                                otherImages.add(path);
                            }
                        }
                    }
                    String url = JSONUtil.getImagePath(splash.getCoverImage(), width);
                    if (JSONUtil.isEmpty(url)) {
                        return null;
                    }
                    Bitmap image = null;
                    if (context == null) {
                        byte[] data = JSONUtil.getByteArrayFromUrl(url, null);
                        if (data == null) {
                            return null;
                        }
                        image = JSONUtil.getImageFromBytes(data, width, ScaleMode.WIDTH);
                    } else {
                        String key = url + width;
                        image = CacheUtil.getInstance(context)
                                .getBitmapFromCache(key);
                        if (image == null) {
                            InputStream is = CacheUtil.getInstance(context)
                                    .getInputStreamFromDiskCache(key);
                            if (is == null) {
                                byte[] data = JSONUtil.getByteArrayFromUrl(url, null);
                                if (data == null) {
                                    return null;
                                }
                                image = JSONUtil.getImageFromBytes(data, width, ScaleMode.WIDTH);
                                if (image != null && image.getWidth() > 1 && image.getHeight() >
                                        1) {
                                    CacheUtil.getInstance(context)
                                            .addByteToCache(key, data, true);
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
                            if (image != null && image.getWidth() > 1 && image.getHeight() > 1) {
                                CacheUtil.getInstance(context)
                                        .addBitmapToCache(key, image, true, true);
                            }
                        }
                    }
                    return image;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            splashTask = null;
            if (bitmap != null && requestListener != null) {
                poster = splash.getPoster();
                requestListener.onRequestCompleted(bitmap);
            }
            if (otherImages != null && !otherImages.isEmpty()) {
                if (lastSplashListTask == null) {
                    lastSplashListTask = new LastSplashListTask();
                    lastSplashListTask.executeOnExecutor(Constants.INFOTHEADPOOL);
                } else {
                    lastSplashListTask.cancel(false);
                }
            }
            super.onPostExecute(bitmap);
        }
    }

    private class LastSplashListTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Bitmap doInBackground(String... params) {
            if (contextWeakReference == null || contextWeakReference.get() == null) {
                return null;
            }
            int size = otherImages.size();
            for (int i = 0; i < size; i++) {
                if (otherImages.size() <= i) {
                    return null;
                }
                String url = JSONUtil.getImagePath(otherImages.get(i), width);
                if (JSONUtil.isEmpty(url)) {
                    return null;
                }
                String key = url + width;
                try {
                    byte[] data = JSONUtil.getByteArrayFromUrl(url, null);
                    if (data == null) {
                        break;
                    }
                    if (contextWeakReference != null && contextWeakReference.get() != null) {
                        CacheUtil.getInstance(contextWeakReference.get())
                                .addByteToCache(key, data, true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (lastSplashListTask != null && lastSplashListTask.equals(this)) {
                lastSplashListTask = null;
            }
            super.onPostExecute(o);
        }
    }
}
