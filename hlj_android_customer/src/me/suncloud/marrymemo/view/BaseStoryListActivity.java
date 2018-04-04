package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by Suncloud on 2016/2/29.
 */
public abstract class BaseStoryListActivity extends HljBaseNoBarActivity implements
        ObjectBindAdapter.ViewBinder<Story>, PullToRefreshBase.OnRefreshListener<ListView>,
        AbsListView.OnScrollListener, AdapterView.OnItemClickListener {


    protected View footerView;
    private View loadView;
    private TextView endView;
    protected PullToRefreshListView listView;
    protected ArrayList<Story> stories;
    protected ObjectBindAdapter<Story> adapter;
    protected boolean isLoad;
    private boolean isEnd;
    protected int currentPage;
    private int lastPage;
    private int height;
    private int imgWidth;
    private int avatarSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(this);
        avatarSize = Util.dp2px(this, 24);
        int width = Math.round(point.x - Util.dp2px(this, 24));
        height = Math.round(width * 7 / 12);
        if (width <= 805) {
            imgWidth = width;
        } else {
            imgWidth = width * 3 / 4;
        }
        stories = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, stories, R.layout.list_story_item, this);
        currentPage = 1;
        lastPage = 1;
        if (footerView != null) {
            loadView = footerView.findViewById(R.id.loading);
            endView = (TextView) footerView.findViewById(R.id.no_more_hint);
            endView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onScrollStateChanged(listView.getRefreshableView(),
                            AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                }
            });
        }
        super.onCreate(savedInstanceState);
        setDefaultStatusBarPadding();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        listView = (PullToRefreshListView) findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Story story = (Story) parent.getAdapter()
                .getItem(position);
        if (story != null && story.getId() > 0) {
            Intent intent = new Intent(this, StoryActivity.class);
            intent.putExtra("id", story.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            onDataLoad(currentPage);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(this)) {
                        if (footerView != null) {
                            loadView.setVisibility(View.VISIBLE);
                            endView.setVisibility(View.GONE);
                        }
                        onDataLoad(++currentPage);
                    } else if (footerView != null) {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
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
    public void setViewValue(View view, final Story story, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.storyView.getLayoutParams().height = height;
            view.setTag(holder);
        }
        holder.tvTitle.setText(story.getTitle());
        holder.tvCommentCount.setText(getString(R.string.label_twitter_comment_count,
                story.getCommentCount()));
        holder.tvCollectCount.setText(getString(R.string.label_collect_count_3,
                story.getCollectCount()));
        String url = story.getCover();

        if (JSONUtil.isEmpty(url)) {
            holder.imgCover.setVisibility(View.GONE);
            holder.hintView.setVisibility(View.VISIBLE);
        } else {
            holder.imgCover.setVisibility(View.VISIBLE);
            holder.hintView.setVisibility(View.GONE);
            if (url.startsWith("http://")) {
                url = JSONUtil.getImagePath(url, imgWidth);
            }
            ImageLoadTask task = new ImageLoadTask(holder.imgCover);
            holder.imgCover.setTag(url);
            AsyncBitmapDrawable asyncDrawable = new AsyncBitmapDrawable(getResources(),
                    R.mipmap.icon_empty_image,
                    task);
            task.loadImage(url, imgWidth, ScaleMode.WIDTH, asyncDrawable);
        }
        String avatar = null;
        String nick = null;
        if (story.getUser() != null) {
            avatar = story.getUser()
                    .getAvatar(avatarSize);
            nick = story.getUser()
                    .getNick();
        }
        holder.tvTime.setText(nick);
        if (!JSONUtil.isEmpty(avatar)) {
            ImageLoadTask iconTask = new ImageLoadTask(holder.imgAvatar, 0);
            holder.imgAvatar.setTag(avatar);
            iconTask.loadImage(avatar,
                    avatarSize,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(),
                            R.mipmap.icon_avatar_primary,
                            iconTask));
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.icon_avatar_primary);
        }
        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (story.getUser() != null) {
                    Intent intent = new Intent(BaseStoryListActivity.this,
                            UserProfileActivity.class);
                    intent.putExtra("id",
                            story.getUser()
                                    .getId());
                    startActivity(intent);
                }
            }
        });
    }


    public abstract void onDataLoad(int page);

    protected void onLoadCompleted(boolean isEnd) {
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

    protected void onLoadFailed() {
        currentPage = lastPage;
        if (footerView != null) {
            footerView.setVisibility(View.VISIBLE);
            endView.setVisibility(View.VISIBLE);
            loadView.setVisibility(View.GONE);
            endView.setText(R.string.hint_net_disconnected);
        }
    }

    static class ViewHolder {
        @BindView(R.id.story_view)
        CardView storyView;
        @BindView(R.id.img_cover)
        RecyclingImageView imgCover;
        @BindView(R.id.no_cover_hint)
        TextView hintView;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R.id.tv_collect_count)
        TextView tvCollectCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void refresh() {
        stories.clear();
        adapter.notifyDataSetChanged();
        footerView.setVisibility(View.GONE);
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        currentPage = lastPage = 1;
        onDataLoad(currentPage);
    }
}
