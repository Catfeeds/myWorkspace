package com.hunliji.hljlivelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;
import com.hunliji.hljlivelibrary.activities.LiveMediaPagerActivity;
import com.hunliji.hljlivelibrary.models.LiveContent;
import com.hunliji.hljlivelibrary.models.LiveContentCoupon;
import com.hunliji.hljlivelibrary.models.LiveContentRedpacket;
import com.hunliji.hljlivelibrary.models.LiveMessage;
import com.hunliji.hljlivelibrary.models.LiveRxEvent;
import com.hunliji.hljlivelibrary.models.wrappers.LiveAuthor;
import com.hunliji.hljlivelibrary.utils.LiveVoicePlayUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/10/28.
 */

public class LiveMessageAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        LiveVoicePlayUtil.VoiceStatusListener {

    private long userId;
    private long channelId;
    private List<LiveMessage> messages;
    private List<LiveMessage> stickMessages;
    private CommunityThread thread;
    private View footerView;
    private Context context;

    private OnMessageClickListener onMessageClickListener;

    private static final int MESSAGE_STICK = 1;
    private static final int MESSAGE_NORMAL = 2;
    private static final int FOOTER = 3;
    private static final int LIVE_REVIEW = 4;

    private static final int INDEX = 2;

    public LiveMessageAdapter(
            Context context,
            long userId,
            long channelId,
            OnMessageClickListener onMessageClickListener) {
        this.context = context;
        this.userId = userId;
        this.channelId = channelId;
        this.onMessageClickListener = onMessageClickListener;
    }

    /**
     * 初始化历史数据，如果历史数据可以和本地数据拼接组合数据返回偏移量，否则只使用历史数据
     *
     * @param messages      历史普通消息
     * @param stickMessages 历史置顶消息
     * @return null 数据断了重置列表 Integer 数据可拼接返回增加数据的偏移量
     */
    public Integer initHistoryMessages(
            List<LiveMessage> messages, List<LiveMessage> stickMessages) {
        int offsetPosition = 0;
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        if (this.stickMessages == null) {
            this.stickMessages = new ArrayList<>();
        }
        boolean isBreak = false;
        if (!messages.isEmpty()) {
            long topId = -1;
            for (LiveMessage message : this.messages) {
                if (message.getId() > 0) {
                    //获取第一条消息id
                    topId = message.getId();
                    break;
                }
            }
            //判断消息是否可拼接
            if (topId - messages.get(messages.size() - 1)
                    .getId() < -1) {
                this.messages = messages;
                isBreak = true;
            } else {
                //可拼接去重插入
                for (int i = messages.size(); i > 0; i--) {
                    LiveMessage message = messages.get(i - 1);
                    if (topId < message.getId()) {
                        this.messages.add(0, message);
                        offsetPosition++;
                    }
                }
            }
        } else if (!this.messages.isEmpty() || this.stickMessages.isEmpty()) {
            isBreak = true;
        }

        offsetPosition += stickMessages.size() - this.stickMessages.size();
        this.stickMessages = stickMessages;
        notifyDataSetChanged();
        return isBreak ? null : offsetPosition;
    }

    /**
     * 更新服务器返回消息 包括插入和删除
     *
     * @param messages      普通消息
     * @param stickMessages 置顶消息
     * @return 消息变动偏移量
     */
    public int updateMessages(List<LiveMessage> messages, List<LiveMessage> stickMessages) {
        int offsetPosition = 0;
        if (this.stickMessages == null) {
            this.stickMessages = new ArrayList<>();
        }
        ArrayList<LiveMessage> addMessages = new ArrayList<>();
        for (LiveMessage stickMessage : stickMessages) {
            if (stickMessage.isDeleted()) {
                //删除消息
                for (LiveMessage stickMessage2 : this.stickMessages) {
                    if (stickMessage2.getId() == stickMessage.getId()) {
                        this.stickMessages.remove(stickMessage2);
                        offsetPosition--;
                        break;
                    }
                }
            } else {
                addMessages.add(stickMessage);
            }
        }
        //插入置顶消息
        this.stickMessages.addAll(0, addMessages);
        offsetPosition += addMessages.size();

        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        addMessages = new ArrayList<>();
        for (LiveMessage message : messages) {
            if (message.isDeleted()) {
                //删除消息
                for (LiveMessage message2 : this.messages) {
                    if (message.getId() == message2.getId()) {
                        this.messages.remove(message2);
                        offsetPosition--;
                        break;
                    }
                }
            } else {
                addMessages.add(message);
            }
        }
        //插入普通消息
        this.messages.addAll(0, addMessages);
        offsetPosition += addMessages.size();
        notifyDataSetChanged();
        return offsetPosition;
    }

    /**
     * 设置回顾
     *
     * @param thread 回顾话题
     */
    public void setThread(CommunityThread thread) {
        if (this.thread == null) {
            this.thread = thread;
            notifyItemInserted(0);
        } else {
            this.thread = thread;
            notifyItemChanged(0);
        }
    }

    /**
     * 置顶消息数
     *
     * @return
     */
    public int getStickMessagesCount() {
        return stickMessages == null ? 0 : stickMessages.size();
    }

    /**
     * 加载下一页
     *
     * @param messages
     */
    public void loadMessages(List<LiveMessage> messages) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        int size = getItemCount() - 1;
        this.messages.addAll(messages);
        notifyItemRangeInserted(size, messages.size());
    }

    /**
     * 更新或插入发送的消息
     *
     * @param message 用户发送的消息
     * @return 首次插入消息列表滚动到普通消息顶部 topIndex 普通消息顶部位置
     */
    public int updateSendMessage(LiveMessage message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        int index = messages.indexOf(message);
        int topIndex = 0;
        if (thread != null) {
            topIndex++;
        }
        topIndex += getStickMessagesCount();
        if (index >= 0) {
            //更新消息不改变列表位置
            notifyItemChanged(index + topIndex);
            return -1;
        } else {
            messages.add(0, message);
            notifyItemInserted(topIndex);
            return topIndex;
        }
    }

    /**
     * 获取当前position的数据
     */
    public Object getItem(int position) {
        if (thread != null) {
            if (position == 0) {
                return thread;
            }
            position--;
        }
        if (stickMessages != null) {
            if (stickMessages.size() > position) {
                return stickMessages.get(position);
            }
            position -= stickMessages.size();
        }
        if (messages != null) {
            if (messages.size() > position) {
                return messages.get(position);
            }
        }
        return null;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    /**
     * 消息发送方判断
     *
     * @param type type%INDEX 用于区分左右消息
     */
    private boolean isMine(int type) {
        return type % INDEX > 0;
    }

    /**
     * @param viewType type/INDEX 用于区分消息类型
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType / INDEX) {
            case MESSAGE_STICK:
                return new StickLiveMessageViewHolder(getView(parent, viewType));
            case MESSAGE_NORMAL:
                if (isMine(viewType)) {
                    return new MineLiveMessageViewHolder(getView(parent, viewType));
                }
                return new LiveMessageViewHolder(getView(parent, viewType));
            case FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case LIVE_REVIEW:
                return new ReviewThreadViewHolder(parent.getContext());
        }
        return null;
    }

    private View getView(ViewGroup parent, int viewType) {
        switch (viewType / INDEX) {
            case FOOTER:
                return footerView;
            default:
                return LayoutInflater.from(parent.getContext())
                        .inflate(isMine(viewType) ? R.layout.message_right___live : R.layout
                                        .message_left___live,
                                parent,
                                false);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (thread != null) {
            count++;
        }
        count += stickMessages == null ? 0 : stickMessages.size();
        count += messages == null ? 0 : messages.size();
        if (footerView != null) {
            count++;
        }
        return count;
    }


    /**
     * 获取最后一条消息id 用于翻页
     *
     * @return
     */
    public long getLastId() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1)
                    .getId();
        }
        return 0;
    }

    /**
     * item 类别 type/INDEX 区分类别 type%INDEX 用于区分左右消息
     *
     * @param position
     * @return type%INDEX>0 为本人发送的消息
     */
    @Override
    public int getItemViewType(int position) {
        if (thread != null) {
            //顶部回顾
            if (position == 0) {
                return LIVE_REVIEW * INDEX;
            }
            position--;
        }
        if (stickMessages != null) {
            //置顶消息
            if (stickMessages.size() > position) {
                LiveMessage message = stickMessages.get(position);
                int type = MESSAGE_STICK * INDEX;
                if (message.getUser() != null && message.getUser()
                        .getId() == userId) {
                    type += 1;
                }
                return type;
            }
            position -= stickMessages.size();
        }
        if (messages != null) {
            //普通消息
            if (messages.size() > position) {
                LiveMessage message = messages.get(position);
                int type = MESSAGE_NORMAL * INDEX;
                if (message.getUser() != null && message.getUser()
                        .getId() == userId) {
                    type += 1;
                }
                return type;
            }
        }
        //底部视图
        return FOOTER * INDEX;
    }

    /**
     * 语音播放状态变化
     *
     * @param message
     * @param status
     */
    @Override
    public void onStatusChange(LiveMessage message, int status) {
        //index 消息位置
        int position = getPosition(message);
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * 消息位置
     *
     * @param message
     * @return
     */
    private int getPosition(LiveMessage message) {
        int position = -1;
        if (stickMessages != null && stickMessages.contains(message)) {
            position = stickMessages.indexOf(message);
        } else if (messages != null && messages.contains(message)) {
            position = messages.indexOf(message);
            if (stickMessages != null) {
                position += stickMessages.size();
            }
        }
        if (position >= 0) {
            if (thread != null) {
                position++;
            }
        }
        return position;
    }

    public class LiveMessageViewHolder extends BaseViewHolder<LiveMessage> {

        @BindView(R2.id.content_layout)
        LinearLayout contentLayout;
        @BindView(R2.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R2.id.message_layout)
        View messageLayout;
        @BindView(R2.id.iv_tag)
        ImageView ivTag;
        @BindView(R2.id.tv_role)
        TextView tvRole;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.tv_time)
        TextView tvTime;
        @BindView(R2.id.tv_stick)
        TextView tvStick;
        @BindView(R2.id.name_layout)
        LinearLayout nameLayout;
        @BindView(R2.id.tv_content)
        TextView tvContent;
        @BindView(R2.id.iv_single_image)
        ImageView ivSingleImage;
        @BindView(R2.id.four_images_layout)
        RelativeLayout fourImagesLayout;
        @BindView(R2.id.six_images_layout)
        RelativeLayout sixImagesLayout;
        @BindView(R2.id.tv_duration_right)
        TextView tvDurationRight;
        @BindView(R2.id.tv_duration_left)
        TextView tvDurationLeft;
        @BindView(R2.id.wave_layout)
        RelativeLayout waveLayout;
        @BindView(R2.id.iv_wave_left)
        ImageView ivWaveLeft;
        @BindView(R2.id.iv_wave_right)
        ImageView ivWaveRight;
        @BindView(R2.id.voice_layout)
        LinearLayout voiceLayout;
        @BindView(R2.id.tv_replay_name)
        TextView tvReplayName;
        @BindView(R2.id.tv_replay_time)
        TextView tvReplayTime;
        @BindView(R2.id.replay_name_layout)
        LinearLayout replayNameLayout;
        @BindView(R2.id.tv_reply_content)
        TextView tvReplyContent;
        @BindView(R2.id.iv_reply_image)
        ImageView ivReplyImage;
        @BindView(R2.id.tv_reply_image_count)
        TextView tvReplyImageCount;
        @BindView(R2.id.reply_image_layout)
        RelativeLayout replyImageLayout;
        @BindView(R2.id.tv_reply_duration)
        TextView tvReplyDuration;
        @BindView(R2.id.iv_reply_wave)
        ImageView ivReplyWave;
        @BindView(R2.id.reply_voice_layout)
        LinearLayout replyVoiceLayout;
        @BindView(R2.id.reply_wave_layout)
        RelativeLayout replyWaveLayout;
        @BindView(R2.id.reply_layout)
        LinearLayout replyLayout;
        @BindView(R2.id.tv_reply_delete)
        TextView tvReplyDelete;
        @BindView(R2.id.images_layout)
        RelativeLayout imagesLayout;
        @BindView(R2.id.video_layout)
        RelativeLayout videoLayout;
        @BindView(R2.id.iv_video)
        ImageView ivVideo;
        @BindView(R2.id.reply_video_layout)
        RelativeLayout replyVideoLayout;
        @BindView(R2.id.iv_reply_video)
        ImageView ivReplyVideo;

        private int avatarSize;
        private int singleHeight;
        private int singleWidth;
        private int sixImageSize;
        private int replyImageWidth;
        private int fourImageSize;
        private int waveDurationWidth;
        private int waveMinWidth;
        private int replyVideoWidth;
        private int replyVideoHeight;
        private int videoWidth;
        private int videoHeight;

        private LiveMessageViewHolder(View itemView) {
            super(itemView);
            Point point = CommonUtil.getDeviceSize(itemView.getContext());
            DisplayMetrics dm = itemView.getResources()
                    .getDisplayMetrics();
            avatarSize = Math.round(dm.density * 36);
            int maxWidth = videoWidth = singleWidth = Math.round(point.x - 140 * dm.density);
            singleHeight = Math.round(dm.density * 180);
            sixImageSize = Math.round((maxWidth - 8 * dm.density) / 3);
            fourImageSize = Math.round((maxWidth - 4 * dm.density) / 2);
            replyVideoWidth = replyImageWidth = Math.round(maxWidth - 20 * dm.density);
            waveMinWidth = Math.round(dm.density * 80);
            waveDurationWidth = Math.round(dm.density * 60);
            videoHeight = Math.round(videoWidth * 9 / 16);
            replyVideoHeight = Math.round(replyVideoWidth * 9 / 16);

            ButterKnife.bind(this, itemView);

            ivSingleImage.getLayoutParams().height = singleHeight;
            ivSingleImage.setOnClickListener(new OnImagesClickListener(0, false));
            for (int i = 0; i < sixImagesLayout.getChildCount(); i++) {
                View view = sixImagesLayout.getChildAt(i);
                view.getLayoutParams().width = sixImageSize;
                view.getLayoutParams().height = sixImageSize;
                view.setOnClickListener(new OnImagesClickListener(i, false));
            }
            for (int i = 0; i < fourImagesLayout.getChildCount(); i++) {
                View view = fourImagesLayout.getChildAt(i);
                view.getLayoutParams().width = fourImageSize;
                view.getLayoutParams().height = fourImageSize;
                view.setOnClickListener(new OnImagesClickListener(i, false));
            }
            replyImageLayout.getLayoutParams().width = replyImageWidth;
            replyImageLayout.getLayoutParams().height = replyImageWidth * 3 / 4;
            replyLayout.getLayoutParams().width = maxWidth;
            tvReplyDelete.getLayoutParams().width = maxWidth;

            videoLayout.getLayoutParams().height = videoHeight;
            videoLayout.getLayoutParams().width = videoWidth;
            replyVideoLayout.getLayoutParams().width = replyVideoWidth;
            replyVideoLayout.getLayoutParams().height = replyVideoHeight;

            replyImageLayout.setOnClickListener(new OnImagesClickListener(0, true));
            waveLayout.setOnClickListener(new OnVoiceClickListener(false));
            replyWaveLayout.setOnClickListener(new OnVoiceClickListener(true));
            videoLayout.setOnClickListener(new OnImagesClickListener(0, false));
            replyVideoLayout.setOnClickListener(new OnImagesClickListener(0, true));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LiveMessage message = getItem();
                    if (message.isError()) {
                        int index = -1;
                        if (stickMessages != null && stickMessages.contains(message)) {
                            index = stickMessages.indexOf(message);
                            stickMessages.remove(index);
                        } else if (messages != null && messages.contains(message)) {
                            index = messages.indexOf(message);
                            messages.remove(index);
                            if (stickMessages != null) {
                                index += stickMessages.size();
                            }
                        }
                        if (index >= 0) {
                            notifyItemRemoved(index);
                        }
                        RxBus.getDefault()
                                .post(new LiveRxEvent(LiveRxEvent.RxEventType.RESEND_MESSAGE,
                                        channelId,
                                        message));
                    } else if (onMessageClickListener != null) {
                        if (message.getMsgKind() == LiveMessage.MSG_KIND_RED_PACKET) {
                            onMessageClickListener.onRedpacket((LiveContentRedpacket) message
                                    .getLiveContent());
                        } else if (message.getMsgKind() == LiveMessage.MSG_KIND_COUPON) {
                            onMessageClickListener.onCoupon((LiveContentCoupon) message
                                    .getLiveContent());
                        } else {
                            onMessageClickListener.showMenu(message);
                        }
                    }
                }
            });
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMessageClickListener != null) {
                        onMessageClickListener.onUserClick(getItem().getUser());
                    }
                }
            });
        }

        private class OnVoiceClickListener implements View.OnClickListener {

            private boolean isReply;

            private OnVoiceClickListener(boolean isReply) {
                this.isReply = isReply;
            }

            @Override
            public void onClick(View v) {
                LiveMessage message = getItem();
                LiveContent content = null;
                if (isReply) {
                    if (message.getReply() != null) {
                        content = message.getReply()
                                .getLiveContent();
                    }
                } else {
                    content = message.getLiveContent();
                }
                LiveVoicePlayUtil.getInstance()
                        .playVoice(v.getContext(), message, content, LiveMessageAdapter.this);
            }
        }

        /**
         * 图片点击整合所有消息图片，然后跳转多图详情
         */
        private class OnImagesClickListener implements View.OnClickListener {

            private int position;
            private boolean isReply;

            private OnImagesClickListener(int position, boolean isReply) {
                this.position = position;
                this.isReply = isReply;
            }

            @Override
            public void onClick(View v) {
                LiveMessage clickMessage = getItem();

                ArrayList<LiveMessage> list = new ArrayList<>(messages);
                Intent intent = new Intent(v.getContext(), LiveMediaPagerActivity.class);
                intent.putParcelableArrayListExtra(LiveMediaPagerActivity.ARG_MESSAGES, list);
                intent.putExtra(LiveMediaPagerActivity.ARG_IS_REPLY, isReply);
                intent.putExtra(LiveMediaPagerActivity.ARG_CLICK_MESSAGE, clickMessage);
                intent.putExtra(LiveMediaPagerActivity.ARG_SUB_POSITION, position);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(com.hunliji.hljchatlibrary.R
                                    .anim.slide_in_right,
                            com.hunliji.hljchatlibrary.R.anim.activity_anim_default);
                }
            }
        }

        @Override
        protected void setViewData(
                Context mContext, final LiveMessage item, int position, int viewType) {
            LiveAuthor user = item.getUser();
            final boolean right = isMine(viewType);

            View redCouponView = contentLayout.findViewById(R.id.red_coupon_layout);
            if (redCouponView != null) {
                contentLayout.removeView(redCouponView);
            }
            if (item.getMsgKind() == LiveMessage.MSG_KIND_RED_PACKET || item.getMsgKind() ==
                    LiveMessage.MSG_KIND_COUPON) {
                nameLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                imagesLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                voiceLayout.setVisibility(View.GONE);
                replyLayout.setVisibility(View.GONE);
                tvReplyDelete.setVisibility(View.GONE);
                int singleWidth2 = singleWidth - CommonUtil.dp2px(mContext, 16);
                int singleHeight2 = singleWidth2 * 238 / 696; // 固定比例
                if (item.getMsgKind() == LiveMessage.MSG_KIND_RED_PACKET) {
                    LiveContentRedpacket redpacket = (LiveContentRedpacket) item.getLiveContent();
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.live_content_extra_layout, null, false);
                    int contentMarginLeft = singleWidth2 * 148 / 484;
                    LinearLayout redLayout = view.findViewById(R.id.red_layout);
                    redLayout.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout
                            .LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(contentMarginLeft, 0, CommonUtil.dp2px(mContext, 12), 0);
                    redLayout.setLayoutParams(params);
                    ImageView imageView = view.findViewById(R.id.img_bg);
                    imageView.setImageResource(R.mipmap.image_msg_redpacket___live);
                    view.findViewById(R.id.coupon_layout)
                            .setVisibility(View.GONE);
                    TextView tvTitle = view.findViewById(R.id.tv_title);
                    tvTitle.setText(redpacket.getGroupName());
                    contentLayout.addView(view);
                    view.getLayoutParams().width = singleWidth2;
                    view.getLayoutParams().height = singleHeight2;
                } else {
                    final LiveContentCoupon coupon = (LiveContentCoupon) item.getLiveContent();
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.live_content_extra_layout, null, false);
                    ImageView imageView = view.findViewById(R.id.img_bg);
                    imageView.setImageResource(R.mipmap.image_msg_coupon___live);
                    view.findViewById(R.id.coupon_layout)
                            .setVisibility(View.VISIBLE);
                    view.findViewById(R.id.red_layout)
                            .setVisibility(View.GONE);
                    TextView tvMoney = view.findViewById(R.id.tv_money);
                    TextView tvMoneySill = view.findViewById(R.id.tv_money_sill);
                    if (coupon != null) {
                        tvMoney.setText(mContext.getString(R.string.label_money,
                                CommonUtil.formatDouble2String(coupon.getValue())));
                        tvMoneySill.setText(coupon.isCashCoupon() ? "现金券" : mContext.getString(R
                                        .string.label_money_sill,
                                CommonUtil.formatDouble2String(coupon.getMoneySill())));
                    }
                    contentLayout.addView(view);
                    view.getLayoutParams().width = singleWidth2;
                    view.getLayoutParams().height = singleHeight2;
                }
            } else {
                nameLayout.setVisibility(View.VISIBLE);
            }

            if (user != null) {
                String avatarPath = ImageUtil.getAvatar(user.getAvatar(), avatarSize);
                Glide.with(ivAvatar.getContext())
                        .load(avatarPath)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(ivAvatar);
                tvName.getLayoutParams().width = 0;
                tvName.requestLayout();
                tvName.setText(user.getName());
                if (user.getKind() == 1) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_vip_blue_28_28);
                } else if (!TextUtils.isEmpty(user.getSpecialty()) && !"普通用户".equals(user
                        .getSpecialty())) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_vip_yellow_28_28);
                } else if (user.getMember()
                        .getId() > 0) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    ivTag.setVisibility(View.GONE);
                }
                if (user.getLiveRole() == 1) {
                    tvRole.setVisibility(View.VISIBLE);
                    tvRole.setText(R.string.label_role1___live);
                } else if (user.getLiveRole() == 2) {
                    tvRole.setVisibility(View.VISIBLE);
                    tvRole.setText(R.string.label_role2___live);
                } else {
                    tvRole.setVisibility(View.GONE);
                }
            }
            if (item.getTime() != null) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(item.getTime()
                        .toString("HH:mm:ss"));
            } else {
                tvTime.setVisibility(View.GONE);
            }
            tvContent.setVisibility(View.GONE);
            imagesLayout.setVisibility(View.GONE);
            videoLayout.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.GONE);
            replyLayout.setVisibility(View.GONE);
            ImageView ivWave;
            TextView tvDuration;
            if (right) {
                ivWave = ivWaveRight;
                tvDuration = tvDurationRight;
                ivWaveLeft.setVisibility(View.GONE);
                tvDurationLeft.setVisibility(View.GONE);
            } else {
                ivWave = ivWaveLeft;
                tvDuration = tvDurationLeft;
                ivWaveRight.setVisibility(View.GONE);
                tvDurationRight.setVisibility(View.GONE);
            }
            ivWave.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.VISIBLE);
            onPlayView(ivWave, item.getLiveContent(), right);
            if (item.getLiveContent() != null) {
                LiveContent content = item.getLiveContent();
                if (!TextUtils.isEmpty(content.getVoicePath())) {
                    voiceLayout.setVisibility(View.VISIBLE);
                    tvDuration.setText(tvDuration.getContext()
                            .getString(R.string.label_voice_length___cm,
                                    Math.round(content.getVoiceDuration())));
                    waveLayout.getLayoutParams().width = (int) Math.round(waveMinWidth +
                            waveDurationWidth * Math.min(
                            1,
                            content.getVoiceDuration() / 60));
                } else {
                    if (!TextUtils.isEmpty(content.getText())) {
                        tvContent.setVisibility(View.VISIBLE);
                        tvContent.setText(content.getText());
                    }
                    if (content.getVideoMedia() != null) {
                        videoLayout.setVisibility(View.VISIBLE);
                        int height = content.getVideoMedia()
                                .getMedia()
                                .getVideo()
                                .getHeight();
                        int width = content.getVideoMedia()
                                .getMedia()
                                .getVideo()
                                .getWidth();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                                videoLayout.getLayoutParams();
                        if (height * singleWidth > width * singleHeight) {
                            params.width = width * singleHeight / height;
                            params.height = singleHeight;
                        } else {
                            params.width = singleWidth;
                            if (width > 0) {
                                params.height = height * singleWidth / width;
                            }
                        }
                        Glide.with(ivVideo.getContext())
                                .load(ImagePath.buildPath(content.getVideoMedia()
                                        .getMedia()
                                        .getVideo()
                                        .getScreenShot())
                                        .width(singleWidth)
                                        .height(singleHeight)
                                        .path())
                                .apply(new RequestOptions().override(singleWidth, singleHeight)
                                        .fitCenter()
                                        .placeholder(R.mipmap.icon_empty_image))
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
                                        int height = resource.getIntrinsicHeight();
                                        int width = resource.getIntrinsicWidth();
                                        ViewGroup.MarginLayoutParams params = (ViewGroup
                                                .MarginLayoutParams) videoLayout.getLayoutParams();
                                        if (height * singleWidth > width * singleHeight) {
                                            params.width = width * singleHeight / height;
                                            params.height = singleHeight;

                                        } else {
                                            params.width = singleWidth;
                                            params.height = height * singleWidth / width;
                                        }
                                        return false;
                                    }
                                })
                                .into(ivVideo);
                    } else if (!CommonUtil.isCollectionEmpty(content.getImageMedias())) {
                        imagesLayout.setVisibility(View.VISIBLE);
                        ivSingleImage.setVisibility(View.GONE);
                        sixImagesLayout.setVisibility(View.GONE);
                        fourImagesLayout.setVisibility(View.GONE);
                        if (content.getImageMedias()
                                .size() == 1) {
                            ivSingleImage.setVisibility(View.VISIBLE);
                            String imagePath = ImagePath.buildPath(content.getImageMedias()
                                    .get(0)
                                    .getMedia()
                                    .getPhoto()
                                    .getImagePath())
                                    .height(singleHeight)
                                    .width(singleWidth)
                                    .path();
                            Glide.with(ivSingleImage.getContext())
                                    .load(imagePath)
                                    .apply(new RequestOptions().override(singleWidth, singleHeight)
                                            .fitCenter()
                                            .placeholder(R.mipmap.icon_empty_image))
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
                                            int height = resource.getIntrinsicHeight();
                                            int width = resource.getIntrinsicWidth();
                                            ViewGroup.MarginLayoutParams params = (ViewGroup
                                                    .MarginLayoutParams) ivSingleImage
                                                    .getLayoutParams();
                                            if (height * singleWidth > width * singleHeight) {
                                                params.width = width * singleHeight / height;
                                                params.height = singleHeight;

                                            } else {
                                                params.width = singleWidth;
                                                params.height = height * singleWidth / width;
                                            }
                                            return false;
                                        }
                                    })
                                    .into(ivSingleImage);
                        } else if (content.getImageMedias()
                                .size() == 2 || content.getImageMedias()
                                .size() == 4) {
                            fourImagesLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < fourImagesLayout.getChildCount(); i++) {
                                ImageView imageView = (ImageView) fourImagesLayout.getChildAt(i);
                                String imagePath = null;
                                if (content.getImageMedias()
                                        .size() > i) {
                                    imagePath = ImagePath.buildPath(content.getImageMedias()
                                            .get(i)
                                            .getMedia()
                                            .getPhoto()
                                            .getImagePath())
                                            .width(fourImageSize)
                                            .cropPath();
                                }
                                if (TextUtils.isEmpty(imagePath)) {
                                    Glide.with(imageView.getContext())
                                            .clear(imageView);
                                    imageView.setVisibility(View.GONE);
                                } else {
                                    imageView.setVisibility(View.VISIBLE);
                                    Glide.with(imageView.getContext())
                                            .load(imagePath)
                                            .apply(new RequestOptions().centerCrop()
                                                    .placeholder(R.mipmap.icon_empty_image))
                                            .into(imageView);
                                }
                            }
                        } else {
                            sixImagesLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < sixImagesLayout.getChildCount(); i++) {
                                ImageView imageView = (ImageView) sixImagesLayout.getChildAt(i);
                                String imagePath = null;
                                if (content.getImageMedias()
                                        .size() > i) {
                                    imagePath = ImagePath.buildPath(content.getImageMedias()
                                            .get(i)
                                            .getMedia()
                                            .getPhoto()
                                            .getImagePath())
                                            .width(sixImageSize)
                                            .cropPath();
                                }
                                if (TextUtils.isEmpty(imagePath)) {
                                    Glide.with(imageView.getContext())
                                            .clear(imageView);
                                    imageView.setVisibility(View.GONE);
                                } else {
                                    imageView.setVisibility(View.VISIBLE);
                                    Glide.with(imageView.getContext())
                                            .load(imagePath)
                                            .apply(new RequestOptions()

                                                    .centerCrop()
                                                    .placeholder(R.mipmap.icon_empty_image))
                                            .into(imageView);
                                }
                            }
                        }

                    }
                }
            }

            LiveMessage reply = item.getReply();
            if (reply == null) {
                replyLayout.setVisibility(View.GONE);
                tvReplyDelete.setVisibility(View.GONE);
            } else if (reply.isDeleted()) {
                tvReplyDelete.setVisibility(View.VISIBLE);
                replyLayout.setVisibility(View.GONE);
            } else {
                replyLayout.setVisibility(View.VISIBLE);
                if (reply.getUser() != null) {
                    tvReplayName.getLayoutParams().width = 0;
                    tvReplayName.requestLayout();
                    tvReplayName.setText(reply.getUser()
                            .getName());
                }
                if (reply.getTime() != null) {
                    tvReplayTime.setText(reply.getTime()
                            .toString("HH:mm:ss"));
                }
                LiveContent replyContent = reply.getLiveContent();
                tvReplyContent.setVisibility(View.GONE);
                replyImageLayout.setVisibility(View.GONE);
                replyVoiceLayout.setVisibility(View.GONE);
                replyVideoLayout.setVisibility(View.GONE);
                onPlayView(ivReplyWave, replyContent, false);
                if (replyContent != null) {
                    if (!TextUtils.isEmpty(replyContent.getVoicePath())) {
                        replyVoiceLayout.setVisibility(View.VISIBLE);
                        tvReplyDuration.setText(tvReplyDuration.getContext()
                                .getString(R.string.label_voice_length___cm,
                                        Math.round(replyContent.getVoiceDuration())));
                        replyWaveLayout.getLayoutParams().width = (int) Math.round(waveMinWidth +
                                waveDurationWidth * Math.min(
                                1,
                                replyContent.getVoiceDuration() / 60));
                    } else {
                        if (!TextUtils.isEmpty(replyContent.getText())) {
                            tvReplyContent.setVisibility(View.VISIBLE);
                            tvReplyContent.setText(replyContent.getText());
                        }
                        if (replyContent.getVideoMedia() != null) {
                            replyVideoLayout.setVisibility(View.VISIBLE);
                            Glide.with(ivReplyVideo.getContext())
                                    .load(ImagePath.buildPath(replyContent.getVideoMedia()
                                            .getMedia()
                                            .getVideo()
                                            .getScreenShot())
                                            .width(replyVideoWidth)
                                            .height(replyVideoHeight)
                                            .cropPath())
                                    .into(ivReplyVideo);
                        } else if (replyContent.getImageMedias() != null && !replyContent
                                .getImageMedias()
                                .isEmpty()) {
                            replyImageLayout.setVisibility(View.VISIBLE);
                            String imagePath = ImagePath.buildPath(replyContent.getImageMedias()
                                    .get(0)
                                    .getMedia()
                                    .getPhoto()
                                    .getImagePath())
                                    .width(replyImageWidth)
                                    .height(replyImageWidth * 3 / 4)
                                    .cropPath();
                            Glide.with(ivReplyImage.getContext())
                                    .load(imagePath)
                                    .apply(new RequestOptions().placeholder(R.mipmap
                                            .icon_empty_image)
                                            .centerCrop())
                                    .into(ivReplyImage);
                            if (replyContent.getImageMedias()
                                    .size() > 1) {
                                tvReplyImageCount.setVisibility(View.VISIBLE);
                                tvReplyImageCount.setText(String.valueOf(replyContent
                                        .getImageMedias()

                                        .size()));
                            } else {
                                tvReplyImageCount.setVisibility(View.GONE);
                            }

                        }
                    }
                }
            }
        }

        private void onPlayView(final ImageView ivWave, LiveContent content, boolean right) {
            if (content == null || TextUtils.isEmpty(content.getVoicePath())) {
                if (ivWave.getDrawable() != null && ivWave.getDrawable() instanceof
                        AnimationDrawable) {
                    ((AnimationDrawable) ivWave.getDrawable()).stop();
                }
                return;
            }
            boolean isAnimation = ivWave.getDrawable() != null && ivWave.getDrawable() instanceof
                    AnimationDrawable;
            if (LiveVoicePlayUtil.getInstance()
                    .isPlay(content)) {
                if (!isAnimation) {
                    ivWave.setImageResource(right ? R.drawable.sl_ic_live_voice_right : R
                            .drawable.sl_ic_live_voice_left);
                }
                AnimationDrawable drawable = null;
                if (ivWave.getDrawable() != null && ivWave.getDrawable() instanceof
                        AnimationDrawable) {
                    drawable = (AnimationDrawable) ivWave.getDrawable();
                }
                if (drawable != null && !drawable.isRunning()) {
                    drawable.start();
                }
            } else if (isAnimation) {
                ivWave.setImageResource(right ? R.mipmap.ic_voice_right_03___live : R.mipmap
                        .ic_voice_left_03___live);
            }
        }
    }

    private class StickLiveMessageViewHolder extends LiveMessageViewHolder {

        private StickLiveMessageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(Context mContext, LiveMessage item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            tvTime.setVisibility(View.GONE);
            tvStick.setVisibility(View.VISIBLE);
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    private class MineLiveMessageViewHolder extends LiveMessageViewHolder {

        private View errView;
        private View loadView;

        private MineLiveMessageViewHolder(View itemView) {
            super(itemView);
            loadView = itemView.findViewById(R.id.progressBar);
            errView = itemView.findViewById(R.id.err);
        }

        @Override
        protected void setViewData(Context mContext, LiveMessage item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            loadView.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
            errView.setVisibility(!item.isSending() && item.isError() ? View.VISIBLE : View.GONE);
        }
    }

    public interface OnMessageClickListener {

        void showMenu(LiveMessage item);

        void onUserClick(Author author);

        void onRedpacket(LiveContentRedpacket liveContentRedpacket);

        void onCoupon(LiveContentCoupon coupon);
    }
}
