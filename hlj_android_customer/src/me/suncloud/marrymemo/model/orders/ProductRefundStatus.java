package me.suncloud.marrymemo.model.orders;

/**
 * Created by werther on 16/4/13.
 */
public class ProductRefundStatus {
    /**
     * 未申请退款
     */
    public static final int NO_REFUND = 0;
    /**
     * 后台发起退款中
     * 1,2,3 状态是老版的退款流程,其他是新的退款流程状态,0是共有的默认未退款的状态
     */
    public static final int OLD_REFUND_PROCESSING = 1;
    /**
     * 后台退款成功
     * 1,2,3 状态是老版的退款流程,其他是新的退款流程状态,0是共有的默认未退款的状态
     */
    public static final int OLD_REFUND_SUCCESS = 2;
    /**
     * 后台退款失败
     * 1,2,3 状态是老版的退款流程,其他是新的退款流程状态,0是共有的默认未退款的状态
     */
    public static final int OLD_REFUND_FAIL = 3;
    /**
     * 等待处理退款
     */
    public static final int MERCHANT_HANDLING_REFUND = 4;
    /**
     * 等待处理退货
     */
    public static final int MERCHANT_HANDLING_RETURN = 5;
    /**
     * 商家拒绝退款
     */
    public static final int REFUND_DECLINED = 6;
    /**
     * 商家拒绝退货
     */
    public static final int RETURN_DECLINED = 7;
    /**
     * 等待退货
     */
    public static final int BUYER_RETURN_PRODUCT = 8;
    /**
     * 等待商家确认
     */
    public static final int MERCHANT_CONFIRMING = 9;
    /**
     * 商家未收到货
     */
    public static final int MERCHANT_PRODUCT_UNRECEIVED = 10;
    /**
     * 退款/退货完成
     */
    public static final int REFUND_COMPLETE = 11;
    /**
     * 退款/退货取消
     */
    public static final int REFUND_CANCELED = 12;
    /**
     * 自动关闭
     */
    public static final int REFUND_CLOSED = 13;
}
