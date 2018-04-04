package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.finder.EntityComment;
import me.suncloud.marrymemo.view.UserProfileActivity;

/**
 * 专题发现页评论adapter
 * Created by chen_bin on 2016/12/15 0015.
 */
public class SubPageCommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<EntityComment> comments;
    private LayoutInflater inflater;
    private int hotCount;
    private int logoSize;
    private int faceSize;
    private int faceSize2;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private OnCommentListener onCommentListener;
    private OnPraiseListener onPraiseListener;

    public SubPageCommentListAdapter(Context context) {
        this.context = context;
        this.faceSize = CommonUtil.dp2px(context, 18);
        this.faceSize2 = CommonUtil.dp2px(context, 16);
        this.logoSize = CommonUtil.dp2px(context, 32);
        this.inflater = LayoutInflater.from(context);
    }

    public List<EntityComment> getComments() {
        return comments;
    }

    public void setComments(List<EntityComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComments(List<EntityComment> comments) {
        if (!CommonUtil.isCollectionEmpty(comments)) {
            int start = getItemCount() - getFooterViewCount();
            this.comments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public int getHotCount() {
        return hotCount;
    }

    public void setHotCount(int hotCount) {
        this.hotCount = hotCount;
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    public void setOnPraiseListener(OnPraiseListener onPraiseListener) {
        this.onPraiseListener = onPraiseListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(comments);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new SubPageCommentViewHolder(inflater.inflate(R.layout
                                .sub_page_comment_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, comments.get(position), position, viewType);
                break;
        }
    }

    public class SubPageCommentViewHolder extends BaseViewHolder<EntityComment> {
        @BindView(R.id.tv_category)
        TextView tvCategory;
        @BindView(R.id.user_layout)
        LinearLayout userLayout;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_nick)
        TextView tvNick;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.check_praised)
        public CheckableLinearLayout checkPraised;
        @BindView(R.id.img_praise)
        public ImageView imgPraise;
        @BindView(R.id.tv_praise_count)
        public TextView tvPraiseCount;
        @BindView(R.id.tv_praise_add)
        public TextView tvPraiseAdd;
        @BindView(R.id.content_layout)
        LinearLayout contentLayout;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.reply_layout)
        LinearLayout replyLayout;
        @BindView(R.id.reply_content_layout)
        LinearLayout replyContentLayout;
        @BindView(R.id.tv_reply_nick)
        TextView tvReplyNick;
        @BindView(R.id.tv_reply_content)
        TextView tvReplyContent;
        @BindView(R.id.tv_reply_hidden)
        TextView tvReplyHidden;
        @BindView(R.id.line_layout)
        View lineLayout;

        public SubPageCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            userLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EntityComment comment = getItem();
                    if (comment != null && comment.getId() > 0) {
                        onGoUserProfile(comment.getUser());
                    }
                }
            });
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCommentListener != null) {
                        onCommentListener.onComment(getItem());
                    }
                }
            });
            checkPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPraiseListener != null) {
                        onPraiseListener.onPraise(SubPageCommentViewHolder.this, getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context, final EntityComment comment, int position, int viewType) {
            if (comment == null) {
                return;
            }
            if (hotCount != 0 && position == 0) {
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(R.string.title_hot_comment);
            } else if (hotCount == position) {
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(R.string.label_twitter_comment_count2);
            } else {
                tvCategory.setVisibility(View.GONE);
            }
            if (position == hotCount - 1 || position == comments.size() - 1) {
                lineLayout.setVisibility(View.GONE);
            } else {
                lineLayout.setVisibility(View.VISIBLE);
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(comment.getUser()
                            .getAvatar())
                            .width(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgAvatar);
            tvNick.setText(comment.getUser()
                    .getName());
            tvTime.setText(comment.getCreatedAt() == null ? "" : HljTimeUtils.getShowTime(context,
                    comment.getCreatedAt()));
            checkPraised.setChecked(comment.isLike());
            tvPraiseCount.setText(String.valueOf(comment.getLikesCount()));
            String atNick;
            if (comment.getReply() == null || comment.getReply()
                    .getId() == 0) {
                atNick = "";
                replyLayout.setVisibility(View.GONE);
            } else {
                atNick = "@" + comment.getReply()
                        .getUser()
                        .getName() + " ";
                replyLayout.setVisibility(View.VISIBLE);
                if (comment.getReply()
                        .isDeleted()) {
                    replyContentLayout.setVisibility(View.GONE);
                    tvReplyHidden.setVisibility(View.VISIBLE);
                } else {
                    replyContentLayout.setVisibility(View.VISIBLE);
                    tvReplyHidden.setVisibility(View.GONE);
                    tvReplyNick.setText(context.getString(R.string.label_reply_name,
                            comment.getReply()
                                    .getUser()
                                    .getName()));
                    tvReplyContent.setText(EmojiUtil.parseEmojiByText2(context,
                            comment.getReply()
                                    .getContent(),
                            faceSize2));
                }
            }
            String content = atNick + comment.getContent();
            SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                    content,
                    faceSize);
            if (builder != null) {
                builder.setSpan(new MyClickableSpan(0, comment),
                        0,
                        atNick.length(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new MyClickableSpan(1, comment),
                        atNick.length(),
                        content.length(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tvContent.setText(builder);
                tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    //昵称点击
    private class MyClickableSpan extends ClickableSpan {
        private int type;
        private EntityComment comment;

        private MyClickableSpan(int type, EntityComment comment) {
            this.type = type;
            this.comment = comment;
        }

        @Override
        public void onClick(View widget) {
            if (type == 0) {
                onGoUserProfile(comment.getUser());
            } else if (type == 1) {
                if (onCommentListener != null) {
                    onCommentListener.onComment(comment);
                }
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (type == 0) {
                ds.setColor(ContextCompat.getColor(context, R.color.colorLink));
            } else {
                ds.setColor(ContextCompat.getColor(context, R.color.colorBlack3));
            }
            ds.setUnderlineText(false);
        }
    }

    public interface OnCommentListener {
        void onComment(EntityComment comment);
    }

    public interface OnPraiseListener {
        void onPraise(SubPageCommentViewHolder commentViewHolder, EntityComment comment);
    }

    private void onGoUserProfile(Author author) {
        if (author != null && author.getId() > 0) {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("id", author.getId());
            context.startActivity(intent);
        }
    }
}