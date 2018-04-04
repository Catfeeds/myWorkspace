package com.hunliji.hljcommonviewlibrary.models;

import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by wangtao on 2017/9/28.
 */

public class GroupAdapterHeader {

    private int groupType;
    private String title;
    private String describe;
    private boolean clickable;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private boolean divider;

    public GroupAdapterHeader(
            int groupType,
            String title,
            String describe,
            boolean clickable,
            int left,
            int top,
            int right,
            int bottom,boolean divider) {
        this.groupType=groupType;
        this.title = title;
        this.describe = describe;
        this.clickable = clickable;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.divider=divider;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setLayoutPadding(View layout) {
        layout.setPadding(CommonUtil.dp2px(layout.getContext(), left),
                CommonUtil.dp2px(layout.getContext(), top),
                CommonUtil.dp2px(layout.getContext(), right),
                CommonUtil.dp2px(layout.getContext(), bottom));
    }

    public boolean showDivider() {
        return divider;
    }

    public int getGroupType() {
        return groupType;
    }
}
