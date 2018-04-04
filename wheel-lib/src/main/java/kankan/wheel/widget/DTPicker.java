package kankan.wheel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.Calendar;

import kankan.wheel.R;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by luohanlin on 15/5/4.
 */
public class DTPicker extends FrameLayout {
    public static final String TAG = DateTimePickerView.class.getSimpleName();
    private final static int MSG_SOUND_EFFECT = 1;

    private Context mContext;
    private MonthDateNumericAdapter monthArrayAdapter;
    private DateNumericAdapter yearNumericAdapter;
    private DateNumericAdapter dayNumericAdapter;
    private DateNumericAdapter hourArrayAdapter;
    private DateNumericAdapter minuteArrayAdapter;
    private DisplayMetrics dm;
    private SoundPool sp;
    private int soundId;
    public OnPickerDateTimeListener onPickerDateTimeListener;
    public OnPickerDateListener onPickerDateListener;
    public OnPickerTimeListener onPickerTimeListener;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int startYear;
    private int type = 0; // 0：日期时间选择控件，1：时间选择控件，2：日期选择控件
    private int playedCount = 5; // 第一次设置滚轮不播放声音,用这个作为标记位
    private boolean isMonthUnDefined = false;//是否月份显示待定

    private int itemHeight;//item height
    private int visibleItems;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SOUND_EFFECT:
                    playSoundEffect();
                    break;
            }
        }
    };

    OnWheelChangedListener yearWheelListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // 在这里响应滚动选择事件
            yearNumericAdapter.setSelectedItem(newValue);
            wheel.invalidateWheel(false);
            year = startYear + newValue;

            if (onPickerDateTimeListener != null) {
                onPickerDateTimeListener.onPickerDateAndTime(year, month, day, hour, minute);
            }
            if (onPickerDateListener != null) {
                onPickerDateListener.onPickerDate(year, month, day);
            }
            mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
        }
    };

    OnWheelChangedListener monthWheelListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // 在这里响应滚动选择事件
            monthArrayAdapter.setSelectedItem(newValue);
            month = newValue + 1;
            wheel.invalidateWheel(false);
            if (onPickerDateTimeListener != null) {
                onPickerDateTimeListener.onPickerDateAndTime(year, month, day, hour, minute);
            }
            if (onPickerDateListener != null) {
                onPickerDateListener.onPickerDate(year, month, day);
            }
            mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
        }
    };

    OnWheelChangedListener dayWheelListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // 在这里响应滚动选择事件
            dayNumericAdapter.setSelectedItem(newValue);
            wheel.invalidateWheel(false);
            day = newValue + 1;
            if (onPickerDateTimeListener != null) {
                onPickerDateTimeListener.onPickerDateAndTime(year, month, day, hour, minute);
            }
            if (onPickerDateListener != null) {
                onPickerDateListener.onPickerDate(year, month, day);
            }
            mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
        }
    };

    OnWheelChangedListener hourWheelListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // 在这里响应滚动选择事件
            hourArrayAdapter.setSelectedItem(newValue);
            wheel.invalidateWheel(false);
            hour = newValue;
            if (onPickerDateTimeListener != null) {
                onPickerDateTimeListener.onPickerDateAndTime(year, month, day, hour, minute);
            }
            if (onPickerTimeListener != null) {
                onPickerTimeListener.onPickerTime(hour, minute);
            }
            mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
        }
    };

    OnWheelChangedListener minuteWheelListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            // 在这里响应滚动选择事件
            minuteArrayAdapter.setSelectedItem(newValue);
            minute = newValue;
            if (onPickerDateTimeListener != null) {
                onPickerDateTimeListener.onPickerDateAndTime(year, month, day, hour, minute);
            }
            if (onPickerTimeListener != null) {
                onPickerTimeListener.onPickerTime(hour, minute);
            }
            wheel.invalidateWheel(false);
            mHandler.sendEmptyMessage(MSG_SOUND_EFFECT);
        }
    };
    private WheelView monthWheel;
    private WheelView yearWheel;
    private WheelView dayWheel;
    private WheelView hourWheel;
    private WheelView minuteWheel;


    public DTPicker(Context context) {
        this(context, null);
    }

    public DTPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DTPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DTPicker);
        itemHeight = (int) array.getDimension(R.styleable.DTPicker_item_height,
                CommonUtil.dp2px(mContext, 24));
        visibleItems = array.getInt(R.styleable.DTPicker_visible_items, -1);
        array.recycle();
    }

    public void setType(int type) {
        this.type = type;
        if (type == 2) {
            playedCount = 2;
        } else if (type == 1) {
            playedCount = 3;
        } else {
            playedCount = 5;
        }
    }

    protected void init() {
        dm = getResources().getDisplayMetrics();
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(mContext, R.raw.time_picker, 1);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.date_time_picker, this, true);
        Calendar calendar = Calendar.getInstance();

        if (type == 2) {
            findViewById(R.id.year_layout).setVisibility(View.GONE);
            findViewById(R.id.month_layout).setVisibility(View.GONE);
            findViewById(R.id.day_layout).setVisibility(View.GONE);

            hourWheel = (WheelView) findViewById(R.id.hour);
            minuteWheel = (WheelView) findViewById(R.id.minute);

            hourWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            minuteWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            hourWheel.setWheelBackground(R.color.colorWhite);
            minuteWheel.setWheelBackground(R.color.colorWhite);

            hourWheel.setBackgroundColor(Color.WHITE);
            minuteWheel.setBackgroundColor(Color.WHITE);

            // hour
            int curHour = calendar.get(Calendar.HOUR_OF_DAY);
            hour = curHour;
            hourArrayAdapter = new DateNumericAdapter(mContext, 0, 23, curHour);
            hourWheel.setViewAdapter(hourArrayAdapter);
            hourWheel.setCurrentItem(curHour);
            hourWheel.addChangingListener(hourWheelListener);

            // minute
            int curMinute = calendar.get(Calendar.MINUTE);
            minute = curMinute;
            minuteArrayAdapter = new DateNumericAdapter(mContext, 0, 59, curMinute);
            minuteWheel.setViewAdapter(minuteArrayAdapter);
            minuteWheel.setCurrentItem(curMinute);
            minuteWheel.addChangingListener(minuteWheelListener);
        } else if (type == 1) {
            findViewById(R.id.hour_layout).setVisibility(View.GONE);
            findViewById(R.id.minute_layout).setVisibility(View.GONE);

            monthWheel = (WheelView) findViewById(R.id.month);
            yearWheel = (WheelView) findViewById(R.id.year);
            dayWheel = (WheelView) findViewById(R.id.day);

            monthWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            yearWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            dayWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            monthWheel.setWheelBackground(R.color.colorWhite);
            yearWheel.setWheelBackground(R.color.colorWhite);
            dayWheel.setWheelBackground(R.color.colorWhite);

            monthWheel.setBackgroundColor(Color.WHITE);
            yearWheel.setBackgroundColor(Color.WHITE);
            dayWheel.setBackgroundColor(Color.WHITE);

            OnWheelChangedListener listener = new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    updateDays(yearWheel, monthWheel, dayWheel);
                }
            };

            // year
            int curYear = calendar.get(Calendar.YEAR);
            year = curYear;
            if (startYear == 0) {
                startYear = curYear;
            }
            yearNumericAdapter = new DateNumericAdapter(mContext, curYear, curYear + 10, 0);
            yearWheel.setViewAdapter(yearNumericAdapter);
            yearWheel.setCurrentItem(curYear);
            yearWheel.addChangingListener(listener);
            yearWheel.addChangingListener(yearWheelListener);

            // month
            int curMonth = calendar.get(Calendar.MONTH);
            month = curMonth + 1;
            monthArrayAdapter = new MonthDateNumericAdapter(mContext, 1, 12, curMonth);
            monthWheel.setViewAdapter(monthArrayAdapter);
            monthWheel.setCurrentItem(curMonth);
            monthWheel.addChangingListener(listener);
            monthWheel.addChangingListener(monthWheelListener);

            // day
            updateDays(monthWheel, monthWheel, dayWheel);
            dayWheel.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        } else {
            monthWheel = (WheelView) findViewById(R.id.month);
            yearWheel = (WheelView) findViewById(R.id.year);
            dayWheel = (WheelView) findViewById(R.id.day);
            hourWheel = (WheelView) findViewById(R.id.hour);
            minuteWheel = (WheelView) findViewById(R.id.minute);

            monthWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            yearWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            dayWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            hourWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            minuteWheel.setShadowColor(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
            monthWheel.setWheelBackground(R.color.colorWhite);
            yearWheel.setWheelBackground(R.color.colorWhite);
            dayWheel.setWheelBackground(R.color.colorWhite);
            hourWheel.setWheelBackground(R.color.colorWhite);
            minuteWheel.setWheelBackground(R.color.colorWhite);
            monthWheel.setBackgroundColor(Color.WHITE);
            yearWheel.setBackgroundColor(Color.WHITE);
            dayWheel.setBackgroundColor(Color.WHITE);
            hourWheel.setBackgroundColor(Color.WHITE);
            minuteWheel.setBackgroundColor(Color.WHITE);

            yearWheel.setCenterRectOffset((int) dm.density);
            monthWheel.setCenterRectOffset((int) dm.density);
            dayWheel.setCenterRectOffset((int) dm.density);
            hourWheel.setCenterRectOffset((int) dm.density);
            minuteWheel.setCenterRectOffset((int) dm.density);

            OnWheelChangedListener listener = new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    updateDays(yearWheel, monthWheel, dayWheel);
                }
            };

            // year
            int curYear = calendar.get(Calendar.YEAR);
            year = curYear;
            if (startYear == 0) {
                startYear = curYear;
            }
            yearNumericAdapter = new DateNumericAdapter(mContext, curYear, curYear + 10, 0);
            yearWheel.setViewAdapter(yearNumericAdapter);
            yearWheel.setCurrentItem(curYear);
            yearWheel.addChangingListener(listener);
            yearWheel.addChangingListener(yearWheelListener);

            // month
            int curMonth = calendar.get(Calendar.MONTH);
            month = curMonth + 1;
            monthArrayAdapter = new MonthDateNumericAdapter(mContext, 1, 12, curMonth);
            monthWheel.setViewAdapter(monthArrayAdapter);
            monthWheel.setCurrentItem(curMonth);
            monthWheel.addChangingListener(listener);
            monthWheel.addChangingListener(monthWheelListener);

            // day
            updateDays(monthWheel, monthWheel, dayWheel);
            dayWheel.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);

            // hour
            int curHour = calendar.get(Calendar.HOUR_OF_DAY);
            hour = curHour;
            hourArrayAdapter = new DateNumericAdapter(mContext, 0, 23, curHour);
            hourWheel.setViewAdapter(hourArrayAdapter);
            hourWheel.setCurrentItem(curHour);
            hourWheel.addChangingListener(hourWheelListener);

            // minute
            int curMinute = calendar.get(Calendar.MINUTE);
            minute = curMinute;
            minuteArrayAdapter = new DateNumericAdapter(mContext, 0, 59, curMinute);
            minuteWheel.setViewAdapter(minuteArrayAdapter);
            minuteWheel.setCurrentItem(curMinute);
            minuteWheel.addChangingListener(minuteWheelListener);
        }
        if (visibleItems != -1) {
            if (monthWheel != null) {
                monthWheel.setVisibleItems(visibleItems);
            }
            if (yearWheel != null) {
                yearWheel.setVisibleItems(visibleItems);
            }
            if (dayWheel != null) {
                dayWheel.setVisibleItems(visibleItems);
            }
            if (hourWheel != null) {
                hourWheel.setVisibleItems(visibleItems);
            }
            if (minuteWheel != null) {
                minuteWheel.setVisibleItems(visibleItems);
            }
        }
    }

    private class DateNumericAdapter extends NumericWheelAdapter {
        int currentItem;
        int currentValue;
        int selectedItem;

        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            this.selectedItem = current;
            setItemResource(R.layout.wheel_item_layout);
            setItemTextResource(R.id.wheel_item);
            setTextSize(18);
        }

        public void resetDateNumber(int minValue, int maxValue, int current) {
            super.minValue = minValue;
            super.maxValue = maxValue;
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            view.setTextSize(18);
            view.setTextColor(getResources().getColor(R.color.wheel_gray));
            if (selectedItem == currentItem) {
                view.setTextColor(getResources().getColor(R.color.colorBlack2));
                view.setTextSize(20);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            View view = super.getItem(index, cachedView, parent);
            view.getLayoutParams().height = itemHeight;
            return view;
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
        }
    }

    private class MonthDateNumericAdapter extends NumericWheelAdapter {
        int currentItem;
        int currentValue;
        int selectedItem;
        int currentMonth;

        public MonthDateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            this.selectedItem = current;
            currentMonth = Calendar.getInstance()
                    .get(Calendar.MONTH);
            setItemResource(R.layout.wheel_item_layout);
            setItemTextResource(R.id.wheel_item);
            setTextSize(18);
        }

        public void resetDateNumber(int minValue, int maxValue, int current) {
            super.minValue = minValue;
            super.maxValue = maxValue;
        }

        public int getUnDefinedIndex() {
            return currentMonth;
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            view.setTextSize(18);
            view.setTextColor(getResources().getColor(R.color.wheel_gray));
            if (selectedItem == currentItem) {
                view.setTextColor(getResources().getColor(R.color.colorBlack2));
                view.setTextSize(20);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            View view = super.getItem(index, cachedView, parent);
            view.getLayoutParams().height = itemHeight;
            return view;
        }

        public void setSelectedItem(int selectedItem) {
            this.selectedItem = selectedItem;
        }

        @Override
        public CharSequence getItemText(int index) {
            if (index >= 0 && index < getItemsCount()) {
                int value = minValue + index;
                String valueStr = null;
                if (!isMonthUnDefined) {
                    valueStr = Integer.toString(value);
                } else {
                    if (currentMonth == index) {
                        //前一个月
                        valueStr = "待定";
                    } else {
                        valueStr = Integer.toString(value - (index > currentMonth ? 1 : 0));
                    }
                }
                return valueStr;
            }
            return null;
        }

        @Override
        public int getItemsCount() {
            return super.getItemsCount() + (isMonthUnDefined ? 1 : 0);
        }
    }

    void updateDays(WheelView year, WheelView month, WheelView dayWheel) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear + year.getCurrentItem());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int currentItem = month.getCurrentItem();

        calendar.set(Calendar.MONTH, currentItem);
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(monthArrayAdapter!=null&&isMonthUnDefined){
            int unDefinedIndex=monthArrayAdapter.getUnDefinedIndex();
            if (currentItem == unDefinedIndex) {
                maxDays = 0;
            }else if(currentItem>unDefinedIndex-1){
                currentItem--;
                calendar.set(Calendar.MONTH, currentItem);
                maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        }

        if (dayNumericAdapter == null) {
            dayNumericAdapter = new DateNumericAdapter(mContext,
                    1,
                    maxDays,
                    calendar.get(Calendar.DAY_OF_MONTH) - 1);
            dayWheel.addChangingListener(dayWheelListener);
        } else {
            dayNumericAdapter.resetDateNumber(1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        dayWheel.setViewAdapter(dayNumericAdapter);
        int curDay = Math.min(maxDays, dayWheel.getCurrentItem() + 1);
        day = curDay;
        dayWheel.setCurrentItem(curDay - 1, true);
    }

    public void updateDays() {
        updateDays(yearWheel, monthWheel, dayWheel);
    }

    public void setCurrentMonth() {
        if (monthArrayAdapter != null && isMonthUnDefined) {
            int currentMonth = Calendar.getInstance()
                    .get(Calendar.MONTH);
            monthWheel.setCurrentItem(currentMonth + 1);
        }
    }

    private void playSoundEffect() {
        if (playedCount > 0) {
            playedCount--;
            return;
        }
        sp.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public interface OnPickerDateTimeListener {
        public void onPickerDateAndTime(int year, int month, int day, int hour, int minute);
    }

    public interface OnPickerDateListener {
        public void onPickerDate(int year, int month, int day);
    }

    public interface OnPickerTimeListener {
        public void onPickerTime(int hour, int minute);
    }

    public void setOnPickerDateTimeListener(OnPickerDateTimeListener onPickerDateTimeListener) {
        this.onPickerDateTimeListener = onPickerDateTimeListener;
    }

    public void setOnPickerTimeListener(OnPickerTimeListener onPickerTimeListener) {
        this.onPickerTimeListener = onPickerTimeListener;
    }

    public void setOnPickerDateListener(OnPickerDateListener onPickerDateListener) {
        this.onPickerDateListener = onPickerDateListener;
    }

    public void setYearLimit(int startYear, int maxLimit) {
        this.startYear = startYear;
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        if (type == 0 || type == 1) {
            year = curYear;
            startYear = startYear == 0 ? curYear : startYear;
            maxLimit = maxLimit < 0 ? 10 : maxLimit;
            if (startYear > year || year < startYear + maxLimit) {
                year = startYear;
            }
            if (yearNumericAdapter == null) {
                yearNumericAdapter = new DateNumericAdapter(mContext,
                        startYear,
                        startYear + maxLimit,
                        0);
                yearWheel.setViewAdapter(yearNumericAdapter);
            } else {
                yearNumericAdapter.resetDateNumber(startYear, startYear + maxLimit, 0);
            }
            yearWheel.setCurrentItem(Math.max(curYear - startYear, 0));
        }
    }

    public void setCurrentCalender(Calendar currentCalender) {
        setCurrentDateCalender(currentCalender);
        setCurrentTimeCalender(currentCalender);
    }

    public void setCurrentDateCalender(Calendar currentCalender) {
        int curYear = currentCalender.get(Calendar.YEAR);
        int curMonth = currentCalender.get(Calendar.MONTH);
        int curDay = currentCalender.get(Calendar.DAY_OF_MONTH);
        if (monthArrayAdapter != null && isMonthUnDefined) {
            if (curMonth >= monthArrayAdapter.getUnDefinedIndex()) {
                curMonth++;
            }
        }
        if (type == 0 || type == 1) {
            yearWheel.setCurrentItem(Math.max(curYear - startYear, 0));
            year = startYear + yearWheel.getCurrentItem();
            monthWheel.setCurrentItem(curMonth);
            month = monthWheel.getCurrentItem() + 1;
            dayWheel.setCurrentItem(curDay - 1);
            updateDays(yearWheel, monthWheel, dayWheel);
        }
    }

    public void setCurrentTimeCalender(Calendar currentCalender) {
        int curHour = currentCalender.get(Calendar.HOUR_OF_DAY);
        int curMinute = currentCalender.get(Calendar.MINUTE);
        if (type == 0 || type == 2) {
            hourWheel.setCurrentItem(curHour);
            minuteWheel.setCurrentItem(curMinute);
        }
    }

    public void hideLabels() {
        findViewById(R.id.year_label).setVisibility(GONE);
        findViewById(R.id.month_label).setVisibility(GONE);
        findViewById(R.id.day_label).setVisibility(GONE);
        findViewById(R.id.hour_label).setVisibility(GONE);
        findViewById(R.id.minute_label).setVisibility(GONE);
    }

    public void setMonthUnDefined(boolean monthUnDefined) {
        isMonthUnDefined = monthUnDefined;
    }

    public boolean isMonthUnDefined() {
        return isMonthUnDefined;
    }
}
