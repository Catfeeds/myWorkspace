package com.hunliji.hljhttplibrary.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.EncodeUtil;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.BlockUploadResult;
import com.hunliji.hljhttplibrary.entities.HljApiException;
import com.hunliji.hljhttplibrary.entities.HljUploadListener;
import com.hunliji.hljhttplibrary.entities.HljUploadListenerBody;
import com.hunliji.hljhttplibrary.entities.HljUploadResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/24.
 */
public class FileApi {

    private static Retrofit upLoadRetrofit;

    private synchronized static Retrofit getUpLoadRetrofit() {
        if (upLoadRetrofit == null) {
            upLoadRetrofit = new Retrofit.Builder().client(new OkHttpClient.Builder().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl("http://up.qiniu.com")
                    .build();
        }
        return upLoadRetrofit;
    }

    public static Observable<String> getTokenObb(String path) {
        return HljHttp.getRetrofit()
                .create(FileService.class)
                .getToken(path)
                .map(new Func1<JsonObject, String>() {
                    @Override
                    public String call(JsonObject jsonObject) {
                        String token = null;
                        if (jsonObject.has("uptoken")) {
                            token = jsonObject.get("uptoken")
                                    .getAsString();
                        } else if (jsonObject.has("token")) {
                            token = jsonObject.get("token")
                                    .getAsString();
                        }
                        return token;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<HljUploadResult> uploadObb(
            String token, File file, HljUploadListener listener) {
        RequestBody fileBody;
        if (listener != null) {
            fileBody = new HljUploadListenerBody(RequestBody.create(null, file), listener);
        } else {
            fileBody = RequestBody.create(null, file);
        }
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",
                file.getName(),
                fileBody);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"), token);
        RequestBody keyBody = null;
        if (file.getName()
                .toLowerCase()
                .endsWith(".gif")) {
            String name = EncodeUtil.md5sum(file);
            if (TextUtils.isEmpty(name)) {
                name = DeviceUuidFactory.getInstance()
                        .getDeviceUuidString() + System.currentTimeMillis();
            }
            keyBody = RequestBody.create(MediaType.parse("text/plain"), name + ".gif");
        }
        return getUpLoadRetrofit().create(FileService.class)
                .uploadFile(tokenBody, part, keyBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<File> download(
            String path, final String filePath) {
        return HljHttp.getRetrofit()
                .create(FileService.class)
                .download(path)
                .map(new Func1<Response<ResponseBody>, File>() {
                    @Override
                    public File call(final Response<ResponseBody> response) {
                        File file = new File(filePath);
                        try {
                            BufferedSink sink = Okio.buffer(Okio.sink(file));
                            sink.writeAll(response.body()
                                    .source());
                            sink.close();
                            return file;
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (file.exists()) {
                                file.deleteOnExit();
                            }
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<BlockUploadResult> makeBlock(
            long blockSize,
            byte[] data,
            Map<String, String> headers,
            final long checkCrc32,
            HljUploadListener listener) {
        RequestBody body;
        if (data != null && data.length > 0) {
            MediaType t = MediaType.parse("application/octet-stream");
            body = RequestBody.create(t, data, 0, data.length);
        } else {
            body = RequestBody.create(null, new byte[0]);
        }
        if (listener != null) {
            body = new HljUploadListenerBody(body, listener);
        }
        return getUpLoadRetrofit().create(FileService.class)
                .makeBlock(blockSize, body, headers)
                .map(new Func1<BlockUploadResult, BlockUploadResult>() {
                    @Override
                    public BlockUploadResult call(BlockUploadResult result) {
                        if (result == null || TextUtils.isEmpty(result.getUploadContext()) ||
                                result.getCrc32() != checkCrc32) {
                            throw new HljApiException("上传失败");
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<BlockUploadResult> uploadChunk(
            String uploadContext,
            long chunkOffset,
            byte[] data,
            Map<String, String> headers,
            final long checkCrc32,
            HljUploadListener listener) {
        RequestBody body;
        if (data != null && data.length > 0) {
            MediaType t = MediaType.parse("application/octet-stream");
            body = RequestBody.create(t, data, 0, data.length);
        } else {
            body = RequestBody.create(null, new byte[0]);
        }
        if (listener != null) {
            body = new HljUploadListenerBody(body, listener);
        }
        return getUpLoadRetrofit().create(FileService.class)
                .uploadChunk(uploadContext, chunkOffset, body, headers)
                .map(new Func1<BlockUploadResult, BlockUploadResult>() {
                    @Override
                    public BlockUploadResult call(BlockUploadResult result) {
                        if (result == null || TextUtils.isEmpty(result.getUploadContext()) ||
                                result.getCrc32() != checkCrc32) {
                            throw new HljApiException("上传失败");
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<HljUploadResult> makeFile(
            long fileSize, String key, List<String> contexts, Map<String, String> headers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String uploadContext : contexts) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(uploadContext);
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"),
                stringBuilder.toString());
        if (TextUtils.isEmpty(key)) {
            return getUpLoadRetrofit().create(FileService.class)
                    .makeFile(fileSize, body, headers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return getUpLoadRetrofit().create(FileService.class)
                .makeFile(fileSize, key, body, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
