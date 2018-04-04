package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.models.product.wrappers.CollectUsers;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2016/10/28.婚品
 * 主抽取了数据model，业务处理的暂时未抽离
 */

public class ShopProduct implements Parcelable {
    public static final int DELIVER_TIME_48_HOURS = 1;
    public static final int DELIVER_TIME_72_HOURS = 2;
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "describe")
    private String describe;
    @SerializedName("is_collect")
    private boolean isCollect;
    @SerializedName("collectors_count")
    private int collectCount;
    @SerializedName("comments_count")
    private int commentCount;
    @SerializedName(value = "show_price")
    private double showPrice; //当前价格，列表显示
    @SerializedName(value = "price")
    private double oldPrice; //收藏时价格
    @SerializedName(value = "actual_price")
    private double actualPrice; //正常时价格
    @SerializedName(value = "sale_price")
    private double salePrice; //活动时价格
    @SerializedName(value = "market_price")
    private double marketPrice; //市场价
    @SerializedName(value = "cover_image")
    private Photo coverImage;
    @SerializedName("shipping_fee")
    private double shipingFee; //邮费 单个运费 0为包邮
    @SerializedName(value = "free_shipping")
    private FreeShipping freeShipping;
    @SerializedName("shiping")
    private boolean shiping; //是否支付运费
    @SerializedName("can_refund")
    private boolean canRefund; //支持退货
    @SerializedName("is_published")
    private int isPublished; //为1时是上架状态
    @SerializedName(value = "merchant")
    private Merchant merchant;
    @SerializedName(value = "skus")
    private List<Sku> skus;
    @SerializedName(value = "rule")
    private WorkRule rule;
    @SerializedName("collect_users")
    private CollectUsers collectUsers;
    @SerializedName("header_photos")
    private ArrayList<Photo> headerPhotos;
    @SerializedName("detail_photos")
    private ArrayList<Photo> detailPhotos;
    @SerializedName(value = "slogan")
    private List<String> slogans; //商品描述
    @SerializedName("latest_comment")
    private ProductComment lastComment;
    @SerializedName(value = "share")
    private ShareInfo share;
    @SerializedName("is_introduced")
    private boolean isIntroduced; // 直播状态，是否正在被介绍
    @SerializedName("dt_extend")
    private String dtExtend; //婚品统计标识
    @SerializedName("delivery_time")
    private int deliverTimeType; // 1：48小时发货，2：72小时发货
    @SerializedName("product_arguments")
    private ArrayList<ProductParameter> parameters; // 婚品参数
    @SerializedName("good_rating_percent")
    private double goodRatingPercent; // 这个商品的好评率

    private transient int limitCount; // 限量活动商品库存
    private transient int productCount; // 实时商品库存
    private transient double topPrice; // 最高价
    private transient double floorPrice; // 最低价
    private transient int saleStatus; //活动状态本地 0 默认状态；1 活动开始；2活动结束
    private transient boolean isSelected; //当前项是否选中

    public boolean isShiping() {
        return shiping;
    }

    public Photo getCoverImage() {
        if (coverImage != null && !TextUtils.isEmpty(coverImage.getImagePath())) {
            String imagePath = coverImage.getImagePath();
            if (!(imagePath.startsWith("http://") || imagePath.startsWith("https://"))) {
                imagePath = "http://" + imagePath;
                coverImage.setImagePath(imagePath);
            }
        }
        return coverImage;
    }

    public String getCoverPath() {
        Photo photo = getCoverImage();
        return photo == null ? null : photo.getImagePath();
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public long getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getShipingFee() {
        return shipingFee;
    }

    public boolean isCanRefund() {
        return canRefund;
    }

    public boolean isPublished() {
        return isPublished == 1;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public WorkRule getRule() {
        return rule;
    }

    public CollectUsers getCollectUsers() {
        return collectUsers;
    }

    public ArrayList<Photo> getHeaderPhotos() {
        return headerPhotos;
    }

    public ArrayList<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public List<String> getSlogans() {
        return slogans;
    }

    public ProductComment getLastComment() {
        return lastComment;
    }

    public ShareInfo getShare() {
        return share;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public double getFloorPrice() {
        return floorPrice;
    }

    public double getTopPrice() {
        return topPrice;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public FreeShipping getFreeShipping() {
        return freeShipping;
    }

    public double getGoodRatingPercent() {
        return goodRatingPercent;
    }

    /**
     * 从sku中获取商品信息
     */
    public void initProductInfo() {
        if (skus == null) {
            return;
        }
        topPrice = 0;
        floorPrice = 0;
        productCount = 0;
        for (Sku sku : skus) {
            limitCount += sku.getLimitNum() - sku.getLimitSoldOut();
            productCount += Math.max(sku.getShowNum(), 0);
            topPrice = Math.max(topPrice, sku.getShowPrice());
            if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                floorPrice = sku.getShowPrice();
            }
        }
    }

    /**
     * 活动商品开始活动更新商品信息
     */
    public void saleStart() {
        if (skus == null || saleStatus == 1) {
            return;
        }
        saleStatus = 1;
        topPrice = 0;
        floorPrice = 0;
        productCount = 0;
        for (Sku sku : skus) {
            sku.setShowPrice(sku.getSalePrice());
            if (rule != null && rule.getType() == 2) {
                sku.setShowNum(sku.getLimitNum() - sku.getLimitSoldOut());
            }
            productCount += Math.max(sku.getShowNum(), 0);
            topPrice = Math.max(topPrice, sku.getShowPrice());
            if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                floorPrice = sku.getShowPrice();
            }
        }
    }

    public boolean isIntroduced() {
        return isIntroduced;
    }

    /**
     * 活动结束还原商品信息
     */
    public void saleEnd() {
        if (skus == null || saleStatus == 2) {
            return;
        }
        saleStatus = 2;
        topPrice = 0;
        floorPrice = 0;
        productCount = 0;
        for (Sku sku : skus) {
            sku.setShowPrice(sku.getActualPrice());
            if (rule != null && rule.getType() == 2) {
                sku.setShowNum(sku.getQuantity());
            }
            productCount += Math.max(sku.getShowNum(), 0);
            topPrice = Math.max(topPrice, sku.getShowPrice());
            if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                floorPrice = sku.getShowPrice();
            }
        }
    }

    public String getDtExtend() {
        return dtExtend;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getDeliverTimeType() {
        return deliverTimeType;
    }

    public ArrayList<ProductParameter> getParameters() {
        return parameters;
    }

    public ShopProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.title);
        dest.writeString(this.describe);
        dest.writeByte(this.isCollect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.collectCount);
        dest.writeInt(this.commentCount);
        dest.writeDouble(this.showPrice);
        dest.writeDouble(this.oldPrice);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.salePrice);
        dest.writeDouble(this.marketPrice);
        dest.writeParcelable(this.coverImage, flags);
        dest.writeDouble(this.shipingFee);
        dest.writeParcelable(this.freeShipping, flags);
        dest.writeByte(this.shiping ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canRefund ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isPublished);
        dest.writeParcelable(this.merchant, flags);
        dest.writeTypedList(this.skus);
        dest.writeParcelable(this.rule, flags);
        dest.writeParcelable(this.collectUsers, flags);
        dest.writeTypedList(this.headerPhotos);
        dest.writeTypedList(this.detailPhotos);
        dest.writeStringList(this.slogans);
        dest.writeParcelable(this.lastComment, flags);
        dest.writeParcelable(this.share, flags);
        dest.writeByte(this.isIntroduced ? (byte) 1 : (byte) 0);
        dest.writeString(this.dtExtend);
        dest.writeInt(this.deliverTimeType);
        dest.writeTypedList(this.parameters);
        dest.writeDouble(this.goodRatingPercent);
    }

    protected ShopProduct(Parcel in) {
        this.id = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.title = in.readString();
        this.describe = in.readString();
        this.isCollect = in.readByte() != 0;
        this.collectCount = in.readInt();
        this.commentCount = in.readInt();
        this.showPrice = in.readDouble();
        this.oldPrice = in.readDouble();
        this.actualPrice = in.readDouble();
        this.salePrice = in.readDouble();
        this.marketPrice = in.readDouble();
        this.coverImage = in.readParcelable(Photo.class.getClassLoader());
        this.shipingFee = in.readDouble();
        this.freeShipping = in.readParcelable(FreeShipping.class.getClassLoader());
        this.shiping = in.readByte() != 0;
        this.canRefund = in.readByte() != 0;
        this.isPublished = in.readInt();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.skus = in.createTypedArrayList(Sku.CREATOR);
        this.rule = in.readParcelable(WorkRule.class.getClassLoader());
        this.collectUsers = in.readParcelable(CollectUsers.class.getClassLoader());
        this.headerPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.detailPhotos = in.createTypedArrayList(Photo.CREATOR);
        this.slogans = in.createStringArrayList();
        this.lastComment = in.readParcelable(ProductComment.class.getClassLoader());
        this.share = in.readParcelable(ShareInfo.class.getClassLoader());
        this.isIntroduced = in.readByte() != 0;
        this.dtExtend = in.readString();
        this.deliverTimeType = in.readInt();
        this.parameters = in.createTypedArrayList(ProductParameter.CREATOR);
        this.goodRatingPercent = in.readDouble();
    }

    public static final Creator<ShopProduct> CREATOR = new Creator<ShopProduct>() {
        @Override
        public ShopProduct createFromParcel(Parcel source) {return new ShopProduct(source);}

        @Override
        public ShopProduct[] newArray(int size) {return new ShopProduct[size];}
    };
}
