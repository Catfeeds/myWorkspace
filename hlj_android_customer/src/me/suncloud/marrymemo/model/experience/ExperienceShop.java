package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

/**
 * 体验店model
 * Created by jinxin on 2016/10/28.
 */

public class ExperienceShop implements Parcelable {
    List<ExperiencePhoto> atlas;//图集
    List<ExperienceEvent> choice;//活动
    Store store;//店铺信息

    public List<ExperiencePhoto> getAtlas() {
        return atlas;
    }

    public void setAtlas(List<ExperiencePhoto> atlas) {
        this.atlas = atlas;
    }

    public List<ExperienceEvent> getChoice() {
        return choice;
    }

    public void setChoice(List<ExperienceEvent> choice) {
        this.choice = choice;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.atlas);
        dest.writeTypedList(this.choice);
        dest.writeParcelable(this.store, flags);
    }

    public ExperienceShop() {}

    protected ExperienceShop(Parcel in) {
        this.atlas = in.createTypedArrayList(ExperiencePhoto.CREATOR);
        this.choice = in.createTypedArrayList(ExperienceEvent.CREATOR);
        this.store = in.readParcelable(Store.class.getClassLoader());
    }

    public static final Creator<ExperienceShop> CREATOR = new Creator<ExperienceShop>() {
        @Override
        public ExperienceShop createFromParcel(Parcel source) {return new ExperienceShop(source);}

        @Override
        public ExperienceShop[] newArray(int size) {return new ExperienceShop[size];}
    };
}
