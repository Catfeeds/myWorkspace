package com.hunliji.marrybiz.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.Rule;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.WorkActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Suncloud on 2016/1/11.
 */
public class WorkHotRecommendFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<Work>, AdapterView.OnItemClickListener, AbsListView.OnScrollListener,
        PullToRefreshBase.OnRefreshListener<ListView> {


    private View rootView;
    private View progressBar;
    private int currentPage = 1;
    private View footView;
    private boolean isEnd;
    private boolean isLoad;
    private View loadView;
    private View endView;
    private int imageWidth;
    private int imageHeight;
    private int badgeWidth;
    private int iconMargin;
    private PullToRefreshListView listView;
    private ArrayList<Work> works;
    private ObjectBindAdapter<Work> adapter;

    private String currentUrl;

    public WorkHotRecommendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = Math.round(point.x - 20 * dm.density);
        imageHeight = Math.round(imageWidth * 63 / 100);
        iconMargin = Math.round(18 * dm.density);
        if (imageWidth > 805) {
            imageWidth = Math.round(point.x * 3 / 4);
        }
        badgeWidth = Math.round(60 * dm.density);
        works = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), works, R.layout.work_list_item, this);
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Work work = (Work) getArguments().getSerializable("work");
            if (work != null) {
                currentUrl = Constants.getAbsUrl(String.format(Constants.HttpPath.HOT_RECOMMEND_URL,
                        work.getId())) + Constants.PAGE_COUNT;
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview2, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        progressBar = rootView.findViewById(R.id.progressBar);
        currentPage = 1;
        progressBar.setVisibility(View.VISIBLE);
        if (!JSONUtil.isEmpty(currentUrl)) {
            new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    public boolean isTop() {
        return listView == null || listView.isReadyForPullStart();
    }

    private class GetWorksTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        private GetWorksTask() {
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
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                return;
            }
            if (url.equals(String.format(currentUrl, currentPage))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    JSONArray jsonArray = null;
                    jsonObject = jsonObject.optJSONObject("data");
                    if (jsonObject != null) {
                        int pageCount = jsonObject.optInt("page_count", 0);
                        isEnd = pageCount <= currentPage;
                        if (isEnd) {
                            endView.setVisibility(View.VISIBLE);
                            loadView.setVisibility(View.GONE);
                        } else {
                            endView.setVisibility(View.GONE);
                            loadView.setVisibility(View.INVISIBLE);
                        }
                        jsonArray = jsonObject.optJSONArray("list");
                    }
                    if (currentPage == 1) {
                        works.clear();
                    }
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0, size = jsonArray.length(); i < size; i++) {
                            Work work = new Work(jsonArray.optJSONObject(i));
                            works.add(work);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                if (works.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();

                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);

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
                isLoad = false;
            }
            super.onPostExecute(jsonObject);
        }
    }

    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (!works.isEmpty() && listView != null) {
                int first = listView.getRefreshableView()
                        .getFirstVisiblePosition();
                int end = listView.getRefreshableView()
                        .getLastVisiblePosition();
                if (first <= end && first >= 0) {
                    for (int i = first; i <= end; i++) {
                        View v = listView.getRefreshableView()
                                .getChildAt(i - first);
                        if (v != null && v.findViewById(R.id.work_item) != null) {
                            Work work = (Work) listView.getRefreshableView()
                                    .getAdapter()
                                    .getItem(i);
                            if (work != null) {
                                if (work.getCommodityType() != 1 && work.isPreSaleOrOnSale()) {
                                    showTimeDown(v, work);
                                }
                            }
                        }
                    }
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Work work = (Work) parent.getAdapter()
                .getItem(position);
        Activity activity = getActivity();
        if (work != null && activity != null) {
            Intent intent = new Intent(getActivity(), WorkActivity.class);
            intent.putExtra("w_id", work.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @Override
    public void setViewValue(View view, Work work, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.badge321 = (ImageView) view.findViewById(R.id.badge);
            holder.activiteView = view.findViewById(R.id.work_activite_layout);
            holder.countView = view.findViewById(R.id.leave_count_layout);
            holder.lineView = view.findViewById(R.id.line_layout);
            holder.timeView = view.findViewById(R.id.leave_heures_layout);
            holder.pricesView = view.findViewById(R.id.prices_layout);
            holder.countNoDayView = view.findViewById(R.id.count_not_day_layout);
            holder.countDayView = view.findViewById(R.id.count_day_layout);
            holder.originalPrice = (TextView) view.findViewById(R.id.original_price);
            holder.originalPrice.getPaint()
                    .setAntiAlias(true);
            holder.originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            holder.discountPrice = (TextView) view.findViewById(R.id.discount_price);
            holder.label = (TextView) view.findViewById(R.id.discount_label);
            holder.day = (TextView) view.findViewById(R.id.tv_count_day);
            holder.hour1 = (TextView) view.findViewById(R.id.tv_count_hour1);
            holder.hour2 = (TextView) view.findViewById(R.id.tv_count_hour2);
            holder.minute1 = (TextView) view.findViewById(R.id.tv_count_minute1);
            holder.minute2 = (TextView) view.findViewById(R.id.tv_count_minute2);
            holder.second1 = (TextView) view.findViewById(R.id.tv_count_second1);
            holder.second2 = (TextView) view.findViewById(R.id.tv_count_second2);
            holder.leaveCount = (TextView) view.findViewById(R.id.leave_count);
            holder.cover = (ImageView) view.findViewById(R.id.work_cover);
            holder.titleName = (TextView) view.findViewById(R.id.title_name);
            holder.collectView = (TextView) view.findViewById(R.id.collect_count);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cover
                    .getLayoutParams();
            params.height = imageHeight;
            view.setTag(holder);
        }

        holder.titleName.setText(work.getTitle());
        holder.collectView.setText(String.valueOf(work.getCollectorsCount()));
        showTimeDown(view, work);
        String url = JSONUtil.getImagePath(work.getCoverPath(), imageWidth);
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadTask task = new ImageLoadTask(holder.cover);
            holder.cover.setTag(url);
            task.loadImage(url,
                    imageWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.cover.setImageBitmap(null);
        }
    }

    private void showTimeDown(View view, Work work) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            return;
        }
        if (work.getCommodityType() == 1) {
            holder.activiteView.setVisibility(View.GONE);
            holder.pricesView.setVisibility(View.GONE);
            holder.badge321.setVisibility(View.GONE);
        } else {
            holder.pricesView.setVisibility(View.VISIBLE);
            holder.discountPrice.setText(Util.formatFloat2String(work.getNowPrice()));
            if (work.getMarketPrice() > 0) {
                holder.originalPrice.setVisibility(View.VISIBLE);
                holder.originalPrice.setText(" " + Util.formatFloat2String(work.getMarketPrice())
                        + " ");
            } else {
                holder.originalPrice.setVisibility(View.GONE);
            }
            Rule rule = work.getRule();
            if (rule != null && rule.getId() > 0) {
                Date date = new Date();
                if (rule.isTimeAble() && rule.getEnd_time() != null && rule.getEnd_time()
                        .before(date)) {
                    //结束
                    holder.activiteView.setVisibility(View.GONE);
                    holder.badge321.setVisibility(View.GONE);
                    holder.lineView.setVisibility(View.GONE);
                } else {
                    boolean isTime = rule.isTimeAble() && rule.getStart_time() != null && rule
                            .getStart_time()
                            .before(date);
                    boolean isLimit = work.getLimit_num() > 0 && (!rule.isTimeAble() || rule
                            .getStart_time() == null || rule.getStart_time()
                            .before(date));
                    String badgePath = JSONUtil.getImagePath(rule.getShowimg(), badgeWidth);
                    if (!JSONUtil.isEmpty(badgePath)) {
                        holder.badge321.setVisibility(View.VISIBLE);
                        if (!badgePath.equals(holder.badge321.getTag())) {
                            ImageLoadTask task = new ImageLoadTask(holder.badge321,
                                    null,
                                    null,
                                    0,
                                    true);
                            holder.badge321.setTag(badgePath);
                            task.loadImage(badgePath,
                                    badgeWidth,
                                    ScaleMode.WIDTH,
                                    new AsyncBitmapDrawable(getResources(),
                                            R.mipmap.icon_empty_image,
                                            task));
                        }
                    } else {
                        holder.badge321.setVisibility(View.GONE);
                    }
                    if (isTime || isLimit) {
                        holder.activiteView.setVisibility(View.VISIBLE);
                        if (isTime && isLimit) {
                            holder.lineView.setVisibility(View.VISIBLE);
                        } else {
                            holder.lineView.setVisibility(View.GONE);
                        }
                        if (!JSONUtil.isEmpty(rule.getShowtxt())) {
                            holder.label.setVisibility(View.VISIBLE);
                            holder.label.setText(work.getRule()
                                    .getShowtxt());
                        } else {
                            holder.label.setVisibility(View.GONE);
                        }
                        if (isTime) {
                            //限时活动
                            holder.timeView.setVisibility(View.VISIBLE);
                            long millisUntil = rule.getEndTimeInMillis() - date.getTime();
                            long leftMillis;
                            int days = (int) (millisUntil / (1000 * 60 * 60 * 24));
                            leftMillis = millisUntil % (1000 * 60 * 60 * 24);
                            int hours = (int) (leftMillis / (1000 * 60 * 60));
                            leftMillis %= 1000 * 60 * 60;
                            int minutes = (int) (leftMillis / (1000 * 60));
                            leftMillis %= 1000 * 60;
                            int seconds = (int) (leftMillis / 1000);
                            if (days > 0) {
                                holder.countDayView.setVisibility(View.VISIBLE);
                                holder.day.setText(String.valueOf(days));
                                holder.countNoDayView.setVisibility(View.GONE);
                            } else {
                                holder.countDayView.setVisibility(View.GONE);
                                holder.countNoDayView.setVisibility(View.VISIBLE);
                                holder.hour1.setText(String.valueOf(hours / 10));
                                holder.hour2.setText(String.valueOf(hours % 10));
                                holder.minute1.setText(String.valueOf(minutes / 10));
                                holder.minute2.setText(String.valueOf(minutes % 10));
                                holder.second1.setText(String.valueOf(seconds / 10));
                                holder.second2.setText(String.valueOf(seconds % 10));
                            }
                        } else {
                            holder.timeView.setVisibility(View.GONE);
                        }
                        if (isLimit) {
                            //限量活动
                            holder.countView.setVisibility(View.VISIBLE);
                            holder.leaveCount.setText(String.valueOf(work.getLimit_num() - work
                                    .getLimit_sold_out()));
                        } else {
                            holder.countView.setVisibility(View.GONE);
                        }
                    } else {
                        holder.activiteView.setVisibility(View.GONE);
                        holder.label.setVisibility(View.GONE);
                    }
                }
            } else {
                //正常
                holder.activiteView.setVisibility(View.GONE);
                holder.badge321.setVisibility(View.GONE);
                holder.label.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad && !JSONUtil.isEmpty(currentUrl)) {
            currentPage = 1;
            new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (!works.isEmpty() && view.getLastVisiblePosition() >= (view.getCount() - 5) &&
                        !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(currentUrl, currentPage));
                }
                break;
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class ViewHolder {
        View activiteView;
        View countView;
        View lineView;
        View timeView;
        View pricesView;
        View countNoDayView;
        View countDayView;
        ImageView cover;
        TextView label;
        TextView day;
        TextView hour1;
        TextView hour2;
        TextView minute1;
        TextView minute2;
        TextView second1;
        TextView second2;
        TextView leaveCount;
        TextView discountPrice;
        TextView originalPrice;
        TextView titleName;
        TextView collectView;
        ImageView badge321;
    }


    @Override
    public void onResume() {
        if (mHandler != null) {
            mHandler.post(mRunnable);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }
}
