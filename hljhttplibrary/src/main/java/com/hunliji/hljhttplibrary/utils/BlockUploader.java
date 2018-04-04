package com.hunliji.hljhttplibrary.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.EncodeUtil;
import com.hunliji.hljhttplibrary.api.FileApi;
import com.hunliji.hljhttplibrary.entities.BlockUploadResult;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/8/4.
 */

public class BlockUploader implements HljUploadListener {


    private RandomAccessFile file;
    private String token;
    private long offset;
    private long totalSize;
    private Map<String, String> headers;
    private List<String> uploadContexts;
    private HljUploadListener hljUploadListener;
    private String key;
    private Subscriber<? super HljUploadResult> subscriber;

    /**
     * 断点上传时的分块大小(默认的分块大小, 不建议改变)
     */
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;
    public static final int chunkSize = 256 * 1024;
    public static final int putThreshold = 512 * 1024;


    public BlockUploader(File file, String token, HljUploadListener hljUploadListener) {
        if (file.getName()
                .toLowerCase()
                .endsWith(".gif")) {
            String name = EncodeUtil.md5sum(file);
            if (TextUtils.isEmpty(name)) {
                name = DeviceUuidFactory.getInstance()
                        .getDeviceUuidString() + System.currentTimeMillis();
            }
            key = name + ".gif";
            try {
                key = Base64.encodeToString(key.getBytes("utf-8"),
                        Base64.URL_SAFE | Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                key = null;
            }
        }
        this.totalSize = file.length();
        this.token = token;
        this.hljUploadListener = hljUploadListener;
        if (hljUploadListener != null) {
            hljUploadListener.setContentLength(totalSize);
        }
        this.headers = new HashMap<>();
        headers.put("Authorization", "UpToken " + token);
        try {
            this.file = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Observable<HljUploadResult> upload() {
        return Observable.create(new Observable.OnSubscribe<HljUploadResult>() {
            @Override
            public void call(Subscriber<? super HljUploadResult> subscriber) {
                BlockUploader.this.subscriber = subscriber;
                createBlockUpload();
            }
        });
    }

    private void uploadBlockOrChunk(BlockUploadResult result) {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            return;
        }
        if (uploadContexts == null) {
            uploadContexts = new ArrayList<>();
        }
        offset += calcPutSize(offset);
        if (offset == totalSize) {
            uploadContexts.add(result.getUploadContext());
            FileApi.makeFile(totalSize, key, uploadContexts, headers)
                    .subscribe(new Subscriber<HljUploadResult>() {
                        @Override
                        public void onCompleted() {
                            if (subscriber == null || subscriber.isUnsubscribed()) {
                                return;
                            }
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (subscriber == null || subscriber.isUnsubscribed()) {
                                return;
                            }
                            subscriber.onError(e);
                        }

                        @Override
                        public void onNext(HljUploadResult hljUploadResult) {
                            if (subscriber == null || subscriber.isUnsubscribed()) {
                                return;
                            }
                            subscriber.onNext(hljUploadResult);
                        }
                    });
        } else if (offset % BLOCK_SIZE == 0) {
            uploadContexts.add(result.getUploadContext());
            createBlockUpload();
        } else
            chunkUpload(result);
    }

    private Observable<byte[]> createChunkObb() {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                final int chunkSize = (int) calcPutSize(offset);
                byte[] chunkBuffer = new byte[chunkSize];
                try {
                    file.seek(offset);
                    file.read(chunkBuffer, 0, chunkSize);
                } catch (IOException e) {
                    subscriber.onError(e);
                    return;
                }
                subscriber.onNext(chunkBuffer);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io());
    }


    private void createBlockUpload() {
        createChunkObb().concatMap(new Func1<byte[], Observable<BlockUploadResult>>() {
            @Override
            public Observable<BlockUploadResult> call(byte[] bytes) {
                return FileApi.makeBlock(calcBlockSize(offset),
                        bytes,
                        headers,
                        bytes(bytes, 0, bytes.length),
                        BlockUploader.this);
            }
        })
                .subscribe(new Subscriber<BlockUploadResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (subscriber == null || subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(BlockUploadResult result) {
                        uploadBlockOrChunk(result);
                    }
                });
    }


    private void chunkUpload(final BlockUploadResult result) {
        createChunkObb().concatMap(new Func1<byte[], Observable<BlockUploadResult>>() {
            @Override
            public Observable<BlockUploadResult> call(byte[] bytes) {
                return FileApi.uploadChunk(result.getUploadContext(),
                        offset % BLOCK_SIZE,
                        bytes,
                        headers,
                        bytes(bytes, 0, bytes.length),
                        BlockUploader.this);
            }
        })
                .subscribe(new Subscriber<BlockUploadResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (subscriber == null || subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(BlockUploadResult result) {
                        uploadBlockOrChunk(result);
                    }
                });
    }


    private long calcPutSize(long offset) {
        long left = totalSize - offset;
        return left < chunkSize ? left : chunkSize;
    }

    private long calcBlockSize(long offset) {
        long left = totalSize - offset;
        return left < BLOCK_SIZE ? left : BLOCK_SIZE;
    }


    /**
     * 计算二进制字节校验码
     *
     * @param data   二进制数据
     * @param offset 起始字节索引
     * @param length 校验字节长度
     * @return 校验码
     */
    public static long bytes(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        return crc32.getValue();
    }

    @Override
    public void transferred(long transBytes) {
        if (hljUploadListener != null) {
            hljUploadListener.transferred(offset + transBytes);
        }
    }

    @Override
    public void setContentLength(long contentLength) {

    }
}
