package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarSubPageActivity;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.car.CarApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import rx.Subscription;

/**
 * Created by wangtao on 2017/4/15.
 */

public class WeddingCarEntryActivity extends HljBaseActivity implements DTPicker
        .OnPickerDateListener {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_date)
    TextView tvDate;

    private City city;
    private DateTime time;
    private DateTime tempTime;
    private boolean timeNull;
    private Dialog datePickerDlg;
    private int currentMonth;
    private Subscription postSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_car_entry);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    private void initData() {
        city = (City) getIntent().getSerializableExtra("city");
        String timeString = getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).getString("wedding_car_time", null);
        if (!TextUtils.isEmpty(timeString)) {
            if (timeString.equals("null")) {
                timeNull = true;
            } else {
                time = new DateTime(timeString);
            }
        }
        if (city == null || city.getId() == 0) {
            city = Session.getInstance()
                    .getMyCity(this);
        }
        if (city.getId() == 0) {
            city = null;
        }
        if (time == null && !timeNull) {
            User user = Session.getInstance()
                    .getCurrentUser(this);
            if (user != null && user.getWeddingDay() != null) {
                time = new DateTime(user.getWeddingDay());
            }
        }
    }

    private void initView() {
        tvCity.setText(city != null ? city.getName() : "请选择");
        if (timeNull) {
            tvDate.setText("待定");
        } else {
            tvDate.setText(time != null ? time.toString("yyyy-MM-dd") : "请选择");
        }
    }

    @OnClick(R.id.city_layout)
    public void onCityClicked() {
        Intent intent = new Intent(this, CityListActivity.class);
        intent.putExtra("resultCity", true);
        intent.putExtra("nonNull", true);
        intent.putExtra("city", city);
        intent.putExtra("hot_city_type", CityListActivity.HotCityType.CAR);
        startActivityForResult(intent, Constants.RequestCode.CITY_CHANGE);
        overridePendingTransition(R.anim.slide_in_up_to_top, R.anim.activity_anim_default);
    }

    @OnClick(R.id.date_layout)
    public void onDateClicked() {
        if (datePickerDlg != null && datePickerDlg.isShowing()) {
            return;
        }
        if (datePickerDlg == null) {
            datePickerDlg = new Dialog(this, R.style.BubbleDialogTheme);
            datePickerDlg.setContentView(R.layout.dialog_date_picker);
            datePickerDlg.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datePickerDlg.dismiss();
                        }
                    });
            datePickerDlg.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datePickerDlg.dismiss();
                            if (tempTime == null) {
                                timeNull = true;
                                tvDate.setText("待定");
                            } else {
                                timeNull = false;
                                time = tempTime;
                                tvDate.setText(time.toString("yyyy-MM-dd"));
                            }
                        }
                    });
            DatePickerView picker = (DatePickerView) datePickerDlg.findViewById(R.id.picker);
            Calendar calendar = Calendar.getInstance();
            currentMonth = calendar.get(Calendar.MONTH);
            picker.setYearLimit(calendar.get(Calendar.YEAR), 10);
            picker.setMonthUnDefined(true);
            if (time != null) {
                calendar.setTime(time.toDate());
                picker.setCurrentCalender(calendar);
                tempTime = new DateTime(calendar.getTime());
            } else if (!timeNull) {
                tempTime = new DateTime(calendar.getTime());
                picker.setCurrentCalender(calendar);
            }
            picker.setOnPickerDateListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));

            Window win = datePickerDlg.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                Point point = JSONUtil.getDeviceSize(this);
                params.width = point.x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        datePickerDlg.show();
    }

    @OnClick(R.id.btn_confirm)
    public void onBtnConfirmClicked() {
        if (city == null || city.getId() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_city_empty_car);
            return;
        }
        if (time == null && !timeNull) {
            ToastUtil.showToast(this, null, R.string.hint_date_empty_car);
            return;
        }
        String timeStr = timeNull ? null : tvDate.getText()
                .toString();
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (postSubscription == null || postSubscription.isUnsubscribed()) {
            postSubscription = CarApi.postCarEntryData(this,
                    city,
                    timeStr,
                    user != null ? user.getId() : null)
                    .subscribe(HljHttpSubscriber.buildSubscriber(this)
                            .build());
        }
        DataConfig config = Session.getInstance()
                .getDataConfig(this);
        if (config == null || !config.isSupportCar(city.getId())) {
            DialogUtil.createSingleButtonDialog(this,
                    getString(R.string.msg_city_unsupported_car),
                    null,
                    null)
                    .show();
            return;
        }
        Intent intent = new Intent(this, WeddingCarSubPageActivity.class);
        intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_ID, city.getId());
        intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_NAME, city.getName());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.CITY_CHANGE:
                    City city = (City) data.getSerializableExtra("city");
                    if (city != null) {
                        this.city = city;
                        tvCity.setText(city.getName());
                    }
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPickerDate(int year, int month, int day) {
        if (currentMonth + 1 != month) {
            if (month > currentMonth) {
                month = month - 1;
            }
            tempTime = new DateTime(year, month, day, 0, 0);
        } else {
            //待定
            tempTime = null;
        }
    }
}
