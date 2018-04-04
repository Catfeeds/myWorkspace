package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.story.Story;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Post;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by werther on 16/8/31.
 */
public class StoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Story> stories;
    private View headerView;
    private View footerView;
    private onItemClickListener onItemClickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private int height;
    private int imgWidth;
    private int avatarSize;

    public StoryAdapter(
            Context context, ArrayList<Story> stories) {
        this.context = context;
        this.stories = stories;

        Point point = JSONUtil.getDeviceSize(context);
        avatarSize = Util.dp2px(context, 24);
        int width = Math.round(point.x - Util.dp2px(context, 24));
        height = Math.round(width * 7 / 12);
        if (width <= 805) {
            imgWidth = width;
        } else {
            imgWidth = width * 3 / 4;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new StoryViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_story_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof StoryViewHolder) {
            holder.setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return stories.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    private Story getItem(int position) {
        return stories.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class StoryViewHolder extends BaseViewHolder<Story> {
        @BindView(R.id.img_cover)
        RecyclingImageView imgCover;
        @BindView(R.id.no_cover_hint)
        TextView noCoverHint;
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
        @BindView(R.id.story_view)
        CardView storyView;
        @BindView(R.id.top_divider)
        View topDivider;

        StoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                Context mContext,
                final Story story,
                final int position,
                int viewType) {
            if (story == null) {
                return;
            }
            topDivider.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            storyView.getLayoutParams().height = height;
            storyView.setCardBackgroundColor(ContextCompat.getColor(storyView.getContext(),R.color.colorWhite));
            tvTitle.setText(story.getTitle());
            tvCommentCount.setText(context.getString(R.string.label_twitter_comment_count,
                    story.getCommentCount()));
            tvCollectCount.setText(context.getString(R.string.label_collect_count_3,
                    story.getCollectCount()));
            String url = story.getCoverPath();

            if (TextUtils.isEmpty(url)) {
                imgCover.setVisibility(View.GONE);
                noCoverHint.setVisibility(View.VISIBLE);
            } else {
                imgCover.setVisibility(View.VISIBLE);
                noCoverHint.setVisibility(View.GONE);
                if (url.startsWith("http://")) {
                    url = JSONUtil.getImagePath(url, imgWidth);
                }
                Glide.with(context)
                        .load(url)
                        .into(imgCover);
            }
            String avatar = null;
            String nick = null;
            if (story.getUser() != null) {
                avatar = ImageUtil.getAvatar(story.getUser()
                        .getAvatar(), avatarSize);
                nick = story.getUser()
                        .getName();
            }
            tvTime.setText(nick);
            if (!JSONUtil.isEmpty(avatar)) {
                Glide.with(context)
                        .load(avatar)
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.mipmap.icon_avatar_primary);
            }
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("id",
                            story.getUser()
                                    .getId());
                    context.startActivity(intent);
                }
            });
            storyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, story);
                    }
                }
            });
        }
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(StoryAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position, Story story);
    }


}
