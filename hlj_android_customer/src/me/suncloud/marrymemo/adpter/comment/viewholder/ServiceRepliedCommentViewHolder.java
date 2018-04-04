package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 服务评价列表viewHolder
 * Created by chen_bin on 2017/6/8 0008.
 */
public class ServiceRepliedCommentViewHolder extends BaseViewHolder<RepliedComment> {
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.comment_view)
    LinearLayout commentView;
    private int logoSize;
    private int emojiSize;
    private OnRepliedCommentListener onRepliedCommentListener;

    public ServiceRepliedCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.logoSize = CommonUtil.dp2px(itemView.getContext(), 28);
        this.emojiSize = CommonUtil.dp2px(itemView.getContext(), 24);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRepliedCommentListener != null) {
                    onRepliedCommentListener.onComment(ServiceRepliedCommentViewHolder.this,
                            getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, RepliedComment repliedComment, int position, int viewType) {
        if (repliedComment == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(repliedComment.getUser()
                        .getAvatar())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        tvName.setText(repliedComment.getUser()
                .isMerchant() ? mContext.getString(R.string.label_comment_for_merchant___cm) :
                repliedComment.getUser()
                .getName());
        if (repliedComment.getCreatedAt() == null) {
            tvTime.setVisibility(View.GONE);
        } else {
            tvTime.setText(repliedComment.getCreatedAt()
                    .toString(mContext.getString(R.string.format_date_type11)));
        }
        String replyUserName;
        if (repliedComment.getReplyUser() == null || repliedComment.getReplyUser()
                .getId() == 0 || repliedComment.getUser()
                .getId() == repliedComment.getReplyUser()
                .getId()) {
            replyUserName = "";
        } else {
            replyUserName = "回复 " + (repliedComment.getReplyUser()
                    .isMerchant() ? mContext.getString(R.string.label_comment_for_merchant___cm)
                    : repliedComment.getReplyUser()
                    .getName()) + "：";
        }
        String content = replyUserName + repliedComment.getContent();
        SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext, content, emojiSize);
        if (builder != null) {
            builder.setSpan(new ClickableSpan(0),
                    0,
                    replyUserName.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(new ClickableSpan(1),
                    replyUserName.length(),
                    content.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvContent.setText(builder);
        }
    }

    private class ClickableSpan extends CharacterStyle {

        private int state;

        private ClickableSpan(int state) {
            this.state = state;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            if (state == 0) {
                ds.setColor(ContextCompat.getColor(itemView.getContext(), R.color.colorLink));
            } else {
                ds.setColor(ContextCompat.getColor(itemView.getContext(), R.color.colorBlack2));
            }
        }
    }

    public void setItemBottomMargin(int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) commentView
                .getLayoutParams();
        params.bottomMargin = bottomMargin;
    }

    public void setOnRepliedCommentListener(OnRepliedCommentListener onRepliedCommentListener) {
        this.onRepliedCommentListener = onRepliedCommentListener;
    }

    public interface OnRepliedCommentListener {
        void onComment(
                ServiceRepliedCommentViewHolder repliedCommentViewHolder,
                RepliedComment repliedComment);
    }
}