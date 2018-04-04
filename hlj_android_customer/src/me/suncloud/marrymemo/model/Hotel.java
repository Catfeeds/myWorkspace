package me.suncloud.marrymemo.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * author:Suncloud
 * 2015/3/18 14:39
 */
public class Hotel implements Identifiable {

    private long id;
    private String cityName;
    private String area;
    private String kind;
    private double price_start;
    private double price_end;
    private long table_num;
//    private ArrayList<HotelHall> hotelHalls;
//    private ArrayList<HotelMenu> hotelMenus;

    public Hotel(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            cityName = JSONUtil.getString(jsonObject, "city_name");
            area = JSONUtil.getString(jsonObject, "area");
            kind = JSONUtil.getString(jsonObject, "kind");
            price_start = jsonObject.optDouble("price_start", 0);
            price_end = jsonObject.optDouble("price_end", 0);
            table_num = jsonObject.optLong("table_num", 0);

//            JSONArray array = jsonObject.optJSONArray("hotel_halls");
//            if (array != null && array.length() > 0) {
//                hotelHalls = new ArrayList<>();
//                for (int i = 0, size = array.length(); i < size; i++) {
//                    hotelHalls.add(new HotelHall(array.optJSONObject(i)));
//                }
//            }
//            JSONArray array = jsonObject.optJSONArray("hotel_menus");
//            if (array != null && array.length() > 0) {
//                hotelMenus = new ArrayList<>();
//                for (int i = 0, size = array.length(); i < size; i++) {
//                    hotelMenus.add(new HotelMenu(array.optJSONObject(i)));
//                }
//            }
        }
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPriceStr(){
        if(price_start<price_end){
            return TextUtils.concat(Util.formatDouble2String(price_start), "-", Util.formatDouble2String(price_end)).toString();
        }
        return Util.formatDouble2String(price_end);
    }

    public long getTable_num() {
        return table_num;
    }

    @Override
    public Long getId() {
        return id;
    }

//    public ArrayList<HotelHall> getHotelHalls() {
//        return hotelHalls == null ? new ArrayList<HotelHall>() : hotelHalls;
//    }

//    public ArrayList<HotelMenu> getHotelMenus() {
//        return hotelMenus == null ? new ArrayList<HotelMenu>() : hotelMenus;
//    }

    public String getCityName() {
        return cityName;
    }
}
