package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 热销标签
 * Created by Suncloud on 2016/6/21.
 */
public class WorkMarketSelectActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<Work>, PullToRefreshBase.OnRefreshListener<ListView> {
    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<Work> works;
    private ObjectBindAdapter<Work> adapter;
    private int type; //1 人气推荐；2 本季热卖;3 超值特卖
    private int width;
    private int limit;
    private List<Long> mCheckedIds;
    private RoundProgressDialog progressDialog;
    private long[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCheckedIds = new ArrayList<>();
        works = new ArrayList<>();
        width = Util.dp2px(this, 99);
        adapter = new ObjectBindAdapter<>(this, works, R.layout.market_work_select_item, this);
        type = getIntent().getIntExtra("type", 1);
        limit = getIntent().getIntExtra("limit", 1);
        ids = getIntent().getLongArrayExtra("checkedIds");
        if (ids != null) {
            for (long id : ids) {
                mCheckedIds.add(id);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_market_select);
        setOkText(R.string.complete);
        setTitle(type == 1 ? R.string.label_work_rec : type == 2 ? R.string.label_work_top : R
                .string.label_work_cheap);
        ButterKnife.bind(this);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Work work = (Work) parent.getAdapter()
                        .getItem(position);
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(WorkMarketSelectActivity.this, WorkActivity.class);
                    intent.putExtra("w_id", work.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        onRefresh(null);
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        if (mCheckedIds.isEmpty() && (ids == null || ids.length == 0)) {
            onBackPressed();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            StringBuilder idsStr = new StringBuilder();
            if (!mCheckedIds.isEmpty()) {
                for (Long id : mCheckedIds) {
                    idsStr.append(idsStr.length() > 0 ? "," : "")
                            .append(id);
                }
                jsonObject.put("type", "add");
            } else {
                for (long id : ids) {
                    idsStr.append(idsStr.length() > 0 ? "," : "")
                            .append(id);
                }
                jsonObject.put("type", "delete");
            }
            jsonObject.put("setmeal_ids", idsStr.toString());
            jsonObject.put("hot_tag", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog == null) {
            progressDialog = JSONUtil.getRoundProgress(this);
            progressDialog.onLoadComplate();
            progressDialog.setCancelable(false);
        }
        new NewHttpPostTask(this, new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                if (isFinishing()) {
                    return;
                }
                JSONObject object = (JSONObject) obj;
                Status status = null;
                if (object.optJSONObject("status") != null) {
                    status = new Status(object.optJSONObject("status"));
                }
                if (status != null && status.getRetCode() == 0) {
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.EventType.WORK_MARKET_REFRESH_FLAG,
                                    null));
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.onComplate(new RoundProgressDialog.OnUpLoadComplate() {
                            @Override
                            public void onUpLoadCompleted() {
                                onBackPressed();
                            }
                        });
                    } else {
                        onBackPressed();
                    }
                } else {
                    progressDialog.dismiss();
                    Util.showToast(WorkMarketSelectActivity.this,
                            status == null ? null : status.getErrorMsg(),
                            R.string.msg_failed_finish_order);
                }
            }

            @Override
            public void onRequestFailed(Object obj) {
                if (isFinishing()) {
                    return;
                }
                progressDialog.dismiss();
                Util.showToast(WorkMarketSelectActivity.this,
                        null,
                        R.string.msg_failed_finish_order);
            }
        }).executeOnExecutor(Constants.INFOTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.WORK_TAG_URL),
                jsonObject.toString());
    }

    @Override
    public void setViewValue(View view, final Work work, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String path = JSONUtil.getImagePath(work.getCoverPath(), width);
        Glide.with(this)
                .load(path)
                .into(holder.ivCover);
        holder.tvTitle.setText(work.getTitle());
        holder.tvPrice.setText(getString(R.string.label_price5,
                Util.formatDouble2String(work.getShowPrice())));
        holder.checkedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckedIds.contains(work.getId())) {
                    mCheckedIds.remove(work.getId());
                    adapter.notifyDataSetChanged();
                } else if (mCheckedIds.size() < limit) {
                    mCheckedIds.add(work.getId());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        holder.checkedView.setImageResource(mCheckedIds.contains(work.getId()) ? R.drawable
                .icon_check_round_40_40_selected : R.drawable.icon_check_round_40_40_normal);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetWorksTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(Constants.HttpPath.MARKET_WORKS_URL, type));
    }

    private class GetWorksTask extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(WorkMarketSelectActivity.this, params[0]);
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
            listView.onRefreshComplete();
            if (jsonObject != null) {
                works.clear();
                JSONArray array = jsonObject.optJSONArray("list");
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        works.add(new Work(array.optJSONObject(i)));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if (works.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }
                Util.setEmptyView(WorkMarketSelectActivity.this,
                        emptyView,
                        R.string.hint_no_works,
                        R.mipmap.icon_empty_common,
                        0,
                        0);
            }
            super.onPostExecute(jsonObject);
        }
    }

    static class ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.checked_view)
        ImageView checkedView;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}