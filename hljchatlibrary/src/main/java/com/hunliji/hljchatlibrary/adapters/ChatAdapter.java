package com.hunliji.hljchatlibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljchatlibrary.R;
import com.hunliji.hljchatlibrary.R2;
import com.hunliji.hljchatlibrary.utils.WSVoicePlayUtil;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackCase;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.TrackWork;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSHints;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSLocation;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSMerchantStats;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTips;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSTrack;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSMedia;
import com.hunliji.hljcommonlibrary.models.realm.WSProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.MapLibraryService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljimagelibrary.models.Size;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljimagelibrary.views.activities.SingleImageActivity;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/10/14.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        WSVoicePlayUtil.VoiceStatusListener {

    public static final String LINK_OF_APPOINTMENT_STR = "预约回电";

    private User sessionUser;
    private User user;

    private OnChatClickListener onChatClickListener;

    private List<WSChat> chats;
    private View headerView;

    public static class CHAT_TYPE {
        public static final int HEADER = -1;
        public static final int TEXT = 1;
        public static final int IMAGE = 2;
        public static final int WORK = 3;
        public static final int PRODUCT = 4;
        public static final int CUSTOM_MEAL = 5;
        public static final int VOICE = 6;
        public static final int TRACK = 7;
        public static final int TRACK_WORK = 8;
        public static final int TRACK_CASE = 9;
        public static final int TRACK_MERCHANT = 10;
        public static final int TRACK_SHOW_WINDOW = 11;
        public static final int TRACK_PRODUCT = 12;
        public static final int TIPS_DEFAULT_VIEW = 13;
        public static final int TIPS_MERCHANT_APPOINTMENT = 14; // 商家很忙，请预约
        public static final int TIPS_MERCHANT_SUCCESS_TIP = 15; // 商家很忙，请预约
        public static final int LOCATION = 16;
        public static final int TRACK_WEDDING_CAR = 17;
        public static final int HINTS_SMART_REPLY = 18; // 商家智能回复
        public static final int MERCHANT_STATS = 19; // 商家私信统计信息
    }

    private static final int INDEX = 2;
    private static final String TIME_FORMAT = "MM-dd HH:mm";

    public ChatAdapter(User sessionUser, User user, OnChatClickListener onChatClickListener) {
        this.sessionUser = sessionUser;
        this.user = user;
        this.onChatClickListener = onChatClickListener;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    public void setChats(List<WSChat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    private int getAdapterPosition(int dataIndex) {
        if (headerView == null) {
            return dataIndex;
        } else {
            return dataIndex + 1;
        }
    }


    public int addChat(WSChat chat) {
        if (chats == null) {
            chats = new ArrayList<>();
        }
        if (chats.contains(chat)) {
            int index = chats.indexOf(chat);
            notifyItemChanged(getAdapterPosition(index));
            return index;
        } else {
            for (WSChat wsChat : chats) {
                if (wsChat.getIdStr()
                        .equals(chat.getIdStr())) {
                    int index = chats.indexOf(chat);
                    notifyItemChanged(getAdapterPosition(index));
                    return index;
                }
            }
            this.chats.add(chat);
            notifyItemInserted(getItemCount() - 1);
            return getItemCount() - 1;
        }
    }

    public void removeChat(WSChat chat) {
        if (chats.contains(chat)) {
            int index = chats.indexOf(chat);
            chats.remove(index);
            notifyItemRemoved(getAdapterPosition(index));
        }
    }

    public void notifyChatChange(WSChat chat) {
        if (chats == null || chats.isEmpty()) {
            return;
        }
        int index = chats.indexOf(chat);
        if (index >= 0) {
            notifyItemChanged(getAdapterPosition(index));
        } else {
            notifyDataSetChanged();
        }
    }

    public void onMessageReadChanged() {
        if (chats == null || chats.isEmpty()) {
            return;
        }
        for (WSChat chat : chats) {
            if (chat.isCurrentUser() && chat.isUnRead()) {
                chat.setUnRead(false);
                notifyItemChanged(getAdapterPosition(chats.indexOf(chat)));
            }
        }
    }

    public WSChat getItem(int position) {
        if (headerView != null) {
            if (position == 0) {
                return null;
            }
            position--;
        }
        return chats.get(position);
    }


    public static boolean isSession(int type) {
        return type % INDEX > 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CHAT_TYPE.HEADER) {
            return new ExtraBaseViewHolder(headerView);
        }
        switch (viewType / INDEX) {
            case CHAT_TYPE.IMAGE:
                return new ImageViewHolder(getView(parent, viewType));
            case CHAT_TYPE.PRODUCT:
            case CHAT_TYPE.CUSTOM_MEAL:
            case CHAT_TYPE.WORK:
                return new ProductViewHolder(getView(parent, viewType));
            case CHAT_TYPE.VOICE:
                return new VoiceViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK:
                return new TrackViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_WORK:
                return new TrackWorkViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_CASE:
                return new TrackCaseViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_PRODUCT:
                return new TrackProductViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_MERCHANT:
                return new TrackMerchantViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_SHOW_WINDOW:
                return new TrackShowWindowViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TIPS_MERCHANT_APPOINTMENT:
                return new MerchantTipsViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TIPS_MERCHANT_SUCCESS_TIP:
                return new SuccessTipsViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TIPS_DEFAULT_VIEW:
                return new TipsDefaultViewHolder(getView(parent, viewType));
            case CHAT_TYPE.LOCATION:
                return new LocationViewHolder(getView(parent, viewType));
            case CHAT_TYPE.MERCHANT_STATS:
                return new MerchantStatsViewHolder(getView(parent, viewType));
            case CHAT_TYPE.HINTS_SMART_REPLY:
                return new HintSmartReplyViewHolder(getView(parent, viewType));
            case CHAT_TYPE.TRACK_WEDDING_CAR:
                return new TrackWeddingCarViewHolder(getView(parent, viewType));
        }
        return new TextViewHolder(getView(parent, viewType));
    }

    @Override
    public int getItemCount() {
        int count = chats == null ? 0 : chats.size();
        if (headerView != null) {
            count++;
        }
        return count;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatBaseViewHolder) {
            ((ChatBaseViewHolder) holder).setView(getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return CHAT_TYPE.HEADER;
        }
        WSChat chat = getItem(position);
        int type = CHAT_TYPE.TEXT;
        if (!TextUtils.isEmpty(chat.getKind())) {
            switch (chat.getKind()) {
                case WSChat.PRODUCT:
                    type = CHAT_TYPE.PRODUCT;
                    break;
                case WSChat.CUSTOM_MEAL:
                    type = CHAT_TYPE.CUSTOM_MEAL;
                    break;
                case WSChat.WORK_OR_CASE:
                    type = CHAT_TYPE.WORK;
                    break;
                case WSChat.IMAGE:
                    type = CHAT_TYPE.IMAGE;
                    break;
                case WSChat.VOICE:
                    type = CHAT_TYPE.VOICE;
                    break;
                case WSChat.TRACK:
                    try {
                        switch (chat.getExtObject(GsonUtil.getGsonInstance())
                                .getTrack()
                                .getAction()) {
                            case WSTrack.WORK:
                                type = CHAT_TYPE.TRACK_WORK;
                                break;
                            case WSTrack.CASE:
                                type = CHAT_TYPE.TRACK_CASE;
                                break;
                            case WSTrack.MERCHANT:
                                type = CHAT_TYPE.TRACK_MERCHANT;
                                break;
                            case WSTrack.SHOW_WINDOW:
                                type = CHAT_TYPE.TRACK_SHOW_WINDOW;
                                break;
                            case WSTrack.PRODUCT:
                                type = CHAT_TYPE.TRACK_PRODUCT;
                                break;
                            case WSTrack.WEDDING_CAR:
                                type = CHAT_TYPE.TRACK_WEDDING_CAR;
                                break;
                            default:
                                type = CHAT_TYPE.TRACK;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        type = CHAT_TYPE.TRACK;
                    }
                    break;
                case WSChat.TIPS:
                    // 解析tips子类型
                    try {
                        switch (chat.getExtObject(GsonUtil.getGsonInstance())
                                .getTips()
                                .getAction()) {
                            case WSTips.ACTION_MERCHANT_APPOINTMENT_TIP:
                            case WSTips.ACTION_CONTACT_WORDS_TIP:
                                type = CHAT_TYPE.TIPS_MERCHANT_APPOINTMENT;
                                break;
                            case WSTips.ACTION_COUPON_SUCCESS_TIP:
                            case WSTips.ACTION_APPOINTMENT_SUCCESS_TIP:
                                type = CHAT_TYPE.TIPS_MERCHANT_SUCCESS_TIP;
                                break;
                            default:
                                // Tips类型消息的默认视图
                                type = CHAT_TYPE.TIPS_DEFAULT_VIEW;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        type = CHAT_TYPE.TIPS_DEFAULT_VIEW;
                    }
                    break;
                case WSChat.LOCATION:
                    type = CHAT_TYPE.LOCATION;
                    break;
                case WSChat.MERCHANT_STATS:
                    type = CHAT_TYPE.MERCHANT_STATS;
                    break;
                case WSChat.HINTS:
                    // 解析hint子类型
                    try {
                        switch (chat.getExtObject(GsonUtil.getGsonInstance())
                                .getHints()
                                .getAction()) {
                            case WSHints.ACTION_MERCHANT_SMART_REPLY:
                                type = CHAT_TYPE.HINTS_SMART_REPLY;
                                break;
                            default:
                                // hints类型消息没有默认类型的视图，也就是使用Text类型的视图
                                break;
                        }
                    } catch (Exception e) {

                    }
                    break;
            }
        }
        if (chat.isCurrentUser()) {
            return type * INDEX;
        } else {
            return type * INDEX + 1;
        }
    }

    private View getView(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType / INDEX) {
            case CHAT_TYPE.IMAGE:
                layoutId = isSession(viewType) ? R.layout.chat_image_left___chat : R.layout
                        .chat_image_right___chat;
                break;
            case CHAT_TYPE.WORK:
            case CHAT_TYPE.PRODUCT:
            case CHAT_TYPE.CUSTOM_MEAL:
                layoutId = isSession(viewType) ? R.layout.chat_product_left___chat : R.layout
                        .chat_product_right___chat;
                break;
            case CHAT_TYPE.VOICE:
                layoutId = isSession(viewType) ? R.layout.chat_voice_left___chat : R.layout
                        .chat_voice_right___chat;
                break;
            case CHAT_TYPE.TRACK:
                layoutId = R.layout.chat_track___chat;
                break;
            case CHAT_TYPE.TRACK_WORK:
                layoutId = R.layout.chat_track_work___chat;
                break;
            case CHAT_TYPE.TRACK_CASE:
                layoutId = R.layout.chat_track_case___chat;
                break;
            case CHAT_TYPE.TRACK_PRODUCT:
                layoutId = R.layout.chat_track_product___chat;
                break;
            case CHAT_TYPE.TRACK_MERCHANT:
                layoutId = R.layout.chat_track_merchant___chat;
                break;
            case CHAT_TYPE.TRACK_SHOW_WINDOW:
                layoutId = R.layout.chat_track_show_window___chat;
                break;
            case CHAT_TYPE.TIPS_DEFAULT_VIEW:
            case CHAT_TYPE.TIPS_MERCHANT_APPOINTMENT:
                layoutId = R.layout.chat_tips_appointment___chat;
                break;
            case CHAT_TYPE.TIPS_MERCHANT_SUCCESS_TIP:
                layoutId = R.layout.chat_tips_success___chat;
                break;
            case CHAT_TYPE.LOCATION:
                layoutId = isSession(viewType) ? R.layout.chat_location_left___chat : R.layout
                        .chat_location_right___chat;
                break;
            case CHAT_TYPE.HINTS_SMART_REPLY:
                layoutId = isSession(viewType) ? R.layout.chat_hints_smart_reply_left___chat : R
                        .layout.chat_hints_smart_reply_right___chat;
                break;
            case CHAT_TYPE.MERCHANT_STATS:
                layoutId = R.layout.chat_merchant_stats___chat;
                break;
            case CHAT_TYPE.TRACK_WEDDING_CAR:
                layoutId = R.layout.chat_track_wedding_car___chat;
                break;
            default:
                layoutId = isSession(viewType) ? R.layout.chat_text_left___chat : R.layout
                        .chat_text_right___chat;
                break;
        }
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
    }

    class ChatBaseViewHolder extends RecyclerView.ViewHolder {
        private WSChat chat;
        private TextView timeView;

        public WSChat getChat() {
            return chat;
        }

        public ChatBaseViewHolder(View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.time);
        }


        public void setView(WSChat item, int position, int viewType) {
            this.chat = item;
            WSChat lastChat = position > 0 ? ChatAdapter.this.getItem(position - 1) : null;
            if (item.getCreatedAt() != null && (lastChat == null || lastChat.getCreatedAt() ==
                    null || (item.getCreatedAt()
                    .getTime() - lastChat.getCreatedAt()
                    .getTime() >= 180000))) {
                timeView.setVisibility(View.VISIBLE);
                timeView.setText(new DateTime(item.getCreatedAt()).toString(TIME_FORMAT));
            } else {
                timeView.setVisibility(View.GONE);
            }
        }
    }

    class ChatMessageBaseViewHolder extends ChatBaseViewHolder {

        private ImageView avatarView;
        private View errView;
        private View loadView;
        private TextView tvRead;
        private ImageView vipLogo;

        private int avatarSize;

        ChatMessageBaseViewHolder(View itemView) {
            super(itemView);
            avatarSize = CommonUtil.dp2px(itemView.getContext(), 40);
            avatarView = itemView.findViewById(R.id.avatar);
            loadView = itemView.findViewById(R.id.progressBar);
            errView = itemView.findViewById(R.id.err);
            tvRead = itemView.findViewById(R.id.tv_read);
            vipLogo = itemView.findViewById(R.id.vip_logo);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WSChat chat = getChat();
                    if (chat.isError() && !chat.isSending() && onChatClickListener != null) {
                        onChatClickListener.resendMessage(chat,
                                getItemViewType(),
                                getAdapterPosition());
                    }
                }
            });
            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WSChat chat = getChat();
                    if (isSession(getItemViewType())) {
                        onChatClickListener.onUserClick(chat.getFromId());
                    }
                }
            });
        }

        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            String avatarPath = null;
            if (isSession(viewType)) {
                if (sessionUser != null) {
                    avatarPath = sessionUser.getAvatar();
                }
            } else {
                if (loadView != null && errView != null && tvRead != null) {
                    if (item.isSending()) {
                        tvRead.setVisibility(View.GONE);
                        errView.setVisibility(View.GONE);
                        loadView.setVisibility(View.VISIBLE);
                    } else if (item.isError()) {
                        loadView.setVisibility(View.GONE);
                        tvRead.setVisibility(View.GONE);
                        errView.setVisibility(View.VISIBLE);
                    } else {
                        errView.setVisibility(View.GONE);
                        loadView.setVisibility(View.GONE);
                        tvRead.setVisibility(View.VISIBLE);
                        tvRead.setText(item.isUnRead() ? R.string.label_unread___chat : R.string
                                .label_read___chat);
                    }
                }
                if (user != null) {
                    avatarPath = user.getAvatar();
                }
            }
            if (TextUtils.isEmpty(avatarPath) && item.getSpeaker() != null) {
                avatarPath = item.getSpeaker()
                        .getAvatar();
            }
            avatarPath = ImageUtil.getAvatar(avatarPath, avatarSize);
            Glide.with(avatarView.getContext())
                    .load(avatarPath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(avatarView);
            if (vipLogo != null) {
                if (isSession(viewType)) {
                    if (sessionUser != null && sessionUser.getExtend() != null && sessionUser
                            .getExtend()
                            .getHljMemberPrivilege() > 0) {
                        vipLogo.setVisibility(View.VISIBLE);
                    } else {
                        vipLogo.setVisibility(View.GONE);
                    }
                } else {
                    if (user != null && user instanceof CustomerUser && ((CustomerUser) user)
                            .isMember()) {
                        vipLogo.setVisibility(View.VISIBLE);
                    } else {
                        vipLogo.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private class TextViewHolder extends ChatMessageBaseViewHolder {

        private TextView content;
        private int faceSize;

        private TextViewHolder(View itemView) {
            super(itemView);
            faceSize = CommonUtil.dp2px(itemView.getContext(), 24);
            content = itemView.findViewById(R.id.content);
            content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onChatClickListener != null) {
                        onChatClickListener.onTextCopyClick(getChat().getContent());
                    }
                    return false;
                }
            });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            if (TextUtils.isEmpty(item.getContent()) && (TextUtils.isEmpty(item.getKind()) ||
                    !item.getKind()
                    .equals(WSChat.TEXT))) {
                content.setText("该内容暂不支持，请更新至最新版查看");
            } else {
                content.setText(EmojiUtil.parseEmojiByTextForHeight(content.getContext(),
                        item.getContent(),
                        faceSize,
                        (int) content.getTextSize()));
            }
        }
    }

    private class ImageViewHolder extends ChatMessageBaseViewHolder {

        private ImageView imageView;
        private int singleEdge;

        private ImageViewHolder(View itemView) {
            super(itemView);
            singleEdge = CommonUtil.dp2px(itemView.getContext(), 150);
            imageView = itemView.findViewById(R.id.image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String path = getChat().getMedia() == null ? null : getChat().getMedia()
                            .getPath();
                    if (!TextUtils.isEmpty(path)) {
                        Intent intent = new Intent(context, SingleImageActivity.class);
                        intent.putExtra("path", path);
                        context.startActivity(intent);
                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                }
            });
        }

        private String getThumbnailUrl(String path, int size) {
            if (!TextUtils.isEmpty(path) && !path.startsWith("http://") && !path.startsWith(
                    "https://")) {
                return path;
            }
            return ImageUtil.getImagePathForWxH(path, size, size);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSMedia image = item.getMedia();
            String path = image == null ? null : getThumbnailUrl(image.getPath(), singleEdge);
            if (!TextUtils.isEmpty(path)) {
                int height = image.getHeight();
                int width = image.getWidth();
                if ((width == 0 || height == 0) && !path.startsWith("http://") && !path.startsWith(
                        "https://")) {
                    Size size = ImageUtil.getImageSizeFromPath(path);
                    width = size.getWidth();
                    height = size.getHeight();
                }
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageView
                        .getLayoutParams();
                if (height != 0 && width != 0) {
                    params.width = height > width ? width * singleEdge / height : singleEdge;
                    params.height = width > height ? height * singleEdge / width : singleEdge;
                }
                final int finalWidth = width;
                final int finalHeight = height;
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(new RequestOptions().dontAnimate()
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
                                if (resource != null && (finalHeight == 0 || finalWidth == 0)) {
                                    int height = resource.getIntrinsicHeight();
                                    int width = resource.getIntrinsicWidth();
                                    ViewGroup.MarginLayoutParams params = (ViewGroup
                                            .MarginLayoutParams) imageView.getLayoutParams();
                                    params.width = height > width ? width * singleEdge / height :
                                            singleEdge;
                                    params.height = width > height ? height * singleEdge / width
                                            : singleEdge;
                                }
                                return false;
                            }
                        })
                        .into(imageView);

            } else {
                imageView.setImageBitmap(null);
                Glide.with(imageView.getContext())
                        .clear(imageView);
            }
        }
    }

    private class VoiceViewHolder extends ChatMessageBaseViewHolder {

        private ImageView imgVoice;
        private TextView tvLength;

        private VoiceViewHolder(View itemView) {
            super(itemView);
            imgVoice = itemView.findViewById(R.id.img_voice);
            tvLength = itemView.findViewById(R.id.tv_length);
            itemView.findViewById(R.id.voice_layout)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WSVoicePlayUtil.getInstance()
                                    .playVoice(v.getContext(), getChat(), ChatAdapter.this);
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            if (item.getMedia() != null) {
                WSMedia voice = item.getMedia();
                tvLength.setText(tvLength.getContext()
                        .getString(R.string.label_voice_length___cm,
                                Math.round(voice.getVoiceDuration())));

                boolean isAnimation = imgVoice.getDrawable() != null && imgVoice.getDrawable()
                        instanceof AnimationDrawable;
                if (WSVoicePlayUtil.getInstance()
                        .getStatus() == WSVoicePlayUtil.PLAY && item == WSVoicePlayUtil
                        .getInstance()
                        .getChat()) {
                    if (!isAnimation) {
                        imgVoice.setImageResource(isSession(viewType) ? R.drawable
                                .sl_ic_voice_left : R.drawable.sl_ic_voice_right);
                        isAnimation = true;
                    }
                    imgVoice.setTag(true);
                } else if (isAnimation) {
                    imgVoice.setTag(false);
                }
                if (isAnimation) {
                    imgVoice.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimationDrawable drawable = null;
                            if (imgVoice.getDrawable() != null && imgVoice.getDrawable()
                                    instanceof AnimationDrawable) {
                                drawable = (AnimationDrawable) imgVoice.getDrawable();
                            }
                            if (drawable != null && imgVoice.getTag() != null && imgVoice.getTag
                                    () instanceof Boolean) {
                                Boolean b = (Boolean) imgVoice.getTag();
                                if (b && !drawable.isRunning()) {
                                    drawable.start();
                                } else if (!b && drawable.isRunning()) {
                                    drawable.stop();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onStatusChange(WSChat chat, int status) {
        notifyChatChange(chat);
    }

    private class ProductViewHolder extends ChatMessageBaseViewHolder {

        private ImageView cover;
        private TextView price;
        private TextView title;
        private int opuImageWidth;
        private int opuImageHeight;

        private ProductViewHolder(View itemView) {
            super(itemView);
            opuImageWidth = CommonUtil.dp2px(itemView.getContext(), 140);
            opuImageHeight = CommonUtil.dp2px(itemView.getContext(), 90);
            itemView.findViewById(R.id.product_layout)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null && getChat().getProduct() != null) {
                                onChatClickListener.onProductClick(getChat().getProduct(),
                                        getItemViewType() / INDEX);
                            }
                        }
                    });
            cover = (ImageView) itemView.findViewById(R.id.cover);
            price = (TextView) itemView.findViewById(R.id.price);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSProduct product = item.getProduct();
            if (product != null) {
                title.setText(product.getTitle());
                if (product.getActualPrice() > 0) {
                    price.setVisibility(View.VISIBLE);
                    price.setText(price.getContext()
                            .getString(R.string.label_price___cm,
                                    NumberFormatUtil.formatDouble2String(product.getActualPrice()
                                    )));
                } else {
                    price.setVisibility(View.GONE);
                }
                String path = ImageUtil.getImagePath(product.getCoverPath(), opuImageWidth);
                if (!TextUtils.isEmpty(path)) {
                    cover.setVisibility(View.VISIBLE);
                    if (product.getHeight() > 0 && product.getWidth() > 0) {
                        cover.getLayoutParams().height = Math.round(opuImageWidth * product
                                .getHeight() / product.getWidth());
                    } else {
                        cover.getLayoutParams().height = opuImageHeight;
                    }
                    Glide.with(cover.getContext())
                            .load(path)
                            .apply(new RequestOptions().fitCenter()
                                    .dontAnimate()
                                    .placeholder(R.mipmap.icon_empty_image))
                            .into(cover);

                } else {
                    cover.setVisibility(View.GONE);
                    Glide.with(cover.getContext())
                            .clear(cover);
                }

            }
        }
    }


    private class TrackViewHolder extends ChatBaseViewHolder {

        private TextView tvDetail;

        private TrackViewHolder(View itemView) {
            super(itemView);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_detail);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            try {
                tvDetail.setText(item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack()
                        .getDetail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TrackWorkViewHolder extends ChatBaseViewHolder {

        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvPrice;
        private int imageWidth;
        private int imageHeight;

        private TrackWorkViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 116);
            this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 74);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            TrackWork work = null;
            try {
                work = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack()
                        .getWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (work == null) {
                return;
            }
            Glide.with(ivCover.getContext())
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivCover);
            tvTitle.setText(work.getTitle());
            tvPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
        }
    }

    private class TrackCaseViewHolder extends ChatBaseViewHolder {

        private ImageView ivCover;
        private TextView tvTitle;
        private int imageWidth;
        private int imageHeight;

        private TrackCaseViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 116);
            this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 74);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            TrackCase aCase = null;
            try {
                aCase = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack()
                        .getCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (aCase == null) {
                return;
            }
            Glide.with(ivCover.getContext())
                    .load(ImagePath.buildPath(aCase.getCoverPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivCover);
            tvTitle.setText(aCase.getTitle());
        }
    }

    private class TrackProductViewHolder extends ChatBaseViewHolder {

        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvPrice;
        private int imageSize;

        private TrackProductViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            this.imageSize = CommonUtil.dp2px(itemView.getContext(), 74);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            TrackProduct product = null;
            try {
                product = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack()
                        .getProduct();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (product == null) {
                return;
            }
            Glide.with(ivCover.getContext())
                    .load(ImagePath.buildPath(product.getCoverPath())
                            .width(imageSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivCover);
            tvTitle.setText(product.getTitle());
            tvPrice.setText(CommonUtil.formatDouble2String(product.getShowPrice()));
        }
    }

    private class TrackMerchantViewHolder extends ChatBaseViewHolder {

        private TrackMerchantViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
        }
    }

    private class TrackShowWindowViewHolder extends ChatBaseViewHolder {

        private ImageView ivImage;
        private int imageHeight;

        private TrackShowWindowViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 90);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSTrack track = null;
            try {
                track = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (track == null) {
                return;
            }
            Glide.with(ivImage.getContext())
                    .load(ImagePath.buildPath(track.getImagePath())
                            .height(imageHeight)
                            .path())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image)
                            .fitCenter())
                    .into(ivImage);
        }
    }

    private class TipsDefaultViewHolder extends ChatBaseViewHolder {
        private final int faceSize;
        private TextView tvTitle;
        private TextView tvContent;

        public TipsDefaultViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            faceSize = CommonUtil.dp2px(itemView.getContext(), 24);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSTips tips = null;
            try {
                tips = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTips();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tips != null) {
                tvTitle.setText(tips.getTitle());
                tvContent.setText(tips.getDetail());
            } else {
                tvContent.setText(EmojiUtil.parseEmojiByTextForHeight(tvContent.getContext(),
                        item.getContent(),
                        faceSize,
                        (int) tvContent.getTextSize()));
            }
        }
    }

    private class MerchantTipsViewHolder extends ChatBaseViewHolder {

        private TextView tvTitle;
        private TextView tvContent;

        public MerchantTipsViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSTips tips = null;
            try {
                tips = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTips();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tips == null) {
                return;
            }
            tvTitle.setText(tips.getTitle());
            if (tips.getDetail()
                    .contains(LINK_OF_APPOINTMENT_STR)) {
                SpannableString ss = new SpannableString(tips.getDetail());
                int startIndex = tips.getDetail()
                        .indexOf(LINK_OF_APPOINTMENT_STR);
                int endIndex = startIndex + LINK_OF_APPOINTMENT_STR.length();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        WSChat chat = getChat();
                        onChatClickListener.onAppointmentClick(chat.getFromId());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvContent.setText(ss);
                tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                tvContent.setLinkTextColor(ContextCompat.getColor(tvContent.getContext(),
                        R.color.colorLink));
            } else {
                tvContent.setText(tips.getDetail());
            }
        }
    }

    private class SuccessTipsViewHolder extends ChatBaseViewHolder {

        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvSubTitle;

        public SuccessTipsViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvSubTitle = itemView.findViewById(R.id.tv_sub_title);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            WSTips tips = null;
            try {
                tips = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTips();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tips == null) {
                return;
            }
            tvTitle.setText(tips.getTitle());
            tvContent.setText(tips.getDetail());
            if (tips.getAction() == WSTips.ACTION_APPOINTMENT_SUCCESS_TIP) {
                tvSubTitle.setText("请保持电话通畅");
            } else if (tips.getAction() == WSTips.ACTION_COUPON_SUCCESS_TIP) {
                tvSubTitle.setText("可到“钱包”中查看");
            } else {
                tvSubTitle.setText("");
            }
        }
    }

    private class LocationViewHolder extends ChatMessageBaseViewHolder {

        private TextView tvAddress;
        private ImageView ivMap;
        private int width;
        private int height;

        private LocationViewHolder(View itemView) {
            super(itemView);
            width = CommonUtil.dp2px(itemView.getContext(), 160);
            height = CommonUtil.dp2px(itemView.getContext(), 90);
            ivMap = itemView.findViewById(R.id.iv_map);
            tvAddress = itemView.findViewById(R.id.tv_address);
            itemView.findViewById(R.id.location_layout)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                WSLocation location = getChat().getExtObject(GsonUtil
                                        .getGsonInstance())
                                        .getLocation();
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Map.NAVIGATE_MAP)
                                        .withString("title", location.getTitle())
                                        .withString("address", location.getAddress())
                                        .withDouble("latitude", location.getLatitude())
                                        .withDouble("longitude", location.getLongitude())
                                        .navigation(v.getContext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            try {
                WSLocation location = getChat().getExtObject(GsonUtil.getGsonInstance())
                        .getLocation();
                tvAddress.setText(location.getAddress());

                MapLibraryService mapLibraryService = (MapLibraryService) ARouter.getInstance()
                        .build(RouterPath.ServicePath.Map.MAP_LIBRARY_SERVICE)
                        .navigation(ivMap.getContext());
                if (mapLibraryService != null) {
                    Glide.with(ivMap.getContext())
                            .load(mapLibraryService.getAMapUrl(location.getLongitude(),
                                    location.getLatitude(),
                                    width,
                                    height,
                                    12,
                                    HljCommon.MARKER_ICON_RED))
                            .apply(new RequestOptions().dontAnimate())
                            .into(ivMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MerchantStatsViewHolder extends ChatBaseViewHolder {
        @BindView(R2.id.time)
        TextView time;
        @BindView(R2.id.img_logo)
        ImageView imgLogo;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.tv_address)
        TextView tvAddress;
        @BindView(R2.id.img_level_icon)
        ImageView imgLevelIcon;
        @BindView(R2.id.merchant_layout)
        LinearLayout merchantLayout;
        @BindView(R2.id.tv_reply_rate)
        TextView tvReplyRate;
        @BindView(R2.id.tv_reply_time)
        TextView tvReplyTime;
        @BindView(R2.id.tv_good_rate)
        TextView tvGoodRate;
        @BindView(R2.id.content_layout)
        FrameLayout contentLayout;

        MerchantStatsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            if (item.getExtObject() == null || item.getExtObject()
                    .getMerchantStats() == null) {
                return;
            }
            final WSMerchantStats stats = item.getExtObject()
                    .getMerchantStats();

            tvName.setText(stats.getName());
            Glide.with(contentLayout.getContext())
                    .load(stats.getLogoPath())
                    .into(imgLogo);
            merchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChatClickListener != null) {
                        onChatClickListener.onMerchantClick(stats.getId());
                    }
                }
            });
            int levelId = 0;
            if (stats.getGrade() == 2) {
                levelId = R.mipmap.icon_merchant_level2_36_36;
            } else if (stats.getGrade() == 3) {
                levelId = R.mipmap.icon_merchant_level3_36_36;
            } else if (stats.getGrade() == 4) {
                levelId = R.mipmap.icon_merchant_level4_36_36;
            }
            if (levelId != 0) {
                imgLevelIcon.setVisibility(View.VISIBLE);
                imgLevelIcon.setImageResource(levelId);
            } else {
                imgLevelIcon.setVisibility(View.GONE);
            }
            tvAddress.setText(stats.getAddress());
            tvAddress.setVisibility(TextUtils.isEmpty(stats.getAddress()) ? View.GONE : View
                    .VISIBLE);
            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Map.NAVIGATE_MAP)
                            .withString("title", stats.getName())
                            .withString("address", stats.getAddress())
                            .withDouble("latitude", stats.getLatitude())
                            .withDouble("longitude", stats.getLongitude())
                            .navigation(v.getContext());
                }
            });
            tvReplyRate.setText(CommonUtil.formatDouble2StringWithOneFloat(stats.getReplyRate() *
                    100) + "%");
            tvReplyTime.setText(String.valueOf((int) stats.getReplyTime()) + "分钟");
            tvGoodRate.setText(CommonUtil.formatDouble2StringWithOneFloat(stats.getGoodRate() *
                    100) + "%");
        }
    }


    class HintSmartReplyViewHolder extends ChatBaseViewHolder {
        @BindView(R2.id.time)
        TextView time;
        @BindView(R2.id.avatar)
        RoundedImageView avatar;
        @BindView(R2.id.content_layout)
        LinearLayout contentLayout;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.detail_layout)
        LinearLayout detailLayout;

        private final int faceSize;
        private final int avatarSize;

        HintSmartReplyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            faceSize = CommonUtil.dp2px(itemView.getContext(), 24);
            avatarSize = CommonUtil.dp2px(itemView.getContext(), 40);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WSChat chat = getChat();
                    if (isSession(getItemViewType())) {
                        onChatClickListener.onUserClick(chat.getFromId());
                    }
                }
            });
        }

        @Override
        public void setView(final WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            String avatarPath = null;
            if (isSession(viewType)) {
                if (sessionUser != null) {
                    avatarPath = sessionUser.getAvatar();
                }
            } else {
                if (user != null) {
                    avatarPath = user.getAvatar();
                }
            }
            if (TextUtils.isEmpty(avatarPath) && item.getSpeaker() != null) {
                avatarPath = item.getSpeaker()
                        .getAvatar();
            }
            avatarPath = ImageUtil.getAvatar(avatarPath, avatarSize);
            Glide.with(avatar.getContext())
                    .load(avatarPath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(avatar);

            WSHints hints = null;
            try {
                hints = item.getExtObject(GsonUtil.getGsonInstance())
                        .getHints();
            } catch (Exception e) {
                e.printStackTrace();
            }
            detailLayout.setVisibility(View.GONE);
            if (hints != null) {
                tvTitle.setText(EmojiUtil.parseEmojiByTextForHeight(tvTitle.getContext(),
                        hints.getTitle(),
                        faceSize,
                        (int) tvTitle.getTextSize()));
                if (!isSession(viewType)) {
                    tvTitle.setTextColor(ContextCompat.getColor(tvTitle.getContext(),
                            R.color.colorWhite));
                }
                if (!CommonUtil.isCollectionEmpty(hints.getDetail())) {
                    detailLayout.setVisibility(View.VISIBLE);
                    detailLayout.removeAllViews();
                    for (final String str : hints.getDetail()) {
                        View itemView = LayoutInflater.from(tvTitle.getContext())
                                .inflate(R.layout.hint_smart_text_item___chat, null, false);
                        TextView tvItem = itemView.findViewById(R.id.tv_send_txt);
                        if (!isSession(viewType)) {
                            tvItem.setTextColor(ContextCompat.getColor(tvTitle.getContext(),
                                    R.color.colorWhite));
                        }
                        tvItem.setText(str);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onChatClickListener != null) {
                                    onChatClickListener.onSmartReplayClick(item, str);
                                }
                            }
                        });
                        detailLayout.addView(itemView);
                    }
                }
            } else {
                tvTitle.setText(EmojiUtil.parseEmojiByTextForHeight(tvTitle.getContext(),
                        item.getContent(),
                        faceSize,
                        (int) tvTitle.getTextSize()));
            }
        }
    }

    private class TrackWeddingCarViewHolder extends ChatBaseViewHolder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvPrice;
        private int imageWidth;
        private int imageHeight;

        private TrackWeddingCarViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 130);
            this.imageHeight = CommonUtil.dp2px(itemView.getContext(), 74);
            itemView.findViewById(R.id.track_item)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChatClickListener != null) {
                                try {
                                    onChatClickListener.onTrackClick(getChat().getExtObject
                                            (GsonUtil.getGsonInstance())
                                            .getTrack());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public void setView(WSChat item, int position, int viewType) {
            super.setView(item, position, viewType);
            TrackWeddingCarProduct carProduct = null;
            try {
                carProduct = item.getExtObject(GsonUtil.getGsonInstance())
                        .getTrack()
                        .getCarProduct();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (carProduct == null) {
                return;
            }
            String coverImagePath = null;
            if (carProduct.getCoverImage() != null) {
                coverImagePath = carProduct.getCoverImage()
                        .getImagePath();
            }
            Glide.with(ivCover.getContext())
                    .load(ImagePath.buildPath(coverImagePath)
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(ivCover);

            tvTitle.setText(carProduct.getTitle());
            tvPrice.setText(CommonUtil.formatDouble2String(carProduct.getShowPrice()));
        }
    }

    public interface OnChatClickListener {

        void resendMessage(WSChat item, int type, int position);

        void onTextCopyClick(String text);

        void onProductClick(WSProduct product, int type);

        void onUserClick(long userId);

        void onTrackClick(WSTrack wsTrack);

        void onAppointmentClick(long userId);

        void onMerchantClick(long merchantId);

        void onSmartReplayClick(WSChat chat, String text);
    }
}
