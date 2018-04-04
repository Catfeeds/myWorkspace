package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class Work implements Identifiable {

    private long id;
    private int commodityType;
    private int collectorsCount;
    private int commentsCount;
    private int earnestPercent;
    private int cheaperPercent;
    private String title;
    private String kind;
    private String describe;
    private String purchaseNotes;
    private Comment latestComment;
    private ArrayList<String> services;
    private String coverPath;
    private double actualPrice;
    private double marketPrice;
    private String status;
    private String rejectedReason;
    private boolean isSoldOut;
    private boolean allowEarnest;
    private boolean cheaperIfAllIn;
    private long saleId;
    private Sale sale;
    private boolean isEnd;
    private double hitCount;
    private double price2;
    private Rule rule;
    private float sale_price;
    private double showPrice;
    private int limit_num;
    private int limit_sold_out;
    private NewMerchant merchant;
    private boolean isLvpai;
    private ArrayList<WorkDescribe> workDescribes;
    private boolean isCollected;

    private String promisePath;
    private String promiseImage;
    private ArrayList<WorkParameter> parameters;
    private ArrayList<WorkParameter> showParameters;
    private ArrayList<Photo> detailPhotos;
    private int version;
    private double earnestMoney; //定金
    private String orderGift; //下单礼
    private String payAllGift; //全款礼
    private float payAllPercent; //全款折扣
    private float saleEarnestMoney; //活动定金
    private float salePayAllPercent; //活动全款折扣
    private long propertyId;
    private Label property;

    private boolean isInstallment;  //分期标志位

    public Work(JSONObject json) {
        if (json != null) {
            id = json.optLong("id");
            saleId = json.optLong("sale_id", 0);
            collectorsCount = json.optInt("collectors_count", 0);
            commodityType = json.optInt("commodity_type", 0);
            title = JSONUtil.getString(json, "title");
            coverPath = JSONUtil.getString(json, "cover_path");
            actualPrice = json.optDouble("actual_price", 0);
            kind = JSONUtil.getString(json, "kind");
            marketPrice = json.optDouble("market_price", 0);
            price2 = json.optDouble("actual_price2", 0);
            commentsCount = json.optInt("comments_count", 0);
            describe = JSONUtil.getString(json, "describe");
            purchaseNotes = JSONUtil.getString(json, "purchase_notes");
            status = JSONUtil.getString(json, "status");
            hitCount = json.optDouble("hit_count", 0);

            isSoldOut = json.optBoolean("is_sold_out");
            earnestPercent = json.optInt("earnest_percent");
            cheaperPercent = json.optInt("cheaper_percent");
            cheaperIfAllIn = json.optInt("cheaper_if_all_in") > 0;
            allowEarnest = json.optInt("allow_earnest") > 0;

            if (!json.isNull("property")) {
                property = new Label(json.optJSONObject("property"));
            }
            propertyId = json.optLong("propertyId");
            isCollected = json.optBoolean("is_collected", false);
            if (!isCollected) {
                isCollected = json.optInt("is_collected") > 0;
            }

            Comment comment = new Comment(json.optJSONObject("last_comment"));
            if (comment.getId() > 0) {
                latestComment = comment;
            }
            if (!json.isNull("services")) {
                JSONArray array = json.optJSONArray("services");
                if (array != null) {
                    services = new ArrayList<>();
                    if (array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            services.add(array.optString(i));
                        }
                    }
                }
            }
            if (!json.isNull("sale")) {
                sale = new Sale(json.optJSONObject("sale"));
            }
            rejectedReason = JSONUtil.getString(json, "reason");
            if (!json.isNull("rule")) {
                rule = new Rule(json.optJSONObject("rule"));
            }
            sale_price = (float) json.optDouble("sale_price", 0);

            showPrice = json.optDouble("show_price", 0);
            limit_num = json.optInt("limit_num", -1);
            limit_sold_out = json.optInt("limit_sold_out", 0);
            if (!json.isNull("merchant")) {
                merchant = new NewMerchant(json.optJSONObject("merchant"));
            }
            isLvpai = json.optInt("is_lvpai", 0) > 0;

            if (!json.isNull("work_items")) {
                JSONArray array = json.optJSONArray("work_items");
                if (array != null) {
                    workDescribes = new ArrayList<>();
                    if (array.length() > 0) {
                        int size = array.length();
                        for (int i = 0; i < size; i++) {
                            workDescribes.add(new WorkDescribe(array.optJSONObject(i)));
                        }
                    }
                }
            }
            JSONObject promise = json.optJSONObject("promise");
            if (promise != null) {
                promisePath = promise.optString("static_path");
                if (promise.optJSONObject("image") != null) {
                    promiseImage = JSONUtil.getString(promise.optJSONObject("image"), "url");
                }
            }
            JSONArray array = json.optJSONArray("mealInfoValue");
            if (array != null && array.length() > 0) {
                parameters = new ArrayList<>();
                showParameters = new ArrayList<>();
                for (int i = 0, size = array.length(); i < size; i++) {
                    WorkParameter parameter = new WorkParameter(array.optJSONObject(i));
                    int property = merchant != null ? (int) merchant.getPropertyId() : 0;
                    switch (property) {
                        case 2:
                            if ("team_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("cameraman");
                                keys.add("photographer");
                                keys.add("hoster");
                                keys.add("makeup_artist");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 6:
                            if ("modeling_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("bride_cloth");
                                keys.add("groom_cloth");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("scenes_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("scenes");
                                keys.add("inner_scenes_num");
                                keys.add("outer_scenes_num");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 7:
                            if ("team_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("shooting_flight");
                                keys.add("shoot_team");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("shooting".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("shoot_time");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("product_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("shoot_num");
                                keys.add("sheet_num");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 8:
                            if ("team_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("shooting_flight");
                                keys.add("shoot_team");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("shooting".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("shoot_time");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("product_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("whole_video");
                                keys.add("mv_length");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 9:
                            if ("service_team".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("makeup_artist");
                                keys.add("makeup_assistant");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("bridal_makeup".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("bridal_styling");
                                keys.add("bridal_makeup");
                                keys.add("makeup_time");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 11:
                            if ("team_level".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("hoster");
                                keys.add("music_supervision");
                                keys.add("site_supervision");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("service".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("service_features");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        case 12:
                            if ("clothing_details".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("clothing");
                                keys.add("fitting");
                                showParameters.add(parameter.parentClone(keys));
                            } else if ("service_form".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("sale_way");
                                keys.add("custom_services");
                                showParameters.add(parameter.parentClone(keys));
                            }
                            break;
                        default:
                            if (showParameters.size() < 2) {
                                showParameters.add(parameter);
                            }
                            break;
                    }
                    parameters.add(parameter);
                }
            }
            array = json.optJSONArray("detail_photos");
            if (array != null && array.length() > 0) {
                detailPhotos = new ArrayList<>();
                for (int i = 0, size = array.length(); i < size; i++) {
                    detailPhotos.add(new Photo(array.optJSONObject(i)));
                }
            }
            version = json.optInt("version");
            earnestMoney = json.optDouble("earnest_money", 0);
            orderGift = JSONUtil.getString(json, "order_gift");
            payAllGift = JSONUtil.getString(json, "pay_all_gift");
            payAllPercent = (float) json.optDouble("pay_all_percent", 0);
            if ("0".equals(orderGift)) {
                orderGift = null;
            }
            if ("0".equals(payAllGift)) {
                payAllGift = null;
            }
            saleEarnestMoney = (float) json.optDouble("sale_earnest_money", 0);
            salePayAllPercent = (float) json.optDouble("sale_pay_all_percent", 0);

            isInstallment = JSONUtil.getBoolean(json, "is_installment");
        }
    }

    public String getKind() {
        if (JSONUtil.isEmpty(kind) && merchant != null) {
            return merchant.getPropertyName();
        }
        return kind;
    }

    public double getHitCount() {
        return hitCount;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPurchaseNotes() {
        return purchaseNotes;
    }

    public void setPurchaseNotes(String purchaseNotes) {
        this.purchaseNotes = purchaseNotes;
    }

    public Comment getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(Comment latestComment) {
        this.latestComment = latestComment;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public int getEarnestPercent() {
        return earnestPercent;
    }

    public int getCheaperPercent() {
        return cheaperPercent;
    }

    public boolean isAllowEarnest() {
        return allowEarnest;
    }

    public boolean isCheaperIfAllIn() {
        return cheaperIfAllIn;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }

    public void setCollectorsCount(int collectorsCount) {
        this.collectorsCount = collectorsCount;
    }

    public int getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(int commodityType) {
        this.commodityType = commodityType;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public Sale getSale() {
        return sale;
    }

    public double getOldPrice() {
        return price2 > 0 ? price2 : actualPrice;
    }

    public boolean isOnSale() {
        return !isEnd && saleId > 0 && sale != null && sale.getEndTime() != null && sale
                .getEndTime()
                .after(new Date());
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public float getSale_price() {
        return sale_price;
    }

    public void setSale_price(float sale_price) {
        this.sale_price = sale_price;
    }


    public double getNowPrice() {
        if (rule != null && rule.getId() > 0 && rule.isActivity() && sale_price > 0) {
            return sale_price;
        }
        return actualPrice;
    }

    public double getShowPrice() {
        if (showPrice == 0) {
            showPrice = getNowPrice();
        }
        return showPrice;
    }

    public int getLimit_sold_out() {
        return limit_sold_out;
    }

    public int getLimit_num() {
        return limit_num;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }

    public boolean isLvpai() {
        return isLvpai;
    }

    public ArrayList<WorkDescribe> getWorkDescribes() {
        return workDescribes;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getPromiseImage() {
        return promiseImage;
    }

    public String getPromisePath() {
        return promisePath;
    }

    public ArrayList<WorkParameter> getParameters() {
        return parameters;
    }

    public ArrayList<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public int getVersion() {
        return version;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public float getPayAllPercent() {
        return payAllPercent;
    }

    public String getOrderGift() {
        return orderGift;
    }

    public String getPayAllGift() {
        return payAllGift;
    }

    public ArrayList<WorkParameter> getShowParameters() {
        return showParameters;
    }


    //当活动预告或者限时+满足条件,（注：限时+表示限时或者限时限量）
    public boolean isPreSaleOrOnSale() {
        return (rule != null && rule.getId() != 0 && rule.getStart_time() != null);
    }


    public IconSign getBondSign() {
        if (merchant != null && merchant.getBondSign() != null && !JSONUtil.isEmpty(merchant
                .getBondSign()
                .getIconUrl())) {
            return merchant.getBondSign();
        }
        return null;
    }

    public String getMerchantName() {
        return merchant != null ? merchant.getName() : null;
    }


    public float getSaleEarnestMoney() {
        return saleEarnestMoney;
    }

    public float getSalePayAllPercent() {
        return salePayAllPercent;
    }

    public long getPropertyId() {
        if (propertyId > 0) {
            return propertyId;
        }
        if (property != null && property.getId() > 0) {
            return property.getId();
        }
        if (merchant != null) {
            return merchant.getPropertyId();
        }
        return 0;
    }

    public boolean isInstallment() {
        return isInstallment;
    }
}