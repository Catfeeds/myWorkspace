package com.hunliji.marrybiz.model;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by luohanlin on 15/6/29.
 */
public class NewOrder implements Identifiable {

    private static final long serialVersionUID = -2064489984627358301L;
    private long id;
    private long userId;
    private long parentId;
    private String parentOrderNo;
    private long prdId;
    private int prdType;
    private double actualPrice;
    private int priceChanged;
    private int prdCount;
    private double paidMoney;
    private double aidMoney;
    private double redPacketMoney;
    private double payAllSavedMoney;  // 根据选择付款方式判断后，如果定金付款，这个值没有意义，如果全款支付，这个值就是全款礼
    private int status;
    private boolean calStatus;
    private int oldStatus;
    private int moneyStatus;
    private String orderNo;
    private boolean isGift;
    private DateTime createdAt;
    private DateTime updatedAt;
    private int payRestMsg;
    private int confirmMsg;
    private String buyerPhone;
    private String userAvatar;
    private String buyerRealName;
    private String statusStr;
    private String prdTitle;
    private String prdCoverPath;
    private double prdActualPrice;
    private double prdBasePrice;
    private DateTime weddingTime;
    private ArrayList<MerchantAction> merchantActions;
    private String userNick;
    //    private ArrayList<OrderHis> orderHises; // 从1.4.2之后的版本开始废弃掉这个字段
    private boolean isFinished;
    private boolean isInvalid; // 无效单标志位,只用来显示无效单的提示,与订单的状态没有任何关系,与关闭与否,成功与否都没有关系,只是显示
    private String invalidStr;
    private ArrayList<OrderPayHistory> payHistories; // 新版的历史记录数据,其实这个记录与之前的那个orderHis一样,
    // 对于客户端来说都没有办法直接使用,因为这个接口在设计的时候直接就是照搬网页端的数据过来,没有针对客户端的界面调整过,但为了要获取付款的几个时间节点,又不得不用,所以只用来获取时间,
    // 其他一律使用正常的接口去计算和现实
    private DateTime expiredAt;
    private double prdEarnestMoney; // 套餐信息中的定金金额
    private double earnestMoney;
    private RefundInfo refundInfo;
    private int payType; // 用户选择定金支付还是全款支付，1：全款， 2：定金，默认1
    private Rule rule; // 套餐优惠活动
    private String message; // 用户留言
    private boolean isInstallment;  //分期标志位
    private double originalEarnestMoney; // 改价钱的原定金
    private double originalActualPrice; // 改价钱的原总价
    private String redPacketInfo; // 红包的信息，有一整个红包信息red_packet_info，解析这个信息
    private boolean isOfflinePay; // 是否是线下收取尾款
    private ArrayList<Photo> protocolPhotos; // 婚礼策划专用的合同图片
    private double intentMoney; // 意向金金额
    private int version; // 订单版本
    private boolean allowEarnest; // 是否允许定金支付
    private String reason; // 拒绝接单的原因

    public static final int STATUS_WAITING_FOR_THE_PAYMENT = 10;
    public static final int STATUS_MERCHANT_ACCEPT_ORDER = 11;
    public static final int STATUS_MERCHANT_CONFIRM_SERVICE = 13;
    public static final int STATUS_WAITING_FOR_ACCEPT_ORDER = 14;
    public static final int STATUS_MERCHANT_REFUSE_ORDER = 15;

    public static final int STATUS_REFUND_REVIEWING = 20;
    public static final int STATUS_CANCEL_REFUND = 21;
    public static final int STATUS_REFUND_APPROVED = 22;
    public static final int STATUS_REFUSE_REFUND = 23;
    public static final int STATUS_REFUND_SUCCESS = 24;

    public static final int STATUS_SERVICE_COMPLETE = 90;
    public static final int STATUS_ORDER_CLOSED = 91;
    public static final int STATUS_ORDER_COMMENTED = 92;
    public static final int STATUS_ORDER_AUTO_CLOSED = 93;

    public static final int MONEY_STATUS_PAID_INTENT = 9; // 已付意向金
    public static final int MONEY_STATUS_PAID_DEPOSIT = 12; // 已付定金
    public static final int MONEY_STATUS_PAID_ALL = 13; // 已付全款

    public static final int PAY_TYPE_DEPOSIT = 2;
    public static final int PAY_TYPE_PAY_ALL = 1;
    public static final int PAY_TYPE_INTENT = 5;


    // 10等待付款
    // 11商家已接单
    // 13商家已确认服务
    // 14等待商家接单
    // 15等待拒绝接单
    // 20退款审核中
    // 21取消申请退款
    // 22退款审核通过
    // 23拒绝退款
    // 24退款成功
    // 90交易成功
    // 91交易关闭
    // 92已评论
    // 93系统自动关

    public NewOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            userId = jsonObject.optLong("user_id", 0);
            parentId = jsonObject.optLong("parent_id", 0);
            parentOrderNo = JSONUtil.getString(jsonObject, "parent_order_no");
            prdId = jsonObject.optLong("prdid", 0);
            prdType = jsonObject.optInt("prd_type", 0);
            actualPrice = jsonObject.optDouble("actual_price", 0);
            priceChanged = jsonObject.optInt("price_changed", 0);
            prdCount = jsonObject.optInt("prd_num", 0);
            paidMoney = jsonObject.optDouble("paid_money", 0);
            aidMoney = jsonObject.optDouble("aid_money", 0);
            earnestMoney = jsonObject.optDouble("earnest_money", 0);
            redPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            payAllSavedMoney = jsonObject.optDouble("pay_all_money", 0);
            status = jsonObject.optInt("status", 0);
            calStatus = jsonObject.optInt("cal_status", 0) > 0;
            oldStatus = jsonObject.optInt("old_status", 0);
            moneyStatus = jsonObject.optInt("money_status", 0);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            isGift = jsonObject.optInt("is_gift", 0) > 0;
            Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at");
            createdAt = new DateTime(date);
            date = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at");
            updatedAt = new DateTime(date);
            date = JSONUtil.getDateFromFormatLong(jsonObject, "expire_time");
            expiredAt = new DateTime(date);
            payRestMsg = jsonObject.optInt("payrest_msg", 0);
            confirmMsg = jsonObject.optInt("confirm_msg", 0);
            isFinished = jsonObject.optInt("is_finished", 0) > 0;
            payType = jsonObject.optInt("pay_type", 1);

            JSONObject buyerObject = jsonObject.optJSONObject("buyer_contact");
            if (buyerObject != null) {
                buyerRealName = JSONUtil.getString(buyerObject, "buyer_name");
                buyerPhone = JSONUtil.getString(buyerObject, "buyer_phone");
            }
            statusStr = JSONUtil.getString(jsonObject, "status_name");
            JSONObject prdObject = jsonObject.optJSONObject("product");
            if (prdObject != null) {
                prdTitle = JSONUtil.getString(prdObject, "title");
                prdCoverPath = JSONUtil.getString(prdObject, "cover_path");
                prdActualPrice = prdObject.optDouble("actual_price", 0);
                prdEarnestMoney = prdObject.optDouble("earnest_money", 0);
                isInstallment = JSONUtil.getBoolean(prdObject, "is_installment");
                prdBasePrice = prdObject.optDouble("base_price", 0);
            }
            date = JSONUtil.getDate(jsonObject, "wedding_time");
            if (date != null) {
                weddingTime = new DateTime(date);
            } else {
                weddingTime = null;
            }
            JSONArray actionArray = jsonObject.optJSONArray("merchant_actions");
            merchantActions = new ArrayList<>();

            if (actionArray != null && actionArray.length() > 0) {
                for (int i = 0; i < actionArray.length(); i++) {
                    MerchantAction merchantAction = new MerchantAction(actionArray.optJSONObject
                            (i));
                    merchantActions.add(merchantAction);
                }
            }

            JSONObject userObject = jsonObject.optJSONObject("user");
            if (userObject != null) {
                userNick = JSONUtil.getString(userObject, "nick");
                userAvatar = JSONUtil.getString(userObject, "avatar");
            }

            isInvalid = jsonObject.optBoolean("is_invalid");
            invalidStr = JSONUtil.getString(jsonObject, "invalid_string");

            payHistories = new ArrayList<>();
            JSONArray historyList = jsonObject.optJSONArray("app_history_list");
            if (historyList != null && historyList.length() > 0) {
                for (int i = 0; i < historyList.length(); i++) {
                    OrderPayHistory history = new OrderPayHistory(historyList.optJSONObject(i));
                    payHistories.add(history);
                }
            }
            JSONObject refundObj = jsonObject.optJSONObject("order_refund");
            if (refundObj != null) {
                refundInfo = new RefundInfo(refundObj);
            }
            JSONObject ruleObj = jsonObject.optJSONObject("rule");
            if (ruleObj != null) {
                rule = new Rule(ruleObj);
            }
            message = JSONUtil.getString(jsonObject, "message");
            originalEarnestMoney = jsonObject.optDouble("original_earnest_money", 0);
            originalActualPrice = jsonObject.optDouble("original_actual_price", 0);
            JSONObject redPacketObj = jsonObject.optJSONObject("red_packet_info");
            if (redPacketObj != null) {
                double moneySill = redPacketObj.optDouble("money_sill", 0); // 红包门槛
                double moneyValue = redPacketObj.optDouble("money_value", 0); // 红包金额
                if (moneySill == 0) {
                    redPacketInfo = "无限制红包";
                } else {
                    redPacketInfo = "满" + Util.formatDouble2String(moneySill) + "减" + Util
                            .formatDouble2String(
                            moneyValue);
                }
            }
            isOfflinePay = jsonObject.optInt("is_offline_pay", 0) > 0;

            protocolPhotos = new ArrayList<>();
            JSONArray imgsArray = jsonObject.optJSONArray("protocol_images");
            if (imgsArray != null && imgsArray.length() > 0) {
                for (int i = 0; i < imgsArray.length(); i++) {
                    Photo photo = GsonUtil.getGsonInstance()
                            .fromJson(imgsArray.optJSONObject(i)
                                    .toString(), Photo.class);
                    protocolPhotos.add(photo);
                }
            }
            intentMoney = jsonObject.optDouble("intent_money", 0);
            version = jsonObject.optInt("version", 0);
            allowEarnest = jsonObject.optInt("allow_earnest", 0) > 0;
            reason = jsonObject.optString("reason");
        }
    }

    public double getIntentMoney() {
        return intentMoney;
    }

    public String getRedPacketInfo() {
        return redPacketInfo;
    }

    public double getOriginalActualPrice() {
        return originalActualPrice;
    }

    public double getOriginalEarnestMoney() {
        return originalEarnestMoney;
    }

    public String getMessage() {
        return message;
    }

    public Rule getRule() {
        return rule;
    }

    public int getPayType() {
        //        // 如果money_status==12，则必定是定金支付的方式
        //        if (moneyStatus == MONEY_STATUS_PAID_DEPOSIT && payType != 5) {
        //            payType = 2;
        //        }
        return payType;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getParentId() {
        return parentId;
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public long getPrdId() {
        return prdId;
    }

    public int getPrdType() {
        return prdType;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public int getPriceChanged() {
        return priceChanged;
    }

    public int getPrdCount() {
        return prdCount;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public double getAidMoney() {
        return aidMoney;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public double getPayAllSavedMoney() {
        return payAllSavedMoney;
    }

    public int getStatus() {
        return status;
    }

    public boolean getCalStatus() {return calStatus;}

    public int getOldStatus() {
        return oldStatus;
    }

    public int getMoneyStatus() {
        return moneyStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public boolean isGift() {
        return isGift;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getPayRestMsg() {
        return payRestMsg;
    }

    public int getConfirmMsg() {
        return confirmMsg;
    }

    public String getUserNick() {
        return userNick;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public String getPrdTitle() {
        return prdTitle;
    }

    public String getPrdCoverPath() {
        return prdCoverPath;
    }

    public double getPrdActualPrice() {
        return prdActualPrice;
    }

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public ArrayList<MerchantAction> getMerchantActions() {
        if (merchantActions == null) {
            merchantActions = new ArrayList<>();
        }
        return merchantActions;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getBuyerRealName() {
        return buyerRealName;
    }

    public double getPrdBasePrice() {
        return prdBasePrice;
    }

    public void setBuyerRealName(String buyerRealName) {
        this.buyerRealName = buyerRealName;
    }

    /**
     * 从历史记录中取出用户付定金(不包含意向金)的时间
     *
     * @return
     */
    public DateTime getPaidDepositTime() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_front")) {
                    return his.getCreatedAt();
                }
            }
        }

        return null;
    }

    /**
     * 从历史记录中取出用户支付意向金的时间
     *
     * @return
     */
    public DateTime getPaidIntentMoneyTime() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_intent")) {
                    return his.getCreatedAt();
                }
            }
        }

        return null;
    }

    /**
     * 从历史记录中获取用户支付定金金额
     *
     * @return
     */
    public double getPaidDepositMoney() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_front")) {
                    return his.getMoney();
                }
            }
        }

        return 0;
    }

    /**
     * 从历史记录中取出用户是否支付过定金
     *
     * @return
     */
    public boolean hasPaidByDeposit() {
        if (payHistories != null && !payHistories.isEmpty()) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_front")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 从历史记录中获取用户付余款金额
     *
     * @return
     */
    public double getPaidRestMoney() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_rest")) {
                    return his.getMoney();
                }
            }
        }

        return 0;
    }

    /**
     * 从历史记录中获取用户付余款的时间
     *
     * @return
     */
    public DateTime getPaidRestTime() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_rest") || his.getEvent()
                        .equals("confirm_rest")) {
                    return his.getCreatedAt();
                }
            }
        }

        return null;
    }

    /**
     * 从历史记录中获取用户用户一次付全款的时间
     *
     * @return
     */
    public DateTime getPayAllTime() {
        if (payHistories != null && payHistories.size() > 0) {
            for (OrderPayHistory his : payHistories) {
                if (his.getEvent()
                        .equals("pay_all")) {
                    return his.getCreatedAt();
                }
            }
        }

        return null;
    }

    private boolean isAllowEarnest() {
        if (version == 1) {
            return earnestMoney > 0;
        } else {
            return allowEarnest;
        }
    }

    public boolean isAllowDeposit(MerchantUser user) {
        boolean isNeedWeddingTime = user.getPropertyId() != Merchant
                .PROPERTY_WEDDING_DRESS_PHOTO;
        if (isAllowEarnest()) {
            // 如果有wedding time的话，则需要判断是否在这之前的三十天之前，否则也不支持定金付款
            if (!isNeedWeddingTime || weddingTime == null) {
                return true;
            } else {
                DateTime dateTime = new DateTime();
                dateTime = dateTime.plusDays(30);
                if (dateTime.isBefore(weddingTime)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public class MerchantAction implements Identifiable {
        private static final long serialVersionUID = 7580062924070413940L;

        private String action;
        private String actionTxt;

        public MerchantAction(JSONObject jsonObject) {
            if (jsonObject != null) {
                action = JSONUtil.getString(jsonObject, "action");
                actionTxt = JSONUtil.getString(jsonObject, "txt");
            }
        }

        public String getAction() {
            return action;
        }

        public String getActionTxt() {
            return actionTxt;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public void setActionTxt(String actionTxt) {
            this.actionTxt = actionTxt;
        }

        @Override
        public Long getId() {
            return null;
        }
    }

    public class OrderHis implements Identifiable {

        private static final long serialVersionUID = 4469633197829250531L;
        private DateTime time;
        private String name;
        private double money;
        private boolean done;

        public OrderHis(JSONObject jsonObject) {
            if (jsonObject != null) {
                Date date = JSONUtil.getDate(jsonObject, "time");
                if (date != null) {
                    time = new DateTime(date);
                } else {
                    time = new DateTime();
                }
                name = JSONUtil.getString(jsonObject, "name");
                done = jsonObject.optBoolean("done");
                JSONObject dataObject = jsonObject.optJSONObject("data");
                if (dataObject != null) {
                    money = dataObject.optDouble("money", 0);
                }
            }
        }

        @Override
        public Long getId() {
            return null;
        }

        public double getMoney() {
            return money;
        }

        public DateTime getTime() {
            return time;
        }

        public String getName() {
            return name;
        }

        public boolean isDone() {
            return done;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public String getInvalidStr() {
        return invalidStr;
    }

    public ArrayList<OrderPayHistory> getPayHistories() {
        return payHistories;
    }

    public DateTime getExpiredAt() {
        return expiredAt;
    }

    public double getPrdEarnestMoney() {
        return prdEarnestMoney;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public RefundInfo getRefundInfo() {
        return refundInfo;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public boolean isOfflinePay() {
        return isOfflinePay;
    }

    public ArrayList<Photo> getProtocolPhotos() {
        return protocolPhotos;
    }

    public String getReason() {
        return reason;
    }

    /**
     * 计算用户实付金额
     *
     * @return
     */
    public double getCustomerRealPayMoney() {
        double customerRealPay;
        if (isOfflinePay()) {
            customerRealPay = getPaidMoney();
        } else {
            if (getPayType() == PAY_TYPE_INTENT) {
                // 意向金支付，需要判断是否经过定金支付
                if (getMoneyStatus() == MONEY_STATUS_PAID_DEPOSIT || (getMoneyStatus() ==
                        MONEY_STATUS_PAID_ALL && getEarnestMoney() > 0)) {
                    // 确定正处于已付定金状态，或者已经处于付完全款且定金数额大于零（如果直接通过全款支付的话，服务器会把原来的定金earnestMoney设置为0）
                    customerRealPay = getIntentMoney() + CommonUtil.positive(getEarnestMoney() -
                            getIntentMoney()) + CommonUtil.positive(
                            getActualPrice() - getEarnestMoney() - getAidMoney() -
                                    getRedPacketMoney());
                } else if (getMoneyStatus() == MONEY_STATUS_PAID_ALL && getEarnestMoney() <= 0) {
                    customerRealPay = getIntentMoney() + CommonUtil.positive(getActualPrice() -
                            getIntentMoney() - getRedPacketMoney() - getAidMoney() -
                            getPayAllSavedMoney());
                } else {
                    customerRealPay = getIntentMoney() + CommonUtil.positive(getActualPrice() -
                            getIntentMoney() - getRedPacketMoney() - getAidMoney());
                }
            } else if (getPayType() == PAY_TYPE_DEPOSIT) {
                // 定金支付
                customerRealPay = getEarnestMoney() + CommonUtil.positive(getActualPrice() -
                        getEarnestMoney() - getAidMoney() - getRedPacketMoney());
            } else {
                customerRealPay = getActualPrice() - getAidMoney() - getRedPacketMoney() -
                        getPayAllSavedMoney();
            }
        }

        return customerRealPay;
    }

    /**
     * 计算商家实收金额
     *
     * @return
     */
    public double getMerchantRealGetMoney() {
        double merchantRealGet;
        double basePriceX = 0; // 结算价和实际价格的差价
        if (getPrdBasePrice() > 0) {
            basePriceX = getActualPrice() - getPrdBasePrice();
        }

        if (isOfflinePay()) {
            merchantRealGet = getPaidMoney();
        } else {
            merchantRealGet = getCustomerRealPayMoney() + getRedPacketMoney() - basePriceX;
        }

        return merchantRealGet;
    }

    public class RefundInfo implements Identifiable {
        private long id;
        private DateTime createdDT;
        private DateTime updatedDT;
        private String reason;
        private String desc;
        private String no;
        private double payMoney;
        private double merchantPayMoney;

        public RefundInfo(JSONObject jsonObject) {
            if (jsonObject != null) {
                id = jsonObject.optLong("id", 0);
                Date createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
                createdDT = new DateTime(createdAt);
                JSONObject reasonObj = jsonObject.optJSONObject("reason");
                if (reasonObj != null) {
                    reason = JSONUtil.getString(reasonObj, "name");
                }
                desc = JSONUtil.getString(jsonObject, "desc");
                no = JSONUtil.getString(jsonObject, "order_no");
                Date updateAt = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
                updatedDT = new DateTime(updateAt);
                payMoney = jsonObject.optDouble("pay_money", 0);
                merchantPayMoney = jsonObject.optDouble("merchant_pay_money", 0);
            }

        }

        public double getMerchantPayMoney() {
            return merchantPayMoney;
        }

        public DateTime getCreatedDT() {
            return createdDT;
        }

        public DateTime getUpdatedDT() {
            return updatedDT;
        }

        public String getReason() {
            return reason;
        }

        public String getDesc() {
            return desc;
        }

        public String getNo() {
            return no;
        }

        @Override
        public Long getId() {
            return id;
        }

        public double getPayMoney() {
            return payMoney;
        }

    }
}
