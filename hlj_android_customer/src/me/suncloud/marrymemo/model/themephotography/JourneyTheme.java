package me.suncloud.marrymemo.model.themephotography;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PosterFloor;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.List;


/**
 * Created by jinxin on 2016/9/20.
 */

public class JourneyTheme implements Parcelable {
    long id;
    String title;
    String remark;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "guide")
    List<Guide> guides;//攻略
    @SerializedName(value = "poster")
    PosterFloor poster;//目的地
    @SerializedName(value = "package")
    List<Work> works;
    @SerializedName(value = "merchant")
    List<Merchant> merchants;//商家
    @SerializedName(value = "watch_count")
    int watchCount;
    public JourneyTheme() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }


    public List<Guide> getGuides() {
        return guides;
    }

    public void setGuides(List<Guide> guides) {
        this.guides = guides;
    }

    public PosterFloor getPoster() {
        return poster;
    }

    public void setPoster(PosterFloor poster) {
        this.poster = poster;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<Merchant> merchants) {
        this.merchants = merchants;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.remark);
        dest.writeString(this.coverPath);
        dest.writeTypedList(this.guides);
        dest.writeParcelable(this.poster, flags);
        dest.writeTypedList(this.works);
        dest.writeTypedList(this.merchants);
        dest.writeInt(this.watchCount);
    }

    protected JourneyTheme(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.remark = in.readString();
        this.coverPath = in.readString();
        this.guides = in.createTypedArrayList(Guide.CREATOR);
        this.poster = in.readParcelable(PosterFloor.class.getClassLoader());
        this.works = in.createTypedArrayList(Work.CREATOR);
        this.merchants = in.createTypedArrayList(Merchant.CREATOR);
        this.watchCount = in.readInt();
    }

    public static final Creator<JourneyTheme> CREATOR = new Creator<JourneyTheme>() {
        @Override
        public JourneyTheme createFromParcel(Parcel source) {
            return new JourneyTheme(source);
        }

        @Override
        public JourneyTheme[] newArray(int size) {return new JourneyTheme[size];}
    };
}
