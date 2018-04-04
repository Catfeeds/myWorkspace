package me.suncloud.marrymemo.adpter.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestContactIndexViewHolder;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestContactViewHolder;
import me.suncloud.marrymemo.model.tools.WeddingGuest;

/**
 * 导入宾客adapter
 * Created by chen_bin on 2017/11/22 0022.
 */
public class WeddingGuestContactListAdapter extends RecyclerView.Adapter<BaseViewHolder>
        implements StickyRecyclerHeadersAdapter<BaseViewHolder> {
    private Context context;
    private List<WeddingGuest> guests;
    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;

    public WeddingGuestContactListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setGuests(List<WeddingGuest> guests) {
        this.guests = guests;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(guests);
    }

    @Override
    public long getHeaderId(int position) {
        return guests.get(position)
                .getFirstChar()
                .charAt(0);
    }

    @Override
    public BaseViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new WeddingGuestContactIndexViewHolder(inflater.inflate(R.layout
                        .wedding_guest_contact_list_index,
                parent,
                false));
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, guests.get(position), position, getItemViewType(position));
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WeddingGuestContactViewHolder holder = new WeddingGuestContactViewHolder(inflater.inflate
                (R.layout.wedding_guest_contact_list_item,
                parent,
                false));
        holder.setOnItemClickListener(onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof WeddingGuestContactViewHolder) {
            WeddingGuestContactViewHolder contactViewHolder = (WeddingGuestContactViewHolder)
                    holder;
            WeddingGuest guest = guests.get(position);
            WeddingGuest lastGuest = null;
            if (position > 0) {
                lastGuest = guests.get(position - 1);
            }
            contactViewHolder.setView(context, guest, position, getItemViewType(position));
            contactViewHolder.setShowTopLineView(lastGuest != null && TextUtils.equals(lastGuest
                            .getFirstChar(),
                    guest.getFirstChar()));
        }
    }

    public int getPositionForSection(String str) {
        for (int i = 0; i < getItemCount(); i++) {
            if (str.equals(guests.get(i)
                    .getFirstChar())) {
                return i;
            }
        }
        return -1;
    }

}
