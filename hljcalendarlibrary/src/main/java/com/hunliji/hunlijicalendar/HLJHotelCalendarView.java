package com.hunliji.hunlijicalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mo_yu on 16/11/26.档期查询
 */
public class HLJHotelCalendarView extends FrameLayout {
    private static final String TAG = HLJHotelCalendarView.class.getSimpleName();

    // default settings
    private static final int DEFAULT_DATE_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_LUNAR_TEXT_COLOR = Color.GRAY;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_SELECTED_COLOR = Color.RED;
    private static final int DEFAULT_DOT_COLOR = Color.RED;
    private static final int DEFAULT_SHADOW_FLAG_COLOR = Color.GRAY;
    private static final int DEFAULT_PRESSED_COLOR = Color.GRAY;
    private static final int DEFAULT_VACATION_BADGE_COLOR = Color.GREEN;
    private static final boolean DEFAULT_SELECTED_STYLE = false;//选中默认是空心圆
    SimpleDateFormat dateFormat;
    private int mDateTextSize;
    private int mLunarDateTextSize;
    private int mLunarDateTextMarginTop;
    private int mDotMarginTop;
    private int mBackgroundLineWidth;
    private int mShadowFlagColor;
    private int mBoardMargin;
    private int mCalendarPaddingLeft;
    private int mCalendarPaddingRight;
    private int mCalendarPaddingTop;
    private int mCalendarPaddingBottom;
    private int mAuspiciousBadgeColor;
    private int mAuspiciousBadgeSize;
    private int mCollectBadgeSize;
    private int mPassedDayColor;
    private Bitmap mCollectBadgeBitMap;
    private Bitmap mCollectBadgeBitMap2;
    private boolean mIsExpandable;
    private ColorStateList mDateTextColor;
    private ColorStateList mLunarDateTextColor;
    private int mPressedColor;
    private int mFlagDotColor;
    private int mFlagDotSize;
    private int mBackgroundColor;
    private int mTitleBackgroundColor;
    private int mSelectedBackgroundColor;
    private int mDaysPerWeek = 7;
    private boolean mSelectStyle;
    private Calendar beginCalendar;
    private Calendar endCalendar;
    private Calendar initialMonth;
    private Calendar tempEndCalendar;
    private Calendar nowCalendar;
    private ViewPager mViewPager;
    private MonthAdapter mAdapter;
    private TextView mTitle;
    private View titleLayout;
    private GridView mWeekTitle;
    private SimpleDateFormat dateTitleFormat;
    private Calendar mTempDate; // 可暂用的 calendar 实例, 以免到处新建无必要的实例
    private String todayStr = "今天";

    /**
     * 农历
     */
    private LunarCalendar lunarCalendar;

    /**
     * 是否显示农历, 默认显示
     */
    private boolean mShowLunarDay = true;
    private ImageView mLeftSwitch;
    private ImageView mRightSwitch;

    /**
     * 当前显示的月份 view 实例
     */
    private MonthView mCurrentMonthView;
    private Calendar mCurrentCalendar;

    private HashMap<String, ArrayList<Integer>> dotDaysMap;
    private HashMap<String, ArrayList<Integer>> closedDaysMap;
    private HashMap<String, ArrayList<Integer>> auspiciousDaysMap;
    private HashMap<String, ArrayList<Integer>> collectDaysMap;
    // 选中的日期
    private HashMap<String, ArrayList<Integer>> mSelectDayMap;
    private OnPageChangeListener mOnPageChangerListener;
    private OnDateSelectedListener mOnDateSelectedListener;
    private OnMonthViewFinishUpdateListener mOnMonthViewFinishUpdateListener;

    public HLJHotelCalendarView(Context context) {
        this(context, null);
    }

    public HLJHotelCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLJHotelCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

        // Setting the style values of this calendar
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HLJCalendarView,
                defStyleAttr,
                0);
        mDateTextColor = typedArray.getColorStateList(R.styleable.HLJCalendarView_date_text_color);
        if (mDateTextColor == null) {
            mDateTextColor = ColorStateList.valueOf(DEFAULT_DATE_TEXT_COLOR);
        }
        mLunarDateTextColor = typedArray.getColorStateList(R.styleable
                .HLJCalendarView_date_lunar_text_color);
        if (mLunarDateTextColor == null) {
            mLunarDateTextColor = ColorStateList.valueOf(DEFAULT_LUNAR_TEXT_COLOR);
        }

        mSelectStyle = typedArray.getBoolean(R.styleable.HLJCalendarView_selected_style,
                DEFAULT_SELECTED_STYLE);
        mBackgroundColor = typedArray.getColor(R.styleable.HLJCalendarView_background_color,
                DEFAULT_BACKGROUND_COLOR);
        mSelectedBackgroundColor = typedArray.getColor(R.styleable
                        .HLJCalendarView_selected_background_color,
                DEFAULT_SELECTED_COLOR);
        mTitleBackgroundColor = typedArray.getColor(R.styleable
                        .HLJCalendarView_title_background_color,
                context.getResources()
                        .getColor(R.color.colorPrimary));
        mFlagDotColor = typedArray.getColor(R.styleable.HLJCalendarView_dot_color,
                DEFAULT_DOT_COLOR);
        mShadowFlagColor = typedArray.getColor(R.styleable.HLJCalendarView_shadow_flag_color,
                DEFAULT_SHADOW_FLAG_COLOR);
        mPressedColor = typedArray.getColor(R.styleable.HLJCalendarView_pressed_color,
                DEFAULT_PRESSED_COLOR);
        mAuspiciousBadgeColor = typedArray.getColor(R.styleable
                        .HLJCalendarView_auspicious_badge_color,
                DEFAULT_VACATION_BADGE_COLOR);

        mDateTextSize = typedArray.getDimensionPixelSize(R.styleable.HLJCalendarView_date_text_size,
                -1);
        mLunarDateTextSize = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_date_lunar_text_size,
                -1);
        mFlagDotSize = typedArray.getDimensionPixelSize(R.styleable.HLJCalendarView_dot_size, -1);
        mLunarDateTextMarginTop = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_date_lunar_text_margin_top,
                -1);
        mBackgroundLineWidth = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_selected_background_line_size,
                -1);
        mDotMarginTop = typedArray.getDimensionPixelSize(R.styleable.HLJCalendarView_dot_margin_top,
                -1);
        mBoardMargin = typedArray.getDimensionPixelSize(R.styleable.HLJCalendarView_board_margin,
                -1);
        mCalendarPaddingLeft = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_calendar_padding_left,
                0);
        mCalendarPaddingRight = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_calendar_padding_right,
                0);
        mCalendarPaddingTop = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_calendar_padding_top,
                0);
        mCalendarPaddingBottom = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_calendar_padding_bottom,
                0);
        mIsExpandable = typedArray.getBoolean(R.styleable.HLJCalendarView_is_expandable_height,
                true);
        mAuspiciousBadgeSize = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_auspicious_badge_size,
                context.getResources()
                        .getDimensionPixelSize(R.dimen.default_vacation_badge_size));
        mCollectBadgeSize = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_collect_badge_size,
                context.getResources()
                        .getDimensionPixelSize(R.dimen.default_vacation_badge_size));
        if (mBoardMargin < 0) {
            mBoardMargin = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_dot_margin_top);
        }
        if (mDotMarginTop < 0) {
            mDotMarginTop = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_dot_margin_top);
        }
        if (mBackgroundLineWidth < 0) {
            mBackgroundLineWidth = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_selected_line_size);
        }
        if (mLunarDateTextMarginTop < 0) {
            mLunarDateTextMarginTop = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_lunar_date_text_margin_top);
        }
        if (mDateTextSize < 0) {
            mDateTextSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_date_text_size);
        }
        if (mLunarDateTextSize < 0) {
            mLunarDateTextSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_lunar_date_text_size);
        }
        if (mFlagDotSize < 0) {
            mFlagDotSize = context.getResources()
                    .getDimensionPixelSize(R.dimen.default_dot_size);
        }

        Drawable drawable = context.getResources()
                .getDrawable(R.drawable.fav_red);
        mCollectBadgeBitMap = ((BitmapDrawable) drawable).getBitmap();
        drawable = context.getResources()
                .getDrawable(R.drawable.fav_white);
        mCollectBadgeBitMap2 = ((BitmapDrawable) drawable).getBitmap();
        mPassedDayColor = context.getResources()
                .getColor(R.color.colorGray2);
    }

    public HLJHotelCalendarView(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化日历
     *
     * @param context        调用位置的 context
     * @param currentMonth   初始化显示的当前月份日历
     * @param leftMonth      最左边最大可选的日期
     * @param rightMonth     最右最大的可选的日期
     * @param firstDayOfWeek 一周开始于哪一天, 周日还是周一
     */
    public void init(
            final Context context,
            Calendar currentMonth,
            Calendar leftMonth,
            Calendar rightMonth,
            int firstDayOfWeek) {
        nowCalendar = Calendar.getInstance();
        this.initialMonth = (Calendar) currentMonth.clone();
        this.beginCalendar = (Calendar) leftMonth.clone();
        this.endCalendar = (Calendar) rightMonth.clone();

        // 设置星期开始第一天
        beginCalendar.setFirstDayOfWeek(firstDayOfWeek);
        endCalendar.setFirstDayOfWeek(firstDayOfWeek);
        initialMonth.setFirstDayOfWeek(firstDayOfWeek);
        mTempDate = Calendar.getInstance();
        lunarCalendar = new LunarCalendar(context);
        tempEndCalendar = (Calendar) endCalendar.clone();
        tempEndCalendar.add(Calendar.DATE, 1);

        dateTitleFormat = new SimpleDateFormat("yyyy年MM月");
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        Log.d(TAG, "Begin from month: " + dateFormat.format(beginCalendar.getTime()));
        Log.d(TAG, "End at month: " + dateFormat.format(endCalendar.getTime()));
        Log.d(TAG, "Now setting day: " + dateFormat.format(initialMonth.getTime()));

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.framelayout_hlj_hotel_calendar, this, true);
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.titleLayout = findViewById(R.id.calendar_title_layout);
        titleLayout.setBackgroundColor(mTitleBackgroundColor);
        TextView title1 = (TextView) titleLayout.findViewById(R.id.dateTitle);
        TextView title2 = (TextView) titleLayout.findViewById(R.id.dateTitle2);
        if (!mSelectStyle) {
            title1.setVisibility(View.GONE);
            title2.setVisibility(View.VISIBLE);
            this.mTitle = title2;
            this.mTitle.setBackgroundResource(0);
        } else {
            this.mTitle = title1;
            // 如果自定义了 title 的背景颜色, 就去掉日期标题的固定颜色,与背景统一
            if (mTitleBackgroundColor != context.getResources()
                    .getColor(R.color.colorPrimary)) {
                this.mTitle.setBackgroundResource(0);
            }
            this.mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker(context);
                }
            });
        }


        this.mWeekTitle = (GridView) findViewById(R.id.week_title);
        mLeftSwitch = (ImageView) findViewById(R.id.leftButton);
        mRightSwitch = (ImageView) findViewById(R.id.rightButton);

        setWillNotDraw(false);
        mViewPager.setWillNotDraw(false);

        setUpAdapter(context);
        setUpWeekTitle(context, firstDayOfWeek);
        setUpClickListener();
    }

    private void setUpAdapter(final Context context) {
        if (mAdapter == null) {
            mAdapter = new MonthAdapter(context);
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    if (mOnDateSelectedListener != null) {
                        Calendar selectedDay = mAdapter.getSelectedDate();
                        Log.d(TAG, "Selected month:" + dateFormat.format(selectedDay.getTime()));
                        mOnDateSelectedListener.onSelectedDayChange(selectedDay);
                    }
                }
            });
        }

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mTitle.setText(mAdapter.getPageTitle(i));
                mCurrentMonthView = mAdapter.getItemAtPosition(i);
                resizeViewWithAnimation();
                if (mOnPageChangerListener != null) {
                    Calendar calendar = (Calendar) beginCalendar.clone();
                    calendar.add(Calendar.MONTH, i);
                    Log.d(TAG, "DateSelector:" + dateFormat.format(calendar.getTime()));
                    mOnPageChangerListener.onPageChange(calendar);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        int currentPosition = 0;
        if (initialMonth.after(beginCalendar) && initialMonth.before(endCalendar)) {
            int a = initialMonth.get(Calendar.YEAR) - beginCalendar.get(Calendar.YEAR);
            int b = initialMonth.get(Calendar.MONTH);
            int c = beginCalendar.get(Calendar.MONTH);
            int d = b - c;
            currentPosition = a * 12 + d;
        }
        mViewPager.setEnabled(true);
        mViewPager.setCurrentItem(currentPosition);
        mAdapter.setSelectedMonth(currentPosition);
        mTitle.setText(mAdapter.getPageTitle(currentPosition));
    }

    /**
     * 设置日历头部的星期标记
     *
     * @param context
     */
    private void setUpWeekTitle(Context context, int firstDayOfWeek) {
        String[] titles = new String[mDaysPerWeek];
        SimpleDateFormat weekDateFormat = new SimpleDateFormat("EE");

        Calendar weekDay = (Calendar) initialMonth.clone();

        if (weekDay.getFirstDayOfWeek() == Calendar.MONDAY) {
            Log.d(TAG, "Monday is the first day of week");
            weekDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else {
            Log.d(TAG, "Sunday is the first day of week");
            weekDay.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        for (int i = 0; i < mDaysPerWeek; i++) {
            String title = weekDateFormat.format(weekDay.getTime());
            if (Locale.getDefault()
                    .toString()
                    .contains(Locale.SIMPLIFIED_CHINESE.toString())) {
                // 如果系统设置为中文, 则去掉星期表示的"周"字
                if (title.length() > 2) {
                    title = title.substring(2, 3);
                } else {
                    title = title.substring(1, 2);
                }
            }
            titles[i] = title;
            weekDay.add(Calendar.DATE, 1);
        }

        mWeekTitle.setPadding(mCalendarPaddingLeft, 0, mCalendarPaddingRight, 0);
        WeekTitleAdapter adapter = new WeekTitleAdapter(context,
                titles,
                firstDayOfWeek,
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorBlack2));
        mWeekTitle.setAdapter(adapter);
    }

    private void setUpClickListener() {
        mLeftSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 左边按钮, 切换为上一个月
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            }
        });

        mRightSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 右边按钮, 切换为下一个月
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        });
    }

    public void setShowLunarDay(boolean mShowLunarDay) {
        this.mShowLunarDay = mShowLunarDay;
    }

    private int getMonthsSinceBeginDate(Calendar date) {
        if (date.before(beginCalendar) || date.after(tempEndCalendar)) {
            return -1;
        }

        return (date.get(Calendar.YEAR) - beginCalendar.get(Calendar.YEAR)) * 12 + (date.get(
                Calendar.MONTH) - beginCalendar.get(Calendar.MONTH));
    }

    /**
     * 设置当前月份的阴影标识
     *
     * @param flags
     */
    public void setShadowFlags(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setShadowFlags(flags);
        }
    }

    /**
     * 设置当前月份的事件标识
     *
     * @param flags
     */
    public void setEventDots(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setEventDotFlags(flags);
        }
    }

    /**
     * 设置当前月份
     *
     * @param calendar
     */
    public void setCurrentCalendar(Calendar calendar) {
        this.mCurrentCalendar = calendar;
    }

    public Calendar getCurrentCalendar() {
        if (mCurrentCalendar == null) {
            mCurrentCalendar = (Calendar) beginCalendar.clone();
        }
        return mCurrentCalendar;
    }


    /**
     * 设置当前月份的吉日标识
     *
     * @param flags
     */
    public void setAuspicious(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setAuspiciousFlags(flags);
        }
    }

    /**
     * 设置当前月份的收藏标识
     *
     * @param flags
     */
    public void setCollects(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setCollectFlags(flags);
        }
    }

    /**
     * 设置所有的 小点标记 的日期
     *
     * @param map 键值类型为:<"2014-12", <"1", "2">> 键为年份拼接月份的字符串, 值为:这个月份需要设置 shadow 背景的日期,
     *            是 Integer 类型的 ArrayList
     */
    public void setDotDaysMap(HashMap<String, ArrayList<Integer>> map) {
        this.dotDaysMap = map;
    }

    /**
     * 设置所有被选中的日期
     *
     * @param map 键值类型为:<"2014-12", <"1", "2">> 键为年份拼接月份的字符串, 值为:这个月份需要设置 选中 背景的日期,
     *            是 Integer 类型的 ArrayList
     */
    public void setSelectDayMap(HashMap<String, ArrayList<Integer>> map) {
        this.mSelectDayMap = map;
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setSelectDayFlags(mSelectDayMap);
        }
    }

    /**
     * 设置所有的 shadow 的日期
     *
     * @param map 键值类型为:<"2014-12", <"1", "2">> 键为年份拼接月份的字符串, 值为:这个月份需要设置 shadow 背景的日期,
     *            是 Integer 类型的 ArrayList
     */
    public void setClosedDaysMap(HashMap<String, ArrayList<Integer>> map) {
        this.closedDaysMap = map;
    }

    /**
     * 设置所有的吉日的日期
     *
     * @param map 键值类型为:<"2014-12", <"1", "2">> 键为年份拼接月份的字符串, 值为:这个月份需要设置假期标记的日期,
     *            是 Integer 类型的 ArrayList
     */
    public void setAuspiciousDaysMap(HashMap<String, ArrayList<Integer>> map) {
        this.auspiciousDaysMap = map;
    }

    /**
     * 设置所有的星星的日期
     *
     * @param map 键值类型为:<"2014-12", <"1", "2">> 键为年份拼接月份的字符串, 值为:这个月份需要设置假期标记的日期,
     *            是 Integer 类型的 ArrayList
     */
    public void setCollectDaysMap(HashMap<String, ArrayList<Integer>> map) {
        this.collectDaysMap = map;
    }

    /**
     * 刷新当前月份的日历视图
     */
    public void invalidateTheCurrentMonthView() {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.invalidate();
        }
    }

    /**
     * 设置收藏的图标, 如果不设置则使用默认的图标
     *
     * @param bitMap  无背景颜色下使用
     * @param bitmap2 有背景颜色下使用
     */
    public void setCollectBadgeBitMap(Bitmap bitMap, Bitmap bitmap2) {
        this.mCollectBadgeBitMap = bitMap;
        this.mCollectBadgeBitMap2 = bitmap2;
    }

    /**
     * 传入获取的当前 calendar 实例,将当前页面置为当前月,将当前日期选中
     *
     * @param calendar
     */
    public void setBackToToday(Calendar calendar) {
        int position = getMonthsSinceBeginDate(calendar);
        if (position != mViewPager.getCurrentItem() || mAdapter.getSelectedDate()
                .get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)) {
            mViewPager.setCurrentItem(position);
            mAdapter.setSelectedMonth(position);
            mAdapter.setSelectedDay(calendar);
            if (mCurrentMonthView != null) {
                mCurrentMonthView.setSelectedDay();
                mCurrentMonthView.invalidate();
            }
        }
    }

    /**
     * 计算当前整个视图应有的高度
     *
     * @return
     */
    private int getCurrentHeight() {
        return mCurrentMonthView.getMeasuredHeight() + this.getPaddingTop() + this
                .getPaddingBottom() + this.titleLayout.getMeasuredHeight() + this.mWeekTitle
                .getMeasuredHeight();
    }

    /**
     * 使用动画效果动态改变整个视图的高度,应该在月份切换的时候调用
     */
    private void resizeViewWithAnimation() {
        if (mCurrentMonthView == null || !mIsExpandable) {
            return;
        }
        ResizeAnimation resizeAnimation = new ResizeAnimation(this, getCurrentHeight());
        resizeAnimation.setDuration(400);
        startAnimation(resizeAnimation);
    }

    /**
     * 日历选择控件,选择月份
     *
     * @param context
     */
    public void showDatePicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View picker = inflater.inflate(R.layout.hlj_datetime_picker, null);
        final DatePicker datePicker = (DatePicker) picker.findViewById(R.id.date_picker);

        // 不显示日历视图
        datePicker.setCalendarViewShown(false);
        // 显示当前月份
        datePicker.updateDate(getCurrentCalendar().get(Calendar.YEAR),
                getCurrentCalendar().get(Calendar.MONTH),
                getCurrentCalendar().get(Calendar.DAY_OF_MONTH));
        datePicker.setMaxDate(endCalendar.getTimeInMillis());
        datePicker.setMinDate(beginCalendar.getTimeInMillis());
        // 无需选择哪一天
        // 5.0 不能使用这个
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int year = context.getResources()
                    .getIdentifier("android:id/day", null, null);
            View dayView = datePicker.findViewById(year);
            dayView.setVisibility(GONE);
        }

        builder.setView(picker);
        final Calendar calendar = (Calendar) beginCalendar.clone();
        calendar.add(Calendar.MONTH, mViewPager.getCurrentItem());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 选中日期并确定后,显示新的月份和刷新日历数据
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.YEAR, datePicker.getYear());
                mTitle.setText(dateTitleFormat.format(calendar.getTime()));
                mViewPager.setCurrentItem(getMonthsSinceBeginDate(calendar));
                dialogInterface.dismiss();
            }
        });

        builder.create()
                .show();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener mOnDateSelectedListener) {
        this.mOnDateSelectedListener = mOnDateSelectedListener;
    }

    public void setOnPageChangerListener(OnPageChangeListener mOnPageChangerListener) {
        this.mOnPageChangerListener = mOnPageChangerListener;
    }

    public void setOnMonthViewFinishUpdate(
            OnMonthViewFinishUpdateListener onMonthViewFinishUpdate) {
        this.mOnMonthViewFinishUpdateListener = onMonthViewFinishUpdate;
    }

    public interface OnDateSelectedListener {
        public void onSelectedDayChange(Calendar calendar);
    }

    public interface OnPageChangeListener {
        public void onPageChange(Calendar calendar);
    }

    public interface OnMonthViewFinishUpdateListener {
        public void onMonthViewFinishUpdate();
    }

    public class MonthAdapter extends PagerAdapter implements OnTouchListener {

        private final Calendar mSelectedDate = (Calendar) initialMonth.clone();
        private GestureDetector mGestureDetector;
        private Context mContext;
        private MonthView mSelectedMonthView;
        /**
         * 记录每一个位置上的每一个 monthview, 在获取当前的月份的视图的时候,根据传入的位置参数获得
         */
        private Map<Integer, MonthView> monthViewMap;
        private int mSelectedMonth;

        private MonthAdapter(Context mContext) {
            this.mContext = mContext;
            mGestureDetector = new GestureDetector(mContext, new CalendarGestureListener());
            mSelectedMonth = getMonthsSinceBeginDate(mSelectedDate);
            monthViewMap = new HashMap<>();
        }

        /**
         * 设置选中的月份
         *
         * @param mSelectedMonth2
         * @return true 如果更改了当前的值
         */
        public void setSelectedMonth(int mSelectedMonth2) {
            this.mSelectedMonth = mSelectedMonth2;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            if (mOnMonthViewFinishUpdateListener != null) {
                mOnMonthViewFinishUpdateListener.onMonthViewFinishUpdate();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MonthView monthView = new MonthView(mContext);

            //            int selectedMonth = (mSelectedMonth == position) ? mSelectedDate.get
            // (Calendar
            //                    .DAY_OF_MONTH) : -1;
            // 如果当前选中日期是当前 item 中的月份,则设置其中的选中日期
            monthView.init(mSelectedDate.get(Calendar.MONTH), position);

            if (mSelectedMonth == position && mSelectedMonthView == null) {
                mSelectedMonthView = monthView;
                mCurrentMonthView = monthView;
            }

            monthView.setClickable(true);
            monthView.setOnTouchListener(this);
            monthViewMap.put(position, monthView);
            container.addView(monthView);

            return monthView;
        }

        public MonthView getItemAtPosition(int position) {
            if (monthViewMap != null && !monthViewMap.isEmpty()) {
                MonthView monthView = monthViewMap.get(position);
                return monthView;
            }

            return null;
        }

        public Calendar getSelectedDate() {
            return mSelectedDate;
        }

        public Calendar getCurrentDate() {
            Calendar mCurrentMonth = (Calendar) beginCalendar.clone();
            return mCurrentMonth;
        }

        public void setSelectedDay(Calendar selectedDay) {
            //            if (selectedDay.get(Calendar.DAY_OF_YEAR) == mSelectedDate.get(Calendar
            // .DAY_OF_YEAR)
            //                    && selectedDay.get(Calendar.YEAR) == mSelectedDate.get(Calendar
            // .YEAR)) {
            //                return;
            //            }

            mSelectedDate.setTimeInMillis(selectedDay.getTimeInMillis());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return beginCalendar.before(endCalendar) ? 12 * (endCalendar.get(Calendar.YEAR) -
                    beginCalendar.get(
                    Calendar.YEAR)) + (endCalendar.get(Calendar.MONTH) - beginCalendar.get
                    (Calendar.MONTH)) + 1 : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar mCurrentMonth = (Calendar) beginCalendar.clone();
            mCurrentMonth.add(Calendar.MONTH, position);
            String date = dateTitleFormat.format(mCurrentMonth.getTime());
            return String.valueOf(date);
        }

        //        private float mDownX;
        //        private float mDownY;
        //        private final float SCROLL_THRESHOLD = 2;
        //        private boolean isOnClick;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            MonthView monthView = (MonthView) v;
            // 如果是单击事件则直接选中
            if (mGestureDetector.onTouchEvent(event)) {
                Log.d(TAG, "On Touch at month adapter");
                // 如果点击位置不是正确的允许的位置,什么都不用干了
                if (!monthView.getDayFromLocation(event.getX(), event.getY(), mTempDate)) {
                    return true;
                }

                // 避免计算点击事件得到的日期超过左右边界
                if (mTempDate.before(beginCalendar) || mTempDate.after(tempEndCalendar)) {
                    return true;
                }

                // 在月视图中记住选中的日期,并且设置标记
                monthView.setSelectedDay();

                onDateTapped(mTempDate);
                if (mSelectedMonth != getMonthsSinceBeginDate(mTempDate)) {
                    // 选中的月份改变,需要刷新之前那个月的
                    //                    if (mSelectedMonthView != null) {
                    //                        mSelectedMonthView.setSelectedDay(-1);
                    //                        mSelectedMonthView.invalidate();
                    //                    }

                    mSelectedMonthView = monthView;

                    setSelectedMonth(getMonthsSinceBeginDate(mTempDate));
                }

                monthView.invalidate();

                return true;
            }

            // 响应点击事件
           /* final int action = event.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN && monthView.getDayFromLocation(event.getX(),
                    event.getY(), mTempDate) && mTempDate.after(beginCalendar) && mTempDate
                    .before(endCalendar)) {
                mDownX = event.getX();
                mDownY = event.getY();
                isOnClick = true;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math.abs
                        (mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                    isOnClick = false;
                }
            }

            if (isOnClick && action != MotionEvent.ACTION_UP) {
                // 在月视图中记住点击的日期,并标记
                monthView.setPressedDay(mTempDate.get(Calendar.DAY_OF_MONTH));
            } else {
                monthView.setPressedDay(-1);
            }

            monthView.invalidate();*/

            return false;
        }

        /**
         * 触发界面修改和标注点击的日期
         *
         * @param day
         */
        private void onDateTapped(Calendar day) {
            setSelectedDay(day);
        }

        class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        }
    }

    private class MonthView extends View {

        private final Rect mTempRect = new Rect();

        private final Paint mDrawPaint = new Paint();

        private final Paint mMonthNumDrawPaint = new Paint();

        private int mWidth;

        private int mHeight;

        private Calendar mCurrentMonth;

        private Calendar mFirstDay;

        private int mNumCells;

        private ArrayList<String> mDayNumbers;

        private ArrayList<String> mLunarDayStrs;

        // 阴影标记
        private ArrayList<Integer> mShadowFlags;

        // 事件红点标记
        private ArrayList<Integer> mEventDotFlags;

        // 选中日期
        private ArrayList<Integer> mSelectDayFlags;

        // 吉日标记
        private ArrayList<Integer> mAuspiciousFlags;

        // 收藏标记
        private ArrayList<Integer> mCollectFlags;

        private int dayShift;

        private int mNumRows = 6;
        private int mPressedDay;
        private boolean mHasPressedDay;
        private int mSelectedMonth;

        public MonthView(Context context) {
            super(context);

            // 设置将用到的 paints
            initilaizePaints();
        }

        /**
         * 初始化 week view
         *
         * @param selectedMonth 选择的月份
         * @param focusedMonth
         */
        public void init(int selectedMonth, int focusedMonth) {
            mSelectedMonth = selectedMonth;
            mPressedDay = -1;
            mHasPressedDay = false;

            mCurrentMonth = (Calendar) beginCalendar.clone();
            mCurrentMonth.add(Calendar.MONTH, focusedMonth);

            if (dotDaysMap != null && !dotDaysMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mEventDotFlags = dotDaysMap.get(yearMonth);
            }

            if (mSelectDayMap != null && !mSelectDayMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mSelectDayFlags = mSelectDayMap.get(yearMonth);
            }

            if (closedDaysMap != null && !closedDaysMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mShadowFlags = closedDaysMap.get(yearMonth);
            }

            if (auspiciousDaysMap != null && !auspiciousDaysMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mAuspiciousFlags = auspiciousDaysMap.get(yearMonth);
            }

            if (collectDaysMap != null && !collectDaysMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mCollectFlags = collectDaysMap.get(yearMonth);
            }

            mNumCells = mDaysPerWeek * mNumRows;
            mDayNumbers = new ArrayList<>();
            mLunarDayStrs = new ArrayList<>();

            Calendar mNowCalendar = (Calendar) mCurrentMonth.clone();

            // 计算月初对于星期头天的偏移量 dayShift
            mNowCalendar.set(Calendar.DAY_OF_MONTH, 1);
            int firstDay = mNowCalendar.get(Calendar.DAY_OF_WEEK);
            if (mNowCalendar.getFirstDayOfWeek() == Calendar.MONDAY) {
                if (firstDay == Calendar.SUNDAY) {
                    dayShift = 6;
                } else {
                    dayShift = firstDay - 2;
                }
            } else {
                dayShift = firstDay - 1;
            }
            mNowCalendar.add(Calendar.DATE, -dayShift);
            mFirstDay = (Calendar) mNowCalendar.clone();

            // 初始化农历计算帮助类


            // 上一个月实例
            Calendar calBefore = (Calendar) mCurrentMonth.clone();
            calBefore.add(Calendar.MONTH, -1);

            //            Debug.startMethodTracing("calc");
            for (int i = 0; i < mNumCells; i++) {
                // 是否是本月
                boolean isCurrentMonth = mNowCalendar.get(Calendar.MONTH) == mCurrentMonth.get(
                        Calendar.MONTH);

                // 是否是上一个月
                boolean isBeforeMonth = mNowCalendar.get(Calendar.MONTH) == calBefore.get
                        (Calendar.MONTH);

                // 只添加前一个月或本月,不显示下一个月
                if (isCurrentMonth) {
                    mDayNumbers.add(String.format(Locale.getDefault(),
                            "%d",
                            mNowCalendar.get(Calendar.DAY_OF_MONTH)));
                } else if (isBeforeMonth) {
                    // 前一个月设置为 -1
                    mDayNumbers.add("-1");
                }

                // 设置农历日期字符
                if (mShowLunarDay) {
                    // 农历转换
                    LunarCalendarConvertUtil.parseLunarCalendar(mNowCalendar.get(Calendar.YEAR),
                            mNowCalendar.get(Calendar.MONTH) + 1,
                            mNowCalendar.get(Calendar.DAY_OF_MONTH),
                            lunarCalendar);
                    String[] lunarStrs = lunarCalendar.getLunarCalendarInfo();
                    String lunarStr;
                    if (!lunarStrs[4].isEmpty()) {
                        // 如果是法定节日,显示法定节日
                        lunarStr = lunarStrs[4];
                    } else if (!lunarStrs[3].isEmpty()) {
                        // 如果是传统节日,显示传统节日
                        lunarStr = lunarStrs[3];
                    } else if (!lunarStrs[5].isEmpty()) {
                        // 如果是节气,显示节气
                        lunarStr = lunarStrs[5];
                    } else {
                        lunarStr = lunarStrs[2];
                    }
                    mLunarDayStrs.add(lunarStr);
                }


                // 下一天
                mNowCalendar.add(Calendar.DATE, 1);
            }

            mNumCells = mDayNumbers.size();

            if (mIsExpandable) {
                resetHeight();
            }

            //            Debug.stopMethodTracing();
        }


        /**
         * 根据点击的x,y值计算出点击的日期并赋值给传入的日历实例
         *
         * @param x           点击位置的 x 坐标
         * @param y           点击位置的 y 坐标
         * @param outCalendar 传入的 calendar 实例, 成功的话此实例就是选中的日期
         * @return 计算成功返回 true, 当前日期之前返回false
         */
        public boolean getDayFromLocation(float x, float y, Calendar outCalendar) {
            if (x < 0 || x > mWidth || y < 0 || y > mHeight) {
                outCalendar.clear();
                return false;
            }

            int dayPositionX = (int) x * mDaysPerWeek / mWidth;
            int dayPositionY = (int) y * mNumRows / mHeight;

            int dayIndex = dayPositionY * mDaysPerWeek + dayPositionX;
            outCalendar.setTimeInMillis(mFirstDay.getTimeInMillis());
            outCalendar.add(Calendar.DAY_OF_MONTH, dayIndex);

            if (outCalendar.get(Calendar.MONTH) != mCurrentMonth.get(Calendar.MONTH)) {
                return false;
            }

            // 当前日期
            if (outCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) && outCalendar.get(
                    Calendar.DAY_OF_YEAR) < nowCalendar.get(Calendar.DAY_OF_YEAR)) {
                return false;
            }

            return true;
        }

        public void setSelectedDay() {
        }

        public void setPressedDay(int mPressedDay) {
            this.mPressedDay = mPressedDay;
            mHasPressedDay = mPressedDay != -1;
        }

        public void setShadowFlags(ArrayList<Integer> shadowFlag) {
            this.mShadowFlags = shadowFlag;
        }

        public void setEventDotFlags(ArrayList<Integer> eventDotFlags) {
            this.mEventDotFlags = eventDotFlags;
        }

        public void setSelectDayFlags(HashMap<String, ArrayList<Integer>> map) {
            String yearMonth = (mCurrentMonth.getTime()
                    .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                    .getMonth() + 1);
            this.mSelectDayFlags = map.get(yearMonth);
        }

        public void setAuspiciousFlags(ArrayList<Integer> auspiciousFlags) {
            this.mAuspiciousFlags = auspiciousFlags;
        }

        public void setCollectFlags(ArrayList<Integer> collectFlags) {
            this.mCollectFlags = collectFlags;
        }

        private void initilaizePaints() {
            mDrawPaint.setFakeBoldText(false);
            mDrawPaint.setAntiAlias(true);
            mDrawPaint.setStyle(Paint.Style.FILL);

            mMonthNumDrawPaint.setFakeBoldText(false);
            mMonthNumDrawPaint.setAntiAlias(true);
            mMonthNumDrawPaint.setStyle(Paint.Style.FILL);
            mMonthNumDrawPaint.setTextAlign(Paint.Align.CENTER);
            mMonthNumDrawPaint.setTextSize(mDateTextSize);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            drawBackground(canvas);
            //            drawGridLine(canvas);
            drawDates(canvas);
        }

        private void drawBackground(Canvas canvas) {
            mDrawPaint.setColor(mBackgroundColor);
            mTempRect.top = 0;
            mTempRect.bottom = mHeight;

            mTempRect.left = 0;
            mTempRect.right = mWidth;

            canvas.drawRect(mTempRect, mDrawPaint);
        }

        private void drawDates(Canvas canvas) {
            final float textHeight = mDrawPaint.getTextSize();
            float textHeight2 = 0;
            if (mShowLunarDay) {
                textHeight2 = mLunarDateTextSize + mLunarDateTextMarginTop;
            }
            float dotHeight = mFlagDotSize + mDotMarginTop;

            final int cellWidth = (mWidth - mCalendarPaddingLeft - mCalendarPaddingRight) /
                    mDaysPerWeek;
            final int cellHeight = (mHeight - mCalendarPaddingTop - mCalendarPaddingBottom) /
                    mNumRows;

            // 单元格的半径
            final int radius = ((cellWidth > cellHeight ? cellHeight : cellWidth) - mBoardMargin)
                    / 2;
            final int offsetOfDot = (int) ((textHeight + textHeight2 + mFlagDotSize) / cellHeight
                    * mLunarDateTextSize);

            int offsetHeightZero = cellHeight / 2 + mCalendarPaddingTop;

            // 阳历日期位置的偏移量
            int offsetHeight = (int) ((cellHeight + textHeight - textHeight2 - dotHeight) / 2) +
                    offsetOfDot + mCalendarPaddingTop;

            // 农历日期位置的偏移量
            int offsetHeight2 = (int) ((cellHeight + textHeight + textHeight2 - dotHeight) / 2) +
                    offsetOfDot + mCalendarPaddingTop;

            // 事件标识圆点的偏移量
            int offsetHeight3 = (int) ((cellHeight + textHeight + textHeight2) / 2 +
                    mDotMarginTop) + offsetOfDot + mCalendarPaddingTop;
            int offsetWidth = cellWidth / 2 + mCalendarPaddingLeft;

            int index = 0;
            Calendar temp = (Calendar) mFirstDay.clone();
            // 计算是否是当前时间所属月,如果是的话,得到当天的dayOfMonth,用于分隔今天之前的日期
            int today = Integer.MIN_VALUE; // 默认today不在本月
            if (mCurrentMonth.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) &&
                    mCurrentMonth.get(
                    Calendar.MONTH) == nowCalendar.get(Calendar.MONTH)) {
                today = nowCalendar.get(Calendar.DAY_OF_MONTH);
            }
            for (int i = 0; i < mNumRows && index < mNumCells - 1; i++) {
                for (int j = 0; j < mDaysPerWeek && index < mNumCells - 1; j++) {
                    index = i * mDaysPerWeek + j;
                    temp.add(Calendar.DAY_OF_MONTH, index);
                    String dayStr = mDayNumbers.get(index);
                    int dayOfMonthIndex = Integer.parseInt(dayStr);
                    boolean isShadow = false;

                    if (!dayStr.equals("-1")) {
                        // 初始为中点
                        int x = j * cellWidth + offsetWidth;
                        int y = i * cellHeight + offsetHeightZero;

                        // 如果这一天有阴影标识,画出阴影
                        if (mShadowFlags != null && mShadowFlags.contains(dayOfMonthIndex)) {
                            mDrawPaint.setColor(mShadowFlagColor);
                            canvas.drawCircle(x, y, radius, mDrawPaint);
                            isShadow = true;
                        }

                        // 如果当前月是选中日期的月份, 则画出选中日期的背景
                        // 多选，点击相同位置取消选中
                        if (mSelectDayFlags != null && mSelectDayFlags.contains(dayOfMonthIndex)
                                && Integer.valueOf(
                                dayStr) >= today) {
                            mDrawPaint.setColor(mSelectedBackgroundColor);
                            if (!mSelectStyle) {
                                mDrawPaint.setStrokeWidth(mBackgroundLineWidth);
                                mDrawPaint.setStyle(Paint.Style.STROKE);
                                y = i * cellHeight + offsetHeightZero;
                                canvas.drawCircle(x, y, radius, mDrawPaint);
                            } else {
                                mDrawPaint.setStyle(Paint.Style.FILL);
                                y = i * cellHeight + offsetHeightZero;
                                canvas.drawCircle(x, y, radius, mDrawPaint);
                                isShadow = true;
                            }
                            mDrawPaint.setStyle(Paint.Style.FILL);

                            // 画一下十字定位中点
                            /*mDrawPaint.setColor(Color.GREEN);
                            mDrawPaint.setStrokeWidth(1);
                            int centerX = j * cellWidth + cellWidth / 2;
                            int centerY = i * cellHeight + cellHeight / 2;
                            canvas.drawLine(centerX - cellWidth / 2, centerY - cellWidth / 2,
                                    centerX + cellWidth / 2, centerY + cellWidth / 2, mDrawPaint);
                            canvas.drawLine(centerX + cellWidth / 2, centerY - cellWidth / 2,
                                    centerX - cellWidth / 2, centerY + cellWidth / 2, mDrawPaint);
                            */
                        }

                        // 点击标记
                        /*if (mHasPressedDay && dayStr.equals(String.valueOf(mPressedDay))) {
                            mDrawPaint.setColor(mPressedColor);
                            y = i * cellHeight + cellHeight / 2;
                            canvas.drawCircle(x, y, cellWidth / 2, mDrawPaint);
                        }*/

                        // 画出公历数字
                        y = i * cellHeight + offsetHeight;
                        mMonthNumDrawPaint.setTextSize(mDateTextSize);
                        if (isShadow) {
                            mMonthNumDrawPaint.setColor(Color.WHITE);
                        } else if (Integer.valueOf(dayStr) < today) {
                            // 过去的日子
                            mMonthNumDrawPaint.setColor(mPassedDayColor);
                        } else {
                            if (mSelectStyle) {
                                if (mSelectDayFlags != null && mSelectDayFlags.contains(
                                        dayOfMonthIndex) && Integer.valueOf(dayStr) >= today) {
                                    mMonthNumDrawPaint.setColor(Color.WHITE);
                                } else if (temp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                                        temp.get(
                                        Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                    mMonthNumDrawPaint.setColor(mSelectedBackgroundColor);
                                } else {
                                    mMonthNumDrawPaint.setColor(mDateTextColor.getDefaultColor());
                                }
                            }
                        }

                        if (Integer.valueOf(dayStr) == today) {
                            mMonthNumDrawPaint.setTextSize(mDateTextSize);
                            canvas.drawText(todayStr, x, y, mMonthNumDrawPaint);
                        } else {
                            mMonthNumDrawPaint.setTextSize(getResources().getDimensionPixelSize(R
                                    .dimen.default_lunar_date_text_size));
                            canvas.drawText(dayStr, x, y, mMonthNumDrawPaint);
                        }


                        if (mShowLunarDay) {
                            // 画出农历
                            String lunarDayStr = mLunarDayStrs.get(index);
                            mMonthNumDrawPaint.setTextSize(mLunarDateTextSize);

                            if (isShadow) {
                                mMonthNumDrawPaint.setColor(Color.WHITE);
                            } else if (Integer.valueOf(dayStr) < today) {
                                mMonthNumDrawPaint.setColor(mPassedDayColor);
                            } else {
                                if (mSelectDayFlags != null && mSelectDayFlags.contains(
                                        dayOfMonthIndex) && Integer.valueOf(dayStr) >= today) {
                                    mMonthNumDrawPaint.setColor(Color.WHITE);
                                } else if (temp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                                        temp.get(
                                        Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                    mMonthNumDrawPaint.setColor(mSelectedBackgroundColor);
                                } else {
                                    mMonthNumDrawPaint.setColor(mLunarDateTextColor
                                            .getDefaultColor());
                                }
                            }
                            y = i * cellHeight + offsetHeight2;
                            canvas.drawText(lunarDayStr, x, y, mMonthNumDrawPaint);
                        }

                        // 如果这一天有事件标识,画出事件标识圆点
                        if (mEventDotFlags != null && mEventDotFlags.contains(dayOfMonthIndex) &&
                                Integer.valueOf(
                                dayStr) >= today) {
                            mDrawPaint.setColor(mFlagDotColor);
                            y = i * cellHeight + offsetHeight3;
                            canvas.drawCircle(x, y, mFlagDotSize / 2, mDrawPaint);
                        }


                        if (mAuspiciousFlags != null && mAuspiciousFlags.contains
                                (dayOfMonthIndex) && Integer.valueOf(
                                dayStr) >= today) {
                            mDrawPaint.setColor(Color.WHITE);
                            x = j * cellWidth + mCalendarPaddingLeft + mBoardMargin / 2 +
                                    mAuspiciousBadgeSize / 2;
                            y = i * cellHeight + mCalendarPaddingTop + mBoardMargin / 2 +
                                    mAuspiciousBadgeSize / 2;
                            canvas.drawCircle(x, y, mAuspiciousBadgeSize / 2, mDrawPaint);
                            mDrawPaint.setColor(mAuspiciousBadgeColor);
                            mDrawPaint.setStyle(Paint.Style.STROKE);
                            mDrawPaint.setStrokeWidth(mBackgroundLineWidth / 2);
                            canvas.drawCircle(x, y, mAuspiciousBadgeSize / 2, mDrawPaint);
                            mDrawPaint.setStyle(Paint.Style.FILL);

                            mMonthNumDrawPaint.setColor(mAuspiciousBadgeColor);
                            mMonthNumDrawPaint.setTextSize(mAuspiciousBadgeSize * 3 / 4);
                            canvas.drawText(getResources().getString(R.string.ji),
                                    x,
                                    (y + mAuspiciousBadgeSize / 2 - mBoardMargin / 2) -
                                            mAuspiciousBadgeSize / 8,
                                    mMonthNumDrawPaint);
                        }

                        if (mCollectFlags != null && mCollectFlags.contains(dayOfMonthIndex) &&
                                Integer.valueOf(
                                dayStr) >= today) {
                            x = j * cellWidth + mCalendarPaddingLeft + mBoardMargin / 2 +
                                    mBackgroundLineWidth;
                            y = i * cellHeight + offsetHeightZero - mCollectBadgeSize / 2;
                            if (isShadow) {
                                canvas.drawBitmap(mCollectBadgeBitMap2, x, y, mDrawPaint);
                            } else {
                                canvas.drawBitmap(mCollectBadgeBitMap, x, y, mDrawPaint);
                            }
                        }
                    }

                    temp.add(Calendar.DAY_OF_MONTH, -index);
                }
            }

        }

        private void drawGridLine(Canvas canvas) {
            mDrawPaint.setColor(mLunarDateTextColor.getDefaultColor());
            mDrawPaint.setStrokeWidth(1);
            final int cellWidth = (mWidth - mCalendarPaddingLeft - mCalendarPaddingRight) /
                    mDaysPerWeek;
            final int cellHeight = (mHeight - mCalendarPaddingTop - mCalendarPaddingBottom) /
                    mNumRows;

            for (int i = 0; i < mDaysPerWeek + 1; i++) {
                float startX = i * cellWidth + mCalendarPaddingLeft;
                canvas.drawLine(startX,
                        mCalendarPaddingTop,
                        startX,
                        mHeight - mCalendarPaddingBottom,
                        mDrawPaint);
            }

            for (int j = 0; j < mNumRows + 1; j++) {
                float startY = j * cellHeight + mCalendarPaddingTop;
                canvas.drawLine(mCalendarPaddingLeft,
                        startY,
                        mWidth - mCalendarPaddingLeft,
                        startY,
                        mDrawPaint);
            }
        }

        private void resetHeight() {
            mNumRows = mNumCells % mDaysPerWeek == 0 ? mNumCells / mDaysPerWeek : mNumCells /
                    mDaysPerWeek + 1;
            mWidth = mViewPager.getWidth();
            mHeight = (mWidth - mCalendarPaddingLeft - mCalendarPaddingRight) / mDaysPerWeek *
                    mNumRows;

            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // 为保证日历单元格宽高相等, 设置相应的整体宽高与行数列数成比例
            // 列数固定为7列, 行数初始为6行, 在计算出对应月份的日期数及其月初偏移数之后再决定具体行数, 可能会是4,5,6这三种

            // 宽的值由mViewPager决定, mViewPager 则由本控件本身决定
            mWidth = mViewPager.getWidth();
            mHeight = (mWidth - mCalendarPaddingLeft - mCalendarPaddingRight) / mDaysPerWeek *
                    mNumRows;
            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

    }

}
