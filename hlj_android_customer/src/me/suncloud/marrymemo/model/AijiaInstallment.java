package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/2/26.
 * 爱家理财分期数据item
 */
public class AijiaInstallment implements Identifiable {
    private static final long serialVersionUID = 4587549189432594605L;

    private double amount;
    private double fees;
    private int num;
    private double rate;
    private String title;
    private String remarks;

    public AijiaInstallment(JSONObject jsonObject) {
        if (jsonObject != null) {
            amount = jsonObject.optDouble("amount", 0);
            fees = jsonObject.optDouble("fees", 0);
            num = jsonObject.optInt("num", 0);
            rate = jsonObject.optDouble("rate", 0);
            title = JSONUtil.getString(jsonObject, "title");
            remarks = JSONUtil.getString(jsonObject, "remarks");
        }
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }

    public String getRemarks() {
        return remarks;
    }

    public double getAmount() {
        return amount;
    }

    public double getFees() {
        return fees;
    }

    public int getNum() {
        return num;
    }

    public double getRate() {
        return rate;
    }

    public String getTitle() {
        return title;
    }
}
