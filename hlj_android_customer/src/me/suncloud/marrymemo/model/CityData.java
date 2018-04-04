package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangtao on 2017/4/17.
 */

public class CityData {

    @SerializedName("list")
    private Map<String,List<City>> cityMap;
    private List<City> allCities;
    @SerializedName("hot_city")
    private List<City> hotCities;
    @SerializedName("relative_city")
    private List<City> relativeCities;
    @SerializedName("cityId")
    private long cityId; //本地记录关联城市对应的城市id

    public List<City> getHotCities() {
        if(hotCities==null){
            return new ArrayList<>();
        }
        return hotCities;
    }

    public List<City> getRelativeCities() {
        if(relativeCities==null){
            return new ArrayList<>();
        }
        return relativeCities;
    }

    public Map<String, List<City>> getCityMap() {
        return cityMap;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
}
