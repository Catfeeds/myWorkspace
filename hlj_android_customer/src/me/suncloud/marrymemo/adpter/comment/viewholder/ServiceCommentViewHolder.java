package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonlibrary.views.widgets.tint.TintColorListImageView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.UserProfileActivity;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 商家主页评论列表viewHolder
 * Created by chen_bin on 2017/4/14 0014.
 */
public class ServiceCommentViewHolder extends BaseServiceCommentViewHolder {
    @BindView(R.id.top_line_layout)
    View topLineLayout;
    @BindView(R.id.images_layout)
    HljGridView imagesLayout;
    @BindView(R.id.tv_work_title)
    TextView tvWorkTitle;
    @BindView(R.id.work_layout)
    LinearLayout workLayout;
    @BindView(R.id.img_praise)
    public TintColorListImageView imgPraise;
    @BindView(R.id.tv_praise_count)
    public TextView tvPraiseCount;
    @BindView(R.id.check_praised)
    public CheckableLinearLayout checkPraised;
    @BindView(R.id.go_comment_layout)
    LinearLayout goCommentLayout;
    @BindView(R.id.tv_praised_nicks)
    TextView tvPraisedNicks;
    @BindView(R.id.praise_line_layout)
    View praiseLineLayout;
    @BindView(R.id.praise_nicks_layout)
    LinearLayout praiseNicksLayout;
    @BindView(R.id.comment_list_layout)
    public LinearLayout commentListLayout;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.comment_count_layout)
    LinearLayout commentCountLayout;
    @BindView(R.id.comment_layout)
    public LinearLayout commentLayout;
    @BindView(R.id.praise_nicks_comment_layout)
    LinearLayout praiseNicksCommentLayout;
    private int emojiSizeS;
    private OnItemClickListener onItemClickListener;
    private OnPraiseListener onPraiseListener;
    private OnCommentListener onCommentListener;

    public ServiceCommentViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_comment_list_item, parent, false));
    }

    public ServiceCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        emojiSizeS = CommonUtil.dp2px(itemView.getContext(), 14);
        imagesLayout.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(itemView
                        .getContext(),
                2)));
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem() == null) {
                    return;
                }
                Author author = getItem().getAuthor();
                if (author != null && author.getId() > 0) {
                    Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
                    intent.putExtra("id", author.getId());
                    view.getContext()
                            .startActivity(intent);
                }
            }
        });
        imagesLayout.setItemClickListener(new HljGridView.GridItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ServiceComment comment = getItem();
                if (comment != null && !CommonUtil.isCollectionEmpty(comment.getPhotos())) {
                    Intent intent = new Intent(v.getContext(), PicsPageViewActivity.class);
                    intent.putExtra("photos", comment.getPhotos());
                    intent.putExtra("position", position);
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
        workLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem() == null) {
                    return;
                }
                Work work = getItem().getWork();
                if (work != null && work.getId() > 0) {
                    Intent intent = new Intent(v.getContext(), WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
        checkPraised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPraiseListener != null) {
                    onPraiseListener.onPraise(ServiceCommentViewHolder.this, getItem());
                }
            }
        });
        goCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCommentListener != null) {
                    onCommentListener.onComment(ServiceCommentViewHolder.this,
                            getItem(),
                            null);
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            final Context mContext,
            final ServiceComment comment,
            final int position,
            int viewType) {
        if (comment == null) {
            return;
        }
        super.setViewData(mContext, comment, position, viewType);
        setPhotos(comment);
        setCommentType(comment);
        addPraisedUsers(comment);
        addComments(mContext, comment);
    }

    private void setPhotos(ServiceComment comment) {
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            imagesLayout.setVisibility(View.VISIBLE);
            imagesLayout.setDate(comment.getPhotos());
        }
    }

    private void setCommentType(ServiceComment comment) {
        Work work = comment.getWork();
        if (work == null || work.getId() == 0) {
            workLayout.setVisibility(View.GONE);
        } else {
            workLayout.setVisibility(View.VISIBLE);
            tvWorkTitle.setText("来自套餐：" + comment.getWork()
                    .getTitle());
        }
    }

    public void addPraisedUsers(ServiceComment comment) {
        checkPraised.setChecked(comment.isLike());
        boolean isPraisesDataEmpty = false;
        if (CommonUtil.isCollectionEmpty(comment.getPraisedUsers())) {
            isPraisesDataEmpty = true;
            praiseNicksLayout.setVisibility(View.GONE);
            tvPraiseCount.setText(R.string.label_be_of_use);
        } else {
            praiseNicksLayout.setVisibility(View.VISIBLE);
            tvPraiseCount.setText(String.valueOf(comment.getLikesCount()));
            StringBuilder sb = new StringBuilder();
            for (int i = 0, size = comment.getPraisedUsers()
                    .size(); i < size; i++) {
                sb.append(comment.getPraisedUsers()
                        .get(i)
                        .getName());
                if (i < size - 1) {
                    sb.append("、");
                }
            }
            tvPraisedNicks.setText(sb.toString());
        }
        boolean isCommentsDataEmpty = CommonUtil.isCollectionEmpty(comment.getRepliedComments());
        praiseLineLayout.setVisibility(!isPraisesDataEmpty && !isCommentsDataEmpty ? View.VISIBLE
                : View.GONE);
        praiseNicksCommentLayout.setVisibility(!isPraisesDataEmpty || !isCommentsDataEmpty ? View
                .VISIBLE : View.GONE);
    }

    //移除自己的项 id为自己的登录id
    public void removePraisedUser(ServiceComment comment, Author author) {
        if (!CommonUtil.isCollectionEmpty(comment.getPraisedUsers())) {
            for (int i = 0, size = comment.getPraisedUsers()
                    .size(); i < size; i++) {
                Author user = comment.getPraisedUsers()
                        .get(i);
                if (user.getId() == author.getId()) {
                    comment.getPraisedUsers()
                            .remove(i);
                    addPraisedUsers(comment);
                    break;
                }
            }
        }
    }

    public void addComments(final Context context, final ServiceComment comment) {
        boolean isCommentsDataEmpty = CommonUtil.isCollectionEmpty(comment.getRepliedComments());
        if (isCommentsDataEmpty) {
            comment.setRepliesExpanded(true);
            commentLayout.setVisibility(View.GONE);
        } else {
            commentLayout.setVisibility(View.VISIBLE);
            int count = commentListLayout.getChildCount();
            int size = comment.getRepliedComments()
                    .size();
            if (size <= 3) {
                comment.setRepliesExpanded(true);
                commentCountLayout.setVisibility(View.GONE);
            } else {
                //查看全部可见并且当前是展开状态
                if (commentCountLayout.getVisibility() == View.VISIBLE && comment
                        .isRepliesExpanded()) {
                    tvCommentCount.setText(R.string.label_pick_up___cm);
                } else {
                    size = 3;
                    comment.setRepliesExpanded(false);
                    tvCommentCount.setText(context.getString(R.string.label_comment_count___cm,
                            comment.getRepliedComments()
                                    .size()));
                }
                commentCountLayout.setVisibility(View.VISIBLE);
                commentCountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comment.setRepliesExpanded(!comment.isRepliesExpanded());
                        addComments(context, comment);
                    }
                });
            }
            if (count > size) {
                commentListLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                if (count > i) {
                    view = commentListLayout.getChildAt(i);
                }
                if (view == null) {
                    View.inflate(context,
                            R.layout.service_replied_comment_brief_info_list_item,
                            commentListLayout);
                    view = commentListLayout.getChildAt(commentListLayout.getChildCount() - 1);
                }
                final RepliedComment repliedComment = comment.getRepliedComments()
                        .get(i);
                String userName = repliedComment.getUser()
                        .isMerchant() ? context.getString(R.string
                        .label_comment_for_merchant___cm) : repliedComment.getUser()
                        .getName();
                String replyUserName;
                if (repliedComment.getReplyUser() == null || repliedComment.getReplyUser()
                        .getId() == 0 || repliedComment.getUser()
                        .getId() == repliedComment.getReplyUser()
                        .getId()) {
                    userName = userName + "：";
                    replyUserName = "";
                } else {
                    userName = userName + " ";
                    replyUserName = context.getString(R.string.label_reply_user_comment,
                            repliedComment.getReplyUser()
                                    .isMerchant() ? context.getString(R.string
                                    .label_comment_for_merchant___cm) : repliedComment
                                    .getReplyUser()
                                    .getName()) + "：";
                }
                TextView tvContent = view.findViewById(R.id.tv_content);
                String content = userName + replyUserName + repliedComment.getContent();
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                        content,
                        emojiSizeS);
                if (builder != null) {
                    builder.setSpan(new CommentReplyClickableSpan(context, 0),
                            0,
                            userName.length(),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new CommentReplyClickableSpan(context, 1),
                            userName.length(),
                            content.length(),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tvContent.setText(builder);
                }
                tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCommentListener != null) {
                            onCommentListener.onComment(ServiceCommentViewHolder.this,
                                    comment,
                                    repliedComment);
                        }
                    }
                });
            }
        }
        boolean isPraisesDataEmpty = CommonUtil.isCollectionEmpty(comment.getPraisedUsers());
        praiseLineLayout.setVisibility(!isPraisesDataEmpty && !isCommentsDataEmpty ? View.VISIBLE
                : View.GONE);
        praiseNicksCommentLayout.setVisibility(!isPraisesDataEmpty || !isCommentsDataEmpty ? View
                .VISIBLE : View.GONE);
    }

    private class CommentReplyClickableSpan extends CharacterStyle {
        private Context context;
        private int state;

        private CommentReplyClickableSpan(Context context, int state) {
            this.context = context;
            this.state = state;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            if (state == 0) {
                ds.setFakeBoldText(true);
                ds.setColor(ThemeUtil.getAttrColor(context,
                        R.attr.hljCommentNameColor,
                        ContextCompat.getColor(context, R.color.colorBlack2)));
            } else {
                ds.setColor(ThemeUtil.getAttrColor(context,
                        R.attr.hljColorContent2,
                        ContextCompat.getColor(context, R.color.colorBlack3)));
            }
        }
    }

    public void setShowTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPraiseListener(OnPraiseListener onPraiseListener) {
        this.onPraiseListener = onPraiseListener;
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    public interface OnPraiseListener {
        void onPraise(ServiceCommentViewHolder commentViewHolder, ServiceComment comment);
    }

    public interface OnCommentListener {
        void onComment(
                ServiceCommentViewHolder commentViewHolder,
                ServiceComment comment,
                RepliedComment repliedComment);
    }
}