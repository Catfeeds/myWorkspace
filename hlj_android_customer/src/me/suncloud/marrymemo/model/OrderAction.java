package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/7/18.
 */
public class OrderAction implements Serializable {

    /**
     *
     ONPAY(1, "付款", true),
     CANCEL(2, "取消订单", false),
     REFUND(3, "申请退款", false),
     SUCCESS(4, "确认服务", true),
     ONCOMENT(5, "评论", true),
     ONPAY_REST(6, "支付余款", true),
     ONCONTACT(7, "联系商家", false),
     ONCHAT(8, "私信商家", false),
     ONCALL(9, "电话商家", false),
     CANCEL_REFUND(10, "撤销退款申请", false);*/

    public static final int ONPAY = 1;
    public static final int CANCEL = 2;
    public static final int REFUND = 3;
    public static final int SUCCESS = 4;
    public static final int ONCOMENT = 5;
    public static final int ONPAY_REST = 6;
    public static final int ONCONTACT = 7;
    public static final int ONCHAT = 8;
    public static final int ONCALL = 9;
    public static final int CANCEL_REFUND = 10;


    private int status;
    private String action;
    private boolean prime;

    public OrderAction(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.status = jsonObject.optInt("action");
            this.action = JSONUtil.getString(jsonObject, "txt");
            this.prime = status == ONPAY || status == SUCCESS || status == ONCOMENT || status == ONPAY_REST;
        }
    }

    public int getStatus() {
        return status;
    }

    public String getAction() {
        return action;
    }

    public boolean isPrime() {
        return prime;
    }
}
