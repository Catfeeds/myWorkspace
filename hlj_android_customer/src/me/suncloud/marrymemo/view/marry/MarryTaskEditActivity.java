package me.suncloud.marrymemo.view.marry;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.widget.ExpandableLayout;
import me.suncloud.marrymemo.widget.WheelView;

/**
 * Created by hua_rong on 2017/11/6 结婚任务 记事项
 */

public class MarryTaskEditActivity extends HljBaseNoBarActivity {

    @Override
    public String pageTrackTagName() {
        return "记笔账";
    }

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.check_box)
    CheckBox checkBox;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_dead_date)
    RelativeLayout rlDeadDate;
    @BindView(R.id.wv_day)
    WheelView wvDay;
    @BindView(R.id.wv_hour)
    WheelView wvHour;
    @BindView(R.id.wv_minute)
    WheelView wvMinute;
    @BindView(R.id.expandable_layout_time)
    ExpandableLayout expandableLayoutTime;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minuteList;
    private String[] weeks;
    private static final int TOTAL_DAY = 365;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber saveSubscriber;
    private String expireAt;
    private long id;
    private HashMap<String, String> timeHashMap;
    private MarryTask marryTask;
    public static final String ARG_MARRY_TASK = "marry_task";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_marry_task);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        weeks = getResources().getStringArray(R.array.week);
        initValue();
        initView();
    }

    private void initValue() {
        timeHashMap = new HashMap<>();
        dayList = getDayList();
        hourList = getHourList();
        minuteList = getMinuteList();
        marryTask = getIntent().getParcelableExtra(ARG_MARRY_TASK);
        wvDay.setCurrentItem(0);
        if (marryTask != null) {
            etTitle.setText(marryTask.getTitle());
            etTitle.setSelection(etTitle.length());
            checkBox.setChecked(marryTask.getToTa() != 0);
            id = marryTask.getId();
            DateTime dateTime = marryTask.getExpireAt();
            if (dateTime != null) {
                tvTime.setText(dateTime.toString(Constants.DATE_FORMAT_LONG2));
                if (dateTime.getMillis() > System.currentTimeMillis()) {
                    Calendar calendar = dateTime.toCalendar(Locale.getDefault());
                    if (calendar != null) {
                        String month = String.format(Locale.getDefault(),
                                "%02d",
                                calendar.get(Calendar.MONTH) + 1);
                        String day = String.format(Locale.getDefault(),
                                "%02d",
                                calendar.get(Calendar.DAY_OF_MONTH));
                        String week = getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1);
                        String time = getString(R.string.label_dead_date, month, day, week);
                        String hour = String.format(Locale.getDefault(),
                                "%02d",
                                calendar.get(Calendar.HOUR_OF_DAY));
                        String minute = String.format(Locale.getDefault(),
                                "%02d",
                                calendar.get(Calendar.MINUTE));
                        wvDay.setCurrentItem(dayList.indexOf(time));
                        wvHour.setCurrentItem(hourList.indexOf(hour));
                        wvMinute.setCurrentItem(minuteList.indexOf(minute));
                    }
                }
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void initView() {
        llDelete.setVisibility(marryTask == null ? View.GONE : View.VISIBLE);
        wvDay.setCyclic(false);
        wvHour.setCyclic(true);
        wvMinute.setCyclic(true);
        initWheelViewStyle(wvDay, dayList);
        initWheelViewStyle(wvHour, hourList);
        initWheelViewStyle(wvMinute, minuteList);
        onWvSelectListener(wvDay, dayList);
        onWvSelectListener(wvHour, hourList);
        onWvSelectListener(wvMinute, minuteList);
    }


    @OnClick(R.id.btn_cancel)
    void onCancel(View view) {
        onBackPressed();
    }

    @OnClick(R.id.tv_delete)
    void onDelete(View view) {
        String title = etTitle.getText()
                .toString()
                .trim();
        if (id == 0) {
            return;
        }
        CommonUtil.unSubscribeSubs(deleteSubscriber);
        deleteSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .setProgressBar(progressBar)
                .build();
        MarryApi.updateTask(1, expireAt, id, title, checkBox.isChecked() ? 1 : 0)
                .subscribe(deleteSubscriber);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        CommonUtil.unSubscribeSubs(saveSubscriber);
        String title = etTitle.getText()
                .toString()
                .trim();
        String time = tvTime.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.showToast(this, "请写一个标题", 0);
            return;
        }
        if (!TextUtils.isEmpty(time)) {
            expireAt = tvTime.getText()
                    .toString()
                    .trim() + ":00";
        }
        saveSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .setProgressBar(progressBar)
                .build();
        MarryApi.updateTask(null, expireAt, id, title, checkBox.isChecked() ? 1 : 0)
                .subscribe(saveSubscriber);
    }


    @OnClick(R.id.rl_dead_date)
    void onDeadDate() {
        hideKeyboard();
        if (expandableLayoutTime.isExpanded()) {
            expandableLayoutTime.collapse();
            ivArrow.setImageResource(R.mipmap.icon_arrow_down_gray_26_14);
        } else {
            expandableLayoutTime.expand();
            ivArrow.setImageResource(R.mipmap.icon_arrow_up_gray_26_14);
        }
    }

    public void hideKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            if (imm != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private void onWvSelectListener(WheelView wheelView, final List<String> list) {
        if (wheelView != null && list != null) {
            wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    if (index >= 0 && index < list.size()) {
                        String dayItem = dayList.get(wvDay.getCurrentItem());
                        String hourItem = hourList.get(wvHour.getCurrentItem());
                        String minuteItem = minuteList.get(wvMinute.getCurrentItem());
                        tvTime.setText(String.format(Locale.getDefault(),
                                "%1$s  %2$s:%3$s",
                                timeHashMap.get(dayItem),
                                hourItem,
                                minuteItem));
                    }
                }
            });
        }
    }

    private void initWheelViewStyle(WheelView wheelView, final List<String> list) {
        if (wheelView != null && list != null) {
            wheelView.setLineSpacingMultiplier(1.8f);
            wheelView.setAdapter(new WheelView.WheelAdapter() {
                @Override
                public int getItemsCount() {
                    return list.size();
                }

                @Override
                public Object getItem(int index) {
                    if (index >= 0 && index < list.size() && list.get(index) != null) {
                        return list.get(index);
                    } else {
                        return "";
                    }
                }

                @Override
                public int indexOf(Object o) {
                    return list.indexOf(o);
                }
            });
        }
    }


    private List<String> getMinuteList() {
        List<String> minuteList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            minuteList.add(String.format(Locale.getDefault(), "%02d", i * 5));
        }
        return minuteList;
    }

    private List<String> getHourList() {
        List<String> hourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        return hourList;
    }

    private List<String> getDayList() {
        String time;
        List<String> dayList = new ArrayList<>();
        for (int i = 0; i < TOTAL_DAY; i++) {
            Calendar calendar = Calendar.getInstance();
            if (i == 0) {
                time = "今天";
            } else {
                calendar.add(Calendar.DATE, +i);
                String month = String.format(Locale.getDefault(),
                        "%02d",
                        calendar.get(Calendar.MONTH) + 1);
                String day = String.format(Locale.getDefault(),
                        "%02d",
                        calendar.get(Calendar.DAY_OF_MONTH));
                String week = getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1);
                time = getString(R.string.label_dead_date, month, day, week);
            }
            String date = getDefaultDate(calendar.getTimeInMillis());
            timeHashMap.put(time, date);
            dayList.add(time);
        }
        return dayList;
    }

    /**
     * @param milliseconds 毫秒数
     * @return 简单的默认格式日期时间
     */
    public static String getDefaultDate(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }


    private String getWeek(int week) {
        if (week <= 0) {
            return weeks[0];
        } else {
            return weeks[week];
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(deleteSubscriber, saveSubscriber);
    }
}
