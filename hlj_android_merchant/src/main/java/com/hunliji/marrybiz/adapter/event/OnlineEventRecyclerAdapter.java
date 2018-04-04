package com.hunliji.marrybiz.adapter.event;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.event.EventWallet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 报名列表
 * Created by chen_bin on 2017/3/8 0008.
 */
public class OnlineEventRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<EventInfo> events;
    private EventWallet eventWallet;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private OnItemClickListener onItemClickListener;

    public OnlineEventRecyclerAdapter(Context context) {
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

    public void setEventWallet(EventWallet eventWallet) {
        this.eventWallet = eventWallet;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
                return new OnlineEventViewHolder(inflater.inflate(R.layout.online_event_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, events.get(position), position, viewType);
                break;
        }
    }

    public class OnlineEventViewHolder extends BaseViewHolder<EventInfo> {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time_title)
        TextView tvTimeTitle;
        @BindView(R.id.tv_point_limit_hint)
        TextView tvPointLimitHint;
        @BindView(R.id.tv_sign_up_count)
        TextView tvSignUpCount;
        @BindView(R.id.tv_sign_up_new_count)
        TextView tvSignUpNewCount;
        @BindView(R.id.line_layout)
        View lineLayout;

        public OnlineEventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        EventInfo eventInfo = getItem();
                        if (eventInfo != null && eventInfo.getId() > 0) {
                            eventInfo.setSignUpNewCount(0);
                            tvSignUpNewCount.setVisibility(View.GONE);
                            onItemClickListener.onItemClick(getAdapterPosition(), eventInfo);
                        }
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
            boolean isSignUpEnd = false;
            if (TextUtils.isEmpty(eventInfo.getTitle())) {
                tvTitle.setText("");
            } else if (eventInfo.getSignUpEndTime() != null) {
                SpannableStringBuilder builder = new SpannableStringBuilder(" " + eventInfo
                        .getTitle());
                Drawable drawable;
                if (!eventInfo.isSignUpEnd()) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.icon_sign_up_tag);
                } else {
                    isSignUpEnd = true;
                    drawable = ContextCompat.getDrawable(context, R.drawable.icon_sign_up_end_tag);
                }
                drawable.setBounds(0,
                        0,
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                builder.setSpan(new HljImageSpan(drawable), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            } else {
                tvTitle.setText(eventInfo.getTitle());
            }
            if (eventWallet != null && eventWallet.getTickets() <= 0 && eventWallet.getPoints()
                    <= 0 && !isSignUpEnd && eventInfo.isShowPointLimit()) {
                tvTimeTitle.setVisibility(View.GONE);
                tvPointLimitHint.setVisibility(View.VISIBLE);
            } else {
                tvPointLimitHint.setVisibility(View.GONE);
                tvTimeTitle.setVisibility(View.VISIBLE);
                tvTimeTitle.setText(TextUtils.isEmpty(eventInfo.getShowTimeTitle()) ? "" :
                        context.getString(
                        R.string.label_show_time_title,
                        eventInfo.getShowTimeTitle()));
            }
            tvSignUpCount.setText(String.valueOf(eventInfo.getSignUpCount()));
            if (eventInfo.getSignUpNewCount() <= 0) {
                tvSignUpNewCount.setVisibility(View.GONE);
            } else {
                tvSignUpNewCount.setVisibility(View.VISIBLE);
                tvSignUpNewCount.setText(context.getString(R.string.label_sign_up_new_count,
                        eventInfo.getSignUpNewCount()));
            }
        }
    }
}
