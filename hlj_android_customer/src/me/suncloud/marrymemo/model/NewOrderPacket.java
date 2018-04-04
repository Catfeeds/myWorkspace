package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * Created by luohanlin on 15/5/27.
 * 活动总订单包装类，包含子订单
 * 当子订单只有一个，也就是只有单个套餐的订单的时候，这个类也作为单个订单来用
 * 不要问我为什么这个json解析类要写得这么复杂，因为接口就是这么设计的
 */
public class NewOrderPacket extends TypeModel {


    public static final int TYPE = 1;
    private static final long serialVersionUID = 8784981107532556116L;
    private int moneyStatus;
    private long id;
    private String orderNum;
    private String packetTitle;
    private Date createdAt;
    private Date weddingTime;
    private String userName;
    private String userPhone;
    private int packetStatus;
    private int packetType;
    private double allMoney;
    private double paidMoney;
    private double payAllSavedMoney;
    private double depositMoney;
    //    private double saveMoney;
    private double aidMoney; // 活动优惠
    private String aidString; // 活动优惠的描述，当没有直接的满减金额aidMoney的时候，就显示活动的描述
    private double redPacketMoney; // 用于这个订单的红包的金额数，应当由subs中的所有子订单中分到的红包金额之后计算得出
    private ArrayList<NewOrder> ruleOrders; // 属于活动的套餐
    private ArrayList<NewOrder> otherOrders; // 不属于活动的套餐
    private ArrayList<OrderAction> actions;
    private String statusStr;
    private int status;
    private Date expiredTime;
    private boolean allowEarnest;
    private boolean isAllOrderRefundSuccess;
    private boolean isLvpai;
    private String freeOrderLink;
    private String disableReason;
    private int disableType; // 1: 不支持, 2: 服务时间小于30天

    private String redPacketNo;

    public NewOrderPacket(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            weddingTime = JSONUtil.getDateFromFormatLong(jsonObject, "wedding_time", false);
            userName = JSONUtil.getString(jsonObject, "buyer_name");
            userPhone = JSONUtil.getString(jsonObject, "buyer_phone");
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            orderNum = JSONUtil.getString(jsonObject, "order_no");
            redPacketNo = JSONUtil.getString(jsonObject, "red_packet_no");
            expiredTime = JSONUtil.getDateFromFormatLong(jsonObject, "order_expired_time", true);
            expiredTime = TimeUtil.getLocalTime(expiredTime);
            freeOrderLink = JSONUtil.getString(jsonObject, "free_order_link");

            JSONObject actionsObject = jsonObject.optJSONObject("actions");
            actions = new ArrayList<>();
            if (actionsObject != null) {
                // 这里的这个状态用来显示整个订单的状态的**字符串**，需要用这个字符串来显示并且通过字符串匹配来使用不同的颜色，已区分订单
                // 这里应该使用整型值来标记状态，然后通过编辑来整理状态枚举的，但......
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

            JSONObject snapShotObject = jsonObject.optJSONObject("snapshot");
            JSONArray subsArray = jsonObject.optJSONArray("subs");
            ruleOrders = new ArrayList<>();
            otherOrders = new ArrayList<>();
            if (snapShotObject != null) {
                JSONArray ruleItemsArray = snapShotObject.optJSONArray("rule_items");
                if (ruleItemsArray != null && ruleItemsArray.length() > 0) {
                    // 业务逻辑规定了一个总订单里面不会包含多个活动，但这接口偏要用数组形式写成这样，我也没办法
                    // 暂且直接获取数组的第一个元素算了
                    JSONObject packetInfoObject = ruleItemsArray.optJSONObject(0)
                            .optJSONObject("ruleinfo");
                    if (packetInfoObject != null) {
                        packetTitle = JSONUtil.getString(packetInfoObject, "name");
                        // 这个status呢，什么鬼啊，难道是上面说的状态对应标记的整型值？？？
                        packetStatus = packetInfoObject.optInt("status", 0);
                        packetType = packetInfoObject.optInt("type", 0);
                        aidString = JSONUtil.getString(packetInfoObject, "description");
                        status = packetInfoObject.optInt("status", 0);
                    }

                    JSONObject packageItemObject = ruleItemsArray.optJSONObject(0);
                    JSONArray itemsArray = packageItemObject.optJSONArray("items");
                    if (itemsArray != null && itemsArray.length() > 0) {
                        for (int i = 0; i < itemsArray.length(); i++) {
                            if (subsArray != null && subsArray.length() > 0) {
                                long prdId = itemsArray.optJSONObject(i)
                                        .optLong("id", 0);
                                for (int j = 0; j < subsArray.length(); j++) {
                                    // prdId 是指套餐的id，与旧版接口中的workID对应
                                    if (prdId == (subsArray.optJSONObject(j)
                                            .optLong("prdid"))) {
                                        JSONObject subObject = subsArray.optJSONObject(j);
                                        NewOrder order = new NewOrder(itemsArray.optJSONObject(i),
                                                subObject);
                                        isLvpai |= order.isLvpai();
                                        ruleOrders.add(order);
                                    }
                                }
                            }
                        }
                    }

                    JSONArray giftItemsArray = packageItemObject.optJSONArray("gift");
                    if (giftItemsArray != null && giftItemsArray.length() > 0) {
                        for (int i = 0; i < giftItemsArray.length(); i++) {
                            if (subsArray != null && subsArray.length() > 0) {
                                long prdId = giftItemsArray.optJSONObject(i)
                                        .optLong("id", 0);
                                for (int j = 0; j < subsArray.length(); j++) {
                                    // prdId 是指套餐的id，与旧版接口中的workID对应
                                    if (prdId == (subsArray.optJSONObject(j)
                                            .optLong("prdid"))) {
                                        JSONObject subObject = subsArray.optJSONObject(j);
                                        NewOrder order = new NewOrder(giftItemsArray
                                                .optJSONObject(i),
                                                subObject);
                                        // 手动处理赠送的套餐的价格设置为0
                                        order.setActualPrice(0);
                                        isLvpai |= order.isLvpai();
                                        ruleOrders.add(order);

                                    }
                                }
                            }
                        }
                    }

                    // pay_all_money不是需“要付的全部金额”的意思（命名明明就是这个意思），而是全额付款所能得到的优惠
                    payAllSavedMoney = packageItemObject.optDouble("pay_all_money", 0);
                    depositMoney = packageItemObject.optDouble("deposit_money", 0);
                    // allMoney这个才是真正的这个活动的总价合计
                    allMoney = packageItemObject.optDouble("allmoney", 0);
                    // 节省的钱数由aidmoney表示，那SaveMoney是什么呢
                    //                        saveMoney = packageItemObject.optDouble
                    // ("savemoney", 0);
                    aidMoney = packageItemObject.optDouble("aidmoney", 0);
                }

                JSONArray otherWorksArray = snapShotObject.optJSONArray("others");
                if (otherWorksArray != null && otherWorksArray.length() > 0) {
                    for (int i = 0; i < otherWorksArray.length(); i++) {
                        // 其他套餐列表中的套餐价价格总计
                        payAllSavedMoney += otherWorksArray.optJSONObject(i)
                                .optDouble("pay_all_money", 0);
                        depositMoney += otherWorksArray.optJSONObject(i)
                                .optDouble("deposit_money", 0);
                        allMoney += otherWorksArray.optJSONObject(i)
                                .optDouble("allmoney", 0);
                        aidMoney += otherWorksArray.optJSONObject(i)
                                .optDouble("aidmoney", 0);
                        if (subsArray != null && subsArray.length() > 0) {
                            long prdId = otherWorksArray.optJSONObject(i)
                                    .optJSONObject("item")
                                    .optLong("id", 0);
                            for (int j = 0; j < subsArray.length(); j++) {
                                if (prdId == (subsArray.optJSONObject(j)
                                        .optLong("prdid"))) {
                                    JSONObject subObject = subsArray.optJSONObject(j);
                                    // 本来订单下应该是一个套餐的列表，但需要额外的字段来表明套餐单独的状态和属性
                                    // 所以新建名叫NewOrder的model来做这件事，而NewOrder需要的字段来自两个列表
                                    // 一个snapshot中的rule_items中的items，一个subs
                                    NewOrder order = new NewOrder(otherWorksArray.optJSONObject(i)
                                            .optJSONObject("item"), subObject);
                                    isLvpai |= order.isLvpai();
                                    otherOrders.add(order);
                                }
                            }
                        }
                    }
                }
            }

            if (subsArray != null && subsArray.length() > 0) {
                // 从sub数组中取得订单的价格信息
                // 所有订单的付款状态都是一样的，所以判断一个money status即可
                moneyStatus = subsArray.optJSONObject(0)
                        .optInt("money_status", 0);
                if (moneyStatus > 0) {
                    allMoney = 0;
                    paidMoney = 0;
                    aidMoney = 0;
                    payAllSavedMoney = 0;
                }
                redPacketMoney = 0;

                for (int i = 0; i < subsArray.length(); i++) {
                    // 如果其中有一个的money status不为空的时候，说明已经付过款了，从这之后总订单的金额计算都以subs中的数据为准
                    JSONObject subOrderObject = subsArray.optJSONObject(i);
                    boolean isGift = subOrderObject.optInt("is_gift", 0) > 0;
                    if (!isGift) {
                        if (moneyStatus > 0) {
                            // 或者的是is_gift的话就不需要计算其价格，不需要也不能参考money_status状态
                            allMoney += subOrderObject.optDouble("actual_price", 0);
                            paidMoney += subOrderObject.optDouble("paid_money", 0);
                            aidMoney += subOrderObject.optDouble("aid_money", 0);
                            payAllSavedMoney += subOrderObject.optDouble("pay_all_money", 0);
                        }
                        redPacketMoney += subOrderObject.optDouble("red_packet_money", 0);

                    }
                }
            }

            if (depositMoney >= allMoney || depositMoney <= 0) {
                // 定金小于零或者定金没有小于总金额，则意味着不能使用定金支付
                allowEarnest = false;
                disableType = 1;
                disableReason = "该套餐暂不支持定金支付";
            } else {
                // 如果有wedding time的话，则需要判断是否在这之前的三十天之前，否则也不支持定金付款
                if (weddingTime == null) {
                    allowEarnest = true;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 30);
                    Calendar weddingCalendar = Calendar.getInstance();
                    weddingCalendar.setTime(weddingTime);
                    if ((weddingTime != null && calendar.before(weddingCalendar))) {
                        allowEarnest = true;
                    } else {
                        allowEarnest = false;
                        disableType = 2;
                        disableReason = "距服务时间已少于30天";
                    }
                }
            }

            // fucking terrible
            ArrayList<NewOrder> allOrders = new ArrayList<>();
            allOrders.addAll(ruleOrders);
            allOrders.addAll(otherOrders);
            isAllOrderRefundSuccess = true;
            for (int i = 0; i < allOrders.size(); i++) {
                if (allOrders.get(i)
                        .getStatus() != 24) {
                    isAllOrderRefundSuccess = false;
                    break;
                }
            }

        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getPacketTitle() {
        return packetTitle;
    }

    public Date getWeddingTime() {
        return weddingTime;
    }

    public int getPacketStatus() {
        return packetStatus;
    }

    public int getPacketType() {
        return packetType;
    }

    public ArrayList<NewOrder> getRuleOrders() {
        return ruleOrders;
    }

    public long getPropertyId() {
        long propertyId = 0;
        if (ruleOrders != null && !ruleOrders.isEmpty()) {
            for (NewOrder order : ruleOrders) {
                if (order.getPropertyId() > 0) {
                    propertyId = order.getPropertyId();
                    break;
                }
            }
        }
        if (otherOrders != null && !otherOrders.isEmpty()) {
            for (NewOrder order : otherOrders) {
                if (order.getPropertyId() > 0 && propertyId == 0) {
                    propertyId = order.getPropertyId();
                    break;
                }
            }
        }
        return propertyId;
    }

    public ArrayList<NewOrder> getOtherOrders() {
        return otherOrders;
    }

    public double getAllMoney() {
        return allMoney;
    }

    public double getPayAllSavedMoney() {
        return payAllSavedMoney;
    }

    public double getDepositMoney() {
        return depositMoney;
    }

    //    public double getSaveMoney() {
    //        return saveMoney;
    //    }

    public double getAidMoney() {
        return aidMoney;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public ArrayList<OrderAction> getActions() {
        return actions;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public int getMoneyStatus() {
        return moneyStatus;
    }

    public String getAidString() {
        return aidString;
    }

    public int getStatus() {
        return status;
    }

    public boolean isAllowEarnest() {
        return allowEarnest;
    }

    public String getEarnestDisableReason() {
        return disableReason;
    }

    public int getDisableType() {
        return disableType;
    }

    // 判断是否全部订单退款成功
    public boolean isAllRefundSuccess() {
        return isAllOrderRefundSuccess;
    }

    public boolean isLvpai() {
        return isLvpai;
    }

    public String getPrds() {
        try {
            JSONArray orderArray = new JSONArray();
            if (ruleOrders != null && !ruleOrders.isEmpty()) {
                for (NewOrder order : ruleOrders) {
                    if (!order.isGift()) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("id", order.getPrdId());
                        orderJson.put("num", 1);
                        orderJson.put("type", 0);
                        orderArray.put(orderJson);
                    }
                }
            }
            if (otherOrders != null && !otherOrders.isEmpty()) {
                for (NewOrder order : otherOrders) {
                    if (!order.isGift()) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("id", order.getPrdId());
                        orderJson.put("num", 1);
                        orderJson.put("type", 0);
                        orderArray.put(orderJson);
                    }
                }
            }
            if (orderArray.length() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("prds", orderArray);
                return jsonObject.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFreeOrderLink() {
        return freeOrderLink;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }
}

