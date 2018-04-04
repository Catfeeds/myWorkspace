package com.hunliji.hljimagelibrary.adapters.viewholders;

import android.content.Context;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

/**
 * Created by chen_bin on 2017/3/18 0018.
 */
public abstract class BaseDraggableItemViewHolder<T> extends AbstractDraggableItemViewHolder {

    private T item;
    private int itemPosition;

    public BaseDraggableItemViewHolder(View itemView) {
        super(itemView);
    }

    public void setView(Context mContext, T item, int position, int viewType) {
        this.itemPosition = position;
        this.item = item;
        try {
            setViewData(mContext, item, position, viewType);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public T getItem() {
        return item;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    protected abstract void setViewData(Context mContext, T item, int position, int viewType);

}