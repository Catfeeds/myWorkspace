package com.hunliji.marrybiz.util;

/**
 * Created by LuoHanLin on 14/11/24.
 * 订单的各个状态枚举
 */
public enum OrderStatusEnum {
    INIT("init", "用户已下单", "接单"),
    CONFIRMED("confirmed", "等待用户付款", "修改金额"),
    DEPOSITED("deposited", "用户已付定金", ""),
    PAYED("paid", "已付款", ""),
    FULFILLED("fulfilled", "服务已完成", ""),
    SUCCEED("succeed", "交易成功", ""),
    CLOSED("closed", "已关闭", ""),
    COMMENTED("commented", "已评价", "查看评价"),
    REFUNDING("refunding", "退款处理中", "");


    private String status; // 状态字符串, 与服务器返回的数据一一对应
    private String statusDescription; // 订单状态的描述
    private String action; // 订单的状态对应商家可以进行的操作

    OrderStatusEnum(String desc1, String desc2, String desc3) {
        this.status = desc1;
        this.statusDescription = desc2;
        this.action = desc3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
