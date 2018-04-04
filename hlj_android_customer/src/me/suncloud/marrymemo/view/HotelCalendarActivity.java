package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
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
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.PostScheduleDateBody;

/**
 * 酒店档期查询
 * Created by mo_yu on 2016/11/26.
 */
public class HotelCalendarActivity extends HljBaseActivity implements HLJHotelCalendarView
        .OnPageChangeListener, HLJHotelCalendarView.OnDateSelectedListener, HLJHotelCalendarView
        .OnMonthViewFinishUpdateListener {

    @BindView(R.id.calendar)
    HLJHotelCalendarView calendarView;
    @BindView(R.id.btn_submit_calendar)
    Button btnSubmitCalendar;
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
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

    private boolean isVisible;
    //    private LinearLayoutManager layoutManager;
    //    private HotelCalendarRecyclerAdapter adapter;

    //档期查询提交
    private HljHttpSubscriber submitSubscriber;
    private long merchantId;
    private long hallId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_calendar);
        ButterKnife.bind(this);
        hallId = getIntent().getLongExtra("hall_id", 0);
        merchantId = getIntent().getLongExtra("id", 0);
        selectedDay = Calendar.getInstance();
        dateVacationDays = new ArrayList<>();
        mSelectDates = new ArrayList<>();

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

        //选择的日期列表
        //        adapter = new HotelCalendarRecyclerAdapter(this, mCalendars);
        //        layoutManager = new LinearLayoutManager(this);
        //        recyclerView.setLayoutManager(layoutManager);
        //        recyclerView.setAdapter(adapter);
        //        adapter.setOnDeleteItem(new HotelCalendarRecyclerAdapter.OnDeleteItem() {
        //            @Override
        //            public void onDeleteRecord(int position, Object object) {
        //                Calendar calendar = (Calendar) object;
        //                Calendar mCalendar= (Calendar) calendar.clone();
        //                int currentYear = mCalendar.get(Calendar.YEAR);
        //                int currentMonth = mCalendar.get(Calendar.MONTH) + 1;
        //                String yearMoth = currentYear + "-" + currentMonth;
        //                ArrayList<Integer> selectDayFlags = hashMapSelectDays.get(yearMoth);
        //                if (selectDayFlags == null) {
        //                    selectDayFlags = new ArrayList<>();
        //                }
        //                mCalendars.remove(mCalendar);
        //                count = count - 1;
        //                selectDayFlags.remove((Object) mCalendar.get(Calendar.DAY_OF_MONTH));
        //                hashMapSelectDays.put(yearMoth, selectDayFlags);
        //                if (yearMoth.endsWith(mYearMoth)) {
        //                    calendarView.setSelectDayMap(hashMapSelectDays);
        //                    calendarView.invalidateTheCurrentMonthView();
        //                }
        //                adapter.notifyDataSetChanged();
        //            }
        //        });
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                Toast.makeText(this,getString(R.string.label_hint_submit_calendar),Toast.LENGTH_SHORT).show();
                return;
            }
            count = count + 1;
            selectDayFlags.add(calendar.get(Calendar.DAY_OF_MONTH));
            mSelectDates.add(getSelectDateStr(mCalendar));
        }
        hashMapSelectDays.put(mYearMoth, selectDayFlags);
        calendarView.setSelectDayMap(hashMapSelectDays);
        calendarView.invalidateTheCurrentMonthView();
        //        adapter.notifyDataSetChanged();
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
        super.onBackPressed();
    }


    @OnClick(R.id.btn_submit_calendar)
    public void onClick() {
        if (mSelectDates != null && mSelectDates.size() > 0){
            submitCalendar();
        }else {
            Toast.makeText(this,getString(R.string.label_hint_submit_empty),Toast.LENGTH_SHORT).show();
        }
    }

    private void submitCalendar() {
        submitSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Intent intent = new Intent();
                        intent.setClass(HotelCalendarActivity.this, AfterScheduleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .build();
        PostScheduleDateBody post = new PostScheduleDateBody();
        post.setScheduleDate(mSelectDates);
        post.setMerchantId(merchantId);
        post.setHallId(hallId>0?hallId:null);
        MerchantApi.submitHotelScheduleObb(post)
                .subscribe(submitSubscriber);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSubscriber);
    }
}