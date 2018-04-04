package com.hunliji.marrybiz.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Income;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class IncomeListActivity extends HljBaseActivity implements AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    @BindView(R.id.list)
    StickyListHeadersListView listView;
    @BindView(R.id.progressBar)
    View progressBar;

    private int emptyHintDrawableId;
    private int emptyHintId;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View footView;
    private View loadView;
    private View endView;

    private ArrayList<Income> incomes;
    private StickyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_list);
        ButterKnife.bind(this);

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);

        emptyHintId = R.string.hint_no_orders;
        emptyHintDrawableId = R.mipmap.icon_empty_common;

        incomes = new ArrayList<>();
        adapter = new StickyAdapter(this, incomes);
        listView.setOnScrollListener(this);
        listView.addFooterView(footView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);

        new GetIncomeListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(Constants.getAbsUrl(Constants.HttpPath.GET_INCOME_LIST),
                        currentPage));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_right);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetIncomeListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.getAbsUrl(Constants.HttpPath.GET_INCOME_LIST),
                                    currentPage));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                String json = JSONUtil.getStringFromUrl(IncomeListActivity.this, url);
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
            int size = 0;
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath.GET_INCOME_LIST),
                    currentPage))) {
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

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                        .img_empty_list_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(IncomeListActivity.this)) {
                    imgEmptyHint.setImageResource(emptyHintDrawableId);
                    emptyHintTextView.setText(emptyHintId);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    emptyHintTextView.setText(R.string.net_disconnected);
                }
            }

            isLoad = false;
            super.onPostExecute(jsonObject);
        }
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
                convertView = inflater.inflate(R.layout.income_list_item, parent, false);
                holder.titleTv = (TextView) convertView.findViewById(R.id.tv_title);
                holder.moneyTv = (TextView) convertView.findViewById(R.id.tv_money);
                holder.orderNoTv = (TextView) convertView.findViewById(R.id.tv_order_no);
                holder.paidTimeTv = (TextView) convertView.findViewById(R.id.tv_paid_time);
                holder.withdrawStatusTv = (TextView) convertView.findViewById(R.id
                        .tv_withdraw_status);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Income income = mData.get(position);
            holder.titleTv.setText(income.getBuyerName() + "——" + income.getPayType());
            holder.orderNoTv.setText(getString(R.string.label_order_no2, income.getOrderNo()));
            holder.paidTimeTv.setText(getString(R.string.label_paid_time2,
                    income.getCreatedAt()
                            .toString(getString(R.string.format_date_type11))));
            holder.withdrawStatusTv.setText(income.getStatusStr());
            if (income.getStatus() == 1) {
                holder.withdrawStatusTv.setTextColor(getResources().getColor(R.color.colorBlack2));
            } else {
                holder.withdrawStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            holder.moneyTv.setText(getString(R.string.label_price,
                    Util.formatDouble2String(income.getIncomeMoney())));

            return convertView;
        }

        class HeaderViewHolder {
            TextView date;
        }

        class ViewHolder {
            TextView titleTv;
            TextView paidTimeTv;
            TextView orderNoTv;
            TextView moneyTv;
            TextView withdrawStatusTv;
        }
    }

}
