package com.hunliji.marrybiz.adapter.notification.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.notification.NotificationGroupItem;
import com.hunliji.marrybiz.view.notification.CommonNotificationActivity;
import com.hunliji.marrybiz.view.notification.CommunityNotificationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2017/8/18.
 */

public class NotificationGroupViewHolder extends BaseViewHolder<NotificationGroupItem> {


    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_news_count)
    TextView tvNewsCount;

    public NotificationGroupViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_group_item, parent, false));
    }


    private NotificationGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (getItem().getGroup()){
                    case COMMUNITY:
                        intent = new Intent(v.getContext(), CommunityNotificationActivity.class);
                        break;
                    default:
                        intent = new Intent(v.getContext(), CommonNotificationActivity.class);
                        intent.putExtra("group",getItem().getGroup());
                        break;
                }
                v.getContext().startActivity(intent);
                if(v.getContext() instanceof Activity){
                    ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, NotificationGroupItem item, int position, int viewType) {
        switch (item.getGroup()) {
            case ORDER:
                tvTitle.setText(R.string.label_notification_group_order);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_order);
                break;
            case RESERVATION:
                tvTitle.setText(R.string.label_notification_group_reservation);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_reservation);
                break;
            case COUPON:
                tvTitle.setText(R.string.label_notification_group_coupon);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_coupon);
                break;
            case EVENT:
                tvTitle.setText(R.string.label_notification_group_event);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_event);
                break;
            case COMMUNITY:
                tvTitle.setText(R.string.label_notification_group_community);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_community);
                break;
            case COMMENT:
                tvTitle.setText(R.string.label_notification_group_comment);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_comment);
                break;
            case INCOME:
                tvTitle.setText(R.string.label_notification_group_income);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_income);
                break;
            default:
                tvTitle.setText(R.string.label_notification_group_other);
                ivIcon.setImageResource(R.mipmap.icon_notice_group_other);
                break;
        }
        if (item.getNewsCount() > 0) {
            tvNewsCount.setVisibility(View.VISIBLE);
            tvNewsCount.setText(String.valueOf(item.getNewsCount() > 99 ? "99+" : item
                    .getNewsCount()));
        } else {
            tvNewsCount.setVisibility(View.GONE);
        }

    }
}
