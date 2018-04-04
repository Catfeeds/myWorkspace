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
import com.hunliji.marrybiz.model.ADVHPurchaseHistory;
import com.hunliji.marrybiz.model.ReturnStatus;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ADVHPurchaseHistoryActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<ADVHPurchaseHistory>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView> {


    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<ADVHPurchaseHistory> histories;
    private ObjectBindAdapter<ADVHPurchaseHistory> adapter;
    private boolean isLoad;
    private boolean isEnd;
    private String currentUrl;
    private int currentPage;
    private View loadView;
    private TextView endView;
    private int emptyHintId;
    private int emptyHintDrawableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advhpuchase_history);
        ButterKnife.bind(this);

        histories = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this,
                histories,
                R.layout.advh_purchase_history_item,
                this);

        emptyHintId = R.string.hint_no_purchase_history;
        emptyHintDrawableId = R.mipmap.icon_empty_common;

        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        loadView = footerView.findViewById(R.id.loading);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);

        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.ADVH_PURCHASE_HISTORY);
        currentPage = 1;
        new GetHistoryTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(currentUrl, currentPage));
    }

    @Override
    public void setViewValue(View view, ADVHPurchaseHistory advhPurchaseHistory, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
        }

        if (advhPurchaseHistory.getType() == 0) {
            holder.tvAdvhCount.setText(getString(R.string.label_advh_count2,
                    advhPurchaseHistory.getNum()));
            holder.tvPurchaseTime.setText(getString(R.string.label_purchase_time,
                    advhPurchaseHistory.getCreatedAt()
                            .toString(getString(R.string.format_date_type11))));
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(Util.formatDouble2String(advhPurchaseHistory.getPrice()));
            holder.tvRmb.setVisibility(View.VISIBLE);
        } else if (advhPurchaseHistory.getType() == 1) {
            holder.tvAdvhCount.setText(getString(R.string.label_advh_count3,
                    advhPurchaseHistory.getNum()));
            holder.tvPurchaseTime.setText(getString(R.string.label_purchase_time2,
                    advhPurchaseHistory.getCreatedAt()
                            .toString(getString(R.string.format_date_type11))));
            holder.tvPrice.setVisibility(View.GONE);
            holder.tvRmb.setVisibility(View.GONE);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    endView.setVisibility(View.GONE);
                    currentPage++;
                    new GetHistoryTask().executeOnExecutor(Constants.LISTTHEADPOOL,
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

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetHistoryTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    private class GetHistoryTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        public GetHistoryTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(ADVHPurchaseHistoryActivity.this, url);
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
            if (url.equals(String.format(currentUrl, currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject
                            ("status"));
                    if (returnStatus != null && returnStatus.getRetCode() == 0) {
                        JSONObject dataObj = jsonObject.optJSONObject("data");
                        if (dataObj != null) {
                            int pageCount = dataObj.optInt("page_count", 0);
                            isEnd = pageCount <= currentPage;
                            if (isEnd) {
                                endView.setVisibility(View.VISIBLE);
                                loadView.setVisibility(View.GONE);
                            } else {
                                endView.setVisibility(View.GONE);
                                loadView.setVisibility(View.INVISIBLE);
                            }
                            if (currentPage == 1) {
                                histories.clear();
                            }

                            JSONArray jsonArray = dataObj.optJSONArray("list");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ADVHPurchaseHistory history = new ADVHPurchaseHistory
                                            (jsonArray.optJSONObject(
                                            i));
                                    histories.add(history);
                                }
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    if (histories.isEmpty()) {
                        View emptyView = listView.getRefreshableView()
                                .getEmptyView();

                        if (emptyView == null) {
                            emptyView = findViewById(R.id.empty_hint_layout);
                            listView.getRefreshableView()
                                    .setEmptyView(emptyView);
                        }

                        ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                                .img_empty_hint);
                        TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id
                                .text_empty_hint);
                        imgEmptyHint.setVisibility(View.VISIBLE);
                        emptyHintTextView.setVisibility(View.VISIBLE);
                        if (JSONUtil.isNetworkConnected(ADVHPurchaseHistoryActivity.this)) {
                            imgEmptyHint.setImageResource(emptyHintDrawableId);
                            emptyHintTextView.setText(emptyHintId);
                        } else {
                            imgEmptyHint.setVisibility(View.GONE);
                            emptyHintTextView.setText(R.string.net_disconnected);
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'advh_purchase_history_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_advh_count)
        TextView tvAdvhCount;
        @BindView(R.id.tv_purchase_time)
        TextView tvPurchaseTime;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
