package me.suncloud.marrymemo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.jakewharton.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Cache;

/**
 * Created by Suncloud on 2015/11/26.
 */
public class CacheUtil {

    private static LruCache<String, Bitmap> cache = null;
    private static DiskLruCache diskCache = null;
    private static DiskLruCache diskCache2 = null;
    private static Cache httpCache;

    private static CacheUtil INSTANCE;

    public static CacheUtil getInstance() {
        return INSTANCE;
    }

    public static CacheUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CacheUtil();
        }
        if (httpCache == null) {
            File httpCacheDirectory = new File(context.getExternalCacheDir(), "responses");
            httpCache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        }
        if (cache == null) {
            final int memClass = ((ActivityManager) context.getSystemService(Context
                    .ACTIVITY_SERVICE)).getMemoryClass();
            cache = new LruCache<String, Bitmap>(1024 * 1024 * memClass / 8) {
                @Override
                protected int sizeOf(String key, Bitmap value) {

                    return value.getByteCount();
                }

                @Override
                protected void entryRemoved(
                        boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }
            };
        }
        if (diskCache == null) {
            try {
                diskCache = DiskLruCache.open(context.getCacheDir(), 1, 1, 1024 * 1024 * 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (diskCache2 == null) {
            try {
                diskCache2 = DiskLruCache.open(new File(context.getCacheDir() + "2"),
                        1,
                        1,
                        1024 * 1024 * 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap == null) {
            bitmap = getBitmapFromDiskCache(key);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    public void addBitmapToCache(String key, Bitmap bitmap, boolean isPng, boolean otherCache) {
        if (key == null || bitmap == null) {
            return;
        }

        addBitmapToMemoryCache(key, bitmap);

        if (!isDiskCacheHasBitmap(key)) {
            addBitmapToDiskCache(bitmap, key, isPng, otherCache);
        }
    }


    public void addByteToCache(String key, byte[] data, boolean otherCache) {
        if (key == null || data == null || data.length == 0) {
            return;
        }
        if (!isDiskCacheHasBitmap(key)) {
            addByteToDiskCache(data, key, otherCache);
        }
    }

    public void addByteToDiskCache(byte[] data, String key, boolean otherCache) {
        try {
            DiskLruCache.Editor editor = (otherCache ? diskCache2 : diskCache).edit(key.hashCode
                    () + "");
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                os.write(data);
                os.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBitmapToDiskCache(Bitmap bitmap, String key, boolean isPng, boolean otherCache) {
        try {
            DiskLruCache.Editor editor = (otherCache ? diskCache2 : diskCache).edit(key.hashCode
                    () + "");
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                if (isPng) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                }
                os.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return cache.get(key);
    }

    public InputStream getInputStreamFromDiskCache(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot == null && diskCache2 != null) {
                snapshot = diskCache2.get(key.hashCode() + "");
            }
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot == null && diskCache2 != null) {
                snapshot = diskCache2.get(key.hashCode() + "");
            }
            if (snapshot != null) {
                try {
                    InputStream is = snapshot.getInputStream(0);
                    if (is != null) {
                        FileDescriptor fd = ((FileInputStream) is).getFD();
                        bitmap = JSONUtil.getImageFromDescriptor(fd, Bitmap.Config.ARGB_8888);
                        is.close();
                    }
                } catch (OutOfMemoryError e) {
                    System.gc();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    bitmap = null;
                }
            }
        } catch (IOException e) {
            bitmap = null;
        }

        return bitmap;
    }

    public boolean isDiskCacheHasBitmap(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot == null && diskCache2 != null) {
                snapshot = diskCache2.get(key.hashCode() + "");
            }
            if (snapshot != null) {
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }


    //    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
    //        Bitmap bitmap = null;
    //        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
    //            synchronized (mReusableBitmaps) {
    //                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
    //                Bitmap item;
    //                while (iterator.hasNext()) {
    //                    item = iterator.next().get();
    //                    if (null != item && item.isMutable()) {
    //                        if (canUseForInBitmap(item, options)) {
    //                            bitmap = item;
    //                            iterator.remove();
    //                            break;
    //                        }
    //                    } else {
    //                        iterator.remove();
    //                    }
    //                }
    //            }
    //        }
    //        return bitmap;
    //    }


    //    private static boolean canUseForInBitmap(
    //            Bitmap candidate, BitmapFactory.Options targetOptions) {
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
    //            return candidate.getWidth() == targetOptions.outWidth
    //                    && candidate.getHeight() == targetOptions.outHeight
    //                    && targetOptions.inSampleSize == 1;
    //        }
    //        int inSampleSize= targetOptions.inSampleSize==0?1:targetOptions.inSampleSize;
    //        int width = targetOptions.outWidth / inSampleSize;
    //        int height = targetOptions.outHeight / inSampleSize;
    //        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
    //        return byteCount <= candidate.getAllocationByteCount();
    //    }

    //    private static int getBytesPerPixel(Bitmap.Config config) {
    //        if (config == Bitmap.Config.ARGB_8888) {
    //            return 4;
    //        } else if (config == Bitmap.Config.RGB_565) {
    //            return 2;
    //        } else if (config == Bitmap.Config.ARGB_4444) {
    //            return 2;
    //        } else if (config == Bitmap.Config.ALPHA_8) {
    //            return 1;
    //        }
    //        return 1;
    //    }


    public Cache getHttpCache() {
        return httpCache;
    }
}
