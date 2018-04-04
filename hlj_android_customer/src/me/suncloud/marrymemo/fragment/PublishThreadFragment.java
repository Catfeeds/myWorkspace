package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Author;
import me.suncloud.marrymemo.model.CommunityPost;
import me.suncloud.marrymemo.model.CommunityThread;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;
import me.suncloud.marrymemo.widget.TabPageIndicator;

/**
 * Created by mo_yu on 2016/5/22.新版话题发布列表
 */
public class PublishThreadFragment extends BaseListFragment<CommunityThread> {
    private int storyLogoImageWidth;
    private SimpleDateFormat shortSimpleDateFormat;
    private int threadLogoWidth;
    private boolean isHim;
    private User user;
    private DisplayMetrics dm;
    private TabPageIndicator indicator;
    private int faceSize;
    private int faceSize2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            isHim = data.getBoolean("isHim");
            if (isHim) {
                user = (User) data.getSerializable("user");
            }
        }

        if (user == null) {
            user = Session.getInstance()
                    .getCurrentUser();
        }

        dm = getResources().getDisplayMetrics();
        storyLogoImageWidth = Math.round(dm.density * 30);
        threadLogoWidth = Math.round(dm.density * 50);
        faceSize = Math.round(dm.density * 18);
        faceSize2 = Math.round(dm.density * 15);
        shortSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT);
        indicator = (TabPageIndicator) getActivity().findViewById(R.id.indicator);
    }

    @Override
    public void setViewValue(View view, final CommunityThread thread, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (thread != null) {
            view.setVisibility(View.VISIBLE);
            Author user = thread.getAuthor();
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

                //新版婚期显示
                Date date = user.getWeddingDay();
                SimpleDateFormat shortSimpleDateFormat = new SimpleDateFormat(Constants
                        .DATE_FORMAT_SHORT);
                if (date != null && user.getIs_pending() != 0) {
                    if (Util.isWedding(date)) {
                        holder.userWeddingDate.setText("婚期 " + shortSimpleDateFormat.format(date));
                    } else {
                        holder.userWeddingDate.setText((user.getGender() == 1) ? "已婚男" : "已为人妻");
                    }
                } else {
                    holder.userWeddingDate.setText((user.getGender() == 1) ? "" : "待字闺中");
                }

                //                holder.userWeddingDate.setText(user.getWeddingDay() == null ?
                // "" : "婚期:" +
                //                        shortSimpleDateFormat.format(
                //                                user.getWeddingDay()));

                holder.userTitle.setText(user.getNick());
            }

            holder.userTime.setText(JSONUtil.isEmpty(HljTimeUtils.getShowTime(getActivity(),
                    thread.getLastPostTime())) ? "" : HljTimeUtils.getShowTime(getActivity(),
                    thread.getLastPostTime()));

            holder.threadCommentCount.setText(getString(R.string.label_comment_count2,
                    thread.getPostCount()));

            CommunityPost post = thread.getPost();
            String path = null;
            //精编话题显示精编的标题和导读
            if (thread.getThreadPages() != null) {
                holder.threadTitle.setText(JSONUtil.isEmpty(thread.getThreadPages()
                        .getTitle()) ? "" : EmojiUtil.parseEmojiByText2(getActivity(),
                        thread.getThreadPages()
                                .getTitle(),
                        faceSize));
                holder.threadContent.setText(EmojiUtil.parseEmojiByText2(getActivity(),
                        thread.getThreadPages()
                                .getSubTitle(),
                        faceSize2));
                path = JSONUtil.getImagePathForWxH(thread.getThreadPages()
                        .getImgPath(), threadLogoWidth, threadLogoWidth);
            } else {
                holder.threadTitle.setText(JSONUtil.isEmpty(thread.getTitle()) ? "" : EmojiUtil
                        .parseEmojiByText2(
                        getActivity(),
                        thread.getTitle(),
                        faceSize));
                if (post != null) {
                    holder.threadContent.setText(EmojiUtil.parseEmojiByText2(getActivity(),
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
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
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

            if (thread.isHidden()) {
                holder.threadHidden.setVisibility(View.VISIBLE);
                holder.threadShow.setVisibility(View.GONE);
                holder.threadHidden.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do nothing just for intercept onclick event
                        return;
                    }
                });
            } else {
                holder.threadHidden.setVisibility(View.GONE);
                holder.threadShow.setVisibility(View.VISIBLE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),
                                CommunityThreadDetailActivity.class);
                        intent.putExtra("id", thread.getId());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                view.findViewById(R.id.user_info_layout)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!Util.loginChecked(getContext())) {
                                    //未登录
                                    Intent intent = new Intent(getActivity(),
                                            UserProfileActivity.class);
                                    intent.putExtra("id",
                                            thread.getAuthor()
                                                    .getId());
                                    startActivity(intent);
                                } else {
                                    //已登录
                                    User my = Session.getInstance()
                                            .getCurrentUser(getContext());
                                    if (my != null && thread.getAuthor()
                                            .getId() != my.getId()) {
                                        Intent intent = new Intent(getActivity(),
                                                UserProfileActivity.class);
                                        intent.putExtra("id",
                                                thread.getAuthor()
                                                        .getId());
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }

            if (position == mDatas.size() - 1) {
                holder.line.setVisibility(View.GONE);
            }

            //话题来源，有频道显示频道，无频道显示小组，都没有不显示
            if (thread.getChannel() != null && !JSONUtil.isEmpty(thread.getChannel()
                    .getTitle())) {
                holder.threadFrom.setText(getString(R.string.label_from_channel,
                        thread.getChannel()
                                .getTitle()));
                holder.threadFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommunityIntentUtil.startCommunityChannelIntent(getActivity(),
                                thread.getChannel()
                                        .getId());
                    }
                });
            } else if (thread.getGroup() != null && !JSONUtil.isEmpty(thread.getGroup()
                    .getTitle())) {
                holder.threadFrom.setText(getString(R.string.label_from_channel,
                        thread.getGroup()
                                .getTitle()));
            } else {
                holder.threadFrom.setText("");
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void setEmptyView() {
        if (mDatas.isEmpty()) {
            View emptyView = listView.getRefreshableView()
                    .getEmptyView();

            if (emptyView == null) {
                emptyView = rootView.findViewById(R.id.empty_hint_layout);
                listView.getRefreshableView()
                        .setEmptyView(emptyView);
            }
            ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
            ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
            TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);

            imgEmptyHint.setVisibility(View.VISIBLE);
            imgNetHint.setVisibility(View.VISIBLE);
            textEmptyHint.setVisibility(View.VISIBLE);
            if (JSONUtil.isNetworkConnected(getActivity())) {
                imgNetHint.setVisibility(View.GONE);
                if (isHim) {
                    textEmptyHint.setText(R.string.label_him_no_thread);
                } else {
                    textEmptyHint.setText(R.string.no_item);
                }
            } else {
                imgEmptyHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.net_disconnected);
            }
        }
    }

    @Override
    protected void getData(final int currentPage) {
        currentUrl = String.format(Constants.getAbsUrl(Constants.HttpPath.GET_PUBLISH_THREAD_LIST),
                user.getId(),
                currentPage,
                Constants.PER_PAGE);
        new GetDataTask() {

            @Override
            protected JSONObject getData(String url) {
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
            protected void onPostData(JSONObject json) {
                if (json != null && json.optJSONObject("status")
                        .optInt("RetCode") == 0 && !(json.optJSONObject("data")
                        .isNull("list"))) {
                    JSONArray list = json.optJSONObject("data")
                            .optJSONArray("list");
                    int totalCount = json.optJSONObject("data")
                            .optInt("total_count");
                    if (indicator != null) {
                        indicator.setTabText(totalCount > 0 ? getString(R.string.label_upload1,
                                totalCount) : getString(R.string.label_upload), 0);
                    }
                    int size = 0;
                    if (list != null) {
                        if (currentPage == 1) {
                            mDatas.clear();
                        }
                        size = list.length();
                        for (int i = 0; i < size; i++) {
                            CommunityThread thread = new CommunityThread(list.optJSONObject(i));
                            mDatas.add(thread);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    if (size < Constants.PER_PAGE) {
                        isEnd = true;
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                    } else {
                        isEnd = false;
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                    isLoad = false;
                    setEmptyView();
                }
            }
        }.executeOnExecutor(Constants.LISTTHEADPOOL, currentUrl);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.thread_list_item2;
    }

    class ViewHolder {
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.user_time)
        TextView userTime;
        @BindView(R.id.user_title)
        TextView userTitle;
        @BindView(R.id.user_wedding_date)
        TextView userWeddingDate;
        @BindView(R.id.thread_title)
        TextView threadTitle;
        @BindView(R.id.thread_image)
        RecyclingImageView threadImage;
        @BindView(R.id.thread_content)
        TextView threadContent;
        @BindView(R.id.thread_from)
        TextView threadFrom;
        @BindView(R.id.thread_comment_count)
        TextView threadCommentCount;
        @BindView(R.id.thread_hidden)
        View threadHidden;
        @BindView(R.id.thread_show)
        View threadShow;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.thread_content_layout)
        View threadContentLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
