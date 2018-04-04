package me.suncloud.marrymemo.adpter.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.LastMessage;
import me.suncloud.marrymemo.widget.CustomTextView;

/**
 * Created by luohanlin on 2017/11/14.
 */

public class MessageHomeListAdapter extends RecyclerView.Adapter<BaseViewHolder<LastMessage>> {

    private Context context;
    private List<LastMessage> messages;

    private View headerView;
    private View footerView;
    private LayoutInflater inflater;

    public static final int ITEM_TYPE_LIST = 0;
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_FOOTER = 2;
    public OnMessageClickListener onMessageClickListener;

    public MessageHomeListAdapter(
            Context context, List<LastMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener) {
        this.onMessageClickListener = onMessageClickListener;
    }

    @Override
    public BaseViewHolder<LastMessage> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View view = inflater.inflate(R.layout.message_list_item, parent, false);
                return new MessageViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(BaseViewHolder<LastMessage> holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof MessageViewHolder) {
                    MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
                    if (position < messages.size() - 1 + getHeaderViewCount()) {
                        messageViewHolder.divider.setVisibility(View.VISIBLE);
                    } else {
                        messageViewHolder.divider.setVisibility(View.GONE);
                    }
                    messageViewHolder.setView(context,
                            messages.get(position - getHeaderViewCount()),
                            position,
                            viewType);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(messages);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    class MessageViewHolder extends BaseViewHolder<LastMessage> {
        @BindView(R.id.logo_img)
        RoundedImageView logoImg;
        @BindView(R.id.tv_unread_count)
        TextView tvUnreadCount;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.tv_draft_label)
        TextView tvDraftLabel;
        @BindView(R.id.last_msg)
        CustomTextView lastMsg;
        @BindView(R.id.divider)
        View divider;
        View view;

        private SimpleDateFormat dateFormat;
        private int logoSize;
        private int faceSize;
        private int maxWidth;

        MessageViewHolder(View v) {
            super(v);
            this.view = v;
            ButterKnife.bind(this, view);
            faceSize = CommonUtil.dp2px(context, 16);
            maxWidth = CommonUtil.dp2px(context, 80);
            logoSize = CommonUtil.dp2px(context, 50);
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat(context.getString(R.string.format_date_type1),
                        Locale.getDefault());
            }

        }

        @Override
        protected void setViewData(
                Context mContext, final LastMessage item, final int position, int viewType) {
            if (item == null) {
                return;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMessageClickListener != null) {
                        onMessageClickListener.onItemClick(item, position);
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onMessageClickListener != null) {
                        onMessageClickListener.onItemLongClick(item, position);
                    }
                    return true;
                }
            });
            if (item.getTime() != null) {
                time.setText(dateFormat.format(item.getTime()));
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(item.getSessionAvatar())
                            .width(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                    .into(logoImg);
            if (item.getUnreadSessionCount() > 0) {
                if (item.getUnreadSessionCount() > 99) {
                    tvUnreadCount.setText("99+");
                } else {
                    tvUnreadCount.setText(String.valueOf(item.getUnreadSessionCount()));
                }
                tvUnreadCount.setVisibility(View.VISIBLE);
            } else {
                tvUnreadCount.setVisibility(View.GONE);
            }
            name.setText(item.getSessionNick());
            tvDraftLabel.setVisibility(TextUtils.isEmpty(item.getDraftContent()) ? View.GONE :
                    View.VISIBLE);
            lastMsg.setImageSpanText(item.getContent(),
                    faceSize,
                    ImageSpan.ALIGN_BASELINE,
                    maxWidth);
        }
    }

    public interface OnMessageClickListener {
        void onItemClick(LastMessage lastMessage, int position);

        void onItemLongClick(LastMessage lastMessage, int position);

        void onUserClick(LastMessage lastMessage, int position);
    }
}

