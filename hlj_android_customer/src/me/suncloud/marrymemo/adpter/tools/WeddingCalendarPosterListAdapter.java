package me.suncloud.marrymemo.adpter.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingCalendarPosterViewHolder;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;

/**
 * Created by chen_bin on 2017/12/12 0012.
 */
public class WeddingCalendarPosterListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<WeddingCalendarItem> calendarItems;
    private DateTime statisticEndAt;
    private int width;
    private LayoutInflater inflater;

    public WeddingCalendarPosterListAdapter(Context context, DateTime statisticEndAt) {
        this.context = context;
        this.statisticEndAt = statisticEndAt;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCalendarItems(List<WeddingCalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
        notifyDataSetChanged();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(calendarItems);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeddingCalendarPosterViewHolder(inflater.inflate(R.layout
                        .wedding_calendar_poster_list_item,
                parent,
                false), statisticEndAt, width);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, calendarItems.get(position), position, getItemViewType(position));
    }

}
