package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hunliji.hljkefulibrary.moudles.EMTrack;
import com.hyphenate.helpdesk.model.ToCustomServiceInfo;

/**
 * Created by wangtao on 2017/10/20.
 */

public abstract class EMChatMessageBaseViewHolder extends EMChatTimeBaseViewHolder {

    private ImageView avatarView;
    private View errView;
    private View loadView;
    private int avatarSize;
    private ImageView vipLogo;
    public OnChatClickListener onChatClickListener;
    private User user;

    public EMChatMessageBaseViewHolder(View itemView) {
        super(itemView);
        user = UserSession.getInstance()
                .getUser(itemView.getContext());
        avatarSize = CommonUtil.dp2px(itemView.getContext(), 40);
        avatarView = itemView.findViewById(R.id.avatar);
        vipLogo = itemView.findViewById(R.id.vip_logo);
        loadView = itemView.findViewById(R.id.progressBar);
        errView = itemView.findViewById(R.id.err);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMChat chat = getItem();
                if (chat.isReceive()) {
                    return;
                }
                if (chat.isSendError() && !chat.isSending()) {
                    if (onChatClickListener != null) {
                        onChatClickListener.resendMessage(chat);
                    }
                }
            }
        });
    }

    public void setOnChatClickListener(OnChatClickListener onChatClickListener) {
        this.onChatClickListener = onChatClickListener;
    }

    public void setChatView(
            Context mContext,
            String avatarPath,
            EMChat item,
            EMChat lastMessage,
            int position,
            int viewType) {
        super.setChatView(mContext, avatarPath, item, lastMessage, position, viewType);
        Glide.with(avatarView.getContext())
                .load(ImagePath.buildPath(avatarPath)
                        .width(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(avatarView);
        if (!item.isReceive()) {
            if (loadView != null && errView != null) {
                if (item.isSending()) {
                    errView.setVisibility(View.GONE);
                    loadView.setVisibility(View.VISIBLE);
                } else if (item.isSendError()) {
                    loadView.setVisibility(View.GONE);
                    errView.setVisibility(View.VISIBLE);
                } else {
                    errView.setVisibility(View.GONE);
                    loadView.setVisibility(View.GONE);
                }
            }
            if (vipLogo != null) {
                if (user != null && user instanceof CustomerUser && ((CustomerUser) user)
                        .isMember()) {
                    vipLogo.setVisibility(View.VISIBLE);
                } else {
                    vipLogo.setVisibility(View.GONE);
                }
            }
        }
    }


    public interface OnChatClickListener {

        void resendMessage(EMChat item);

        void onTextCopyClick(String text);

        void onEnquiryCancelClick(EMChat chat);

        void onEnquiryConfirmClick(EMChat chat);

        void onTrackClick(EMTrack track);

        void onMerchantClick(Merchant merchant);

        void onRobotMenuClick(Object menuItem);

        void onTransferToKefuClick(ToCustomServiceInfo toCustomServiceInfo);
    }
}
