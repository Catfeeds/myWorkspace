package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class ModifyData implements Parcelable {

    public static final int D_TYPE_MARRY_CARD = 1;
    public static final int D_TYPE_ID_CARD = 2;

    @SerializedName("apply_at")
    DateTime applyAt;
    @SerializedName("bride_new")
    String brideNewName;
    @SerializedName("bride_old")
    String brideOldName;
    @SerializedName("bride_photo")
    String bridePhotoPath;
    @SerializedName("card_id")
    long cardId;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("document_type")
    int documentType; // 证件类型,1结婚证，2身份证
    @SerializedName("groom_new")
    String groomNewName;
    @SerializedName("groom_old")
    String groomOldName;
    @SerializedName("groom_photo")
    String groomPhotoPath;
    @SerializedName("phone")
    String phone;
    @SerializedName("remark")
    String reviewRemark; // 审核结果备注，只有在审核不通过的时候会返回原因
    @SerializedName("status")
    int status;
    @SerializedName("updated_at")
    DateTime updatedAt;

    public DateTime getApplyAt() {
        return applyAt;
    }

    public String getBrideNewName() {
        return brideNewName;
    }

    public String getBrideOldName() {
        return brideOldName;
    }

    public String getBridePhotoPath() {
        return bridePhotoPath;
    }

    public long getCardId() {
        return cardId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public int getDocumentType() {
        return documentType;
    }

    public String getGroomNewName() {
        return groomNewName;
    }

    public String getGroomOldName() {
        return groomOldName;
    }

    public String getGroomPhotoPath() {
        return groomPhotoPath;
    }

    public String getPhone() {
        return phone;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public int getStatus() {
        return status;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.applyAt);
        dest.writeString(this.brideNewName);
        dest.writeString(this.brideOldName);
        dest.writeString(this.bridePhotoPath);
        dest.writeLong(this.cardId);
        dest.writeSerializable(this.createdAt);
        dest.writeInt(this.documentType);
        dest.writeString(this.groomNewName);
        dest.writeString(this.groomOldName);
        dest.writeString(this.groomPhotoPath);
        dest.writeString(this.phone);
        dest.writeString(this.reviewRemark);
        dest.writeInt(this.status);
        dest.writeSerializable(this.updatedAt);
    }

    public ModifyData() {}

    protected ModifyData(Parcel in) {
        this.applyAt = (DateTime) in.readSerializable();
        this.brideNewName = in.readString();
        this.brideOldName = in.readString();
        this.bridePhotoPath = in.readString();
        this.cardId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.documentType = in.readInt();
        this.groomNewName = in.readString();
        this.groomOldName = in.readString();
        this.groomPhotoPath = in.readString();
        this.phone = in.readString();
        this.reviewRemark = in.readString();
        this.status = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<ModifyData> CREATOR = new Parcelable
            .Creator<ModifyData>() {
        @Override
        public ModifyData createFromParcel(Parcel source) {return new ModifyData(source);}

        @Override
        public ModifyData[] newArray(int size) {return new ModifyData[size];}
    };
}
