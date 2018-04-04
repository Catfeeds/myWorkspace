package com.hunliji.marrybiz.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 同业竞对model
 * Created by jinxin on 2016/6/20.
 */
public class SameBusiness implements Identifiable {

    private String property;
    private String city;
    private NewMerchant my;
    private String rate;
    private String myRank;
    private List<NewMerchant> merchantList;

    public SameBusiness(JSONObject json) {
        if (json != null) {
            property = json.optString("property");
            city = json.optString("city");
            my = new NewMerchant(json.optJSONObject("my"));
            rate = json.optString("rate");
            myRank = json.optString("my_rank");
            JSONArray list = json.optJSONArray("list");
            if (list != null) {
                merchantList = new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject item = list.optJSONObject(i);
                    NewMerchant merchant = new NewMerchant(item);
                    if (merchant.getId() > 0) {
                        merchantList.add(merchant);
                    }
                }
            }
        }
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public NewMerchant getMy() {
        return my;
    }

    public void setMy(NewMerchant my) {
        this.my = my;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMyRank() {
        return myRank;
    }

    public void setMyRank(String myRank) {
        this.myRank = myRank;
    }

    public List<NewMerchant> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<NewMerchant> merchantList) {
        this.merchantList = merchantList;
    }

    @Override
    public Long getId() {
        return null;
    }
}
