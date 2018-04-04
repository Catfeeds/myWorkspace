package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipFile;

public class ChannelUtil {

    private static final String CHANNEL_KEY = "hlj_channel";
    private static final String CHANNEL_APK_KEY = "hlj_apk_channel_";
    private static final String CHANNEL_VERSION_KEY = "hlj_channel_version";
    private static String mChannel;
    private static int versionCode = -1;
    private static String mApkChannel;

    static final byte[] MAGIC = new byte[]{0x21, 0x48, 0x4c, 0x4a, 0x21}; //!HLJ!
    static final int SHORT_LENGTH = 2;

    /**
     * 返回市场。  如果获取失败返回""
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        //内存中获取
        if (!TextUtils.isEmpty(mChannel)) {
            return mChannel;
        }
        mApkChannel = getChannelFromApk(context);

        //sp中获取当前版本渠道
        mChannel = getChannelBySharedPreferences(context);
        if (!TextUtils.isEmpty(mChannel) && !"update".equals(mChannel) && !"unknown".equals
                (mChannel)) {
            return mChannel;
        }
        //从apk中获取
        mChannel = mApkChannel;

        if (TextUtils.isEmpty(mChannel)) {
            mChannel = "unknown";
        } else {
            //保存渠道sp中备用
            saveChannelBySharedPreferences(context, mChannel);
        }
        return mChannel;
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @return
     */
    public static synchronized String getChannelFromApk(Context context) {
        if (!TextUtils.isEmpty(mApkChannel)) {
            return mApkChannel;
        }
        if (context == null) {
            return null;
        }
        mApkChannel = getApkChannelBySharedPreferences(context);
        if (!TextUtils.isEmpty(mApkChannel)) {
            return mApkChannel;
        }

        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.publicSourceDir;
        if (TextUtils.isEmpty(sourceDir)) {
            sourceDir = appinfo.sourceDir;
        }
        if (TextUtils.isEmpty(sourceDir)) {
            sourceDir = context.getPackageCodePath();
        }
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String comment = zipfile.getComment();
                if (!TextUtils.isEmpty(comment) && comment.contains("!HLJ!")) {
                    mApkChannel = comment.substring(0,
                            comment.length() - MAGIC.length - SHORT_LENGTH);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(mApkChannel)) {
            try {
                mApkChannel += readZipComment(new File(sourceDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(mApkChannel)) {
            try {
                ApplicationInfo appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                mApkChannel = appInfo.metaData.getString("HLJ_CHANNEL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(mApkChannel)) {
            saveApkChannelBySharedPreferences(context, mApkChannel);
        }
        return mApkChannel;
    }

    private static String readZipComment(File file) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long index = raf.length();
            byte[] buffer = new byte[MAGIC.length];
            index -= MAGIC.length;
            // read magic bytes
            raf.seek(index);
            raf.readFully(buffer);
            // if magic bytes matched
            if (isMagicMatched(buffer)) {
                index -= SHORT_LENGTH;
                raf.seek(index);
                // read content length field
                int length = readShort(raf);
                if (length > 0) {
                    index -= length;
                    raf.seek(index);
                    // read content bytes
                    byte[] bytesComment = new byte[length];
                    raf.readFully(bytesComment);
                    return new String(bytesComment, "UTF-8");
                } else {
                    throw new MarketNotFoundException("Zip comment content not found");
                }
            } else {
                throw new MarketNotFoundException("Zip comment magic bytes not found");
            }
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    private static class MarketNotFoundException extends IOException {
        public MarketNotFoundException() {
            super();
        }

        private MarketNotFoundException(final String message) {
            super(message);
        }

    }

    private static boolean isMagicMatched(byte[] buffer) {
        if (buffer.length != MAGIC.length) {
            return false;
        }
        for (int i = 0; i < MAGIC.length; ++i) {
            if (buffer[i] != MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    private static short readShort(DataInput input) throws IOException {
        byte[] buf = new byte[SHORT_LENGTH];
        input.readFully(buf);
        ByteBuffer bb = ByteBuffer.wrap(buf)
                .order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort(0);
    }

    private static void saveApkChannelBySharedPreferences(Context context, String channel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString(CHANNEL_APK_KEY + getVersionCode(context), channel);
        editor.apply();
    }

    private static String getApkChannelBySharedPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(CHANNEL_APK_KEY + getVersionCode(context), "");
    }

    /**
     * 本地保存channel & 对应版本号
     *
     * @param context
     * @param channel
     */
    private static void saveChannelBySharedPreferences(Context context, String channel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY, channel);
        editor.putInt(CHANNEL_VERSION_KEY, getVersionCode(context));
        editor.apply();
    }

    /**
     * 从sp中获取channel 不区分版本
     *
     * @param context
     * @return 为空表示获取异常、sp中的值已经失效、sp中没有此值
     */
    private static String getChannelBySharedPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(CHANNEL_KEY, "");
    }

    /**
     * 从包信息中获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        if (versionCode <= 0) {
            try {
                versionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }
}
