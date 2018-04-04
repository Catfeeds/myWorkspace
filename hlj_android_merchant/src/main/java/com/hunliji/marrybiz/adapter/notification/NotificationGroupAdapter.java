package com.hunliji.marrybiz.adapter.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.adapter.notification.viewholder.NotificationGroupViewHolder;
import com.hunliji.marrybiz.model.notification.NotificationGroupItem;

import java.util.List;

/**
 * Created by wangtao on 2017/8/18.
 */

public class NotificationGroupAdapter extends RecyclerView
        .Adapter<BaseViewHolder<NotificationGroupItem>> {

    private List<NotificationGroupItem> groupItems;
    private Context context;

    public NotificationGroupAdapter(Context context) {
        this.context = context;
    }

    public void setGroupItems(List<NotificationGroupItem> groupItems) {
        this.groupItems = groupItems;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<NotificationGroupItem> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new NotificationGroupViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<NotificationGroupItem> holder, int position) {
        holder.setView(context,getItem(position),position,getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return groupItems==null?0:groupItems.size();
    }


    public NotificationGroupItem getItem(int position) {
        if(CommonUtil.isCollectionEmpty(groupItems)){
            return null;
        }
        return groupItems.get(position);
    }
}
