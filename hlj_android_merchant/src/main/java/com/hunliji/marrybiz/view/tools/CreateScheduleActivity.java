package com.hunliji.marrybiz.view.tools;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.tools.ToolsApi;
import com.hunliji.marrybiz.fragment.DatePickerFragment;
import com.hunliji.marrybiz.fragment.TimePickerFragment;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.model.tools.Schedule;
import com.hunliji.marrybiz.view.NewOrderDetailActivity;
import com.umeng.analytics.MobclickAgent;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 新建档期
 */
@RuntimePermissions
public class CreateScheduleActivity extends HljBaseActivity {
    @BindView(R.id.date_layout)
    LinearLayout dateLayout;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.time_layout)
    LinearLayout timeLayout;
    @BindView(R.id.time_flow_layout)
    FlowLayout timeFlowLayout;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_select_contact)
    ImageButton btnSelectContact;
    @BindView(R.id.property_layout)
    LinearLayout propertyLayout;
    @BindView(R.id.type_layout)
    LinearLayout typeLayout;
    @BindView(R.id.type_flow_layout)
    FlowLayout typeFlowLayout;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.et_pay_price)
    EditText etPayPrice;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.edit_layout)
    LinearLayout editLayout;
    private Dialog exitDialog;
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private ArrayList<CheckBox> timeChildViews;
    private Schedule schedule;
    private Calendar tempCalendar;
    private String[] times;
    private String[] types;
    private String oldDate = "";
    private String oldTime = "";
    private String oldTimeStr = "";
    private String oldUserName = "";
    private String oldPhone = "";
    private String oldActivityType = "";
    private String oldAddress = "";
    private String oldPrice = "";
    private String oldPayPrice = "";
    private String oldMessage = "";
    private boolean isEdit;
    private int type;
    private int property; //1 四大金刚+婚礼策划 0 其他商家
    private InputMethodManager imm;
    private Subscription getContactSub;
    private HljHttpSubscriber postSub;
    private HljHttpSubscriber deleteSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        ButterKnife.bind(this);
        initValues();
        initViews();
    }

    private void initValues() {
        timeChildViews = new ArrayList<>();
        tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.SECOND, 0);
        type = getIntent().getIntExtra("type", 0);
        property = getIntent().getIntExtra("property", 0);
        schedule = getIntent().getParcelableExtra("schedule");
        if (schedule == null) {
            schedule = new Schedule();
        }
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        times = new String[]{getString(R.string.label_morning), getString(R.string
                .label_luncheon), getString(
                R.string.label_afternoon), getString(R.string.label_dinner), getString(R.string
                .label_night)};
        types = new String[]{getString(R.string.label_wedding_feast), getString(R.string
                .label_birthday_feast), getString(
                R.string.label_baby_feast), getString(R.string.label_dress_rehearsal), getString
                (R.string.label_activity)};
    }

    private void initViews() {
        for (String str : times) {
            View.inflate(this, R.layout.schedule_flow_item, timeFlowLayout);
            CheckBox checkBox = (CheckBox) timeFlowLayout.getChildAt(timeFlowLayout.getChildCount
                    () - 1);
            checkBox.setText(str);
            timeChildViews.add(checkBox);
        }
        for (String str : types) {
            CheckBox checkBox = (CheckBox) View.inflate(this, R.layout.schedule_flow_item, null);
            typeFlowLayout.addView2(checkBox,
                    new ViewGroup.LayoutParams(CommonUtil.dp2px(this, 60),
                            CommonUtil.dp2px(this, 30)));
            checkBox.setText(str);
        }
        typeFlowLayout.setOnChildCheckedChangeListener(new FlowLayout
                .OnChildCheckedChangeListener() {
            @Override
            public void onCheckedChange(View childView, int index) {
                CheckBox checkBox = (CheckBox) childView;
                schedule.setActivityType(!checkBox.isChecked() ? null : String.valueOf(index + 1));
            }
        });
        if (type == Schedule.TYPE_RESERVATION || property == 0) {
            timeFlowLayout.setVisibility(View.GONE);
            tvTime.setVisibility(View.VISIBLE);
            typeLayout.setVisibility(View.GONE);
            if (type == Schedule.TYPE_RESERVATION) {
                propertyLayout.setVisibility(View.GONE);
            }
        } else {
            timeLayout.setClickable(false);
        }
        if (!isDetail()) {
            setOkText(R.string.label_save);
            if (isEdit) {
                setTitle(type == Schedule.TYPE_RESERVATION ? R.string
                        .title_activity_update_reservation : R.string
                        .title_activity_update_schedule);
            } else {
                setTitle(type == Schedule.TYPE_RESERVATION ? R.string
                        .title_activity_create_reservation : R.string
                        .title_activity_create_schedule);
                schedule.setDate(getIntent().getStringExtra("date"));
                tvDate.setText(schedule.getDate()
                        .toString(getString(R.string.format_date_type14)));
                oldDate = tvDate.getText()
                        .toString();
            }
        }
        if (type == Schedule.TYPE_ORDER || isDetail()) {
            etUserName.setEnabled(false);
            findViewById(R.id.tv_star3).setVisibility(View.INVISIBLE);
            etPhone.setEnabled(false);
            btnSelectContact.setVisibility(View.GONE);
            findViewById(R.id.tv_star4).setVisibility(View.INVISIBLE);
            etPrice.setEnabled(false);
            etPayPrice.setEnabled(false);
            if (isDetail()) {
                if (type == Schedule.TYPE_ORDER) {
                    setOkText(R.string.label_view_orders);
                }
                setTitle(type == Schedule.TYPE_RESERVATION ? R.string
                        .title_activity_reservation_detail : R.string
                        .title_activity_schedule_detail);
                dateLayout.setClickable(false);
                tvDate.setCompoundDrawables(null, null, null, null);
                findViewById(R.id.tv_star).setVisibility(View.INVISIBLE);
                timeLayout.setClickable(false);
                timeFlowLayout.setVisibility(View.GONE);
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setCompoundDrawables(null, null, null, null);
                findViewById(R.id.tv_star2).setVisibility(View.INVISIBLE);
                typeFlowLayout.setVisibility(View.GONE);
                tvType.setVisibility(View.VISIBLE);
                etAddress.setEnabled(false);
                etMessage.setEnabled(false);
                editLayout.setVisibility(View.VISIBLE);
            }
        }
        showScheduleDetail();
    }

    private void showScheduleDetail() {
        if (schedule == null || schedule.getId() == 0) {
            return;
        }
        //时间
        if (schedule.getDate() == null) {
            tvDate.setText("");
        } else {
            tvDate.setText(schedule.getDate()
                    .toString(getString(R.string.format_date_type14)));
            tempCalendar.set(Calendar.YEAR,
                    schedule.getDate()
                            .getYear());
            tempCalendar.set(Calendar.MONTH,
                    schedule.getDate()
                            .getMonthOfYear() - 1);
            tempCalendar.set(Calendar.DATE,
                    schedule.getDate()
                            .getDayOfMonth());
        }
        oldDate = tvDate.getText()
                .toString();
        if (type == Schedule.TYPE_RESERVATION || property == 0) {
            if (schedule.getTime() == null) {
                tvTime.setText("");
            } else {
                tvTime.setText(schedule.getTime()
                        .toString(getString(R.string.format_date_type13)));
                tempCalendar.set(Calendar.HOUR_OF_DAY,
                        schedule.getTime()
                                .getHourOfDay());
                tempCalendar.set(Calendar.MINUTE,
                        schedule.getTime()
                                .getMinuteOfHour());
            }
            oldTime = tvTime.getText()
                    .toString();
        } else {
            if (TextUtils.isEmpty(schedule.getTimeStr())) {
                tvTime.setText("");
            } else {
                StringBuilder sb = new StringBuilder();
                String[] strings = schedule.getTimeStr()
                        .split(",");
                for (int i = 0, size = strings.length; i < size; i++) {
                    if (strings[i].length() == 0) {
                        continue;
                    }
                    int index = Integer.valueOf(strings[i]) - 1;
                    if (index >= times.length) {
                        continue;
                    }
                    timeChildViews.get(index)
                            .setChecked(true);
                    sb.append(times[index]);
                    if (i != size - 1) {
                        sb.append(".");
                    }
                }
                tvTime.setText(sb.toString());
            }
            oldTimeStr = tvTime.getText()
                    .toString()
                    .replace(".", "");
            //类型
            if (typeLayout.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(schedule.getActivityType())) {
                    tvType.setText("");
                } else {
                    String activityType = schedule.getActivityType()
                            .replace(",", "");
                    int index = Integer.valueOf(activityType) - 1;
                    if (index < types.length) {
                        tvType.setText(types[index]);
                        typeFlowLayout.setCheckedChild(index);
                    }
                }
                oldActivityType = tvType.getText()
                        .toString();
            }
        }
        tvTime.setHint(timeLayout.isClickable() ? getString(R.string.hint_select_time) : "");
        String name;
        String phone;
        double price;
        double payPrice;
        //订单，buyerName和buyerPhone
        if (type == Schedule.TYPE_ORDER) {
            NewOrder order = schedule.getOrder();
            name = order.getBuyerRealName();
            phone = order.getBuyerPhone();
            price = order.getActualPrice();
            payPrice = order.getPaidMoney();
        } else {
            name = schedule.getUserName();
            phone = schedule.getPhone();
            price = schedule.getPrice();
            payPrice = schedule.getPayPrice();
        }
        //姓名
        etUserName.setHint(etUserName.isEnabled() ? getString(R.string.hint_enter_full_name) : "");
        etUserName.setText(name);
        etUserName.setSelection(etUserName.length());
        oldUserName = etUserName.getText()
                .toString();
        //电话
        etPhone.setHint(etPhone.isEnabled() ? getString(R.string.hint_enter_phone) : "");
        etPhone.setText(phone);
        oldPhone = etPhone.getText()
                .toString();
        //地点
        etAddress.setHint(etAddress.isEnabled() ? getString(R.string.hint_enter_address) : "");
        etAddress.setText(schedule.getAddress());
        oldAddress = etAddress.getText()
                .toString();
        //价格
        etPrice.setHint(etPrice.isEnabled() ? getString(R.string.hint_enter_price) : "");
        etPrice.setText(CommonUtil.formatDouble2String(price));
        oldPrice = etPrice.getText()
                .toString();
        //已付
        etPayPrice.setHint(etPayPrice.isEnabled() ? getString(R.string.hint_enter_paid) : "");
        etPayPrice.setText(CommonUtil.formatDouble2String(payPrice));
        oldPayPrice = etPayPrice.getText()
                .toString();
        //备注
        etMessage.setHint(etMessage.isEnabled() ? getString(R.string.hint_enter_remark) : "");
        etMessage.setText(schedule.getMessage());
        oldMessage = etMessage.getText()
                .toString();
    }

    @Override
    public void onOkButtonClick() {
        if (!isDetail()) {
            //判断日期是否为空
            if (tvDate.length() == 0) {
                ToastUtil.showToast(this, null, R.string.hint_select_date);
                return;
            }
            //除了订单外的多可以选择时间，只是选择时间的方式不同。
            boolean isSelectTime = false;
            if (timeFlowLayout.getVisibility() == View.VISIBLE) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = timeChildViews.size(); i < size; i++) {
                    if (timeChildViews.get(i)
                            .isChecked()) {
                        isSelectTime = true;
                        sb.append(",");
                        sb.append(i + 1);
                    }
                }
                if (isSelectTime) {
                    sb.append(",");
                }
                schedule.setTimeStr(sb.toString());
            } else {
                isSelectTime = tvTime.length() > 0;
            }
            if (!isSelectTime) {
                ToastUtil.showToast(this, null, R.string.hint_select_time);
                return;
            }
            if (etUserName.length() == 0) {
                ToastUtil.showToast(this, null, R.string.hint_enter_full_name);
                return;
            }
            if (etPhone.length() == 0) {
                ToastUtil.showToast(this, null, R.string.hint_enter_phone);
                return;
            }
            schedule.setUserName(etUserName.getText()
                    .toString());
            schedule.setPhone(etPhone.getText()
                    .toString());
            if (TextUtils.isEmpty(etPrice.getText())) {
                schedule.setPrice(0);
            } else {
                schedule.setPrice(Double.valueOf(etPrice.getText()
                        .toString()));
            }
            if (TextUtils.isEmpty(etPayPrice.getText())) {
                schedule.setPayPrice(0);
            } else {
                schedule.setPayPrice(Double.valueOf(etPayPrice.getText()
                        .toString()));
            }
            schedule.setAddress(etAddress.getText()
                    .toString());
            schedule.setMessage(etMessage.getText()
                    .toString());
            schedule.setType(type);
            CommonUtil.unSubscribeSubs(postSub);
            postSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<Schedule>() {
                        @Override
                        public void onNext(Schedule object) {
                            Intent intent = getIntent();
                            if (schedule.getId() == 0) {
                                ToastUtil.showCustomToast(CreateScheduleActivity.this,
                                        R.string.msg_create_success);
                                schedule.setId(object.getId());
                                intent.putExtra("action", "create");
                            } else {
                                ToastUtil.showCustomToast(CreateScheduleActivity.this,
                                        R.string.msg_update_success);
                                intent.putExtra("action", "update");
                            }
                            intent.putExtra("schedule", schedule);
                            setResult(RESULT_OK, intent);
                            CreateScheduleActivity.super.onBackPressed();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            if (schedule.getId() == 0) {
                ToolsApi.createScheduleObb(schedule)
                        .subscribe(postSub);
            } else {
                ToolsApi.updateScheduleObb(schedule)
                        .subscribe(postSub);
            }
        } else if (schedule.getOrder()
                .getId() > 0) {
            Intent intent = new Intent(this, NewOrderDetailActivity.class);
            intent.putExtra("id",
                    schedule.getOrder()
                            .getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    //选择日期
    @OnClick(R.id.date_layout)
    void onSelectDate() {
        try {
            tempCalendar.set(Calendar.YEAR,
                    schedule.getDate()
                            .getYear());
            tempCalendar.set(Calendar.MONTH,
                    schedule.getDate()
                            .getMonthOfYear() - 1);
            tempCalendar.set(Calendar.DATE,
                    schedule.getDate()
                            .getDayOfMonth());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    tempCalendar.set(i, i1, i2);
                    DateTime dateTime = new DateTime(tempCalendar.getTime());
                    if (schedule.getTime() != null) {
                        schedule.setTime(dateTime.toString(getString(R.string.format_date)));
                    }
                    schedule.setDate(dateTime.toString(getString(R.string.format_date_type8)));
                    tvDate.setText(dateTime.toString(getString(R.string.format_date_type14)));
                }
            });
        }
        datePickerFragment.setCalendar(tempCalendar);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    //选择时间
    @OnClick(R.id.time_layout)
    public void onSelectTime() {
        try {
            tempCalendar.set(Calendar.HOUR_OF_DAY,
                    schedule.getTime()
                            .getHourOfDay());
            tempCalendar.set(Calendar.MINUTE,
                    schedule.getTime()
                            .getMinuteOfHour());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timePickerFragment == null) {
            timePickerFragment = new TimePickerFragment();
            timePickerFragment.setTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    tempCalendar.set(Calendar.MINUTE, minute);
                    DateTime dateTime = new DateTime(tempCalendar.getTime());
                    schedule.setTime(dateTime.toString(getString(R.string.format_date)));
                    tvTime.setText(dateTime.toString(getString(R.string.format_date_type13)));
                }
            });
        }
        timePickerFragment.setCalendar(tempCalendar);
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    //选择联系人
    @OnClick(R.id.btn_select_contact)
    public void onSelectContact() {
        CreateScheduleActivityPermissionsDispatcher.onReadContactWithCheck(this);
    }

    //编辑
    @OnClick(R.id.btn_edit)
    public void onEdit() {
        Intent intent = getIntent();
        intent.setClass(this, CreateScheduleActivity.class);
        intent.putExtra("is_edit", true);
        intent.putExtra("schedule", schedule);
        startActivityForResult(intent, Constants.RequestCode.UPDATE_SCHEDULE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //删除
    @OnClick(R.id.btn_delete)
    public void onDelete() {
        DialogUtil.createDoubleButtonDialog(this,
                getString(R.string.hint_are_you_sure_to_delete),
                getString(R.string.action_ok),
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(deleteSub);
                        deleteSub = HljHttpSubscriber.buildSubscriber(CreateScheduleActivity
                                .this)
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(CreateScheduleActivity.this,
                                                R.string.msg_delete_success);
                                        Intent intent = getIntent();
                                        intent.putExtra("action", "delete");
                                        setResult(RESULT_OK, intent);
                                        CreateScheduleActivity.super.onBackPressed();
                                    }
                                })
                                .setProgressDialog(DialogUtil.createProgressDialog(
                                        CreateScheduleActivity.this))
                                .build();
                        ToolsApi.deleteScheduleObb(schedule.getId())
                                .subscribe(deleteSub);
                    }
                },
                null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (isDetail()) {
            super.onBackPressed();
            return;
        }
        String newTime = "";
        String newTimeStr = "";
        if (timeFlowLayout.getVisibility() == View.VISIBLE) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, size = timeChildViews.size(); i < size; i++) {
                if (timeChildViews.get(i)
                        .isChecked()) {
                    sb.append(times[i]);
                }
            }
            newTimeStr = sb.toString();
        } else {
            newTime = tvTime.getText()
                    .toString();
        }
        String newActivityType = "";
        if (typeLayout.getVisibility() == View.VISIBLE) {
            newActivityType = TextUtils.isEmpty(schedule.getActivityType()) ? "" : types[Integer
                    .valueOf(
                    schedule.getActivityType()) - 1];
        }
        if (TextUtils.equals(oldDate,
                tvDate.getText()
                        .toString()) && TextUtils.equals(oldTime, newTime) && TextUtils.equals(
                oldTimeStr,
                newTimeStr) && TextUtils.equals(oldUserName,
                etUserName.getText()
                        .toString()) && TextUtils.equals(oldPhone,
                etPhone.getText()
                        .toString()) && TextUtils.equals(oldActivityType,
                newActivityType) && TextUtils.equals(oldAddress,
                etAddress.getText()
                        .toString()) && TextUtils.equals(oldPrice,
                etPrice.getText()
                        .toString()) && TextUtils.equals(oldPayPrice,
                etPayPrice.getText()
                        .toString()) && TextUtils.equals(oldMessage,
                etMessage.getText()
                        .toString())) {
            super.onBackPressed();
        } else {
            if (exitDialog != null && exitDialog.isShowing()) {
                return;
            }
            if (exitDialog == null) {
                exitDialog = DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.label_exit_edit2),
                        null,
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CreateScheduleActivity.super.onBackPressed();
                            }
                        },
                        null);
            }
            exitDialog.show();
        }
    }

    private boolean isDetail() {
        return !isEdit && schedule.getId() > 0;
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void onReadContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Constants.RequestCode.GET_CONTACT);
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_read_contact___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CreateScheduleActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case Constants.RequestCode.GET_CONTACT:
                    getSelectedContact(data.getData());
                    break;
                case Constants.RequestCode.UPDATE_SCHEDULE:
                    schedule = data.getParcelableExtra("schedule");
                    if (schedule != null) {
                        setResult(RESULT_OK, data);
                        showScheduleDetail();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getSelectedContact(final Uri uri) {
        CommonUtil.unSubscribeSubs(getContactSub);
        getContactSub = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                String phone = null;
                try {
                    ContentResolver cr = getContentResolver();
                    Cursor c = cr.query(uri, null, null, null, null);
                    if (c != null && c.getCount() > 0) {
                        c.moveToFirst();
                        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                new String[]{c.getString(c.getColumnIndex(ContactsContract
                                        .Contacts._ID))},
                                null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            phone = cursor.getString(cursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.NUMBER));
                            if (!TextUtils.isEmpty(phone)) {
                                phone = phone.replaceAll("\\s+", "")
                                        .replaceAll("-", "");
                            }
                            cursor.close();
                        }
                        c.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subscriber.onNext(phone);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (TextUtils.isEmpty(s)) {
                            ToastUtil.showToast(CreateScheduleActivity.this,
                                    getString(R.string.msg_err_get_contact___cm,
                                            getString(R.string.app_name)),
                                    0);
                            return;
                        }
                        etPhone.setText(s);
                    }
                });
    }

    @OnTextChanged({R.id.et_message})
    public void afterTextChanged(Editable s) {
        if (s != null && s.length() >= 500 && etMessage.isFocused()) {
            ToastUtil.showToast(this, null, R.string.hint_enter_remark_max);
        }
    }

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
        CommonUtil.unSubscribeSubs(postSub, deleteSub, getContactSub);
    }
}