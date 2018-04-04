package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/5/4.
 * 婚纱晒照组图
 */

public class WeddingPhotoGroup implements Parcelable {
    ArrayList<Photo> photos;
    String describe;
    int praiseCount;

    transient boolean isCollapseViewOpened; // 列表显示使用的特殊的本地标志位

    public WeddingPhotoGroup(ArrayList<Photo> photos, String describe, int praiseCount) {
        this.photos = photos;
        this.describe = describe;
        this.praiseCount = praiseCount;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean isCollapseViewOpened() {
        return isCollapseViewOpened;
    }

    public void setCollapseViewOpened(boolean collapseViewOpened) {
        isCollapseViewOpened = collapseViewOpened;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.photos);
        dest.writeString(this.describe);
        dest.writeInt(this.praiseCount);
    }

    protected WeddingPhotoGroup(Parcel in) {
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.describe = in.readString();
        this.praiseCount = in.readInt();
    }

    public static final Creator<WeddingPhotoGroup> CREATOR = new Creator<WeddingPhotoGroup>() {
        @Override
        public WeddingPhotoGroup createFromParcel(Parcel source) {
            return new WeddingPhotoGroup(source);
        }

        @Override
        public WeddingPhotoGroup[] newArray(int size) {return new WeddingPhotoGroup[size];}
    };
}
