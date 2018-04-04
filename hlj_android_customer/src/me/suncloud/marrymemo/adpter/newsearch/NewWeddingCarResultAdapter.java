package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWeddingCarViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by hua_rong on 2018/1/8
 * 婚车结果页
 */

public class NewWeddingCarResultAdapter extends NewBaseSearchResultAdapter {

    public NewWeddingCarResultAdapter(Context context, ArrayList<? extends Object> data) {
        super(context, data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            default:
                SmallWeddingCarViewHolder viewHolder = new SmallWeddingCarViewHolder
                        (layoutInflater.inflate(
                        R.layout.small_wedding_car_item___cv,
                        parent,
                        false));
                viewHolder.setOnItemClickListener(onItemClickListener);
                return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                if (holder instanceof SmallWeddingCarViewHolder) {
                    SmallWeddingCarViewHolder viewHolder = (SmallWeddingCarViewHolder) holder;
                    viewHolder.setShowBottomLineView(index < data.size() - 1);
                    viewHolder.setView(context,
                            (WeddingCarProduct) data.get(index),
                            index,
                            viewType);
                }
                break;
        }
    }

}
