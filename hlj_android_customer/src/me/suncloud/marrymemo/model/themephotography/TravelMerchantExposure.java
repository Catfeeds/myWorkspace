package me.suncloud.marrymemo.model.themephotography;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅拍品牌专场频道页展示项
 * Created by chen_bin on 2017/5/13 0013.
 */
public class TravelMerchantExposure implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "start_at")
    private DateTime startAt;
    @SerializedName(value = "end_at")
    private DateTime endAt;
    @SerializedName(value = "cover")
    private String cover;
    @SerializedName(value = "target_url")
    private String targetUrl;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "watch_count")
    private int watchCount;
    @SerializedName(value = "set_meals")
    private List<Work> works;

    public long getId() {
        return id;
    }

    public DateTime getStartAt() {
        return startAt;
    }

    public DateTime getEndAt() {
        return endAt;
    }

    public String getCover() {
        return cover;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public List<Work> getWorks() {
        return works;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.startAt);
        dest.writeSerializable(this.endAt);
        dest.writeString(this.cover);
        dest.writeString(this.targetUrl);
        dest.writeString(this.title);
        dest.writeInt(this.watchCount);
        dest.writeTypedList(this.works);
    }

    public TravelMerchantExposure() {}

    protected TravelMerchantExposure(Parcel in) {
        this.id = in.readLong();
        this.startAt = (DateTime) in.readSerializable();
        this.endAt = (DateTime) in.readSerializable();
        this.cover = in.readString();
        this.targetUrl = in.readString();
        this.title = in.readString();
        this.watchCount = in.readInt();
        this.works = in.createTypedArrayList(Work.CREATOR);
    }

    public static final Creator<TravelMerchantExposure> CREATOR = new
            Creator<TravelMerchantExposure>() {
        @Override
        public TravelMerchantExposure createFromParcel(Parcel source) {
            return new TravelMerchantExposure(source);
        }

        @Override
        public TravelMerchantExposure[] newArray(int size) {
            return new TravelMerchantExposure[size];
        }
    };
}
