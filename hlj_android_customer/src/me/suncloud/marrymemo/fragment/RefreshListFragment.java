package me.suncloud.marrymemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/2/25.
 */
public abstract class RefreshListFragment extends ScrollAbleFragment implements AbsListView
        .OnScrollListener, PullToRefreshBase.OnRefreshListener<ListView> {

    protected View footerView;
    private View loadView;
    private TextView endView;
    protected PullToRefreshListView listView;
    protected boolean isLoad;
    private boolean isEnd;
    protected int currentPage;
    private int lastPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPage = 1;
        lastPage = 1;
        loadView = footerView.findViewById(R.id.loading);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
    }

    @Override
    public void refresh(Object... params) {
        footerView.setVisibility(View.GONE);
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        currentPage = lastPage = 1;
        onDataLoad(currentPage);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            onDataLoad(currentPage);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(getActivity())) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        onDataLoad(++currentPage);
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

    public abstract void onDataLoad(int page);

    protected void onLoadCompleted(boolean isEnd) {
        lastPage = currentPage;
        this.isEnd = isEnd;
        footerView.setVisibility(View.VISIBLE);
        if (isEnd) {
            endView.setVisibility(currentPage > 1 ? View.VISIBLE : View.GONE);
            loadView.setVisibility(View.GONE);
            endView.setText(R.string.no_more);
        } else {
            endView.setVisibility(View.GONE);
            loadView.setVisibility(View.INVISIBLE);
        }
    }

    protected void onLoadFailed() {
        currentPage = lastPage;
        footerView.setVisibility(View.VISIBLE);
        endView.setVisibility(View.VISIBLE);
        loadView.setVisibility(View.GONE);
        endView.setText(R.string.hint_net_disconnected);
    }

    @Override
    public View getScrollableView() {
        return listView == null ? null : listView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listView != null) {
            listView.getRefreshableView()
                    .setAdapter(null);
        }
    }
}
