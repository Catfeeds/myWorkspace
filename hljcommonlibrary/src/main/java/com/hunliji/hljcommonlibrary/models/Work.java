package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐model
 * Created by jinxin on 2016/9/23.
 */

public class Work implements Parcelable {

    public static final class SecondCategoryType {
        public static final int LV_PAI = 4;
    }

    long id;
    String title;
    String describe;
    @SerializedName(value = "market_price")
    double marketPrice;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "sale_price")
    double salePrice;
    @SerializedName(value = "limit_num")
    int limitNum;
    @SerializedName(value = "limit_sold_out")
    int limitSoldOut;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "collectors_count")
    int collectorsCount;
    @SerializedName(value = "show_price")
    double showPrice;
    @SerializedName(value = "earnest_money")
    double earnestMoney;
    @SerializedName(value = "is_installment")
    boolean isInstallment;
    @SerializedName(value = "is_collected")
    boolean isCollected;
    @SerializedName(value = "is_sold_out")
    boolean isSoldOut;
    @SerializedName(value = "pay_all_percent")
    double payAllPercent;
    @SerializedName(value = "hot_tag")
    int hotTag;
    @SerializedName(value = "commodity_type")
    int commodityType;
    @SerializedName(value = "sub_title")
    String subTitle;
    String link;
    String city;
    int weight;
    List<Mark> marks;
    Merchant merchant;
    WorkRule rule;
    @SerializedName(value = "show_pay_all_percent")
    double showPayAllPercent;//全款礼 新加的字段对以前的逻辑没影响 showPayAllPercent * showprice 就是全款礼金额
    @SerializedName(value = "reason")
    String reason; //审核不通过的理由
    int status; //审核状态：0审核中 1已发布 3未通过审核
    @SerializedName(value = "sales_count")
    int salesCount; //售出个数
    @SerializedName("media_items_video_count")
    private int mediaVideosCount;//视频个数
    @SerializedName("media_items_count")
    private int mediaItemsCount;// 套餐案例里面包含的图片数量
    @SerializedName("media_items")
    List<WorkMediaItem> mediaItems;
    int version;
    @SerializedName("allow_earnest")
    boolean allowEarnest;
    // 活动状态下的两个价格，在订单快照中没有show_pay_all_percent字段这个计算好的值，只能自行计算
    @SerializedName("sale_earnest_money")
    double saleEarnestMoney;
    @SerializedName("sale_pay_all_percent")
    double salePayAllPercent;
    @SerializedName("pay_all_gift")
    String payAllGift;
    @SerializedName("base_price")
    double basePrice; // 结算价
    @SerializedName("free_trial_yarn")
    boolean isFreeTrialYarn;
    @SerializedName("comments_count")
    int commentsCount;
    @SerializedName("vertical_image")
    String verticalImage;
    @SerializedName(value = "upgrade_marks")
    private List<String> upgradeMarks; //本周热门标签
    private String cpm;
    @SerializedName(value = "is_lvpai")
    private boolean isLvPai;
    @SerializedName(value = "second_category")
    private SecondCategory secondCategory; //二级分类
    @SerializedName("is_introduced")
    private boolean isIntroduced; // 直播状态，是否正在被介绍
    @SerializedName("intent_price")
    private double intentPrice; // 意向金 大于0且活动中生效，否则依旧判断定金,具体逻辑需要参考旧版的Work的model
    @SerializedName("lv_pai_city")
    private String lvPaiCity;
    @SerializedName("second_category_id")
    private long secondCategoryId; //二级分类id
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    @SerializedName("dt_extend")
    private String dtExtend; //套餐/案例统计标识

    transient boolean isHideSearch;

    public transient List<WorkMediaItem> verticalMediaItems;
    public transient List<WorkMediaItem> horizontalMediaItems;
    public transient final static int COMMODITY_TYPE_WORK = 0; //套餐
    public transient final static int COMMODITY_TYPE_CASE = 1;  //案例

    public static final Creator<Work> CREATOR = new Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel in) {
            return new Work(in);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return TextUtils.isEmpty(title) ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public int getLimitSoldOut() {
        return limitSoldOut;
    }

    public void setLimitSoldOut(int limitSoldOut) {
        this.limitSoldOut = limitSoldOut;
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

    public double getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public void setEarnestMoney(double earnestMoney) {
        this.earnestMoney = earnestMoney;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    public void setInstallment(boolean installment) {
        isInstallment = installment;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public double getPayAllPercent() {
        return payAllPercent;
    }

    public void setPayAllPercent(double payAllPercent) {
        this.payAllPercent = payAllPercent;
    }

    public int getHotTag() {
        return hotTag;
    }

    public void setHotTag(int hotTag) {
        this.hotTag = hotTag;
    }

    public int getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(int commodityType) {
        this.commodityType = commodityType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public WorkRule getRule() {
        return rule;
    }

    public void setRule(WorkRule rule) {
        this.rule = rule;
    }

    //当活动预告或者限时+满足条件,（注：限时+表示限时或者限时限量）
    public boolean isPreSaleOrOnSale() {
        return (rule != null && rule.getId() != 0 && rule.getStartTime() != null);
    }

    public double getShowPayAllPercent() {
        return showPayAllPercent;
    }

    public void setShowPayAllPercent(double showPayAllPercent) {
        this.showPayAllPercent = showPayAllPercent;
    }

    public String getPayAllGift() {
        return payAllGift;
    }

    public IconSign getBondSign() {
        if (merchant != null && merchant.getBondSign() != null && !TextUtils.isEmpty(merchant
                .getBondSign()
                .getUrl())) {
            return merchant.getBondSign();
        }
        return null;
    }

    public boolean isAllowEarnest() {
        if (version == 1) {
            return earnestMoney > 0;
        } else {
            return allowEarnest;
        }
    }

    /**
     * 根据给定的订单下单时间，判断是否处于活动期间，返回正确的定金金额
     *
     * @param orderDate
     * @return
     */
    public double getShowEarnestMoney(DateTime orderDate) {
        if (isRuleByDate(orderDate)) {
            return saleEarnestMoney;
        } else
            return earnestMoney;
    }


    /**
     * 根据给定的下单时间，判断是否是活动，返回全款优惠金额
     *
     * @return
     */
    public double getShowPayAllSavedMoney(DateTime orderDate) {
        if (isRuleByDate(orderDate)) {
            return Math.round(salePayAllPercent * salePrice);
        } else {
            return Math.round(payAllPercent * actualPrice);
        }
    }

    public boolean isRuleByDate(DateTime orderDate) {
        if (rule != null && rule.getId() > 0 && ((rule.getEndTime() == null || rule.getEndTime()
                .isAfter(orderDate)) && rule.getStartTime() == null || rule.getStartTime()
                .isBefore(orderDate))) {
            return true;
        } else
            return false;
    }

    public boolean isIntroduced() {
        return isIntroduced;
    }

    public double getSalePayAllPercent() {
        return salePayAllPercent;
    }

    public double getSaleEarnestMoney() {
        return saleEarnestMoney;
    }

    public String getReason() {return reason;}

    public int getStatus() {return status;}

    public int getSalesCount() {return salesCount;}

    public int getMediaVideosCount() {
        return mediaVideosCount;
    }

    public int getMediaItemsCount() {
        return mediaItemsCount;
    }

    public List<WorkMediaItem> getMediaItems() {
        return mediaItems;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public boolean isFreeTrialYarn() {
        return isFreeTrialYarn;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getVerticalImage() {
        return verticalImage;
    }

    public List<String> getUpgradeMarks() {
        return upgradeMarks;
    }

    public String getCpm() {
        return cpm;
    }

    public boolean isLvPai() {
        return secondCategoryId == SecondCategoryType.LV_PAI;
    }

    public SecondCategory getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(SecondCategory secondCategory) {
        this.secondCategory = secondCategory;
    }

    public double getIntentPrice() {
        return intentPrice;
    }

    public String getLvPaiCity() {
        return lvPaiCity;
    }

    public List<WorkMediaItem> getVerticalMediaItems() {
        return verticalMediaItems == null ? new ArrayList<WorkMediaItem>() : verticalMediaItems;
    }

    public void setVerticalMediaItems(List<WorkMediaItem> verticalMediaItems) {
        this.verticalMediaItems = verticalMediaItems;
    }

    public List<WorkMediaItem> getHorizontalMediaItems() {
        return horizontalMediaItems == null ? new ArrayList<WorkMediaItem>() : horizontalMediaItems;
    }

    public void setHorizontalMediaItems(List<WorkMediaItem> horizontalMediaItems) {
        this.horizontalMediaItems = horizontalMediaItems;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public String getDtExtend() {
        return dtExtend;
    }

    public boolean isHideSearch() {
        return isHideSearch;
    }

    public void setHideSearch(boolean hideSearch) {
        isHideSearch = hideSearch;
    }

    public Work() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.describe);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.salePrice);
        dest.writeInt(this.limitNum);
        dest.writeInt(this.limitSoldOut);
        dest.writeString(this.coverPath);
        dest.writeInt(this.collectorsCount);
        dest.writeDouble(this.showPrice);
        dest.writeDouble(this.earnestMoney);
        dest.writeByte(this.isInstallment ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSoldOut ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.payAllPercent);
        dest.writeInt(this.hotTag);
        dest.writeInt(this.commodityType);
        dest.writeString(this.subTitle);
        dest.writeString(this.link);
        dest.writeString(this.city);
        dest.writeInt(this.weight);
        dest.writeTypedList(this.marks);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.rule, flags);
        dest.writeDouble(this.showPayAllPercent);
        dest.writeString(this.reason);
        dest.writeInt(this.status);
        dest.writeInt(this.salesCount);
        dest.writeInt(this.mediaVideosCount);
        dest.writeInt(this.mediaItemsCount);
        dest.writeTypedList(this.mediaItems);
        dest.writeInt(this.version);
        dest.writeByte(this.allowEarnest ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.saleEarnestMoney);
        dest.writeDouble(this.salePayAllPercent);
        dest.writeString(this.payAllGift);
        dest.writeDouble(this.basePrice);
        dest.writeByte(this.isFreeTrialYarn ? (byte) 1 : (byte) 0);
        dest.writeInt(this.commentsCount);
        dest.writeString(this.verticalImage);
        dest.writeStringList(this.upgradeMarks);
        dest.writeString(this.cpm);
        dest.writeByte(this.isLvPai ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.secondCategory, flags);
        dest.writeByte(this.isIntroduced ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.intentPrice);
        dest.writeString(this.lvPaiCity);
        dest.writeLong(this.secondCategoryId);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeString(this.dtExtend);
    }

    protected Work(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.describe = in.readString();
        this.marketPrice = in.readDouble();
        this.actualPrice = in.readDouble();
        this.salePrice = in.readDouble();
        this.limitNum = in.readInt();
        this.limitSoldOut = in.readInt();
        this.coverPath = in.readString();
        this.collectorsCount = in.readInt();
        this.showPrice = in.readDouble();
        this.earnestMoney = in.readDouble();
        this.isInstallment = in.readByte() != 0;
        this.isCollected = in.readByte() != 0;
        this.isSoldOut = in.readByte() != 0;
        this.payAllPercent = in.readDouble();
        this.hotTag = in.readInt();
        this.commodityType = in.readInt();
        this.subTitle = in.readString();
        this.link = in.readString();
        this.city = in.readString();
        this.weight = in.readInt();
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.rule = in.readParcelable(WorkRule.class.getClassLoader());
        this.showPayAllPercent = in.readDouble();
        this.reason = in.readString();
        this.status = in.readInt();
        this.salesCount = in.readInt();
        this.mediaVideosCount = in.readInt();
        this.mediaItemsCount = in.readInt();
        this.mediaItems = in.createTypedArrayList(WorkMediaItem.CREATOR);
        this.version = in.readInt();
        this.allowEarnest = in.readByte() != 0;
        this.saleEarnestMoney = in.readDouble();
        this.salePayAllPercent = in.readDouble();
        this.payAllGift = in.readString();
        this.basePrice = in.readDouble();
        this.isFreeTrialYarn = in.readByte() != 0;
        this.commentsCount = in.readInt();
        this.verticalImage = in.readString();
        this.upgradeMarks = in.createStringArrayList();
        this.cpm = in.readString();
        this.isLvPai = in.readByte() != 0;
        this.secondCategory = in.readParcelable(SecondCategory.class.getClassLoader());
        this.isIntroduced = in.readByte() != 0;
        this.intentPrice = in.readDouble();
        this.lvPaiCity = in.readString();
        this.secondCategoryId = in.readLong();
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.dtExtend = in.readString();
    }

}