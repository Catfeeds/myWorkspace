package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Rule;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * 婚车详情
 * Created by jinxin on 2017/12/27 0027.
 */

public class WeddingCarProduct implements Parcelable {

    public static final int TYPE_WORK = 1;//套餐
    public static final int TYPE_SELF = 2;//自选

    long id;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "city_code")
    long cityCode;
    @SerializedName(value = "city")
    City city;
    @SerializedName(value = "cover_image")
    Photo coverImage;
    String describe;
    @SerializedName(value = "detail_photos")
    List<Photo> detailPhotos;
    @SerializedName(value = "header_photos")
    List<Photo> headerPhotos;//轮播头图
    @SerializedName(value = "is_published")
    boolean isPublished;
    @SerializedName(value = "market_price")
    double marketPrice;//市场价
    @SerializedName(value = "rule_id")
    long ruleId;
    Rule rule;//活动信息
    @SerializedName(value = "sale_price")
    double salePrice;
    ShareInfo share;
    @SerializedName(value = "show_price")
    double showPrice;//专享价,秒杀价
    @SerializedName(value = "sold_count")
    int soldCount;
    String title;
    @SerializedName(value = "is_hot_sale")
    boolean isHotSale;
    List<WeddingCarSku> skus;
    @SerializedName(value = "main_car")
    String mainCar;//主婚车
    @SerializedName(value = "sub_car")
    String subCar;//副婚车
    @SerializedName(value = "merchant")
    WeddingCarDetailComment merchantComment;//评论
    @SerializedName(value = "lastOrder")
    WeddingCarOrder lastOrder;
    @SerializedName(value = "is_miaosha")
    boolean isMiaoSha;//秒杀
    @SerializedName("type")
    int type;

    transient String showSubCarTitle;
    transient boolean showBuyerAnim;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public long getCityCode() {
        return cityCode;
    }

    public void setCityCode(long cityCode) {
        this.cityCode = cityCode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Photo getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Photo coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public void setDetailPhotos(List<Photo> detailPhotos) {
        this.detailPhotos = detailPhotos;
    }

    public List<Photo> getHeaderPhotos() {
        return headerPhotos;
    }

    public void setHeaderPhotos(List<Photo> headerPhotos) {
        this.headerPhotos = headerPhotos;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        this.isPublished = published;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public ShareInfo getShare() {
        return share;
    }

    public void setShare(ShareInfo share) {
        this.share = share;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WeddingCarSku> getSkus() {
        return skus;
    }

    public void setSkus(List<WeddingCarSku> skus) {
        this.skus = skus;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public boolean isHotSale() {
        return isHotSale;
    }

    public String getMainCar() {
        return mainCar;
    }

    public void setMainCar(String mainCar) {
        this.mainCar = mainCar;
    }

    public String getShowSubCarTitle() {
        if (!CommonUtil.isEmpty(subCar) && CommonUtil.isEmpty(showSubCarTitle)) {
            String a = subCar.replace("/", "x");
            showSubCarTitle = a.replace("or", "/");
        }
        return showSubCarTitle;
    }

    public WeddingCarDetailComment getMerchantComment() {
        return merchantComment;
    }

    public WeddingCarOrder getLastOrder() {
        return lastOrder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShowBuyerAnim() {
        return showBuyerAnim;
    }

    public void setShowBuyerAnim(boolean showBuyerAnim) {
        this.showBuyerAnim = showBuyerAnim;
    }

    public boolean isMiaoSha() {
        return isMiaoSha;
    }

    public WeddingCarProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.actualPrice);
        dest.writeLong(this.cityCode);
        dest.writeParcelable(this.city, flags);
        dest.writeParcelable(this.coverImage, flags);
        dest.writeString(this.describe);
        dest.writeTypedList(this.detailPhotos);
        dest.writeTypedList(this.headerPhotos);
        dest.writeByte(this.isPublished ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.marketPrice);
        dest.writeLong(this.ruleId);
        dest.writeParcelable(this.rule, flags);
        dest.writeDouble(this.salePrice);
        dest.writeParcelable(this.share, flags);
        dest.writeDouble(this.showPrice);
        dest.writeInt(this.soldCount);
        dest.writeString(this.title);
        dest.writeByte(this.isHotSale ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.skus);
        dest.writeString(this.mainCar);
        dest.writeString(this.subCar);
        dest.writeParcelable(this.merchantComment, flags);
        dest.writeParcelable(this.lastOrder, flags);
        dest.writeByte(this.isMiaoSha ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected WeddingCarProduct(Parcel in) {
        this.id = in.readLong();
        this.actualPrice = in.readDouble();
        this.cityCode = in.readLong();
        this.city = in.readParcelable(City.class.getClassLoader());
        this.coverImage = in.readParcelable(Photo.class.getClassLoader());
        this.describe = in.readString();
        this.detailPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.headerPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.isPublished = in.readByte() != 0;
        this.marketPrice = in.readDouble();
        this.ruleId = in.readLong();
        this.rule = in.readParcelable(Rule.class.getClassLoader());
        this.salePrice = in.readDouble();
        this.share = in.readParcelable(ShareInfo.class.getClassLoader());
        this.showPrice = in.readDouble();
        this.soldCount = in.readInt();
        this.title = in.readString();
        this.isHotSale = in.readByte() != 0;
        this.skus = in.createTypedArrayList(WeddingCarSku.CREATOR);
        this.mainCar = in.readString();
        this.subCar = in.readString();
        this.merchantComment = in.readParcelable(WeddingCarDetailComment.class.getClassLoader());
        this.lastOrder = in.readParcelable(WeddingCarOrder.class.getClassLoader());
        this.isMiaoSha = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<WeddingCarProduct> CREATOR = new Creator<WeddingCarProduct>() {
        @Override
        public WeddingCarProduct createFromParcel(Parcel source) {
            return new WeddingCarProduct(source);
        }

        @Override
        public WeddingCarProduct[] newArray(int size) {return new WeddingCarProduct[size];}
    };
}
