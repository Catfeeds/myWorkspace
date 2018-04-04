package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.merchant.Hotel;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.modelwrappers.BondSignWrapper;
import com.hunliji.hljcommonlibrary.models.modelwrappers.ParentArea;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 16/7/27.
 */
public class Merchant implements Parcelable {

    public transient static final int PROPERTY_WEDDING_PLAN = 2; //婚礼策划
    public transient static final int PROPERTY_WEDDING_DRESS_PHOTO = 6;//婚纱摄影
    public transient static final int PROPERTY_WEDDING_PHOTO = 7; //婚礼摄影
    public transient static final int PROPERTY_WEDDING_SHOOTING = 8;//婚礼摄像
    public transient static final int PROPERTY_WEDDING_MAKEUP = 9; //婚礼跟妆
    public transient static final int PROPERTY_WEDDING_COMPERE = 11; //婚礼司仪
    public transient static final int PROPERTY_WEDDING_DRESS = 12; //婚纱礼服
    public transient static final int PROPERTY_WEEDING_HOTEL = 13; //婚宴酒店
    public transient static final int PROPERTY_JEWELRY = 14; //珠宝
    public transient static final int PROPERTY_FLORAL_DESSERT = 15;    // 花艺甜品

    public transient static final int SHOP_TYPE_PRODUCT = 1; //婚品商家
    public transient static final int SHOP_TYPE_HOTEL = 3; //酒店商家
    public transient static final int SHOP_TYPE_CAR = 2; //婚车商家

    public transient static final int MERCHANT_PRO = 1; //专业版
    public transient static final int MERCHANT_ULTIMATE = 2; //旗舰版

    //婚纱摄影二级分类
    public transient static final long WEDDING_DRESS_PHOTO_second_CATEGORY_ID = 1;//婚纱摄影
    public transient static final long WEDDING_DRESS_PHOTO_second_CATEGORY_CHILD_ID = 2;//儿童摄影
    public transient static final long WEDDING_DRESS_PHOTO_second_CATEGORY_PHOTO_ID = 3;//写真摄影
    public transient static final long WEDDING_DRESS_PHOTO_second_CATEGORY_THEME_ID = 4;//旅拍

    long id;
    @SerializedName(value = "user_id")
    long userId;
    float rating = 3;
    String name;
    @SerializedName(value = "desc", alternate = "des")
    String desc;
    String address;
    @SerializedName(value = "logo_path_square", alternate = {"logo"})
    String logoPath;
    @SerializedName(value = "logo_path")
    String logoPath2;
    @SerializedName(value = "cover_path")
    String coverPath;
    double latitude;
    double longitude;
    @SerializedName(value = "active_works_pcount")
    int activeWorkCount;
    @SerializedName(value = "active_cases_pcount")
    int activeCaseCount;
    @SerializedName(value = "last_order_comment")
    ServiceComment latestOrderComment;
    @SerializedName(value = "last_merchant_comment")
    ServiceComment latestMerchantComment;
    @SerializedName(value = "sign")
    BondSignWrapper bondSignWrapper;
    Label property;
    @SerializedName(value = "back_img")
    String backImg;
    JsonElement grade;
    Integer gradeInt;
    @SerializedName(value = "feed_count")
    int feedCount;
    @SerializedName(value = "contact_phones")
    ArrayList<String> contactPhone;
    // 用户端专有字段
    @SerializedName(value = "is_collected")
    boolean isCollected; // 当前用户是否关注
    @SerializedName(value = "share")
    ShareInfo shareInfo;

    @SerializedName(value = "fans_count")
    int fansCount;
    JsonElement privilege;
    List<Work> packages;
    ArrayList<String> merchantPromise; //诺
    ArrayList<String> chargeBack; //退
    @SerializedName(value = "exclusive_offer")
    String exclusiveOffer;  //惠
    @SerializedName(value = "free_order")
    String freeOrder; //免
    @SerializedName(value = "platform_gift")
    String platformGift; //礼
    String exclusiveContent; //惠详情

    Photo privilegeContent;//特权图片
    String guidePath;
    String shopGift;  //到店礼
    String costEffective; //超划算
    @SerializedName("city")
    String cityName;//商家城市
    @SerializedName("city_code")
    long cityCode;//城市id
    @SerializedName(value = "comments_count", alternate = {"order_comments_count"})
    int commentCount; // 这里的评论条数包括：对此商家的评论+对此商家下面所有套餐的所有评论

    @SerializedName("works_count")
    int worksCount; //婚品数

    @SerializedName("hotel")
    Hotel hotel;
    transient private Boolean hasCoupon = null; //商家优惠券标志

    @SerializedName("up_count")
    private int praisedCount;// 点赞数 user信息 用于问答相关

    @SerializedName("merchant_comment")
    CommentStatistics commentStatistics; // 商家评论相关的所有参数 包含近期评价概况


    @SerializedName(value = "property_id")
    long property_id;//商家评论列表 套餐id
    @SerializedName(value = "shop_type")
    int shopType; //同NewMerchant shop_type 商家类型
    @SerializedName(value = "is_new")
    boolean isNew;//新老酒店判断
    @SerializedName(value = "user_commented")
    boolean userCommented;//同NewMerchant userCommented
    @SerializedName(value = "real_photos")
    List<Photo> realPhotos;//店铺照片
    @SerializedName(value = "finder_sub_page")
    TopicUrl topic;// 商家主页的专题 jsonString
    @SerializedName(value = "finder_activity")
    EventInfo eventInfo;// 商家主页的活动 jsonString
    @SerializedName(value = "coupon")
    ArrayList<CouponInfo> coupons;  //商家优惠券的jsonString
    @SerializedName(value = "recommend_meal", alternate = "recommend_meals")
    ArrayList<Work> recommendInfoStr; //商家店铺jsonString
    JsonElement notice;//包含 noticeStr and noticeImgPath
    String noticeStr;
    String noticeImgPath;
    @SerializedName(value = "custom")
    CustomSetmeal customSetmeal;
    @SerializedName(value = "merchant_achievement")
    List<Poster> merchantAchievement;//商家成就
    @SerializedName("merchant_comments_count")
    int merchantCommentsCount; // 对此商家的评论数，不包含商家下的套餐的评论数
    @SerializedName(value = "shop_gift_count")
    int shopGiftCount; //到店礼领取数量
    @SerializedName(value = "is_pro")
    int isPro;
    @SerializedName(value = "shop_area")
    ParentArea shopArea;
    @SerializedName(value = "show_grade")
    private Boolean showGrade;
    transient private String privilegeFeature; //商家酒店个性化特权标志
    private String cpm;
    private transient int couponCount;//优惠券数目，列表使用（只返回数目更加效率）
    @SerializedName(value = "hotel_order_view_count")
    private int hotelOrderViewCount;
    @SerializedName(value = "price_start")
    private double priceStart; //起始价格
    @SerializedName(value = "is_installment")
    private boolean isInstallment; //是否支支持分期
    @SerializedName("good_rating_percent")
    private double goodRatingPercent; // 商家好评率
    @SerializedName("sale_count")
    private int saleCount;
    @SerializedName("is_self_run")
    private boolean isSelfRun;

    public long getProperty_id() {
        return property_id;
    }

    public void setProperty_id(long property_id) {
        this.property_id = property_id;
    }

    private void initPrivilege() {
        try {
            if (this.privilege == null || this.privilege.isJsonNull()) {
                return;
            }
            JSONObject privilege = new JSONObject(this.privilege.toString());
            JSONObject items = privilege.optJSONObject("items");
            this.guidePath = privilege.optString("guide_path");
            this.couponCount = privilege.optInt("coupon_count");
            if (!privilege.isNull("background_img")) {
                JSONObject content = privilege.optJSONObject("background_img");
                Gson gson = new Gson();
                privilegeContent = gson.fromJson(content.toString(), Photo.class);
            }
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

                                String main = valueObj.optString("main");
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
                                String main = valueObj.optString("main");
                                chargeBack.add(main);
                            }
                        }
                    }
                }

                hasCoupon = items.optInt("coupon", 0) > 0;
                if (!hasCoupon) {
                    hasCoupon = items.optBoolean("coupon");
                }
            }

            exclusiveOffer = CommonUtil.getString(privilege, "exclusive_offer");
            freeOrder = privilege.optString("free_order");
            platformGift = privilege.optString("platform_gift");
            exclusiveContent = privilege.optString("content");
            privilegeFeature = CommonUtil.getString(privilege, "feature");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isCoupon() {
        if (hasCoupon == null) {
            initPrivilege();
        }
        return hasCoupon != null && hasCoupon;
    }

    public Hotel getHotel() {
        return hotel;
    }

    /**
     * 这里的评论条数包括：对此商家的评论+对此商家下面所有套餐的所有评论
     *
     * @return
     */
    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public float getRating() {
        return (float) Math.round(rating * 10) / 10;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getAddress() {
        return address;
    }

    public String getLogoPath() {
        if (TextUtils.isEmpty(logoPath)) {
            return logoPath2;
        }
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getActiveWorkCount() {
        return activeWorkCount;
    }

    public int getActiveCaseCount() {
        return activeCaseCount;
    }

    public Comment getLatestOrderComment() {
        return latestOrderComment;
    }

    public Label getProperty() {
        if (property == null) {
            property = new Label();
        }
        return property;
    }

    public String getPropertyName() {
        return property == null ? "" : property.getName();
    }

    public String getBackImg() {
        return backImg;
    }

    public int getGrade() {
        if (showGrade != null && !showGrade) {
            return 0;
        }
        if (gradeInt != null) {
            return gradeInt;
        }
        gradeInt = 0;
        if (grade == null || grade.isJsonNull()) {
            return gradeInt;
        }
        try {
            if (grade.isJsonObject()) {
                gradeInt = ((JsonObject) grade).get("grade_level")
                        .getAsInt();
            } else {
                gradeInt = grade.getAsInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gradeInt;
    }

    public String getUrl() {
        return shareInfo == null ? null : shareInfo.getUrl();
    }

    public int getFeedCount() {
        return feedCount;
    }

    public IconSign getBondSign() {
        return bondSignWrapper == null ? null : bondSignWrapper.getBondSign();
    }

    public String getBondSignUrl() {
        return bondSignWrapper == null ? "" : bondSignWrapper.getBondSignUrl();
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public ArrayList<String> getContactPhone() {
        return contactPhone;
    }

    public List<Work> getPackages() {
        return packages;
    }

    public void setPackages(List<Work> packages) {
        this.packages = packages;
    }


    public String getCostEffective() {
        if (costEffective == null) {
            initPrivilege();
        }
        return costEffective;
    }

    public String getPrivilegeFeature() {
        if (privilegeFeature == null) {
            initPrivilege();
        }
        return privilegeFeature;
    }

    public void setCostEffective(String costEffective) {
        this.costEffective = costEffective;
    }

    public String getShopGift() {
        if (shopGift == null) {
            initPrivilege();
        }
        return shopGift;
    }

    public void setShopGift(String shopGift) {
        this.shopGift = shopGift;
    }

    public String getGuidePath() {
        if (guidePath == null) {
            initPrivilege();
        }
        return guidePath;
    }

    public Photo getPrivilegeContent() {
        if (privilegeContent == null) {
            initPrivilege();
        }
        return privilegeContent;
    }

    public void setGuidePath(String guidePath) {
        this.guidePath = guidePath;
    }

    public String getExclusiveContent() {
        if (exclusiveContent == null) {
            initPrivilege();
        }
        return exclusiveContent;
    }

    public void setExclusiveContent(String exclusiveContent) {
        this.exclusiveContent = exclusiveContent;
    }

    public String getPlatformGift() {
        if (platformGift == null) {
            initPrivilege();
        }
        return platformGift;
    }

    public void setPlatformGift(String platformGift) {
        this.platformGift = platformGift;
    }

    public String getFreeOrder() {
        if (freeOrder == null) {
            initPrivilege();
        }
        return freeOrder;
    }

    public void setFreeOrder(String freeOrder) {
        this.freeOrder = freeOrder;
    }

    public String getExclusiveOffer() {
        if (exclusiveOffer == null) {
            initPrivilege();
        }
        return exclusiveOffer;
    }

    public void setExclusiveOffer(String exclusiveOffer) {
        this.exclusiveOffer = exclusiveOffer;
    }

    public ArrayList<String> getChargeBack() {
        if (chargeBack == null) {
            if (exclusiveOffer == null) {
                initPrivilege();
            }
        }
        return chargeBack;
    }

    public void setChargeBack(ArrayList<String> chargeBack) {
        this.chargeBack = chargeBack;
    }

    public ArrayList<String> getMerchantPromise() {
        if (merchantPromise == null) {
            initPrivilege();
        }
        return merchantPromise;
    }

    public void setMerchantPromise(ArrayList<String> merchantPromise) {
        this.merchantPromise = merchantPromise;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public String getCityName() {
        return cityName;
    }

    public long getCityCode() {
        return cityCode;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public int getWorksCount() {
        return worksCount;
    }

    public int getPraisedCount() {
        return praisedCount;
    }

    public int getShopType() {
        return shopType;
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isUserCommented() {
        return userCommented;
    }

    public void setUserCommented(boolean userCommented) {
        this.userCommented = userCommented;
    }

    public List<Photo> getRealPhotos() {
        if (realPhotos == null) {
            realPhotos = new ArrayList<>();
        }
        return realPhotos;
    }

    public void setRealPhotos(List<Photo> realPhotos) {
        this.realPhotos = realPhotos;
    }

    public TopicUrl getTopic() {
        return topic;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public ArrayList<CouponInfo> getCoupons() {
        return coupons;
    }

    public ArrayList<Work> getNewRecommendMeals() {
        return recommendInfoStr;
    }

    public String getNoticeStr() {
        if (TextUtils.isEmpty(noticeStr)) {
            if (notice != null && notice.isJsonObject()) {
                JsonObject jsonObject = notice.getAsJsonObject();
                if (jsonObject.has("type")) {
                    int type = jsonObject.get("type")
                            .getAsInt();
                    if (type == 1) {
                        noticeStr = jsonObject.get("content")
                                .getAsString();
                    } else if (type == 2) {
                        noticeImgPath = jsonObject.get("content")
                                .getAsString();
                    }
                }
            }
        }
        return noticeStr;
    }

    public String getNoticeImgPath() {
        if (TextUtils.isEmpty(noticeImgPath)) {
            if (notice != null && notice.isJsonObject()) {
                JsonObject jsonObject = notice.getAsJsonObject();
                if (jsonObject.has("type")) {
                    int type = jsonObject.get("type")
                            .getAsInt();
                    if (type == 1) {
                        noticeStr = jsonObject.get("content")
                                .getAsString();
                    } else if (type == 2) {
                        noticeImgPath = jsonObject.get("content")
                                .getAsString();
                    }
                }
            }
        }
        return noticeImgPath;
    }

    public boolean isIndividualMerchant() {
        long propertyId = getPropertyId();
        return propertyId == 7 || propertyId == 8 || propertyId == 9 || propertyId == 11;
    }

    public long getPropertyId() {
        return property == null ? 0 : property.getId();
    }

    /**
     * 对此商家的评论数，不包含商家下的套餐的评论数
     *
     * @return
     */
    public int getMerchantCommentsCount() {
        return merchantCommentsCount;
    }

    /**
     * 转换 MerchantUser 对象 私信使用
     */
    public MerchantUser toUser() {
        return toUser(getShopType());
    }

    public MerchantUser toUser(int shopType) {
        MerchantUser user = new MerchantUser();
        user.setNick(getName());
        user.setId(getUserId());
        user.setAvatar(getLogoPath());
        user.setMerchantId(getId());
        user.setShopType(shopType);
        return user;
    }

    public CustomSetmeal getCustomSetmeal() {
        return customSetmeal;
    }

    public void setCustomSetmeal(CustomSetmeal customSetmeal) {
        this.customSetmeal = customSetmeal;
    }

    public Merchant() {}

    public CommentStatistics getCommentStatistics() {
        return commentStatistics;
    }

    public List<Poster> getMerchantAchievement() {
        if (merchantAchievement == null) {
            merchantAchievement = new ArrayList<>();
        }
        return merchantAchievement;
    }

    public void setMerchantAchievement(List<Poster> merchantAchievement) {
        this.merchantAchievement = merchantAchievement;
    }

    public int getShopGiftCount() {
        return shopGiftCount;
    }

    public int getIsPro() {
        return isPro;
    }

    public void setIsPro(int isPro) {
        this.isPro = isPro;
    }

    public ParentArea getShopArea() {
        if (shopArea == null) {
            shopArea = new ParentArea();
        }
        return shopArea;
    }

    public String getCpm() {
        return cpm;
    }

    public int getCouponCount() {
        return couponCount;
    }

    public int getHotelOrderViewCount() {
        return hotelOrderViewCount;
    }

    public double getPriceStart() {
        return priceStart;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public double getGoodRatingPercent() {
        return goodRatingPercent;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public boolean isSelfRun() {
        return isSelfRun;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        initPrivilege();
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeFloat(this.rating);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.address);
        dest.writeString(this.logoPath);
        dest.writeString(this.logoPath2);
        dest.writeString(this.coverPath);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.activeWorkCount);
        dest.writeInt(this.activeCaseCount);
        dest.writeParcelable(this.latestOrderComment, flags);
        dest.writeParcelable(this.bondSignWrapper, flags);
        dest.writeParcelable(this.property, flags);
        dest.writeString(this.backImg);
        dest.writeInt(this.getGrade());
        dest.writeInt(this.feedCount);
        dest.writeStringList(this.contactPhone);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeInt(this.fansCount);
        dest.writeTypedList(this.packages);
        dest.writeStringList(this.merchantPromise);
        dest.writeStringList(this.chargeBack);
        dest.writeString(this.exclusiveOffer);
        dest.writeString(this.freeOrder);
        dest.writeString(this.platformGift);
        dest.writeString(this.exclusiveContent);
        dest.writeString(this.guidePath);
        dest.writeString(this.shopGift);
        dest.writeString(this.costEffective);
        dest.writeString(this.cityName);
        dest.writeLong(this.cityCode);
        dest.writeInt(this.commentCount);
        dest.writeInt(this.worksCount);
        dest.writeParcelable(this.hotel, flags);
        dest.writeInt(this.praisedCount);
        dest.writeParcelable(this.commentStatistics, flags);
        dest.writeLong(this.property_id);
        dest.writeInt(this.shopType);
        dest.writeByte(this.isNew ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userCommented ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.realPhotos);
        dest.writeInt(this.merchantCommentsCount);
        dest.writeInt(this.shopGiftCount);
        dest.writeInt(this.isPro);
        dest.writeParcelable(this.shopArea, flags);
        dest.writeString(this.cpm);
        dest.writeInt(this.couponCount);
        dest.writeValue(showGrade);
        dest.writeString(privilegeFeature);
        dest.writeInt(this.hotelOrderViewCount);
        dest.writeDouble(this.priceStart);
        dest.writeByte(this.isInstallment ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.goodRatingPercent);
        dest.writeInt(this.saleCount);
        dest.writeByte(this.isSelfRun ? (byte) 1 : (byte) 0);
    }

    protected Merchant(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.rating = in.readFloat();
        this.name = in.readString();
        this.desc = in.readString();
        this.address = in.readString();
        this.logoPath = in.readString();
        this.logoPath2 = in.readString();
        this.coverPath = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.activeWorkCount = in.readInt();
        this.activeCaseCount = in.readInt();
        this.latestOrderComment = in.readParcelable(Comment.class.getClassLoader());
        this.bondSignWrapper = in.readParcelable(BondSignWrapper.class.getClassLoader());
        this.property = in.readParcelable(Label.class.getClassLoader());
        this.backImg = in.readString();
        this.gradeInt = in.readInt();
        this.feedCount = in.readInt();
        this.contactPhone = in.createStringArrayList();
        this.isCollected = in.readByte() != 0;
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.fansCount = in.readInt();
        this.packages = in.createTypedArrayList(Work.CREATOR);
        this.merchantPromise = in.createStringArrayList();
        this.chargeBack = in.createStringArrayList();
        this.exclusiveOffer = in.readString();
        this.freeOrder = in.readString();
        this.platformGift = in.readString();
        this.exclusiveContent = in.readString();
        this.guidePath = in.readString();
        this.shopGift = in.readString();
        this.costEffective = in.readString();
        this.cityName = in.readString();
        this.cityCode = in.readLong();
        this.commentCount = in.readInt();
        this.worksCount = in.readInt();
        this.hotel = in.readParcelable(Hotel.class.getClassLoader());
        this.praisedCount = in.readInt();
        this.commentStatistics = in.readParcelable(CommentStatistics.class.getClassLoader());
        this.property_id = in.readLong();
        this.shopType = in.readInt();
        this.isNew = in.readByte() != 0;
        this.userCommented = in.readByte() != 0;
        this.realPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.merchantCommentsCount = in.readInt();
        this.shopGiftCount = in.readInt();
        this.isPro = in.readInt();
        this.shopArea = in.readParcelable(ParentArea.class.getClassLoader());
        this.cpm = in.readString();
        this.couponCount = in.readInt();
        this.showGrade = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.privilegeFeature = in.readString();
        this.hotelOrderViewCount = in.readInt();
        this.priceStart = in.readDouble();
        this.isInstallment = in.readByte() != 0;
        this.goodRatingPercent = in.readDouble();
        this.saleCount = in.readInt();
        this.isSelfRun = in.readByte() != 0;
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel source) {return new Merchant(source);}

        @Override
        public Merchant[] newArray(int size) {return new Merchant[size];}
    };
}
