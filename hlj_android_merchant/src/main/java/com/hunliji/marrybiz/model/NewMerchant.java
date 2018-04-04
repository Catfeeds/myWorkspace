package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.JodaTimePermission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class NewMerchant implements Identifiable {

    private long id;
    private long userId;
    private float rating;
    private String name;
    private String address;
    private String logoPath;
    private String latitude;
    private String longitude;
    private Label property;
    private IconSign bondSign;
    private String bondSignUrl;
    private String desc;
    private Comment latestComment;
    private int commentsCount;
    private String coverPath;
    private String backImg;
    private String guidePath;
    private String shopGift;  //到店礼
    private String costEffective; //超划算
    private ArrayList<String> merchantPromise; //诺
    private ArrayList<String> chargeBack; //退
    private int grade;
    private int fansCount;
    private int activeWorkCount;
    private int activeCaseCount;
    private String platformGift; //礼
    private int feedCount;//动态
    private boolean isFocus;

    public NewMerchant(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            activeWorkCount = json.optInt("active_works_pcount", 0);
            activeCaseCount = json.optInt("active_cases_pcount", 0);
            userId = json.optLong("user_id", 0);
            coverPath = JSONUtil.getString(json, "cover_path");
            name = JSONUtil.getString(json, "name");
            address = JSONUtil.getString(json, "address");
            logoPath = JSONUtil.getString(json, "logo_path");
            rating = (float) json.optDouble("rating", 3);
            latitude = json.optString("latitude");
            longitude = json.optString("longitude");
            desc = JSONUtil.getString(json, "desc");
            if (JSONUtil.isEmpty(desc)) {
                desc = JSONUtil.getString(json, "des");
            }
            commentsCount = json.optInt("order_comments_count", 0);
            Comment comment = new Comment(json.optJSONObject(
                    "last_order_comment"));
            if (comment.getId() > 0) {
                latestComment = comment;
            }
            if (!json.isNull("property")) {
                property = new Label(json.optJSONObject("property"));
            }
            if (!json.isNull("sign")) {
                JSONObject jsonObject = json.optJSONObject("sign");
                if (jsonObject != null) {
                    bondSignUrl = JSONUtil.getString(jsonObject,
                            "bond_sign_url");
                    if (!jsonObject.isNull("bond_sign")) {
                        bondSign = new IconSign(jsonObject.optJSONObject(
                                "bond_sign"));
                        if (JSONUtil.isEmpty(bondSign.getIconUrl())) {
                            bondSign = null;
                        }
                    }
                }
            }
            isFocus = json.optInt("is_focus", 0) > 0;
            feedCount = json.optInt("feed_count", 0);
            fansCount = json.optInt("fans_count", 0);
            this.backImg = json.optString("back_img");
            this.grade = json.optInt("grade");
            if (!json.isNull("privilege")) {
                JSONObject privilege = json.optJSONObject("privilege");
                if (privilege != null) {
                    JSONObject items = privilege.optJSONObject("items");
                    this.guidePath = privilege.optString("guide_path");
                    if (items != null) {
                        JSONObject shop_gift = items.optJSONObject("shop_gift");
                        if (shop_gift != null) {
                            this.shopGift = shop_gift.optString("value");
                        }

                        JSONObject cost_effective = items.optJSONObject(
                                "cost_effective");
                        if (cost_effective != null) {
                            this.costEffective = cost_effective.optString(
                                    "value");
                        }

                        JSONObject merchant_promise = items.optJSONObject(
                                "merchant_promise");
                        if (merchant_promise != null) {
                            JSONArray value = merchant_promise.optJSONArray(
                                    "value");
                            merchantPromise = new ArrayList<>();
                            if (value != null) {
                                for (int i = 0; i < value.length(); i++) {
                                    JSONObject valueObj = value.optJSONObject
                                            (i);
                                    if (valueObj != null) {
                                        int id = valueObj.optInt("id");
                                        String main = valueObj.optString
                                                ("main");
                                        merchantPromise.add(main);
                                    }
                                }
                            }
                        }

                        JSONObject charge_back = items.optJSONObject(
                                "charge_back");
                        if (charge_back != null) {
                            JSONArray value = charge_back.optJSONArray("value");
                            chargeBack = new ArrayList<>();
                            if (value != null) {
                                for (int i = 0; i < value.length(); i++) {
                                    JSONObject valueObj = value.optJSONObject
                                            (i);
                                    if (valueObj != null) {
                                        int id = valueObj.optInt("id");
                                        String main = valueObj.optString
                                                ("main");
                                        chargeBack.add(main);
                                    }
                                }
                            }
                        }
                    }
                    platformGift = JSONUtil.getString(privilege,
                            "platform_gift");
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public float getRating() {
        return rating;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPropertyName() {
        return property == null ? null : property.getName();
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public Comment getLatestComment() {
        return latestComment;
    }

    public String getDesc() {
        return desc;
    }

    public IconSign getBondSign() {
        return bondSign;
    }

    public String getBondSignUrl() {
        return bondSignUrl;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public long getUserId() {
        return userId;
    }

    public long getPropertyId() {
        return property == null ? 0 : property.getId();
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public ArrayList<String> getChargeBack() {
        return chargeBack;
    }

    public void setChargeBack(ArrayList<String> chargeBack) {
        this.chargeBack = chargeBack;
    }

    public ArrayList<String> getMerchantPromise() {
        return merchantPromise;
    }

    public void setMerchantPromise(ArrayList<String> merchantPromise) {
        this.merchantPromise = merchantPromise;
    }

    public String getCostEffective() {
        return costEffective;
    }

    public void setCostEffective(String costEffective) {
        this.costEffective = costEffective;
    }

    public String getShopGift() {
        return shopGift;
    }

    public void setShopGift(String shopGift) {
        this.shopGift = shopGift;
    }

    public String getGuidePath() {
        return guidePath;
    }

    public void setGuidePath(String guidePath) {
        this.guidePath = guidePath;
    }

    public String getBackImg() {
        return backImg;
    }

    public void setBackImg(String backImg) {
        this.backImg = backImg;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getActiveWorkCount() {
        return activeWorkCount;
    }

    public int getActiveCaseCount() {
        return activeCaseCount;
    }

    public String getPlatformGift() {
        return platformGift;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }
}


