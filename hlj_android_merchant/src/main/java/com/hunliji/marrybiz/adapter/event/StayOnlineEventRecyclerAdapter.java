package com.hunliji.marrybiz.adapter.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 待上线活动列表适配器
 * Created by chen_bin on 2017/2/3 0003.
 */
public class StayOnlineEventRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<EventInfo> events;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public StayOnlineEventRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setEvents(List<EventInfo> events) {
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
                return new StayOnlineEventViewHolder(inflater.inflate(R.layout
                                .stay_online_event_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_LIST) {
            holder.setView(context, events.get(position), position, viewType);
        }
    }

    public class StayOnlineEventViewHolder extends BaseViewHolder<EventInfo> {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.line_layout)
        View lineLayout;

        public StayOnlineEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventInfo eventInfo = getItem();
                    if (eventInfo != null && !TextUtils.isEmpty(eventInfo.getLink())) {
                        Intent intent = new Intent(context, HljWebViewActivity.class);
                        intent.putExtra("path", eventInfo.getLink());
                        intent.putExtra("title", eventInfo.getTitle());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final EventInfo eventInfo,
                final int position,
                int viewType) {
            if (eventInfo == null) {
                return;
            }
            lineLayout.setVisibility(position < events.size() - 1 ? View.VISIBLE : View.GONE);
            tvTitle.setText(eventInfo.getTitle());
            if (eventInfo.getPublishTime() == null || eventInfo.getSignUpEndTime() == null) {
                tvTime.setText("");
            } else {
                tvTime.setText(context.getString(R.string.label_sign_up_time,
                        eventInfo.getPublishTime()
                                .toString(context.getString(R.string.format_date_type11,
                                        Locale.getDefault())),
                        eventInfo.getSignUpEndTime()
                                .toString(context.getString(R.string.format_date_type11,
                                        Locale.getDefault()))));
            }
        }
    }
}
