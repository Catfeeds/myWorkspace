package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.adpter.community.viewholder.WeddingBibleViewHolder;
import me.suncloud.marrymemo.model.community.WeddingBible;

/**
 * Created by chen_bin on 2018/3/19 0019.
 */
public class WeddingBiblesAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<WeddingBible> bibles;

    public WeddingBiblesAdapter(Context context) {
        this.context = context;
    }

    public void setBibles(List<WeddingBible> bibles) {
        this.bibles = bibles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(bibles);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeddingBibleViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, bibles.get(position), position, getItemViewType(position));
    }

}
