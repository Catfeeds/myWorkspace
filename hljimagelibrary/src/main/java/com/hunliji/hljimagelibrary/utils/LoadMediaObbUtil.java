package com.hunliji.hljimagelibrary.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljimagelibrary.models.Gallery;
import com.hunliji.hljimagelibrary.models.Size;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/7/20.
 */

public class LoadMediaObbUtil {


    public static Observable<Photo> queryImageObb(
            final ContentResolver cr,
            final boolean isGifSupport) {
        return Observable.create(new Observable.OnSubscribe<Photo>() {
            @Override
            public void call(Subscriber<? super Photo> subscriber) {
                String[] strings;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    strings = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media
                            .BUCKET_ID, MediaStore.Images.Media.ORIENTATION, MediaStore.Images
                            .Media.WIDTH, MediaStore.Images.Media.HEIGHT};
                } else {
                    strings = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media
                            .BUCKET_ID};
                }
                Cursor cursor = MediaStore.Images.Media.query(cr,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        strings,
                        MediaStore.Images.Media.MIME_TYPE + "<>? and " + "" + MediaStore.Images
                                .Media.SIZE + ">=?",
                        new String[]{isGifSupport ? "*" : "image/gif", "1024"},
                        MediaStore.Images.Media.DATE_ADDED + " desc");
                if (cursor != null) {
                    if (cursor.moveToFirst())
                        do {
                            String path = cursor.getString(0);
                            Photo item = new Photo();
                            item.setBucketId(cursor.getLong(1));
                            item.setImagePath(path);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (cursor.getInt(2) % 180 == 0) {
                                    item.setWidth(cursor.getInt(3));
                                    item.setHeight(cursor.getInt(4));
                                } else {
                                    item.setWidth(cursor.getInt(4));
                                    item.setHeight(cursor.getInt(3));
                                }
                            }
                            subscriber.onNext(item);
                        } while (cursor.moveToNext());
                    cursor.close();
                }
                subscriber.onCompleted();
            }
        })
                .map(new Func1<Photo, Photo>() {
                    @Override
                    public Photo call(Photo photo) {
                        try {
                            if (photo.getWidth() == 0 || photo.getHeight() == 0) {
                                Size size = ImageUtil.getImageSizeFromPath(photo.getImagePath());
                                photo.setWidth(size.getWidth());
                                photo.setHeight(size.getHeight());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return photo;
                    }
                });
    }

    public static Observable<Photo> queryVideoObb(final ContentResolver cr) {
        return Observable.create(new Observable.OnSubscribe<Photo>() {
            @Override
            public void call(Subscriber<? super Photo> subscriber) {
                String[] strings;
                strings = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media
                        .BUCKET_ID, MediaStore.Video.Media.DURATION};

                Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        strings,
                        MediaStore.Video.Media.MIME_TYPE + "==?",
                        new String[]{"video/mp4"},
                        MediaStore.Images.Media.DATE_ADDED + " desc");
                if (cursor != null) {
                    if (cursor.moveToFirst())
                        do {
                            String path = cursor.getString(0);
                            Photo item = new Photo();
                            item.setVideo(true);
                            item.setBucketId(Gallery.ALL_VIDEO_ID);
                            item.setImagePath(path);
                            item.setDuration(cursor.getLong(2));
                            subscriber.onNext(item);
                        } while (cursor.moveToNext());
                    cursor.close();
                }
                subscriber.onCompleted();
            }
        })
                .map(new Func1<Photo, Photo>() {
                    @Override
                    public Photo call(Photo photo) {
                        try {
                            Size size = ImageUtil.getVideoSizeFromPath(photo.getImagePath());
                            photo.setWidth(size.getWidth());
                            photo.setHeight(size.getHeight());
                            photo.setDuration(size.getDuration());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return photo;
                    }
                })
                .filter(new Func1<Photo, Boolean>() {
                    @Override
                    public Boolean call(Photo photo) {
                        return photo.getDuration() >= 1000;
                    }
                });
    }

    public static Observable<Gallery> queryImageGalleryObb(
            final ContentResolver cr,
            final boolean isGifSupport) {
        return Observable.create(new Observable.OnSubscribe<Gallery>() {
            @Override
            public void call(Subscriber<? super Gallery> subscriber) {
                Cursor cursor = MediaStore.Images.Media.query(cr,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media
                                .BUCKET_DISPLAY_NAME, "count(*) as photo_count", MediaStore
                                .Images.Media.DATA},
                        MediaStore.Images.Media.MIME_TYPE + "<>? and " + MediaStore.Images.Media
                                .SIZE + ">=?) group by (" + MediaStore.Images.Media.BUCKET_ID,
                        new String[]{isGifSupport ? "*" : "image/gif", "1024"},
                        MediaStore.Images.Media.DATE_ADDED + " desc");
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            Gallery gallery = new Gallery(cursor.getLong(0),
                                    cursor.getString(1),
                                    cursor.getInt(2),
                                    cursor.getString(3));
                            subscriber.onNext(gallery);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                subscriber.onCompleted();
            }
        });
    }
}
