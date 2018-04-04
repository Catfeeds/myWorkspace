package me.suncloud.marrymemo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.CommunityPost;
import me.suncloud.marrymemo.model.CommunityThread;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpDeleteTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.widget.MarkHeaderView;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * 更多话题页面
 * 瀑布流
 * Created by jinxin on 16/4/26.
 */
public class MoreMarkThreadActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<CommunityThread>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.list_view)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    View progressBar;
    @BindView(R.id.img_empty_hint)
    ImageView imgEmptyHint;
    @BindView(R.id.img_net_hint)
    ImageView imgNetHint;
    @BindView(R.id.text_empty_hint)
    TextView textEmptyHint;
    @BindView(R.id.empty_hint_layout)
    LinearLayout emptyHintLayout;
    private final int FOLLOW_LOGIN = 10;
    private ObjectBindAdapter<CommunityThread> adapter;
    private ArrayList<CommunityThread> mData;
    private long markId;
    private int markType;

    private SimpleDateFormat shortSimpleDateFormat;
    private int storyLogoImageWidth;
    private int threadLogoWidth;
    private DisplayMetrics dm;


    protected boolean isLoad;
    private boolean isEnd;
    protected int currentPage;
    protected View footView;
    private View loadView;
    private TextView endView;
    private String currentUrl;
    private boolean isMore;
    private MarkHeaderView markHeaderView;
    private City city;
    //1已关注 0未关注
    private int follow;
    private int headerHeight;
    private int faceSize;
    private int faceSize2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MarkHeaderView.MARK) {
                headerHeight = (int) msg.obj;
                setEmptyView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markId = getIntent().getLongExtra("markId", 0);
        markType = getIntent().getIntExtra("markType", 0);
        isMore = getIntent().getBooleanExtra("isMore", false);
        setContentView(R.layout.activity_listview_mark_order);
        ButterKnife.bind(this);

        String markTitle = getIntent().getStringExtra("markTitle");
        if (isMore) {
            if (!JSONUtil.isEmpty(markTitle) && !markTitle.contains("-话题")) {
                markTitle += "-话题";
            }
        }
        setTitle(JSONUtil.isEmpty(markTitle) ? getString(R.string.label_subject_wedding) :
                markTitle);
        city = Session.getInstance()
                .getMyCity(this);
        initFoot();
        initHeader();
        mData = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, mData, R.layout.thread_list_item3, this);
        if (isMore) {
            hideOkText();
        } else {
            showOkText();
        }
        listView.getRefreshableView()
                .setOnScrollListener(this);
        if (!isMore) {
            listView.getRefreshableView()
                    .addHeaderView(markHeaderView);
        }
        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setOnRefreshListener(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        dm = getResources().getDisplayMetrics();
        storyLogoImageWidth = Math.round(dm.density * 30);
        shortSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT,
                Locale.getDefault());
        threadLogoWidth = Math.round(dm.density * 50);
        faceSize = Math.round(dm.density * 18);
        faceSize2 = Math.round(dm.density * 15);

        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getData(currentPage);
    }

    @Override
    public void onOkButtonClick() {
        onFollow();
    }

    private void initHeader() {
        if (!isMore) {
            markHeaderView = new MarkHeaderView(this);
            markHeaderView.setActivity(this);
            markHeaderView.setRelativeId(markId);
            markHeaderView.setHeightHandler(mHandler);
            markHeaderView.setOnItemClickListener(new MarkHeaderView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, JSONObject data, int position) {
                    if (data != null) {
                        long id = data.optLong("id");
                        String markTitle = data.optString("name");
                        int type = data.optInt("marked_type");
                        Util.markActionActivity(MoreMarkThreadActivity.this,
                                type,
                                markTitle,
                                id,
                                false);
                    }
                }
            });
            markHeaderView.setOnDataChangeListener(new MarkHeaderView.OnDataChangeListener() {
                @Override
                public void onDataChanged(
                        ArrayList<JSONObject> data,
                        MarkHeaderView.MarkHeaderAdapter adapter,
                        ViewGroup parent) {
                    if (data != null && data.size() <= 0) {
                        markHeaderView.setContentVisible(false, false);
                    }
                }
            });
            markHeaderView.setPaddingVisible(true);
        }
    }

    private void initFoot() {
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing just consume event
            }
        });
        endView = (TextView) footView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
            }
        });
        loadView = footView.findViewById(R.id.loading);
    }

    private void getData(int currentPage) {
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_LIST,
                markId,
                markType,
                null,
                null,
                city == null ? 0 : city.getId(),
                currentPage);
        currentUrl = currentUrl.replace("order=null&", "")
                .replace("sort=null&", "");
        new GetThreadTask().executeOnExecutor(Constants.LISTTHEADPOOL, currentUrl);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void onFollow() {
        if (!Util.loginBindChecked(this, FOLLOW_LOGIN)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", markId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //1已关注 0未关注
        if (follow == 1) {
            //取消关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpDeleteTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 0;
                    setOkText(R.string.label_follow);
                    Util.showToast(R.string.hint_discollect_complete2, MoreMarkThreadActivity.this);
                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(MoreMarkThreadActivity.this,
                            returnStatus,
                            R.string.msg_fail_to_cancel_follow,
                            network);
                }
            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_CANCEL_FOLLOW, markId));
        } else if (follow == 0) {
            //添加关注
            progressBar.setVisibility(View.VISIBLE);
            new StatusHttpPostTask(this, new StatusRequestListener() {
                @Override
                public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                    progressBar.setVisibility(View.GONE);
                    follow = 1;
                    setOkText(R.string.label_followed);
                    Util.showToast(R.string.label_mark_followed, MoreMarkThreadActivity.this);

                }

                @Override
                public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                    progressBar.setVisibility(View.GONE);
                    Util.postFailToast(MoreMarkThreadActivity.this,
                            returnStatus,
                            R.string.msg_fail_to_follow,
                            network);
                }

            }).execute(Constants.getAbsUrl(Constants.HttpPath.MARK_ADD_FOLLOW),
                    jsonObject.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOLLOW_LOGIN && resultCode == Activity.RESULT_OK) {
            //关注重新登录回来
            onFollow();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    @Override
    public void setViewValue(View view, final CommunityThread thread, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (view != null) {
            if (thread != null) {
                view.setVisibility(View.VISIBLE);
                final User user = thread.getUser();
                if (user != null) {
                    String logoPath = JSONUtil.getImagePathForWxH(user.getAvatar(),
                            storyLogoImageWidth,
                            storyLogoImageWidth);
                    if (!JSONUtil.isEmpty(logoPath)) {
                        ImageLoadTask task = new ImageLoadTask(holder.userIcon, null, 0);
                        holder.userIcon.setTag(logoPath);
                        task.loadImage(logoPath,
                                storyLogoImageWidth,
                                ScaleMode.ALL,
                                new AsyncBitmapDrawable(getResources(),
                                        R.mipmap.icon_avatar_primary,
                                        task));
                    }

                    if (user.getWeddingDay() != null && user.getIsPending() != 0) {
                        if (user.getWeddingDay()
                                .before(Calendar.getInstance()
                                        .getTime())) {
                            holder.userWeddingDate.setText(user.getGender() == 1 ? getString(R
                                    .string.label_married2) : getString(
                                    R.string.label_married));
                        } else {
                            holder.userWeddingDate.setText(getString(R.string.label_wedding_time1,
                                    shortSimpleDateFormat.format(user.getWeddingDay())));
                        }
                        holder.userWeddingDate.setVisibility(View.VISIBLE);
                    } else {
                        holder.userWeddingDate.setText(user.getGender() == 1 ? "" : getString(R
                                .string.label_no_wedding_day));
                        holder.userWeddingDate.setVisibility(View.VISIBLE);
                    }
                    holder.userTitle.setText(user.getNick());
                }

                holder.userTime.setText(JSONUtil.isEmpty(Util.getRefrshTime(thread
                                .getLastPostTime(),
                        this)) ? "" : Util.getRefrshTime(thread.getLastPostTime(), this));
                holder.threadCommentCount.setText(getString(R.string.label_comment_count2,
                        thread.getPostCount()));

                if (thread.isHidden()) {
                    holder.threadHidden.setVisibility(View.VISIBLE);
                }

                CommunityPost post = thread.getPost();
                String path = null;
                //精编话题显示精编的标题和导读
                if (thread.getThreadPages() != null) {
                    holder.threadTitle.setText(JSONUtil.isEmpty(thread.getThreadPages()
                            .getTitle()) ? "" : EmojiUtil.parseEmojiByText2(this,
                            thread.getThreadPages()
                                    .getTitle(),
                            faceSize));
                    holder.threadContent.setText(EmojiUtil.parseEmojiByText2(this,
                            thread.getThreadPages()
                                    .getSubTitle(),
                            faceSize2));
                    path = JSONUtil.getImagePathForWxH(thread.getThreadPages()
                            .getImgPath(), threadLogoWidth, threadLogoWidth);
                } else {
                    holder.threadTitle.setText(JSONUtil.isEmpty(thread.getTitle()) ? "" :
                            EmojiUtil.parseEmojiByText2(
                            this,
                            thread.getTitle(),
                            faceSize));
                    if (post != null) {
                        holder.threadContent.setText(EmojiUtil.parseEmojiByText2(this,
                                post.getMessage(),
                                faceSize2));
                        ArrayList<Photo> photos = post.getPhotos();
                        if (photos != null && photos.size() >= 1) {
                            Photo photo = photos.get(0);
                            path = JSONUtil.getImagePathForWxH(photo.getPath(),
                                    threadLogoWidth,
                                    threadLogoWidth);
                        } else {
                            holder.threadImage.setImageBitmap(null);
                            holder.threadImage.setVisibility(View.GONE);
                        }
                    }
                }

                if (!JSONUtil.isEmpty(path)) {
                    holder.threadImage.setVisibility(View.VISIBLE);
                    ImageLoadTask task = new ImageLoadTask(holder.threadImage, 0);
                    holder.threadImage.setTag(path);
                    task.loadImage(path,
                            threadLogoWidth,
                            ScaleMode.ALL,
                            new AsyncBitmapDrawable(getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                } else {
                    holder.threadImage.setImageBitmap(null);
                    holder.threadImage.setVisibility(View.GONE);
                }

                if (holder.threadImage.getVisibility() == View.GONE) {
                    holder.threadContentLayout.setPadding(0,
                            holder.threadContentLayout.getPaddingTop(),
                            holder.threadContentLayout.getPaddingRight(),
                            holder.threadContentLayout.getPaddingBottom());
                } else {
                    holder.threadContentLayout.setPadding(Math.round(dm.density * 8),
                            holder.threadContentLayout.getPaddingTop(),
                            holder.threadContentLayout.getPaddingRight(),
                            holder.threadContentLayout.getPaddingBottom());
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MoreMarkThreadActivity.this,
                                CommunityThreadDetailActivity.class);
                        intent.putExtra("id", thread.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });

                holder.userInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User threadUser = thread.getUser();
                        if (threadUser == null) {
                            return;
                        }
                        User currentUser = Session.getInstance()
                                .getCurrentUser();
                        if ((currentUser != null && threadUser.getId()
                                .equals(currentUser.getId()))) {
                            return;
                        }
                        Intent intent = new Intent(MoreMarkThreadActivity.this,
                                UserProfileActivity.class);
                        intent.putExtra("id", threadUser.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                holder.threadFromContent.setClickable(true);

            } else {
                view.setVisibility(View.GONE);
            }
        }

    }

    private void setEmptyView() {
        if (mData.isEmpty()) {
            footView.setVisibility(View.GONE);
            textEmptyHint.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(this)) {
                imgEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(getString(R.string.no_item));
            } else {
                imgNetHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(getString(R.string.net_disconnected));
            }

            if (!isMore) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        emptyHintLayout.getLayoutParams();
                if (params != null) {
                    if (markHeaderView.getData()
                            .size() > 0) {
                        params.height = Math.round(JSONUtil.getDeviceSize(this).y - headerHeight
                                - Math.round(
                                dm.density * (45 + 10 + 10 + 2)));
                    } else {
                        params.height = JSONUtil.getDeviceSize(this).y;
                    }
                }
            }
            emptyHintLayout.setVisibility(View.VISIBLE);
        } else {
            emptyHintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    getData(currentPage);
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
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isLoad) {
            currentPage = 1;
            getData(currentPage);
        }
    }

    class ViewHolder {
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.user_title)
        TextView userTitle;
        @BindView(R.id.user_time)
        TextView userTime;
        @BindView(R.id.user_wedding_date)
        TextView userWeddingDate;
        @BindView(R.id.user_info_layout)
        LinearLayout userInfoLayout;
        @BindView(R.id.thread_title)
        TextView threadTitle;
        @BindView(R.id.thread_image)
        RecyclingImageView threadImage;
        @BindView(R.id.thread_content)
        TextView threadContent;
        @BindView(R.id.thread_content_layout)
        LinearLayout threadContentLayout;
        @BindView(R.id.thread_title_layout)
        LinearLayout threadTitleLayout;
        @BindView(R.id.thread_from)
        TextView threadFrom;
        @BindView(R.id.thread_from_content)
        TextView threadFromContent;
        @BindView(R.id.thread_comment_count)
        TextView threadCommentCount;
        @BindView(R.id.thread_from_layout)
        LinearLayout threadFromLayout;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.thread_show)
        RelativeLayout threadShow;
        @BindView(R.id.hidden_text)
        TextView hiddenText;
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.thread_hidden)
        LinearLayout threadHidden;
        @BindView(R.id.thread_layout)
        FrameLayout threadLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class GetThreadTask extends AsyncTask<String, Void, JSONObject> {
        private String url;

        private GetThreadTask() {
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
                return new JSONObject(json);
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
            if (url.equalsIgnoreCase(currentUrl)) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        if (currentPage == 1) {
                            mData.clear();
                        }
                        int pageCount = data.optInt("page_count", 0);
                        isEnd = pageCount <= currentPage;
                        if (isEnd) {
                            endView.setVisibility(View.VISIBLE);
                            loadView.setVisibility(View.GONE);
                            endView.setText(R.string.no_more);
                        } else {
                            endView.setVisibility(View.GONE);
                            loadView.setVisibility(View.INVISIBLE);
                        }

                        follow = data.optInt("is_follow", 0);
                        //1已关注 0未关注
                        if (follow == 0) {
                            setOkText(R.string.label_follow);
                        } else if (follow == 1) {
                            setOkText(R.string.label_followed);
                        }
                        JSONArray list = data.optJSONArray("list");
                        if (list != null) {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject item = list.optJSONObject(i);
                                CommunityThread thread = new CommunityThread(item);
                                if (thread.getId() > 0) {
                                    mData.add(thread);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            setEmptyView();
            super.onPostExecute(jsonObject);
        }
    }
}
