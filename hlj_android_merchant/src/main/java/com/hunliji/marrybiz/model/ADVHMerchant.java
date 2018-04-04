package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Suncloud on 2015/12/21.
 */
public class ADVHMerchant implements Identifiable {

    private long id;
    private int status;
    private String remark;
    private Date updatedAt;
    private long expiredAt;
    private ADVHHelper helper;
    private ArrayList<ADVHMerchantHistory> histories;
    private int totalNum;
    private boolean special; //特殊派单开关标志

    public ADVHMerchant(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            status=jsonObject.optInt("status");
            updatedAt=JSONUtil.getDateFromFormatLong(jsonObject,"created_at",true);
            expiredAt=jsonObject.optLong("expired_at")*1000;
            JSONObject itemObject=jsonObject.optJSONObject("item");
            if(itemObject!=null){
                remark= JSONUtil.getString(itemObject,"remark");
            }
            helper=new ADVHHelper(jsonObject.optJSONObject("helper"));
            JSONArray historyArray=jsonObject.optJSONArray("history");
            if(historyArray!=null&&historyArray.length()>0){
                histories=new ArrayList<>();
                for(int i=0,size=historyArray.length();i<size;i++){
                    histories.add(new ADVHMerchantHistory(historyArray.optJSONObject(i)));
                }
            }
            JSONObject object=jsonObject.optJSONObject("round");
            totalNum=1;
            if(object!=null){
                totalNum=object.optInt("show_total_num",1);
            }
            special = jsonObject.optInt("special")>0;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public ADVHHelper getHelper() {
        return helper;
    }

    public ArrayList<ADVHMerchantHistory> getHistories() {
        return histories;
    }

    public Date getUpdatedAt() {
        return TimeUtil.getLocalTime(updatedAt);
    }

    public int getStatus() {
        return status;
    }

    public long getExpiredAt() {
        return expiredAt-TimeUtil.getmTimeOffset();
    }

    public String getRemark() {
        return remark;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUpdatedAt(long expiredAt) {
        this.updatedAt = new Date(expiredAt);
    }

    public int getTotalNum() {
        return totalNum;
    }

    public boolean isSpecial() {
        return special;
    }
}
