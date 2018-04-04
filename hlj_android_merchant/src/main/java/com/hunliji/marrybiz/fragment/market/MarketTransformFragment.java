package com.hunliji.marrybiz.fragment.market;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wheelpickerlibrary.picker.SingleWheelPicker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.market.MarketApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.market.MarketTransform;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Func2;

/**
 * Created by hua_rong on 2017/8/15 0015
 */

public class MarketTransformFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.tv_statistical_period)
    TextView tvStatisticalPeriod;
    @BindView(R.id.iv_transform)
    ImageView ivTransform;
    @BindView(R.id.tv_consult_rate)
    TextView tvConsultRate;
    @BindView(R.id.ll_consult)
    LinearLayout llConsult;
    @BindView(R.id.tv_consult_order_rate)
    TextView tvConsultOrderRate;
    @BindView(R.id.ll_consult_order)
    LinearLayout llConsultOrder;
    @BindView(R.id.tv_order_rate)
    TextView tvOrderRate;
    @BindView(R.id.ll_order_rate)
    LinearLayout llOrderRate;
    @BindView(R.id.ll_detail_1)
    LinearLayout llDetail1;
    @BindView(R.id.ll_detail_2)
    LinearLayout llDetail2;
    @BindView(R.id.ll_detail_3)
    LinearLayout llDetail3;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    Unbinder unbinder;
    @BindView(R.id.tv_consult_title)
    TextView tvConsultTitle;
    @BindView(R.id.tv_consult_order_title)
    TextView tvConsultOrderTitle;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;

    private Dialog dialog;
    private List<String> dataList;
    private HljHttpSubscriber refreshSubscriber;
    private long merchantId;
    private String mondayOfThisWeek;
    private String mondayOfLastWeek;
    private SparseArray<DateTime> dateTimeHashMap;

    public static MarketTransformFragment newInstance() {
        Bundle args = new Bundle();
        MarketTransformFragment fragment = new MarketTransformFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        dataList = new ArrayList<>();
        dateTimeHashMap = new SparseArray<>();
        for (int i = 0; i < 52; i++) {
            dataList.add(getTime(getStartDate(i)) + "-" + getTime(getEndDate(i)));
            dateTimeHashMap.put(i, getStartDate(i));
        }
        MerchantUser merchantUser = Session.getInstance()
                .getCurrentUser(getContext());
        if (merchantUser != null) {
            merchantId = merchantUser.getMerchantId();
        }
        mondayOfThisWeek = getMonday().toString(Constants.DATE_FORMAT_SHORT);
        mondayOfLastWeek = getStartDate(0).toString(Constants.DATE_FORMAT_SHORT);
    }

    /**
     * 获取本周一
     *
     * @return
     */
    private DateTime getMonday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new DateTime(cal.getTime());
    }

    /**
     * 获取星期一
     *
     * @param week week==0 上周一
     * @return
     */
    public DateTime getStartDate(int week) {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 1 - dayOfWeek;
        cal.add(Calendar.DATE, offset - 7 * (week + 1));
        return new DateTime(cal.getTime());
    }

    /**
     * 获取星期天
     *
     * @param week week==0 上周日
     * @return
     */
    public DateTime getEndDate(int week) {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 7 - dayOfWeek;
        cal.add(Calendar.DATE, offset - 7 * (week + 1));
        return new DateTime(cal.getTime());
    }

    private String getTime(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.toString(getString(R.string.format_date_type4));
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_transform, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNetError();
        initView();
        scrollView.setOnRefreshListener(this);
        onRefresh(scrollView);
    }


    private void initView() {

        tvStatisticalPeriod.setText(getTime(getStartDate(0)) + "-" + getTime(getEndDate(0)));

        int imageWidth = CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),
                32);
        int imageHeight = (int) Math.round(imageWidth * 625.0 / 972.0);
        ivTransform.getLayoutParams().width = imageWidth;
        ivTransform.getLayoutParams().height = imageHeight;
        //咨询转化率
        RelativeLayout.LayoutParams consultParams = (RelativeLayout.LayoutParams) llConsult
                .getLayoutParams();
        consultParams.leftMargin = (int) Math.round(imageWidth * 232.0 / 530.0);
        consultParams.topMargin = (int) Math.round(imageHeight * 90.0 / 340.0);
        llConsult.setLayoutParams(consultParams);
        //咨询-下单转化率
        RelativeLayout.LayoutParams consultOrderParams = (RelativeLayout.LayoutParams)
                llConsultOrder.getLayoutParams();
        consultOrderParams.leftMargin = (int) Math.round(imageWidth * 210.0 / 530.0);
        consultOrderParams.topMargin = (int) Math.round(imageHeight * 194.0 / 340.0);
        llConsultOrder.setLayoutParams(consultOrderParams);
        //下单转化率
        RelativeLayout.LayoutParams orderParams = (RelativeLayout.LayoutParams) llOrderRate
                .getLayoutParams();
        orderParams.leftMargin = (int) Math.round(imageWidth * 420.0 / 530.0);
        orderParams.topMargin = (int) Math.round(imageHeight * 130.0 / 340.0);
        llOrderRate.setLayoutParams(orderParams);

        double textSize = 42 * CommonUtil.getDeviceSize(getContext()).x / 1080.0;
        double titleSize = 36 * CommonUtil.getDeviceSize(getContext()).x / 1080.0;
        tvConsultRate.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(textSize));
        tvOrderRate.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(textSize));
        tvConsultOrderRate.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(textSize));
        tvConsultOrderTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(titleSize));
        tvConsultTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(titleSize));
        tvOrderTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.round(titleSize));
    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(scrollView);
            }
        });
    }

    @Override
    public void refresh(Object... params) {

    }

    @OnClick(R.id.rl_statistical_period)
    void onSelectPeriod() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.single_wheel_picker___cm);
                final SingleWheelPicker picker = window.findViewById(R.id.picker);
                picker.setItems(dataList);
                TextView close = window.findViewById(R.id.close);
                TextView confirm = window.findViewById(R.id.confirm);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        int selectedItemIndex = picker.getCurrentItem();
                        tvStatisticalPeriod.setText(dataList.get(selectedItemIndex));
                        DateTime dateTime = dateTimeHashMap.get(selectedItemIndex);
                        mondayOfLastWeek = dateTime.toString(Constants.DATE_FORMAT_SHORT);
                        mondayOfThisWeek = dateTime.plusDays(7)
                                .toString(Constants.DATE_FORMAT_SHORT);
                        onRefresh(scrollView);
                    }
                });
                ViewGroup.LayoutParams params = window.getAttributes();
                Point point = CommonUtil.getDeviceSize(getContext());
                params.width = point.x;
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style);
            } else {
                dialog.dismiss();
            }
        }
        if (dialog != null) {
            dialog.show();
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            Observable<HljHttpResult<MarketTransform>> aObservable = MarketApi.getUserTransform(
                    merchantId,
                    mondayOfThisWeek);
            Observable<HljHttpResult<MarketTransform>> bObservable = MarketApi.getUserTransform(
                    merchantId,
                    mondayOfLastWeek);
            Observable<ResultZip> observable = Observable.zip(aObservable,
                    bObservable,
                    new Func2<HljHttpResult<MarketTransform>, HljHttpResult<MarketTransform>,
                            ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpResult<MarketTransform> thisWeek,
                                HljHttpResult<MarketTransform> lastWeek) {
                            return new ResultZip(thisWeek, lastWeek);
                        }
                    });
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip resultZip) {
                            MarketTransform thisWeek = resultZip.thisWeek.getData();
                            MarketTransform lastWeek = resultZip.lastWeek.getData();
                            if (thisWeek != null && lastWeek != null) {
                                setView(thisWeek, lastWeek);
                            }
                        }
                    })
                    .setProgressBar(scrollView.isRefreshing() ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setPullToRefreshBase(scrollView)
                    .build();
            observable.subscribe(refreshSubscriber);
        }
    }

    private void setView(MarketTransform thisWeek, MarketTransform lastWeek) {
        double orderPrice = thisWeek.getOrderPrice();
        double lastOrderPrice = lastWeek.getOrderPrice();
        long viewNum = thisWeek.getViewNum();
        long lastViewNum = lastWeek.getViewNum();
        long connectNum = thisWeek.getConnectNum();
        long lastConnectNum = lastWeek.getConnectNum();
        long orderNum = thisWeek.getOrderNum();
        long lastOrderNum = lastWeek.getOrderNum();
        double totalPrice = orderPrice * orderNum;
        double lastTotalPrice = lastOrderPrice * lastOrderNum;

        setDetailView((LinearLayout) llDetail1.getChildAt(0),
                getString(R.string.label_number_of_visitors),
                viewNum,
                lastViewNum,
                true);

        setDetailView((LinearLayout) llDetail1.getChildAt(1),
                getString(R.string.label_number_of_contacts),
                connectNum,
                lastConnectNum,
                true);

        setDetailView((LinearLayout) llDetail2.getChildAt(0),
                getString(R.string.label_number_of_orders),
                orderNum,
                lastOrderNum,
                true);

        setDetailView((LinearLayout) llDetail2.getChildAt(1),
                getString(R.string.label_order_amount),
                totalPrice,
                lastTotalPrice,
                false);

        setDetailView((LinearLayout) llDetail3.getChildAt(0),
                getString(R.string.label_customer_price),
                orderPrice,
                lastOrderPrice,
                false);

        tvConsultRate.setText(thisWeek.getConsultRate());
        tvOrderRate.setText(thisWeek.getOrderRate());
        tvConsultOrderRate.setText(thisWeek.getConsultOrderRate());

        //咨询转换
        setBottomView((LinearLayout) llBottom.getChildAt(0),
                getString(R.string.label_consult_conversion_rate),
                thisWeek.geConsultDoubleRate(),
                lastWeek.geConsultDoubleRate(),
                true);

        setBottomView((LinearLayout) llBottom.getChildAt(1),
                getString(R.string.label_consult_order_conversion_rate),
                thisWeek.getConsultOrderDoubleRate(),
                lastWeek.getConsultOrderDoubleRate(),
                true);

        setBottomView((LinearLayout) llBottom.getChildAt(2),
                getString(R.string.label_order_conversion_rate),
                thisWeek.getOrderDoubleRate(),
                lastWeek.getOrderDoubleRate(),
                false);
    }

    private void setDetailView(
            LinearLayout linearLayout,
            String name,
            double count,
            double lastCount,
            boolean isLong) {
        linearLayout.getLayoutParams().width = (CommonUtil.getDeviceSize(getContext()).x -
                CommonUtil.dp2px(
                getContext(),
                32)) / 2;
        TextView tvName = linearLayout.findViewById(R.id.tv_name);
        TextView tvCount = linearLayout.findViewById(R.id.tv_count);
        TextView tvRate = linearLayout.findViewById(R.id.tv_rate);
        tvName.setText(name);
        tvCount.setText(isLong ? Util.getIntegerPartFromDouble(count) : MarketTransform.getFloat(
                count));
        if (lastCount > 0) {
            double rate = (count - lastCount) / (lastCount * 1.0);
            tvRate.setText("" + MarketTransform.getFloat(rate * 100) + "%");
            tvRate.setTextColor(Color.parseColor(rate > 0 ? "#ff5843" : "#5bb966"));
        } else {
            tvRate.setText("--");
            tvRate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        }

    }

    private void setBottomView(
            LinearLayout linearLayout,
            String title,
            Double thisRate,
            Double lastRate,
            boolean lineVisible) {
        TextView tvTitle = linearLayout.findViewById(R.id.tv_title);
        TextView tvThisWeek = linearLayout.findViewById(R.id.tv_this_week);
        TextView tvLastWeek = linearLayout.findViewById(R.id.tv_last_week);
        TextView tvFluctuation = linearLayout.findViewById(R.id.tv_fluctuation);
        View line = linearLayout.findViewById(R.id.line);
        tvTitle.setText(title);
        if (thisRate != null) {
            tvThisWeek.setText("" + MarketTransform.getFloat(thisRate) + "%");
        } else {
            tvThisWeek.setText("--");
        }
        if (lastRate != null) {
            tvLastWeek.setText("" + MarketTransform.getFloat(lastRate) + "%");
        } else {
            tvLastWeek.setText("--");
        }
        if (thisRate != null && lastRate != null && lastRate > 0) {
            double rate = (thisRate - lastRate) / lastRate;
            tvFluctuation.setTextColor(Color.parseColor(rate > 0 ? "#ff5843" : "#5bb966"));
            tvFluctuation.setText("" + MarketTransform.getFloat(rate * 100) + "%");
        } else {
            tvFluctuation.setText("--");
            tvFluctuation.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
        }
        line.setVisibility(lineVisible ? View.VISIBLE : View.GONE);
    }


    private class ResultZip {
        HljHttpResult<MarketTransform> thisWeek;//上周转化数据
        HljHttpResult<MarketTransform> lastWeek;//上上周转化数据

        ResultZip(
                HljHttpResult<MarketTransform> thisWeek, HljHttpResult<MarketTransform> lastWeek) {
            this.thisWeek = thisWeek;
            this.lastWeek = lastWeek;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber);
    }

}
