package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/9/21.
 */
public class CarProduct implements Identifiable {
    private long id;
    private String title;
    private String describe;
    private String cover;
    private ArrayList<CarSku> skus;
    private ArrayList<Photo> headerPhotos;
    private ArrayList<Photo> detailPhotos;
    private int soldCount;
    private double salePrice;
    private double actualPrice;
    private double marketPrice;
    private double showPrice;
    private Photo cover_image;
    private Rule rule;
    private ShareInfo shareInfo;
    private boolean is_published;
    private Order lastOrder;
    private int city_code;
    private int type;
    private String cityName;

    public CarProduct(JSONObject object) {
        if (object != null) {
            this.id = object.optInt("id");
            this.title = object.optString("title");
            this.soldCount = object.optInt("sold_count");
            this.describe = object.optString("describe");
            this.actualPrice = object.optDouble("actual_price", 0);
            this.marketPrice = object.optDouble("market_price", 0);
            this.salePrice = object.optDouble("sale_price", 0);
            this.showPrice = object.optDouble("show_price", 0);
            this.is_published = object.optInt("is_published", 1) > 0;
            JSONObject lastOrder = object.optJSONObject("lastOrder");
            if (lastOrder != null) {
                this.lastOrder = new Order(lastOrder);
            }
            this.headerPhotos = initPhotos(object.optJSONArray("header_photos"));
            this.detailPhotos = initPhotos(object.optJSONArray("detail_photos"));
            this.cover_image = initPhoto(object.optJSONObject("cover_image"));
            this.skus = initCarskus(object.optJSONArray("skus"));
            this.rule = initRule(object.optJSONObject("rule"));
            this.shareInfo = initShareInfo(object.optJSONObject("share"));
            this.city_code = object.optInt("city_code");
            this.type=object.optInt("type");
        }
    }

    private ShareInfo initShareInfo(JSONObject share) {
        return new ShareInfo(share);
    }

    private ArrayList<Photo> initPhotos(JSONArray array) {
        try {
            ArrayList<Photo> headerPhotos = new ArrayList<Photo>();
            if (array != null && array.length() > 0) {
                int size = array.length();
                for (int i = 0; i < size; i++) {
                    JSONObject object = array.optJSONObject(i);
                    headerPhotos.add(initPhoto(object));
                }
            }
            return headerPhotos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Photo initPhoto(JSONObject object) {
        return new Photo(object);
    }

    private CarSku initCarsku(JSONObject object) {
        return new CarSku(object);
    }

    private ArrayList<CarSku> initCarskus(JSONArray array) {
        try {
            if (array != null && array.length() > 0) {
                ArrayList<CarSku> list = new ArrayList<CarSku>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(initCarsku(array.optJSONObject(i)));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Rule initRule(JSONObject object) {
        if (object != null) {
            return new Rule(object);
        }
        return null;
    }


    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }


    public void setCover(Photo cover) {
        this.cover_image = cover;
    }

    @Override
    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public ArrayList<CarSku> getSkus() {
        return skus;
    }

    public ArrayList<Photo> getHeaderPhotos() {
        return headerPhotos;
    }

    public ArrayList<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public int getSoldCount() {
        return soldCount;
    }


    public double getSalePrice() {
        return salePrice;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSkus(ArrayList<CarSku> skus) {
        this.skus = skus;
    }

    public void setHeaderPhotos(ArrayList<Photo> headerPhotos) {
        this.headerPhotos = headerPhotos;
    }

    public void setDetailPhotos(ArrayList<Photo> detailPhotos) {
        this.detailPhotos = detailPhotos;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }


    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Photo getCover_image() {
        return cover_image;
    }

    public void setCover_image(Photo cover_image) {
        this.cover_image = cover_image;
    }

    public String getCover() {
        return JSONUtil.isEmpty(cover) ? (cover_image != null ? cover_image.getPath() : null) :
                cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean is_published() {
        return is_published;
    }

    public void setIs_published(boolean is_published) {
        this.is_published = is_published;
    }

    public Order getLastOrder() {
        return lastOrder;
    }

    public int getCity_code() {
        return city_code;
    }

    public void setCity_code(int city_code) {
        this.city_code = city_code;
    }

    public void setLastOrder(Order lastOrder) {
        this.lastOrder = lastOrder;
    }

    public class Order implements Identifiable {
        public String buyer_phone;
        public int created_at;

        public Order(JSONObject order) {
            this.buyer_phone = order.optString("buyer_phone");
            this.created_at = order.optInt("created_at");
        }

        @Override
        public Long getId() {
            return new Long(0);
        }
    }

    public int getType() {
        return type;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getUrl(){
        return shareInfo==null?null:shareInfo.getUrl();
    }
}
