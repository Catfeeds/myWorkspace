package me.suncloud.marrymemo.adpter.event;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigEventViewHolder;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.event.EventDetailActivity;

/**
 * 大图活动样式
 * Created by chen_bin on 2016/12/15 0015.
 */
public class BigEventRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<EventInfo> events;
    private LayoutInflater inflater;
    private int firstEndPosition = -1;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public BigEventRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<EventInfo> getEvents() {
        return events;
    }

    public void setEvents(List<EventInfo> events) {
        this.firstEndPosition = -1;
        this.events = events;
        notifyDataSetChanged();
    }

    public void addEvents(List<EventInfo> events) {
        if (!CommonUtil.isCollectionEmpty(events)) {
            int start = getItemCount() - getFooterViewCount();
            this.events.addAll(events);
            notifyItemRangeInserted(start, events.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(events);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                BigEventViewHolder eventViewHolder = new BigEventViewHolder(inflater.inflate(R
                                .layout.big_event_list_item___cv,
                        parent,
                        false), BigEventViewHolder.STYLE_COMMON);
                eventViewHolder.setOnItemClickListener(new OnItemClickListener<EventInfo>() {
                    @Override
                    public void onItemClick(int position, EventInfo eventInfo) {
                        if (eventInfo != null && eventInfo.getId() > 0) {
                            Intent intent = new Intent(context, EventDetailActivity.class);
                            intent.putExtra("id", eventInfo.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return eventViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof BigEventViewHolder) {
                    EventInfo eventInfo = events.get(position);
                    BigEventViewHolder eventViewHolder = (BigEventViewHolder) holder;
                    eventViewHolder.setShowBottomThickLineView(position < events.size() - 1);
                    eventViewHolder.setView(context, eventInfo, position, viewType);
                    setShowEventEndHeaderView(eventViewHolder, position);
                }
                break;
        }
    }

    private void setShowEventEndHeaderView(BigEventViewHolder eventViewHolder, int position) {
        if (firstEndPosition == -1 && eventViewHolder.isSignUpEnd()) {
            firstEndPosition = position;
        }
        eventViewHolder.setShowEventEndHeaderView(context, firstEndPosition == position, position);
    }
}