package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityAuthor;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.community.CommunityIntentUtil;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.community.CommunityChannelActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.widget.RecyclingImageView;

/**
 * Created by mo_yu on 2017/5/13.用户发布或收藏的话题Adapter
 */
public class UserThreadAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<CommunityThread> threads;

    private static final int ITEM_TYPE = 1;
    private static final int DEFAULT_TYPE = 2;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 3;
    private static final int HEADER_TYPE = 4;

    private View headerView;
    private View footerView;

    public UserThreadAdapter(
            Context context, ArrayList<CommunityThread> threads) {
        this.context = context;
        this.threads = threads;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE:
                return new PublishViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.thread_list_item2, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof PublishViewHolder) {
            holder.setView(context,
                    threads.get(headerView == null ? position : position - 1),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
        //        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return (threads != null ? threads.size() : 0) + (footerView == null ? 0 : 1) +
                (headerView == null ? 0 : 1);
    }

    class PublishViewHolder extends BaseViewHolder<CommunityThread> {
        @BindView(R.id.user_icon)
        RoundedImageView userIcon;
        @BindView(R.id.user_time)
        TextView userTime;
        @BindView(R.id.user_title)
        TextView userTitle;
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
        @BindView(R.id.thread_comment_count)
        TextView threadCommentCount;
        @BindView(R.id.thread_from_layout)
        RelativeLayout threadFromLayout;
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

        private int logoWidth;
        private int threadImageWidth;
        private int faceSize;
        private int faceSizeSub;
        private int padding;

        PublishViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            logoWidth = CommonUtil.dp2px(view.getContext(), 30);
            threadImageWidth = CommonUtil.dp2px(view.getContext(), 50);
            faceSize = CommonUtil.dp2px(view.getContext(), 18);
            faceSizeSub = CommonUtil.dp2px(view.getContext(), 16);
            padding = CommonUtil.dp2px(view.getContext(), 8);
        }

        @Override
        protected void setViewData(
                final Context context, final CommunityThread thread, int position, int viewType) {
            CommunityAuthor user = thread.getAuthor();
            final Activity mContext = (Activity) context;
            if (user != null) {
                Glide.with(mContext)
                        .load(ImagePath.buildPath(user.getAvatar())
                                .height(logoWidth)
                                .width(logoWidth)
                                .cropPath())
                        .apply(new RequestOptions()
                                .dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                        .into(userIcon);
                //新版婚期显示
                DateTime date = user.getWeddingDay();
                if (date != null && user.isPending() != 0) {
                    if (HljTimeUtils.isWedding(date)) {
                        userWeddingDate.setText("婚期 " + date.toString(Constants.DATE_FORMAT_SHORT));
                    } else {
                        userWeddingDate.setText((user.getGender() == 1) ? "已婚男" : "已为人妻");
                    }
                } else {
                    userWeddingDate.setText((user.getGender() == 1) ? "" : "待字闺中");
                }
                userTitle.setText(user.getName());
            }

            userTime.setText(JSONUtil.isEmpty(HljTimeUtils.getShowTime(mContext,
                    thread.getLastPostTime())) ? "" : HljTimeUtils.getShowTime(mContext,
                    thread.getLastPostTime()));
            threadCommentCount.setText(mContext.getString(R.string.label_comment_count2,
                    String.valueOf(thread.getPostCount())));

            String path = null;
            //精编话题显示精编的标题和导读
            threadTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    thread.getShowTitle(),
                    faceSize));
            threadContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                    thread.getShowSubtitle(),
                    faceSizeSub));
            if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                path = ImagePath.buildPath(thread.getShowPhotos()
                        .get(0)
                        .getImagePath())
                        .width(threadImageWidth)
                        .height(threadImageWidth)
                        .cropPath();
            }
            if (!TextUtils.isEmpty(path)) {
                threadImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(path)
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(threadImage);
            } else {
                threadImage.setVisibility(View.GONE);
            }
            if (threadImage.getVisibility() == View.GONE) {
                threadContentLayout.setPadding(0,
                        threadContentLayout.getPaddingTop(),
                        threadContentLayout.getPaddingRight(),
                        threadContentLayout.getPaddingBottom());
            } else {
                threadContentLayout.setPadding(padding,
                        threadContentLayout.getPaddingTop(),
                        threadContentLayout.getPaddingRight(),
                        threadContentLayout.getPaddingBottom());
            }
            if (thread.isHidden()) {
                threadHidden.setVisibility(View.VISIBLE);
                threadShow.setVisibility(View.GONE);
                threadHidden.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do nothing just for intercept onclick event
                        return;
                    }
                });
            } else {
                threadHidden.setVisibility(View.GONE);
                threadShow.setVisibility(View.VISIBLE);
                threadShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", thread.getId());
                        mContext.startActivity(intent);
                        mContext.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                userInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (thread.getAuthor()
                                .getId() != 0) {
                            Intent intent = new Intent(mContext, UserProfileActivity.class);
                            intent.putExtra("id",
                                    thread.getAuthor()
                                            .getId());
                            mContext.startActivity(intent);
                        }
                    }
                });
            }

            if (position == threads.size() - 1) {
                line.setVisibility(View.GONE);
            }

            //话题来源，有频道显示频道，无频道显示小组，都没有不显示
            final CommunityChannel communityChannel = thread.getChannel();
            if (!TextUtils.isEmpty(communityChannel.getTitle())) {
                threadFrom.setVisibility(View.VISIBLE);
                threadFrom.setText(mContext.getString(R.string.label_from_channel,
                        communityChannel.getTitle()));
                threadFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommunityIntentUtil.startCommunityChannelIntent(v.getContext(),
                                communityChannel.getId());
                    }
                });
            } else {
                threadFrom.setVisibility(View.GONE);
            }
        }
    }

}

