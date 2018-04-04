package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by mo_yu on 2016/6/20.生意罗盘-店铺数据
 */
public class ShopStatic implements Serializable {
    private boolean isPr0; //是否开通店铺宝
    private String multilPvRank;//展示店铺pv
    private String multilexamplePvRank;//展示案例pv
    private String multilpacketPvRank;//展示套餐pv
    private String property;
    private String city;
    private ArrayList<Date> shopDate = new ArrayList<>();
    private ArrayList<String> shopDateList = new ArrayList<>();
    private ArrayList<Integer> storeList = new ArrayList<>();
    private ArrayList<Integer> workList = new ArrayList<>();
    private ArrayList<Integer> caseList = new ArrayList<>();

    public ShopStatic(JSONObject json) throws Exception {
        if(json!=null) {
            isPr0 = json.optBoolean("is_pro",false);
            if (!isPr0){
                isPr0 = json.optInt("is_pro",0)>0;
            }

            multilPvRank = JSONUtil.getString(json,"multilPv_rank");
            multilexamplePvRank = JSONUtil.getString(json,"multilexamplePv_rank");
            multilpacketPvRank = JSONUtil.getString(json,"multilpacketPv_rank");
            property = JSONUtil.getString(json,"property");
            city = JSONUtil.getString(json,"city");
            if (!json.isNull("list")){
                JSONObject jsonObject = json.optJSONObject("list");
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()){
                    shopDate.add(stringToDate(iterator.next()));
                }
                dateSort(shopDate);
                for (int i = 0;i<shopDate.size();i++){
                    String key = dateToString(shopDate.get(i));
                    String dateString = dateToMDString(shopDate.get(i));
                    shopDateList.add(dateString);
                    if (!jsonObject.isNull(key)){
                        JSONArray jsonArray = jsonObject.optJSONArray(key);
                        if (jsonArray!=null&&jsonArray.length()==3){
                            storeList.add(jsonArray.optInt(0));
                            caseList.add(jsonArray.optInt(1));
                            workList.add(jsonArray.optInt(2));
                        }
                    }
                }

            }
        }
    }

    private void dateSort(ArrayList<Date> mList){
        Collections.sort(mList, new Comparator<Date>() {
            /**
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             * equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(Date lhs, Date rhs) {
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (lhs.after(rhs)) {
                    return 1;
                }
                return -1;

            }
        });
    }

    //把字符串转为日期
    public static Date stringToDate(String strDate) throws Exception
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    //把日期转为字符串
    public static String dateToString(Date date)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    //把日期转为只含日月字符串
    public static String dateToMDString(Date date)
    {
        DateFormat df = new SimpleDateFormat("MM-dd");
        return df.format(date);
    }

    public boolean isPr0() {
        return isPr0;
    }

    public void setPr0(boolean pr0) {
        isPr0 = pr0;
    }

    public String getMultilPvRank() {
        return multilPvRank;
    }

    public void setMultilPvRank(String multilPvRank) {
        this.multilPvRank = multilPvRank;
    }

    public String getMultilexamplePvRank() {
        return multilexamplePvRank;
    }

    public void setMultilexamplePvRank(String multilexamplePvRank) {
        this.multilexamplePvRank = multilexamplePvRank;
    }

    public String getMultilpacketPvRank() {
        return multilpacketPvRank;
    }

    public void setMultilpacketPvRank(String multilpacketPvRank) {
        this.multilpacketPvRank = multilpacketPvRank;
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

    public ArrayList<Date> getShopDate() {
        return shopDate;
    }

    public void setShopDate(ArrayList<Date> shopDate) {
        this.shopDate = shopDate;
    }

    public ArrayList<String> getShopDateList() {
        return shopDateList;
    }

    public void setShopDateList(ArrayList<String> shopDateList) {
        this.shopDateList = shopDateList;
    }

    public ArrayList<Integer> getStoreList() {
        return storeList;
    }

    public void setStoreList(ArrayList<Integer> storeList) {
        this.storeList = storeList;
    }

    public ArrayList<Integer> getWorkList() {
        return workList;
    }

    public void setWorkList(ArrayList<Integer> workList) {
        this.workList = workList;
    }

    public ArrayList<Integer> getCaseList() {
        return caseList;
    }

    public void setCaseList(ArrayList<Integer> caseList) {
        this.caseList = caseList;
    }
}
