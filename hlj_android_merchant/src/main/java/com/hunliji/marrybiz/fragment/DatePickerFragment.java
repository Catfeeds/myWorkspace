package com.hunliji.marrybiz.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by LuoHanLin on 14/11/17.
 * 日期选择控件,可以设置初始日期,选择确定后的时间由 DateSetListener 监听
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog
        .OnDateSetListener {

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    /**
     * 设定控件初始化时的默认日期,如果不设置则默认为当前的日期
     *
     * @param calendar
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * 设定确定日期后的回调监听
     *
     * @param dateSetListener
     */
    public void setDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c;
        if (calendar == null) {
            c = Calendar.getInstance();
        } else {
            c = calendar;
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        dateSetListener.onDateSet(datePicker, i, i2, i3);
    }
}
