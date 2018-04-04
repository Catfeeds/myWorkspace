package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;

/**
 * Created by Suncloud on 2016/7/19.
 */
public class OrderConfirmActivity extends HljBaseActivity implements DTPicker.OnPickerDateListener {

    @BindView(R.id.tv_serve_date)
    TextView tvServeDate;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Dialog selectTimeDialog;
    private NewOrder order;
    private Calendar tempCalendar;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        order = (NewOrder) getIntent().getSerializableExtra("order");
    }

    public void onDateSelect(View view) {
        if (selectTimeDialog != null && selectTimeDialog.isShowing()) {
            return;
        }
        if (selectTimeDialog == null) {
            selectTimeDialog = new Dialog(this, R.style.BubbleDialogTheme);
            selectTimeDialog.setContentView(R.layout.dialog_date_picker);
            selectTimeDialog.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectTimeDialog.dismiss();
                        }
                    });
            selectTimeDialog.findViewById(R.id.confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectTimeDialog.dismiss();
                            if (tempCalendar != null) {
                                if (calendar == null) {
                                    calendar = Calendar.getInstance();
                                }
                                calendar.setTime(tempCalendar.getTime());
                                tvServeDate.setText(new DateTime(tempCalendar.getTime()).toString(
                                        Constants.DATE_FORMAT_SHORT));
                                btnConfirm.setEnabled(true);
                            }
                        }
                    });
            DatePickerView picker = (DatePickerView) selectTimeDialog.findViewById(R.id.picker);
            picker.setYearLimit(2000, 49);
            picker.setCurrentCalender(Calendar.getInstance());
            if (tempCalendar != null) {
                tempCalendar = Calendar.getInstance();
            }
            picker.setOnPickerDateListener(this);
            picker.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            Window win = selectTimeDialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        selectTimeDialog.show();
    }


    public void onConfirm(View view) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
            jsonObject.put("service_time",
                    new DateTime(order.getWeddingTime()).toString(Constants.DATE_FORMAT_LONG));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(OrderConfirmActivity.this,
                                    R.string.msg_success_confirm_service2,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = jsonObject1.optJSONObject("data");
                            if (dataObject != null) {
                                NewOrder newOrder = new NewOrder(dataObject);
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .SERVICE_ORDER_REFRESH_WITH_OBJECT,
                                                newOrder));
                                onBackPressed();
                            }
                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(OrderConfirmActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(OrderConfirmActivity.this,
                                R.string.msg_fail_to_confirm_service,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(OrderConfirmActivity.this,
                            R.string.msg_fail_to_confirm_service,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderConfirmActivity.this,
                        R.string.msg_fail_to_confirm_service,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_CONFIRM_SERVICE),
                jsonObject.toString());
    }


    @Override
    public void onPickerDate(int year, int month, int day) {
        if (tempCalendar == null) {
            tempCalendar = new GregorianCalendar(year, month - 1, day);
        } else {
            tempCalendar.set(year, month - 1, day);
        }
    }
}
