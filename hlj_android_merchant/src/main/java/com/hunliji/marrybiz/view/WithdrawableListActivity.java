package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Income;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class WithdrawableListActivity extends HljBaseActivity implements AdapterView
        .OnItemClickListener, AbsListView.OnScrollListener {

    @BindView(R.id.bottom_layout)
    View bottomLayout;
    @BindView(R.id.btn_action)
    Button actionBtn;
    @BindView(R.id.list)
    StickyListHeadersListView listView;
    @BindView(R.id.progressBar)
    View progressBar;

    private int emptyHintDrawableId;
    private int emptyHintDrawableId2;
    private int emptyHintId;
    private int emptyHintId2;
    private ArrayList<Income> incomes;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View footView;
    private View loadView;
    private View endView;
    private StickyAdapter adapter;
    private String currentUrl;
    private Dialog dialog;
    private double refundMoney;
    private double compensationMoney;
    private double returnRedPacketMoney;

    private boolean selectAll;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_order_list);
        ButterKnife.bind(this);

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);

        getIntent().getDoubleExtra("refund_money", 0);
        getIntent().getDoubleExtra("compensation_money", 0);

        setOkText(R.string.label_select_all);

        emptyHintId = R.string.hint_no_orders;
        emptyHintId2 = R.string.label_no_bind;
        emptyHintDrawableId = R.mipmap.icon_empty_order;
        emptyHintDrawableId2 = R.drawable.icon_excalmatory_mark;
        incomes = new ArrayList<>();
        adapter = new StickyAdapter(this, incomes);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        // 如果传入特殊参数的话,显示特殊列表为空提示
        url = getIntent().getStringExtra("url");
        if (!JSONUtil.isEmpty(url)) {
            setNoBindHint();
        } else {
            currentUrl = Constants.getAbsUrl(Constants.HttpPath.GET_WITHDRAW_LIST);

            if (incomes.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);

                new GetIncomeListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        String.format(currentUrl, currentPage));
            }
        }
    }

    private void setNoBindHint() {
        View emptyView = listView.getEmptyView();
        if (emptyView == null) {
            emptyView = findViewById(R.id.empty_hint_layout);
            listView.setEmptyView(emptyView);
        }
        bottomLayout.setVisibility(View.GONE);
        hideOkText();
        setTitle(R.string.label_apply_withdraw);

        ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
        ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
        TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
        TextView btnAction = (TextView) emptyView.findViewById(R.id.btn_empty_hint);
        btnAction.setText(R.string.label_know_about_bind);

        imgEmptyHint.setVisibility(View.VISIBLE);
        textEmptyHint.setVisibility(View.VISIBLE);
        imgNetHint.setVisibility(View.GONE);
        textEmptyHint.setText(emptyHintId2);
        imgEmptyHint.setImageResource(emptyHintDrawableId2);
        btnAction.setVisibility(View.VISIBLE);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawableListActivity.this, HljWebViewActivity.class);
                intent.putExtra("title", getString(R.string.label_bond_sign_merchant));
                intent.putExtra("path", url);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        actionBtn.setEnabled(listView.getCheckedItemCount() > 0);
    }

    @Override
    public void onOkButtonClick() {
        selectAll = !selectAll;
        if (selectAll) {
            setOkText(R.string.label_cancel_select_all);
        } else {
            setOkText(R.string.label_select_all);
        }

        for (int i = 0; i < incomes.size(); i++) {
            listView.setItemChecked(i, selectAll);
        }

        actionBtn.setEnabled(listView.getCheckedItemCount() > 0);
    }

    @OnClick(R.id.btn_action)
    void onAction() {
        progressBar.setVisibility(View.VISIBLE);
        new GetRevenueStaticsTask().execute();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetIncomeListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(currentUrl, currentPage));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void showWithdrawInfo() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        // 计算申请提现的概况
        double totalWithdraw = 0;
        SparseBooleanArray checkedSPA = listView.getCheckedItemPositions();
        for (int i = 0; i < incomes.size(); i++) {
            if (checkedSPA.get(i)) {
                totalWithdraw += incomes.get(i)
                        .getIncomeMoney();
            }
        }
        final double withdrawable = totalWithdraw - refundMoney - compensationMoney -
                returnRedPacketMoney;

        dialog = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_withdraw_info, null);
        v.findViewById(R.id.btn_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        TextView withdrawMoney1 = (TextView) v.findViewById(R.id.tv_withdraw_1);
        TextView withdrawMoney2 = (TextView) v.findViewById(R.id.tv_withdraw_2);
        TextView withdrawTotal = (TextView) v.findViewById(R.id.tv_withdraw_total);
        TextView needRefund = (TextView) v.findViewById(R.id.tv_need_refund);
        TextView needCompensation = (TextView) v.findViewById(R.id.tv_need_compensation);
        TextView needReturnRedPacket = (TextView) v.findViewById(R.id.tv_need_redpacket);

        if (withdrawable >= 0) {
            withdrawMoney1.setText(Util.getIntegerPartFromDouble(withdrawable));
            withdrawMoney2.setText(Util.getFloatPartFromDouble(withdrawable));
        } else {
            withdrawMoney1.setText(Util.getIntegerPartFromDouble(0));
            withdrawMoney2.setText(Util.getFloatPartFromDouble(0));
        }

        withdrawTotal.setText(getString(R.string.label_price,
                Util.formatDouble2String(totalWithdraw)));
        needRefund.setText(getString(R.string.label_price, Util.formatDouble2String(refundMoney)));
        needCompensation.setText(getString(R.string.label_price,
                Util.formatDouble2String(compensationMoney)));
        needReturnRedPacket.setText(getString(R.string.label_price,
                Util.formatDouble2String(returnRedPacketMoney)));

        v.findViewById(R.id.btn_withdraw)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 申请提现
                        dialog.dismiss();
                        postWithdraw();
                    }
                });

        dialog.setContentView(v);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = point.x;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);

        dialog.show();
    }

    private void postWithdraw() {
        final JSONObject jsonObject = new JSONObject();
        String str = "";
        SparseBooleanArray checkedSPA = listView.getCheckedItemPositions();
        for (int i = 0; i < incomes.size(); i++) {
            if (checkedSPA.get(i)) {
                str += String.valueOf(incomes.get(i)
                        .getId()) + ",";
            }
        }

        try {
            jsonObject.put("cashflow_id", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;

                    if (resultObject != null && resultObject.optJSONObject("status") != null) {
                        int retCode = resultObject.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(WithdrawableListActivity.this,
                                    R.string.msg_succeed_apply_withdraw,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = getIntent();
                            intent.putExtra("withdrawed", true);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        } else {
                            String msg = JSONUtil.getString(resultObject.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(WithdrawableListActivity.this, msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(WithdrawableListActivity.this,
                                R.string.msg_fail_to_withdraw,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(WithdrawableListActivity.this,
                            R.string.msg_fail_to_withdraw,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WithdrawableListActivity.this,
                        R.string.msg_fail_to_withdraw,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_WITHDRAW), jsonObject.toString());
    }

    public class StickyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;
        private ArrayList<Income> mData;

        public StickyAdapter(Context context, ArrayList<Income> orders) {
            inflater = LayoutInflater.from(context);
            mData = orders;
        }

        @Override
        public View getHeaderView(int i, View view, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (view == null) {
                holder = new HeaderViewHolder();
                view = inflater.inflate(R.layout.date_head_item, viewGroup, false);
                holder.date = (TextView) view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (HeaderViewHolder) view.getTag();
            }

            holder.date.setText(mData.get(i)
                    .getCreatedAt()
                    .toString(getString(R.string.format_date_type10)));

            return view;
        }

        @Override
        public long getHeaderId(int i) {
            return mData.get(i)
                    .getDateVal();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? 0 : mData.get(position)
                    .getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.withdrawable_list_item, parent, false);
                holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.check);
                holder.titleTv = (TextView) convertView.findViewById(R.id.tv_title);
                holder.moneyTv = (TextView) convertView.findViewById(R.id.tv_money);
                holder.paidStatusTv = (TextView) convertView.findViewById(R.id.tv_paid_status);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleTv.setText(mData.get(position)
                    .getBuyerName());
            holder.paidStatusTv.setText(mData.get(position)
                    .getPayType());
            holder.moneyTv.setText(getString(R.string.label_price,
                    Util.formatDouble2String(mData.get(position)
                            .getIncomeMoney())));

            return convertView;
        }

        class HeaderViewHolder {
            TextView date;
        }

        class ViewHolder {
            TextView titleTv;
            TextView paidStatusTv;
            TextView moneyTv;
            CheckedTextView checkedTextView;
        }
    }

    public class GetIncomeListTask extends AsyncTask<String, Integer, JSONObject> {
        String url;

        public GetIncomeListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(WithdrawableListActivity.this, url);
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
            super.onPostExecute(jsonObject);
            int size = 0;
            if (url.equals(String.format(currentUrl, currentPage))) {
                loadView.setVisibility(View.INVISIBLE);
                if (jsonObject != null) {
                    JSONObject statusObject = jsonObject.optJSONObject("status");
                    if (statusObject != null && statusObject.optInt("RetCode", -1) == 0) {
                        // 数据正确
                        JSONArray jsonArray = jsonObject.optJSONObject("data")
                                .optJSONArray("list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            size = jsonArray.length();
                            if (currentPage == 1) {
                                incomes.clear();
                            }
                            for (int i = 0; i < size; i++) {
                                JSONObject dataObject = jsonArray.optJSONObject(i);
                                Income order = new Income(dataObject);
                                incomes.add(order);
                            }
                        }
                    }
                }

                // 刷新
                adapter.notifyDataSetChanged();
            }


            if (size < 20) {
                isEnd = true;
                footView.findViewById(R.id.no_more_hint)
                        .setVisibility(View.VISIBLE);
                loadView.setVisibility(View.GONE);
            } else {
                isEnd = false;
                footView.findViewById(R.id.no_more_hint)
                        .setVisibility(View.GONE);
                loadView.setVisibility(View.INVISIBLE);
            }

            if (incomes.isEmpty()) {
                View emptyView = listView.getEmptyView();

                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.setEmptyView(emptyView);
                }

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id
                        .text_empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(WithdrawableListActivity.this)) {
                    imgEmptyHint.setImageResource(emptyHintDrawableId);
                    emptyHintTextView.setText(emptyHintId);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    emptyHintTextView.setText(R.string.net_disconnected);
                }
                bottomLayout.setVisibility(View.GONE);
            } else {
                bottomLayout.setVisibility(View.VISIBLE);
            }

            isLoad = false;
        }
    }

    private class GetRevenueStaticsTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_REVENUE_STATICS);
            try {
                String json = JSONUtil.getStringFromUrl(WithdrawableListActivity.this, url);
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
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        refundMoney = dataObject.optDouble("need_refund", 0);
                        compensationMoney = dataObject.optDouble("need_indemnity", 0);
                        returnRedPacketMoney = dataObject.optDouble("need_redpacket", 0);

                        showWithdrawInfo();
                    } else {
                        Toast.makeText(WithdrawableListActivity.this,
                                getString(R.string.msg_error_server),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(WithdrawableListActivity.this,
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(WithdrawableListActivity.this,
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


}
