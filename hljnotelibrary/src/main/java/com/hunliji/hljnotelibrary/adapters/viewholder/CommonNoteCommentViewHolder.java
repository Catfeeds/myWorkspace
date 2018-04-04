package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.HljNote;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/7/11.通用评论
 */

public class CommonNoteCommentViewHolder extends BaseViewHolder<RepliedComment> {

    @BindView(R2.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R2.id.tv_nick)
    TextView tvNick;
    @BindView(R2.id.tv_reply_time)
    TextView tvReplyTime;
    @BindView(R2.id.tv_auth_address_and_notes)
    TextView tvAuthAddressAndNotes;
    @BindView(R2.id.user_layout)
    LinearLayout userLayout;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.tv_from_note)
    TextView tvFromNote;
    @BindView(R2.id.img_vip)
    ImageView imgVip;

    private int logoSize;
    private int faceSize;
    private OnCommentListener onCommentListener;

    public CommonNoteCommentViewHolder(final View itemView, String entityType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.faceSize = CommonUtil.dp2px(itemView.getContext(), 18);
        this.logoSize = CommonUtil.dp2px(itemView.getContext(), 40);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepliedComment comment = getItem();
                if (comment != null && comment.getId() > 0) {
                    onGoUserProfile(comment.getUser(), itemView.getContext());
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
        if (entityType.equals(HljNote.NOTE_BOOK_TYPE)) {
            tvFromNote.setVisibility(View.VISIBLE);
            tvFromNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) itemView.getContext();
                    RepliedComment repliedComment = getItem();
                    if (repliedComment != null && repliedComment.getId() != 0) {
                        Intent intent = new Intent(itemView.getContext(), NoteDetailActivity.class);
                        intent.putExtra("note_id", repliedComment.getEntityId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        } else {
            tvFromNote.setVisibility(View.GONE);
        }
    }

    public void hideBottomLine() {
        lineLayout.setVisibility(View.GONE);
    }

    //昵称点击
    private class MyClickableSpan extends ClickableSpan {
        private int state;
        private RepliedComment comment;
        private Context context;

        private MyClickableSpan(Context context, int state, RepliedComment comment) {
            this.state = state;
            this.comment = comment;
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            if (state == 0) {
                onGoUserProfile(comment.getUser(), context);
            } else if (state == 1) {
                if (onCommentListener != null) {
                    onCommentListener.onComment(comment);
                }
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (state == 0) {
                ds.setColor(ContextCompat.getColor(context, R.color.colorLink));
            } else {
                ds.setColor(ContextCompat.getColor(context, R.color.colorBlack3));
            }
            ds.setUnderlineText(false);
        }
    }

    @Override
    protected void setViewData(
            Context context, RepliedComment comment, int position, int viewType) {
        if (comment == null) {
            return;
        }
        Author author = comment.getUser();
        Glide.with(context)
                .load(ImagePath.buildPath(author.getAvatar())
                        .width(logoSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary)
                        .error(R.mipmap.icon_avatar_primary))
                .into(imgAvatar);
        if (author.getKind() == 1) {
            imgVip.setVisibility(View.VISIBLE);
            imgVip.setImageResource(R.mipmap.icon_vip_blue_28_28);
        } else if (!TextUtils.isEmpty(author.getSpecialty()) && !"普通用户".equals(author
                .getSpecialty())) {
            imgVip.setVisibility(View.VISIBLE);
            imgVip.setImageResource(R.mipmap.icon_vip_yellow_28_28);
        } else if (author.getMember()
                .getId() > 0) {
            imgVip.setVisibility(View.VISIBLE);
            imgVip.setImageResource(R.mipmap.icon_member_28_28);
        } else {
            imgVip.setVisibility(View.GONE);
        }
        tvNick.setText(author.getName());
        tvReplyTime.setText(comment.getCreatedAt() == null ? "" : HljTimeUtils.getShowTime(context,
                comment.getCreatedAt()));
        if (!TextUtils.isEmpty(author.getHometown())) {
            tvAuthAddressAndNotes.setText(context.getString(R.string
                            .fmt_address_and_note_count___note,

                    author.getHometown(), author.getNoteCount()));
        } else {
            tvAuthAddressAndNotes.setText(context.getString(R.string.label_note_count___note,
                    author.getNoteCount()));
        }
        String atNick;
        if (comment.getReplyUser() == null || comment.getReplyUser()
                .getId() == 0 || comment.getReplyUser()
                .getId() == author.getId()) {
            atNick = "";
        } else {
            atNick = "回复 " + comment.getReplyUser()
                    .getName() + " ";
        }
        String content = atNick + comment.getContent();
        SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(context, content, faceSize);
        if (builder != null) {
            builder.setSpan(new MyClickableSpan(context, 0, comment),
                    0,
                    atNick.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(new MyClickableSpan(context, 1, comment),
                    atNick.length(),
                    content.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }
        tvContent.setText(builder);
    }

    public interface OnCommentListener {
        void onComment(RepliedComment comment);
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    private void onGoUserProfile(Author author, Context context) {
        if (HljNote.isMerchant(context)) {
            return;
        }
        if (author != null && author.getId() > 0) {
            if (author.getKind() == 0) {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                        .withLong("id", author.getId())
                        .navigation(context);
            } else {
                ARouter.getInstance()
                        .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                        .withLong("id", author.getMerchantId())
                        .navigation(context);
            }
        }
    }
}
