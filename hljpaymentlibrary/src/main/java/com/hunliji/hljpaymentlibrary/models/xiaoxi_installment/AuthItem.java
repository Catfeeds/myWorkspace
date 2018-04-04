package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljpaymentlibrary.R;

/**
 * 小犀分期-授信认证项
 * Created by chen_bin on 2017/8/16 0016.
 */
public class AuthItem implements Parcelable {
    @SerializedName(value = "name")
    private String name; //认证项名称
    @SerializedName(value = "url")
    private String url;
    @SerializedName(value = "code")
    private int code; //认证项编码
    @SerializedName(value = "status")
    private int status; //认证项状态
    @SerializedName(value = "type")
    private int type; //0表示基础认证项,1表示提额认证项

    public transient final static int TYPE_BASIC = 0; //基础认证项
    public transient final static int TYPE_INCREASE_LIMIT = 1; //提额认证项

    public transient final static int STATUS_AUTHORIZED = 100; //用户已认证
    public transient final static int STATUS_UNAUTHORIZED = 0; //未认证
    public transient final static int STATUS_EXPIRED = 200; //认证已过期

    public transient final static int CODE_REAL_NAME = 101; //实名认证
    public transient final static int CODE_BANK_CARD = 102; //绑定银行卡
    public transient final static int CODE_BASIC_USER_INFO = 2; //基本信息
    public transient final static int CODE_CREDIT_CARD_BILL = 7; //信用卡账单
    public transient final static int CODE_DEPOSIT_CARD_BILL = 8; //储蓄卡流水导入
    public transient final static int CODE_HOUSE_FUND = 9; //公积金认证

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        if (status != STATUS_AUTHORIZED && status != STATUS_UNAUTHORIZED && status !=
                STATUS_EXPIRED) {
            status = STATUS_UNAUTHORIZED;
        }
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuthItemLogo() {
        int resId = 0;
        switch (code) {
            case AuthItem.CODE_REAL_NAME:
                resId = R.mipmap.icon_real_name_68_68___pay;
                break;
            case AuthItem.CODE_BANK_CARD:
                resId = R.mipmap.icon_bank_card_68_68___pay;
                break;
            case AuthItem.CODE_BASIC_USER_INFO:
                resId = R.mipmap.icon_user_info_68_68___pay;
                break;
            case AuthItem.CODE_DEPOSIT_CARD_BILL:
                resId = R.mipmap.icon_deposit_card_68_68___pay;
                break;
            case AuthItem.CODE_HOUSE_FUND:
                resId = R.mipmap.icon_house_fund_68_68___pay;
                break;
            case AuthItem.CODE_CREDIT_CARD_BILL:
                resId = R.mipmap.icon_credit_card_bill_68_68___pay;
                break;
        }
        return resId;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeInt(this.code);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
    }

    public AuthItem() {}

    protected AuthItem(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
        this.code = in.readInt();
        this.status = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<AuthItem> CREATOR = new Creator<AuthItem>() {
        @Override
        public AuthItem createFromParcel(Parcel source) {return new AuthItem(source);}

        @Override
        public AuthItem[] newArray(int size) {return new AuthItem[size];}
    };
}
