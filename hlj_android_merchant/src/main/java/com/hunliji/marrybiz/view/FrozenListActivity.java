package com.hunliji.marrybiz.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.FrozenRecord;
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

/**
 * 待结算记录列表
 */
public class FrozenListActivity extends HljBaseActivity implements AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {

    @BindView(R.id.list)
    StickyListHeadersListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int emptyHintDrawableId;
    private int emptyHintId;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View footView;
    private View loadView;
    private View endView;

    private ArrayList<FrozenRecord> frozenRecords;
    private StickyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frozen_list);
        ButterKnife.bind(this);

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);

        emptyHintId = R.string.hint_no_orders;
        emptyHintDrawableId = R.mipmap.icon_empty_common;

        frozenRecords = new ArrayList<>();
        adapter = new StickyAdapter(this, frozenRecords);
        listView.setOnScrollListener(this);
        listView.addFooterView(footView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);

        new GetFrozenListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(Constants.getAbsUrl(Constants.HttpPath.GET_FROZEN_LIST),
                        currentPage));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetFrozenListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.getAbsUrl(Constants.HttpPath.GET_FROZEN_LIST),
                                    currentPage));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FrozenRecord record = (FrozenRecord) adapterView.getAdapter()
                .getItem(i);
        if (record != null) {
            Intent intent = null;
            if (record.getOrderType() == 0) {
                // 定制套餐
                intent = new Intent(this, CustomOrderDetailActivity.class);
                intent.putExtra("id", record.getId());
            } else if (record.getOrderType() == 1) {
                // 普通套餐
                intent = new Intent(this, NewOrderDetailActivity.class);
                intent.putExtra("id", record.getId());
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }
    }

    public class StickyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;
        private ArrayList<FrozenRecord> mData;

        public StickyAdapter(Context context, ArrayList<FrozenRecord> records) {
            inflater = LayoutInflater.from(context);
            mData = records;
        }

        @Override
        public View getHeaderView(int i, View view, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (view == null) {
                holder = new HeaderViewHolder();
                view = inflater.inflate(R.layout.date_head_item2, viewGroup, false);
                holder.date = (TextView) view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (HeaderViewHolder) view.getTag();
            }

            holder.date.setText(mData.get(i)
                    .getCreatedAt()
                    .toString(getString(R.string.format_date_type6)));

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
                convertView = inflater.inflate(R.layout.frozen_list_item, parent, false);
                holder.tvMoney1 = (TextView) convertView.findViewById(R.id.tv_money1);
                holder.tvMoney2 = (TextView) convertView.findViewById(R.id.tv_money2);
                holder.tvOrderNo = (TextView) convertView.findViewById(R.id.tv_order_no);
                holder.tvPaidTime = (TextView) convertView.findViewById(R.id.tv_paid_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FrozenRecord record = mData.get(position);
            holder.tvOrderNo.setText(getString(R.string.label_order_no4, record.getOrderNo()));
            holder.tvMoney1.setText(Util.getIntegerPartFromDouble(record.getFrozenAmount()));
            holder.tvMoney2.setText(Util.getFloatPartFromDouble(record.getFrozenAmount()));
            holder.tvPaidTime.setText(record.getCreatedAt()
                    .toString(getString(R.string.format_date_type11)));

            return convertView;
        }

        class HeaderViewHolder {
            TextView date;
        }

        class ViewHolder {
            TextView tvMoney1;
            TextView tvMoney2;
            TextView tvPaidTime;
            TextView tvOrderNo;
        }
    }

    public class GetFrozenListTask extends AsyncTask<String, Integer, JSONObject> {
        String url;

        public GetFrozenListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            url = strings[0];
            try {
                String json = JSONUtil.getStringFromUrl(FrozenListActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            int size = 0;
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath.GET_FROZEN_LIST),
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
                                frozenRecords.clear();
                            }
                            for (int i = 0; i < size; i++) {
                                JSONObject dataObject = jsonArray.optJSONObject(i);
                                FrozenRecord record = new FrozenRecord(dataObject);
                                frozenRecords.add(record);
                            }
                        }
                    }
                }

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

            if (frozenRecords.isEmpty()) {
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
                if (JSONUtil.isNetworkConnected(FrozenListActivity.this)) {
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

}
