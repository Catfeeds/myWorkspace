package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.marrybiz.view.experience.AdvListActivity;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/12/19.体验店订单
 */

public class AdvDetail implements Parcelable {

    public static final int ORDER_SEARCH = -3;//搜索
    public static final int ORDER_ALL = -2;//全部
    // 数据库对应状态从-1开始。-3和-2本地使用
    public static final int ORDER_UN_SEND = -1;//未派单
    public static final int ORDER_UN_READ = 0;//未查看
    public static final int ORDER_HAVE_READ = 1;//已查看
    public static final int ORDER_HAVE_EXPIRED = 2;//已过期
    public static final int ORDER_COME_SHOP = 3;//到店/跟进
    public static final int ORDER_FOLLOW_UP_FAILED = 4;//跟进失败
    public static final int ORDER_HAVE_CREATE = 5;//已成单
    public static final int ORDER_HAVE_REFUND = 6;//已退单

    long id;
    Admin admin;
    Lead lead;
    @SerializedName("created_at")
    DateTime createdAt;//生成时间
    /**
     * -1未派单， 0未查看，1已查看（是否跟进 last_call_result是否为null），
     * 2已过期，3到店/跟进，4跟进失败， 5已成单（feedback.status 0 成单提交 1:成单通过，2:成单不通过 ）
     * 6已退单（feedback.status 3 退单提交 4 退单通过 5退单不通过）
     */
    int status;//订单状态
    @SerializedName("time_remain")
    long timeRemain;//可查看剩余时间(不使用)
    @SerializedName("is_come")
    boolean isCome;//0未到店 1 到店
    @SerializedName("viewed_at")
    DateTime viewedAt;//查看时间
    @SerializedName("exp_saler")
    String expSaler;//体验店销售
    @SerializedName("order_no")
    String orderNo;//订单编号 
    @SerializedName("ordered_from")
    String orderedFrom;
    @SerializedName(value = "hotel_saler_setup")
    ExperienceSales experienceSales;
    AdvOrder order;
    @SerializedName("expired_at")
    DateTime expiredAt;//体验店推荐过期时间

    public long getId() {
        return id;
    }

    public Admin getAdmin() {
        return admin;
    }

    public Lead getLead() {
        return lead;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeRemain() {
        return timeRemain;
    }

    public boolean isCome() {
        return isCome;
    }

    public void setCome(boolean come) {
        isCome = come;
    }

    public DateTime getViewedAt() {
        return viewedAt;
    }

    public String getExpSaler() {
        return expSaler;
    }

    public void setExpSaler(String expSaler) {
        this.expSaler = expSaler;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOrderedFrom() {
        return orderedFrom;
    }

    public ExperienceSales getExperienceSales() {
        return experienceSales;
    }

    public AdvOrder getOrder() {
        return order;
    }

    public DateTime getExpiredAt() {
        return expiredAt;
    }

    public static String getStatusStringByType(int status, int type) {
        if (type == AdvListActivity.ADV_FOR_EXPERIENCE) {
            // 根据status现实给体验店推荐单的显示状态内容
            switch (status) {
                case AdvDetail.ORDER_HAVE_READ:
                    return "已查看";
                case AdvDetail.ORDER_HAVE_EXPIRED:
                    return "已过期";
                case AdvDetail.ORDER_COME_SHOP:
                    return "已查看";
                case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    return "跟进失败";
                case AdvDetail.ORDER_HAVE_CREATE:
                    return "已成单";
                case AdvDetail.ORDER_HAVE_REFUND:
                    return "已退单";
                default:
                    return "未查看";
            }
        } else {
            // 根据status现实给客资推荐单的显示状态内容
            switch (status) {
                case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    return "跟进失败";
                case AdvDetail.ORDER_HAVE_CREATE:
                    return "已成单";
                default:
                    return "跟进中";
            }
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.admin, flags);
        dest.writeParcelable(this.lead, flags);
        dest.writeSerializable(this.createdAt);
        dest.writeInt(this.status);
        dest.writeLong(this.timeRemain);
        dest.writeByte(this.isCome ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.viewedAt);
        dest.writeString(this.expSaler);
        dest.writeString(this.orderNo);
        dest.writeString(this.orderedFrom);
        dest.writeParcelable(this.experienceSales, flags);
        dest.writeParcelable(this.order, flags);
        HljTimeUtils.writeDateTimeToParcel(dest, expiredAt);
    }

    public AdvDetail() {}

    protected AdvDetail(Parcel in) {
        this.id = in.readLong();
        this.admin = in.readParcelable(Admin.class.getClassLoader());
        this.lead = in.readParcelable(Lead.class.getClassLoader());
        this.createdAt = (DateTime) in.readSerializable();
        this.status = in.readInt();
        this.timeRemain = in.readLong();
        this.isCome = in.readByte() != 0;
        this.viewedAt = (DateTime) in.readSerializable();
        this.expSaler = in.readString();
        this.orderNo = in.readString();
        this.orderedFrom = in.readString();
        this.experienceSales = in.readParcelable(ExperienceSales.class.getClassLoader());
        this.order = in.readParcelable(AdvOrder.class.getClassLoader());
        this.expiredAt = HljTimeUtils.readDateTimeToParcel(in);
    }

    public static final Parcelable.Creator<AdvDetail> CREATOR = new Parcelable.Creator<AdvDetail>
            () {
        @Override
        public AdvDetail createFromParcel(Parcel source) {
            return new AdvDetail(source);
        }

        @Override
        public AdvDetail[] newArray(int size) {return new AdvDetail[size];}
    };
}
