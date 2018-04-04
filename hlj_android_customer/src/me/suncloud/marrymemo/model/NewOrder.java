package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.model.orders.Installment;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * Created by luohanlin on 15/5/27.
 */
public class NewOrder implements Identifiable {

    private long id;
    private String title;
    private String coverPath;
    private String describe;
    private String statusStr;
    private ArrayList<OrderAction> actions;
    private int commodityType;
    private String phoneNum;
    private long merchantId;
    private String merchantName;
    private double actualPrice;
    private double salePrice;
    private double marketPrice;
    private long propertyId;
    private boolean allowEarnest;
    private boolean cheaperIfAllIn;
    private double depositPercent;
    private long prdId;
    private int prdNum;
    private int prdType;
    private double payPrice;
    private double payAllMoney;
    private double payDepositMoney;
    private double payAidMoney;
    private String orderNum;
    private String merchantLogoPath;
    private long merchantUserId;
    private ArrayList<String> contactPhones;
    private int moneyStatus; // 这个订单的付款状态，12：已付定金， 13：付款完成
    private double refundPrice;
    private String refundReason;
    private String refuseReason;
    private String refundDesc;
    private Date createdAt;
    private Date updatedAt;
    private String refundOrderNum;
    private boolean isGift;
    private double paidMoney;
    private int status;
    private double earnestMoney; //定金
    private String orderGift; //下单礼
    private String payAllGift; //全款礼
    private int version;
    private boolean bondSign;
    private boolean isFinished;
    private Date expiredTime;
    /**
     * 特殊情况,由于新的定制套餐的退款订单信息需要归入之前的退款单列表
     * 为简化逻辑,所以继续使用NewOrder的model,新增一个标志位用于识别退款单类型
     * 新增一个构造方法用于将新的定制套餐退款单的数据填入NewOrder的model中
     */
    private boolean isCustomOrderRefund;

    private boolean isInstallment;  //分期标志位
    private Installment installment; //分期结构
    private boolean isIntentPay; // 服务订单里面的付款类型，主要用于显示意向金订单


    public NewOrder(JSONObject jsonObject) {
        //      这是退款订单的实例化方法，正常订单用下面的
        if (jsonObject != null) {
            id = jsonObject.optLong("old_sub_order_id");
            if (!jsonObject.isNull("item")) {
                JSONObject itemJson = jsonObject.optJSONObject("item");
                if (itemJson != null) {
                    prdId = itemJson.optLong("id", 0);
                    title = JSONUtil.getString(itemJson, "title");
                    coverPath = JSONUtil.getString(itemJson, "cover_path");

                    isInstallment = JSONUtil.getBoolean(itemJson, "is_installment");
                    isIntentPay = JSONUtil.getBoolean(itemJson, "is_intent");
                    JSONObject installmentObject = itemJson.optJSONObject("installment");
                    if (installmentObject != null) {
                        installment = new Installment(installmentObject);
                    }
                }
            }
            prdId = jsonObject.optLong("prdid", prdId);
            prdNum = jsonObject.optInt("prd_num", 0);
            prdType = jsonObject.optInt("prd_type", 0);
            actualPrice = jsonObject.optDouble("actual_price", 0);
            salePrice = jsonObject.optDouble("sale_price", 0);
            paidMoney = jsonObject.optDouble("money", 0);
            refundPrice = jsonObject.optDouble("pay_money", 0);
            orderNum = JSONUtil.getString(jsonObject, "sub_order_no");
            refundOrderNum = JSONUtil.getString(jsonObject, "order_no");
            refundDesc = JSONUtil.getString(jsonObject, "desc");
            status = jsonObject.optInt("status", 0);
            refundReason = JSONUtil.getString(jsonObject, "refund_reason");
            refuseReason = JSONUtil.getString(jsonObject, "refuse_reason");
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            updatedAt = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            JSONObject merchantObject = jsonObject.optJSONObject("merchant");
            contactPhones = new ArrayList<>();
            if (merchantObject != null) {
                merchantId = merchantObject.optInt("id", 0);
                merchantName = JSONUtil.getString(merchantObject, "name");
                merchantLogoPath = JSONUtil.getString(merchantObject, "logo_path");
                if (!merchantObject.isNull("property") && propertyId == 0) {
                    propertyId = new MenuItem(merchantObject.optJSONObject("property")).getId();
                }
                JSONArray phonesArray = merchantObject.optJSONArray("contact_phones");
                if (phonesArray != null && phonesArray.length() > 0) {
                    for (int i = 0; i < phonesArray.length(); i++) {
                        String phone = phonesArray.optString(i);
                        if (!JSONUtil.isEmpty(phone)) {
                            contactPhones.add(phonesArray.optString(i));
                        }
                    }
                }
                bondSign = merchantObject.optInt("bond_sign", 0) > 0;
                merchantUserId = merchantObject.optLong("user_id", 0);
            }
            JSONObject actionsObject = jsonObject.optJSONObject("actions");
            actions = new ArrayList<>();
            if (actionsObject != null) {
                statusStr = JSONUtil.getString(actionsObject, "status");
                JSONArray actionsArray = actionsObject.optJSONArray("can_do");
                if (actionsArray != null && actionsArray.length() > 0) {
                    for (int i = 0; i < actionsArray.length(); i++) {
                        OrderAction orderAction = new OrderAction(actionsArray.optJSONObject(i));
                        if (orderAction.getStatus() > 0) {
                            actions.add(orderAction);
                        }
                    }
                }
            }
        }
    }

    public NewOrder(JSONObject jsonObject, JSONObject subObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            title = JSONUtil.getString(jsonObject, "title");
            coverPath = JSONUtil.getString(jsonObject, "cover_path");
            describe = JSONUtil.getString(jsonObject, "describe");
            commodityType = jsonObject.optInt("commodity_type", 0);
            phoneNum = JSONUtil.getString(jsonObject, "phone_num");
            actualPrice = jsonObject.optDouble("actual_price", 0);
            marketPrice = jsonObject.optDouble("market_price", 0);
            propertyId = jsonObject.optLong("property_id", 0);
            allowEarnest = jsonObject.optInt("allow_earnest", 0) > 0;
            cheaperIfAllIn = jsonObject.optInt("cheaper_if_all_in", 0) > 0;
            depositPercent = jsonObject.optDouble("deposit_percent", 0);
            earnestMoney = jsonObject.optDouble("earnest_money", 0);
            orderGift = JSONUtil.getString(jsonObject, "order_gift");
            payAllGift = JSONUtil.getString(jsonObject, "pay_all_gift");
            if ("0".equals(orderGift)) {
                orderGift = null;
            }
            if ("0".equals(payAllGift)) {
                payAllGift = null;
            }

            JSONObject prdObject = jsonObject.optJSONObject("prdinfo");
            if (prdObject != null) {
                prdId = prdObject.optLong("id", 0);
                prdNum = prdObject.optInt("num", 0);
                prdType = prdObject.optInt("type", 0);
            }
            JSONObject payObject = jsonObject.optJSONObject("pay_info");
            if (payObject != null) {
                payPrice = payObject.optDouble("price", 0);
                payAllMoney = payObject.optDouble("pay_all_money", 0);
                payDepositMoney = payObject.optDouble("deposit_money", 0);
                payAidMoney = payObject.optDouble("aidmoney", 0);
            }
            JSONObject merchantObject = jsonObject.optJSONObject("merchant");
            contactPhones = new ArrayList<>();
            if (merchantObject != null) {
                merchantId = merchantObject.optInt("id", 0);
                merchantName = JSONUtil.getString(merchantObject, "name");
                merchantLogoPath = JSONUtil.getString(merchantObject, "logo_path");
                if (!merchantObject.isNull("property") && propertyId == 0) {
                    propertyId = new MenuItem(merchantObject.optJSONObject("property")).getId();
                }
                JSONArray phonesArray = merchantObject.optJSONArray("contact_phones");
                if (phonesArray != null && phonesArray.length() > 0) {
                    for (int i = 0; i < phonesArray.length(); i++) {
                        contactPhones.add(phonesArray.optString(i));
                    }
                }
                bondSign = merchantObject.optInt("bond_sign", 0) > 0;
                merchantUserId = merchantObject.optLong("user_id", 0);
            }
            version = jsonObject.optInt("version");

            isInstallment = JSONUtil.getBoolean(jsonObject, "is_installment");
            JSONObject installmentObject = jsonObject.optJSONObject("installment");
            if (installmentObject != null) {
                installment = new Installment(installmentObject);
            }
        }
        if (subObject != null) {
            status = subObject.optInt("status", 0);
            moneyStatus = subObject.optInt("money_status", 0);
            JSONObject actionsObject = subObject.optJSONObject("actions");
            orderNum = JSONUtil.getString(subObject, "order_no");
            isGift = subObject.optInt("is_gift") > 0;
            isFinished = JSONUtil.getBoolean(subObject, "is_finished");
            expiredTime = JSONUtil.getDateFromFormatLong(subObject, "expire_time", true);
            expiredTime = TimeUtil.getLocalTime(expiredTime);
            actions = new ArrayList<>();
            if (actionsObject != null) {
                statusStr = JSONUtil.getString(actionsObject, "status");
                JSONArray actionsArray = actionsObject.optJSONArray("can_do");
                if (actionsArray != null && actionsArray.length() > 0) {
                    for (int i = 0; i < actionsArray.length(); i++) {
                        OrderAction orderAction = new OrderAction(actionsArray.optJSONObject(i));
                        if (orderAction.getStatus() > 0) {
                            actions.add(orderAction);
                        }
                    }
                }
            }
        }
    }

    /**
     * 特殊情况,由于新的定制套餐的退款订单信息需要归入之前的退款单列表
     * 为简化逻辑,所以继续使用NewOrder的model,新增一个标志位用于识别退款单类型
     * 新增一个构造方法用于将新的定制套餐退款单的数据填入NewOrder的model中
     * 此时的id是退款单id
     */
    public NewOrder(JSONObject jsonObject, boolean isCustomOrderRefund) {
        this.isCustomOrderRefund = isCustomOrderRefund;
        // 只用于退款单列表,所以需要的数据不多,只解析需要的数据
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            status = jsonObject.optInt("status");
            JSONObject customOrderObj = jsonObject.optJSONObject("order");
            if (customOrderObj != null) {
                CustomSetmealOrder customSetmealOrder = new CustomSetmealOrder(customOrderObj);
                title = customSetmealOrder.getCustomSetmeal()
                        .getTitle();
                coverPath = customSetmealOrder.getCustomSetmeal()
                        .getImgCover();
                merchantName = customSetmealOrder.getCustomSetmeal()
                        .getMerchant()
                        .getName();
                paidMoney = customSetmealOrder.getPaidMoney();
            }

            JSONObject merchantObject = jsonObject.optJSONObject("merchant");
            if (merchantObject != null) {
                merchantId = merchantObject.optInt("id", 0);
                merchantName = JSONUtil.getString(merchantObject, "name");
                merchantLogoPath = JSONUtil.getString(merchantObject, "logo_path");
                if (!merchantObject.isNull("property") && propertyId == 0) {
                    propertyId = new MenuItem(merchantObject.optJSONObject("property")).getId();
                }
                merchantUserId = merchantObject.optLong("user_id", 0);
            }

            refundPrice = jsonObject.optDouble("pay_money");
            // 退款状态
            switch (status) {
                case 20:
                    statusStr = "退款审核中";
                    break;
                case 21:
                    statusStr = "已取消退款申请";
                    break;
                case 23:
                    statusStr = "退款被拒绝";
                    break;
                case 24:
                    statusStr = "退款成功";
                    break;
            }
        }
    }

    public boolean isCustomOrderRefund() {
        return isCustomOrderRefund;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getDescribe() {
        return describe;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public double getActualPrice() {
        if (salePrice > 0) {
            return salePrice;
        }
        return actualPrice;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public boolean isAllowEarnest() {
        return allowEarnest;
    }

    public boolean isCheaperIfAllIn() {
        return cheaperIfAllIn;
    }

    public long getPrdId() {
        return prdId;
    }

    public int getPrdType() {
        return prdType;
    }

    public ArrayList<OrderAction> getActions() {
        return actions;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getMerchantLogoPath() {
        return merchantLogoPath;
    }

    public ArrayList<String> getContactPhones() {
        return contactPhones;
    }

    public long getMerchantUserId() {
        return merchantUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public double getRefundPrice() {
        return refundPrice;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public String getRefundOrderNum() {
        return refundOrderNum;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public boolean isGift() {
        return isGift;
    }

    public int getStatus() {
        return status;
    }

    public boolean isLvpai() {
        return false;
    }

    public String getPayAllGift() {
        return payAllGift;
    }


    public String getOrderGift() {
        return orderGift;
    }

    public int getVersion() {
        return version;
    }

    public boolean getBondSign() {
        return bondSign;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public Installment getInstallment() {
        return installment;
    }

    public boolean isIntentPay() {
        return isIntentPay;
    }
}
