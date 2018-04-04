package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WeddingCompereAvatarViewHolder;

/**
 * Created by hua_rong on 2017/8/1.
 * 婚礼司仪 头部
 */

public class WeddingCompereHeaderAdapter extends RecyclerView.Adapter<BaseViewHolder<Merchant>> {

    private Context context;
    private List<Merchant> merchantList;
    private LayoutInflater inflater;

    public WeddingCompereHeaderAdapter(Context context, List<Merchant> merchantList) {
        this.context = context;
        this.merchantList = merchantList;
        inflater = LayoutInflater.from(context);
    }

    public void setMerchantList(List<Merchant> merchantList) {
        this.merchantList = merchantList;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Merchant> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.property_wedding_compere_header_item,
                parent,
                false);
        return new WeddingCompereAvatarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Merchant> holder, int position) {
        if (holder instanceof WeddingCompereAvatarViewHolder) {
            WeddingCompereAvatarViewHolder viewHolder = (WeddingCompereAvatarViewHolder) holder;
            viewHolder.setView(context,
                    merchantList.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return merchantList == null ? 0 : merchantList.size();
    }

}
