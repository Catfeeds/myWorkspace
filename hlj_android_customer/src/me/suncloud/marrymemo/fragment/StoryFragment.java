package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
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
import me.suncloud.marrymemo.model.Story;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.MyStoryListActivity;
import me.suncloud.marrymemo.view.StoryActivity;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by mo_yu on 2016/6/28.故事列表
 */
public class StoryFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<Story>, PullToRefreshBase.OnRefreshListener<ListView>, AbsListView
        .OnScrollListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {
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

    private String sort;
    //    private View progressBar;
    private long userId;
    private View emptyView;
    private User user;

    private ImageButton backTopView;
    private Handler mHandler;
    private boolean isHide;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(getActivity());
        avatarSize = Util.dp2px(getActivity(), 24);
        int width = Math.round(point.x - Util.dp2px(getActivity(), 24));
        height = Math.round(width * 7 / 12);
        if (width <= 805) {
            imgWidth = width;
        } else {
            imgWidth = width * 3 / 4;
        }
        stories = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), stories, R.layout.list_story_item, this);
        currentPage = 1;
        lastPage = 1;

        sort = "pop";
        footerView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
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
        mHandler = new Handler();
        user = Session.getInstance()
                .getCurrentUser();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_listview, container, false);
        emptyView = view.findViewById(R.id.empty_hint_layout);
        backTopView = (ImageButton) view.findViewById(R.id.backtop_btn);
        backTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTop();
            }
        });
        //        progressBar = view.findViewById( R.id.progressBar );
        listView = (PullToRefreshListView) view.findViewById(R.id.list);
        listView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        View headView = getActivity().getLayoutInflater()
                .inflate(R.layout.story_list_header, null);
        RadioGroup storySortView = (RadioGroup) headView.findViewById(R.id.story_sort_view);
        storySortView.setOnCheckedChangeListener(this);
        TextView writeStoryBtn = (TextView) headView.findViewById(R.id.write_story_btn);
        writeStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.loginBindChecked(getActivity(), Constants.Login.MY_STORY_LOGIN)) {
                    Intent intent = new Intent(getActivity(), MyStoryListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });
        listView.getRefreshableView()
                .addFooterView(footerView);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .addHeaderView(headView);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapter);
        onRefresh(listView);
        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Story story = (Story) parent.getAdapter()
                .getItem(position);
        if (story != null && story.getId() > 0) {
            Intent intent = new Intent(getActivity(), StoryActivity.class);
            intent.putExtra("id", story.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode && requestCode == Constants.Login.MY_STORY_LOGIN) {
            User user = Session.getInstance()
                    .getCurrentUser(getActivity());
            if (user != null && user.getId() != 0) {
                Intent intent = new Intent(getActivity(), MyStoryListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            onDataLoad(currentPage);
        }
    }

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param u
     */
    public void userRefresh(User u) {
        if (u != null) {
            if (user != null) {
                if (!u.getId()
                        .equals(user.getId())) {
                    onRefresh(listView);
                }
            } else {
                user = u;
                onRefresh(listView);
            }
        } else {
            user = null;
            onRefresh(listView);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(getActivity())) {
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
    public void setViewValue(View view, final Story story, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.storyView.getLayoutParams().height = height;
            view.setTag(holder);
        }
        holder.storyView.setCardBackgroundColor(R.color.colorWhite);
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
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    intent.putExtra("id",
                            story.getUser()
                                    .getId());
                    startActivity(intent);
                }
            }
        });
    }


    public void onDataLoad(int page) {
        new GetStoriesTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl(page));
    }

    private String getUrl(int page) {
        if (userId > 0) {
            return Constants.getAbsUrl(String.format(Constants.HttpPath.USER_STORIES_URL, userId));
        }
        return Constants.getAbsUrl(String.format(Constants.HttpPath.STORIES_URL + Constants
                .PAGE_COUNT,
                JSONUtil.isEmpty(sort) ? "pop" : sort,
                page));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.story_sort_default:
                sort = "pop";
                break;
            case R.id.story_sort_hot:
                sort = "story_collects_count";
                break;
            case R.id.story_sort_new:
                sort = "created_at";
                break;
        }
        //        progressBar.setVisibility( View.VISIBLE );
        refresh();
    }

    private class GetStoriesTask extends AsyncTask<String, Object, JSONObject> {
        String url;

        private GetStoriesTask() {
            isLoad = true;
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
        protected void onPostExecute(JSONObject result) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (url.equals(getUrl(currentPage))) {
                isLoad = false;
                //                progressBar.setVisibility( View.GONE );
                listView.onRefreshComplete();
                if (result != null) {
                    if (currentPage == 1) {
                        stories.clear();
                    }
                    int count = result.optInt("page_count");
                    JSONArray array = result.optJSONArray("list");
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            stories.add(new Story(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    onLoadCompleted(userId > 0 || count <= currentPage);
                } else if (!stories.isEmpty()) {
                    onLoadFailed();
                }
                if (stories.isEmpty()) {
                    View listEmptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (listEmptyView == null) {
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                        listEmptyView = listView.getRefreshableView()
                                .getEmptyView();
                    }
                    Util.setEmptyView(getActivity(),
                            listEmptyView,
                            R.string.no_item,
                            R.drawable.icon_common_empty,
                            0,
                            0);

                }
            }
            super.onPostExecute(result);
        }
    }


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

    @Override
    public void refresh(Object... params) {

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

    public void scrollTop() {
        listView.getRefreshableView()
                .smoothScrollToPosition(0);
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem < 10) {
            if (!isHide) {
                hideFiltrateAnimation();
            }
        } else if (isHide) {
            if (backTopView.getVisibility() == View.GONE) {
                backTopView.setVisibility(View.VISIBLE);
            }
            showFiltrateAnimation();
        }
    }

    private void showFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = false;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isHide) {
                                hideFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }

    private boolean isAnimEnded() {
        return backTopView != null && (backTopView.getAnimation() == null || backTopView
                .getAnimation()
                .hasEnded());
    }

    private void hideFiltrateAnimation() {
        if (backTopView == null) {
            return;
        }
        isHide = true;
        if (isAnimEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isHide) {
                                showFiltrateAnimation();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backTopView.startAnimation(animation);
        }
    }
}
