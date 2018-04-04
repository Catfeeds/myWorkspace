/**
 *
 */
package com.hunliji.marrybiz.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.entity.ImageLoadProgressListener;
import com.hunliji.marrybiz.entity.ProgressListener;
import com.hunliji.marrybiz.entity.ProgressRequestBody;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.Persistent;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;


/**
 * @author iDay
 */
public class JSONUtil {
    private static int MaximumTextureSize;
    private static Point deviceSize;
    private static String cha;

    public static String getString(JSONObject obj, String name) {
        return obj.isNull(name) ? null : obj.optString(name);
    }

    public static Date getDate(JSONObject obj, String name) {
        try {
            return obj.isNull(name) ? null : DateUtils.parseDate(obj.optString(name)
                            .contains("Z") ? (obj.optString(name) + "+00:00") : obj.optString(name),
                    Constants.DATE_TIME_FORMAT_LONG,
                    Constants.DATE_TIME_FORMAT_LONG3,
                    Constants.DATE_TIME_FORMAT_LONG4,
                    Constants.DATE_TIME_FORMAT_LONG5,
                    Constants.DATE_TIME_FORMAT_LONG6,
                    Constants.DATE_FORMAT_SHORT,
                    Constants.DATE_FORMAT_LONG);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DateTime getDateTime(JSONObject jsonObject, String name) {
        return new DateTime(getDate(jsonObject, name));
    }

    public static Date getDataFromTimStamp(JSONObject jsonObject, String name) {
        if (!jsonObject.isNull(name)) {
            long timeInMillis = jsonObject.optLong(name, 0);
            if (timeInMillis > 0) {
                Timestamp timestamp = new Timestamp(timeInMillis * 1000L);
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTimeInMillis(timestamp.getTime());

                return calendar.getTime();
            }
        }
        return null;
    }

    public static Date getDateFromFormatLong(JSONObject obj, String name) {
        return getDateFromFormatLong(obj, name, false);
    }


    public static Date getDateFromFormatLong(JSONObject obj, String name, boolean isServer) {
        try {
            String dateStr = JSONUtil.getString(obj, name);
            if (!JSONUtil.isEmpty(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                        isServer ? Locale.CHINA : Locale.getDefault());
                return simpleDateFormat.parse(obj.optString(name));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param obj
     * @param name
     * @param isServer 首次设置时间由客户端生成的时间isServer = false, 服务器的时间则为true
     * @return
     */
    public static Date getDateFromFormatShort(JSONObject obj, String name, boolean isServer) {
        try {
            String dateStr = JSONUtil.getString(obj, name);
            if (!JSONUtil.isEmpty(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants
                        .DATE_FORMAT_SHORT,
                        isServer ? Locale.CHINA : Locale.getDefault());
                return simpleDateFormat.parse(obj.optString(name));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringFromUrl(Context context, String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                    .build();
            HttpUrl httpUrl = getUrl(url);
            Request.Builder builder = new Request.Builder().get()
                    .url(httpUrl);
            auth(context, builder, "GET", httpUrl, null);
            Call call = client.newCall(builder.build());
            Response response = call.execute();
            return response.body()
                    .string();
        } catch (Exception e) {
            return null;
        }
    }

    private static void auth(
            Context context, Request.Builder builder, String method, HttpUrl url, String string) {
        builder.addHeader("appver", Constants.APP_VERSION);
        builder.addHeader("phone",
                DeviceUuidFactory.getInstance()
                        .getDeviceUuidString());
        builder.addHeader("appName", "weddingMerchant");
        builder.addHeader("devicekind", "android");

        MerchantUser u = null;
        if (context != null) {
            u = Session.getInstance()
                    .getCurrentUser(context);
        } else {
            u = Session.getInstance()
                    .getCurrentUser();
        }
        if (u != null && !TextUtils.isEmpty(u.getToken())) {
            builder.addHeader("Http-Access-Token", u.getToken());
            builder.addHeader("token", u.getUserToken());
            builder.addHeader("secret", JSONUtil.getMD5(u.getUserToken() + Constants.LOGIN_SEED));
            builder.addHeader("city", getCityString(u));
        }

        String timestamp = String.valueOf(HljTimeUtils.getServerCurrentTimeMillis() / 1000);
        builder.addHeader("timestamp", timestamp);
        builder.addHeader("Authorization",
                HljHttp.getAuthorization(method, url, string, timestamp));

    }

    public static HttpUrl getUrl(String url) {
        if (url == null)
            throw new IllegalArgumentException("url == null");

        // Silently replace websocket URLs with HTTP URLs.
        if (url.regionMatches(true, 0, "ws:", 0, 3)) {
            url = "http:" + url.substring(3);
        } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
            url = "https:" + url.substring(4);
        }

        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed == null)
            throw new IllegalArgumentException("unexpected url: " + url);
        return parsed;
    }

    private static String getCityString(MerchantUser user) {
        JSONObject city = new JSONObject();
        if (user != null) {
            try {
                city.put("gps_longitude", user.getLongitude());
                city.put("gps_latitude", user.getLatitude());
                if (!JSONUtil.isEmpty(user.getCityName())) {
                    city.put("gps_city", URLEncoder.encode(user.getCityName(), "UTF-8"));
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return city.toString();
    }

    public static String delete(
            Context context, String url, Map<String, String> headers) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl httpUrl = getUrl(url);
            Request.Builder builder = new Request.Builder().delete()
                    .url(httpUrl);
            auth(context, builder, "DELETE", httpUrl, null);
            Call call = client.newCall(builder.build());
            Response response = call.execute();
            return response.body()
                    .string();
        } catch (Exception e) {
            return null;
        }
    }

    public static String post(
            Context context, String url, Map<String, String> headers) throws IOException {
        return uploadEmpty(context, url, "POST", headers);
    }

    public static String postFormWithAttach(
            Context context,
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            Map<String, String> headers) throws IOException {
        return uploadUrlEncodedForm(context, url, map, listener, "POST", headers);
    }

    public static String postJsonWithAttach(
            Context context,
            String url,
            String json,
            ProgressListener listener,
            Map<String, String> headers) throws IOException {
        return uploadJsonString(context, url, json, listener, "POST", headers);
    }

    public static String postMultipartWithAttach(
            Context context,
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            ContentResolver cr,
            boolean uncompress,
            Map<String, String> headers) throws IOException {
        return uploadMultipartForm(context, url, map, listener, cr, uncompress, "POST", headers);
    }

    public static String put(
            Context context, String url, Map<String, String> headers) throws IOException {
        return uploadEmpty(context, url, "PUT", headers);
    }

    public static String putJsonWithAttach(
            Context context,
            String url,
            String json,
            ProgressListener listener,
            Map<String, String> headers) throws IOException {
        return uploadJsonString(context, url, json, listener, "PUT", headers);
    }

    public static String patch(
            Context context, String url, Map<String, String> headers) throws IOException {
        return uploadEmpty(context, url, "PATCH", headers);
    }

    public static String patchFormWithAttach(
            Context context,
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            Map<String, String> headers) throws IOException {
        return uploadUrlEncodedForm(context, url, map, listener, "PATCH", headers);
    }

    public static String patchJsonWithAttach(
            Context context,
            String url,
            String json,
            ProgressListener listener,
            Map<String, String> headers) throws IOException {
        return uploadJsonString(context, url, json, listener, "PATCH", headers);
    }

    private static String uploadEmpty(
            Context context,
            String url,
            String method,
            Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        FormBody formBody = new FormBody.Builder().build();
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(context, builder, method, httpUrl, null);
        switch (method) {
            case "POST":
                builder.post(formBody);
                break;
            case "DELETE":
                builder.delete(formBody);
                break;
            case "PUT":
                builder.put(formBody);
                break;
            case "PATCH":
                builder.patch(formBody);
                break;
        }
        Call call = client.newCall(builder.build());
        Response response = call.execute();
        return response.body()
                .string();
    }

    private static String uploadMultipartForm(
            Context context,
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            ContentResolver cr,
            boolean uncompress,
            String method,
            Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        List<File> temples = new ArrayList<>();
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder().setType(MultipartBody
                .FORM);
        addParts(multiBuilder, map, temples, cr, uncompress);
        Request.Builder builder = new Request.Builder().url(httpUrl);
        RequestBody body;
        if (listener == null) {
            body = multiBuilder.build();
        } else {
            body = new ProgressRequestBody(multiBuilder.build(), listener);
        }
        auth(context, builder, method, httpUrl, null);
        switch (method) {
            case "POST":
                builder.post(body);
                break;
            case "DELETE":
                builder.delete(body);
                break;
            case "PUT":
                builder.put(body);
                break;
            case "PATCH":
                builder.patch(body);
                break;
        }
        Call call = client.newCall(builder.build());
        try {
            return call.execute()
                    .body()
                    .string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            for (File temple : temples) {
                FileUtil.deleteFile(temple);
            }
        }
    }


    private static String uploadUrlEncodedForm(
            Context context,
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            String method,
            Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        FormBody.Builder formBuilder = new FormBody.Builder();
        addParts(formBuilder, map);
        RequestBody formBody = formBuilder.build();
        RequestBody body;
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(context, builder, method, httpUrl, bodyToString(formBody));
        if (listener == null) {
            body = formBody;
        } else {
            body = new ProgressRequestBody(formBody, listener);
        }
        switch (method) {
            case "POST":
                builder.post(body);
                break;
            case "DELETE":
                builder.delete(body);
                break;
            case "PUT":
                builder.put(body);
                break;
            case "PATCH":
                builder.patch(body);
                break;
        }
        Call call = client.newCall(builder.build());
        Response response = call.execute();
        return response.body()
                .string();
    }

    private static String uploadJsonString(
            Context context,
            String url,
            String json,
            ProgressListener listener,
            String method,
            Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        RequestBody stringBody = RequestBody.create(MediaType.parse("application/json"), json);
        RequestBody body;
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(context, builder, method, httpUrl, json);
        if (listener == null) {
            body = stringBody;
        } else {
            body = new ProgressRequestBody(stringBody, listener);
        }
        switch (method) {
            case "POST":
                builder.post(body);
                break;
            case "DELETE":
                builder.delete(body);
                break;
            case "PUT":
                builder.put(body);
                break;
            case "PATCH":
                builder.patch(body);
                break;
        }
        Call call = client.newCall(builder.build());
        Response response = call.execute();
        return response.body()
                .string();
    }

    private static String bodyToString(final RequestBody body) {
        try {
            final Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @SuppressWarnings("unchecked")
    public static void addParts(
            FormBody.Builder builder, Map<String, Object> map) throws IOException {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof ArrayList<?>) {
                    ArrayList<?> maps = (ArrayList<?>) entry.getValue();
                    for (Object childMap : maps) {
                        if (childMap instanceof Map<?, ?>) {
                            Map<String, Object> map2 = (Map<String, Object>) childMap;
                            addParts(builder, map2);
                        } else {
                            builder.add(entry.getKey(), String.valueOf(childMap));
                        }
                    }
                } else {
                    builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addParts(
            MultipartBody.Builder multiBuilder,
            Map<String, Object> map,
            List<File> tempfiles,
            ContentResolver cr,
            boolean uncompress) throws IOException {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    if (!uncompress && (file.getName()
                            .contains(".jpg") || file.getName()
                            .contains(".JPG"))) {
                        File tmpfile = getimage(cr, file);
                        if (tmpfile != null) {
                            tempfiles.add(tmpfile);
                            multiBuilder.addFormDataPart(entry.getKey(),
                                    tmpfile.getName(),
                                    RequestBody.create(null, tmpfile));
                        } else {
                            multiBuilder.addFormDataPart(entry.getKey(),
                                    file.getName(),
                                    RequestBody.create(null, file));
                        }
                    } else {
                        multiBuilder.addFormDataPart(entry.getKey(),
                                file.getName(),
                                RequestBody.create(null, file));
                    }
                } else if (entry.getValue() instanceof ArrayList<?>) {
                    ArrayList<?> maps = (ArrayList<?>) entry.getValue();
                    for (Object childMap : maps) {
                        if (childMap instanceof Map<?, ?>) {
                            Map<String, Object> map2 = (Map<String, Object>) childMap;
                            addParts(multiBuilder, map2, tempfiles, cr, uncompress);
                        } else {
                            multiBuilder.addFormDataPart(entry.getKey(), String.valueOf(childMap));
                        }
                    }
                } else {
                    multiBuilder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
    }

    private static File getimage(ContentResolver cr, File file) throws IOException {
        boolean change = false;
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inMutable = true;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        int rate = Math.max(opts.outHeight / 2048, opts.outWidth / 1024);
        rate = Math.max(rate, 1);
        if (rate > 1) {
            change = true;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = rate;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(file), "r");
            bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            pfd.close();
        } else {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        }
        int degree = JSONUtil.getOrientation(file.getAbsolutePath());
        if (degree > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            try {
                bitmap = Bitmap.createBitmap(bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        matrix,
                        false);
            } catch (OutOfMemoryError e) {
            }
        }
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while ((baos.toByteArray().length / 1024 > 300) && (options > 70)) {
                change = true;
                baos.reset();
                options -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            baos.close();
            if (!change) {
                bitmap.recycle();
                return null;
            }
            try {
                File out = File.createTempFile("img-", file.getName());
                Uri uri = Uri.fromFile(out);
                OutputStream imageOut = cr.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, imageOut);
                imageOut.flush();
                imageOut.close();
                return out;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bitmap.recycle();
            }
        }
        return null;
    }

    private static Bitmap getImageFromUrl(String url, int size, ScaleMode mode) throws IOException {
        return getImageFromUrl(url, size, mode, null);
    }

    private static Bitmap getImageFromUrl(
            String url,
            int size,
            ScaleMode mode,
            ImageLoadProgressListener listener) throws IOException {
        InputStream is = getContentFromUrl(url, listener);

        byte[] data = readStreamToByteArray(is, listener, url);
        is.close();

        return getImageFromBytes(data, size, mode);
    }

    public static Bitmap getImageFromBytes(byte[] data) {
        return getImageFromBytes(data, 0, ScaleMode.ALL);
    }

    /**
     * @param size
     * @param mode
     * @param data
     * @return
     */
    public static Bitmap getImageFromBytes(byte[] data, int size, ScaleMode mode) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        if (size > 0) {
            opts.inMutable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            int msize;
            switch (mode) {
                case WIDTH:
                    msize = opts.outWidth;
                    break;
                case HEIGHT:
                    msize = opts.outHeight;
                    break;
                default:
                    msize = Math.min(opts.outHeight, opts.outWidth);
                    break;
            }
            int rate = msize / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        if (image == null) {
            return null;
        }
        if (getMaximumTextureSize() > 0 && (image.getWidth() > getMaximumTextureSize() || image
                .getHeight() > getMaximumTextureSize())) {
            float scale = Math.min(getMaximumTextureSize() / (float) image.getWidth(),
                    getMaximumTextureSize() / (float) image.getHeight());
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            image = Bitmap.createBitmap(image,
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight(),
                    matrix,
                    false);
        }
        return image;
    }

    public static int getMaximumTextureSize() {
        if (MaximumTextureSize == 0) {
            EGL10 egl = (EGL10) EGLContext.getEGL();

            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            // Initialise
            int[] version = new int[2];
            egl.eglInitialize(display, version);

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            // Query actual list configurations
            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display,
                    configurationsList,
                    totalConfigurations[0],
                    totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            // Iterate through all the configurations to located the maximum
            // texture
            // size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check for width since opengl textures are always
                // squared
                egl.eglGetConfigAttrib(display,
                        configurationsList[i],
                        EGL10.EGL_MAX_PBUFFER_WIDTH,
                        textureSize);

                // Keep track of the maximum texture size
                if (maximumTextureSize > textureSize[0] || maximumTextureSize == 0) {
                    maximumTextureSize = textureSize[0];
                }

                Log.i("GLHelper", Integer.toString(textureSize[0]));
            }

            // Release
            egl.eglTerminate(display);
            Log.i("GLHelper", "Maximum GL texture size: " + Integer.toString(maximumTextureSize));
            MaximumTextureSize = maximumTextureSize;
        }

        return MaximumTextureSize;
    }

    public static byte[] readStreamToByteArray(InputStream is) {
        return readStreamToByteArray(is, null, null);
    }

    /**
     * @param is
     * @return
     */

    public static byte[] readStreamToByteArray(
            InputStream is, ImageLoadProgressListener listener, String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                if (listener != null) {
                    listener.transferred(length, url);
                }
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] readStreamToByteArray(
            InputStream is, ImageLoadProgressListener listener, String url, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        long mHasRead = 0;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                mHasRead += length;
                if (listener != null) {
                    listener.transferred(length, url);
                }
            }
            baos.flush();
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

    public static String readStreamToString(InputStream is) {
        return new String(readStreamToByteArray(is));
    }

    public static void saveStreamToFile(InputStream in, OutputStream out) {
        saveStreamToFile(in, out, null);
    }

    public static void saveStreamToFile(
            InputStream in, OutputStream out, ImageLoadProgressListener listener) {
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                if (listener != null) {
                    listener.transferred(length, null);
                }
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStringToFile(String content, OutputStream out) {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        try {
            writer.write(content);
            writer.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageFromUrl(String url, int size) throws IOException {
        return getImageFromUrl(url, size, ScaleMode.ALL);
    }

    public static InputStream getContentFromUrl(String url) throws IOException {
        return getContentFromUrl(url, null);
    }

    public static HttpEntity getEntityFromUrl(String url) throws IOException {
        return getEntityFromUrl(url, null);
    }

    public static InputStream getContentFromUrl(
            String url, ImageLoadProgressListener listener) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        if (listener != null) {
            listener.setContentLength(entity.getContentLength(), url);
        }
        return entity.getContent();
    }

    public static HttpEntity getEntityFromUrl(
            String url, ImageLoadProgressListener listener) throws IOException {
        try {
            HttpGet get = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (listener != null) {
                listener.setContentLength(entity.getContentLength(), url);
            }
            return entity;
        } catch (IllegalArgumentException ignored) {

        }
        return null;
    }

    public static Size getImageSizeFromPath(String path) {
        Size size = new Size();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int degree = getOrientation(path);
        if (degree % 180 != 0) {
            size.setHeight(opts.outWidth);
            size.setWidth(opts.outHeight);
            return size;
        }
        size.setHeight(opts.outHeight);
        size.setWidth(opts.outWidth);
        return size;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr,
            String path,
            int size,
            Bitmap.Config config,
            boolean isMaxLimit) throws FileNotFoundException {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        if (size > 0) {
            opts.inMutable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int msize = Math.min(opts.outHeight, opts.outWidth);
            //            if (isMaxLimit) {
            //                msize = Math.max(opts.outHeight, opts.outWidth);
            //            }
            int rate = msize / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        Bitmap image;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        if (getMaximumTextureSize() > 0 && (image.getWidth() > getMaximumTextureSize() || image
                .getHeight() > getMaximumTextureSize())) {
            float scale = Math.min(getMaximumTextureSize() / (float) image.getWidth(),
                    getMaximumTextureSize() / (float) image.getHeight());
            if (matrix == null) {
                matrix = new Matrix();
                matrix.postScale(scale, scale);
            }
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                Bitmap bm = Bitmap.createBitmap(image,
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        matrix,
                        false);
                image.recycle();
                image = null;
                return bm;
            } catch (OutOfMemoryError e) {
                return image;
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr, String path, int size) throws FileNotFoundException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, false);
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr,
            String path,
            int size,
            boolean isMaxLimit) throws FileNotFoundException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, isMaxLimit);
    }

    public static int getOrientation(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getImagePathForUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        if (uri.toString()
                .startsWith("file")) {
            return uri.getPath();
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media
                    .ORIENTATION};
            Cursor cursor = context.getContentResolver()
                    .query(uri, filePathColumn, null, null, null);
            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            int orientation = 0;
            if (path.endsWith(".jpg")) {
                orientation = cursor.getInt(cursor.getColumnIndex(filePathColumn[1]));
            }
            cursor.close();
            if (orientation != 0) {
                try {
                    ExifInterface exifInterface = new ExifInterface(path);
                    switch (orientation) {
                        case 90:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                            break;
                        case 180:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_180));
                            break;
                        case 270:
                            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
                            break;
                    }
                    exifInterface.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return path;
        }

    }

    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final ImageLoadTask imageLoadTask = getImageLoadTask(imageView);

        if (imageLoadTask != null) {
            final String url = imageLoadTask.getUrl();
            if (!data.equals(url)) {
                // Cancel previous task
                imageLoadTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private static ImageLoadTask getImageLoadTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncBitmapDrawable) {
                final AsyncBitmapDrawable asyncDrawable = (AsyncBitmapDrawable) drawable;
                return asyncDrawable.getImageLoadTask();
            }
        }
        return null;
    }

    public static String removeFileSeparator(String filePath) {
        if (filePath.startsWith(File.separator)) {
            filePath = filePath.substring(1);
        }
        return filePath.replace(File.separator, "-");
    }

    public static Point getDeviceSize(Context context) {
        if (deviceSize == null || deviceSize.x == 0 || deviceSize.y == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            deviceSize = new Point();
            display.getSize(deviceSize);
        }
        return deviceSize;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("") || str.equals("null");
    }

    public static String getWebPath(String str) {
        return JSONUtil.isEmpty(str) ? null : (str.startsWith("http") ? str : "http://" + str);
    }

    public static String getImagePath(String str, int width) {
        if (getMaximumTextureSize() > 0) {
            return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ?
                    String.format(
                    Constants.PHOTO_URL2,
                    width,
                    getMaximumTextureSize())
                    .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL2,
                    width,
                    getMaximumTextureSize()))) : Constants.HOST + str);
        }
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL,
                width)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL,
                width))) : Constants.HOST + str);
    }

    public static String getImagePathForRound(String str, int width) {
        if (getMaximumTextureSize() > 0) {
            return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ?
                    String.format(
                    Constants.PHOTO_URL5,
                    width,
                    getMaximumTextureSize())
                    .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL5,
                    width,
                    getMaximumTextureSize()))) : Constants.HOST + str);
        }
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL,
                width)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL,
                width))) : Constants.HOST + str);
    }

    public static String getImagePathForH(String str, int height) {
        if (getMaximumTextureSize() > 0) {
            return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ?
                    String.format(
                    Constants.PHOTO_URL2,
                    getMaximumTextureSize(),
                    height)
                    .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL2,
                    getMaximumTextureSize(),
                    height))) : Constants.HOST + str);
        }
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL4,
                height)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL4,
                height))) : Constants.HOST + str);
    }

    public static String getImagePathForWxH(String str, int width, int height) {
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL2,
                width,
                height)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL2,
                width,
                height))) : Constants.HOST + str);
    }

    public static String getImagePath2(String str, int size) {
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL3,
                size,
                size)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL3,
                size,
                size))) : Constants.HOST + str);

    }

    public static String getAvatar(String str, int size) {
        return isEmpty(str) ? null : (str.startsWith("http") ? (str.endsWith(".avatar") ? str :
                (str + (str.contains(
                "?") ? String.format(Constants.PHOTO_URL3, size, size)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL3,
                size,
                size)))) : Constants.HOST + str);
    }

    public static String getVideoPath(Persistent persistent) {
        return persistent == null ? null : JSONUtil.isEmpty(persistent.getStreamingPhone()) ?
                (JSONUtil.isEmpty(
                persistent.getIphone()) ? null : persistent.getIphone()) : persistent
                .getStreamingPhone();
    }

    public static String getAudioPath(String path) {
        return JSONUtil.isEmpty(path) ? null : !path.contains("?") ? path +
                "?avthumb/mp3/ab/32k/ar/22050" : path;

    }

    public static String getURLEncoder() {
        if (JSONUtil.isEmpty(cha)) {
            try {
                cha = URLEncoder.encode("|", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                cha = "%7C";
                e.printStackTrace();
            }
        }
        return cha;
    }

    public static Bitmap getPlaceHolder(Resources resources) {
        return BitmapFactory.decodeResource(resources, R.mipmap.icon_empty_image);
    }


    public static String getMD5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int temp = 0xff & b;
                if (temp <= 0x0F) {
                    sb.append("0")
                            .append(Integer.toHexString(temp));
                } else {
                    sb.append(Integer.toHexString(temp));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAge(Date birthday) {
        if (birthday == null) {
            return 0;
        }
        Calendar old = Calendar.getInstance();
        old.setTime(birthday);
        return Calendar.getInstance()
                .get(Calendar.YEAR) - old.get(Calendar.YEAR);
    }

    public static ProgressDialog getProgress(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        // progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setMessage(context.getString(R.string.msg_preparing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }


    public static RoundProgressDialog getRoundProgress(Context context) {
        RoundProgressDialog progressDialog = new RoundProgressDialog(context,
                R.style.BubbleDialogTheme);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.show();
        return progressDialog;
    }

    public static Bitmap scaleImage(Bitmap src, Size size) {
        if (size.getHeight() / (float) src.getHeight() > size.getWidth() / (float) src.getWidth()) {
            return scaleWithHeight(src, size);
        } else {
            return scaleWithWidth(src, size);
        }
    }

    /**
     * @param src
     * @param size
     * @return
     */
    private static Bitmap scaleWithWidth(Bitmap src, Size size) {
        float rate = src.getWidth() / (float) size.getWidth();
        int height = Math.round(src.getHeight() / rate);
        return Bitmap.createScaledBitmap(src, size.getWidth(), height, false);
    }

    /**
     * @param src
     * @param size
     * @return
     */
    private static Bitmap scaleWithHeight(Bitmap src, Size size) {
        float rate = src.getHeight() / (float) size.getHeight();
        int width = Math.round(src.getWidth() / rate);
        return Bitmap.createScaledBitmap(src, width, size.getHeight(), false);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }


    public static Map<String, String> getHeaderMap(
            String category, String action, String screen, long id, String additional) {
        Map<String, String> headers = getHeaderMap(category, action, screen, id);
        if (!JSONUtil.isEmpty(additional)) {
            headers.put("additional", additional);
        }
        return headers;
    }

    public static Map<String, String> getHeaderMap(
            String category, String action, String screen, long id) {
        Map<String, String> headers = new HashMap<>();
        headers.put("category", category);
        headers.put("action", action);
        headers.put("screen", screen);
        if (id > 0) {
            headers.put("ident", String.valueOf(id));
        }
        return headers;
    }

    public static Bitmap getThumbImageForPath(
            ContentResolver cr, String path, int size) throws FileNotFoundException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        if (size > 0) {
            opts.inMutable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int rate = Math.max(opts.outHeight, opts.outWidth) / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        Bitmap image;
        if (cr != null) {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
        } else {
            image = BitmapFactory.decodeFile(path, opts);
        }
        if (image == null) {
            return null;
        }
        Matrix matrix = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        boolean isChange = false;
        int options = 95;
        while (baos.toByteArray().length / 1024 > 32) {
            isChange = true;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        if (isChange) {
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            image = BitmapFactory.decodeStream(isBm, null, null);
            try {
                isBm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int degree = getOrientation(path);
        if (degree > 0) {
            matrix = new Matrix();
            matrix.postRotate(degree);
        }
        if (matrix != null) {
            try {
                Bitmap bm = Bitmap.createBitmap(image,
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        matrix,
                        false);
                image.recycle();
                image = null;
                return bm;
            } catch (OutOfMemoryError e) {
                return image;
            }
        }
        return image;
    }

    public static byte[] getByteArrayFromUrl(
            String url, ImageLoadProgressListener listener) throws IOException {
        ResponseBody body = getBodyFromUrl(url, listener);
        if (body == null) {
            return null;
        }
        InputStream is = body.byteStream();
        if (is == null) {
            return null;
        }
        return JSONUtil.readStreamToByteArray(is, null, url, body.contentLength());
    }

    public static ResponseBody getBodyFromUrl(
            String url, ImageLoadProgressListener listener) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(new Request.Builder().get()
                .url(url)
                .build());
        Response response = call.execute();
        ResponseBody body = response.body();
        if (listener != null) {
            listener.setContentLength(body.contentLength(), url);
        }
        return response.body();
    }


    public static boolean getBoolean(JSONObject obj, String name) {
        Object object = obj.opt(name);
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return obj.optInt(name) > 0;
        }
    }
}
