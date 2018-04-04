package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DateTimePickerView;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;


public class ContactInfoActivity extends HljBaseActivity implements DTPicker
        .OnPickerDateTimeListener {

    @BindView(R.id.et_contact_name)
    EditText etContactName;
    @BindView(R.id.et_contact_phone)
    EditText etContactPhone;
    @BindView(R.id.tv_car_use_time)
    TextView tvCarUseTime;
    @BindView(R.id.et_car_use_addr)
    EditText etCarUseAddr;
    private Dialog dialog;
    private Calendar tempCalendar;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String oldName = "";
    private String oldPhone = "";
    private String oldTime = "";
    private String oldAddr = "";
    private String time = "";
    private Dialog confirmDialog;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setOkText(R.string.label_save);
        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));

        oldName = getIntent().getStringExtra("name") == null ? "" : getIntent().getStringExtra(
                "name");
        oldPhone = getIntent().getStringExtra("phone") == null ? "" : getIntent().getStringExtra(
                "phone");
        oldAddr = getIntent().getStringExtra("address") == null ? "" : getIntent().getStringExtra(
                "address");
        oldTime = getIntent().getStringExtra("time") == null ? "" : getIntent().getStringExtra(
                "time");

        etContactName.setText(oldName);
        etContactPhone.setText(oldPhone);
        etCarUseAddr.setText(oldAddr);

        setSwipeBackEnable(false);
        calendar = Calendar.getInstance();

        if (!JSONUtil.isEmpty(oldTime)) {
            Date oldDateTime = JSONUtil.getDateFromString(oldTime);
            if (oldDateTime.after(new Date())) {
                calendar.setTime(JSONUtil.getDateFromString(oldTime));
                time = dateFormat.format(oldDateTime);
                tvCarUseTime.setText(time);
            } else {
                oldTime = "";
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public void onOkButtonClick() {
        if (etContactName.length() == 0) {
            Toast.makeText(this, getString(R.string.msg_name_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etContactPhone.length() == 0) {
            Toast.makeText(this, getString(R.string.msg_phone_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (tvCarUseTime.length() == 0) {
            Toast.makeText(this, getString(R.string.msg_time_empty), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (etCarUseAddr.length() == 0) {
            Toast.makeText(this, getString(R.string.msg_empty_car_addr), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.isMobileNO(etContactPhone.getText()
                .toString())) {
            Toast.makeText(this, getString(R.string.hint_new_number_error), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // 如果选择的时间在当前时间之前，则提示错误
        Calendar nowCalendar = Calendar.getInstance();
        if (calendar.before(nowCalendar)) {
            Toast.makeText(ContactInfoActivity.this,
                    getString(R.string.msg_wrong_time),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String name = etContactName.getText()
                .toString();
        String phone = etContactPhone.getText()
                .toString();
        String address = etCarUseAddr.getText()
                .toString();

        Intent intent = getIntent();
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("time", time);
        intent.putExtra("address", address);

        setResult(RESULT_OK, intent);

        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);

        super.onOkButtonClick();
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    public void confirmBack() {
        String newName = etContactName.getText()
                .toString();
        String newPhone = etContactPhone.getText()
                .toString();
        String newAddr = etCarUseAddr.getText()
                .toString();

        if (!newName.equals(oldName) || !newPhone.equals(oldPhone) || !time.equals(oldTime) ||
                !newAddr.equals(
                oldAddr)) {
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
                        if (imm != null && getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        ContactInfoActivity.super.onBackPressed();
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
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_out_right);
        }
    }

    @OnClick(R.id.car_use_time_layout)
    void onTimeSelect() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_date_time_picker___cm, null);
            v.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            v.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar nowCalendar = Calendar.getInstance();
                            if (tempCalendar != null && tempCalendar.before(nowCalendar)) {
                                Toast.makeText(ContactInfoActivity.this,
                                        getString(R.string.msg_wrong_time4),
                                        Toast.LENGTH_SHORT)
                                        .show();

                                return;
                            } else {
                                dialog.dismiss();
                                if (tempCalendar != null) {
                                    calendar.setTime(tempCalendar.getTime());
                                }
                                time = dateFormat.format(calendar.getTime());
                                tvCarUseTime.setText(time);
                            }
                        }
                    });
            DateTimePickerView picker = (DateTimePickerView) v.findViewById(R.id.picker);
            Calendar nowCalendar = Calendar.getInstance();
            picker.setYearLimit(nowCalendar.get(Calendar.YEAR), 5);
            picker.setCurrentCalender(calendar);
            picker.setOnPickerDateTimeListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            dialog.setContentView(v);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }

        dialog.show();
    }

    @Override
    public void onPickerDateAndTime(int year, int month, int day, int hour, int minute) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day, hour, minute);
        } else {
            tempCalendar.set(year, month - 1, day, hour, minute);
        }
    }
}
