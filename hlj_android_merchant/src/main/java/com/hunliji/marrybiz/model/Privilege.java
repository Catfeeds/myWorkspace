package com.hunliji.marrybiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Suncloud on 2016/1/8.
 */
public class Privilege implements Parcelable {

    long id;
    String logo;
    @SerializedName(value = "main_title")
    String name;
    @SerializedName(value = "sub_title")
    String describe;
    @SerializedName(value = "path")
    String url;
    @SerializedName(value = "is_achieved")
    boolean isAchieved;
    @SerializedName(value = "level_achieved")
    boolean levelAchieved;
    int level;
    int status;  //0表示未开启，1表示开启，2表示关闭，3表示审核中，4表示审核未通过
    ArrayList<PrivilegeOption> options;
    String content;
    String reason;
    int rule;
    private int imageId;//本地图片资源id
    private int type;

    public static class Market {
        public static final int TYPE_COUPON = 0;//优惠券
        public static final int TYPE_EYE_SYSTEM = 1;//天眼系统
        public static final int TYPE_POLY_OFF_TREASURE = 2;//聚客宝
        public static final int TYPE_EASY_CHAT = 3;//轻松聊
        public static final int TYPE_EVENT_FLYER = 4;//活动微传单
        public static final int TYPE_PACKAGE_HOT_STANDARD = 5;//套餐热标
        public static final int TYPE_WEDDING_WALL = 6;//婚礼墙
        public static final int TYPE_MIN_PROGRAM = 7;//小程序
        public static final int TYPE_MICRO_WEBSITE = 8;//微官网
        public static final int TYPE_THEME_TEMPLATE = 9;//主题模板
        public static final int TYPE_RECOMMENDED_WINDOW = 10;//推荐橱窗
        public static final int TYPE_SHOP_MANAGER = 11;//多店管理
        public static final int TYPE_SHOP_GIFT = 12;//到店礼
        public static final int TYPE_MERCHANT_PROMISE = 13;//商家承诺
        public static final int TYPE_RETURN_ORDER = 14;//订单可退
    }


    private static final int SHOP_GIFT = 1;//到店礼
    private static final int COST_EFFECTIVE = 4;//超划算
    private static final int MERCHANT_PROMISE = 5;//商家承诺
    private static final int RETURN_ORDER = 6;//订单可退
    private static final int HOT_TAG_SILVER = 17;//银牌商家 套餐热标
    private static final int HOT_TAG_GOLD = 18;//金牌商家 套餐热标
    private static final int COUPON = 20;//优惠券
    private static final int EASY_CHAT = 30;//轻松聊

    /**
     * MarketManagerPrivilegeTypeShopGift          = 1,    ///< 到店礼
     * MarketManagerPrivilegeTypeCostEffective     = 4,    ///< 超划算
     * MarketManagerPrivilegeTypeMerchantPromise   = 5,    ///< 商家承诺
     * MarketManagerPrivilegeTypeReturnOrder       = 6,    ///< 订单可退
     * MarketManagerPrivilegeTypeHotTagSilver      = 17,   ///< 银牌商家 套餐热标
     * MarketManagerPrivilegeTypeHotTagGold        = 18,   ///< 金牌商家 套餐热标
     * MarketManagerPrivilegeTypeCoupon            = 20,   ///< 优惠券
     * MarketManagerPrivilegeTypeEasyChat         = 30, ///< 轻松聊
     */

    public Privilege(String name, int imageId, int type) {
        this.name = name;
        this.imageId = imageId;
        this.type = type;
    }

    public int getImageId() {
        return imageId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        if (id == SHOP_GIFT) {
            return Market.TYPE_SHOP_GIFT;
        } else if (id == MERCHANT_PROMISE) {
            return Market.TYPE_MERCHANT_PROMISE;
        } else if (id == RETURN_ORDER) {
            return Market.TYPE_RETURN_ORDER;
        } else if (id == HOT_TAG_SILVER || id == HOT_TAG_GOLD) {
            return Market.TYPE_PACKAGE_HOT_STANDARD;
        } else if (id == COUPON) {
            return Market.TYPE_COUPON;
        } else if (id == EASY_CHAT) {
            return Market.TYPE_EASY_CHAT;
        }
        return type;
    }

    public Privilege(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            logo = JSONUtil.getString(jsonObject, "logo");
            name = JSONUtil.getString(jsonObject, "main_title");
            describe = JSONUtil.getString(jsonObject, "sub_title");
            url = JSONUtil.getString(jsonObject, "path");
            isAchieved = jsonObject.optInt("is_achieved") > 0;
            levelAchieved = jsonObject.optInt("level_achieved") > 0;
            level = jsonObject.optInt("level");
            status = jsonObject.optInt("status");
        }
    }

    public void editPrivilege(JSONObject jsonObject) {
        if (jsonObject != null) {
            content = JSONUtil.getString(jsonObject, "content");
            reason = JSONUtil.getString(jsonObject, "reason");
            status = jsonObject.optInt("check_status");
            rule = jsonObject.optInt("rule");
            JSONArray array = jsonObject.optJSONArray("option_list");
            if (array != null && array.length() > 0) {
                options = new ArrayList<>();
                for (int i = 0, size = array.length(); i < size; i++) {
                    options.add(new PrivilegeOption(array.optJSONObject(i)));
                }
            }
        }
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getDescribe() {
        return describe;
    }

    public String getUrl() {
        return url;
    }

    public boolean isAchieved() {
        return isAchieved;
    }

    public boolean isLevelAchieved() {
        return levelAchieved;
    }

    public int getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }

    public Long getId() {
        return id;
    }

    public ArrayList<PrivilegeOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<PrivilegeOption> options) {
        this.options = options;
    }

    public int getRule() {
        return rule;
    }

    public String getContent() {
        return content;
    }

    public String getReason() {
        return reason;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.logo);
        dest.writeString(this.name);
        dest.writeString(this.describe);
        dest.writeString(this.url);
        dest.writeByte(this.isAchieved ? (byte) 1 : (byte) 0);
        dest.writeByte(this.levelAchieved ? (byte) 1 : (byte) 0);
        dest.writeInt(this.level);
        dest.writeInt(this.status);
        dest.writeList(this.options);
        dest.writeString(this.content);
        dest.writeString(this.reason);
        dest.writeInt(this.rule);
    }

    public Privilege() {}

    protected Privilege(Parcel in) {
        this.id = in.readLong();
        this.logo = in.readString();
        this.name = in.readString();
        this.describe = in.readString();
        this.url = in.readString();
        this.isAchieved = in.readByte() != 0;
        this.levelAchieved = in.readByte() != 0;
        this.level = in.readInt();
        this.status = in.readInt();
        this.options = new ArrayList<PrivilegeOption>();
        in.readList(this.options, PrivilegeOption.class.getClassLoader());
        this.content = in.readString();
        this.reason = in.readString();
        this.rule = in.readInt();
    }

    public static final Parcelable.Creator<Privilege> CREATOR = new Parcelable.Creator<Privilege>
            () {
        @Override
        public Privilege createFromParcel(Parcel source) {return new Privilege(source);}

        @Override
        public Privilege[] newArray(int size) {return new Privilege[size];}
    };
}
