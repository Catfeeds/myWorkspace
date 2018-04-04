package com.hunliji.hljpaymentlibrary.models;

import android.support.annotation.Nullable;

/**
 * Created by Suncloud on 2016/8/15.
 */
public class PayRxEvent {

    private RxEventType type;
    private Object object;

    public enum RxEventType {
        PAY_SUCCESS, //付款成功
        PAY_FAIL, //付款失败
        PAY_CANCEL, // 付款取消
        RESET_PASSWORD, //找回密码验证通过

        BIND_BANK_CARD_SUCCESS,//绑卡成功
        ADD_BASIC_USER_INFO_SUCCESS, //添加基本信息成功
        AUTHORIZE_CREDIT_CARD_BILL_SUCCESS, //信用卡账单认证成功
        AUTHORIZE_DEPOSIT_CARD_BILL_SUCCESS,//储蓄卡认证成功
        AUTHORIZE_HOUSE_FUND_SUCCESS, //住房公积金认证成功
        HAD_AUTHORIZED, //已经授信认证过
        AUTHORIZE_CANCEL, //基础认项取消

        INIT_LIMIT_CLOSE, //基础认证结果页关闭
        INCREASE_LIMIT_CLOSE, //提额页关闭
        LIMIT_CONTINUE_PAY, //继续支付
        REPAY_SUCCESS,//还款成功

        ORDER_SUBMIT_SUCCESS, // 51收单成功
        CREDIT_NOT_ENOUGH, //3024, 51返回的授信额度不足，意味着： 真实额度 < amount < 显示额度
        CREDIT_NOT_ENOUGH2, // 900001, 婚礼纪服务器返回的额度不足，意味着： amount > 显示额度
        INSTALLMENT_PAY_SUCCESS, // 分期提交成功，与付款成功不完全一样，所以区别对待
        AUTHORIZE_NOT_PASSED, // 风控校验未通过
        PAY_BY_OTHERS, // 使用其他方式支付
    }

    /**
     * 构造一个用于PayRxEvent消息传递的参数
     *
     * @param type   消息类型,MessageEvent.EventType里面取
     * @param object 传递的消息本身
     */
    public PayRxEvent(RxEventType type, @Nullable Object object) {
        this.type = type;
        this.object = object;
    }

    public RxEventType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
