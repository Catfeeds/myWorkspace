package me.suncloud.marrymemo.adpter.message;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.views.activities.CardReplyActivity;
import com.hunliji.hljcardcustomerlibrary.views.activities.ReceiveGiftCashActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.message.NotificationGroupItem;
import me.suncloud.marrymemo.view.notification.CommunityNotificationActivity;
import me.suncloud.marrymemo.view.notification.EventNotificationActivity;
import me.suncloud.marrymemo.view.notification.FinancialNotificationActivity;
import me.suncloud.marrymemo.view.notification.OrderNotificationActivity;
import me.suncloud.marrymemo.view.notification.SystemNotificationActivity;

/**
 * Created by luohanlin on 2017/11/15.
 */

public class MsgHeaderFlowAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<NotificationGroupItem> mData;
    private LayoutInflater layoutInflater;

    public MsgHeaderFlowAdapter(Context c, ArrayList<NotificationGroupItem> d) {
        this.context = c;
        this.mData = d;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = layoutInflater.inflate(R.layout.message_home_flow_item, null);
        FlowViewHolder holder = new FlowViewHolder(convertView);
        convertView.setTag(holder);
        holder.setView(position);

        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : (mData.size() / 4) + (mData.size() % 4 == 0 ? 0 : 1);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    class FlowViewHolder {
        @BindView(R.id.img_logo_1)
        ImageView imgLogo1;
        @BindView(R.id.tv_title_1)
        TextView tvTitle1;
        @BindView(R.id.tv_unread_1)
        TextView tvUnread1;
        @BindView(R.id.msg_layout_1)
        RelativeLayout msgLayout1;
        @BindView(R.id.img_logo_2)
        ImageView imgLogo2;
        @BindView(R.id.tv_title_2)
        TextView tvTitle2;
        @BindView(R.id.tv_unread_2)
        TextView tvUnread2;
        @BindView(R.id.msg_layout_2)
        RelativeLayout msgLayout2;
        @BindView(R.id.img_logo_3)
        ImageView imgLogo3;
        @BindView(R.id.tv_title_3)
        TextView tvTitle3;
        @BindView(R.id.tv_unread_3)
        TextView tvUnread3;
        @BindView(R.id.msg_layout_3)
        RelativeLayout msgLayout3;
        @BindView(R.id.img_logo_4)
        ImageView imgLogo4;
        @BindView(R.id.tv_title_4)
        TextView tvTitle4;
        @BindView(R.id.tv_unread_4)
        TextView tvUnread4;
        @BindView(R.id.msg_layout_4)
        RelativeLayout msgLayout4;

        FlowViewHolder(View view) {ButterKnife.bind(this, view);}

        private void setView(int position) {
            ArrayList<NotificationGroupItem> listInOnePage = new ArrayList<>(mData.subList
                    (position * 4,
                    Math.min((position + 1) * 4, mData.size())));
            for (int i = 0; i < 4; i++) {
                NotificationGroupItem item = null;
                if (i < listInOnePage.size()) {
                    item = listInOnePage.get(i);
                }
                switch (i) {
                    case 1:
                        setItemView(msgLayout2, imgLogo2, tvTitle2, tvUnread2, item);
                        break;
                    case 2:
                        setItemView(msgLayout3, imgLogo3, tvTitle3, tvUnread3, item);
                        break;
                    case 3:
                        setItemView(msgLayout4, imgLogo4, tvTitle4, tvUnread4, item);
                        break;
                    default:
                        setItemView(msgLayout1, imgLogo1, tvTitle1, tvUnread1, item);
                        break;
                }
            }
        }

        private void setItemView(
                RelativeLayout msgLayout,
                ImageView imgLogo,
                TextView tvTitle,
                TextView tvUnread,
                NotificationGroupItem item) {
            if (item != null) {
                msgLayout.setVisibility(View.VISIBLE);
                Intent intent;
                switch (item.getGroup()) {
                    case ORDER:
                        tvTitle.setText("订单动态");
                        imgLogo.setImageResource(R.mipmap.icon_msg_order);
                        intent = new Intent(context, OrderNotificationActivity.class);
                        break;
                    case COMMUNITY:
                        tvTitle.setText(context.getString(R.string.label_news_community));
                        imgLogo.setImageResource(R.mipmap.icon_msg_community);
                        intent = new Intent(context, CommunityNotificationActivity.class);
                        break;
                    case GIFT:
                        tvTitle.setText("礼物礼金");
                        imgLogo.setImageResource(R.mipmap.icon_msg_gift);
                        intent = new Intent(context, ReceiveGiftCashActivity.class);
                        break;
                    case SIGN:
                        tvTitle.setText("宾客回复");
                        imgLogo.setImageResource(R.mipmap.icon_msg_sign);
                        intent = new Intent(context, CardReplyActivity.class);
                        break;
                    case FINANCIAL:
                        tvTitle.setText("结婚助手");
                        imgLogo.setImageResource(R.mipmap.icon_msg_financial);
                        intent = new Intent(context, FinancialNotificationActivity.class);
                        break;
                    case EVENT:
                        tvTitle.setText("活动通知");
                        imgLogo.setImageResource(R.mipmap.icon_msg_event);
                        intent = new Intent(context, EventNotificationActivity.class);
                        break;
                    default:
                        // 系统通知
                        tvTitle.setText("系统通知");
                        imgLogo.setImageResource(R.mipmap.icon_msg_system);
                        intent = new Intent(context, SystemNotificationActivity.class);
                        break;
                }
                if (item.getNewCount() > 0) {
                    if (item.getNewCount() > 99) {
                        tvUnread.setText("99+");
                    } else {
                        tvUnread.setText(String.valueOf(item.getNewCount()));
                    }
                    tvUnread.setVisibility(View.VISIBLE);
                } else {
                    tvUnread.setVisibility(View.GONE);
                }
                final Intent finalIntent = intent;
                msgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalIntent != null) {
                            context.startActivity(finalIntent);
                        }
                    }
                });
            } else {
                msgLayout.setVisibility(View.INVISIBLE);
            }

        }
    }
}
