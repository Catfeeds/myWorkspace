package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 笔记本类型
 * Created by chen_bin on 2017/6/24 0024.
 */
public class NotebookType implements Parcelable {
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "type")
    private int type;

    public transient static final int TYPE_WEDDING_PHOTO = 1; //婚纱照
    public transient static final int TYPE_WEDDING_PLAN = 2; //婚礼筹备
    public transient static final int TYPE_PRODUCT_PLAN = 3; //婚品筹备
    public transient static final int TYPE_WEDDING_PERSON = 4; //婚礼人

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.type);
    }

    public NotebookType() {}

    public NotebookType(String name) {
        this.name = name;
    }

    public NotebookType(int type,String name){
        this.type = type;
        this.name = name;
    }

    protected NotebookType(Parcel in) {
        this.name = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<NotebookType> CREATOR = new Creator<NotebookType>() {
        @Override
        public NotebookType createFromParcel(Parcel source) {return new NotebookType(source);}

        @Override
        public NotebookType[] newArray(int size) {return new NotebookType[size];}
    };
}
