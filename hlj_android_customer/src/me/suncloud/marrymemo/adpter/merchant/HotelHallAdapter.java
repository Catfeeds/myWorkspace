package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;

import java.util.List;

import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelHallViewHolder;

/**
 * Created by wangtao on 2017/10/10.
 */

public class HotelHallAdapter extends RecyclerView.Adapter<BaseViewHolder<HotelHall>> {

    private Context context;
    private List<HotelHall> halls;
    private OnItemClickListener<HotelHall> onItemClickListener;
    private Merchant merchant;

    public HotelHallAdapter(
            Context context, Merchant merchant, OnItemClickListener<HotelHall> onItemClickListener) {
        this.context = context;
        this.halls = merchant.getHotel().getHotelHalls();
        this.onItemClickListener = onItemClickListener;
        this.merchant=merchant;
    }

    @Override
    public BaseViewHolder<HotelHall> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HotelHallViewHolder(parent,onItemClickListener,merchant.getId());
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<HotelHall> holder, int position) {
        holder.setView(context,halls.get(position),position,getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return halls==null?0:halls.size();
    }
}
