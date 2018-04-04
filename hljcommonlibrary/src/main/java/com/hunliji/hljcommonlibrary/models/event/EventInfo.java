package com.hunliji.hljcommonlibrary.models.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 活动model
 * Created by chen_bin on 2016/9/13 0013.
 */
public class EventInfo implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "publish_time")
    private DateTime publishTime;
    @SerializedName(value = "sign_up_end_time")
    private DateTime signUpEndTime;
    @SerializedName(value = "end_time")
    private DateTime endTime;
    @SerializedName(value = "start_time")
    private DateTime startTime;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "content_html")
    private String contentHtml;
    @SerializedName(value = "address")
    private String address;
    @SerializedName(value = "surface_img")
    private String surfaceImg;
    @SerializedName(value = "list_img")
    private String listImg;
    @SerializedName(value = "find_img")
    private String findImg;
    @SerializedName(value = "show_time_title")
    private String showTimeTitle;
    @SerializedName(value = "link")
    private String link;
    @SerializedName(value = "is_pay")
    private boolean isPay;
    @SerializedName(value = "draw_status")
    private boolean drawStatus;
    @SerializedName(value = "status")
    private boolean status;
    @SerializedName(value = "is_sign_up")
    private boolean isNeedSignUp;
    @SerializedName(value = "sign_up_fee")
    private double signUpFee;
    @SerializedName(value = "sign_up_count")
    private int signUpCount;
    @SerializedName(value = "sign_up_outside_count")
    private int signUpOutsideCount; //外部平台报名数
    @SerializedName(value = "sign_up_limit")
    private int signUpLimit;
    @SerializedName(value = "show_sign_up_limit")
    private int showSignUpLimit;
    @SerializedName(value = "watch_count")
    private int watchCount;
    @SerializedName(value = "winner_limit")
    private int winnerLimit;
    @SerializedName(value = "sign_up_new_count")
    private int signUpNewCount;
    @SerializedName(value = "user_sign_up_info")
    private SignUpInfo signUpInfo;
    @SerializedName(value = "merchant")
    private Merchant merchant;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    @SerializedName(value = "user_report_info")
    private ReportInfo reportInfo;
    @SerializedName(value = "summary")
    private String summary;
    @SerializedName(value = "sign_up_new_all_count")
    private int signUpNewAllCount;//新增活动报名数目
    @SerializedName(value = "pay_type")
    private int payType; //活动类型
    @SerializedName(value = "actual_bought_phones")
    private int actualBoughtPhones; //实际购买了号码的报名数
    @SerializedName(value = "create_point")
    private int createPoint; //保底数额
    @SerializedName("popup_img")
    private String popupImg; // 首页弹窗显示的图片

    public transient final static int PAY_TYPE_ONE_PLUS_ONE = 0; //报1计1
    public transient final static int PAY_TYPE_ONLINE_ONE_PLUS_ONE = 1; //上线费+报1计1
    public transient final static int PAY_TYPE_MINIMUM_ONE_PLUS_ONE = 2; //保底+超1计1
    public transient final static int PAY_TYPE_FREE_SIGN = 3; //整场购买，不扣活动点

    public EventInfo() {}

    public DateTime getStartTime() {
        return startTime;
    }

    public long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public DateTime getPublishTime() {return publishTime;}

    public DateTime getSignUpEndTime() {
        return signUpEndTime;
    }

    //判断活动报名是否结束
    public boolean isSignUpEnd() {
        return signUpEndTime != null && signUpEndTime.getMillis() < HljTimeUtils
                .getServerCurrentTimeMillis();
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getTitle() {
        return title;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public String getAddress() {
        return address;
    }

    public String getSurfaceImg() {
        return surfaceImg;
    }

    public String getListImg() {
        return listImg;
    }

    public String getFindImg() {
        return findImg;
    }

    public String getShowTimeTitle() {
        return showTimeTitle;
    }

    public String getLink() {
        return link;
    }

    public boolean isPay() {
        return isPay;
    }

    public boolean isDrawStatus() {
        return drawStatus;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isNeedSignUp() {
        return isNeedSignUp;
    }

    public double getSignUpFee() {
        return signUpFee;
    }

    public int getSignUpCount() {
        return signUpCount;
    }

    public int getSignUpOutsideCount() {
        return signUpOutsideCount;
    }

    public int getSignUpLimit() {
        return signUpLimit;
    }

    public int getShowSignUpLimit() {
        return showSignUpLimit;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getWinnerLimit() {
        return winnerLimit;
    }

    public int getSignUpNewCount() {
        return signUpNewCount;
    }

    public void setSignUpNewCount(int signUpNewCount) {
        this.signUpNewCount = signUpNewCount;
    }

    public SignUpInfo getSignUpInfo() {
        if (signUpInfo == null) {
            signUpInfo = new SignUpInfo();
        }
        return signUpInfo;
    }

    public void setSignUpInfo(SignUpInfo signUpInfo) {
        this.signUpInfo = signUpInfo;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }

    public String getSummary() {
        return summary;
    }

    public int getSignUpNewAllCount() {
        return signUpNewAllCount;
    }

    public void setSignUpNewAllCount(int signUpNewAllCount) {
        this.signUpNewAllCount = signUpNewAllCount;
    }

    public int getPayType() {
        return payType;
    }

    public int getActualBoughtPhones() {
        return actualBoughtPhones;
    }

    public int getCreatePoint() {
        return createPoint;
    }

    public String getPopupImg() {
        return popupImg;
    }

    //活动点不足体术
    public boolean isShowPointLimit() {
        return !isPay() && (getPayType() == PAY_TYPE_ONE_PLUS_ONE || getPayType() ==
                PAY_TYPE_ONLINE_ONE_PLUS_ONE || (getPayType() == PAY_TYPE_MINIMUM_ONE_PLUS_ONE &&
                getActualBoughtPhones() >= getCreatePoint()));
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeSerializable(this.publishTime);
        dest.writeSerializable(this.signUpEndTime);
        dest.writeSerializable(this.endTime);
        dest.writeSerializable(this.startTime);
        dest.writeString(this.title);
        dest.writeString(this.contentHtml);
        dest.writeString(this.address);
        dest.writeString(this.surfaceImg);
        dest.writeString(this.listImg);
        dest.writeString(this.findImg);
        dest.writeString(this.showTimeTitle);
        dest.writeString(this.link);
        dest.writeByte(this.isPay ? (byte) 1 : (byte) 0);
        dest.writeByte(this.drawStatus ? (byte) 1 : (byte) 0);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedSignUp ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.signUpFee);
        dest.writeInt(this.signUpCount);
        dest.writeInt(this.signUpOutsideCount);
        dest.writeInt(this.signUpLimit);
        dest.writeInt(this.showSignUpLimit);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.winnerLimit);
        dest.writeInt(this.signUpNewCount);
        dest.writeParcelable(this.signUpInfo, flags);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeParcelable(this.reportInfo, flags);
        dest.writeString(this.summary);
        dest.writeInt(this.signUpNewAllCount);
        dest.writeInt(this.payType);
        dest.writeInt(this.actualBoughtPhones);
        dest.writeInt(this.createPoint);
        dest.writeString(this.popupImg);
    }

    protected EventInfo(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.publishTime = (DateTime) in.readSerializable();
        this.signUpEndTime = (DateTime) in.readSerializable();
        this.endTime = (DateTime) in.readSerializable();
        this.startTime = (DateTime) in.readSerializable();
        this.title = in.readString();
        this.contentHtml = in.readString();
        this.address = in.readString();
        this.surfaceImg = in.readString();
        this.listImg = in.readString();
        this.findImg = in.readString();
        this.showTimeTitle = in.readString();
        this.link = in.readString();
        this.isPay = in.readByte() != 0;
        this.drawStatus = in.readByte() != 0;
        this.status = in.readByte() != 0;
        this.isNeedSignUp = in.readByte() != 0;
        this.signUpFee = in.readDouble();
        this.signUpCount = in.readInt();
        this.signUpOutsideCount = in.readInt();
        this.signUpLimit = in.readInt();
        this.showSignUpLimit = in.readInt();
        this.watchCount = in.readInt();
        this.winnerLimit = in.readInt();
        this.signUpNewCount = in.readInt();
        this.signUpInfo = in.readParcelable(SignUpInfo.class.getClassLoader());
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.reportInfo = in.readParcelable(ReportInfo.class.getClassLoader());
        this.summary = in.readString();
        this.signUpNewAllCount = in.readInt();
        this.payType = in.readInt();
        this.actualBoughtPhones = in.readInt();
        this.createPoint = in.readInt();
        this.popupImg = in.readString();
    }

    public static final Creator<EventInfo> CREATOR = new Creator<EventInfo>() {
        @Override
        public EventInfo createFromParcel(Parcel source) {return new EventInfo(source);}

        @Override
        public EventInfo[] newArray(int size) {return new EventInfo[size];}
    };
}