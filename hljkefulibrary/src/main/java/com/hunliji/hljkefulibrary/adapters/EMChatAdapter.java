package com.hunliji.hljkefulibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMChatMenuViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMChatMessageBaseViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMChatTimeBaseViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMChatTransferViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMEnquiryDoneViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMEnquiryViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMExtraViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMImageViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMMerchantViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMTextViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMTrackViewHolder;
import com.hunliji.hljkefulibrary.adapters.viewholders.EMVoiceViewHolder;
import com.hunliji.hljkefulibrary.moudles.EMChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/10/20.
 */

public class EMChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static class ItemType {
        private static final int Header = -1;
        private static final int Text = 1;
        private static final int Image = 2;
        private static final int Voice = 3;
        private static final int Track = 4;
        private static final int Merchant = 5;
        private static final int Enquiry = 6;
        private static final int EnquiryDone = 7;
        private static final int RobotMenu = 8;
        private static final int TransferHint = 9;
        private static final int ExtraView = 10;
    }

    private Context context;
    private View headerView;
    private List<EMChat> chats;

    private String sessionAvatar;
    private String userAvatar;

    private EMChatMessageBaseViewHolder.OnChatClickListener onChatClickListener;

    public EMChatAdapter(Context context, String sessionAvatar, String userAvatar) {
        this.context = context;
        this.sessionAvatar = sessionAvatar;
        this.userAvatar = userAvatar;
    }

    public void reset(String sessionAvatar, String userAvatar) {
        if(chats!=null){
            chats.clear();
        }
        this.sessionAvatar = sessionAvatar;
        this.userAvatar = userAvatar;
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    public void setOnChatClickListener(
            EMChatMessageBaseViewHolder.OnChatClickListener onChatClickListener) {
        this.onChatClickListener = onChatClickListener;
    }

    public void setChats(List<EMChat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public void addTopChats(List<EMChat> chats) {
        if (CommonUtil.isCollectionEmpty(chats)) {
            return;
        }
        if (this.chats == null) {
            this.chats = new ArrayList<>();
        }
        this.chats.addAll(0, chats);
        int index = headerView == null ? 0 : 1;
        notifyItemRangeInserted(index, chats.size());
    }

    public void addChat(EMChat chat) {
        if (chat == null) {
            return;
        }
        List<EMChat> chats = new ArrayList<>();
        chats.add(chat);
        addChats(chats);
    }

    public void addChats(List<EMChat> chats) {
        if (CommonUtil.isCollectionEmpty(chats)) {
            return;
        }
        if (this.chats == null) {
            this.chats = new ArrayList<>();
        }
        this.chats.addAll(chats);
        notifyItemRangeInserted(getItemCount() - chats.size(), chats.size());
    }


    public void notifyChat(EMChat chat) {
        if (chat != null) {
            int index = chats.indexOf(chat);
            if (index >= 0) {
                index += headerView == null ? 0 : 1;
                notifyItemChanged(index);
            }
        }
    }

    public void removeChat(EMChat chat) {
        int index = chats.indexOf(chat);
        if (index >= 0) {
            chats.remove(index);
            index += headerView == null ? 0 : 1;
            notifyItemRemoved(index);
        }
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.Header) {
            return new ExtraBaseViewHolder(headerView);
        } else if (viewType / 2 == ItemType.EnquiryDone) {
            return new EMEnquiryDoneViewHolder(parent);
        }else {
            EMChatMessageBaseViewHolder viewHolder = null;
            switch (viewType / 2) {
                case ItemType.Text:
                    viewHolder = new EMTextViewHolder(parent, isReceive(viewType));
                    break;
                case ItemType.Image:
                    viewHolder = new EMImageViewHolder(parent, isReceive(viewType));
                    break;
                case ItemType.Voice:
                    viewHolder = new EMVoiceViewHolder(parent, isReceive(viewType));
                    break;
                case ItemType.Track:
                    viewHolder = new EMTrackViewHolder(parent, isReceive(viewType));
                    break;
                case ItemType.Merchant:
                    viewHolder = new EMMerchantViewHolder(parent, isReceive(viewType));
                    break;
                case ItemType.Enquiry:
                    viewHolder = new EMEnquiryViewHolder(parent);
                    break;
                case ItemType.RobotMenu:
                    viewHolder = new EMChatMenuViewHolder(parent);
                    break;
                case ItemType.TransferHint:
                    viewHolder = new EMChatTransferViewHolder(parent);
                    break;
                case ItemType.ExtraView:
                    viewHolder = new EMExtraViewHolder(parent);
                    break;
            }
            if (viewHolder != null) {
                viewHolder.setOnChatClickListener(onChatClickListener);
            }
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof EMChatTimeBaseViewHolder) {
            ((EMChatTimeBaseViewHolder) holder).setChatView(context,
                    getAvatar(holder.getItemViewType()),
                    getItem(position),
                    getItem(position - 1),
                    position,
                    holder.getItemViewType());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null) {
            if (position == 0) {
                return ItemType.Header;
            }
        }
        int type;
        EMChat chat = getItem(position);
        switch (chat.getType()) {
            case EMChat.IMAGE:
                type = ItemType.Image;
                break;
            case EMChat.VOICE:
                type = ItemType.Voice;
                break;
            case EMChat.TRACK:
                type = ItemType.Track;
                break;
            case EMChat.MERCHANT:
                type = ItemType.Merchant;
                break;
            case EMChat.ENQUIRY:
                type = ItemType.Enquiry;
                break;
            case EMChat.ENQUIRY_RESULT:
                type = ItemType.EnquiryDone;
                break;
            case EMChat.ROBOT:
                type = ItemType.RobotMenu;
                break;
            case EMChat.TRANSFER_HINT:
                type = ItemType.TransferHint;
                break;
            case EMChat.EXTRA_VIEW:
                type = ItemType.ExtraView;
                break;
            default:
                type = ItemType.Text;
                break;
        }
        type *= 2;
        if (chat.isReceive()) {
            type++;
        }
        return type;
    }

    private boolean isReceive(int viewType) {
        return viewType > 0 && viewType % 2 == 1;
    }

    @Override
    public int getItemCount() {
        if (CommonUtil.isCollectionEmpty(chats)) {
            return 0;
        }
        int size = headerView == null ? 0 : 1;
        size += chats.size();
        return size;
    }

    public EMChat getItem(int position) {
        if (position < 0) {
            return null;
        }
        if (headerView != null) {
            if (position == 0) {
                return null;
            }
            position--;
        }
        return chats.get(position);
    }

    private String getAvatar(int viewType) {
        if (isReceive(viewType)) {
            return sessionAvatar;
        }
        return userAvatar;
    }
}
