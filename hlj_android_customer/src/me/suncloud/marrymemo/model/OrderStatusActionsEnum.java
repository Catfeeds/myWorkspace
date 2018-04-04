package me.suncloud.marrymemo.model;

/**
 * Created by luohanlin on 15/6/6.
 * 描述一个订单的状态和这个订单可以执行的操作混杂在一起，那我命名的时候也就只能同时指明
 * 这个枚举既有订单的状态，又有其可以执行的操作
 * 一定的行为是对应的操作按钮的样式是不一样的，所以用字段prime硬性对顶表明区分
 */
public enum OrderStatusActionsEnum {
    ONPAY(1, "付款", true),
    CANCEL(2, "取消订单", false),
    REFUND(3, "申请退款", false),
    SUCCESS(4, "确认服务", true),
    ONCOMENT(5, "评论", true),
    ONPAY_REST(6, "支付余款", true),
    ONCONTACT(7, "联系商家", false),
    ONCHAT(8, "私信商家", false),
    ONCALL(9, "电话商家", false),
    CANCEL_REFUND(10, "撤销退款申请", false);

    public final int status;
    public final String action;
    public final boolean prime;

    OrderStatusActionsEnum(int status, String action, boolean prime) {
        this.status = status;
        this.action = action;
        this.prime = prime;
    }
}
