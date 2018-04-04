package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    private static SimpleDateFormat format;
    private static final String chatPathName = "chat";
    private static final String imagePathName = "image";
    private static final String recordsPathName = "records";
    private static final String videoPathName = "video";
    private static final String voicePathName = "voice";
    private static final String cropPathName = "crop";
    private static final String themePathName = "theme";
    private static final String pagePathName = "page";
    private static final String fontPathName = "font";

    private static File getAlbumDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), getAlbumName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getVideoAlumDir() {
        // storage/emulated/0/DCIM/Camera目录 如果没有这个目录，则取storage/emulated/0
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DCIM)
                .getAbsolutePath(), "Camera");
        if (!dir.exists()) {
            // storage/emulated/0
            dir = new File(Environment.getExternalStorageDirectory(), getAlbumName());
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
    }

    private static File getAlbumDir(Context context) {
        if (context == null) {
            return getAlbumDir();
        }
        File dir = context.getFilesDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getCropAlbumDir() {
        File dir = new File(getAlbumDir(), cropPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getChatAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context), chatPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getUserChatAlbumDir(Context context, String user) {
        File dir = new File(getChatAlbumDir(context), user);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getImageAlbumDir() {
        File dir = new File(getAlbumDir(), imagePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getVideoAlbumDir() {
        File dir = new File(getVideoAlumDir(), videoPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getVoiceAlbumDir(Context context, String user) {
        File dir = new File(getUserChatAlbumDir(context, user), voicePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getRecordsAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context), recordsPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getThemeAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context), themePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getPageAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context), pagePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getFontAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context), fontPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static String getAlbumName() {
        return "hunliji";
    }

    public static File createImageFile() {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String imageFileName = timeStamp + ".jpg";

        return new File(getImageAlbumDir(), imageFileName);
    }


    public static File createCropImageFile() {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String imageFileName = timeStamp + ".jpg";

        return new File(getCropAlbumDir(), imageFileName);
    }

    public static File createVideoFile() {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String videoFileName = timeStamp + ".mp4";

        return new File(getVideoAlbumDir(), videoFileName);
    }


    public static File createSoundFile(Context context) {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String musicFileName = timeStamp + ".mp3";

        return new File(getRecordsAlbumDir(context), musicFileName);
    }


    public static File createUserVoiceFile(Context context, String user) {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String musicFileName = timeStamp + ".amr";

        return new File(getVoiceAlbumDir(context, user), musicFileName);
    }

    public static File createThemeFile(Context context, String url) {
        String fileName = url;
        try {
            fileName = new URL(url).getFile();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        fileName = JSONUtil.removeFileSeparator(fileName);
        return new File(getThemeAlbumDir(context), fileName);
    }

    public static File createPageFile(Context context, String url) {
        String fileName = JSONUtil.getMD5(url);
        if (JSONUtil.isEmpty(fileName)) {
            fileName = JSONUtil.removeFileSeparator(fileName);
        }
        return new File(getPageAlbumDir(context), fileName);
    }

    public static File createFontFile(Context context, String path) {
        String fileName = path;
        try {
            fileName = new URL(path).getFile();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        fileName = JSONUtil.removeFileSeparator(fileName);
        return new File(getFontAlbumDir(context), fileName);
    }

    public static String getTemporaryFileName(Context mContext) {
        return mContext.getCacheDir()
                .getAbsolutePath() + File.separator + "temp_record";
    }

    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static boolean unZipFile(String zipFile, String folderPath) throws IOException {

        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(
                    folderPath,
                    ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        return true;
    }

    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            //Log.d("upZipFile", "1ret = "+ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                //Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            //Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        return ret;
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            boolean isDeleted = false;
            try {
                isDeleted = file.delete();
            } catch (Exception ignored) {

            }
            if (!isDeleted) {
                file.deleteOnExit();
            }
        }
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
