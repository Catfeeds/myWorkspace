package me.suncloud.marrymemo.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.Identifiable;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 只有一个listview的fragment的基类 支持下拉加载 适用于Get请求的Task
 * Created by jinxin on 2016/3/2.
 */
public abstract class BaseListFragment<T extends Identifiable> extends RefreshFragment implements
        ObjectBindAdapter.ViewBinder<T>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener, AbsListView.OnItemClickListener {
    protected List<T> mDatas;
    protected ObjectBindAdapter<T> adapter;
    protected PullToRefreshListView listView;
    protected View progressBar;
    protected View footView;
    protected View rootView;

    protected boolean isLoad;
    protected View loadView;
    protected TextView endView;
    protected boolean isEnd;
    protected int currentPage;
    protected String currentUrl;

    private int layoutRes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutRes = setLayoutRes();
        mDatas = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getContext(), mDatas, layoutRes, this);
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = (TextView) footView.findViewById(R.id.no_more_hint);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, null);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        progressBar = rootView.findViewById(R.id.progressBar);
        if (mDatas.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;
            getData(currentPage);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void setEmptyView() {
        if (mDatas.isEmpty()) {
            View emptyView = listView.getRefreshableView()
                    .getEmptyView();

            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
                listView.getRefreshableView()
                        .setEmptyView(emptyView);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
            ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            imgNetHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(getActivity())) {
                imgNetHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.no_item);
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.net_disconnected);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            listView.setRefreshing();
            currentPage = 1;
            getData(currentPage);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(getActivity())) {
                        currentPage++;
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.VISIBLE);
                        getData(currentPage);
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
                }
                break;
            default:
                break;
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
    public void refresh(Object... params) {
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);
        mDatas.clear();
        adapter.notifyDataSetChanged();
        listView.onRefreshComplete();
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        getData(currentPage);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * do nothing
     */
    protected void getData(int currentPage) {

    }

    protected abstract class GetDataTask extends AsyncTask<String, Void, JSONObject> {
        private String url;

        public GetDataTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            return getData(url);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isDetached() || getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            listView.onRefreshComplete();
            if (url.equalsIgnoreCase(currentUrl)) {
                onPostData(jsonObject);
            }
            super.onPostExecute(jsonObject);
        }

        protected abstract JSONObject getData(String url);

        protected abstract void onPostData(JSONObject json);
    }

    protected abstract int setLayoutRes();
}
