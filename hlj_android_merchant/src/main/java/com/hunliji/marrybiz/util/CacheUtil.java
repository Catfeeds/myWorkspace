package com.hunliji.marrybiz.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.jakewharton.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Suncloud on 2015/11/26.
 */
public class CacheUtil {

    private static LruCache<String, Bitmap> cache = null;
    private static DiskLruCache diskCache = null;

    private static CacheUtil INSTANCE;

    public static CacheUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CacheUtil();
        }
        if (cache == null) {
            final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            cache = new LruCache<String, Bitmap>(1024 * 1024 * memClass / 8) {
                @Override
                protected int sizeOf(String key, Bitmap value) {

                    return value.getByteCount();
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {
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
            DiskLruCache.Editor editor = diskCache.edit(key.hashCode() + "");
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
            DiskLruCache.Editor editor =  diskCache.edit(key.hashCode() + "");
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
//            if (snapshot == null && diskCache2 != null) {
//                snapshot = diskCache2.get(key.hashCode() + "");
//            }
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
//            if (snapshot == null && diskCache2 != null) {
//                snapshot = diskCache2.get(key.hashCode() + "");
//            }
            if (snapshot != null) {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    opt.inPurgeable = true;
                    opt.inInputShareable = true;
                    InputStream is = snapshot.getInputStream(0);
                    // byte[] data = JSONUtil.readStreamToByteArray(is);
                    // bitmap = JSONUtil.getImageFromBytes(data);
                    bitmap = BitmapFactory.decodeStream(is, null, opt);
                    is.close();
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
//            if (snapshot == null && diskCache2 != null) {
//                snapshot = diskCache2.get(key.hashCode() + "");
//            }
            if (snapshot != null) {
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

}
