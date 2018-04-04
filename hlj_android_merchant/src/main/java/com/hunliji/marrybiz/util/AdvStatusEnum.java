package com.hunliji.marrybiz.util;

import com.hunliji.marrybiz.model.experience.AdvDetail;

/**
 * Created by LuoHanLin on 14/11/24.
 * 订单的各个状态枚举
 */
public enum AdvStatusEnum {
    ALL("全部", AdvDetail.ORDER_ALL, ""),
    UN_READ("待查看", AdvDetail.ORDER_UN_READ, "unread"),
    HAVE_READ("已查看", AdvDetail.ORDER_HAVE_READ, "read"),
    READING("跟进中", AdvDetail.ORDER_COME_SHOP, "reading"),
    COME_SHOP("已到店", AdvDetail.ORDER_COME_SHOP, "is_come"),
    HAVE_CREATE("已成单", AdvDetail.ORDER_HAVE_CREATE, "formed"),
    HAVE_EXPIRED("已过期", AdvDetail.ORDER_HAVE_EXPIRED, "expired"),
    FOLLOW_UP_FAILED("跟进失败", AdvDetail.ORDER_FOLLOW_UP_FAILED, "follow-failed");


    public static final AdvStatusEnum[] exShopEnums = {ALL, UN_READ, HAVE_READ, COME_SHOP,
            HAVE_CREATE, HAVE_EXPIRED, FOLLOW_UP_FAILED};
    public static final AdvStatusEnum[] hotelEnums = {ALL, READING, COME_SHOP, HAVE_CREATE,
            FOLLOW_UP_FAILED};

    private String tabName;//订单列表对应tab名称
    private int status; // 订单状态
    private String tab; // 订单列表对应tab参数

    AdvStatusEnum(String tabName, int status, String tab) {
        this.tabName = tabName;
        this.status = status;
        this.tab = tab;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}
