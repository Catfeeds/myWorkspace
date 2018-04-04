package com.hunliji.marrybiz.view.tools;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hunlijicalendar.HLJScheduleCalendarView;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.tools.ToolsApi;
import com.hunliji.marrybiz.db.CalendarDBAdapter;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.model.tools.ItemMonth;
import com.hunliji.marrybiz.model.tools.Schedule;
import com.hunliji.marrybiz.model.wrapper.HljHttpSchedulesData;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.ShSwitchView;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 日程管理界面
 * created by chen_bin 2016/06/25/.
 */
public class ScheduleManageActivity extends HljBaseNoBarActivity implements
        HLJScheduleCalendarView.OnPageChangeListener, HLJScheduleCalendarView
        .OnMonthViewFinishUpdateListener, HLJScheduleCalendarView.OnDateSelectedListener,
        ObservableScrollViewCallbacks, ObjectBindAdapter.ViewBinder<Schedule>,
        HLJScheduleCalendarView.OnDatePickerChangeListener, AdapterView.OnItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.week_title)
    GridView wekTitle;
    @BindView(R.id.list_view)
    ObservableListView listView;
    @BindView(R.id.switch_layout)
    LinearLayout switchLayout;
    @BindView(R.id.switch_view)
    ShSwitchView switchView;
    @BindView(R.id.suspend_layout)
    LinearLayout suspendLayout;
    @BindView(R.id.btn_back_to_today)
    ImageButton btnBackToToday;
    private HLJScheduleCalendarView calendarView;
    private LinearLayout doLayout;
    private TextView tvCanDo;
    private TextView tvCanNotDo;
    private ShSwitchView switchView2;
    private ObjectBindAdapter<Schedule> adapter;
    private LongSparseArray<ArrayList<Schedule>> listSparseArray;
    private LongSparseArray<ArrayList<Integer>> eventDotsSparseArray;
    private LongSparseArray<ArrayList<Integer>> fullDotsSparseArray;
    private ArrayList<Schedule> schedules;
    private CalendarDBAdapter calendarDBAdapter;
    private Dialog progressDialog;
    private Dialog createNewDialog;
    private DecimalFormat decimalFormat;
    private String[] times;
    private String currentYear = "0";
    private String currentMonth = "0";
    private String currentDay = "0";
    private boolean isBackToToday;
    private int currentHeight;
    private int splitHeight;
    private int property;
    private Subscriber initSub;
    private Subscriber switchSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_manage);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        schedules = new ArrayList<>();
        times = new String[]{getString(R.string.label_morning), getString(R.string
                .label_luncheon), getString(
                R.string.label_afternoon), getString(R.string.label_dinner), getString(R.string
                .label_night)};
        listSparseArray = new LongSparseArray<>();
        eventDotsSparseArray = new LongSparseArray<>();
        fullDotsSparseArray = new LongSparseArray<>();
        decimalFormat = new DecimalFormat("00");
        splitHeight = Util.dp2px(this, 2);
        Calendar beginCalendar = new GregorianCalendar(2016, 1, 0, 0, 0, 0);
        Calendar endCalendar = new GregorianCalendar(2025, 12, 0, 0, 0, 0);
        calendarDBAdapter = new CalendarDBAdapter(this);
        listView.setScrollViewCallbacks(this);
        View headerView = View.inflate(this, R.layout.schedule_manage_header, null);
        listView.addHeaderView(headerView);
        calendarView = (HLJScheduleCalendarView) headerView.findViewById(R.id.calendar_view);
        doLayout = (LinearLayout) headerView.findViewById(R.id.do_layout);
        tvCanDo = (TextView) headerView.findViewById(R.id.tv_can_do);
        tvCanNotDo = (TextView) headerView.findViewById(R.id.tv_can_not_do);
        switchView2 = (ShSwitchView) headerView.findViewById(R.id.switch_view);
        switchView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
            }
        });
        listView.addFooterView(View.inflate(this, R.layout.schedule_manage_footer, null));
        listView.setOnItemClickListener(this);
        adapter = new ObjectBindAdapter<>(this, schedules, R.layout.my_schedule_list_item, this);
        listView.setAdapter(adapter);
        wekTitle.setAdapter(new ArrayAdapter<>(this,
                R.layout.schedule_calendar_week_title_item,
                R.id.calendar_caption_date,
                new String[]{"日", "一", "二", "三", "四", "五", "六"}));
        wekTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        calendarView.init(this,
                Calendar.getInstance(),
                beginCalendar,
                endCalendar,
                Calendar.SUNDAY);
        calendarView.invalidateTheCurrentMonthView();
        calendarView.setOnPageChangerListener(this);
        calendarView.setOnMonthViewFinishUpdate(this);
        calendarView.setOnDateSelectedListener(this);
        calendarView.setOnDatePickerChangeListener(this);
        setSwitchView(false);
        onSelect(true, Calendar.getInstance());
    }

    @Override
    public void onSelectedDayChange(Calendar calendar) {
        onSelect(false, calendar);
    }

    @Override
    public void onPageChange(Calendar calendar) {
        Calendar cal;
        if (isBackToToday) {
            isBackToToday = false;
            cal = Calendar.getInstance();
        } else {
            cal = getDayOfMonth(calendar, Integer.valueOf(currentDay));
            calendarView.setSelectDay(cal.get(Calendar.DAY_OF_MONTH));
            calendarView.invalidateTheCurrentMonthView();
            currentDay = decimalFormat.format(cal.get(Calendar.DAY_OF_MONTH));
        }
        onSelect(true, cal);
    }

    private void onSelect(boolean isPageChange, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (year == Integer.valueOf(currentYear) && month == Integer.valueOf(currentMonth) && day
                == Integer.valueOf(
                currentDay)) {
            return;
        }
        addJYInfo(calendar);
        btnBackToToday.setVisibility(DateUtils.isSameDay(Calendar.getInstance(),
                calendar) ? View.GONE : View.VISIBLE);
        currentYear = decimalFormat.format(calendar.get(Calendar.YEAR));
        currentMonth = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        currentDay = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        tvTitle.setText(new DateTime(calendar.getTime()).toString(getString(R.string
                .format_date_type6)));
        if (isPageChange) {
            if (eventDotsSparseArray.indexOfKey(getYearAndMonthKey()) < 0 || fullDotsSparseArray
                    .indexOfKey(
                    getYearAndMonthKey()) < 0) {
                getData(true);
                return;
            }
            calendarView.setEventDots(getEventDots(getYearAndMonthKey()));
            calendarView.setFullDots(getFullDots(getYearAndMonthKey()));
            calendarView.invalidateTheCurrentMonthView();
        }
        if (listSparseArray.indexOfKey(getYearAndMonthAndDayKey()) >= 0) {
            schedules.clear();
            schedules.addAll(getSchedules(getYearAndMonthAndDayKey()));
            adapter.notifyDataSetChanged();
            setSwitchView(getFullDots(getYearAndMonthKey()).contains(Integer.valueOf(currentDay)));
        } else {
            getData(false);
        }
    }

    //添加宜忌
    public void addJYInfo(Calendar calendar) {
        try {
            calendarDBAdapter.open();
            String[] array = calendarDBAdapter.getYJInfo(calendar);
            calendarDBAdapter.close();
            if (!TextUtils.isEmpty(array[0])) {
                String yiStr = array[0].replaceAll("、", "  ");
                tvCanDo.setText(yiStr);
            } else {
                tvCanDo.setText("");
            }
            if (!TextUtils.isEmpty(array[1])) {
                tvCanNotDo.setText(array[1].replaceAll("、", "  "));
            } else {
                tvCanNotDo.setText("");
            }
        } catch (SQLException ignored) {
        }
    }

    //获取数据
    private void getData(final boolean isNeedLoadDouble) {
        if (initSub == null || initSub.isUnsubscribed()) {
            Observable<HljHttpSchedulesData> scheduleObb = ToolsApi.getScheduleListObb(
                    getYearAndMonthAndDay());
            if (isNeedLoadDouble) {
                Observable<HljHttpData<List<ItemMonth>>> itemMonthObb = ToolsApi
                        .getItemMonthListObb(
                        currentYear + "-" + currentMonth + "-01");
                Observable<ResultZip> observable = Observable.zip(scheduleObb,
                        itemMonthObb,
                        new Func2<HljHttpSchedulesData, HljHttpData<List<ItemMonth>>, ResultZip>() {
                            @Override
                            public ResultZip call(
                                    HljHttpSchedulesData schedulesData,
                                    HljHttpData<List<ItemMonth>> itemMonthsData) {
                                ResultZip resultZip = new ResultZip();
                                resultZip.schedulesData = schedulesData;
                                resultZip.itemMonthsData = itemMonthsData;
                                return resultZip;
                            }
                        });
                initSub = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                            @Override
                            public void onNext(ResultZip resultZip) {
                                setData(true, resultZip);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                setData(true, new ResultZip());
                            }
                        })
                        .setDataNullable(true)
                        .setProgressDialog(createProgressDialog())
                        .build();
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(initSub);
            } else {
                initSub = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpSchedulesData>() {
                            @Override
                            public void onNext(HljHttpSchedulesData listHljHttpData) {
                                ResultZip resultZip = new ResultZip();
                                resultZip.schedulesData = listHljHttpData;
                                setData(false, resultZip);
                            }
                        })
                        .setOnErrorListener(new SubscriberOnErrorListener() {
                            @Override
                            public void onError(Object o) {
                                setData(false, new ResultZip());
                            }
                        })
                        .setDataNullable(true)
                        .setProgressDialog(createProgressDialog())
                        .build();
                scheduleObb.subscribe(initSub);
            }
        }
    }

    //设置数据
    private void setData(boolean isNeedLoadDouble, ResultZip resultZip) {
        if (isNeedLoadDouble) {
            ArrayList<Integer> eventDots = getEventDots(getYearAndMonthKey());
            ArrayList<Integer> fullDots = getFullDots(getYearAndMonthKey());
            eventDots.clear();
            fullDots.clear();
            if (resultZip.itemMonthsData != null && resultZip.itemMonthsData.getData() != null) {
                for (ItemMonth itemMonth : resultZip.itemMonthsData.getData()) {
                    int day = Integer.valueOf(itemMonth.getDate()
                            .split("-")[2]);
                    if (!eventDots.contains(day)) {
                        eventDots.add(day);
                    }
                    if (itemMonth.isFullStatus()) {
                        if (!fullDots.contains(day)) {
                            fullDots.add(day);
                        }
                        if (getYearAndMonthAndDay().equals(itemMonth.getDate())) {
                            setSwitchView(true);
                        }
                    }
                }
            }
            eventDotsSparseArray.put(getYearAndMonthKey(), eventDots);
            fullDotsSparseArray.put(getYearAndMonthKey(), fullDots);
            calendarView.setEventDots(eventDots);
            calendarView.setFullDots(fullDots);
            calendarView.invalidateTheCurrentMonthView();
        }
        ArrayList<Schedule> schedules2 = getSchedules(getYearAndMonthAndDayKey());
        schedules.clear();
        schedules2.clear();
        if (resultZip.schedulesData != null) {
            property = resultZip.schedulesData.getProperty();
            suspendLayout.setVisibility(property < 0 ? View.GONE : View.VISIBLE);
            if (resultZip.schedulesData.getData() != null) {
                for (Schedule schedule : resultZip.schedulesData.getData()) {
                    if (TextUtils.isEmpty(schedule.getEntityId()) && schedule.getType() == 1) {
                        continue;
                    }
                    schedule.setDate(getYearAndMonthAndDay());
                    schedules.add(schedule);
                    schedules2.add(schedule);
                }
            }
        }
        setSwitchView(getFullDots(getYearAndMonthKey()).contains(Integer.valueOf(currentDay)));
        adapter.notifyDataSetChanged();
        listSparseArray.put(getYearAndMonthAndDayKey(), schedules2);
    }

    private class ResultZip {
        HljHttpSchedulesData schedulesData;
        HljHttpData<List<ItemMonth>> itemMonthsData;
    }

    class ViewHolder {
        @BindView(R.id.top_line_layout)
        View topLineLayout;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.message_layout)
        LinearLayout messageLayout;
        @BindView(R.id.tv_message)
        TextView tvMessage;
        @BindView(R.id.bottom_line_layout)
        View bottomLineLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void setViewValue(View view, Schedule schedule, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.topLineLayout.setVisibility(View.VISIBLE);
        holder.bottomLineLayout.setVisibility(position == schedules.size() - 1 ? View.VISIBLE :
                View.GONE);
        String userName;
        String phone;
        //类型 ： 0 自定义日程
        if (schedule.getType() == 0) {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_color_ffa73c);
            holder.tvType.setText(R.string.label_schedule);
            userName = schedule.getUserName();
            phone = schedule.getPhone();
        }
        //1 订单
        else if (schedule.getType() == 1) {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_color_d2a1f7);
            holder.tvType.setText(R.string.label_order);
            NewOrder order = schedule.getOrder();
            userName = order.getBuyerRealName();
            phone = order.getBuyerPhone();
        }
        //2 预约
        else {
            holder.tvType.setBackgroundResource(R.drawable.sp_r2_primary);
            holder.tvType.setText(R.string.label_reservation2);
            userName = schedule.getUserName();
            phone = schedule.getPhone();
        }
        //姓名
        if (TextUtils.isEmpty(userName)) {
            holder.tvUserName.setText(R.string.label_unfilled);
            holder.tvUserName.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        } else {
            holder.tvUserName.setText(userName);
            holder.tvUserName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        }
        //电话
        if (TextUtils.isEmpty(phone)) {
            holder.tvPhone.setText(R.string.label_unfilled);
            holder.tvPhone.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        } else {
            holder.tvPhone.setText(phone);
            holder.tvPhone.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        }
        //时间
        String time = null;
        if (schedule.getType() == 2 || property == 0) {
            time = schedule.getTime() == null ? "" : schedule.getTime()
                    .toString(getString(R.string.format_date_type13));
        } else if (!TextUtils.isEmpty(schedule.getTimeStr())) {
            StringBuilder sb = new StringBuilder();
            String[] strings = schedule.getTimeStr()
                    .split(",");
            for (int i = 0, size = strings.length; i < size; i++) {
                if (TextUtils.isEmpty(strings[i])) {
                    continue;
                }
                int index = Integer.valueOf(strings[i]) - 1;
                if (index >= times.length) {
                    continue;
                }
                sb.append(times[index]);
                if (i != size - 1) {
                    sb.append(".");
                }
            }
            time = sb.toString();
        }
        if (TextUtils.isEmpty(time)) {
            holder.tvTime.setText(R.string.label_unfilled);
            holder.tvTime.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        } else {
            holder.tvTime.setText(time);
            holder.tvTime.setTextColor(ContextCompat.getColor(this, R.color.colorBlack2));
        }
        //备注
        if (!TextUtils.isEmpty(schedule.getMessage())) {
            holder.messageLayout.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(schedule.getMessage());
        } else {
            holder.messageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Schedule schedule = (Schedule) parent.getAdapter()
                .getItem(position);
        if (schedule == null || schedule.getId() == 0) {
            return;
        }
        Intent intent = new Intent(this, CreateScheduleActivity.class);
        intent.putExtra("position", schedules.indexOf(schedule));
        intent.putExtra("type", schedule.getType());
        intent.putExtra("property", property);
        intent.putExtra("schedule", schedule);
        startActivityForResult(intent, Constants.RequestCode.SCHEDULE_DETAIL);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.CREATE_SCHEDULE:
                    createSchedule(data);
                    break;
                case Constants.RequestCode.SCHEDULE_DETAIL:
                    String action = data.getStringExtra("action");
                    if ("update".equals(action)) {
                        updateSchedule(data);
                    } else if ("delete".equals(action)) {
                        deleteSchedule(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //创建日程，创建约
    private void createSchedule(Intent data) {
        Schedule schedule = data.getParcelableExtra("schedule");
        if (schedule == null) {
            return;
        }
        String[] dates = schedule.getDate()
                .toString(getString(R.string.format_date_type8))
                .split("-");
        if (dates.length < 3) {
            return;
        }
        long yearAndMonthKey = Long.valueOf(dates[0] + dates[1]);
        long yearAndMonthAndDayKey = Long.valueOf(dates[0] + dates[1] + dates[2]);
        ArrayList<Integer> eventDots = getEventDots(yearAndMonthKey);
        int day = Integer.valueOf(dates[2]);
        if (!eventDots.contains(day)) {
            eventDots.add(day);
            eventDotsSparseArray.put(yearAndMonthKey, eventDots);
            if (getYearAndMonthKey() == yearAndMonthKey) {
                calendarView.setEventDots(eventDots);
                calendarView.invalidateTheCurrentMonthView();
            }
        }
        //新增的就是当前选中的日期
        if (getYearAndMonthAndDayKey() == yearAndMonthAndDayKey) {
            schedules.add(0, schedule);
            ArrayList<Schedule> schedule2 = getSchedules(yearAndMonthAndDayKey);
            schedule2.add(0, schedule);
            listSparseArray.put(yearAndMonthAndDayKey, schedule2);
            adapter.notifyDataSetChanged();
            setSwitchView(switchView.isOn());
        }
        //新增的不是当前选中的日期
        else if (listSparseArray.indexOfKey(yearAndMonthAndDayKey) >= 0) {
            ArrayList<Schedule> schedule2 = getSchedules(yearAndMonthAndDayKey);
            schedule2.add(0, schedule);
            listSparseArray.put(yearAndMonthAndDayKey, schedule2);
        }
    }

    //修改
    private void updateSchedule(Intent data) {
        int position = data.getIntExtra("position", -1);
        if (position == -1 || position >= schedules.size() || schedules.size() == 0) {
            return;
        }
        Schedule schedule = data.getParcelableExtra("schedule");
        if (schedule == null) {
            return;
        }
        //如果修改的时候修改了时间的话，则删除掉之前的。
        if (!schedules.get(position)
                .getDate()
                .isEqual(schedule.getDate())) {
            deleteSchedule(data);
            createSchedule(data);
        } else {
            schedules.set(position, schedule);
            ArrayList<Schedule> scheduleList = getSchedules(getYearAndMonthAndDayKey());
            scheduleList.set(position, schedule);
            listSparseArray.put(getYearAndMonthAndDayKey(), scheduleList);
            adapter.notifyDataSetChanged();
        }
    }

    //删除
    private void deleteSchedule(Intent data) {
        int position = data.getIntExtra("position", -1);
        if (position == -1 || position >= schedules.size() || schedules.size() == 0) {
            return;
        }
        schedules.remove(position);
        ArrayList<Schedule> scheduleList = getSchedules(getYearAndMonthAndDayKey());
        scheduleList.remove(position);
        listSparseArray.put(getYearAndMonthAndDayKey(), scheduleList);
        adapter.notifyDataSetChanged();
        if (schedules.isEmpty()) {
            int day = Integer.valueOf(currentDay);
            ArrayList<Integer> eventDots = getEventDots(getYearAndMonthKey());
            if (eventDots.contains(day)) {
                eventDots.remove((Integer) day);
                eventDotsSparseArray.put(getYearAndMonthKey(), eventDots);
                calendarView.setEventDots(eventDots);
                calendarView.invalidateTheCurrentMonthView();
            }
        }
    }

    @Override
    public void onMonthViewFinishUpdate() {
        currentHeight = calendarView.getCurrentHeight() + doLayout.getMeasuredHeight() +
                splitHeight;
    }

    @Override
    public void onDatePickerChangeListener(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //is same year,month,day
        if (Integer.valueOf(currentYear) == year && Integer.valueOf(currentMonth) == month &&
                Integer.valueOf(
                currentDay) == day) {
            return;
        }
        //is same year,month
        //same page
        if (Integer.valueOf(currentYear) == year && Integer.valueOf(currentMonth) == month) {
            calendarView.setSelectDay(day);
            calendarView.invalidateTheCurrentMonthView();
            onSelectedDayChange(calendar);
            return;
        }
        //date picker change page
        this.currentDay = decimalFormat.format(day);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (scrollY >= currentHeight) {
            wekTitle.setVisibility(View.GONE);
            switchLayout.setVisibility(View.VISIBLE);
        } else {
            wekTitle.setVisibility(View.VISIBLE);
            switchLayout.setVisibility(View.GONE);
        }
    }

    //设置已满未满
    @OnClick(R.id.switch_view)
    public void onSwitch() {
        if (switchSub == null || switchSub.isUnsubscribed()) {
            switchSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            boolean flag = !switchView.isOn();
                            setSwitchView(flag);
                            ArrayList<Integer> fullDots = getFullDots(getYearAndMonthKey());
                            int day = Integer.valueOf(currentDay);
                            //add
                            if (flag && !fullDots.contains(day)) {
                                fullDots.add(day);
                            } else if (fullDots.contains(day)) {
                                fullDots.remove((Integer) day);
                                if (schedules.isEmpty()) {
                                    ArrayList<Integer> eventDots = getEventDots
                                            (getYearAndMonthKey());
                                    eventDots.remove((Integer) day);
                                    eventDotsSparseArray.put(getYearAndMonthKey(), eventDots);
                                }
                            }
                            fullDotsSparseArray.put(getYearAndMonthKey(), fullDots);
                            calendarView.setFullDots(fullDots);
                            calendarView.invalidateTheCurrentMonthView();
                        }
                    })
                    .setProgressDialog(createProgressDialog())
                    .build();
            ToolsApi.changeStatusObb(getYearAndMonthAndDay(), switchView.isOn() ? 0 : 1)
                    .subscribe(switchSub);
        }
    }

    //顶部title项点击
    @OnClick(R.id.select_month_layout)
    public void onSelectMonth() {
        calendarView.showDatePicker(this,
                Integer.valueOf(currentYear),
                Integer.valueOf(currentMonth),
                Integer.valueOf(currentDay));
    }

    //回到今日
    @OnClick(R.id.btn_back_to_today)
    public void onBackToToday() {
        isBackToToday = true;
        calendarView.setBackToToday(Calendar.getInstance());
    }

    //新建
    @OnClick(R.id.btn_create_new)
    public void onCreateNew() {
        if (createNewDialog != null && createNewDialog.isShowing()) {
            return;
        }
        if (createNewDialog == null) {
            createNewDialog = new Dialog(this, R.style.BubbleDialogTheme);
            createNewDialog.setContentView(R.layout.dialog_create_new_schedule);
            createNewDialog.findViewById(R.id.btn_create_schedule)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createNewDialog.dismiss();
                            Intent intent = new Intent(ScheduleManageActivity.this,
                                    CreateScheduleActivity.class);
                            intent.putExtra("type", Schedule.TYPE_SCHEDULE);
                            intent.putExtra("property", property);
                            intent.putExtra("date", getYearAndMonthAndDay());
                            startActivityForResult(intent, Constants.RequestCode.CREATE_SCHEDULE);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
            createNewDialog.findViewById(R.id.btn_create_reservation)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createNewDialog.dismiss();
                            Intent intent = new Intent(ScheduleManageActivity.this,
                                    CreateScheduleActivity.class);
                            intent.putExtra("type", Schedule.TYPE_RESERVATION);
                            intent.putExtra("property", property);
                            intent.putExtra("date", getYearAndMonthAndDay());
                            startActivityForResult(intent, Constants.RequestCode.CREATE_SCHEDULE);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    });
            createNewDialog.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createNewDialog.dismiss();
                        }
                    });
            Point point = JSONUtil.getDeviceSize(this);
            Window win = createNewDialog.getWindow();
            if (win != null) {
                ViewGroup.LayoutParams params = win.getAttributes();
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        createNewDialog.show();
    }

    //查看全部
    @OnClick(R.id.btn_see_all)
    public void onSeeAll() {
        startActivity(new Intent(this, MyScheduleListActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setSwitchView(boolean isFullStatus) {
        switchView.setOn(isFullStatus);
        switchView2.setOn(isFullStatus);
    }

    private Dialog createProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this, R.style.BubbleDialogTheme);
            Window win = progressDialog.getWindow();
            if (win != null) {
                win.setDimAmount(0);
            }
        }
        return progressDialog;
    }

    //get schedules
    private ArrayList<Schedule> getSchedules(long key) {
        ArrayList<Schedule> scheduleList = listSparseArray.get(key);
        return scheduleList == null ? new ArrayList<Schedule>() : scheduleList;
    }

    //get eventDots
    private ArrayList<Integer> getEventDots(long key) {
        ArrayList<Integer> eventDots = eventDotsSparseArray.get(key);
        return eventDots == null ? new ArrayList<Integer>() : eventDots;
    }

    //get fullDots
    private ArrayList<Integer> getFullDots(long key) {
        ArrayList<Integer> fullDots = fullDotsSparseArray.get(key);
        return fullDots == null ? new ArrayList<Integer>() : fullDots;
    }

    //选择最大的日期
    public Calendar getDayOfMonth(Calendar calendar, int currentDay) {
        Calendar cal = Calendar.getInstance();
        int maxDay = calendar.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, currentDay > maxDay ? maxDay : currentDay);
        return cal;
    }

    private String getYearAndMonthAndDay() {
        return currentYear + "-" + currentMonth + "-" + currentDay;
    }

    private Long getYearAndMonthAndDayKey() {
        return Long.valueOf(currentYear + currentMonth + currentDay);
    }

    private Long getYearAndMonthKey() {return Long.valueOf(currentYear + currentMonth);}

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, switchSub);
    }
}