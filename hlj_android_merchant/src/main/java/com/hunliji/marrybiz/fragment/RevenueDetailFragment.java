package com.hunliji.marrybiz.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.model.revenue.BondBalanceDetail;
import com.hunliji.marrybiz.model.revenue.OrderDetail;
import com.hunliji.marrybiz.model.revenue.RevenueDetail;
import com.hunliji.marrybiz.model.revenue.WaterDetail;
import com.hunliji.marrybiz.model.revenue.WithdrawDetail;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.BondBalanceDetailActivity;
import com.hunliji.marrybiz.view.NewOrderDetailActivity;
import com.hunliji.marrybiz.view.RevenueWithdrawDetailActivity;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by werther on 16/3/17.
 * 用于收入管理中各种详细记录的fragment list
 * 包括:收支明细中的收入,支出和提现
 * 担保金列表中的普通套餐和定制套餐
 * 待结算列表中的普通套餐和定制套餐
 * 保证金明细列表
 */
public class RevenueDetailFragment extends RefreshFragment implements AdapterView
        .OnItemClickListener, AbsListView.OnScrollListener {

    private View rootView;
    private View progressBar;
    private int emptyHintDrawableId;
    private int emptyHintId;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View footView;
    private View loadView;
    private View endView;

    private StickyListHeadersListView listView;
    private ArrayList<RevenueDetail> details;
    private StickyAdapter adapter;
    private int type; // 对应RevenueDetail中定义的各种type
    public static final String ARG_NAME_TYPE = "type";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        details = new ArrayList<>();
        adapter = new StickyAdapter(getActivity(), details);

        emptyHintId = R.string.hint_no_records;
        emptyHintDrawableId = R.mipmap.icon_empty_common;

        footView = View.inflate(getContext(), R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);
        EventBus.getDefault()
                .register(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stickyhead_listview, container, false);
        listView = rootView.findViewById(R.id.list);
        progressBar = rootView.findViewById(R.id.progressBar);

        listView.addFooterView(footView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);

        if (getArguments() != null) {
            type = getArguments().getInt(ARG_NAME_TYPE);
        }

        if (details.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;
            String typedUrl = getTypedUrl();

            new GetDetailsListTask().executeOnExecutor(Constants.LISTTHEADPOOL, typedUrl);
        }

        return rootView;
    }

    private String getTypedUrl() {
        String typedUrl = "";
        switch (type) {
            //提现中
            case RevenueDetail.TYPE_WITHDRAWING:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .REVENUE_WITHDRAW_DETAIL_LIST),
                        0,
                        currentPage);
                break;
            //提现成功
            case RevenueDetail.TYPE_WITHDRAW_SUCCESS:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .REVENUE_WITHDRAW_DETAIL_LIST),
                        1,
                        currentPage);
                break;
            //提现失败
            case RevenueDetail.TYPE_WITHDRAWING_FAIL:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .REVENUE_WITHDRAW_DETAIL_LIST),
                        2,
                        currentPage);
                break;
            //保证金明细
            case RevenueDetail.TYPE_BOND_BALANCE_DETAIL:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .GET_BOND_BALANCE_LIST),
                        currentPage);
                break;
            //流水明细
            case RevenueDetail.TYPE_WATER_DETAIL:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .GET_WATER_DETAIL_LIST),
                        currentPage);
                break;
            //订单明细
            case RevenueDetail.TYPE_ORDER_DETAIL:
                typedUrl = String.format(Constants.getAbsUrl(Constants.HttpPath
                                .GET_ORDER_DETAIL_LIST),
                        currentPage);
                break;
        }

        return typedUrl;
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == 7) {
            refresh();
        }
    }

    @Override
    public void refresh(Object... params) {
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);

        String typedUrl = getTypedUrl();

        new GetDetailsListTask().executeOnExecutor(Constants.LISTTHEADPOOL, typedUrl);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RevenueDetail detail = (RevenueDetail) adapterView.getAdapter()
                .getItem(i);
        Intent intent = null;
        if (detail != null) {
            switch (type) {
                case RevenueDetail.TYPE_WITHDRAWING:
                case RevenueDetail.TYPE_WITHDRAW_SUCCESS:
                case RevenueDetail.TYPE_WITHDRAWING_FAIL:
                    if (detail.getId() > 0) {
                        intent = new Intent(getContext(), RevenueWithdrawDetailActivity.class);
                        intent.putExtra("id", detail.getId());
                    }
                    break;
                case RevenueDetail.TYPE_BOND_BALANCE_DETAIL:
                    BondBalanceDetail bondBalanceDetail = (BondBalanceDetail) detail;
                    intent = new Intent(getContext(), BondBalanceDetailActivity.class);
                    intent.putExtra("detail", bondBalanceDetail);
                    break;
                //流水明细
                case RevenueDetail.TYPE_WATER_DETAIL:
                    int detailType = detail.getType();
                    switch (detailType) {
                        case RevenueDetail.ORDER_REFUND:
                        case RevenueDetail.RED_ENVELOPES_RETURNED:
                        case RevenueDetail.ORDER_INCOME:
                            if (detail.getEntityId() > 0 && !TextUtils.isEmpty(detail.getOrderNo
                                    ())) {
                                intent = new Intent(getContext(), NewOrderDetailActivity.class);
                                intent.putExtra("id", detail.getEntityId());
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                //订单明细
                case RevenueDetail.TYPE_ORDER_DETAIL:
                    if (detail.getId() > 0) {
                        intent = new Intent(getContext(), NewOrderDetailActivity.class);
                        intent.putExtra("id", detail.getId());
                    }
                    break;
            }
        }
        if (intent != null) {
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        switch (i) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (absListView.getLastVisiblePosition() >= (absListView.getCount() - 5) &&
                        !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    String typedUrl = getTypedUrl();
                    new GetDetailsListTask().executeOnExecutor(Constants.LISTTHEADPOOL, typedUrl);
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    public class StickyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;
        private ArrayList<RevenueDetail> mData;
        private Context context;

        public StickyAdapter(Context context, ArrayList<RevenueDetail> mData) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.mData = mData;
        }

        @Override
        public View getHeaderView(int position, View view, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (view == null) {
                holder = new HeaderViewHolder();
                view = inflater.inflate(R.layout.date_head_item2, viewGroup, false);
                holder.date = view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (HeaderViewHolder) view.getTag();
            }

            holder.date.setText(mData.get(position)
                    .getDateTime()
                    .toString(getString(R.string.format_date_type6)));

            return view;
        }

        @Override
        public long getHeaderId(int position) {
            return mData.get(position)
                    .getDateVal();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData == null ? null : mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mData == null ? 0 : mData.get(i)
                    .getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.revenue_detail_list_item, viewGroup, false);
                holder.tvTitle = view.findViewById(R.id.tv_title);
                holder.tvDateTime = view.findViewById(R.id.tv_time);
                holder.tvMoney1 = view.findViewById(R.id.tv_money1);
                holder.tvMoney2 = view.findViewById(R.id.tv_money2);
                holder.tvSign = view.findViewById(R.id.tv_sign);
                holder.tvTag = view.findViewById(R.id.tv_tag);
                holder.tvTag1 = view.findViewById(R.id.tv_tag1);
                holder.tvTag2 = view.findViewById(R.id.tv_tag2);
                holder.ivArrow = view.findViewById(R.id.iv_arrow);
                holder.tvMessage = view.findViewById(R.id.tv_message);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            RevenueDetail detail = mData.get(i);
            holder.tvTitle.setText(detail.getTitle());
            DateTime dateTime = detail.getDateTime();
            if (dateTime != null) {
                holder.tvDateTime.setText(dateTime.toString(getString(R.string
                        .format_date_type11)));
            }
            holder.tvMoney1.setText(Util.getIntegerPartFromDouble(detail.getAmount()));
            holder.tvMoney2.setText(Util.getFloatPartFromDouble(detail.getAmount()));
            holder.tvTag.setVisibility(View.GONE);
            holder.tvTag1.setVisibility(View.GONE);
            holder.tvTag2.setVisibility(View.GONE);
            holder.tvSign.setVisibility(View.GONE);
            switch (type) {
                //流水明细
                case RevenueDetail.TYPE_WATER_DETAIL:
                    if (detail.getAmount() > 0) {
                        holder.tvSign.setVisibility(View.VISIBLE);
                        holder.tvSign.setText("+");
                        holder.tvSign.setTextColor(ContextCompat.getColor(context,
                                R.color.colorAccent));
                    }
                    holder.tvMoney1.setTextColor(ContextCompat.getColor(context,
                            R.color.colorAccent));
                    holder.tvMoney2.setTextColor(ContextCompat.getColor(context,
                            R.color.colorAccent));
                    int waterDetailType = detail.getType();
                    switch (waterDetailType) {
                        case RevenueDetail.RED_ENVELOPES_RETURNED:
                        case RevenueDetail.ORDER_REFUND:
                        case RevenueDetail.ORDER_INCOME:
                            holder.ivArrow.setVisibility(View.VISIBLE);
                            break;
                        default:
                            holder.ivArrow.setVisibility(View.GONE);
                            break;
                    }
                    break;
                //订单明细
                case RevenueDetail.TYPE_ORDER_DETAIL:
                    if (detail.getAmount() > 0) {
                        holder.tvSign.setVisibility(View.VISIBLE);
                        holder.tvSign.setText("+");
                        holder.tvSign.setTextColor(ContextCompat.getColor(context,
                                R.color.colorAccent));
                    }
                    holder.tvMoney1.setTextColor(ContextCompat.getColor(context,
                            R.color.colorAccent));
                    holder.tvMoney2.setTextColor(ContextCompat.getColor(context,
                            R.color.colorAccent));
                    OrderDetail orderDetail = (OrderDetail) detail;
                    //tag顺序 退款单->线下付尾款->无效单
                    if (orderDetail.isRefundTag()) {
                        holder.tvTag.setText(context.getString(R.string.label_order_refund));
                        holder.tvTag.setVisibility(View.VISIBLE);
                    }
                    if (orderDetail.isOfflinePay()) {
                        holder.tvTag1.setVisibility(View.VISIBLE);
                        holder.tvTag1.setText(context.getString(R.string
                                .label_line_under_the_tail));
                    }
                    if (orderDetail.isInvalid()) {
                        holder.tvTag2.setText(context.getString(R.string.label_invalid_order));
                        holder.tvTag2.setVisibility(View.VISIBLE);
                    }
                    break;
                case RevenueDetail.TYPE_BOND_BALANCE_DETAIL:
                    if (TextUtils.isEmpty(detail.getMemo())) {
                        holder.tvMessage.setVisibility(View.GONE);
                    } else {
                        holder.tvMessage.setVisibility(View.VISIBLE);
                        holder.tvMessage.setText(detail.getMemo());
                    }
                    int colorValue;
                    if (detail.getAmount() > 0) {
                        colorValue = Color.parseColor("#80d089");
                        holder.tvSign.setVisibility(View.GONE);
                    } else {
                        colorValue = ContextCompat.getColor(getContext(), R.color.colorBlack2);
                        holder.tvSign.setText("-");
                        holder.tvSign.setTextColor(colorValue);
                    }
                    holder.tvMoney1.setTextColor(colorValue);
                    holder.tvMoney2.setTextColor(colorValue);
                    holder.ivArrow.setVisibility(View.GONE);
                    break;
            }

            return view;
        }

        class HeaderViewHolder {
            TextView date;
        }

        class ViewHolder {
            TextView tvTitle;
            TextView tvDateTime;
            TextView tvSign;
            TextView tvMoney1;
            TextView tvMoney2;
            TextView tvTag;
            TextView tvTag1;
            TextView tvTag2;
            TextView tvMessage;
            ImageView ivArrow;
        }
    }

    private class GetDetailsListTask extends AsyncTask<String, Integer, JSONObject> {

        String url;

        public GetDetailsListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() != null && getActivity().isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            String typedUrl = getTypedUrl();

            if (url.equals(typedUrl)) {
                int size = 0;
                if (jsonObject != null) {
                    ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject
                            ("status"));
                    if (returnStatus.getRetCode() == 0) {
                        JSONArray dataArray = jsonObject.optJSONObject("data")
                                .optJSONArray("list");
                        if (dataArray != null && dataArray.length() > 0) {
                            if (currentPage == 1) {
                                details.clear();
                            }
                            size = dataArray.length();
                            switch (type) {
                                //流水明细
                                case RevenueDetail.TYPE_WATER_DETAIL:
                                    for (int i = 0; i < size; i++) {
                                        RevenueDetail detail = new WaterDetail(dataArray
                                                .optJSONObject(
                                                i));
                                        details.add(detail);
                                    }
                                    break;
                                //订单明细
                                case RevenueDetail.TYPE_ORDER_DETAIL:
                                    for (int i = 0; i < size; i++) {
                                        RevenueDetail detail = new OrderDetail(dataArray
                                                .optJSONObject(
                                                i));
                                        details.add(detail);
                                    }
                                    break;
                                case RevenueDetail.TYPE_WITHDRAWING:
                                case RevenueDetail.TYPE_WITHDRAW_SUCCESS:
                                case RevenueDetail.TYPE_WITHDRAWING_FAIL:
                                    for (int i = 0; i < size; i++) {
                                        RevenueDetail detail = new WithdrawDetail(dataArray
                                                .optJSONObject(
                                                i));
                                        details.add(detail);
                                    }
                                    break;
                                case RevenueDetail.TYPE_BOND_BALANCE_DETAIL:
                                    for (int i = 0; i < size; i++) {
                                        RevenueDetail detail = new BondBalanceDetail(dataArray
                                                .optJSONObject(
                                                i));
                                        details.add(detail);
                                    }
                                    break;
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                returnStatus.getErrorMsg(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    adapter.notifyDataSetChanged();
                }

                if (size < 20) {
                    isEnd = true;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                } else {
                    isEnd = false;
                    endView.setVisibility(View.GONE);
                    loadView.setVisibility(View.INVISIBLE);
                }
                isLoad = false;
                setEmptyView();
            }

            super.onPostExecute(jsonObject);
        }
    }

    private void setEmptyView() {
        if (details.isEmpty()) {
            View emptyView = listView.getEmptyView();
            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
                listView.setEmptyView(emptyView);
            }
            emptyView.setVisibility(View.VISIBLE);
            ImageView imgEmptyHint = emptyView.findViewById(R.id.img_empty_list_hint);
            TextView emptyHintTextView = emptyView.findViewById(R.id.empty_hint);
            imgEmptyHint.setVisibility(View.VISIBLE);
            emptyHintTextView.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(getActivity())) {
                imgEmptyHint.setImageResource(emptyHintDrawableId);
                emptyHintTextView.setText(emptyHintId);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                emptyHintTextView.setText(R.string.net_disconnected);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault()
                .unregister(this);
    }
}
