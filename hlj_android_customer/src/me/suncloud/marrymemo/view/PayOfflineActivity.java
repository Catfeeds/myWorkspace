package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PacketsListAdapter;
import me.suncloud.marrymemo.model.OfflineOrder;
import me.suncloud.marrymemo.model.RedPacket;
import me.suncloud.marrymemo.task.NewHttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

public class PayOfflineActivity extends HljBaseNoBarActivity {

    private ArrayList<RedPacket> redPackets;
    private RedPacket selectedRedPacket;
    private RedPacket pendingRedPacket;
    private TextView redPacketAmount;
    private TextView realPriceTv;
    private EditText totalPriceEt;
    private double totalPrice;
    private double redPacketMoney;
    private Dialog dialog;
    private View progressBar;
    private View contentLayout;
    private View bottomLayout;
    private View redPacketLayout;
    private long merchantId;
    private double realPrice;
    private OfflineOrder offlineOrder;
    private boolean backToList;

    private PacketsListAdapter packetsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_offline);
        setDefaultStatusBarPadding();

        merchantId = getIntent().getLongExtra("merchant_id", 0);
        offlineOrder = (OfflineOrder) getIntent().getSerializableExtra("offline_order");
        backToList = getIntent().getBooleanExtra("back_to_list", false);

        contentLayout = findViewById(R.id.content_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        redPacketLayout = findViewById(R.id.red_packet_layout);
        realPriceTv = (TextView) findViewById(R.id.tv_real_price);
        totalPriceEt = (EditText) findViewById(R.id.et_total_price);
        progressBar = findViewById(R.id.progressBar);
        redPacketAmount = (TextView) findViewById(R.id.tv_saved_amount);

        if (offlineOrder != null) {
            // 从订单列表或者从订单详情跳转过来的
            totalPrice = offlineOrder.getTotalPrice();
            totalPriceEt.setText(Util.formatDouble2String(totalPrice));

            realPrice = totalPrice - redPacketMoney;
            if (realPrice > 0) {
                realPriceTv.setText(Util.formatDouble2String(realPrice));
            } else {
                realPrice = 0;
                realPriceTv.setText("0");
            }
        }
        totalPriceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setPrices();
            }
        });

        totalPriceEt.clearFocus();
        redPackets = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        new GetRedPacketsTask().execute();
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setPrices() {
        if (totalPriceEt.length() > 0) {
            totalPrice = Double.valueOf(totalPriceEt.getText()
                    .toString());
            realPrice = totalPrice - redPacketMoney;
            if (realPrice < 0) {
                realPrice = 0;
            }

            realPriceTv.setText(Util.formatDouble2String(realPrice));
        }
    }

    public void onPay(View view) {
        if (totalPrice <= 0) {
            Toast.makeText(this, R.string.msg_negtive_price, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            if (offlineOrder != null) {
                jsonObject.put("order_no", offlineOrder.getOrderNum());
                jsonObject.put("merchant_id", offlineOrder.getMerchantId());
            } else {
                jsonObject.put("merchant_id", merchantId);
            }
            jsonObject.put("money", String.valueOf(totalPrice));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject != null && jsonObject.optJSONObject("status") != null &&
                        jsonObject.optJSONObject(
                        "status")
                        .optInt("RetCode", -1) == 0) {
                    Log.d(PayOfflineActivity.class.getSimpleName(), jsonObject.toString());
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        String orderNum = JSONUtil.getString(dataObject, "order_no");
                        long orderId = dataObject.optLong("order_id");
                        Intent intent = new Intent(PayOfflineActivity.this,
                                OfflineOrderPaymentActivity.class);
                        intent.putExtra("back_to_list", backToList);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("order_no", orderNum);
                        intent.putExtra("real_price", realPrice);
                        intent.putExtra("red_packet_no",
                                selectedRedPacket == null ? "" : selectedRedPacket.getTicketNo());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        String msg = jsonObject.optJSONObject("status")
                                .optString("msg");
                        Toast.makeText(PayOfflineActivity.this, msg, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(PayOfflineActivity.this, "提交失败", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                Toast.makeText(PayOfflineActivity.this, "提交失败", Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_SUBMIT_OFFLINE_ORDER),
                jsonObject.toString());

    }

    private class GetRedPacketsTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_RED_PACKETS_URL);
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
                contentLayout.setVisibility(View.VISIBLE);
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RedPacket redPacket = new RedPacket(jsonArray.optJSONObject(i));
                        redPackets.add(redPacket);
                        if (offlineOrder != null && offlineOrder.getRedPacketNum() != null &&
                                offlineOrder.getRedPacketNum()
                                .equalsIgnoreCase(redPacket.getTicketNo())) {
                            // 新取到的红包列表中有这个订单已选择使用的，可以选择
                            selectedRedPacket = redPacket;
                        }
                    }
                    if (packetsListAdapter != null) {
                        packetsListAdapter.notifyDataSetChanged();
                    }
                    if (redPackets.size() > 0) {
                        // 不使用红包的列表占位实体
                        redPacketLayout.setVisibility(View.VISIBLE);

                        redPackets.add(new RedPacket(-1));

                        // 默认选中之前已有的，否则默认选中第一个
                        if (selectedRedPacket == null) {
                            selectedRedPacket = redPackets.get(0);
                        }

                        redPacketAmount.setText(Html.fromHtml(getString(R.string.label_saved_money,
                                Util.formatDouble2String(selectedRedPacket.getAmount()))));

                        // 修改需付金额
                        redPacketMoney = selectedRedPacket.getAmount();
                    } else {
                        // 没有红包不显示红包选择界面
                        redPacketLayout.setVisibility(View.GONE);
                    }

                    setPrices();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    public void showRedPackets(View view) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        if (packetsListAdapter == null) {
            packetsListAdapter = new PacketsListAdapter(redPackets);
        }
        dialog = DialogUtil.createRedPacketDialog(dialog,
                this,
                packetsListAdapter,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        pendingRedPacket = (RedPacket) parent.getAdapter()
                                .getItem(position);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (pendingRedPacket != null) {
                            selectedRedPacket = pendingRedPacket;
                        }
                        if (selectedRedPacket == null) {
                            return;
                        }
                        if (selectedRedPacket.getId() < 0) {
                            // 不使用红包
                            redPacketAmount.setText(R.string.label_use_not_red_enve2);
                        } else {
                            // 使用红包
                            redPacketAmount.setText(Html.fromHtml(getString(R.string
                                            .label_saved_money,
                                    Util.formatDouble2String(selectedRedPacket.getAmount()))));
                        }

                        // 修改需付金额
                        redPacketMoney = selectedRedPacket.getAmount();
                        setPrices();
                    }
                },
                redPackets.indexOf(selectedRedPacket));
        dialog.show();
    }
}
