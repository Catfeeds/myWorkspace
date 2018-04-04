package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.R;

/**
 * Created by Suncloud on 2016/8/1.
 */
public class ToastUtil {

    private static Toast toast;
    private static Toast collectToast;

    public static void showToast(Context context, String hintStr, int hintId) {
        showToast(context, hintStr, hintId, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String hintStr, int hintId, int duration) {
        if (hintId != 0 || !TextUtils.isEmpty(hintStr)) {
            if (toast != null) {
                toast.cancel();
            }
            if (!TextUtils.isEmpty(hintStr)) {
                toast = Toast.makeText(context, hintStr, duration);
            } else {
                toast = Toast.makeText(context, hintId, duration);
            }
            toast.show();
        }
    }

    public static void showCustomToast(Context mContext, int hintId) {
        showCustomToast(mContext, mContext.getString(hintId));
    }

    public static void showCustomToast(Context mContext, String hintStr) {
        TextView hintView = null;
        if (collectToast != null) {
            hintView = collectToast.getView().findViewById(R.id.hint);
        }
        if (hintView != null) {
            hintView.setText(hintStr);
        } else {
            collectToast = new Toast(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.hlj_custom_toast___cm, null);
            hintView = view.findViewById(R.id.hint);
            hintView.setText(hintStr);
            collectToast.setView(view);
            collectToast.setDuration(Toast.LENGTH_SHORT);
            collectToast.setGravity(Gravity.CENTER, 0, 0);
        }
        collectToast.show();
    }

}
