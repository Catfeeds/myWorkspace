package me.suncloud.marrymemo.adpter.community.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityEventViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.CommunityEventThreadAdapter;
import me.suncloud.marrymemo.view.community.CommunityEventActivity;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by mo_yu on 2018/3/14.往期活动
 */

public class CommunityEventHistoryViewHolder extends TrackerCommunityEventViewHolder {

    @BindView(R.id.tv_event_title)
    TextView tvEventTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_go_event)
    TextView tvGoEvent;
    private CommunityEventThreadAdapter adapter;

    public CommunityEventHistoryViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_event_history_layout, parent, false));
    }

    public CommunityEventHistoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        final Activity activity = (Activity) itemView.getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(),
                HORIZONTAL,
                false);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommunityEventThreadAdapter(itemView.getContext());
        recyclerView.setAdapter(adapter);
        tvGoEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCommunityEvent(activity, getItem());
            }
        });
        tvEventTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCommunityEvent(activity, getItem());
            }
        });
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView,
                HljTaggerName.CommunityHomeFragment.COMMUNITY_ACTIVITY_HISTORY_THREAD_LIST);
    }

    private void goCommunityEvent(Activity activity, CommunityEvent communityEvent) {
        if (communityEvent == null) {
            return;
        }
        Intent intent = new Intent(activity, CommunityEventActivity.class);
        intent.putExtra(CommunityEventActivity.ARG_ID, communityEvent.getId());
        intent.putExtra(CommunityEventActivity.ARG_POSITION, 1);
        activity.startActivity(intent);
        activity.overridePendingTransition(com.hunliji.hljnotelibrary.R.anim.slide_in_right,
                com.hunliji.hljnotelibrary.R.anim.activity_anim_default);
    }

    @Override
    protected void setViewData(
            Context mContext, CommunityEvent communityEvent, int position, int viewType) {
        if (!CommonUtil.isCollectionEmpty(communityEvent.getHotThreads())) {
            itemView.setVisibility(View.VISIBLE);
            adapter.setList(communityEvent.getHotThreads());
            tvEventTitle.setText(communityEvent.getTitle());
        }else {
            itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }
}
