package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.MyScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
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
 * Created by Suncloud on 2016/6/20.
 */
public class WorkMarketActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<MyScrollView> {
    @BindView(R.id.tv_rec_limit)
    TextView tvRecLimit;
    @BindView(R.id.rec_works)
    LinearLayout recWorksLayout;
    @BindView(R.id.btn_rec_add)
    Button btnRecAdd;
    @BindView(R.id.rec_layout)
    LinearLayout recLayout;
    @BindView(R.id.tv_top_limit)
    TextView tvTopLimit;
    @BindView(R.id.top_works)
    LinearLayout topWorksLayout;
    @BindView(R.id.btn_top_add)
    Button btnTopAdd;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;
    @BindView(R.id.tv_cheap_limit)
    TextView tvCheapLimit;
    @BindView(R.id.cheap_works)
    LinearLayout cheapWorksLayout;
    @BindView(R.id.btn_cheap_add)
    Button btnCheapAdd;
    @BindView(R.id.cheap_layout)
    LinearLayout cheapLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.rec_add_layout)
    RelativeLayout recAddLayout;
    @BindView(R.id.top_add_layout)
    RelativeLayout topAddLayout;
    @BindView(R.id.cheap_add_layout)
    RelativeLayout cheapAddLayout;
    private int width;
    private int recCount;
    private int topCount;
    private int cheapCount;
    private ArrayList<Work> recWorks;  //人气推荐
    private ArrayList<Work> topWorks;   //本季热卖
    private ArrayList<Work> cheapWorks; //超值特价
    private RoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        recWorks = new ArrayList<>();
        topWorks = new ArrayList<>();
        cheapWorks = new ArrayList<>();
        super.onCreate(savedInstanceState);
        width = Util.dp2px(this, 99);
        setContentView(R.layout.activity_work_market);
        setOkText(R.string.menu_help);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setOnRefreshListener(this);
        onRefresh(null);
        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }
    }

    @Override
    public void onOkButtonClick() {
        super.onOkButtonClick();
        progressBar.setVisibility(View.VISIBLE);
        LinkUtil.getInstance(this)
                .getLink(Constants.LinkNames.HOT_TAG_HELP, new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {
                        if (isFinishing()) {
                            return;
                        }
                        progressBar.setVisibility(View.GONE);
                        String url = (String) obj;
                        if (!JSONUtil.isEmpty(url)) {
                            Intent intent = new Intent(WorkMarketActivity.this,
                                    HljWebViewActivity.class);
                            intent.putExtra("path", url);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        if (isFinishing()) {
                            return;
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void onWorkSelect(View view) {
        Intent intent = new Intent(this, WorkMarketSelectActivity.class);
        List<Work> works = null;
        switch (view.getId()) {
            case R.id.btn_rec_add:
                works = recWorks;
                intent.putExtra("type", 1);
                intent.putExtra("limit", recCount + recWorks.size());
                break;
            case R.id.btn_top_add:
                works = topWorks;
                intent.putExtra("type", 2);
                intent.putExtra("limit", topCount + topWorks.size());
                break;
            case R.id.btn_cheap_add:
                works = cheapWorks;
                intent.putExtra("type", 3);
                intent.putExtra("limit", cheapCount + cheapWorks.size());
                break;
        }
        if (works != null && !works.isEmpty()) {
            long[] ids = new long[works.size()];
            for (int i = 0, size = works.size(); i < size; i++) {
                Work work = works.get(i);
                ids[i] = work.getId();
            }
            intent.putExtra("checkedIds", ids);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
        new GetMarketTask().executeOnExecutor(Constants.INFOTHEADPOOL);
    }

    private class GetMarketTask extends AsyncTask<Object, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(WorkMarketActivity.this,
                        Constants.getAbsUrl(Constants.HttpPath.WORK_MARKET_INFO));
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
            scrollView.onRefreshComplete();
            if (jsonObject != null) {
                //人气推荐
                recWorks.clear();
                JSONArray recArray = jsonObject.optJSONArray("rec");
                recCount = jsonObject.optInt("rec_count");
                if (recArray != null && recArray.length() > 0) {
                    for (int i = 0, size = recArray.length(); i < size; i++) {
                        recWorks.add(new Work(recArray.optJSONObject(i)));
                    }
                }
                recLayout.setVisibility(View.VISIBLE);
                if (recCount > 0 || recWorks.size() > 0) {
                    tvRecLimit.setText(getString(R.string.label_count_limit, recCount));
                    recAddLayout.setVisibility(recCount > 0 ? View.VISIBLE : View.GONE);
                    int size = recWorks.size();
                    int count = recWorksLayout.getChildCount();
                    if (count > size) {
                        recWorksLayout.removeViews(size, count - size);
                    }
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Work work = recWorks.get(i);
                            View view;
                            if (i < count) {
                                view = recWorksLayout.getChildAt(i);
                            } else {
                                view = View.inflate(WorkMarketActivity.this,
                                        R.layout.market_work_item,
                                        null);
                                recWorksLayout.addView(view);
                            }
                            setWorkViewValue(view, work, 1);
                        }
                    }
                }
                //本季热卖
                topWorks.clear();
                JSONArray topArray = jsonObject.optJSONArray("top");
                topCount = jsonObject.optInt("top_count");
                if (topArray != null && topArray.length() > 0) {
                    for (int i = 0, size = topArray.length(); i < size; i++) {
                        topWorks.add(new Work(topArray.optJSONObject(i)));
                    }
                }
                topLayout.setVisibility(View.VISIBLE);
                if (topCount > 0 || topWorks.size() > 0) {
                    tvTopLimit.setText(getString(R.string.label_count_limit, topCount));
                    topAddLayout.setVisibility(topCount > 0 ? View.VISIBLE : View.GONE);
                    int size = topWorks.size();
                    int count = topWorksLayout.getChildCount();
                    if (count > size) {
                        topWorksLayout.removeViews(size, count - size);
                    }
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Work work = topWorks.get(i);
                            View view;
                            if (i < count) {
                                view = topWorksLayout.getChildAt(i);
                            } else {
                                view = View.inflate(WorkMarketActivity.this,
                                        R.layout.market_work_item,
                                        null);
                                topWorksLayout.addView(view);
                            }
                            setWorkViewValue(view, work, 2);
                        }
                    }
                }
                //超值特卖
                cheapWorks.clear();
                JSONArray cheapArray = jsonObject.optJSONArray("cheap");
                cheapCount = jsonObject.optInt("cheap_count");
                if (cheapArray != null && cheapArray.length() > 0) {
                    for (int i = 0, size = cheapArray.length(); i < size; i++) {
                        cheapWorks.add(new Work(cheapArray.optJSONObject(i)));
                    }
                }
                cheapLayout.setVisibility(View.VISIBLE);
                if (cheapCount > 0 || cheapWorks.size() > 0) {
                    tvCheapLimit.setText(getString(R.string.label_count_limit, cheapCount));
                    cheapAddLayout.setVisibility(cheapCount > 0 ? View.VISIBLE : View.GONE);
                    int size = cheapWorks.size();
                    int count = cheapWorksLayout.getChildCount();
                    if (count > size) {
                        cheapWorksLayout.removeViews(size, count - size);
                    }
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Work work = cheapWorks.get(i);
                            View view;
                            if (i < count) {
                                view = cheapWorksLayout.getChildAt(i);
                            } else {
                                view = View.inflate(WorkMarketActivity.this,
                                        R.layout.market_work_item,
                                        null);
                                cheapWorksLayout.addView(view);
                            }
                            setWorkViewValue(view, work, 3);
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void setWorkViewValue(final View view, final Work work, final int type) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkMarketActivity.this, WorkActivity.class);
                intent.putExtra("w_id", work.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
        String path = JSONUtil.getImagePath(work.getCoverPath(), width);
        Glide.with(this)
                .load(path)
                .into(holder.ivCover);
        holder.tvTitle.setText(work.getTitle());
        holder.tvPrice.setText(getString(R.string.label_price5,
                Util.formatDouble2String(work.getShowPrice())));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("setmeal_ids", work.getId());
                    jsonObject.put("type", "delete");
                    jsonObject.put("hot_tag", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressDialog == null) {
                    progressDialog = JSONUtil.getRoundProgress(WorkMarketActivity.this);
                    progressDialog.onLoadComplate();
                    progressDialog.setCancelable(false);
                } else {
                    progressDialog.reset();
                    progressDialog.show();
                }
                new NewHttpPostTask(WorkMarketActivity.this, new OnHttpRequestListener() {
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
                            if (type == 1) {
                                recWorksLayout.removeView(view);
                                recCount++;
                                tvRecLimit.setText(getString(R.string.label_count_limit, recCount));
                                recAddLayout.setVisibility(View.VISIBLE);
                                recWorks.remove(work);
                            } else if (type == 2) {
                                topWorksLayout.removeView(view);
                                topCount++;
                                tvTopLimit.setText(getString(R.string.label_count_limit, topCount));
                                topAddLayout.setVisibility(View.VISIBLE);
                                topWorks.remove(work);
                            } else if (type == 3) {
                                cheapWorksLayout.removeView(view);
                                cheapCount++;
                                tvCheapLimit.setText(getString(R.string.label_count_limit,
                                        cheapCount));
                                cheapAddLayout.setVisibility(View.VISIBLE);
                                cheapWorks.remove(work);
                            }
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.onComplate();
                            }
                        } else {
                            progressDialog.dismiss();
                            Util.showToast(WorkMarketActivity.this,
                                    status == null ? null : status.getErrorMsg(),
                                    R.string.msg_channel_delete_error);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {
                        if (isFinishing()) {
                            return;
                        }
                        progressDialog.dismiss();
                        Util.showToast(WorkMarketActivity.this,
                                null,
                                R.string.msg_channel_delete_error);
                    }
                }).executeOnExecutor(Constants.INFOTHEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.WORK_TAG_URL),
                        jsonObject.toString());
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.WORK_MARKET_REFRESH_FLAG) {
            onRefresh(null);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .unregister(this);
        }
    }
}
