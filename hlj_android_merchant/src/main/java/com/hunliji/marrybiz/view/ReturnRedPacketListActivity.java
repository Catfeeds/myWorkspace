package com.hunliji.marrybiz.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.ReturnRedPacket;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReturnRedPacketListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<ReturnRedPacket>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView> {

    @BindView(R.id.list)
    PullToRefreshListView listView;
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
    private SimpleDateFormat simpleDateFormat;

    private ArrayList<ReturnRedPacket> returnRedPackets;
    private ObjectBindAdapter<ReturnRedPacket> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_red_packet_list);
        ButterKnife.bind(this);

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);

        emptyHintId = R.string.hint_no_orders;
        emptyHintDrawableId = R.mipmap.icon_empty_order;

        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type11));

        returnRedPackets = new ArrayList<>();

        adapter = new ObjectBindAdapter<>(this,
                returnRedPackets,
                R.layout.return_redpacket_list_item);
        adapter.setViewBinder(this);
        listView.setAdapter(adapter);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);

        if (returnRedPackets.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;
            new GetReturnRedPacketListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.getAbsUrl(Constants.HttpPath.GET_RETURN_REDPACKET_LIST),
                            currentPage));
        }
    }

    @Override
    public void setViewValue(View view, ReturnRedPacket returnRedPacket, int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.tvOrderNo.setText(getString(R.string.label_invalid_order_no,
                    returnRedPacket.getOrderNo()));
            holder.tvMoney.setText(getString(R.string.label_price,
                    Util.formatDouble2String(returnRedPacket.getMoney())));
            holder.tvReturnStatus.setText(returnRedPacket.getIsPaid() == 1 ? R.string
                    .label_returned : R.string.label_not_returned);
            holder.tvUpdatedAt.setText(simpleDateFormat.format(returnRedPacket.getUpdatedAt()));
            if (returnRedPacket.getIsPaid() == 0) {
                holder.tvReturnStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                holder.tvReturnStatus.setTextColor(getResources().getColor(R.color.colorGray));
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetReturnRedPacketListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(Constants.getAbsUrl(Constants.HttpPath
                                    .GET_RETURN_REDPACKET_LIST),
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
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetReturnRedPacketListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(Constants.getAbsUrl(Constants.HttpPath.GET_RETURN_REDPACKET_LIST),
                            currentPage));
        }
    }

    public class GetReturnRedPacketListTask extends AsyncTask<String, Integer, JSONObject> {
        String url;

        public GetReturnRedPacketListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(ReturnRedPacketListActivity.this, url);
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
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath
                    .GET_RETURN_REDPACKET_LIST),
                    currentPage))) {
                loadView.setVisibility(View.INVISIBLE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    JSONObject statusObject = jsonObject.optJSONObject("status");
                    if (statusObject != null && statusObject.optInt("RetCode", -1) == 0) {
                        // 数据正确
                        JSONArray jsonArray = jsonObject.optJSONObject("data")
                                .optJSONArray("list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            size = jsonArray.length();
                            if (currentPage == 1) {
                                returnRedPackets.clear();
                            }
                            for (int i = 0; i < size; i++) {
                                JSONObject dataObject = jsonArray.optJSONObject(i);
                                ReturnRedPacket returnRedPacket = new ReturnRedPacket(dataObject);
                                returnRedPackets.add(returnRedPacket);
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

            if (returnRedPackets.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();

                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                        .img_empty_list_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(ReturnRedPacketListActivity.this)) {
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

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'return_redpacket_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_order_no)
        TextView tvOrderNo;
        @BindView(R.id.tv_updated_at)
        TextView tvUpdatedAt;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_return_status)
        TextView tvReturnStatus;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
