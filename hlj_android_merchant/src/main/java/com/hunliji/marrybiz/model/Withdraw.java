package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by luohanlin on 15/7/13.
 */
public class Withdraw implements Identifiable {
    private static final long serialVersionUID = 3482348323313065160L;

    private long id;
    private double pendingMoney; // 申请提现总金额
    private double totalWithdrawMoney; // 经过计算扣除等..后的实际提现金额
    private double refundMoney;
    private double compensationMoney;
    private double returnRedPacketMoney;
    private int destType; // 提现到哪里 1:银行卡,2支付宝,3.信用卡
    private String tradeNo; // 交易号
    private int status;
    private long merchantId;
    private String statusStr;
    private DateTime createdAt;
    private DateTime updatedAt;

    private ArrayList<WithdrawSub> subs;
    private ArrayList<HisRec> hisRecs;

    public Withdraw(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            pendingMoney = jsonObject.optDouble("pending_money", 0);
            totalWithdrawMoney = jsonObject.optDouble("total_money", 0);
            refundMoney = jsonObject.optDouble("refund_money", 0);
            compensationMoney = jsonObject.optDouble("indemnity_money", 0);
            returnRedPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            status = jsonObject.optInt("status");
            destType = jsonObject.optInt("dest_type", 0);
            tradeNo = JSONUtil.getString(jsonObject, "trade_no");
            merchantId = jsonObject.optLong("merchant_id", 0);
            Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at");
            createdAt = new DateTime(date);
            Date date2 = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at");
            updatedAt = new DateTime(date2);
            switch (status) {
                case 0:
                    statusStr = "提现中";
                    break;
                case 1:
                    statusStr = "提现成功";
                    break;
            }

            subs = new ArrayList<>();
            JSONArray subArray = jsonObject.optJSONArray("child_flows");
            if (subArray != null && subArray.length() > 0) {
                for (int i = 0; i < subArray.length(); i++) {
                    WithdrawSub sub = new WithdrawSub(subArray.optJSONObject(i));
                    subs.add(sub);
                }
            }

            hisRecs = new ArrayList<>();
            JSONArray hisArray = jsonObject.optJSONArray("history_records");
            if (hisArray != null && hisArray.length() > 0) {
                for (int i = 0; i < hisArray.length(); i++) {
                    HisRec hisRec = new HisRec(hisArray.optJSONObject(i));
                    hisRecs.add(hisRec);
                }
            }

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public double getPendingMoney() {
        return pendingMoney;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public long getDateVal() {
        String str = createdAt.getYear() + "" + createdAt.getMonthOfYear();
        long val = 0;
        try {
            val = Long.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public double getRefundMoney() {
        return refundMoney;
    }

    public double getCompensationMoney() {
        return compensationMoney;
    }

    public double getTotalWithdrawMoney() {
        return totalWithdrawMoney;
    }

    public int getDestType() {
        return destType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public ArrayList<WithdrawSub> getSubs() {
        return subs;
    }

    public ArrayList<HisRec> getHisRecs() {
        return hisRecs;
    }

    public double getReturnRedPacketMoney() {
        return returnRedPacketMoney;
    }

    public class WithdrawSub implements Identifiable {
        private static final long serialVersionUID = 3405850705453479593L;
        private long id;
        private String orderNo;
        private long merchantWithdrawId;
        private long withdrawId; // 提款父订单的id
        private long orderSubId;
        private double money;
        private DateTime createdAt;
        private String payType;

        public WithdrawSub(JSONObject jsonObject) {
            if (jsonObject != null) {
                id = jsonObject.optLong("id", 0);
                orderNo = JSONUtil.getString(jsonObject, "order_no");
                merchantWithdrawId = jsonObject.optLong("merchant_withdraw_id", 0);
                withdrawId = jsonObject.optLong("cashflow_id", 0);
                money = jsonObject.optDouble("money", 0);
                Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at");
                createdAt = new DateTime(date);
                orderSubId = jsonObject.optLong("order_sub_id", 0);
                payType = JSONUtil.getString(jsonObject, "pay_type");
            }
        }

        @Override
        public Long getId() {
            return id;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public long getMerchantWithdrawId() {
            return merchantWithdrawId;
        }

        public long getWithdrawId() {
            return withdrawId;
        }

        public long getOrderSubId() {
            return orderSubId;
        }

        public double getMoney() {
            return money;
        }

        public DateTime getCreatedAt() {
            return createdAt;
        }

        public String getPayType() {
            return payType;
        }

        public long getDateVal() {
            String str = createdAt.getYear() + "" + createdAt.getMonthOfYear();
            long val = 0;
            try {
                val = Long.valueOf(str);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return val;
        }
    }

    public class HisRec implements Identifiable {
        private static final long serialVersionUID = 5145193020642614376L;

        private long id;
        private DateTime createdAt;
        private int status;
        private String statusStr;

        public HisRec(JSONObject jsonObject) {
            if (jsonObject != null) {
                id = jsonObject.optLong("id", 0);
                status = jsonObject.optInt("status", 0);
                switch (status) {
                    case 0:
                        statusStr = "提交申请";
                        break;
                    case 1:
                        statusStr = "婚礼纪处理中";
                        break;
                    case 2:
                        statusStr = "提现成功";
                        break;
                }
                Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at");
                createdAt = new DateTime(date);
            }
        }

        @Override
        public Long getId() {
            return id;
        }

        public DateTime getCreatedAt() {
            return createdAt;
        }

        public int getStatus() {
            return status;
        }

        public String getStatusStr() {
            return statusStr;
        }
    }


}
