package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.realm.Notification;
import com.hunliji.hljcommonlibrary.models.realm.NotificationExtra;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/9/7.
 * 所有的通知的列表（包括通知、社区通知、订单通知、活动通知）的Adapter
 */
public class NotificationRecyclerAdapter extends RecyclerView
        .Adapter<BaseViewHolder<Notification>> {

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener notificationClickListener;
    private View footerView;

    private static final int FLAG = 3;
    private static final int ITEM_TYPE = 0;
    private static final int GROUP_TYPE = 1;
    private static final int MERGE_TYPE = 2;

    private static final int FOOTER_TYPE = -1;

    public NotificationRecyclerAdapter(
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

    @Override
    public BaseViewHolder<Notification> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_TYPE) {
            return new ExtraViewHolder(getView(parent, viewType));
        } else if (viewType % FLAG == ITEM_TYPE) {
            switch (viewType / FLAG) {

                case Notification.NotificationType.ORDER:
                    return new OrderItemViewHolder(getView(parent, viewType));
                case Notification.NotificationType.EVENT:
                    return new EventItemViewHolder(getView(parent, viewType));
                case Notification.NotificationType.NOTE_TYPE:
                case Notification.NotificationType.SUB_PAGE:
                case Notification.NotificationType.COMMUNITY:
                    return new CommunityItemViewHolder(getView(parent, viewType));
                case Notification.NotificationType.FINANCIAL:
                    return new FinancialItemViewHolder(getView(parent, viewType));
                //                case Notification.NotificationType.MERCHANT_FEED:
                //                case Notification.NotificationType.PARTNER_INVITE:
                //                case Notification.NotificationType.ORDER_COMMENT:
                //                case Notification.NotificationType.RECV_INSURANCE:
                //                case Notification.NotificationType.APPOINTMENT_LIVE_START:
                //                case Notification.NotificationType.HUNLIJI:
                default:
                    return new NotificationItemViewHolder(getView(parent, viewType));
            }
        } else if (viewType % FLAG == GROUP_TYPE) {
            switch (viewType / FLAG) {
                case Notification.NotificationType.COMMUNITY:
                    return new CommunityGroupViewHolder(getView(parent, viewType));
                case Notification.NotificationType.ORDER:
                    return new OrderGroupViewHolder(getView(parent, viewType));
                case Notification.NotificationType.SIGN:
                    return new SignGroupViewHolder(getView(parent, viewType));
                case Notification.NotificationType.GIFT:
                    return new GiftGroupViewHolder(getView(parent, viewType));
                case Notification.NotificationType.EVENT:
                    return new EventGroupViewHolder(getView(parent, viewType));
                case Notification.NotificationType.FINANCIAL:
                    return new FinancialGroupViewHolder(getView(parent, viewType));
            }
        } else if (viewType % FLAG == MERGE_TYPE) {
            switch (viewType / FLAG) {
                case Notification.NotificationType.COMMUNITY:
                case Notification.NotificationType.SUB_PAGE:
                    return new CommunityMergeViewHolder(getView(parent, viewType));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Notification> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    private View getView(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == FOOTER_TYPE) {
            view = footerView;
        } else if (viewType % FLAG == ITEM_TYPE) {
            switch (viewType / FLAG) {
                case Notification.NotificationType.ORDER:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_item_order, parent, false);
                    break;
                case Notification.NotificationType.EVENT:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_item_event, parent, false);
                    break;
                case Notification.NotificationType.SUB_PAGE:
                case Notification.NotificationType.NOTE_TYPE:
                case Notification.NotificationType.COMMUNITY:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_item_community, parent, false);
                    break;
                case Notification.NotificationType.FINANCIAL:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_item_financial, parent, false);
                    break;
                //                case Notification.NotificationType.MERCHANT_FEED:
                //                case Notification.NotificationType.PARTNER_INVITE:
                //                case Notification.NotificationType.ORDER_COMMENT:
                //                case Notification.NotificationType.RECV_INSURANCE:
                //                case Notification.NotificationType.APPOINTMENT_LIVE_START:
                //                case Notification.NotificationType.HUNLIJI:
                default:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_item, parent, false);
                    break;
            }
        } else if (viewType % FLAG == GROUP_TYPE) {
            switch (viewType / FLAG) {
                case Notification.NotificationType.COMMUNITY:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_group_community, parent, false);
                    break;
                case Notification.NotificationType.ORDER:
                case Notification.NotificationType.SIGN:
                case Notification.NotificationType.GIFT:
                case Notification.NotificationType.EVENT:
                case Notification.NotificationType.FINANCIAL:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_group_comment, parent, false);
                    break;
            }
        } else if (viewType % FLAG == MERGE_TYPE) {
            switch (viewType / FLAG) {
                case Notification.NotificationType.COMMUNITY:
                case Notification.NotificationType.SUB_PAGE:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.notification_merga_community, parent, false);
                    break;
            }
        }

        return view;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
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
        if (notification.isGroup()) {
            return notification.getNotifyType() * FLAG + GROUP_TYPE;
        }
        if (notification.isMerge()) {
            return notification.getNotifyType() * FLAG + MERGE_TYPE;
        } else
            return notification.getNotifyType() * FLAG + ITEM_TYPE;
    }

    public Notification getItem(int position) {
        if (notifications != null && notifications.size() > position) {
            return notifications.get(position);
        }
        return null;
    }

    public class ExtraViewHolder extends BaseViewHolder<Notification> {

        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
        }
    }

    private class BaseNotificationGroupViewHolder extends BaseViewHolder<Notification> {

        private View line;
        private TextView tvNewsCount;
        private TextView tvTime;
        protected ImageView ivIcon;
        protected TextView tvTitle;
        protected TextView tvContent;

        public BaseNotificationGroupViewHolder(View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvNewsCount = (TextView) itemView.findViewById(R.id.tv_news_count);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationClickListener != null) {
                        notificationClickListener.onGroupClick(getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            if (item.getNewCount() > 0) {
                tvNewsCount.setVisibility(View.VISIBLE);
                tvNewsCount.setText(item.getNewCount() > 99 ? "99+" : String.valueOf(item
                        .getNewCount()));
            } else {
                tvNewsCount.setVisibility(View.GONE);
            }
            if (item.getCreatedAt() != null) {
                tvTime.setText(DateUtils.getRelativeTimeSpanString(item.getCreatedAt()
                        .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
            }
        }
    }

    private class SignGroupViewHolder extends BaseNotificationGroupViewHolder {

        public SignGroupViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_color_ff8cd0);
            ivIcon.setImageResource(R.drawable.icon_news_sign);
            tvTitle.setText(R.string.label_news_sign);
            tvContent.setText(R.string.notification_sign_type1);
        }
    }

    private class GiftGroupViewHolder extends BaseNotificationGroupViewHolder {

        public GiftGroupViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_color_ffaf24);
            ivIcon.setImageResource(R.drawable.icon_news_gift);
            tvTitle.setText(R.string.label_news_gift);
            tvContent.setText(item.getBody());
        }
    }


    private class FinancialGroupViewHolder extends BaseNotificationGroupViewHolder {

        FinancialGroupViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_color_8c94ff);
            ivIcon.setImageResource(R.drawable.icon_wallet_bag);
            tvTitle.setText(R.string.label_news_financial);
            tvContent.setText(item.getContent());
        }
    }

    private class EventGroupViewHolder extends BaseNotificationGroupViewHolder {

        public EventGroupViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_ff8c47);
            ivIcon.setImageResource(R.drawable.icon_news_event);
            tvTitle.setText(R.string.label_news_event);
            tvContent.setText(item.getContent());
        }
    }

    private class OrderGroupViewHolder extends BaseNotificationGroupViewHolder {

        public OrderGroupViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_color_acdd43);
            ivIcon.setImageResource(R.drawable.icon_news_order);
            tvTitle.setText(R.string.label_news_order);
            int stringId = 0;
            if (!TextUtils.isEmpty(item.getAction())) {
                switch (item.getAction()) {
                    case "receive_order":
                        stringId = R.string.notification_order_type1;
                        break;
                    case "refuse_order":
                    case "car_order_refusedto":
                        stringId = R.string.notification_order_type2;
                        break;
                    case "success_refund":
                        stringId = R.string.notification_order_type3;
                        break;
                    case "refuse_refund":
                        stringId = R.string.notification_order_type4;
                        break;
                    case "change_price":
                        stringId = R.string.notification_order_type5;
                        break;
                    case "remind_payrest":
                    case "car_order_deposited":
                        stringId = R.string.notification_order_type6;
                        break;
                    case "send_express":
                        stringId = R.string.notification_order_type7;
                        break;
                    case "shopchange_price":
                        stringId = R.string.notification_order_type8;
                        break;
                    case "shopsuccess_refund":
                    case "car_order_refund":
                        stringId = R.string.notification_order_type3;
                        break;
                    case "shopchange_shipping":
                        stringId = R.string.notification_order_type9;
                        break;
                    case "car_change_price":
                        stringId = R.string.notification_order_type10;
                        break;
                    case "car_order_receiving":
                        stringId = R.string.notification_order_type11;
                        break;
                    default:
                        break;

                }
            }
            if (stringId > 0) {
                tvContent.setText(stringId);
            } else {
                tvContent.setText(item.getBody());
            }
        }
    }

    private class CommunityGroupViewHolder extends BaseNotificationGroupViewHolder {

        private TextView tvName;

        public CommunityGroupViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            ivIcon.setBackgroundResource(R.drawable.sp_r22_color_ffcd3b);
            ivIcon.setImageResource(R.drawable.icon_news_community);
            tvTitle.setText(R.string.label_news_community);
            int stringId = 0;
            if (!TextUtils.isEmpty(item.getAction())) {
                switch (item.getAction()) {
                    case "plus1":
                        stringId = R.string.notification_community_type4;
                        break;
                    case "thread_reply":
                        stringId = R.string.notification_community_type1;
                        break;
                    case "post_reply":
                        stringId = R.string.notification_community_type2;
                        break;
                    case "post_praise":
                        stringId = R.string.notification_community_type3;
                        break;
                    case "story_praise":
                        stringId = R.string.notification_community_type5;
                        break;
                    case "story_comment":
                        stringId = R.string.notification_community_type6;
                        break;
                    case "story_comment_reply":
                        stringId = R.string.notification_community_type7;
                    default:
                        break;
                }
            }
            if ("del_post".equals(item.getAction()) || "del_thread".equals(item.getAction())) {
                tvName.setVisibility(View.GONE);
            } else {
                tvName.setVisibility(View.VISIBLE);
                tvName.getLayoutParams().width = 0;
                tvName.requestLayout();
                tvName.setText(item.getParticipantName());
            }
            if (stringId > 0) {
                tvContent.setText(stringId);
            } else {
                tvContent.setText(item.getBody());
            }
        }
    }

    /**
     * 通知主页列表里非组合类单条消息的View holder，统一使用这个显示
     * 目前的种类包括MERCHANT_FEED、SUB_PAGE、PARTNER_INVITE、HUNLIJI:
     */
    class NotificationItemViewHolder extends BaseViewHolder<Notification> {
        @BindView(R.id.iv_avatar)
        RoundedImageView ivAvatar;
        @BindView(R.id.ic_new)
        View icNew;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.content_layout)
        LinearLayout contentLayout;
        @BindView(R.id.line)
        View line;

        NotificationItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationClickListener != null) {
                        notificationClickListener.onItemClick(getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            line.setVisibility(position == notifications.size() - 1 ? View.GONE : View.VISIBLE);
            icNew.setVisibility(item.isNew() ? View.VISIBLE : View.GONE);

            if (item.getNotifyType() == Notification.NotificationType.HUNLIJI) {
                //系统通知
                tvName.setText("纪小犀");
                ivAvatar.setImageResource(R.drawable.icon_jixiaoxi_112_112);
            } else {
                switch (item.getAction()) {
                    // 设置名称，如果是邀请另一半的金币通知，需要设置特殊昵称和头像
                    case Notification.NotificationAction.ACCEPT_PARTNER_POINT:
                    case Notification.NotificationAction.INVITE_PARTNER_POINT:
                    case Notification.NotificationAction.UNBIND_PARTNER:
                    case Notification.NotificationAction.NOTE_JOIN_REPOSITORY:
                    case Notification.NotificationAction.APPOINTMENT_LIVE_START:
                        tvName.setText("纪小犀");
                        ivAvatar.setImageResource(R.drawable.icon_jixiaoxi_112_112);
                        break;
                    default:
                        tvName.setText(item.getParticipantName());
                        Glide.with(mContext)
                                .load(ImageUtil.getAvatar(item.getParticipantAvatar(),
                                        CommonUtil.dp2px(mContext, 45)))
                                .apply(new RequestOptions().placeholder(R.mipmap
                                        .icon_avatar_primary)
                                        .dontAnimate())
                                .into(ivAvatar);
                        break;
                }
            }

            // 设置content，各种类型的通知需要做一定的修改
            switch (item.getAction()) {
                case Notification.NotificationAction.SUBPAGE_COMMENT_PRAISE:
                    // 专栏点赞
                    tvContent.setText(R.string.notification_subpage_praise);
                    break;
                case Notification.NotificationAction.COMMENT_REPLY:
                case Notification.NotificationAction.SUBPAGE_COMMENT_REPLY:
                    // 动态专题回复
                    tvContent.setText("回复：" + item.getContent());
                    break;
                case Notification.NotificationAction.ORDER_COMMENT_REVIEW:
                case Notification.NotificationAction.ORDER_COMMENT_REVIEW_REPLY:
                case Notification.NotificationAction.DEL_NOTE:
                    tvContent.setText(item.getContent());
                    break;
                default:
                    tvContent.setText(item.getBody());
                    break;
            }
            if (TextUtils.isEmpty(item.getExtraImage())) {
                ivImage.setVisibility(View.GONE);
            } else {
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(ImageUtil.getImagePath(item.getExtraImage(),
                                CommonUtil.dp2px(mContext, 54)))
                        .into(ivImage);
            }
            if (item.getCreatedAt() != null) {
                tvTime.setText(DateUtils.getRelativeTimeSpanString(item.getCreatedAt()
                        .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
            }
        }
    }

    private class OrderItemViewHolder extends BaseViewHolder<Notification> {

        private TextView tvTime;
        private TextView tvBody;
        private TextView tvContent;
        private TextView tvTitle;
        private TextView tvMerchantName;
        private ImageView ivCover;
        private ImageView icCustom;
        private RelativeLayout workLayout;
        private int coverSize;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            coverSize = Math.round(CommonUtil.getDeviceSize(itemView.getContext()).x / 5);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvBody = (TextView) itemView.findViewById(R.id.tv_body);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvMerchantName = (TextView) itemView.findViewById(R.id.tv_merchant_name);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            icCustom = (ImageView) itemView.findViewById(R.id.ic_custom);
            workLayout = (RelativeLayout) itemView.findViewById(R.id.work_layout);
            workLayout.getLayoutParams().height = coverSize;
            ivCover.getLayoutParams().width = coverSize;

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
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            tvBody.setText(item.getBody());
            if (!TextUtils.isEmpty(item.getContent())) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(item.getContent());
            } else {
                tvContent.setVisibility(View.GONE);
            }

            DateTime currentDate = null;
            DateTime lastDate = null;

            if (item.getCreatedAt() != null) {
                currentDate = new DateTime(item.getCreatedAt());
            }
            if (position > 0) {
                Notification lastItem = NotificationRecyclerAdapter.this.getItem(position - 1);
                if (lastItem != null && lastItem.getCreatedAt() != null) {
                    lastDate = new DateTime(lastItem.getCreatedAt());
                }
            }
            if (currentDate == null || (lastDate != null && lastDate.getDayOfYear() ==
                    currentDate.getDayOfYear())) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(currentDate.toString("yyyy-MM-dd"));
            }

            workLayout.setVisibility(View.GONE);
            icCustom.setVisibility(View.GONE);
            if (item.getExtraObject() != null) {
                NotificationExtra extraObject = item.getExtraObject();
                String merchantName = null;
                if (extraObject.getMerchant() != null) {
                    merchantName = extraObject.getMerchant()
                            .getName();
                }
                if (extraObject.getSetMeal() != null) {
                    NotificationExtra.ExSetMeal setMeal = extraObject.getSetMeal();
                    workLayout.setVisibility(View.VISIBLE);
                    setCover(setMeal.getCoverPath());
                    tvTitle.setText(setMeal.getTitle());
                } else if (extraObject.getCustomSetMeal() != null) {
                    NotificationExtra.ExCustomSetMeal customSetMeal = extraObject
                            .getCustomSetMeal();
                    icCustom.setVisibility(View.VISIBLE);
                    workLayout.setVisibility(View.VISIBLE);
                    merchantName = customSetMeal.getMerchantName();
                    setCover(customSetMeal.getCoverPath());
                    tvTitle.setText(customSetMeal.getTitle());
                } else if (extraObject.getShop() != null) {
                    NotificationExtra.ExShop shop = extraObject.getShop();
                    workLayout.setVisibility(View.VISIBLE);
                    merchantName = shop.getMerchantName();
                    setCover(shop.getCoverPath());
                    tvTitle.setText(shop.getTitle());
                } else if (extraObject.getCar() != null) {
                    NotificationExtra.ExCar car = extraObject.getCar();
                    workLayout.setVisibility(View.VISIBLE);
                    merchantName = null;
                    setCover(car.getCoverPath());
                    tvTitle.setText(car.getTitle());
                }
                if (TextUtils.isEmpty(merchantName)) {
                    tvMerchantName.setVisibility(View.GONE);
                } else {
                    tvMerchantName.setVisibility(View.VISIBLE);
                    tvMerchantName.setText(merchantName);
                }
            }
        }

        private void setCover(String path) {
            path = JSONUtil.getImagePath2(path, coverSize);
            if (!TextUtils.isEmpty(path)) {
                Glide.with(ivCover.getContext())
                        .load(path)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(ivCover);
            } else {
                Glide.with(ivCover.getContext())
                        .clear(ivCover);
                ivCover.setImageBitmap(null);
            }

        }
    }

    private class EventItemViewHolder extends BaseViewHolder<Notification> {

        private TextView tvTime;
        private TextView tvContent;
        private View infoClickLayout;


        public EventItemViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            infoClickLayout = itemView.findViewById(R.id.info_click_layout);

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
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            tvContent.setText(item.getContent());

            DateTime currentDate = null;
            DateTime lastDate = null;

            if (item.getCreatedAt() != null) {
                currentDate = new DateTime(item.getCreatedAt());
            }
            if (position > 0) {
                Notification lastItem = NotificationRecyclerAdapter.this.getItem(position - 1);
                if (lastItem != null && lastItem.getCreatedAt() != null) {
                    lastDate = new DateTime(lastItem.getCreatedAt());
                }
            }
            if (currentDate == null || (lastDate != null && lastDate.getDayOfYear() ==
                    currentDate.getDayOfYear())) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(currentDate.toString("yyyy-MM-dd"));
            }
            if ("user_member_gift_box".equals(item.getAction())) {
                infoClickLayout.setVisibility(View.GONE);
            } else {
                infoClickLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class BaseCommunityViewHolder extends BaseViewHolder<Notification> {
        private ImageView ivAvatar;
        protected TextView tvName;
        private TextView tvTime;
        private TextView tvBody;
        private TextView tvContent;
        private LinearLayout extraLayout;
        private TextView tvExtraTitle;
        private ImageView ivExtraCover;
        private int size;

        public BaseCommunityViewHolder(View itemView) {
            super(itemView);
            size = CommonUtil.dp2px(itemView.getContext(), 40);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvBody = (TextView) itemView.findViewById(R.id.tv_body);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            extraLayout = (LinearLayout) itemView.findViewById(R.id.extra_layout);
            tvExtraTitle = (TextView) itemView.findViewById(R.id.tv_extra_title);
            ivExtraCover = (ImageView) itemView.findViewById(R.id.iv_extra_cover);
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
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            String path = ImageUtil.getAvatar(item.getParticipantAvatar(), size);
            if (!JSONUtil.isEmpty(path)) {
                Glide.with(ivAvatar.getContext())
                        .load(path)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(ivAvatar);
            } else {
                Glide.with(ivAvatar.getContext())
                        .clear(ivAvatar);
                ivAvatar.setImageResource(R.mipmap.icon_avatar_primary);
            }
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
                path = ImageUtil.getImagePath2(expand.getCoverPath(), size);
                if (!JSONUtil.isEmpty(path)) {
                    ivExtraCover.setVisibility(View.VISIBLE);
                    Glide.with(ivExtraCover.getContext())
                            .load(path)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_empty_image))
                            .into(ivExtraCover);
                } else {
                    Glide.with(ivExtraCover.getContext())
                            .clear(ivExtraCover);
                    ivExtraCover.setVisibility(View.GONE);
                }
            } else {
                extraLayout.setVisibility(View.GONE);
            }
        }
    }

    private class CommunityItemViewHolder extends BaseCommunityViewHolder {

        View line;

        public CommunityItemViewHolder(View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.view_line);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(context, item, position, viewType);
            tvName.setText(item.getParticipantName());
            line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        }
    }

    private class CommunityMergeViewHolder extends BaseCommunityViewHolder {

        private TextView tvMergeCount;
        View line;

        public CommunityMergeViewHolder(View itemView) {
            super(itemView);
            tvMergeCount = (TextView) itemView.findViewById(R.id.tv_merge_count);
            line = itemView.findViewById(R.id.view_line);
        }

        @Override
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {
            super.setViewData(context, item, position, viewType);
            line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            tvName.getLayoutParams().width = 0;
            tvName.requestLayout();
            if (item.getMergeCount() > 3) {
                tvMergeCount.setText(tvMergeCount.getContext()
                        .getString(R.string.label_merge_count, item.getMergeCount()));
            } else {
                tvMergeCount.setVisibility(View.GONE);
            }
            tvName.setText(item.getMergeParticipantName());
        }
    }


    private class FinancialItemViewHolder extends BaseViewHolder<Notification> {

        private TextView tvTime;
        private ImageView ivCover;
        private int width;

        public FinancialItemViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            width = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(itemView
                            .getContext(),
                    24);
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
        protected void setViewData(
                Context mContext, Notification item, int position, int viewType) {

            DateTime currentDate = null;
            DateTime lastDate = null;

            if (item.getCreatedAt() != null) {
                currentDate = new DateTime(item.getCreatedAt());
            }
            if (position > 0) {
                Notification lastItem = NotificationRecyclerAdapter.this.getItem(position - 1);
                if (lastItem != null && lastItem.getCreatedAt() != null) {
                    lastDate = new DateTime(lastItem.getCreatedAt());
                }
            }
            if (currentDate == null || (lastDate != null && lastDate.getDayOfYear() ==
                    currentDate.getDayOfYear())) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(currentDate.toString("yyyy-MM-dd"));
            }
            try {
                Glide.with(ivCover.getContext())
                        .load(ImageUtil.getImagePath(item.getExtraObject()
                                .getPoster()
                                .getPath(), width))
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                                .fitCenter())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(
                                    @Nullable GlideException e,
                                    Object model,
                                    Target<Drawable> target,
                                    boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    Drawable resource,
                                    Object model,
                                    Target<Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource) {
                                ivCover.getLayoutParams().height = Math.round(resource
                                        .getIntrinsicHeight() * ivCover.getWidth() / resource
                                        .getIntrinsicWidth());
                                return false;
                            }
                        })

                        .into(ivCover);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnNotificationClickListener {

        void onItemClick(Notification notification);

        void onGroupClick(Notification notification);

        //        void onItemDeleteClick(Notification notification);
        //
        //        void onGroupDeleteClick(Notification notification);

        //        void onUserClick(Notification notification);


    }


}
