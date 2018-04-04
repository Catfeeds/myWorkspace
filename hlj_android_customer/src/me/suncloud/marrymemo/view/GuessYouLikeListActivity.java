package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 猜你喜欢
 * Created by jinxin on 2016/11/29 0029.
 */

public class GuessYouLikeListActivity extends HljBaseActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private ArrayList<Work> recommends;
    private View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommends = getIntent().getParcelableArrayListExtra("recommend");
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);
        footerView = getLayoutInflater().inflate(R.layout.hlj_foot_no_more___cm, null);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        if (recommends == null || recommends.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            LikeAdapter adapter = new LikeAdapter();
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
            recyclerView.getRefreshableView()
                    .setLayoutManager(manager);
            recyclerView.getRefreshableView()
                    .setAdapter(adapter);
        }
    }

    class LikeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int FOOTER = 1;
        private int ITEM = 2;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == FOOTER) {
                return new ExtraViewHolder(footerView);
            } else {
                SmallWorkViewHolder workHolder = new SmallWorkViewHolder(LayoutInflater.from(
                        GuessYouLikeListActivity.this)
                        .inflate(R.layout.small_common_work_item___cv, parent, false));
                workHolder.setStyle(SmallWorkViewHolder.STYLE_GUESS_YOU_LIKE);
                return workHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            int type = getItemViewType(position);
            if (type == ITEM) {
                SmallWorkViewHolder workViewHolder = (SmallWorkViewHolder) holder;
                workViewHolder.setView(GuessYouLikeListActivity.this,
                        recommends.get(position),
                        position,
                        ITEM);
                workViewHolder.setShowBottomThinLineView(position < recommends.size() - 1);
                workViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Work work = recommends.get(position);
                        if (work != null && work.getId() > 0) {
                            Intent intent = new Intent(GuessYouLikeListActivity.this,
                                    WorkActivity.class);
                            intent.putExtra("id", work.getId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int type = 0;
            if (position != recommends.size()) {
                type = ITEM;
            } else {
                type = FOOTER;
            }
            return type;
        }

        @Override
        public int getItemCount() {
            return recommends.size() == 0 ? 0 : recommends.size() + 1;
        }
    }

    class ExtraViewHolder extends RecyclerView.ViewHolder {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }
    }
}
