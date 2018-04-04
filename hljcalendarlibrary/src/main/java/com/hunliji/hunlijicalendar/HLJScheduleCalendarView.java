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

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by LuoHanLin on 14/12/11.
 */
public class HLJScheduleCalendarView extends FrameLayout {
    private static final String TAG = HLJCalendarView.class.getSimpleName();

    // default settings
    private static final int DEFAULT_DATE_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_LUNAR_TEXT_COLOR = Color.GRAY;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_SELECTED_COLOR = Color.RED;
    private static final int DEFAULT_DOT_COLOR = Color.RED;
    private static final int DEFAULT_SHADOW_FLAG_COLOR = Color.GRAY;
    private static final int DEFAULT_PRESSED_COLOR = Color.GRAY;
    private static final int DEFAULT_VACATION_BADGE_COLOR = Color.GREEN;

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
    private int mVacationBadgeColor;
    private int mVacationBadgeSize;
    private int mCollectBadgeSize;
    private Bitmap mCollectBadgeBitMap;
    private Bitmap mCollectBadgeBitMap2;
    private boolean mIsExpandable;
    private ColorStateList mDateTextColor;
    private ColorStateList mLunarDateTextColor;
    private int mPressedColor;
    private int mFlagDotColor;
    private int mFlagDotSize;
    private int mBackgroundColor;
    private int mSelectedBackgroundColor;
    private int mDaysPerWeek = 7;
    private int mCurrentItem = -1;
    private int mOffsetHeight;
    private Calendar beginCalendar;
    private Calendar endCalendar;
    private Calendar initialMonth;

    private ViewPager mViewPager;
    private MonthAdapter mAdapter;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateTitleFormat;

    private Calendar mTempDate; // 可暂用的 calendar 实例, 以免到处新建无必要的实例

    /**
     * 农历
     */
    private LunarCalendar lunarCalendar;

    /**
     * 是否显示农历, 默认显示
     */
    private boolean mShowLunarDay = true;

    /**
     * 当前显示的月份 view 实例
     */
    private MonthView mCurrentMonthView;

    private HashMap<String, ArrayList<Integer>> closedDaysMap;

    public HLJScheduleCalendarView(Context context) {
        this(context, null);
    }

    public HLJScheduleCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLJScheduleCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mBackgroundColor = typedArray.getColor(R.styleable.HLJCalendarView_background_color,
                DEFAULT_BACKGROUND_COLOR);
        mSelectedBackgroundColor = typedArray.getColor(R.styleable
                        .HLJCalendarView_selected_background_color,
                DEFAULT_SELECTED_COLOR);
        mFlagDotColor = typedArray.getColor(R.styleable.HLJCalendarView_dot_color,
                DEFAULT_DOT_COLOR);
        mShadowFlagColor = typedArray.getColor(R.styleable.HLJCalendarView_shadow_flag_color,
                DEFAULT_SHADOW_FLAG_COLOR);
        mPressedColor = typedArray.getColor(R.styleable.HLJCalendarView_pressed_color,
                DEFAULT_PRESSED_COLOR);
        mVacationBadgeColor = typedArray.getColor(R.styleable.HLJCalendarView_vacation_badge_color,
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
        mVacationBadgeSize = typedArray.getDimensionPixelSize(R.styleable
                        .HLJCalendarView_vacation_badge_size,
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
        mOffsetHeight = context.getResources()
                .getDimensionPixelSize(R.dimen.default_offset_height);
        Drawable drawable = context.getResources()
                .getDrawable(R.drawable.fav_red);
        mCollectBadgeBitMap = ((BitmapDrawable) drawable).getBitmap();
        drawable = context.getResources()
                .getDrawable(R.drawable.fav_white);
        mCollectBadgeBitMap2 = ((BitmapDrawable) drawable).getBitmap();
    }

    public HLJScheduleCalendarView(
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
        this.initialMonth = (Calendar) currentMonth.clone();
        this.beginCalendar = (Calendar) leftMonth.clone();
        this.endCalendar = (Calendar) rightMonth.clone();

        // 设置星期开始第一天
        beginCalendar.setFirstDayOfWeek(firstDayOfWeek);
        endCalendar.setFirstDayOfWeek(firstDayOfWeek);
        initialMonth.setFirstDayOfWeek(firstDayOfWeek);
        mTempDate = Calendar.getInstance();
        lunarCalendar = new LunarCalendar(context);

        dateTitleFormat = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());

        Log.d(TAG, "Begin from month: " + dateFormat.format(beginCalendar.getTime()));
        Log.d(TAG, "End at month: " + dateFormat.format(endCalendar.getTime()));
        Log.d(TAG, "Now setting day: " + dateFormat.format(initialMonth.getTime()));

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.framelayout_hlj_schedule_calendar, this, true);
        mViewPager = findViewById(R.id.viewpager);
        setWillNotDraw(false);
        mViewPager.setWillNotDraw(false);
        setUpAdapter(context);
        setUpWeekTitle();
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
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
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
    }

    /**
     * 设置日历头部的星期标记
     */
    private void setUpWeekTitle() {
        String[] titles = new String[mDaysPerWeek];
        SimpleDateFormat weekDateFormat = new SimpleDateFormat("EE", Locale.getDefault());

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
    }

    public void setShowLunarDay(boolean mShowLunarDay) {
        this.mShowLunarDay = mShowLunarDay;
    }

    private int getMonthsSinceBeginDate(Calendar date) {
        if (date.before(beginCalendar) || date.after(endCalendar)) {
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
     * 当前选中的day
     *
     * @param selectDay
     */
    public void setSelectDay(int selectDay) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setSelectedDay(selectDay);
        }
    }

    /**
     * 设置档期时候已满
     *
     * @param flags
     */
    public void setFullDots(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setFullDotFlags(flags);
        }
    }

    /**
     * 设置当前月份的假日标识
     *
     * @param flags
     */
    public void setVacations(ArrayList<Integer> flags) {
        if (mCurrentMonthView != null) {
            mCurrentMonthView.setVacationFlags(flags);
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

    public void setClosedDaysMap(HashMap<String, ArrayList<Integer>> map) {
        this.closedDaysMap = map;
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
                mCurrentMonthView.setSelectedDay(calendar.get(Calendar.DAY_OF_MONTH));
                mCurrentMonthView.invalidate();
            }
        }
    }

    /**
     * 计算当前整个视图应有的高度
     *
     * @return
     */
    public int getCurrentHeight() {
        return mCurrentMonthView.getMeasuredHeight() + this.getPaddingTop() + this
                .getPaddingBottom() + CommonUtil.dp2px(
                getContext(),
                44.5f);
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

    public class MonthAdapter extends PagerAdapter implements OnTouchListener {

        private GestureDetector mGestureDetector;

        private Context mContext;

        private MonthView mSelectedMonthView;

        /**
         * 记录每一个位置上的每一个 monthview, 在获取当前的月份的视图的时候,根据传入的位置参数获得
         */
        private Map<Integer, MonthView> monthViewMap;

        private final Calendar mSelectedDate = (Calendar) initialMonth.clone();
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

            int selectedMonth = (mSelectedMonth == position) ? mSelectedDate.get(Calendar
                    .DAY_OF_MONTH) : -1;
            // 如果当前选中日期是当前 item 中的月份,则设置其中的选中日期
            monthView.init(selectedMonth, position);

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
                // 在月视图中记住选中的日期,并且设置标记
                monthView.setSelectedDay(mTempDate.get(Calendar.DAY_OF_MONTH));

                // 避免计算点击事件得到的日期超过左右边界
                if (mTempDate.before(beginCalendar) || mTempDate.after(endCalendar)) {
                    return true;
                }

                onDateTapped(mTempDate);
                if (mSelectedMonth != getMonthsSinceBeginDate(mTempDate)) {
                    // 选中的月份改变,需要刷新之前那个月的
                    if (mSelectedMonthView != null) {
                        mSelectedMonthView.setSelectedDay(-1);
                        mSelectedMonthView.invalidate();
                    }

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

        private boolean mHasSelectedDay = false;

        // 默认没有选中的日期
        private int mSelectedDay = -1;

        private int mNumCells;

        private ArrayList<String> mDayNumbers;

        private ArrayList<String> mLunarDayStrs;

        // 阴影标记
        private ArrayList<Integer> mShadowFlags;

        // 事件红点标记
        private ArrayList<Integer> mEventDotFlags;

        //档期已满
        private ArrayList<Integer> mFullDotFlags;

        // 假期标记
        private ArrayList<Integer> mVacationFlags;

        // 收藏标记
        private ArrayList<Integer> mCollectFlags;

        private int dayShift;

        private int mNumRows = 6;
        private int mPressedDay;
        private boolean mHasPressedDay;

        public MonthView(Context context) {
            super(context);

            // 设置将用到的 paints
            initilaizePaints();
        }

        /**
         * 初始化 week view
         *
         * @param selectedMonthDay 当月选中的日期
         * @param focusedMonth
         */
        public void init(int selectedMonthDay, int focusedMonth) {
            mSelectedDay = selectedMonthDay;
            mHasSelectedDay = mSelectedDay != -1;
            mPressedDay = -1;
            mHasPressedDay = false;

            mCurrentMonth = (Calendar) beginCalendar.clone();
            mCurrentMonth.add(Calendar.MONTH, focusedMonth);

            if (closedDaysMap != null && !closedDaysMap.isEmpty()) {
                String yearMonth = (mCurrentMonth.getTime()
                        .getYear() + 1900) + "-" + (mCurrentMonth.getTime()
                        .getMonth() + 1);
                this.mShadowFlags = closedDaysMap.get(yearMonth);
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
         * @return 计算成功返回 true
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

            return outCalendar.get(Calendar.MONTH) == mCurrentMonth.get(Calendar.MONTH);

        }

        public void setSelectedDay(int mSelectedDay) {
            this.mSelectedDay = mSelectedDay;
            mHasSelectedDay = mSelectedDay != -1;
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

        public void setFullDotFlags(ArrayList<Integer> fullDotFlags) {
            this.mFullDotFlags = fullDotFlags;
        }

        public void setVacationFlags(ArrayList<Integer> vacationFlags) {
            this.mVacationFlags = vacationFlags;
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

            final int cellWidth = (mWidth - mCalendarPaddingLeft - mCalendarPaddingRight) /
                    mDaysPerWeek;
            final int cellHeight = (mHeight - mCalendarPaddingTop - mCalendarPaddingBottom) /
                    mNumRows;

            // 单元格的半径
            final int radius = ((cellWidth > cellHeight ? cellHeight : cellWidth) - mBoardMargin)
                    / 2;
            final int offsetOfDot = (int) ((textHeight + textHeight2) / cellHeight *
                    mLunarDateTextSize);

            int offsetHeightZero = cellHeight / 2 + mCalendarPaddingTop;

            // 阳历日期位置的偏移量
            int offsetHeight = (int) ((cellHeight + textHeight - textHeight2) / 2) + offsetOfDot
                    + mCalendarPaddingTop;

            // 农历日期位置的偏移量
            int offsetHeight2 = (int) ((cellHeight + textHeight + textHeight2) / 2) + offsetOfDot
                    + mCalendarPaddingTop;

            // 事件标识圆点的偏移量
            int offsetHeight3 = cellHeight + mDotMarginTop + mFlagDotSize;

            int offsetWidth = cellWidth / 2 + mCalendarPaddingLeft;

            int index = 0;
            Calendar temp = (Calendar) mFirstDay.clone();
            if (mCurrentItem == -1) {
                mCurrentItem = mViewPager.getCurrentItem();
            }
            int currentDay = initialMonth.get(Calendar.DAY_OF_MONTH);
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

                        //判断当天的是否已满
                        boolean isFull = false;
                        if (mFullDotFlags != null && mFullDotFlags.contains(dayOfMonthIndex)) {
                            isFull = true;
                            mDrawPaint.setColor(Color.parseColor("#7ee7e7e7"));
                            mDrawPaint.setStyle(Paint.Style.FILL);
                            mDrawPaint.setStrokeWidth(mBackgroundLineWidth);
                            y = i * cellHeight + offsetHeightZero;
                            canvas.drawCircle(x, y, radius, mDrawPaint);
                        }

                        //今天是否在界面上
                        boolean isCurrentDay = mViewPager.getCurrentItem() == mCurrentItem &&
                                currentDay == dayOfMonthIndex;
                        //今天在界面上，并且被选中了
                        boolean isSelectCurrentDay = false;
                        //如果当前选中的日期是今天的话，则画出选中日期的背景
                        if (!isFull && isCurrentDay && mHasSelectedDay && mSelectedDay ==
                                currentDay) {
                            isSelectCurrentDay = true;
                            mDrawPaint.setColor(mSelectedBackgroundColor);
                            mDrawPaint.setStyle(Paint.Style.FILL);
                            y = i * cellHeight + offsetHeightZero;
                            canvas.drawCircle(x, y, radius, mDrawPaint);
                        }

                        // 如果当前月是选中日期的月份, 则画出选中日期的背景
                        if (mHasSelectedDay && dayStr.equals(String.valueOf(mSelectedDay)) &&
                                !isSelectCurrentDay) {
                            mDrawPaint.setColor(mSelectedBackgroundColor);
                            mDrawPaint.setStrokeWidth(mBackgroundLineWidth);
                            mDrawPaint.setStyle(Paint.Style.STROKE);
                            y = i * cellHeight + offsetHeightZero;
                            canvas.drawCircle(x, y, radius, mDrawPaint);
                        }

                        // 画出公历数字
                        y = i * cellHeight + offsetHeight;
                        mMonthNumDrawPaint.setTextSize(mDateTextSize);
                        if (isShadow) {
                            mMonthNumDrawPaint.setColor(Color.WHITE);
                        } else if (isCurrentDay) {
                            mMonthNumDrawPaint.setColor((mHasSelectedDay && currentDay ==
                                    mSelectedDay && !isFull) ? Color.parseColor(
                                    "#ffffff") : ContextCompat.getColor(getContext(),
                                    R.color.colorPrimary));
                        } else if (isFull) {
                            mMonthNumDrawPaint.setColor(Color.parseColor("#aaaaaa"));
                        } else {
                            mMonthNumDrawPaint.setColor(mDateTextColor.getDefaultColor());
                        }
                        canvas.drawText(dayStr, x, y, mMonthNumDrawPaint);

                        if (mShowLunarDay) {
                            // 画出农历
                            String lunarDayStr = mLunarDayStrs.get(index);
                            mMonthNumDrawPaint.setTextSize(mLunarDateTextSize);
                            if (isShadow) {
                                mMonthNumDrawPaint.setColor(Color.WHITE);
                            } else if (isCurrentDay) {
                                mMonthNumDrawPaint.setColor((mHasSelectedDay && currentDay ==
                                        mSelectedDay && !isFull) ? Color.parseColor(
                                        "#ffffff") : ContextCompat.getColor(getContext(),
                                        R.color.colorPrimary));
                            } else if (isFull) {
                                mMonthNumDrawPaint.setColor(Color.parseColor("#aaaaaa"));
                            } else {
                                mMonthNumDrawPaint.setColor(mLunarDateTextColor.getDefaultColor());
                            }
                            y = i * cellHeight + offsetHeight2;
                            canvas.drawText(lunarDayStr, x, y, mMonthNumDrawPaint);
                        }

                        // 如果这一天有事件标识,画出事件标识圆点
                        if (mEventDotFlags != null && mEventDotFlags.contains(dayOfMonthIndex) &&
                                !isFull) {
                            mDrawPaint.setColor(mFlagDotColor);
                            mDrawPaint.setStyle(Paint.Style.FILL);
                            y = i * cellHeight + offsetHeight3;
                            canvas.drawCircle(x, y, mFlagDotSize / 2, mDrawPaint);
                        }

                        //假期
                        if (mVacationFlags != null && mVacationFlags.contains(dayOfMonthIndex)) {
                            mDrawPaint.setColor(mVacationBadgeColor);
                            x = j * cellWidth + mCalendarPaddingLeft + mBoardMargin / 2 +
                                    mVacationBadgeSize / 2;
                            y = i * cellHeight + mCalendarPaddingTop + mBoardMargin / 2 +
                                    mVacationBadgeSize / 2;
                            canvas.drawCircle(x, y, mVacationBadgeSize / 2, mDrawPaint);
                            mDrawPaint.setColor(Color.WHITE);
                            mDrawPaint.setStyle(Paint.Style.STROKE);
                            mDrawPaint.setStrokeWidth(mBackgroundLineWidth / 2);
                            canvas.drawCircle(x, y, mVacationBadgeSize / 2, mDrawPaint);
                            mDrawPaint.setStyle(Paint.Style.FILL);

                            mMonthNumDrawPaint.setColor(Color.WHITE);
                            mMonthNumDrawPaint.setTextSize(mVacationBadgeSize * 3 / 4);
                            canvas.drawText("假",
                                    x,
                                    (y + mVacationBadgeSize / 2 - mCalendarPaddingTop -
                                            mBoardMargin / 2) - mVacationBadgeSize / 8,
                                    mMonthNumDrawPaint);
                        }

                        //收藏
                        if (mCollectFlags != null && mCollectFlags.contains(dayOfMonthIndex)) {
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
            mHeight = mWidth / mDaysPerWeek * mNumRows + mOffsetHeight;
            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // 为保证日历单元格宽高相等, 设置相应的整体宽高与行数列数成比例
            // 列数固定为7列, 行数初始为6行, 在计算出对应月份的日期数及其月初偏移数之后再决定具体行数, 可能会是4,5,6这三种

            // 宽的值由mViewPager决定, mViewPager 则由本控件本身决定
            mWidth = mViewPager.getWidth();
            if (mWidth == 0) {
                mWidth = mViewPager.getMeasuredWidth();
            }
            mHeight = mWidth / mDaysPerWeek * mNumRows + mOffsetHeight;
            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

    }

    /**
     * 日历选择控件,选择月份
     *
     * @param context
     */
    /**
     * 日历选择控件,选择月份
     *
     * @param context
     */
    public void showDatePicker(Context context, int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View picker = View.inflate(getContext(), R.layout.hlj_datetime_picker, null);
        final DatePicker datePicker = picker.findViewById(R.id.date_picker);

        // 不显示日历视图
        datePicker.setCalendarViewShown(false);
        datePicker.updateDate(year, month - 1, day);
        datePicker.setMaxDate(endCalendar.getTimeInMillis());
        datePicker.setMinDate(beginCalendar.getTimeInMillis());
        builder.setView(picker);
        final Calendar calendar = (Calendar) beginCalendar.clone();
        calendar.add(Calendar.MONTH, mViewPager.getCurrentItem());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 选中日期并确定后,显示新的月份和刷新日历数据
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                if (mOnDatePickChangeListenenr != null) {
                    mOnDatePickChangeListenenr.onDatePickerChangeListener(calendar);
                }
                mViewPager.setCurrentItem(getMonthsSinceBeginDate(calendar));
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        builder.create()
                .show();
    }


    public interface OnDateSelectedListener {
        void onSelectedDayChange(Calendar calendar);
    }

    public interface OnPageChangeListener {
        void onPageChange(Calendar calendar);
    }

    public interface OnMonthViewFinishUpdateListener {
        void onMonthViewFinishUpdate();
    }

    public interface OnDatePickerChangeListener {
        void onDatePickerChangeListener(Calendar calendar);
    }

    private OnPageChangeListener mOnPageChangerListener;

    private OnDateSelectedListener mOnDateSelectedListener;

    private OnMonthViewFinishUpdateListener mOnMonthViewFinishUpdateListener;

    private OnDatePickerChangeListener mOnDatePickChangeListenenr;

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

    public void setOnDatePickerChangeListener(OnDatePickerChangeListener onDatePickerChange) {
        this.mOnDatePickChangeListenenr = onDatePickerChange;
    }
}
