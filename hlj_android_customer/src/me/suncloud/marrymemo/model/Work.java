package me.suncloud.marrymemo.model;

import android.content.Context;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.models.SecondCategory;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.Installment;
import me.suncloud.marrymemo.util.JSONUtil;

public class Work implements Identifiable {


    private long id;
    private int commodityType;
    private int collectorCount;
    private int commentsCount;
    private int earnestPercent;
    private int cheaperPercent;
    private int watchCount; // 浏览数
    private int mediaItemsCount; // 套餐案例里面包含的图片数量
    private int mediaVideosCount;//视频个数
    private boolean isCollected;
    private boolean allowEarnest;
    private boolean cheaperIfAllIn;
    private String kind;
    private String title;
    private float price;
    private String describe;
    private String coverPath;
    private float marketPrice;
    private double showPrice;
    private String purchaseNotes;
    private NewMerchant merchant;
    private ArrayList<String> services;
    private boolean isSoldOut;
    private String status;
    private Sale sale;
    private Rule rule;
    private float sale_price;
    private int limit_num;
    private int limit_sold_out;
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
    private ShareInfo shareInfo;
    private float saleEarnestMoney; //活动定金
    private float salePayAllPercent; //活动全款折扣
    private double oldPrice;
    private MenuItem property;
    private long propertyId;
    private int hotTag; // 1:人气推荐 2:本季热卖
    private String caseHeadImg; //案例头图
    private long relatedSetMealId; //关联套餐 id 本地转化
    private String subTitle; //套餐副标题

    private boolean isInstallment;  //分期标志位
    private Installment installment; //分期结构

    private double intentPrice; //意向金 大于0且活动中生效，否则依旧判断定金

    private String latestCommentStr;
    private String secondCategoryStr;
    private String lvPaiCity;

    private transient ServiceComment latestComment;
    private transient SecondCategory secondCategory;

    public transient final static int COMMODITY_TYPE_WORK = 0; //套餐
    public transient final static int COMMODITY_TYPE_CASE = 1;  //案例


    public Work(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            collectorCount = json.optInt("collectors_count", 0);
            commodityType = json.optInt("commodity_type", 0);
            commentsCount = json.optInt("comments_count", 0);
            earnestPercent = json.optInt("earnest_percent", 0);
            cheaperPercent = json.optInt("cheaper_percent", 0);
            allowEarnest = json.optInt("allow_earnest") > 0;
            isCollected = json.optBoolean("is_collected", false);
            if (!isCollected) {
                isCollected = json.optInt("is_collected") > 0;
            }
            mediaVideosCount = json.optInt("media_items_video_count", 0);
            isSoldOut = json.optBoolean("is_sold_out", false);
            status = JSONUtil.getString(json, "status");
            cheaperIfAllIn = json.optInt("cheaper_if_all_in") > 0;
            if (!allowEarnest) {
                allowEarnest = json.optBoolean("allow_earnest", false);
            }
            if (!cheaperIfAllIn) {
                cheaperIfAllIn = json.optBoolean("cheaper_if_all_in", false);
            }
            kind = JSONUtil.getString(json, "kind");
            if (JSONUtil.isEmpty(kind)) {
                kind = JSONUtil.getString(json, "property_name");
            }
            title = JSONUtil.getString(json, "title");
            price = (float) json.optDouble("actual_price", 0);
            oldPrice = json.optDouble("price", 0);
            showPrice = json.optDouble("show_price", 0);
            marketPrice = (float) json.optDouble("market_price", 0);
            describe = JSONUtil.getString(json, "describe");
            coverPath = JSONUtil.getString(json, "cover_path");
            purchaseNotes = JSONUtil.getString(json, "purchase_notes");
            if (!json.isNull("merchant")) {
                merchant = new NewMerchant(json.optJSONObject("merchant"));
            }
            latestCommentStr = json.optString("last_comment");
            if (!json.isNull("sale")) {
                sale = new Sale(json.optJSONObject("sale"));
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

            if (!json.isNull("rule")) {
                rule = new Rule(json.optJSONObject("rule"));
            }
            sale_price = (float) json.optDouble("sale_price", 0);
            limit_num = json.optInt("limit_num", -1);
            limit_sold_out = json.optInt("limit_sold_out", 0);

            watchCount = json.optInt("watch_count", 0);
            mediaItemsCount = json.optInt("media_items_count", 0);

            JSONObject promise = json.optJSONObject("promise");
            if (promise != null) {
                promisePath = promise.optString("static_path");
                if (promise.optJSONObject("image") != null) {
                    promiseImage = JSONUtil.getString(promise.optJSONObject("image"), "url");
                }
            }
            JSONArray array = json.optJSONArray("mealInfoValue");
            if (!json.isNull("property")) {
                property = new MenuItem(json.optJSONObject("property"));
            }
            propertyId = json.optLong("propertyId");
            if (array != null && array.length() > 0) {
                parameters = new ArrayList<>();
                showParameters = new ArrayList<>();
                for (int i = 0, size = array.length(); i < size; i++) {
                    WorkParameter parameter = new WorkParameter(array.optJSONObject(i));
                    int property = (int) getPropertyId();
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
                        case 14:
                            int jewelryType = json.optInt("jewelry_type",
                                    0); // 珠宝商家类型 1 钻戒 2 对戒 3 其他（吊坠/手链/宝石等）
                            switch (jewelryType) {
                                case 1:
                                    if ("diamond_main".equals(parameter.getFieldName())) {
                                        ArrayList<String> keys = new ArrayList<>();
                                        keys.add("diamond_main_weight");
                                        keys.add("diamond_main_color");
                                        keys.add("diamond_main_pure");
                                        keys.add("diamond_main_cut");
                                        keys.add("diamond_main_certificate");
                                        showParameters.add(parameter.parentClone(keys));
                                    }
                                    break;
                                case 2:
                                    if ("ring_information".equals(parameter.getFieldName())) {
                                        ArrayList<String> keys = new ArrayList<>();
                                        keys.add("ring_material");
                                        keys.add("ring_check");
                                        keys.add("ring_diamond");
                                        keys.add("ring_cut");
                                        keys.add("ring_certificate");
                                        showParameters.add(parameter.parentClone(keys));
                                    }
                                    break;
                                case 3:
                                    if ("jewelry_information".equals(parameter.getFieldName())) {
                                        ArrayList<String> keys = new ArrayList<>();
                                        keys.add("treasure_style");
                                        keys.add("treasure_shape");
                                        keys.add("treasure_weight");
                                        keys.add("treasure_check");
                                        showParameters.add(parameter.parentClone(keys));
                                    }
                                    break;
                            }
                        case 15:
                            if ("welcome_block".equals(parameter.getFieldName())) {
                                ArrayList<String> keys = new ArrayList<>();
                                keys.add("welcome_area");
                                keys.add("banquet_block");
                                keys.add("banquet_area");
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
            if ("0".equals(orderGift)) {
                orderGift = null;
            }
            if ("0".equals(payAllGift)) {
                payAllGift = null;
            }
            payAllPercent = (float) json.optDouble("pay_all_percent", 0);
            if (!json.isNull("share")) {
                ShareInfo share = new ShareInfo(json.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }
            saleEarnestMoney = (float) json.optDouble("sale_earnest_money", 0);
            salePayAllPercent = (float) json.optDouble("sale_pay_all_percent", 0);
            hotTag = json.optInt("hot_tag", 0);
            subTitle = JSONUtil.getString(json, "sub_title");
            JSONObject jsonObject = json.optJSONObject("related_set_meal");
            if (jsonObject != null && jsonObject.optInt("status") > 0) {
                relatedSetMealId = jsonObject.optLong("id");
            }
            caseHeadImg = JSONUtil.getString(json, "case_head_img");

            isInstallment = JSONUtil.getBoolean(json, "is_installment");
            JSONObject installmentObject = json.optJSONObject("installment");
            if (installmentObject != null) {
                installment = new Installment(installmentObject);
            }

            intentPrice = json.optDouble("intent_price", 0);
            secondCategoryStr = json.optString("second_category");
            lvPaiCity = json.optString("lv_pai_city");
        }
    }

    public double getShowPrice() {
        return showPrice;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCollectorCount() {
        return collectorCount;
    }

    public String getKind() {
        SecondCategory category = getSecondCategory();
        if (category != null && !TextUtils.isEmpty(category.getTitle())) {
            return category.getTitle();
        }
        if (property != null && !JSONUtil.isEmpty(property.getName())) {
            return property.getName();
        }
        if (JSONUtil.isEmpty(kind) && merchant != null) {
            return merchant.getPropertyName();
        }
        return kind;
    }

    public int getCommodityType() {
        return commodityType;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getMerchantId() {
        return merchant != null ? merchant.getId() : 0;
    }

    public String getMerchantName() {
        return merchant != null ? merchant.getName() : null;
    }

    public String getDescribe() {
        if (!JSONUtil.isEmpty(describe)) {
            return describe.trim();
        }
        return describe;
    }

    public String getPurchaseNotes() {
        return purchaseNotes;
    }

    public ServiceComment getLatestComment() {
        if (TextUtils.isEmpty(latestCommentStr)) {
            return null;
        }
        if (latestComment == null) {
            latestComment = GsonUtil.getGsonInstance()
                    .fromJson(latestCommentStr, ServiceComment.class);
        }
        return latestComment;
    }

    public NewMerchant getMerchant() {
        if (merchant == null) {
            merchant = new NewMerchant(new JSONObject());
        }
        return merchant;
    }

    public IconSign getBondSign() {
        if (merchant != null && merchant.getBondSign() != null && !JSONUtil.isEmpty(merchant
                .getBondSign()
                .getIconUrl())) {
            return merchant.getBondSign();
        }
        return null;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public float getMarketPrice() {
        return marketPrice;
    }

    public boolean isAllowEarnest() {
        if (version == 1) {
            return earnestMoney > 0;
        } else {
            return allowEarnest;
        }
    }

    public boolean isCheaperIfAllIn() {
        return cheaperIfAllIn;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public int getCheaperPercent() {
        return cheaperPercent;
    }

    public int getEarnestPercent() {
        return earnestPercent;
    }

    public boolean isSoldOut() {
        return isSoldOut || ((!"published".equals(status)) && (!"1".equals(status)));
    }

    public String getUrl() {
        return Constants.getAbsUrl(commodityType != 1 ? String.format(Constants.HttpPath
                        .WORK_SHARE_URL,
                id) : String.format(Constants.HttpPath.CASES_SHARE_URL, id));
    }

    public Sale getSale() {
        return sale;
    }

    //当活动预告或者限时+满足条件,（注：限时+表示限时或者限时限量）
    public boolean isPreSaleOrOnSale() {
        return (rule != null && rule.getId() != 0 && rule.getStart_time() != null);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
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

    public int getLimit_sold_out() {
        return limit_sold_out;
    }

    public int getLimit_num() {
        return limit_num;
    }

    public String getWatchCount() {
        if (watchCount < 10000) {
            return String.valueOf(watchCount);
        } else {
            double d = watchCount / 10000.d;
            String s = String.format("%.1f", d);
            return s + "万";
        }
    }

    public int getMediaVideosCount() {
        return mediaVideosCount;
    }

    public int getMediaItemsCount() {
        return mediaItemsCount;
    }

    public void setCollectorCount(int collectorCount) {
        this.collectorCount = collectorCount;
    }

    public String getPromiseImage() {
        return promiseImage;
    }

    public String getPromisePath() {
        return promisePath;
    }

    public ArrayList<WorkParameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    public ArrayList<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public int getVersion() {
        return version;
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

    public ShareInfo getShareInfo(Context context) {
        if (shareInfo == null) {
            shareInfo = new ShareInfo(new JSONObject());
            if (commodityType != 1) {
                shareInfo.setTitle(context.getString(R.string.label_work_share));
                shareInfo.setDesc(context.getString(R.string.work_share_msg, title));
                shareInfo.setDesc2(context.getString(R.string.work_share_msg, title));
            } else {
                shareInfo.setTitle(context.getString(R.string.label_opu_share));
                shareInfo.setDesc(context.getString(R.string.opu_share_msg,
                        getMerchantName(),
                        title));
                shareInfo.setDesc2(context.getString(R.string.opu_share_weibo_msg,
                        getMerchantName(),
                        title));
            }
            shareInfo.setIcon(coverPath);
            shareInfo.setUrl(getUrl());
        }
        return shareInfo;
    }

    public ArrayList<WorkParameter> getShowParameters() {
        return showParameters;
    }

    public float getSaleEarnestMoney() {
        //当意向金大于0时活动定金失效
        if (intentPrice > 0) {
            return 0;
        }
        return saleEarnestMoney;
    }

    /**
     * 根据新老订单的版本，有不同的定金计算方法
     * change : 新老订单都取earnest money
     * earnest percent弃用
     *
     * @return
     */
    public double getEarnestMoney() {
        return earnestMoney;
    }

    public float getSalePayAllPercent() {
        return salePayAllPercent;
    }

    public double getOldPrice() {
        if (oldPrice == 0) {
            oldPrice = getShowPrice();
        }
        return oldPrice;
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

    public int getHotTag() {
        return hotTag;
    }

    public long getRelatedSetMealId() {
        return relatedSetMealId;
    }

    public String getCaseHeadImg() {
        if (JSONUtil.isEmpty(caseHeadImg)) {
            return coverPath;
        }
        return caseHeadImg;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public Installment getInstallment() {
        return installment;
    }

    public String getLvPaiCity() {
        return lvPaiCity;
    }

    public double getIntentPrice() {
        return intentPrice;
    }

    /**
     * 判断套餐当前是不是满足意向金支付
     *
     * @return
     */
    public boolean isIntentPayNow() {
        Date date = new Date();
        if (intentPrice > 0 && (rule != null && rule.getId() > 0 && ((rule.getEnd_time() == null
                || rule.getEnd_time()
                .after(date)) && rule.getStart_time() == null || rule.getStart_time()
                .before(date)))) {
            return true;
        } else {
            return false;
        }
    }

    public SecondCategory getSecondCategory() {
        if (CommonUtil.isEmpty(secondCategoryStr)) {
            return null;
        }
        if (secondCategory == null) {
            secondCategory = GsonUtil.getGsonInstance()
                    .fromJson(secondCategoryStr, SecondCategory.class);
        }
        return secondCategory;
    }

}