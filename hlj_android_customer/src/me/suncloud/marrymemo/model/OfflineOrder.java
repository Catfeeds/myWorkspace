package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.CANCEL;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.CANCEL_REFUND;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONCALL;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONCHAT;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONCOMENT;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONCONTACT;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONPAY;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.ONPAY_REST;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.REFUND;
import static me.suncloud.marrymemo.model.OrderStatusActionsEnum.SUCCESS;

/**
 * Created by luohanlin on 15/6/16.
 * 到店付款订单
 */
public class OfflineOrder implements Identifiable {

    private static final long serialVersionUID = -2368449720517620183L;

    private long id;
    private long userId;
    private long redPacketId;
    private String redPacketNum;
    private double redPacketMoney;
    private double totalPrice;
    private double paid;
    private int status;
    private String orderNum;
    private Date createdAt;
    private Date updatedAt;
    private String phone;
    private long merchantId;
    private String merchantName;
    private ArrayList<String> merchantContactPhones;
    private ArrayList<OrderStatusActionsEnum> actionsEnums;
    private String statusStr;

    public OfflineOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            userId = jsonObject.optLong("user_id", 0);
            redPacketId = jsonObject.optLong("red_package_id", 0);
            redPacketNum = JSONUtil.getString(jsonObject, "red_package_no");
            redPacketMoney = jsonObject.optDouble("red_package_money", 0);
            totalPrice = jsonObject.optDouble("money", 0);
            paid = jsonObject.optDouble("paid_money", 0);
            status = jsonObject.optInt("status", 0);
            orderNum = JSONUtil.getString(jsonObject, "order_no");
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            updatedAt = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            phone = JSONUtil.getString(jsonObject, "phone");
            JSONObject merchantObject = jsonObject.optJSONObject("merchant");
            merchantContactPhones = new ArrayList<>();
            if (merchantObject != null) {
                merchantId = merchantObject.optLong("id", 0);
                merchantName = JSONUtil.getString(merchantObject, "name");
                JSONArray contactArray = merchantObject.optJSONArray("contact_phones");
                if (contactArray != null && contactArray.length() > 0) {
                    for (int i = 0; i < contactArray.length(); i++) {
                        String phone=contactArray.optString(i);
                        if(!JSONUtil.isEmpty(phone)) {
                            merchantContactPhones.add(phone);
                        }
                    }
                }
            }

            JSONObject actionsObject = jsonObject.optJSONObject("actions");
            actionsEnums = new ArrayList<>();
            if (actionsObject != null) {
                // 这里的这个状态用来显示整个订单的状态的**字符串**，需要用这个字符串来显示并且通过字符串匹配来使用不同的颜色，已区分订单
                // 这里应该使用整型值来标记状态，然后通过编辑来整理状态枚举的，但......
                statusStr = JSONUtil.getString(actionsObject, "status");
                JSONArray actionsArray = actionsObject.optJSONArray("can_do");
                if (actionsArray != null && actionsArray.length() > 0) {
                    for (int i = 0; i < actionsArray.length(); i++) {
                        switch (actionsArray.optJSONObject(i).optInt("action")) {
                            case 1:
                                actionsEnums.add(ONPAY);
                                break;
                            case 2:
                                actionsEnums.add(CANCEL);
                                break;
                            case 3:
                                actionsEnums.add(REFUND);
                                break;
                            case 4:
                                actionsEnums.add(SUCCESS);
                                break;
                            case 5:
                                actionsEnums.add(ONCOMENT);
                                break;
                            case 6:
                                actionsEnums.add(ONPAY_REST);
                                break;
                            case 7:
                                actionsEnums.add(ONCONTACT);
                                break;
                            case 8:
                                actionsEnums.add(ONCHAT);
                                break;
                            case 9:
                                actionsEnums.add(ONCALL);
                                break;
                            case 10:
                                actionsEnums.add(CANCEL_REFUND);
                                break;
                        }
                    }
                }
            }
        }
    }

    public long getUserId() {
        return userId;
    }

    public String getRedPacketNum() {
        return redPacketNum;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getPaid() {
        return paid;
    }

    public int getStatus() {
        return status;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getPhone() {
        return phone;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public ArrayList<String> getMerchantContactPhones() {
        return merchantContactPhones;
    }

    public ArrayList<OrderStatusActionsEnum> getActionsEnums() {
        return actionsEnums;
    }

    public String getStatusStr() {
        return statusStr;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getRedPacketId() {
        return redPacketId;
    }
}
