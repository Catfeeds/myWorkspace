package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.HotCommunityThreadAdapter;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by mo_yu on 2018/3/12.今日热门话题
 */

public class HotThreadLayoutViewHolder extends BaseViewHolder<List<CommunityFeed>> {

    @BindView(R.id.hot_thread_recycler_view)
    RecyclerView recyclerView;
    private HotCommunityThreadAdapter adapter;

    public HotThreadLayoutViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_hot_thread_list_layout, parent, false));
    }

    public HotThreadLayoutViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(),
                HORIZONTAL,
                false);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HotCommunityThreadAdapter(itemView.getContext());
        recyclerView.setAdapter(adapter);
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(recyclerView,
                HljTaggerName.CommunityHomeFragment.HOT_COMMUNITY_THREAD_LIST);
    }

    @Override
    protected void setViewData(
            Context mContext, List<CommunityFeed> list, int position, int viewType) {
        adapter.setList(list);
    }
}
