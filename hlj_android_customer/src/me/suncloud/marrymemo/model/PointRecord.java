package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/3/1.
 */
public class PointRecord implements Serializable{

    private boolean isSignBefore;
    private boolean isSignToday;
    //金币
    private int balance;
    private int dayCount;
    private int daliySignScore;
    private String homePage;
    private String coinPage;
    private long userId;

    public PointRecord(JSONObject jsonObject){
        if(jsonObject!=null){
            isSignBefore=jsonObject.optInt("is_sign_before",0)>0;
            isSignToday=jsonObject.optInt("is_sign_today",0)>0;
            balance=jsonObject.optInt("balance",0);
            dayCount=jsonObject.optInt("day_count",0);
            daliySignScore=jsonObject.optInt("daliy_sign_score",0);
            homePage= JSONUtil.getString(jsonObject,"home_page");
            coinPage= JSONUtil.getString(jsonObject,"coin_page");
        }
    }

    public boolean isSignBefore() {
        return isSignBefore;
    }

    public boolean isSignToday() {
        return isSignToday;
    }

    public int getBalance() {
        return balance;
    }

    public int getDayCount() {
        return dayCount;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getCoinPage() {
        return coinPage;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public int getDaliySignScore() {
        return daliySignScore;
    }
}
