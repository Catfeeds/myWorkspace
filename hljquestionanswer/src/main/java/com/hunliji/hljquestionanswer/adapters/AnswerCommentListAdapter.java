package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.models.AnswerComment;
import com.hunliji.hljquestionanswer.utils.QuestionAnswerTogglesUtil;
import com.hunliji.hljquestionanswer.utils.SpanEllipsizeEndHelper;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2016/8/22 回答评论列表
 */
public class AnswerCommentListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AnswerComment> answerComments = new ArrayList<>();
    private long questionAuthId;
    private OnContentClickListener onContentClickListener;
    private int faceSize;
    private int faceSize2;
    private int logoSize;
    private LayoutInflater inflater;

    public AnswerCommentListAdapter(
            Context context, ArrayList<AnswerComment> answerComments, long questionAuthId) {
        this.context = context;
        this.answerComments = answerComments;
        this.inflater = LayoutInflater.from(context);
        DisplayMetrics display = context.getResources()
                .getDisplayMetrics();
        this.faceSize = Math.round(display.density * 18);
        this.faceSize2 = Math.round(display.density * 14);
        this.logoSize = Math.round(display.density * 40);
        this.questionAuthId = questionAuthId;
    }


    @Override
    public int getCount() {
        return answerComments.size();
    }

    @Override
    public Object getItem(int position) {
        return answerComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        @BindView(R2.id.user_layout)
        LinearLayout userLayout;
        @BindView(R2.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R2.id.tv_user_nick)
        TextView userNick;
        @BindView(R2.id.tv_question_auth)
        TextView questionAuth;
        @BindView(R2.id.tv_post_time)
        TextView postTime;
        @BindView(R2.id.check_praised)
        LinearLayout checkPraised;
        @BindView(R2.id.img_thumb_up)
        ImageView imgThumbUp;
        @BindView(R2.id.tv_praise_count)
        TextView praiseCount;
        @BindView(R2.id.tv_add)
        TextView add;
        @BindView(R2.id.content_layout)
        LinearLayout contentLayout;
        @BindView(R2.id.tv_content)
        TextView content;
        @BindView(R2.id.reply_layout)
        LinearLayout replyLayout;
        @BindView(R2.id.reply_content_layout)
        LinearLayout replyContentLayout;
        @BindView(R2.id.tv_reply_user_nick)
        TextView replyUserNick;
        @BindView(R2.id.tv_reply_content)
        TextView replyContent;
        @BindView(R2.id.tv_reply_hidden)
        TextView replyHidden;
        @BindView(R2.id.split)
        View split;
        @BindView(R2.id.iv_tag)
        ImageView ivTag;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AnswerComment item = answerComments.get(position);
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.answer_comment_list_item___qa, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (position != answerComments.size() - 1) {
            vh.split.setVisibility(View.VISIBLE);
        } else {
            vh.split.setVisibility(View.GONE);
        }
        //头像
        String avatar = ImageUtil.getImagePath2(item.getUser()
                .getAvatar(), logoSize);
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(context)
                    .load(avatar)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(vh.imgAvatar);
        } else {
            Glide.with(context)
                    .clear(vh.imgAvatar);
            vh.imgAvatar.setImageResource(R.mipmap.icon_avatar_primary);
        }

        if (item.getUser()
                .getKind() == 1) {
            vh.ivTag.setVisibility(View.VISIBLE);
            vh.ivTag.setImageResource(R.mipmap.icon_vip_blue_28_28);
        } else if (!TextUtils.isEmpty(item.getUser()
                .getSpecialty()) && !"普通用户".equals(item.getUser()
                .getSpecialty())) {
            vh.ivTag.setVisibility(View.VISIBLE);
            vh.ivTag.setImageResource(R.mipmap.icon_vip_yellow_28_28);
        } else if (item.getUser()
                .getMember()
                .getId() > 0) {
            vh.ivTag.setVisibility(View.VISIBLE);
            vh.ivTag.setImageResource(R.mipmap.icon_member_28_28);
        } else {
            vh.ivTag.setVisibility(View.GONE);
        }
        //评论昵称
        if (!TextUtils.isEmpty(item.getUser()
                .getName())) {
            vh.userNick.setVisibility(View.VISIBLE);
            vh.userNick.setText(item.getUser()
                    .getName());
            if (questionAuthId == item.getUser()
                    .getId()) {
                //显示题主标志
                vh.questionAuth.setVisibility(View.VISIBLE);
            } else {
                vh.questionAuth.setVisibility(View.GONE);
            }
        } else {
            vh.userNick.setVisibility(View.GONE);
        }
        //评论时间
        vh.postTime.setText(HljTimeUtils.getShowTime(context, item.getCreatedAt()));
        //是否点赞
        if (item.isLike()) {
            vh.imgThumbUp.setImageResource(R.mipmap.icon_praise_primary_40_40);
            vh.praiseCount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            vh.imgThumbUp.setImageResource(R.mipmap.icon_praise_gray_40_40);
            vh.praiseCount.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }
        //点赞个数
        vh.praiseCount.setText(String.valueOf(item.getLikesCount()));
        //评论内容b不为空
        if (!TextUtils.isEmpty(item.getContent())) {
            SpannableStringBuilder style;
            String atNick;
            if (item.getReply() == null || item.getReply()
                    .getUser() == null || TextUtils.isEmpty(item.getReply()
                    .getUser()
                    .getName())) {
                atNick = "";
            } else {
                atNick = "@" + item.getReply()
                        .getUser()
                        .getName() + " ";
            }
            String content = atNick + item.getContent();
            style = (EmojiUtil.parseEmojiByText2(context, content, faceSize));
            if (style != null) {
                style.setSpan(new ClickableSpanImpl(0, position, item),
                        0,
                        atNick.length(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                style.setSpan(new ClickableSpanImpl(1, position, item),
                        atNick.length(),
                        content.length(),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                vh.content.setText(SpanEllipsizeEndHelper.matchMaxWidth(style, vh.content));
            } else {
                vh.content.setText(null);
            }
            vh.content.setMovementMethod(LinkMovementMethod.getInstance());
            vh.content.setVisibility(View.VISIBLE);
        } else {
            vh.content.setVisibility(View.GONE);
        }
        //回复不为空
        if (item.getReply() != null) {
            vh.replyLayout.setVisibility(View.VISIBLE);
            //如果评论里面的回复被删除掉了
            if (item.getReply()
                    .isDeleted()) {
                vh.replyContentLayout.setVisibility(View.GONE);
                vh.replyHidden.setVisibility(View.VISIBLE);
            } else {
                vh.replyContentLayout.setVisibility(View.VISIBLE);
                vh.replyHidden.setVisibility(View.GONE);
                //回复昵称
                if (item.getReply()
                        .getUser() == null || TextUtils.isEmpty(item.getReply()
                        .getUser()
                        .getName())) {
                    vh.replyUserNick.setVisibility(View.GONE);
                } else {
                    vh.replyUserNick.setText(String.format(item.getReply()
                            .getUser()
                            .getName()) + "：");
                    vh.replyUserNick.setVisibility(View.VISIBLE);
                }
                //回复内容
                if (!TextUtils.isEmpty(item.getReply()
                        .getContent())) {
                    vh.replyContent.setVisibility(View.VISIBLE);
                    vh.replyContent.setText(EmojiUtil.parseEmojiByText2(context,
                            item.getReply()
                                    .getContent(),
                            faceSize2));
                } else {
                    vh.replyContent.setVisibility(View.GONE);
                }
            }
        } else {
            vh.replyLayout.setVisibility(View.GONE);
        }
        vh.contentLayout.setOnClickListener(new OnClickListenerImpl(position, item));
        vh.userLayout.setOnClickListener(new OnClickListenerImpl(position, item));
        final ViewHolder vhs = vh;
        vh.checkPraised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionAnswerTogglesUtil.onAnswerCommentPraise((Activity) context,
                        vhs.checkPraised,
                        vhs.imgThumbUp,
                        vhs.praiseCount,
                        vhs.add,
                        item);
            }
        });
        return convertView;
    }

    private class OnClickListenerImpl implements View.OnClickListener {

        private int position;

        private AnswerComment item;

        public OnClickListenerImpl(int position, AnswerComment item) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.content_layout) {
                if (onContentClickListener != null) {
                    onContentClickListener.onContentClick(position, item);
                }

            } else if (i == R.id.user_layout) {
                if (item.getUser() == null || HljQuestionAnswer.isMerchant(context)) {
                    return;
                }
                if (item.getUser()
                        .getKind() == 0) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                            .withLong("id",
                                    item.getUser()
                                            .getId())
                            .navigation(context);
                } else {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                            .withLong("id",
                                    item.getUser()
                                            .getMerchantId())
                            .navigation(context);
                }
            }
        }
    }

    public void setOnContentClickListener(OnContentClickListener onContentClickListener) {
        this.onContentClickListener = onContentClickListener;
    }

    public interface OnContentClickListener {
        void onContentClick(int position, AnswerComment item);
    }

    private class ClickableSpanImpl extends ClickableSpan {

        private int state;

        private int position;

        private AnswerComment item;

        public ClickableSpanImpl(int state, int position, AnswerComment item) {
            this.state = state;
            this.position = position;
            this.item = item;
        }

        @Override
        public void onClick(View widget) {
            if (state == 0) {
                if (item.getReply() == null || item.getReply()
                        .getUser() == null || HljQuestionAnswer.isMerchant(context)) {
                    return;
                }
                if (item.getReply()
                        .getUser()
                        .getKind() == 0) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                            .withLong("id",
                                    item.getReply()
                                            .getUser()
                                            .getId())
                            .navigation(context);
                } else {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                            .withLong("id",
                                    item.getReply()
                                            .getUser()
                                            .getMerchantId())
                            .navigation(context);
                }
            } else {
                if (onContentClickListener != null) {
                    onContentClickListener.onContentClick(position, item);
                }
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (state == 0) {
                ds.setColor(ContextCompat.getColor(context, R.color.blue));
            } else {
                ds.setColor(ContextCompat.getColor(context, R.color.colorBlack3));
            }
            ds.setUnderlineText(false);
        }
    }
}