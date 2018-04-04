package com.hunliji.hljcommonlibrary.adapters;

/**
 * Created by chen_bin on 16/9/14.
 * Recycler Adapter常用的监听器接口定义
 */
public interface OnItemLongClickListener<T> {
    void onItemLongClick(int position, T object);
}
