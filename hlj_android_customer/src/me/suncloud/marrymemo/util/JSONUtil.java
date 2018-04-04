/**
 *
 */
package me.suncloud.marrymemo.util;

import android.content.ContentResolver;
import android.content.Context;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljhttplibrary.HljHttp;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.entity.ImageLoadProgressListener;
import me.suncloud.marrymemo.entity.ProgressListener;
import me.suncloud.marrymemo.entity.ProgressRequestBody;
import me.suncloud.marrymemo.model.Persistent;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.widget.RoundProgressDialog;
import okhttp3.Cache;
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

    public static Date getDate(JSONObject obj, String name) {
        return getDateFromString(JSONUtil.getString(obj, name));
    }

    public static Date getDateFromString(String time) {
        if (JSONUtil.isEmpty(time)) {
            return null;
        }
        try {
            return parseDateWithLeniency(time.contains("Z") ? (time + "+00:00") : time,
                    Constants.DATE_TIME_FORMAT_LONG,
                    Constants.DATE_FORMAT_LONG,
                    Constants.DATE_FORMAT_LONG2,
                    Constants.DATE_TIME_FORMAT_LONG3,
                    Constants.DATE_TIME_FORMAT_LONG4,
                    Constants.DATE_TIME_FORMAT_LONG5,
                    Constants.DATE_TIME_FORMAT_LONG6,
                    Constants.DATE_FORMAT_SHORT);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Date parseDateWithLeniency(
            String str, String... parsePatterns) throws ParseException {
        if (str != null && parsePatterns != null) {
            SimpleDateFormat parser = null;
            ParsePosition pos = new ParsePosition(0);
            for (String parsePattern : parsePatterns) {
                String pattern = parsePattern;
                if (parsePattern.endsWith("ZZ")) {
                    pattern = parsePattern.substring(0, parsePattern.length() - 1);
                }
                pos.setIndex(0);
                String str2 = str;
                if (parsePattern.endsWith("ZZ")) {
                    str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
                }
                if (parser == null) {
                    parser = new SimpleDateFormat(pattern, Locale.getDefault());
                    parser.setLenient(true);
                } else {
                    parser.applyPattern(pattern);
                }
                Date date = parser.parse(str2, pos);
                if (date != null && pos.getIndex() == str2.length()) {
                    return date;
                }
            }

            throw new ParseException("Unable to parse the date: " + str, -1);
        } else {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
    }

    /**
     * 时间格式 yyyy-MM-dd HH:mm:ss
     *
     * @param isServer 首次设置时间由客户端生成的时间isServer = false, 服务器的时间则为true
     */
    public static Date getDateFromFormatLong(JSONObject obj, String name, boolean isServer) {
        try {
            String dateStr = JSONUtil.getString(obj, name);
            if (!JSONUtil.isEmpty(dateStr) && !"0000-00-00 00:00:00".equals(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                        isServer ? Locale.CHINA : Locale.getDefault());
                return simpleDateFormat.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间格式 yyyy-MM-dd
     *
     * @param isServer 首次设置时间由客户端生成的时间isServer = false, 服务器的时间则为true
     */
    public static Date getDateFromFormatShort(JSONObject obj, String name, boolean isServer) {
        try {
            String dateStr = JSONUtil.getString(obj, name);
            if (!JSONUtil.isEmpty(dateStr) && !"0000-00-00".equals(dateStr)) {
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

    public static String getStringFromUrl(String url) throws IOException {
        try {
            Cache cache = CacheUtil.getInstance()
                    .getHttpCache();
            OkHttpClient client;
            if (cache != null) {
                client = new OkHttpClient.Builder().cache(cache)
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .build();
            } else {
                client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                        .build();
            }
            HttpUrl httpUrl = getUrl(url);
            Request.Builder builder = new Request.Builder().get()
                    .url(httpUrl);
            auth(builder, "GET", httpUrl, null);
            Call call = client.newCall(builder.build());
            Response response = call.execute();
            return response.body()
                    .string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getEmptyFromUrl(String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder().get()
                    .url(url);
            Response response = client.newCall(builder.build())
                    .execute();
            response.body()
                    .close();
        } catch (Exception ignored) {
        }
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

    private static void auth(Request.Builder builder, String method, HttpUrl url, String string) {
        User u = Session.getInstance()
                .getCurrentUser();
        builder.addHeader("appver", Constants.APP_VERSION);
        builder.addHeader("appName", "weddingUser");
        if (Constants.DEBUG) {
            builder.addHeader("test", "1");
        }
        builder.addHeader("phone",
                DeviceUuidFactory.getInstance()
                        .getDeviceUuidString());
        builder.addHeader("city",
                LocationSession.getInstance()
                        .getLocalString(null));
        if (LocationSession.getInstance()
                .getCity(null) != null) {
            builder.addHeader("cid",
                    String.valueOf(LocationSession.getInstance()
                            .getCity(null)
                            .getCid()));
        }
        builder.addHeader("devicekind", "android");
        if (u != null) {
            builder.addHeader("token", u.getToken());
            builder.addHeader("secret", JSONUtil.getMD5(u.getToken() + Constants.LOGIN_SEED));
        }

        String timestamp = String.valueOf(HljTimeUtils.getServerCurrentTimeMillis() / 1000);
        builder.addHeader("timestamp", timestamp);
        builder.addHeader("Authorization",
                HljHttp.getAuthorization(method, url, string, timestamp));
    }


    public static String delete(String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl httpUrl = getUrl(url);
            Request.Builder builder = new Request.Builder().delete()
                    .url(httpUrl);
            auth(builder, "DELETE", httpUrl, null);
            Call call = client.newCall(builder.build());
            Response response = call.execute();
            return response.body()
                    .string();
        } catch (Exception e) {
            return null;
        }
    }

    public static String post(String url) throws IOException {
        return uploadEmpty(url, "POST");
    }

    public static String postFormWithAttach(
            String url, Map<String, Object> map, ProgressListener listener) throws IOException {
        return uploadUrlEncodedForm(url, map, listener, "POST");
    }

    public static String postJsonWithAttach(
            String url, String json, ProgressListener listener) throws IOException {
        return uploadJsonString(url, json, listener, "POST");
    }

    public static String postMultipartWithAttach(
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            ContentResolver cr,
            boolean uncompress) throws IOException {
        return uploadMultipartForm(url, map, listener, cr, uncompress, "POST");
    }

    public static String put(String url) throws IOException {
        return uploadEmpty(url, "PUT");
    }

    public static String putFormWithAttach(
            String url, Map<String, Object> map, ProgressListener listener) throws IOException {
        return uploadUrlEncodedForm(url, map, listener, "PUT");
    }

    public static String putJsonWithAttach(
            String url, String json, ProgressListener listener) throws IOException {
        return uploadJsonString(url, json, listener, "PUT");
    }

    public static String putMultipartWithAttach(
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            ContentResolver cr) throws IOException {
        return uploadMultipartForm(url, map, listener, cr, false, "PUT");
    }

    public static String patch(String url) throws IOException {
        return uploadEmpty(url, "PATCH");
    }

    public static String patchFormWithAttach(
            String url, Map<String, Object> map, ProgressListener listener) throws IOException {
        return uploadUrlEncodedForm(url, map, listener, "PATCH");
    }

    public static String patchJsonWithAttach(
            String url, String json, ProgressListener listener) throws IOException {
        return uploadJsonString(url, json, listener, "PATCH");
    }

    private static String uploadEmpty(String url, String method) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        FormBody formBody = new FormBody.Builder().build();
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(builder, method, httpUrl, null);
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
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            ContentResolver cr,
            boolean uncompress,
            String method) throws IOException {
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
        auth(builder, method, httpUrl, null);
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
            String url,
            Map<String, Object> map,
            ProgressListener listener,
            String method) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        FormBody.Builder formBuilder = new FormBody.Builder();
        addParts(formBuilder, map);
        RequestBody formBody = formBuilder.build();
        RequestBody body;
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(builder, method, httpUrl, bodyToString(formBody));
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
            String url, String json, ProgressListener listener, String method) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = getUrl(url);
        RequestBody stringBody = RequestBody.create(MediaType.parse("application/json"), json);
        RequestBody body;
        Request.Builder builder = new Request.Builder().url(httpUrl);
        auth(builder, method, httpUrl, json);
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
    private static void addParts(
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
    private static void addParts(
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
                        File tmpfile = getImage(cr, file);
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

    private static File getImage(ContentResolver cr, File file) throws IOException {
        try {
            boolean change = false;
            Bitmap bitmap;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            int rate = Math.max(opts.outHeight / 1536, opts.outWidth / 1024);
            rate = Math.max(rate, 1);
            if (rate > 1) {
                change = true;
            }
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
            addInBitmapOptions(opts, CacheUtil.getInstance());
            if (cr != null) {
                ParcelFileDescriptor pfd = cr.openFileDescriptor(Uri.fromFile(file), "r");
                if (pfd != null) {
                    try {
                        bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(),
                                null,
                                opts);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        return null;
                    } finally {
                        pfd.close();
                    }
                } else {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                }
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            }
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int options = 100;
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    while ((baos.toByteArray().length / 1024 > 300) && (options > 70)) {
                        change = true;
                        baos.reset();
                        options -= 10;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                baos.close();
                if (!change) {
                    bitmap.recycle();
                    return null;
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
                    } catch (OutOfMemoryError ignored) {
                    }
                }
                try {
                    File out = File.createTempFile("img-", file.getName());
                    Uri uri = Uri.fromFile(out);
                    assert cr != null;
                    OutputStream imageOut = cr.openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, imageOut);
                    if (imageOut == null) {
                        return null;
                    }
                    imageOut.flush();
                    imageOut.close();
                    return out;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return JSONUtil.readStreamToByteArray(is, listener, url, body.contentLength());
    }


    public static Bitmap getImageFromUrl(
            Context context, String url, File file, int width, int height) {
        return getImageFromUrl(context, url, file, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getImageFromUrl(
            Context context, String url, File file, int width, int height, Bitmap.Config config) {
        try {
            Bitmap bitmap = null;
            if (file != null && file.exists() && file.length() > 0) {
                bitmap = JSONUtil.getImageFromPath(context.getContentResolver(),
                        file.getAbsolutePath(),
                        width,
                        height,
                        config);
            }
            if (bitmap == null) {
                byte[] data = JSONUtil.getByteArrayFromUrl(url, null);
                if (data == null) {
                    return null;
                }
                if (file != null) {
                    FileOutputStream fileOut = new FileOutputStream(file);
                    fileOut.write(data);
                    fileOut.flush();
                    fileOut.close();
                }
                bitmap = JSONUtil.getImageFromBytes(data, width, height, config);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap getImageFromBytes(
            byte[] data, int width, int height, Bitmap.Config config) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        if (width > 0 || height > 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            int rate = 0;
            if (width > 0) {
                rate = opts.outWidth / width;
            }
            if (height > 0) {
                rate = rate > 0 ? Math.min(rate, opts.outHeight / height) : opts.outHeight / height;
            }
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        addInBitmapOptions(opts, CacheUtil.getInstance());
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (OutOfMemoryError ignored) {
            System.gc();
        }
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

    public static Bitmap getImageFromBytes(byte[] data, int size, ScaleMode mode) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (size > 0) {
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
        addInBitmapOptions(opts, CacheUtil.getInstance());
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (OutOfMemoryError ignored) {
            System.gc();
        }
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

    private static byte[] readStreamToByteArray(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        byte[] data;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
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

    private static byte[] readStreamToByteArray(
            InputStream is, ImageLoadProgressListener listener, String url, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        long mHasRead = 0;
        byte[] data = null;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
                mHasRead += length;
                if (listener != null) {
                    listener.transferred(length, url);
                }
                baos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (size <= 0 || mHasRead == size) {
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

    static void saveStreamToFile(
            InputStream in, OutputStream out) {
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static InputStream getContentFromUrl(
            String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(new Request.Builder().get()
                .url(url)
                .build());
        Response response = call.execute();
        return response.body()
                .byteStream();
    }

    private static ResponseBody getBodyFromUrl(
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
            int width,
            int height,
            Bitmap.Config config) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        getMaximumTextureSize();
        int outRate = 1;
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        while (getMaximumTextureSize() > 0 && (outWidth / outRate > getMaximumTextureSize() ||
                outHeight / outRate > getMaximumTextureSize())) {
            outRate++;
        }
        int rate = 1;
        if (width > 0) {
            rate = outWidth / width;
        }
        if (height > 0) {
            rate = Math.min(rate, outHeight / height);
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = Math.max(rate, outRate);
        addInBitmapOptions(opts, CacheUtil.getInstance());
        Bitmap image = null;
        try {
            ParcelFileDescriptor pfd = null;
            if (cr != null) {
                pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
            }
            if (pfd != null) {
                image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
                pfd.close();
            } else {
                image = BitmapFactory.decodeFile(path, opts);
            }
            if (image == null) {
                return null;
            }
            Matrix matrix = null;
            int degree = getOrientation(path);
            if (degree > 0) {
                matrix = new Matrix();
                matrix.postRotate(degree);
            }
            if (matrix != null) {
                try {
                    image = Bitmap.createBitmap(image,
                            0,
                            0,
                            image.getWidth(),
                            image.getHeight(),
                            matrix,
                            false);
                } catch (OutOfMemoryError ignored) {
                }
            }
        } catch (OutOfMemoryError e) {
            if (image != null && !image.isRecycled()) {
                image.recycle();
                image = null;
                System.gc();
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr,
            String path,
            int size,
            Bitmap.Config config,
            boolean isMaxLimit) throws IOException {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        if (size > 0) {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int msize = Math.min(opts.outHeight, opts.outWidth);
            if (isMaxLimit) {
                msize = Math.max(opts.outHeight, opts.outWidth);
            }
            int rate = msize / size;
            rate = Math.max(rate, 1);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = rate;
        }
        addInBitmapOptions(opts, CacheUtil.getInstance());
        Bitmap image;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        ParcelFileDescriptor pfd = null;
        if (cr != null) {
            pfd = cr.openFileDescriptor(Uri.fromFile(new File(path)), "r");
        }
        if (pfd != null) {
            image = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, opts);
            pfd.close();
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
            matrix = new Matrix();
            matrix.postScale(scale, scale);
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
                image = Bitmap.createBitmap(image,
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        matrix,
                        false);
            } catch (OutOfMemoryError ignored) {
            }
        }
        return image;
    }

    public static Bitmap getImageFromPath(
            ContentResolver cr, String path, int size) throws IOException {
        return getImageFromPath(cr, path, size, Bitmap.Config.RGB_565, false);
    }

    private static int getOrientation(String path) {
        if (TextUtils.isEmpty(path) || !path.endsWith(".jpg")) {
            return 0;
        }
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

    static String removeFileSeparator(String filePath) {
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

    static String getWebPath(String str) {
        return JSONUtil.isEmpty(str) ? null : (str.startsWith("http") || str.startsWith("https")
                ? str : "http://" + str);
    }

    /**
     * 七牛图片参数处理 webp格式 等比缩放不裁剪
     *
     * @param str   图片链接
     * @param width 宽度上限
     */
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

    /**
     * 七牛图片参数处理 原格式 等比缩放不裁剪
     *
     * @param str   图片链接
     * @param width 宽度上限
     */
    public static String getImagePathWithoutFormat(String str, int width) {
        if (getMaximumTextureSize() > 0) {
            return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ?
                    String.format(
                    Constants.PHOTO_URL6,
                    width,
                    getMaximumTextureSize())
                    .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL6,
                    width,
                    getMaximumTextureSize()))) : Constants.HOST + str);
        }
        return isEmpty(str) ? null : (str.startsWith("http") ? (str + (str.contains("?") ? String
                .format(
                Constants.PHOTO_URL5,
                width)
                .replace("?", getURLEncoder()) : String.format(Constants.PHOTO_URL5,
                width))) : Constants.HOST + str);
    }

    /**
     * 七牛图片参数处理 webp格式 等比缩放不裁剪
     *
     * @param str    图片链接
     * @param height 高度上限
     */
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

    /**
     * 七牛图片参数处理 webp格式 等比缩放不裁剪
     *
     * @param str    图片链接
     * @param width  宽度上限
     * @param height 高度上限
     */
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

    /**
     * 七牛图片参数处理 webp格式 等比缩放居中裁剪
     *
     * @param str  图片链接
     * @param size 最少高宽
     */
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

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char)
                        ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(text.getBytes("utf-8"));
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
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

    public static RoundProgressDialog getRoundProgress(Context context) {
        RoundProgressDialog progressDialog = new RoundProgressDialog(context,
                R.style.BubbleDialogTheme);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.show();
        return progressDialog;
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

    public static int[] getViewCenterPositionOnScreen(View view) {
        if (view == null) {
            return null;
        }
        int[] position = new int[2];
        int width = view.getWidth();
        int height = view.getHeight();
        view.getLocationOnScreen(position);
        position[0] = position[0] + width / 2;
        position[1] = position[1] + height / 2;
        return position;
    }


    private static void addInBitmapOptions(BitmapFactory.Options options, CacheUtil cache) {
        options.inMutable = true;
        //        if (cache != null) {
        //            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
        //            if (inBitmap != null) {
        //                options.inBitmap = inBitmap;
        //            }
        //        }
    }


    public static Bitmap getImageFromDescriptor(
            FileDescriptor fileDescriptor, Bitmap.Config config) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;
        addInBitmapOptions(opts, CacheUtil.getInstance());
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
    }
}
