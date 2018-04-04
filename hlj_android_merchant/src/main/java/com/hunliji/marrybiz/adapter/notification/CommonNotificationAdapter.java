package com.hunliji.marrybiz.adapter.notification;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.notification.NotificationGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商家通用通知样式
 * Created by wangtao on 2016/11/23.
 */

public class CommonNotificationAdapter extends RecyclerView.Adapter<BaseViewHolder<Notification>> {

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener notificationClickListener;
    private View footerView;
    private NotificationGroup group;


    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = -1;


    public CommonNotificationAdapter(
            Context context, OnNotificationClickListener listener, NotificationGroup group) {
        this.context = context;
        this.notificationClickListener = listener;
        this.notifications = new ArrayList<>();
        this.group = group;
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
    public BaseViewHolder<Notification> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return new ExtraViewHolder(footerView);
        } else {
            return new CommonNotificationViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Notification> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
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

    public class ExtraViewHolder extends BaseViewHolder<Notification> {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setViewData(
                Context mContext, Notification item, int position, int viewType) {
        }
    }


    public class CommonNotificationViewHolder extends BaseViewHolder<Notification> {

        @BindView(R.id.iv_new)
        View ivNew;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;

        public CommonNotificationViewHolder(View itemView) {
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
            time.setText(DateUtils.getRelativeTimeSpanString(notification.getCreatedAt()
                    .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
            title.setText(getNotifyTypeName(title.getContext(),notification.getNotifyType(),
                    notification.getAction(),notification.getContent()));
            switch (notification.getAction()) {
                case "order_comment_merchant":
                case "order_comment_review_reply_merchant":
                    content.setText(notification.getContent());
                    break;
                default:
                    if (notification.getNotifyType() == Notification.NotificationType.EVENT &&
                            !TextUtils.isEmpty(
                            notification.getContent())) {
                        content.setText(notification.getContent());
                        break;
                    }
                    content.setText(notification.getBody());
                    break;
            }
            icon.setVisibility(View.VISIBLE);
            if (group != null) {
                switch (group) {
                    case ORDER:
                        icon.setImageResource(R.mipmap.icon_notice_order);
                        break;
                    case RESERVATION:
                        icon.setImageResource(R.mipmap.icon_notice_reservation);
                        break;
                    case COUPON:
                        icon.setImageResource(R.mipmap.icon_notice_coupon);
                        break;
                    case EVENT:
                        icon.setImageResource(R.mipmap.icon_notice_event);
                        break;
                    case COMMENT:
                        icon.setImageResource(R.mipmap.icon_notice_comment);
                        break;
                    case INCOME:
                        icon.setImageResource(R.mipmap.icon_notice_income);
                        break;
                    default:
                        icon.setImageResource(R.mipmap.icon_notice_other);
                        break;
                }
            } else {
                icon.setImageResource(R.mipmap.icon_notice_other);
            }
        }

        private String getNotifyTypeName(Context context,int notifyType, String action,String title) {
            switch (notifyType) {
                case 1:
                    return context.getString(R.string.label_notify_type1);
                case 2:
                    return context.getString(R.string.label_notify_type2);
                case 3:
                    return context.getString(R.string.label_notify_type3);
                case 4:
                    return context.getString(R.string.label_notify_type4);
                case 5:
                    return context.getString(R.string.label_notify_type5);
                case 6:
                    return context.getString(R.string.label_notify_type6);
                case 7:
                    return context.getString(R.string.label_notify_type7);
                case 9:
                    return context.getString(R.string.label_notify_type9);
                case 13:
                    return context.getString(R.string.label_notify_type13);
                case 16:
                    if (action.equals(Notification.NotificationAction.QA_QUESTION_MERCHANT) ||
                            action.equals(
                            Notification.NotificationAction.QA_ANSWER_MERCHANT)) {
                        return context.getString(R.string.label_notify_type162);
                    } else {
                        return context.getString(R.string.label_notify_type16);
                    }
                case 19:
                    return context.getString(R.string.label_notify_type19);
                default:
                    if(!TextUtils.isEmpty(title)){
                        return title;
                    }
                    return context.getString(R.string.label_notify_type_default);
            }
        }
    }
}
