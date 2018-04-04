package com.hunliji.marrybiz.adapter.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.R;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 动态问答非点赞通知
 * Created by wangtao on 2016/11/23.
 */

public class CommunityCommentNotificationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener notificationClickListener;
    private View footerView;


    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = -1;


    public CommunityCommentNotificationAdapter(
            Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationClickListener = listener;
        this.notifications = new ArrayList<>();
    }

    public void setNotifications(List<Notification> notificationList) {
        notifications.clear();
        notifications.addAll(notificationList);
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return new ExtraBaseViewHolder(footerView);
        } else {
            return new CommentNotificationViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_community_comment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CommentNotificationViewHolder) {
            ((CommentNotificationViewHolder) holder).setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        if (notifications == null || notifications.isEmpty()) {
            return 0;
        }
        int count = notifications.size();
        if (footerView != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = getItem(position);
        if (notification == null) {
            return FOOTER_TYPE;
        }
        return ITEM_TYPE;
    }

    public Notification getItem(int position) {
        if (notifications != null && notifications.size() > position) {
            return notifications.get(position);
        }
        return null;
    }

    public void notifyItem(Notification notification) {
        if (CommonUtil.isCollectionEmpty(notifications)) {
            return;
        }
        int index = notifications.indexOf(notification);
        if (index >= 0) {
            notifyItemChanged(index);
        }

    }


    public class CommentNotificationViewHolder extends BaseViewHolder<Notification> {

        @BindView(R.id.iv_new)
        View ivNew;
        @BindView(R.id.iv_avatar)
        RoundedImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.line)
        View line;

        public CommentNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationClickListener != null) {
                        notificationClickListener.onItemClick(getItem());
                    }
                }
            });
        }

        @Override
        public void setViewData(
                Context mContext, Notification notification, int position, int viewType) {
            ivNew.setVisibility(notification.isNew() ? View.VISIBLE : View.GONE);
            line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            tvName.setText(notification.getParticipantName());
            Glide.with(mContext)
                    .load(ImageUtil.getAvatar(notification.getParticipantAvatar(),
                            CommonUtil.dp2px(mContext, 45)))
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary)
                            .dontAnimate())
                    .into(ivAvatar);
            if (notification.getCreatedAt() != null) {
                tvTime.setText(DateUtils.getRelativeTimeSpanString(notification.getCreatedAt()
                        .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
            }
            int actionId = 0;
            switch (notification.getAction()) {
                case Notification.NotificationAction.FEED_COMMENT:
                case Notification.NotificationAction.QA_ANSWER_COMMENT:
                    //评论
                    actionId = R.string.label_notification_content_comment;
                    break;
                case Notification.NotificationAction.COMMENT_REPLY:
                case Notification.NotificationAction.QA_ANSWER_COMMENT_REPLY:
                    //回复
                    actionId = R.string.label_notification_content_reply;
                    break;
                case Notification.NotificationAction.POST_QA_ANSWER_HOT:
                    //回答标为热门
                    actionId = R.string.label_notification_qa_answer_hot;
                    break;
                case Notification.NotificationAction.POST_QA_ANSWER_DELETED:
                    //回答被删除
                    actionId = R.string.label_notification_qa_answer_delete;
                    break;
                default:
                    break;

            }
            if (notification.getNotifyType() == Notification.NotificationType.NOTE_TYPE &&
                    !TextUtils.isEmpty(
                    notification.getContent())) {
                tvContent.setText(notification.getContent());
            } else if (actionId == 0) {
                tvContent.setText(notification.getBody());
            } else {
                tvContent.setText(tvContent.getContext()
                        .getString(actionId, notification.getContent()));
            }
            if (TextUtils.isEmpty(notification.getExtraImage())) {
                ivImage.setVisibility(View.GONE);
                if (TextUtils.isEmpty(notification.getExtraText())) {
                    tvText.setVisibility(View.GONE);
                } else {
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(notification.getExtraText());
                }
            } else {
                tvText.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(ImageUtil.getImagePath(notification.getExtraImage(),
                                CommonUtil.dp2px(mContext, 54)))
                        .into(ivImage);
            }
        }
    }
}
