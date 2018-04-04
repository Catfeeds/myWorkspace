package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 15/2/5.
 */
public class RedPacket implements Identifiable {

    private static final long serialVersionUID = -4498762276275081246L;

    private long id;
    private long redPacketId;
    private String state; // # init 领红包 #used 已使用 #expired 已过期
    private Date beginDate;
    private Date endDate;
    private Date useTime;
    private String ticketNo;
    private double amount;
    private String redPacketName;
    private String categoryType;
    private int status;
    private double moneySill; // 红包满减金额字段
    private String moneySillStr; // 满减金额描述字段

    public RedPacket(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.redPacketId = jsonObject.optLong("red_packet_id", 0);
            this.ticketNo = JSONUtil.getString(jsonObject, "ticket_no");
            this.state = JSONUtil.getString(jsonObject, "state");
            this.beginDate = JSONUtil.getDate(jsonObject, "start_at");
            this.endDate = JSONUtil.getDate(jsonObject, "end_at");
            this.useTime = JSONUtil.getDate(jsonObject, "use_time");
            this.amount = jsonObject.optDouble("price", 0);
            if(amount==0){
                amount=jsonObject.optDouble("money_value",0);
            }
            if(beginDate==null) {
                this.beginDate = JSONUtil.getDateFromFormatLong(jsonObject, "validity_starttime",true);
            }
            if(endDate==null) {
                this.endDate = JSONUtil.getDateFromFormatLong(jsonObject, "validity_endtime", true);
            }
            this.redPacketName = JSONUtil.getString(jsonObject,"red_packet_name");
            this.categoryType = JSONUtil.getString(jsonObject,"red_packet_type_category_name");
            this.status=jsonObject.optInt("status");
            this.moneySill = jsonObject.optDouble("money_sill", 0);
            this.moneySillStr = JSONUtil.getString(jsonObject, "money_sill_text");
        }
    }

    public RedPacket(long id) {
        this.id = id;
        this.amount = 0;
        this.ticketNo = "";
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public long getRedPacketId() {
        return redPacketId;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public String getState() {
        return state;
    }

    public int getStatus() {
        return status;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getRedPacketName() {
        return redPacketName;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getMoneySill() {
        return moneySill;
    }

    public String getMoneySillStr() {
        return moneySillStr;
    }
}
