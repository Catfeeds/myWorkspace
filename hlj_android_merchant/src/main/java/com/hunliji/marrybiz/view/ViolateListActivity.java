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
import com.hunliji.marrybiz.model.Violate;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/1/8.
 */
public class ViolateListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Violate>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView>, View.OnClickListener {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<Violate> violates;
    private ObjectBindAdapter<Violate> adapter;
    private View loadView;
    private TextView endView;
    private int currentPage;
    private String currentUrl;
    private boolean isEnd;
    private boolean isLoad;
    private int lastPage;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        violates = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, violates, R.layout.violate_item, this);
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
        currentPage = lastPage = 1;
        progressBar.setVisibility(View.VISIBLE);
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.VIOLATE_LIST);
        new GetViolatesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(currentUrl, currentPage));
    }

    private class GetViolatesTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetViolatesTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(ViolateListActivity.this, url);
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
                        violates.clear();
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            violates.add(new Violate(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int pageCount = jsonObject.optInt("page_count", 0);
                    isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(violates.isEmpty() ? View.GONE : View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                } else if (!violates.isEmpty()) {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
                if (violates.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }

                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);
                    emptyHintTextView.setVisibility(View.VISIBLE);
                    if (JSONUtil.isNetworkConnected(ViolateListActivity.this)) {
                        imgEmptyHint.setVisibility(View.VISIBLE);
                        imgNetHint.setVisibility(View.GONE);
                        emptyHintTextView.setText(R.string.hint_violate_empty);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        imgNetHint.setVisibility(View.VISIBLE);
                        emptyHintTextView.setText(R.string.net_disconnected);
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new GetViolatesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
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
                        new GetViolatesTask().executeOnExecutor(Constants.LISTTHEADPOOL,
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
            AbsListView view,
            int firstVisibleItem,
            int visibleItemCount,
            int totalItemCount) {

    }

    @Override
    public void setViewValue(View view, Violate violate, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.info.setText(violate.getInfo());
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(getString(R.string.format_date_type8),
                    Locale.getDefault());
        }
        holder.time.setText(simpleDateFormat.format(violate.getTime()));
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

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'violate_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     * .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.info)
        TextView info;
        @BindView(R.id.time)
        TextView time;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
