package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.HotCommunityChannelAdapter;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by mo_yu on 2018/3/13.
 */

public class HotCommunityChannelLayoutViewHolder extends BaseViewHolder<List<CommunityChannel>> {


    @BindView(R.id.tv_my_community_channel)
    TextView tvMyCommunityChannel;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private HotCommunityChannelAdapter adapter;
    private OnChannelClickListener onChannelClickListener;

    public HotCommunityChannelLayoutViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_hot_channel_layout, parent, false));
    }

    public HotCommunityChannelLayoutViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(),
                HORIZONTAL,
                false);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HotCommunityChannelAdapter(itemView.getContext());
        recyclerView.setAdapter(adapter);
        tvMyCommunityChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChannelClickListener != null) {
                    onChannelClickListener.onChannelClick();
                }
            }
        });
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView,
                HljTaggerName.CommunityHomeFragment.COMMUNITY_HOT_CHANNEL_LIST);
    }

    public void setOnChannelClickListener(OnChannelClickListener onChannelClickListener) {
        this.onChannelClickListener = onChannelClickListener;
    }

    public interface OnChannelClickListener {
        void onChannelClick();
    }

    @Override
    protected void setViewData(
            Context mContext, List<CommunityChannel> list, int position, int viewType) {
        adapter.setList(list);
    }
}
