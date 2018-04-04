package me.suncloud.marrymemo.adpter.marry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.viewholder.MarryTaskGroupViewHolder;
import me.suncloud.marrymemo.adpter.marry.viewholder.MarryTaskViewHolder;
import me.suncloud.marrymemo.adpter.marry.viewholder.OnMarryTaskClickListener;
import me.suncloud.marrymemo.model.marry.MarryTask;

/**
 * Created by hua_rong on 2017/11/7
 * 结婚任务
 */

public class MarryTaskAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<MarryTask> marryTasks;
    private View headerView;
    private LayoutInflater inflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_GROUP_ITEM = 2;
    private OnMarryTaskClickListener onMarryTaskListener;

    public MarryTaskAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setMarryTasks(List<MarryTask> marryTasks) {
        this.marryTasks = marryTasks;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_GROUP_ITEM:
                View groupView = inflater.inflate(R.layout.marry_book_group_item, parent, false);
                return new MarryTaskGroupViewHolder(groupView);
            default:
                View itemView = inflater.inflate(R.layout.marry_task_item, parent, false);
                return new MarryTaskViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarryTaskViewHolder) {
            MarryTaskViewHolder viewHolder = (MarryTaskViewHolder) holder;
            viewHolder.setOnMarryTaskListener(onMarryTaskListener);
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        } else if (holder instanceof MarryTaskGroupViewHolder) {
            MarryTaskGroupViewHolder viewHolder = (MarryTaskGroupViewHolder) holder;
            viewHolder.setView(context,
                    getItem(position),
                    position - (headerView == null ? 0 : 1),
                    getItemViewType(position));
        }
    }

    private MarryTask getItem(int position) {
        return marryTasks.get(headerView == null ? position : position - 1);
    }

    @Override
    public int getItemCount() {
        return (marryTasks == null ? 0 : marryTasks.size()) + (headerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        }
        MarryTask marryTask = getItem(position);
        if (marryTask != null && marryTask.isGroup()) {
            return TYPE_GROUP_ITEM;
        }
        return TYPE_ITEM;
    }


    public void setOnMarryTaskListener(OnMarryTaskClickListener onMarryTaskListener) {
        this.onMarryTaskListener = onMarryTaskListener;
    }


}

