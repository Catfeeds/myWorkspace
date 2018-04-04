package com.hunliji.marrybiz.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Poster;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    public static final int RANDOM_STRING_NONE = 0;
    public static final int RANDOM_STRING_CHAR = 1;
    public static final int RANDOM_STRING_NUM = 2;
    public static SimpleDateFormat cardTimeFormat = null;
    public static SimpleDateFormat orderTimeFormat = null;
    public static int statusBarHeight;
    private static boolean isFirstCollect = false;
    private static boolean isFirstThreadCollect = true;
    private static Toast collectToast;
    private static Toast authErrorToast;
    private static NumberFormat numberFormat;
    private static Toast toast;

    public static int dp2px(Context context, int dp) {
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

    public static void sendEasyTracker(Context context, String et) {
        if (!JSONUtil.isEmpty(et)) {
            Tracker easyTracker = EasyTracker.getInstance(context);
            easyTracker.set(Fields.SCREEN_NAME, et);
            easyTracker.send(MapBuilder.createAppView()
                    .build());
        }
    }

    public static void showToast(int stringId, Context mContext) {
        TextView hintView = null;
        if (collectToast != null) {
            hintView = (TextView) collectToast.getView()
                    .findViewById(R.id.hint);
        }
        if (hintView != null) {
            hintView.setText(stringId);
        } else {
            collectToast = new Toast(mContext);
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.hlj_custom_toast___cm, null);
            hintView = (TextView) view.findViewById(R.id.hint);
            hintView.setText(stringId);
            collectToast.setView(view);
            collectToast.setDuration(Toast.LENGTH_SHORT);
            collectToast.setGravity(Gravity.CENTER, 0, 0);
        }
        try {
            collectToast.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public static void showToast(String string, Context mContext) {
        TextView hintView = null;
        if (collectToast != null) {
            hintView = (TextView) collectToast.getView()
                    .findViewById(R.id.hint);
        }
        if (hintView != null) {
            hintView.setText(string);
        } else {
            collectToast = new Toast(mContext);
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.hlj_custom_toast___cm, null);
            hintView = (TextView) view.findViewById(R.id.hint);
            hintView.setText(string);
            collectToast.setView(view);
            collectToast.setDuration(Toast.LENGTH_SHORT);
            collectToast.setGravity(Gravity.CENTER, 0, 0);
        }
        try {
            collectToast.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public static void showAuthErrorToast(Context context) {
        TextView hintView = null;
        if (authErrorToast != null) {
            hintView = (TextView) authErrorToast.getView()
                    .findViewById(R.id.hint);
        }
        if (hintView != null) {
            hintView.setText(R.string.msg_auth_error);
        } else {
            authErrorToast = new Toast(context);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.logout_hint_view, null);
            hintView = (TextView) view.findViewById(R.id.hint);
            hintView.setText(R.string.msg_auth_error);
            authErrorToast.setView(view);
            authErrorToast.setDuration(Toast.LENGTH_LONG);
            authErrorToast.setGravity(Gravity.CENTER, 0, 0);
        }
        try {
            authErrorToast.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public static void showFirstCollectNoticeDialog(final Dialog dialog, Activity activity) {
        if (isFirstCollect(activity)) {
            View view = activity.getLayoutInflater()
                    .inflate(R.layout.dialog_msg_notice, null);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);

            tvMsg.setText(R.string.hint_notice_collected_succeed);
            Button btnConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);

            btnConfirm.setText(R.string.action_ok);

            view.findViewById(R.id.btn_notice_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(activity);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }

    }

    public static void showGroupFirstCollectNoticeDialog(Activity activity) {
        if (isFirstThreadCollect(activity)) {
            final Dialog dialog = new Dialog(activity, R.style.BubbleDialogTheme);
            View view = activity.getLayoutInflater()
                    .inflate(R.layout.dialog_msg_notice, null);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);

            tvMsg.setText(R.string.hint_notice_collected_succeed2);
            Button btnConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);

            btnConfirm.setText(R.string.action_ok);

            view.findViewById(R.id.btn_notice_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(activity);
            params.width = Math.round(point.x * 5 / 7);
            window.setAttributes(params);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }

    }

    public static Drawable drawableScale(Context mContext, int id, int size) {
        Drawable drawable = mContext.getResources()
                .getDrawable(id);
        float f = Math.max(drawable.getIntrinsicWidth() / (float) size,
                drawable.getIntrinsicHeight() / (float) size);
        drawable.setBounds(0,
                0,
                Math.round(drawable.getIntrinsicWidth() / f),
                Math.round(drawable.getIntrinsicHeight() / f));
        return drawable;
    }

    public static CharSequence maxEmsText(CharSequence c, int maxEms) {
        int size = c.length();
        if (size > maxEms) {
            double len = 0;
            for (int i = 0; i < size; i++) {
                int tmp = (int) c.charAt(i);
                if (tmp > 0 && tmp < 127) {
                    len += 0.5;
                } else {
                    len++;
                }
                if (Math.round(len) == maxEms) {
                    c = c.subSequence(0, i) + "…";
                    break;
                }
            }
        }
        return c;
    }

    public static int getTextLength(CharSequence c) {
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

    public static String getCharAndNumr(int length, int type) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if (type == RANDOM_STRING_CHAR) {
                charOrNum = "char";
            } else if (type == RANDOM_STRING_NUM) {
                charOrNum = "num";
            }

            if ("char".equalsIgnoreCase(charOrNum)) {
                int choice = /* random.nextInt(2) % 2 == 0 ? 65 : 97 */97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }

    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1]\\d{10}";
        if (JSONUtil.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    public static float string2Folat(String string) {
        if (JSONUtil.isEmpty(string)) {
            return 0;
        } else {
            try {
                return Float.valueOf(string);
            } catch (NumberFormatException e) {
            }
            return 0;
        }
    }

    public static String hidePhoneNum(String number) {
        if (!JSONUtil.isEmpty(number)) {
            int size = number.length();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < size; i++) {
                if (i > 2 && i < 7) {
                    buffer.append("*");
                } else {
                    buffer.append(number.charAt(i));
                }
            }
            return buffer.toString();
        }
        return number;

    }

    public static void deleteTextOrImage(EditText editText) {
        editText.dispatchKeyEvent(new KeyEvent(0,
                        0,
                        0,
                        KeyEvent.KEYCODE_DEL,
                        0,
                        0,
                        0,
                        0,
                        KeyEvent.KEYCODE_ENDCALL));
    }

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
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


    public static void bannerAction(Context mContext, Poster poster) {
        BannerUtil.bannerAction(mContext,
                poster.getTargetType(),
                poster.getTargetId(),
                poster.getUrl());
    }

    public static boolean isFirstThreadCollect(Context mContext) {
        if (isFirstThreadCollect) {
            SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            isFirstThreadCollect = preferences.getBoolean("is_first_thread_collect", true);
            preferences.edit()
                    .putBoolean("is_first_thread_collect", false)
                    .commit();
            return isFirstThreadCollect;
        }
        return isFirstThreadCollect;

    }

    public static boolean isFirstCollect(Context mContext) {
        if (!isFirstCollect) {
            SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                    Context.MODE_PRIVATE);
            isFirstCollect = preferences.getBoolean("is_first_collect", true);
            preferences.edit()
                    .putBoolean("is_first_collect", false)
                    .commit();
            return isFirstCollect;
        }

        return !isFirstCollect;

    }

    public static String time2UtcString(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_LONG2);
        TimeZone pst = TimeZone.getTimeZone("UTC+0");
        dateFormatter.setTimeZone(pst);
        return dateFormatter.format(date);
    }

    /**
     * Double 类型精确计算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2)
                .doubleValue();
    }

    /**
     * Double 类型精确计算, 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));

        return bd1.multiply(bd2)
                .doubleValue();
    }

    /**
     * Double 类型精确计算,除法
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入,小数点位数精度
     * @return
     */
    public static double div(double d1, double d2, int scale) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));

        return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    public static String formatFloat2String(float f) {
        if (f > (int) f) {
            return String.valueOf(f);
        } else {
            return String.valueOf((int) f);
        }
    }

    public static String getIntegerPartFromDouble(double d) {
        if (d < 0 && ((int) d) == 0) {
            return "-" + String.valueOf((int) d);
        }
        return String.valueOf((int) d);
    }

    public static String getFloatPartFromDouble(double d) {
        if (d != (long) d) {
            String str = new DecimalFormat("0.00").format(d);
            return str.substring(str.indexOf("."), str.length());
        } else {
            return ".00";
        }
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

    public static String formatFloat2String(double d) {
        if (d > (int) d) {
            return String.valueOf(d);
        } else {
            return String.valueOf((int) d);
        }
    }

    public static NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
        }
        return numberFormat;
    }

    /**
     * 校验邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1," + "" +
                "3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isIgnore(Context context, String md5) {
        return !JSONUtil.isEmpty(md5) && md5.equalsIgnoreCase(context.getSharedPreferences(
                "umeng_update",
                0)
                .getString("ignore", ""));
    }

    public static void ignoreUpdate(Context context, String md5) {
        if (JSONUtil.isEmpty(md5)) {
            return;
        }
        context.getSharedPreferences("umeng_update", 0)
                .edit()
                .putString("ignore", md5)
                .commit();
    }


    public static Dialog initShareDialog(Context context, View.OnClickListener clickListener) {
        Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
        View dialogView = View.inflate(context, R.layout.dialog_share_menu, null);
        dialog.setContentView(dialogView);

        dialog.findViewById(R.id.share_pengyou)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.share_weixing)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.share_weibo)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.share_qq)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.action_cancel)
                .setOnClickListener(clickListener);

        int x = CommonUtil.getDeviceSize(context).x;
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        int itemOffset = (int) ((x - 48 * dm.density - 60 * 4 * dm.density) / 3);

        LinearLayout layoutShare = (LinearLayout) dialogView.findViewById(R.id.layout_share);
        boolean isFirst = false;
        int childCount = layoutShare.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = layoutShare.getChildAt(i);
            if (child instanceof LinearLayout) {
                if (child.getVisibility() != View.VISIBLE) {
                    continue;
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child
                        .getLayoutParams();
                if (!isFirst) {
                    isFirst = true;
                    params.leftMargin = 0;
                    continue;
                }
                params.leftMargin = itemOffset;
            }
        }

        Window win = dialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(context);
        params.width = point.x;
        win.setAttributes(params);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);
        return dialog;
    }

    //计算字符串的长度，英文当半个字符，中文当一个字符
    public static long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    public static long calculateLength2(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len++;
            } else {
                len += 2;
            }
        }
        return Math.round(len);
    }


    /**
     * 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
     *
     * @param id
     * @return true:格式正确,false:格式错误
     */
    public static boolean isValidIdStr(String id) {
        String idRegex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";

        return !JSONUtil.isEmpty(id) && id.matches(idRegex);
    }

    public static void showToast(Context context, String hintStr, int hintId) {
        if (hintId > 0 || !JSONUtil.isEmpty(hintStr)) {
            if (toast == null) {
                toast = JSONUtil.isEmpty(hintStr) ? Toast.makeText(context,
                        hintId,
                        Toast.LENGTH_SHORT) : Toast.makeText(context, hintStr, Toast.LENGTH_SHORT);
            } else if (JSONUtil.isEmpty(hintStr)) {
                toast.setText(hintId);
            } else {
                toast.setText(hintStr);
            }
            toast.show();
        }
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
     * 向下取整(舍去小数精度),返回字符串
     *
     * @param d
     * @return
     */
    public static String roundDownDouble2String(double d) {
        int i = (int) d;

        return String.valueOf(i);
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

    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint ==
                0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000)
                && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)));
    }

    public static void startWebView(Activity activity, String url) {
        Intent intent = new Intent(activity, HljWebViewActivity.class);
        intent.putExtra("path", url);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    public static List<Poster> getPosterList(
            JSONObject jsonObject, String site, boolean posterNullable) {
        ArrayList<Poster> posters = new ArrayList<>();
        if (jsonObject == null || JSONUtil.isEmpty(site)) {
            return posters;
        }
        JSONObject siteObject = jsonObject.optJSONObject(site);
        if (siteObject != null) {
            JSONArray array = siteObject.optJSONArray("holes");
            if (array != null && array.length() > 0) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    JSONObject postJson = null;
                    if (array.optJSONObject(i) != null) {
                        postJson = array.optJSONObject(i)
                                .optJSONObject("posters");
                    }
                    Poster poster = new Poster(postJson);
                    if (posterNullable || poster.getId() > 0) {
                        posters.add(poster);
                    }
                }
            }
        }
        return posters;
    }

    public static void setEmptyView(
            Context context,
            View emptyView,
            int emptyHintId,
            int emptyDrawableId,
            int emptyHintId2,
            int emptyButtonId) {
        emptyView.setVisibility(View.VISIBLE);
        ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
        ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
        TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
        TextView textEmpty2Hint = (TextView) emptyView.findViewById(R.id.text_empty2_hint);
        TextView emptyBtn = (TextView) emptyView.findViewById(R.id.btn_empty_hint);

        if (JSONUtil.isNetworkConnected(context)) {
            imgNetHint.setVisibility(View.GONE);
            if (emptyHintId != 0) {
                textEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(emptyHintId);
            } else {
                textEmptyHint.setVisibility(View.GONE);
            }
            if (emptyHintId2 != 0) {
                textEmpty2Hint.setVisibility(View.VISIBLE);
                textEmpty2Hint.setText(emptyHintId2);
            } else {
                textEmpty2Hint.setVisibility(View.GONE);
            }
            if (emptyDrawableId != 0) {
                imgEmptyHint.setVisibility(View.VISIBLE);
                imgEmptyHint.setImageResource(emptyDrawableId);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
            }
            if (emptyButtonId != 0) {
                emptyBtn.setVisibility(View.VISIBLE);
                emptyBtn.setText(emptyButtonId);
            } else {
                emptyBtn.setVisibility(View.GONE);
            }
        } else {
            emptyBtn.setVisibility(View.GONE);
            imgEmptyHint.setVisibility(View.GONE);
            textEmpty2Hint.setVisibility(View.GONE);
            imgNetHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);
            textEmptyHint.setText(R.string.net_disconnected);
        }
    }

    /**
     * 计算制定date和现在之间的时间差
     *
     * @param date
     * @return
     */
    public static String getShowTime(Context context, Date date) {
        SimpleDateFormat shortSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT);
        if (date == null) {
            return null;
        }
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = date.getTime();

        long del_time = cur_time - time;
        long sec = del_time / 1000;
        long min = sec / 60;
        if (min < 60) {
            if (min < 1) {
                return "刚刚";
            }
            return context.getResources()
                    .getString(R.string.label_last_min, min);
        } else {
            long h = min / 60;
            if (h < 24) {
                return context.getResources()
                        .getString(R.string.label_last_hour, h);
            }
            long day = h / 24;
            if (day <= 5) {
                return context.getResources()
                        .getString(R.string.label_last_day, day);
            } else {
                return shortSimpleDateFormat.format(date);
            }
        }
    }
}
