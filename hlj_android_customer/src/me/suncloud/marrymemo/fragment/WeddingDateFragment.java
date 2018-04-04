package me.suncloud.marrymemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.user.UserApi;
import me.suncloud.marrymemo.util.Session;
import rx.functions.Func1;

/**
 * Created by mo_yu on 2018/3/20.设置婚期
 */
public class WeddingDateFragment extends DialogFragment implements DatePicker
        .OnDateChangedListener {

    public static final String ARG_DATE_TIME = "date_time";
    @BindView(R.id.date_picker)
    DatePicker datePicker;
    @BindView(R.id.btn_count_down)
    Button btnCountDown;
    @BindView(R.id.btn_close)
    ImageButton btnClose;
    Unbinder unbinder;
    private Context mContext;
    private HljHttpSubscriber saveWeddingDateSub;
    private Calendar tempCalendar;
    private onDateSelectedListener onDateSelectedListener;

    public static WeddingDateFragment newInstance(DateTime dateTime) {
        WeddingDateFragment fragment = new WeddingDateFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE_TIME, dateTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_wedding_date, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        if (getArguments() != null) {
            DateTime dateTime = (DateTime) getArguments().getSerializable(ARG_DATE_TIME);
            setDateTime(dateTime);
        }
        return rootView;
    }

    private void init() {
        datePicker.init(Calendar.getInstance()
                        .get(Calendar.YEAR),
                Calendar.getInstance()
                        .get(Calendar.MONTH),
                Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH),
                this);

        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        Date minDate = new Date();//当前时间
        Calendar minCalendar = Calendar.getInstance();//日历对象
        minCalendar.setTime(minDate);//设置当前日期
        minCalendar.add(Calendar.YEAR, -1);
        long minDateMillis = minCalendar.getTimeInMillis();
        Date maxDate = new Date();
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.setTime(maxDate);
        maxCalendar.add(Calendar.YEAR, 10);
        long maxDateMillis = maxCalendar.getTimeInMillis();
        datePicker.setMinDate(minDateMillis);
        datePicker.setMaxDate(maxDateMillis);
    }

    //设置婚期
    private void saveWeddingDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        final DateTime weddingDate = new DateTime(calendar.getTime());
        map.put("weddingday", weddingDate.toString(HljTimeUtils.DATE_FORMAT_SHORT));
        CommonUtil.unSubscribeSubs(saveWeddingDateSub);
        saveWeddingDateSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        if (jsonElement != null) {
                            ToastUtil.showCustomToast(getContext(),
                                    R.string.label_set_wedding_date_success);
                            try {
                                Session.getInstance()
                                        .setCurrentUser(getContext(),
                                                new JSONObject(jsonElement.getAsJsonObject()
                                                        .toString()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (onDateSelectedListener != null) {
                                onDateSelectedListener.onDateSelected(tempCalendar);
                            }
                        }
                        onClose(null);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        onClose(null);
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .build();
        UserApi.editMyBaseInfoObb(map)
                .onErrorReturn(new Func1<Throwable, JsonElement>() {
                    @Override
                    public JsonElement call(Throwable throwable) {
                        return null;
                    }
                })
                .subscribe(saveWeddingDateSub);
    }


    public void setDateTime(DateTime time) {
        if (time != null) {
            int year = time.getYear();
            int month = Math.max(time.monthOfYear()
                    .get() - 1, 0);
            int day = time.dayOfMonth()
                    .get();
            datePicker.updateDate(year, month, day);
        }
    }


    @OnClick(R.id.btn_close)
    public void onClose(View view) {
        dismiss();
    }

    @OnClick(R.id.btn_count_down)
    public void onCountDown(View view) {
        saveWeddingDate(tempCalendar);
    }

    public void setOnDateSelectedListener(
            onDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0);
        } else {
            tempCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(saveWeddingDateSub);
    }

    public interface onDateSelectedListener {
        void onDateSelected(Calendar calendar);
    }

}
