package me.suncloud.marrymemo.view.orders;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import me.suncloud.marrymemo.util.TimeUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * 婚宴酒店订单联系人填写
 */
public class HotelOrderInfoEditActivity extends HljBaseActivity implements View
        .OnFocusChangeListener, View.OnTouchListener, DTPicker.OnPickerDateListener {

    public static final String ARG_INFO = "info";

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.serve_time_layout)
    LinearLayout serveTimeLayout;

    private Calendar calendar;
    private Calendar tempCalendar;
    private Dialog dialog;
    private Dialog confirmDialog;
    private String oldName;
    private String oldPhone;
    private DateTime oldTime;
    private DateTime time;
    private InputMethodManager imm;

    private ServeCustomerInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_order_info_edit);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_order_info_edit);
        setOkText(R.string.label_finished);
        setSwipeBackEnable(false);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        calendar = Calendar.getInstance();
        info = getIntent().getParcelableExtra(ARG_INFO);
        if (info == null) {
            return;
        }

        oldName = info.getCustomerName();
        oldPhone = info.getCustomerPhone();
        oldTime = info.getServeTime();
        if (oldTime != null) {
            calendar.setTimeInMillis(oldTime.getMillis());
            tvTime.setText(oldTime.toString(HljCommon.DateFormat.DATE_FORMAT_SHORT));
            time = new DateTime(oldTime.getMillis());
        }
        etName.setText(oldName);
        etPhone.setText(oldPhone);
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
        if (!etName.getText()
                .toString()
                .equals(oldName) || !etPhone.getText()
                .toString()
                .equals(oldPhone) || (time != null && !time.equals(oldTime))) {
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
                        HotelOrderInfoEditActivity.super.onBackPressed();
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
        if (etName.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_name_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etPhone.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (tvTime.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_time_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.isMobileNO(etPhone.getText()
                .toString())) {
            Toast.makeText(this, getString(R.string.hint_new_number_error), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // 如果选择的时间在当前时间之前，则提示错误
        Calendar nowCalendar = Calendar.getInstance();
        if (TimeUtil.calendarCompareTo(nowCalendar, calendar) > 0) {
            Toast.makeText(HotelOrderInfoEditActivity.this,
                    getString(R.string.msg_wrong_time),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String name = etName.getText()
                .toString();
        String phone = etPhone.getText()
                .toString();

        // 存储当前填写的用户信息
        info.setCustomerName(name);
        info.setCustomerPhone(phone);
        info.setServeTime(time.getMillis());
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
                                Toast.makeText(HotelOrderInfoEditActivity.this,
                                        "婚期时间不能再今天之前",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                dialog.dismiss();
                                if (tempCalendar != null) {
                                    calendar.setTime(tempCalendar.getTime());
                                }
                                setSwipeBackEnable(false);
                                time = new DateTime(calendar);
                                tvTime.setText(time.toString(HljCommon.DateFormat
                                        .DATE_FORMAT_SHORT));
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
