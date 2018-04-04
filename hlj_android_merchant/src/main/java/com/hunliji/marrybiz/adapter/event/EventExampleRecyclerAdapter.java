package com.hunliji.marrybiz.adapter.event;

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
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.event.EventExample;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 活动范例列表适配器
 * Created by chen_bin on 2017/2/3 0003.
 */
public class EventExampleRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<EventExample> examples;
    private LayoutInflater inflater;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public EventExampleRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setExamples(List<EventExample> examples) {
        this.examples = examples;
        notifyDataSetChanged();
    }

    public void addExamples(List<EventExample> examples) {
        if (!CommonUtil.isCollectionEmpty(examples)) {
            int start = getItemCount() - getFooterViewCount();
            this.examples.addAll(examples);
            notifyItemRangeInserted(start, examples.size());
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
        return getFooterViewCount() + CommonUtil.getCollectionSize(examples);
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
                return new EventExampleViewHolder(inflater.inflate(R.layout.event_example_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_LIST) {
            holder.setView(context, examples.get(position), position, viewType);
        }
    }

    public class EventExampleViewHolder extends BaseViewHolder<EventExample> {
        @BindView(R.id.tv_title)
        TextView tvTitle;

        public EventExampleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventExample eventExample = getItem();
                    if (eventExample != null && !TextUtils.isEmpty(eventExample.getUrl())) {
                        Intent intent = new Intent(context, HljWebViewActivity.class);
                        intent.putExtra("path", eventExample.getUrl());
                        intent.putExtra("title", eventExample.getTitle());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final EventExample eventExample,
                final int position,
                int viewType) {
            if (eventExample == null) {
                return;
            }
            tvTitle.setText(eventExample.getTitle());
        }
    }
}