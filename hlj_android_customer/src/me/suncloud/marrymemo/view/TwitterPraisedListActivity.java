package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.makeramen.rounded.RoundedImageView;

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
import me.suncloud.marrymemo.model.PraisedAuthor;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/7/20.商家动态点赞用户列表
 */
public class TwitterPraisedListActivity extends HljBaseActivity implements AbsListView
        .OnScrollListener, ObjectBindAdapter.ViewBinder<PraisedAuthor>, AdapterView
        .OnItemClickListener, View.OnClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<PraisedAuthor> authors;
    private ObjectBindAdapter<PraisedAuthor> adapter;
    private long id;
    private View loadView;
    private View endView;
    private boolean isEnd;
    private boolean onLoad;
    private int currentPage;

    private DisplayMetrics dm;
    private int logoSize;//商家头像

    private boolean isMerchant;

    @Override
    public void onCreate(
            Bundle savedInstanceState) {
        isMerchant = getIntent().getBooleanExtra("isMerchant", true);

        authors = new ArrayList<>();
        id = getIntent().getLongExtra("id", 0);
        adapter = new ObjectBindAdapter<>(this,
                authors,
                R.layout.merchant_twitter_praised_item,
                this);
        dm = getResources().getDisplayMetrics();
        logoSize = Math.round(40 * dm.density);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_praised_list);
        ButterKnife.bind(this);
        View footerView = View.inflate(this, R.layout.list_foot_no_more_2, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);
        currentPage = 1;
        new GetPraisedAllListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_PRAISED_LIST,
                        id,
                        currentPage)));
    }

    private class GetPraisedAllListTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        private GetPraisedAllListTask() {
            onLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
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
            if (TwitterPraisedListActivity.this.isFinishing()) {
                return;
            }
            if (url.equals(String.format(Constants.getAbsUrl(Constants.HttpPath.GET_PRAISED_LIST,
                    id,
                    currentPage)))) {
                listView.onRefreshComplete();
                int size = 0;
                if (jsonObject != null) {
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        authors.clear();
                    }
                    if (array != null && array.length() > 0) {
                        size = array.length();
                        for (int i = 0; i < size; i++) {
                            PraisedAuthor author = new PraisedAuthor(array.optJSONObject(i));
                            authors.add(author);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                if (size < 20) {
                    isEnd = true;
                    endView.setVisibility(currentPage == 1 ? View.GONE : View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                } else {
                    isEnd = false;
                    endView.setVisibility(View.GONE);
                    loadView.setVisibility(View.INVISIBLE);
                }
                if (authors.isEmpty()) {
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
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);

                    imgEmptyHint.setVisibility(View.GONE);
                    imgNetHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.GONE);

                    if (JSONUtil.isNetworkConnected(TwitterPraisedListActivity.this)) {
                        imgNetHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.no_item);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }
                    onLoad = false;
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = 1;
        new GetPraisedAllListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                Constants.getAbsUrl(String.format(Constants.HttpPath.GET_PRAISED_LIST,
                        id,
                        currentPage)));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !onLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetPraisedAllListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            Constants.getAbsUrl(String.format(Constants.HttpPath.GET_PRAISED_LIST,
                                    id,
                                    currentPage)));
                }
                break;
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void setViewValue(View view, PraisedAuthor praisedAuthor, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final User author = praisedAuthor.getAuthor();
        if (author != null) {
            if (!JSONUtil.isEmpty(author.getAvatar2())) {
                String url = JSONUtil.getImagePath(author.getAvatar2(), logoSize);
                if (!JSONUtil.isEmpty(url)) {
                    ImageLoadUtil.loadImageViewWithoutTransition(TwitterPraisedListActivity.this,
                            url,
                            holder.praisedUserLogo);
                } else {
                    ImageLoadUtil.clear(TwitterPraisedListActivity.this, holder.praisedUserLogo);
                    holder.praisedUserLogo.setImageBitmap(null);
                }
            }
            holder.tvPraisedUserName.setText(author.getNick());
            /**
             * 用户端点击用户信息，跳转到用户主页
             * 商家端不跳转
             */
            holder.llPraisedUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isMerchant) {
                        goToUserPage(author.getId());
                    }
                }
            });

        }
        holder.tvPraisedTime.setText(HljTimeUtils.getShowTime(this, praisedAuthor.getCreatedAt()));
    }

    static class ViewHolder {
        @BindView(R.id.praised_user_logo)
        RoundedImageView praisedUserLogo;
        @BindView(R.id.tv_praised_user_name)
        TextView tvPraisedUserName;
        @BindView(R.id.tv_praised_time)
        TextView tvPraisedTime;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.ll_praised_user_view)
        LinearLayout llPraisedUserView;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }

    /**
     * 抽取跳转动作
     */
    public void goToUserPage(long userId) {
        //跳转到用户主页
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }
}
