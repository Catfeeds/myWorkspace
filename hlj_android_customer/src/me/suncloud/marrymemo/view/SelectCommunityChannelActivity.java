package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.SelectChannelAdapter;
import me.suncloud.marrymemo.model.CommunityChannel;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.util.JSONUtil;

public class SelectCommunityChannelActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, SelectChannelAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_hint_layout)
    LinearLayout emptyHintLayout;
    private ArrayList<CommunityChannel> channels;
    private SelectChannelAdapter adapter;
    private long channelId;
    private boolean isLoadDone;

    public final static String ARG_CHANNEL_ID = "channel_id";
    public final static String ARG_CHANNEL_TITLE = "channel_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community_channel);
        ButterKnife.bind(this);

        setRecyclerView();
        channelId = getIntent().getLongExtra(ARG_CHANNEL_ID, 0);

        progressBar.setVisibility(View.VISIBLE);
        startRequestData();
    }

    private void startRequestData() {
        channels.clear();
        new GetMyChannelsTask("已关注",
                Constants.getAbsUrl(Constants.HttpPath.MY_ALL_FOLLOWED_CHANNELS)).execute();
    }

    private void setRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        channels = new ArrayList<>();
        adapter = new SelectChannelAdapter(this, channels);
        adapter.setOnItemClickListener(this);

        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (!isLoadDone) {
            return;
        }
        startRequestData();
    }

    @Override
    public void onItemClick(View view, int position) {
        CommunityChannel channel = channels.get(position);
        Intent intent = getIntent();
        intent.putExtra(ARG_CHANNEL_ID, channel.getId());
        intent.putExtra(ARG_CHANNEL_TITLE, channel.getTitle());
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    private class GetMyChannelsTask extends AsyncTask<String, Integer, JSONObject> {

        private String title;
        private String url;

        public GetMyChannelsTask(String title, String url) {
            isLoadDone = false;
            this.title = title;
            this.url = url;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(url);

                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONArray jsonArray = jsonObject.optJSONObject("data")
                            .optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        CommunityChannel titleHolderChannel = new CommunityChannel(new JSONObject
                                ());
                        titleHolderChannel.setId((long) -1);
                        titleHolderChannel.setTitle(title);
                        channels.add(titleHolderChannel);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            CommunityChannel channel = new CommunityChannel(jsonArray.optJSONObject(
                                    i));
                            if (channelId == channel.getId()) {
                                channel.setSelected(true);
                            }
                            //0表示话题频道，1表示晒婚照频道
                            if (channel.getType() == 0) {
                                channels.add(channel);
                            }
                        }
                        if (channels.size() == 1) {
                            channels.remove(titleHolderChannel);
                        }
                    }
                }
            }

            new GetRecChannelsTask("其他频道 (发布时关注)",
                    Constants.getAbsUrl(Constants.HttpPath.COMMUNITY_HOT_CHANNEL_URL2)).execute();
            super.onPostExecute(jsonObject);
        }
    }

    private class GetRecChannelsTask extends AsyncTask<String, Integer, JSONObject> {

        private String title;
        private String url;

        public GetRecChannelsTask(String title, String url) {
            this.title = title;
            this.url = url;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(url);

                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            isLoadDone = true;
            progressBar.setVisibility(View.GONE);
            recyclerView.onRefreshComplete();
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus != null && returnStatus.getRetCode() == 0) {
                    JSONArray jsonArray = jsonObject.optJSONObject("data")
                            .optJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        CommunityChannel titleHolderChannel = new CommunityChannel(new JSONObject
                                ());
                        titleHolderChannel.setId((long) -2);
                        titleHolderChannel.setTitle(title);
                        channels.add(titleHolderChannel);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            CommunityChannel channel = new CommunityChannel(jsonArray.optJSONObject(
                                    i)
                                    .optJSONObject("entity"));
                            if (!containsItem(channels, channel)) {
                                if (channelId == channel.getId()) {
                                    channel.setSelected(true);
                                }
                                channels.add(channel);
                            }
                        }
                    }
                }
            }
            if (channels.isEmpty()) {
                emptyHintLayout.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
            super.onPostExecute(jsonObject);
        }
    }

    private boolean containsItem(
            ArrayList<CommunityChannel> channelArrayList, CommunityChannel channel) {
        for (CommunityChannel c : channelArrayList) {
            if (c.getId()
                    .equals(channel.getId())) {
                return true;
            }
        }

        return false;
    }
}
