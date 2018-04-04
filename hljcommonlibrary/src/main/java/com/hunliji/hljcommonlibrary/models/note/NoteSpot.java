package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;

/**
 * spot
 * Created by chen_bin on 2017/6/27 0027.
 */
public class NoteSpot implements Parcelable {
    @SerializedName(value = "price")
    private double price;
    @SerializedName(value = "entity")
    private NoteSpotEntity noteSpotEntity;
    @SerializedName(value = "layout")
    private NoteSpotLayout noteSpotLayout;
    @SerializedName(value = "mark")
    private NoteMark noteMark;

    private transient ArrayList<String> tags;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public NoteSpotEntity getNoteSpotEntity() {
        if (noteSpotEntity == null) {
            noteSpotEntity = new NoteSpotEntity();
        }
        return noteSpotEntity;
    }

    public void setNoteSpotEntity(NoteSpotEntity noteSpotEntity) {
        this.noteSpotEntity = noteSpotEntity;
    }

    public NoteSpotLayout getNoteSpotLayout() {
        if (noteSpotLayout == null) {
            noteSpotLayout = new NoteSpotLayout();
        }
        return noteSpotLayout;
    }

    public void setNoteSpotLayout(NoteSpotLayout noteSpotLayout) {
        this.noteSpotLayout = noteSpotLayout;
    }

    public NoteMark getNoteMark() {
        if (noteMark == null) {
            noteMark = new NoteMark();
        }
        return noteMark;
    }

    public void setNoteMark(NoteMark noteMark) {
        this.noteMark = noteMark;
    }

    public ArrayList<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.isEmpty()) {
            return tags;
        }
        if (!TextUtils.isEmpty(getNoteSpotEntity().getTitle())) {
            tags.add(getNoteSpotEntity().getTitle());
        }
        if (!TextUtils.isEmpty(getNoteMark().getName())) {
            tags.add(getNoteMark().getName());
        }
        if (price > 0) {
            tags.add(CommonUtil.formatDouble2String(price) + "元");
        }
        return tags;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.price);
        dest.writeParcelable(this.noteSpotEntity, flags);
        dest.writeParcelable(this.noteSpotLayout, flags);
        dest.writeParcelable(this.noteMark, flags);
    }

    public NoteSpot() {}

    public NoteSpot(NoteSpotLayout noteSpotLayout) {
        this.noteSpotLayout = noteSpotLayout;
    }

    protected NoteSpot(Parcel in) {
        this.price = in.readDouble();
        this.noteSpotEntity = in.readParcelable(NoteSpotEntity.class.getClassLoader());
        this.noteSpotLayout = in.readParcelable(NoteSpotLayout.class.getClassLoader());
        this.noteMark = in.readParcelable(NoteMark.class.getClassLoader());
    }

    public static final Creator<NoteSpot> CREATOR = new Creator<NoteSpot>() {
        @Override
        public NoteSpot createFromParcel(Parcel source) {return new NoteSpot(source);}

        @Override
        public NoteSpot[] newArray(int size) {return new NoteSpot[size];}
    };
}
