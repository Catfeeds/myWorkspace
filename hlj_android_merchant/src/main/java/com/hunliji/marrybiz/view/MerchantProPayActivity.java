package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
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
 * Created by Suncloud on 2016/6/16.
 */
public class MerchantProPayActivity extends HljBaseActivity {

    @BindView(R.id.tv_actual_price)
    TextView tvActualPrice;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;
    @BindView(R.id.tv_market_price)
    TextView tvMarketPrice;
    @BindView(R.id.discount_layout)
    RelativeLayout discountLayout;
    @BindView(R.id.tv_bd_name)
    EditText tvBdName;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private double actualPrice;
    private Subscriber<PayRxEvent> paySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_pro_pay);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        new GetProInfoTask().executeOnExecutor(Constants.INFOTHEADPOOL);
    }

    private class GetProInfoTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(MerchantProPayActivity.this,
                        Constants.getAbsUrl(Constants.HttpPath.PRO_PAY_INFO));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).getJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                actualPrice = jsonObject.optDouble("actual_price", 0);
                double marketPrice = jsonObject.optDouble("market_price", 0);
                if (actualPrice > 0) {
                    findViewById(R.id.info_layout).setVisibility(View.VISIBLE);
                    tvActualPrice.setText(Util.formatDouble2String(actualPrice));
                    if (marketPrice > 0 && marketPrice > actualPrice) {
                        discountLayout.setVisibility(View.VISIBLE);
                        tvDiscount.setText(getString(R.string.label_time_discount,
                                (float) Math.round(actualPrice * 100 / marketPrice) / 10));
                        tvMarketPrice.setText(getString(R.string.label_original_price2,
                                Util.formatDouble2String(marketPrice)));
                        tvMarketPrice.getPaint()
                                .setAntiAlias(true);
                        tvMarketPrice.getPaint()
                                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }


    public void onAgreement(View view) {
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.MERCHANT_PRO_PROTOCOL, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        progressBar.setVisibility(View.GONE);
                        String url = (String) obj;
                        if (!JSONUtil.isEmpty(url)) {
                            Intent intent = new Intent(MerchantProPayActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("path", url);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void onPay(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (tvBdName.length() > 0 && !JSONUtil.isEmpty(tvBdName.getText()
                    .toString()
                    .trim())) {
                jsonObject.put("bd_name",
                        tvBdName.getText()
                                .toString()
                                .trim());
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
            new PayConfig.Builder(this).params(jsonObject)
                    .path(Constants.HttpPath.PRO_PAY_URL)
                    .price(actualPrice)
                    .subscriber(paySubscriber)
                    .llpayMode(true)
                    .payAgents(payTypes, DataConfig.getPayAgents())
                    .build()
                    .pay();
        } catch (JSONException e) {
            e.printStackTrace();
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
                        Intent intent = new Intent(MerchantProPayActivity.this, HomeActivity.class);
                        intent.putExtra("index", 0);
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
