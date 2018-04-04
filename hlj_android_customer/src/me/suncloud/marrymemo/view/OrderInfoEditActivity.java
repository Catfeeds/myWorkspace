package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;

public class OrderInfoEditActivity extends HljBaseActivity implements View.OnFocusChangeListener,
        View.OnTouchListener, DTPicker.OnPickerDateListener {

    private EditText nameEt;
    private EditText phoneEt;
    private TextView timeTv;
    private Calendar calendar;
    private Calendar tempCalendar;
    private Dialog dialog;
    private Dialog confirmDialog;
    private boolean needWeddingTime;
    private String oldName;
    private String oldPhone;
    private DateTime oldTime;
    private DateTime time;
    private InputMethodManager imm;

    private ServeCustomerInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_edit);
        setTitle(R.string.title_activity_order_info_edit);
        setOkText(R.string.label_finished);
        setSwipeBackEnable(false);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        calendar = Calendar.getInstance();
        nameEt = (EditText) findViewById(R.id.et_name);
        phoneEt = (EditText) findViewById(R.id.et_phone);
        timeTv = (TextView) findViewById(R.id.tv_time);

        needWeddingTime = getIntent().getBooleanExtra("is_need_wedding_time", true);
        info = getIntent().getParcelableExtra("info");

        if (info == null) {
            return;
        }

        if (needWeddingTime) {
            findViewById(R.id.serve_time_layout).setVisibility(View.VISIBLE);
            time = oldTime = info.getServeTime();
            if (time != null) {
                try {
                    timeTv.setText(time.toString("yyyy-MM-dd"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            findViewById(R.id.serve_time_layout).setVisibility(View.GONE);
        }
        oldName = info.getCustomerName();
        oldPhone = info.getCustomerPhone();
        nameEt.setText(oldName);
        phoneEt.setText(oldPhone);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // 如果有编辑过内容，则提示信息
        if (imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        confirmBack();
    }

    public void confirmBack() {
        if (!nameEt.getText()
                .toString()
                .equals(oldName) || !phoneEt.getText()
                .toString()
                .equals(oldPhone) || (needWeddingTime && time != null && !time.equals(oldTime))) {
            if (confirmDialog != null && confirmDialog.isShowing()) {
                return;
            }
            if (confirmDialog == null) {
                confirmDialog = new Dialog(this, R.style.BubbleDialogTheme);
                View v = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
                Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
                Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
                msgAlertTv.setText(R.string.msg_confirm_drop_edited);
                confirmBtn.setText(R.string.label_drop_edit);
                cancelBtn.setText(R.string.label_wrong_action);
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        OrderInfoEditActivity.super.onBackPressed();
                        overridePendingTransition(0, R.anim.slide_out_right);
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.setContentView(v);
                Window window = confirmDialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            confirmDialog.show();
        } else {
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (nameEt.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_name_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (phoneEt.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (needWeddingTime && timeTv.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_time_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.isMobileNO(phoneEt.getText()
                .toString())) {
            Toast.makeText(this, getString(R.string.hint_new_number_error), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // 如果选择的时间在当前时间之前，则提示错误
        Calendar nowCalendar = Calendar.getInstance();
        if (needWeddingTime && TimeUtil.calendarCompareTo(nowCalendar, calendar) > 0) {
            Toast.makeText(OrderInfoEditActivity.this,
                    getString(R.string.msg_wrong_time),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String name = nameEt.getText()
                .toString();
        String phone = phoneEt.getText()
                .toString();

        // 存储当前填写的用户信息
        info.setCustomerName(name);
        info.setCustomerPhone(phone);
        if (needWeddingTime) {
            info.setServeTime(time.getMillis());
        }
        ServeCustomerInfoUtil.saveServeCustomerInfo(this, info);

        Intent intent = getIntent();
        intent.putExtra("info", info);
        setResult(RESULT_OK, intent);

        if (imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
        super.onOkButtonClick();
    }

    public void showDatetimePicker(View view) {
        showDatetimePicker(view, null);
    }

    public void showDatetimePicker(View view, MotionEvent event) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_date_picker);
            dialog.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 如果选择的时间在当前时间之前，则提示错误
                            DateTime pickedDT;
                            if (tempCalendar != null) {
                                pickedDT = new DateTime(tempCalendar);
                            } else {
                                pickedDT = new DateTime(calendar);
                            }
                            if (pickedDT.isBeforeNow()) {
                                Toast.makeText(OrderInfoEditActivity.this,
                                        getString(R.string.msg_wrong_time),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                dialog.dismiss();
                                if (tempCalendar != null) {
                                    calendar.setTime(tempCalendar.getTime());
                                }
                                setSwipeBackEnable(false);
                                time = new DateTime(calendar);
                                timeTv.setText(time.toString("yyyy-MM-dd"));
                            }
                        }
                    });
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        DatePickerView picker = (DatePickerView) dialog.findViewById(R.id.picker);
        picker.setYearLimit(2000, 49);
        if (time != null) {
            calendar.set(time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth());
        }
        picker.setCurrentCalender(calendar);
        picker.setOnPickerDateListener(this);
        picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics().density *
                (24 * 8));

        dialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showDatetimePicker(v, null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showDatetimePicker(v, event);
        }
        return true;
    }

    @Override
    public void onPickerDate(int year, int month, int day) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day);
        } else {
            tempCalendar.set(year, month - 1, day);
        }
    }
}
