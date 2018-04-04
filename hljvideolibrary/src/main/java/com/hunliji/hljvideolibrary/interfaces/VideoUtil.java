package com.hunliji.hljvideolibrary.interfaces;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by luohanlin on 2017/7/10.
 */

public class VideoUtil {

    public static String getVideoPathForUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        if (uri.toString()
                .startsWith("file")) {
            return uri.getPath();
        } else {
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            Cursor cursor = context.getContentResolver()
                    .query(uri, filePathColumn, null, null, null);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();

            return path;
        }
    }
}
