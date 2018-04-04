package com.hunliji.hljcarlibrary.views.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hunlijicalendar.HLJHotelCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 婚车档期查询
 * Created by mo_yu on 2016/11/26.
 */
public class WeddingCarCalendarActivity extends HljBaseActivity implements HLJHotelCalendarView
        .OnPageChangeListener, HLJHotelCalendarView.OnDateSelectedListener, HLJHotelCalendarView
        .OnMonthViewFinishUpdateListener {

    public static final String WEDDING_CAR_PRODUCT = "wedding_car_product";
    public static final String ARG_CITY = "city";

    @BindView(R2.id.calendar)
    HLJHotelCalendarView calendarView;
    @BindView(R2.id.btn_submit_calendar)
    Button btnSubmitCalendar;
    @BindView(R2.id.empty)
    View empty;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    private Calendar currentCalendar;
    private Calendar selectedDay;
    private int mCurrentYear;
    private int mCurrentMonth;
    private String mYearMoth;
    private ArrayList<String> dateVacationDays;//节假日
    private ArrayList<String> mSelectDates;//选择的日期集合
    private HashMap<String, ArrayList<Integer>> hashMapAuspiciousDays = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> hashMapVacationDays = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> hashMapSelectDays = new HashMap<>();
    private int count;
    private City city;

    private boolean isVisible;
    private Dialog backDialog;
    private WeddingCarProduct carProduct;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_calendar___car);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
    }

    private void initConstant() {
        if (getIntent() != null) {
            carProduct = getIntent().getParcelableExtra(WEDDING_CAR_PRODUCT);
            city = getIntent().getParcelableExtra(ARG_CITY);
        }
        if (city == null) {
            city = LocationSession.getInstance()
                    .getCity(this);
        }
        selectedDay = Calendar.getInstance();
        dateVacationDays = new ArrayList<>();
        mSelectDates = new ArrayList<>();
    }

    private void initWidget() {
        Calendar beginCalendar = Calendar.getInstance(); // 从当前月份开始
        currentCalendar = Calendar.getInstance();
        setCurrentCalendar(currentCalendar);
        Calendar endCalendar = new GregorianCalendar(mCurrentYear + 1,
                12,
                0,
                0,
                0,
                0); // 在后一年的12月底结束

        calendarView.setAuspiciousDaysMap(hashMapAuspiciousDays);
        hashMapVacationDays.put(mCurrentYear + "-" + mCurrentMonth,
                HljTimeUtils.convertDateToDays(dateVacationDays));
        calendarView.setDotDaysMap(hashMapVacationDays);
        calendarView.init(this, selectedDay, beginCalendar, endCalendar, Calendar.SUNDAY);
        calendarView.setOnPageChangerListener(this);
        calendarView.setOnDateSelectedListener(this);
        calendarView.setOnMonthViewFinishUpdate(this);
    }

    @Override
    public void onPageChange(Calendar calendar) {
        setCurrentCalendar(calendar);
        calendarView.setCurrentCalendar(calendar);
        calendarView.setEventDots(HljTimeUtils.convertDateToDays(dateVacationDays));
        calendarView.invalidateTheCurrentMonthView();
    }

    private void setCurrentCalendar(Calendar calendar) {
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1;
        mYearMoth = mCurrentYear + "-" + mCurrentMonth;
    }

    private String getSelectDateStr(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(date);
    }

    @Override
    protected void onRestart() {
        calendarView.setEventDots(HljTimeUtils.convertDateToDays(dateVacationDays));
        calendarView.invalidateTheCurrentMonthView();
        super.onRestart();
    }

    @Override
    public void onSelectedDayChange(Calendar calendar) {
        Calendar mCalendar = (Calendar) calendar.clone();
        ArrayList<Integer> selectDayFlags = hashMapSelectDays.get(mYearMoth);
        if (selectDayFlags == null) {
            selectDayFlags = new ArrayList<>();
        }
        if (selectDayFlags.contains(calendar.get(Calendar.DAY_OF_MONTH))) {
            count = count - 1;
            selectDayFlags.remove((Object) calendar.get(Calendar.DAY_OF_MONTH));
            mSelectDates.remove(getSelectDateStr(mCalendar));
        } else {
            if (count >= 4) {
                Toast.makeText(this,
                        getString(R.string.label_hint_submit_calendar___car),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            count = count + 1;
            selectDayFlags.add(calendar.get(Calendar.DAY_OF_MONTH));
            mSelectDates.add(getSelectDateStr(mCalendar));
        }
        hashMapSelectDays.put(mYearMoth, selectDayFlags);
        calendarView.setSelectDayMap(hashMapSelectDays);
        calendarView.invalidateTheCurrentMonthView();
        btnSubmitCalendar.setEnabled(!mSelectDates.isEmpty());
    }

    @Override
    public void onMonthViewFinishUpdate() {
        isVisible = true;
        empty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onOkButtonClick() {
        if (isVisible) {//当日历显示完全后
            calendarView.showDatePicker(this);
        }
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    private void showBackDialog() {
        if (backDialog != null && backDialog.isShowing()) {
            return;
        }

        if (backDialog == null) {
            backDialog = DialogUtil.createDoubleButtonDialog(this,
                    "您所在的城市婚车租赁十分火热，提前查询预订才能确保结婚当天租到心仪的婚车哦",
                    "继续查询",
                    "退出查询",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            backDialog.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            backDialog.dismiss();
                            WeddingCarCalendarActivity.super.onBackPressed();
                        }
                    });
            Button btnCancel = (Button) backDialog.findViewById(R.id.btn_cancel);
            btnCancel.setTextColor(getResources().getColor(R.color.colorGray));
        }

        backDialog.show();
    }

    @OnClick(R2.id.btn_submit_calendar)
    public void onClick() {
        if (mSelectDates != null && mSelectDates.size() > 0) {
            submitCalendar();
        } else {
            Toast.makeText(this,
                    getString(R.string.label_hint_submit_empty___car),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void submitCalendar() {
        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }

        if (carProduct == null || carProduct.getMerchantComment() == null) {
            return;
        }
        if (carProduct.getMerchantComment() == null) {
            return;
        }
        City city = carProduct.getCity();
        WSTrack wsTrack = new WSTrack("发起咨询页");
        TrackWeddingCarProduct trackWeddingCarProduct = new TrackWeddingCarProduct(carProduct);
        wsTrack.setAction(WSTrack.WEDDING_CAR);
        wsTrack.setCarProduct(trackWeddingCarProduct);
        Postcard p = ARouter.getInstance()
                .build(RouterPath.IntentPath.Customer.WsCustomChatActivityPath
                        .WS_CUSTOMER_CHAT_ACTIVITY)
                .withLong(RouterPath.IntentPath.Customer.BaseWsChat.ARG_USER_ID,
                        carProduct.getMerchantComment()
                                .getUserId())
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_WS_TRACK, wsTrack)
                .withParcelable(RouterPath.IntentPath.Customer.BaseWsChat.ARG_CITY, city);
        if (mSelectDates != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("我是")
                    .append(city.getName())
                    .append("的用户，想咨询");
            for (String string : mSelectDates) {
                builder.append(string)
                        .append("、");
            }
            if (builder.lastIndexOf("、") == builder.length() - 1) {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("这个婚车是否可租");
            p.withString(RouterPath.IntentPath.Customer.BaseWsChat.ARG_AUTO_MSG,
                    builder.toString());
        }
        p.navigation(this);
    }
}