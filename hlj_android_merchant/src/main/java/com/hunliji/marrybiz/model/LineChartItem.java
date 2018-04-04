package com.hunliji.marrybiz.model;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.widget.MyMarkerView;

import java.text.DecimalFormat;

public class LineChartItem extends ChartItem {

    private Typeface mTf;
    private String mTitle;
    private String mDesc;
    private Context mContext;
    private String mNoData;
    private boolean emptyData;

    public LineChartItem(ChartData<?> cd, Context c, String title, String desc, boolean empty) {
        super(cd);

        mContext = c;
        mTitle = title;
        mDesc = desc;
        mNoData = desc;
        emptyData = empty;
        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_LINECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_linechart, null);
            holder.chart = (LineChart) convertView.findViewById(R.id.chart);
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.hintNoData = convertView.findViewById(R.id.hint_no_data);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(mTitle);
        if (emptyData) {
            holder.hintNoData.setVisibility(View.VISIBLE);
            holder.hintNoData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);

        holder.chart.setMarkerView(mv);
        Highlight h = new Highlight(0, 0);
        holder.chart.highlightTouch(h);
        holder.chart.setNoDataTextDescription(mNoData);

        holder.chart.setDescription(mDesc);
        holder.chart.setDrawGridBackground(false);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5,false);
        leftAxis.setStartAtZero(true);
//        leftAxis.setValueFormatter(new MyValueFormatter());

        YAxis rightAxis = holder.chart.getAxisRight();
        leftAxis.setTypeface(mTf);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5,false);
        rightAxis.setStartAtZero(true);
//        rightAxis.setValueFormatter(new MyValueFormatter());

        holder.chart.setScaleEnabled(true);

        holder.chart.setData((LineData) mChartData);
        mChartData.setValueFormatter(new MyValueFormatter());
        holder.chart.animateX(750);

        return convertView;
    }

    private static class ViewHolder {
        LineChart chart;
        TextView titleView;
        View hintNoData;
    }

    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("########");
        }

        @Override
        public String getFormattedValue(
                float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return null;
        }
    }
}
