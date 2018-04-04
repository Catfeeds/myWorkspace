package com.hunliji.marrybiz.fragment.market;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.ShopStatic;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 店铺数据图表信息fragment
 * Created by mo_yu on 2016/6/17.
 */
public class ShopDataInfoFragment extends RefreshFragment implements OnChartGestureListener,
        OnChartValueSelectedListener, View.OnClickListener {

    public final static int STORE_SHOP_DATA = 0;//店铺数据
    public final static int WORK_SHOP_DATA = 1;//套餐数据
    public final static int CASE_SHOP_DATA = 2;//案例数据
    @BindView(R.id.tv_shop_data_percentage)
    TextView tvShopDataPercentage;
    @BindView(R.id.rl_shop_data_percentage)
    RelativeLayout rlShopDataPercentage;
    @BindView(R.id.tv_shop_data_count_tip)
    TextView tvShopDataCountTip;
    @BindView(R.id.tv_shop_data_count)
    TextView tvShopDataCount;
    @BindView(R.id.rl_msg_shop_data_tip)
    RelativeLayout rlMsgShopDataTip;
    @BindView(R.id.line_chant)
    LineChart mChart;
    @BindView(R.id.line_chant_lock)
    RelativeLayout lineChantLock;
    private Unbinder unbinder;

    private int type;//选择的数据类型
    private ShopStatic shopStatic;
    private ArrayList<Integer> list = new ArrayList<>();

    public static ShopDataInfoFragment newInstance(ShopStatic shopStatic, int type) {
        ShopDataInfoFragment fragment = new ShopDataInfoFragment();
        fragment.shopStatic = shopStatic;
        fragment.type = type;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_data_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        switch (type) {
            case STORE_SHOP_DATA:
                list = shopStatic.getStoreList();
                tvShopDataCountTip.setText(getString(R.string.msg_shop_data_tip, "店铺"));
                if (shopStatic != null) {
                    tvShopDataPercentage.setText(Html.fromHtml(getString(R.string
                                    .msg_shop_data_percentage,
                            shopStatic.getCity(),
                            shopStatic.isPr0() ? shopStatic.getMultilPvRank() : "*%",
                            shopStatic.getProperty())));
                }
                break;
            case WORK_SHOP_DATA:
                list = shopStatic.getWorkList();
                tvShopDataCountTip.setText(getString(R.string.msg_shop_data_tip, "套餐"));
                if (shopStatic != null) {
                    tvShopDataPercentage.setText(Html.fromHtml(getString(R.string
                                    .msg_shop_data_percentage,
                            shopStatic.getCity(),
                            shopStatic.isPr0() ? shopStatic.getMultilpacketPvRank() : "*%",
                            shopStatic.getProperty())));
                }
                break;
            case CASE_SHOP_DATA:
                list = shopStatic.getCaseList();
                tvShopDataCountTip.setText(getString(R.string.msg_shop_data_tip, "案例"));
                if (shopStatic != null) {
                    tvShopDataPercentage.setText(Html.fromHtml(getString(R.string
                                    .msg_shop_data_percentage,
                            shopStatic.getCity(),
                            shopStatic.isPr0() ? shopStatic.getMultilexamplePvRank() : "*%",
                            shopStatic.getProperty())));
                }
                break;
        }
        if (shopStatic.isPr0()) {
            mChart.setVisibility(View.VISIBLE);
            lineChantLock.setVisibility(View.GONE);
            rlMsgShopDataTip.setVisibility(View.VISIBLE);
        } else {
            mChart.setVisibility(View.GONE);
            lineChantLock.setVisibility(View.VISIBLE);
            rlMsgShopDataTip.setVisibility(View.GONE);
        }
        initLineChat();
    }


    private void initLineChat() {
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.setScrollbarFadingEnabled(true);
        mChart.setDragEnabled(true);// 是否可以拖拽
        mChart.setScaleEnabled(false);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        yAxis.setAxisMaxValue(800f);
        yAxis.setAxisMinValue(0f);
        yAxis.setAxisLineColor(Color.parseColor("#f8f8f8"));
        yAxis.setGridColor(Color.parseColor("#f8f8f8")); //X轴上的刻度竖线的颜色
        yAxis.setGridLineWidth(1f); //X轴上的刻度竖线的宽 float类型
        yAxis.setTextSize(10);
        yAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
        yAxis.setDrawZeroLine(false);
        yAxis.setLabelCount(4, false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.parseColor("#f8f8f8"));
        xAxis.setGridColor(Color.parseColor("#f8f8f8")); //X轴上的刻度竖线的颜色
        xAxis.setGridLineWidth(1f); //X轴上的刻度竖线的宽 float类型
        xAxis.setTextSize(10);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);

        mChart.getAxisRight()
                .setEnabled(false);// 隐藏右边 的坐标轴
        mChart.getLegend()
                .setEnabled(false);// 不显示比例图标示
        //自定义缩放(float scaleX, float scaleY, float x, float y)X缩放倍数，Y缩放倍数，x坐标，y坐标。1f是原大小
        if (shopStatic != null && shopStatic.getShopDateList() != null) {
            float size = shopStatic.getShopDateList()
                    .size() - 1;
            float scale = size / 6.0f;
            if (scale < 1) {
                scale = 1.0f;
            }
            mChart.zoom(scale, 1.0f, 0f, 0f);
            int maxValue = 1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) > maxValue) {
                    maxValue = list.get(i);
                }
            }
            float max = (int) (maxValue * 1.4);
            yAxis.setAxisMaxValue(max);
            setData(list, shopStatic.getShopDateList());
        }

    }

    //设置数据
    private void setData(ArrayList<Integer> values, ArrayList<String> xVals) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        // y轴的数据
        for (int i = 0; i < values.size(); i++) {
            float val = values.get(i);
            yVals.add(new Entry(val, i));
        }

        LineDataSet set;

        if (mChart.getData() != null && mChart.getData()
                .getDataSetCount() > 0) {
            set = (LineDataSet) mChart.getData()
                    .getDataSetByIndex(0);
            set.setYVals(yVals);
            mChart.getData()
                    .setXVals(xVals);
            mChart.getData()
                    .notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set = new LineDataSet(yVals, "");
            set.setColor(ContextCompat.getColor(getContext(), R.color.color_yellow));//设置趋势线的颜色
            set.setLineWidth(2f);//设置趋势线的宽度
            set.setCircleColor(Color.parseColor("#99ffcd3b"));
            //设置结点外圆圈的颜色
            set.setCircleRadius(6f);//设置结点外圆圈的半径
            set.setDrawCircleHole(true);//设置结点内圆圈可绘制
            set.setCircleColorHole(ContextCompat.getColor(getContext(),
                    R.color.color_yellow));//设置结点内圆圈的颜色
            set.setCircleHoleRadius(4f);//设置结点内圆圈的半径
            set.setDrawValues(false);//不显示坐标轴上的值
            set.setHighlightLineWidth(1f);// 高亮的线的宽度
            set.setHighLightColor(ContextCompat.getColor(getContext(),
                    R.color.color_yellow));// 高亮的线的颜色
            set.setDrawHorizontalHighlightIndicator(false);// 横向高亮线设置为不显示
            set.setDrawFilled(true);//填充包含区域
            set.setValueTextSize(10f);      //设置坐标提示文字的大小
            set.setValueTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorBlack3));  //设置坐标提示文字的颜色

            set.setFillColor(Color.parseColor("#99ffcd3b"));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);
            // set data
            mChart.setData(data);
            if (xVals.size() > 0) {
                //默认选择昨天的数据（最后一条）
                int position = xVals.size() - 1;
                mChart.highlightValue(position, 0, true);
                mChart.moveViewToX(position);
                mChart.invalidate();
            }
        }
    }

    @Override
    public void onViewCreated(
            View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onChartGestureStart(
            MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(
            MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(
            Entry e, int dataSetIndex, Highlight h) {
        switch (type) {
            case STORE_SHOP_DATA:
                tvShopDataCount.setText(getString(R.string.msg_shop_data,
                        shopStatic.getShopDateList()
                                .get(e.getXIndex()) + " 店铺",
                        (int) e.getVal()));
                break;
            case WORK_SHOP_DATA:
                tvShopDataCount.setText(getString(R.string.msg_shop_data,
                        shopStatic.getShopDateList()
                                .get(e.getXIndex()) + " 套餐",
                        (int) e.getVal()));
                break;
            case CASE_SHOP_DATA:
                tvShopDataCount.setText(getString(R.string.msg_shop_data,
                        shopStatic.getShopDateList()
                                .get(e.getXIndex()) + " 案例",
                        (int) e.getVal()));
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {

    }

}
