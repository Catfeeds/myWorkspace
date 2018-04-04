package me.suncloud.marrymemo.adpter.community.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker
        .TrackerCommunityEventViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.community.CommunityEventActivity;

/**
 * Created by mo_yu on 2018/3/14.社区进行中的活动
 */

public class CommunityEventStartViewHolder extends TrackerCommunityEventViewHolder {

    @BindView(R.id.tv_event_title)
    TextView tvEventTitle;
    @BindView(R.id.img_event_thread)
    ImageView imgEventThread;
    @BindView(R.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R.id.tv_answer_tip)
    TextView tvAnswerTip;
    @BindView(R.id.tv_go_event)
    TextView tvGoEvent;
    private int imageWidth;
    private int imageHeight;
    private int avatarSize;

    public CommunityEventStartViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_event_start_layout, parent, false));
    }

    public CommunityEventStartViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                32);
        imageHeight = imageWidth * 276 / 686;
        imgEventThread.getLayoutParams().height = imageHeight;
        avatarSize = CommonUtil.dp2px(itemView.getContext(), 20);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem() == null) {
                    return;
                }
                Activity activity = (Activity) v.getContext();
                Intent intent = new Intent(activity, CommunityEventActivity.class);
                intent.putExtra(CommunityEventActivity.ARG_ID, getItem().getId());
                activity.startActivity(intent);
                activity.overridePendingTransition(com.hunliji.hljnotelibrary.R.anim.slide_in_right,
                        com.hunliji.hljnotelibrary.R.anim.activity_anim_default);
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, CommunityEvent communityEvent, int position, int viewType) {
        if (!CommonUtil.isEmpty(communityEvent.getTitle())) {
            tvEventTitle.setVisibility(View.VISIBLE);
            tvEventTitle.setText(communityEvent.getTitle());
        } else {
            tvEventTitle.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(ImagePath.buildPath(communityEvent.getImage())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(imgEventThread);
        String authorName = null;
        String authorAvatar = null;
        Author lastAuth = communityEvent.getLastUser();
        if (lastAuth != null) {
            authorAvatar = lastAuth.getAvatar();
            authorName = lastAuth.getName();
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(authorAvatar)
                        .width(avatarSize)
                        .height(avatarSize)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(rivAuthAvatar);
        tvAuthName.setText(authorName);
        tvAnswerTip.setText(mContext.getString(R.string.format_community_event_tip,
                communityEvent.getWatchCount()));
    }

    @Override
    public View trackerView() {
        return itemView;
    }
}
