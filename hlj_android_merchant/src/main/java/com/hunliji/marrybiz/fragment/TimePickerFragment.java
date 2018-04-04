package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chen_bin on 14/11/17.
 * 时间选择控件,可以设置初始时间,选择确定后的时间由 TimeSetListener 监听
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog
        .OnTimeSetListener {

    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    /**
     * 设定控件初始化时的默认时间,如果不设置则默认为当前的时间
     *
     * @param calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * 设定确定时间后的回调监听
     *
     * @param timeSetListener
     */
    public void setTimeSetListener(TimePickerDialog.OnTimeSetListener timeSetListener) {
        this.timeSetListener = timeSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c;
        if (calendar == null) {
            c = Calendar.getInstance(Locale.getDefault());
        } else {
            c = calendar;
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeSetListener.onTimeSet(view, hourOfDay, minute);
    }
}
