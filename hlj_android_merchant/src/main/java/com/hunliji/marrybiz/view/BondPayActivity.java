package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by Suncloud on 2015/12/15.
 */
public class BondPayActivity extends HljBaseActivity {

    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.merchant_name)
    TextView merchantName;
    @BindView(R.id.property)
    TextView property;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btn_pay)
    Button btnPay;
    private double fee;
    private double bondBalance;
    private MerchantUser user;
    private Subscriber<PayRxEvent> paySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond_pay);
        ButterKnife.bind(this);
        user = Session.getInstance()
                .getCurrentUser(this);

        progressBar.setVisibility(View.VISIBLE);
        new GetBondFeeBalanceTask().execute();
    }

    private void setViewValue() {
        fee = user.getBondFee() - bondBalance;
        price.setText(getString(R.string.label_price2, Util.formatDouble2String(fee)));
        merchantName.setText(getString(R.string.label_merchant_name4, user.getName()));
        property.setText(getString(R.string.label_merchant_property2, user.getPropertyName()));
        btnPay.setEnabled(true);
    }

    public void onPay(View view) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (paySubscriber == null) {
            paySubscriber = initSubscriber();
        }
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        ArrayList<String> payTypes = new ArrayList<>();
        if (dataConfig != null && dataConfig.getPayTypes() != null) {
            payTypes.addAll(dataConfig.getPayTypes());
        }
        new PayConfig.Builder(this).params(new JSONObject())
                .path(Constants.HttpPath.BOND_PAY_URL)
                .price(fee)
                .subscriber(paySubscriber)
                .llpayMode(true)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }

    private class GetBondFeeBalanceTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_WITHDRAW_STATICS);
            try {
                String json = JSONUtil.getStringFromUrl(BondPayActivity.this, url);
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
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    bondBalance = dataObj.optDouble("bond_fee", 0);

                    setViewValue();
                } else {
                    Toast.makeText(BondPayActivity.this,
                            returnStatus.getErrorMsg(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(BondPayActivity.this,
                        R.string.msg_fail_to_get_withdraw_statistics,
                        Toast.LENGTH_SHORT)
                        .show();
            }

            super.onPostExecute(jsonObject);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySubscriber);
    }

    //支付成功
    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        // 保证金支付成功之后跳转成功提醒页面
                        Intent intent = new Intent(BondPayActivity.this, BondPayDoneActivity.class);
                        intent.putExtra("fee", fee);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                        break;
                }
            }
        };
    }
}
