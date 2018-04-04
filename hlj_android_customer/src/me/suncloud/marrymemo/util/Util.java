package me.suncloud.marrymemo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljsharelibrary.adapters.viewholders.ShareActionViewHolder;
import com.hunliji.hljsharelibrary.models.ShareAction;
import com.hunliji.hljsharelibrary.widgets.ShareDialog;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.view.MarkDetailActivity;
import me.suncloud.marrymemo.view.MoreMarkProductActivity;
import me.suncloud.marrymemo.view.MoreMarkThreadActivity;
import me.suncloud.marrymemo.view.MoreMarkWorkAndCaseActivity;
import me.suncloud.marrymemo.view.login.LoginActivity;
import me.suncloud.marrymemo.view.login.WeddingDateSetActivity;

import static com.hunliji.hljsharelibrary.models.ShareAction.PengYouQuan;
import static com.hunliji.hljsharelibrary.models.ShareAction.QQ;
import static com.hunliji.hljsharelibrary.models.ShareAction.WeiBo;
import static com.hunliji.hljsharelibrary.models.ShareAction.WeiXin;

public class Util {

    public static SimpleDateFormat cardTimeFormat = null;
    public static int statusBarHeight;
    private static Toast collectToast;
    private static Toast toast;
    private static Dialog newFirstDialog;
    private static NumberFormat numberFormat;

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
        collectToast.show();
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
            view.findViewById(R.id.hint_image)
                    .setVisibility(View.GONE);
            hintView.setText(string);
            collectToast.setView(view);
            collectToast.setDuration(Toast.LENGTH_SHORT);
            collectToast.setGravity(Gravity.CENTER, 0, 0);
        }
        collectToast.show();
    }

    /**
     * 新的收藏dialog 修改
     *
     * @param context
     * @param type
     * @return
     */
    public static void showFirstCollectNoticeDialog(Context context, int type) {
        if (isNewFirstCollect(context, type)) {
            newFirstDialog = new Dialog(context, R.style.BubbleDialogTheme);
            View view = View.inflate(context, R.layout.dialog_collect_msg_notice, null);
            TextView btnConfirm = (TextView) view.findViewById(R.id.btn_notice_confirm);
            btnConfirm.setText(R.string.action_ok);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newFirstDialog.dismiss();
                }
            });
            view.findViewById(R.id.icon_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newFirstDialog.dismiss();
                        }
                    });
            newFirstDialog.setContentView(view);
            Window window = newFirstDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(context);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
            TextView tvMsg = (TextView) newFirstDialog.findViewById(R.id.tv_notice_content);
            TextView tvTitle = (TextView) newFirstDialog.findViewById(R.id.tv_notice_title);
            switch (type) {
                case 4:
                    tvMsg.setText(R.string.hint_notice_work_collected_succeed1);
                    break;
                case 5:
                    tvMsg.setText(R.string.hint_notice_thread_collected_succeed1);
                    break;
                case 6:
                    tvTitle.setText(R.string.hint_notice_merchant_followed_succeed);
                    tvMsg.setText(R.string.hint_notice_merchant_followed_succeed1);
                    break;
                default:
                    break;
            }
            try {
                newFirstDialog.show();
                SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_FILE,
                        Context.MODE_PRIVATE);
                preferences.edit()
                        .putBoolean("is_first" + type, false)
                        .apply();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新的收藏dialog判断  婚品 套餐 案例 话题 商家
     *
     * @param mContext
     * @param type     4 婚品 套餐 案例 5 话题 6 商家
     * @return
     */
    public static boolean isNewFirstCollect(Context mContext, int type) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first" + type, true);
    }

    /**
     * 读取保存到本地的userId 用于不同用户之间读取SharedPreferences 判断
     */
    public static long getUserId(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getLong("userId", 0);
    }

    /**
     * 保存用户Id到本地
     */
    public static SharedPreferences saveUserId(Context mContext) {
        User currentUser = Session.getInstance()
                .getCurrentUser(mContext);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putLong("userId", currentUser == null ? 0 : currentUser.getId())
                .apply();
        return sharedPreferences;
    }

    public static CharSequence maxEmsText(CharSequence c, int maxEms) {
        if (c == null) {
            return null;
        }
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

    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1]\\d{10}";
        return !JSONUtil.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    /**
     * 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
     *
     * @param id
     * @return
     */
    public static boolean validIdStr(String id) {
        String idRegex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        return !JSONUtil.isEmpty(id) && id.matches(idRegex);
    }

    public static String hidePhoneNum(String number) {
        if (!JSONUtil.isEmpty(number)) {
            int size = number.length();
            StringBuilder buffer = new StringBuilder();
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

    public static String getCardTimeString(Context context, Date date) {
        if (cardTimeFormat == null) {
            cardTimeFormat = new SimpleDateFormat(context.getString(R.string.fmt_card_time),
                    Locale.getDefault());
        }
        if (!cardTimeFormat.getTimeZone()
                .equals(TimeZone.getDefault())) {
            cardTimeFormat.setTimeZone(TimeZone.getDefault());
        }
        return cardTimeFormat.format(date);
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

    public static boolean isFirstCollect(Context mContext, int type) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first" + type, true);

    }

    public static String time2UtcString(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_LONG2,
                Locale.getDefault());
        TimeZone pst = TimeZone.getTimeZone("UTC+0");
        dateFormatter.setTimeZone(pst);
        return dateFormatter.format(date);
    }

    public static String time2ShortString(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT,
                Locale.getDefault());
        return dateFormatter.format(date);
    }

    public static String time2RFC3339String(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_LONG7,
                Locale.getDefault());
        return dateFormatter.format(date);
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

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources()
                        .getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources()
                .getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 将px值转换为dip或dp值，保证尺寸大小不变
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将px值转换为sp值，保证文字大小不变
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources()
                .getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
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


    public static int calculateCount(String string) {
        String str = string.replaceAll("(\\s{2,})|(,+)|(，+)|(。+)|(\\.+)|(;+)" + "", " ");
        if (TextUtils.isEmpty(str)) {
            return 0;
        } else {
            String tempText = str.trim();
            if (tempText.length() == 0) {
                return 0;
            } else if (tempText.length() > 0 && (!tempText.contains(" "))) {
                return 1;
            } else {
                return getArrayLength(tempText);
            }
        }
    }

    //截取空格
    public static int getArrayLength(String originStr) {
        String[] strArray = originStr.split(" ");
        return strArray.length;
    }

    public static void onResume(Activity activity) {
        MobclickAgent.onResume(activity);
        TCAgent.onResume(activity);

    }

    public static void onPause(Activity activity) {
        MobclickAgent.onPause(activity);
        TCAgent.onPause(activity);

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

    /**
     * 校验邮箱格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\" + ".[0-9]{1," + "" +
                "3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1," + "3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static int parseColor(String colorString) {
        if (JSONUtil.isEmpty(colorString)) {
            return 0;
        }
        try {
            if (colorString.startsWith("#") || colorString.length() == 7) {
                long color = Long.parseLong(colorString.substring(1), 16);
                if (colorString.length() == 7) {
                    color |= 0x00000000ff000000;
                }
                return (int) color;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    @SuppressLint("DefaultLocale")
    public static String parseColor(int color) {
        try {
            color &= 0x0000000000FFFFFF;
            String hexString = Integer.toHexString(color);
            while (hexString.length() < 6) {
                hexString = "0" + hexString;
            }
            return "#" + hexString;
        } catch (Exception ignored) {
        }
        return "#000000";
    }

    public static boolean loginChecked(Context context) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        return user != null && user.getId() > 0;
    }

    public static boolean loginBindChecked(
            Fragment fragment, Context context, int requestCode) {
        return loginBindChecked(fragment, context, requestCode, false);
    }

    public static boolean loginBindChecked(
            Fragment fragment, Context context, int requestCode, boolean isNewTask) {
        User user = Session.getInstance()
                .getCurrentUser(context);
        Intent intent = null;
        if (user == null) {
            intent = new Intent(context, LoginActivity.class);
            intent.putExtra("type", Constants.Login.LOGINCHECK);
        } else if (user.getWeddingDay() == null && user.getIsPending() != 1) {
            intent = new Intent(context, WeddingDateSetActivity.class);
            intent.putExtra("type", Constants.Login.LOGINCHECK);
        }
        if (intent != null) {
            if (isNewTask) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (requestCode > 0) {
                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestCode);
                } else if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
            } else {
                context.startActivity(intent);
            }
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_up_to_top,
                        R.anim.activity_anim_default);
            }
            return false;
        }
        return true;
    }

    public static boolean loginBindChecked(Context context, int requestCode) {
        return loginBindChecked(null, context, requestCode);
    }

    public static boolean loginBindChecked(Context context) {
        return loginBindChecked(null, context, 0);
    }

    public static boolean loginBindCheckedWithNewTask(
            Context context, int requestCode) {
        return loginBindChecked(null, context, requestCode, true);
    }

    //国家法定节假日(每年年初需要更新下assets目录下的calendar_holiday.txt)
    public static ArrayList<String> getOfficialHolidayList(Context context) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources()
                    .getAssets()
                    .open("calendar_holiday.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void postFailToast(
            Context context, ReturnStatus returnStatus, int id, boolean network) {
        int hintId = 0;
        String hintStr = null;
        if (!network) {
            hintId = R.string.hint_no_connection;
        } else if (returnStatus != null && !JSONUtil.isEmpty(returnStatus.getErrorMsg())) {
            hintStr = returnStatus.getErrorMsg();
        } else {
            hintId = id;
        }
        if (hintId > 0 || !JSONUtil.isEmpty(hintStr)) {
            if (toast == null) {
                toast = hintId > 0 ? Toast.makeText(context,
                        hintId,
                        Toast.LENGTH_SHORT) : Toast.makeText(context, hintStr, Toast.LENGTH_SHORT);
            } else if (hintId > 0) {
                toast.setText(hintId);
            } else {
                toast.setText(hintStr);
            }
            toast.show();
        }
    }


    public static void showToast(Context context, String hintStr, int hintId) {
        if (hintId > 0 || !JSONUtil.isEmpty(hintStr)) {
            if (toast == null) {
                toast = hintId > 0 ? Toast.makeText(context,
                        hintId,
                        Toast.LENGTH_SHORT) : Toast.makeText(context, hintStr, Toast.LENGTH_SHORT);
            } else if (hintId > 0) {
                toast.setText(hintId);
            } else {
                toast.setText(hintStr);
            }
            toast.show();
        }
    }

    public static Dialog initShareDialog(
            Context context, final ShareUtil shareUtil, final ShareActionViewHolder.OnShareClickListener onShareClickListener) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.setActions(PengYouQuan, WeiXin, QQ, WeiBo);
        dialog.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                if (onShareClickListener != null) {
                    onShareClickListener.onShare(v, action);
                }
                switch (action) {
                    case PengYouQuan:
                        if (shareUtil != null) {
                            shareUtil.shareToPengYou();
                        }
                        break;
                    case WeiXin:
                        if (shareUtil != null) {
                            shareUtil.shareToWeiXing();
                        }
                        break;
                    case QQ:
                        if (shareUtil != null) {
                            shareUtil.shareToQQ();
                        }
                        break;
                    case WeiBo:
                        if (shareUtil != null) {
                            shareUtil.shareToWeiBo();
                        }
                        break;
                }
            }
        });
        return dialog;
    }

    public static Dialog initTextShareDialog(
            Context context,
            String titleStr,
            final TextShareUtil shareUtil,
            final ShareActionViewHolder.OnShareClickListener onShareClickListener) {
        ShareDialog dialog = new ShareDialog(context);
        dialog.setTitle(titleStr);
        dialog.setActions(PengYouQuan, WeiXin, QQ, WeiBo);
        dialog.setOnShareClickListener(new ShareActionViewHolder.OnShareClickListener() {
            @Override
            public void onShare(View v, ShareAction action) {
                if (onShareClickListener != null) {
                    onShareClickListener.onShare(v, action);
                }
                switch (action) {
                    case PengYouQuan:
                        if (shareUtil != null) {
                            shareUtil.shareToPengYou();
                        }
                        break;
                    case WeiXin:
                        if (shareUtil != null) {
                            shareUtil.shareToWeiXing();
                        }
                        break;
                    case QQ:
                        if (shareUtil != null) {
                            shareUtil.shareToQQ();
                        }
                        break;
                    case WeiBo:
                        if (shareUtil != null) {
                            shareUtil.shareToWeiBo();
                        }
                        break;
                }
            }
        });
        return dialog;
    }

    public static Dialog initSelectImgDialog(
            Context context, View.OnClickListener clickListener) {
        Dialog dialog = new Dialog(context, R.style.BubbleDialogTheme);
        dialog.setContentView(R.layout.dialog_add_menu);
        dialog.findViewById(R.id.action_camera_video)
                .setVisibility(View.GONE);
        dialog.findViewById(R.id.action_cancel)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.action_gallery)
                .setOnClickListener(clickListener);
        dialog.findViewById(R.id.action_camera_photo)
                .setOnClickListener(clickListener);
        Window win = dialog.getWindow();
        ViewGroup.LayoutParams params = win.getAttributes();
        Point point = JSONUtil.getDeviceSize(context);
        params.width = point.x;
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialog_anim_rise_style);

        return dialog;
    }

    public static String formatCount(Context context, int count) {
        if (count < 10000) {
            return String.valueOf(count);
        } else {
            return context.getString(R.string.label_count_format, (float) count / 10000);
        }
    }


    public static void setEmptyView(
            Context context,
            View emptyView,
            int emptyHintId,
            int emptyDrawableId,
            int emptyHintId2,
            int emptyButtonId) {
        setEmptyView(context,
                emptyView,
                emptyHintId,
                emptyDrawableId,
                emptyHintId2,
                emptyButtonId,
                null);
    }

    public static void setEmptyView(
            Context context,
            View emptyView,
            int emptyHintId,
            int emptyDrawableId,
            int emptyHintId2,
            int emptyButtonId,
            String emptyHintStr) {
        emptyView.setVisibility(View.VISIBLE);
        ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
        ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
        TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
        TextView textEmpty2Hint = (TextView) emptyView.findViewById(R.id.text_empty2_hint);
        TextView emptyBtn = (TextView) emptyView.findViewById(R.id.btn_empty_hint);

        if (JSONUtil.isNetworkConnected(context)) {
            imgNetHint.setVisibility(View.GONE);
            if (!JSONUtil.isEmpty(emptyHintStr)) {
                textEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(emptyHintStr);
            } else if (emptyHintId != 0) {
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
     * 计算一个与现有id不重复的id
     *
     * @param ids
     * @return
     */
    public static int getUniqueId(ArrayList<Integer> ids) {
        int id = (int) (Math.random() * Integer.MAX_VALUE);
        while (!isUnique(id, ids)) {
            // 有相同的,自增再计算
            id += 1;
        }

        return id;
    }

    private static boolean isUnique(int id, ArrayList<Integer> ids) {
        for (int cId : ids) {
            if (cId == id) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将图片压缩转换成Base64格式的字符串
     *
     * @param imgPath
     * @return
     */
    public static String encodeImageBase64(String imgPath) {
        String encodedImage = null;
        Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);
        int maxEdge = myBitmap.getWidth() > myBitmap.getHeight() ? myBitmap.getWidth() : myBitmap
                .getHeight();
        // 大于850则压缩,否则不压缩
        int rate = 100;
        if (maxEdge > 500) {
            rate = (int) (((double) 500 / maxEdge) * 100);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.WEBP, rate, baos);
        byte[] b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static String getRefrshTime(Date date, Context context) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat shortSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT,
                Locale.getDefault());
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = date.getTime();

        if (time == 0) {
            return "从未";
        }
        long del_time = cur_time - time;
        long sec = del_time / 1000;
        long min = sec / 60;
        if (min < 60) {
            if (min < 1) {
                return "刚刚";
            }
            return context.getString(R.string.label_last_reply_min, min);
        } else {
            long h = min / 60;
            if (h < 24) {
                return context.getString(R.string.label_last_hour, h);
            }
            long day = h / 24;
            if (day <= 5) {
                return context.getString(R.string.label_last_day, day);
            } else {
                return shortSimpleDateFormat.format(date);
            }
        }
    }

    /**
     * 标签跳转
     *
     * @param context
     * @param type
     * @param markTitle
     * @param markId
     * @param isMore
     */
    public static void markAction(
            Context context, int type, String markTitle, long markId, boolean isMore) {
        Intent intent = new Intent(context, MarkDetailActivity.class);
        if (intent != null) {
            intent.putExtra("isMore", isMore);
            intent.putExtra("markId", markId);
            intent.putExtra("markTitle", markTitle);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    /**
     * 标签跳转
     *
     * @param context
     * @param type
     * @param markTitle
     * @param markId
     * @param isMore
     */
    public static void markActionActivity(
            Context context, int type, String markTitle, long markId, boolean isMore) {
        Intent intent = null;
        switch (type) {
            case 1:
                //聚合
                intent = new Intent(context, MarkDetailActivity.class);
                break;
            case 2:
                //套餐
                intent = new Intent(context, MoreMarkWorkAndCaseActivity.class);
                intent.putExtra("markType", Constants.MARK_TYPE.WORK);
                break;
            case 3:
                //案例
                intent = new Intent(context, MoreMarkWorkAndCaseActivity.class);
                intent.putExtra("markType", Constants.MARK_TYPE.CASE);
                break;
            case 4:
                //话题
                intent = new Intent(context, MoreMarkThreadActivity.class);
                intent.putExtra("markType", Constants.MARK_TYPE.THREAD);
                break;
            case 5:
                //婚品
                intent = new Intent(context, MoreMarkProductActivity.class);
                intent.putExtra("markType", Constants.MARK_TYPE.PRODUCT);
                break;
            default:
                intent = new Intent(context, MarkDetailActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("isMore", isMore);
            intent.putExtra("markId", markId);
            intent.putExtra("markTitle", markTitle);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
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

    /**
     * 计算制定date和现在之间的时间差
     *
     * @param date
     * @return
     */
    public static boolean isWedding(Date date) {
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = date.getTime();

        long del_time = time - cur_time;
        if (del_time > 0) {
            return true;
        }
        return false;
    }

    public static int getTextViewWidth(TextView textView, String text) {
        return Math.round(JSONUtil.isEmpty(text) ? 0 : textView.getPaint()
                .measureText(text));
    }

    /**
     * bitmap中的透明色用白色替换
     *
     * @param bitmap
     * @return
     */
    public static Bitmap changeColor(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] colorArray = new int[w * h];
        int n = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = getMixtureWhite(bitmap.getPixel(j, i));
                colorArray[n++] = color;
            }
        }
        return Bitmap.createBitmap(colorArray, w, h, Bitmap.Config.ARGB_8888);
    }

    /**
     * 获取和白色混合颜色
     *
     * @return
     */
    private static int getMixtureWhite(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(getSingleMixtureWhite(red, alpha), getSingleMixtureWhite

                (green, alpha), getSingleMixtureWhite(blue, alpha));
    }

    /**
     * 获取单色的混合值
     *
     * @param color
     * @param alpha
     * @return
     */
    private static int getSingleMixtureWhite(int color, int alpha) {
        int newColor = color * alpha / 255 + 255 - alpha;
        return newColor > 255 ? 255 : newColor;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}