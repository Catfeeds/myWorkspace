package com.hunliji.hljcommonlibrary.interfaces.selectable;


/**
 * Created by jinxin on 2017/3/1 0001.
 */

public class SelectMode<T> implements SelectAble<T> {
    //具体Model 比如wrok case 等等一些在list里需要单选的model
    T data;
    boolean selected = false;

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public T getMode() {
        return this.data;
    }

    @Override
    public void setMode(T t) {
        this.data = t;
    }
}
