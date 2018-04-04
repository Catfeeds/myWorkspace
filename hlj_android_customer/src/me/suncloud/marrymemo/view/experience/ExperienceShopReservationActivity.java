package me.suncloud.marrymemo.view.experience;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.entities.HljResultAction;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.model.experience.ExperienceReservationBody;
import me.suncloud.marrymemo.model.experience.Store;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import rx.Observable;

/**
 * 体验店入口首页的预约到店
 * Created by jinxin on 2016/10/27.
 */

public class ExperienceShopReservationActivity extends HljBaseActivity implements View
        .OnFocusChangeListener, View.OnTouchListener, DTPicker.OnPickerDateTimeListener {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Dialog dialog;
    private Calendar tempCalendar;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private HljHttpSubscriber reservationSubcriber;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        store = getIntent().getParcelableExtra("store");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_reservation);
        ButterKnife.bind(this);
        User user = Session.getInstance()
                .getCurrentUser(this);
        if (user != null && user.getId() > 0) {
            etPhone.setText(user.getPhone());
        }
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type7));
        etTime.setOnFocusChangeListener(this);
        etTime.setOnTouchListener(this);
        etTime.clearFocus();
    }

    public void onSubmit(View view) {
        String name = etName.getText()
                .toString();
        String phone = etPhone.getText()
                .toString();
        if (JSONUtil.isEmpty(name) || JSONUtil.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.label_reserve_hint), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!Util.isMobileNO(phone)) {
            Toast.makeText(this, getString(R.string.hint_new_number_error), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        reservationSubcriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<HljResultAction>>() {
                    @Override
                    public void onNext(HljHttpResult<HljResultAction> action) {
                        if (action != null) {
                            HljHttpStatus status = action.getStatus();
                            if (status != null) {
                                if (status.getRetCode() == 0) {
                                    //成功
                                    Intent intent = new Intent(ExperienceShopReservationActivity
                                            .this, ExperienceShopReservationSucceedActivity.class);
                                    intent.putExtra("store", store);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.activity_anim_default);
                                    finish();
                                } else {
                                    Toast.makeText(ExperienceShopReservationActivity
                                            .this, status.getMsg(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                })
                .build();

        ExperienceReservationBody body = new ExperienceReservationBody();
        body.setStoreId(store.getStoreId());
        body.setName(name);
        body.setMobile(phone);
        Observable<HljHttpResult<HljResultAction>> observable = ExperienceApi.resveration(body);
        observable.subscribe(reservationSubcriber);
    }

    @Override
    protected void onFinish() {
        if (reservationSubcriber != null && !reservationSubcriber.isUnsubscribed()) {
            reservationSubcriber.unsubscribe();
        }
        super.onFinish();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showDatetimePicker(v, null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showDatetimePicker(v, event);
        }
        return true;
    }

    @Override
    public void onPickerDateAndTime(
            int year, int month, int day, int hour, int minute) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day, hour, minute);
        } else {
            tempCalendar.set(year, month - 1, day, hour, minute);
        }
    }

    public void showDatetimePicker(View view, MotionEvent event) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
            v.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            v.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar nowCalendar = Calendar.getInstance();
                            if (tempCalendar != null && tempCalendar.before(nowCalendar)) {
                                Toast.makeText(ExperienceShopReservationActivity.this,
                                        getString(R.string.msg_wrong_time2),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            } else {
                                dialog.dismiss();
                                if (tempCalendar != null) {
                                    calendar.setTime(tempCalendar.getTime());
                                }
                                setSwipeBackEnable(false);
                                etTime.setText(simpleDateFormat.format(calendar.getTime()));
                            }
                        }
                    });
            DatePickerView picker = (DatePickerView) v.findViewById(R.id.picker);
            picker.setYearLimit(2000, 49);
            picker.setCurrentCalender(calendar);
            picker.setOnPickerDateTimeListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            dialog.setContentView(v);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        dialog.show();
    }
}
