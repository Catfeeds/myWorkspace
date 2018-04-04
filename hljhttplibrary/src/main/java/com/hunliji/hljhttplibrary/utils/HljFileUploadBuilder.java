package com.hunliji.hljhttplibrary.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/24.
 */
public class HljFileUploadBuilder {

    private String Tag = "HljFileUpload";

    private File file;
    private String host;
    private String tokenPath;
    private HljUploadListener hljUploadListener;
    private SubscriberOnStartListener onStartListener;
    private SubscriberOnNextListener<HljUploadResult> onNextListener;
    private SubscriberOnErrorListener<Throwable> onErrorListener;
    private Observable<String> tokenObb;
    private boolean compress;
    private Context context;
    private int quality;

    public static final String QINIU_IMAGE_TOKEN = "p/wedding/home/APIUtils/image_upload_token";
    public static final String QINIU_VOICE_URL = "p/wedding/home/APIUtils/audio_upload_token";
    public static final String QINIU_VIDEO_URL = "p/wedding/home/APIUtils/video_upload_token";

    public static class UploadFrom {
        public static final String MESSAGE_VOICE = "message_voice";
        public static final String NOTE_MEDIA = "NoteMedia";
        public static final String LIVE_VIDEO = "LiveChannelMessage";
        public static final String CARD_AUDIO = "CardAudiosV2";
        public static final String CARD = "Card";
    }

    public HljFileUploadBuilder(File file) {
        this.file = file;
    }

    public HljFileUploadBuilder host(String host) {
        this.host = host;
        return this;
    }
    public HljFileUploadBuilder tokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
        return this;
    }

    public HljFileUploadBuilder tokenPath(String tokenPath, String from) {
        this.tokenPath = tokenPath + "?from=" + from;
        return this;
    }

    public HljFileUploadBuilder progressListener(HljUploadListener hljUploadListener) {
        this.hljUploadListener = hljUploadListener;
        return this;
    }

    public HljFileUploadBuilder onNextListener(
            SubscriberOnNextListener<HljUploadResult> onNextListener) {
        this.onNextListener = onNextListener;
        return this;
    }

    public HljFileUploadBuilder onStartListener(
            SubscriberOnStartListener onStartListener) {
        this.onStartListener = onStartListener;
        return this;
    }

    public HljFileUploadBuilder onErrorListener(
            SubscriberOnErrorListener<Throwable> onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    /**
     * 获取七牛token请求可由外部配置
     *
     * @return
     */
    public HljFileUploadBuilder tokenObb(Observable<String> tokenObb) {
        this.tokenObb = tokenObb;
        return this;
    }

    /**
     * 是否压缩，暂时只支持jpg结尾的图片
     */
    public HljFileUploadBuilder compress(Context context) {
        this.context = context;
        this.compress = true;
        return this;
    }

    public HljFileUploadBuilder compress(Context context,int quality) {
        this.context = context;
        this.compress = true;
        this.quality = quality;
        return this;
    }


    public Observable<HljUploadResult> build() {
        if (tokenObb == null) {
            String path=tokenPath;
            if(!TextUtils.isEmpty(host)){
                path=host;
                if(!host.endsWith("/")){
                    path+="/";
                }
                path+=tokenPath;
            }
            tokenObb = FileApi.getTokenObb(path);
        }
        Observable<HljUploadResult> observable;
        if (compress) {
            observable = Observable.zip(tokenObb,
                    FileUtil.getFileCompressObb(context.getContentResolver(), file, quality),
                    new Func2<String, File, FileTokenZip>() {
                        @Override
                        public FileTokenZip call(String s, File file) {
                            return new FileTokenZip(file, s);
                        }
                    })
                    .flatMap(new Func1<FileTokenZip, Observable<HljUploadResult>>() {
                        @Override
                        public Observable<HljUploadResult> call(
                                FileTokenZip fileTokenZip) {
                            if (file.length() < BlockUploader.putThreshold) {
                                return FileApi.uploadObb(fileTokenZip.getToken(),
                                        fileTokenZip.getFile(),
                                        hljUploadListener);
                            }
                            return new BlockUploader(fileTokenZip.getFile(),
                                    fileTokenZip.getToken(),
                                    hljUploadListener).upload();
                        }
                    });
        } else {
            observable = tokenObb.flatMap(new Func1<String, Observable<HljUploadResult>>() {
                @Override
                public Observable<HljUploadResult> call(String s) {
                    if (file.length() < BlockUploader.putThreshold) {
                        return FileApi.uploadObb(s, file, hljUploadListener);
                    }
                    return new BlockUploader(file, s, hljUploadListener).upload();
                }
            });
        }
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private class FileTokenZip {
        String token;
        File file;

        private FileTokenZip(File file, String token) {
            this.file = file;
            this.token = token;
        }

        private File getFile() {
            return file;
        }

        private String getToken() {
            return token;
        }
    }

}
