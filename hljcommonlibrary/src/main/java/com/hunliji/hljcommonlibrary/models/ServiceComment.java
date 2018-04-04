package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 对应着数据库的order_comments表，用来保存服务套餐订单（定制套餐订单）的评价，以及对服务商家的评价（到过店 和 买过套餐）
 * Created by chen_bin on 2017/4/17 0017.
 */
public class ServiceComment extends Comment implements Parcelable {
    @SerializedName(value = "reply_comments")
    private ArrayList<RepliedComment> repliedComments; //评论的回复
    @SerializedName(value = "merchant_info", alternate = "merchant")
    private Merchant merchant;
    @SerializedName(value = "work")
    private Work work; //套餐
    @SerializedName(value = "type")
    private int type;    // 0: 普通套餐,1:定制套餐 3 商家评论
    @SerializedName(value = "comment_type")
    private int commentType; // 2的时候是,婚品评论
    @SerializedName(value = "know_type")
    private int knowType;  //1：去过实体店 、2：买过套餐、3：来自笔记、4：通过婚礼纪购买
    @SerializedName(value = "watch_count")
    private int watchCount;//浏览数
    @SerializedName(value = "order_info")
    private CommonOrderInfo commonOrderInfo;
    @SerializedName(value = "share")
    private ShareInfo shareInfo;
    @SerializedName(value = "appeal_status")
    private int appealStatus;//0 待审核 1 已处理 2未发起申诉

    private transient String subOrderNo; //子订单号
    private transient boolean isRepliesExpanded; //评论的回复是都展开
    private transient int contentStatus;

    public transient final static int KNOW_TYPE_MERCHANT = 1;//去过实体店
    public transient final static int KNOW_TYPE_BOUGHT = 2; //买过套餐
    public transient final static int KNOW_TYPE_FROM_NOTE = 3; //来自笔记
    public transient final static int KNOW_TYPE_BY_HLJ = 4; //通过婚礼纪购买

    public ArrayList<RepliedComment> getRepliedComments() {
        if (repliedComments == null) {
            repliedComments = new ArrayList<>();
        }
        return repliedComments;
    }

    public void setRepliedComments(ArrayList<RepliedComment> repliedComments) {
        this.repliedComments = repliedComments;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    //是否是酒店商家
    private boolean isHotelMerchant() {
        Merchant merchant = getMerchant();
        return merchant != null && merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL;
    }

    public Work getWork() {
        if (work == null) {
            work = new Work();
        }
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public int getType() {
        return type;
    }

    public int getCommentType() {
        return commentType;
    }

    public int getKnowType() {
        return knowType;
    }

    public String getKnowTypeStr() {
        switch (knowType) {
            case KNOW_TYPE_MERCHANT:
                return isHotelMerchant() ? "我到过这家酒店" : "我到过这家店铺";
            case KNOW_TYPE_BOUGHT:
                return isHotelMerchant() ? "通过婚礼纪预订" : "我买过这家套餐";
            case KNOW_TYPE_FROM_NOTE:
                return "来自笔记";
            case KNOW_TYPE_BY_HLJ:
                return "通过婚礼纪购买";
            default:
                return null;
        }
    }

    public void setKnowType(int knowType) {
        this.knowType = knowType;
    }

    public String getWatchCount() {
        if (watchCount < 10000) {
            return String.valueOf(watchCount);
        } else {
            double d = watchCount / 10000.d;
            String s = String.format("%.1f", d);
            return s + "万";
        }
    }

    public CommonOrderInfo getCommonOrderInfo() {
        if (commonOrderInfo == null) {
            commonOrderInfo = new CommonOrderInfo();
        }
        return commonOrderInfo;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public int getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(int appealStatus) {
        this.appealStatus = appealStatus;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public boolean isRepliesExpanded() {
        return isRepliesExpanded;
    }

    public void setRepliesExpanded(boolean repliesExpanded) {
        isRepliesExpanded = repliesExpanded;
    }

    public int getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.repliedComments);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.work, flags);
        dest.writeInt(this.type);
        dest.writeInt(this.commentType);
        dest.writeInt(this.knowType);
        dest.writeInt(this.watchCount);
        dest.writeParcelable(this.commonOrderInfo, flags);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeInt(this.appealStatus);
    }

    public ServiceComment() {}

    protected ServiceComment(Parcel in) {
        super(in);
        this.repliedComments = in.createTypedArrayList(RepliedComment.CREATOR);
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.work = in.readParcelable(Work.class.getClassLoader());
        this.type = in.readInt();
        this.commentType = in.readInt();
        this.knowType = in.readInt();
        this.watchCount = in.readInt();
        this.commonOrderInfo = in.readParcelable(CommonOrderInfo.class.getClassLoader());
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.appealStatus = in.readInt();
    }

    public static final Creator<ServiceComment> CREATOR = new Creator<ServiceComment>() {
        @Override
        public ServiceComment createFromParcel(Parcel source) {
            return new ServiceComment(source);
        }

        @Override
        public ServiceComment[] newArray(int size) {
            return new ServiceComment[size];
        }
    };
}
