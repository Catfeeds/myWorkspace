package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/12/26.
 * 修改请帖新郎新娘姓名的申请的结果和修改权限相关数据
 */

public class ModifyNameResult implements Parcelable {

    public static final int STATUS_ALLOW_MODIFY = 0;
    public static final int STATUS_MODIFY_REVIEWING = 1;
    public static final int STATUS_MODIFY_PASS = 2; // 需要上传资料的修改，也是审核成功的标识
    public static final int STATUS_MODIFY_FAIL = 3;
    public static final int STATUS_MODIFY_LOCKED = 4; // 正在提现中，不能修改

    @SerializedName("modify_data")
    ModifyData modifyData;
    // 0，允许修改。1，请帖姓名修改申请已提交，预计1个工作日内审核完成。2，为保证您的资金安全，请上传证件照修改请帖姓名。3，抱歉，请帖姓名修改申请未通过
    @SerializedName("msg")
    String message;
    @SerializedName("status")
    // 0.允许直接修改; 1.审核中；2.修改需要上传；3.修改审核失败
            int status;

    public ModifyData getModifyData() {
        return modifyData;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.modifyData, flags);
        dest.writeString(this.message);
        dest.writeInt(this.status);
    }

    public ModifyNameResult() {}

    protected ModifyNameResult(Parcel in) {
        this.modifyData = in.readParcelable(ModifyData.class.getClassLoader());
        this.message = in.readString();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<ModifyNameResult> CREATOR = new Parcelable
            .Creator<ModifyNameResult>() {
        @Override
        public ModifyNameResult createFromParcel(Parcel source) {
            return new ModifyNameResult(source);
        }

        @Override
        public ModifyNameResult[] newArray(int size) {return new ModifyNameResult[size];}
    };
}