package me.suncloud.marrymemo.model.realm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mo_yu on 2017/5/5.晒婚纱照草稿
 */

public class WeddingPhotoItemDraft extends RealmObject implements Parcelable {

    private String description;
    @SerializedName("photos")
    private RealmList<RealmJsonPic> pics;
    private boolean isCollapseViewOpened; // 列表显示使用的特殊的本地标志位

    public boolean isCollapseViewOpened() {
        return isCollapseViewOpened;
    }

    public void setCollapseViewOpened(boolean collapseViewOpened) {
        isCollapseViewOpened = collapseViewOpened;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<RealmJsonPic> getPics() {
        return pics;
    }

    public ArrayList<Photo> getPhotos() {
        ArrayList<Photo> photos = new ArrayList<>();
        if (pics != null) {
            for (RealmJsonPic pic : pics) {
                photos.add(pic.convertToPhoto());
            }
        }
        return photos;
    }

    public void setPics(RealmList<RealmJsonPic> pics) {
        this.pics = pics;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeList(new ArrayList<>(this.pics));
        dest.writeByte(this.isCollapseViewOpened ? (byte) 1 : (byte) 0);
    }

    public WeddingPhotoItemDraft() {}

    protected WeddingPhotoItemDraft(Parcel in) {
        this.description = in.readString();
        this.pics = new RealmList<>();
        in.readList(pics, RealmJsonPic.class.getClassLoader());
        this.isCollapseViewOpened = in.readByte() != 0;
    }

    public static final Creator<WeddingPhotoItemDraft> CREATOR = new
            Creator<WeddingPhotoItemDraft>() {
        @Override
        public WeddingPhotoItemDraft createFromParcel(Parcel source) {
            return new WeddingPhotoItemDraft(source);
        }

        @Override
        public WeddingPhotoItemDraft[] newArray(int size) {return new WeddingPhotoItemDraft[size];}
    };
}
