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
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingCalendarItemViewHolder;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;

/**
 * Created by chen_bin on 2017/12/12 0012.
 */
public class WeddingCalendarItemListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private List<WeddingCalendarItem> calendarItems;
    private DateTime statisticEndAt;

    private WeddingCalendarItemViewHolder.OnCollectListener onCollectListener;
    private WeddingCalendarItemViewHolder.OnShareListener onShareListener;
    private String brandTagImagePath;

    public WeddingCalendarItemListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setBrandTagImagePath(String brandTagImagePath) {
        this.brandTagImagePath = brandTagImagePath;
    }

    public List<WeddingCalendarItem> getCalendarItems() {
        return calendarItems;
    }

    public void setCalendarItems(List<WeddingCalendarItem> calendarItems) {
        this.calendarItems = calendarItems;
        notifyDataSetChanged();
    }

    public void setStatisticEndAt(DateTime statisticEndAt) {
        this.statisticEndAt = statisticEndAt;
    }

    public void setOnCollectListener(
            WeddingCalendarItemViewHolder.OnCollectListener onCollectListener) {
        this.onCollectListener = onCollectListener;
    }

    public void setOnShareListener(WeddingCalendarItemViewHolder.OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(calendarItems);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WeddingCalendarItemViewHolder holder = new WeddingCalendarItemViewHolder(inflater.inflate
                (R.layout.wedding_calendar_item_list_item,
                parent,
                false), statisticEndAt, brandTagImagePath);
        holder.setOnCollectListener(onCollectListener);
        holder.setOnShareListener(onShareListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, calendarItems.get(position), position, getItemViewType(position));
    }

}
