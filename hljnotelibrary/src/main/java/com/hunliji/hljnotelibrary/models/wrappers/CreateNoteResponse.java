package com.hunliji.hljnotelibrary.models.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 创建笔记成功
 * Created by chen_bin on 2017/7/17 0017.
 */
public class CreateNoteResponse implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "sync_success")
    private boolean syncSuccess;

    public long getId() {
        return id;
    }

    public boolean isSyncSuccess() {
        return syncSuccess;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.syncSuccess ? (byte) 1 : (byte) 0);
    }

    public CreateNoteResponse() {}

    protected CreateNoteResponse(Parcel in) {
        this.id = in.readLong();
        this.syncSuccess = in.readByte() != 0;
    }

    public static final Creator<CreateNoteResponse> CREATOR = new Creator<CreateNoteResponse>() {
        @Override
        public CreateNoteResponse createFromParcel(Parcel source) {
            return new CreateNoteResponse(source);
        }

        @Override
        public CreateNoteResponse[] newArray(int size) {return new CreateNoteResponse[size];}
    };
}