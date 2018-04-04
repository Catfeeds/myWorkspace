package com.hunliji.hljhttplibrary.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by werther on 16/8/10.
 */
public class DownloadUtil {

    public static byte[] getByteArrayFromUrl(
            String url) throws IOException {
        ResponseBody body = getBodyFromUrl(url);
        if (body == null) {
            return null;
        }
        InputStream is = body.byteStream();
        if (is == null) {
            return null;
        }
        return readStreamToByteArray(is, url, body.contentLength());
    }

    public static ResponseBody getBodyFromUrl(
            String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(new Request.Builder().get()
                .url(url)
                .build());
        Response response = call.execute();
        return response.body();
    }

    public static byte[] readStreamToByteArray(
            InputStream is, String url, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        long mHasRead = 0;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                mHasRead += length;
                baos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mHasRead == size) {
            data = baos.toByteArray();
        }
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
