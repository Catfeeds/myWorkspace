package me.suncloud.marrymemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;

/**
 * 选择婚期的dialog
 * Created by jinxin on 2017/1/4 0004.
 */

public class WeddingDateDialog extends Dialog implements DatePicker.OnDateChangedListener {
    @BindView(R.id.date_picker)
    DatePicker datePicker;
    @BindView(R.id.btn_count_down)
    Button btnCountDown;

    private Context mContext;
    private Calendar tempCalendar;
    private onDateSelectedListener onDateSelectedListener;

    public WeddingDateDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_wedding_date, null);
        ButterKnife.bind(this, view);
        setContentView(view);
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        datePicker.init(Calendar.getInstance()
                        .get(Calendar.YEAR),
                Calendar.getInstance()
                        .get(Calendar.MONTH),
                Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH),
                this);

        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        Date minDate = new Date();//当前时间
        Calendar minCalendar = Calendar.getInstance();//日历对象
        minCalendar.setTime(minDate);//设置当前日期
        minCalendar.add(Calendar.YEAR, -1);
        long minDateMillis = minCalendar.getTimeInMillis();
        Date maxDate = new Date();
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.setTime(maxDate);
        maxCalendar.add(Calendar.YEAR, 10);
        long maxDateMillis = maxCalendar.getTimeInMillis();
        datePicker.setMinDate(minDateMillis);
        datePicker.setMaxDate(maxDateMillis);
    }


    public void setDateTime(DateTime time) {
        if (time != null) {
            int year = time.getYear();
            int month = Math.max(time.monthOfYear().get() - 1, 0);
            int day = time.dayOfMonth()
                    .get();
            datePicker.updateDate(year, month, day);
        }
    }


    @OnClick(R.id.btn_close)
    public void onClose(View view) {
        if (isShowing()) {
            dismiss();
        }
    }

    @OnClick(R.id.btn_count_down)
    public void onCountDown(View view) {
        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(tempCalendar);
        }
        onClose(null);
    }

    public void setOnDateSelectedListener(
            onDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0);
        } else {
            tempCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
        }
    }

    public interface onDateSelectedListener {
        void onDateSelected(Calendar calendar);
    }

}
