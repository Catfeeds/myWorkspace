package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarHotViewHolder;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/12/26.婚车精选
 */

public class WeddingCarHotRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<WeddingCarProduct> cars;
    private Context mContext;
    private OnItemClickListener<WeddingCarProduct> onItemClickListener;
    private View footerView;

    public static final int ITEM = 1;
    public static final int FOOTER = -1;

    public WeddingCarHotRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener<WeddingCarProduct> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCars(List<WeddingCarProduct> cars) {
        this.cars = cars;
        notifyDataSetChanged();
    }

    public void addCars(List<WeddingCarProduct> cars) {
        if (cars == null) {
            return;
        }
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.addAll(cars);
        notifyItemRangeInserted(this.cars.size() - cars.size(), cars.size());
    }

    public void clear() {
        if (cars == null) {
            return;
        }
        cars.clear();
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new WeddingCarHotViewHolder(parent, onItemClickListener);
            case FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ITEM:
                holder.setView(mContext, getItem(position), position, type);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (cars == null || cars.isEmpty()) {
            return 0;
        }
        if (footerView != null) {
            return cars.size() + 1;
        }
        return cars.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (cars != null && position < cars.size()) {
            return ITEM;
        }
        return FOOTER;
    }

    private WeddingCarProduct getItem(int position) {
        if (cars != null && position < cars.size()) {
            return cars.get(position);
        }
        return null;
    }
}
