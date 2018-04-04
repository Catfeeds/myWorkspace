package com.hunliji.marrybiz.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/12/13.商家订单子订单
 */

public class MerchantOrderSub implements Parcelable {

    long id;
    @SerializedName("original_money")
    double originalMoney;
    @SerializedName("actual_money")
    double actualMoney;
    @SerializedName("city_code")
    long cityCode;
    String desc;//说明
    @SerializedName("num_type")
    int numType;//0无需改变，如微信小程序（注册）1时间2金额3次数 4期限，如专业版升级为旗舰版
    String num;//支付后获得的时间/金额/次数，和后台统一;专升旗舰时，存入期限日期
    @SerializedName("created_at")
    DateTime createdAt;
    BdProduct product;
    String city;//城市
    @SerializedName("protocol_title")
    String protocolTitle;//协议标题
    @SerializedName("protocol_content")
    String protocolContent;//协议内容链接

    public static final int TIME_TYPE = 1;//时间是月份，12个月
    public static final int AMOUNT_TYPE = 2;//金额 元
    public static final int NUMBER_TYPE = 3;//次数 次
    public static final int DATE_TYPE = 4;//期限是日期 2017-12-13

    public long getId() {
        return id;
    }

    public double getOriginalMoney() {
        return originalMoney == 0 ? actualMoney : originalMoney;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public long getCityCode() {
        return cityCode;
    }

    public String getDesc() {
        return desc;
    }

    public int getNumType() {
        return numType;
    }

    public String getNum() {
        return num;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public BdProduct getProduct() {
        return product;
    }

    public String getCity() {
        return city;
    }

    public String getProtocolTitle() {
        if (!CommonUtil.isEmpty(protocolTitle) && !protocolTitle.startsWith("《")) {
            return "《" + protocolTitle + "》";
        }
        return protocolTitle;
    }

    public String getProtocolContent() {
        return protocolContent;
    }

    public MerchantOrderSub() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.originalMoney);
        dest.writeDouble(this.actualMoney);
        dest.writeLong(this.cityCode);
        dest.writeString(this.desc);
        dest.writeInt(this.numType);
        dest.writeString(this.num);
        dest.writeSerializable(this.createdAt);
        dest.writeParcelable(this.product, flags);
        dest.writeString(this.city);
        dest.writeString(this.protocolTitle);
        dest.writeString(this.protocolContent);
    }

    protected MerchantOrderSub(Parcel in) {
        this.id = in.readLong();
        this.originalMoney = in.readDouble();
        this.actualMoney = in.readDouble();
        this.cityCode = in.readLong();
        this.desc = in.readString();
        this.numType = in.readInt();
        this.num = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.product = in.readParcelable(BdProduct.class.getClassLoader());
        this.city = in.readString();
        this.protocolTitle = in.readString();
        this.protocolContent = in.readString();
    }

    public static final Creator<MerchantOrderSub> CREATOR = new Creator<MerchantOrderSub>() {
        @Override
        public MerchantOrderSub createFromParcel(Parcel source) {
            return new MerchantOrderSub(source);
        }

        @Override
        public MerchantOrderSub[] newArray(int size) {return new MerchantOrderSub[size];}
    };
}
