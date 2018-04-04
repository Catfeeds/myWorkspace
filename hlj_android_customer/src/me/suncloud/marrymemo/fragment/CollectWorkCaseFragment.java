package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;

public class CollectWorkCaseFragment extends RefreshListFragment implements ObjectBindAdapter
        .ViewBinder<Work>, AdapterView.OnItemClickListener {

    private View progressBar;
    private View rootView;
    private long filterId;
    private int imageWidth;
    private int casePadding;
    private int commodityType;
    private ArrayList<Work> works;
    private ObjectBindAdapter<Work> adapter;

    public static CollectWorkCaseFragment newInstance(int commodityType, long filterId) {
        CollectWorkCaseFragment fragment = new CollectWorkCaseFragment();
        Bundle args = new Bundle();
        args.putInt("commodityType", commodityType);
        args.putLong("id", filterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        imageWidth = Util.dp2px(getActivity(), 116);
        casePadding = Util.dp2px(getActivity(), 8);
        works = new ArrayList<>();
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        adapter = new ObjectBindAdapter<>(getActivity(), works, R.layout.collect_item, this);
        if (getArguments() != null) {
            filterId = getArguments().getLong("id", 0);
            commodityType = getArguments().getInt("commodityType", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.getRefreshableView()
                .setOnScrollListener(this);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        if (CommonUtil.isCollectionEmpty(works)) {
            footerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(listView);
        }
        return rootView;
    }

    private String getUrl(int page) {
        return Constants.getAbsUrl(String.format(Constants.HttpPath.FAVORITES_WORKS_URL,
                commodityType,
                page,
                filterId));
    }

    @Override
    public void refresh(Object... params) {
        if (listView == null) {
            return;
        }
        if (params != null && params.length > 0 && params[0] instanceof Long) {
            long id = (Long) params[0];
            if (filterId != id) {
                filterId = id;
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                works.clear();
                adapter.notifyDataSetChanged();
                super.refresh(params);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Work work = (Work) parent.getAdapter()
                .getItem(position);
        Activity activity = getActivity();
        if (work != null && activity != null) {
            Intent intent;
            if (commodityType != 1 && work.getCommodityType() == 0) {
                intent = new Intent(getActivity(), WorkActivity.class);
            } else {
                intent = new Intent(getActivity(), CaseDetailActivity.class);
            }
            intent.putExtra("id", work.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

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
                String json = JSONUtil.getStringFromUrl(url);
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
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (url.equals(getUrl(currentPage))) {
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    int pageCount = jsonObject.optInt("page_count", 0);
                    if (onTabTextChangeListener != null) {
                        int totalCount = jsonObject.optInt("total_count", 0);
                        onTabTextChangeListener.onTabTextChange(totalCount);
                    }
                    onLoadCompleted(pageCount <= currentPage);
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        works.clear();
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            works.add(new Work(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if (!works.isEmpty()) {
                    onLoadFailed();
                }
                if (works.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    Util.setEmptyView(getActivity(),
                            emptyView,
                            commodityType == 0 ? R.string.hint_collect_work_empty : R.string
                                    .hint_collect_case_empty,
                            R.mipmap.icon_empty_common,
                            0,
                            0);
                }
                isLoad = false;
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onDataLoad(int page) {
        new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    @Override
    public void setViewValue(View view, Work work, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvTitle.setText(work.getTitle());
        holder.lineLayout.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if (commodityType == 1 || work.getCommodityType() == 1) {
            holder.infoLayout.setPadding(0, casePadding, 0, casePadding);
            holder.tvRmb.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);
            holder.tvDescribe.setVisibility(View.VISIBLE);
            holder.tvDescribe.setText(work.getMerchantName());
        } else {
            holder.infoLayout.setPadding(0, 0, 0, 0);
            holder.tvRmb.setVisibility(View.VISIBLE);
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            if (work.isSoldOut()) {
                holder.tvDescribe.setVisibility(View.VISIBLE);
                holder.tvDescribe.setText(R.string.label_invalid);
            } else if (work.getShowPrice() < work.getOldPrice()) {
                holder.tvDescribe.setVisibility(View.VISIBLE);
                holder.tvDescribe.setText(getString(R.string.label_discount,
                        Util.formatDouble2String(work.getOldPrice() - work.getShowPrice())));
            } else {
                holder.tvDescribe.setVisibility(View.GONE);
            }
        }
        String coverPath = JSONUtil.getImagePath(work.getCoverPath(), imageWidth);
        if (!JSONUtil.isEmpty(coverPath)) {
            ImageLoadTask task = new ImageLoadTask(holder.imgCover);
            holder.imgCover.setTag(coverPath);
            task.loadImage(coverPath,
                    imageWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.imgCover.setImageBitmap(null);
        }
        if (!JSONUtil.isEmpty(work.getKind())) {
            holder.tvProperty.setText(work.getKind());
        } else if (work.getMerchant() != null && !JSONUtil.isEmpty(work.getMerchant()
                .getPropertyName())) {
            holder.tvProperty.setText(work.getMerchant()
                    .getPropertyName());
        } else {
            holder.tvProperty.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_describe)
        TextView tvDescribe;
        @BindView(R.id.info_layout)
        LinearLayout infoLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
