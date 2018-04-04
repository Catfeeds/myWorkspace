package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/8/3.
 */
public class ShopProduct extends BaseProduct {

    private int soldCount;
    private int limitCount;
    private double salePrice;
    private double actualPrice;
    private double marketPrice;
    private String describe;
    private String shopImg;
    private NewMerchant merchant;
    private ArrayList<Sku> skus;
    private ArrayList<Photo> headerPhotos;
    private ArrayList<Photo> detailPhotos;
    private double topPrice;
    private double floorPrice;
    private boolean isOld;
    private boolean shiping;
    private boolean canRefund;
    private Rule rule;
    private ProductComment comment;
    private ArrayList<User> collectUsers;
    private ShareInfo shareInfo;
    private int status;
    private int productCount;
    private boolean isPublished;
    private double oldPrice;
    private double shipingFee;//单个运费 0为包邮
    private FreeShipping freeShipping;

    public ShopProduct(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id");
            this.title = JSONUtil.getString(json, "title");
            this.describe = JSONUtil.getString(json, "describe");
            this.like = json.optBoolean("is_collect");
            this.soldCount = json.optInt("sold_count");
            this.price = json.optDouble("show_price", 0);
            this.oldPrice = json.optDouble("price", 0);
            this.actualPrice = json.optDouble("actual_price", 0);
            this.salePrice = json.optDouble("sale_price", 0);
            this.marketPrice = json.optDouble("market_price", 0);
            this.likeCount = json.optInt("collectors_count");
            this.repliesCount = json.optInt("comments_count");
            this.shopImg = JSONUtil.getString(json, "shop_img");
            this.subjectDesc = JSONUtil.getString(json, "subject_desc");
            this.isOld = json.optBoolean("is_old");
            this.shiping = json.optInt("shiping") == 0;
            this.shipingFee = json.optDouble("shipping_fee", 0);
            this.canRefund = json.optInt("can_refund") > 0;
            this.status = json.optInt("status", 0);
            this.isPublished = json.optInt("is_published") == 1;
            if (!json.isNull("cover_image") && json.optJSONObject("cover_image") != null) {
                JSONObject coverObject = json.optJSONObject("cover_image");
                this.photo = JSONUtil.getString(coverObject, "img");
                if (!JSONUtil.isEmpty(photo) && !photo.startsWith("http")) {
                    photo = "http://" + photo;
                }
                this.width = coverObject.optInt("width");
                this.height = coverObject.optInt("height");
            }
            if (!json.isNull("merchant")) {
                merchant = new NewMerchant(json.optJSONObject("merchant"));
            }
            if (!json.isNull("skus")) {
                JSONArray array = json.optJSONArray("skus");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    skus = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        Sku sku = new Sku(array.optJSONObject(i));
                        limitCount += sku.getLimitNum() - sku.getLimit_sold_out();
                        productCount += Math.max(sku.getShowNum(), 0);
                        if (topPrice < sku.getShowPrice()) {
                            topPrice = sku.getShowPrice();
                        }
                        if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                            floorPrice = sku.getShowPrice();
                        }
                        skus.add(sku);
                    }
                }
            }
            if (!json.isNull("rule")) {
                rule = new Rule(json.optJSONObject("rule"));
            }
            if (!json.isNull("collect_users")) {
                JSONObject jsonObject = json.optJSONObject("collect_users");
                if (jsonObject != null && !jsonObject.isNull("list")) {
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (array != null && array.length() > 0) {
                        int size = array.length();
                        collectUsers = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            collectUsers.add(new User(array.optJSONObject(i)));
                        }
                    }
                }
            }
            if (!json.isNull("header_photos")) {
                JSONArray array = json.optJSONArray("header_photos");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    headerPhotos = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        headerPhotos.add(new Photo(array.optJSONObject(i)));
                    }
                }
            }
            if (!json.isNull("detail_photos")) {
                JSONArray array = json.optJSONArray("detail_photos");
                if (array != null && array.length() > 0) {
                    int size = array.length();
                    detailPhotos = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        detailPhotos.add(new Photo(array.optJSONObject(i)));
                    }
                }
            }

            if (!json.isNull("latest_comment")) {
                comment = new ProductComment(json.optJSONObject("latest_comment"));
            }

            if (!json.isNull("share")) {
                ShareInfo share = new ShareInfo(json.optJSONObject("share"));
                if (!JSONUtil.isEmpty(share.getTitle()) && !JSONUtil.isEmpty(share.getUrl())) {
                    shareInfo = share;
                }
            }

            if (!json.isNull("free_shipping")) {
                JSONObject free = json.optJSONObject("free_shipping");
                FreeShipping freeShipping = new FreeShipping(free);
                this.freeShipping = freeShipping;
            }

            if (JSONUtil.isEmpty(photo)) {
                photo = JSONUtil.getString(json, "cover_path");
            }
        }
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public ArrayList<Photo> getDetailPhotos() {
        return detailPhotos;
    }

    public String getDescribe() {
        return describe;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public ArrayList<Photo> getHeaderPhotos() {
        return headerPhotos;
    }

    public ArrayList<Sku> getSkus() {
        return skus;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }

    public long getMerchantId() {
        return merchant == null ? 0 : merchant.getId();
    }

    public String getMerchantName() {
        return merchant == null ? null : merchant.getName();
    }

    public double getFloorPrice() {
        return floorPrice;
    }

    public double getTopPrice() {
        return topPrice;
    }

    public boolean isOld() {
        return isOld;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void removeShowImage() {
        if (rule != null && !JSONUtil.isEmpty(rule.getShowimg2())) {
            rule.setShowimg(null);
        }
    }

    public ArrayList<User> getCollectUsers() {
        return collectUsers;
    }

    public Rule getRule() {
        return rule;
    }

    public ProductComment getComment() {
        return comment;
    }

    public boolean isCanRefund() {
        return canRefund;
    }

    public boolean isShiping() {
        return shiping;
    }

    public String getShopImg() {
        return shopImg;
    }

    public void setShopImg(String shopImg) {
        this.shopImg = shopImg;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public void saleStart() {
        if (skus != null && !skus.isEmpty()) {
            topPrice = 0;
            floorPrice = 0;
            productCount = 0;
            for (Sku sku : skus) {
                sku.setShowPrice(sku.getSalePrice());
                if (rule != null && rule.getType() == 2) {
                    sku.setShowNum(sku.getLimitNum() - sku.getLimit_sold_out());
                }
                productCount += Math.max(sku.getShowNum(), 0);
                if (topPrice < sku.getShowPrice()) {
                    topPrice = sku.getShowPrice();
                }
                if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                    floorPrice = sku.getShowPrice();
                }
            }
        }
    }


    public void saleEnd() {
        if (skus != null && !skus.isEmpty()) {
            topPrice = 0;
            floorPrice = 0;
            productCount = 0;
            for (Sku sku : skus) {
                sku.setShowPrice(sku.getActualPrice());
                if (rule != null && rule.getType() == 2) {
                    sku.setShowNum(sku.getQuantity());
                }
                productCount += Math.max(sku.getShowNum(), 0);
                if (topPrice < sku.getShowPrice()) {
                    topPrice = sku.getShowPrice();
                }
                if (floorPrice == 0 || floorPrice > sku.getShowPrice()) {
                    floorPrice = sku.getShowPrice();
                }
            }
        }
    }

    public int getStatus() {
        return status;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public String getUrl() {
        return shareInfo == null ? null : shareInfo.getUrl();
    }

    public double getOldPrice() {
        if (oldPrice == 0) {
            oldPrice = getPrice();
        }
        return oldPrice;
    }

    public double getShipingFee() {
        return shipingFee;
    }

    public FreeShipping getFreeShipping() {
        return freeShipping;
    }

    /**
     * 婚品model 转换 用于ShoppingCartItem
     * id
     * title
     * photo
     *
     * @param product 新婚品
     */
    public ShopProduct(
            com.hunliji.hljcommonlibrary.models.product.ShopProduct product) {
        id = product.getId();
        title = product.getTitle();
        photo = product.getCoverPath();
    }
}
