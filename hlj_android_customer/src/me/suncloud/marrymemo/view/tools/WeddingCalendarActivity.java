package me.suncloud.marrymemo.view.tools;

import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardlibrary.utils.Lunar;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hunlijicalendar.HLJCalendarView;
import com.hunliji.hunlijicalendar.LunarCalendar;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.WeddingCalendarItemListAdapter;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingCalendarItemViewHolder;
import me.suncloud.marrymemo.api.tools.ToolsApi;
import me.suncloud.marrymemo.db.CalendarDBAdapter;
import me.suncloud.marrymemo.fragment.tools.WeddingCalendarPosterListFragment;
import me.suncloud.marrymemo.model.tools.CalendarBrandConf;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;
import me.suncloud.marrymemo.model.tools.wrappers.HljWeddingCalendarItemsData;
import me.suncloud.marrymemo.util.Util;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 黄道吉日
 * Created by chen_bin on 2017/12/11 0011.
 */
public class WeddingCalendarActivity extends HljBaseNoBarActivity implements HLJCalendarView
        .OnPageChangeListener, HLJCalendarView.OnDateSelectedListener, HLJCalendarView
        .OnDatePickerChangeListener, WeddingCalendarItemViewHolder.OnCollectListener,
        WeddingCalendarItemViewHolder.OnShareListener {

    @Override
    public String pageTrackTagName() {
        return "黄道吉日";
    }

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.calendar_view)
    HLJCalendarView calendarView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.img_brand_bg)
    ImageView imgBrandBg;

    private PopupWindow filterPopWin;
    private LunarCalendar lunarCalendar;

    private CalendarDBAdapter dbAdapter;
    private WeddingCalendarItemListAdapter adapter;

    private SparseArray<List<String>> luckyDaysArray;
    private ArrayList<String> vacationDays;

    private DateTime currentDate;
    private DateTime pageDate;
    private DateTime statisticEndAt; //截止时间
    private CalendarBrandConf calendarBrandConf;

    private int luckyDayType;

    private HljHttpSubscriber getItemsObb;
    private HljHttpSubscriber getItemObb;
    private HljHttpSubscriber collectSub;
    private HljHttpSubscriber brandConfSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_calendar);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        initLoad();
        getCalendarItems();
    }

    private void initValues() {
        currentDate = new DateTime();
        pageDate = currentDate;
        setTitle(currentDate);
        lunarCalendar = new LunarCalendar(this);
        dbAdapter = new CalendarDBAdapter(this);
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = new GregorianCalendar(2025, 12, 0, 0, 0, 0); // 在2025.12底结束
        calendarView.init(this, startCalendar, startCalendar, endCalendar, Calendar.MONDAY);
        calendarView.setOnPageChangerListener(this);
        calendarView.setOnDateSelectedListener(this);
        calendarView.setOnDatePickerChangeListener(this);
        vacationDays = Util.getOfficialHolidayList(this);
        try {
            dbAdapter.open();
            luckyDaysArray = dbAdapter.getLuckyDays(startCalendar.get(Calendar.YEAR),
                    endCalendar.get(Calendar.YEAR));
            dbAdapter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getCalendarItems();
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        Point point = CommonUtil.getDeviceSize(this);
        int offset = (point.x - CommonUtil.dp2px(this, 330)) / 2;
        recyclerView.setPadding(offset, 0, offset, 0);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WeddingCalendarItemListAdapter(this);
        adapter.setOnCollectListener(this);
        adapter.setOnShareListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initLoad() {
        brandConfSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<CalendarBrandConf>() {
                    @Override
                    public void onNext(CalendarBrandConf conf) {
                        calendarBrandConf = conf;
                        setCalendarBrandInfo();
                    }
                })
                .build();

        ToolsApi.getCalendarBrandConf()
                .subscribe(brandConfSub);
    }

    private void setCalendarBrandInfo() {
        if (calendarBrandConf.getBrandInfoInCalendar()
                .getBackImage()
                .isShow()) {
            calendarView.setSelectedBgPath(calendarBrandConf.getBrandInfoInCalendar()
                    .getBackImage()
                    .getImgPath());
            calendarView.setSelectedTxtColorStr(calendarBrandConf.getBrandInfoInCalendar()
                    .getBackImage()
                    .getNumFontColor());
            calendarView.setSelectedLunarTxtColorStr(calendarBrandConf.getBrandInfoInCalendar()
                    .getBackImage()
                    .getLunarFontColor());
        } else {
            calendarView.setSelectedBgPath(null);
            calendarView.setSelectedTxtColorStr(null);
            calendarView.setSelectedLunarTxtColorStr(null);
        }

        if (calendarBrandConf.getBrandInfoBelowCalendar()
                .getFontBackImage()
                .isShow()) {
            adapter.setBrandTagImagePath(calendarBrandConf.getBrandInfoBelowCalendar()
                    .getFontBackImage()
                    .getImgPath());
            adapter.notifyDataSetChanged();
        } else {
            adapter.setBrandTagImagePath(null);
        }

        if (calendarBrandConf.getBrandInfoBelowCalendar()
                .getBackImage()
                .isShow()) {
            Glide.with(this)
                    .load(calendarBrandConf.getBrandInfoBelowCalendar()
                            .getBackImage()
                            .getImgPath())
                    .into(imgBrandBg);
        } else {
            Glide.with(this)
                    .clear(imgBrandBg);
        }

        calendarView.invalidateTheCurrentMonthView();
    }

    private void getCalendarItems() {
        if (getItemsObb == null || getItemsObb.isUnsubscribed()) {
            getItemsObb = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljWeddingCalendarItemsData>() {
                        @Override
                        public void onNext(HljWeddingCalendarItemsData calendarItemsData) {
                            List<WeddingCalendarItem> calendarItems = null;
                            if (calendarItemsData != null) {
                                statisticEndAt = calendarItemsData.getStatisticEndAt();
                                calendarItems = calendarItemsData.getData();
                            }
                            if (statisticEndAt == null) {
                                statisticEndAt = new DateTime().plusYears(2);
                                statisticEndAt = statisticEndAt.withMonthOfYear(12)
                                        .withDayOfMonth(31)
                                        .withHourOfDay(23)
                                        .withMinuteOfHour(59)
                                        .withSecondOfMinute(59);
                            }
                            adapter.setStatisticEndAt(statisticEndAt);
                            calendarView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (calendarView != null) {
                                        setVacationDays(currentDate);
                                        setLuckyDays(currentDate, luckyDayType);
                                    }
                                }
                            });
                            sortCalendarItems(calendarItems, currentDate);
                        }
                    })
                    .setDataNullable(true)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setProgressBar(progressBar)
                    .build();
            ToolsApi.getCalendarItemsObb()
                    .map(new Func1<HljWeddingCalendarItemsData, HljWeddingCalendarItemsData>() {

                        @Override
                        public HljWeddingCalendarItemsData call(
                                HljWeddingCalendarItemsData calendarItemsData) {
                            if (calendarItemsData != null && !calendarItemsData.isEmpty()) {
                                List<WeddingCalendarItem> calendarItems = new ArrayList<>();
                                calendarItems.addAll(calendarItemsData.getData());
                                try {
                                    dbAdapter.open();
                                    for (WeddingCalendarItem calendarItem : calendarItems) {
                                        addCalendarItemParams(calendarItem);
                                    }
                                    dbAdapter.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                calendarItemsData.setData(calendarItems);
                            }
                            return calendarItemsData;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getItemsObb);
        }
    }

    private void getCalendarItemByDate(final DateTime date) {
        if (date.isAfter(statisticEndAt)) {
            addCalendarItem(date, new WeddingCalendarItem());
            return;
        }
        CommonUtil.unSubscribeSubs(getItemObb);
        getItemObb = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<WeddingCalendarItem>() {
                    @Override
                    public void onNext(WeddingCalendarItem item) {
                        addCalendarItem(date, item);
                    }
                })
                .build();
        ToolsApi.getCalendarItemByDateObb(date.toString(HljTimeUtils.DATE_FORMAT_SHORT))
                .subscribe(getItemObb);
    }

    private void addCalendarItem(DateTime date, WeddingCalendarItem item) {
        List<WeddingCalendarItem> calendarItems = adapter.getCalendarItems();
        for (int i = 0, size = calendarItems.size(); i < size; i++) {
            WeddingCalendarItem calendarItem = calendarItems.get(i);
            if (HljTimeUtils.isSameDay(date, calendarItem.getDate())) {
                calendarItem.setHot(item.getHot());
                calendarItem.setCount(item.getCount());
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.title_layout)
    void onSelectDate() {
        if (getItemsObb != null && !getItemsObb.isUnsubscribed()) {
            return;
        }
        calendarView.showDatePicker(this, currentDate);
    }

    @OnClick(R.id.tv_filter)
    void onFilter() {
        if (getItemsObb != null && !getItemsObb.isUnsubscribed()) {
            return;
        }
        if (filterPopWin != null && filterPopWin.isShowing()) {
            filterPopWin.dismiss();
            return;
        }
        if (filterPopWin == null) {
            View view = View.inflate(this, R.layout.dialog_filter_wedding_calendar, null);
            final CheckableLinearGroup cgGroup = view.findViewById(R.id.cg_group);
            cgGroup.setOnCheckedChangeListener(new CheckableLinearGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                    filterPopWin.dismiss();
                    cgGroup.check(checkedId);
                    switch (checkedId) {
                        case R.id.cb_all:
                            setLuckyDays(pageDate, WeddingCalendarItem.TYPE_ALL);
                            break;
                        case R.id.cb_marriage:
                            setLuckyDays(pageDate, WeddingCalendarItem.TYPE_MARRIAGE);
                            break;
                        case R.id.cb_engagement:
                            setLuckyDays(pageDate, WeddingCalendarItem.TYPE_ENGAGEMENT);
                            break;
                        case R.id.cb_betrothal_gift:
                            setLuckyDays(pageDate, WeddingCalendarItem.TYPE_BETROTHAL_GIFT);
                            break;
                        case R.id.cb_uxorilocal_marriage:
                            setLuckyDays(pageDate, WeddingCalendarItem.TYPE_UXORILOCAL_MARRIAGE);
                            break;
                    }
                }
            });
            filterPopWin = new PopupWindow(this);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            filterPopWin = new PopupWindow(view,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight(),
                    true);
            filterPopWin.setContentView(view);
            filterPopWin.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                    android.R.color.transparent)));
            filterPopWin.setOutsideTouchable(true);
        }
        filterPopWin.showAsDropDown(tvFilter,
                -filterPopWin.getContentView()
                        .getMeasuredWidth() + tvFilter.getMeasuredWidth() - CommonUtil.dp2px(this,
                        12),
                -CommonUtil.dp2px(this, 8));
    }

    @Override
    public void onPageChange(Calendar calendar) {
        DateTime date = new DateTime(calendar);
        this.pageDate = date;
        setTitle(date);
        setVacationDays(date);
        setLuckyDays(date, luckyDayType);
    }

    @Override
    public void onSelectedDayChange(Calendar calendar) {
        DateTime date = new DateTime(calendar);
        if (HljTimeUtils.isSameDay(date, currentDate)) {
            return;
        }
        this.currentDate = date;
        sortCalendarItems(adapter.getCalendarItems(), date);
    }

    @Override
    public void onDatePickerChangeListener(Calendar calendar) {
        final DateTime date = new DateTime(calendar);
        if (HljTimeUtils.isSameDay(date, currentDate)) {
            return;
        }
        this.currentDate = date;
        sortCalendarItems(adapter.getCalendarItems(), date);
        calendarView.post(new Runnable() {
            @Override
            public void run() {
                if (calendarView != null) {
                    calendarView.setSelectedDay(date.getDayOfMonth());
                    calendarView.invalidateTheCurrentMonthView();
                }
            }
        });
    }

    @Override
    public void onCollect(final int position, final WeddingCalendarItem calendarItem) {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (calendarItem == null || calendarItem.getDate() == null) {
            return;
        }
        CommonUtil.unSubscribeSubs(collectSub);
        collectSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<Object>() {
                    @Override
                    public void onNext(Object object) {
                        ToastUtil.showCustomToast(WeddingCalendarActivity.this,
                                calendarItem.getId() == 0 ? R.string.msg_success_to_collect___cm
                                        : R.string.msg_success_to_un_collect___cm);
                        if (calendarItem.getId() > 0 && !HljTimeUtils.isSameDay(currentDate,
                                calendarItem.getDate())) {
                            adapter.getCalendarItems()
                                    .remove(position);
                            adapter.notifyItemRemoved(position);
                            return;
                        }
                        long id = 0;
                        if (object instanceof WeddingCalendarItem) {
                            WeddingCalendarItem item = (WeddingCalendarItem) object;
                            id = item.getId();
                        }
                        calendarItem.setId(calendarItem.getId() == 0 ? id : 0);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        if (calendarItem.getId() > 0) {
            ToolsApi.unCollectCalendarItemObb(calendarItem.getId())
                    .subscribe(collectSub);
        } else {
            ToolsApi.collectCalendarItemObb(calendarItem.getDate()
                    .toString(HljTimeUtils.DATE_FORMAT_SHORT))
                    .subscribe(collectSub);
        }
    }

    @Override
    public void onShare(int position, WeddingCalendarItem calendarItem) {
        WeddingCalendarPosterListFragment fragment = WeddingCalendarPosterListFragment.newInstance(
                new ArrayList<>(adapter.getCalendarItems()),
                calendarItem,
                statisticEndAt);
        fragment.show(getSupportFragmentManager(), "WeddingCalendarPosterListFragment");
    }

    private void setLuckyDays(DateTime date, int type) {
        this.luckyDayType = type;
        List<String> days = null;
        if (luckyDaysArray != null) {
            List<String> luckyDays = luckyDaysArray.get(type);
            if (!CommonUtil.isCollectionEmpty(luckyDays)) {
                days = new ArrayList<>();
                for (String str : luckyDays) {
                    if (str.startsWith(date.getYear() + "-" + date.getMonthOfYear() + "-")) {
                        days.add(str);
                    }
                }
            }
        }
        calendarView.setAuspicious(HljTimeUtils.convertDateToDays(days));
        calendarView.invalidateTheCurrentMonthView();
    }

    private void setVacationDays(DateTime date) {
        List<String> days = null;
        if (!CommonUtil.isCollectionEmpty(vacationDays)) {
            days = new ArrayList<>();
            for (String str : vacationDays) {
                if (str.startsWith(date.getYear() + "-" + date.getMonthOfYear() + "-")) {
                    days.add(str);
                }
            }
        }
        calendarView.setEventDots(HljTimeUtils.convertDateToDays(days));
    }

    private void sortCalendarItems(List<WeddingCalendarItem> calendarItems, DateTime date) {
        recyclerView.scrollToPosition(0);
        boolean isReset = true;
        WeddingCalendarItem tempCalendarItem = null;
        if (!CommonUtil.isCollectionEmpty(calendarItems)) {
            if (calendarItems.get(0)
                    .getId() == 0) {
                calendarItems.remove(0);
            }
            for (int i = 0, size = calendarItems.size(); i < size; i++) {
                WeddingCalendarItem calendarItem = calendarItems.get(i);
                if (calendarItem.getDate() == null) {
                    continue;
                }
                if (HljTimeUtils.isSameDay(date, calendarItem.getDate())) { //此日期已存在收藏列表中
                    tempCalendarItem = calendarItem;
                    isReset = i != 0;
                    if (isReset) { //如果是第一项就出现的话则不需要做移位处理
                        calendarItems.remove(i);
                    }
                    break;
                }
            }
            Collections.sort(calendarItems, new Comparator<WeddingCalendarItem>() {
                @Override
                public int compare(WeddingCalendarItem i1, WeddingCalendarItem i2) {
                    DateTime date = i1.getDate();
                    DateTime date2 = i2.getDate();
                    if (date == null || date2 == null) {
                        return 0;
                    }
                    return date.compareTo(date2);
                }
            });
        }
        if (tempCalendarItem == null) {
            tempCalendarItem = new WeddingCalendarItem();
            tempCalendarItem.setDate(date);
            try {
                dbAdapter.open();
                addCalendarItemParams(tempCalendarItem);
                dbAdapter.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (isReset) {
            calendarItems.add(0, tempCalendarItem);
        }
        adapter.setCalendarItems(calendarItems);
        getCalendarItemByDate(date);
    }

    /**
     * 给列表添加额外数据，包括农历，星期几等。
     *
     * @param calendarItem
     */
    private void addCalendarItemParams(WeddingCalendarItem calendarItem) {
        if (calendarItem == null || calendarItem.getDate() == null) {
            return;
        }
        Lunar lunar = new Lunar(calendarItem.getDate());
        calendarItem.setLunar(lunar.getLunar());
        calendarItem.setWeek(lunar.getWeek());
        calendarItem.setSolarTerm(getSolarTerm(calendarItem.getDate()));
        try {
            String[] array = dbAdapter.getYJInfo(calendarItem.getDate());
            String yi = array[0];
            String ji = array[1];
            calendarItem.setMarriageStatus(getYJStatus(WeddingCalendarItem.KEY_MARRIAGE, yi, ji));
            calendarItem.setEngagementStatus(getYJStatus(WeddingCalendarItem.KEY_ENGAGEMENT,
                    yi,
                    ji));
            calendarItem.setBetrothalGiftStatus(getYJStatus(WeddingCalendarItem.KEY_BETROTHAL_GIFT,
                    yi,
                    ji));
            calendarItem.setUxorilocalMarriageStatus(getYJStatus(WeddingCalendarItem
                            .KEY_UXORILOCAL_MARRIAGE,
                    yi,
                    ji));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * date跟春风，秋风，夏至，冬至作比较，获取当前日期所在的节气范围
     *
     * @param date
     * @return
     */
    private int getSolarTerm(DateTime date) {
        Map<String, DateTime> map = lunarCalendar.getLunarSolarTermDates(date.getYear());
        DateTime springEquinoxDate = map.get(getString(R.string.terms5));
        DateTime summerSolsticeDate = map.get(getString(R.string.terms11));
        DateTime autumnEquinoxDate = map.get(getString(R.string.terms17));
        DateTime winterSolsticeDate = map.get(getString(R.string.terms23));
        if (springEquinoxDate != null && date.isBefore(springEquinoxDate)) {
            return WeddingCalendarItem.SOLAR_TERM_WINTER_SOLSTICE;
        } else if (summerSolsticeDate != null && date.isBefore(summerSolsticeDate)) {
            return WeddingCalendarItem.SOLAR_TERM_SPRING_EQUINOX;
        } else if (autumnEquinoxDate != null && date.isBefore(autumnEquinoxDate)) {
            return WeddingCalendarItem.SOLAR_TERM_SUMMER_SOLSTICE;
        } else if (winterSolsticeDate != null & date.isBefore(winterSolsticeDate)) {
            return WeddingCalendarItem.SOLAR_TERM_AUTUMN_EQUINOX;
        }
        return WeddingCalendarItem.SOLAR_TERM_WINTER_SOLSTICE;
    }

    /**
     * 通过key获取宜，忌
     *
     * @param key
     * @param yi
     * @param ji
     * @return
     */
    private int getYJStatus(String key, String yi, String ji) {
        if (!TextUtils.isEmpty(yi) && yi.contains(key)) {
            return WeddingCalendarItem.STATUS_YI;
        }
        if (!TextUtils.isEmpty(ji) && ji.contains(key)) {
            return WeddingCalendarItem.STATUS_JI;
        }
        return WeddingCalendarItem.STATUS_PING;
    }

    private void setTitle(DateTime dateTime) {
        tvTitle.setText(dateTime.toString(getString(R.string.format_date_type16)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (filterPopWin != null && filterPopWin.isShowing()) {
            filterPopWin.dismiss();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(getItemsObb, getItemObb, collectSub, brandConfSub);
    }
}
