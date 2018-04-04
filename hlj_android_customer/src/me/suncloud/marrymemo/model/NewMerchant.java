package me.suncloud.marrymemo.model;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.suncloud.marrymemo.util.JSONUtil;

public class NewMerchant implements Identifiable {

    private long id;
    private long userId;
    private int workCount;
    private int fansCount;
    private int orderCount;
    private int feedCount;
    private int commentsCount; // 这里的评论条数包括：对此商家的评论+对此商家下面所有套餐的所有评论
    private int merchantCommentsCount; // 对此商家的评论数，不包含商家下的套餐的评论数
    private float rating;
    private String name;
    private String desc;
    private String address;
    private String logoPath;
    private String coverPath;
    private String latitude;
    private String longitude;
    private ArrayList<String> contactPhone;
    private int activeWorkCount;
    private int activeCaseCount;
    private boolean isCollected;
    private int saleCount;
    private IconSign bondSign;
    private String bondSignUrl;
    private ShareInfo shareInfo;
    private MenuItem property;
    private long categoryId;
    private String backImg;
    private String guidePath;
    private String shopGift;  //到店礼
    private String costEffective; //超划算
    private ArrayList<String> merchantPromise; //诺
    private ArrayList<String> chargeBack; //退
    private int grade;
    private int shopType;
    private CustomSetmeal customSetmeal;
    private boolean isPromote;
    private boolean coupon; //商家优惠券标志
    private Hotel hotel;
    private String cityName;

    /*
    *酒店优惠
     */
    private String exclusiveOffer;  //惠
    private String freeOrder; //免
    private String platformGift; //礼
    private String exclusiveContent; //惠详情
    private boolean isNew; //新老酒店标识
    private boolean userCommented;

    private String noticeStr;
    private String noticeImgPath;
    private ArrayList<Photo> realPhotos;//店铺照片
    private ArrayList<Work> recommendMeals;//店铺推荐
    private String couponStr; //商家优惠券的jsonString
    private String commentStatisticsStr; //近期评价概况jsonString
    private String latestCommentStr;
    private List<Work> packages;

    private transient List<CouponInfo> coupons;
    private transient CommentStatistics commentStatistics;
    private transient ServiceComment latestComment;
    private transient ArrayList<Poster> achievementPosters; // 商家成就

    public NewMerchant(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            userId = json.optLong("user_id", 0);
            isCollected = json.optBoolean("is_collected", false);
            if (!isCollected) {
                isCollected = json.optInt("is_collected") > 0;
            }
            workCount = json.optInt("works_count", 0);
            fansCount = json.optInt("fans_count", 0);
            orderCount = json.optInt("order_count", 0);
            feedCount = json.optInt("feed_count", 0);
            cityName = JSONUtil.getString(json, "city");
            commentsCount = json.optInt("order_comments_count", 0);
            if (commentsCount <= 0) {
                commentsCount = json.optInt("comments_count", 0);
            }
            merchantCommentsCount = json.optInt("merchant_comments_count", 0);
            rating = (float) Math.round(json.optDouble("rating", 3) * 10) / 10;
            name = JSONUtil.getString(json, "name");
            desc = JSONUtil.getString(json, "desc");
            if (JSONUtil.isEmpty(desc)) {
                desc = JSONUtil.getString(json, "des");
            }
            address = JSONUtil.getString(json, "address");
            logoPath = JSONUtil.getString(json, "logo_path_square");
            if (JSONUtil.isEmpty(logoPath)) {
                logoPath = JSONUtil.getString(json, "logo_path");
            }
            coverPath = JSONUtil.getString(json, "cover_path");
            latitude = JSONUtil.getString(json, "latitude");
            longitude = JSONUtil.getString(json, "longitude");
            activeWorkCount = json.optInt("active_works_pcount", 0);
            activeCaseCount = json.optInt("active_cases_pcount", 0);
            saleCount = json.optInt("sale_count", 0);
            categoryId = json.optLong("category_id");
            this.backImg = json.optString("back_img");
            this.grade = json.optInt("grade");
            if (!json.isNull("property")) {
                property = new MenuItem(json.optJSONObject("property"));
            }
            latestCommentStr = json.optString("last_merchant_comment");
            if (!json.isNull("custom")) {
                CustomSetmeal customSetmeal = new CustomSetmeal(json.optJSONObject("custom"));
                if (customSetmeal.getId() > 0 && customSetmeal.getMerchantId() > 0) {
                    this.customSetmeal = customSetmeal;
                }
            }
            if (!json.isNull("contact_phones")) {
                contactPhone = new ArrayList<>();
                JSONArray array = json.optJSONArray("contact_phones");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        String phone = array.optString(i);
                        if (!JSONUtil.isEmpty(phone)) {
                            contactPhone.add(phone);
                        }
                    }
                }
            }

            if (!json.isNull("share")) {
                ShareInfo share = new ShareInfo(json.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }
            JSONObject jsonObject = json.optJSONObject("sign");
            if (jsonObject != null) {
                bondSignUrl = JSONUtil.getString(jsonObject, "bond_sign_url");
                if (!jsonObject.isNull("bond_sign")) {
                    bondSign = new IconSign(jsonObject.optJSONObject("bond_sign"));
                    if (JSONUtil.isEmpty(bondSign.getIconUrl())) {
                        bondSign = null;
                    }
                }
            }

            JSONObject privilege = json.optJSONObject("privilege");
            if (privilege != null) {
                JSONObject items = privilege.optJSONObject("items");
                this.guidePath = privilege.optString("guide_path");
                if (items != null) {
                    JSONObject shop_gift = items.optJSONObject("shop_gift");
                    if (shop_gift != null) {
                        this.shopGift = shop_gift.optString("value");
                    }

                    JSONObject cost_effective = items.optJSONObject("cost_effective");
                    if (cost_effective != null) {
                        this.costEffective = cost_effective.optString("value");
                    }

                    JSONObject merchant_promise = items.optJSONObject("merchant_promise");
                    if (merchant_promise != null) {
                        JSONArray value = merchant_promise.optJSONArray("value");
                        merchantPromise = new ArrayList<>();
                        if (value != null && value.length() > 0) {
                            for (int i = 0; i < value.length(); i++) {
                                JSONObject valueObj = value.optJSONObject(i);
                                if (valueObj != null) {
                                    //                                    int
                                    // id = valueObj
                                    // .optInt("id");
                                    String main = JSONUtil.getString(valueObj, "main");
                                    merchantPromise.add(main);
                                }
                            }
                        }
                    }

                    JSONObject charge_back = items.optJSONObject("charge_back");
                    if (charge_back != null) {
                        JSONArray value = charge_back.optJSONArray("value");
                        chargeBack = new ArrayList<>();
                        if (value != null && value.length() > 0) {
                            for (int i = 0; i < value.length(); i++) {
                                JSONObject valueObj = value.optJSONObject(i);
                                if (valueObj != null) {
                                    //                                    int
                                    // id = valueObj
                                    // .optInt("id");
                                    String main = JSONUtil.getString(valueObj, "main");
                                    chargeBack.add(main);
                                }
                            }
                        }
                    }
                    coupon = items.optInt("coupon", 0) > 0;
                    if (!coupon) {
                        coupon = JSONUtil.getBoolean(items, "coupon");
                    }
                }
                exclusiveOffer = JSONUtil.getString(privilege, "exclusive_offer");
                freeOrder = JSONUtil.getString(privilege, "free_order");
                platformGift = JSONUtil.getString(privilege, "platform_gift");
                exclusiveContent = JSONUtil.getString(privilege, "content");
            }
            shopType = json.optInt("shop_type", 0);
            JSONObject hotelJson = json.optJSONObject("hotel");
            if (hotelJson != null) {
                Hotel hotel = new Hotel(hotelJson);
                if (hotel.getId() > 0) {
                    this.hotel = hotel;
                }
            }
            isNew = json.optInt("is_new") > 0;
            userCommented = json.optBoolean("user_commented", false);

            JSONObject noticJson = json.optJSONObject("notice");
            if (noticJson != null) {
                int type = noticJson.optInt("type", 0);
                if (type == 1) {
                    noticeStr = JSONUtil.getString(noticJson, "content");
                } else if (type == 2) {
                    noticeImgPath = JSONUtil.getString(noticJson, "content");
                }
            }
            JSONArray realPhotos = json.optJSONArray("real_photos");
            if (realPhotos != null) {
                this.realPhotos = new ArrayList<>();
                for (int i = 0; i < realPhotos.length(); i++) {
                    JSONObject p = realPhotos.optJSONObject(i);
                    Photo photo = new Photo(p);
                    if (!JSONUtil.isEmpty(photo.getPath())) {
                        this.realPhotos.add(photo);
                    }
                }
            }
            JSONArray recommendMeals = json.optJSONArray("recommend_meal");
            if (recommendMeals != null) {
                this.recommendMeals = new ArrayList<>();
                for (int i = 0; i < recommendMeals.length(); i++) {
                    JSONObject meal = recommendMeals.optJSONObject(i);
                    Work work = new Work(meal);
                    if (work.getId() > 0) {
                        this.recommendMeals.add(work);
                    }
                }
            }
            couponStr = json.optString("coupon");
            commentStatisticsStr = json.optString("merchant_comment");
            JSONArray packages = json.optJSONArray("packages");
            if (packages != null) {
                this.packages = new ArrayList<>();
                for (int i = 0, size = packages.length(); i < size; i++) {
                    JSONObject obj = packages.optJSONObject(i);
                    Work work = new Work(obj);
                    if (work.getId() > 0) {
                        this.packages.add(work);
                    }
                }
            }

            JSONArray achievementArray = json.optJSONArray("merchant_achievement");
            if (achievementArray != null) {
                this.achievementPosters = new ArrayList<>();
                Gson gson = GsonUtil.getGsonInstance();
                for (int i = 0; i < achievementArray.length(); i++) {
                    JSONObject achievement = achievementArray.optJSONObject(i);
                    Poster poster = gson.fromJson(achievement.toString(), Poster.class);
                    achievementPosters.add(poster);
                }
            }
        }
    }

    public ArrayList<Poster> getAchievementPosters() {
        if (achievementPosters == null) {
            achievementPosters = new ArrayList<>();
        }
        return achievementPosters;
    }

    public int getActiveWorkCount() {
        return activeWorkCount;
    }

    public int getActiveCaseCount() {
        return activeCaseCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityName() {
        return cityName;
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

    /**
     * 这里的评论条数包括：对此商家的评论+对此商家下面所有套餐的所有评论
     *
     * @return
     */
    public int getCommentsCount() {
        return commentsCount;
    }

    /**
     * 对此商家的评论数，不包含商家下的套餐的评论数
     *
     * @return
     */
    public int getMerchantCommentsCount() {
        return merchantCommentsCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public int getWorkCount() {
        return workCount;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<String> getContactPhone() {
        return contactPhone == null ? new ArrayList<String>() : contactPhone;
    }

    public long getUserId() {
        return userId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public IconSign getBondSign() {
        return bondSign;
    }

    public String getBondSignUrl() {
        return bondSignUrl;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public long getPropertyId() {
        return property == null ? 0 : property.getId();
    }

    public String getPropertyName() {
        return property == null ? null : property.getName();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isIndividualMerchant() {
        long propertyId = getPropertyId();
        return propertyId == 7 || propertyId == 8 || propertyId == 9 || propertyId == 11;
    }

    public String getShopGift() {
        return shopGift;
    }

    public String getGuidePath() {
        if (chargeBack != null && !chargeBack.isEmpty()) {
            return guidePath;
        } else if (merchantPromise != null && !merchantPromise.isEmpty()) {
            return guidePath;
        }
        return null;
    }

    public String getBackImg() {
        return backImg;
    }

    public String getCostEffective() {
        return costEffective;
    }

    public ArrayList<String> getMerchantPromise() {
        if (merchantPromise == null) {
            merchantPromise = new ArrayList<>();
        }
        return merchantPromise;
    }


    public List<Work> getPackages() {
        return packages;
    }

    public ArrayList<String> getChargeBack() {
        if (chargeBack == null) {
            chargeBack = new ArrayList<>();
        }
        return chargeBack;

    }

    public int getGrade() {
        return grade;
    }

    public int getShopType() {
        return shopType;
    }

    public CustomSetmeal getCustomSetmeal() {
        return customSetmeal;
    }

    public boolean isPromote() {
        return isPromote;
    }

    public void setPromote(boolean promote) {
        isPromote = promote;
    }

    public boolean isCoupon() {
        return coupon;
    }

    public String getExclusiveOffer() {
        return exclusiveOffer;
    }

    public String getFreeOrder() {
        return freeOrder;
    }

    public String getPlatformGift() {
        return platformGift;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isUserCommented() {
        return userCommented;
    }

    public String getNoticeStr() {
        return noticeStr;
    }

    public String getNoticeImgPath() {
        return noticeImgPath;
    }


    public String getUrl() {
        return shareInfo == null ? null : shareInfo.getUrl();
    }

    public String getExclusiveContent() {
        return exclusiveContent;
    }

    public ArrayList<Photo> getRealPhotos() {
        return realPhotos;
    }

    public ArrayList<Work> getRecommendMeals() {
        return recommendMeals;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public CommentStatistics getCommentStatistics() {
        if (CommonUtil.isEmpty(commentStatisticsStr)) {
            return null;
        }
        if (commentStatistics == null) {
            commentStatistics = GsonUtil.getGsonInstance()
                    .fromJson(commentStatisticsStr, CommentStatistics.class);
        }
        return commentStatistics;
    }

    //老model解析优惠券列表
    public List<CouponInfo> getCoupons() {
        if (CommonUtil.isEmpty(couponStr)) {
            return null;
        }
        if (coupons == null) {
            coupons = Arrays.asList(GsonUtil.getGsonInstance()
                    .fromJson(couponStr, CouponInfo[].class));
        }
        return coupons;
    }

}
