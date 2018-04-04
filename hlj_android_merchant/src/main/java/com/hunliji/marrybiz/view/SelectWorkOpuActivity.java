package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.CustomSetmeal;
import com.hunliji.marrybiz.model.Identifiable;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectWorkOpuActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Identifiable>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView>, AdapterView.OnItemClickListener {


    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.root_item)
    TextView okItem;
    private int type; // 选择类型

    private int emptyHintDrawableId;
    private int emptyHintId;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View loadView;
    private View endView;
    private ArrayList<Identifiable> works;
    private ObjectBindAdapter<Identifiable> adapter;
    private Identifiable selectedWork;
    private long merchantId;
    private int imageWidth;
    private int imageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_work_opu);
        ButterKnife.bind(this);
        merchantId = Session.getInstance()
                .getCurrentUser(this)
                .getMerchantId();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = Math.round(100 * dm.density);
        imageHeight = Math.round(64 * dm.density);
        type = getIntent().getIntExtra("type", 0);
        setTitle(type == 0 ? R.string.title_activity_select_work_opu : R.string.label_select_opu);
        View headView = getLayoutInflater().inflate(R.layout.empty_placeholder5, null);
        View footView = getLayoutInflater().inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);
        emptyHintId = R.string.hint_no_works;
        emptyHintDrawableId = R.mipmap.icon_empty_order;
        works = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, works, R.layout.select_work_list_item);
        adapter.setViewBinder(this);
        listView.getRefreshableView()
                .addHeaderView(headView, null, false);
        listView.getRefreshableView()
                .addFooterView(footView, null, false);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        if (works.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            onRefresh(null);
        }
        setOkText(R.string.label_confirm);
        okItem.setClickable(false);
        okItem.setTextColor(getResources().getColor(R.color.colorGray));
    }

    @Override
    public void onOkButtonClick() {
        if (selectedWork != null) {
            Intent intent = getIntent();
            if (selectedWork instanceof Work) {
                if (type != 0) {
                    ((Work) selectedWork).setActualPrice(0);
                }
                intent.putExtra("work", selectedWork);
            }
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
        super.onOkButtonClick();
    }

    @Override
    public void setViewValue(View view, Identifiable identifiable, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.cover
                    .getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;

            view.setTag(holder);
        }
        if (identifiable instanceof Work) {
            Work work = (Work) identifiable;
            holder.custom.setVisibility(View.GONE);
            String url = JSONUtil.getImagePath(work.getCoverPath(), imageWidth);
            Glide.with(this)
                    .load(url)
                    .into(holder.cover);
            holder.title.setText(work.getTitle());
            if (type == 0) {
                holder.installment.setVisibility(work.isInstallment() ? View.VISIBLE : View.GONE);
                holder.actualPrice.setText(getString(R.string.label_price,
                        Util.formatDouble2String(work.getActualPrice())));
                holder.actualPrice.setVisibility(View.VISIBLE);
            } else {
                holder.installment.setVisibility(View.GONE);
                holder.actualPrice.setVisibility(View.GONE);
            }
        } else if (identifiable instanceof CustomSetmeal) {
            CustomSetmeal customWork = (CustomSetmeal) identifiable;
            holder.custom.setVisibility(View.VISIBLE);
            holder.installment.setVisibility(View.GONE);
            String url = JSONUtil.getImagePath(customWork.getCoverPath(), imageWidth);
            Glide.with(SelectWorkOpuActivity.this)
                    .load(url)
                    .into(holder.cover);
            holder.title.setText(customWork.getTitle());
            holder.actualPrice.setText(getString(R.string.label_price,
                    Util.formatDouble2String(customWork.getActualPrice())));
            holder.actualPrice.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
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
            new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Identifiable object = (Identifiable) parent.getAdapter()
                .getItem(position);
        if (object != null) {
            selectedWork = object;
            okItem.setClickable(true);
            okItem.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            okItem.setClickable(false);
            okItem.setTextColor(getResources().getColor(R.color.colorGray));
        }
    }

    private class GetWorksTask extends AsyncTask<String, JSONObject, JSONObject> {

        String url;

        public GetWorksTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(SelectWorkOpuActivity.this, url);
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
            if (isFinishing()) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            if (url.equals(getUrl())) {
                listView.onRefreshComplete();
                int size;
                if (jsonObject != null) {
                    int count = jsonObject.optInt("total_count", 0);
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        works.clear();
                    }
                    if (array != null && array.length() > 0) {
                        size = array.length();
                        for (int i = 0; i < size; i++) {
                            works.add(new Work(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (works.size() < count) {
                        isEnd = false;
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    } else {
                        isEnd = true;
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                    }
                }

                isLoad = false;
                if (works.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    emptyView.setVisibility(View.VISIBLE);
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_list_hint);
                    TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    emptyHintTextView.setVisibility(View.VISIBLE);
                    if (JSONUtil.isNetworkConnected(SelectWorkOpuActivity.this)) {
                        imgEmptyHint.setImageResource(emptyHintDrawableId);
                        emptyHintTextView.setText(emptyHintId);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        emptyHintTextView.setText(R.string.net_disconnected);
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private String getUrl() {
        return String.format(Constants.getAbsUrl(Constants.HttpPath.GET_MERCHANT_WORKS_AND_CASES),
                merchantId,
                type == 0 ? "set_meal" : "case",
                null,
                currentPage,
                20);
    }

    static class ViewHolder {
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.checkbox)
        ImageView checkbox;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.actual_price)
        TextView actualPrice;
        @BindView(R.id.custom)
        View custom;
        @BindView(R.id.installment)
        View installment;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
