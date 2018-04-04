package com.hunliji.hljcommonlibrary.interfaces.selectable;

/**
 * 是否支持单选的Model
 * Created by jinxin on 2017/3/1 0001.
 */

public interface SelectAble<T> {
    boolean isSelected();

    void setSelected(boolean selected);

    T getMode();

    void setMode(T t);
}
