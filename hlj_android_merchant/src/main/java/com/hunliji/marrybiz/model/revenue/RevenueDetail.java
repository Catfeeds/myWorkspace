package com.hunliji.marrybiz.model.revenue;

import com.hunliji.marrybiz.model.Identifiable;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/3/14.
 * 收入,支出,提现,记录的model
 */
public class RevenueDetail implements Identifiable {

    private static final long serialVersionUID = -4369713824521498844L;
    long id;
    long entityId;
    String orderNo;
    int type; // 7.订单退款;12,广告费:13,购买专业版;16,保证金充值(余额);21,聚客宝;22,红包退回(7 和 22 才有详情)
    String title; // 不同的类型对应不同的标题,不同的状态的标题也不一样
    double amount;
    DateTime dateTime;
    String memo; // 备注信息
    public static final int TYPE_WITHDRAWING = 1; // 提现中
    public static final int TYPE_WITHDRAW_SUCCESS = 2; // 提现成功
    public static final int TYPE_WITHDRAWING_FAIL = 3; // 提现失败
    public static final int TYPE_BOND_BALANCE_DETAIL = 4; // 保证金明细
    public static final int TYPE_WATER_DETAIL = 5; // 流水明细
    public static final int TYPE_ORDER_DETAIL = 6; // 订单明细

    /**
     * 订单支出常量信息
     * 2提现，4订单收入 7.订单退款 16缴纳保证金(余额);12广告费;13购买专业版;14直客宝;
     * 21聚客宝;22红包退回26开通小程序27购买注册服务
     */
    public static final int ORDER_INCOME = 4;//订单收入
    public static final int ORDER_REFUND = 7;//订单退款;
    public static final int RED_ENVELOPES_RETURNED = 22;//红包退回;


    @Override
    public Long getId() {
        return id;
    }

    public long getEntityId() {
        return entityId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getMemo() {
        return memo;
    }

    public long getDateVal() {
        String str = dateTime.getYear() + "" + dateTime.getMonthOfYear();
        long val = 0;
        try {
            val = Long.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public int getType() {
        return type;
    }


}
