package com.hunliji.hljcommonlibrary.adapters;

/**
 * Created by werther on 16/9/14.
 * Recycler Adapter常用的监听器接口定义
 */
public interface OnItemClickListener<T> {
    void onItemClick(int position, T object);
}
