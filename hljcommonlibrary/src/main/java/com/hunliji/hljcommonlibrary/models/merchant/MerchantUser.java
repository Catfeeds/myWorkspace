package com.hunliji.hljcommonlibrary.models.merchant;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.HttpHeaderCity;
import com.hunliji.hljcommonlibrary.models.User;


/**
 * Created by werther on 16/7/23.
 * 用户客户端Http用于验证用户信息的基本信息类
 */
public class MerchantUser extends User {
    @SerializedName(value = "user_id")
    long id;
    @SerializedName(value = "id")
    long merchantId;
    @SerializedName(value = "latitude")
    double latitude;
    @SerializedName(value = "longitude")
    double longitude;
    @SerializedName(value = "city")
    String cityName;
    @SerializedName(value = "user_token")
    String token;
    @SerializedName(value = "token")
    String accessToken;
    @SerializedName("logo_path_square")
    String logoPathSquare;
    @SerializedName("logo_path")
    String logoPath;
    @SerializedName("is_pro")
    int isPro;
    @SerializedName("shop_type")
    int shopType;
    int examine;
    int certifyStatus;

    public static final int SHOP_TYPE_SERVICE = 0;
    public static final int SHOP_TYPE_PRODUCT = 1;
    public static final int SHOP_TYPE_CAR = 2;
    public static final int SHOP_TYPE_HOTEL = 3;
    public static final int SHOP_TYPE_SERVICE_PARENT_ACCOUNT = 5; // 服务商家父账号，（客户端不允许登陆）

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getHttpHeadCityStr() {
        Gson gson = new Gson();
        return gson.toJson(new HttpHeaderCity(longitude, latitude, cityName));
    }

    @Override
    public long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isPro() {
        return isPro > 0;
    }

    public void setPro(int pro) {
        isPro = pro;
    }

    public int getIsPro() {
        return isPro;
    }

    public int getShopType() {
        return shopType;
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
    }

    public int getExamine() {
        return examine;
    }

    public void setExamine(int examine) {
        this.examine = examine;
    }

    public int getCertifyStatus() {
        return certifyStatus;
    }

    public void setCertifyStatus(int certifyStatus) {
        this.certifyStatus = certifyStatus;
    }

    @Override
    public String getAvatar() {
        if (!TextUtils.isEmpty(logoPathSquare)) {
            return logoPathSquare;
        }
        return logoPath;
    }

    @Override
    public void setAvatar(String avatar) {
        this.logoPathSquare = avatar;
        super.setAvatar(avatar);
    }

    public MerchantUser() {
        super();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.cityName);
        dest.writeString(this.token);
        dest.writeString(this.accessToken);
        dest.writeString(this.logoPathSquare);
        dest.writeString(this.logoPath);
        dest.writeInt(this.isPro);
        dest.writeInt(this.shopType);
    }

    protected MerchantUser(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.cityName = in.readString();
        this.token = in.readString();
        this.accessToken = in.readString();
        this.logoPathSquare = in.readString();
        this.logoPath = in.readString();
        this.isPro = in.readInt();
        this.shopType = in.readInt();
    }

    public static final Creator<MerchantUser> CREATOR = new Creator<MerchantUser>() {
        @Override
        public MerchantUser createFromParcel(Parcel source) {
            return new MerchantUser(source);
        }

        @Override
        public MerchantUser[] newArray(int size) {return new MerchantUser[size];}
    };
}
