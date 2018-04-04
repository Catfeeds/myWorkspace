package me.suncloud.marrymemo.adpter.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.tools.viewholder.WeddingGuestViewHolder;
import me.suncloud.marrymemo.model.tools.WeddingGuest;


/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingGuestListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<WeddingGuest> guests;
    private LayoutInflater inflater;
    private WeddingGuestViewHolder.OnDeleteGuestListener onDeleteGuestListener;
    private WeddingGuestViewHolder.OnSubtractGuestLister onSubtractGuestLister;
    private WeddingGuestViewHolder.OnPlusGuestListener onPlusGuestListener;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public WeddingGuestListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setGuests(List<WeddingGuest> guests) {
        this.guests = guests;
        notifyDataSetChanged();
    }

    public void addGuests(List<WeddingGuest> guests) {
        if (!CommonUtil.isCollectionEmpty(guests)) {
            int start = getItemCount() - getFooterViewCount();
            this.guests.addAll(guests);
            notifyItemRangeInserted(start, guests.size());
        }
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnDeleteGuestListener(
            WeddingGuestViewHolder.OnDeleteGuestListener onDeleteGuestListener) {
        this.onDeleteGuestListener = onDeleteGuestListener;
    }

    public void setOnSubtractGuestLister(
            WeddingGuestViewHolder.OnSubtractGuestLister onSubtractGuestLister) {
        this.onSubtractGuestLister = onSubtractGuestLister;
    }

    public void setOnPlusGuestListener(
            WeddingGuestViewHolder.OnPlusGuestListener onPlusGuestListener) {
        this.onPlusGuestListener = onPlusGuestListener;
    }

    @Override

    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(guests);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                WeddingGuestViewHolder guestViewHolder = new WeddingGuestViewHolder(inflater
                        .inflate(
                        R.layout.wedding_guest_list_item,
                        parent,
                        false));
                guestViewHolder.setOnDeleteGuestListener(onDeleteGuestListener);
                guestViewHolder.setOnSubtractGuestLister(onSubtractGuestLister);
                guestViewHolder.setOnPlusGuestListener(onPlusGuestListener);
                return guestViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, guests.get(index), index, viewType);
                break;
        }
    }


}
