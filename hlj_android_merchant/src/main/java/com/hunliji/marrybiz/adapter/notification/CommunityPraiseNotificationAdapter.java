package com.hunliji.marrybiz.adapter.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.models.realm.NotificationExtra;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.R;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 互动点赞通知 暂时只有问答的
 * Created by wangtao on 2016/11/23.
 */

public class CommunityPraiseNotificationAdapter extends RecyclerView
        .Adapter<BaseViewHolder<Notification>> {

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener notificationClickListener;
    private View footerView;


    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = -1;


    public CommunityPraiseNotificationAdapter(
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
    public BaseViewHolder<Notification> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return new ExtraViewHolder(footerView);
        } else {
            return new CommunityMergeViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_merga_community, parent, false));
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

    public void notifyNotificationRead(Long mergeId) {
        if(CommonUtil.isCollectionEmpty(notifications)){
            return;
        }
        for(Notification notification:notifications){
            if(notification.getMergeNewsCount()==0){
                continue;
            }
            if(mergeId==null||mergeId.equals(notification.getMergeId())){
                notification.setMergeNewsCount(0);
                notifyItemChanged(notifications.indexOf(notification));
            }

        }
    }

    private class ExtraViewHolder extends BaseViewHolder<Notification> {

        private ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setViewData(
                Context mContext, Notification item, int position, int viewType) {
        }
    }


    public class CommunityMergeViewHolder extends BaseViewHolder<Notification> {

        @BindView(R.id.iv_new)
        View ivNew;
        @BindView(R.id.iv_avatar)
        RoundedImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_body)
        TextView tvBody;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.extra_layout)
        LinearLayout extraLayout;
        @BindView(R.id.iv_extra_cover)
        ImageView ivExtraCover;
        @BindView(R.id.tv_extra_title)
        TextView tvExtraTitle;
        @BindView(R.id.tv_merge_count)
        TextView tvMergeCount;
        private int size;

        public CommunityMergeViewHolder(View itemView) {
            super(itemView);
            size = CommonUtil.dp2px(itemView.getContext(), 40);
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
                Context mContext, Notification item, int position, int viewType) {
            ivNew.setVisibility(item.getMergeNewsCount()>0?View.VISIBLE:View.GONE);
            Glide.with(ivAvatar.getContext())
                    .load(ImageUtil.getAvatar(item.getParticipantAvatar(), size))
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(ivAvatar);
            tvBody.setText(item.getBody());
            if (item.getCreatedAt() != null) {
                tvTime.setText(DateUtils.getRelativeTimeSpanString(item.getCreatedAt()
                        .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
            }

            if (!TextUtils.isEmpty(item.getContent())) {
                tvContent.setText(item.getContent());
                tvContent.setVisibility(View.VISIBLE);
            } else {
                tvContent.setVisibility(View.GONE);
            }

            if (item.getExtraObject() != null && item.getExtraObject()
                    .getExpand() != null) {
                NotificationExtra.ExExpand expand = item.getExtraObject()
                        .getExpand();
                extraLayout.setVisibility(View.VISIBLE);
                tvExtraTitle.setText(expand.getTitle());
                String path = ImageUtil.getImagePath2(expand.getCoverPath(), size);
                if (!TextUtils.isEmpty(path)) {
                    ivExtraCover.setVisibility(View.VISIBLE);
                    Glide.with(ivExtraCover.getContext())
                            .load(path)
                            .apply(new RequestOptions()
                                    .dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image))
                            .into(ivExtraCover);
                } else {
                    Glide.with(ivExtraCover.getContext()).clear(ivExtraCover);
                    ivExtraCover.setVisibility(View.GONE);
                }
            } else {
                extraLayout.setVisibility(View.GONE);
            }
            tvName.getLayoutParams().width = 0;
            tvName.requestLayout();
            if (item.getMergeCount() > 3) {
                tvMergeCount.setText(tvMergeCount.getContext()
                        .getString(R.string.label_merge_count, item.getMergeCount() + ""));
            } else {
                tvMergeCount.setVisibility(View.GONE);
            }
            tvName.setText(item.getMergeParticipantName());
        }
    }
}