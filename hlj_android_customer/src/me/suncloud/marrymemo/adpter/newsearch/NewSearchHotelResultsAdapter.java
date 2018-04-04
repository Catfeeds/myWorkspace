package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallHotelViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by werther on 16/12/1.
 * 酒店结果页
 */

public class NewSearchHotelResultsAdapter extends NewBaseSearchResultAdapter {

    public NewSearchHotelResultsAdapter(Context context, ArrayList<? extends Object> data) {
        super(context, data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            default:
                SmallHotelViewHolder holder = new SmallHotelViewHolder(layoutInflater.inflate(R
                                .layout.small_common_hotel_item___cv,
                        parent,
                        false));
                holder.setOnItemClickListener(onItemClickListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                SmallHotelViewHolder merchantViewHolder = (SmallHotelViewHolder) holder;
                merchantViewHolder.setShowBottomLineView(index < data.size() - 1);
                merchantViewHolder.setView(context, (Merchant) data.get(index), index, viewType);
                break;
        }
    }
}
