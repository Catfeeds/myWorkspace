package com.hunliji.marrybiz.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    private static SimpleDateFormat format;
    private static final String chatPathName = "chat";
    private static final String imagePathName = "image";
    private static final String recordsPathName = "records";
    private static final String videoPathName = "video";
    private static final String voicePathName = "voice";
    private static final String cropPathName = "crop";


    public static File getAlbumDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), getAlbumName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getAlbumDir(Context context) {
        if(context==null){
            return getAlbumDir();
        }
        File dir=context.getFilesDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getCropAlbumDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), cropPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getChatAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context),chatPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getUserChatAlbumDir(Context context,String user) {
        File dir = new File(getChatAlbumDir(context),user);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getImageAlbumDir() {
        File dir = new File(getAlbumDir(),imagePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getVideoAlbumDir() {
        File dir = new File(getAlbumDir(),videoPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getVoiceAlbumDir(Context context,String user) {
        File dir = new File(getUserChatAlbumDir(context,user),voicePathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getRecordsAlbumDir(Context context) {
        File dir = new File(getAlbumDir(context),recordsPathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getAlbumName() {
        return "hunliji_biz";
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

    public static File createSoundFile(Context context) {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String musicFileName = timeStamp + ".mp3";

        return new File(getRecordsAlbumDir(context), musicFileName);
    }


    public static File createUserVoiceFile(Context context,String user) {
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
        String timeStamp = format.format(new Date());
        String musicFileName = timeStamp + ".amr";

        return new File(getVoiceAlbumDir(context,user), musicFileName);
    }

    public static String getTemporaryFileName(Context mContext) {
        return mContext.getCacheDir().getAbsolutePath() + File.separator + "temp_record";
    }

    /**
     * 拍照返回的路径
     * @return
     */
    public static String getTakePicturePath(){
        return "hunliji_tpc";
    }

    /**
     * 创建拍照返回的文件
     * @return
     */
    public  static String makeTakePicturePath(){
        return createImageFile().getPath();
//        String name = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss",Locale.getDefault()).format(new Date())+".jpg";
//        return new File(makeTakePictureDir(),name).getPath();
    }

    /**
     * 创建拍照图片文件夹的sdcard路径
     * @return
     */
    public static File makeTakePictureDir(){
        File file = null;
        if(hasExtraCard()){
             file = new File(android.os.Environment.getExternalStorageDirectory(),getTakePicturePath());
            if(!file.exists()){
                file.mkdirs();
            }
        }
        return file;
    }

    public static boolean hasExtraCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
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
}
