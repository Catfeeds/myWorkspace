package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import kankan.wheel.widget.ArrayPickerView;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.NewOrderPacket;
import me.suncloud.marrymemo.model.RefundReason;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;

public class RefundApplyActivity extends HljBaseNoBarActivity {

    private TextView refundReasonTv;
    private EditText refundExplainEt;
    private Dialog dialog;
    private String orderNum;
    private ArrayList<RefundReason> reasonArray;
    private View progressBar;
    private String[] reasons;
    private RefundReason selectedReason;
    private Dialog alertDialog;
    private NewOrderPacket orderPacket;
    private boolean isCustomOrder;
    private boolean isRefundIntentMoney; // 在用户付完意向金且没有付余款的情况下，如果退款则直接退还意向金
    private CustomSetmealOrder customSetmealOrder;
    private boolean isRefundInstallment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_apply);

        setDefaultStatusBarPadding();

        // 新加入的定制套餐退款,二者有些许区别
        isCustomOrder = getIntent().getBooleanExtra("is_custom_order", false);
        orderNum = getIntent().getStringExtra("order_num");
        isRefundIntentMoney = getIntent().getBooleanExtra("is_refund_intent_money", false);
        isRefundInstallment = getIntent().getBooleanExtra("is_refund_installment", false);

        progressBar = findViewById(R.id.progressBar);
        refundExplainEt = (EditText) findViewById(R.id.et_refund_explain);
        refundReasonTv = (TextView) findViewById(R.id.tv_refund_reason);
        refundExplainEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    // 只要改写过内容就禁用滑动返回
                    setSwipeBackEnable(false);
                }
            }
        });


        reasonArray = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        new GetRefundReasonsTask().execute();
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (isCustomOrder) {
            intent.putExtra("custom_order", (Serializable) customSetmealOrder);
        } else {
            intent.putExtra("order_packet", orderPacket);
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void onSubmit(View view) {
        if (refundReasonTv.length() <= 0) {
            Toast.makeText(this, getString(R.string.msg_empty_refund_reason), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String reasonExtra = "";
        if (refundExplainEt.length() > 0) {
            reasonExtra = refundExplainEt.getText()
                    .toString();
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason_id", selectedReason == null ? 0 : selectedReason.getId());
            jsonObject.put("desc", reasonExtra);
            if (isCustomOrder) {
                jsonObject.put("order_id", orderNum);
            } else {
                jsonObject.put("order_no", orderNum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        if (isCustomOrder) {
            url = Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_APPLY_REFUND);
        } else {
            url = Constants.getAbsUrl(Constants.HttpPath.POST_REFUND_APPLY);
        }

        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                if (obj != null) {
                    JSONObject jsonObject2 = (JSONObject) obj;
                    if (jsonObject2.optJSONObject("status") != null) {
                        if (jsonObject2.optJSONObject("status")
                                .optInt("RetCode", -1) == 0) {
                            // 退款申请成功后需要重新获取订单信息，并在返回给订单详情后进行刷新
                            if (isCustomOrder) {
                                customSetmealOrder = new CustomSetmealOrder
                                        (jsonObject2.optJSONObject(
                                        "data"));

                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT,
                                                customSetmealOrder));
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .CUSTOM_SETMEAL_ORDER_NEW_REFUND_COUNT,
                                                1));
                            } else {
                                orderPacket = new NewOrderPacket(jsonObject2.optJSONObject("data"));

                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .SERVICE_ORDER_REFRESH_WITH_OBJECT,
                                                orderPacket));
                                EventBus.getDefault()
                                        .post(new MessageEvent(MessageEvent.EventType
                                                .SERVICE_ORDER_NEW_REFUND_COUNT,
                                                1));
                            }
                            showSuccessDialog();
                        } else {
                            String error = jsonObject2.optJSONObject("status")
                                    .optString("msg");
                            Toast.makeText(RefundApplyActivity.this, error, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(RefundApplyActivity.this,
                                getString(R.string.msg_fail_to_apply_refund),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(RefundApplyActivity.this,
                            getString(R.string.msg_fail_to_apply_refund),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                Toast.makeText(RefundApplyActivity.this,
                        getString(R.string.msg_fail_to_apply_refund),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(url, jsonObject.toString());

    }

    public void onSelectReason(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        if (dialog == null) {
            dialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_array_picker, null);
            final ArrayPickerView arrayPickerView = (ArrayPickerView) v.findViewById(R.id.picker);
            arrayPickerView.setData(reasons);
            arrayPickerView.setSelect(0);

            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(R.string.label_refund_reason);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) arrayPickerView
                    .getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().density * (24 * 6));
            dialog.setContentView(v);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params2 = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params2.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
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
                            selectedReason = reasonArray.get(arrayPickerView.getSelectedItem());
                            refundReasonTv.setText(selectedReason.getName());
                            setSwipeBackEnable(false);
                            dialog.dismiss();
                        }
                    });
        }
        dialog.show();
    }

    private class GetRefundReasonsTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_REFUND_REASON);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null && jsonObject.optJSONObject("status") != null && jsonObject
                    .optJSONObject(
                    "status")
                    .optInt("RetCode", -1) == 0) {
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if (jsonArray != null && jsonArray.length() > 0) {
                    reasons = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RefundReason reason = new RefundReason(jsonArray.optJSONObject(i));
                        reasonArray.add(reason);
                        reasons[i] = reason.getName();
                    }
                }
            }

            findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
            super.onPostExecute(jsonObject);
        }
    }

    private void showSuccessDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        if (alertDialog == null) {
            alertDialog = new Dialog(this, R.style.BubbleDialogTheme);
            View v = getLayoutInflater().inflate(R.layout.dialog_msg_single_button, null);
            Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
            v.findViewById(R.id.extend_layout)
                    .setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    onBackPressed();
                }
            });

            TextView tvContent = (TextView) v.findViewById(R.id.tv_alert_msg);
            if (isRefundIntentMoney) {
                tvContent.setText(R.string.label_refund_intent_money);
            }
            if (isRefundInstallment) {
                tvContent.setText("我们将尽快与你联系\n" + "在您确定退款后，请将分期账单\n" + "及时选择“提前结清”，否则会继续计息");
            }
            alertDialog.setContentView(v);
            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
        }

        alertDialog.show();
    }

}
