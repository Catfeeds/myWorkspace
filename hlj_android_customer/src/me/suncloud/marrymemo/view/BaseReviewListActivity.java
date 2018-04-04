package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter.ViewBinder;
import me.suncloud.marrymemo.model.Reply;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

public abstract class BaseReviewListActivity extends HljBaseActivity implements
        ViewBinder<Reply>, OnItemClickListener, OnItemLongClickListener,
        OnRefreshListener<ListView>, OnScrollListener {

    private PullToRefreshListView listView;
    protected int count;
    protected String titleStr;
    private ArrayList<Reply> list;
    private ObjectBindAdapter<Reply> adapter;
    private TextView content;
    protected long userId;
    private int currentPage;
    private int lastPage;
    private boolean isEnd;
    private boolean isLoad;
    private View loadView;
    private TextView endView;
    private View progressBar;
    private boolean hasSend;
    protected View headerView;
    private View footerView;
    private Dialog deleteDialog;
    private Reply reply;
    private TextView title;
    protected RoundProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        if(Session.getInstance().getCurrentUser(this)==null){
            finish();
            return;
        }
        userId = Session.getInstance()
                .getCurrentUser(this)
                .getId();
        title = (TextView) findViewById(R.id.root_tv_toolbar_title);
        list = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, list, R.layout.review_list_item);
        adapter.setViewBinder(this);
        if (count > 0) {
            title.setText(titleStr + "  (" + count + ")");
        } else {
            title.setText(titleStr);
        }
        footerView = View.inflate(this, R.layout.list_foot_no_more, null);
        loadView = footerView.findViewById(R.id.loading);
        endView = (TextView) footerView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        progressBar = findViewById(R.id.progressBar);
        listView = (PullToRefreshListView) findViewById(R.id.review_list);
        if (headerView != null) {
            listView.getRefreshableView()
                    .addHeaderView(headerView);
        }
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setMode(Mode.PULL_FROM_START);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.getRefreshableView()
                .setOnItemLongClickListener(this);
        listView.setOnScrollListener(this);
        content = (TextView) findViewById(R.id.content);
        content.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMsg(null);
                }
                return false;
            }
        });
        currentPage = 1;
        new ReplyListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(currentPage));

    }

    public abstract String getUrl(int currentPage);

    public abstract void deleteReply(Reply reply, StatusRequestListener listener);

    public abstract void postReply(String content, Reply reply, StatusRequestListener listener);


    public void sendMsg(View view) {
        if (content.length() == 0 || hasSend) {
            return;
        }
        hasSend = true;
        postReply(content.getText()
                .toString(), reply, new ReplyPostListener());
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("count", count);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void setViewValue(View view, final Reply t, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.iconView = (ImageView) view.findViewById(R.id.user_icon);
            holder.content = (TextView) view.findViewById(R.id.content);
            holder.time = (TextView) view.findViewById(R.id.label_time);
            holder.nick = (TextView) view.findViewById(R.id.user_nick);
        }
        if (t.getUser() != null) {
            String url = t.getUser()
                    .getAvatar();
            if (!JSONUtil.isEmpty(url)) {
                holder.iconView.setTag(url);
                ImageLoadTask task = new ImageLoadTask(holder.iconView, 0);
                task.loadImage(url,
                        holder.iconView.getLayoutParams().width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.icon_avatar_primary),
                                task));
            } else {
                holder.iconView.setImageResource(R.mipmap.icon_avatar_primary);
            }
            holder.nick.setText(t.getUser()
                    .getNick());
        }
        if (t.getQuoteName() != null) {
            holder.content.setText(Html.fromHtml(String.format(getString(R.string.quote),
                    t.getQuoteName(),
                    t.getContent())));
        } else {
            holder.content.setText(t.getContent());
        }
        if (t.getTime() != null) {
            holder.time.setText(DateUtils.getRelativeTimeSpanString(t.getTime()
                            .getTime(),
                    Calendar.getInstance()
                            .getTimeInMillis(),
                    DateUtils.MINUTE_IN_MILLIS));
        }
        holder.iconView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (t.getUserId() > 0) {
                    Intent intent = new Intent(BaseReviewListActivity.this,
                            UserProfileActivity.class);
                    intent.putExtra("id", t.getUserId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Reply reply = (Reply) parent.getAdapter()
                .getItem(position);
        if (reply != null && content.length() == 0) {
            if ((this.reply != null && reply.getId()
                    .equals(this.reply.getId())) || reply.getUserId() == userId) {
                this.reply = null;
                content.setHint(R.string.review_hint);
            } else if (reply.getUserId() != userId) {
                this.reply = reply;
                content.setHint(String.format(getString(R.string.quote_hint),
                        reply.getUser()
                                .getNick()));
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        final Reply reply = (Reply) parent.getAdapter()
                .getItem(position);
        if (reply != null && reply.getUserId() == userId) {
            deleteDialog = DialogUtil.createDoubleButtonDialog(deleteDialog,
                    BaseReviewListActivity.this,
                    getString(R.string.hint_detele_reply),
                    getString(R.string.action_ok),
                    getString(R.string.action_cancel),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            deleteReply(reply, new ReplyDeleteListener(reply));
                        }
                    },
                    null);
            deleteDialog.show();
        }
        return true;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            refresh();
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        new ReplyListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                getUrl(++currentPage));
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

    private class ViewHolder {
        ImageView iconView;
        TextView content;
        TextView time;
        TextView nick;
    }

    private class ReplyListTask extends AsyncTask<String, Object, JSONObject> {

        String url;

        private ReplyListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String jsonStr = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                return new JSONObject(jsonStr).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (url.endsWith(getUrl(currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (result != null) {
                    if (currentPage == 1) {
                        list.clear();
                    }
                    int pageCount = result.optInt("page_count");
                    count = result.optInt("total_count");
                    if (count > 0) {
                        title.setText(titleStr + "  (" + count + ")");
                    } else {
                        title.setText(titleStr);
                    }
                    JSONArray array = result.optJSONArray("list");
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            list.add(new Reply(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    onLoadCompleted(pageCount <= currentPage);
                } else if (!list.isEmpty()) {
                    onLoadFailed();
                }
            }
            super.onPostExecute(result);
        }
    }

    private void onLoadCompleted(boolean isEnd) {
        lastPage = currentPage;
        this.isEnd = isEnd;
        if (footerView != null) {
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

    }

    private void onLoadFailed() {
        currentPage = lastPage;
        if (footerView != null) {
            footerView.setVisibility(View.VISIBLE);
            endView.setVisibility(View.VISIBLE);
            loadView.setVisibility(View.GONE);
            endView.setText(R.string.hint_net_disconnected);
        }
    }

    public void refresh() {
        currentPage = 1;
        new ReplyListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(currentPage));
    }

    private class ReplyDeleteListener implements StatusRequestListener {

        private Reply reply;

        private ReplyDeleteListener(Reply reply) {
            this.reply = reply;
        }

        @Override
        public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
            progressBar.setVisibility(View.GONE);
            count--;
            if (count > 0) {
                title.setText(titleStr + "  (" + count + ")");
            } else {
                title.setText(titleStr);
            }
            list.remove(reply);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
            progressBar.setVisibility(View.GONE);
            Util.postFailToast(BaseReviewListActivity.this, returnStatus, 0, network);
        }
    }

    private class ReplyPostListener implements StatusRequestListener {

        @Override
        public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.onComplate();
            }
            hasSend = false;
            reply = null;
            content.setHint(R.string.review_hint);
            content.setText(null);
            refresh();
        }

        @Override
        public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            hasSend = false;
            Util.postFailToast(BaseReviewListActivity.this, returnStatus, 0, network);
        }
    }

}
