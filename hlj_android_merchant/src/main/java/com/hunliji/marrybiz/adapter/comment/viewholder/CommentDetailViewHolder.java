package com.hunliji.marrybiz.adapter.comment.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.CommentDetailAdapter;
import com.hunliji.marrybiz.model.comment.SubmitAppealBody;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/13.
 * 评论详情 item
 */

public class CommentDetailViewHolder extends BaseViewHolder<RepliedComment> {

    @BindView(R.id.tv_reply_content)
    TextView tvReplyContent;
    @BindView(R.id.ib_complain)
    ImageButton ibComplain;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_reply_time)
    TextView tvReplyTime;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    private CommentDetailAdapter.OnCommentListener onCommentListener;
    private List<RepliedComment> replyList = new ArrayList<>();

    public void setReplyList(List<RepliedComment> replyList) {
        this.replyList = replyList;
    }

    public void setOnReplyUserListener(CommentDetailAdapter.OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    /**
     * 隐藏申诉icon
     */
    public void hideComplainIcon() {
        ibComplain.setVisibility(View.GONE);
    }

    public void hideLine() {
        line.setVisibility(View.GONE);
    }


    public CommentDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            final Context context, final RepliedComment reply, final int position, int viewType) {
        if (reply != null) {
            itemView.setVisibility(View.VISIBLE);
            line.setVisibility(position == replyList.size() - 1 ? View.GONE : View.VISIBLE);
            String content = reply.getContent();
            //user 回复 replyUser
            final Author user = reply.getUser();
            Glide.with(context)
                    .load(ImagePath.buildPath(user.getAvatar())
                            .width(CommonUtil.dp2px(context, 27))
                            .height(CommonUtil.dp2px(context, 27))
                            .cropPath())
                    .into(ivAvatar);
            Author replyUser = reply.getReplyUser();
            ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(context,
                    R.color.colorPrimary));
            tvUserName.setText(user.isMerchant() ? context.getString(R.string
                    .label_merchant_comment) : user.getName());
            DateTime replyTime = reply.getCreatedAt();
            if (replyTime != null) {
                tvReplyTime.setText(replyTime.toString(context.getString(R.string
                        .format_date_type12)));
            }
            if (replyUser == null || replyUser.getId() <= 0) {
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                        content,
                        CommonUtil.dp2px(context, 14));
                tvReplyContent.setText(builder);
            } else {
                String replyName = replyUser.getName();
                if (replyUser.isMerchant()) {
                    replyName = context.getString(R.string.label_merchant_comment);
                }
                String replyHeader = "回复" + replyName + ":";
                content = replyHeader + content;
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context,
                        content,
                        CommonUtil.dp2px(context, 14));
                builder.setSpan(span, 0, replyHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvReplyContent.setText(builder);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!user.isMerchant()) {
                        if (onCommentListener != null) {
                            onCommentListener.onReplyUser(reply);
                        }
                    } else {
                        if (onCommentListener != null) {
                            onCommentListener.onDeleteClick(position, reply);
                        }
                    }
                }
            });
            //0 待审核 1 已处理 2未发起申诉
            if (user.isMerchant()) {
                ibComplain.setVisibility(View.GONE);
            } else {
                ibComplain.setVisibility(View.VISIBLE);
                ibComplain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCommentListener != null) {
                            onCommentListener.onComplainClick(position,
                                    reply,
                                    SubmitAppealBody.TYPE_COMMENT);
                        }
                    }
                });
            }
        }
    }
}
