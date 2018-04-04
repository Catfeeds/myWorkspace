package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.ADVHPrice;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.util.DialogUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.util.popuptip.PopupRule;
import com.hunliji.marrybiz.view.merchantservice.BondPlanDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;


public class BuyADVHActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<ADVHPrice>, PullToRefreshBase.OnRefreshListener<ListView>, AdapterView
        .OnItemClickListener {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.packet_detail_info)
    LinearLayout packetDetailInfo;
    @BindView(R.id.btn_buy)
    Button btnBuy;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_advh_count)
    TextView tvAdvhCount;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    private ObjectBindAdapter<ADVHPrice> adapter;
    private ArrayList<ADVHPrice> advhPrices;
    private ADVHPrice selectedADVHPrice;
    private MerchantUser user;
    private Dialog dialog;
    private Subscriber<PayRxEvent> paySubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_advh);
        ButterKnife.bind(this);

        View headView = getLayoutInflater().inflate(R.layout.buy_advh_list_head, null);
        listView.getRefreshableView()
                .addHeaderView(headView, null, false);
        advhPrices = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, advhPrices, R.layout.advh_price_list_item, this);
        listView.setAdapter(adapter);
        listView.getRefreshableView()
                .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.getRefreshableView()
                .setItemsCanFocus(true);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);

        progressBar.setVisibility(View.VISIBLE);
        user = Session.getInstance()
                .getCurrentUser(this);
        new GetPricesTask().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                        .ADVH_PRICE_LIST,
                user.getMerchantId())));
    }

    @Override
    public void setViewValue(View view, ADVHPrice advhPrice, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);

            view.setTag(holder);
        }

        holder.tvAdvhCount.setText(getString(R.string.label_advh_count,
                String.valueOf(advhPrice.getQuantity())));
        holder.tvPrice.setText(getString(R.string.label_price2,
                Util.formatDouble2String(advhPrice.getPrice())));

        if (advhPrice.getDiscountPercent() > 0 && advhPrice.getDiscountPercent() < 1) {
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText(getString(R.string.label_discount,
                    Util.formatDouble2String(advhPrice.getDiscountPercent() * 10)));
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        if (advhPrice.getGiftNum() > 0) {
            holder.tvTotalCount.setVisibility(View.VISIBLE);
            holder.tvTotalCount.setText(getString(R.string.label_advh_total_count,
                    String.valueOf(advhPrice.getGiftNum()),
                    String.valueOf(advhPrice.getGiftNum() + advhPrice.getQuantity())));
        } else {
            holder.tvTotalCount.setVisibility(View.GONE);
        }

        if (position == advhPrices.size() - 1) {
            holder.bottomLine2.setVisibility(View.VISIBLE);
            holder.bottomLine.setVisibility(View.GONE);
        } else {
            holder.bottomLine2.setVisibility(View.GONE);
            holder.bottomLine.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetPricesTask().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                        .ADVH_PRICE_LIST,
                user.getMerchantId())));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ADVHPrice advhPrice = (ADVHPrice) parent.getAdapter()
                .getItem(position);
        if (advhPrice != null) {
            int totalQuantity = advhPrice.getQuantity() + advhPrice.getGiftNum();
            btnBuy.setEnabled(true);
            packetDetailInfo.setVisibility(View.VISIBLE);
            tvAdvhCount.setText(Html.fromHtml(getString(R.string.label_purchased_advh_count,
                    String.valueOf(totalQuantity))));
            tvPrice.setText(Html.fromHtml(getString(R.string.label_need_pay,
                    Util.formatDouble2String(advhPrice.getPrice()))));
            selectedADVHPrice = advhPrice;
        } else {
            btnBuy.setEnabled(false);
            packetDetailInfo.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_buy)
    void onConfirm() {
        if (!user.isPro()) {
            showHintDialog(getString(R.string.hint_merchant_pro__qa), 0);
            return;
        } else if (!user.isBondSign()) {
            showHintDialog(getString(R.string.hint_privilege_bond_sign2), 2);
            return;
        }
        if (selectedADVHPrice == null) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("advid", selectedADVHPrice.getId());
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
                    .path(Constants.HttpPath.ADVH_PAY)
                    .price(selectedADVHPrice.getPrice())
                    .subscriber(paySubscriber)
                    .llpayMode(true)
                    .payAgents(payTypes, DataConfig.getPayAgents())
                    .build()
                    .pay();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'advh_price_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_advh_count)
        TextView tvAdvhCount;
        @BindView(R.id.tv_discount)
        TextView tvDiscount;
        @BindView(R.id.tv_total_count)
        TextView tvTotalCount;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.bottom_line2)
        View bottomLine2;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    private class GetPricesTask extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(BuyADVHActivity.this, params[0]);
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
            listView.onRefreshComplete();
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONArray jsonArray = jsonObject.optJSONObject("data")
                            .optJSONArray("Template");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        advhPrices.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            ADVHPrice advhPrice = new ADVHPrice(jsonArray.optJSONObject(i));
                            advhPrices.add(advhPrice);
                        }

                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < advhPrices.size(); i++) {
                            ADVHPrice ap = advhPrices.get(i);
                            if (ap.isSelected()) {
                                final int finalI = i;
                                listView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.getRefreshableView()
                                                .setItemChecked(finalI + 2, true);
                                    }
                                }, 300);
                                int totalQuantity = ap.getQuantity() + ap.getGiftNum();
                                btnBuy.setEnabled(true);
                                packetDetailInfo.setVisibility(View.VISIBLE);
                                tvAdvhCount.setText(Html.fromHtml(getString(R.string
                                                .label_purchased_advh_count,
                                        String.valueOf(totalQuantity))));
                                tvPrice.setText(Html.fromHtml(getString(R.string.label_need_pay,
                                        Util.formatDouble2String(ap.getPrice()))));
                                selectedADVHPrice = ap;
                            }
                        }
                    }
                }
            }

            super.onPostExecute(jsonObject);
        }
    }

    /**
     * @param hint 弹窗提示内容
     * @param type 弹窗类型
     *             0：非店铺专业版
     *             1：等级不足
     *             2：未开通保证金
     *             3：保证余额不足
     *             2-3合并为 了解保证金 2017-06-06
     */
    private void showHintDialog(String hint, final int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        String confirmStr = getString(R.string.label_learn_about_detail___qa);
        switch (type) {
            case 2:
            case 3:
                confirmStr = getString(R.string.label_learn_about_bond);
                break;
        }
        dialog = DialogUtil.createDoubleButtonDialog(dialog,
                this,
                null,
                hint,
                confirmStr,
                getString(R.string.label_close),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        switch (type) {
                            case 0:
                                PopupRule.getDefault()
                                        .goMerchantProAdsWebActivity(BuyADVHActivity.this);
                                break;
                            case 2:
                            case 3:
                                Intent intent = new Intent(BuyADVHActivity.this,
                                        BondPlanDetailActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                break;
                        }
                    }
                },
                null);
        dialog.show();
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
                        // 客资购买成功之后跳转客资管理页
                        Intent intent = new Intent(BuyADVHActivity.this,
                                ADVHMerchantListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
    }
}
