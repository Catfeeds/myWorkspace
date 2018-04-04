package com.hunliji.hunlijicalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hunlijicalendar.R;

import java.util.Calendar;

/**
 * Created by chenbin on 2016/6/25 0025.
 */
public class WeekTitleAdapter extends BaseAdapter {

    private Context context;
    private String[] titles;
    private int firstDayOfWeek;
    private int color;
    private int defaultColor;
    private LayoutInflater inflater;

    public WeekTitleAdapter(
            Context context, String[] titles, int firstDayOfWeek, int color, int defaultColor) {
        this.context = context;
        this.titles = titles;
        this.firstDayOfWeek = firstDayOfWeek;
        this.color = color;
        this.defaultColor = defaultColor;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder {
        public TextView calendarCaptionDate;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            vh = new ViewHolder();
            view = inflater.inflate(R.layout.calendar_week_title_item, null);
            vh.calendarCaptionDate = view.findViewById(R.id.calendar_caption_date);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.calendarCaptionDate.setText(titles[i]);
        if ((firstDayOfWeek == Calendar.SUNDAY && (i == 0 || i == titles.length - 1)) ||
                (firstDayOfWeek == Calendar.MONDAY && (i == titles.length - 1 || i == titles
                        .length - 2))) {
            vh.calendarCaptionDate.setTextColor(color);
        } else {
            vh.calendarCaptionDate.setTextColor(defaultColor);
        }
        return view;
    }
}
