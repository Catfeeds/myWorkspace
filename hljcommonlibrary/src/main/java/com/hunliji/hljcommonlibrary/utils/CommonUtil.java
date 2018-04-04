package com.hunliji.hljcommonlibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.entity.ImageLoadProgressListener;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.ApplicationConfigService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;

import rx.Subscription;


/**
 * Created by werther on 16/7/23.
 */
public class CommonUtil {
    private static Point deviceSize;
    private static int statusBarHeight;
    private static NumberFormat numberFormat;
    private static String appVersion;

    public static String getAppVersion(Context context) {
        if (!TextUtils.isEmpty(appVersion)) {
            return appVersion;
        }
        try {
            appVersion = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
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
        return str;
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

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isCollectionEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static int getCollectionSize(Collection collection) {
        return collection == null ? 0 : collection.size();
    }

    public static int getTextLength(CharSequence c) {
        if (c == null) {
            return 0;
        }
        int size = c.length();
        if (size > 0) {
            float len = 0;
            for (int i = 0; i < size; i++) {
                int tmp = (int) c.charAt(i);
                if (tmp > 0 && tmp < 127) {
                    len += 0.5;
                } else {
                    len++;
                }
            }
            return Math.round(len);
        }
        return 0;
    }

    /**
     * 手机号验证
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1]\\d{10}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
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

    /**
     * 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X,x
     *
     * @param id
     * @return
     */
    public static boolean validIdStr(String id) {
        String idRegex = "(^\\d{10}$)|(^\\d{15}$)|(^\\d{17}([0-9]|X|x)$)";

        return !TextUtils.isEmpty(id) && id.matches(idRegex);
    }

    public static boolean validBirthdayStr(String idCard) {
        int strYear;
        int strMonth;
        int strDay;
        if (idCard.length() == 15) {
            strYear = Integer.parseInt("19" + idCard.substring(6, 8));
            strMonth = Integer.parseInt(idCard.substring(8, 10));
            strDay = Integer.parseInt(idCard.substring(10, 12));
        } else {
            strYear = Integer.parseInt(idCard.substring(6, 10));
            strMonth = Integer.parseInt(idCard.substring(10, 12));
            strDay = Integer.parseInt(idCard.substring(12, 14));
        }
        return validDate(strYear, strMonth, strDay);
    }

    /**
     * 验证小于当前日期 是否有效
     *
     * @param iYear  待验证日期(年)
     * @param iMonth 待验证日期(月 1-12)
     * @param iDate  待验证日期(日)
     * @return 是否有效
     */
    public static boolean validDate(int iYear, int iMonth, int iDate) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int datePerMonth;
        if (iYear < 1900 || iYear >= year) {
            return false;
        }
        if (iMonth < 1 || iMonth > 12) {
            return false;
        }
        switch (iMonth) {
            case 4:
            case 6:
            case 9:
            case 11:
                datePerMonth = 30;
                break;
            case 2:
                boolean dm = ((iYear % 4 == 0 && iYear % 100 != 0) || (iYear % 400 == 0)) &&
                        (iYear > 1900 && iYear < year);
                datePerMonth = dm ? 29 : 28;
                break;
            default:
                datePerMonth = 31;
        }
        return (iDate >= 1) && (iDate <= datePerMonth);
    }


    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources()
                        .getDisplayMetrics());
    }

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources()
                        .getDisplayMetrics());
    }

    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.getResources()
                        .getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            Class<?> c;
            Object obj;
            Field field;
            int x;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj)
                        .toString());
                statusBarHeight = context.getResources()
                        .getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return statusBarHeight;

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

    public static String formatDouble2String(double f) {
        if (f > (long) f) {
            return getNumberFormat().format(f);
        }
        return getNumberFormat().format((long) f);
    }

    public static String formatDouble2StringPositive(double f) {
        if (f > 0) {
            return formatDouble2String(f);
        }
        return formatDouble2String(0);
    }

    /**
     * 向下取整非负数整数字符串
     *
     * @param d
     * @return
     */
    public static String roundDownDouble2StringPositive(double d) {
        int i = (int) d;
        if (d > 0) {
            return String.valueOf(i);
        }

        return String.valueOf(0);
    }

    /**
     * 将double类型的数字转换为字符串,不保留小数,向上取整
     *
     * @param d
     * @return
     */
    public static String roundUpDouble2String(double d) {
        if ((d > 0 && d > (long) d) || (d < 0 && d < (long) d)) {
            return getNumberFormat().format((long) d + 1);
        }

        return getNumberFormat().format((long) d);
    }

    /**
     * 将double类型的数字转换为字符串,不保留小数,向上取整
     * 小于零的数字,取整为零
     *
     * @param d
     * @return
     */
    public static String roundUpDouble2StringPositive(double d) {
        if (d > 0) {
            return roundUpDouble2String(d);
        }

        return roundUpDouble2String(0);
    }

    /**
     * 将double类型的数字转换成有两位小数的字符串
     * 当等于0的时候直接显示0.00
     *
     * @param d
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatDouble2StringWithTwoFloat(double d) {
        return String.format("%.2f", d);
    }

    /**
     * 将double类型的数字转换成有两位小数的字符串
     * 当等于0的时候直接显示0.00
     *
     * @param d
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatDouble2StringWithOneFloat(double d) {
        return String.format("%.1f", d);
    }


    /**
     * 将double类型的数字转换成有两位小数的字符串
     * 当小于零的时候,直接显示0.00
     *
     * @param d
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatDouble2StringWithTwoFloat2(double d) {
        if (d <= 0) {
            return "0.00";
        }
        return String.format("%.2f", d);
    }

    public static NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
        }
        return numberFormat;
    }

    /**
     * 注销subscription
     *
     * @param subs 多个实参
     */
    public static void unSubscribeSubs(Subscription... subs) {
        for (Subscription sub : subs) {
            if (sub != null && !sub.isUnsubscribed()) {
                sub.unsubscribe();
            }
        }
    }

    public static boolean isUnsubscribed(Subscription... subs) {
        for (Subscription sub : subs) {
            if (sub != null && !sub.isUnsubscribed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 用反射的方式获取Activity中所有的Subscription并且注销
     *
     * @param activity
     */
    public static void unSubscribeActivity(Activity activity) {
        Field[] fields = activity.getClass()
                .getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }
        try {
            for (Field field : fields) {
                Object o = field.get(activity);
                if (o instanceof Subscription) {
                    Subscription subscription = (Subscription) o;
                    subscription.unsubscribe();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static int getAppType() {
        try {
            ApplicationConfigService applicationConfig = (ApplicationConfigService) ARouter.getInstance()
                    .build(RouterPath.ServicePath.APPLICATION_CONFIG)
                    .navigation();
            return applicationConfig.getAppType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getString(JSONObject obj, String name) {
        if (obj.isNull(name)) {
            return null;
        }
        String string = obj.optString(name);
        if ("null".equalsIgnoreCase(string)) {
            return null;
        }
        return string;
    }

    public static boolean isEmpty(String string) {
        if (TextUtils.isEmpty(string) || "null".equalsIgnoreCase(string)) {
            return true;
        }
        return false;
    }


    public static Spanned fromHtml(Context context, String string, Object... formatArgs) {
        string = string.replace("color=#ff705e",
                "color=#" + Integer.toHexString(ContextCompat.getColor(context,
                        R.color.colorPrimary) & 0xffffff));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(String.format(string, formatArgs), Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(String.format(string, formatArgs));
        }
    }

    public static String readStreamToString(InputStream is) {
        return new String(readStreamToByteArray(is));
    }

    public static byte[] readStreamToByteArray(InputStream is) {
        return readStreamToByteArray(is, null, null);
    }

    public static byte[] readStreamToByteArray(
            InputStream is, ImageLoadProgressListener listener, String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        byte[] data;
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

    public static double positive(double number) {
        return number >= 0 ? number : 0;
    }

    public static boolean isHttpUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }

    public static String getAsString(JsonElement jsonElement, String name) {
        try {
            return jsonElement.getAsJsonObject()
                    .get(name)
                    .getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean getAsBoolean(JsonElement jsonElement, String name) {
        try {
            return jsonElement.getAsJsonObject()
                    .get(name)
                    .getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static double getAsDouble(JsonElement jsonElement, String name) {
        try {
            return jsonElement.getAsJsonObject()
                    .get(name)
                    .getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAsLong(JsonElement jsonElement, String name) {
        try {
            return jsonElement.getAsJsonObject()
                    .get(name)
                    .getAsLong();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int getAsInt(JsonElement jsonElement, String name) {
        try {
            return jsonElement.getAsJsonObject()
                    .get(name)
                    .getAsInt();
        } catch (Exception e) {
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

    public static class PacketType{
        public static final int CUSTOMER = 0; //婚礼纪用户端
        public static final int MERCHANT = 2; //商家端
        public static final int CARD_MASTER = 3; //请帖大师
        public static final int POS_CLIENT = 4; //Pos机
    }
}
