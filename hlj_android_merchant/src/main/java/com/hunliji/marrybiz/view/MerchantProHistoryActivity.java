package com.hunliji.marrybiz.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.ProHistory;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/6/20.
 */
public class MerchantProHistoryActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<ProHistory>, PullToRefreshBase.OnRefreshListener<ListView>, AbsListView
        .OnScrollListener, View.OnClickListener {
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private ArrayList<ProHistory> histories;
    private ObjectBindAdapter<ProHistory> adapter;
    private int currentPage;
    private String currentUrl;
    private boolean isEnd;
    private boolean isLoad;
    private int lastPage;
    private View loadView;
    private TextView endView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        histories = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, histories, R.layout.pro_history_item, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ButterKnife.bind(this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        loadView = footerView.findViewById(R.id.loading);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        currentPage = lastPage = 1;
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.PRO_HISTORY_LIST);
        onRefresh(null);

    }

    @Override
    public void setViewValue(View view, ProHistory proHistory, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.tvProTime.setText(getString(R.string.label_merchant_pro_time, 1));
            view.setTag(holder);
        }
        holder.line.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        holder.tvPayDate.setText(proHistory.getCreatedAt() != null ? proHistory.getCreatedAt()
                .toString(getString(R.string.format_date_type11)) : null);

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetHistoryList().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        new GetHistoryList().executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(currentUrl, currentPage));
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_more_hint:
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                break;
        }
    }

    private class GetHistoryList extends AsyncTask<String, Object, JSONObject> {

        private String url;

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(MerchantProHistoryActivity.this, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(String.format(currentUrl, currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    lastPage = currentPage;
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        histories.clear();
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            histories.add(new ProHistory(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int pageCount = jsonObject.optInt("page_count", 0);
                    isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(histories.isEmpty() || currentPage == 1 ? View.GONE
                                : View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                } else if (!histories.isEmpty()) {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
                if (histories.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(MerchantProHistoryActivity.this,
                            emptyView,
                            R.string.hint_pro_history_empty,
                            R.mipmap.icon_empty_common,
                            0,
                            0);
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.tv_pro_time)
        TextView tvProTime;
        @BindView(R.id.tv_pay_date)
        TextView tvPayDate;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
